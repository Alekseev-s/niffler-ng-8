package guru.qa.niffler.utils;

import guru.qa.niffler.jupiter.extension.ScreenShotExtension;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

public class ScreenDiffResult implements BooleanSupplier {

    private final BufferedImage expected;
    private final BufferedImage actual;
    private final ImageDiff diff;
    private final Boolean hasDiff;

    public ScreenDiffResult(BufferedImage expected, BufferedImage actual) {
        this.expected = expected;
        this.actual = actual;
        this.diff = new ImageDiffer().makeDiff(expected, actual);
        this.hasDiff = diff.hasDiff();
    }

    @Override
    public boolean getAsBoolean() {
        if (hasDiff){
            ScreenShotExtension.setExpected(expected);
            ScreenShotExtension.setActual(actual);
            ScreenShotExtension.setDiff(diff.getMarkedImage());
        }
        return hasDiff;
    }
}
