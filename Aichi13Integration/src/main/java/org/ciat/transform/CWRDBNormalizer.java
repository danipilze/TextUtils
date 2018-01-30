package org.ciat.transform;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import org.ciat.model.DataSourceName;
import org.ciat.model.FileProgressBar;
import org.ciat.model.TaxonFinder;
import org.ciat.model.Utils;

public class CWRDBNormalizer extends Normalizer{

	
	private static final String INPUT_SEPARATOR = "\\|";

	public void process(File input, File output) {

		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(output, true)));
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(new FileInputStream(input), "UTF-8"))) {

			/* header */
			String line = reader.readLine();
			if (colIndex.isEmpty()) {
				colIndex = Utils.getColumnsIndex(line, INPUT_SEPARATOR);
			}
			/* */

			/* progress bar */
			FileProgressBar bar = new FileProgressBar(input.length());
			/* */

			line = reader.readLine();
			String past = "";

			while (line != null) {

				line = line.replace("\"", "");
				String[] values = line.split(INPUT_SEPARATOR);
				String normal = normalize(values);
				if (normal != null && !normal.equals(past)) {
					writer.println(normal);
					past = normal;
				}

				/* show progress */
				bar.update(line.length());
				/* */

				line = reader.readLine();

			}
			bar.finish();

		} catch (FileNotFoundException e) {
			System.out.println("File not found " + input.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String normalize(String[] values) {

		if (values.length == colIndex.size()) {
			if (!values[colIndex.get("final_origin_stat")].equals("introduced")) {
				if (values[colIndex.get("coord_source")].equals("original")
						|| values[colIndex.get("coord_source")].equals("georef")) {
					if (values[colIndex.get("visibility")].equals("1")) {
						if (values[colIndex.get("source")].equals("G") || values[colIndex.get("source")].equals("H")) {

							String date = values[colIndex.get("colldate")];
							if (date.length() > 3) {
								date = date.substring(0, 4);
								if (Utils.isNumeric(date)) {

									int year = Integer.parseInt(date);
									String lon = values[colIndex.get("final_lon")];
									String lat = values[colIndex.get("final_lat")];
									String country = values[colIndex.get("final_iso2")];
									String basis = values[colIndex.get("source")];
									if (year >= Normalizer.YEAR && Utils.isNumeric(lon) && Utils.isNumeric(lat) && country.length() == 2) {

										String taxonKey = TaxonFinder.getInstance().fetchTaxonInfo(values[colIndex.get("taxon_final")]);
										if (taxonKey != null) {
											String result = taxonKey + SEPARATOR + lon + SEPARATOR + lat
													+ SEPARATOR + country + SEPARATOR + basis + SEPARATOR + getDataSourceName();
											return result;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}



	private DataSourceName getDataSourceName() {
		return DataSourceName.CWRDB;
	}

}
