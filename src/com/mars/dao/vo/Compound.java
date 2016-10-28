/**
 * 
 */
package com.mars.dao.vo;

import java.io.Serializable;

/**
 * @author PATTLMX
 *
 */
public class Compound implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String rootNumber;

	@Override
	public String toString() {
		return "Compound [rootNumber=" + rootNumber + ", lotNumber="
				+ lotNumber + ", saltExtension=" + saltExtension + "]";
	}

	public String getRootNumber() {
		return rootNumber;
	}

	public void setRootNumber(String rootNumber) {
		this.rootNumber = rootNumber;
	}

	public String getLotNumber() {
		return lotNumber;
	}

	public void setLotNumber(String lotNumber) {
		this.lotNumber = lotNumber;
	}

	public String getSaltExtension() {
		return saltExtension;
	}

	public void setSaltExtension(String saltExtension) {
		this.saltExtension = saltExtension;
	}

	private String lotNumber;
	private String saltExtension;

}
