package com.github.lombrozo

final class BenchmarkDiff implements Diff {

    private Benchmarks base
    private Benchmarks compare

    BenchmarkDiff(final Benchmarks base, final Benchmarks compare) {
        this.base = base
        this.compare = compare
    }

    Summary summary() throws NothingToCompare {
        if (base.all().isEmpty() || compare.all().isEmpty()) {
            throw new NothingToCompare()
        }
        def changes = []
        base.all().each { baseBenchmark ->
            def compareBenchmark = compare.all().find {
                it.name() == baseBenchmark.name() && it.mode() == baseBenchmark.mode()
            }
            if (compareBenchmark) {
                double baseScore = baseBenchmark.score()
                double compareScore = compareBenchmark.score()
                double change = compareScore - baseScore
                double percentageChange = (change / baseScore) * 100
                changes.add(
                  new Change(
                    baseBenchmark.name(),
                    baseScore,
                    compareScore,
                    change,
                    percentageChange,
                    baseBenchmark.unit(),
                    baseBenchmark.mode()
                  )
                )
            }
        }
        return new Summary(changes)
    }
}
