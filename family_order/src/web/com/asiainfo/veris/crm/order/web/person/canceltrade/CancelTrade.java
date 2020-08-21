
package com.asiainfo.veris.crm.order.web.person.canceltrade;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelTrade.java
 * @Description: 业务返销view
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-23 下午7:36:07
 */
public abstract class CancelTrade extends PersonBasePage
{

    /**
     * 取消前校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void cancelBeforeCheck(IRequestCycle cycle) throws Exception
    {
        IData pdData = getData();
        pdData.put(Route.ROUTE_EPARCHY_CODE, pdData.getString("EPARCHY_CODE"));
        IDataset dataset = CSViewCall.call(this, "SS.CancelTradeSVC.commitBeforeCheck", pdData);
        setAjax(dataset);
    }

    /**
     * 执行返销动作
     * 
     * @param cycle
     * @throws Exception
     */
    public void cancelTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pdData = getData();
        pdData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CancelTradeSVC.cancelTradeReg", pdData);
        setAjax(dataset);
    }

    /**
     * 初始化方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pageParam = getData();
        String nowDate = SysDateMgr.getSysDate();
        pageParam.put("cond_START_DATE", nowDate);
        pageParam.put("cond_END_DATE", nowDate);
        this.setCondition(pageParam);

        // 查询可返销的业务类型
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        input.put("CANCEL_TYPE_CODE", pageParam.getString("cond_CANCEL_TYPE_CODE"));
        input.put("NET_TYPE_CODE", pageParam.getString("cond_NET_TYPE_CODE"));
        input.put("RSRV_STR1", pageParam.getString("cond_RSRV_STR1"));
        IDataset undoTradeTypes = CSViewCall.call(this, "CS.TradeTypeInfoQrySVC.queryCancelTradeType", input);
        if (IDataUtil.isNotEmpty(undoTradeTypes))
        {
            for (int i = 0, size = undoTradeTypes.size(); i < size; i++)
            {
                IData tempData = undoTradeTypes.getData(i);
                String tradeType = tempData.getString("TRADE_TYPE");
                String tradeTypeCode = tempData.getString("TRADE_TYPE_CODE");
                tempData.put("TRADE_TYPE", "[" + tradeTypeCode + "]" + tradeType);
            }
            setTradeTypeCodeList(undoTradeTypes);
        }
    }

    /**
     * 查询可返销的订单
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData ajax = new DataMap();
        IData pdData = getData("cond");
        String tradeTypeCode = pdData.getString("TRADE_TYPE_CODE", "");
        if (StringUtils.isBlank(tradeTypeCode)) {//查询所有可返销的业务类型数据
            IData input = new DataMap();
            input.put("EPARCHY_CODE", null);
            input.put("CANCEL_TYPE_CODE", null);
            input.put("NET_TYPE_CODE", null);
            input.put("RSRV_STR1", null);
            IDataset undoTradeTypes = CSViewCall.call(this, "CS.TradeTypeInfoQrySVC.queryCancelTradeType", input);
            IDataset tradeList  = new DatasetList();
            if (!undoTradeTypes.isEmpty()) {
                for (int i = 0; i < undoTradeTypes.size(); i++) {
                    pdData.put("TRADE_TYPE_CODE", undoTradeTypes.getData(i).getString("TRADE_TYPE_CODE"));
                    // 查询用户可返销订单信息
                    IDataset returnInfos = CSViewCall.call(this, "SS.CancelTradeSVC.queryCancelTrade", pdData);
                    if (IDataUtil.isNotEmpty(returnInfos)) {
                        IDataset tradeInfos = returnInfos.getData(0).getDataset(("TRADE_INFO"));
                        if (!IDataUtil.isEmpty(tradeInfos)) {
                            for (int j = 0; j < tradeInfos.size(); j++) {
                            		IData idReturnInfo = tradeInfos.getData(j);
	                                String strTradeTypeCode = idReturnInfo.getString("TRADE_TYPE_CODE", "");
	                                String strBrand = idReturnInfo.getString("BRAND_CODE", "");
	 	                                if("PWLW".equals(strBrand))
	 	                                {
			                                //物联网卡营销活动可以返销
			                                if("240".equals(strTradeTypeCode)){
			                                	tradeList.add(tradeInfos.getData(j));
			                                }
	 	                                }else{
	 	                                	tradeList.add(tradeInfos.getData(j));
	 	                                }
                            }
                        }
                    }
                  
                }
            }
            if(IDataUtil.isEmpty(tradeList)||tradeList.size()==0)
            {
                 ajax.put("QUERY_CODE", "N");
                 ajax.put("QUERY_INFO", "该用户没有可返销数据！");
            }
            setValidCancelTradeList(tradeList);

        } else {

            // 查询用户可返销订单信息
            IDataset returnInfos = CSViewCall.call(this, "SS.CancelTradeSVC.queryCancelTrade", pdData);
            if (IDataUtil.isNotEmpty(returnInfos)) {
                IDataset tradeInfos = returnInfos.getData(0).getDataset(("TRADE_INFO"));
                if(IDataUtil.isEmpty(tradeInfos)){
                	 ajax.put("QUERY_CODE", "N");
                     ajax.put("QUERY_INFO", "该用户没有该业务类型可返销数据！");	 	              
                }else{
                	for (int i = 0; i < tradeInfos.size(); i++) 
                	{
                		IData idReturnInfo = tradeInfos.getData(i);
                		String strBrand = idReturnInfo.getString("BRAND_CODE", "");
                		if("PWLW".equals(strBrand))
                		{
                			if (!"240".equals(tradeTypeCode)) {
                				ajax.put("QUERY_CODE", "N");
                				ajax.put("QUERY_INFO", "该用户没有该业务类型可返销数据！");
                			} else {
                				setValidCancelTradeList(tradeInfos);
                			}
                		}else {
            				setValidCancelTradeList(tradeInfos);
            			}
                		break;
                	}
                }
            } else {
                ajax.put("QUERY_CODE", "N");
                ajax.put("QUERY_INFO", "获取用户资料无数据，请检查该号码是否为正常号码！");
            }
        }

        setAjax(ajax);
    }

    /**
     * @Description 票据返销校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void ticketCancelCheck(IRequestCycle cycle) throws Exception
    {
        IData pdData = getData();
        pdData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset dataset = CSViewCall.call(this, "SS.CancelTradeSVC.ticketCancelCheck", pdData);
        setAjax(dataset);
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setTradeTypeCodeList(IDataset tradeTypeCodeList);

    public abstract void setValidCancelTradeList(IDataset validCancelTradeList);
}
