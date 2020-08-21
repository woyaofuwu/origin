
package com.asiainfo.veris.crm.iorder.web.merch.SingleMarketingActivity;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MarketingActivityDetail extends PersonBasePage
{
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setCount(long count);

    public abstract void setSmsInfo(IData smsInfo);

    /**
     * 页面初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        //duhj data.putAll(getGiftFeeCutoffValue());
        IData PMoffer = UpcViewCall.queryOfferByOfferId(this, "K", data.getString("SALE_PACKAGE_ID", ""),"");//UpcViewCall.queryOfferNameByOfferId("K", data.getString("SALE_PACKAGE_ID", ""));

        String offerName = PMoffer.getString("OFFER_NAME");//UpcViewCall.queryOfferNameByOfferId("K", data.getString("SALE_PACKAGE_ID", ""));        
        String activeType = data.getString("ACTIVE_TYPE", "");
        if ("WD".equals(activeType))
        {
            data.put("AUTH_TYPE", "04");
        }
        else
        {
            data.put("AUTH_TYPE", "00");
        }
        data.put("SALE_PACKAGE_NAME", offerName);
        setInfo(data);
    }

    /**
     * 关闭页面时如果业务受理未完成，需要释放预占的资源信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void releaseGoodsInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String serialNumber = data.getString("SERIAL_NUMBER", "");
        String goodsImei = data.getString("IMEI", "");
        String eparchyCode = data.getString("EPARCHY_CODE", "");
        String packageId = data.getString("PACKAGE_ID", "");
        String campnType = data.getString("CAMPN_TYPE", "");
        String valueCardList = data.getString("VALUE_CARD_LIST", "");
        String chnalType = data.getString("CHANL_TYPE");

        IData input = new DataMap();
        input.put("VALUE_CARD_LIST", valueCardList);
        input.put("SERIAL_NUMBER", serialNumber);
        input.put("IMEI", goodsImei);
        input.put("PACKAGE_ID", packageId);
        input.put("CAMPN_TYPE", campnType);
        input.put("CHNAL_TYPE", chnalType);
        input.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        CSViewCall.call(this, "SS.SaleActiveQuerySVC.OccupyReleaseGoods", input);
    }


    /**
     * 业务提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.putAll(new DataMap(data.getString("SALEACITVEDATA")));
        data.remove("SALEACITVEDATA");
        IDataset rtDataset = CSViewCall.call(this, "SS.SaleActiveRegSVC.tradeReg", data);
        this.setAjax(rtDataset);
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
}
