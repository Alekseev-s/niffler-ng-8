package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementCondition;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.spend.Bubble;
import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.CheckResult.accepted;
import static com.codeborne.selenide.CheckResult.rejected;

@ParametersAreNonnullByDefault
public class StatCondition {

    @Nonnull
    public static WebElementCondition color(Color expectedColor) {
        return new WebElementCondition("color " + expectedColor.rgb) {
            @Nonnull
            @Override
            public CheckResult check(Driver driver, WebElement webElement) {
                final String rgba = webElement.getCssValue("background-color");
                return new CheckResult(
                        expectedColor.rgb.equals(rgba),
                        rgba
                );
            }
        };
    }

    @Nonnull
    public static WebElementsCondition color(@Nonnull Color... expectedColors) {
        return new WebElementsCondition() {

            private final String expectedRgba = Arrays.stream(expectedColors).map(c -> c.rgb).toList().toString();

            @Nonnull
            @Override
            public CheckResult check(Driver driver, List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedColors)) {
                    throw new IllegalArgumentException("No expected colors given");
                }
                if (expectedColors.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedColors.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                final List<String> actualRgbaList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Color colorToCheck = expectedColors[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    actualRgbaList.add(rgba);
                    if (passed) {
                        passed = colorToCheck.rgb.equals(rgba);
                    }
                }

                if (!passed) {
                    final String actualRgba = actualRgbaList.toString();
                    final String message = String.format(
                            "List colors mismatch (expected: %s, actual: %s)", expectedRgba, actualRgba
                    );
                    return rejected(message, actualRgba);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedRgba;
            }
        };
    }

    public static WebElementsCondition bubbles(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedBubbleStr = Arrays.stream(expectedBubbles).toList().toString();

            @Nonnull
            @Override
            public CheckResult check(@Nonnull Driver driver, @Nonnull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles) && !elements.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                final List<Bubble> actualBubblesList = new ArrayList<>();
                for (int i = 0; i < elements.size(); i++) {
                    final WebElement elementToCheck = elements.get(i);
                    final Bubble bubbleToCheck = expectedBubbles[i];
                    final String rgba = elementToCheck.getCssValue("background-color");
                    final String text = elementToCheck.getText();
                    actualBubblesList.add(new Bubble(Color.fromRgb(rgba), text));
                    if (passed) {
                        passed = bubbleToCheck.color().rgb.equals(rgba) && bubbleToCheck.text().equals(text);
                    }
                }

                if (!passed) {
                    final String actualBubbles = actualBubblesList.toString();
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbleStr, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbleStr;
            }
        };
    }

    public static WebElementsCondition bubblesAnyOrder(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedBubbleStr = Arrays.stream(expectedBubbles).toList().toString();

            @Nonnull
            @Override
            public CheckResult check(@Nonnull Driver driver, @Nonnull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles) && !elements.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (expectedBubbles.length != elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                List<Bubble> expectedBubblesList = Arrays.stream(expectedBubbles).sorted().toList();
                List<Bubble> actualBubblesList = elements.stream()
                        .map(el ->
                                new Bubble(
                                        Color.fromRgb(el.getCssValue("background-color")),
                                        el.getText()
                                )
                        ).sorted().toList();

                for (int i = 0; i < actualBubblesList.size(); i++) {
                    if (passed) {
                        passed = expectedBubblesList.get(i).equals(actualBubblesList.get(i));
                    }
                }

                if (!passed) {
                    final String actualBubbles = actualBubblesList.toString();
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbleStr, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbleStr;
            }
        };
    }

    public static WebElementsCondition bubblesContains(@Nonnull Bubble... expectedBubbles) {
        return new WebElementsCondition() {
            private final String expectedBubbleStr = Arrays.stream(expectedBubbles).toList().toString();

            @Nonnull
            @Override
            public CheckResult check(@Nonnull Driver driver, @Nonnull List<WebElement> elements) {
                if (ArrayUtils.isEmpty(expectedBubbles) && !elements.isEmpty()) {
                    throw new IllegalArgumentException("No expected bubbles given");
                }

                if (expectedBubbles.length > elements.size()) {
                    final String message = String.format("List size mismatch (expected: %s, actual: %s)", expectedBubbles.length, elements.size());
                    return rejected(message, elements);
                }

                boolean passed = true;
                List<Bubble> actualBubblesList = elements.stream()
                        .map(el ->
                                new Bubble(
                                        Color.fromRgb(el.getCssValue("background-color")),
                                        el.getText()
                                )
                        ).toList();

                for (Bubble expectedBubble : expectedBubbles) {
                    if (passed) {
                        for (Bubble bubble : actualBubblesList) {
                            if (expectedBubble.equals(bubble)) {
                                passed = true;
                                break;
                            } else passed = false;
                        }
                    }
                }

                if (!passed) {
                    final String actualBubbles = actualBubblesList.toString();
                    final String message = String.format(
                            "List bubbles mismatch (expected: %s, actual: %s)", expectedBubbleStr, actualBubbles
                    );
                    return rejected(message, actualBubbles);
                }
                return accepted();
            }

            @Override
            public String toString() {
                return expectedBubbleStr;
            }
        };
    }
}
