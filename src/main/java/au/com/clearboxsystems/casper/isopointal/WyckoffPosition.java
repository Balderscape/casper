package au.com.clearboxsystems.casper.isopointal;

import au.com.clearboxsystems.casper.math.Vector3;

/**
 * Created by pauls on 11/08/15.
 */
public class WyckoffPosition {
	public WyckoffTransform xTransform = new WyckoffTransform();
	public WyckoffTransform yTransform = new WyckoffTransform();
	public WyckoffTransform zTransform = new WyckoffTransform();

	public WyckoffPosition() {
	}

	public void set(WyckoffPosition p) {
		xTransform.set(p.xTransform);
		yTransform.set(p.yTransform);
		zTransform.set(p.zTransform);
	}

	public Vector3 getPosition(Vector3 posVariable) {
		Vector3 result = new Vector3();

		result.x = xTransform.apply(posVariable);
		result.y = yTransform.apply(posVariable);
		result.z = zTransform.apply(posVariable);

		return result;
	}
}
