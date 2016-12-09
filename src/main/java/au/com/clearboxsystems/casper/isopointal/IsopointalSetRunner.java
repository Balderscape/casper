package au.com.clearboxsystems.casper.isopointal;

import com.fasterxml.jackson.databind.ObjectMapper;

//TSH next few
import java.nio.file.*;
import java.nio.charset.*;
import java.util.*;

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
    public static final int MAX_DOUBLINGS = 2;
    public static final int MIN_SAME = 2;
    public static final int NUM_RUNS = 3;
    public static final double SAME_EPS = 0.001;
    // Trouble Sets 208abcij, 81,920,000 trials
    /*
    FCC - 225a / 225b / 228b for two wyckoff: 227cd
    BCC - 229a / 230a /228a
    SC - 221a / 229c /226a
    DC - 227a
    Graphite - 194bc (or 194a)
    simple triangular prism 191a?

     */

    IsopointalSetFactory isopointalSetFactory = new IsopointalSetFactory();

    public static void main(String[] args) {
        IsopointalSetRunner runner = new IsopointalSetRunner();

/*        for (int A = 15; A >= 1; A-=1) {
            for (int beta = 12; beta >= 1; beta-=1) {
                runner.runSet(A/10.0, beta, 12, PermType.Combination, 1, 1);
            }
        }
*/

/*
        for (int A = 30; A >=2; A-=4) {
            for (int rhobar0 = 12; rhobar0 >= 1; rhobar0 -= 1) {
                runner.runSet(A, rhobar0, 2.0, PermType.Special, 1, 1);
            }
        }
*/
 /*   for (int tenK = 100; tenK >=  60; tenK -= 5) {
        for (int hundredphi = 80; hundredphi >= 40; hundredphi -= 3) {
            runner.runSet(tenK / 10.0, hundredphi / 100.0, 0.00, PermType.Combination, 2, 2);
        }
    }  */

 /*      for (int tenr0 = 12; tenr0 >= 12; tenr0 -=0) {
            for (int hundredepsilon = 100; hundredepsilon >=0; hundredepsilon -=10){
                runner.runSet(tenr0 / 10.0, hundredepsilon / 100.0, 0.00, PermType.Combination, 1, 1);

            }
        } */

           runner.runSet(8.5, 0.68, 0.00, PermType.Combination, 2, 2);

  /*      for (int tenbeta = 150; tenbeta >=  20; tenbeta -=10) {
            for (int rhobar0 = 12; rhobar0 >= 1; rhobar0 -= 1) {
                runner.runSet(10.0, rhobar0, tenbeta / 10.0, PermType.Special, 1, 1);
            }
        }
*/

        /*for (int tenepsilon = 18; tenepsilon >= 0; tenepsilon-=2) {
            for (int tenr0 = 10; tenr0 <=21; tenr0++) {
                runner.runSet(tenr0/10.0, tenepsilon/10.0, 0.02, PermType.Combination, 1, 1);
            }
        }*/

    }

    public void runSet(double potential_param1, double potential_param2, double potential_param3, PermType permType, int min, int max) {

        String resultPath = "results/" + potential_param1 + "-" + potential_param2 + "-" + potential_param3 + "/";
        File iso = new File(resultPath + "iso");
        File xtl = new File(resultPath + "xtl");
        File cif = new File(resultPath + "cif");
        File GofR = new File(resultPath + "GofR");

        if (!iso.exists())
            iso.mkdirs();

        if (!xtl.exists())
            xtl.mkdirs();

        if (!cif.exists())
            cif.mkdirs();

        if (!GofR.exists())
            GofR.mkdirs();

        IsopointalSetRunner runner = new IsopointalSetRunner();
        runner.computeMinEnergies(potential_param1, potential_param2, potential_param3, permType, min, max, resultPath);
    }

    public void computeMinEnergies(double potential_param1, double potential_param2, double potential_param3, PermType permType, int min, int max, String resultPath) {
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);


        List<IsopointalSet> isopointalSets = null;
        switch(permType) {
            case Degree:
                isopointalSets = generateAllIsopointalSetsOfDegree(min, max);
                break;
            case Combination:
                isopointalSets = generateAllIsopointalSetsWithPermutations(min, max);
                break;
            case Special:
                isopointalSets = generateSpecialIsopointalSets();
                break;

        }
        List<IsopointalSetResult> minResults = new ArrayList<>();

        int numSets = isopointalSets.size();
        System.out.println("Computing ("+ potential_param1 + "-" + potential_param2 + "-" + potential_param3+ ") energies for " + numSets + " isopointal sets on " + (Runtime.getRuntime().availableProcessors() - 1) + " CPUs");
        AtomicInteger done = new AtomicInteger();

        long startTime = System.currentTimeMillis();
        for (IsopointalSet set : isopointalSets) {

            //System.out.println("one");

            Runnable worker = new Runnable() {
                @Override
                public void run() {
                    //System.out.println("two");

                    SimulatedAnneal sa = new SimulatedAnneal();

                    int numTrials = 5000 * set.getDegreesOfFreedom();
                    int sameCount;
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
                            results[i] = sa.runSimulatedAnneal(numTrials, 2, 0.01, set, potential_param1, potential_param2, potential_param3);

                        minEnergy = results[0].energyPerAtom;
                        minResult = results[0];
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
                    } while (sameCount < MIN_SAME && tries <= MAX_DOUBLINGS);

//                  System.out.println ("\n-----------------------------");
//                 System.out.println ("Isopointal Set Results");
//                  System.out.println ("-----------------------------");
//                  System.out.format("%20s%20s%15s%2energyResults.lastIndexOf(resultSet)4s%30s%10s", "Number of Sets", "Isopointal Set", "Energy", "Density", "Number of trials", "DOF");
//                  System.out.println("");
//                 System.out.format("%20s%20s%40d%40d%25s%25s","        "  +"(" + done.incrementAndGet() + "/" + numSets + ")" + "             " + minResult.isopointalSet +" =? "+ set.name + ":  " + "       "+ minEnergy + "     "+minResult.density +  "      " +numTrials +   "                  " + set.getDegreesOfFreedom()  + (sameCount < MIN_SAME ? " (Gave Up)" : ""));

                  System.out.println("(" + done.incrementAndGet() + "/" + numSets + ")" + minResult.isopointalSet +" =? "+ set.name + ":  " + minEnergy + ", density = "+minResult.density+", numTrials = " + numTrials + " (degree " + set.getDegreesOfFreedom() + ")" + (sameCount < MIN_SAME ? " (Gave Up)" : ""));
//                    System.out.println("(" + done.incrementAndGet() + "/" + numSets + ")" );
//                    System.out.println("("  + minResult.isopointalSet +" =? "+ set.name + ":  " );
//                    System.out.println( minEnergy);
//                    System.out.println(",density =  "+minResult.density+"");
//                    System.out.println( "numTrials = " + numTrials + " (degree " + set.getDegreesOfFreedom() + ")" + (sameCount < MIN_SAME ? " (Gave Up)" : ")"));

                    minResult.attempts = tries;
                    minResult.timeoutBeforeMinFound = sameCount < MIN_SAME;
                    minResult.sameCount = sameCount;

//                  EnergyResult result = new EnergyResult(set.name, minEnergy, sameCount >= MIN_SAME, minResult.density);
//                  synchronized (energyResults) {
//                        energyResults.add(result);
//                  }

                    ObjectMapper om = new ObjectMapper();
                    String runName = minResult.isopointalSet + "-" + minResult.pot_param1 + "-" + minResult.pot_param2 + "-" + minResult.pot_param3+ "-" + System.currentTimeMillis();

                    File file = new File(resultPath + "/iso/" + runName + ".iso");
                    try {
                        om.writerWithDefaultPrettyPrinter().writeValue(file, minResult);
                    } catch (IOException ex) {
                        System.out.println(ex);
                        ex.printStackTrace();
                    }

                    XTLFileGenerator.createXTLFile(resultPath, minResult, runName);
                    CIFFileGenerator.createCIFFile(resultPath, minResult, runName);
                    GofRFileGenerator.createGofRFile(resultPath, minResult, runName);

                    synchronized(minResults) {
                        minResults.add(minResult);
                    }
                }
            };
            executor.submit(worker);
        }

        executor.shutdown();
        while(!executor.isTerminated()) {
            try {
               executor.awaitTermination(2, TimeUnit.SECONDS);
          } catch (InterruptedException ignore) {}
        }
        EnergyRunResults resultSet = new EnergyRunResults();
        resultSet.potential_param1 = potential_param1;
        resultSet.potential_param2 = potential_param2;
        resultSet.potential_param3 = potential_param3;
        resultSet.type = permType;
        resultSet.min = min;
        resultSet.max = max;

        int found;


        synchronized (minResults) {
            Collections.sort(minResults);

            List<EnergyResult> energyResults = new ArrayList<>();
            for (IsopointalSetResult isoResult : minResults)
                energyResults.add(new EnergyResult(isoResult.isopointalSet, isoResult.energyPerAtom, !isoResult.timeoutBeforeMinFound, isoResult.density));

            resultSet.energies = energyResults;
            EnergyResult best = energyResults.get(0);

            ObjectMapper om = new ObjectMapper();
            File file = new File(resultPath + "/" + potential_param1 + "-" + potential_param2 + "-" + potential_param3 + "-" + permType.name() + " - " + min + "-" + max + "-" + System.currentTimeMillis());
            try {
                om.writerWithDefaultPrettyPrinter().writeValue(file, resultSet);
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }

            //Alex and Toby's tabular attempt
            try {
                ArrayList<String> lines = new ArrayList<String>();
                lines.add("#potential_param1="+String.valueOf(resultSet.potential_param1)+" potential_param2="+String.valueOf(resultSet.potential_param2)+" potential_param3="+String.valueOf(resultSet.potential_param3)+" type="+String.valueOf(resultSet.type)+" min="+String.valueOf(resultSet.min)+" max="+String.valueOf(resultSet.max));
                lines.add("#index,energy,density,isopointal set, minFound");
//                for (int i=0; i<energyResults.lastIndexOf(resultSet); i++) {
                for (int i=0; i<energyResults.size(); i++) {
                    if (resultSet.energies.get(i).minFound) {
                        found = 1;
                    } else {
                        found = 0;
                    }
                    lines.add(String.valueOf(i)+" "+ String.valueOf(resultSet.energies.get(i).energy) +" "+String.valueOf(resultSet.energies.get(i).density)+" "+String.valueOf(found)+" \""+String.valueOf(resultSet.energies.get(i).isopointalSet)+"\"");
                }

                Path file1 = Paths.get(resultPath + "/Table-" + potential_param1 + "-" + potential_param2 + "-" + potential_param3 + "-" + permType.name() + " - " + min + "-" + max + "-" + System.currentTimeMillis()+".dat");
                Files.write(file1, lines, Charset.forName("UTF-8"));
            } catch (IOException ex) {
                System.out.println(ex);
                ex.printStackTrace();
            }

            System.out.println("Run took " + ((System.currentTimeMillis()-startTime) / 1000.0 / 60.0) + " minutes" );
            System.out.println("Best ("+ potential_param1 + "-" + potential_param2 + "-" + potential_param3 + ") Result,  " + best.isopointalSet + ": " + best.energy);
        }

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

    public List<IsopointalSet> generateSpecialIsopointalSets() {
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
            wyckoffCombinations.addAll(generateWyckoffCombinations("", sites, 1));

            for (String wyckoffCombination : wyckoffCombinations) {
                IsopointalSet set = isopointalSetFactory.getIsopointalSet(sg.number, wyckoffCombination);
                if ((set.name.equals("225a"))||(set.name.equals("226a"))||(set.name.equals("227a"))||(set.name.equals("229a"))) {
//                if ((set.name.equals("225a"))||(set.name.equals("229a"))) {
//                  System.out.println(set.name + ": " + set.getDegreesOfFreedom());
//                  if ((set.name.equals("208a"))||(set.name.equals("208b"))||(set.name.equals("208c"))||(set.name.equals("209i"))||(set.name.equals("208j")))  {
                    System.out.println(set.name + ": " + set.getDegreesOfFreedom());
                    result.add(set);
                }
            }

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
