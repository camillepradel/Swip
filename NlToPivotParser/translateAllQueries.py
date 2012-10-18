import os
import string
import xml.sax
from nltk import wordpunct_tokenize
from suds.client import Client
import commands
import urllib
import urllib2

class QaldHandler(xml.sax.ContentHandler):
    answer_type = "plop"
    query_id = 0
    aggregation = False
    in_string = False
    string_content = ""

    def startElement(self, name, attrs):
        if name == "question":
            QaldHandler.answer_type = attrs['answertype']
            QaldHandler.query_id = attrs['id']
            QaldHandler.aggregation = attrs['aggregation']
            print 'question ' + QaldHandler.query_id
        if name == "string":
            QaldHandler.in_string = True

    def endElement(self, name):
        if name == "string":
            QaldHandler.string_content = QaldHandler.string_content.replace('\n','')
            print QaldHandler.string_content
            QaldHandler.in_string = False

            # SOAP web service
            d = dict(adaptedNlQuery=QaldHandler.string_content, getClass=False)
            gazetteer_result = gazetteer_client.service.getQueryWithGatheredNamedEntities(**d)
            print gazetteer_result

            # RESTfull web service            
            values = { "nlQuery":gazetteer_result, "lang":'en', "pos":'treeTagger', "dep":'malt' }
            data = urllib.urlencode(values)
            req = urllib2.Request('http://localhost:8080/NlToPivotStanford/resources/rest/translateQuery?' + data)
            response = urllib2.urlopen(req)
            pivot_Query = response.read()
            print pivot_Query + '\n'

            QaldHandler.string_content = ""



    def characters(self, content):
        if QaldHandler.in_string:
            QaldHandler.string_content += content

addClasses = False
gazetteer_url = 'http://swipserver:8080/NlToPivotGatePipeline/NlToPivotGatePipelineWS?wsdl'
gazetteer_client = Client(gazetteer_url)
parser = xml.sax.make_parser()
parser.setContentHandler(QaldHandler())
parser.parse(open("musicbrainz-train.xml","r"))