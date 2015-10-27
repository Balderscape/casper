package au.com.clearboxsystems.casper.isopointal;

import java.io.PrintWriter;

/**
 * Created by pauls on 21/10/15.
 */
public class XTLFileGenerator {

	public static void createXTLFile(String resultPath, IsopointalSetResult result, String name) {
		try {
			PrintWriter pw = new PrintWriter(resultPath + "/xtl/" + name + ".xtl");

			pw.println("TITLE " + name);
			pw.println("CELL");
			pw.printf("  %.6f   %.6f   %.6f   %.6f   %.6f   %.6f\n",
					result.a,
					result.b,
					result.c,
					result.alpha * 180 / Math.PI,
					result.beta * 180 / Math.PI,
					result.gamma * 180 / Math.PI
			);
			pw.println("SYMMETRY NUMBER 1");
			pw.println("SYMMETRY LABEL  P1");
			pw.println("ATOMS");
			pw.println("NAME         X           Y           Z");
			for (WyckoffPositionResult pos : result.wyckoffPositions)
				pw.printf("H           %.6f   %.6f   %.6f\n",
						pos.fracX,
						pos.fracY,
						pos.fracZ);
			pw.println("EOF");

			pw.close();
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}
	}
}
