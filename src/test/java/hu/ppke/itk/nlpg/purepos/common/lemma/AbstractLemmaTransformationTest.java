package hu.ppke.itk.nlpg.purepos.common.lemma;

import junit.framework.Assert;
import org.junit.Test;

public class AbstractLemmaTransformationTest {

    @Test
    public void casingTest(){
        Assert.assertEquals(0,Transformation.checkCasing(".","."));
        Assert.assertEquals(0,Transformation.checkCasing(".",""));
        Assert.assertEquals(-1,Transformation.checkCasing("Minden","mind"));
        Assert.assertEquals(0,Transformation.checkCasing("Legnagyobb","nagy"));
        Assert.assertEquals(1,Transformation.checkCasing("bálint","Bálint"));
    }

}
