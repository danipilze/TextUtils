package org.ciat.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class Utils {
	
	private static Map<String, Locale> localeMap= initCountryCodeMapping();
	
	public static boolean isNumeric(String str) {
		if (str == null) {
			return false;
		}
		try {
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static Map<String, Integer> getColumnsIndex(String line, String separator) {
		Map<String, Integer> colIndex = new LinkedHashMap<String, Integer>();
		String[] columnNames = line.split(separator);
		for (int i = 0; i < columnNames.length; i++) {
			colIndex.put(columnNames[i], i);
		}
		return colIndex;
	}

	public static boolean areValidCoordinates(String decimallatitude, String decimallongitude) {
		if (!isNumeric(decimallatitude)) {
			return false;
		} else {
			Double lat = Double.parseDouble(decimallatitude);
			if (lat == 0 || lat > 90 || lat < -90) {
				return false;
			}
		}
		if (!isNumeric(decimallongitude)) {
			return false;
		} else {
			Double lat = Double.parseDouble(decimallongitude);
			if (lat == 0 || lat > 180 || lat < -180) {
				return false;
			}
		}
		return true;
	}

	private static Map<String, Locale> initCountryCodeMapping() {
		String[] countries = Locale.getISOCountries();
		Map<String, Locale> localeMap = new HashMap<String, Locale>(countries.length);
		for (String country : countries) {
			Locale locale = new Locale("", country);
			localeMap.put(locale.getISO3Country().toUpperCase(), locale);
		}
	return localeMap;
	}

	public static String iso3CountryCodeToIso2CountryCode(String iso3CountryCode) {
		return localeMap.get(iso3CountryCode).getCountry();
	}

	public static String iso2CountryCodeToIso3CountryCode(String iso2CountryCode) {
		Locale locale = new Locale("", iso2CountryCode);
		return locale.getISO3Country();
	}
}
