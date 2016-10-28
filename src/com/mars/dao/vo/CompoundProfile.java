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
		return "CompoundProfile [compoundID=" + Id + ",outline_queue_id="
				+ outline_queue_id + ", location=" + location
				+ ", ConcentrationUnit=" + ConcentrationUnit + ", volume="
				+ volume + ", concentration=" + concentration + "] ";
	}

	public String getoutline_queue_id() {
		return outline_queue_id;
	}

	public void setoutline_queue_id(String outline_queue_id) {
		this.outline_queue_id = outline_queue_id;
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
	private String outline_queue_id;
	private String location;
	private String ConcentrationUnit;
	private String volume;
	private String concentration;
	private String Id;

}
