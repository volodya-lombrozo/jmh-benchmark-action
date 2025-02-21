# jmh-benchmark-action

To add a JMH benchmark action to your repository, create a new file in
the `.github/workflows` directory with the following content:

```yaml
name: Performance Regression Check
  on:
    pull_request:
      branches:
        - main

  jobs:
    benchmark:
      name: JMH
      runs-on: ubuntu-latest
      steps:
        - name: Run JMH Benchmark Action
          uses: volodya-lombrozo/jmh-benchmark-action@main
          with:
            java-version: "11"
            base-ref: "main"
            benchmark-command: |
              mvn test-compile
              mvn jmh:benchmark -Pbenchmark -Djmh.benchmarks=XnavBenchmark -Djmh.wi=1 -Djmh.i=2 -Djmh.f=1 -Djmh.rf=json -Djmh.rff=benchmark.json
            github-token: ${{ secrets.GITHUB_TOKEN }}
            benchmark-file: "benchmark.json"
```

This action will run JMH benchmarks on every pull request to the `main` branch.
The action will compare the results of the benchmarks with the results of the
benchmarks on the `main` branch and print comment with the results of the
comparison, for example:

___
### 🚀 Performance Analysis

| Test | Base Score | PR Score | Change | % Change | Unit | Mode |
|------|------------|---------|--------|----------|------|------|
| `com.github.lombrozo.xnav.XnavBenchmark.element` | 5.685 | 5.640 | -0.045 | -0.79% | us/op | Average Time |
| `com.github.lombrozo.xnav.XnavBenchmark.xpath` | 8.973 | 9.089 | 0.116 | 1.30% | us/op | Average Time |

✅ Performance gain: `com.github.lombrozo.xnav.XnavBenchmark.element` is faster by 0.045 us/op (0.79%)
⚠️ Performance loss: `com.github.lombrozo.xnav.XnavBenchmark.xpath` is slower by 0.116 us/op (1.30%)
___