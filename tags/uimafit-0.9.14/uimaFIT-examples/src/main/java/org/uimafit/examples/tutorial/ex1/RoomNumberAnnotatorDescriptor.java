package org.uimafit.examples.tutorial.ex1;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.TypeSystemDescription;
import org.uimafit.examples.tutorial.type.RoomNumber;
import org.uimafit.factory.AnalysisEngineFactory;
import org.uimafit.factory.TypeSystemDescriptionFactory;

/**
 * This class provides a main method which shows how to generate an xml
 * descriptor file using the RoomNumberAnnotator class definition. The resulting
 * XML descriptor file is the same as the one provided in the uimaj-examples
 * except that instead of building the file in parallel with the class
 * definition, it is now built completely by using the class definition.
 * 
 * @author Philip Ogren
 * 
 */
public class RoomNumberAnnotatorDescriptor {

	public static AnalysisEngineDescription createDescriptor() throws ResourceInitializationException {
		TypeSystemDescription typeSystemDescription = TypeSystemDescriptionFactory.createTypeSystemDescription(RoomNumber.class);
		return AnalysisEngineFactory.createPrimitiveDescription(RoomNumberAnnotator.class, typeSystemDescription);
	}

	public static void main(String[] args) throws Exception {
		File outputDirectory = new File("src/main/resources/org/uimafit/examples/tutorial/ex1/");
		outputDirectory.mkdirs();
		AnalysisEngineDescription aed = createDescriptor();
		aed.toXML(new FileOutputStream(new File(outputDirectory, "RoomNumberAnnotator.xml")));
	}

}