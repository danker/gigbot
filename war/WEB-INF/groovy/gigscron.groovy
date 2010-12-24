import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults
import com.breomedia.gigbot.Listing
import com.breomedia.gigbot.UserPreferences
import com.breomedia.gigbot.dao.ProcessedResultsDAO
import com.breomedia.gigbot.dao.UserPreferencesDAO

import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

// run each users job based on their preferences 
process(new UserPreferencesDAO().getUserPreferences())

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
		if (userPrefs.runInterval && (now > (userPrefs.runInterval.hours + processedResults.lastRun))) {
			shouldProcess = true
		}
	}

	return shouldProcess
}

// ------------------------------------
// deleteResultsAndListings
// ------------------------------------
private void deleteResultsAndListings(String user) { //TODO: Get this code out of here into a DAO

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
	
	// TODO: Create an app-specific reciever email address, so we can use that in the "sender" attribute
	// http://code.google.com/appengine/docs/java/mail/overview.html
	if (userPref.alertViaEmail) {
		mail.send sender: "danker@gmail.com",
			to: "${userPref.owner}@gmail.com",
			subject: "GigBot Search Results",
			htmlBody: "The GigBot found ${results.matchedListingCount} gigs based on your search preferences.<br/>http://thegigbot.appspot.com/gigs/list"
	}
	
}