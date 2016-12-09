package au.com.clearboxsystems.casper.isopointal;

/**
 * Created by abro9163 on 2/12/16.
 */

import java.util.ArrayList;
import java.util.List;

public class OPPotential {

    public final double K;    // Frequency of the potential
    public final double phi;     // phase shift
    public final double blank;

    public final double Rcutoff;      // Atoms further away than this value are ignored

    private final double RcutoffSq;
    private final double Rcutoff_3;
    private final double Rcutoff_15;     //new cutoff for OPP after 3rd well
    private final double shift;

    private List<Double> interAtomicDistances[];
    private int[] multiplicity;

    public OPPotential() {
        this(1.0,0.0,0.0);
    }

    public OPPotential(double K, double phi,double blank) {
        this.K = K;
        this.phi = phi;
        this.blank =blank;




        Rcutoff = ((4*Math.PI+phi)/K)+1.25; /* out to the edge of the third well (third peak of cosine function) */

        RcutoffSq = Rcutoff * Rcutoff;

        Rcutoff_3 = Rcutoff*Rcutoff*Rcutoff;
        Rcutoff_15 = Rcutoff_3*Rcutoff_3*Rcutoff_3*Rcutoff_3*Rcutoff_3;
        shift = 1.0/(Rcutoff_15) - 1/(Rcutoff_3) * Math.cos(K*(Rcutoff-1.25)-phi);

    }



    public double computeEnergy(IsopointalSet isopointalSet) {
        double totalEnergy = 0;

        generateInterAtomDistances(isopointalSet);

        for (int i = 0; i < isopointalSet.getNumPositions(); i++) {
            double energy = 0.0;

            double phiSum = 0;
            for (double dist : interAtomicDistances[i]) {
                phiSum += computeOPP(dist);
            }
            energy += 0.5 * phiSum;

            int mult = multiplicity[i];
            totalEnergy += mult * energy;
            i += (mult - 1); // Skip the positions with same energy;
        }

        return totalEnergy;
    }

    private void generateInterAtomDistances(IsopointalSet isopointalSet) {
        int numAtoms = isopointalSet.getNumPositions();
        interAtomicDistances = new List[numAtoms];
        multiplicity = new int[numAtoms];

        int dz = (int)Math.ceil(Rcutoff / isopointalSet.vecC.z);
        int dy = (int)Math.ceil((Rcutoff + Math.abs(dz * isopointalSet.vecC.y)) / isopointalSet.vecB.y);
        int dx = (int)Math.ceil((Rcutoff + Math.abs(dz * isopointalSet.vecC.x) + Math.abs(dy * isopointalSet.vecB.x)) / isopointalSet.vecA.x);

        for (int i = 0; i < numAtoms; i++) {
            interAtomicDistances[i] = new ArrayList<>();
            for (int j = 0; j < numAtoms; j++) {


                for (int x = -dx; x <= dx; x++) {
                    for (int y = -dy; y <= dy; y++) {
                        for (int z = -dz; z <= dz; z++) {
                            if (x == 0 && y == 0 && z == 0 && i == j)
                                continue;

                            double rsq = isopointalSet.getDistSqBetweenPositions(i, j, x, y, z);
                            if (rsq <= RcutoffSq)
                                interAtomicDistances[i].add(Math.sqrt(rsq));
                        }
                    }
                }
            }
            multiplicity[i] = isopointalSet.getMultiplicity(i);
            i += (multiplicity[i]-1);
        }
    }

    private double computeOPP(double r) {

        // Short range repulsion term
        double r3;
        double r6;
        double r15;
        r3=r*r*r;
        r6=r3*r3;
        r15=r6*r6*r3;

        //double OP = 1.0/(r15)+1.0/r3;

        // calculate the damped oscillation term




        return 1.0/(r15) + (1.0/r3)*Math.cos(K*(r-1.25)-phi)-shift;

    }


    public static void main(String[] args) {
        OPPotential pot = new OPPotential();
//       System.out.println("LJ(2): " + pot.computeOPP(4));
//       System.out.println("LJ(1): " + pot.computeOPP(2));
    }
}
