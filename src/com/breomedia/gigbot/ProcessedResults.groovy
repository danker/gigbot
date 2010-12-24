package com.breomedia.gigbot

class ProcessedResults {
	
	String owner
	List matchedListings = [ ]
	Date lastRun = new Date()
	
	// stats
	Long totalItemsProcessed
	Long matchedListingCount
	
	String toString() {
		"owner: $owner, lastRun: $lastRun, totalItemsProcessed: $totalItemsProcessed, matchedListingCount: $matchedListingCount, matchedListings: $matchedListings"
	}
}