import com.breomedia.gigbot.GigProcessor
import com.breomedia.gigbot.ProcessedResults

// TODO: Sanatize inputs!

def seeds = params.seeds.split(", ")
request['seedstr'] = params.seeds

def keywords = params.keywords.split(", ")
request['keywordstr'] = params.keywords

// grab the user
request['currentUser'] = user
log.info "user object ${user}"

request['results'] = GigProcessor.process(user, seeds, keywords)

forward 'gigs.gtpl'