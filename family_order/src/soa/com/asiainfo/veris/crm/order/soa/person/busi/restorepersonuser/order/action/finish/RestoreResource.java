
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

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
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
        if(IDataUtil.isEmpty(userInfo))
        {
        	userInfo = UserInfoQry.qryUserInfoByUserIdFromHis(userId);
        };
        String netTypeCode = userInfo.getString("NET_TYPE_CODE", "00");
        String productId = "";
        String brandCode = "";
        // 获取业务台帐产品子表
        IDataset tradeProductInfos = TradeProductInfoQry.getTradeProductBySelByTradeModify(tradeId, BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(tradeProductInfos))
        {
            for (int i = 0, count = tradeProductInfos.size(); i < count; i++)
            {
                IData productTrade = tradeProductInfos.getData(i);
                if (StringUtils.equals("1", productTrade.getString("MAIN_TAG")))
                {
                    productId = productTrade.getString("PRODUCT_ID");
                    brandCode = productTrade.getString("BRAND_CODE");
                    break;
                }
            }
        }
        // 获取业务台帐资源子表
        IDataset tradeResInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId, BofConst.MODIFY_TAG_ADD);
        if (IDataUtil.isNotEmpty(tradeResInfos))
        {
            boolean isChangePhone = false;// 是否换号码
            boolean needPossOldPhone = false;// 是否需要重新占有原号码
            boolean isChangeSimCard = false;// 是否换sim卡
            String imsi = "";
            String simCardNo = "";
            String serialNumber = ""; // 要从资源台账中取
            for (int i = 0; i < tradeResInfos.size(); i++)
            {
                IData resData = tradeResInfos.getData(i);
                String strResTypeCode = resData.getString("RES_TYPE_CODE");
                if (StringUtils.equals("0", strResTypeCode))// 手机号码
                {
                    if (StringUtils.equals("1", resData.getString("RSRV_TAG1"))) // 复机时新选的资源
                    {
                        isChangePhone = true;
                    }                    
                    else if (StringUtils.equals("1", resData.getString("RSRV_TAG2"))) // 复机需要重新占有原号码
                    {
                        needPossOldPhone = true;
                    }

                    serialNumber = resData.getString("RES_CODE");
                    //由于复机时号码在use表，完工时号码在idl表造成的报错没有定位到，所以在完工的时候再调资源接口判断改号码在哪。
                    if("310".equals(tradeTypeCode)&& !needPossOldPhone){ 
                        IDataset resDataset = ResCall.restoreCheckMPhone(serialNumber);
                        if (IDataUtil.isNotEmpty(resDataset)&&resDataset.size()>0)
                        {
                        	IData data = resDataset.getData(0);
                            if (StringUtils.equals("5",data.getString("KI_STATE","")) || 
                            		StringUtils.equals("2",data.getString("KI_STATE","")) || 
                            		StringUtils.equals("6",data.getString("KI_STATE",""))){
                            	needPossOldPhone = true; //需要重新预占
								//重新选占
                                ResCall.checkResourceForMphone("0", serialNumber, "0");
                                //重新预占号码
                                ResCall.resEngrossForMphone(serialNumber);
                            }
                        }
                    }
                }
                else if (StringUtils.equals("1", strResTypeCode))// sim卡
                {
                    if (StringUtils.equals("1", resData.getString("RSRV_TAG1", ""))) // 复机时新选的资源
                    {
                        isChangeSimCard = true;
                    }
                    simCardNo = resData.getString("RES_CODE");
                    imsi = resData.getString("IMSI");
                }
            }
            if (isChangePhone || needPossOldPhone)
            {
                // 占用新号码
                if (StringUtils.equals(PersonConst.M2M_NET_TYPE_CODE, netTypeCode))
                {
                    ResCall.resPossessForIOTMphone("0", "0", simCardNo, imsi, serialNumber, "0", productId);
                }
                else
                {
                    ResCall.resPossessForMphone(simCardNo, imsi, serialNumber, productId, tradeId, brandCode, "0");
                }
            }
            else
            {
                // 恢复原号码
                if (StringUtils.equals(PersonConst.M2M_NET_TYPE_CODE, netTypeCode))
                {
                    ResCall.restoreTMobile(serialNumber, productId);
                }
                else
                {
                    ResCall.restoreMobile(serialNumber, productId);
                }
            }

            if (isChangeSimCard)
            {
                // 占用新sim卡
                ResCall.resPossessForSimAgent("0", serialNumber, simCardNo, "0", tradeId,
                        userId, "", "", productId, tradeTypeCode, "");
            }
            else
            {
                // 恢复原sim卡
                if (StringUtils.equals(PersonConst.M2M_NET_TYPE_CODE, netTypeCode))
                {
                    ResCall.restoreTSimcard(simCardNo);
                }
                else
                {
                    ResCall.restoreSimcard(simCardNo);
                }
            }

            // 老号新卡需要调用号码匹配
            if (!isChangePhone && isChangeSimCard)
            {
                // 掉资源接口进行号卡匹配
                if (StringUtils.equals(PersonConst.M2M_NET_TYPE_CODE, netTypeCode))
                {
                    ResCall.matchTSimNumMgr(serialNumber, simCardNo, imsi);
                }
                else
                {
                    ResCall.matchSimNumMgr(serialNumber, simCardNo, imsi);
                }
            }
        }
        else
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "复机完工时没有获取到资源信息！");
        }
    }
}
