package au.com.clearboxsystems.casper.isopointal;

import au.com.clearboxsystems.casper.math.Vector3;

/**
 * Created by pauls on 10/08/15.
 */
public class Vector3Binding {
	public Vector3 boundVector;
	public VariableBinding xBinding = new VariableBinding();
	public VariableBinding yBinding = new VariableBinding();
	public VariableBinding zBinding = new VariableBinding();

	public Vector3Binding(Vector3 boundVector) {
		this.boundVector = boundVector;
	}

	public void apply() {
		boundVector.x = xBinding.getValue();
		boundVector.y = yBinding.getValue();
		boundVector.z = zBinding.getValue();
	}
}
