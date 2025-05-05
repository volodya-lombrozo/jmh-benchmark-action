package com.github.lombrozo

import org.json.JSONObject
import spock.lang.Specification

final class JsonBenchmarkTest extends Specification {

    def "retrieves name"() {
        setup:
        def benchmark = new JsonBenchmark(json())

        expect:
        benchmark.name() == "com.github.lombrozo.xnav.XnavBenchmark.xpath"
    }

    def "retrieves score"() {
        setup:
        def benchmark = new JsonBenchmark(json())

        expect:
        benchmark.score() == 8.875712588759713
    }

    def "retrieves mode"() {
        setup:
        def benchmark = new JsonBenchmark(json())

        expect:
        benchmark.mode() == "avgt"
    }

    def "retrieves unit"() {
        setup:
        def benchmark = new JsonBenchmark(json())

        expect:
        benchmark.unit() == "us/op"
    }

    def "retrieves all from real json"() {
        setup:
        def benchmark = new JsonBenchmark(json())

        expect:
        benchmark.name() == "com.github.lombrozo.xnav.XnavBenchmark.xpath"
        benchmark.score() == 8.875712588759713
        benchmark.mode() == "avgt"
        benchmark.unit() == "us/op"
    }

    def "retrieves benchmark params"() {
        setup:
        def json = getClass().getResource("/parametrized.json").text

        when:
        def result = new JsonBenchmarks(json).all().get(0).params()

        then:
        assert result.get("impl").equals("dom-xml"): "The 'impl' should be 'dom-xml'"
        assert result.get("size").equals("small"): "The 'size' should be 'small'"
        assert result.size() == 2: "Map should have 2 entries"
    }

    def "should return true when benchmarks have the same name and mode"() {
        setup:
        def first = new JsonBenchmark(json("TestBenchmark", "avgt", 10.0, "ms"))
        def second = new JsonBenchmark(json("TestBenchmark", "avgt", 15.0, "ms"))

        expect:
        first.same(second)
    }

    def "should return false when benchmarks have different names"() {
        setup:
        def first = new JsonBenchmark(json("TestBenchmark1", "avgt", 10.0, "ms"))
        def second = new JsonBenchmark(json("TestBenchmark2", "avgt", 15.0, "ms"))

        expect:
        !first.same(second)
    }

    def "should return false when benchmarks have different modes"() {
        setup:
        def first = new JsonBenchmark(json("TestBenchmark", "avgt", 10.0, "ms"))
        def second = new JsonBenchmark(json("TestBenchmark", "thrpt", 15.0, "ms"))

        expect:
        !first.same(second)
    }

    def "should return false when benchmarks have different params"() {
        setup:
        def first = new JsonBenchmark(json("TestBenchmark", "avgt", 10.0, "ms", ["param1": "value1",]))
        def second = new JsonBenchmark(json("TestBenchmark", "avgt", 10.0, "ms", ["param1": "value2"]))

        expect:
        !first.same(second)
    }

    def "should return true when benchmarks have the same params"() {
        setup:
        def first = new JsonBenchmark(json("TestBenchmark", "avgt", 10.0, "ms", ["param1": "value1", "param2": "value2"]))
        def second = new JsonBenchmark(json("TestBenchmark", "avgt", 10.0, "ms", ["param2": "value2", "param1": "value1"]))

        expect:
        first.same(second)
    }

    private static JSONObject json(String name, String mode, double score, String unit, Map<String, String> params) {
        return new JSONObject([
          "benchmark"    : name,
          "mode"         : mode,
          "primaryMetric": [
            "score"    : score,
            "scoreUnit": unit
          ],
          "params"       : params
        ])
    }

    private static JSONObject json(String name, String mode, double score, String unit) {
        return new JSONObject([
          "benchmark"    : name,
          "mode"         : mode,
          "primaryMetric": [
            "score"    : score,
            "scoreUnit": unit
          ],
          "params"       : [:]
        ])
    }

    private JSONObject json() {
        new JSONObject(getClass().getResource("/single.json").text)
    }

}
