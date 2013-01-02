import sys
from xml.dom.minidom import parse
import urllib
import urllib2
import ast

usage = """usage: python executeSparqlQueries inputFile outputFile [nbQ]
	- inputFile: file containing nl queries to be translated, in the qald format
	- outputFile: result file containing pivot queries
	- nbQ: number of sparql queries to execute for each user query (default: 1)"""


if len(sys.argv) < 3:
	sys.exit(usage)
	
# read arguments
inputFilename = sys.argv[1]
outputFilename = sys.argv[2]
if len(sys.argv) >= 4:
	nbQ = int(sys.argv[3])
else:
	nbQ = 1

# open and read input XML file
dom = parse(inputFilename)

# RESTfull web service configuration
ws_url = 'http://192.168.250.91:8080/musicbrainz/sparql?'

for question_element in dom.getElementsByTagName('question'):
	print ' + question ' + question_element.attributes["id"].value
	queryInterpretationElements = question_element.getElementsByTagName('queryInterpretation')

	for rank in range(0,min(queryInterpretationElements.length, nbQ)):
		print "rank " + str(rank)
		queryInterpretationElement = queryInterpretationElements[rank]
		sparqlQuery = queryInterpretationElement.getElementsByTagName('sparqlQuery')[0].firstChild.wholeText.replace('\n','')
		print 'sparqlQuery: ' + sparqlQuery

		# RESTfull web service call
		values = { "query": sparqlQuery, "output": "json", }
		data = urllib.urlencode(values)
		req = urllib2.Request(ws_url + data)
		response = urllib2.urlopen(req)
		responseDict = ast.literal_eval(response.read())
		print responseDict
		# get answers element from the response
		

		outputFile = open(outputFilename, 'w')	
		outputFile.write(dom.toprettyxml())
