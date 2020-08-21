
package com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class HEMUTerminalReqData extends BaseReqData
{
	private String action_type; // 操作类型：1：换机 2：退机 3：申领
	
	private String deposit;//押金
	
	private String terminalId;//终端串号
	
	private String terminalName;//终端名称
	
	private String oldTerminalId;//终端串号
	
	private String terminalType;//终端型号
	
	private String returnDate;//预计归还时间
	
	private String depositTradeId;//BOSS押金转移流水

	private String instId;//实例ID
	
	private String productId;
	
	private String packageId;
	
	private String isHSW;
	/**
	 * @return the action_type
	 */
	public String getAction_type() {
		return action_type;
	}

	/**
	 * @param actionType the action_type to set
	 */
	public void setAction_type(String actionType) {
		action_type = actionType;
	}

	/**
	 * @return the deposit
	 */
	public String getDeposit() {
		return deposit;
	}

	/**
	 * @param deposit the deposit to set
	 */
	public void setDeposit(String deposit) {
		this.deposit = deposit;
	}

	/**
	 * @return the terminalId
	 */
	public String getTerminalId() {
		return terminalId;
	}

	/**
	 * @param terminalId the terminalId to set
	 */
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}

	/**
	 * @return the terminalType
	 */
	public String getTerminalType() {
		return terminalType;
	}

	/**
	 * @param terminalType the terminalType to set
	 */
	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	/**
	 * @return the returnDate
	 */
	public String getReturnDate() {
		return returnDate;
	}

	/**
	 * @param returnDate the returnDate to set
	 */
	public void setReturnDate(String returnDate) {
		this.returnDate = returnDate;
	}

	/**
	 * @return the depositTradeId
	 */
	public String getDepositTradeId() {
		return depositTradeId;
	}

	/**
	 * @param depositTradeId the depositTradeId to set
	 */
	public void setDepositTradeId(String depositTradeId) {
		this.depositTradeId = depositTradeId;
	}

	/**
	 * @return the instId
	 */
	public String getInstId() {
		return instId;
	}

	/**
	 * @param instId the instId to set
	 */
	public void setInstId(String instId) {
		this.instId = instId;
	}

	/**
	 * @return the productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * @param productId the productId to set
	 */
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * @return the packageId
	 */
	public String getPackageId() {
		return packageId;
	}

	/**
	 * @param packageId the packageId to set
	 */
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}

	/**
	 * @return the isHSW
	 */
	public String getIsHSW() {
		return isHSW;
	}

	/**
	 * @param isHSW the isHSW to set
	 */
	public void setIsHSW(String isHSW) {
		this.isHSW = isHSW;
	}

	/**
	 * @return the oldTerminalId
	 */
	public String getOldTerminalId() {
		return oldTerminalId;
	}

	/**
	 * @param oldTerminalId the oldTerminalId to set
	 */
	public void setOldTerminalId(String oldTerminalId) {
		this.oldTerminalId = oldTerminalId;
	}

	/**
	 * @return the terminalName
	 */
	public String getTerminalName() {
		return terminalName;
	}

	/**
	 * @param terminalName the terminalName to set
	 */
	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}
	
	
}
