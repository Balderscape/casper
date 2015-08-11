package au.com.clearboxsystems.casper.isopointal;

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
}
