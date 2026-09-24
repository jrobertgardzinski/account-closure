package com.jrobertgardzinski.closure;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The two questions every participant asks of a command before it acts, asked once here so that
 * three services cannot answer them three different ways.
 */
@Epic("Contract")
@Feature("Account closure vocabulary")
class ClosureCommandTest {

    private ClosureCommand command(String email, String initiatedBy) {
        return new ClosureCommand(ClosureMessages.PURGE_USER_CONTENT, "saga-1", email, initiatedBy,
                Optional.empty());
    }

    @Test
    @DisplayName("a command with no address names nobody, blank or absent alike")
    void an_unaddressed_command_is_recognised() {
        assertTrue(command("leaver@example.com", ClosureInitiator.SELF.wire()).isAddressed());
        assertFalse(command("   ", ClosureInitiator.SELF.wire()).isAddressed());
        assertFalse(command("", ClosureInitiator.SELF.wire()).isAddressed());
        assertFalse(command(null, ClosureInitiator.SELF.wire()).isAddressed());
    }

    @Test
    @DisplayName("only an administrator's closure may carry conditions; every unknown word deletes")
    void conditions_need_an_administrator() {
        assertTrue(command("leaver@example.com", ClosureInitiator.ADMIN.wire()).allowsConditions());
        assertFalse(command("leaver@example.com", ClosureInitiator.SELF.wire()).allowsConditions());
        assertFalse(command("leaver@example.com", "MODERATOR").allowsConditions());
        assertFalse(command("leaver@example.com", null).allowsConditions());
    }
}
