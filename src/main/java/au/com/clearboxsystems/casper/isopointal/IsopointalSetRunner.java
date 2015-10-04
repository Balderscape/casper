package au.com.clearboxsystems.casper.isopointal;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pauls on 4/10/15.
 */
public class IsopointalSetRunner {

    IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

    public static void main(String[] args) {
        IsopointalSetRunner runner = new IsopointalSetRunner();
        runner.computeMinEnergies(0.5, 4, 1, 3);
    }



    public void computeMinEnergies(double A, double beta, int minDegree, int maxDegree) {
        List<IsopointalSet> isopointalSets = generateAllIsopointalSets(minDegree, maxDegree);
        List<EnergyResult> energyResults = new ArrayList<>();

        int numSets = isopointalSets.size();
        System.out.println("Computing energies for " + numSets + " isopointal sets");
        AtomicInteger done = new AtomicInteger();
        AtomicInteger bestResult = new AtomicInteger();

        isopointalSets.parallelStream().forEach((set) -> {
            SimulatedAnneal sa = new SimulatedAnneal();

            int numRuns = 8;
            int numTrials = 10000;
            int sameCount = 0;
            double minEnergy;
            boolean first = true;
            do {
                if (first)
                    first = false;
                else
                    numTrials *= 2;

                double[] energies = new double[numRuns];
                for (int i = 0; i < numRuns; i++)
                    energies[i] = sa.runSimulatedAnneal(numTrials, 2, 0.01, set, A, beta);

                minEnergy = energies[0];
                for (int i = 1; i < numRuns; i++)
                    if (energies[i] < minEnergy)
                        minEnergy = energies[i];

                sameCount = 0;
                for (int i = 0; i < numRuns; i++) {
                    if (Math.abs(energies[i] - minEnergy) < 0.0001)
                        sameCount++;
                }
            } while (sameCount < 6);
            System.out.println("(" + done.incrementAndGet() + "/" + numSets + ")" + set.name + ":  " + minEnergy + ", numTrials = " + numTrials + " (degree " + set.getDegreesOfFreedom() + ")");

            EnergyResult result = new EnergyResult(set.name, minEnergy);
            synchronized (energyResults) {
                energyResults.add(result);
                if (result.energy < energyResults.get(bestResult.get()).energy)
                    bestResult.set(energyResults.size() - 1);
            }
        });

        EnergyRunResults resultSet = new EnergyRunResults();
        resultSet.A = A;
        resultSet.beta = beta;
        resultSet.minDegree = minDegree;
        resultSet.maxDegree = maxDegree;
        resultSet.energies = energyResults;

        EnergyResult best = energyResults.get(bestResult.get());
        resultSet.bestEnergy = best;

        ObjectMapper om = new ObjectMapper();
        File file = new File("results/" + A + "-" + beta + "-" + minDegree + "-" + maxDegree + "-" + System.currentTimeMillis());
        try {
            om.writerWithDefaultPrettyPrinter().writeValue(file, resultSet);
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }

        System.out.println("Best Result,  " + best.isopointalSet + ": " + best.energy);

    }

    public List<IsopointalSet> generateAllIsopointalSets(int minDegreesOfFreedom, int maxDegreesOfFreedom) {
        List<IsopointalSet> result = new ArrayList<>();
        for (int i = 1; i <= IsopointalSet.NUM_SPACE_GROUPS; i++) {
            SpaceGroup sg = isopointalSetFactory.getSpaceGroup(i);

            int k = sg.getNumConstrainedWyckoffSites() + maxDegreesOfFreedom - sg.getDegreesOfFreedom();
            result.addAll(generateAllIsopointalSets(sg, k, minDegreesOfFreedom, maxDegreesOfFreedom));
        }

        return result;
    }


    public List<IsopointalSet> generateAllIsopointalSets(SpaceGroup spaceGroup, int k, int minDegreesOfFreedom, int maxDegreesOfFreedom) {

        List<IsopointalSet> result = new ArrayList<>();

        int numWyckoffSites = spaceGroup.getNumWyckoffSites();
        String sites = "";
        for (int i = 0; i < numWyckoffSites; i++) {
            WyckoffSite site = spaceGroup.getWyckoffSite(numWyckoffSites - i - 1);
            if (site.getDegreesOfFreedom() == 0)
                sites += site.code.toLowerCase();
            else
                sites += site.code.toUpperCase();
        }

        List<String> wyckoffCombinations = new ArrayList<>();
        for (int i = 1; i <= k; i++) {
            wyckoffCombinations.addAll(generateWyckoffCombinations("", sites, i));
        }

        for (String wyckoffCombination : wyckoffCombinations) {
            IsopointalSet set = isopointalSetFactory.getIsopointalSet(spaceGroup.number, wyckoffCombination);
//            System.out.println(set.name + ": " + set.getDegreesOfFreedom());
            int degree = set.getDegreesOfFreedom();
            if (degree >= minDegreesOfFreedom && degree <= maxDegreesOfFreedom)
                result.add(set);
        }

        return result;
    }



    // Uppercase sites indicate that they are replaced after selection (i.e. they can occur multiple times in result)
    public List<String> generateWyckoffCombinations(String prefix, String sites, int k) {
        if (k == 0)
            return Collections.singletonList(prefix);

        int N = sites.length();
        if (N == 0)
            return Collections.EMPTY_LIST;

        List<String> result = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            String chosenSite = "" + Character.toLowerCase(sites.charAt(i));
            String remainingSites;
            if (Character.isUpperCase(sites.charAt(i)))
                remainingSites = sites.substring(i, N);
            else
                remainingSites = sites.substring(i + 1, N);

            result.addAll(generateWyckoffCombinations(prefix + chosenSite, remainingSites, k-1));
        }

        return result;
    }
}
