package com.github.lombrozo

import spock.lang.Specification

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

final class BenchmarkComparatorTest extends Specification {

    Path faster = Paths.get("fast.json")
    Path longer = Paths.get("long.json")

    def setupTempDirectory(final Path baseJson, final Path prJson) {
        Path directory = Files.createTempDirectory("groovy-jmh-tests" + new Random().nextInt())
        directory.toFile().deleteOnExit()
        Files.createDirectory(directory.resolve("base"))
        Files.createDirectory(directory.resolve("pr"))
        def base = directory.resolve("base").resolve("benchmark.json")
        Files.write(base, json(baseJson))
        base.toFile().deleteOnExit()
        def pr = directory.resolve("pr").resolve("benchmark.json")
        Files.write(pr, json(prJson))
        pr.toFile().deleteOnExit()
        return directory
    }

    def json(final Path resource) {
        return getClass().getResourceAsStream("/$resource").text.getBytes(Charset.defaultCharset())
    }

    def "test compareBenchmarks with performance loss"() {
        setup:
        Path directory = setupTempDirectory(faster, longer)
        def actual = "benchmark-comment.md"

        when:
        BenchmarkComparator.compareBenchmarks(
          directory.resolve("base").resolve("benchmark.json").toString(),
          directory.resolve("pr").resolve("benchmark.json").toString(),
          directory.resolve(actual).toString()
        )

        then:
        def comment = directory.resolve(actual)
        def output = new String(Files.readAllBytes(comment))
        comment.toFile().deleteOnExit()
        output.contains("| `com.github.lombrozo.xnav.XnavBenchmark.xpath` | 8.876 | 8.976 | 0.100 | 1.13% | us/op |")
        output.contains("⚠️ Performance loss: `com.github.lombrozo.xnav.XnavBenchmark.xpath` is slower by 0.100 us/op (1.13%)")
    }

    def "test compareBenchmarks with performance gain"() {
        setup:
        Path directory = setupTempDirectory(longer, faster)
        def actual = "benchmark-comment.md"

        when:
        BenchmarkComparator.compareBenchmarks(
          directory.resolve("base").resolve("benchmark.json").toString(),
          directory.resolve("pr").resolve("benchmark.json").toString(),
          directory.resolve(actual).toString()
        )

        then:
        def comment = directory.resolve(actual)
        def output = new String(Files.readAllBytes(comment))
        comment.toFile().deleteOnExit()
        output.contains("| `com.github.lombrozo.xnav.XnavBenchmark.xpath` | 8.976 | 8.876 | -0.100 | -1.12% | us/op |")
        output.contains("✅ Performance gain: `com.github.lombrozo.xnav.XnavBenchmark.xpath` is faster by 0.100 us/op (1.12%)")
    }

    def "test compareBenchmarks with no change"() {
        setup:
        Path directory = setupTempDirectory(faster, faster)
        def actual = "benchmark-comment.md"

        when:
        BenchmarkComparator.compareBenchmarks(
          directory.resolve("base").resolve("benchmark.json").toString(),
          directory.resolve("pr").resolve("benchmark.json").toString(),
          directory.resolve(actual).toString()
        )

        then:
        def comment = directory.resolve(actual)
        def output = new String(Files.readAllBytes(comment))
        comment.toFile().deleteOnExit()
        output.contains("| `com.github.lombrozo.xnav.XnavBenchmark.xpath` | 8.876 | 8.876 | 0.000 | 0.00% | us/op |")
        output.contains("✅ Performance gain: `com.github.lombrozo.xnav.XnavBenchmark.xpath` is faster by 0.000 us/op (0.00%)")
    }
}