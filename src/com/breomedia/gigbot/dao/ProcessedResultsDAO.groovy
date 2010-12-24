package com.breomedia.gigbot.dao

import com.breomedia.gigbot.ProcessedResults
import com.breomedia.gigbot.Listing

import groovyx.gaelyk.logging.GroovyLogger
import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

class ProcessedResultsDAO {
	
	def log = new GroovyLogger("ProcessedResultsDAO")
	
	// ------------------------------------
	// getResultsFromDatastore
	// ------------------------------------
	public ProcessedResults getResultsFromDatastore(String owner) {

		ProcessedResults results = null

		def query = new Query("ProcessedResults")
		query.addFilter("owner", Query.FilterOperator.EQUAL, owner)
		PreparedQuery preparedQuery = DatastoreServiceFactory.datastoreService.prepare(query)
		def resultEntity = preparedQuery.asSingleEntity()

		if (resultEntity) {

			// something was returned, so grab all the listings too and
			// construct the ProcessedResults object
			//TODO: WHY DOESN'T THIS WORK? results = resultEntity as ProcessedResults
			results = getProcessedResultsFromEntity(resultEntity)

			query = new Query("Listing")
			query.addFilter("owner", Query.FilterOperator.EQUAL, owner)
			preparedQuery = DatastoreServiceFactory.datastoreService.prepare(query)
			// get all listings for the logged in user and add to the
			// ProcessedResults object
			preparedQuery.asList(withLimit(1000)).each {
				results.matchedListings.add(new Listing(title: it.title, link: it.link))
			}

		} else {
			log.info "No existing processed results for user: ${owner}"
		}

		return results
	}
	
	// ------------------------------------
	// saveResults
	// ------------------------------------
	public void saveResults(results) {

		Entity resultsEntity = new Entity("ProcessedResults")

		resultsEntity.matchedListingCount = results.matchedListingCount
		resultsEntity.totalItemsProcessed = results.totalItemsProcessed
		resultsEntity.owner = results.owner
		resultsEntity.lastRun = results.lastRun
		resultsEntity.save()

		results.matchedListings.each {
			Entity listing = it as Entity
			listing.owner = results.owner
			//TODO: It would be much more awesome to use the mixed-in save method! -- listing.save()
			DatastoreServiceFactory.datastoreService.put(listing)
		}

	}
	
	// ------------------------------------
	// getProcessedResultsFromEntity
	// ------------------------------------
	private ProcessedResults getProcessedResultsFromEntity(prEntity) {
		
		ProcessedResults pr = new ProcessedResults()
		
		pr.owner = prEntity.owner
		
		if (prEntity.lastRun) {
			pr.lastRun = prEntity.lastRun as Date
		}
		
		if (prEntity.totalItemsProcessed) {
			pr.totalItemsProcessed = prEntity.totalItemsProcessed as Long
		}
		
		if (prEntity.matchedListingCount) {
			pr.matchedListingCount = prEntity.matchedListingCount as Long
		}

		return pr
	}

	//TODO: Need to move the delete method here, but would like to inject the "datastore" ref in this object
}