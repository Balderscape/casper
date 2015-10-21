package au.com.clearboxsystems.casper.isopointal;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by pauls on 4/10/15.
 */
public class IsopointalSetRunner {
    public static final int MAX_TRIES = 4;
    public static final int MIN_SAME = 2;
    public static final double SAME_EPS = 0.001;
    // Trouble Sets 208abcij, 81,920,000 trials

    IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

    public static void main(String[] args) {
        IsopointalSetRunner runner = new IsopointalSetRunner();
        runner.computeMinEnergies(0.5, 4, PermType.Combination, 1, 1);
    }

    public void computeMinEnergies(double A, double beta, PermType permType, int min, int max) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());


        List<IsopointalSet> isopointalSets = null;
        switch(permType) {
            case Degree:
                isopointalSets = generateAllIsopointalSetsOfDegree(min, max);
                break;
            case Combination:
                isopointalSets = generateAllIsopointalSetsWithPermutations(min, max);
                break;

        }
        List<EnergyResult> energyResults = new ArrayList<>();

        int numSets = isopointalSets.size();
        System.out.println("Computing energies for " + numSets + " isopointal sets");
        AtomicInteger done = new AtomicInteger();

        for (IsopointalSet set : isopointalSets) {

            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    SimulatedAnneal sa = new SimulatedAnneal();

                    int numRuns = 8;
                    int numTrials = 10000 * set.getDegreesOfFreedom();
                    int sameCount = 0;
                    double minEnergy = Double.MAX_VALUE;
                    IsopointalSetResult minResult = null;
                    boolean first = true;
                    int tries = 0;
                    do {
                        if (first)
                            first = false;
                        else
                            numTrials *= 2;

                        IsopointalSetResult[] results = new IsopointalSetResult[numRuns];
                        for (int i = 0; i < numRuns; i++)
                            results[i] = sa.runSimulatedAnneal(numTrials, 2, 0.01, set, A, beta);

                        minEnergy = results[0].energyPerAtom;
                        for (int i = 1; i < numRuns; i++)
                            if (results[i].energyPerAtom < minEnergy) {
                                minEnergy = results[i].energyPerAtom;
                                minResult = results[i];
                            }

                        sameCount = 0;
                        for (int i = 0; i < numRuns; i++) {
                            if (Math.abs(results[i].energyPerAtom - minEnergy) < SAME_EPS)
                                sameCount++;
                        }
                        tries++;
                    } while (sameCount < MIN_SAME && tries < MAX_TRIES);
                    System.out.println("(" + done.incrementAndGet() + "/" + numSets + ")" + set.name + ":  " + minEnergy + ", numTrials = " + numTrials + " (degree " + set.getDegreesOfFreedom() + ")");

                    EnergyResult result = new EnergyResult(set.name, minEnergy, tries < MAX_TRIES);
                    synchronized (energyResults) {
                        energyResults.add(result);
                    }

                    ObjectMapper om = new ObjectMapper();
                    String runName = minResult.isopointalSet + "-" + minResult.EAM_A + "-" + minResult.EAM_beta + "-" + System.currentTimeMillis();

                    File file = new File("results/iso/" + runName + ".json");
                    try {
                        om.writerWithDefaultPrettyPrinter().writeValue(file, minResult);
                    } catch (IOException ex) {
                        System.out.println(ex);
                        ex.printStackTrace();
                    }

                    XTLFileGenerator.createXTLFile(minResult, runName);
                }
            };
            executor.submit(worker);
        }

        executor.shutdown();
        while(!executor.isTerminated()) {
            try {
                executor.awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException ignore) {}
        }
        EnergyRunResults resultSet = new EnergyRunResults();
        resultSet.A = A;
        resultSet.beta = beta;
        resultSet.type = permType;
        resultSet.min = min;
        resultSet.max = max;
        resultSet.energies = energyResults;

        Collections.sort(resultSet.energies);

        EnergyResult best = energyResults.get(0);

        ObjectMapper om = new ObjectMapper();
        File file = new File("results/" + A + "-" + beta + "-" + permType.name() + " - " + min + "-" + max + "-" + System.currentTimeMillis());
        try {
            om.writerWithDefaultPrettyPrinter().writeValue(file, resultSet);
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }

        System.out.println("Best Result,  " + best.isopointalSet + ": " + best.energy);

    }

    public List<IsopointalSet> generateAllIsopointalSetsWithPermutations(int minK, int maxK) {
        List<IsopointalSet> result = new ArrayList<>();
        for (int i = 1; i <= IsopointalSet.NUM_SPACE_GROUPS; i++) {
            SpaceGroup sg = isopointalSetFactory.getSpaceGroup(i);

            int numWyckoffSites = sg.getNumWyckoffSites();
            String sites = "";
            for (int j = 0; j < numWyckoffSites; j++) {
                WyckoffSite site = sg.getWyckoffSite(numWyckoffSites - j - 1);
                if (site.getDegreesOfFreedom() == 0)
                    sites += site.code.toLowerCase();
                else
                    sites += site.code.toUpperCase();
            }

            List<String> wyckoffCombinations = new ArrayList<>();
            for (int j = minK; j <= maxK; j++) {
                wyckoffCombinations.addAll(generateWyckoffCombinations("", sites, j));
            }

            for (String wyckoffCombination : wyckoffCombinations) {
                IsopointalSet set = isopointalSetFactory.getIsopointalSet(sg.number, wyckoffCombination);
//            System.out.println(set.name + ": " + set.getDegreesOfFreedom());
                result.add(set);
            }

        }

        return result;
    }

    public List<IsopointalSet> generateAllIsopointalSetsOfDegree(int minDegreesOfFreedom, int maxDegreesOfFreedom) {
        List<IsopointalSet> result = new ArrayList<>();
        for (int i = 1; i <= IsopointalSet.NUM_SPACE_GROUPS; i++) {
            SpaceGroup sg = isopointalSetFactory.getSpaceGroup(i);

            int k = sg.getNumConstrainedWyckoffSites() + maxDegreesOfFreedom - sg.getDegreesOfFreedom();
            result.addAll(generateAllIsopointalSetsOfDegree(sg, k, minDegreesOfFreedom, maxDegreesOfFreedom));
        }

        return result;
    }


    public List<IsopointalSet> generateAllIsopointalSetsOfDegree(SpaceGroup spaceGroup, int maxK, int minDegreesOfFreedom, int maxDegreesOfFreedom) {

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
        for (int i = 1; i <= maxK; i++) {
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
