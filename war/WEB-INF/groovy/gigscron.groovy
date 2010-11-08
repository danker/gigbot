import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults
import com.breomedia.gigbot.Listing
import com.breomedia.gigbot.UserPreferences

import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

// run each users job based on their preferences 
process(getUserPreferences())

// ------------------------------------
// getUserPreferences
// ------------------------------------
private List getUserPreferences() {
	
	List userPrefs = null
	
	def query = new Query("UserPreferences")
	PreparedQuery preparedQuery = datastore.prepare(query)
	// TODO: Gracefully handle more than a thousand users
	preparedQuery.asList(withLimit(1000)).each {
		userPrefs.add(it as UserPreferences)
	}
	
	return userPrefs
}

// ------------------------------------
// process
// ------------------------------------
private void process(List userPrefs) {
	
	userPrefs.each { userPref ->
		
		// get the latest Processed Results, if any
		ProcessedResults oldResults = getResultsFromDatastore() //TODO: Refactor...using this method in 2 diff groovlets!
		
		// determine if we should process gigs, based on last run and user preference
		if (shouldProcess(userPref, oldResults)) {
			
			// delete old results and listings
			deleteResultsAndListings(userPref.owner)
			
			// run new job and save results
			ProcessedResults newProcessedResults = GigProcessor.process(userPref.owner, userPref.seeds, userPref.keywords)
			saveResults(newProcessedResults) //TODO: Refactor...using this method in 2 diff groovlets
			
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
		if (now > ((24/userPrefs.runInterval).hours + processedResults.lastRun)) {
			shouldProcess = true
		}
	}

	return shouldProcess
}

// ------------------------------------
// getResultsFromDatastore
// ------------------------------------
private ProcessedResults getResultsFromDatastore() {
	
	ProcessedResults results = null
	
	def query = new Query("ProcessedResults")
	query.addFilter("owner", Query.FilterOperator.EQUAL, user.nickname)
	PreparedQuery preparedQuery = datastore.prepare(query)
	def resultEntity = preparedQuery.asSingleEntity()
	
	if (resultEntity) {

		// something was returned, so grab all the listings too and
		// construct the ProcessedResults object
		results = resultEntity as ProcessedResults
		
		query = new Query("Listing")
		query.addFilter("owner", Query.FilterOperator.EQUAL, user.nickname)
		preparedQuery = datastore.prepare(query)
		// get all listings for the logged in user and add to the
		// ProcessedResults object
		preparedQuery.asList(withLimit(1000)).each {
			results.matchedListings.add(new Listing(title: it.title, link: it.link))
		}
		
	}
	
	return results
}

// ------------------------------------
// saveResults
// ------------------------------------
private void saveResults(results) {

	Entity resultsEntity = new Entity("ProcessedResults")

	resultsEntity.matchedListingCount = results.matchedListingCount
	resultsEntity.totalItemsProcessed = results.totalItemsProcessed
	resultsEntity.owner = results.owner
	resultsEntity.lastRun = results.lastRun
	resultsEntity.save()

	results.matchedListings.each {
		Entity listing = it as Entity
		listing.owner = results.owner
		listing.save()
	}

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
		preparedQuery.asSingleEntity().delete()
	}
	
	// delete the Listings
	datastore.withTransaction {
		def query = new Query("Listing")
		query.addFilter("owner", Query.FilterOperator.EQUAL, user)
		PreparedQuery preparedQuery = datastore.prepare(query)
		preparedQuery = datastore.prepare(query)
		// get all listings for the logged in user and add to the
		// ProcessedResults object
		// TODO: Deal with more than 1000 result gracefully
		preparedQuery.asList(withLimit(1000)).each { listing ->
			listing.delete()
		}
	}
	
}

// ------------------------------------
// alertUser
// ------------------------------------
private void alertUser(UserPreferences userPref, ProcessedResults results) {
	// TODO: Implement email and IM alerting
}