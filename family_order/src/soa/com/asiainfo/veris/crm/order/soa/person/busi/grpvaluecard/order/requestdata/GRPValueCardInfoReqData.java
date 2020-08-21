
package com.asiainfo.veris.crm.order.soa.person.busi.grpvaluecard.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class GRPValueCardInfoReqData extends BaseReqData
{
    /** 卡号 */
    private String valueCardNo;

    /** 卡类型名称 */
    private String cardType;

    private String resKindCode;

    /** 生产厂家 */
    private String xFactoryName;

    /** 有价卡状态 */
    private String xStateName;

    /** 有效日期 */
    private String endDate;

    /** 销售标识 */
    private String saleTag;

    /** 归属库存位置 */
    private String xStockName;

    /** 入库时间 */
    private String timeIn;

    /** 入库员工 */
    private String xStaffNameIn;

    /** 销售时间 */
    private String saleTime;

    /** 销售员工 */
    private String xSaleStaffName;

    /** 销售金额（元） */
    private String saleMoney;

    /** 开始卡号 */
    private String startCardNo;

    /** 结束卡号 */
    private String endCardNo;

    /** 面值 */
    private String valueCodeName;

    /** 销售价 */
    private String singleprice;

    /** 总价（元） */
    private String totalPrice;

    /** 数量 */
    private int rowCount;

    /**  */
    private String valeCode;

    /** = DEVICE_PRICE */
    private String advisePrice;

    /***/
    private String activateInfo;

    private String activeFlag;

    /** 原价 */
    private String devicePrice;

    public String getActivateInfo()
    {
        return activateInfo;
    }

    public String getActiveFlag()
    {
        return activeFlag;
    }

    public String getAdvisePrice()
    {
        return advisePrice;
    }

    public String getCardType()
    {
        return cardType;
    }

    public String getDevicePrice()
    {
        return devicePrice;
    }

    public String getEndCardNo()
    {
        return endCardNo;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getResKindCode()
    {
        return resKindCode;
    }

    public int getRowCount()
    {
        return rowCount;
    }

    public String getSaleMoney()
    {
        return saleMoney;
    }

    public String getSaleTag()
    {
        return saleTag;
    }

    public String getSaleTime()
    {
        return saleTime;
    }

    public String getSingleprice()
    {
        return singleprice;
    }

    public String getStartCardNo()
    {
        return startCardNo;
    }

    public String getTimeIn()
    {
        return timeIn;
    }

    public String getTotalPrice()
    {
        return totalPrice;
    }

    public String getValeCode()
    {
        return valeCode;
    }

    public String getValueCardNo()
    {
        return valueCardNo;
    }

    public String getValueCodeName()
    {
        return valueCodeName;
    }

    public String getXFactoryName()
    {
        return xFactoryName;
    }

    public String getXSaleStaffName()
    {
        return xSaleStaffName;
    }

    public String getXStaffNameIn()
    {
        return xStaffNameIn;
    }

    public String getXStateName()
    {
        return xStateName;
    }

    public String getXStockName()
    {
        return xStockName;
    }

    public void setActivateInfo(String activateInfo)
    {
        this.activateInfo = activateInfo;
    }

    public void setActiveFlag(String activeFlag)
    {
        this.activeFlag = activeFlag;
    }

    public void setAdvisePrice(String advisePrice)
    {
        this.advisePrice = advisePrice;
    }

    public void setCardType(String cardType)
    {
        this.cardType = cardType;
    }

    public void setDevicePrice(String devicePrice)
    {
        this.devicePrice = devicePrice;
    }

    public void setEndCardNo(String endCardNo)
    {
        this.endCardNo = endCardNo;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setResKindCode(String resKindCode)
    {
        this.resKindCode = resKindCode;
    }

    public void setRowCount(int rowCount)
    {
        this.rowCount = rowCount;
    }

    public void setSaleMoney(String saleMoney)
    {
        this.saleMoney = saleMoney;
    }

    public void setSaleTag(String saleTag)
    {
        this.saleTag = saleTag;
    }

    public void setSaleTime(String saleTime)
    {
        this.saleTime = saleTime;
    }

    public void setSingleprice(String singleprice)
    {
        this.singleprice = singleprice;
    }

    public void setStartCardNo(String startCardNo)
    {
        this.startCardNo = startCardNo;
    }

    public void setTimeIn(String timeIn)
    {
        this.timeIn = timeIn;
    }

    public void setTotalPrice(String totalPrice)
    {
        this.totalPrice = totalPrice;
    }

    public void setValeCode(String valeCode)
    {
        this.valeCode = valeCode;
    }

    public void setValueCardNo(String valueCardNo)
    {
        this.valueCardNo = valueCardNo;
    }

    public void setValueCodeName(String valueCodeName)
    {
        this.valueCodeName = valueCodeName;
    }

    public void setXFactoryName(String factoryName)
    {
        xFactoryName = factoryName;
    }

    public void setXSaleStaffName(String saleStaffName)
    {
        xSaleStaffName = saleStaffName;
    }

    public void setXStaffNameIn(String staffNameIn)
    {
        xStaffNameIn = staffNameIn;
    }

    public void setXStateName(String stateName)
    {
        xStateName = stateName;
    }

    public void setXStockName(String stockName)
    {
        xStockName = stockName;
    }

}
