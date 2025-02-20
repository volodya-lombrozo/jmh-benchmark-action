const fs = require('fs');

async function run() {
    const github = require('@actions/github');
    const core = require('@actions/core');

    const token = core.getInput("github-token");
    const benchmarkFile = core.getInput("benchmark-file");
    const context = github.context;

    // Get benchmark file name from environment variable
    const benchmarkFile = process.env.BENCHMARK_FILE || "benchmark.json"
    const path = `pr/${benchmarkFile}`;
    let benchmarkResults = '';

    if (fs.existsSync(path)) {
        benchmarkResults = fs.readFileSync(path, 'utf8');
    } else {
        benchmarkResults = 'No benchmark results found.';
    }

    const commentBody = `
    ### 🔥 JMH Benchmark Results 🔥
    \`\`\`json
    ${benchmarkResults}
    \`\`\`
    `;

    const octokit = github.getOctokit(token);
    await octokit.rest.issues.createComment({
        issue_number: context.issue.number,
        owner: context.repo.owner,
        repo: context.repo.repo,
        body: commentBody
    });
}

run().catch(error => {
    console.error(error);
    process.exit(1);
});
