<% import com.breomedia.gigbot.ProcessedResults %>

<html>
	<body>
		<p>Welcome, ${request.currentUser.nickname}
		<p>RSS Feeds Scanned: ${request.seedstr}</p>
		<p>Keywords: ${request.keywordstr}</p>
		
		<h1>${request.results.stats.matchedListings} Matchings Gigs</h1>
		<ul>
			<% request.results.matchedListings.each { %>
			<li><a href="${it.link}">${it.title}</a></li>
			<% } %>
		</ul>
	</body>
</html>