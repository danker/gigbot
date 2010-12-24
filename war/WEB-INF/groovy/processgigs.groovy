import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults
import com.breomedia.gigbot.Listing
import com.breomedia.gigbot.dao.ProcessedResultsDAO

import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

//TODO: REMOVE THIS GROOVLET (and possibly gigsearchform.gtpl)

// TODO: Sanatize inputs!
def seeds = params.seeds.split(", ")
request['seedstr'] = params.seeds

def keywords = params.keywords.split(", ")
request['keywordstr'] = params.keywords

// grab the user
request['currentUser'] = user
log.info "user object ${user}"

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