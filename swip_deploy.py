#!/usr/bin/python3.2
# -*-coding:Utf-8 -*

from subprocess import call
from os import listdir
from os import chdir
from os.path import isdir
from sys import argv

if len(argv) > 1:
	err_count = 0
	d = argv[1]

	if isdir(d):
		chdir(d)
		err_count += call(['ant', 'dist'])
		chdir('../')
	else:
		print(d, " is not a valid directory")
		err_count += 1

	if err_count == 0:
		print()
		print("Copying files to tomcat...")
		print()
		err_count += call(['cp ' + d + '/dist/*.war /opt/tomcat/webapps/'], shell=True)

	if err_count == 0:
		print()
		print("==================")
		print("SUCCESS")
		print("==================")
	else:
		print()
		print("==================")
		print("FAIL")
		print("==================")

else:
	err_count = 0

	for d in listdir():
		if d[0] != '.' and isdir(d):
			chdir(d)
			err_count += call(['ant', 'dist'])
			chdir('../')

	if err_count == 0:
		print()
		print("Copying files to tomcat...")
		print()
		err_count += call(['cp */dist/*.war /opt/tomcat/webapps/'], shell=True)

	if err_count == 0:
		print()
		print("==================")
		print("SUCCESS")
		print("==================")
	else:
		print()
		print("==================")
		print("FAIL")
		print("==================")
