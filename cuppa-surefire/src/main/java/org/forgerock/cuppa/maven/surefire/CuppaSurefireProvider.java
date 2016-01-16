package org.forgerock.cuppa.maven.surefire;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.maven.surefire.providerapi.AbstractProvider;
import org.apache.maven.surefire.providerapi.ProviderParameters;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.suite.RunResult;
import org.apache.maven.surefire.util.TestsToRun;
import org.forgerock.cuppa.CuppaTestProvider;
import org.forgerock.cuppa.Test;

/**
 * Maven Surefire and Failsafe provider for locating and running Cuppa tests.
 */
public final class CuppaSurefireProvider extends AbstractProvider {

    private final ProviderParameters providerParameters;

    /**
     * Constructs a new Cuppa Surefire Provider.
     *
     * @param parameters The Surefire provider parameters.
     */
    public CuppaSurefireProvider(ProviderParameters parameters) {
        this.providerParameters = parameters;
    }

    @Override
    public RunResult invoke(Object forkTestSet) {
        ReporterFactory reporterFactory = providerParameters.getReporterFactory();
        RunListener listener = reporterFactory.createReporter();
        instantiateTestClasses(scanClasspathForTests());
        CuppaTestProvider.runTests(new CuppaSurefireReporter(listener));
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
