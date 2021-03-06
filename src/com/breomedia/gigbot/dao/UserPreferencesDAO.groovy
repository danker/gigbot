package com.breomedia.gigbot.dao

import com.breomedia.gigbot.UserPreferences

import groovyx.gaelyk.logging.GroovyLogger
import com.google.appengine.api.datastore.*
import static com.google.appengine.api.datastore.FetchOptions.Builder.*

class UserPreferencesDAO {
		
	// ------------------------------------
	// getUserPreferences
	// ------------------------------------
	public List getUserPreferences(limit=1000) { // TODO: Gracefully handle more than a thousand users

		List userPrefs = new ArrayList()

		def query = new Query("UserPreferences")
		PreparedQuery preparedQuery = DatastoreServiceFactory.datastoreService.prepare(query)
		preparedQuery.asList(withLimit(limit)).each { upEntity ->

			userPrefs.add(getUserPreferencesObjectFromEntity(upEntity))
			//TODO: wish I could do it this way!
			//userPrefs.add(it as UserPreferences)

		}

		return userPrefs
	}
	
	// ------------------------------------
	// getPreferencesForUser
	// ------------------------------------
	public UserPreferences getPreferencesForUser(user) {
		return getUserPreferencesObjectFromEntity(getPreferencesEntityForUser(user))		
	}
	
	// ------------------------------------
	// getPreferencesEntityForUser
	// ------------------------------------
	public def getPreferencesEntityForUser(user) {
		
		def query = new Query("UserPreferences")
		query.addFilter("owner", Query.FilterOperator.EQUAL, user)
		PreparedQuery preparedQuery = DatastoreServiceFactory.datastoreService.prepare(query)
		
		return preparedQuery?.asSingleEntity()
	
	}

	// ------------------------------------
	// getUserPreferencesObjectFromEntity
	// ------------------------------------	
	private UserPreferences getUserPreferencesObjectFromEntity(upEntity) {
		
		// TODO: Figure out why "up = upEntity as UserPreferences" barfs on non-string values when the value from the Entity is null.
		// NOTE: the above would only work in a groovlet...but I still can't get it to work regardless
		UserPreferences up = null
		
		if (upEntity) {
			
			up = new UserPreferences()

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
		
		}
		
		return up
	}
	
}