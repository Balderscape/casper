package au.com.clearboxsystems.casper.isopointal;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.annotations.Test;

/**
 * Created by pauls on 10/08/15.
 */
public class ScrapeSpaceGroups {

	// 144

	@Test
	public void testName() throws Exception {
		Document doc = Jsoup.connect("http://www.cryst.ehu.es/cgi-bin/cryst/programs/nph-wp-list?gnum=" + 29).get();

		String additionalPositions = doc.select("table tbody tr:has(th) + tr td").get(0).text();
		System.out.println("Group: " + additionalPositions);

		Elements sites = doc.select("table tbody tr:has(table)");
		for (Element site : sites) {
			Elements fields = site.select("> td");
			if (fields.size() != 4)
				continue;

			int multiplicity = Integer.parseInt(fields.get(0).text().trim());
			String wyckoffLetter = fields.get(1).text().trim();
			String positions = fields.get(3).text();
			System.out.println("mul: " + multiplicity + ", letter: " + wyckoffLetter + " - " + positions);
		}
	}


}
