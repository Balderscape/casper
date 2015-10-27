package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by pauls on 21/10/15.
 */
public class IsopointalSetResult implements Comparable<IsopointalSetResult> {
	public static SpaceGroupFactory sgf = new SpaceGroupFactory();
	public static final double ENERGY_EPS = 0.001;

	public double EAM_A;
	public double EAM_beta;
	public double energyPerAtom;
	public double volume;
	public double density;

	public double startkT;
	public double endkT;
	public int numTrials;
	public double simTime;
	public int attempts;
    public int sameCount;
	public boolean timeoutBeforeMinFound;

	public String isopointalSet;
	public int spaceGroup;
	public int highestMultInSpaceGroup;
	public double a;
	public double b;
	public double c;
	public double alpha;
	public double beta;
	public double gamma;

	public WyckoffSiteResult[] wyckoffSites;
	public WyckoffPositionResult[] wyckoffPositions;

	// 1. Create list of equal best structures.
	// 2. Sort by :
	// a. lowest number of wyckoff sites
	// b. choose space group with highest general multiplicity (not multiplicity of chosen wyckoff site)
	// c. choose structure with lowest multiplicity on chosen wyckoff site (sum if multiple sites)
	// d. choose lowest site index (i.e. a beats b)
	@Override
	public int compareTo(IsopointalSetResult o) {
		double diffEnergy = energyPerAtom - o.energyPerAtom;
		if (diffEnergy < - ENERGY_EPS)
			return -1;
		else if (diffEnergy > ENERGY_EPS)
			return 1;

		int diffSites = wyckoffSites.length - o.wyckoffSites.length;
		if (diffSites < 0)
			return -1;
		else if (diffSites > 0)
			return 1;

		int diffHighestMult = highestMultInSpaceGroup - o.highestMultInSpaceGroup;
		if (diffHighestMult > 0)
			return -1;
		else if (diffHighestMult < 0)
			return 1;

		int diffWickoffMult = sumMult() - o.sumMult();
		if (diffWickoffMult < 0)
			return -1;
		else if (diffWickoffMult > 0)
			return 1;

		int diffLowestSite = lowestSite() - o.lowestSite();
		if (diffLowestSite < 0)
			return -1;
		else if (diffLowestSite > 0)
			return 1;

		return 0;
	}

	private int sumMult() {
		int result = 0;
		for (WyckoffSiteResult site : wyckoffSites)
			result += site.multiplicity;

		return result;
	}

	private int lowestSite() {
		int result = (int)'a' - (int)'z';
		for (WyckoffSiteResult site : wyckoffSites) {
			int code = (int) (site.code.charAt(0)) - (int) 'a';
			if (code < result)
				result = code;
		}

		return result;
	}



	// 1. Create list of equal best structures.
	// 2. Sort by :
	// a. lowest number of wyckoff sites
	// b. choose space group with highest general multiplicity (not multiplicity of chosen wyckoff site)
	// c. choose structure with lowest multiplicity on chosen wyckoff site (sum if multiple sites)
	// d. choose lowest site index (i.e. a beats b)
}
