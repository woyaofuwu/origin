
package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;

public class BaseSaleGoodsData extends ProductModuleData
{
    private String resCode = "0";

    private String postName;

    private String postAddress;

    private String postCode;

    private String goodsNum = "1";

    private String serialNumberB;

    private String saleStaffId;

    private String resTag;

    private String productElementFee;

    public BaseSaleGoodsData()
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
    }

    public BaseSaleGoodsData(IData map) throws Exception
    {
        this.setElementType(BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
        this.setElementId(map.getString("ELEMENT_ID"));
        this.setStartDate(map.getString("START_DATE"));
        this.setEndDate(map.getString("END_DATE"));
        this.setProductId(map.getString("PRODUCT_ID"));
        this.setPackageId(map.getString("PACKAGE_ID"));
        this.setSerialNumberB(map.getString("SERIAL_NUMBER_B"));
        this.setSaleStaffId(map.getString("SALE_STAFF_ID"));
        this.setResCode(map.getString("RES_CODE", "-1"));
        this.setPostName(map.getString("POST_NAME"));
        this.setPostAddress(map.getString("POST_ADDRESS"));
        this.setPostCode(map.getString("POST_CODE"));
        this.setModifyTag(map.getString("MODIFY_TAG"));
        this.setGoodsNum(map.getString("GOODS_NUM", "1"));
        this.setRemark(map.getString("REMARK"));
        this.setProductElementFee(map.getString("FEE"));
    }

    public String getGoodsNum()
    {
        return goodsNum;
    }

    public String getPostAddress()
    {
        return postAddress;
    }

    public String getPostCode()
    {
        return postCode;
    }

    public String getPostName()
    {
        return postName;
    }

    public String getProductElementFee()
    {
        return productElementFee;
    }

    public String getResCode()
    {
        return resCode;
    }

    public String getResTag()
    {
        return resTag;
    }

    public String getSaleStaffId()
    {
        return saleStaffId;
    }

    public String getSerialNumberB()
    {
        return serialNumberB;
    }

    public void setGoodsNum(String goodsNum)
    {
        this.goodsNum = goodsNum;
    }

    public void setPostAddress(String postAddress)
    {
        this.postAddress = postAddress;
    }

    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }

    public void setPostName(String postName)
    {
        this.postName = postName;
    }

    public void setProductElementFee(String productElementFee)
    {
        this.productElementFee = productElementFee;
    }

    public void setResCode(String resCode)
    {
        this.resCode = resCode;
    }

    public void setResTag(String resTag)
    {
        this.resTag = resTag;
    }

    public void setSaleStaffId(String saleStaffId)
    {
        this.saleStaffId = saleStaffId;
    }

    public void setSerialNumberB(String serialNumberB)
    {
        this.serialNumberB = serialNumberB;
    }

}
