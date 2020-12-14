package Forms;

import Constants.C;
import Constants.ResistorFormatter;
import com.simtechdata.classHelpers.Switcher;
import com.simtechdata.controls.*;
import com.simtechdata.datatypes.CStringBuilder;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainForm {

    public MainForm() {
        Switcher.addScene(ap, C.MAIN_FORM, width, height);
        makeControls();
        makeMenu();
    }

    private final double               width                  = C.coreWidth;
    private final double               height                 = C.coreHeight;
    private final CAnchorPane          ap                     = ap();
    private final int                  LOCR1                  = 0;
    private final int                  LOCR2                  = 1;
    private final int                  LOCVIN                 = 2;
    private final int                  LOCVOUT                = 3;
    private final int                  SET                    = 4;
    private final int                  CALC                   = 5;
    private final int                  CLEAR                  = 6;
    private final int                  R1                     = 10;
    private final int                  R2                     = 11;
    private final int                  VIN                    = 12;
    private final int                  VOUT                   = 13;
    private final Map<Integer, Double> userValues             = new HashMap<>();
    private       int                  lastClick              = -1;
    private       double               currentX;
    private       double               currentY;
    private       Integer              optionSelected;
    private final List<String>         valuesCSV              = new ArrayList();
    private       boolean              comboActive            = false;
    private       CVBox                vbOptions;
    private       CTextField           tfEntry;
    private       CTextArea            taResults;
    private       CLabel               lblR1, lblR2, lblVIN, lblVOUT, lblError;

    private CAnchorPane ap() {
        return new CAnchorPane(this.width, this.height).styleSheet(C.CSS_ANCHOR_PANE).styleClass("anchor-pane").backImage(C.VOLT_DIV_IMG).backOpacity(1.0).build();
    }

    private void makeControls() {
        lblR1     = new CLabel(ap, "").leftTop(340, 300).width(200).height(35).visible(true).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_LABEL).styleClass("voltage-label").build();
        lblR2     = new CLabel(ap, "").leftTop(335, 465).width(200).height(35).visible(true).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_LABEL).styleClass("voltage-label").build();
        lblVIN    = new CLabel(ap, "").leftTop(5, 410).width(200).height(35).visible(true).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_LABEL).styleClass("voltage-label").build();
        lblVOUT   = new CLabel(ap, "").leftTop(580, 480).width(200).height(35).visible(true).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_LABEL).styleClass("voltage-label").build();
        lblError  = new CLabel(ap, "").leftTop(555, 410).width(250).height(35).visible(true).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_LABEL).styleClass("error-label").build();
        tfEntry   = new CTextField(ap).styleSheet(C.CSS_TEXT_FIELD).width(100).height(35).alignment(Pos.CENTER_LEFT).visible(false).build();
        taResults = new CTextArea(ap).styleSheet(C.CSS_TEXT_AREA).leftTop(10, 10).width(680).height(200).editable(false).build();
        taResults.setDisable(true);
        CButton btnSet       = new CButton("Set").styleSheet(C.CSS_BUTTON).width(75).height(25).build();
        CButton btnCalculate = new CButton("Calculate").styleSheet(C.CSS_BUTTON).width(75).height(25).build();
        CButton btnClear     = new CButton("Clear").styleSheet(C.CSS_BUTTON).width(75).height(25).build();
        btnSet.setOnAction(e -> {
            new Thread(() -> {
                optionSelected = SET;
                try {TimeUnit.MILLISECONDS.sleep(500);}catch (InterruptedException ignored) {}
                vbOptions.hide();
            }).start();
        });
        btnCalculate.setOnAction(e -> {
            new Thread(() -> {
                optionSelected = CALC;
                try {TimeUnit.MILLISECONDS.sleep(500);}catch (InterruptedException ignored) {}
                vbOptions.hide();
            }).start();
        });
        btnClear.setOnAction(e -> {
            new Thread(() -> {
                optionSelected = CLEAR;
                try {TimeUnit.MILLISECONDS.sleep(500);}catch (InterruptedException ignored) {}
                vbOptions.hide();
            }).start();
        });
        vbOptions = new CVBox(ap).withButton(btnSet).withButton(btnCalculate).withButton(btnClear).leftTop(0, 0).visible(false).padding(10, 10, 10, 10).alignment(Pos.CENTER).build();
        setControlActions();
    }

    private String extractNumber(String sValue) {
        if (sValue.contains("k") || sValue.contains("K")) {
            sValue = sValue.replaceAll("[^\\d.]", "");
            sValue = String.valueOf(Double.parseDouble(sValue) * 1000);
        }
        else if (sValue.contains("m") || sValue.contains("M")) {
            sValue = sValue.replaceAll("[^\\d.]", "");
            sValue = String.valueOf(Double.parseDouble(sValue) * 1000000);
        }
        return sValue;
    }

    private void setControlActions() {
        ap.setOnMouseClicked(event -> {
            currentX = event.getSceneX();
            currentY = event.getSceneY();
            //System.out.println(currentX + "," + currentY);
            optionSelected = -1;
            if (currentX > 250 && currentX < 375 && currentY > 290 && currentY < 400) {
                lastClick = LOCR1;
            }
            else if (currentX > 250 && currentX < 375 && currentY > 400 && currentY < 575) {
                lastClick = LOCR2;
            }
            else if (currentX > 10 && currentX < 200 && currentY > 350 && currentY < 490) {
                lastClick = LOCVIN;
            }
            else if (currentX > 470 && currentX < 620 && currentY > 440 && currentY < 590) {
                lastClick = LOCVOUT;
            }
            else {
                lastClick = -1;
                vbOptions.hide();
            }
            if (lastClick != -1) vbOptions.showAt(currentX, currentY);
        });
        tfEntry.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB)) {
                String       value    = extractNumber(tfEntry.getText());
                double       newValue = Double.parseDouble(value);
                final String result   = formatNumber(newValue);
                switch (tfEntry.getPurpose()) {
                    case R1:
                        if (result.equals("TOO HIGH")) {
                            userValues.remove(R1);
                        }
                        else {
                            if (userValues.containsKey(R1)) userValues.replace(R1, newValue);
                            else userValues.put(R1, newValue);
                        }
                        Platform.runLater(() -> {
                            lblR1.setText(result);
                        });
                        break;

                    case R2:
                        if (result.equals("TOO HIGH")) {
                            userValues.remove(R2);
                        }
                        else {
                            if (userValues.containsKey(R2)) userValues.replace(R2, newValue);
                            else userValues.put(R2, newValue);
                        }
                        Platform.runLater(() -> {
                            lblR2.setText(result);
                        });
                        break;

                    case VIN:
                        if (userValues.containsKey(VIN)) userValues.replace(VIN, newValue);
                        else userValues.put(VIN, newValue);
                        Platform.runLater(() -> {
                            lblVIN.setText(C.round(newValue, 3) + "V");
                        });
                        break;

                    case VOUT:
                        if (userValues.containsKey(VOUT)) userValues.replace(VOUT, newValue);
                        else userValues.put(VOUT, newValue);
                        Platform.runLater(() -> {
                            lblVOUT.setText(C.round(newValue, 3) + "V");
                        });
                        break;
                }
                Platform.runLater(() -> {
                    tfEntry.setText("");
                    tfEntry.hide();
                });
            }
        });
        tfEntry.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || !newValue.matches("\\.") || !newValue.matches("k") || !newValue.matches("K") || !newValue.matches("m") || !newValue.matches("M")) {
                tfEntry.setText(newValue.replaceAll("[^\\d.mMkK]", ""));
            }
        });
        valuesCSV.add("R1,R2,VIN,VR1,VR2");
    }

    private void makeMenu() {
        CButton exitButton = new CButton("Exit").styleSheet(C.CSS_BUTTON).width(65).build();
        CButton btnReset   = new CButton("Reset").styleSheet(C.CSS_BUTTON).width(65).build();
        CButton btnCopyLog = new CButton("Copy Log").styleSheet(C.CSS_BUTTON).width(65).build();
        CButton btnCombos  = new CButton("Combo Finder").styleSheet(C.CSS_BUTTON).width(65).build();
        btnReset.setOnAction(e -> {
            for (int x = R1; x <= VOUT; x++) clearValue(x);
        });
        exitButton.setOnAction(e -> closeApp());
        btnCopyLog.setOnAction(e -> {
            String finalCSV = "";
            for (String log : valuesCSV) {
                finalCSV += log + "\n";
            }
            StringSelection stringSelection = new StringSelection(finalCSV);
            Clipboard       clipboard       = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
        });
        btnCombos.setOnAction(e -> {
            if (comboActive) Switcher.showLastScene();
            else {
                comboActive = true;
                new AddResistors().showForm();
            }
        });
        CMenu menu = Switcher.getMenu(C.MAIN_FORM).withButton(btnReset).withButton(btnCopyLog).withButton(btnCombos).withExitButton(exitButton).build();
        menu.finishMenu();
    }

    private void logResults() {
        boolean vinSet  = userValues.containsKey(VIN);
        boolean r1Set   = userValues.containsKey(R1);
        boolean r2Set   = userValues.containsKey(R2);
        boolean voutSet = userValues.containsKey(VOUT);
        if (vinSet && r1Set && r2Set && voutSet) {
            final CStringBuilder csb = new CStringBuilder();
            final CStringBuilder log = new CStringBuilder();
            double               vr1 = userValues.get(VIN) - userValues.get(VOUT);
            double               LR1 = userValues.get(R1);
            double               LR2 = userValues.get(R2);
            String               sR1 = String.valueOf(LR1);
            String               sR2 = String.valueOf(LR2);
            String               vIN = String.valueOf(userValues.get(VIN));
            String               vR1 = String.valueOf(vr1);
            String               vR2 = String.valueOf(userValues.get(VOUT));
            log.a(sR1).cm().a(sR2).cm().a(vIN).cm().a(vR1).cm().a(vR2);
            valuesCSV.add(log.toString());
            csb.a("R1 = ").a(formatNumber(LR1));
            csb.a(toColumn(csb.getLength(), "R2 = " + formatNumber(LR2), 2));
            csb.a(toColumn(csb.getLength(), "VIN = " + C.round(userValues.get(VIN), 3), 3));
            csb.a(toColumn(csb.getLength(), "VR1 = " + C.round(vr1, 3), 4));
            csb.a(toColumn(csb.getLength(), "VR2 = " + C.round(userValues.get(VOUT), 3), 5)).cr();
            Platform.runLater(() -> {
                taResults.appendText(csb.toString());
            });
        }
    }

    private String toColumn(int length, String item, int col) {
        CStringBuilder response = new CStringBuilder();
        final int      c2       = 20;
        final int      c3       = 40;
        final int      c4       = 60;
        final int      c5       = 80;
        int            spaces;
        switch (col) {
            case 2:
                spaces = c2 - length;
                for (int x = 0; x < spaces; x++) {
                    response.sp();
                }
                break;

            case 3:
                spaces = c3 - length;
                for (int x = 0; x < spaces; x++) {
                    response.sp();
                }
                break;

            case 4:
                spaces = c4 - length;
                for (int x = 0; x < spaces; x++) {
                    response.sp();
                }
                break;

            case 5:
                spaces = c5 - length;
                for (int x = 0; x < spaces; x++) {
                    response.sp();
                }
                break;
        }
        response.a(item);
        return response.toString();
    }

    private void clearValue(Integer label) {
        switch (label) {
            case R1:
                Platform.runLater(() -> {
                    lblR1.setText("");
                });
                userValues.remove(R1);
                break;

            case R2:
                Platform.runLater(() -> {
                    lblR2.setText("");
                });
                userValues.remove(R2);
                break;

            case VIN:
                Platform.runLater(() -> {
                    lblVIN.setText("");
                });
                userValues.remove(VIN);
                break;

            case VOUT:
                Platform.runLater(() -> {
                    lblVOUT.setText("");
                });
                userValues.remove(VOUT);
                break;

            default:
                break;
        }
    }

    private void fadeError(String msg) {
        new Thread(() -> {
            Platform.runLater(() -> {
                lblError.setText(msg);
                lblError.setOpacity(1.0);
            });
            try {TimeUnit.SECONDS.sleep(4);}catch (InterruptedException ignored) {}
            for (int x = 100; x >= 0; x--) {
                final double o = (double) x / 100;
                Platform.runLater(() -> {
                    lblError.setOpacity(o);
                });
                try {TimeUnit.MILLISECONDS.sleep(19);}catch (InterruptedException ignored) {}
            }
        }).start();
    }

    private String formatNumber(double value) {
        ResistorFormatter newValue = new ResistorFormatter(value, "Ω");
        return newValue.toString();
    }

    private void actOnR1(int option) {
        if (option == SET) {
            Platform.runLater(() -> {
                clearValue(R1);
                tfEntry.setPurpose(R1);
                tfEntry.showAt(350, 305);
                tfEntry.requestFocus();
            });
        }
        if (option == CALC) {
            calcR1();
        }
        if (option == CLEAR) {
            userValues.remove(R1);
            clearValue(R1);
        }
    }

    private void actOnR2(int option) {
        if (option == SET) {
            Platform.runLater(() -> {
                clearValue(R2);
                tfEntry.setPurpose(R2);
                tfEntry.showAt(340, 465);
                tfEntry.requestFocus();
            });
        }
        if (option == CALC) {
            calcR2();
        }
        if (option == CLEAR) {
            userValues.remove(R2);
            clearValue(R2);
        }
    }

    private void actOnVin(int option) {
        if (option == SET) {
            Platform.runLater(() -> {
                clearValue(VIN);
                tfEntry.setPurpose(VIN);
                tfEntry.showAt(5, 410);
                tfEntry.requestFocus();
            });
        }
        if (option == CALC) {
            calcVIN();
        }
        if (option == CLEAR) {
            userValues.remove(VIN);
            clearValue(VIN);
        }
    }

    private void actOnVout(int option) {
        if (option == SET) {
            Platform.runLater(() -> {
                clearValue(VOUT);
                tfEntry.setPurpose(VOUT);
                tfEntry.showAt(570, 480);
                tfEntry.requestFocus();
            });
        }
        if (option == CALC) {
            calcVOut();
        }
        if (option == CLEAR) {
            userValues.remove(VOUT);
            clearValue(VOUT);
        }
    }

    private void calcVOut() {
        boolean vinSet = userValues.containsKey(VIN);
        boolean r1Set  = userValues.containsKey(R1);
        boolean r2Set  = userValues.containsKey(R2);
        if (vinSet && r1Set && r2Set) {
            double vin  = userValues.get(VIN);
            double r2   = userValues.get(R2);
            double r1   = userValues.get(R1);
            double vout = vin * (r2 / (r1 + r2));
            if (userValues.containsKey(VOUT)) userValues.replace(VOUT, vout);
            else userValues.put(VOUT, vout);
            Platform.runLater(() -> {
                lblVOUT.setText(String.valueOf(C.round(vout, 2)) + "V");
            });
            logResults();
        }
        else {
            CStringBuilder csb   = new CStringBuilder();
            int            total = 0;
            total += vinSet ? 0 : 1;
            total += r1Set ? 0 : 1;
            total += r2Set ? 0 : 1;
            String adj = (total > 1) ? " Are " : " is ";
            csb.a(vinSet ? "" : " VIN ");
            csb.a(r1Set ? "" : " R1 ");
            csb.a(r2Set ? "" : " R2 ");
            csb.a(adj);
            csb.a("Missing! ");
            fadeError(csb.toString());
        }
    }

    private void calcR1() {
        boolean vinSet  = userValues.containsKey(VIN);
        boolean voutSet = userValues.containsKey(VOUT);
        boolean r2Set   = userValues.containsKey(R2);
        if (vinSet && voutSet && r2Set) {
            double vin  = userValues.get(VIN);
            double vout = userValues.get(VOUT);
            double r2   = userValues.get(R2);
            double r1   = (r2 * (vin - vout)) / (vout);
            if (userValues.containsKey(R1)) userValues.replace(R1, r1);
            else userValues.put(R1, r1);
            Platform.runLater(() -> {
                lblR1.setText(formatNumber((long) r1));
            });
            logResults();
        }
        else {
            CStringBuilder csb   = new CStringBuilder();
            int            total = 0;
            total += vinSet ? 0 : 1;
            total += voutSet ? 0 : 1;
            total += r2Set ? 0 : 1;
            String adj = (total > 1) ? " Are " : " is ";
            csb.a(vinSet ? "" : " VIN ");
            csb.a(voutSet ? "" : " VOUT ");
            csb.a(r2Set ? "" : " R2 ");
            csb.a(adj);
            csb.a("Missing!");
            fadeError(csb.toString());
        }
    }

    private void calcR2() {
        boolean vinSet  = userValues.containsKey(VIN);
        boolean r1Set   = userValues.containsKey(R1);
        boolean voutSet = userValues.containsKey(VOUT);
        if (vinSet && r1Set && voutSet) {
            double vin  = userValues.get(VIN);
            double vout = userValues.get(VOUT);
            double r1   = userValues.get(R1);
            double r2   = (r1 * vout) / (vin - vout);
            if (userValues.containsKey(R2)) userValues.replace(R2, r2);
            else userValues.put(R2, r2);
            Platform.runLater(() -> {
                lblR2.setText(formatNumber((long) r2));
            });
            logResults();
        }
        else {
            CStringBuilder csb   = new CStringBuilder();
            int            total = 0;
            total += vinSet ? 0 : 1;
            total += voutSet ? 0 : 1;
            total += r1Set ? 0 : 1;
            String adj = (total > 1) ? " Are " : " is ";
            csb.a(vinSet ? "" : " VIN ");
            csb.a(voutSet ? "" : " VOUT ");
            csb.a(r1Set ? "" : " R1 ");
            csb.a(adj);
            csb.a("Missing!");
            fadeError(csb.toString());
        }
    }

    private void calcVIN() {
        boolean r2Set   = userValues.containsKey(R2);
        boolean r1Set   = userValues.containsKey(R1);
        boolean voutSet = userValues.containsKey(VOUT);
        if (r2Set && r1Set && voutSet) {
            double r2   = userValues.get(R2);
            double r1   = userValues.get(R1);
            double vout = userValues.get(VOUT);
            double vin  = (vout * (r1 + r2)) / r2;
            if (userValues.containsKey(VIN)) userValues.replace(VIN, vin);
            else userValues.put(VIN, vin);
            Platform.runLater(() -> {
                lblVIN.setText(String.valueOf(C.round(vin, 2)) + "V");
            });
            logResults();
        }
        else {
            CStringBuilder csb   = new CStringBuilder();
            int            total = 0;
            total += voutSet ? 0 : 1;
            total += r2Set ? 0 : 1;
            total += r1Set ? 0 : 1;
            String adj = (total > 1) ? " Are " : " is ";
            csb.a(voutSet ? "" : " VOUT ");
            csb.a(r1Set ? "" : " R1 ");
            csb.a(r2Set ? "" : " R2 ");
            csb.a(adj);
            csb.a("Missing!");
            fadeError(csb.toString());
        }
    }

    private Runnable monitorMouseClicks() {
        return () -> {
            boolean keepRunning = true;
            while (keepRunning) {
                switch (lastClick) {
                    case LOCR1:
                        while (lastClick == LOCR1 && vbOptions.isVisible()) {
                            if (optionSelected.equals(SET)) {
                                actOnR1(SET);
                            }
                            if (optionSelected.equals(CALC)) {
                                actOnR1(CALC);
                                lastClick = -1;
                            }
                            if (optionSelected.equals(CLEAR)) {
                                actOnR1(CLEAR);
                            }
                            try {TimeUnit.MILLISECONDS.sleep(100);}catch (InterruptedException ignored) {}
                        }
                        break;
                    case LOCR2:
                        while (lastClick == LOCR2 && vbOptions.isVisible()) {
                            if (optionSelected.equals(SET)) {
                                actOnR2(SET);
                            }
                            if (optionSelected.equals(CALC)) {
                                actOnR2(CALC);
                                lastClick = -1;
                            }
                            if (optionSelected.equals(CLEAR)) {
                                actOnR2(CLEAR);
                            }
                            try {TimeUnit.MILLISECONDS.sleep(100);}catch (InterruptedException ignored) {}
                        }
                        break;
                    case LOCVIN:
                        while (lastClick == LOCVIN && vbOptions.isVisible()) {
                            if (optionSelected.equals(SET)) {
                                actOnVin(SET);
                            }
                            if (optionSelected.equals(CALC)) {
                                actOnVin(CALC);
                                lastClick = -1;
                            }
                            if (optionSelected.equals(CLEAR)) {
                                actOnVin(CLEAR);
                            }
                            try {TimeUnit.MILLISECONDS.sleep(100);}catch (InterruptedException ignored) {}
                        }
                        break;
                    case LOCVOUT:
                        while (lastClick == LOCVOUT && vbOptions.isVisible()) {
                            if (optionSelected.equals(SET)) {
                                actOnVout(SET);
                            }
                            if (optionSelected.equals(CALC)) {
                                actOnVout(CALC);
                                lastClick = -1;
                            }
                            if (optionSelected.equals(CLEAR)) {
                                actOnVout(CLEAR);
                            }
                            try {TimeUnit.MILLISECONDS.sleep(100);}catch (InterruptedException ignored) {}
                        }
                        break;
                    default:
                        try {TimeUnit.MILLISECONDS.sleep(100);}catch (InterruptedException ignored) {}
                        keepRunning = true;
                        break;
                }
            }
        };
    }

    private void closeApp() {
        System.exit(0);
    }

    public void start() {
        Switcher.showScene(C.MAIN_FORM);
        new Thread(monitorMouseClicks()).start();
    }

    public void showForm() {
        Switcher.showScene(C.MAIN_FORM);
    }
}
