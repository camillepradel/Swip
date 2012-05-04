#!/usr/bin/python
import sys
import os
from random import *
from XmlReader import XmlReader
from LabelledTree import *
from PennTreeBankReader import *

# File modified by djame to read full mrg file instead of directory (for both the ftb and the ptb)
#  the ftb is direclty read from ptb and not xml files
# computation of scoring modified (there was a bug there. See line 74)



# def sample(ftreebank,ptreebank):
#	  j = 0
#	  i = 0
#	  sample_indexes = set([])
#	  ftb_length = 0
#	  sample_length = 0
#	  while i < len(ftreebank) and j < len(ptreebank):
# 	ftb_length = ftb_length + int(ftreebank[i])
#  	if (int(ftreebank[i]) <= int(ptreebank[j])):
# 		   #print j
# 		   sample_indexes.add(j)
# 		   sample_length = sample_length + int(ptreebank[j])
# 	elif(int(ptreebank[j]) < int(ftreebank[i])):
# 		   while (int(ptreebank[j]) < int(ftreebank[i])):
# 		j = j + 1
# 		   #print j
# 		   sample_indexes.add(j)
# 		   sample_length = sample_length + int(ptreebank[j])
# 	i = i + 1
# 	j = j + 1
#	  ftb_true_length = ftb_length
#	  while i <	 len(ftreebank) :
# 	ftb_true_length =  ftb_true_length + int(ftreebank[i])
# 	i = i + 1
#	  ftb_avg_length = float(ftb_true_length)/float(len(ftreebank))
#	  sample_avg_length = float(sample_length)/float(len(sample_indexes))

#	  sys.stderr.write(str(ftb_true_length)+'\n')

#	  while ftb_avg_length > sample_avg_length or sample_length < ftb_true_length:
# 	i = len(ptreebank) - 1
# 	while (i >= 0 and i in set(sample_indexes)):
# 		i = i - 1
# 	sample_indexes.add(i)   
# 	sample_length = sample_length + int(ptreebank[i])
# 	sample_avg_length = float(sample_length)/float(len(sample_indexes))
# 	sys.stderr.write(str(sample_avg_length)+'\n')
#	  for k in sample_indexes:
# 	print k

def sample(ftreebank,ptreebank):
	j = 0
	i = 0
	
	ftb_true_length = 0
	while i <  len(ftreebank):
		ftb_true_length =	ftb_true_length + int(ftreebank[i])
		i = i + 1
	ftb_avg_length = float(ftb_true_length)/float(len(ftreebank))
	sys.stderr.write("FTB avg length : "+str(ftb_avg_length)+'\n')

	#Iterative algorithm
	i = 0
	sample_indexes = set([])
	sample_length = 0
	sample_avg_length = 0
	while sample_length < ftb_true_length:
		if i < len(ptreebank):
			if i not in sample_indexes:
				score = random()
				#Djame  : ici bug chelou : float(float(ftb_avg_length) /float(ftb_avg_length)) ca fait 1
				# devrait etre float(float(sample_avg_length) /float(ftb_avg_length))
				modif = float(ptreebank[i]) - float(float(sample_avg_length) /float(ftb_avg_length))
				score = float(score) + float(modif)*5

  #				  if sample_avg_length < ftb_avg_length and ptreebank[i] < ftb_avg_length:
#					  score = score - 0.5
#				  elif sample_avg_length < ftb_avg_length and ptreebank[i] > ftb_avg_length:
#					  score = score + 0.5
#				  elif sample_avg_length > ftb_avg_length and ptreebank[i] < ftb_avg_length:
#					  score = score + 0.5
#				  elif sample_avg_length > ftb_avg_length and ptreebank[i] > ftb_avg_length:
#					  score = score - 0.5
				if score > 0.5: 
					sample_indexes.add(i)
					sample_length = sample_length + ptreebank[i]
					sample_avg_length = float(sample_length)/float(len(sample_indexes))
					sys.stderr.write("Sample avg length : "+str(ftb_avg_length)+"\nSample total length : "+str(sample_length)+'\n')
				i = i + 1
		else:
			i = 0
	sample_list = list(sample_indexes)
	sample_list.sort()
	return sample_list

def length_vector_ptb(inputfile1):
	# now take a file instead of a dir
	reader = PtbReader()
	#files = os.listdir(inputfile1)
	res = []
	src_treebank = reader.read_mrg(open(inputfile1))
	for tree in src_treebank:
		tree.remove_traces_ptb()
		tree.remove_ptb_annotations()
		tree = tree.add_dummy_root()	 
		res.append(len(tree.tree_yield()))
	return res

def length_vector_ftb(input_file):
	# also takes a file and not a dir
	# in a ptb format as well
	reader = PtbReader()
	treebank =	reader.read_mrg(open(input_file))
	res = []
	for tree in treebank:
		# transformation not necessary as we're reading already
		#transormed treebank
		#tree.merge_num()
		#tree.merge_cpds()
		res.append(len(tree.tree_yield()))
	return res


ftbfile = sys.argv[1]
ptbfile = sys.argv[2]

ftb = length_vector_ftb(ftbfile)
ptb = length_vector_ptb(ptbfile)

idxes = sample(ftb,ptb)
for idx in idxes:
	print idx
out = open(ptbfile+".idx4ftb","w")
for idx in idxes:
	out.write(str(ptb[idx])+'\n')
out.close()
