@Grab('org.json:json:20250107')
import org.json.JSONArray
import org.json.JSONObject
import java.nio.file.Files
import java.nio.file.Paths

class BenchmarkComparator {

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
        report.append("### 🚀 Performance Report\n\n")
        report.append("| Test | Base Score | PR Score | Change | % Change | Unit |\n")
        report.append("|------|------------|---------|--------|----------|------|\n")

        baseResults.each { baseResult ->
            if (baseResult.getString("mode") == "avgt") {
                def testName = baseResult.getString("benchmark")
                def baseMode = baseResult.getString("mode")
                def baseScore = baseResult.getJSONObject("primaryMetric").getDouble("score")
                def scoreUnit = baseResult.getJSONObject("primaryMetric").getString("scoreUnit")

                prResults.each { prResult ->
                    if (prResult.getString("benchmark") == testName && prResult.getString("mode") == baseMode) {
                        def prScore = prResult.getJSONObject("primaryMetric").getDouble("score")
                        double change = prScore - baseScore
                        double percentageChange = (change / baseScore) * 100

                        report.append(String.format("| `%s` | %.3f | %.3f | %.3f | %.2f%% | %s |\n",
                          testName, baseScore, prScore, change, percentageChange, scoreUnit))

                        String fpchange = String.format("%.2f", Math.abs(percentageChange))
                        String fchange = String.format("%.3f", Math.abs(change))
                        if (baseMode == "thrpt") {
                            if (change < 0) {
                                report.append("\n⚠️ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                                if (percentageChange < -100) {
                                    report.append("\n❌ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                                }
                            } else {
                                report.append("\n✅ Performance gain: `${testName}` is faster by ${fchange} ${scoreUnit} (${fpchange}%)")
                            }
                        } else {
                            if (change > 0) {
                                report.append("\n⚠️ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                                if (percentageChange > 100) {
                                    report.append("\n❌ Performance loss: `${testName}` is slower by ${fchange} ${scoreUnit} (${fpchange}%)")
                                }
                            } else {
                                report.append("\n✅ Performance gain: `${testName}` is faster by ${fchange} ${scoreUnit} (${fpchange}%)")
                            }
                        }
                    }
                }
            }
        }

        Files.write(Paths.get(output), report.toString().bytes)
        println "Benchmark comparison completed. Results saved to $output"
    }
}

