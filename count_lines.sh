#!/bin/bash
echo Source Files
wc -l src/Main2.java src/XmlIdent.java src/XmlTestBuilder.java src/XmlVisitor.java \
src/gr/omadak/leviathan/asp/*.java \
src/gr/omadak/leviathan/asp/objects/*.java \
tests/gr/omadak/leviathan/asp/*.java \
grama/*.g grama/*.act | grep total
echo Generated Files
wc -l build/src/gr/omadak/leviathan/asp/*.java | grep total
echo Xml Files
wc -l objects/*.xml | grep total
