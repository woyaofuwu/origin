
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.opengroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class OpenGroupMemberReqData extends MemberReqData
{
    private String memRoleB;

    private String batchId;//批次号 @zhuoyingzhi date 20180330
        
    private boolean immeffect;

    private String immediate;// 预约

    private IData payInfo; // 付费关系

    private IDataset productParamInfo; // 产品参数信息

    private IDataset productInfo; // 产品信息

    private String imsPassword = "";

    private IDataset resList;

    private String custInfoTeltype; // IMS客户端类型

    public String getCustInfoTeltype()
    {
        return custInfoTeltype;
    }

    public String getImmediate()
    {
        return immediate;
    }

    public boolean getImmeffect()
    {
        return immeffect;
    }

    public String getImsPassword()
    {
        return imsPassword;
    }

    public String getMemRoleB()
    {
        return memRoleB;
    }

    public IData getPayInfo()
    {
        return payInfo;
    }

    public IDataset getProductInfo()
    {
        return productInfo;
    }

    public IDataset getProductParamInfo()
    {
        return productParamInfo;
    }

    public IDataset getResList()
    {
        return resList;
    }

    public void setCustInfoTeltype(String custInfoTeltype)
    {
        this.custInfoTeltype = custInfoTeltype;
    }

    public void setImmediate(String immediate)
    {
        this.immediate = immediate;
    }

    public void setImmeffect(boolean immeffect)
    {
        this.immeffect = immeffect;
    }

    public void setImsPassword(String imsPassword)
    {
        this.imsPassword = imsPassword;
    }

    public void setMemRoleB(String memRoleB)
    {
        this.memRoleB = memRoleB;
    }

    public void setPayInfo(IData payInfo)
    {
        this.payInfo = payInfo;
    }

    public void setProductInfo(IDataset productInfo)
    {
        this.productInfo = productInfo;
    }

    public void setProductParamInfo(IDataset productParamInfo)
    {
        this.productParamInfo = productParamInfo;
    }

    public void setResList(IDataset resList)
    {
        this.resList = resList;
    }
    
    
    /**
     * REQ201801150022_新增IMS号码开户人像比对功能
     * <br/>
     *  台帐里面的batchId
     * @author zhuoyingzhi
     * @date
     */
    public String getbatchId()
    {
        return batchId;
    }
    public String setbatchId(String batchId)
    {
        return this.batchId = batchId;
    }
    
}
