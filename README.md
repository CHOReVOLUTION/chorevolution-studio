# CHOReVOLUTION Studio


# Table Of Contents
* [Developing](#developing)
   * [Requirements](#requirements)
   * [Eclipse IDE](#eclipseIDE)
   * [Building](#building)

## Requirements

* [Apache Maven 3.3.3+](https://maven.apache.org/install.html)
* [Java 8+](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)

##eclipseIDE
Setting the Environment

1) Download Eclipse Modeling Tools (Oxygen version) https://eclipse.org/downloads/

2) Install New Software in Eclipse
	- Eclipse Java Web Developer Tools > http://download.eclipse.org/releases/oxygen (contained in the category: "Web, XML, Java EE and OSGi Enterprise Development")
	- Sirius > http://download.eclipse.org/sirius/updates/releases/5.1.1/oxygen/
	- M2Eclipse > http://download.eclipse.org/technology/m2e/releases/
	- M2Eclipse connector Tycho > http://repo1.maven.org/maven2/.m2e/connectors/m2eclipse-tycho/0.9.0/N/LATEST/
	- BPMN2 modeler > http://download.eclipse.org/bpmn2-modeler/updates/oxygen/1.4.2
	- CHOReVOLUTION Modeling Notations > http://nexus.disim.univaq.it/content/sites/chorevolution-modeling-notations/2.2.0

3) Run mvn -U clean on the chorevolution-studio\extra\eu.chorevolution.studio.eclipse.core.configurator\

4) Run mvn clean verify from the parent pom

5) Open the eclipse and import the maven projects for the chorevolution-studio

6) Create a Run Configuration in order to tests your modification without generate the CHOReVOLUTION Studio bundle:
	1) select Eclipse Application and press new
	2) name: chorevolution-studio-runtime
	3) workspace location: ${workspace_loc}/../chorevolution-studio-runtime
	4) in the Arguments tab add -Djavax.ws.rs.ext.RuntimeDelegate=org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl in the "VM arguments" text area


## Building
In order to generate the CHOReVOLUTION Studio bundle, you can 

1) Run the script "build.bat -f -b" or the script "build.sh -f -b" depending on your operating system.

OR

1) Run mvn clean on the chorevolution-studio\extra\eu.chorevolution.studio.eclipse.core.configurator

2) Run mvn -Pbuild-ide from the parent pom 

At the end the bundle are located in the chorevolution-studio\releng\eu.chorevolution.studio.eclipse.product\target\products folder.
