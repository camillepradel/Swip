import sys
from xml.dom.minidom import parse
from suds.client import Client

usage = """usage: python applyGazetteer inputFile outputFile
    - inputFile: file containing queries to be "gazetteed", to the qald format
    - outputFile: result file containing "gazetteed" queries."""


def getText(nodelist):
    rc = []
    for node in nodelist:
        if node.nodeType == node.TEXT_NODE:
            rc.append(node.data)
    return ''.join(rc)


if len(sys.argv) < 3:
	print usage
	
else:
	#input and output
	inputFilename = sys.argv[1]
	outputFilename = sys.argv[2]
	dom = parse(inputFilename)

	# web service
	gazetteer_url = 'http://swip.univ-tlse2.fr/NlToPivotGazetteer/resources/rest/gatherNamedEntities'
	gazetteer_client = Client(gazetteer_url)

	for question_element in dom.getElementsByTagName('question'):
		print question_element.attributes['id'].value
		questionString = question_element.getElementsByTagName('string')[0].firstChild.wholeText.replace('\n','')
		print questionString
		d = dict(text=questionString, tagWithClass=False)
		gazetteer_result = gazetteer_client.service.getQueryWithGatheredNamedEntities(**d)
		print gazetteer_result
		gazetteed_question_element = dom.createElement("gazetteedQuestion")
		gazetteed_question_element.appendChild(dom.createTextNode(gazetteer_result))
		question_element.appendChild(gazetteed_question_element)
	
	outputFile = open(outputFilename, 'w')	
	outputFile.write(dom.toprettyxml())
