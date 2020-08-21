package com.asiainfo.veris.crm.order.web.frame.csview.common.component.offer;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.ElementPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public class OfferListHandler extends CSBizHttpHandler {

	public void switchGroup() throws Exception{
		IData data = this.getData();
		String serviceName = data.getString("SERVICE_NAME");
		String groupId = data.getString("GROUP_ID");
		String groupName = data.getString("GROUP_NAME");
		String page = data.getString("page","");
		String menu = data.getString("m","");
		//System.out.println("==========OfferListHandler=========="+data);
		//不限的情况 查询joinrel
		if(StringUtils.equals(groupId, "-1"))
		{  
		    switchJoinRel();
		    return;
		    
		}
		
		IDataset children = null;
		if(StringUtils.isBlank(serviceName)){
			children = UpcViewCall.queryGroupComRelOffer(this, groupId);
		}
		else{
			children = CSViewCall.call(this, serviceName, data);
		}
		
		ElementPrivUtil.filterElementListByPriv(getVisit().getStaffId(), children);
		DataHelper.sort(children, "OFFER_TYPE",IDataset.TYPE_STRING,IDataset.ORDER_DESCEND,"OFFER_CODE", IDataset.TYPE_INTEGER,IDataset.ORDER_ASCEND);
	 
		for (Iterator iterator = children.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next();
            
            String offerType = StaticUtil.getStaticValue("OFFER_LIST_NODISPLAY", item.getString("OFFER_CODE"));
            
            if (item.getString("OFFER_TYPE","").equals(offerType) && !"crm9B21".equals(menu) && !"crm9B11".equals(menu) 
            		&& !"KDRH001".equals(menu) && !"crmN100".equals(menu) && !"crmNB11".equals(menu) && !"crmNB21".equals(menu) )
            {
                iterator.remove();
            } 
        }
		
		IDataset categorys = new DatasetList();
		IData category = new DataMap();
		category.put("CATEGORY_ID", "-1");
		category.put("CATEGORY_NAME", groupName);
		category.put("OFFER_LIST", children);
		categorys.add(category);
		this.setAjax(categorys);
	}
	
	public void switchLable() throws Exception{
        IData data = this.getData();
        String productId = data.getString("PRODUCT_ID");
        String category = data.getString("CATEGORY_ID");
        String labelId = data.getString("LABEL_ID");
        String labelKeyId = data.getString("LABEL_KEY_ID");
        
       this.setAjax(UpcViewCall.qryOfferByTagInfo(this, productId, "P", labelId, labelKeyId, category));
    }
	
	public void switchJoinRel()throws Exception{
	    
	    IData data = this.getData();
        String joinServiceName = data.getString("JOIN_SERVICE_NAME");
        String categoryId = data.getString("CATEGORY_ID");
        String productId = data.getString("PRODUCT_ID");
        IDataset categorys = new DatasetList();
        if(StringUtils.isBlank(joinServiceName)){
            categorys = UpcViewCall.queryOffersByMultiCategory(this, productId, data.getString("EPARCHY_CODE"), categoryId, "2");
        }else
        {
            categorys = CSViewCall.call(this, joinServiceName, data);
        }
        
        //将没有销售品列表的品类删除掉
        if(IDataUtil.isNotEmpty(categorys))
        {
            for(int i = categorys.size() -1; i >= 0; i--)
            {
                IData categroy = categorys.getData(i);
                if(IDataUtil.isEmpty(categroy.getDataset("OFFER_LIST")))
                {
                    categorys.remove(i);
                }
            }
        }
        
        this.setAjax(categorys);
     }
}
