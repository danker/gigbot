package com.breomedia.gigbot

class UserPreferences {
	
	String owner
	String seeds
	String keywords
	boolean alertViaEmail
	boolean alertViaGTalk
	int runInterval
	
	String toString() {
		"owner: $owner, seeds: $seeds, keywords: $keywords, alertViaEmail: $alertViaEmail, alertViaGTalk: $alertViaGTalk, runInterval: $runInterval"
	}
}