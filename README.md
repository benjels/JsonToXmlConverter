# TroveJsonProcessor

This program processes the json files that are used to store the content for Trove newspaper archives.

In the Trove data, each issue of the newspaper is represented by a .json file that contains several json objects inside. Each of those json objects represents an article that was in that newspaper. There are normally approx. 10-30 of these articles per newspaper.

The program splits each article's json file into several smaller json files. One for each article. Then it creates an xml version of each of these json files using the json.org java library (http://www.json.org/java/). This is because the GATE natural text processing API only works with the XML and HTML formats. It would definitely be possibly to just supply GATE with the original json as "unformatted plain text", but it is nice to keep the semantic tags that were included by the author of the original json files.

