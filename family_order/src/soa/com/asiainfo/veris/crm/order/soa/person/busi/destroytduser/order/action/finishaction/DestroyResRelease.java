
package com.asiainfo.veris.crm.order.soa.person.busi.destroytduser.order.action.finishaction;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: DestroyResRelease.java
 * @Description: 销户资源释放finishaction
 * @version: v1.0.0
 * @author: liuke
 * @date: 下午09:06:02 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-30 liuke v1.0.0 修改原因
 */
public class DestroyResRelease implements ITradeFinishAction
{

    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String isYHFHUser = mainTrade.getString("RSRV_STR3", "0");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        String removeTag = "2";// 立即销户 --给资源接口用，只有真正销户多少天之后才能回收号码
        if (StringUtils.equals("7230", tradeTypeCode))
        {
            removeTag = "3";// 欠费预销--
        }

        if (StringUtils.equals("1", isYHFHUser))// 是影号副号销户
        {
            String serialNumber = mainTrade.getString("SERIAL_NUMBER");
            // 副号码释放
            // ResCall.releaseOneCardMultiDeputy(serialNumber, serialNumber, "2");
        }
        else
        {
            // 正常用户资源释放
            IDataset resTradeInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId, BofConst.MODIFY_TAG_DEL);
            if (IDataUtil.isNotEmpty(resTradeInfos))
            {
                String userId = mainTrade.getString("USER_ID");
                IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
                boolean isTNetFlag = userInfo.getString("NET_TYPE_CODE", "").equals("07");// 标记是否物联网

                for (int i = 0; i < resTradeInfos.size(); i++)
                {
                    IData resTemp = resTradeInfos.getData(i);
                    if (StringUtils.equals("0", resTemp.getString("RES_TYPE_CODE")))
                    {
                        if (isTNetFlag)
                        {
                            ResCall.destroyRealeaseTMphone(resTemp.getString("RES_CODE"), removeTag);
                        }
                        else
                        {
                            ResCall.destroyRealeaseMphone(resTemp.getString("RES_CODE"), removeTag);
                        }
                    }
                    else if (StringUtils.equals("1", resTemp.getString("RES_TYPE_CODE")))
                    {
                        if (isTNetFlag)
                        {
                            ResCall.destroyRealeaseTSimCard(resTemp.getString("RES_CODE"));
                        }
                        else
                        {
                            ResCall.destroyRealeaseSimCard(resTemp.getString("RES_CODE"),"");
                        }
                    }
                }
            }
        }
    }
}
