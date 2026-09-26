package com.jrobertgardzinski.closure;

/** One unit of work: the mark and its confirmation must commit together, or not at all. */
@FunctionalInterface
public interface Atomically {

    void run(Runnable step);
}
