PurePos
=======

PurePos is an open source Hidden Markov model based morphological tagger tool that has an interface to an integrated morphological analyzer and thus performs full disambiguated morphological analysis including stemming of words both known and unknown to the morphological analyzer. The tagger is implemented in Java using algorithms from HunPos implementation and has a permissive LGPL license thus it is easy to integrate and modify. It is fast to train and use while having an accuracy on par with HunPos and TnT and also with slow to train Maximum Entropy or Conditional Random Field based taggers. Full integration with morphology and an incremental training feature make it suited for integration in web based applications.

It is distributed under LGPL license.

Binaries
------------

Precompiled package, Hungarian model can be obtained from the [downloads page](https://github.com/ppke-nlpg/purepos/downloads).

Compiling
---------------

**Dependency:** Maven 2

1. Clone the repository
2. `mvn package`

Usage
---------

Trainig the tagger needs a text file, that has the following format:

* each sentence is in a new line,
* each word has a lemma and a POS tag: `word#lemma#tag`, that are separated with a space.

`java -jar purepos-<version>.jar train -m out.model -i tagged_input.txt`

For tagging, you must provide a file that has:

* sentences in new lines,
* words separated by a space.

`java -jar purepos-<version>.jar tag -m out.model -i input.txt`

For further help run the jar file with `-h`.

### Configuration file

With the `-f` option one can provide a configuration file that describes tag mappings.
A tag mapping is composed of two parts: a regular expression pattern and a replacement string.
If the tagger is informed by the analyzer about a theoretically possible but previously unseen morphosyntactic label, 
its probabilities are calculated according to a mapped tag. 

An example configuration file is below, that removes `|lat` substrings from tags.

    <?xml version="1.0" encoding="UTF-8" ?>
    <config>
    <mapping pattern="^(.*)(\|lat)(.*)$" to="$1FN$3" />
    </config>
    
### Preanalyzed input

The disambiguator can be pipelined with a morphological analyzer. For this the analyzer tool must provide an analyzed sentences per line where tokens are separated with spaces and have the following format (with or without the scores):

    token{{lemma1[tag1]$$score1||lemma2[tag2]$$score2}}
    token{{lemma1[tag1]||lemma2[tag2]}}
    
Scores are incorporated as lexical log probabilities.


Reference
---------------

If you use the tool, please cite the following paper: <br/>
[**György Orosz, Attila Novák**. (2012). *PurePos -- an open source morphological disambiguator.* Proceedings of the 9th International Workshop on Natural Language Processing and Cognitive Science.](https://github.com/downloads/ppke-nlpg/purepos/purepos.pdf)



[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/ppke-nlpg/purepos/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

