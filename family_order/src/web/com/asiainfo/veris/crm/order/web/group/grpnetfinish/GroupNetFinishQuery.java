
package com.asiainfo.veris.crm.order.web.group.grpnetfinish;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GroupNetFinishQuery extends GroupBasePage
{
    public abstract void setCondition(IData data);

    public abstract void setInfo(IData data);

    public abstract void setInfos(IDataset infos);

    public abstract void setTradeCodes(IDataset tradeCodes);

    public abstract void setGroupNetInfos(IData groupNetInfos);

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData initdata = getData("cond", true);

        setCondition(initdata);
    }

    public void queryTradeInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String custId = param.getString("CUST_ID");
        IDataset tradeInfos = new DatasetList();

        IData params = new DataMap();
        params.put("CUST_ID", custId);

        IDataset datas = CSViewCall.call(this, "SS.BookTradeSVC.queryOrderInfo", params);

        for (int i = 0; i < datas.size(); i++)
        {
            IData data = datas.getData(i);
            IData datainfo = new DataMap();
            String product_name = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, data.getString("PRODUCT_ID", ""));
            String str = "订单编号:" + data.getString("ORDER_ID") + " || 产品名称:" + product_name + " || 集团产品编码:" + data.getString("SERIAL_NUMBER") + " || 受理时间:" + data.getString("ACCEPT_DATE");

            datainfo.put("TradeId", str);
            tradeInfos.add(datainfo);
        }
        setTradeCodes(tradeInfos);
    }

    public void queryGroupNetInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String orderId = param.getString("TRADE_ID");

        IData params = new DataMap();
        params.put("ORDER_ID", orderId);
        params.put("BRAND_CODE", "N001");
        
        IDataset datas = CSViewCall.call(this, "SS.BookTradeSVC.queryMainTradeByOrderId", params);

        if (!datas.isEmpty() && datas.size() >0)
        {
            IData tradeInfo = datas.getData(0);
            IData data = new DataMap();

            data.put("TRADE_ID", tradeInfo.get("TRADE_ID"));
            data.put("MANAGER_PHONE", tradeInfo.get("RSRV_STR8"));
            data.put("MANAGER_ADDR", tradeInfo.get("RSRV_STR9"));
            data.put("MANAGER_NAME", tradeInfo.get("RSRV_STR7"));
            data.put("CUST_NAME", tradeInfo.get("CUST_NAME"));
            data.put("CUST_ID", tradeInfo.get("CUST_ID"));

            IData userparam = new DataMap();
            userparam.put("TRADE_ID", tradeInfo.get("TRADE_ID"));

            IDataset tradeuserInfos = CSViewCall.call(this, "SS.BookTradeSVC.queryTradeUserInfo", userparam);

            IData tradeuserinfo = (IData) tradeuserInfos.getData(0);
            data.put("CONTRACT_ID", tradeuserinfo.get("CONTRACT_ID"));

            String product_name = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, tradeInfo.getString("PRODUCT_ID", ""));
            data.put("PRODUCT_NAME", product_name);
            String brand = UpcViewCall.getBrandNameByBrandCode(this, tradeInfo.getString("BRAND_CODE", ""));
//            	StaticUtil.getStaticValue(getVisit(), "TD_S_BRAND", "BRAND_CODE", "BRAND", tradeInfo.getString("BRAND_CODE", ""));
            data.put("BRAND", brand);
            String net_type = UpcViewCall.queryOfferExplainByOfferId(this, "P", tradeInfo.getString("PRODUCT_ID", ""));
//            String net_type = StaticUtil.getStaticValue(getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "PRODUCT_EXPLAIN", tradeInfo.getString("PRODUCT_ID", ""));
            data.put("NET_TYPE", net_type);

            IData acctInfo = UCAInfoIntfViewUtil.qryGrpAcctInfoByAcctId(this, tradeInfo.getString("ACCT_ID"));

            if (null != acctInfo && acctInfo.size() > 0)
            {
                data.put("PAY_NAME", acctInfo.get("PAY_NAME"));
                String pay_mode = StaticUtil.getStaticValue(getVisit(), "TD_S_PAYMODE", "PAY_MODE_CODE", "PAY_MODE", acctInfo.getString("PAY_MODE_CODE", ""));
                data.put("PAY_MODE", pay_mode);
                String bank = StaticUtil.getStaticValue(getVisit(), "TD_B_BANK", "BANK_CODE", "BANK", acctInfo.getString("BANK_CODE", ""));
                data.put("BANK", bank);
                data.put("BANK_ACCT_NO", acctInfo.get("BANK_ACCT_NO"));
            }
            setGroupNetInfos(data);
        }
    }

    public void submitGroupNetInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        IData params = new DataMap();
        String orderId = param.getString("TRADE_ID");
        params.put("ORDER_ID", orderId);
        
        IDataset tradeSetCount = CSViewCall.call(this, "SS.BookTradeSVC.updateOrderState", params);
        IData dataTradeCount = tradeSetCount.getData(0);
        int tradecount = dataTradeCount.getInt("RESULT");

        IData data = new DataMap();
        if (tradecount > 0)
        {
            data.put("RESULTINFO", "集团专网开户竣工登记成功!");
        }
        else
        {
            data.put("RESULTINFO", "集团专网开户竣工登记失败!");
        }
        setAjax(data);

    }
    
    
    public void updateOtherInfoByOrderId(IData params) throws Exception
    {
        params.put("ORDER_ID", params.getString("ORDER_ID"));
        IDataset idataset = CSViewCall.call(this, "SS.BookTradeSVC.queryMainTradeByOrderId", params);

        if (null != idataset && idataset.size() > 0)
        {
            for (int i = 0; i < idataset.size(); i++)
            {
                IData data = idataset.getData(i);
                if ("1".equals(data.getString("OLCOM_TAG")))
                {
                    IDataset userOtherList = CSViewCall.call(this, "SS.BookTradeSVC.queryTradeOtherByTradeId", data);
                    if(null != userOtherList && userOtherList.size() >0){
                        IData userOther = userOtherList.getData(0);
                        userOther.put("RSRV_DATE4", SysDateMgr.getLastDateThisMonth());
                        userOther.put("RSRV_DATE5", SysDateMgr.getLastDateThisMonth());
                        
                        IData other = new DataMap();
                        other.put("TRADE_ID", data.getString("TRADE_ID"));
                        other.put("USER_OTHER",userOther);
                        CSViewCall.call(this, "SS.BookTradeSVC.updateTradeOtherByTradeId", other);
                    }
                }
            }
        }
    }
    

}
