package org.uimafit.factory;

import java.io.IOException;

import org.apache.uima.ResourceSpecifierFactory;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMAFramework;
import org.apache.uima.analysis_component.CasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.analysis_engine.CasIterator;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReaderDescription;
import org.apache.uima.collection.CollectionReader_ImplBase;
import org.apache.uima.util.Progress;
import org.junit.Test;

public class AggregateWithReaderTest {
	
	/**
	 * Demo of running a collection reader as part of an aggregate engine. This allows to run
	 * a pipeline an access the output CASes directly - no need to write the data to files.
	 */
	@Test
	public void demoAggregateWithReader() throws UIMAException {
		ResourceSpecifierFactory factory = UIMAFramework.getResourceSpecifierFactory();

		CollectionReaderDescription reader = factory.createCollectionReaderDescription();
		reader.getMetaData().setName("reader");
		reader.setImplementationName(SimpleReader.class.getName());

		AnalysisEngineDescription analyzer = factory.createAnalysisEngineDescription();
		analyzer.getMetaData().setName("analyzer");
		analyzer.setPrimitive(true);
		analyzer.setImplementationName(SimpleAnalyzer.class.getName());

		FixedFlow flow = factory.createFixedFlow();
		flow.setFixedFlow(new String[] { "reader", "analyzer" });

		AnalysisEngineDescription aggregate = factory.createAnalysisEngineDescription();
		aggregate.getMetaData().setName("aggregate");
		aggregate.getAnalysisEngineMetaData().setFlowConstraints(flow);
		aggregate.getAnalysisEngineMetaData().getOperationalProperties().setOutputsNewCASes(true);
		aggregate.getAnalysisEngineMetaData().getOperationalProperties()
				.setMultipleDeploymentAllowed(false);
		aggregate.setPrimitive(false);
		aggregate.getDelegateAnalysisEngineSpecifiersWithImports().put("reader", reader);
		aggregate.getDelegateAnalysisEngineSpecifiersWithImports().put("analyzer", analyzer);

		AnalysisEngine pipeline = UIMAFramework.produceAnalysisEngine(aggregate);
		CasIterator iterator = pipeline.processAndOutputNewCASes(pipeline.newCAS());
		while (iterator.hasNext()) {
			CAS cas = iterator.next();
			System.out.printf("[%s] is [%s]%n", cas.getDocumentText(), cas.getDocumentLanguage());
		}
	}

	/**
	 * Demo of disguising a reader as a CAS multiplier. This works because internally, UIMA wraps
	 * the reader in a CollectionReaderAdapter. This nice thing about this is, that in principle
	 * it would be possible to define sofa mappings. However, UIMA-2419 prevents this.
	 */
	@Test
	public void demoAggregateWithDisguisedReader() throws UIMAException {
		ResourceSpecifierFactory factory = UIMAFramework.getResourceSpecifierFactory();
		
		AnalysisEngineDescription reader = factory.createAnalysisEngineDescription();
		reader.getMetaData().setName("reader");
		reader.setPrimitive(true);
		reader.setImplementationName(SimpleReader.class.getName());
		reader.getAnalysisEngineMetaData().getOperationalProperties().setOutputsNewCASes(true);

		AnalysisEngineDescription analyzer = factory.createAnalysisEngineDescription();
		analyzer.getMetaData().setName("analyzer");
		analyzer.setPrimitive(true);
		analyzer.setImplementationName(SimpleAnalyzer.class.getName());

		FixedFlow flow = factory.createFixedFlow();
		flow.setFixedFlow(new String[] { "reader", "analyzer" });

		AnalysisEngineDescription aggregate = factory.createAnalysisEngineDescription();
		aggregate.getMetaData().setName("aggregate");
		aggregate.setPrimitive(false);
		aggregate.getAnalysisEngineMetaData().setFlowConstraints(flow);
		aggregate.getAnalysisEngineMetaData().getOperationalProperties().setOutputsNewCASes(true);
		aggregate.getAnalysisEngineMetaData().getOperationalProperties()
				.setMultipleDeploymentAllowed(false);
		aggregate.getDelegateAnalysisEngineSpecifiersWithImports().put("reader", reader);
		aggregate.getDelegateAnalysisEngineSpecifiersWithImports().put("analyzer", analyzer);

		AnalysisEngine pipeline = UIMAFramework.produceAnalysisEngine(aggregate);
		CasIterator iterator = pipeline.processAndOutputNewCASes(pipeline.newCAS());
		while (iterator.hasNext()) {
			CAS cas = iterator.next();
			System.out.printf("[%s] is [%s]%n", cas.getDocumentText(), cas.getDocumentLanguage());
		}
	}

	public static class SimpleReader extends CollectionReader_ImplBase {
		private boolean done = false;

		public void getNext(CAS aCAS) throws IOException, CollectionException {
			aCAS.setDocumentText("Anyone up for a game of Foosball?");
			done = true;
		}

		public boolean hasNext() throws IOException, CollectionException {
			return !done;
		}

		public Progress[] getProgress() {
			return new Progress[0];
		}

		public void close() throws IOException {
			// Nothing to do
		}
	}

	public static class SimpleAnalyzer extends CasAnnotator_ImplBase {

		@Override
		public void process(CAS aCas) throws AnalysisEngineProcessException {
			aCas.setDocumentLanguage("en");
		}
	}
}
