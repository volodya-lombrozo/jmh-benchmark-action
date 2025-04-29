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


    private JSONObject json() {
        new JSONObject(getClass().getResource("/single.json").text)
    }

}
