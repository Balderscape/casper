package au.com.clearboxsystems.casper.isopointal;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by pauls on 14/08/15.
 */
public class SpaceGroupFactory {

	private Map<Integer, SpaceGroup> spaceGroupCache = new HashMap<>();
	private ObjectMapper om = new ObjectMapper();

	public SpaceGroup getSpaceGroup(int number) {
		if (!spaceGroupCache.containsKey(number)) {
			try {
				File file = new File("data/sg/sg" + number + ".json");
				SpaceGroup sg = om.readValue(file, SpaceGroup.class);
				spaceGroupCache.put(number, sg);
			} catch (IOException e) {
				return null;
			}
		}

		return spaceGroupCache.get(number);
	}
}
