#!/usr/bin/env python
# encoding: utf-8

import json, io
from pprint import pprint


f = open("signs2.json")

data = json.load(f)
outfile = open(u'words2.json', "w")

l = []
for entry in data:
	strbuf = ""
	for word in entry["words"]:
		strbuf += word["word"]+", "
	strbuf = strbuf[:-2]
	l.append({"word":strbuf, "id":entry["id"]})
json.dump(l, outfile)
outfile.close()
f.close()