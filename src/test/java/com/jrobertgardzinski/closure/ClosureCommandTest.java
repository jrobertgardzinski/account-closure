package com.jrobertgardzinski.closure;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.jrobertgardzinski.identity.UserId;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("a command carrying the leaver's id is addressed even without an address")
    void an_id_addresses_the_command_on_its_own() {
        UserId leaver = UserId.random();
        assertTrue(new ClosureCommand(ClosureMessages.PURGE_USER_CONTENT, "saga-1", "",
                Optional.of(leaver), ClosureInitiator.SELF.wire(), Optional.empty()).isAddressed());
        assertEquals(Optional.empty(), command("leaver@example.com", ClosureInitiator.SELF.wire()).userId(),
                "the old shape carries no id");
    }

    @Test
    @DisplayName("the wire form of an id is read leniently: blank or mangled means absent")
    void the_wire_form_is_read_leniently() {
        UserId leaver = UserId.random();
        assertEquals(Optional.of(leaver), ClosureCommand.userIdOf(leaver.toString()));
        assertEquals(Optional.empty(), ClosureCommand.userIdOf(null));
        assertEquals(Optional.empty(), ClosureCommand.userIdOf("  "));
        assertEquals(Optional.empty(), ClosureCommand.userIdOf("not-a-uuid"));
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
