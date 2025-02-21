@Grab('org.spockframework:spock-core:2.4-M5-groovy-4.0')
@Grab('org.json:json:20250107')
import spock.lang.Specification
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import java.nio.file.Path

class CompareTest extends Specification {

    def "test compareBenchmarks"() {
        setup:
        Path directory = Files.createTempDirectory("groovy-jmh-tests");
        directory.toFile().deleteOnExit();
        print "Created temp directory ${directory}"
        def actual = "benchmark-comment.md"
        Files.createDirectory(directory.resolve("base"))
        Files.createDirectory(directory.resolve("pr"))
        Files.write(
          directory.resolve("base").resolve("benchmark.json"),
          '{"test1": {"score": 100.0}}'.bytes
        )
        Files.write(
          directory.resolve("pr").resolve("benchmark.json"),
          '{"test1": {"score": 110.0}}'.bytes
        )

        when:
        new GroovyShell().evaluate(new File("compare.groovy"))

        then:
        def output = new String(Files.readAllBytes(Paths.get(actual)))
        output.contains("| test1 | 100.000 | 110.000 | 10.000 | 10.00% |")
    }
}