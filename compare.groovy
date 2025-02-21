@Grab('org.json:json:20250107')
import BenchmarkComparator

BenchmarkComparator.compareBenchmarks("base/benchmark.json", "pr/benchmark.json", "benchmark-comment.md")
