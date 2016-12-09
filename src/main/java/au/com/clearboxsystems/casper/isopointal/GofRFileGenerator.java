package au.com.clearboxsystems.casper.isopointal;

import au.com.clearboxsystems.casper.math.Vector3;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by abro9163 on 7/12/16.
 */



public class GofRFileGenerator {

    private static List<Double> interAtomicDistances[];
    private static int[] multiplicity;
    public static double Rcutoff=4.0;      // Atoms further away than this value are ignored

    private static double a, b, c;
    private static double cosAlpha;
    private static double cosBeta;
    private static double cosGamma;
    private static double sinGamma;
    private static double invSinGamma;
    private static double unitVolume;
    public static double volume;
    public static double density;

    public static Vector3 vecA = new Vector3();
    public static Vector3 vecB = new Vector3();
    public static Vector3 vecC = new Vector3();


    private static void transformToCartesian(Vector3 relPos, Vector3 resultPos) {
        //TSH copied from IsopointalSet.java

        resultPos.x = relPos.x * a + relPos.y * b * cosGamma + relPos.z * c * cosBeta;
        resultPos.y = relPos.y * b * sinGamma + relPos.z * c * (cosAlpha - cosBeta * cosGamma) * invSinGamma;
        resultPos.z = relPos.z * c * unitVolume * invSinGamma;
    }

    public static double getDistSqBetweenPositions(Vector3 pos1, Vector3 pos2, int xOff, int yOff, int zOff) {
        //TSH copied from IsopointalSet.java
        //Vector3 pos1 = cartesianPositions[idx1];
        //Vector3 pos2 = cartesianPositions[idx2];

        double dx = pos1.x - pos2.x - xOff * vecA.x - yOff * vecB.x - zOff * vecC.x;
        double dy = pos1.y - pos2.y - xOff * vecA.y - yOff * vecB.y - zOff * vecC.y;
        double dz = pos1.z - pos2.z - xOff * vecA.z - yOff * vecB.z - zOff * vecC.z;

        return dx * dx + dy * dy + dz * dz;
    }

    private static void generateInterAtomDistances_GofR(IsopointalSetResult result, PrintWriter pw) {
        int numAtoms = result.numPositions;
        int numSites = result.numSites;
        interAtomicDistances = new List[numAtoms];
        multiplicity = new int[numAtoms];
        Vector3 posi=new Vector3(0,0,0);
        Vector3 posj=new Vector3(0,0,0);
        a = result.a;
        b = result.b;
        c = result.c;
        cosAlpha = Math.cos(result.alpha);
        cosBeta = Math.cos(result.beta);
        cosGamma = Math.cos(result.gamma);
        sinGamma = Math.sin(result.gamma);
        invSinGamma = 1.0 / sinGamma;
        unitVolume = Math.sqrt(1 - cosAlpha * cosAlpha - cosBeta * cosBeta - cosGamma * cosGamma + 2 * cosAlpha * cosBeta * cosGamma);
        volume = a * b * c * unitVolume;
        density = numAtoms / volume;
        int site;

        transformToCartesian(new Vector3(1, 0, 0), vecA);
        transformToCartesian(new Vector3(0, 1, 0), vecB);
        transformToCartesian(new Vector3(0, 0, 1), vecC);

        int dz = (int)Math.ceil(Rcutoff / vecC.z);
        int dy = (int)Math.ceil((Rcutoff + Math.abs(dz * vecC.y)) / vecB.y);
        int dx = (int)Math.ceil((Rcutoff + Math.abs(dz * vecC.x) + Math.abs(dy * vecB.x)) / vecA.x);

        //System.out.println("generateIAD_GofR: numAtoms="+numAtoms);

        site=0;
        for (int i = 0; i < numAtoms; i++) {

            transformToCartesian(new Vector3(result.wyckoffPositions[i].fracX,result.wyckoffPositions[i].fracY,result.wyckoffPositions[i].fracZ ), posi);

            interAtomicDistances[i] = new ArrayList<>();
            for (int j = 0; j < numAtoms; j++) {
                transformToCartesian(new Vector3(result.wyckoffPositions[j].fracX,result.wyckoffPositions[j].fracY,result.wyckoffPositions[j].fracZ ), posj);

                for (int x = -dx; x <= dx; x++) {
                    for (int y = -dy; y <= dy; y++) {
                        for (int z = -dz; z <= dz; z++) {
                            if (x == 0 && y == 0 && z == 0 && i == j)
                                continue;

                            double rsq = getDistSqBetweenPositions(posi, posj, x, y, z);
                            if (rsq <= Rcutoff*Rcutoff) {
                                interAtomicDistances[i].add(Math.sqrt(rsq));
                                //pw.println("i="+i+" j="+j+" x="+x+" y="+y+" z="+z+" r="+Math.sqrt(rsq));
                                for (int wsite=0; wsite<numSites;wsite++) {
                                    if (site==wsite) {
                                        pw.print(Math.sqrt(rsq)+" ");
                                    } else {
                                        pw.print("-1 ");
                                    }
                                }
                                pw.println();
                            }
                        }
                    }
                }
            }
           // System.out.println("generateIAD_GofR:            i="+i+" multiplicity[i]="+result.wyckoffSites[site].multiplicity);
            multiplicity[i] = result.wyckoffSites[site].multiplicity;
            i += (multiplicity[i]-1);
            site++;
        }
    }



    public static void createGofRFile(String resultPath, IsopointalSetResult result, String name) {
        try {
            PrintWriter pw = new PrintWriter(resultPath + "/GofR/" + name + ".dat");

            pw.println("#Isopointal set " + name);

            generateInterAtomDistances_GofR(result, pw);

            pw.close();
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
        }
    }
}



