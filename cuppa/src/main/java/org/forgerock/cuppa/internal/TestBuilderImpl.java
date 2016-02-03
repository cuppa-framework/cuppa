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

package org.forgerock.cuppa.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Test;
import org.forgerock.cuppa.model.TestBuilder;

final class TestBuilderImpl implements TestBuilder {

    private final Behaviour behaviour;
    private final String description;
    private final Class<?> testClass;
    private Optional<TestFunction> function = Optional.empty();
    private Set<String> tags = new HashSet<>();

    TestBuilderImpl(Behaviour behaviour, String description, Class<?> testClass) {
        this.behaviour = behaviour;
        this.description = description;
        this.testClass = testClass;
    }

    @Override
    public TestBuilder withTags(String tag, String... tags) {
        this.tags.addAll(Arrays.asList(tags));
        this.tags.add(tag);
        return this;
    }

    @Override
    public void asserts(TestFunction function) {
        this.function = Optional.ofNullable(function);
    }

    Test build() {
        return new Test(behaviour, testClass, description, function, tags);
    }
}
