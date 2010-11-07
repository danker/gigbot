<% import com.breomedia.gigbot.ProcessedResults %>
<% import com.breomedia.gigbot.Listing %>

<html>
	<body>
		<p>Welcome, ${request.currentUser.nickname}
		<p>RSS Feeds Scanned: ${request.seedstr}</p>
		<p>Keywords: ${request.keywordstr}</p>
		
		<h1>${request.results.matchedListingCount} Matchings Gigs as of: ${request.results.lastRun.format("EEE, d MMM yyyy HH:mm:ss z")}</h1>
		<ul>
			<% request.results.matchedListings.each { %>
			<li><a href="${it.link}">${it.title}</a></li>
			<% } %>
		</ul>
	</body>
</html>