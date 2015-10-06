package au.com.clearboxsystems.casper.isopointal;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class IsopointalSetRunnerTest extends TestCase {

    public void testGenerateWyckoffCombinations() throws Exception {
        IsopointalSetRunner isopointalSetRunner = new IsopointalSetRunner();

        List<String> sets = isopointalSetRunner.generateWyckoffCombinations("", "abcDE", 6);

        System.out.println(Arrays.toString(sets.toArray()));
    }


    public void testGenerateIsopointalSets() throws Exception {
        IsopointalSetRunner isopointalSetRunner = new IsopointalSetRunner();
        IsopointalSetFactory factory = new IsopointalSetFactory();

        SpaceGroup sg = factory.getSpaceGroup(127);
        List<IsopointalSet> sets = isopointalSetRunner.generateAllIsopointalSetsOfDegree(sg, 10, 0, 2);

        for (IsopointalSet set : sets) {
            System.out.println(set.name);
        }
    }

    public void testGenerateIsopointalSets2() throws Exception {
        IsopointalSetRunner isopointalSetRunner = new IsopointalSetRunner();
        List<IsopointalSet> sets = isopointalSetRunner.generateAllIsopointalSetsOfDegree(0, 1);

        for (IsopointalSet set : sets) {
            System.out.println(set.name);
        }

        System.out.println("Num Sets: " + sets.size());

    }

}