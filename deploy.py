#!/usr/bin/python3.2
# -*-coding:Utf-8 -*

from os import listdir
from os import chdir
from os.path import isdir
from os.path import getmtime
from os.path import exists
from subprocess import call

before = dict()
after = dict()
errCount = 0

for d in listdir():
	if d[0] != '.' and isdir(d):
		chdir(d)
		if exists('dist/' + d + '.war'):
			before[d] = getmtime('dist/' + d + '.war')
		else:
			before[d] = 0
		if call(['ant', 'dist']) == 0:
			after[d] = getmtime('dist/' + d + '.war')
		else:
			errCount += 1
		chdir('../')

if errCount == 0:
	for k in before.keys():
		if before[k] != after[k]:
			print('Deploying ' + k + '...')
			errCount += call(['cp ' + k + '/dist/' + k + '.war /opt/tomcat/webapps/'], shell=True)

if errCount == 0:
	print()
	print('----------------------')
	print('DEPLOYMENT SUCCESFUL')
	print('-------------------')
else:
	print()
	print('----------------------')
	print('DEPLOYMENT FAILED')
	print('-------------------')