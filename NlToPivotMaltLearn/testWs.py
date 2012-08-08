
from suds.client import Client

url = 'http://localhost:8080/NlToPivotGatePipeline/NlToPivotGatePipelineWS?wsdl'
client = Client(url)
line = 'Which groups was David Bowie a member of?'
d = dict(adaptedNlQuery=line)
result = client.service.getQueryWithGatheredNamedEntities(**d)

print result