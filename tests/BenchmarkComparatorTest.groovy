@Grab('org.spockframework:spock-core:2.4-M5-groovy-4.0')
@Grab('org.json:json:20250107')
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class BenchmarkComparatorTest extends Specification {

    def setupTempDirectory(String baseJson, String prJson) {
        Path directory = Files.createTempDirectory("groovy-jmh-tests" + new Random().nextInt())
        directory.toFile().deleteOnExit()
        Files.createDirectory(directory.resolve("base"))
        Files.createDirectory(directory.resolve("pr"))
        def base = directory.resolve("base").resolve("benchmark.json")
        Files.write(base, baseJson.bytes)
        base.toFile().deleteOnExit()
        def pr = directory.resolve("pr").resolve("benchmark.json")
        Files.write(pr, prJson.bytes)
        pr.toFile().deleteOnExit()
        return directory
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

    def faster = '''
[
    {
        "jmhVersion" : "1.37",
        "benchmark" : "com.github.lombrozo.xnav.XnavBenchmark.xpath",
        "mode" : "avgt",
        "threads" : 1,
        "forks" : 1,
        "jvm" : "/opt/hostedtoolcache/Java_Zulu_jdk/11.0.26-4/x64/bin/java",
        "jvmArgs" : [
        ],
        "jdkVersion" : "11.0.26",
        "vmName" : "OpenJDK 64-Bit Server VM",
        "vmVersion" : "11.0.26+4-LTS",
        "warmupIterations" : 1,
        "warmupTime" : "10 s",
        "warmupBatchSize" : 1,
        "measurementIterations" : 2,
        "measurementTime" : "10 s",
        "measurementBatchSize" : 1,
        "primaryMetric" : {
            "score" : 8.875712588759713,
            "scoreError" : "NaN",
            "scoreConfidence" : [
                "NaN",
                "NaN"
            ],
            "scorePercentiles" : {
                "0.0" : 8.868145528261346,
                "50.0" : 8.875712588759713,
                "90.0" : 8.88327964925808,
                "95.0" : 8.88327964925808,
                "99.0" : 8.88327964925808,
                "99.9" : 8.88327964925808,
                "99.99" : 8.88327964925808,
                "99.999" : 8.88327964925808,
                "99.9999" : 8.88327964925808,
                "100.0" : 8.88327964925808
            },
            "scoreUnit" : "us/op",
            "rawData" : [
                [
                    8.868145528261346,
                    8.88327964925808
                ]
            ]
        },
        "secondaryMetrics" : {
        }
    }
]
'''

    def longer = '''
    [
        {
            "jmhVersion" : "1.37",
            "benchmark" : "com.github.lombrozo.xnav.XnavBenchmark.xpath",
            "mode" : "avgt",
            "threads" : 1,
            "forks" : 1,
            "jvm" : "/opt/hostedtoolcache/Java_Zulu_jdk/11.0.26-4/x64/bin/java",
            "jvmArgs" : [],
            "jdkVersion" : "11.0.26",
            "vmName" : "OpenJDK 64-Bit Server VM",
            "vmVersion" : "11.0.26+4-LTS",
            "warmupIterations" : 1,
            "warmupTime" : "10 s",
            "warmupBatchSize" : 1,
            "measurementIterations" : 2,
            "measurementTime" : "10 s",
            "measurementBatchSize" : 1,
            "primaryMetric" : {
                "score" : 8.975884654491566,
                "scoreError" : "NaN",
                "scoreConfidence" : ["NaN", "NaN"],
                "scorePercentiles" : {
                    "0.0" : 8.972752889811659,
                    "50.0" : 8.975884654491566,
                    "90.0" : 8.979016419171472,
                    "95.0" : 8.979016419171472,
                    "99.0" : 8.979016419171472,
                    "99.9" : 8.979016419171472,
                    "99.99" : 8.979016419171472,
                    "99.999" : 8.979016419171472,
                    "99.9999" : 8.979016419171472,
                    "100.0" : 8.979016419171472
                },
                "scoreUnit" : "us/op",
                "rawData" : [
                    [8.972752889811659, 8.979016419171472]
                ]
            },
            "secondaryMetrics" : {}
        }
    ]
    '''
}