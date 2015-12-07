Cuppa

BDD-style test framework inspired by <a href="https://mochajs.org">Mocha</a>

How does it work?

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
