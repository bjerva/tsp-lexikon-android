#!/usr/bin/env python
# encoding: utf-8

import json

if __name__ == '__main__':
	f = open('../assets/signs2.json')

	data = json.load(f)

	#Split each json object into a new file
	for entry in data:
		outfile = open(unicode(str(entry['id'])+'.json'), 'w')
		json.dump(entry, outfile)
		outfile.close()

	#Assert everything went well
	for entry in data:
		small_f = open(str(entry['id'])+'.json')
		small_d = json.load(small_f)
		small_f.close()
		assert small_d == entry