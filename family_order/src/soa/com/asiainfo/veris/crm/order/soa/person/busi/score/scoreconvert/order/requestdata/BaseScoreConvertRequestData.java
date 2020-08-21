
package com.asiainfo.veris.crm.order.soa.person.busi.score.scoreconvert.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class BaseScoreConvertRequestData extends BaseReqData
{
    private String itemId;// 礼品id

    private String itemNum;// 兑换数量

    private String delivProvince;// 配送省

    private String city;// 配送市

    private String district;// 配送区

    private String cusAddcode;// 邮编

    private String cusTel;// 联系电话

    private String delivTimeReq;// 配送时间

    private String cusAdd;// 送货地址

    public String getCity()
    {
        return city;
    }

    public String getCusAdd()
    {
        return cusAdd;
    }

    public String getCusAddcode()
    {
        return cusAddcode;
    }

    public String getCusTel()
    {
        return cusTel;
    }

    public String getDelivProvince()
    {
        return delivProvince;
    }

    public String getDelivTimeReq()
    {
        return delivTimeReq;
    }

    public String getDistrict()
    {
        return district;
    }

    public String getItemId()
    {
        return itemId;
    }

    public String getItemNum()
    {
        return itemNum;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public void setCusAdd(String cusAdd)
    {
        this.cusAdd = cusAdd;
    }

    public void setCusAddcode(String cusAddcode)
    {
        this.cusAddcode = cusAddcode;
    }

    public void setCusTel(String cusTel)
    {
        this.cusTel = cusTel;
    }

    public void setDelivProvince(String delivProvince)
    {
        this.delivProvince = delivProvince;
    }

    public void setDelivTimeReq(String delivTimeReq)
    {
        this.delivTimeReq = delivTimeReq;
    }

    public void setDistrict(String district)
    {
        this.district = district;
    }

    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }

    public void setItemNum(String itemNum)
    {
        this.itemNum = itemNum;
    }
}
