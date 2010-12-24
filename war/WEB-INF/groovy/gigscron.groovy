import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults
import com.breomedia.gigbot.Listing
import com.breomedia.gigbot.UserPreferences
import com.breomedia.gigbot.dao.ProcessedResultsDAO

import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

// run each users job based on their preferences 
process(getUserPreferences())

// ------------------------------------
// getUserPreferences
// ------------------------------------
private List getUserPreferences() {
	
	List userPrefs = new ArrayList()
	
	def query = new Query("UserPreferences")
	PreparedQuery preparedQuery = datastore.prepare(query)
	// TODO: Gracefully handle more than a thousand users
	preparedQuery.asList(withLimit(1000)).each { upEntity ->
		
		UserPreferences up = new UserPreferences()
		
		if (upEntity.runInterval) {
			up.runInterval = upEntity.runInterval as Integer
		}
		if (upEntity.alertViaEmail) {
			up.alertViaEmail = true
		}
		if (upEntity.alertViaGTalk) {
			up.alertViaGTalk = true
		}
		up.seeds = upEntity.seeds
		up.keywords = upEntity.keywords
		up.owner = upEntity.owner
		
		userPrefs.add(up)
		//TODO: MOVE THIS TO A DAO!!!
		//TODO: wish I could do it this way!
		//userPrefs.add(it as UserPreferences)
	}
	
	return userPrefs
}

// ------------------------------------
// process
// ------------------------------------
private void process(List userPrefs) {
	
	ProcessedResultsDAO processedResultsDAO = new ProcessedResultsDAO()
	
	userPrefs.each { userPref ->

		// get the latest Processed Results, if any
		ProcessedResults oldResults = processedResultsDAO.getResultsFromDatastore(userPref.owner) 
		
		// determine if we should process gigs, based on last run and user preference
		if (shouldProcess(userPref, oldResults)) {
			
			// delete old results and listings (TODO: Should we ever hold on to old listings?)
			deleteResultsAndListings(userPref.owner)
			
			// run new job and save results
			newProcessedResults = findNewGigsForUser(processedResultsDAO, userPref)
			
			// alert via email or gtalk
			alertUser(userPref, newProcessedResults)
			
		}
	}
	
}

// ------------------------------------
// shouldProcess
// ------------------------------------
private boolean shouldProcess(UserPreferences userPrefs, ProcessedResults processedResults) {

	Date now = new Date()
	boolean shouldProcess = false
	
	use (groovy.time.TimeCategory) {
		if (now > (userPrefs.runInterval.hours + processedResults.lastRun)) {
			shouldProcess = true
		}
	}

	return shouldProcess
}

// ------------------------------------
// deleteResultsAndListings
// ------------------------------------
private void deleteResultsAndListings(String user) {

	// delete the ProcessedResults entity
	datastore.withTransaction {
		def query = new Query("ProcessedResults")
		query.addFilter("owner", Query.FilterOperator.EQUAL, user)
		PreparedQuery preparedQuery = datastore.prepare(query)
		preparedQuery?.asSingleEntity()?.delete()
	}

	// delete the Listings
	def query = new Query("Listing")
	query.addFilter("owner", Query.FilterOperator.EQUAL, user)
	PreparedQuery preparedQuery = datastore.prepare(query)
	preparedQuery = datastore.prepare(query)
	// get all listings for the user and add to the ProcessedResults object
	// TODO: Deal with more than 1000 result gracefully
	preparedQuery?.asList(withLimit(1000)).each { listing ->
		listing.delete()
	}

}

// ------------------------------------
// findNewGigsForUser
// ------------------------------------
private ProcessedResults findNewGigsForUser(ProcessedResultsDAO processedResultsDAO, UserPreferences userPref) {
	
	ProcessedResults newProcessedResults = null

	if (userPref.seeds && userPref.keywords) {
		newProcessedResults = GigProcessor.process(userPref.owner, userPref.seeds.split(", "), userPref.keywords.split(", "))
		processedResultsDAO.saveResults(newProcessedResults)		
	} else {
		log.info("User ${userPref.owner} does not have seed URLs or Search Terms (Keywords) specified.");
	}
	
	return newProcessedResults
}

// ------------------------------------
// alertUser
// ------------------------------------
private void alertUser(UserPreferences userPref, ProcessedResults results) {
	// TODO: Implement email and IM alerting
}