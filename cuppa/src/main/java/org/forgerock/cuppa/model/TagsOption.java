/*
 * Copyright 2015-2016 ForgeRock AS.
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * An option for tagging tests and test blocks. Tags can be used to group tests together to be included or excluded from
 * a test run.
 */
public final class TagsOption extends Option<Set<String>> {
    /**
     * Create a new tags option.
     *
     * @param value A set of tags.
     */
    public TagsOption(Set<String> value) {
        super(Collections.unmodifiableSet(new HashSet<>(value)));
    }
}
