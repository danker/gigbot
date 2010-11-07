import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults
import com.breomedia.gigbot.Listing

import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

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
// Result Processing Methods
// ------------------------------------

//
private ProcessedResults getResults(seeds, keywords) {
	
	// if we have results cached/saved, return those.
	// Otherwise fetch the latest and greatest
	ProcessedResults results = getResultsFromDatastore()
	
	if (!results) {
		
		log.info "Did not find results in the datastore for user = ${user.nickname}, fetching new results..."
		
		results = GigProcessor.process(user, seeds, keywords)
		saveResults(results)
	
	} else {
		
		log.info "Found results (as of: ${results.lastRun} ) in the datastore for user = ${user.nickname}"
		
	}
	
	return results
}

//
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

//
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