package org.openengsb.labs.paxexam.karaf.container.internal.examAdaptions;

import org.openengsb.labs.paxexam.karaf.container.internal.Constants;
import org.openengsb.labs.paxexam.karaf.container.internal.util.NormalisedMavenVersionAdapter;
import org.ops4j.pax.exam.Info;
import org.osgi.framework.Version;

/**
 * Factory to create a features.xml file meeting the currently used pax-exam version.
 */
public final class ExamFeaturesFileFactory {

    private ExamFeaturesFileFactory() {
        // Not required for a final class
    }

    public static ExamFeaturesFile createExamFeaturesFile() {
        return createExamFeaturesFile("", Constants.DEFAULT_START_LEVEL);
    }

    public static ExamFeaturesFile createExamFeaturesFile(String featuresXml) {
        return createExamFeaturesFile(featuresXml, Constants.DEFAULT_START_LEVEL);
    }

    public static ExamFeaturesFile createExamFeaturesFile(String extension, int startLevel) {
        Version examVersion = retrieveVersion();
        if (examVersion.getMajor() < 2) {
            throw new IllegalStateException("Exam versions < 2.0 are not supported");
        }
        if (examVersion.getMajor() == 2 && examVersion.getMinor() < 4) {
            return new ExamFeaturesFile23(extension, startLevel);
        }
        return new ExamFeaturesFile24(extension, startLevel);
    }

    private static Version retrieveVersion() {
        return new NormalisedMavenVersionAdapter(Info.getPaxExamVersion());
    }

}
