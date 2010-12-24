<% import com.breomedia.gigbot.UserPreferences %>

<% include '/WEB-INF/includes/header.gtpl' %>

<h2>Edit User Prefs for ${request.currentUser.nickname}</h2>

<form action="/gigs/userprefsupdate" method="GET">

	<input type="hidden" name="id" value="${request.upKey?.id ?: ""}" />

	<div>
		<p><label for="seeds">RSS Feeds: (comma-separated)</label></p>
		<textarea name="seeds" rows="10" cols="50">${request.userPrefs?.seeds ?: ""}</textarea>
	</div>
	
	<div>
		<p><label for="keywords">Keywords: (comma-separated)</label></p>
		<textarea name="keywords" rows="10" cols="50">${request.userPrefs?.keywords ?: ""}</textarea>
	</div>
	
	<div>
		<label for="alertViaEmail">Alert via Email?</label>
		<input type=checkbox name="alertViaEmail" value="true" ${!(request.userPrefs?.alertViaEmail) ?: "checked"} />
	</div>

	<div>
		<label for="alertViaGTalk">Alert via GTalk?</label>
		<input type=checkbox name="alertViaGTalk" value="true" ${!(request.userPrefs?.alertViaGTalk) ?: "checked"} />
	</div>

	<div>
		<p><label for="runInterval">How often would you like to check for new gigs?</label></p>
		Once daily: <input type="radio" name="runInterval" value="24" ${!(request.userPrefs?.runInterval == 24) ?: "checked"} /><br/>
		Twice daily: <input type="radio" name="runInterval" value="12" ${!(request.userPrefs?.runInterval == 12) ?: "checked"} /><br/>
		Four times daily: <input type="radio" name="runInterval" value="6" ${!(request.userPrefs?.runInterval == 6) ?: "checked"} />
	</div>
	
	<div>
		<p><input type="submit" value="Save Preferences"></p>
	</div>

</form>

<% include '/WEB-INF/includes/footer.gtpl' %>
