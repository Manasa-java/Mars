/**
 * 
 */
package com.mars.dao.vo;

import java.io.Serializable;

/**
 * @author PATTLMX
 *
 */
public class CompoundProfile implements Serializable {

	@Override
	public String toString() {
		return "CompoundProfile [outline_queue_id=" + outlineQueueId
				+ ", location=" + location + ", ConcentrationUnit="
				+ ConcentrationUnit + ", volume=" + volume + ", concentration="
				+ concentration + ", Id=" + Id + ", lotNumber=" + lotNumber
				+ "]";
	}

	public String getOutlineQueueId() {
		return outlineQueueId;
	}

	public void setOutlineQueueId(String outlineQueueId) {
		this.outlineQueueId = outlineQueueId;
	}
	public String getId() {
		return Id;
	}

	public void setId(String id) {
		Id = id;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getConcentrationUnit() {
		return ConcentrationUnit;
	}

	public void setConcentrationUnit(String concentrationUnit) {
		ConcentrationUnit = concentrationUnit;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getConcentration() {
		return concentration;
	}

	public void setConcentration(String concentration) {
		this.concentration = concentration;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String outlineQueueId;
	private String location;
	private String ConcentrationUnit;
	private String volume;
	private String concentration;
	private String Id;
	private String lotNumber;
	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

}
