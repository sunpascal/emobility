package de.unibamberg.eesys.projekt.gps;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.util.Xml;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;

/**
 * 
 * GPX parser used to load GPX files containing location data from the device's SD card. 
 * Loading files can be done from the app settings menu -> "Load GPX file" which will load all files in the top 
 * folder of the sdcard ending with ".gpx".  
 * the class is for testing purposes
 *  
 * @author pascal for reference see
 *         https://developer.android.com/training/basics/network-ops/xml.html //
 *         Todo: handle missing values (e.g. skip entire waypoint)
 */
public class GpxLoader {

	private AppContext appContext;
	private static final String ns = null; // We don't use namespaces

	public class LocationStrings extends Location {

		public LocationStrings(String provider) {
			super(provider);
		}

	}

	public class Extensions {
		public float speed = -1;
		public float accuracy = -1;
		public float course = -1;
	}

	public GpxLoader(AppContext a) {
		appContext = a;
	}

	public List<Location> loadGpx(String filename) {

		InputStream instream = null;
		try {
			instream = appContext.getResources().getAssets().open(filename);

		} catch (java.io.FileNotFoundException e) {
			Log.e("--", "File not found");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Log.e("--", "Unknown exception: " + e.getMessage());
		}

		List<Location> result = null;
		try {
			result = parse(instream);
		} catch (XmlPullParserException e) {
			Log.e("--", "Unknown XmlPullParserException: " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("--", "IOException: " + e.getMessage());
			e.printStackTrace();
		}

		L.d("GPX import: Loaded " + result.size() + " waypoints from "
				+ filename + ".");
		return result;

	}

	public List<Location> parse(InputStream in) throws XmlPullParserException,
			IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readGpx(parser);
		} finally {
			in.close();
		}
	}

	private List<Location> readGpx(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Location> allLocations = new ArrayList();

		parser.require(XmlPullParser.START_TAG, ns, "gpx");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("trk")) {
				allLocations.addAll(readTrk(parser));
			} else {
				skip(parser);
			}
		}
		return allLocations;
	}

	/**
	 * reads the trk
	 * 
	 * @param parser
	 * @return
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private List<Location> readTrk(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List<Location> allLocations = new ArrayList();

		parser.require(XmlPullParser.START_TAG, ns, "trk");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("trkseg")) {
				allLocations.addAll((readTrkseg(parser)));
			} else {
				skip(parser);
			}
		}
		return allLocations;
	}

	private List<Location> readTrkseg(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		List locations = new ArrayList();

		parser.require(XmlPullParser.START_TAG, ns, "trkseg");
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("trkpt")) {
				locations.add(readTrkPt(parser));
			} else {
				skip(parser);
			}
		}
		return locations;
	}

	private Location readTrkPt(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Location newLocation = new LocationStrings(LocationManager.GPS_PROVIDER);

		parser.require(XmlPullParser.START_TAG, ns, "trkpt");
		newLocation.setLatitude(Double.parseDouble(parser.getAttributeValue(
				null, "lat")));
		newLocation.setLongitude(Double.parseDouble(parser.getAttributeValue(
				null, "lon")));

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("ele")) {
				newLocation.setAltitude(Double.parseDouble(readEle(parser)));
			} else if (name.equals("time")) {
				newLocation.setTime(gpxTimeToLong(readTime(parser)));
			} else if (name.equals("extensions")) {
				Extensions ext = readExtensions(parser);
				if (ext.speed != -1)
					newLocation.setSpeed(ext.speed);
				if (ext.accuracy != -1)
					newLocation.setAccuracy(ext.accuracy);
				if (ext.course != -1)
				newLocation.setBearing(ext.course);
			} else {
				skip(parser);
			}
		}
		return newLocation;
	}

	private String readEle(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "ele");
		String ele = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "ele");
		return ele;
	}

	private String readTime(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "time");
		String time = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "time");
		return time;
	}

	/**
	 * reads the content of a tag, if there are to interior tags
	 * 
	 * @param parser
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	private Extensions readExtensions(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		Extensions ext = new Extensions();

		parser.require(XmlPullParser.START_TAG, ns, "extensions");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}

			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("gpx10:speed")) {
				ext.speed = Float.parseFloat(readText(parser));
			} else if (name.equals("ogt10:accuracy")) {
				ext.accuracy = Float.parseFloat(readText(parser));
			} else if (name.equals("gpx10:course")) {
				ext.course = Float.parseFloat(readText(parser));
			} else {
				skip(parser);
			}
		}
		return ext;
	}

	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

	/**
	 * uses joda library to convert a time string found in .gpx (e.g.
	 * "2015-01-05T13:29:48Z") to a long which can given to Location.setTime()
	 * or be used for a java Timestamp
	 * 
	 * @param timestamp
	 *            as it comes from gpx
	 * @return long
	 */
	public long gpxTimeToLong(String timestamp) {
		/*
		 * for some reason it has three trailing zeros and is longer than normal
		 * timestamps, therefore divide by 1000 (to convert timestamps use:
		 * http://www.epochconverter.com/epoch/timezones.php?epoch=1420464588)
		 */
		long millisSinceUnixEpoch = (new DateTime(timestamp).getMillis());

		return millisSinceUnixEpoch;
	}

}
