package com.agileengine.sergiy.dyrda;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by Sergiy Dyrda on 22.06.2018
 */
public class SimilarElementSearcher {

    private static Logger LOGGER = LoggerFactory.getLogger(SimilarElementSearcher.class);

    private static String CHARSET_NAME = "utf8";

    public static Optional<ElementWithPath> getSimilarElement(File htmlFile, Element originalElement) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            SimilarityPoint similarityPoint = getSimilarityPoint(originalElement);

            Deque<ParentNode> parents = new LinkedList<>(similarityPoint.getParentsWithAttributes());

            if (!parents.isEmpty() && parents.size() > 2) { // at least must be head & body
                parents.pollLast(); // remove last tag to expand query range
            }

            Attributes attributes = similarityPoint.getAttributes().clone();
            attributes.remove("id");

            String query = getAbsolutePathToElement(similarityPoint.getTagName(), attributes, parents);

            Element element = doc.selectFirst(query);
            if (element != null)  {
                return Optional.of(new ElementWithPath(element,
                        getAbsolutePathToElement(similarityPoint.getTagName(), similarityPoint.getAttributes(), similarityPoint.getParentsWithAttributes())));
            }
            else return Optional.empty();

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    public static String getAbsolutePathToElement(Element element) {
        SimilarityPoint point = getSimilarityPoint(element);
        return getAbsolutePathToElement(point.getTagName(), point.getAttributes(), point.getParentsWithAttributes());
    }

    private static SimilarityPoint getSimilarityPoint(Element element) {
        Function<Element, ParentNode> mapper = (parent -> new ParentNode(parent.tagName(), parent.className(), parent.id()));
        LinkedList<ParentNode> parents = element.parents().stream().map(mapper)
                .collect(Collector.of(LinkedList::new, Deque::addFirst, (left, right) -> {
                    right.addAll(left);
                    return right;
                }));

        return new SimilarityPoint(element.tagName(), element.attributes(), parents);
    }


    private static String getAbsolutePathToElement(String tagName, Attributes attributes, Deque<ParentNode> parents) {
        String query = parents.stream().map(pn -> pn.toStringDelimited(".", "#"))
                .collect(Collectors.joining(" "));

        StringBuilder builder = new StringBuilder(query).append(" ").append(tagName);

        attributes.forEach(attr -> builder.append("[").append(attr.getKey()).append("]"));
        query = builder.toString();
        return query;
    }

}
