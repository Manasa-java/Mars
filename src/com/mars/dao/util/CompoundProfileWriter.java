/**
 * 
 */
package com.mars.dao.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import com.mars.dao.vo.CompoundProfile;

/**
 * @author PATTLMX
 *
 */
public class CompoundProfileWriter {

	public static final Logger LOG = Logger
			.getLogger(CompoundProfileWriter.class);
	/**
	 * 
	 * @param profile
	 * @param isPowder
	 */
	public static void writeToFile(CompoundProfile profile, String fileName) {
		try {
			LOG.info("File Name " + fileName);

			String content = profile.toString();

			LOG.info("Content  " + content);
			File file = new File(fileName);

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.write('\n');
			bw.close();

			LOG.info("Done");

		} catch (IOException e) {
			LOG.error("Error : ", e);
		}
	}

	/**
	 * 
	 * @param profile
	 * @param isPowder
	 */
	public static void writeToFile(List<CompoundProfile> profiles,
			String fileName) {
		try {
			LOG.info("File Name " + fileName);

			File file = new File(fileName);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			long startTime = System.currentTimeMillis();
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			for (CompoundProfile profile : profiles) {
				bw.write(profile.toString());
				bw.write('\n');
			}

			long endTime = System.currentTimeMillis();
			bw.close();

			LOG.info("Batch Write Done in - " + (endTime - startTime) + "ms");

		} catch (IOException e) {
			LOG.error("Error : ", e);
		}
	}
}
