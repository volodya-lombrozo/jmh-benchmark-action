package com.github.lombrozo;

final class MarkdownSummary {

    private final Diff diff;

    MarkdownSummary(final Diff diff) {
        this.diff = diff;
    }

    String asMarkdown() {
        try {
            final Summary summary = this.diff.summary();
            final StringBuilder markdown = new StringBuilder(0);
            markdown.append("### 🚀 Performance Analysis\n\n");
            markdown.append("| Test | Base Score | PR Score | Change | % Change | Unit | Mode |\n");
            markdown.append("|------|------------|----------|--------|----------|------|------|\n");
            for (final Change change : summary.rows()) {
                markdown.append(
                  String.format(
                    "| `%s` | %.3f | %.3f | %.3f | %.2f%% | %s | %s |%n",
                    change.name(),
                    change.baseScore(),
                    change.newScore(),
                    change.diffScore(),
                    change.diffPercent(),
                    change.unit(),
                    hmode(change.mode())
                  )
                );
            }
            markdown.append(this.recup(summary)).append("\n");
            return markdown.toString();
        } catch (final NothingToCompare nothing) {
            return """### ⚠️ Benchmark Comparison Unavailable

Unfortunately, one of the benchmarks is missing, and we couldn't generate a performance comparison report.

Please ensure that both the base and PR benchmark results are available for analysis.
""";
        }
    }

    private String recup(final Summary summary) {
        final StringBuilder markdown = new StringBuilder(0);
        for (final Change change : summary.rows()) {
            if (change.mode().equals("thrpt")) {
                if (change.diffScore() < 0) {
                    if (change.diffPercent() < -100) {
                        markdown.append(this.critical(change));
                    } else {
                        markdown.append(this.loss(change));
                    }
                } else {
                    markdown.append(this.gain(change));
                }
            } else if (change.mode().equals("avgt")) {
                if (change.diffScore() > 0) {
                    if (change.diffPercent() > 100) {
                        markdown.append(this.critical(change));
                    } else {
                        markdown.append(this.loss(change));
                    }
                } else {
                    markdown.append(this.gain(change));
                }
            }
        }
        return markdown.toString();
    }

    private String gain(final Change change) {
        return String.format(
          "%n✅ Performance gain: `%s` is faster by %.3f %s (%.2f%%)",
          change.name(),
          Math.abs(change.diffScore()),
          change.unit(),
          Math.abs(change.diffPercent())
        );
    }

    private String loss(final Change change) {
        return String.format(
          "%n⚠️ Performance loss: `%s` is slower by %.3f %s (%.2f%%)",
          change.name(),
          change.diffScore(),
          change.unit(),
          change.diffPercent()
        );
    }

    private String critical(final Change change) {
        return String.format(
          "%n❌ Performance loss: `%s` is slower by %.3f %s (%.2f%%)",
          change.name(),
          change.diffScore(),
          change.unit(),
          change.diffPercent()
        );
    }

    /**
     * Human readable mode.
     * @param mode Mode
     * @return Human readable mode
     */
    private static String hmode(final String mode) {
        return switch (mode) {
            case "avgt" -> "Average Time";
            case "thrpt" -> "Throughput";
            case "sample" -> "Sample";
            case null -> mode;
            default -> mode;
        };
    }
}
