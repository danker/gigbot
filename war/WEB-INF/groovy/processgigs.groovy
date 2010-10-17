import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults

// Define the pages which contain links to products - our "seeds" in crawl parlance.
def seeds = ["http://stlouis.craigslist.org/web/index.rss"]
request['seedstr'] = seeds.join(", ")

def keywords = ["freelance", "photoshop", "wordpress", "html",
        "designer", "graphic design", "webdesign", "powerpoint", "css",
        "flash", "html5", "joomla"]
request['keywordstr'] = keywords.join(", ")

// grab the user
request['currentUser'] = user
log.info "user object ${user}"

request['results'] = GigProcessor.process(user, seeds, keywords)

forward 'gigs.gtpl'