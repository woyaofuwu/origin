
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.data;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class TerminalUpData
{

    public String saleLogId;

    public String xChoiceTag;

    public String saleStaffId;

    public String contractId;

    public String xResNoS;

    public String xResNoE;

    public String materialCode;

    public String acceptDate;

    public String channelCode;

    public String isSaleReward;

    public String deviceCost;

    public String discountPrice;

    public String advanceFee;

    public String operFee;

    public String totalFee;

    public String preValue1;

    public String consumeLimit;

    public String months;

    public String packageId;

    public String serialNumber;

    public String userName;

    public String productMode;// 全网统一操盘合约机销售标志

    public String contractType;// 捆绑合约类型

    public String para2;// 绑定营销方案类型

    public String xChoiceTagFlag;// 裸机标志，1表示裸机，2表示非裸机

    public String activeId;// 四码合一激活码

    public String getAcceptDate()
    {
        return acceptDate;
    }

    public String getActiveId()
    {
        return activeId;
    }

    public String getAdvanceFee()
    {
        return advanceFee;
    }

    public String getChannelCode()
    {
        return channelCode;
    }

    public String getConsumeLimit()
    {
        return consumeLimit;
    }

    public String getContractId()
    {
        return contractId;
    }

    public String getContractType()
    {
        return contractType;
    }

    public String getDeviceCost()
    {
        return deviceCost;
    }

    public String getDiscountPrice()
    {
        return discountPrice;
    }

    public String getIsSaleReward()
    {
        return isSaleReward;
    }

    public String getMaterialCode()
    {
        return materialCode;
    }

    public String getMonths()
    {
        return months;
    }

    public String getOperFee()
    {
        return operFee;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getPara2()
    {
        return para2;
    }

    public String getPreValue1()
    {
        return preValue1;
    }

    public String getProductMode()
    {
        return productMode;
    }

    public String getSaleLogId()
    {
        return saleLogId;
    }

    public String getSaleStaffId()
    {
        return saleStaffId;
    }

    public String getSerialNumber()
    {
        return serialNumber;
    }

    public String getTotalFee()
    {
        return totalFee;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getxChoiceTag()
    {
        return xChoiceTag;
    }

    public String getxChoiceTagFlag()
    {
        return xChoiceTagFlag;
    }

    public String getxResNoE()
    {
        return xResNoE;
    }

    public String getxResNoS()
    {
        return xResNoS;
    }

    public void setAcceptDate(String acceptDate)
    {
        this.acceptDate = acceptDate;
    }

    public void setActiveId(String activeId)
    {
        this.activeId = activeId;
    }

    public void setAdvanceFee(String advanceFee)
    {
        this.advanceFee = advanceFee;
    }

    public void setChannelCode(String channelCode)
    {
        this.channelCode = channelCode;
    }

    public void setConsumeLimit(String consumeLimit)
    {
        this.consumeLimit = consumeLimit;
    }

    public void setContractId(String contractId)
    {
        this.contractId = contractId;
    }

    public void setContractType(String contractType)
    {
        this.contractType = contractType;
    }

    public void setDeviceCost(String deviceCost)
    {
        this.deviceCost = deviceCost;
    }

    public void setDiscountPrice(String discountPrice)
    {
        this.discountPrice = discountPrice;
    }

    public void setIsSaleReward(String isSaleReward)
    {
        this.isSaleReward = isSaleReward;
    }

    public void setMaterialCode(String materialCode)
    {
        this.materialCode = materialCode;
    }

    public void setMonths(String months)
    {
        this.months = months;
    }

    public void setOperFee(String operFee)
    {
        this.operFee = operFee;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setPara2(String para2)
    {
        this.para2 = para2;
    }

    public void setPreValue1(String preValue1)
    {
        this.preValue1 = preValue1;
    }

    public void setProductMode(String productMode)
    {
        this.productMode = productMode;
    }

    public void setSaleLogId(String saleLogId)
    {
        this.saleLogId = saleLogId;
    }

    public void setSaleStaffId(String saleStaffId)
    {
        this.saleStaffId = saleStaffId;
    }

    public void setSerialNumber(String serialNumber)
    {
        this.serialNumber = serialNumber;
    }

    public void setTotalFee(String totalFee)
    {
        this.totalFee = totalFee;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public void setxChoiceTag(String xChoiceTag)
    {
        this.xChoiceTag = xChoiceTag;
    }

    public void setxChoiceTagFlag(String xChoiceTagFlag)
    {
        this.xChoiceTagFlag = xChoiceTagFlag;
    }

    public void setxResNoE(String xResNoE)
    {
        this.xResNoE = xResNoE;
    }

    public void setxResNoS(String xResNoS)
    {
        this.xResNoS = xResNoS;
    }

    public IData toData()
    {
        IData data = new DataMap();

        if (StringUtils.isNotBlank(this.saleLogId))
        {
            data.put("SALE_LOG_ID", this.saleLogId);
        }
        if (StringUtils.isNotBlank(this.xChoiceTag))
        {
            data.put("X_CHOICE_TAG", this.xChoiceTag);
        }
        if (StringUtils.isNotBlank(this.saleStaffId))
        {
            data.put("SALE_STAFF_ID", this.saleStaffId);
        }
        if (StringUtils.isNotBlank(this.contractId))
        {
            data.put("CONTRACT_ID", this.contractId);
        }
        if (StringUtils.isNotBlank(this.productMode))
        {
            data.put("PRODUCT_MODE", this.productMode);
        }
        if (StringUtils.isNotBlank(this.xResNoS))
        {
            data.put("X_RES_NO_S", this.xResNoS);
        }
        if (StringUtils.isNotBlank(this.xResNoE))
        {
            data.put("X_RES_NO_E", this.xResNoE);
        }
        if (StringUtils.isNotBlank(this.materialCode))
        {
            data.put("PARA_VALUE10", this.materialCode);
        }
        if (StringUtils.isNotBlank(this.acceptDate))
        {
            data.put("PARA_VALUE11", this.acceptDate);
        }
        if (StringUtils.isNotBlank(this.channelCode))
        {
            data.put("PARA_VALUE12", this.channelCode);
        }
        if (StringUtils.isNotBlank(this.isSaleReward))
        {
            data.put("PARA_VALUE13", this.isSaleReward);
        }
        if (StringUtils.isNotBlank(this.deviceCost))
        {
            data.put("PARA_VALUE14", this.deviceCost);
        }
        if (StringUtils.isNotBlank(this.discountPrice))
        {
            data.put("PARA_VALUE15", this.discountPrice);
        }
        if (StringUtils.isNotBlank(this.advanceFee))
        {
            data.put("PARA_VALUE16", this.advanceFee);
        }
        if (StringUtils.isNotBlank(this.operFee))
        {
            data.put("PARA_VALUE17", this.operFee);
        }
        if (StringUtils.isNotBlank(this.totalFee))
        {
            data.put("PARA_VALUE18", this.totalFee);
        }
        if (StringUtils.isNotBlank(this.contractType))
        {
            data.put("PARA_VALUE9", this.contractType);
        }
        if (StringUtils.isNotBlank(this.preValue1))
        {
            data.put("PREVALUE1", this.preValue1);
        }
        if (StringUtils.isNotBlank(this.para2))
        {
            data.put("PARA2", this.para2);
        }
        if (StringUtils.isNotBlank(this.consumeLimit))
        {
            data.put("PARA_VALUE7", this.consumeLimit);
        }
        if (StringUtils.isNotBlank(this.months))
        {
            data.put("PARA_VALUE6", this.months);
        }
        if (StringUtils.isNotBlank(this.packageId))
        {
            data.put("PARA1", this.packageId);
        }
        if (StringUtils.isNotBlank(this.serialNumber))
        {
            data.put("SERIAL_NUMBER", this.serialNumber);
        }
        if (StringUtils.isNotBlank(this.userName))
        {
            data.put("USER_NAME", this.userName);
        }
        if (StringUtils.isNotBlank(this.xChoiceTagFlag))
        {
            data.put("X_CHOICE_TAG_FLAG", this.xChoiceTagFlag);
        }
        if (StringUtils.isNotBlank(this.activeId))
        {
            data.put("ACTIVE_ID", this.activeId);
        }

        return data;
    }
}
