package com.mars.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.mars.dao.vo.CompoundProfile;
import com.mars.dao.vo.InventoryView;

public class AssayDao {

	public static final Logger LOG = Logger.getLogger(AssayDao.class);

	public static final String STATE_ID = "2";
	public static final String STOCK_ON_HAND = "0";
	public static final String COMPLETE_TRANSFER_STATUS = "COMPLETE";

	private ConnectionUtil connectionUtil;

	/**
	 * Default Constructor
	 */
	public AssayDao() {

		if (connectionUtil == null)

			connectionUtil = ConnectionUtil.getInstance();

	}

	/**
	 * 
	 * @param compounds
	 * @return List<CompoundProfile>
	 * @throws SQLException
	 */
	private List<CompoundProfile> getCompoundProfile() throws SQLException {
		List<CompoundProfile> compoundProfiles = null;
		Connection ct = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			ct = connectionUtil.createConnection();
			st = ct.createStatement();

			String query = "select qi.id as ID, qi.outline_queue_id as outline_queue_id, qi.state_id, "
					+ "qit.Transfer_status, o.CONCENTRATION as CONCENTRATION, "
					+ " o.CONC_UNIT as CONC_UNIT, o.LOCATION as LOCATION, "
					+ " o.VOLUME as VOLUME from mars.queue_items qi, "
					+ "mars.libra_order_profiles o, mars.queue_item_transfers qit "
					+ "where qi.STATE_ID=2 and qi.STOCK_ON_HAND=0 "
					+ "and qi.outline_queue_id=o.outline_queue_id "
					+ "and qi.id=qit.queue_item_id (+) "
					+ "and (qit.Transfer_status='COMPLETE' or "
					+ "qit.TRANSFER_STATUS IS NULL)";

			LOG.info("Query : " + query);

			rs = st.executeQuery(query);

			compoundProfiles = new ArrayList<CompoundProfile>();

			while (rs.next()) {
				CompoundProfile compoundProfile = new CompoundProfile();
				compoundProfile.setoutline_queue_id(rs
						.getString("outline_queue_id"));
				compoundProfile.setConcentration(rs.getString("CONCENTRATION"));
				compoundProfile.setConcentrationUnit(rs.getString("CONC_UNIT"));
				compoundProfile.setId(rs.getString("Id"));
				compoundProfile.setLocation(rs.getString("LOCATION"));
				compoundProfile.setVolume(rs.getString("VOLUME"));

				compoundProfiles.add(compoundProfile);

			}

			LOG.info("CompoundProfile Size:" + compoundProfiles.size());
			for (CompoundProfile compoundProfile : compoundProfiles) {
				LOG.info("CompoundProfile :" + compoundProfile);
			}

		} catch (ClassNotFoundException e) {
			LOG.error("Error : ", e);
		} catch (FileNotFoundException e) {
			LOG.error("Error : ", e);
		} catch (SQLException e) {
			LOG.error("Error : ", e);
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (ct != null)
				ct.close();
		}
		return compoundProfiles;
	}

	/**
	 * 
	 * @param compoundProfile
	 * @param liquidInventoryViews
	 * @return
	 * @throws SQLException
	 */
	public boolean checkAliquotSiteForLiquid(CompoundProfile compoundProfile)
			throws SQLException {
		List<InventoryView> inventoryViews = null;
		Connection ct = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			ct = connectionUtil.createConnection();
			st = ct.createStatement();
			String query = "select ROOT_NUMBER, ALIQUOT_SITE from mars.LIBRA_ALIQUOT_ON_HAND where ROOT_NUMBER='"
					+ compoundProfile.getoutline_queue_id() + "'";

			LOG.info("Inventory view Query-" + query);
			rs = st.executeQuery(query);

			inventoryViews = new ArrayList<InventoryView>();
			while (rs.next()) {
				InventoryView inventoryView = new InventoryView();
				inventoryView.setRootNumber(rs.getString("ROOT_NUMBER"));
				inventoryView.setAliquotSite(rs.getString("ALIQUOT_SITE"));
				inventoryViews.add(inventoryView);
			}

			LOG.info("inventoryViews Liquid Size:" + inventoryViews.size());

		} catch (ClassNotFoundException e) {
			LOG.error("Error : ", e);
		} catch (FileNotFoundException e) {
			LOG.error("Error : ", e);
		} catch (SQLException e) {
			LOG.error("Error : ", e);
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (ct != null)
				ct.close();
		}

		if (isEmpty(inventoryViews)) {
			return false;
		}

		for (InventoryView inventory : inventoryViews) {
			if (compoundProfile.getoutline_queue_id().equalsIgnoreCase(
					inventory.getRootNumber())) {
				if (hasText(compoundProfile.getLocation())
						&& hasText(inventory.getAliquotSite())
						&& compoundProfile.getLocation().equalsIgnoreCase(
								inventory.getAliquotSite())) {
					return true;
				} else {
					// send available location
				}
			}
		}

		return false;
	}
	/**
	 * 
	 * @param compoundProfile
	 * @param powderInventoryViews
	 * @return
	 * @throws SQLException
	 */
	public boolean checkAliquotSiteForPowder(CompoundProfile compoundProfile)
			throws SQLException {
		List<InventoryView> inventoryViews = null;
		Connection ct = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			ct = connectionUtil.createConnection();
			st = ct.createStatement();
			rs = st.executeQuery("select ROOT_NUMBER, ALIQUOT_SITE from mars.LIBRA_INVENTORY_AMOUNT_ON_HAND where Root_number='"
					+ compoundProfile.getoutline_queue_id() + "'");

			inventoryViews = new ArrayList<InventoryView>();
			while (rs.next()) {
				InventoryView inventoryView = new InventoryView();
				inventoryView.setRootNumber(rs.getString("ROOT_NUMBER"));
				inventoryView.setAliquotSite(rs.getString("ALIQUOT_SITE"));
				inventoryViews.add(inventoryView);
			}

			LOG.info("inventoryViews Powder Size:" + inventoryViews.size());

		} catch (ClassNotFoundException e) {
			LOG.error("Error : ", e);
		} catch (FileNotFoundException e) {
			LOG.error("Error : ", e);
		} catch (SQLException e) {
			LOG.error("Error : ", e);
		} finally {
			if (rs != null)
				rs.close();
			if (st != null)
				st.close();
			if (ct != null)
				ct.close();
		}

		if (isEmpty(inventoryViews)) {
			return false;
		}

		for (InventoryView inventory : inventoryViews) {
			if (compoundProfile.getoutline_queue_id().equalsIgnoreCase(
					inventory.getRootNumber())) {
				if (hasText(compoundProfile.getLocation())
						&& hasText(inventory.getAliquotSite())
						&& compoundProfile.getLocation().equalsIgnoreCase(
								inventory.getAliquotSite())) {
					return true;
				} else {
					// send available location
				}
			}
		}

		return false;
	}

	/**
	 * checking aliquot site
	 * 
	 * @throws SQLException
	 */
	public void checkAliquotSite() throws SQLException {

		LOG.info("Start checkAliquotSite");
		long startTime = System.currentTimeMillis();
		List<CompoundProfile> compoundProfiles = getCompoundProfile();
		if (!isEmpty(compoundProfiles)) {
			for (CompoundProfile compoundProfile : compoundProfiles) {
				if (compoundProfile != null) {
					if (hasText(compoundProfile.getVolume())
							&& hasText(compoundProfile.getConcentration())
							&& hasText(compoundProfile.getConcentrationUnit())) {
						LOG.info("Checking liquid" + compoundProfile);
						// Liquid
						boolean exists = checkAliquotSiteForLiquid(compoundProfile);
						if (!exists) {
							// Write to file
							writeToFile(compoundProfile, false);
						}

					} else if (hasText(compoundProfile.getVolume())) {
						System.out
								.println("Checking powder " + compoundProfile);
						// Powder
						boolean exists = checkAliquotSiteForPowder(compoundProfile);

						if (!exists) {
							// Write to file
							writeToFile(compoundProfile, true);
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();
		LOG.info("End checkAliquotSite .. Time Consumed - "
				+ (endTime - startTime) / 1000 + "s");
	}

	/**
	 * 
	 * @param profile
	 * @param isPowder
	 */
	private void writeToFile(CompoundProfile profile, boolean isPowder) {
		try {
			String fileName;

			if (isPowder)
				fileName = "PowderCompoundUnmatchedLocation.txt";
			else
				fileName = "LiquidCompoundUnmatchedLocation.txt";

			LOG.info("File Name " + fileName);

			String content = profile.toString();

			LOG.info("Content  " + content);
			File file = new File(fileName);

			// if file doesnt exists, then create it
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
	 * @param str
	 * @return
	 */
	public static boolean hasLength(CharSequence str) {
		return (str != null && str.length() > 0);
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public static boolean hasText(CharSequence str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null || collection.isEmpty());
	}

	public static void main(String args[]) throws Exception {
		/**
		 * 1. Get the Compound using id, stateId, stockOnHand and transferStatus
		 * 2. get the Compound Info from libra profile using root Number 3.
		 * check Inventory for location match if Liquid call - >
		 * LIBRA_ALIQUOT_ON_HAND and match location else call ->
		 * LIBRA_INVENTORY_AMOUNT_ON_HAND and match location 4. if match ->
		 * don't print it.. else print it.
		 **/

		AssayDao dao = new AssayDao();
		dao.checkAliquotSite();

	}

}
