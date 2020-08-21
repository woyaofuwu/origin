
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

public class BaseSaleDepositData extends ProductModuleData
{
    private String serialNumberB;

    private String giftUserId;

    private String campnId;

    private String paymentId;

    private String fee;

    private String payMode;

    private String inDepositCode;

    private String outDepositCode;

    private String months;

    private String depositType;

    private String giftStartDate;

    private String giftEndDate;

    private String productElementFee;

    private List<AttrData> attrs = new ArrayList<AttrData>();

    public BaseSaleDepositData()
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
    }

    public BaseSaleDepositData(IData map) throws Exception
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SALEDEPOSIT);
        this.setElementId(map.getString("ELEMENT_ID"));
        this.setStartDate(map.getString("START_DATE"));
        this.setEndDate(map.getString("END_DATE"));
        this.setProductId(map.getString("PRODUCT_ID"));
        this.setPackageId(map.getString("PACKAGE_ID"));
        this.setCampnId(map.getString("CAMPN_ID"));
        this.setGiftUserId(map.getString("GIFT_USER_ID"));
        this.setSerialNumberB(map.getString("SERIAL_NUMBER_B"));
        this.setPaymentId(map.getString("PAYMENT_ID"));
        this.setFee(map.getString("FEE"));
        this.setPayMode(map.getString("PAY_MODE"));
        this.setInDepositCode(map.getString("IN_DEPOSIT_CODE"));
        this.setOutDepositCode(map.getString("OUT_DEPOSIT_CODE"));
        this.setModifyTag(map.getString("MODIFY_TAG"));
        this.setRemark(map.getString("REMARK"));
        this.setMonths(map.getString("MONTHS"));
        this.setDepositType(map.getString("DEPOSIT_TYPE"));
        this.setGiftStartDate(map.getString("GIFT_START_DATE"));
        this.setGiftEndDate(map.getString("GIFT_END_DATE"));
        this.setProductElementFee(map.getString("FEE"));

        IDataset attrs = map.getDataset("ATTR_PARAM");
        if (IDataUtil.isNotEmpty(attrs))
        {
            List<AttrData> attrDatas = new ArrayList<AttrData>();
            for (int i = 0, size = attrs.size(); i < size; i++)
            {
                IData attr = attrs.getData(i);
                AttrData attrData = new AttrData();
                attrData.setAttrCode(attr.getString("ATTR_CODE"));
                attrData.setAttrValue(attr.getString("ATTR_VALUE"));
                attrData.setModifyTag(attr.getString("MODIFY_TAG"));
                attrDatas.add(attrData);
                this.setAttrs(attrDatas);
            }
        }
    }

    public List<AttrData> getAttrs()
    {
        return attrs;
    }

    public String getCampnId()
    {
        return campnId;
    }

    /***
     * * @return Returns the depositType.
     */
    public String getDepositType()
    {
        return depositType;
    }

    public String getFee()
    {
        return fee;
    }

    public String getGiftEndDate()
    {
        return giftEndDate;
    }

    public String getGiftStartDate()
    {
        return giftStartDate;
    }

    public String getGiftUserId()
    {
        return giftUserId;
    }

    public String getInDepositCode()
    {
        return inDepositCode;
    }

    /***
     * * @return Returns the months.
     */
    public String getMonths()
    {
        return months;
    }

    public String getOutDepositCode()
    {
        return outDepositCode;
    }

    public String getPaymentId()
    {
        return paymentId;
    }

    public String getPayMode()
    {
        return payMode;
    }

    public String getProductElementFee()
    {
        return productElementFee;
    }

    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    public void setAttrs(List<AttrData> attrs)
    {
        this.attrs = attrs;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
    }

    /***
     * @param depositType
     *            The depositType to set.
     */
    public void setDepositType(String depositType)
    {
        this.depositType = depositType;
    }

    public void setFee(String fee)
    {
        this.fee = fee;
    }

    public void setGiftEndDate(String giftEndDate)
    {
        this.giftEndDate = giftEndDate;
    }

    public void setGiftStartDate(String giftStartDate)
    {
        this.giftStartDate = giftStartDate;
    }

    public void setGiftUserId(String giftUserId)
    {
        this.giftUserId = giftUserId;
    }

    public void setInDepositCode(String inDepositCode)
    {
        this.inDepositCode = inDepositCode;
    }

    public void setMonths(String months)
    {
        this.months = months;
    }

    public void setOutDepositCode(String outDepositCode)
    {
        this.outDepositCode = outDepositCode;
    }

    public void setPaymentId(String paymentId)
    {
        this.paymentId = paymentId;
    }

    public void setPayMode(String payMode)
    {
        this.payMode = payMode;
    }

    public void setProductElementFee(String productElementFee)
    {
        this.productElementFee = productElementFee;
    }

    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }

}
