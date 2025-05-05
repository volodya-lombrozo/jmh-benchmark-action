package com.github.lombrozo

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
final class Change {

    private final String name;
    private final double baseScore;
    private final double newScore;
    private final double diffScore;
    private final double diffPercent;
    private final String unit;
    private final String mode;
    private final Map<String, String> params;

    /**
     * Constructor for Change.
     * @param name Name of the benchmark
     * @param baseScore Base score
     * @param newScore New score
     * @param diffScore Difference score
     * @param diffPercent Difference percentage
     * @param unit Unit of measurement
     * @param mode Mode of the benchmark
     */
    Change(
      final String name,
      final double baseScore,
      final double newScore,
      final double diffScore,
      final double diffPercent,
      final String unit,
      final String mode
    ) {
        this(name, baseScore, newScore, diffScore, diffPercent, unit, mode, [:])
    }


    /**
     * Constructor for Change.
     * @param name Name of the benchmark
     * @param baseScore Base score
     * @param newScore New score
     * @param diffScore Difference score
     * @param diffPercent Difference percentage
     * @param unit Unit of measurement
     * @param mode Mode of the benchmark
     * @param params Additional parameters
     */
    Change(
      final String name,
      final double baseScore,
      final double newScore,
      final double diffScore,
      final double diffPercent,
      final String unit,
      final String mode,
      final Map<String, String> params
    ) {
        this.name = name;
        this.baseScore = baseScore;
        this.newScore = newScore;
        this.diffScore = diffScore;
        this.diffPercent = diffPercent;
        this.unit = unit;
        this.mode = mode;
        this.params = params;
    }

    /**
     * Get the name of the benchmark.
     * @return Name of the benchmark
     */
    String name() {
        if (params.isEmpty()) {
            return this.name;
        } else {
            return String.format("%s (%s)", this.name, paramsToString())
        }
    }

    /**
     * Get the parameters of the benchmark as a string.
     * @return Parameters as a string
     */
    private String paramsToString() {
        return this.params.collect { k, v -> "${k}=${v}" }.join(", ")
    }

    /**
     * Get the base score.
     * @return Base score
     */
    double baseScore() {
        return this.baseScore;
    }

    /**
     * Get the new score.
     * @return New score
     */
    double newScore() {
        return this.newScore;
    }

    /**
     * Get the difference score.
     * @return Difference score
     */
    double diffScore() {
        return this.diffScore;
    }

    /**
     * Get the difference percentage.
     * @return Difference percentage
     */
    double diffPercent() {
        return this.diffPercent;
    }

    /**
     * Get the unit of measurement.
     * @return Unit of measurement
     */
    String unit() {
        return this.unit;
    }

    /**
     * Get the mode of the benchmark.
     * @return Mode of the benchmark
     */
    String mode() {
        return this.mode;
    }

    @Override
    String toString() {
        return "Change: ${name()}, baseScore: ${baseScore()}, newScore: ${newScore()}, diffScore: ${diffScore()}, diffPercent: ${diffPercent()}, unit: ${unit()}, mode: ${mode()}";
    }
}
