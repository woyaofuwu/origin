package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.priceoffercha;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.IUpcConst;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class PriceOfferCha extends BizTempComponent
{
    public abstract void setInfo(IData info);
    public abstract void setPriceOfferCha(IData priceOfferCha);
    public abstract void setPriceOfferChaList(IDataset priceOfferChaList);
    
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);

        if (isAjax)
        {
          includeScript(writer, "scripts/iorder/icsserv/component/enterprise/priceoffercha/PriceOfferCha.js", false, false);
        }
        else
        {
          getPage().addResAfterBodyBegin("scripts/iorder/icsserv/component/enterprise/priceoffercha/PriceOfferCha.js", false, false);
        }

        IData info = new DataMap();

        info.put("id", StringUtils.isBlank(getId()) ? "priceOfferChaPopupItem" : getId());

        setInfo(info);
        
        String offerId = this.getPage().getData().getString("OFFER_ID", "");
        String offerInsId = this.getPage().getData().getString("OFFER_INS_ID", "");
        String subscriberInsId = this.getPage().getData().getString("USER_ID", "");
        
        if(StringUtils.isNotBlank(offerId) && StringUtils.isBlank(offerInsId))
        {
            queryPriceOfferChaListByOfferId(offerId);
        }
        else if(StringUtils.isNotBlank(offerId) && StringUtils.isNotBlank(offerInsId))
        {
            boolean isMeb = getPage().getData().getBoolean("IS_MEB", false);
            queryOfferChaListForChg(offerId, offerInsId, subscriberInsId, isMeb);
        }
    }
    
    private void queryOfferChaListForChg(String offerId, String offerInsId, String subscriberInsId, boolean isMeb) throws Exception
    {
        IData data = new DataMap();
        data.put("OFFER_ID", offerId);
        data.put("OFFER_INS_ID", offerInsId);
        data.put("INST_TYPE", IUpcConst.ELEMENT_TYPE_CODE_DISCNT);
        data.put("USER_ID", subscriberInsId);
        data.put("EPARCHY_CODE", getPage().getVisit().getLoginEparchyCode());
        data.put(Route.ROUTE_EPARCHY_CODE, isMeb?getPage().getVisit().getLoginEparchyCode():Route.CONN_CRM_CG);
        IDataset insChaList = CSViewCall.call(this, "SS.QueryAttrParamSVC.queryUserAttrForChgInit", data);// EcOfferViewUtil.queryUmOfferChaForPriceChaInit(offerId, offerInsId, subscriberInsId);
        IData insCha = new DataMap();
        if(DataUtils.isNotEmpty(insChaList)){
        	insCha = insChaList.first();
        }
        IDataset priceOfferChaList = IUpcViewCall.queryChaByOfferId(offerId);
        
        if(DataUtils.isNotEmpty(priceOfferChaList))
        {
            for(int i = 0, size = priceOfferChaList.size(); i < size; i++)
            {
                IData priceOfferChaData = priceOfferChaList.getData(i);
                
                String fieldType = priceOfferChaData.getString("FIELD_TYPE");
                
                priceOfferChaData.put("DATA_TYPE", getDataType(fieldType));
                
                String fieldName = priceOfferChaData.getString("FIELD_NAME");
                if(insCha.containsKey(fieldName)){
                	IData attr = insCha.getData(fieldName);
                	if(DataUtils.isNotEmpty(attr)){
                		priceOfferChaData.put("ATTR_VALUE", attr.getString("ATTR_VALUE"));
                	}
            	}else{
                    priceOfferChaData.put("ATTR_VALUE", priceOfferChaData.getString("DEFAULT_VALUE"));
            	}


                if("A".equals(priceOfferChaData.getString("SHOW_MODE")))
                {
                    priceOfferChaData.put("CALCULATE_FORMULA", "100*PRICE*COUNT");
                }
                
                //如果IS_EDIT编辑字段为空,则塞入1,允许编辑
                if(StringUtils.isBlank(priceOfferChaData.getString("IS_EDIT"))){
                	priceOfferChaData.put("IS_EDIT", "1");
                }
                
                //对field_name = 7362 M2M产品进行特殊处理
                if("7362".equals(priceOfferChaData.getString("FIELD_NAME",""))){
                	IData param = new DataMap();
                	param.put("PARAM_CODE", priceOfferChaData.getString("OFFER_CODE"));
                	param.put("PARAM_ATTR", 886);
                	param.put("SUBSYS_CODE", "CSM");
                	IDataset flowValues = CSViewCall.call(this, "CS.SelectedElementSVC.getElementAttr4ProFlow", param);
                	priceOfferChaData.put("ATTR_SINGLE_FLOW", String.valueOf(flowValues.first().getInt("PARA_CODE1")));
                	
                	priceOfferChaData.put("IS_EDIT", "0");
                }
            }
        }

        setPriceOfferChaList(priceOfferChaList);
    }
    
    private void queryPriceOfferChaListByOfferId(String offerId) throws Exception
    {
        IDataset priceOfferChaList = IUpcViewCall.queryChaByOfferId(offerId);
        
        if(DataUtils.isNotEmpty(priceOfferChaList))
        {
            for(int i = 0, size = priceOfferChaList.size(); i < size; i++)
            {
                IData priceOfferChaData = priceOfferChaList.getData(i);
                
                String fieldType = priceOfferChaData.getString("FIELD_TYPE");
                
                priceOfferChaData.put("DATA_TYPE", getDataType(fieldType));
                
                priceOfferChaData.put("ATTR_VALUE", priceOfferChaData.getString("DEFAULT_VALUE"));

                if("A".equals(priceOfferChaData.getString("SHOW_MODE")))
                {
                    priceOfferChaData.put("CALCULATE_FORMULA", "100*PRICE*COUNT");
                }
                
                if(StringUtils.isBlank(priceOfferChaData.getString("IS_EDIT"))){
                	priceOfferChaData.put("IS_EDIT", "1");
                }
                
                //对field_name = 7362 M2M产品进行特殊处理
                if("7362".equals(priceOfferChaData.getString("FIELD_NAME",""))){
                	IData param = new DataMap();
                	param.put("PARAM_CODE", priceOfferChaData.getString("OFFER_CODE"));
                	param.put("PARAM_ATTR", 886);
                	param.put("SUBSYS_CODE", "CSM");
                	IDataset flowValues = CSViewCall.call(this, "CS.SelectedElementSVC.getElementAttr4ProFlow", param);
                	priceOfferChaData.put("ATTR_SINGLE_FLOW", String.valueOf(flowValues.first().getInt("PARA_CODE1")));
                	
                	int num = 0;
                	for(int j=0;j<priceOfferChaList.size();j++){
                		IData data = priceOfferChaList.getData(j);
                		if("7361".equals(data.getString("FIELD_NAME",""))){
                			num = data.getInt("DEFAULT_VALUE");
                			break;
                		}
                	}
                	int sum = flowValues.first().getInt("PARA_CODE1")*num;
                	priceOfferChaData.put("ATTR_VALUE", String.valueOf(sum));
                	
                	priceOfferChaData.put("IS_EDIT", "0");
                }
            }
        }
        
        setPriceOfferChaList(priceOfferChaList);
        
    }
    
    private String getDataType(String valueType) throws Exception
    {//0-字符串，1-数字，2-日期 ， 3-自然数（大于等于0的整数）
        String dataType = "text";
        if("1".equals(valueType))
        {
            dataType = "numeric";
        }
        else if("2".equals(valueType))
        {
            dataType = "date";
        }else if("3".equals(valueType))
        {
        	dataType = "nature";
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
