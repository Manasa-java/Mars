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
				compoundProfile.setLocation(rs.getString("LOCATION"));
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

		if (CollectionUtils.isEmpty(inventoryViews)) {
			return false;
		}

		for (InventoryView inventory : inventoryViews) {
			if (compoundProfile.getOutlineQueueId().equalsIgnoreCase(
					inventory.getRootNumber())) {
				if (StringUtils.hasText(compoundProfile.getLocation())
						&& StringUtils.hasText(inventory.getAliquotSite())
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

		if (CollectionUtils.isEmpty(inventoryViews)) {
			return false;
		}

		for (InventoryView inventory : inventoryViews) {
			if (compoundProfile.getOutlineQueueId().equalsIgnoreCase(
					inventory.getRootNumber())) {
				if (StringUtils.hasText(compoundProfile.getLocation())
						&& StringUtils.hasText(inventory.getAliquotSite())
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
		if (!CollectionUtils.isEmpty(compoundProfiles)) {
			List<CompoundProfile> liquidStack = new ArrayList<CompoundProfile>();
			List<CompoundProfile> powderStack = new ArrayList<CompoundProfile>();

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
						if (StringUtils.hasText(compoundProfile.getLotNumber()))
							exists = checkAliquotSiteForLiquid(compoundProfile);

						if (!exists) {
							// Write to file
							// CompoundProfileWriter.writeToFile(compoundProfile,
							// false);
							liquidStack.add(compoundProfile);
						}

					} else if (StringUtils.hasText(compoundProfile.getVolume())) {
						if (LOG.isDebugEnabled())
							LOG.debug("Checking powder " + compoundProfile);
						// Powder
						boolean exists = false;
						if (StringUtils.hasText(compoundProfile.getLotNumber()))
							exists = checkAliquotSiteForPowder(compoundProfile);

						if (!exists) {
							// Write to file
							// CompoundProfileWriter.writeToFile(compoundProfile,
							// true);
							powderStack.add(compoundProfile);
						}
					}
				}
			}

			CompoundProfileWriter.writeToFile(liquidStack, false);

			CompoundProfileWriter.writeToFile(liquidStack, true);
		}
		long endTime = System.currentTimeMillis();
		LOG.info("End checkAliquotSite .. Time Consumed - "
				+ (endTime - startTime) / 1000 + "s");
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
