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

package org.forgerock.cuppa;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.forgerock.cuppa.model.Option;
import org.forgerock.cuppa.model.Options;
import org.testng.annotations.Test;

public class OptionsTest {

    @Test
    public void addingNewOptionShouldReturnNewOptionCopy() {
        Options options = Options.EMPTY;
        Options optionsCopy = options.set(new TestOption());
        assertThat(options.get(TestOption.class)).isEqualTo(Optional.empty());
        assertThat(optionsCopy.get(TestOption.class)).isNotEqualTo(Optional.empty());
    }

    @Test
    public void removingOptionShouldReturnNewOptionCopy() {
        Options options = Options.EMPTY.set(new TestOption());
        Options optionsCopy = options.unset(TestOption.class);
        assertThat(options.get(TestOption.class)).isNotEqualTo(Optional.empty());
        assertThat(optionsCopy.get(TestOption.class)).isEqualTo(Optional.empty());
    }

    private static final class TestOption extends Option<String> {
        private TestOption() {
            super("a");
        }
    }
}
