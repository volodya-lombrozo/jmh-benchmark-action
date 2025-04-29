package com.github.lombrozo

import org.json.JSONArray

import java.nio.file.Files
import java.nio.file.Paths

final class BenchmarkComparator {

    /**
     * Read a JSON array
     * @param path Path to the JSON file
     * @return Json array
     */
    static JSONArray json(String path) {
        try {
            return new JSONArray(new String(Files.readAllBytes(Paths.get(path))))
        } catch (Exception e) {
            println "Error reading $path: ${e.message}"
            throw new IllegalStateException("Invalid JSON file.", e)
        }
    }

    static void compareBenchmarks(String base, String pr, String output) {
        def baseResults = json(base)
        def prResults = json(pr)


        if (!baseResults || !prResults) {
            println "Error: Missing or invalid benchmark files."
            return
        } else {
            println "Comparing benchmarks '$base' and '$pr'"
            println "Results will be saved to $output"
        }

        def report = new StringBuilder()
        def summary = new StringBuilder()
        report.append("### 🚀 Performance Analysis\n\n")
        report.append("| Test | Base Score | PR Score | Change | % Change | Unit | Mode |\n")
        report.append("|------|------------|---------|--------|----------|------|------|\n")

        baseResults.each { baseResult ->
            def testName = baseResult.getString("benchmark")
            def baseMode = baseResult.getString("mode")
            def baseScore = baseResult.getJSONObject("primaryMetric").getDouble("score")
            def scoreUnit = baseResult.getJSONObject("primaryMetric").getString("scoreUnit")
            def modeDescription = baseMode == "avgt" ? "Average Time" : "Throughput"

            prResults.each { prResult ->
                if (prResult.getString("benchmark") == testName && prResult.getString("mode") == baseMode) {
                    def prScore = prResult.getJSONObject("primaryMetric").getDouble("score")
                    double change = prScore - baseScore
                    double percentageChange = (change / baseScore) * 100

                    report.append(String.format("| `%s` | %.3f | %.3f | %.3f | %.2f%% | %s | %s |\n",
                      testName, baseScore, prScore, change, percentageChange, scoreUnit, modeDescription))

                    String fpchange = String.format("%.2f", Math.abs(percentageChange))
                    String fchange = String.format("%.3f", Math.abs(change))
                    if (baseMode == "thrpt") {
                        if (change < 0) {
                            summary.append("\n⚠️ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                            if (percentageChange < -100) {
                                summary.append("\n❌ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                            }
                        } else {
                            summary.append("\n✅ Performance gain: `${testName}` is faster by ${fchange} ${scoreUnit} (${fpchange}%)")
                        }
                    } else {
                        if (change > 0) {
                            summary.append("\n⚠️ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                            if (percentageChange > 100) {
                                summary.append("\n❌ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                            }
                        } else {
                            summary.append("\n✅ Performance gain: `${testName}` is faster by ${fchange} ${scoreUnit} (${fpchange}%)")
                        }
                    }
                }
            }
        }
        report.append(summary.toString())
        Files.write(Paths.get(output), report.toString().bytes)
        println "Benchmark comparison completed. Results saved to $output"
    }
}

