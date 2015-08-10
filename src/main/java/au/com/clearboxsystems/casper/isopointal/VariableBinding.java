package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 10/08/15.
 */
public class VariableBinding {
	public Variable value;
	public double scale = 1;
	public double offset = 0;

	public double getValue() {
		if (value != null)
			return value.val * scale + offset;
		else
			return offset;
	}
}
