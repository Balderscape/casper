package au.com.clearboxsystems.casper.isopointal;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

	@JsonIgnore
	public double a, b, c;

	@JsonIgnore
	public double alpha, beta, gamma;

}
