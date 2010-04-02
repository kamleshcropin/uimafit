
/* First created by JCasGen Fri Apr 02 09:46:36 MDT 2010 */
package org.uutuc.tutorial.type;

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.FSGenerator;
import org.apache.uima.cas.FeatureStructure;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.Type;
import org.apache.uima.cas.impl.FeatureImpl;
import org.apache.uima.cas.Feature;
import org.apache.uima.jcas.tcas.Annotation_Type;

/** 
 * Updated by JCasGen Fri Apr 02 11:08:51 MDT 2010
 * @generated */
public class DateTimeAnnotation_Type extends Annotation_Type {
  /** @generated */
  protected FSGenerator getFSGenerator() {return fsGenerator;}
  /** @generated */
  private final FSGenerator fsGenerator = 
    new FSGenerator() {
      public FeatureStructure createFS(int addr, CASImpl cas) {
  			 if (DateTimeAnnotation_Type.this.useExistingInstance) {
  			   // Return eq fs instance if already created
  		     FeatureStructure fs = DateTimeAnnotation_Type.this.jcas.getJfsFromCaddr(addr);
  		     if (null == fs) {
  		       fs = new DateTimeAnnotation(addr, DateTimeAnnotation_Type.this);
  			   DateTimeAnnotation_Type.this.jcas.putJfsFromCaddr(addr, fs);
  			   return fs;
  		     }
  		     return fs;
        } else return new DateTimeAnnotation(addr, DateTimeAnnotation_Type.this);
  	  }
    };
  /** @generated */
  public final static int typeIndexID = DateTimeAnnotation.typeIndexID;
  /** @generated 
     @modifiable */
  public final static boolean featOkTst = JCasRegistry.getFeatOkTst("org.uutuc.tutorial.type.DateTimeAnnotation");
 
  /** @generated */
  final Feature casFeat_shortDateString;
  /** @generated */
  final int     casFeatCode_shortDateString;
  /** @generated */ 
  public String getShortDateString(int addr) {
        if (featOkTst && casFeat_shortDateString == null)
      jcas.throwFeatMissing("shortDateString", "org.uutuc.tutorial.type.DateTimeAnnotation");
    return ll_cas.ll_getStringValue(addr, casFeatCode_shortDateString);
  }
  /** @generated */    
  public void setShortDateString(int addr, String v) {
        if (featOkTst && casFeat_shortDateString == null)
      jcas.throwFeatMissing("shortDateString", "org.uutuc.tutorial.type.DateTimeAnnotation");
    ll_cas.ll_setStringValue(addr, casFeatCode_shortDateString, v);}
    
  



  /** initialize variables to correspond with Cas Type and Features
	* @generated */
  public DateTimeAnnotation_Type(JCas jcas, Type casType) {
    super(jcas, casType);
    casImpl.getFSClassRegistry().addGeneratorForType((TypeImpl)this.casType, getFSGenerator());

 
    casFeat_shortDateString = jcas.getRequiredFeatureDE(casType, "shortDateString", "uima.cas.String", featOkTst);
    casFeatCode_shortDateString  = (null == casFeat_shortDateString) ? JCas.INVALID_FEATURE_CODE : ((FeatureImpl)casFeat_shortDateString).getCode();

  }
}



    