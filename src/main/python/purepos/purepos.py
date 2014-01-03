#!/usr/bin/env python
# -*- coding: utf-8 -*-
# Copyright (C) György Orosz
# Author: György Orosz (oroszgy@itk.ppke.hu)
# URL: <http://github.com/ppke-nlpg/purepos>
# For license information, see LICENSE.TXT

"""
A Python 2.x interface for PurePos.
"""

import os, sys
from subprocess import Popen, PIPE

from StringIO import StringIO


_purepos_url = 'https://github.com/ppke-nlpg/purepos'
_purepos_encoding = "UTF-8"
_purepos_version = "2.0"
_purepos_bin = "purepos-" + _purepos_version +".one-jar.jar"

_base_dir = os.path.dirname( os.path.abspath( __file__) )
_purepos_dir = _base_dir
_szeged_model = os.path.join(_base_dir, "szeged.model")
sys.path.append(_base_dir)

def triplet2txt(triplet):
        return u"#".join(triplet)
    
def txt2triplet(txt):
    return tuple(txt.split(u"#"))
    
def annot2txt(annot):
    token = annot[0]
    anals = annot[1]
    txt = StringIO()
    txt.write(token)
    if anals:
        txt.write(u"{{")
        for anal in anals:
            if len(annot)>2 and not annot[2]:
                txt.write(u"*")
            txt.write(anal[0])
            txt.write(anal[1])
            if len(anal)>2 and anal[2]:
                txt.write(u"$$")
                txt.write(anal[2])
            txt.write(u"||")
        txt.write(u"}}")
        out = txt.getvalue()[:-4]+u"}}"
    else:
        out = txt.getvalue()
    return out

def parse_scoredsent(txt):
    s_pos = txt.find(u"$$")
    score = float(txt[s_pos+2:-2])
    rest = txt[:s_pos]
    tokens = rest.split(u" ")
    tokens = map(txt2triplet, tokens)
    return (tokens, score)

class PurePosBase(object):
    
    def __init__(self, model_path, mode, options=None,
                 encoding = None, verbose=False):    
        model_path = model_path or _szeged_model
        self._encoding = encoding or _purepos_encoding
        self.verbose = verbose
        _options = options or []
        
        self._purepos_bin = os.path.abspath(os.path.join(_purepos_dir, _purepos_bin))
        self._cmd = ["java", "-Djava.library.path="+_base_dir, "-Dhumor.path="+_base_dir+"",
        "-jar", self._purepos_bin, mode, "-m", os.path.abspath(model_path)] + _options

        if verbose: pstderr = None
        else: pstderr = open(os.devnull, 'wb')
        
        self._purepos = Popen(self._cmd, shell=False, stdin=PIPE, stdout=PIPE,
                                stderr=pstderr)
        self._closed = False

    def __del__(self):
        self.close()

    def close(self):
        if not self._closed:
            self._purepos.communicate()
            self._closed = True

    def __enter__(self):
        return self
        
#    def __exit__(self, exc_type, exc_value, traceback):
#        self.close()
        

class PurePosTrainer(PurePosBase):
    
    def __init__(self, model_path, train_text=None, 
                 encoding=None, verbose=False):
        PurePosBase.__init__(self, model_path, "train", [], encoding, verbose)
        if train_text:
            self.train(train_text, finalize=True)
            
    def train(self, text, finalize=True):
        self._train(text)
        if finalize:
            self.finalize_training()
            
    def _train(self, sents):
        """
        Text is a list of sentences. A sentence is a list of (word, lemma, tag) tuples.
        """
        
        txt = u"\n".join([ u" ".join(map(triplet2txt, sent)) for sent in sents])
        if isinstance(txt, unicode):
            txt = txt.encode(self._encoding)
        self._purepos.stdin.write(txt)
        self._purepos.stdin.write("\n")
        self._purepos.stdin.flush()
    
    def finalize_training(self):
        self.close()
            
class PurePosTagger(PurePosBase):

    def __init__(self, model_path=_szeged_model, multi_tag=None, encoding=None, verbose=False):
        options = []
        self._multitag=bool(multi_tag)
        if multi_tag:
            options =  ["-n", str(multi_tag), "-d"]
#            options =  ["-n", str(multi_tag),]# "-b", "1000", "-d"]
        PurePosBase.__init__(self, model_path, "tag", options, encoding, verbose)
        


    def tag(self, tokens):
        """
        Tags a single sentence: a list of words.
        The tokens should not contain any newline characters.
        """
        out = StringIO()
        assert type(tokens) is list
        for token in tokens:
            token_str = token
            if isinstance(token, tuple):
                token_str = annot2txt(token)
                
            if isinstance(token_str, unicode):
                token_str = token_str.encode(self._encoding)
            elif isinstance(token_str, str):
                pass
            else: raise Exception("Unkwon input format: %s"%str(token))
            assert "\n" not in token, "Tokens should not contain newlines"
            out.write(token_str)
            out.write(" ")
                
        out = out.getvalue()[:-1]
        try:
            self._purepos.stdin.write(out)
            self._purepos.stdin.write("\n")
            self._purepos.stdin.flush()
            out = self._purepos.stdout.readline().strip().decode(self._encoding)
        except:
            sys.stderr.write(repr(out) + "\n")
            raise
        if not self._multitag:
            ret = map(txt2triplet, out.split(u" "))
        else:
            sents = out.split(u"\t")
            ret = map(parse_scoredsent, sents)
            
        return ret 


if __name__ == "__main__":
    p = PurePosTagger(sys.argv[1], verbose=False)
    print 'Enter tokens to tag, blank line to end sentence, Ctrl-C to exit'
    sent = []
    while True:
        inp = sys.stdin.readline().rstrip()
        print p.tag(inp.decode("utf8"))

