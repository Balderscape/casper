package au.com.clearboxsystems.casper.isopointal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pauls on 11/08/15.
 */
public class SpaceGroup {
	public int number;
	public CrystalSystem crystalSystem;
	public String additionalPositionsShortForm;
	public List<WyckoffSite> wyckoffSites = new ArrayList<>();

}
