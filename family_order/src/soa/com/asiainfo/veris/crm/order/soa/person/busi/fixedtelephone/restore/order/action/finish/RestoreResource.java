
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: RestoreResource.java
 * @Description: 复机资源恢复(和实占)
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-22 上午10:40:14
 */
public class RestoreResource implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        // 资源占用
        String strTradeId = mainTrade.getString("TRADE_ID");
        // 获取业务台帐资源子表
        IDataset tradeResInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(strTradeId, BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(tradeResInfos))
        {
            String serialNumber = mainTrade.getString("SERIAL_NUMBER");
            String userId = mainTrade.getString("USER_ID");
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
            boolean isTNetFlag = userInfo.getString("NET_TYPE_CODE", "").equals("07");// 标记是否物联网

            this.ResPossess(tradeResInfos, serialNumber, isTNetFlag);// 新资源实占
            this.resRestore(tradeResInfos, isTNetFlag);// 老资源恢复
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机完工时没有获取到资源信息！");
        }
    }

    /**
     * 资源实占(处理在复机时可能更换sim卡的情况)
     * 
     * @param resTradeData
     * @throws Exception
     */
    private void ResPossess(IDataset resTradeData, String serialNumber, boolean isTNetFlag) throws Exception
    {
        boolean NeedMatch = false;// 是否需要号码匹配
        String imsi = "";
        String simcard = "";
        for (int i = 0; i < resTradeData.size(); i++)
        {
            // 复机时新选的资源
            if (StringUtils.equals("1", resTradeData.getData(i).getString("RSRV_TAG1", "")))
            {
                String strResTypeCode = resTradeData.getData(i).getString("RES_TYPE_CODE");
                if (StringUtils.equals("0", strResTypeCode))// 换手机号码
                {
                    if (isTNetFlag)// 物联网
                    {
                    }
                    else
                    {
                    }
                }
                else if (StringUtils.equals("1", strResTypeCode))// 换sim卡
                {
                    NeedMatch = true;
                    simcard = resTradeData.getData(i).getString("RES_CODE");
                    imsi = resTradeData.getData(i).getString("IMSI");
                    if (isTNetFlag)// 物联网
                    {
                        // ResCall.resPossessForIOTSim("0", "0", serialNumber, simcard, strResTypeCode);
                    }
                    else
                    {
                        // ResCall.resPossessForSim("0", "0", serialNumber, simcard, strResTypeCode);
                    }
                }
            }
        }
        if (NeedMatch)// 需要号码匹配
        {
            // 掉资源接口进行号卡匹配
            if (isTNetFlag)// 物联网
            {
                // ResCall.matchTSimNumMgr(serialNumber, simcard, imsi);
            }
            else
            {
                // ResCall.matchSimNumMgr(serialNumber, simcard, imsi);
            }
        }
    }

    /**
     * 资源恢复（处理复机时不变化的资源）
     * 
     * @param resTradeData
     * @throws Exception
     */
    private void resRestore(IDataset resTradeData, boolean isTNetFlag) throws Exception
    {
        for (int i = 0; i < resTradeData.size(); i++)
        {
            // 代表是复机时使用的原资源
            if (!StringUtils.equals("1", resTradeData.getData(i).getString("RSRV_TAG1", "")))
            {
                String strResTypeCode = resTradeData.getData(i).getString("RES_TYPE_CODE");
                String resCode = resTradeData.getData(i).getString("RES_CODE");
                if (StringUtils.equals("0", strResTypeCode))// 号码
                {
                    if (isTNetFlag)// 物联网
                    {
                        // ResCall.restoreTMobile(resCode);
                    }
                    else
                    {
                        // ResCall.restoreMobile(resCode);
                    }
                }
                else if (StringUtils.equals("1", strResTypeCode))// sim卡
                {
                    if (isTNetFlag)// 物联网
                    {
                        // ResCall.restoreTSimcard(resCode);
                    }
                    else
                    {
                        // ResCall.restoreSimcard(resCode);
                    }
                }
            }
        }
    }
}
