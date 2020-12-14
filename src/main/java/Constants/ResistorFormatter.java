package Constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ResistorFormatter {
	public static final Map<Integer,String> prefixes;
	static {
		Map<Integer,String> tempPrefixes = new HashMap<Integer,String>();
		tempPrefixes.put(0,"");
		tempPrefixes.put(3,"k");
		tempPrefixes.put(6,"M");
		tempPrefixes.put(9,"G");
		prefixes = Collections.unmodifiableMap(tempPrefixes);
	}

	String type;
	double value;

	public ResistorFormatter(double value, String type) {
		this.value = value;
		this.type = type;
	}

	public String toString() {
		double tval  = value;
		int    order = 0;
		if (tval > (1000000000)) {
			return "TOO HIGH";
		}
		else if (tval < 1000) {
			return C.round(tval,0) + type;
		}
		while (tval > 1000.0) {
			tval /= 1000.0;
			order += 3;
		}
		return C.round(tval, 2) + prefixes.get(order) + type;
	}
}
