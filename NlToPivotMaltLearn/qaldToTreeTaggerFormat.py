import os
import string
import xml.sax
from nltk import wordpunct_tokenize
from suds.client import Client
import commands

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
#            for (k,v) in attrs.items():
#                print k + " " + v
        if name == "string":
            QaldHandler.in_string = True

    def endElement(self, name):
        if name == "string":
            QaldHandler.string_content = QaldHandler.string_content.replace('\n','')
            print QaldHandler.string_content
            QaldHandler.in_string = False

#            tokenized_list = wordpunct_tokenize(QaldHandler.string_content)
#            tokenized_sentence = ''
#            for word in tokenized_list:
#                tokenized_sentence += word + '\n'
#            print tokenized_sentence

            d = dict(adaptedNlQuery=QaldHandler.string_content)
            gazetteer_result = client.service.getQueryWithGatheredNamedEntities(**d)
            print gazetteer_result

#            while '(' in gazetteer_result:
#                iSpace = string.find(gazetteer_result, ' ')
#                iPar = string.find(gazetteer_result, '(')
#                if iSpace >= 0 and iSpace < iPar:
#                    gazetteer_result = gazetteer_result[iSpace+1:]
#                elif iPar >= 0:
#                    gazetteer = gazetteer_result[:iPar]
#                    iPar2 = string.find(gazetteer_result, ')')
#                    type = gazetteer_result[iPar+1:iPar2]
#                    tokenized_sentence = string.replace(tokenized_sentence, string.replace(gazetteer, '_', '\n'), gazetteer)
#                    gazetteer_result = gazetteer_result[iPar2+1:]
#                else:
#                    gazetteer_result = ''

            gazetteer_result_without_type = gazetteer_result

            while '(' in gazetteer_result_without_type:
                iPar1 = string.find(gazetteer_result_without_type, '(')
                iPar2 = string.find(gazetteer_result_without_type, ')')
                gazetteer_result_without_type = gazetteer_result_without_type[:iPar1] + gazetteer_result_without_type[iPar2+1:]

            # POS tagging with tree tagger
            command = "echo '" + gazetteer_result_without_type + "' | /mnt/data/treeTagger/cmd/tree-tagger-english"
            pos_tagged_sentence = commands.getoutput(command)
            #print command
            pos_tagged_sentence = pos_tagged_sentence[string.find(pos_tagged_sentence, '\n')+1:]
            pos_tagged_sentence = pos_tagged_sentence[string.find(pos_tagged_sentence, '\n')+1:]
            pos_tagged_sentence = pos_tagged_sentence[string.find(pos_tagged_sentence, '\n')+1:]
            print pos_tagged_sentence

            # adapt POS tagged query to maltparser input format
            # changes to make:
            # new column 1 created: ID
            # old column 1 -> new column 2 FORM
            # old column 3 -> new column 3 LEMMA
            # old column 2 -> new columns 4 & 5 CPOSTAG & POSTAG
            # new column 6 created: FEATS (always '_')
            adapted_pos_tagged_sentence = ''
            num_word = 1
            while len(pos_tagged_sentence) > 0:
                id_tab1 = string.find(pos_tagged_sentence, '\t')
                id_tab2 = string.find(pos_tagged_sentence, '\t', id_tab1+1)
                id_end_line = string.find(pos_tagged_sentence, '\n')
                if id_end_line < 0:
                    id_end_line = len(pos_tagged_sentence)
                form = pos_tagged_sentence[:id_tab1]
                postag = pos_tagged_sentence[id_tab1+1:id_tab2]
                lemma = pos_tagged_sentence[id_tab2+1:id_end_line]
                adapted_pos_tagged_sentence += str(num_word) + '\t' + form + '\t' + lemma + '\t' + postag + '\t' + postag + '\t_\n'
                num_word += 1
                if id_end_line == len(pos_tagged_sentence):
                    pos_tagged_sentence = ''
                else:
                    pos_tagged_sentence = pos_tagged_sentence[id_end_line+1:]
            print adapted_pos_tagged_sentence

            # write POS tagged query to be parsed by maltparser
            pos_tagged_filename = 'mb_train_pos_tagged_query/mb_train_pos_tagged_query_' + QaldHandler.query_id
            f = open(pos_tagged_filename,'w')
            f.write(adapted_pos_tagged_sentence)
            f.close()
            QaldHandler.string_content = ""

            # execute maltparser on POS tagged query
            dependency_filename = 'mb_train_dep_query/mb_train_dependency_query_' + QaldHandler.query_id
            command = "java -Xmx1024m -jar /mnt/data/maltParser/maltparser-1.7.1/maltparser-1.7.1.jar -c engmalt.poly-1.7 -i " + pos_tagged_filename + " -o " + dependency_filename + " -m parse"
            os.system(command)
            print command

            # add a column with the class of the named entity
#            final_sentence = ''
#            num_word = 0
#            while len(pos_tagged_sentence) > 0:
#                id_end_line = string.find(pos_tagged_sentence, '\n')
#                if id_end_line < 0:
#                    id_end_line = len(pos_tagged_sentence) - 1
#                id_tab = string.find(pos_tagged_sentence, '\t')
#                word = pos_tagged_sentence[:id_tab]
#                if '_' in word:
#                    id_word = string.find(gazetteer_result, word)
#                    word_class = gazetteer_result[id_word + len(word) + 1 : id_word + string.find(gazetteer_result[id_word:], ')')]
#                    final_sentence += str(num_word) + '\t' + pos_tagged_sentence[:id_end_line] + '\t' + word_class + '\n'
#                else:
#                    final_sentence += str(num_word) + '\t' + pos_tagged_sentence[:id_end_line] + '\t_\n'
#                pos_tagged_sentence = pos_tagged_sentence[id_end_line+1:]
#                num_word += 1
#            print final_sentence




    def characters(self, content):
        if QaldHandler.in_string:
            QaldHandler.string_content += content

addClasses = False
url = 'http://localhost:8080/NlToPivotGatePipeline/NlToPivotGatePipelineWS?wsdl'
client = Client(url)
parser = xml.sax.make_parser()
parser.setContentHandler(QaldHandler())
parser.parse(open("musicbrainz-train.xml","r"))