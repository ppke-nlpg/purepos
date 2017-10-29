package hu.ppke.itk.nlpg.purepos.common.lemma;

import junit.framework.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;

public class AbstractLemmaTransformationTest {

    @Test
    public void casingTest(){
        Assert.assertEquals(0,AbstractLemmaTransformation.checkCasing(".","."));
        Assert.assertEquals(0,AbstractLemmaTransformation.checkCasing(".",""));
        Assert.assertEquals(-1,AbstractLemmaTransformation.checkCasing("Minden","mind"));
        Assert.assertEquals(0,AbstractLemmaTransformation.checkCasing("Legnagyobb","nagy"));
        Assert.assertEquals(1,AbstractLemmaTransformation.checkCasing("bálint","Bálint"));
    }

}
