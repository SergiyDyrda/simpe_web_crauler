# Find similar element in HTML


It is built on top of [Jsoup](https://jsoup.org/).

Main functionality was implemented on top of [org.jsoup.select.Selector](https://jsoup.org/apidocs/org/jsoup/select/Selector.html) - CSS-like element selector, that finds elements matching a query.

To use application make the following steps
in the root project folder:
1. `gradlew build` - if using UN*X</p>
   `gradlew.bat` - if using Windows

2. `java -jar build/libs/simple-crauler-0.0.1.jar` `<input_origin_file_path>` `<input_other_sample_file_path>`
