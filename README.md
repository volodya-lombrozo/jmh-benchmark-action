# jmh-benchmark-action

`jmh-benchmark-action` is a GitHub Action designed to automate the process of
running Java Microbenchmark Harness (JMH) benchmarks on pull requests. It
compares the performance results against the base branch to identify any
performance regressions or improvements. This action is particularly useful for
developers who want to ensure that their code changes do not negatively impact
the performance of their application.

## How It Works

The `jmh-benchmark-action` automates the process of running JMH benchmarks on
both the pull request and the base branch, then compares the results to identify
performance changes. Here's a step-by-step breakdown of how it works:

1. **Check Out Branches**: The action checks out both the pull request branch
   and the base branch using the `actions/checkout` action.

2. **Set Up Java and Groovy**: It sets up the required Java version
   using `actions/setup-java` and Groovy using `wtfjoke/setup-groovy`.

3. **Run Benchmarks**: The action runs the specified benchmark command on both
   the base and pull request branches to generate benchmark results.

4. **Compare Results**: Using a Groovy script (`compare.groovy`), it compares
   the benchmark results from the two branches. The script reads JSON files
   containing the benchmark results and generates a report highlighting any
   performance gains or losses.

5. **Upload Artifacts**: The action saves the pull request number and uploads
   the benchmark comparison report as artifacts for further use, such as posting
   comments on the pull request.

This process ensures that any performance regressions or improvements are
clearly identified and reported, helping maintain optimal application
performance.

## Table of Contents

- [Introduction](#introduction)
- [Installation](#installation)
- [Print messages to a personal repository](#personal-repository-messages)
- [Print messages to an open source repository](#open-source-repository-messages)
- [Contributing](#contributing)
- [License](#license)

## Introduction

`jmh-benchmark-action` is a GitHub Action that runs JMH benchmarks on pull
requests and compares the results with the base branch. It helps in identifying
performance regressions and improvements.

## Installation

To add a JMH benchmark action to your repository, create a new file in
the `.github/workflows` directory with the following content.

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
          benchmark-file: "benchmark.json"
```

This action will run JMH benchmarks on every pull request to the `main` branch.
The action will compare the results of the benchmarks with the results of the
benchmarks on the `main` branch and print comment with the results of the
comparison.

<details>
<summary> Message Example </summary>

### 🚀 Performance Analysis

| Test                                             | Base Score | PR Score | Change | % Change | Unit  | Mode         |
|--------------------------------------------------|------------|----------|--------|----------|-------|--------------|
| `com.github.lombrozo.xnav.XnavBenchmark.element` | 5.685      | 5.640    | -0.045 | -0.79%   | us/op | Average Time |
| `com.github.lombrozo.xnav.XnavBenchmark.xpath`   | 8.973      | 9.089    | 0.116  | 1.30%    | us/op | Average Time |

✅ Performance gain: `com.github.lombrozo.xnav.XnavBenchmark.element` is faster
by 0.045 us/op (0.79%)

⚠️ Performance loss: `com.github.lombrozo.xnav.XnavBenchmark.xpath` is slower by
0.116 us/op (1.30%)

</details>

## Personal Repository Messages

If you use `jmh-benchmark-action` in your personal repository, you can print the
benchmark results directly withing the same workflow:

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
          benchmark-file: "benchmark.json"
      - name: Post a Comment with Benchmark Results
        uses: actions/github-script@v7
        with:
          github-token: ${{ secrets.GITHUB_TOKEN }}
          script: |
            const fs = require('fs');
            const path = 'benchmark-comment.md';
            let commentBody = '';
            if (fs.existsSync(path)) {
              commentBody = fs.readFileSync(path, 'utf8');
            } else {
              commentBody = 'No benchmark comparison available.';
            }
            github.rest.issues.createComment({
              issue_number: context.issue.number,
              owner: context.repo.owner,
              repo: context.repo.repo,
              body: commentBody
            });
```

This works because `GITHUB_TOKEN` is a secret token that is automatically
generated by GitHub Actions and has the necessary permissions to post comments
to the repository if the repository is owned by the user who created the token.

## Open Source Repository Messages

In case you use `jmh-benchmark-action` in an open source repository, you need
to solve the issue with the permissions to post comments. By default, all the
pull requests from forks do not have access to the secrets of the repository.
Moreover, `GITHUB_TOKEN` has only read permissions to the repository.

To solve this issue, it's better to split benchmark generation and comment
posting into two separate workflows. The first workflow will generate the
benchmark results and save them to a file. The second workflow will read the
file and post the comment with the benchmark results.
By doing this, you avoid the need to give write permissions to the forked
repositories.

For example, here is how you can split the workflow. Benchmark workflow:

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
            mvn jmh:benchmark -Pbenchmark -Djmh.benchmarks=XnavBenchmark -Djmh.wi=1 -Djmh.i=1 -Djmh.f=1 -Djmh.rf=json -Djmh.rff=benchmark.json
          benchmark-file: "benchmark.json"
```

And the comment posting workflow:

```yaml
name: Post Benchmark Comment
on:
  workflow_run:
    workflows: [ "Performance Regression Check" ]
    types:
      - completed

jobs:
  post-comment:
    runs-on: ubuntu-latest
    steps:
      - name: Download Benchmark Comment
        uses: actions/download-artifact@v4
        with:
          name: benchmark-comment
          run-id: ${{ github.event.workflow_run.id }}
          github-token: ${{ secrets.GITHUB_TOKEN }}
      - name: Post Comment on PR
        uses: mshick/add-pr-comment@v2
        with:
          issue: ${{ github.event.workflow_run.pull_requests[0].number }}
          message-path: |
            benchmark-comment.md
          repo-token: ${{ secrets.GITHUB_TOKEN }}
```

The second workflow starts when the first workflow is completed. It downloads
the benchmark results from the first workflow and posts them as a comment to the
pull request.

Pay attention that the second workflow should be merged into the main branch
before the first workflow is triggered.

## Contributing

Contributions are welcome! Just open an issue or submit a pull request.

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file
for details.

