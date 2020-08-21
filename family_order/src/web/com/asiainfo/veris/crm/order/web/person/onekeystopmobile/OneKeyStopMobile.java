package com.asiainfo.veris.crm.order.web.person.onekeystopmobile;

import com.ailk.common.data.impl.DatasetList;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

import java.math.BigDecimal;
import java.util.Iterator;


public abstract class OneKeyStopMobile extends PersonBasePage
{
    public void queryCustInfo(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IDataset custinfo = CSViewCall.call(this, "SS.OneKeyStopMobileSVC.queryCustInfo", data);
        IData custInfo = custinfo.getData(0);
        String custname = custInfo.getString("CUST_NAME");
        String psptid = custInfo.getString("PSPT_ID");
        StringBuilder custnamesb = new StringBuilder(custname);
        StringBuilder psptidsb = new StringBuilder(psptid);
        custnamesb.replace(1, 2, "*");
        psptidsb.replace(6, 14, "********");
        custInfo.put("CUST_NAME", custnamesb.toString());
        custInfo.put("PSPT_ID", psptidsb.toString());
        setCustInfo(custInfo);;
        setAjax(custInfo);
    }
    
    public void checkOpenDate(IRequestCycle cycle) throws Exception{
        IData data = getData();
        data.put("PATTERN_TAG_YYMM","1");//只校验年月标识 add by liangdg3 at 20191019 for REQ201908120013++一键停机界面优化需求
        IDataset dataset = CSViewCall.call(this, "SS.OneKeyStopMobileSVC.checkOpenDate", data);
        IData result = dataset.getData(0);
        setAjax(result);
    }
    
    public void queryUUInfos(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IDataOutput uuinfosout = CSViewCall.callPage(this, "SS.OneKeyStopMobileSVC.queryUUInfos", data,getPagination("NavBarPart"));
        IDataset uuinfos = uuinfosout.getData();
        long count = uuinfosout.getDataCount();
        setUuInfos(uuinfos);
        setTotalCount(count);
        setAjax(uuinfos);
    }
    
    public void queryLastTrade(IRequestCycle cycle) throws Exception{
        IData data = getData();
        String tradeTypeName = "";
        IDataset lasttrade = CSViewCall.call(this, "SS.OneKeyStopMobileSVC.queryLastTrade", data);
        IData lastTrade = new DataMap();
        if(IDataUtil.isNotEmpty(lasttrade)){
            lastTrade = lasttrade.getData(0);
            if(!"".equals(lastTrade.getString("TRADE_TYPE_CODE",""))){
            	tradeTypeName = StaticUtil.getStaticValue(getVisit(), "TD_S_TRADETYPE", "TRADE_TYPE_CODE", "TRADE_TYPE", lastTrade.getString("TRADE_TYPE_CODE"));
            	lastTrade.put("TRADE_TYPE", tradeTypeName);
            }
        }
        setLastTrade(lastTrade);
        setAjax(lastTrade);
    }
    
	
    public void queryPayLog(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IDataset outset = CSViewCall.call(this, "SS.OneKeyStopMobileSVC.queryPayLog", data);
        IData lastPayLog = new DataMap();
        if(IDataUtil.isNotEmpty(outset)){
            lastPayLog = outset.getData(0);
            //add by liangdg3 at 20191019 for REQ201908120013++一键停机界面优化需求start
            //原金额返回值以分为单位,现需以元为单位,处理RECV_FEE
            String recvFee=lastPayLog.getString("RECV_FEE");
            lastPayLog.put("RECV_FEE", fmMoney(recvFee)+"元");
            //add by liangdg3 at 20191019 for REQ201908120013++一键停机界面优化需求end
        }
        setLastPayLog(lastPayLog);
        setAjax(lastPayLog);
    }
    
    public void queryCdrBil(IRequestCycle cycle) throws Exception{
        IData data = getData();
        data.put("BILL_TYPE", "101");
        data.put("X_PROCURATORAT_TAG", true);
        IDataset dataset = CSViewCall.call(this, "SS.OneKeyStopMobileSVC.queryCdrBil", data);
        IData result = dataset.getData(0);
        setAjax(result);
    }
    
    public void queryMonFeeInfos(IRequestCycle cycle) throws Exception{
        IData data = getData();
        IDataOutput output = CSViewCall.callPage(this, "SS.OneKeyStopMobileSVC.queryMonFeeInfos", data,getPagination("consumeNavBarPart"));
        IDataset MonFeeInfos = output.getData();
        //add by liangdg3 at 20191019 for REQ201908120013一键停机界面优化需求start
        //原金额返回值以分为单位,现需以元为单位,处理TOTAL_FEE
        IDataset monFeeInfosResp=null;
        if(IDataUtil.isNotEmpty(MonFeeInfos)){
            monFeeInfosResp=new DatasetList();
            for (Iterator mit = MonFeeInfos.iterator(); mit.hasNext();){
                IData monFeeInfo = (IData) mit.next();
                String totalFee = monFeeInfo.getString("TOTAL_FEE");
                monFeeInfo.put("TOTAL_FEE", fmMoney(totalFee)+"元");
                monFeeInfosResp.add(monFeeInfo);
            }
        }
        long count = output.getDataCount();
        setMonFeeInfos(monFeeInfosResp);
        setTotalCount(count);
        setAjax(monFeeInfosResp);
        //add by liangdg3 at 20191019 for REQ201908120013一键停机界面优化需求end
    }
    
    public void onTradeSubmit(IRequestCycle requestCycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
            data.put("SERIAL_NUMBER", data.getString("cond_SERIAL_NUMBER"));
        }
        data.put("TRADE_TYPE_CODE","131");
        data.put("OPR_SOURCE","M");
        IDataset dataset = CSViewCall.call(this, "SS.ChangeSvcStateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    /**
     * 格式化为金额0.00格式
     *
     * @param amount
     *            金额单位：分
     * @return
     */
    private  String fmMoney(String amount) {
        if(StringUtils.isNotBlank(amount))
        {
            return new BigDecimal(amount).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP).toString();
        }else
        {
            return "0.00";
        }
    }
    public abstract void setCustInfo(IData custInfo);
    public abstract void setLastTrade(IData custInfo);
    public abstract void setUuInfos(IDataset uuInfos);
    public abstract void setUuInfo(IData uuInfo);
    public abstract void setMonFeeInfos(IDataset monFeeInfos);
    public abstract void setMonFeeInfo(IData monFeeInfo);
    public abstract void setLastPayLog(IData lastPayLog);
    public abstract void setTotalCount(long totalCount);
}
