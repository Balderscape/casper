package au.com.clearboxsystems.casper.isopointal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pauls on 10/08/15.
 */
public class ScrapeSpaceGroups {

	// 144

	Pattern positionPattern = Pattern.compile("\\(([^)]+)\\)");

	@Test
	public void testFetch() throws Exception {
		SpaceGroup sg15 = fetchSpaceGroup(15);
		ObjectMapper om = new ObjectMapper();
		System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(sg15));
	}

	@Test
	public void loadAllSpaceGroups() throws Exception {
		ObjectMapper om = new ObjectMapper();
		for (int i = 43; i <= 230; i++) {
			SpaceGroup sg = fetchSpaceGroup(i);
			File file = new File("data/sg/sg" + i + ".json");
			om.writerWithDefaultPrettyPrinter().writeValue(file, sg);
		}
	}

	public SpaceGroup fetchSpaceGroup(int number) throws Exception {
		Document doc = Jsoup.connect("http://www.cryst.ehu.es/cgi-bin/cryst/programs/nph-wp-list?gnum=" + number).get();

		String additionalPositions = doc.select("table tbody tr:has(th) + tr td").get(0).text();

		SpaceGroup sg = new SpaceGroup();
		sg.number = number;
		sg.crystalSystem = CrystalSystem.fromSpaceGroup(number);
		sg.additionalPositionsShortForm = additionalPositions;

		Elements sites = doc.select("table tbody tr:has(table)");
		for (Element site : sites) {
			Elements fields = site.select("> td");
			if (fields.size() != 4)
				continue;

			int multiplicity = Integer.parseInt(fields.get(0).text().trim());
			String wyckoffLetter = fields.get(1).text().trim();
			String positions = fields.get(3).text();

			System.out.println("mul: " + multiplicity + ", letter: " + wyckoffLetter + " - " + positions);
			WyckoffSite wyckoffSite = parseWyckoffSite(wyckoffLetter, multiplicity, positions, additionalPositions);
			sg.wyckoffSites.add(wyckoffSite);
		}

		return sg;
	}

	private WyckoffSite parseWyckoffSite(String letter, int multiplicity, String positions, String additionalPositions) {
		WyckoffSite site = new WyckoffSite();
		site.code = letter;
		site.positionsShortForm = positions;

		Matcher positionMatcher = positionPattern.matcher(positions);
		while (positionMatcher.find()) {
			String position = positionMatcher.group(1);
			WyckoffPosition wyckoffPosition = parseWyckoffPosition(position);
			site.positions.add(wyckoffPosition);
		}

		if (additionalPositions.trim().length() != 0) {
			List<WyckoffPosition> addPos = new ArrayList<>();
			Matcher additionalPositionMatcher = positionPattern.matcher(additionalPositions);
			while (additionalPositionMatcher.find()) {
				String position = additionalPositionMatcher.group(1);
				WyckoffPosition wyckoffPosition = parseWyckoffPosition(position);
				addPos.add(wyckoffPosition);
			}
			int numSites = site.positions.size();
			for (int i = 0; i < numSites; i++) {
				WyckoffPosition orig = site.positions.get(i);
				WyckoffPosition add = addPos.get(0);
				orig.xTransform.offset += add.xTransform.offset;
				orig.yTransform.offset += add.yTransform.offset;
				orig.zTransform.offset += add.zTransform.offset;
				for (int j = 1; j < addPos.size(); j++) {
					add = addPos.get(j);
					WyckoffPosition newPos = new WyckoffPosition();
					newPos.set(orig);
					newPos.xTransform.offset += add.xTransform.offset;
					newPos.yTransform.offset += add.yTransform.offset;
					newPos.zTransform.offset += add.zTransform.offset;
					site.positions.add(newPos);
				}
			}
		}

		if (site.positions.size() != multiplicity) {
			System.out.println("Parse Error: multiplicity mismatch!");
			throw new RuntimeException("Parse Error: multiplicity mismatch!");
		}
		return site;
	}

	private WyckoffPosition parseWyckoffPosition(String position) {
		WyckoffPosition wyckoffPosition = new WyckoffPosition();

		String xyz[] = position.split(",");
		parseTransform(wyckoffPosition.xTransform, xyz[0]);
		parseTransform(wyckoffPosition.yTransform, xyz[1]);
		parseTransform(wyckoffPosition.zTransform, xyz[2]);

		return wyckoffPosition;

	}

	private void parseTransform(WyckoffTransform wyckoffTransform, String transform) {
		if (transform.length() == 0)
			return;

		int idx = 0;
		double scale = 1;

		char nextToken = transform.charAt(idx++);
		if (nextToken == '-') {
			scale = -1;
			nextToken = transform.charAt(idx++);
		} else if (nextToken == '+') {
			scale = 1;
			nextToken = transform.charAt(idx++);
		}

		if (nextToken == 'x') {
			wyckoffTransform.xScale = scale;
			parseTransform(wyckoffTransform, transform.substring(idx));
			return;
		}

		if (nextToken == 'y') {
			wyckoffTransform.yScale = scale;
			parseTransform(wyckoffTransform, transform.substring(idx));
			return;
		}

		if (nextToken == 'z') {
			wyckoffTransform.zScale = scale;
			parseTransform(wyckoffTransform, transform.substring(idx));
			return;
		}

		if (isNumber(nextToken)) {
			double numerator = (nextToken - '0');
			double denominator = 1;

			if (idx < transform.length()) {
				nextToken = transform.charAt(idx++);
				while (isNumber(nextToken)) {
					numerator *= 10;
					numerator += (nextToken - '0');
					if (idx >= transform.length())
						break;
					nextToken = transform.charAt(idx++);
				}
				if (nextToken == '/') {
					denominator = 0;
					if (idx >= transform.length()) {
						System.out.println("Parse error: " + transform);
						throw new RuntimeException();
					}
					nextToken = transform.charAt(idx++);
					while (isNumber(nextToken)) {
						denominator *= 10;
						denominator += (nextToken - '0');
						if (idx >= transform.length())
							break;
						nextToken = transform.charAt(idx++);
					}
					if (!isNumber(nextToken))
						idx--;

				} else {
					idx--;
				}
			}
			wyckoffTransform.offset = scale * numerator / denominator;
			parseTransform(wyckoffTransform, transform.substring(idx));
		}

	}

	private boolean isNumber(char c) {
		return (c >= '0' && c <= '9');
	}

}
