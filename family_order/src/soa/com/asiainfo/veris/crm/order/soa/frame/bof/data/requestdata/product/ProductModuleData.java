package com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;

public class ProductModuleData
{
    private List<AttrData> attrs = new ArrayList<AttrData>();

    private String campnId;

    private String cancelAbsoluteDate;

    private String cancelOffSet;

    private String cancelTag;

    private String cancelUnit;

    private String elementId;

    private String elementType;

    private String remark;

    private String enableTag;

    private String endAbsoluteDate;

    private String endDate;

    private String endEnableTag;

    private String endOffSet;

    private String endUnit;

    private String instId;

    private String modifyTag;

    private String packageId;

    private String productId;

    private String startAbsoluteDate;

    private String startDate;

    private String startOffset;

    private String startUnit;

    private OfferCfg offerCfg;
    
    public void addAttr(AttrData attrData)
    {
        this.attrs.add(attrData);
    }

    public List<AttrData> getAttrs()
    {
        return attrs;
    }

    public String getCampnId()
    {
        return campnId;
    }

    public String getCancelAbsoluteDate()
    {
        return cancelAbsoluteDate;
    }

    public String getCancelOffSet()
    {
        return cancelOffSet;
    }

    public String getCancelTag()
    {
        return cancelTag;
    }

    public String getCancelUnit()
    {
        return cancelUnit;
    }

    public String getElementId()
    {
        return elementId;
    }

    public String getElementType()
    {
        return elementType;
    }

    public String getEnableTag()
    {
        return enableTag;
    }

    public String getEndAbsoluteDate()
    {
        return endAbsoluteDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getEndEnableTag()
    {
        return endEnableTag;
    }

    public String getEndOffSet()
    {
        return endOffSet;
    }

    public String getEndUnit()
    {
        return endUnit;
    }

    public String getInstId()
    {
        return instId;
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

    public String getRemark()
    {
        return remark;
    }

    public String getStartAbsoluteDate()
    {
        return startAbsoluteDate;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getStartOffset()
    {
        return startOffset;
    }

    public String getStartUnit()
    {
        return startUnit;
    }

    public void setAttrs(List<AttrData> attrs)
    {
        this.attrs = attrs;
    }

    public void setCampnId(String campnId)
    {
        this.campnId = campnId;
    }

    public void setCancelAbsoluteDate(String cancelAbsoluteDate)
    {
        this.cancelAbsoluteDate = cancelAbsoluteDate;
    }

    public void setCancelOffSet(String cancelOffSet)
    {
        this.cancelOffSet = cancelOffSet;
    }

    public void setCancelTag(String cancelTag)
    {
        this.cancelTag = cancelTag;
    }

    public void setCancelUnit(String cancelUnit)
    {
        this.cancelUnit = cancelUnit;
    }

    public void setElementId(String elementId)
    {
        this.elementId = elementId;
    }

    public void setElementType(String elementType)
    {
        this.elementType = elementType;
    }

    public void setEnableTag(String enableTag)
    {
        this.enableTag = enableTag;
    }

    public void setEndAbsoluteDate(String endAbsoluteDate)
    {
        this.endAbsoluteDate = endAbsoluteDate;
    }

    public void setEndDate(String endDate)
    {
        this.endDate = endDate;
    }

    public void setEndEnableTag(String endEnableTag)
    {
        this.endEnableTag = endEnableTag;
    }

    public void setEndOffSet(String endOffSet)
    {
        this.endOffSet = endOffSet;
    }

    public void setEndUnit(String endUnit)
    {
        this.endUnit = endUnit;
    }

    public void setInstId(String instId)
    {
        this.instId = instId;
    }

    public void setModifyTag(String modifyTag)
    {
        this.modifyTag = modifyTag;
    }

    public void setPackageId(String packageId)
    {
        this.packageId = packageId;
    }

    protected void setPkgElementConfig() throws Exception
    {
        IData product = UProductInfoQry.qrySaleActiveProductByPK(productId);
        IData enableMode = null;
        if(IDataUtil.isEmpty(product))//普通产品
        {
            enableMode = UPackageElementInfoQry.queryElementEnableMode(this.productId, this.packageId, this.elementId, this.elementType);

        }else//营销活动
        {
            enableMode = UPackageElementInfoQry.querySaleElementEnableMode(this.packageId, this.packageId, this.elementId, this.elementType);
        }
        
        if (IDataUtil.isNotEmpty(enableMode))
        {
            this.setEnableTag(enableMode.getString("ENABLE_TAG"));
            this.setStartAbsoluteDate(enableMode.getString("START_ABSOLUTE_DATE"));
            this.setStartOffset(enableMode.getString("START_OFFSET"));
            this.setStartUnit(enableMode.getString("START_UNIT"));
            this.setEndEnableTag(enableMode.getString("END_ENABLE_TAG"));
            this.setEndAbsoluteDate(enableMode.getString("END_ABSOLUTE_DATE"));
            this.setEndOffSet(enableMode.getString("END_OFFSET"));
            this.setEndUnit(enableMode.getString("END_UNIT"));
            this.setCancelTag(enableMode.getString("CANCEL_TAG"));
        }
        else
        {
            this.setCancelTag("3");// 退订时如果已下线，则默认月底终止
        }
    }

    public void setPkgElementConfig(String packageId) throws Exception
    {
        IData product = UProductInfoQry.qrySaleActiveProductByPK(this.getProductId());
        IData enableMode = null;
        if(IDataUtil.isEmpty(product))//普通产品
        {     //modify by hefeng  由于预约主产品变更后，在对用户进行服务或优惠变更，这个product_id 是预约的产品ID ,packageId是老产品的，所以会有数据无法找到
        	  //导致程序失效方式会有问题
//            enableMode = UPackageElementInfoQry.queryElementEnableMode(this.getProductId(), packageId, this.getElementId(), this.getElementType());
        	   enableMode =UPackageElementInfoQry.queryElementEnableModeByGroupId(packageId, this.getElementId(), this.getElementType());

        }else//营销活动产品
        {
            enableMode = UPackageElementInfoQry.querySaleElementEnableMode(packageId, "-1", this.getElementId(), this.getElementType());

        }
        
        if (IDataUtil.isNotEmpty(enableMode))
        {
            this.setEnableTag(enableMode.getString("ENABLE_TAG"));
            this.setStartAbsoluteDate(enableMode.getString("START_ABSOLUTE_DATE"));
            this.setStartOffset(enableMode.getString("START_OFFSET"));
            this.setStartUnit(enableMode.getString("START_UNIT"));
            this.setEndEnableTag(enableMode.getString("END_ENABLE_TAG"));
            this.setEndAbsoluteDate(enableMode.getString("END_ABSOLUTE_DATE"));
            this.setEndOffSet(enableMode.getString("END_OFFSET"));
            this.setEndUnit(enableMode.getString("END_UNIT"));
            this.setCancelTag(enableMode.getString("CANCEL_TAG"));
        }
        else
        {
            this.setCancelTag("3");// 退订时如果已下线，则默认月底终止
        }
    }

    public void setProductId(String productId)
    {
        this.productId = productId;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public void setStartAbsoluteDate(String startAbsoluteDate)
    {
        this.startAbsoluteDate = startAbsoluteDate;
    }

    public void setStartDate(String startDate)
    {
        this.startDate = startDate;
    }

    protected void setStartOffset(String startOffset)
    {
        this.startOffset = startOffset;
    }

    protected void setStartUnit(String startUnit)
    {
        this.startUnit = startUnit;
    }

	public OfferCfg getOfferCfg() throws Exception{
		if(this.offerCfg == null)
			this.offerCfg = OfferCfg.getInstance(this.elementId, this.elementType);
		return this.offerCfg;
	}
}
