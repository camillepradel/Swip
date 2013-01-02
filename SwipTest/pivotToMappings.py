import sys
import traceback
from xml.dom.minidom import parse
import urllib
import urllib2
import ast
import re
import urlparse

usage = """usage: python nlToPivot inputFile outputFile [pivotQueryElementName] [kb] [nbMap]
	- inputFile: file containing nl queries to be translated, in the qald format
	- outputFile: result file containing pivot queries
	- pivotQueryElementName: name of the XML element containing the pivot query to process (default: pivotQuery)
	- kb: target knowledge base (default: musicbrainz)
	- nbMap number of query mappings to store (default: 5)"""

def getText(nodelist):
	rc = []
	for node in nodelist:
		if node.nodeType == node.TEXT_NODE:
			rc.append(node.data)
	return ''.join(rc)

def replaceGen(string, gen):
	result = string
	for id in gen:
		result = result.replace('_gen' + id + '_', gen[id][0])
	return result


if len(sys.argv) < 3:
	sys.exit(usage)
	
# read arguments
inputFilename = sys.argv[1]
outputFilename = sys.argv[2]
if len(sys.argv) >= 4:
	pivotQueryElementName = sys.argv[3]
else:
	pivotQueryElementName = "pivotQuery"
if len(sys.argv) >= 5:
	kb = sys.argv[4]
else:
	kb = "musicbrainz"
if len(sys.argv) >= 6:
	nbMap = int(sys.argv[5])
else:
	nbMap = 5

# open and read input XML file
dom = parse(inputFilename)

# RESTfull web service configuration
ws_url = 'http://localhost/PivotToMappings/resources/rest/generateBestMappings?'

for question_element in dom.getElementsByTagName('question'):
	try:
		print ' + question ' + question_element.attributes["id"].value
		supported = question_element.attributes["supported"].value
		if supported == "true":
			pivotQueryString = question_element.getElementsByTagName(pivotQueryElementName)[0].firstChild.wholeText.replace('\n','')

			# RESTfull web service call
			values = { "pivotQuery":pivotQueryString, "kb":kb, "numMappings":nbMap }
			data = urllib.urlencode(values)
			req = urllib2.Request(ws_url + data)
			response = urllib2.urlopen(req)
			# responseDict = ast.literal_eval(response.read())
			responseDict = ast.literal_eval(response.read())
			print '\nresponseDict: ' + str(responseDict) + '\n'
			#responseDict = urlparse.parse_qs(response.read())
			#print '\nresponseDict: ' + str(responseDict) + '\n\n\n\n\n'
			rank = 1
			for interpretation in responseDict['content']:
				try:
					queryInterpretationElement = dom.createElement("queryInterpretation")
					queryInterpretationElement.setAttribute('rank', str(rank))
					rank += 1
					question_element.appendChild(queryInterpretationElement)
					# relevancy mark
					relevancyMark = re.findall("\d+.\d+", interpretation['mappingDescription'])[0]
					print relevancyMark
					relevancyMarkElement = dom.createElement("relevancyMark")
					relevancyMarkElement.appendChild(dom.createTextNode(relevancyMark))
					queryInterpretationElement.appendChild(relevancyMarkElement)
					# descriptive sentence
					descriptiveSentence = replaceGen(interpretation['descriptiveSentence']['string'], interpretation['descriptiveSentence']['gen'])
					print descriptiveSentence
					descriptiveSentenceElement = dom.createElement("descriptiveSentence")
					descriptiveSentenceElement.appendChild(dom.createTextNode(descriptiveSentence))
					queryInterpretationElement.appendChild(descriptiveSentenceElement)
					# sparql query
					sparqlQuery = replaceGen(interpretation['sparqlQuery']['string'], interpretation['sparqlQuery']['uris'])
					# FIXME: dirty fix - il faudrait decoder la requete plus proprement
					sparqlQuery = sparqlQuery.replace('\/', '/')
				except:
					print '>>> traceback <<<'
					traceback.print_exc()
					print '>>> end of traceback <<<'
					sparqlQuery = "ERROR"
				print sparqlQuery
				sparqlQueryElement = dom.createElement("sparqlQuery")
				sparqlQueryElement.appendChild(dom.createTextNode(sparqlQuery))
				queryInterpretationElement.appendChild(sparqlQueryElement)

			outputFile = open(outputFilename, 'w')	
			outputFile.write(dom.toprettyxml())

		else:
			print "NOT SUPPORTED\n"

	except:
		print '>>> traceback <<<'
		traceback.print_exc()
		print '>>> end of traceback <<<'