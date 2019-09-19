/*
 * Copyright 2016 ForgeRock AS.
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

package org.forgerock.cuppa.model;

import static java.util.Collections.emptySet;

import java.util.Set;

/**
 * Encapsulates the tags to be used to filter which tests to be run as a part of the test run.
 */
public class Tags {

    /**
     * No tags specified, meaning no tests should be filtered from the test run.
     */
    public static final Tags EMPTY_TAGS = new Tags(emptySet(), emptySet(), "");

    /**
     * The set of tags which tests must be tagged with to be included in the test run.
     */
    public final Set<String> tags;

    /**
     * The set of excluded tags which tests must not be tagged with to be included in the test run.
     */
    public final Set<String> excludedTags;

    /**
     * An expression of tags using condition to create complex tag filtering.
     */
    public final String expressionTags;

    /**
     * Constructs a {@code Tags} instance with the specified tags and anti-tags (excluded tags).
     *
     * @param tags The set of tags which tests must be tagged with to be included in the test run.
     * @param excludedTags The set of excluded tags which tests must not be tagged with to be included
     *     in the test run.
     * @param expressionTags An expression using condition to create complex tag filtering
     * {@link org.forgerock.cuppa.expression.Condition}
     */
    public Tags(Set<String> tags, Set<String> excludedTags, String expressionTags) {
        this.tags = tags;
        this.expressionTags = expressionTags;
        this.excludedTags = excludedTags;
    }

    /**
     * Constructs a {@code Tags} instance with the specified tags.
     *
     * @param tags The set of tags which tests must be tagged with to be included in the test run.
     * @return The {@code Tags} instance.
     */
    public static Tags tags(Set<String> tags) {
        return new Tags(tags, emptySet(), "");
    }

    /**
     * Constructs a {@code Tags} instance with the specified anti-tags (excluded tags).
     *
     * @param excludedTags The set of excluded tags which tests must not be tagged with to be
     *     included in the test run.
     * @return The {@code Tags} instance.
     */
    public static Tags excludedTags(Set<String> excludedTags) {
        return new Tags(emptySet(), excludedTags, "");
    }

    /**
     * Constructs a {@code Tags} instance with the specified expression tag.
     *
     * @param expressionTag The expression tag
     * @return The {@code Tags} instance.
     */
    public static Tags expressionTags(String expressionTag) {
        return new Tags(emptySet(), emptySet(), expressionTag);
    }
}
