package com.github.lombrozo

import spock.lang.Specification

final class SummaryTest extends Specification {

    def "should retrieve all changes"() {
        setup:
        def change1 = new Change("Benchmark1", 100.0, 110.0, 10.0, 10.0, "ms", "avgt")
        def change2 = new Change("Benchmark2", 200.0, 180.0, -20.0, -10.0, "ms", "avgt")
        def summary = new Summary(change1, change2)

        expect:
        summary.rows().size() == 2
        summary.rows().contains(change1)
        summary.rows().contains(change2)
    }

    def "should handle empty list of changes"() {
        setup:
        def summary = new Summary([])

        expect:
        summary.rows().isEmpty()
    }

    def "should correctly retrieve changes from a list"() {
        setup:
        def changes = [
          new Change("Benchmark1", 100.0, 110.0, 10.0, 10.0, "ms", "avgt"),
          new Change("Benchmark2", 200.0, 180.0, -20.0, -10.0, "ms", "avgt")
        ]
        def summary = new Summary(changes)

        expect:
        summary.rows() == changes
    }
}