package au.com.clearboxsystems.casper.isopointal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

import static org.testng.Assert.*;

/**
 * Created by pauls on 16/09/15.
 */
public class SimulatedAnnealTest {

	@Test
	public void testName() throws Exception {
		SimulatedAnneal sim = new SimulatedAnneal();

//		double minEnergy = sim.findMinimumEnergy(2, 100000, 225, new String[]{"f"},  0.5, 4);
//		double minEnergy = sim.findMinimumEnergy(2, 100000, 229, new String[]{"a"},  1, 2);
//		double minEnergy = sim.findMinimumEnergy(5, 100000, 89, new String[]{"a", "b", "c", "d", "e", "f", "h"},  0.5, 4);

		IsopointalSet set = new IsopointalSetFactory().getIsopointalSet(193, "e");
		IsopointalSetResult result = sim.runSimulatedAnneal(100000, 2, 0.01, set, 1.5, 1.2, 12);
		System.out.println(result.energyPerAtom);

		ObjectMapper om = new ObjectMapper();
		String runName = result.isopointalSet + "-" + result.pot_param1 + "-" + result.pot_param2+ "-" + result.pot_param3 + "-" + System.currentTimeMillis();

		String resultPath = "results/";
		File iso = new File(resultPath + "iso");
		File xtl = new File(resultPath + "xtl");
		File cif = new File(resultPath + "cif");

		if (!iso.exists())
			iso.mkdirs();

		if (!xtl.exists())
			xtl.mkdirs();

		if (!cif.exists())
			cif.mkdirs();


		File file = new File("results/iso/" + runName + ".json");
		try {
			om.writerWithDefaultPrettyPrinter().writeValue(file, result);
		} catch (IOException ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}

		XTLFileGenerator.createXTLFile("results", result, runName);
		CIFFileGenerator.createCIFFile("results", result, runName);
	}
}