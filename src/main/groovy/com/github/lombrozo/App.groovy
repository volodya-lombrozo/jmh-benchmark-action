package com.github.lombrozo

import java.nio.file.Files
import java.nio.file.Paths

final class App {
    static void main(String[] args) {
        if (args.length < 1) {
            println "add at least some command"
            System.exit(-1)
        }
        if (args.length < 3) {
            println "<base> <pr> <result>\nbase: path to the base benchmark file\npr: path to the pull request benchmark file\nresult: path to the result file"
            System.exit(-1)
        }
        String base = args[0]
        String pr = args[1]
        String result = args[2]
        if (!base || !pr) {
            println "Error: Missing or invalid benchmark files."
            System.exit(-1)
        } else {
            println "Comparing benchmarks '$base' and '$pr'"
            println "Results will be saved to $result"
        }
        def basepath = Paths.get(base).toAbsolutePath()
        if (Files.notExists(basepath)) {
            throw new IllegalArgumentException("Base file does not exist: ${basepath}")
        }
        def prpath = Paths.get(pr).toAbsolutePath()
        if (Files.notExists(prpath)) {
            throw new IllegalArgumentException("PR file does not exist: $prpath")
        }
        def markdown = new MarkdownSummary(
          new BenchmarkDiff(
            new JsonBenchmarks(basepath),
            new JsonBenchmarks(prpath)
          ),
          findThreshold(args)
        ).asMarkdown()
        Files.write(Paths.get(result), markdown.bytes)
    }

    static int findThreshold(String[] args) {
        int threshold = 100
        args.each { arg ->
            if (arg.startsWith("--threshold=")) {
                try {
                    threshold = Integer.parseInt(arg.split("=")[1])
                } catch (NumberFormatException e) {
                    println "Invalid threshold value. Using default: $threshold"
                }
            }
        }
        return threshold
    }
}
