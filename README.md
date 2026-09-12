# account-closure

The words five services must agree on to close one account.

```java
// identity, stating the fact
Map.of(ClosureMessages.Field.TYPE, ClosureMessages.ACCOUNT_DELETION_REQUESTED,
       ClosureMessages.Field.INITIATED_BY, ClosureInitiator.SELF.wire());

// a content service, receiving a command
if (!ClosureInitiator.allowsConditions(command.path(INITIATED_BY).asText())) {
    // the leaver asked; the policy beside it is not honoured, whatever it says
}
```

## Why a library for seven strings

Because they were not seven strings. They were five spellings of one agreement: identity had an
enum (`DeletionInitiator`), the orchestrator had constants in a `final class` (`RequestedBy`), and
each content service had a `private static final String BY_ADMIN = "ADMIN"` — plus the message
names as literals in every one of them.

None of that is a compile error when it drifts. A renamed command is a command nobody acts on; a
mis-read initiator is a leaver's erasure carried out under an administrator's conditions, which is
the one outcome the whole saga exists to prevent. The pacts catch some of it, between the pairs
that have pacts.

## The asymmetry is the point

`ClosureInitiator.of` is not a symmetrical parse. Anything that is not exactly `ADMIN` reads as
`SELF`, because both unknowns — a message from before the field existed, and a value nobody
recognises — must fall to the side that deletes. A right to erasure that a typo can suspend is not
one.

## What is deliberately NOT here

The envelope (`id`, `version`), the topics, the policy object's shape, and anything about how a
message travels. Those are per-deployment and per-service: the orchestrator speaks of a `Source`
and a `Destination` precisely so the same saga can run in a monolith with no topics at all.

Pure Java, no dependencies — Micronaut, Spring and Quarkus consumers all hold this.
