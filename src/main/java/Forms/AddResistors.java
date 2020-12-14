package Forms;

import Constants.C;
import Constants.ResistorFormatter;
import classHelpers.ComboCalculations;
import com.simtechdata.classHelpers.Switcher;
import com.simtechdata.controls.*;
import com.simtechdata.datatypes.CStringBuilder;
import javafx.application.Platform;
import javafx.geometry.Pos;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddResistors {

	public AddResistors() {
		this.ap = ap();
		Switcher.addScene(ap, C.ADD_RESISTORS, width, height);
		makeControls();
		makeMenu();
	}

	private final double                  width       = C.coreWidth;
	private final double                  height      = C.coreHeight;
	private final List<Double>            userValues  = new ArrayList<>();
	private final List<String>            finalValues = new ArrayList<>();
	private final List<String>            csvLog 	  = new ArrayList<>();
	private final List<ComboCalculations> comboValues = new ArrayList<>();
	private final CAnchorPane             ap;

	private CTextArea  taValues;
	private CTextArea  taResults;
	private CTextField tfValue;
	private CTextField tfVIN, tfVOUT, tfTolerance;
	private CCheckBox cbxEnterList;

	private CAnchorPane ap() {
		return new CAnchorPane(this.width, this.height).styleSheet(C.CSS_ANCHOR_PANE).styleClass("anchor-pane").build();
	}

	private void makeControls() {
		new CLabel(ap, "Your Values").leftTop(15, 10).styleSheet(C.CSS_LABEL).alignment(Pos.CENTER_LEFT).build();
		new CLabel(ap, "Results").leftTop(130, 10).styleSheet(C.CSS_LABEL).alignment(Pos.CENTER_LEFT).build();
		new CLabel(ap, "Paste in full list").leftTop(40, 480).styleSheet(C.CSS_LABEL).alignment(Pos.CENTER_LEFT).build();
		taValues  = new CTextArea(ap).leftTop(10, 35).width(100).height(500).styleSheet(C.CSS_TEXT_AREA).editable(false).build();
		taResults = new CTextArea(ap).leftTop(125, 35).width(575).height(500).styleSheet(C.CSS_TEXT_AREA).editable(false).build();
		taValues.setDisable(true);
		taResults.setDisable(true);
		cbxEnterList = new CCheckBox(ap).leftTop(15,480).styleSheet(C.CSS_CHECKBOX).build();
		new CLabel(ap, "Enter value then enter")	.leftTop(15 , 510).styleSheet(C.CSS_LABEL).alignment(Pos.CENTER_LEFT).build();
		new CLabel(ap, "Input Voltage")			.leftTop(180, 510).styleSheet(C.CSS_LABEL).alignment(Pos.CENTER_LEFT).build();
		new CLabel(ap, "Desired Output Voltage")	.leftTop(305, 510).styleSheet(C.CSS_LABEL).alignment(Pos.CENTER_LEFT).build();
		new CLabel(ap, "Output Tolerance")			.leftTop(480, 510).styleSheet(C.CSS_LABEL).alignment(Pos.CENTER_LEFT).build();
		tfValue     = new CTextField(ap).leftTop(10,  525).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_TEXT_FIELD).build();
		tfVIN       = new CTextField(ap).leftTop(175, 525).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_TEXT_FIELD).build();
		tfVOUT      = new CTextField(ap).leftTop(300, 525).alignment(Pos.CENTER_LEFT).styleSheet(C.CSS_TEXT_FIELD).build();
		tfTolerance = new CTextField(ap).leftTop(475, 525).alignment(Pos.CENTER_LEFT).toolTip(
				"Set the offset that you can tolerate for the output voltage.\nFor example if you want 5V out, but will tolerate 6 or 4,\nthen put 1 for this value.").styleSheet(
				C.CSS_TEXT_FIELD).build();
		setControlActions();
	}

	private void setControlActions() {
		tfValue.setOnAction(e -> {
			String sValue = processValue(tfValue.getText());
			double newValue = Double.parseDouble(sValue);
			userValues.add(newValue);
			Platform.runLater(() -> {
				taValues.appendText(new ResistorFormatter(newValue, "Ω").toString() + "\n");
				tfValue.clear();
				tfValue.requestFocus();
			});
		});
		tfValue.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*") || !newValue.matches("\\.") || !newValue.matches("k") || !newValue.matches("K") || !newValue.matches("m") || !newValue.matches("M")) {
				tfValue.setText(newValue.replaceAll("[^\\d.mMkK]", ""));
			}
		});
		tfVIN.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*") || !newValue.matches("\\.")) {
				tfVIN.setText(newValue.replaceAll("[^\\d.]", ""));
			}
		});
		tfVOUT.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*") || !newValue.matches("\\.")) {
				tfVOUT.setText(newValue.replaceAll("[^\\d.]", ""));
			}
		});
		tfTolerance.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*") || !newValue.matches("\\.")) {
				tfTolerance.setText(newValue.replaceAll("[^\\d.]", ""));
			}
		});
		cbxEnterList.selectedProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				taValues.clear();
				taValues.setDisable(false);
				taValues.setEditable(true);
				taValues.requestFocus();
				userValues.clear();
			}
			else {
				taValues.setDisable(true);
				taValues.setEditable(false);
				tfValue.requestFocus();
			}
		});
	}

	private void makeMenu() {
		CButton exitButton   = new CButton("Exit").styleSheet(C.CSS_BUTTON).width(65).build();
		CButton btnCopyLog   = new CButton("Copy Log").styleSheet(C.CSS_BUTTON).width(65).build();
		CButton btnCalculate = new CButton("Find Combos").styleSheet(C.CSS_BUTTON).width(65).build();
		CButton btnBack      = new CButton("Back").styleSheet(C.CSS_BUTTON).width(65).build();
		btnCopyLog.setOnAction(e->{
			String finalCSV = "";
			for (String log:csvLog) {
				finalCSV += log + "\n";
			}
			StringSelection stringSelection = new StringSelection(finalCSV);
			Clipboard       clipboard       = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		});
		btnCalculate.setOnAction(e -> {
			findCombinations();
		});
		btnBack.setOnAction(e -> {
			Switcher.showLastScene();
		});
		exitButton.setOnAction(e -> {
			System.exit(0);
		});
		CMenu menu = Switcher.getMenu(C.ADD_RESISTORS).withButton(btnCalculate).withButton(btnCopyLog).withButton(btnBack).withExitButton(exitButton).build();
		menu.finishMenu();
	}

	private String processValue(String sValue) {
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

	private String toColumn(int length, String item, int col) {
		CStringBuilder response = new CStringBuilder();
		final int      c2       = 15;
		final int      c3       = 30;
		final int      c4       = 45;
		final int      c5       = 60;
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

	private void findCombinations() {
		double vIN = 0;
		double vOUT = 0;
		double vTolerance = 0;
		if (tfVIN.getText().length() > 0 && tfVOUT.getText().length() > 0 && tfTolerance.getText().length() > 0) {
			vIN        = Double.parseDouble(tfVIN.getText());
			vOUT       = Double.parseDouble(tfVOUT.getText());
			vTolerance = Double.parseDouble(tfTolerance.getText());
		}
		if (vIN > 0 && vOUT > 0 && vTolerance > 0) {
			String  regEx = "(\\d+\\.\\d+)k|(\\d+\\.\\d+)m|(\\d+\\.\\d+)M|(\\d+\\.\\d+)K|(\\d+)";
			Pattern p = Pattern.compile(regEx);
			if (userValues.size() == 0 && taValues.getText().length() > 5) {
				finalValues.clear();
				String[] sValues = taValues.getText().split("\n");
				for (String value : sValues) {
					Matcher m = p.matcher(value);
					if (m.matches()) {
						if (m.group(0).length() == value.length()) {
							String sValue = processValue(value);
							double newValue = Double.parseDouble(sValue);
							userValues.add(newValue);
							finalValues.add(new ResistorFormatter(newValue,"Ω").toString());
						}
					}
				}
				taValues.clear();
				taValues.setDisable(true);
				taValues.setEditable(false);
				for (String resistor : finalValues) {
					taValues.appendText(resistor + "\n");
				}
			}
			for (double first : userValues) {
				for (double second : userValues) {
					comboValues.add(new ComboCalculations(vIN, first, second));
				}
			}
			taResults.clear();
			List<String> tempResults = new ArrayList<>();
			csvLog.clear();
			csvLog.add("VIN,R1,R2,VR2");
			for (ComboCalculations combo : comboValues) {
				double vO      = combo.getVOUT();
				double tValue1 = vOUT + vTolerance;
				double tValue2 = vOUT - vTolerance;
				if ((vO <= tValue1) && (vO >= vOUT) || (vO >= tValue2) && (vO <= vOUT)) {
					final CStringBuilder csb  = new CStringBuilder();
					double dVIN = combo.getvIN();
					double dR1 = combo.getR1();
					double dR2 = combo.getR2();
					double dVOUT = combo.getVOUT();
					csvLog.add(dVIN + "," + dR1 + "," + dR2 + "," + dVOUT);
					String               vin  = "VIN = " + C.round(dVIN, 3) + "V";
					String               r1   = "R1 = " + new ResistorFormatter(dR1, "Ω");
					String               r2   = "R2 = " + new ResistorFormatter(dR2, "Ω");
					String               vout = "VR2 = " + C.round(dVOUT, 3) + "V";
					csb.a(vin);
					csb.a(toColumn(csb.getLength(), r1, 2));
					csb.a(toColumn(csb.getLength(), r2, 3));
					csb.a(toColumn(csb.getLength(), vout, 4));
					if (!tempResults.contains(csb.toString())) tempResults.add(csb.toString());
				}
			}
			for (final String result : tempResults) {
				Platform.runLater(() -> {
					taResults.appendText(result + "\n");
				});
			}
		}
	}

	public void showForm() {
		Switcher.showScene(C.ADD_RESISTORS);
		tfValue.requestFocus();
	}
}
