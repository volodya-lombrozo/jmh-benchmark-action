package com.github.lombrozo

final class NothingToCompare extends Exception {

    NothingToCompare() {
        super("One of the benchmarks is empty. Nothing to compare.")
    }
}
