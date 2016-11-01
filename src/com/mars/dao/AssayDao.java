package com.mars.dao;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.mars.dao.util.CollectionUtils;
import com.mars.dao.util.CompoundProfileWriter;
import com.mars.dao.util.ConnectionUtil;
import com.mars.dao.util.StringUtils;
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

			String query = "select qi.id as ID, qi.lot_number as LOT_NUMBER, qi.outline_queue_id as outline_queue_id, qi.state_id, "
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
				compoundProfile.setOutlineQueueId(rs
						.getString("outline_queue_id"));
				compoundProfile.setConcentration(rs.getString("CONCENTRATION"));
				compoundProfile.setConcentrationUnit(rs.getString("CONC_UNIT"));
				compoundProfile.setId(rs.getString("Id"));

				String location = rs.getString("LOCATION");

				if (StringUtils.hasText(location)) {
					if ("Lake County".equalsIgnoreCase(location))
						location = "LC";
					else if ("LUDWIGSHAFEN".equalsIgnoreCase(location))
						location = "LU";
					else if ("WORCESTER".equalsIgnoreCase(location))
						location = "ABC";
				}
				compoundProfile.setLocation(location);
				compoundProfile.setVolume(rs.getString("VOLUME"));
				compoundProfile.setLotNumber(rs.getString("LOT_NUMBER"));
				compoundProfiles.add(compoundProfile);

			}

			LOG.info("CompoundProfile Size:" + compoundProfiles.size());
			if (LOG.isDebugEnabled()) {
				for (CompoundProfile compoundProfile : compoundProfiles) {
					LOG.debug("CompoundProfile :" + compoundProfile);
				}
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
	private boolean checkAliquotSiteForLiquid(CompoundProfile compoundProfile)
			throws SQLException {
		List<InventoryView> inventoryViews = null;
		Connection ct = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			ct = connectionUtil.createConnection();
			st = ct.createStatement();
			String query = "select ROOT_NUMBER, ALIQUOT_SITE from mars.LIBRA_ALIQUOT_ON_HAND where LOT_NUMBER='"
					+ compoundProfile.getLotNumber() + "'";
			if (LOG.isDebugEnabled())
				LOG.debug("Inventory view Query-" + query);
			rs = st.executeQuery(query);

			inventoryViews = new ArrayList<InventoryView>();
			while (rs.next()) {
				InventoryView inventoryView = new InventoryView();
				inventoryView.setRootNumber(rs.getString("ROOT_NUMBER"));
				inventoryView.setAliquotSite(rs.getString("ALIQUOT_SITE"));
				inventoryViews.add(inventoryView);
			}

			LOG.debug("inventoryViews Liquid Size:" + inventoryViews.size());

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

		if (CollectionUtils.isEmpty(inventoryViews)) {
			return false;
		}

		for (InventoryView inventory : inventoryViews) {
			LOG.info("*********************************************************");
			LOG.info("compoundProfile.getLocation() - "
					+ compoundProfile.getLocation());
			LOG.info("inventory.getAliquotSite() -"
					+ inventory.getAliquotSite());
			LOG.info("*********************************************************");
			if (StringUtils.hasText(compoundProfile.getLocation())
					&& StringUtils.hasText(inventory.getAliquotSite())
					&& compoundProfile.getLocation().equalsIgnoreCase(
							inventory.getAliquotSite())) {
				return true;
			} else {
				// send available location
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
	private boolean checkAliquotSiteForPowder(CompoundProfile compoundProfile)
			throws SQLException {
		List<InventoryView> inventoryViews = null;
		Connection ct = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			ct = connectionUtil.createConnection();
			st = ct.createStatement();
			rs = st.executeQuery("select ROOT_NUMBER, ALIQUOT_SITE from mars.LIBRA_INVENTORY_AMOUNT_ON_HAND where lot_number='"
					+ compoundProfile.getLotNumber() + "'");

			inventoryViews = new ArrayList<InventoryView>();
			while (rs.next()) {
				InventoryView inventoryView = new InventoryView();
				inventoryView.setRootNumber(rs.getString("ROOT_NUMBER"));
				inventoryView.setAliquotSite(rs.getString("ALIQUOT_SITE"));
				inventoryViews.add(inventoryView);
			}

			LOG.debug("inventoryViews Powder Size:" + inventoryViews.size());

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

		if (CollectionUtils.isEmpty(inventoryViews)) {
			return false;
		}

		for (InventoryView inventory : inventoryViews) {
			LOG.debug("*********************************************************");
			LOG.debug("compoundProfile.getLocation() - "
					+ compoundProfile.getLocation());
			LOG.debug("inventory.getAliquotSite() -"
					+ inventory.getAliquotSite());
			LOG.debug("*********************************************************");
			if (StringUtils.hasText(compoundProfile.getLocation())
					&& StringUtils.hasText(inventory.getAliquotSite())
					&& compoundProfile.getLocation().equalsIgnoreCase(
							inventory.getAliquotSite())) {
				return true;
			} else {
				// send available location
			}

		}

		return false;
	}

	/**
	 * 
	 * @param queueItemId
	 * @return
	 * @throws SQLException
	 */
	private String getBestLotNumber(String queueItemId) throws SQLException {
		String bestLotNumber = null;
		Connection ct = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			ct = connectionUtil.createConnection();
			st = ct.createStatement();
			String query = "select BEST_LOT_NUMBER from mars.QUEUE_ITEM_BEST_LOTS where QUEUE_ITEM_ID='"
					+ queueItemId + "'";

			LOG.info("getBestLotNumber Query-" + query);
			rs = st.executeQuery(query);

			while (rs.next()) {

				bestLotNumber = rs.getString("BEST_LOT_NUMBER");

			}

			LOG.debug("BestLotNumber :" + bestLotNumber);

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

		return bestLotNumber;
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
		if (!CollectionUtils.isEmpty(compoundProfiles)) {
			List<CompoundProfile> liquidMissingStack = new ArrayList<CompoundProfile>();
			List<CompoundProfile> powderMissingStack = new ArrayList<CompoundProfile>();
			List<CompoundProfile> liquidPresentStack = new ArrayList<CompoundProfile>();
			List<CompoundProfile> powderPresentStack = new ArrayList<CompoundProfile>();
			List<CompoundProfile> missingVolumeStack = new ArrayList<CompoundProfile>();

			for (CompoundProfile compoundProfile : compoundProfiles) {
				if (compoundProfile != null) {
					if (StringUtils.hasText(compoundProfile.getVolume())
							&& StringUtils.hasText(compoundProfile
									.getConcentration())
							&& StringUtils.hasText(compoundProfile
									.getConcentrationUnit())) {
						if (LOG.isDebugEnabled())
							LOG.debug("Checking liquid" + compoundProfile);
						// Liquid
						boolean exists = false;
						if (StringUtils.hasText(compoundProfile.getLotNumber())) {
							exists = checkAliquotSiteForLiquid(compoundProfile);
						} else {
							// Retrieve the best_lot_number from
							// queue_item_best_lot
							// when queue_item_best_lot.queue_item_id =
							// queue_items.id

							String bestLotNumber = getBestLotNumber(compoundProfile
									.getId());
							if (StringUtils.hasText(bestLotNumber)) {
								compoundProfile.setLotNumber(bestLotNumber);
								exists = checkAliquotSiteForLiquid(compoundProfile);
							}
						}

						if (!exists) {
							// Write to file
							// CompoundProfileWriter.writeToFile(compoundProfile,
							// false);
							liquidMissingStack.add(compoundProfile);
						} else {
							liquidPresentStack.add(compoundProfile);
						}

					} else if (StringUtils.hasText(compoundProfile.getVolume())) {
						if (LOG.isDebugEnabled())
							LOG.debug("Checking powder " + compoundProfile);
						// Powder
						boolean exists = false;
						if (StringUtils.hasText(compoundProfile.getLotNumber())) {
							exists = checkAliquotSiteForPowder(compoundProfile);
						} else {
							// Retrieve the best_lot_number from
							// queue_item_best_lot
							// when queue_item_best_lot.queue_item_id =
							// queue_items.id

							String bestLotNumber = getBestLotNumber(compoundProfile
									.getId());
							if (StringUtils.hasText(bestLotNumber)) {
								compoundProfile.setLotNumber(bestLotNumber);
								exists = checkAliquotSiteForPowder(compoundProfile);
							}
						}

						if (!exists) {
							// Write to file
							// CompoundProfileWriter.writeToFile(compoundProfile,
							// true);
							powderMissingStack.add(compoundProfile);
						} else {
							powderPresentStack.add(compoundProfile);
						}
					} else {
						missingVolumeStack.add(compoundProfile);
					}
				}
			}

			LOG.info("LiquidMissing - " + liquidMissingStack.size());
			LOG.info("PowderMissing - " + powderMissingStack.size());
			LOG.info("LiquidMatching - " + liquidPresentStack.size());
			LOG.info("PowderMatching - " + powderPresentStack.size());
			LOG.info("Volume Null - " + missingVolumeStack.size());

			CompoundProfileWriter.writeToFile(liquidMissingStack,
					"LiquidCompoundUnmatchedLocation.txt");

			CompoundProfileWriter.writeToFile(powderMissingStack,
					"PowderCompoundUnmatchedLocation.txt");

			CompoundProfileWriter.writeToFile(liquidPresentStack,
					"LiquidCompoundMatchedLocation.txt");

			CompoundProfileWriter.writeToFile(powderPresentStack,
					"PowderCompoundMatchedLocation.txt");
			CompoundProfileWriter.writeToFile(missingVolumeStack,
					"MissingVolumeCompounds.txt");
		}
		long endTime = System.currentTimeMillis();
		LOG.info("End checkAliquotSite .. Time Consumed - "
				+ (endTime - startTime) / 1000 + "s");
	}

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
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
