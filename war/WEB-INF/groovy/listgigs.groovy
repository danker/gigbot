import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults
import com.breomedia.gigbot.UserPreferences
import com.breomedia.gigbot.dao.ProcessedResultsDAO
import com.breomedia.gigbot.dao.UserPreferencesDAO

// get the prefs for the currently logged-in user
UserPreferences userPrefs = new UserPreferencesDAO().getPreferencesForUser(user.nickname)

def seeds = userPrefs.seeds.split(", ")
request['seedstr'] = userPrefs.seeds

def keywords = userPrefs.keywords.split(", ")
request['keywordstr'] = userPrefs.keywords

request['currentUser'] = user
request['results'] = getResults(seeds, keywords)

forward 'gigs.gtpl'

// ------------------------------------
private ProcessedResults getResults(seeds, keywords) {
	
	// if we have results cached/saved, return those.
	// Otherwise fetch the latest and greatest
	ProcessedResultsDAO processedResultsDAO = new ProcessedResultsDAO()
	ProcessedResults results = processedResultsDAO.getResultsFromDatastore(user.nickname)
	
	if (!results) {
		
		log.info "Did not find results in the datastore for user = ${user.nickname}, fetching new results..."
		
		results = GigProcessor.process(user, seeds, keywords)
		processedResultsDAO.saveResults(results)
	
	} else {
		
		log.info "Found results (as of: ${results.lastRun} ) in the datastore for user = ${user.nickname}"
		
	}
	
	return results
}