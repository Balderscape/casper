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
    public static final int MAX_DOUBLINGS = 4;
    public static final int MIN_SAME = 4;
    public static final int NUM_RUNS = 5;
    public static final double SAME_EPS = 0.001;
    // Trouble Sets 208abcij, 81,920,000 trials
    /*
    FCC - 225a
    BCC - 229a
    SC - 221a
    DC - 227a
    Graphite - 194bc (or 194a)
     */

    IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

    public static void main(String[] args) {
        IsopointalSetRunner runner = new IsopointalSetRunner();

        for (int A = 15; A >= 0; A--) {
            for (int beta = 12; beta >= 0; beta--) {
                runner.runSet(A / 10.0, beta, PermType.Combination, 1, 1);
            }
        }

    }

    public void runSet(double A, double beta, PermType permType, int min, int max) {

        String resultPath = "results/" + A + "-" + beta + "/";
        File iso = new File(resultPath + "iso");
        File xtl = new File(resultPath + "xtl");
        File cif = new File(resultPath + "cif");

        if (!iso.exists())
            iso.mkdirs();

        if (!xtl.exists())
            xtl.mkdirs();

        if (!cif.exists())
            cif.mkdirs();

        IsopointalSetRunner runner = new IsopointalSetRunner();
        runner.computeMinEnergies(A, beta, permType, min, max, resultPath);
    }

    public void computeMinEnergies(double A, double beta, PermType permType, int min, int max, String resultPath) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);


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
        System.out.println("Computing ( "+ A + "-" + beta+ ") energies for " + numSets + " isopointal sets on " + (Runtime.getRuntime().availableProcessors() - 1) + " CPUs");
        AtomicInteger done = new AtomicInteger();

        long startTime = System.currentTimeMillis();
        for (IsopointalSet set : isopointalSets) {

            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    SimulatedAnneal sa = new SimulatedAnneal();

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

                        IsopointalSetResult[] results = new IsopointalSetResult[NUM_RUNS];
                        for (int i = 0; i < NUM_RUNS; i++)
                            results[i] = sa.runSimulatedAnneal(numTrials, 2, 0.01, set, A, beta);

                        minEnergy = results[0].energyPerAtom;
                        for (int i = 1; i < NUM_RUNS; i++)
                            if (results[i].energyPerAtom < minEnergy) {
                                minEnergy = results[i].energyPerAtom;
                                minResult = results[i];
                            }

                        sameCount = 0;
                        for (int i = 0; i < NUM_RUNS; i++) {
                            if (Math.abs(results[i].energyPerAtom - minEnergy) < SAME_EPS)
                                sameCount++;
                        }
                        tries++;
                    } while (sameCount < MIN_SAME && tries < MAX_DOUBLINGS);
                    System.out.println("(" + done.incrementAndGet() + "/" + numSets + ")" + set.name + ":  " + minEnergy + ", numTrials = " + numTrials + " (degree " + set.getDegreesOfFreedom() + ")");

                    minResult.attempts = tries;
                    minResult.timeoutBeforeMinFound = sameCount < MIN_SAME;

                    EnergyResult result = new EnergyResult(set.name, minEnergy, sameCount >= MIN_SAME, minResult.density);
                    synchronized (energyResults) {
                        energyResults.add(result);
                    }

                    ObjectMapper om = new ObjectMapper();
                    String runName = minResult.isopointalSet + "-" + minResult.EAM_A + "-" + minResult.EAM_beta + "-" + System.currentTimeMillis();

                    File file = new File(resultPath + "/iso/" + runName + ".iso");
                    try {
                        om.writerWithDefaultPrettyPrinter().writeValue(file, minResult);
                    } catch (IOException ex) {
                        System.out.println(ex);
                        ex.printStackTrace();
                    }

                    XTLFileGenerator.createXTLFile(resultPath, minResult, runName);
                    CIFFileGenerator.createCIFFile(resultPath, minResult, runName);
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
        File file = new File(resultPath + "/" + A + "-" + beta + "-" + permType.name() + " - " + min + "-" + max + "-" + System.currentTimeMillis());
        try {
            om.writerWithDefaultPrettyPrinter().writeValue(file, resultSet);
        } catch (IOException ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }


        System.out.println("Run took " + ((startTime - System.currentTimeMillis()) / 1000.0 / 60.0) + " minutes" );
        System.out.println("Best ( \"+ A + \"-\" + beta+ \") Result,  " + best.isopointalSet + ": " + best.energy);

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
