package com.agileengine.sergiy.dyrda;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class JsoupFindSimilarElement {

    private static Logger LOGGER = LoggerFactory.getLogger(JsoupFindSimilarElement.class);

    private static String CHARSET_NAME = "utf8";

    private static String originalFilePath;
    private static String[] otherFilesPaths;

    public static void main(String[] args) {
        if (args != null && args.length >= 2) {
            originalFilePath = args[0];
            otherFilesPaths = new String[args.length - 1];
            System.arraycopy(args, 1, otherFilesPaths, 0, args.length - 1);
        } else {
            System.out.println("Usage: java -jar simple-crauler-0.0.1.jar <input_origin_file_path> <input_other_sample_file_path>");
            System.exit(-1);
        }

        Optional<Element> buttonOpt = findElementById(new File(originalFilePath), "make-everything-ok-button");
        buttonOpt.ifPresent(element -> {
            SimilarityPoint point = getSimilarityPoint(element);
            System.out.println("\nInitial file: " + originalFilePath);
            System.out.println("Button: " + element.text());
            System.out.println("Path: " + getAbsolutePathToElement(point.tagName, point.attributes, point.parentsWithAttributes));
            System.out.println("/-------------------------/");
            System.out.println("/-------------------------/\n\n");

        });
        buttonOpt.map(JsoupFindSimilarElement::getSimilarityPoint).ifPresent(similarityPoint -> Stream.of(otherFilesPaths).forEach(sample -> {
            Optional<ElementWithPath> similarElement = getSimilarElement(new File(sample), similarityPoint);
            System.out.println("/-------------------------/");
            System.out.println("File: " + sample);
            if (similarElement.isPresent()) {
                ElementWithPath elementWithPath = similarElement.get();
                System.out.println("Button: " + elementWithPath.element.text());
                System.out.println("Path: " + elementWithPath.absolutePath);
            } else {
                System.out.println("Unfortunately there is no similar element");
            }
            System.out.println("/-------------------------/\n\n");

        }));
    }

    private static Optional<Element> findElementById(File htmlFile, String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            return Optional.of(doc.getElementById(targetElementId));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }


    private static Optional<ElementWithPath> getSimilarElement(File htmlFile, SimilarityPoint similarityPoint) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            Deque<ParentNode> parents = new LinkedList<>(similarityPoint.parentsWithAttributes);

            if (!parents.isEmpty() && parents.size() > 3) {
                parents.pollFirst(); // remove head
                parents.pollFirst(); // remove body
                parents.pollLast(); // remove last tag to expand query range
            }

            Attributes attributes = similarityPoint.attributes.clone();
            attributes.remove("id");

            String query = getAbsolutePathToElement(similarityPoint.tagName, attributes, parents);

            Element element = doc.selectFirst(query);
            if (element != null)  {
                return Optional.of(new ElementWithPath(element, getAbsolutePathToElement(similarityPoint.tagName, similarityPoint.attributes, similarityPoint.parentsWithAttributes)));
            }
            else return Optional.empty();

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static String getAbsolutePathToElement(String tagName, Attributes attributes, Deque<ParentNode> parents) {
        String query = parents.stream().map(pn -> pn.toStringDelimited(".", "#"))
                .collect(Collectors.joining(" "));

        StringBuilder builder = new StringBuilder(query).append(" ").append(tagName);

        attributes.forEach(attr -> builder.append("[").append(attr.getKey()).append("]"));
        query = builder.toString();
        return query;
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


    private static class SimilarityPoint {
        String tagName;
        Attributes attributes;
        Deque<ParentNode> parentsWithAttributes;

        SimilarityPoint(String tagName, Attributes attributes, Deque<ParentNode> parentsWithAttributes) {
            this.tagName = tagName;
            this.attributes = attributes;
            this.parentsWithAttributes = parentsWithAttributes;
        }
    }

    private static class ParentNode {
        String tag, clazz, id;

        ParentNode(String tag, String clazz, String id) {
            this.tag = tag;
            this.clazz = clazz;
            this.id = id;
        }

        String toStringDelimited(String clazzPointer, String idPointer) {
            StringBuilder builder = new StringBuilder(tag);
            if (!clazz.isEmpty()) {
                String[] classes = clazz.split(" ");
                for (String cc : classes) {
                    builder.append(String.format("%s%s", clazzPointer, cc));
                }
            }
            if (!id.isEmpty()) builder.append(String.format("%s%s", idPointer, id));
            return builder.toString();
        }

    }

    private static class ElementWithPath {
        Element element;
        String absolutePath;

        ElementWithPath(Element element, String absolutePath) {
            this.element = element;
            this.absolutePath = absolutePath;
        }
    }
}