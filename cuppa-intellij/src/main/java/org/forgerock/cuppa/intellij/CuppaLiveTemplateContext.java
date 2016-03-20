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

package org.forgerock.cuppa.intellij;

import com.intellij.codeInsight.template.JavaCodeContextType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Extension to provide a context for Cuppa live templates so that they are only
 * available in statements in Cuppa test classes.
 */
public final class CuppaLiveTemplateContext extends JavaCodeContextType {
    private final Statement statement = new Statement();

    /**
     * Constructs a new context.
     */
    public CuppaLiveTemplateContext() {
        super("CUPPA_TEST_STATEMENT", "Cuppa test class", Statement.class);
    }

    @Override
    public boolean isInContext(@NotNull PsiFile file, int offset) {
        return statement.isInContext(file, offset) && super.isInContext(file, offset);
    }

    @Override
    protected boolean isInContext(@NotNull PsiElement element) {
        return CuppaUtils.isCuppaClass(element);
    }
}
