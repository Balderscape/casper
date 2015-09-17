package au.com.clearboxsystems.casper;

import au.com.clearboxsystems.casper.isopointal.SimulatedAnneal;
import jdk.nashorn.internal.codegen.types.Range;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by pauls on 16/09/15.
 */
public class BCCGraphRun {


	public static void main(String[] args) {

		XYSeries FCCenergySeries = new XYSeries("FCC Energy");
		XYSeries BCCenergySeries = new XYSeries("BCC Energy");

		SimulatedAnneal simAnneal = new SimulatedAnneal();

		List<Double> x = new ArrayList<>(16);
		for (int i = 0; i <= 15; i++)
			x.add(i / 10.0);

		x.parallelStream().forEach((A) -> {
			System.out.println("A: " + A);
			double energy = simAnneal.findMinimumEnergy(4, 500000, 225, new String[]{"a"}, 12, A, 2);
			FCCenergySeries.add((double)A, energy);
		});

		x.parallelStream().forEach((A) -> {
			System.out.println("A: " + A);
			double energy = simAnneal.findMinimumEnergy(4, 500000, 229, new String[]{"a"}, 12, A, 2);
			BCCenergySeries.add((double)A, energy);
		});

//
//
//		for (double A = 0; A <= 1.5; A+=0.1) {
//			System.out.println("A: " + A);
//			double energy = simAnneal.findMinimumEnergy(2, 100000, 225, new String[]{"a"}, 12, A, 4);
//			FCCenergySeries.add(A, energy);
//		}
//
//
//		for (double A = 0; A <= 1.5; A+=0.1) {
//			System.out.println("A: " + A);
//			double energy = simAnneal.findMinimumEnergy(2, 100000, 229, new String[]{"a"}, 12, A, 4);
//			BCCenergySeries.add(A, energy);
//		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(FCCenergySeries);
		dataset.addSeries(BCCenergySeries);

		JFreeChart chart = ChartFactory.createXYLineChart(
				"Effect of varying A on energy",
				"A",
				"Energy",
				dataset,
				PlotOrientation.VERTICAL,
				false,
				true,
				false
		);

		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));

		JFrame frame = new JFrame();
		frame.setContentPane(chartPanel);
		frame.pack();
		RefineryUtilities.centerFrameOnScreen(frame);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
