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

	public IsopointalSet getIsopointalSet(int spaceGroupNumber, String wyckoffSiteLetters) {

		SpaceGroup spaceGroup = spaceGroupFactory.getSpaceGroup(spaceGroupNumber);
		String name = "" + spaceGroupNumber + wyckoffSiteLetters;

		List<WyckoffSite> wyckoffSites = new ArrayList<>(wyckoffSiteLetters.length());
		for (int i = 0; i < wyckoffSiteLetters.length(); i++) {
			wyckoffSites.add(spaceGroup.getWyckoffSite("" + wyckoffSiteLetters.charAt(i)));
		}

		return new IsopointalSet(name, spaceGroup, wyckoffSites);
	}


    public IsopointalSet getIsopointalSet(SpaceGroup spaceGroup, int wyckoffSiteIdxs[]) {
        int spaceGroupNumber = spaceGroup.number;

        // Get new spaceGroup
        spaceGroup = spaceGroupFactory.getSpaceGroup(spaceGroupNumber);
        String name = "" + spaceGroupNumber;

        List<WyckoffSite> wyckoffSites = new ArrayList<>(wyckoffSiteIdxs.length);
        for (int wyckoffSiteIdx : wyckoffSiteIdxs) {
            WyckoffSite wyckoffSite = spaceGroup.getWyckoffSite(wyckoffSiteIdx);
            wyckoffSites.add(wyckoffSite);
            name += wyckoffSite.code;
        }

        IsopointalSet isopointalSet = new IsopointalSet(name, spaceGroup, wyckoffSites);

        return isopointalSet;
    }


    public SpaceGroup getSpaceGroup(int spaceGroup) {
        return spaceGroupFactory.getSpaceGroup(spaceGroup);
    }
}
