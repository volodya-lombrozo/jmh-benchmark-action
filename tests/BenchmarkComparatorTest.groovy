@Grab('org.spockframework:spock-core:2.4-M5-groovy-4.0')
@Grab('org.json:json:20250107')
import spock.lang.Specification

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class BenchmarkComparatorTest extends Specification {

    def "test compareBenchmarks"() {
        setup:
        Path directory = Files.createTempDirectory("groovy-jmh-tests");
        directory.toFile().deleteOnExit();
        print "Created temp directory ${directory}"
        def actual = "benchmark-comment.md"
        Files.createDirectory(directory.resolve("base"))
        Files.createDirectory(directory.resolve("pr"))
        Files.write(directory.resolve("base").resolve("benchmark.json"), faster.bytes)
        Files.write(directory.resolve("pr").resolve("benchmark.json"), longer.bytes)

        when:
        BenchmarkComparator.compareBenchmarks(
          directory.resolve("base").resolve("benchmark.json").toString(),
          directory.resolve("pr").resolve("benchmark.json").toString(),
          actual
        )

        then:
        def output = new String(Files.readAllBytes(Paths.get(actual)))
        output.contains("| test1 | 100.000 | 110.000 | 10.000 | 10.00% |")
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