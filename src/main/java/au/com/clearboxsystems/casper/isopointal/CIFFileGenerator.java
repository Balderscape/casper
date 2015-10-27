package au.com.clearboxsystems.casper.isopointal;

import java.io.PrintWriter;

/**
 * Created by pauls on 21/10/15.
 */
public class CIFFileGenerator {

	public static void createCIFFile(String resultPath, IsopointalSetResult result, String name) {
		try {
			PrintWriter pw = new PrintWriter(resultPath + "/cif/" + name + ".cif");

			pw.println("_symmetry_Int_Tables_number  " + result.spaceGroup);
			pw.println();

			pw.printf("_cell_length_a    %.5f\n", result.a);
			pw.printf("_cell_length_b    %.5f\n", result.b);
			pw.printf("_cell_length_c    %.5f\n", result.c);
			pw.printf("_cell_angle_alpha %.5f\n", result.alpha * 180 / Math.PI);
			pw.printf("_cell_angle_beta  %.5f\n", result.beta * 180 / Math.PI);
			pw.printf("_cell_angle_gamma %.5f\n", result.gamma * 180 / Math.PI);
			pw.println();

			pw.println("loop_");
			pw.println("_atom_site_label");
			pw.println("_atom_site_type_symbol");
			pw.println("_atom_site_symmetry_multiplicity");
			pw.println("_atom_site_Wyckoff_label");
			pw.println("_atom_site_fract_x");
			pw.println("_atom_site_fract_y");
			pw.println("_atom_site_fract_z");
			pw.println("_atom_site_occupancy");

			int idx = 1;
			for (WyckoffSiteResult site : result.wyckoffSites)
				pw.printf("H%d H %d %s %.5f %.5f %.5f %.5f\n",
						idx++,
						site.multiplicity,
						site.code,
						site.fracX,
						site.fracY,
						site.fracZ,
						1.0
						);

			pw.close();
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}
	}
}
