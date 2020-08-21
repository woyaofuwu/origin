package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.offer;


import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class OfferList extends CSBizTempComponent {
	
	public abstract void setCategoryId(String categoryId);
    public abstract String getCategoryId();
    public abstract void setAfterAction(String afterAction);
    public abstract String getAfterAction();
    public abstract void setBeforeAction(String beforeAction);
    public abstract String getBeforeAction();
    public abstract void setCategoryList(IDataset categoryList);
    public abstract String getServiceName();
    public abstract void setColNum(String colNum);
    public abstract void setGroupList(IDataset groupList);
    public abstract void setLabelList(IDataset labelList);
    public abstract String getSwitchGroupService();
    public abstract String getProductId();
    public abstract IData getSvcParam();
    public abstract void setProductId(String productId);

    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/offer/offerlist.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/iorder/icsserv/component/offer/offerlist.js");
        }
        IData data = this.getPage().getData();
        String productId = this.getProductId();
        if (StringUtils.isBlank(productId))
        {
        	productId = data.getString("PRODUCT_ID");
        }
        
        if(StringUtils.isBlank(productId)){
        	return;
        }
        
        if(IDataUtil.isNotEmpty(this.getSvcParam())){
        	data.putAll(this.getSvcParam());
        }
        
        this.setProductId(productId);
        
        String serviceName = this.getServiceName();
        if(StringUtils.isBlank(serviceName)){
        	
        	IDataset groups = new DatasetList();
            
            //获取主产品下的组
            IDataset offerGroups = UpcViewCall.queryOfferGroups(this,productId);
            if(IDataUtil.isNotEmpty(offerGroups)){
                for(int i= 0;i<offerGroups.size();i++)
                {
                    IData offerGroup = offerGroups.getData(i);
                    String removegroupId = data.getString("REMOVE_GROUP_ID", "");
                    String groupId = offerGroup.getString("GROUP_ID", "");
                    String tradetypeCode = data.getString("TRADE_TYPE_CODE", "");

                    if(removegroupId.equals(groupId) && "606".equals(tradetypeCode))
                    {
                        offerGroups.remove(i);
                        i--;
                    }

                    if(("41005405".equals(groupId) || "41005605".equals(groupId)) && !"606".equals(tradetypeCode))
                    {
                        offerGroups.remove(i);
                        i--;
                    }
                }
            	groups.addAll(offerGroups);
            }
            DataHelper.sort(groups, "FORCE_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND, "DEFAULT_TAG", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            //构造用于获取打散商品的组
            if(StringUtils.equals("true", StaticUtil.getStaticValue("OFFER_LIST_PARAM", "DISPLAY_SWITCH_JOIN_REL"))){
                IData group = new DataMap();
                group.put("GROUP_NAME", "其它");
                group.put("GROUP_ID", "-1");
                groups.add(group);
            }
            this.setGroupList(groups);
             
        }
        else{
            data.put("CATEGORY_ID", this.getCategoryId());
        	IData rst = CSViewCall.callone(this, serviceName, data);
        	IDataset groups = rst.getDataset("GROUPS");
        	if (StringUtils.isNotBlank(rst.getString("MEB_PRODUCT_ID")))
            {
                setProductId(rst.getString("MEB_PRODUCT_ID"));
            }
        	
        	this.setGroupList(groups);
        }
        
        setLabelList(UpcViewCall.qryAllTagAndTagValueByOfferId(this, productId, "P"));

    }
    
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
    	super.cleanupAfterRender(cycle);
    	this.setGroupList(null);
    	this.setLabelList(null);
    	this.setProductId(null);
    }
}
