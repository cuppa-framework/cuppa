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

import com.intellij.codeInspection.reference.EntryPoint;
import com.intellij.codeInspection.reference.RefElement;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.util.xmlb.XmlSerializer;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

/**
 * Extension to prevent the "Unused declaration" inspection from marking
 * a Cuppa test class as unused.
 *
 * <p>This class is also used by the "Unused declaration" inspection's
 * settings dialog, where the user can select whether or not this entry
 * point should be used.</p>
 */
public final class CuppaEntryPoint extends EntryPoint {
    private boolean isSelected = true;

    @NotNull
    @Override
    public String getDisplayName() {
        return "Cuppa test classes";
    }

    @Override
    public boolean isEntryPoint(@NotNull RefElement refElement, @NotNull PsiElement psiElement) {
        return isEntryPoint(psiElement);
    }

    @Override
    public boolean isEntryPoint(@NotNull PsiElement psiElement) {
        return isSelected && psiElement instanceof PsiClass && CuppaUtils.isCuppaClass(psiElement);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        XmlSerializer.serializeInto(this, element);
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        if (!isSelected) {
            XmlSerializer.deserializeInto(this, element);
        }
    }
}
