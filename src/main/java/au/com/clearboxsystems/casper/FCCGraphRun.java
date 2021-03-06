package au.com.clearboxsystems.casper;

import au.com.clearboxsystems.casper.isopointal.IsopointalSetResult;
import au.com.clearboxsystems.casper.isopointal.SimulatedAnneal;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;

/**
 * Created by pauls on 16/09/15.
 */
public class FCCGraphRun {


	public static void main(String[] args) {

		XYSeries energySeries = new XYSeries("FCC Energy");

		SimulatedAnneal simAnneal = new SimulatedAnneal();

		for (double A = 0; A <= 1.5; A+=0.1) {
			System.out.println("A: " + A);
			IsopointalSetResult energy = simAnneal.findMinimumEnergy(8, 100000, 225, new String[]{"a"}, A, 4, 12);
			energySeries.add(A, energy.energyPerAtom);
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(energySeries);

		JFreeChart chart = ChartFactory.createXYLineChart(
				"Effect of varying A on FCC energy",
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
