package au.com.clearboxsystems.casper.isopointal;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */
import au.com.clearboxsystems.casper.math.Vector3;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: pauls
 * Timestamp: 10/08/2015 11:11 AM
 */
public class WyckoffSite {
	public String code;
	public int numVariables; // Could be 0 - 3;
	public Variable[] variables;
	public Vector3[] positions;
	public Vector3Binding[] bindings;

	public WyckoffSite() {
		code = "e";

		variables = new Variable[1];
		for (int i = 0; i < variables.length; i++)
			variables[i] = new Variable();

		positions = new Vector3[4];
		for (int i = 0; i < positions.length; i++)
			positions[i] = new Vector3();

		bindings = new Vector3Binding[4];
		for (int i = 0; i < bindings.length; i++)
			bindings[i] = new Vector3Binding(positions[i]);
	}

	public void loadBindings() {
		bindings[0].xBinding.value = variables[0];
		bindings[0].yBinding.value = variables[0];
		bindings[0].zBinding.value = variables[0];

		bindings[1].xBinding.value = variables[0];
		bindings[1].xBinding.scale = -1;
		bindings[1].yBinding.value = variables[0];
		bindings[1].yBinding.scale = -1;
		bindings[1].zBinding.value = variables[0];

		bindings[2].xBinding.value = variables[0];
		bindings[2].xBinding.scale = -1;
		bindings[2].yBinding.value = variables[0];
		bindings[2].zBinding.value = variables[0];
		bindings[2].zBinding.scale = -1;

		bindings[3].xBinding.value = variables[0];
		bindings[3].yBinding.value = variables[0];
		bindings[3].yBinding.scale = -1;
		bindings[3].zBinding.value = variables[0];
		bindings[3].zBinding.scale = -1;
	}

	public void updatePositions() {
		for (int i = 0; i < bindings.length; i++)
			bindings[i].apply();
	}

	public void parseConfig(String config) {
	//  "(x,x,x)	(-x,-x,x)	(-x,x,-x)	(x,-x,-x)"
	//  "(x,y,z)	(-y,x-y,z+1/3)	(-x+y,-x,z+2/3)"
		Map<String, Variable> variableMap = new HashMap<>();

		Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
		Matcher matcher = pattern.matcher(config);
		while (matcher.find()) {
			String position = matcher.group(1);
			parsePosition(position, variableMap);
		}
	}

	private void parsePosition(String position, Map<String, Variable> variableMap) {
		String xyz[] = position.split(",");
		if (xyz.length != 3) {
			System.out.println("Parse error: " + position);
			return;
		}

		for (int i = 0; i < 3; i++) {
			xyz[i] = xyz[i].trim();
			double sign = 1;
			if (xyz[i].startsWith("-")) {
				sign = -1;
				xyz[i] = xyz[i].substring(1);
			}

//			if (xyz[i].charAt(0) >= '0' && xy)

		}


	}


}
