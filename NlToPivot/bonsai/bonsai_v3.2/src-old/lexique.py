#!/usr/bin/env python -O
# -*- coding: iso-8859-15 -*-
#

#################
# LES CLITIQUES #
#################

# Liste des clitiques post-verbaux que l'on peut rencontrer en francais
clitiques_post_verbaux=["je","tu","il","elle","on","nous","vous","ils","elles","moi","toi","lui","leur","le","la","les","y","en"]
clitiques_post_verbaux.extend(["-je","-tu","-il","-elle","-on","-nous","-vous","-ils","-elles","-moi","-toi","-lui","-leur","-le","-la","-les","-y","-en"])
clitiques_post_verbaux.extend(["-t-il","-t-elle","-t-on","-t-ils","-t-elles"])

# Listes des clitiques selon les fonctions syntaxiques qu'ils peuvent entretenir avec le verbe
liste_aff2sujet = ["je","j'","tu","il","elle","ils","elles","on"]
liste_aff2cod = ["le","la","les","l'"]
liste_aff2coi = ["lui","y","en"]
#cas ambigues:
liste_aff2suj_cod_coi = ["nous","vous"]
liste_aff2cod_coi = ["me","m'","te","t'","moi","toi"]

#avec tiret:
liste_aff2sujet.append(["-je","-tu","-il","-t-il","-elle","-t-elle","-ils","-t-ils","-elles","-t-elles","-on","-t-on"])
liste_aff2cod.append(["-le","-la","-les"])
liste_aff2coi.append(["-lui","-y","-en"])
#cas ambigues:
liste_aff2suj_cod_coi.append(["-nous","-vous"])
liste_aff2cod_coi.append(["-moi","-toi"])

###############
# AUXILIAIRES #
###############

# Les différentes formes de "etre":
liste_etre = ['suis', 'es', 'est', 'sommes', 'êtes', 'sont', 'étais', 'était', 'étions', 'étiez', 'étaient', 'serai', 'seras', 'sera', 'serons', 'serez', 'seront', 'fus', 'fut', 'fûmes', 'fûtes', 'furent', 'sois', 'soit', 'soyons', 'soyez', 'soient', 'fusse', 'fusses', 'fût', 'fussions', 'fussiez', 'fussent', 'serais', 'serait', 'serions', 'seriez', 'seraient','étant']

# Les formes de "etre" en fonction du mode/temps:
liste_etre_ind = ["suis","es","est","sommes","êtes","sont","étais","était","étions","étiez","étaient"]
liste_etre_ind.extend(["Êtes","Étais","Était","Étions","Étiez","Étaient"])
liste_etre_ind.extend(["fus","fut","fûmes","fûtes","furent","serai","seras","sera","serons","serez","seront"])
liste_etre_cond = ["serais","serait","serions","seriez","seraient"]
liste_etre_inf = ["être","Être"]
liste_etre_ppart = ["été","Été"]
liste_etre_ppres = ["étant","Étant"]
liste_etre_subj = ["sois","soit","soyons","soyez","soient","fusse","fusses","fût","fussions","fussiez","fussent"]
liste_etre_imp = ["sois","soyons","soyez"]

# Les formes de "etre" utilisées pour construire le passif
liste_aux_pass = ['été', 'être']

# Toutes les formes de "avoir":
liste_avoir = ['ai', 'as', 'a', 'avons', 'avez', 'ont', 'avais', 'avait', 'avions', 'aviez', 'avaient','aurai', 'auras', 'aura', 'aurons', 'aurez', 'auront', 'eus', 'eut', 'eûmes', 'eûtes', 'eurent', 'aurais', 'aurait', 'aurions', 'auriez', 'auriont' ,'aie', 'aies', 'ait', 'ayons', 'ayez', 'aient', 'eusse', 'eusses', 'eût', 'eussions', 'eussiez', 'eussent','ayant']

# Toutes les formes de "faire":
liste_faire = ['fais', 'fait', 'faisons', 'faites', 'font', 'faisais', 'faisait', 'faisions', 'faisiez', 'faisaient','fis', 'fit', 'fîmes', 'fîtes', 'firent', 'ferai', 'feras', 'fera', 'ferons', 'ferez', 'feront','fasse', 'fasses', 'fassions', 'fassiez', 'fassent', 'fisse', 'fisses', 'fissions', 'fissiez', 'fissent','ferais', 'ferait', 'ferions', 'feriez', 'feraient','faisant','faire']

#######################
# VERBES ET AUX. ETRE #
#######################

liste_Ppart_auxEtre = ["allé", "allée", "allés", "allées","apparu", "apparue", "apparus", "apparues","arrivé", "arrivée", "arrivés", "arrivées",                  "décédé", "décédée", "décédés", "décédées","demeuré", "demeurée", "demeurés", "demeurées","devenu", "devenue", "devenus", "devenues","entré", "entrée", "entrés", "entrées","intervenu", "intervenue", "intervenus", "intervenues","mort", "morte", "morts", "mortes", "né", "née", "nés", "nées","parti", "partie", "partis", "parties","parvenu", "parvenue", "parvenus", "parvenues","redevenu", "redevenue", "redevenus", "redevenues","reparti", "repartie", "repartis", "reparties","resté", "restée", "restés", "restées","retombé", "retombée", "retombés", "retombées","revenu", "revenue",  "revenus",  "revenues","tombé", "tombée", "tombés", "tombées","venu", "venue",  "venus",  "venues"]

###############################
# VERBES ET ATTRIBUT DU SUJET #
###############################

copula_wordset = set(['parais','paraissaient','paraissais','paraissait','paraissant','paraisse','paraissent','paraisses','paraissez','paraissiez','paraissions','paraissons','paraît','paraîtra','paraîtrai','paraîtraient','paraîtrais','paraîtrait','paraîtras','paraître','paraîtrez','paraîtriez','paraîtrions','paraîtrons','paraîtront','paru','parue','parues','parurent','parus','parusse','parussent','parusses','parussiez','parussions','parut','parûmes','parût','parûtes','apparais','apparaissaient','apparaissais','apparaissait','apparaissant','apparaisse','apparaissent','apparaisses','apparaissez','apparaissiez','apparaissions','apparaissons','apparaît','apparaîtra','apparaîtrai','apparaîtraient','apparaîtrais','apparaîtrait','apparaîtras','apparaître','apparaîtrez','apparaîtriez','apparaîtrions','apparaîtrons','apparaîtront','apparu','apparue','apparues','apparurent','apparus','apparusse','apparussent','apparusses','apparussiez','apparussions','apparut','apparûmes','apparût','apparûtes','es','est','furent','fus','fusse','fussent','fusses','fussiez','fussions','fut','fûmes','fût','fûtes','sera','serai','seraient','serais','serait','seras','serez','seriez','serions','serons','seront','soient','sois','soit','sommes','sont','soyez','soyons','suis','étaient','étais','était','étant','étiez','étions','été','êtes','être','resta','restai','restaient','restais','restait','restant','restas','restasse','restassent','restasses','restassiez','restassions','reste','restent','rester','restera','resterai','resteraient','resterais','resterait','resteras','resterez','resteriez','resterions','resterons','resteront','restes','restez','restiez','restions','restons','restâmes','restât','restâtes','restèrent','resté','restée','restées','restés','demeura','demeurai','demeuraient','demeurais','demeurait','demeurant','demeuras','demeurasse','demeurassent','demeurasses','demeurassiez','demeurassions','demeure','demeurent','demeurer','demeurera','demeurerai','demeureraient','demeurerais','demeurerait','demeureras','demeurerez','demeureriez','demeurerions','demeurerons','demeureront','demeures','demeurez','demeuriez','demeurions','demeurons','demeurâmes','demeurât','demeurâtes','demeurèrent','demeuré','demeurée','demeurées','demeurés','sembla','semblai','semblaient','semblais','semblait','semblant','semblas','semblasse','semblassent','semblasses','semblassiez','semblassions','semble','semblent','sembler','semblera','semblerai','sembleraient','semblerais','semblerait','sembleras','semblerez','sembleriez','semblerions','semblerons','sembleront','sembles','semblez','sembliez','semblions','semblons','semblâmes','semblât','semblâtes','semblèrent','semblé','semblée','semblées','semblés','devenaient','devenais','devenait','devenant','devenez','deveniez','devenions','devenir','devenons','devenu','devenue','devenues','devenus','deviendra','deviendrai','deviendraient','deviendrais','deviendrait','deviendras','deviendrez','deviendriez','deviendrions','deviendrons','deviendront','devienne','deviennent','deviennes','deviens','devient','devinrent','devins','devinsse','devinssent','devinsses','devinssiez','devinssions','devint','devînmes','devînt','devîntes','redevenaient','redevenais','redevenait','redevenant','redevenez','redeveniez','redevenions','redevenir','redevenons','redevenu','redevenue','redevenues','redevenus','redeviendra','redeviendrai','redeviendraient','redeviendrais','redeviendrait','redeviendras','redeviendrez','redeviendriez','redeviendrions','redeviendrons','redeviendront','redevienne','redeviennent','redeviennes','redeviens','redevient','redevinrent','redevins','redevinsse','redevinssent','redevinsses','redevinssiez','redevinssions','redevint','redevînmes','redevînt','redevîntes'])

#########
# PIVOT #
#########

liste_relations_PIVOT = ['suj','obj','a_obj','aff','ats','ato','de_obj','mod','obj','p_obj','coord','dep_coord','aux_tps','aux_pass','aux_caus','det','ponct']
liste_relations_arg_pred_PIVOT = ['suj','obj','a_obj','aff','ats','ato','de_obj','mod','obj','p_obj']


########
# EASY #
########

liste_chunks_EASY = ['GP','GN','PV','NV','GA','GR']
liste_relations_EASY = ["suj_v","aux_v","cod_v","cpl_v","mod_v","mod_n","mod_a","mod_r","mod_p","ats","ato","comp","coord1","coord2","juxt","app"]

