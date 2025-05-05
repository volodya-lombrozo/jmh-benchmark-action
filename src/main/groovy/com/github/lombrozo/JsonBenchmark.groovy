package com.github.lombrozo


import org.json.JSONObject

final class JsonBenchmark implements Benchmark {

    private JSONObject json

    JsonBenchmark(final String json) {
        fromString(json)
    }

    JsonBenchmark(final JSONObject json) {
        this.json = json
    }

    @Override
    String name() {
        return json.getString("benchmark")
    }

    @Override
    double score() {
        return json.getJSONObject("primaryMetric").getDouble("score")
    }

    @Override
    String mode() {
        return json.getString("mode")
    }

    @Override
    String unit() {
        return json.getJSONObject("primaryMetric").getString("scoreUnit")
    }

    @Override
    Map<String, String> params() {
        return json.getJSONObject("params").toMap()
    }

    @Override
    boolean same(final Benchmark other) {
        return this.name() == other.name()
          && this.mode() == other.mode()
          && this.params() == other.params()
    }

    @Override
    String toString() {
        return "Benchmark JSON: $json"
    }

    private static JSONObject fromString(String json) {
        return new JSONObject(json);
    }
}
