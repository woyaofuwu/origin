
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.io.Serializable;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class ElementModel implements Serializable
{
    private static final long serialVersionUID = 1L;

    private String productId;

    private String productMode;

    private String packageId;

    private String elementId;

    private String elementTypeCode;

    private String startDate;

    private String endDate;

    private String modifyTag;

    private String instId;

    private IDataset attrParam;

    private String diversifyAcctTag; // 分散处理标记

    private String rsrvTag2;

    private String isNeedPf;

    private String rsrv_num1; // 暂时不知道老系统为什么入0

    private String rsrv_num2; // 暂时不知道老系统为什么入0

    private String rsrv_num3; // 暂时不知道老系统为什么入0

    private String rsrvStr5;
    
    private String rsrvStr6;
    
    private String rsrvStr7;
    
    public ElementModel(IData data) throws Exception
    {
        this.productId = data.getString("PRODUCT_ID");
        this.productMode = (StringUtils.isBlank(productId) || StringUtils.equals("-1", productId)) ? "" : UProductInfoQry.getProductModeByProductId(productId);
        this.packageId = data.getString("PACKAGE_ID");
        this.elementId = data.getString("ELEMENT_ID");
        this.elementTypeCode = data.getString("ELEMENT_TYPE_CODE");
        // 转换时间构建模型数据
        this.startDate = SysDateMgr.getEnableDate(data.getString("START_DATE"), data.getString("ACCEPT_TIME", SysDateMgr.getSysTime()));

        this.endDate = SysDateMgr.getEndDate(data.getString("ACCEPT_TIME", SysDateMgr.getSysTime()), data.getString("START_DATE"), data.getString("END_DATE"), data.getBoolean("IF_BOOKING", false), data.getString("CANCEL_TAG", "0"), data.getBoolean(
                "EFFECT_NOW", false), data.getString("MODIFY_TAG"));

        this.modifyTag = data.getString("MODIFY_TAG");
        this.instId = data.getString("INST_ID");
        this.attrParam = IDataUtil.getDataset(data, "ATTR_PARAM");

        if (IDataUtil.isEmpty(this.attrParam))
        {
            this.attrParam = new DatasetList();
        }
        this.diversifyAcctTag = data.getString("DIVERSIFY_ACCT_TAG");
        this.rsrvTag2 = data.getString("RSRV_TAG2");
        this.isNeedPf = data.getString("IS_NEED_PF");
        this.rsrv_num1 = data.getString("RSRV_NUM1", "0");
        this.rsrv_num2 = data.getString("RSRV_NUM2", "0");
        this.rsrv_num3 = data.getString("RSRV_NUM3", "0");
        this.rsrvStr5 = data.getString("RSRV_STR5", "");
        this.rsrvStr6 = data.getString("RSRV_STR6", "");
        this.rsrvStr7 = data.getString("RSRV_STR7", "");
    }

    protected IData convertData()
    {
        IData data = new DataMap();

        data.put("PRODUCT_ID", this.productId);
        data.put("PRODUCT_MODE", this.productMode);
        data.put("PACKAGE_ID", this.packageId);
        data.put("ELEMENT_ID", this.elementId);
        data.put("S".equals(elementTypeCode) ? "SERVICE_ID" : "DISCNT_CODE", this.elementId);
        data.put("ELEMENT_TYPE_CODE", this.elementTypeCode);
        data.put("START_DATE", this.startDate);
        data.put("END_DATE", this.endDate);
        data.put("MODIFY_TAG", this.modifyTag);
        data.put("INST_ID", this.instId);
        data.put("DIVERSIFY_ACCT_TAG", this.diversifyAcctTag);
        data.put("RSRV_TAG2", this.rsrvTag2);
        data.put("IS_NEED_PF", this.isNeedPf);
        data.put("RSRV_NUM1", this.rsrv_num1);
        data.put("RSRV_NUM2", this.rsrv_num2);
        data.put("RSRV_NUM3", this.rsrv_num3);
        data.put("RSRV_STR5", this.rsrvStr5);
        data.put("RSRV_STR6", this.rsrvStr6);
        data.put("RSRV_STR7", this.rsrvStr7);
        return data;
    }

    public IDataset getAttrParam()
    {
        return attrParam;
    }

    public String getDiversifyAcctTag()
    {
        return diversifyAcctTag;
    }

    public String getElementId()
    {
        return elementId;
    }

    public String getElementTypeCode()
    {
        return elementTypeCode;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getInstId()
    {
        return instId;
    }

    public String getIsNeedPf()
    {
        return isNeedPf;
    }

    public String getModifyTag()
    {
        return modifyTag;
    }

    public String getPackageId()
    {
        return packageId;
    }

    public String getProductId()
    {
        return productId;
    }

    public String getProductMode()
    {
        return productMode;
    }

    public String getRsrv_num1()
    {
        return rsrv_num1;
    }

    public String getRsrv_num2()
    {
        return rsrv_num2;
    }

    public String getRsrv_num3()
    {
        return rsrv_num3;
    }

    public String getRsrvStr5() {
		return rsrvStr5;
	}

    public String getRsrvStr6() {
		return rsrvStr6;
	}
    
    public String getRsrvStr7() {
		return rsrvStr7;
	}
    
    public String getRsrvTag2()
    {
        return rsrvTag2;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public void setAttrParam(IDataset attrParam)
    {
        this.attrParam = attrParam;
    }

    public void setDiversifyAcctTag(String diversifyAcctTag)
    {
        this.diversifyAcctTag = diversifyAcctTag;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public void setElementTypeCode(String elementTypeCode)
    {
        this.elementTypeCode = elementTypeCode;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setIsNeedPf(String isNeedPf)
    {
        this.isNeedPf = isNeedPf;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setProductMode(String productMode)
    {
        this.productMode = productMode;
    }

    public void setRsrv_num1(String rsrv_num1)
    {
        this.rsrv_num1 = rsrv_num1;
    }

    public void setRsrv_num2(String rsrv_num2)
    {
        this.rsrv_num2 = rsrv_num2;
    }

    public void setRsrv_num3(String rsrv_num3)
    {
        this.rsrv_num3 = rsrv_num3;
    }


	public void setRsrvStr5(String rsrvStr5) {
		this.rsrvStr5 = rsrvStr5;
	}
	
	public void setRsrvStr6(String rsrvStr6) {
		this.rsrvStr6 = rsrvStr6;
	}
	
	public void setRsrvStr7(String rsrvStr7) {
		this.rsrvStr7 = rsrvStr7;
	}
	
    public void setRsrvTag2(String rsrvTag2)
    {
        this.rsrvTag2 = rsrvTag2;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

}
