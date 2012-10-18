import sys
from xml.dom.minidom import parse
import urllib
import urllib2

usage = """usage: python translateToPivot inputFile outputFile lang pos dep
    - inputFile: file containing "gazetteed" queries to be translated, in the qald format
    - outputFile: result file containing pivot queries.
    - lang: language of queries (default: en)
    - pos: POS-tagger to use (default: treeTagger)
    - dep: dependency parser (default: malt)"""


def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)


if len(sys.argv) < 3:
	print usage
	
else:
	# read arguments
	inputFilename = sys.argv[1]
	outputFilename = sys.argv[2]
	if len(sys.argv) >= 4:
		lang = sys.argv[3]
	else:
		lang = "en"
	if len(sys.argv) >= 5:
		pos = sys.argv[4]
	else:
		pos = "treeTagger"
	if len(sys.argv) >= 6:
		dep = sys.argv[5]
	else:
		dep = "malt"

	# open and read input XML file
	dom = parse(inputFilename)

	# RESTfull web service configuration
	ws_url = 'http://localhost:8080/NlToPivotStanford/resources/rest/translateQuery?'

	for question_element in dom.getElementsByTagName('question'):
		print question_element.attributes['id'].value
		questionString = question_element.getElementsByTagName('gazetteedQuestion')[0].firstChild.wholeText.replace('\n','')
		print questionString

		# RESTfull web service call
		values = { "nlQuery":questionString, "lang":lang, "pos":pos, "dep":dep }
		data = urllib.urlencode(values)
		req = urllib2.Request(ws_url + data)
		response = urllib2.urlopen(req)
		pivot_Query = response.read()
		print pivot_Query + '\n'
	
	outputFile = open(outputFilename, 'w')	
	outputFile.write(dom.toprettyxml())
