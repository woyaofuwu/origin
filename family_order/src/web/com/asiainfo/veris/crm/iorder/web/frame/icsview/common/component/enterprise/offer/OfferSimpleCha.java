package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.offer;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class OfferSimpleCha extends BizTempComponent
{
    public abstract void setInfo(IData info);
    public abstract void setOfferSimpleCha(IData offerSimpleCha);
    public abstract void setOfferSimpleChaList(IDataset offerSimpleChaList);
    
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);

        if (isAjax)
        {
          includeScript(writer, "scripts/iorder/icsserv/component/enterprise/offer/OfferSimpleCha.js", false, false);
        }
        else
        {
          getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/offer/OfferSimpleCha.js", false, false);
        }

        IData info = new DataMap();

        info.put("id", StringUtils.isBlank(getId()) ? "offerSimpleChaPopupItem" : getId());

        setInfo(info);
        
        String offerId = this.getPage().getData().getString("OFFER_ID", "");
        String offerType = this.getPage().getData().getString("OFFER_TYPE", "");
        String offerInsId = this.getPage().getData().getString("OFFER_INS_ID", "");
        String subscriberInsId = this.getPage().getData().getString("USER_ID", "");
        
        if(StringUtils.isNotBlank(offerId) && StringUtils.isBlank(offerInsId))
        {
            queryOfferSimpleChaListByOfferId(offerId);
        }
        else if(StringUtils.isNotBlank(offerId) && StringUtils.isNotBlank(offerInsId))
        {
//            String orderLineId = this.getPage().getData().getString("ORDER_LINE_ID", "");
            queryOfferChaListForChg(offerId,offerType, offerInsId, subscriberInsId);
        }
    }
    
    
    private void queryOfferChaListForChg(String offerId,String offerType, String offerInsId, String subscriberInsId) throws Exception
    {
    	IDataset offerSimpleChaList = IUpcViewCall.queryChaByOfferId(offerId);
    	
    	
    	IData param = new DataMap();
    	param.put("USER_ID", subscriberInsId);
    	param.put("INST_TYPE", offerType);
    	param.put("INST_ID", offerInsId);
    	param.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());
        IDataset userAttr = CSViewCall.call(this,"CS.UserAttrInfoQrySVC.getUserAttrByUserIdInstid",param);
        IData attrMap = new DataMap();
        if(DataUtils.isNotEmpty(userAttr)){
        	for(int a=0,size=userAttr.size();a<size;a++){
        		attrMap.put(userAttr.getData(a).getString("ATTR_CODE"), userAttr.getData(a).getString("ATTR_VALUE"));
        	}
        }
        
        if(DataUtils.isNotEmpty(offerSimpleChaList))
        {
            for(int i = 0, size = offerSimpleChaList.size(); i < size; i++)
            {
                IData offerSimpleChaData = offerSimpleChaList.getData(i);
                
                offerSimpleChaData.put("VALUE",attrMap.getString(offerSimpleChaData.getString("FIELD_NAME"),offerSimpleChaData.getString("DEFAULT_VALUE")));
                offerSimpleChaData.put("OFFER_CHA_INS_ID",offerInsId);//属性本身没有实例编码，塞归属元素实例给它用用

                if("A".equals(offerSimpleChaData.getString("SHOW_MODE")))
                {
                    offerSimpleChaData.put("CALCULATE_FORMULA", "100*PRICE*COUNT");
                }
            }
        }
        setOfferSimpleChaList(offerSimpleChaList);
    }
    
    private void queryOfferSimpleChaListByOfferId(String offerId) throws Exception
    {
        IDataset offerSimpleChaList = IUpcViewCall.queryChaByOfferId(offerId);
        
        if(DataUtils.isNotEmpty(offerSimpleChaList))
        {
            for(int i = 0, size = offerSimpleChaList.size(); i < size; i++)
            {
                IData offerSimpleChaData = offerSimpleChaList.getData(i);
                offerSimpleChaData.put("VALUE", offerSimpleChaData.getString("DEFAULT_VALUE"));
                if("A".equals(offerSimpleChaData.getString("SHOW_MODE")))
                {
                    offerSimpleChaData.put("CALCULATE_FORMULA", "100*PRICE*COUNT");
                }
            }
        }
        
        setOfferSimpleChaList(offerSimpleChaList);
        
    }
    
    private String getDataType(String valueType) throws Exception
    {//0-字符串，1-数字，2-日期
        String dataType = "text";
        if("1".equals(valueType))
        {
            dataType = "numeric";
        }
        else if("2".equals(valueType))
        {
            dataType = "date";
        }
        return dataType;
    }
    
//    private IDataset test(IDataset priceChaDataset) throws Exception
//    {
//        IData data = priceChaDataset.getData(0);
//        IData data1 = new DataMap(data);
//        data1.put("CODE","80001");
//        data1.put("VALUE_TYPE","010005");
//        data1.put("CHA_SPEC_NAME","日期test");
//        data1.put("CHA_SPEC_ID","400017703");
//        priceChaDataset.add(data1);
//        
//        IData data2 = new DataMap(data);
//        data2.put("CODE","80002");
//        data2.put("VALUE_TYPE","010007");
//        data2.put("CHA_SPEC_NAME","下拉框test");
//        data2.put("CHA_SPEC_ID","400017702");
//        priceChaDataset.add(data2);
//        
//        IData data3 = new DataMap(data);
//        data3.put("CODE","80003");
//        data3.put("VALUE_TYPE","010009");
//        data3.put("CHA_SPEC_NAME","复选框test");
//        data3.put("CHA_SPEC_ID","400017701");
//        priceChaDataset.add(data3);
//        
//        return priceChaDataset;
//    }
    
}
