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

import com.intellij.codeInsight.AnnotationUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassInitializer;
import com.intellij.psi.PsiElement;

/**
 * Extension to prevent the "Class initializer may be 'static'" inspection from
 * marking the non-static class initializer block in Cuppa test classes.
 */
public final class CuppaCantBeStaticExtension implements Condition<PsiElement> {
    @Override
    public boolean value(PsiElement psiElement) {
        if (psiElement instanceof PsiClassInitializer) {
            PsiClass containingClass = ((PsiClassInitializer) psiElement).getContainingClass();
            if (containingClass == null) {
                return false;
            }
            if (AnnotationUtil.isAnnotated(containingClass, "org.forgerock.cuppa.Test", true)) {
                return true;
            }
        }
        return false;
    }
}
