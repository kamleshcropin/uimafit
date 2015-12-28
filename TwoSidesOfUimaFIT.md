# Introduction #

While uimaFIT provides many features for a UIMA developer, there are two overarching themes that most features fall under.  These two sides of uimaFIT are, while complementary, largely independent of each other.  One of the beauties of uimaFIT is that a developer that uses one side of uimaFIT extensively is not required to use the other side at all.

# Simplify Component Implementation #

The first broad theme of uimaFIT provides features that **simplify component implementation**.  Our favorite example of this is the @ConfigurationParameter annotation which allows you to annotate a member variable as a configuration parameter.  This annotation in combination with the method ConfigurationParameterInitializer.initialize completely automates the process of initializing member variables with values from the UimaContext passed into your analysis engine's initialize method.  Similarly, the annotation @ExternalResource annotation in combination with the method ExternalResourceInitializer.initialize completely automates the binding of an external resource as defined in the UimaContext to a member variable.  Dispensing with manually writing the code that performs these two tasks reduces effort, eliminates verbose and potentially buggy boiler-plate code, and makes implementing a UIMA component more enjoyable.  Consider, for example, a member variable that is of type 'Locale'.  With uimaFIT you can simply annotate the member variable with '@ConfigurationParameter' and have your initialize method automatically initialize the variable correctly with a string value in the UimaContext such as "en\_US".

# Simplify Component Instantiation #

The second broad theme of uimaFIT provides features that **simplify component instantiation**.  Working with UIMA, have you ever said to yourself "but I just want to tag some text!?"  What does it take to "just tag some text?"  Here's a list of things you must do with the traditional approach:

  * wrap your tagger as a UIMA analysis engine
  * write a descriptor file for your analysis engine
  * write a CAS consumer that produces the desired output
  * write another descriptor file for the CAS consumer
  * write a descriptor file for a collection reader
  * write a descriptor file that describes a pipeline
  * invoke the Collection Processing Manager with your pipeline descriptor file

Each of these steps has its own pitfalls and can be rather time consuming.  This is a rather unsatisfying answer to our simple desire to just tag some text.  With uimaFIT you can literally eliminate all of these steps.  Here's a simple snippet of Java code that demonstrates "tagging some text" with uimaFIT:

```

TypeSystemDescription typeSystemDescription = createTypeSystemDescription();

JCas jCas = createJCas(typeSystemDescription);

jCas.setDocumentText("some text");

AnalysisEngine tokenizer = createPrimitive(MyTokenizer.class, typeSystemDescription);

AnalysisEngine tagger = createPrimitive(MyTagger.class, typeSystemDescription);

runPipeline(jCas, tokenizer, tagger);

for(Token token : iterate(jCas, Token.class)){
    System.out.println(token.getTag());
}

```

This code assumes several static method imports (e.g. createTypeSystemDescription) provided by uimaFIT for brevity.  And while the terseness of this code won't make a python programmer blush - it is certainly much easier than the seven steps outlined above!

uimaFIT provides mechanisms to instantiate and run UIMA components programmatically with or without descriptor files.  For example, if you have a descriptor file for your analysis engine defined by MyTagger.java (as shown above), then you can instead instantiate the analysis engine with:

```
AnalysisEngine tagger = createAnalysisEngine("mypackage.MyTagger");
```

This will find the descriptor file 'mypackage/MyTagger.xml' by name.  Similarly, you can find a descriptor file by location with createAnalysisEngineFromPath().  However, if you want to dispense with XML descriptor files altogether (and you probably do), you can use the method createPrimitive() as shown above.  One of the driving motivations for creating the second side of uimaFIT is our frustration with descriptor files and our desire to eliminate them.  Descriptor files are difficult to maintain because they are generally tightly coupled with java code, they decay without warning, they are wearisome to test, and they proliferate, among other reasons.

# Is this cheating? #

One question that is often raised by new uimaFIT users is whether or not it breaks the "UIMA way".  That is, does adopting uimaFIT lead me down a path of creating UIMA components and systems that are incompatible with the traditional UIMA approach?  The answer to this question is no.  For starters, uimaFIT does not skirt the UIMA mechanism of describing components - it only skips the XML part of it.  For example, when the method createPrimitive is called (as shown above) an AnalysisEngineDescription is created for the AnalysisEngine.  This is the same object type that is instantiated when a descriptor file is used.  So, instead of parsing XML to instantiate an AnalysisEngineDescription, uimaFIT uses a factory method to instantiate it from method parameters.  One of the happy benefits of this approach is that for a given AnalysisEnginedDescription (which can be obtained directly with createPrimitiveDescription) you can generate an XML descriptor file using AnalysisEngineDescription.toXML().  So, uimaFIT actually provides a very simple and direct path for **generating** XML descriptor files rather than manually creating and maintaining them!

It is also useful to clarify that if you only want to use one side or the other of uimaFIT, then you are free to do so.  This is possible precisely because uimaFIT does not workaround UIMA's mechanisms for describing components but rather uses them directly.  For example, if the only thing you want to use in uimaFIT is the '@ConfigurationParameter', then you can do so without worrying about what effect this will have on your descriptor files.  This is because your analysis engine will be initialized with exactly the same UimaContext regardless of whether you instantiate your analysis engine in the "UIMA way" or use one of uimaFIT's factory methods.  Similarly, a UIMA component does not need to be annotated with @ConfiguratioParameter for you to make use of the createPrimitive method.  This is because when you pass configuration parameter values in to the createPrimitive method, they are added to an AnalysisEngineDescription which is used by UIMA to populate a UimaContext - just as it would if you used a descriptor file.

# Conclusion #
Because uimaFIT can be used to simplify component implementation and instantiation it is easy to assume that you can't do one without the other.  This page has demonstrated that while these two sides of uimaFIT complement each other, they are not coupled together and each can be effectively used without the other.  Similarly, by understanding how uimaFIT uses the UIMA component description mechanisms directly, one can be assured that uimaFIT enables UIMA development that is compatible and consistent with the UIMA standard and APIs.