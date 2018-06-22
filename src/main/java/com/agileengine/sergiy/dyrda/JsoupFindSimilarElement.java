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
            System.out.println("\nInitial file: " + originalFilePath);
            System.out.println("Button: " + element.text());
            System.out.println("Path: " + SimilarElementSearcher.getAbsolutePathToElement(element));
            System.out.println("/-------------------------/");
            System.out.println("/-------------------------/\n\n");

        });
        buttonOpt.ifPresent(element -> Stream.of(otherFilesPaths).forEach(sample -> {
            Optional<ElementWithPath> similarElement = SimilarElementSearcher.getSimilarElement(new File(sample), element);
            System.out.println("/-------------------------/");
            System.out.println("File: " + sample);
            if (similarElement.isPresent()) {
                ElementWithPath elementWithPath = similarElement.get();
                System.out.println("Button: " + elementWithPath.getElement().text());
                System.out.println("Path: " + elementWithPath.getAbsolutePath());
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

}