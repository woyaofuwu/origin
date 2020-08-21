
package com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;

public class PcrfTradeData extends BaseTradeData
{

    /**
	 * 
	 */
	private static final long serialVersionUID = 4742256102694227797L;

	private String userId;

    private String instId;
    
    private String relaInstId;

    private String acceptMonth;

    private String modifyTag;

    private String serviceCode;

    private String billingType;

    private String usageState;

    private String startDate;

    private String endDate;

    private String rsrvStr1;

    private String rsrvStr2;

    private String rsrvStr3;

    private String rsrvStr4;

    private String rsrvStr5;

    public PcrfTradeData()
    {

    }

    public PcrfTradeData(IData data) 
    {
        this.userId = data.getString("USER_ID");
        this.instId = data.getString("INST_ID");
        this.relaInstId = data.getString("RELA_INST_ID");
        this.acceptMonth = data.getString("ACCEPT_MONTH");
        this.modifyTag = data.getString("MODIFY_TAG");
        this.serviceCode = data.getString("SERVICE_CODE");
        this.billingType = data.getString("BILLING_TYPE");
        this.usageState = data.getString("USAGE_STATE");
        this.startDate = data.getString("START_DATE");
        this.endDate = data.getString("END_DATE");
        this.rsrvStr1 = data.getString("RSRV_STR1");
        this.rsrvStr2 = data.getString("RSRV_STR2");
        this.rsrvStr3 = data.getString("RSRV_STR3");
        this.rsrvStr4 = data.getString("RSRV_STR4");
        this.rsrvStr5 = data.getString("RSRV_STR5");
    }

    public PcrfTradeData clone()
    {
        PcrfTradeData pcrfTradeData = new PcrfTradeData();
        pcrfTradeData.setUserId(this.getUserId());
        pcrfTradeData.setInstId(this.getInstId());
        pcrfTradeData.setRelaInstId(this.getRelaInstId());
        pcrfTradeData.setAcceptMonth(this.getAcceptMonth());
        pcrfTradeData.setModifyTag(this.getModifyTag());
        pcrfTradeData.setServiceCode(this.getServiceCode());
        pcrfTradeData.setBillingType(this.getBillingType());
        pcrfTradeData.setUsageState(this.getUsageState());
        pcrfTradeData.setStartDate(this.getStartDate());
        pcrfTradeData.setEndDate(this.getEndDate());
        pcrfTradeData.setRsrvStr1(this.getRsrvStr1());
        pcrfTradeData.setRsrvStr2(this.getRsrvStr2());
        pcrfTradeData.setRsrvStr3(this.getRsrvStr3());
        pcrfTradeData.setRsrvStr4(this.getRsrvStr4());
        pcrfTradeData.setRsrvStr5(this.getRsrvStr5());

        return pcrfTradeData;
    }
    
    public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getInstId() {
		return instId;
	}

	public void setInstId(String instId) {
		this.instId = instId;
	}

	public String getRelaInstId() {
		return relaInstId;
	}

	public void setRelaInstId(String relaInstId) {
		this.relaInstId = relaInstId;
	}

	public String getAcceptMonth() {
		return acceptMonth;
	}

	public void setAcceptMonth(String acceptMonth) {
		this.acceptMonth = acceptMonth;
	}

	public String getModifyTag() {
		return modifyTag;
	}

	public void setModifyTag(String modifyTag) {
		this.modifyTag = modifyTag;
	}

	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getBillingType() {
		return billingType;
	}

	public void setBillingType(String billingType) {
		this.billingType = billingType;
	}

	public String getUsageState() {
		return usageState;
	}

	public void setUsageState(String usageState) {
		this.usageState = usageState;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getRsrvStr1() {
		return rsrvStr1;
	}

	public void setRsrvStr1(String rsrvStr1) {
		this.rsrvStr1 = rsrvStr1;
	}

	public String getRsrvStr2() {
		return rsrvStr2;
	}

	public void setRsrvStr2(String rsrvStr2) {
		this.rsrvStr2 = rsrvStr2;
	}

	public String getRsrvStr3() {
		return rsrvStr3;
	}

	public void setRsrvStr3(String rsrvStr3) {
		this.rsrvStr3 = rsrvStr3;
	}

	public String getRsrvStr4() {
		return rsrvStr4;
	}

	public void setRsrvStr4(String rsrvStr4) {
		this.rsrvStr4 = rsrvStr4;
	}

	public String getRsrvStr5() {
		return rsrvStr5;
	}

	public void setRsrvStr5(String rsrvStr5) {
		this.rsrvStr5 = rsrvStr5;
	}

	public IData toData()
    {
        IData data = new DataMap();
        data.put("USER_ID", this.userId);
        data.put("INST_ID", this.instId);
        data.put("RELA_INST_ID", this.relaInstId);
        data.put("ACCEPT_MONTH", this.acceptMonth);
        data.put("SERVICE_CODE", this.serviceCode);
        data.put("BILLING_TYPE", this.billingType);
        data.put("USAGE_STATE", this.usageState);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("RSRV_STR1", this.rsrvStr1);
        data.put("RSRV_STR2", this.rsrvStr2);
        data.put("RSRV_STR3", this.rsrvStr3);
        data.put("RSRV_STR4", this.rsrvStr4);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("MODIFY_TAG", this.modifyTag);

        return data;
    }

    public String toString()
    {
        IData data = new DataMap();
        data.put(getTableName(), this.toData());
        return data.toString();
    }

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "TF_B_TRADE_PCRF";
	}
}
