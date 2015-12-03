PurePos
=======
PurePos is an open-source HMM-based automatic morphological annotation tool. 
It can perform tagging and lemmatization at the same time, it is very fast to train, with the possibility of easy integration of symbolic rule-based components into the annotation process that can be used to boost the accuracy of the tool. 
The hybrid approach implemented in PurePos is especially beneficial in the case of rich morphology, highly detailed annotation schemes and if a small amount of training data is available. 

It is distributed under the permissive LGPL license.

Compiling
---------------

**Dependencies:** Maven 2

1. Clone the repository
2. `$ mvn package -DskipTests`

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

### Java

For details please check the Demo.java file: https://github.com/ppke-nlpg/purepos/blob/master/src/main/java/hu/ppke/itk/nlpg/purepos/Demo.java

### Python

A wrapper package for Python is available at https://github.com/ppke-nlpg/purepos.py


### Configuration file

One can provide a configuration file with `-f`. 

First of all, the user can describe mappings of morphosyntactic tags.
A mapping is composed of two parts: a regular expression pattern and a replacement string. For details check our paper (Orosz et al. 2013).

An example configuration file which maps Latin HuMor tags (with `|lat`) to standard ones:

        <?xml version="1.0" encoding="UTF-8" ?>
        <config>
        <tag_mapping pattern="^(.*)(\|lat)(.*)$" to="$1$3" />
    	</config>
    
Mapping of lemmata is also possible with config files. One can e.g. use a `"+"` character to mark boundaries of compound words.  Utilizing the  configuration below:

1. the "+" is omitted from the lemmata during training (e.g. `word+form#word+form#NN` is learnt as `word+form#wordform#NN`)
2. if the preanalyzed input has any lemma separated with the marker character, corrresponding probabilites are calculated through mapping, while the output will remain as in the preanylyzed input. (e.g. `word+form{{word+form[NN]}}` is processed as calculating lemma probabilites for the lemma `wordform` while the output of the tagger will be `word+form#wordform#[NN]`)
        
    	<?xml version="1.0" encoding="UTF-8" ?>
    	<config>
    	<lemma_mapping pattern="[+]" to="" /> 
    	</config>
    	
PurePos is also able to mark analyses that are unknown to the analyzer used. For this one can use a marker character. An example configuration file for utilizing the ``*`` character as a marker is:

    	<?xml version="1.0" encoding="UTF-8" ?>
    	<config>
    	<guessed_marker>*</guessed_marker>
    	</config>
	
One can tune the the lemmatization model by assigning a fixed ``w`` weight to the suffix model. (In this case the score of the unigram model will be ``1-w``). An example for this:

    	<?xml version="1.0" encoding="UTF-8" ?>
    	<config>
    	<suffix_model_weight>1.0</suffix_model_weight>
    	</config>
    
### Preanalyzed input

The disambiguator tool can be pipelined with a morphological analyzer or guesser. For this, the analyzer must provide analyses in the following format (with or without the scores):

        token{{lemma1[tag1]$$score1||lemma2[tag2]$$score2}}
        token{{lemma1[tag1]||lemma2[tag2]}}
    
Scores given in the input are incorporated as lexical log-probabilities.
Please note, that the brackets will be part of the tag. Therefore, inputting `houses{{house[NNS]}}` will result in `houses#house#[NNS]`.

References
---------------

If you use the tool, please cite the following papers: <br/>

* [**PurePos 2.0: a hybrid tool for morphological disambiguation.** Orosz, G.; and Novák, A. *In Proceedings of the International Conference on Recent Advances in Natural Language Processing (RANLP 2013)*, page 539–545, Hissar, Bulgaria, 2013. INCOMA Ltd. Shoumen, BULGARIA.](http://aclweb.org/anthology//R/R13/R13-1071.pdf)

* [**PurePos – an open source morphological disambiguator.** Orosz, G.; and Novák, A. In Sharp, B.; and Zock, M., editor, *In Proceedings of the 9th International Workshop on Natural Language Processing and Cognitive Science*, page 53–63, Wroclaw, 2012. ](https://github.com/downloads/ppke-nlpg/purepos/purepos.pdf)

