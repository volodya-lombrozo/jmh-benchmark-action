package com.github.lombrozo

import spock.lang.Specification

final class JsonBenchmarksTest extends Specification {

    def "retrieves benchmark results"() {
        setup:
        def path = getClass().getResource("/fast.json").text

        when:
        def result = new JsonBenchmarks(path).all()

        then:
        assert result: "Result should not be null, for $path"
        assert result.size() > 0: "Result should not be empty"
    }

}