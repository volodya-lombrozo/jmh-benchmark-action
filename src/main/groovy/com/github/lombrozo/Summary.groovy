package com.github.lombrozo

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
final class Summary {

    private final List<Change> changes;

    /**
     * Constructor for Summary.
     * @param changes Array of changes
     */
    Summary(Change... changes) {
        this(Arrays.asList(changes))
    }

    /**
     * Constructor for Summary.
     * @param changes List of changes
     */
    Summary(final List<Change> changes) {
        this.changes = changes
    }

    List<Change> rows() {
        return changes;
    }
}
