import httplib, urllib

updateDomain = "swip.univ-tlse2.fr:8080"
updatePath = "/musicbrainz/update"

queriesNamedGraphUri = "http://swip.univ-tlse2.fr/musicbrainz/queries"
usefulMatchingsFile = "usefulMatchings.ttl"

patternsNamedGraphUri = "http://swip.univ-tlse2.fr/musicbrainz/patterns"
patternsTurtleFiles = ["patterns-nt/simple_pattern.nt", ]
# patternsTurtleFiles = ["patterns-nt/cd_info.nt", "patterns-nt/artist_member.nt"]

def getSparqlInsert(file, namedGraphUri):
	insertQuery = ""
	inFile = open(file)
	for line in inFile:
		if line.startswith("@prefix"):
			insertQuery += line[1:-2] + "\n" # remove '@' and '.' from the prefix declarations
	insertQuery += "INSERT DATA {\n"
	insertQuery += "  GRAPH <" + namedGraphUri + "> {\n"
	inFile = open(file)
	for line in inFile:
		if not line.startswith("@prefix"):
			insertQuery += line # copy all lines which are not prefix declarations
	insertQuery += "  }\n"
	insertQuery += "}"
	return insertQuery

def postQuery(updateQuery):
	params = urllib.urlencode({'update': updateQuery,})
	headers = {"Content-type": "application/x-www-form-urlencoded", "Accept": "text/plain"}
	conn = httplib.HTTPConnection(updateDomain)
	conn.request("POST", updatePath, params, headers)
	response = conn.getresponse()
	print response.status, response.reason
	#data = response.read()
	conn.close()

print "clear queries named graph and insert usefull matchings data"
query = "CLEAR GRAPH <" + queriesNamedGraphUri + ">;\n\n"
query += getSparqlInsert(usefulMatchingsFile, queriesNamedGraphUri)
#print query;
postQuery(query)

print "clear (and not drop, in order to keep the reasoner configuration) patterns named graph and insert new patterns data"
query = "CLEAR GRAPH <" + patternsNamedGraphUri + ">;\n\n"
postQuery(query)
for patternsTurtleFile in patternsTurtleFiles:
	print patternsTurtleFile
	query = getSparqlInsert(patternsTurtleFile, patternsNamedGraphUri)
	# print query
	postQuery(query)