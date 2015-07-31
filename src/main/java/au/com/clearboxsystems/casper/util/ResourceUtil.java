package au.com.clearboxsystems.casper.util;
/**
 * Copyright (C) 2014 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * User: pauls
 * Timestamp: 6/01/14 1:48 PM
 */
public class ResourceUtil {

	public static String stringFromResource(String resourceName) throws IOException {
		try (InputStream is = ResourceUtil.class.getResourceAsStream(resourceName)) {
			return stringFromInputStream(is);
		}
	}

	public static String stringFromInputStream(InputStream stream) {
		Scanner s = new Scanner(stream).useDelimiter("\\A");
		return s.hasNext() ? s.next() : "";
	}
}
