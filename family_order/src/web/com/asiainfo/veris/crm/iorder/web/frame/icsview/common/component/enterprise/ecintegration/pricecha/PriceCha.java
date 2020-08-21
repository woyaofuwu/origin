package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.ecintegration.pricecha;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class PriceCha extends BizTempComponent
{
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        String jsFile = "scripts/iorder/icsserv/component/enterprise/ecintegration/pricecha/PriceCha.js";

        if (isAjax)
        {
            includeScript(writer, jsFile, false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin(jsFile, false, false);
        }
        
        String action = getPage().getData().getString("ACTION");
        if("queryPriceOfferChaList".equals(action))
        {
            queryPriceOfferChaList();
        }
    }
    
    public void queryPriceOfferChaList() throws Exception
    {
        String offerId = getPage().getData().getString("OFFER_ID");

        IDataset priceOfferChaList = IUpcViewCall.queryChaByOfferId(offerId);
        if (DataUtils.isNotEmpty(priceOfferChaList))
        {
            for (int i = 0, size = priceOfferChaList.size(); i < size; i++)
            {
                IData priceOfferChaData = priceOfferChaList.getData(i);

                String fieldType = priceOfferChaData.getString("FIELD_TYPE");

                priceOfferChaData.put("DATA_TYPE", getDataType(fieldType));

                priceOfferChaData.put("ATTR_VALUE", priceOfferChaData.getString("DEFAULT_VALUE"));

                if ("A".equals(priceOfferChaData.getString("SHOW_MODE")))
                {
                    priceOfferChaData.put("CALCULATE_FORMULA", "100*PRICE*COUNT");
                }

                if (StringUtils.isBlank(priceOfferChaData.getString("IS_EDIT")))
                {
                    priceOfferChaData.put("IS_EDIT", "1");
                }

                // 对field_name = 7362 M2M产品进行特殊处理
                if ("7362".equals(priceOfferChaData.getString("FIELD_NAME", "")))
                {
                    IData param = new DataMap();
                    param.put("PARAM_CODE", priceOfferChaData.getString("OFFER_CODE"));
                    param.put("PARAM_ATTR", 886);
                    param.put("SUBSYS_CODE", "CSM");
                    IDataset flowValues = CSViewCall.call(this, "CS.SelectedElementSVC.getElementAttr4ProFlow", param);
                    priceOfferChaData.put("ATTR_SINGLE_FLOW", String.valueOf(flowValues.first().getInt("PARA_CODE1")));

                    int num = 0;
                    for (int j = 0; j < priceOfferChaList.size(); j++)
                    {
                        IData data = priceOfferChaList.getData(j);
                        if ("7361".equals(data.getString("FIELD_NAME", "")))
                        {
                            num = data.getInt("DEFAULT_VALUE");
                            break;
                        }
                    }
                    int sum = flowValues.first().getInt("PARA_CODE1") * num;
                    priceOfferChaData.put("ATTR_VALUE", String.valueOf(sum));

                    priceOfferChaData.put("IS_EDIT", "0");
                }
            }
        }

        setPriceOfferChaList(priceOfferChaList);
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
    
    public abstract void setInfo(IData info);
    public abstract void setPriceOfferCha(IData priceOfferCha);
    public abstract void setPriceOfferChaList(IDataset priceOfferChaList);
}
