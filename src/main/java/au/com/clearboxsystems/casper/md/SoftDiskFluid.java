package au.com.clearboxsystems.casper.md;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.Stage;
import au.com.clearboxsystems.casper.math.Vector2;
import au.com.clearboxsystems.casper.math.Vector2I;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an example taken from ch2 of the art of molecular dynamics simulation.
 *
 * User: pauls
 * Timestamp: 3/08/2015 2:21 PM
 */
public class SoftDiskFluid {
	private static final int NUM_DIMENSIONS = 2;
	private static final Logger LOG = LoggerFactory.getLogger(SoftDiskFluid.class);

	public static class Config {
		public double deltaT = 0.005;
		public double density = 0.8;
		public Vector2I initUnitCell = new Vector2I(50, 50);
		public int pathMol = -1;
		public int lineAvg = 10;
		public int stepAvg = 100;
//		public int stepEquil = 0;
		public int stepLimit = 100000;
		public double temperature = 1;
	}

	private Config config;

	private double rCut;
	private Vector2 region;
	private int nMol;
	private double velocityMagnitude;

	List<Molecule2D> molecules;
	Molecule2DPath moleculePath;

	private StatisticalDouble totalEnergy = new StatisticalDouble();
	private StatisticalDouble kineticEnergy = new StatisticalDouble();
	private StatisticalDouble pressure = new StatisticalDouble();

	private int stepCount;

	private double simulationTime = 0;
	private double uSum = 0;
	private double virSum = 0;
	private Vector2 vSum = new Vector2();
	private double vvSum = 0;

	private Stage stage;

	public void run(Config config) {
		init(config);
		LOG.info(" STEP SIM_TIME AVG_VEL TOT_NRGY AVG/SD KIN_NRGY AVG/SD PRESSURE AVG/SD");

		try {
			Thread.sleep(1000);
		} catch (InterruptedException ignore) {}
		while (stepCount < config.stepLimit) {
			singleStep();
			stage.update();
			try {
				Thread.sleep(10);
			} catch (InterruptedException ignore) {}
		}
	}

	private void init(Config config) {
		this.config = config;
		setupParams();
		setupJob();

		resetStats();
		stepCount = 0;

		stage = new Stage();
		stage.setExitOnClose(true);
		for (Molecule2D mol : molecules) {
			stage.addSphere(mol);
		}
	}

	private void setupParams() {
		rCut = Math.pow(2., 1. / 6.);
		region = config.initUnitCell.nScale(1. / Math.sqrt(config.density));
		nMol = config.initUnitCell.product();
		velocityMagnitude = Math.sqrt(NUM_DIMENSIONS * (1. - 1. / nMol) * config.temperature);
	}

	private void setupJob() {
		molecules = new ArrayList<>();

		Vector2 gap = region.nDiv(config.initUnitCell);
		Vector2 velSum = new Vector2();

		for (int nx = 0; nx < config.initUnitCell.x; nx++) {
			for (int ny = 0; ny < config.initUnitCell.y; ny++) {
				Molecule2D mol = new Molecule2D();
				mol.position = new Vector2(nx + 0.5, ny + 0.5).times(gap).sAdd(-0.5, region);
				mol.velocity = Vector2.rand(velocityMagnitude);
				velSum.add(mol.velocity);
				mol.acceleration = new Vector2();

				molecules.add(mol);
			}
		}

		// Remove any flow
		velSum.scale(-1. / nMol);
		for (int i = 0; i < nMol; i++) {
			molecules.get(i).velocity.add(velSum);
		}

		if (config.pathMol >= 0)
			moleculePath = new Molecule2DPath(molecules.get(config.pathMol));
	}

	private void resetStats() {
		totalEnergy.zero();
		kineticEnergy.zero();
		pressure.zero();
	}

	private void singleStep() {
		simulationTime = ++stepCount * config.deltaT;

		startLeapfrog();
		applyBoundaryConditions();
		computeForces();
		completeLeapfrog();
		evaluateProperties();

		if (stepCount % config.stepAvg == 0) {
			printProperties();
			resetStats();
		}

		if (config.pathMol >=0 && stepCount % config.lineAvg == 0) {
			moleculePath.addPoint(molecules.get(config.pathMol));
			stage.clearLines();
			stage.addLines(moleculePath.getLines());
		}
	}

	private void printProperties() {
		LOG.info("{}", String.format("%5d %8.4f %7.4f %7.4f %7.4f %7.4f %7.4f %7.4f %7.4f",
				stepCount, simulationTime, (vSum.x + vSum.y) / nMol,
				totalEnergy.average(), totalEnergy.stdDev(),
				kineticEnergy.average(), kineticEnergy.stdDev(),
				pressure.average(), pressure.stdDev()));
	}

	private void evaluateProperties() {
		vSum.set(0, 0);
		vvSum = 0;

		for (Molecule2D mol : molecules) {
			vSum.add(mol.velocity);
			vvSum += mol.velocity.dot(mol.velocity);
		}

		double ke = 0.5 * vvSum / nMol;
		kineticEnergy.accum(ke);
		totalEnergy.accum(ke + uSum / nMol);
		pressure.accum(config.density * (vvSum + virSum) / (nMol * NUM_DIMENSIONS));
	}

	private void computeForces() {
		double rrCut = rCut * rCut;
		for (Molecule2D mol : molecules) {
			mol.acceleration.set(0, 0);
		}

		uSum = 0;
		virSum = 0;
		for (int j1 = 0; j1 < nMol - 1; j1++) {
			Molecule2D mol1 = molecules.get(j1);
			for (int j2 = j1 + 1; j2 < nMol; j2++) {
				Molecule2D mol2 = molecules.get(j2);
				Vector2 dr = mol1.position.nSub(mol2.position).wrap(region);
				double rr = dr.dot(dr);
				if (rr < rrCut) {
					double rri = 1. / rr;
					double rri3 = rri * rri * rri;
					double fcVal = 48. * rri3 * (rri3 - 0.5) * rri;
					mol1.acceleration.sAdd(fcVal, dr);
					mol2.acceleration.sAdd(-fcVal, dr);
					uSum += 4. * rri3 * (rri3 - 1.) + 1.;
					virSum += fcVal * rr;
				}
			}
		}
	}


	private void startLeapfrog() {
		for (Molecule2D mol : molecules) {
			mol.velocity.sAdd(0.5 * config.deltaT, mol.acceleration);
			mol.position.sAdd(config.deltaT, mol.velocity);
		}
	}

	private void completeLeapfrog() {
		for (Molecule2D mol : molecules) {
			mol.velocity.sAdd(0.5 * config.deltaT, mol.acceleration);
		}
	}

	private void applyBoundaryConditions() {
		for (Molecule2D mol : molecules) {
			mol.position.wrap(region);
		}

	}

	public static void main(String[] args) {
		Config config = new Config();

		SoftDiskFluid softDiskFluid = new SoftDiskFluid();
		softDiskFluid.run(config);
	}


}
