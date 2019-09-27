/*
 * Copyright 2015-2017 ForgeRock AS.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.forgerock.cuppa.maven.surefire;

import static java.util.Collections.emptySet;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.maven.surefire.providerapi.AbstractProvider;
import org.apache.maven.surefire.providerapi.ProviderParameters;
import org.apache.maven.surefire.report.ReporterFactory;
import org.apache.maven.surefire.report.RunListener;
import org.apache.maven.surefire.suite.RunResult;
import org.forgerock.cuppa.Runner;
import org.forgerock.cuppa.Test;
import org.forgerock.cuppa.model.Options;
import org.forgerock.cuppa.model.Tags;
import org.forgerock.cuppa.model.TestBlock;
import org.forgerock.cuppa.reporters.CompositeReporter;
import org.forgerock.cuppa.reporters.DefaultReporter;
import org.forgerock.cuppa.reporters.Reporter;

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
        Map<String, String> properties = parameters.getProviderProperties();
        tags = new Tags(getIncludedTags(properties), getExcludedTags(properties),
                getExpressionTags(properties));

        if (!tags.expressionTags.isEmpty() && (!tags.tags.isEmpty() || !tags.excludedTags.isEmpty())) {
            throw new RuntimeException("Use of groupsExpression/tagsExpression cannot be used with "
                    + "excludedGroups/excludedTags or groups/tags");
        }
    }

    private String getExpressionTags(Map<String, String> properties) {
        return getTagsFromPropertiesOrSystem("groupsExpression", "tagsExpression", properties);
    }

    private Set<String> getIncludedTags(Map<String, String> properties) {
        return split(getTagsFromPropertiesOrSystem("groups", "tags", properties));
    }

    private Set<String> getExcludedTags(Map<String, String> properties) {
        return split(getTagsFromPropertiesOrSystem("excludedGroups", "excludedTags", properties));
    }

    private String getTagsFromPropertiesOrSystem(String groupName, String tagName,
            Map<String, String> properties) {
        String groups = properties.get(groupName);
        if (groups == null) {
            groups = System.getProperty(groupName);
        }
        String tags = properties.get(tagName);
        String overrideTags = System.getProperty(tagName);
        return getTags(groups, overrideTags == null ? tags : overrideTags);
    }

    private String getTags(String groups, String tags) {
        if (groups != null && tags != null) {
            throw new RuntimeException("Use of 'groups/excludedGroups/groupsExpression' and"
                    + " 'tags/excludedTags/tagsExpression are mutually exclusive.");
        } else if (groups != null) {
            return groups;
        } else if (tags != null) {
            return tags;
        } else {
            return "";
        }
    }

    private Set<String> split(String s) {
        if (s.isEmpty()) {
            return emptySet();
        }
        return Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toSet());
    }

    @Override
    public RunResult invoke(Object forkTestSet) {
        ReporterFactory reporterFactory = providerParameters.getReporterFactory();
        RunListener listener = reporterFactory.createReporter();
        Runner runner = new Runner(Options.EMPTY.set(new Runner.TagsRunOption(tags)));
        TestBlock rootBlock = runner.defineTests(getSuites());
        List<Reporter> reporters = Arrays.asList(new DefaultReporter(), new CuppaSurefireReporter(listener));
        runner.run(rootBlock, new CompositeReporter(reporters));
        return reporterFactory.close();
    }

    @Override
    public Iterable<Class<?>> getSuites() {
        return Arrays.asList(providerParameters.getScanResult()
                .applyFilter(clazz -> Arrays.stream(clazz.getAnnotations())
                                .anyMatch(annotation -> Test.class.equals(annotation.annotationType())),
                        providerParameters.getTestClassLoader()).getLocatedClasses());
    }
}
