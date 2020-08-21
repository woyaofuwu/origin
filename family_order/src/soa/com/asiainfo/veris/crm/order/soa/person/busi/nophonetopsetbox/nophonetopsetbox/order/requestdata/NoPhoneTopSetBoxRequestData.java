package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetbox.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * 
 * @author zhengkai
 *
 */
public class NoPhoneTopSetBoxRequestData extends BaseReqData
{
    private String SerialNumber; // 受理号码(147手机号码)：记录信息统一用原来的USER_ID,不用KD_的USER_ID;

    private String userAction; // 0:购买 1：换机

    private String oldResNo; // 旧终端号

    private String oldProductId;

    private String oldBasePkgs; // 格式：PLATSVCID,PRODUCTID老的基础优惠平台服务ID:40188070,19091900

    private String oldOptionPkgs;

    private String resNo;

    private String resTypeCode;

    private String resBrandCode;

    private String resBrandName;

    private String resKindCode;

    private String resKindName;

    private String resStateCode; // 1-空闲 4-已销售

    private String resStateName;

    private String supplyId;

    private String resFee;

    private String productId;

    private String basePkgs; // 基础服务ID，不带逗号

    private String optionPkgs;

    private String artificalSericesTag; // 0:不上门 1：上门

    private String wideAddr;

    private String remarks;

    private String wideTradeId; // 宽带未完工工单

    private String wideState; // 宽带状态
    
    //宽带号码
    private String serialNumberB;
    
    //魔百和受理时长
    private String topSetBoxTime;
    
    //魔百和受理时长费用
    private String topSetBoxFee;

    //魔百和结束时间
    private String topSetBoxEndDate;
    
    /**
     * 是否是宽带融合开户生成魔百和开户订单
     */
    private String isMergeWideUserCreate;
    
    private String workStaffId;//施工人员工号
    private String workDepatrId;//施工人员部门编码
    private String workCityCode;//施工人员业务区
    public String getWorkStaffId() {
		return workStaffId;
	}

	public void setWorkStaffId(String workStaffId) {
		this.workStaffId = workStaffId;
	}

	public String getWorkDepatrId() {
		return workDepatrId;
	}

	public void setWorkDepatrId(String workDepatrId) {
		this.workDepatrId = workDepatrId;
	}

	public String getWorkCityCode() {
		return workCityCode;
	}

	public void setWorkCityCode(String workCityCode) {
		this.workCityCode = workCityCode;
	}

	public String getArtificalSericesTag()
    {
        return artificalSericesTag;
    }

    public String getBasePkgs()
    {
        return basePkgs;
    }

    public String getOldBasePkgs()
    {
        return oldBasePkgs;
    }

    public String getOldOptionPkgs()
    {
        return oldOptionPkgs;
    }

    public String getOldProductId()
    {
        return oldProductId;
    }

    public String getOldResNo()
    {
        return oldResNo;
    }

    public String getOptionPkgs()
    {
        return optionPkgs;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getRemarks()
    {
        return remarks;
    }

    public String getResBrandCode()
    {
        return resBrandCode;
    }

    public String getResBrandName()
    {
        return resBrandName;
    }

    public String getResFee()
    {
        return resFee;
    }

    public String getResKindCode()
    {
        return resKindCode;
    }

    public String getResKindName()
    {
        return resKindName;
    }

    public String getResNo()
    {
        return resNo;
    }

    public String getResStateCode()
    {
        return resStateCode;
    }

    public String getResStateName()
    {
        return resStateName;
    }

    public String getResTypeCode()
    {
        return resTypeCode;
    }

    public String getSerialNumber()
    {
        return SerialNumber;
    }

    public String getSupplyId()
    {
        return supplyId;
    }

    public String getUserAction()
    {
        return userAction;
    }

    public String getWideAddr()
    {
        return wideAddr;
    }

    public String getWideState()
    {
        return wideState;
    }

    public String getWideTradeId()
    {
        return wideTradeId;
    }

    public void setArtificalSericesTag(String artificalSericesTag)
    {
        this.artificalSericesTag = artificalSericesTag;
    }

    public void setBasePkgs(String basePkgs)
    {
        this.basePkgs = basePkgs;
    }

    public void setOldBasePkgs(String oldBasePkgs)
    {
        this.oldBasePkgs = oldBasePkgs;
    }

    public void setOldOptionPkgs(String oldOptionPkgs)
    {
        this.oldOptionPkgs = oldOptionPkgs;
    }

    public void setOldProductId(String oldProductId)
    {
        this.oldProductId = oldProductId;
    }

    public void setOldResNo(String oldResNo)
    {
        this.oldResNo = oldResNo;
    }

    public void setOptionPkgs(String optionPkgs)
    {
        this.optionPkgs = optionPkgs;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setRemarks(String remarks)
    {
        this.remarks = remarks;
    }

    public void setResBrandCode(String resBrandCode)
    {
        this.resBrandCode = resBrandCode;
    }

    public void setResBrandName(String resBrandName)
    {
        this.resBrandName = resBrandName;
    }

    public void setResFee(String resFee)
    {
        this.resFee = resFee;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public void setResKindName(String resKindName)
    {
        this.resKindName = resKindName;
    }

    public void setResNo(String resNo)
    {
        this.resNo = resNo;
    }

    public void setResStateCode(String resStateCode)
    {
        this.resStateCode = resStateCode;
    }

    public void setResStateName(String resStateName)
    {
        this.resStateName = resStateName;
    }

    public void setResTypeCode(String resTypeCode)
    {
        this.resTypeCode = resTypeCode;
    }

    public void setSerialNumber(String serialNumber)
    {
        SerialNumber = serialNumber;
    }

    public void setSupplyId(String supplyId)
    {
        this.supplyId = supplyId;
    }

    public void setUserAction(String userAction)
    {
        this.userAction = userAction;
    }

    public void setWideAddr(String wideAddr)
    {
        this.wideAddr = wideAddr;
    }

    public void setWideState(String wideState)
    {
        this.wideState = wideState;
    }

    public void setWideTradeId(String wideTradeId)
    {
        this.wideTradeId = wideTradeId;
    }

    public String getIsMergeWideUserCreate()
    {
        return isMergeWideUserCreate;
    }

    public void setIsMergeWideUserCreate(String isMergeWideUserCreate)
    {
        this.isMergeWideUserCreate = isMergeWideUserCreate;
    }

	public String getSerialNumberB() {
		return serialNumberB;
	}

	public void setSerialNumberB(String serialNumberB) {
		this.serialNumberB = serialNumberB;
	}

	public String getTopSetBoxTime() {
		return topSetBoxTime;
	}

	public void setTopSetBoxTime(String topSetBoxTime) {
		this.topSetBoxTime = topSetBoxTime;
	}

	public String getTopSetBoxFee() {
		return topSetBoxFee;
	}

	public void setTopSetBoxFee(String topSetBoxFee) {
		this.topSetBoxFee = topSetBoxFee;
	}

	public String getTopSetBoxEndDate() {
		return topSetBoxEndDate;
	}

	public void setTopSetBoxEndDate(String topSetBoxEndDate) {
		this.topSetBoxEndDate = topSetBoxEndDate;
	}
}
