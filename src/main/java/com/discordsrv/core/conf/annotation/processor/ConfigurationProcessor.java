/*
 * DiscordSRV2-Core: A library for generic Minecraft plugin development for all DiscordSRV2 projects
 * Copyright (C) 2018 DiscordSRV
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.discordsrv.core.conf.annotation.processor;

import com.discordsrv.core.conf.annotation.Val;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.SimpleElementVisitor8;
import javax.tools.Diagnostic;
import java.util.Set;

/**
 * Annotations processor for checking @Configured constructors.
 */
@SupportedAnnotationTypes("com.discordsrv.core.conf.annotation.Configured")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ConfigurationProcessor extends AbstractProcessor {

    private final ElementVisitor<Void, Element> visitor = new SimpleElementVisitor8<Void, Element>() {
        @Override
        public Void visitType(final TypeElement e, final Element element) {
            if (e.getEnclosingElement().getKind() != ElementKind.PACKAGE) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String
                    .format("%s: All classes with @Configured constructors must not be inner classes.",
                        element.toString()));
            } else if (!e.getModifiers().contains(Modifier.PUBLIC)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s: All classes with @Configured constructors must be public.", element.toString()));
            } else if (e.getModifiers().contains(Modifier.ABSTRACT)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String
                    .format("%s: All classes with @Configured constructors must not be abstract.", element.toString()));
            } else if (e.getKind() == ElementKind.ENUM) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String
                    .format("%s: All classes with @Configured constructors must not be enums.", element.toString()));
            }
            return super.visitType(e, element);
        }

        @Override
        public Void visitExecutable(final ExecutableElement e, final Element element) {
            e.getParameters().forEach(variable -> visitVariable(variable, element));
            if (!e.getModifiers().contains(Modifier.PUBLIC)) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR,
                    String.format("%s: All @Configured constructors must be public.", element.toString()));
            }
            visitType((TypeElement) e.getEnclosingElement(), e);
            return super.visitExecutable(e, element);
        }

        @Override
        public Void visitVariable(final VariableElement e, final Element element) {
            if (e.getAnnotation(Val.class) == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, String
                    .format("%s: All parameters of @Configured constructors must be annotated with @Val.",
                        element.toString()));
            }
            return super.visitVariable(e, element);
        }
    };

    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {
        annotations.forEach(
            type -> roundEnv.getElementsAnnotatedWith(type).forEach(element -> element.accept(visitor, element)));
        return true;
    }

}
