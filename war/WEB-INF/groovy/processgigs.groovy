// Define the pages which contain links to products - our "seeds" in crawl parlance.
def seeds = ["http://stlouis.craigslist.org/web/index.rss"]
request['seeds'] = seeds
request['seedstr'] = seeds.join(", ")

def keywords = ["freelance", "photoshop", "wordpress", "html",
        "designer", "graphic design", "webdesign", "powerpoint", "css",
        "flash", "html5", "joomla"]

request['keywords'] = keywords
request['keywordstr'] = keywords.join(", ")

// grab the user
request['currentUser'] = user
log.info "user object ${user}"
log.info "userService ${users}"

def results = [ ]

slurper = new XmlSlurper()

// Now let's loop through each seed URL in turn.
seeds.each() {

    log.info "Accessing seed URL ${it}"
    def seedURL = new URL(it)

    seedURL.withReader { seedReader ->

        def rss = slurper.parse(seedReader)

        // need to get at some specific namespaces
        def ns = [:]
        ns.dc = "http://purl.org/dc/elements/1.1/"
        ns.rdf = "http://www.w3.org/1999/02/22-rdf-syntax-ns"
        rss.declareNamespace(ns)

        log.info "JOBS FROM CHANNEL: " + rss.channel.'dc:title'
        log.info "CHANNEL LINK: " + rss.channel.link

        // case-insensitive, match the pattern anywhere in the body of the input string
        def myRegex = /(?im)^.*\b(/ + keywords.join("|") + /)\b.*$/
        log.info "REGEX to match: ${myRegex}"
        log.info "Matching description against the following words: " + keywords.join(" OR ")

        def matchedListings = 0
        rss.item.each {

            String description = it.description.text()

            if (description.find(myRegex)) {
	
				results.add(['title':it.'dc:title', 'link':it.link])
	
                log.info "=#" * 12 + " MATCH ${matchedListings+1} " + "=#" * 12
                log.info "JOB TITLE: " + it.'dc:title'
                log.info "JOB LINK: " + it.link
                //log.info "JOB DESCRIPTION\n" + description
                matchedListings++
            }

        }

		request['stats'] = ['matchedListings':matchedListings, 'totalItemsProcessed':rss.item.size()]
        log.info "Matched ${matchedListings} of ${rss.item.size()} listings."

    }
}

request['results'] = results

forward 'gigs.gtpl'