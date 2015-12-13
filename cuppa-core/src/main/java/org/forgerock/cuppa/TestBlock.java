package org.forgerock.cuppa;

import static org.forgerock.cuppa.Behaviour.SKIP;
/**
 * Encapsulates the test ('it') function block.
 */
class TestBlock {

    private final Behaviour behaviour;
    private final String description;
    private final TestFunction function;

    TestBlock(Behaviour behaviour, String description, TestFunction function) {
        this.behaviour = behaviour;
        this.description = description;
        this.function = function;
    }

    Behaviour getBehaviour() {
        return behaviour;
    }

    void runTest(Behaviour behaviour, Reporter reporter) {
        if (behaviour.combine(this.behaviour) != SKIP) {
            try {
                function.apply();
                reporter.testPass(description);
            } catch (AssertionError e) {
                reporter.testFail(description, e);
            } catch (PendingException e) {
                reporter.testPending(description);
            } catch (Exception e) {
                reporter.testError(description, e);
            }
        } else {
            reporter.testSkip(description);
        }
    }
}
