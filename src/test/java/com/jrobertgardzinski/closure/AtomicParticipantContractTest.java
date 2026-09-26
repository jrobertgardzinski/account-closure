package com.jrobertgardzinski.closure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** The contract for a participant that confirms through an outbox: the confirmation must be made inside the mark's unit of work. */
public abstract class AtomicParticipantContractTest extends ClosureParticipantContractTest {

    private boolean inside;
    private int confirmedCount = -1;
    private final List<String> confirmedInside = new ArrayList<>();

    protected final Atomically atomically = step -> {
        inside = true;
        try {
            step.run();
        } finally {
            inside = false;
        }
    };

    protected final ClosureConfirmations confirmations = (sagaId, leaver, reserved) -> {
        if (inside) {
            confirmedInside.add(sagaId);
        }
        confirmedCount = reserved;
    };

    @Override
    protected final int confirmed() {
        return confirmedCount;
    }

    @Test
    @DisplayName("the confirmation is made inside the same unit of work as the mark")
    void the_confirmation_is_made_inside_the_unit_of_work() {
        givenLeaverHolds(3);
        handle(command(ClosureMessages.PURGE_USER_CONTENT));
        assertEquals(List.of(SAGA), confirmedInside);
    }
}
