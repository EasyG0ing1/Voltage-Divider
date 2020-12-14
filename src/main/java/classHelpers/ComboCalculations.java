package classHelpers;

public class ComboCalculations {

	public ComboCalculations(double vIN, double R1, double R2) {
		this.vIN = vIN;
		this.R1 = R1;
		this.R2 = R2;
		findVOUT();
	}

	private final double vIN;
	private double vOUT;
	private final double R1;
	private final double R2;

	private void findVOUT() {
		vOUT = vIN * (R2 / (R1 + R2));
	}

	public double getVOUT() {return vOUT;}

	public double getvIN() {
		return vIN;
	}

	public double getR1() {
		return R1;
	}

	public double getR2() {
		return R2;
	}
}
