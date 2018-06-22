package com.agileengine.sergiy.dyrda;

import org.jsoup.nodes.Element;

/**
 * Created by Sergiy Dyrda on 22.06.2018
 */
class ElementWithPath {
    private Element element;
    private String absolutePath;

    ElementWithPath(Element element, String absolutePath) {
        this.element = element;
        this.absolutePath = absolutePath;
    }

    public Element getElement() {
        return element;
    }

    public String getAbsolutePath() {
        return absolutePath;
    }
}
