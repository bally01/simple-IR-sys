# simple-IR-sys
Simple IR system on CACM set documents created for my Information Retrival and Websearch class as part of an assignment.
Contains programs to create an Inverted Index and programs to Search through the documents with keywords, sentences, and questions.

InvertIdx.java -- creates Inverted index for all doc in the set

Search.java -- is the search class for the queries

TestSearchInterface.java -- console interface for search

Stemmer.java -- opensourced stemming class for optional stemming component *not in use unless you uncomment my stemming component

*Note: TestInvert was a preliminary testing class not used in the search program

### Current Settings:
- stop word removal [ON]
- stemming [OFF] >commented out
- all docs with any relevance is returned (a.k.a no top-k)
- postings list is in order of docID
- I am using td-idf variation with log10 (idf = log(N/IDF); TF = log(F)+1)

### To use or test:
Compile all files,
run InvertIdx.java to create the Inverted Index,
run TestSearchInterface.java to search using your own queries


