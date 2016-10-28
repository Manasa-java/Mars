/**
 * 
 */
package com.mars.dao.vo;

import java.io.Serializable;

/**
 * @author PATTLMX
 *
 */
public class InventoryView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String rootNumber;

	public String getRootNumber() {
		return rootNumber;
	}
	@Override
	public String toString() {
		return "InventoryView [rootNumber=" + rootNumber + ", aliquotSite="
				+ aliquotSite + "]";
	}
	public void setRootNumber(String rootNumber) {
		this.rootNumber = rootNumber;
	}
	public String getAliquotSite() {
		return aliquotSite;
	}
	public void setAliquotSite(String aliquotSite) {
		this.aliquotSite = aliquotSite;
	}
	private String aliquotSite;

}
