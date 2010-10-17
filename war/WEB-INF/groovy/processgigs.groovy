import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults

log.info "INPUT " + params.seeds
log.info "INPUT " + params.keywords

//def seeds = ["http://stlouis.craigslist.org/web/index.rss"]
def seeds = params.seeds.split(", ")
request['seedstr'] = params.seeds

//def keywords = ["freelance", "photoshop", "wordpress", "html",
//        "designer", "graphic design", "webdesign", "powerpoint", "css",
//        "flash", "html5", "joomla"]
def keywords = params.keywords.split(", ")
request['keywordstr'] = params.keywords

// grab the user
request['currentUser'] = user
log.info "user object ${user}"

request['results'] = GigProcessor.process(user, seeds, keywords)

forward 'gigs.gtpl'