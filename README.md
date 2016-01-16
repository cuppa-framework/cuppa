# Cuppa [![Build Status](https://travis-ci.org/phillcunnington/cuppa.svg?branch=master)](https://travis-ci.org/phillcunnington/cuppa)

Cuppa is a BDD-style test framework for Java 8. It makes writing tests productive and fun.

It is inspired by the wonderful <a href="https://mochajs.org">Mocha</a>.

### Example

```java
import static org.assertj.core.api.Assertions.assertThat;
import static org.forgerock.mocha.Mocha.*;
import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;

@Test
public class ListTest {
    List<Integer> list = Arrays.asList(1, 2, 3);
    {
        describe("List#indexOf", () -> {
            when("the value is not present", () -> {
                it("returns -1", () -> {
                    assertThat(list.indexOf(5)).isEqualTo(-1);
                    assertThat(list.indexOf(0)).isEqualTo(-1);
                });
            });
        });
    }
}
```

### Status

Cuppa is still in active development and hasn't reached a stable state yet.
