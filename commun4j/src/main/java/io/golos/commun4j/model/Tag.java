package io.golos.commun4j.model;

import io.golos.commun4j.abi.writer.Abi;
import io.golos.commun4j.abi.writer.StringCompress;

@Abi
public class Tag {
    private String tag;

    public Tag(String tag) {
        this.tag = tag;
    }

    @StringCompress
    public String getTag() {
        return tag;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tag='" + tag + '\'' +
                '}';
    }
}
