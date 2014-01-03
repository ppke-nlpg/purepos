PurePos
=======
PurePos is an open-source HMM-based automatic morphological annotation tool. 
It can perform tagging and lemmatization at the same time, it is very fast to train, with the possibility of easy integration of symbolic rule-based components into the annotation process that can be used to boost the accuracy of the tool. 
The hybrid approach implemented in PurePos is especially beneficial in the case of rich morphology, highly detailed annotation schemes and if a small amount of training data is available. Evaluation of the tool was on a Hungarian corpus revealed that its hybrid components significantly improve overall annotation accuracy.

It is distributed under the permissive LGPL license.

Compiling
---------------

**Dependencies:** Maven 2

1. Clone the repository
2. `$ mvn package`

Usage
---------

Trainig the tagger needs a corpus with  the following format:
* sentences are separated in new lines, while there are spaces between tagged words,
* each token in a sentence must be annotated with its lemma and POS tag separated by a hashmark: `word#lemma#tag`.

`$ java -jar purepos-<version>.jar train -m out.model [-i tagged_input.txt]`

The raw text file which is to be tagged must contain:
* sentences in new lines,
* and words separated by spaces.

`$ java -jar purepos-<version>.jar tag -m out.model [-i input.txt] [-o output.txt]`

For further help:

`$ java -jar purepos-<version>.jar -h`

### Configuration file

One can provide a configuration file with `-f` that describes mappings of morphosyntactic tags.
A mapping is composed of two parts: a regular expression pattern and a replacement string.
For details check our paper (Orosz et al. 2013).

An example configuration file which maps Latin HuMor tags (with `|lat`) to standard ones:

    <?xml version="1.0" encoding="UTF-8" ?>
    <config>
    <mapping pattern="^(.*)(\|lat)(.*)$" to="$1$3" />
    </config>
    
### Preanalyzed input

The disambiguator tool can be pipelined with a morphological analyzer or guesser. For this, the analyzer tool must provide analyses in the following format (with or without the scores):

    token{{lemma1[tag1]$$score1||lemma2[tag2]$$score2}}
    token{{lemma1[tag1]||lemma2[tag2]}}
    
Scores given in the input are incorporated as lexical log-probabilities.

Reference
---------------

If you use the tool, please cite the following papers: <br/>
* [**PurePos 2.0: a hybrid tool for morphological disambiguation.** Orosz, G.; and Novák, A. *In Proceedings of the International Conference on Recent Advances in Natural Language Processing (RANLP 2013)*, page 539–545, Hissar, Bulgaria, 2013. INCOMA Ltd. Shoumen, BULGARIA.](http://aclweb.org/anthology//R/R13/R13-1071.pdf)

* [**PurePos – an open source morphological disambiguator.** Orosz, G.; and Novák, A. In Sharp, B.; and Zock, M., editor, *In Proceedings of the 9th International Workshop on Natural Language Processing and Cognitive Science*, page 53–63, Wroclaw, 2012. ](https://github.com/downloads/ppke-nlpg/purepos/purepos.pdf)


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/ppke-nlpg/purepos/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

