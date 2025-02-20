@Grab('org.json:json:20210307')
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths

def parseJsonFile(String filePath) {
    try {
        return new JSONObject(new String(Files.readAllBytes(Paths.get(filePath))))
    } catch (Exception e) {
        println "Error reading $filePath: ${e.message}"
        throw new IllegalStateException("Invalid JSON file.")
    }
}

def compareBenchmarks(String baseFile, String prFile, String outputFile) {
    def baseResults = parseJsonFile(baseFile)
    def prResults = parseJsonFile(prFile)

    if (!baseResults || !prResults) {
        println "Error: Missing or invalid benchmark files."
        return
    } else {
        println "Comparing benchmarks..."
    }

    def report = new StringBuilder()
    report.append("### 🔥 JMH Benchmark Comparison 🔥\n\n")
    report.append("| Test | Base Score | PR Score | Change | % Change |\n")
    report.append("|------|------------|---------|--------|----------|\n")

    baseResults.keySet().each { test ->
        if (prResults.has(test)) {
            def baseScore = baseResults.getJSONObject(test).getDouble("score")
            def prScore = prResults.getJSONObject(test).getDouble("score")
            def change = prScore - baseScore
            def percentageChange = (change / baseScore) * 100

            report.append(String.format("| %s | %.3f | %.3f | %.3f | %.2f%% |\n",
              test, baseScore, prScore, change, percentageChange))
        }
    }

    Files.write(Paths.get(outputFile), report.toString().bytes)
    println "Benchmark comparison completed. Results saved to $outputFile"
}

compareBenchmarks("base/benchmark.json", "pr/benchmark.json", "benchmark-comment.md")
