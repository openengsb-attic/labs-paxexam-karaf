# Welcome to the PAXEXAM Apache Karaf extension

## Introduction

While [http://team.ops4j.org/wiki/display/paxexam/Pax+Exam](PAXEXAM) is an OSGi testing framework which is (since 
version 2.0) able to run plain osgi runtime natively or based on 
[http://team.ops4j.org/wiki/display/paxrunner/Pax+Runner](PAXRUNNER) [http://karaf.apache.org](Apache Karaf) is an 
OSGi Server based on freely selectable OSGi runtimes. In addition Apache Karaf adds tons of additional configurations 
and libraries making it far easier to work with OSGi. On the other hand though those extensions make it very hard to 
test Apache Karaf itself or on Apache Karaf based distributions using PAXEXAM.

This project creates a container for PAXEXAM starting any Apache Karaf based distribution directly and deploys your 
test probes on it. That way less configuration is needed and the risk for errors is drastically reduced since the
exact configurations of the distribution are used.

## Limitations

Currently this extension does only work with distributions based on Karaf >= 2.2.0 like the latest Karaf releases
themselves or the [http://openengsb.org](OpenEngSB). For this reason [http://servicemix.apache.org](Servicemix) in
a version smaller or equal 4.3.0 won't work with this framework.

## Usage

Basically there is nearly no difference to any other PAXEXAM test except that you use
org.fossbrain.extensions.paxexam.karaf/container as your container instead of native or runner in your pom.xml.

From that point on there are minor differences. The extension tries to support the configuration options available 
via PAXEXAM itself as good as possible. Still there are various additional configurations and some are handled 
a little bit differently than in a "regular" PAXEXAM environment. The following list describes the various options:

### CustomFrameworkOption

The framework itself is non of the typical runtimes you define normally in PAXEXAM. Instead you define a packed
distribution as zip or tar.gz. Those distributions have to follow the Karaf packaging style. Therefore instead
of Karaf you can also enter Servicemix or the OpenEngSB.

<pre>
new CustomFrameworkOption(
  "mvn:org.apache.karaf/apache-karaf/2.2.3/zip", // artifact to unpack and use
  "karaf", // name; display only
  "karaf-2.2.3") // full name; also only for displaying
</pre>

