/**
 * Copyright 2013-present memtrip LTD.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.golos.commun4j.abi.writer.preprocessor;

import com.google.auto.service.AutoService;
import com.google.googlejavaformat.java.FormatterException;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import io.golos.commun4j.abi.writer.Abi;
import io.golos.commun4j.abi.writer.AbiWriter;
import io.golos.commun4j.abi.writer.preprocessor.gen.Generate;

@AutoService(Processor.class)
public final class Preprocessor extends AbstractProcessor {
    private Filer filer;
    private Elements elementUtils;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        filer = env.getFiler();
        elementUtils = env.getElementUtils();
        messager = env.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annoations, RoundEnvironment env) {

        Set<? extends Element> abiElements = env.getElementsAnnotatedWith(Abi.class);
        Set<? extends Element> abiWriterElements = env.getElementsAnnotatedWith(AbiWriter.class);

        if (!abiElements.isEmpty()) {
            if (abiWriterElements.isEmpty()) {
                messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "You must annotate an interface with @AbiWriter for us to generate the boilerplate for you.\n" +
                                "We will generate the boilerplate in the package of this interface and generate a class named \n" +
                                "AbiBinaryGen${your_interface_name} for you.");
                return false;
            } else {

                ParseAnnotations parseAnnotations = new ParseAnnotations(elementUtils);

                try {
                    new Generate(
                            parseAnnotations.abiWriter(abiWriterElements),
                            parseAnnotations.abi(abiElements),
                            new FreeMarker(),
                            new SourceFileGenerator(filer, messager)
                    ).generate();
                } catch (IOException | FormatterException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(Abi.class.getCanonicalName());
        set.add(AbiWriter.class.getCanonicalName());
        return set;
    }
}