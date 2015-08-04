package au.com.clearboxsystems.casper.md;
/**
 * Copyright (C) 2015 Clearbox Systems Pty Ltd
 * http://www.clearboxsystems.com.au
 */

import au.com.clearboxsystems.casper.gl.shape.Line;
import au.com.clearboxsystems.casper.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * User: pauls
 * Timestamp: 4/08/2015 10:04 AM
 */
public class Molecule2DPath {

	public static class PathSegment extends Line {
	}

	private int maxPathLength = 100;
	public LinkedList<PathSegment> pathSegments = new LinkedList<>();

	public Vector3 lastPoint;

	public Molecule2DPath(Molecule2D startingPoint) {
		this.lastPoint = startingPoint.getPosition();
	}

	public void addPoint(Molecule2D point) {
		PathSegment segment = new PathSegment();
		segment.startPosition = lastPoint;

		lastPoint = point.getPosition();
		segment.stopPosition = lastPoint;
		if (pathSegments.size() == maxPathLength)
			pathSegments.removeFirst();

		pathSegments.addLast(segment);
	}


	public List<Line> getLines() {
		List<Line> lines = new ArrayList<>();
		for (Line line : pathSegments) {
			lines.add(line);
		}
		return lines;
	}

}
