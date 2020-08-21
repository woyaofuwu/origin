/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午03:07:59
 */
public class ChooseNetworkReqData extends BaseReqData
{

    private String operType; // 操作类型

    private String cooperArea; // 合作方地区

    private String cooperNet; // 合作方网络

    public String getCooperArea()
    {
        return cooperArea;
    }

    public String getCooperNet()
    {
        return cooperNet;
    }

    public String getOperType()
    {
        return operType;
    }

    public void setCooperArea(String cooperArea)
    {
        this.cooperArea = cooperArea;
    }

    public void setCooperNet(String cooperNet)
    {
        this.cooperNet = cooperNet;
    }

    public void setOperType(String operType)
    {
        this.operType = operType;
    }

}
