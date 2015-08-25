package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.util.ArrayList;
import java.util.List;

/**
 * User: pauls
 * Timestamp: 25/08/2015 9:52 AM
 */
public class IsopointalSetFactory {

	private SpaceGroupFactory spaceGroupFactory = new SpaceGroupFactory();

	public IsopointalSet getIsopointalSet(int spaceGroupNumber, String wyckoffSiteLetters[]) {

		SpaceGroup spaceGroup = spaceGroupFactory.getSpaceGroup(spaceGroupNumber);
		String name = "" + spaceGroupNumber;

		List<WyckoffSite> wyckoffSites = new ArrayList<>(wyckoffSiteLetters.length);
		for (String wyckoffSiteLetter : wyckoffSiteLetters) {
			wyckoffSites.add(spaceGroup.getWyckoffSite(wyckoffSiteLetter));
			name += wyckoffSiteLetter;
		}

		IsopointalSet isopointalSet = new IsopointalSet(name, spaceGroup, wyckoffSites);


		return isopointalSet;
	}
}
