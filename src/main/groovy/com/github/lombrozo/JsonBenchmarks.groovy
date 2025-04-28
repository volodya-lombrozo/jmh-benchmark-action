package com.github.lombrozo

import org.json.JSONArray
import org.json.JSONObject

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path

class JsonBenchmarks implements Benchmarks {

    private JSONArray array

    JsonBenchmarks(final Path path) {
        fromFile(path)
    }

    JsonBenchmarks(final String json) {
        fromString(json)
    }

    @Override
    List<Benchmark> all() {
        return array.collect({ it -> new JsonBenchmark(it as JSONObject) as Benchmark })
    }

    /**
     * Read a JSON array from a file.
     * @param path Path to the JSON file
     */
    private void fromFile(final Path path) {
        try {
            array = new JSONArray(new String(Files.readAllBytes(path), Charset.defaultCharset()))
        } catch (Exception e) {
            println "Error reading $path: ${e.message}"
            throw new IllegalStateException("Invalid JSON file.", e)
        }
    }

    /**
     * Read a JSON array from a string.
     * @param json JSON string
     */
    private void fromString(final String json) {
        try {
            array = new JSONArray(json)
        } catch (Exception e) {
            println "Error parsing JSON: ${e.message}"
            throw new IllegalStateException("Invalid JSON string.", e)
        }
    }
}
