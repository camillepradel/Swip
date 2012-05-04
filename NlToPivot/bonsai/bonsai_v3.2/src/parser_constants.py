#!/usr/bin/env python
# -*- coding: UTF-8 -*-


def encode_const_metasymbols(word_form,ext=False):
    """
    This functions recodes reserved symbols occuring on the input of constituent parsing with specific reserved strings to avoid confusion between symbols and metasymbols. The conversion is made for penn treebank encoding and extended penn treebank encoding.
    
    @param word_form: the string to be encoded
    @type word_form: string
    @param ext: set to true it switches to a recoding for extended penn treebank encoding set to false, it does standard penn treebank encoding
    @type ext: boolean
    @return:the encoded string
    @rtype:string
    """
    if ext:
        return word_form.replace(",","<C>").replace("(","<LBR>").replace(")","<RBR>").replace("[","<LSBR>").replace("]","<RSBR>").replace('-','<MIN>').replace('=','<EQ>')
    else:
#        return word_form.replace("(","<LBR>").replace(")","<RBR>")
        return word_form.replace("(","-LRB-").replace(")","-RRB-")


def decode_const_metasymbols(word_form,ext=False):
    """
    This functions reverts back reserved symbols encoded with metachars to their original form.
    
    @param word_form:the string to be decoded 
    @type word_form:string
    @param ext: set to true it switches to a decoding for extended penn treebank encoding set to false, it does standard penn treebank encoding
    @type ext: boolean
    @return:a decoded string
    @rtype:string
    """
    if ext:
        return word_form.replace("<C>",',').replace("<LBR>",'(').replace("<RBR>",')').replace("<LSBR>",'[').replace("<RSBR>",']').replace('<MIN>','-').replace('<EQ>','=').replace('<D>','-')
    else:
        #return word_form.replace("-LRB-",'(').replace("-RRB-",')')
        # backward compatibility
        return word_form.replace("-LRB-",'(').replace("-RRB-",')').replace('<RBR>',')').replace('<LBR>','(')


GramForms = {'avoir': ['avoir','ai', 'as', 'a', 'avons', 'avez', 'ont', 'avais', 'avait', 'avions', 'aviez', 'avaient', 
                       'aurai', 'auras', 'aura', 'aurons', 'aurez', 'auront', 
                       'eus', 'eut', 'eûmes', 'eûtes', 'eurent', 'aurais', 'aurait', 'aurions', 'auriez', 'auriont' 
                       'aie', 'aies', 'ait', 'ayons', 'ayez', 'aient', 'eusse', 'eusses', 'eût', 'eussions', 'eussiez', 'eussent', 
                       'ayant'],
             'etre': ['suis', 'es', 'est', 'sommes', 'êtes', 'sont', 'étais', 'était', 'étions', 'étiez', 'étaient', 
                      'serai', 'seras', 'sera', 'serons', 'serez', 'seront', 'fus', 'fut', 'fûmes', 'fûtes', 'furent', 
                      'sois', 'soit', 'soyons', 'soyez', 'soient', 'fusse', 'fusses', 'fût', 'fussions', 'fussiez', 'fussent', 
                      'serais', 'serait', 'serions', 'seriez', 'seraient', 
                      'étant','être'],
             'faire': ['fais', 'fait', 'faisons', 'faites', 'font', 'faisais', 'faisait', 'faisions', 'faisiez', 'faisaient', 
                       'fis', 'fit', 'fîmes', 'fîtes', 'firent', 'ferai', 'feras', 'fera', 'ferons', 'ferez', 'feront', 
                       'fasse', 'fasses', 'fassions', 'fassiez', 'fassent', 'fisse', 'fisses', 'fissions', 'fissiez', 'fissent',
                       'ferais', 'ferait', 'ferions', 'feriez', 'feraient',
                       'faisant', 
                       'faire'],
             'etre_pp': ['allé', 'allée', 'allés", allées', 'apparu', 'apparue', 'apparus', 'apparues', 
                         'arrivé', 'arrivée', 'arrivés', 'arrivées', 'décédé', 'décédée', 'décédés', 'décédées',  
                         'demeuré', 'demeurée', 'demeurés', 'demeurées', 'devenu', 'devenue', 'devenus', 'devenues',  
                         'entré', 'entrée', 'entrés', 'entrées', 'intervenu', 'intervenue', 'intervenus', 'intervenues',  
                         'mort', 'morte', 'morts', 'mortes', 'né', 'née', 'nés', 'nées', 
                         'parti', 'partie', 'partis', 'parties', 'parvenu', 'parvenue', 'parvenus', 'parvenues',  
                         'apparu', 'apparue', 'apparus', 'apparues', 'apparu', 'apparue', 'apparus', 'apparues',
                         'redevenu', 'redevenue', 'redevenus', 'redevenues', 'reparti', 'repartie', 'repartis', 'reparties' 
                         'resté', 'restée', 'restés', 'restées', 'retombé', 'retombée', 'retombés', 'retombées', 
                         'revenu', 'revenue',  'revenus',  'revenues', 'tombé', 'tombée', 'tombés', 'tombées', 
                         'venu', 'venue',  'venus',  'venues']
             }
