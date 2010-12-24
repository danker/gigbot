import com.breomedia.gigbot.UserPreferences
import com.breomedia.gigbot.dao.UserPreferencesDAO

UserPreferencesDAO upDAO = new UserPreferencesDAO()

UserPreferences up = upDAO.getPreferencesForUser(user.nickname)
def upEntity = upDAO.getPreferencesEntityForUser(user.nickname)

request.currentUser = user
request.upKey = upEntity?.key
request.userPrefs = up

forward 'edituserprefs.gtpl'