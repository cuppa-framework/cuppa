package org.forgerock.cuppa.maven.surefire;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.surefire.providerapi.AbstractProvider;
import org.apache.maven.surefire.providerapi.ProviderParameters;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.suite.RunResult;
import org.apache.maven.surefire.util.TestsToRun;
import org.forgerock.cuppa.CuppaTestProvider;
import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.model.Tags;

/**
 * Maven Surefire and Failsafe provider for locating and running Cuppa tests.
 */
public final class CuppaSurefireProvider extends AbstractProvider {

    private final ProviderParameters providerParameters;
    private final Tags tags;

    /**
     * Constructs a new Cuppa Surefire Provider.
     *
     * @param parameters The Surefire provider parameters.
     */
    public CuppaSurefireProvider(ProviderParameters parameters) {
        this.providerParameters = parameters;
        Properties properties = parameters.getProviderProperties();
        tags = new Tags(getTags(properties), getExcludedTags(properties));
    }

    private Set<String> getTags(Properties properties) {
        String groups = properties.getProperty("groups");
        String tags = properties.getProperty("tags");
        String overrideTags = System.getProperty("tags");
        return getTags(groups, overrideTags == null ? tags : overrideTags);
    }

    private Set<String> getTags(String groups, String tags) {
        if (groups != null && tags != null) {
            throw new RuntimeException("Use of 'groups/excludedGroups' and 'tags/excludedTags' "
                    + "are mutually exclusive.");
        } else if (groups != null) {
            return split(groups);
        } else if (tags != null) {
            return split(tags);
        } else {
            return Collections.emptySet();
        }
    }

    private Set<String> getExcludedTags(Properties properties) {
        String excludedGroups = properties.getProperty("excludedGroups");
        String excludedTags = properties.getProperty("excludedTags");
        String overrideExcludedTags = System.getProperty("excludedTags");
        return getTags(excludedGroups, overrideExcludedTags == null ? excludedTags : overrideExcludedTags);
    }

    private Set<String> split(String s) {
        return Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    @Override
    public RunResult invoke(Object forkTestSet) {
        ReporterFactory reporterFactory = providerParameters.getReporterFactory();
        RunListener listener = reporterFactory.createReporter();
        instantiateTestClasses(scanClasspathForTests());
        CuppaTestProvider.runTests(new CuppaSurefireReporter(listener), tags);
        return reporterFactory.close();
    }

    @Override
    public Iterator getSuites() {
        return scanClasspathForTests().iterator();
    }

    private TestsToRun scanClasspathForTests() {
        return providerParameters.getScanResult()
                .applyFilter(clazz -> Arrays.stream(clazz.getAnnotations())
                        .anyMatch(annotation -> Test.class.equals(annotation.annotationType())),
                        providerParameters.getTestClassLoader());
    }

    private void instantiateTestClasses(TestsToRun testClasses) {
        Arrays.stream(testClasses.getLocatedClasses()).forEach(testClass -> {
            try {
                CuppaTestProvider.setTestClass(testClass);
                testClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new IllegalStateException("Must be able to instantiate test classes", e);
            }
        });
    }
}
