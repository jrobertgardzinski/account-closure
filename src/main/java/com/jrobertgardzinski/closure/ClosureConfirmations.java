package com.jrobertgardzinski.closure;

/** Tells the orchestrator the reversible step is done and how many rows it reserved. */
@FunctionalInterface
public interface ClosureConfirmations {

    void confirm(String sagaId, String leaver, int reserved);
}
