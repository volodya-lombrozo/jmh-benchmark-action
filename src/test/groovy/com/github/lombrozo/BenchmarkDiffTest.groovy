package com.github.lombrozo

import spock.lang.Specification

class BenchmarkDiffTest extends Specification {

    def "should throw NothingToCompare when base benchmarks are empty"() {
        setup:
        def base = new MockBenchmarks([])
        def compare = new MockBenchmarks([new MockBenchmark("Test", 10.0, "avgt", "ms")])
        def diff = new BenchmarkDiff(base, compare)

        when:
        diff.summary()

        then:
        thrown(NothingToCompare)
    }

    def "should throw NothingToCompare when compare benchmarks are empty"() {
        setup:
        def base = new MockBenchmarks([new MockBenchmark("Test", 10.0, "avgt", "ms")])
        def compare = new MockBenchmarks([])
        def diff = new BenchmarkDiff(base, compare)

        when:
        diff.summary()

        then:
        thrown(NothingToCompare)
    }

    def "should throw NothingToCompare when both base and compare benchmarks are empty"() {
        setup:
        def base = new MockBenchmarks([])
        def compare = new MockBenchmarks([])
        def diff = new BenchmarkDiff(base, compare)

        when:
        diff.summary()

        then:
        thrown(NothingToCompare)
    }

    def "should return null summary when base and compare benchmarks are non-empty"() {
        setup:
        def base = new MockBenchmarks([new MockBenchmark("Test", 10.0, "avgt", "ms")])
        def compare = new MockBenchmarks([new MockBenchmark("Test", 12.0, "avgt", "ms")])
        def diff = new BenchmarkDiff(base, compare)

        when:
        def result = diff.summary()

        then:
        result == new Summary(new Change("Test", 10.0, 12.0, 2.0, 20.0, "ms", "avgt"))
    }

    private static class MockBenchmark implements Benchmark {
        private final String name
        private final double score
        private final String mode
        private final String unit

        MockBenchmark(String name, double score, String mode, String unit) {
            this.name = name
            this.score = score
            this.mode = mode
            this.unit = unit
        }

        @Override
        String name() {
            return name
        }

        @Override
        double score() {
            return score
        }

        @Override
        String mode() {
            return mode
        }

        @Override
        String unit() {
            return unit
        }
    }

    private static class MockBenchmarks implements Benchmarks {
        private final List<Benchmark> benchmarks

        MockBenchmarks(List<Benchmark> benchmarks) {
            this.benchmarks = benchmarks
        }

        @Override
        List<Benchmark> all() {
            return benchmarks
        }
    }
}