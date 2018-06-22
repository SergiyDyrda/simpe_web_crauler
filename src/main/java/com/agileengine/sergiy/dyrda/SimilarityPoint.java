package com.agileengine.sergiy.dyrda;

import org.jsoup.nodes.Attributes;

import java.util.Deque;

/**
 * Created by Sergiy Dyrda on 22.06.2018
 */
class SimilarityPoint {
    private String tagName;
    private Attributes attributes;
    private Deque<ParentNode> parentsWithAttributes;

    SimilarityPoint(String tagName, Attributes attributes, Deque<ParentNode> parentsWithAttributes) {
        this.tagName = tagName;
        this.attributes = attributes;
        this.parentsWithAttributes = parentsWithAttributes;
    }

    public String getTagName() {
        return tagName;
    }

    public Attributes getAttributes() {
        return attributes;
    }

    public Deque<ParentNode> getParentsWithAttributes() {
        return parentsWithAttributes;
    }

}
