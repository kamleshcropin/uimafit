uimaFIT 1.3.0 is a minor feature release. It should be binary backwards compatible with 1.2.0 and provides new features some of which we introduce next. For a complete list of changes see the [CHANGES](https://uimafit.googlecode.com/svn/tags/uimafit-parent-1.3.0/uimaFIT/CHANGES) file

## External resources ##

External resources can now be bound to a component just like you would set a configuration parameter if the component uses the uimaFIT `@ExternalResource` annotation to declare a resource dependency. So if the component declares an external resource like

```
final static String RES_MODEL = "Model";
@ExternalResource(key = RES_MODEL)
private Model model;
```

you can set the resource like

```
AnalysisEngineDescription desc = createPrimitiveDescription(Annotator.class,
    Annotator.PARAM_STRICT, true,
    Annotator.RES_MODEL, createExternalResourceDescription(
        Model.class, "path/to/my/model"));
```

## Logging ##

uimaFIT now provides a more convenient logging API similar to Apache Commons Logging and Log4J when using the uimaFIT component base classes:

```
getLogger().error("Unable to read file", e);
```

## selectBetween ##

This new method allows to select all annotations of a specific type located between two boundary annotations:

```
Collection<Token> tokens = selectBetween(Token.class, 
    leftBoundaryAnnotation, rightBoundaryAnnotation);
```

A typical use case would be for relation extraction where you want to extract, say, all tokens between two named entity mentions.