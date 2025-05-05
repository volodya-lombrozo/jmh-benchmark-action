package com.github.lombrozo

import spock.lang.Specification

final class ChangeTest extends Specification {

    def "should correctly retrieve all properties"() {
        setup:
        def change = new Change(
          "BenchmarkTest",
          100.0,
          120.0,
          20.0,
          20.0,
          "ms",
          "avgt"
        )

        expect:
        change.name() == "BenchmarkTest"
        change.baseScore() == 100.0
        change.newScore() == 120.0
        change.diffScore() == 20.0
        change.diffPercent() == 20.0
        change.unit() == "ms"
        change.mode() == "avgt"
    }

    def "should return correct string representation"() {
        setup:
        def change = new Change(
          "BenchmarkTest",
          100.0,
          120.0,
          20.0,
          20.0,
          "ms",
          "avgt"
        )

        expect:
        change.toString() == "Change: BenchmarkTest, baseScore: 100.0, newScore: 120.0, diffScore: 20.0, diffPercent: 20.0, unit: ms, mode: avgt"
    }

    def "creates human readable name with the use of params"() {
        setup:
        def change = new Change(
          "BenchmarkTest",
          100.0,
          120.0,
          20.0,
          20.0,
          "ms",
          "avgt",
          ["param1": "value1", "param2": "value2"]
        )

        expect:
        change.name() == "BenchmarkTest (param1=value1, param2=value2)"
    }
}