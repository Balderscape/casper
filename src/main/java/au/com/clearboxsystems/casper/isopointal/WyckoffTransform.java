package au.com.clearboxsystems.casper.isopointal;

import au.com.clearboxsystems.casper.math.Vector3;

/**
 * Created by pauls on 11/08/15.
 */
public class WyckoffTransform {
	public double xScale;
	public double yScale;
	public double zScale;
	public double offset;

	public void set(WyckoffTransform t) {
		xScale = t.xScale;
		yScale = t.yScale;
		zScale = t.zScale;
		offset = t.offset;
	}

	public double apply(Vector3 v) {
		return offset + xScale * v.x + yScale * v.y + zScale * v.z;
	}
}
