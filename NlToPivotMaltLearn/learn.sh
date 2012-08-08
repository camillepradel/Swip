#learn
java -jar /mnt/data/maltParser/maltparser-1.7.1/maltparser-1.7.1.jar -c trained_model -i mb_train_pivot_query/mb_train_dependency_query_concat -m learn 

java -jar /mnt/data/maltParser/maltparser-1.7.1/maltparser-1.7.1.jar -c trained_model -i mb_train_pivot_query/mb_train_dependency_query_concat -m learn -if /mnt/data/gitSwip/NlToPivotMaltLearn/conllx-learn.xml -F /mnt/data/gitSwip/NlToPivotMaltLearn/NivreEager-learn.xml


#parse
java -jar /mnt/data/maltParser/maltparser-1.7.1/maltparser-1.7.1.jar -c trained_model -i mb_train_dep_query/mb_train_dependency_query_3 -o out.conll -m parse

