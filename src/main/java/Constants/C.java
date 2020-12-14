package Constants;

import javafx.scene.image.Image;
import java.util.Objects;

public class C {
    public static        ClassLoader resource        = ClassLoader.getSystemClassLoader();
    public final static  Integer     MAIN_FORM       = 200;
    public final static  Integer     ADD_RESISTORS   = 201;
    public final static  double      coreWidth       = 900;
    public final static  double      coreHeight      = 600;
    public static final  String      CSS_ANCHOR_PANE = Objects.requireNonNull(resource.getResource("StyleSheets/AnchorPane.css")).toExternalForm();
    public static final  String      CSS_LABEL       = Objects.requireNonNull(resource.getResource("StyleSheets/Label.css")).toExternalForm();
    public static final  String      CSS_CHECKBOX    = Objects.requireNonNull(resource.getResource("StyleSheets/CheckBox.css")).toExternalForm();
    public static final  String      CSS_BUTTON      = Objects.requireNonNull(resource.getResource("StyleSheets/Button.css")).toExternalForm();
    public static final  String      CSS_TEXT_AREA   = Objects.requireNonNull(resource.getResource("StyleSheets/TextArea.css")).toExternalForm();
    public final static  String      CSS_TEXT_FIELD  = Objects.requireNonNull(resource.getResource("StyleSheets/TextField.css")).toExternalForm();
    private final static String      VOLTAGE_DIV     = Objects.requireNonNull(resource.getResource("images/VoltageDivider.png")).toExternalForm();
    public final static  Image       VOLT_DIV_IMG    = new Image(VOLTAGE_DIV);

    public static double round(double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
