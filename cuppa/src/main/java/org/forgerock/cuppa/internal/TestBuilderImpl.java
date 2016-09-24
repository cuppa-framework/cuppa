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

import static org.forgerock.cuppa.model.TestBlockType.DESCRIBE;
import static org.forgerock.cuppa.model.TestBlockType.WHEN;

import java.util.Optional;

import org.forgerock.cuppa.TestBuilder;
import org.forgerock.cuppa.functions.TestBlockFunction;
import org.forgerock.cuppa.functions.TestFunction;
import org.forgerock.cuppa.model.Behaviour;
import org.forgerock.cuppa.model.Option;
import org.forgerock.cuppa.model.Options;

final class TestBuilderImpl implements TestBuilder {
    private Options options = Options.EMPTY;
    private Behaviour behaviour = Behaviour.NORMAL;

    @Override
    public TestBuilder with(Option<?>... options) {
        for (Option<?> o : options) {
            this.options = this.options.set(o);
        }
        return this;
    }

    @Override
    public TestBuilder skip() {
        behaviour = Behaviour.SKIP;
        return this;
    }

    @Override
    public TestBuilder only() {
        behaviour = Behaviour.ONLY;
        return this;
    }

    @Override
    public void describe(String description, TestBlockFunction function) {
        TestContainer.INSTANCE.testBlock(DESCRIBE, behaviour, description, function, options);
    }

    @Override
    public void when(String description, TestBlockFunction function) {
        TestContainer.INSTANCE.testBlock(WHEN, behaviour, description, function, options);
    }

    @Override
    public void it(String description, TestFunction function) {
        TestContainer.INSTANCE.it(behaviour, description, Optional.of(function), options);
    }

    @Override
    public void it(String description) {
        TestContainer.INSTANCE.it(behaviour, description, Optional.empty(), options);
    }
}
