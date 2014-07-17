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

    Usage: java -jar <purepos.jar> [options...] arguments...
     tag|train                        : Mode selection: train for training the
                                        tagger, tag for tagging a text with the
                                        given model.
     -a (--analyzer) <analyzer>       : Set the morphological analyzer. <analyzer>
                                        can be 'none', 'integrated' or a file :
                                        <morphologicalTableFile>. The default is to
                                        use the integrated one. Tagging only option.
     -b (--beam-theta) <theta>        : Set the beam-search limit. The default is
                                        1000. Tagging only option.
     -c (--encoding) <encoding>       : Encoding used to read the training set, or
                                        write the results. The default is your OS
                                        default.
     -d (--beam-decoder)              : Use Beam Search decoder. The default is to
                                        employ the Viterbi algorithm. Tagging only
                                        option.
     -e (--emission-order) <number>   : Order of emission. First order means that
                                        the given word depends only on its tag. The
                                        default is 2.  Training only option.
     -f (--config-file) <file>        : Configuratoin file containg tag mappings.
                                        Defaults to do not map any tag.
     -g (--max-guessed) <number>      : Limit the max guessed tags for each token.
                                        The default is 10. Tagging only option.
     -h (--help)                      : Print this message.
     -i (--input-file) <file>         : File containg the training set (for
                                        tagging) or the text to be tagged (for
                                        tagging). The default is the standard input.
     -m (--model) <modelfile>         : Specifies a path to a model file. If an
                                        exisiting model is given for training, the
                                        tool performs incremental training.
     -n (--max-results) <number>      : Set the expected maximum number of tag
                                        sequences (with its score). The default is
                                        1. Tagging only option.
     -o (--output-file) <file>        : File where the tagging output is redirected.
                                        Tagging only option.
     -r (--rare-frequency) <treshold> : Add only words to the suffix trie with
                                        frequency less than the given treshold. The
                                        default is 10.  Training only option.
     -s (--suffix-length) <length>    : Use a suffix trie for guessing unknown
                                        words tags with the given maximum suffix
                                        length. The default is 10.  Training only
                                        option.
     -t (--tag-order) <number>        : Order of tag transition. Second order means
                                        trigram tagging. The default is 2. Training
                                        only option.


Python interface is also provided, for details check [the module](https://github.com/ppke-nlpg/purepos/blob/master/src/main/python/purepos/purepos.py) or [the demo](https://github.com/ppke-nlpg/purepos/blob/master/src/main/python/purepos/demo.py).


### Configuration file

One can provide a configuration file with `-f` that describes mappings of morphosyntactic tags.
A mapping is composed of two parts: a regular expression pattern and a replacement string.
For details check our paper (Orosz et al. 2013).

An example configuration file which maps Latin HuMor tags (with `|lat`) to standard ones:

    <?xml version="1.0" encoding="UTF-8" ?>
    <config>
    <tag_mapping pattern="^(.*)(\|lat)(.*)$" to="$1$3" />

    
Mapping of lemma strings is also possible. An example for deleting ``+`` and ``*`` characters is:
    
	<?xml version="1.0" encoding="UTF-8" ?>
	<config>
	<lemma_mapping pattern="[+*]" to="" /> 
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

