package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;

public class CreateLineGroupMemberReqData extends CreateGroupMemberReqData{

	private IData ACCT_INFO;// 账户信息

    private String acctId;// 账户标识

    // 三户资料块区
    private boolean acctIsAdd;// 账户判断标识:账户是否新增,true为新增,false为取已有的

    private IData ACCT_CONSIGN;// 托收

    private String netTypeCode;
    
    private String IF_JKDT;//是否是集客大厅传来的工单
    
    private String firstTimeNextAcct = "";
    
    private int pfWait;
    
    private IData interData;

    private IData zjData;

    private IDataset attrData;

    private IDataset commonData;

    private IData dataline;
    
    private String startdate;
    
    private IDataset zjDataset;

    public int getPfWait() {
		return pfWait;
	}

	public void setPfWait(int pfWait) {
		this.pfWait = pfWait;
	}

	public IData getACCT_INFO() {
		return ACCT_INFO;
	}

	public void setACCT_INFO(IData ACCT_INFO) {
		this.ACCT_INFO = ACCT_INFO;
	}

	public String getAcctId() {
		return acctId;
	}

	public void setAcctId(String acctId) {
		this.acctId = acctId;
	}

	public boolean isAcctIsAdd() {
		return acctIsAdd;
	}

	public void setAcctIsAdd(boolean acctIsAdd) {
		this.acctIsAdd = acctIsAdd;
	}

	public IData getACCT_CONSIGN() {
		return ACCT_CONSIGN;
	}

	public void setACCT_CONSIGN(IData ACCT_CONSIGN) {
		this.ACCT_CONSIGN = ACCT_CONSIGN;
	}

	public String getNetTypeCode() {
		return netTypeCode;
	}

	public void setNetTypeCode(String netTypeCode) {
		this.netTypeCode = netTypeCode;
	}

	public String getIF_JKDT() {
		return IF_JKDT;
	}

	public void setIF_JKDT(String IF_JKDT) {
		this.IF_JKDT = IF_JKDT;
	}

	public String getFirstTimeNextAcct() {
		return firstTimeNextAcct;
	}

	public void setFirstTimeNextAcct(String firstTimeNextAcct) {
		this.firstTimeNextAcct = firstTimeNextAcct;
	}
	  public IDataset getCommonData()
    {
        return commonData;
    }

    public void setCommonData(IDataset commonData)
    {
        this.commonData = commonData;
    }

    public IData getDataline()
    {
        return dataline;
    }

    public void setDataline(IData dataline)
    {
        this.dataline = dataline;
    }

    public IDataset getAttrData()
    {
        return attrData;
    }

    public void setAttrData(IDataset attrData)
    {
        this.attrData = attrData;
    }

    public IData getInterData()
    {
        return interData;
    }

    public void setInterData(IData interData)
    {
        this.interData = interData;
    }

    public IData getZjData()
    {
        return zjData;
    }

    public void setZjData(IData zjData)
    {
        this.zjData = zjData;
    }
    
    public String getStartDate() {
		return startdate;
	}

	public void setStartDate(String startdate) {
		this.startdate = startdate;
	}

	public IDataset getZjDataset() {
		return zjDataset;
	}

	public void setZjDataset(IDataset zjDataset) {
		this.zjDataset = zjDataset;
	}
    
	

}
