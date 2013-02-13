#!/usr/bin/env python
# encoding: utf-8

import json

if __name__ == '__main__':
	f = open('signs2.json')

	data = json.load(f)
	outfile = open(u'words2.json', 'w')

	l = []
	for entry in data:
		#Unpack the word-arrays
		strbuf = ''
		for word in entry['words']:
			strbuf += word['word']+', '
		strbuf = strbuf[:-2]
		#Add a dict with word and id to l
		l.append({'word':strbuf, 'id':entry['id']})

	#Dump the list as a json array
	json.dump(l, outfile)
	outfile.close()
	f.close()