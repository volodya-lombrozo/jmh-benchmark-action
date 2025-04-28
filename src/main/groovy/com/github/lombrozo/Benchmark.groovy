package com.github.lombrozo

interface Benchmark {
    String name();

    double score();

    String mode();

    String unit();
}