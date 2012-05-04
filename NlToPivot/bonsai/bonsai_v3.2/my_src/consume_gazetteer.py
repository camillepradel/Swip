#!/usr/bin/env python

import fileinput
from suds.client import Client

url = 'http://localhost:8080/NlToPivotGatePipeline/NlToPivotGatePipelineWS?wsdl'
client = Client(url)
result = ""
for line in fileinput.input():
	d = dict(adaptedNlQuery=line)
	result += client.service.getQueryWithGatheredNamedEntities(**d)

print result
