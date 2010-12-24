<% include '/WEB-INF/includes/header.gtpl' %>

<h1>Welcome</h1>

<p>
	<% if (user && users.isUserAdmin()) { %> 
		You're an Admin!<br>
		What <a href="datetime.groovy">time</a> is it?
	<% } %>
</p>

<% if (users.isUserLoggedIn()) { %>
	<a href="/gigs/userprefs">Manage Preferences</a> |
	<a href="/gigs/list">View gigs</a> |
	<a href="${users.createLogoutURL('/')}">Logout</a> 
<%    } else { %> 
	<a href="${users.createLoginURL('/')}">Login</a> 
<% } %>

<% include '/WEB-INF/includes/footer.gtpl' %>

