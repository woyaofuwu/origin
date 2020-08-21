
package com.asiainfo.veris.crm.iorder.web.person.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

import java.util.HashMap;
import java.util.Map;

public abstract class SaleActiveTradeNew extends PersonBasePage
{
    public void checkSaleBook(IRequestCycle cycle) throws Exception
    {
        IData returnData = new DataMap();

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", getData().getString("SERIAL_NUMBER"));

        IDataset infos = CSViewCall.call(this, "SS.SaleActiveSVC.checkSaleBook", params);
        if (IDataUtil.isNotEmpty(infos))
        {
            returnData = infos.getData(0);
        }
        else
        {
            returnData.put("AUTH_BOOK_SALE", 0);
        }

        setAjax(returnData);
    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData initParam = new DataMap();
        initParam.put("authType", data.getString("authType", "00"));
        initParam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE", "240"));
        initParam.put("LABEL_ID", data.getString("LABEL_ID"));
        setInfo(initParam);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.putAll(new DataMap(data.getString("SALEACTIVEDATA")));
        data.remove("SALEACTIVEDATA");
        IDataset rtDataset = CSViewCall.call(this, "SS.SaleActiveRegSVC.tradeReg", data);
        this.setAjax(rtDataset);
    }
    
    public void search(IRequestCycle cycle) throws Exception
    {
        IData param = this.getData();
        String searchContent = param.getString("PRODUCT_NAME");
        String lableId = param.getString("LABEL_ID");
        if (StringUtils.isNotBlank(searchContent) && StringUtils.isNotBlank(lableId) )
        {
        	Map<String, String> cond = new HashMap<String, String>();
            if (StringUtils.isNotBlank(lableId))
            {
                cond.put("LABEL_ID", lableId);
            }
            SearchResponse resp = SearchClient.search("SALE_PRODUCT", searchContent, cond, 0, 30);
             
            this.setAjax(resp.getDatas());
        }
    }
    /**
     * REQ201607220020  RED_PACK
     * chenxy3 20160901 
     * SERIAL_NUMBER=18876168056&AMT_VAL=0&PRODUCT_ID=60007581&CAMPN_TYPE=YX03&EPARCHY_CODE=0898&RES_NO=201608310101
     * */
    public void redPackPlaceOrder(IRequestCycle cycle) throws Exception
    {
        IData returnData = new DataMap();
        String sn=getData().getString("SERIAL_NUMBER");
        String userId=getData().getString("USER_ID");
        String prodId=getData().getString("PRODUCT_ID");
        String packId=getData().getString("PACKAGE_ID");
        IData params = new DataMap();
        params.put("SERIAL_NUMBER",sn );
        params.put("AMT_VAL", getData().getString("AMT_VAL"));
        params.put("PRODUCT_ID", getData().getString("PRODUCT_ID"));
        params.put("CAMPN_TYPE", getData().getString("CAMPN_TYPE"));
        params.put("EPARCHY_CODE", getData().getString("EPARCHY_CODE"));
        params.put("RES_NO", getData().getString("RES_NO"));
        IDataset callResults = CSViewCall.call(this, "CS.SaleActiveQuerySVC.redPackActiveOrder", params);
        
        String resultCode="0";
        if(IDataUtil.isNotEmpty(callResults)){
        	resultCode=callResults.getData(0).getString("X_RESULTCODE");
        	if("1".equals(resultCode)){ 
        		returnData.putAll(callResults.getData(0));
        		returnData.put("SERIAL_NUMBER", sn);
        		returnData.put("USER_ID", userId);
        		returnData.put("PRODUCT_ID", prodId);
        		returnData.put("PACKAGE_ID", packId);
        	}
        } 
        setAjax(returnData);
    }
    
    public abstract void setCustInfo(IData custInfo);

    public abstract void setGoods(IDataset goods);

    public abstract void setInfo(IData info);

    public abstract void setProducts(IDataset products);

    public abstract void setYcPackages(IDataset ycPackages);
}
