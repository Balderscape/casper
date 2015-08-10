package au.com.clearboxsystems.casper.isopointal;

import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.testng.Assert.*;

/**
 * Created by pauls on 10/08/15.
 */
public class WyckoffSiteTest {

	@Test
	public void testRegex() throws Exception {
		String ex = "(x,x,x)	(-x,-x,x)	(-x,x,-x)	(x,-x,-x)";

		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(ex);
		while(m.find())
			System.out.println(m.group(1));


	}
}