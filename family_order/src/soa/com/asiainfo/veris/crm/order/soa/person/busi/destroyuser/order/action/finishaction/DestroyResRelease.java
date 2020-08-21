
package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.mobileuseful.BestUseMobileBean;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: DestroyResRelease.java
 * @Description: 销户资源释放finishaction
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-9 下午4:49:48
 */
public class DestroyResRelease implements ITradeFinishAction
{

	protected static Logger log = Logger.getLogger(BestUseMobileBean.class);
	
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String isYHFHUser = mainTrade.getString("RSRV_STR3", "0");
        IDataset userTradeDataset = TradeUserInfoQry.getTradeUserByTradeId(tradeId);
        if (IDataUtil.isEmpty(userTradeDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_225);
        }
        oneNoOneTerminal(mainTrade);
        boolean bUserNpFlag = false;// 是否携转用户
        IData userTradeData = userTradeDataset.getData(0);
        //存在多条tf_b_trade_user时，有可能取的不是主台账号码的记录，所以加一下代码
        for(int i = 0; i < userTradeDataset.size(); i++){
        	String serialNumberA = mainTrade.getString("SERIAL_NUMBER","0");
        	String serialNumberB = userTradeDataset.getData(i).getString("SERIAL_NUMBER", "0");
        	if(StringUtils.equals(serialNumberA, serialNumberB)){
        		userTradeData = userTradeDataset.getData(i);
        	}
        }
        String userTagSet = userTradeData.getString("USER_TAG_SET", "");
        if (StringUtils.equals("1", StringUtils.substring(userTagSet, 0, 1)) || StringUtils.equals("6", StringUtils.substring(userTagSet, 0, 1)))// 取第一位携转标识
        // 1-已携入
        // 6-携回
        {
            bUserNpFlag = true;
        }
        String removeTag = userTradeData.getString("REMOVE_TAG");
        boolean isTNetFlag = StringUtils.equals("07", userTradeData.getString("NET_TYPE_CODE", ""));// 标记是否物联网

        if (StringUtils.equals("1", isYHFHUser))// 是影号副号销户
        {
        }
        else
        {
            // 正常用户资源释放
            IDataset resTradeInfos = TradeResInfoQry.queryTradeResByTradeIdAndModifyTag(tradeId, BofConst.MODIFY_TAG_DEL);
            if (IDataUtil.isNotEmpty(resTradeInfos))
            {
                for (int i = 0, count = resTradeInfos.size(); i < count; i++)
                {
                    IData resTemp = resTradeInfos.getData(i);
                    if (StringUtils.equals("0", resTemp.getString("RES_TYPE_CODE")))
                    {
                        if (isTNetFlag)
                        {
                            ResCall.destroyRealeaseTMphone(resTemp.getString("RES_CODE"), removeTag);
                        }
                        else if (!bUserNpFlag)
                        {
                            ResCall.destroyRealeaseMphone(resTemp.getString("RES_CODE"), removeTag);
                        }
                        else if (bUserNpFlag)
                        {
                            ResCall.modifyNpMphoneInfo("", "7", resTemp.getString("RES_CODE"));
                        }
                    }
                    else if (StringUtils.equals("1", resTemp.getString("RES_TYPE_CODE")))
                    {
                        if (isTNetFlag)
                        {
                            ResCall.destroyRealeaseTSimCard(resTemp.getString("RES_CODE"));
                        }
                        else if (!bUserNpFlag)
                        {
                            String activeTag = "";
                            if (StringUtils.equals("1", mainTrade.getString("RSRV_STR10")))
                            { 
                                activeTag = "1";
                            }
                            ResCall.destroyRealeaseSimCard(resTemp.getString("RES_CODE"),activeTag);
                        }
                        else if (bUserNpFlag)
                        {
                            ResCall.modifyNpSimInfo(resTemp.getString("RES_CODE"), "5", "", "", "");
                        }
                    }
                }
            }
        }
    }

	private void oneNoOneTerminal(IData mainTrade) throws Exception {
		String oldEid = "";
        String oldIccid = "";
        IDataset resTrades = TradeResInfoQry.queryAllTradeResByTradeId(mainTrade.getString("TRADE_ID"));
        for(int i=0,j=resTrades.size();i<j;i++){
        	IData resTrade = resTrades.getData(i);
        	if("E".equals(resTrade.getString("RES_TYPE_CODE"))){
        		if("OneNoOneTerminal".equals(resTrade.getString("RSRV_STR1"))){
        			String [] oldEids = resTrade.getString("RSRV_STR2").split("@");
        			oldEid = oldEids[0];
    				oldIccid = resTrade.getString("RES_CODE");
    				break;
        		}							
			}
        }
        
        if(StringUtils.isEmpty(oldEid)){
        	return;
        }
        String sysData = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        String orderid = "HN"+sysData;
        IData param = new DataMap();
		param.put("orderId", orderid);
		param.put("MSISDN", mainTrade.getString("SERIAL_NUMBER"));
		param.put("eid1", oldEid);
		param.put("ICCID1", oldIccid);
		param.put("bizType", "003");//销户
		param.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		
		IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", "crm.ABILITY.CIP73");
        IDataset urls = Dao.qryBySql(AbilityEncrypting.getInterFaceSQL, paramurl, "cen");
        String url = "";
        
        if (urls != null && urls.size() > 0)
        {
           url = urls.getData(0).getString("PARAM_VALUE", "");
         }
         else
         {
             CSAppException.appError("-1", "crm.feedback接口地址未在TD_S_BIZENV表中配置");
         }
        
        String apiAddress = url;
        
        //调用能开接口
		AbilityEncrypting.callAbilityPlatCommon(apiAddress,param);
		String staffId = mainTrade.getString("TRADE_STAFF_ID");
		setOneNumOneDeviceCache(orderid,staffId);
	}
    private void setOneNumOneDeviceCache(String ospOrderId, String StaffId) throws Exception	{
		IData param = new DataMap();
		param.put("STAFF_ID", StaffId); //保存补换esim设备，营业员工号
		SharedCache.set("OneNumOneDevice_" + ospOrderId, param, 1800); // 秒
	}	
}
