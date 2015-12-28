<h1>Type Descriptor Detection</h1>



UIMA requires that types that are used in the CAS are defined in XML files - so-called type system descriptions (TSD). Whenever a UIMA component is created, it must be associated with such a type system. While it is possible to manually load the type system descriptors and pass them to each
UIMA component and to each created CAS, it is quite inconvenient to do so. For this reason, uimaFIT supports the automatic detection of such files in the classpath. Thus is becomes possible for a UIMA component provider to have component's type automatically detected and thus the components becomes immediately usable by adding it to the classpath.

# Making types auto-detectable #

The provider of a type system should create a file `META-INF/org.uimafit/types.txt` in the classpath. This file should define the locations of the type system descriptions. Assume that a type `org.uimafit.type.Token` is specified  in the TSD `org/uimafit/type/Token.xml`, then the file should have the following contents:

```
classpath*:org/uimafit/type/Token.xml
```

To specify multiple TSDs, add additonal lines to the file. If you have a large number of TSDs, you may prefer to add a pattern. Assume that we have a large number of TSDs under `org/uimafit/type`, we can use the following pattern which recursively scans the package `org.uimafit.type` and all sub-packages for XML files and tries to load them as TSDs.

```
classpath*:org/uimafit/type/**/*.xml
```

Try to design your packages structure in a way that TSDs and JCas wrapper classes generated from them are separate from the rest of your code.

If it is not possible or inconvenient to add the `types.txt` file, patterns can also be specified using a system property. Multiple patterns may be specified separated by semicolon:

```
org.uimafit.type.import_pattern=classpath*:org/uimafit/type/**/*.xml
```

# Using type auto-detection #

The auto-detected type system can be obtained from the `TypeSystemDescriptionFactory`:

```
TypeSystemDescription tsd = TypeSystemDescriptionFactory.createTypeSystemDescription()
```

Popular factory methods also support auto-detection:

```
AnalysisEngine ae = createPrimitive(MyEngine.class);
```

# Multiple `META-INF/org.uimafit/types.txt` files #

uimaFIT supports multiple `types.txt` files in the classpath (e.g. in differnt JARs).

The `types.txt` files are located via Spring using the classpath search pattern:

```
TYPE_MANIFEST_PATTERN = "classpath*:META-INF/org.uimafit/types.txt" 
```

This resolves to a list URLs pointing to ALL `types.txt`files. The resolved URLs are unique and will point either to a specific point in the file system or into a specific JAR. These URLs can be handled by the standard Java URL loading mechanism.

Example:

```
jar:/path/to/syntax-types.jar!/META-INF/org.uimafit/types.txt 
jar:/path/to/token-types.jar!/META-INF/org.uimafit/types.txt
```

uimaFIT then reads all patters from all of these URLs and uses these to search the classpath again. The patterns now resolve to a list of URLs pointing to the individual type system XML descriptors. All of these URLs are collected in a set to avoid duplicate loading (for performance optimization - not strictly necessary because the UIMA type system merger can handle compatible duplicates). Then the descriptors are loaded into memory and merged using the standard UIMA type system merger (`CasCreationUtils.mergeTypeSystems()`).

Example:

```
jar:/path/to/syntax-types.jar!/desc/types/Syntax.xml 
jar:/path/to/token-types.jar!/org/foobar/typesystems/Tokens.xml 
```
Voilá, the result is a type system covering all types could be found in the classpath.

I recommend
  1. to put type system descriptors into packages resembling a namespace you "own" and to use a package-scoped wildcard search
```
classpath*:org/uimafit/type/**/*.xml`
```
  1. or when putting descriptors into a "well-known" package like `desc.type`, that `types.txt` file  should explicitly list all type system descriptors instead of using a wildcard search or
```
classpath*:desc/type/Token.xml 
classpath*:desc/type/Syntax.xml 
```

Method 1 should be preferred. Both methods can be mixed.

# Performance note and caching #

Currently uimaFIT evaluates the patterns for TSDs once and caches the locations, but not the actual merged type system description. A rescan can be forced using `TypeSystemDescriptionFactory.forceTypeDescriptorsScan()`. This may change in future.

# Potential problems #

The mechanism works fine. However, there are specific issues with Java in general that one should be aware of.

## m2eclipse fails to copy descriptors to target/classes ##

There seems to be a bug in m2eclipse that causes resources not always to be copied to `target/classes`. If UIMA complains about type definitions missing at runtime, try to clean/rebuild your project and carefully check the m2eclipse console in the console view for error messages that might cause m2eclipse to abort.

## Class version conflicts ##

A problem can occur if you end up having multiple incompatible versions of the same type system in the classpath. This is a general problem and not related to the auto-detection feature. It is the same as when you have incompatible version of a particular class (e.g. JCas wrapper or some third-party-library) in the classpath. The behavior of the Java Classloader is undefined in that case. The detection will do its best to try and load everything it can find, but the UIMA type system merger may barf or you may end up with undefined behavior at runtime because one of the class versions is used at random.

## Classes and resources in the default package ##

It is bad practice to place classes into the default (unnamed) package. In fact it is not possible to import classes from the default package in another class. Similarly it is a bad idea to put resources at the root of the classpath. The Spring documentation on resources [explains this in detail](http://static.springsource.org/spring/docs/3.0.x/reference/resources.html#resources-app-ctx-wildcards-in-resource-paths).

For this reason the `types.txt` resides in `/META-INF/org.uimafit` and I suggest that type system descriptors reside either in a proper package like `/org/foobar/typesystems/XXX.xml` or in `/desc/types/XXX.xml`.