package com.github.lombrozo

import spock.lang.Specification

final class MarkdownSummaryTest extends Specification {

    def "should generate markdown for valid diff summary"() {
        setup:
        def change = new Change("Benchmark1", 100.0, 120.0, 20.0, 20.0, "ms", "avgt")
        def summary = new Summary(change)
        def markdownSummary = new MarkdownSummary(() -> summary)

        when:
        def result = markdownSummary.asMarkdown()

        then:
        result.contains("Benchmark1")
        result.contains("100.000")
        result.contains("120.000")
        result.contains("20.00")
        result.contains("20.00%")
        result.contains("ms")
        result.contains("Average Time")
    }

    def "should return empty markdown when NothingToCompare is thrown"() {
        setup:

        def markdownSummary = new MarkdownSummary(() -> { throw new NothingToCompare() })

        when:
        def result = markdownSummary.asMarkdown()

        then:
        normalize(result) == normalize(getClass().getClassLoader().getResourceAsStream("unavailable.md").text)
    }

    def "should generate markdown for performance gain"() {
        setup:
        def change = new Change("com.github.lombrozo.xnav.XnavBenchmark.xpath", 8.976, 8.876, -0.100, -1.12, "us/op", "avgt")
        def summary = new Summary(change)
        def markdownSummary = new MarkdownSummary(() -> summary)

        when:
        def result = markdownSummary.asMarkdown()

        then:
        normalize(result) == normalize(getClass().getClassLoader().getResourceAsStream("gain.md").text)
    }

    def "should generate markdown for performance loss"() {
        setup:
        def change = new Change("com.github.lombrozo.xnav.XnavBenchmark.xpath", 8.876, 8.976, 0.100, 1.13, "us/op", "avgt")
        def summary = new Summary(change)
        def markdownSummary = new MarkdownSummary(() -> summary)

        when:
        def result = markdownSummary.asMarkdown()

        then:
        normalize(result) == normalize(getClass().getClassLoader().getResourceAsStream("loss.md").text)
    }

    def "should generate markdown for same performance"() {
        setup:
        def change = new Change("com.github.lombrozo.xnav.XnavBenchmark.xpath", 8.876, 8.876, 0.000, 0.00, "us/op", "avgt")
        def summary = new Summary(change)
        def markdownSummary = new MarkdownSummary(() -> summary)

        when:
        def result = markdownSummary.asMarkdown()

        then:
        normalize(result) == normalize(getClass().getClassLoader().getResourceAsStream("same.md").text)
    }

    def "should generate markdown for critical performance loss"() {
        setup:
        def change = new Change("critical", 1, 10, 9.000, 900.00, "us/op", "avgt")
        def summary = new Summary(change)
        def markdownSummary = new MarkdownSummary(() -> summary)

        when:
        def result = markdownSummary.asMarkdown()

        then:
        normalize(result) == normalize(getClass().getClassLoader().getResourceAsStream("critical_loss.md").text)
    }

    def "should generate markdown for specific threshold, where the change is above of the threshold"() {
        setup:
        def change = new Change("above", 1, 10, 9, 900, "us/op", "avgt")
        def summary = new Summary(change)
        def markdownSummary = new MarkdownSummary(() -> summary, 100)

        when:
        def result = markdownSummary.asMarkdown()

        then:
        result.contains("is slower by 9.000 us/op (900.00%)")
        result.contains("❌")
    }


    def "should generate markdown for specific threshold, where the change is below of the threshold"() {
        setup:
        def change = new Change("below", 1, 10, 9, 900, "us/op", "avgt")
        def summary = new Summary(change)
        def markdownSummary = new MarkdownSummary(() -> summary, 1000)

        when:
        def result = markdownSummary.asMarkdown()

        then:
        result.contains("is slower by 9.000 us/op (900.00%)")
        result.contains("⚠️ ")
    }

    def normalize(String text) {
        text.replaceAll("\\r\\n|\\r|\\n", "")
    }
}
