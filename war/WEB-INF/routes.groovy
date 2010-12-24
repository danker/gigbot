// example routes
/*
get "/blog/@year/@month/@day/@title", forward: "/WEB-INF/groovy/blog.groovy?year=@year&month=@month&day=@day&title=@title"
get "/something", redirect: "/blog/2008/10/20/something", cache: 2.hours
get "/book/isbn/@isbn", forward: "/WEB-INF/groovy/book.groovy?isbn=@isbn", validate: { isbn ==~ /\d{9}(\d|X)/ }
*/

get "/hello", forward: "/hello.gtpl"
get "/gigs/list", forward: "/listgigs.groovy"
//get "/gigs/search", forward: "/gigsearchform.gtpl"
get "/gigs/userprefs", forward: "/getuserprefs.groovy"
get "/gigs/userprefsupdate", forward: "/saveuserprefs.groovy"
get "/cron/findgigs", forward: "/gigscron.groovy"