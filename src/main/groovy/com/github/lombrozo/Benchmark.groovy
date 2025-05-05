package com.github.lombrozo

interface Benchmark {
    String name();

    double score();

    String mode();

    String unit();

    Map<String, String> params();

    boolean same(Benchmark other);
}
