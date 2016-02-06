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

package org.forgerock.cuppa;

/**
 * Instantiates test classes.
 */
@FunctionalInterface
public interface TestInstantiator {
    /**
     * Instantiate a test class. Must throw an exception if it was not possible to instantiate the test class.
     *
     * @param testClass The class to instantiate.
     * @throws Exception Thrown if test class could not be instantiated.
     */
    void instantiate(Class<?> testClass) throws Exception;
}
