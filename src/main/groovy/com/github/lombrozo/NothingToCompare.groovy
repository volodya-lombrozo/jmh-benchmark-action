package com.github.lombrozo

class NothingToCompare extends Exception {

    NothingToCompare() {
        super("One of the benchmarks is empty. Nothing to compare.")
    }
}
