package au.com.clearboxsystems.casper.isopointal;

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
}
