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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;

import io.golos.commun4j.abi.writer.AccountNameCollectionCompress;
import io.golos.commun4j.abi.writer.AccountNameCompress;
import io.golos.commun4j.abi.writer.AssetCompress;
import io.golos.commun4j.abi.writer.BlockNumCompress;
import io.golos.commun4j.abi.writer.BlockPrefixCompress;
import io.golos.commun4j.abi.writer.BoolCompress;
import io.golos.commun4j.abi.writer.ByteCompress;
import io.golos.commun4j.abi.writer.BytesCompress;
import io.golos.commun4j.abi.writer.ChainIdCompress;
import io.golos.commun4j.abi.writer.CheckSumCompress;
import io.golos.commun4j.abi.writer.ChildCompress;
import io.golos.commun4j.abi.writer.CollectionCompress;
import io.golos.commun4j.abi.writer.CyberNameCollectionCompress;
import io.golos.commun4j.abi.writer.CyberNameCompress;
import io.golos.commun4j.abi.writer.DataCompress;
import io.golos.commun4j.abi.writer.FloatCompress;
import io.golos.commun4j.abi.writer.HexCollectionCompress;
import io.golos.commun4j.abi.writer.IntCollectionCompress;
import io.golos.commun4j.abi.writer.IntCompress;
import io.golos.commun4j.abi.writer.InterfaceCollectionCompress;
import io.golos.commun4j.abi.writer.LongCollectionCompress;
import io.golos.commun4j.abi.writer.LongCompress;
import io.golos.commun4j.abi.writer.NameCompress;
import io.golos.commun4j.abi.writer.NullableCompress;
import io.golos.commun4j.abi.writer.PublicKeyCompress;
import io.golos.commun4j.abi.writer.ShortCollectionCompress;
import io.golos.commun4j.abi.writer.ShortCompress;
import io.golos.commun4j.abi.writer.StringCollectionCompress;
import io.golos.commun4j.abi.writer.StringCompress;
import io.golos.commun4j.abi.writer.SymbolCodeCompress;
import io.golos.commun4j.abi.writer.SymbolCompress;
import io.golos.commun4j.abi.writer.TimestampCompress;
import io.golos.commun4j.abi.writer.VariableUIntCompress;
import io.golos.commun4j.abi.writer.preprocessor.model.AbiModel;
import io.golos.commun4j.abi.writer.preprocessor.model.AbiWriterModel;
import io.golos.commun4j.abi.writer.preprocessor.model.CompressType;
import io.golos.commun4j.abi.writer.preprocessor.model.FieldModel;

final class ParseAnnotations {

    private final Elements elementUtils;

    ParseAnnotations(Elements elementUtils) {
        this.elementUtils = elementUtils;
    }

    AbiWriterModel abiWriter(Set<? extends Element> elements) {
        Element element = elements.iterator().next();
        return new AbiWriterModel(
                element.getSimpleName().toString(),
                elementUtils.getPackageOf(element).getQualifiedName().toString()
        );
    }

    List<AbiModel> abi(Set<? extends Element> elements) {

        List<AbiModel> abiModels = new ArrayList<>();

        for (Element element : elements) {
            abiModels.add(new AbiModel(
                    element.getSimpleName().toString(),
                    elementUtils.getPackageOf(element).getQualifiedName().toString(),
                    fields(element)
            ));
        }

        return abiModels;
    }

    private List<FieldModel> fields(Element element) {

        List<FieldModel> fieldModels = new ArrayList<>();

        for (Element child : element.getEnclosedElements()) {
            if (elementHasFieldAnnotation(child)) {
                fieldModels.add(new FieldModel(
                        extractName(child.getSimpleName().toString()),
                        extractClassType(child),
                        extractAnnotationType(child),
                        hasAnnotation(child, NullableCompress.class)
                ));
            }
        }

        return fieldModels;
    }

    private String extractName(String name) {
        if (name.contains("$")) {
            return name.split("$")[0];
        } else {
            return name;
        }
    }

    private String extractClassType(Element element) {
        TypeMirror typeMirror = element.asType();
        return typeMirror.toString();
    }

    private CompressType extractAnnotationType(Element element) {
        if (hasAnnotation(element, NameCompress.class)) {
            return CompressType.NAME;
        } else if (hasAnnotation(element, AccountNameCompress.class)) {
            return CompressType.ACCOUNT_NAME;
        } else if (hasAnnotation(element, BlockNumCompress.class)) {
            return CompressType.BLOCK_NUM;
        } else if (hasAnnotation(element, BlockPrefixCompress.class)) {
            return CompressType.BLOCK_PREFIX;
        } else if (hasAnnotation(element, PublicKeyCompress.class)) {
            return CompressType.PUBLIC_KEY;
        } else if (hasAnnotation(element, AssetCompress.class)) {
            return CompressType.ASSET;
        } else if (hasAnnotation(element, ChainIdCompress.class)) {
            return CompressType.CHAIN_ID;
        } else if (hasAnnotation(element, HexCollectionCompress.class)) {
            return CompressType.HEX_COLLECTION;
        } else if (hasAnnotation(element, DataCompress.class)) {
            return CompressType.DATA;
        } else if (hasAnnotation(element, TimestampCompress.class)) {
            return CompressType.TIMESTAMP;
        } else if (hasAnnotation(element, ByteCompress.class)) {
            return CompressType.BYTE;
        } else if (hasAnnotation(element, BoolCompress.class)) {
            return CompressType.BOOL;
        } else if (hasAnnotation(element, ShortCompress.class)) {
            return CompressType.SHORT;
        } else if (hasAnnotation(element, IntCompress.class)) {
            return CompressType.INT;
        } else if (hasAnnotation(element, VariableUIntCompress.class)) {
            return CompressType.VARIABLE_UINT;
        } else if (hasAnnotation(element, LongCompress.class)) {
            return CompressType.LONG;
        } else if (hasAnnotation(element, FloatCompress.class)) {
            return CompressType.FLOAT;
        } else if (hasAnnotation(element, BytesCompress.class)) {
            return CompressType.BYTES;
        } else if (hasAnnotation(element, StringCompress.class)) {
            return CompressType.STRING;
        } else if (hasAnnotation(element, StringCollectionCompress.class)) {
            return CompressType.STRING_COLLECTION;
        } else if (hasAnnotation(element, CollectionCompress.class)) {
            return CompressType.COLLECTION;
        } else if (hasAnnotation(element, IntCollectionCompress.class)) {
            return CompressType.INT_COLLECTION;
        } else if (hasAnnotation(element, ShortCollectionCompress.class)) {
            return CompressType.SHORT_COLLECTION;
        } else if (hasAnnotation(element, AccountNameCollectionCompress.class)) {
            return CompressType.ACCOUNT_NAME_COLLECTION;
        } else if (hasAnnotation(element, ChildCompress.class)) {
            return CompressType.CHILD;
        } else if (hasAnnotation(element, LongCollectionCompress.class)) {
            return CompressType.LONG_COLLECTION;
        } else if (hasAnnotation(element, SymbolCodeCompress.class)) {
            return CompressType.SYMBOL_CODE;
        } else if (hasAnnotation(element, SymbolCompress.class)) {
            return CompressType.SYMBOL;
        } else if (hasAnnotation(element, CheckSumCompress.class)) {
            return CompressType.CHECK_SUM_256;
        } else if (hasAnnotation(element, CyberNameCollectionCompress.class)) {
            return CompressType.CYBER_NAME_COLLECTION;
        } else if (hasAnnotation(element, CyberNameCompress.class)) {
            return CompressType.NAME;
        } else if (hasAnnotation(element, InterfaceCollectionCompress.class)) {
            return CompressType.INTERFACE_COLLECTION;
        } else {
            throw new IllegalStateException("this method is not covering all the values " +
                    "allowed by elementHasFieldAnnotation. This method is broken!");
        }
    }

    private boolean elementHasFieldAnnotation(Element element) {
        return hasAnnotation(element, NameCompress.class)
                || hasAnnotation(element, AccountNameCompress.class)
                || hasAnnotation(element, BlockNumCompress.class)
                || hasAnnotation(element, BlockPrefixCompress.class)
                || hasAnnotation(element, PublicKeyCompress.class)
                || hasAnnotation(element, AssetCompress.class)
                || hasAnnotation(element, InterfaceCollectionCompress.class)
                || hasAnnotation(element, ChainIdCompress.class)
                || hasAnnotation(element, HexCollectionCompress.class)
                || hasAnnotation(element, DataCompress.class)
                || hasAnnotation(element, TimestampCompress.class)
                || hasAnnotation(element, CheckSumCompress.class)
                || hasAnnotation(element, ByteCompress.class)
                || hasAnnotation(element, ShortCompress.class)
                || hasAnnotation(element, IntCompress.class)
                || hasAnnotation(element, VariableUIntCompress.class)
                || hasAnnotation(element, LongCompress.class)
                || hasAnnotation(element, FloatCompress.class)
                || hasAnnotation(element, BytesCompress.class)
                || hasAnnotation(element, CyberNameCompress.class)
                || hasAnnotation(element, StringCompress.class)
                || hasAnnotation(element, StringCollectionCompress.class)
                || hasAnnotation(element, CollectionCompress.class)
                || hasAnnotation(element, AccountNameCollectionCompress.class)
                || hasAnnotation(element, ChildCompress.class)
                || hasAnnotation(element, BoolCompress.class)
                || hasAnnotation(element, CyberNameCollectionCompress.class)
                || hasAnnotation(element, SymbolCompress.class)
                || hasAnnotation(element, SymbolCodeCompress.class)
                || hasAnnotation(element, IntCollectionCompress.class)
                || hasAnnotation(element, ShortCollectionCompress.class)
                || hasAnnotation(element, LongCollectionCompress.class);
    }

    private boolean hasAnnotation(Element element, Class<? extends Annotation> clazz) {
        return element.getAnnotation(clazz) != null;
    }
}
