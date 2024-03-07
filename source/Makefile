
#   Makefile for randomX package

JAVA = /usr/java

CLASSPATH = $(JAVA)/classes:..

SRC = randomX.java randomJava.java randomHotBits.java randomLCG.java \
      randomMCG.java randomLEcuyer.java

CLASS = $(SRC:.java=.class)

randomX:  $(CLASS)

.java.class:
	javac -classpath $(CLASSPATH) $*.java

clean:
	rm -f *.bak *.class
	rm -rf doc
	mkdir doc

RELEASE = $(SRC) Makefile

release:
	tar cfv /tmp/randomX.tar $(RELEASE)
	gzip /tmp/randomX.tar

html:
	javadoc -classpath $(CLASSPATH) -d doc randomX
	tools/yuck

images:
	( cd $(JAVA)/images ; tar cfv /tmp/ZZimages.tar . )
	rm -rf doc
	mkdir doc
	mkdir doc/images
	(cd doc/images ; tar xfv /tmp/ZZimages.tar )
	rm -f /tmp/ZZimages.tar
	touch randomX.java

.SUFFIXES:  .java .class
