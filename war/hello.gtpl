<html>
	<body>
		<h1>Hello!</h1>
		<p>Hello world!</p>
		
		<% if (user && users.isUserAdmin()) { %> 
			You're an Admin! 
		<% } %> 
		
		<% if (users.isUserLoggedIn()) { %> 
			<a href="${users.createLogoutURL('/')}">Logout</a> 
		<%    } else { %> 
			<a href="${users.createLoginURL('/')}">Login</a> 
		<% } %>
		
	</body>
</html>