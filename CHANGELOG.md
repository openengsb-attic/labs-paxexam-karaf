# PAXEXAM Apache Karaf extension Changelog

## v1.0.0

The 1.0.0 release does not differ in many ways from the previous 0.5.0 release except for some minor improvements and new features. But since there had not been many new features in the mean time we've decided to signal the stability of the extension by pushing the version number to 1.0.0.

### Features

* [#10] Add new option to set start lvl of bundles deployed in setup features.xml

With the deafault start lvls inthe initial features.xml it could happen from time to time that the exam bundle is not started correctly. In case those start lvls are set lower (or make completely configurable) it was possible to work around this problem.

* [#6] Add new option to change used invoker

In pax-exam the invoker is chosen by the pax.exam.invoker system property. By now the PaxExam Karaf extension uses only junit by default. With this extension you can now add "KarafDistributionOption.useOwnKarafExamSystemConfiguration("myCustomInvokerConfig")" to your options, overwriting the default junit configuration.

### Improvements

* [#8] Add changelog.md file to source tree

With the switch to the Github Issue tracker we needed a new way to add a changelog. This had been changed by adding this file.

### Bugs

* [#17] Correct creation of examfeatures.xml file

There's the option to use the features.xml instead of the deploy folder. Nevetheless, the features.xml generated for this purpose was simply wrong. The problem had been fixed and an integration test had been added to validate the behavior.

* [#15] Fixed problem that scanFeatures and extending etc/org.apache.karaf.features.xml are not compabible

The problem was that we initially wanted to avoid multible editions of the same file. Nevertheless, there are files where such modifications could come from various sources, such as etc/org.apache.karaf.features.xml. A method had been added to fix those problems in a quite general way.

* [#18] useDeployFolder(false) together with scan features play nicley together now

The problem was that the scanFeatures produces an url we simply don't want to have in our features.xml.

