import com.breomedia.gigbot.UserPreferences
import com.google.appengine.api.datastore.*

def query = new Query("UserPreferences")
query.addFilter("owner", Query.FilterOperator.EQUAL, user.nickname)
PreparedQuery preparedQuery = datastore.prepare(query)

Entity upEntity = preparedQuery.asSingleEntity()

UserPreferences up = new UserPreferences()
if (upEntity) {
	// TODO: Figure out why "up = upEntity as UserPreferences" barfs on non-string values when the value from the Entity is null.
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
}

request.currentUser = user
request.upKey = upEntity?.key
request.userPrefs = up

forward 'edituserprefs.gtpl'