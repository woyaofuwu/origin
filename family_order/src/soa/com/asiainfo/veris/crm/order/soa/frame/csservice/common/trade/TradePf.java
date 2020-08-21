
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import java.util.Iterator;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.callpf.GetPfInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.pfctrl.PfCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.pfctrl.PfCtrlFilterByExpr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.pfctrl.PfCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMbDisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.DealPocBizBean;


public final class TradePf
{
    public static IDataset backPf(IData pfBackData) throws Exception
    {
        String intfTag = pfBackData.getString("INTF_TAG", "");
        String cancelTag = "";

        // INTF_TAG:0,正常；1，变更；2，撤消
        if ("0".equals(intfTag) || "1".equals(intfTag))
        {
            cancelTag = "0";
        }
        else if ("2".equals(intfTag))
        {
            cancelTag = "2";
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "INTF_TAG");
        }

        // 订单标识
        String orderId = pfBackData.getString("ORDER_ID");
        String tradeId = pfBackData.getString("TRADE_ID");

        // 受理月份(必须用orderid的)
        String acceptMonth = StrUtil.getAcceptMonthById(orderId);

        // 返回信息
        String execCode = pfBackData.getString("EXEC_CODE");
        String execDetailDesc = pfBackData.getString("EXEC_DESC");

        String activityCode = "";
        String phaseTg = pfBackData.getString("PHASE_TAG", "1");

        if ("1".equals(phaseTg))
        {
            activityCode = "backPfe"; // 服开最终实时回单
        }
        else if ("2".equals(phaseTg))
        {
            activityCode = "backPfp"; // 服开阶段实时回单
        }
        else if ("3".equals(phaseTg))
        {
            activityCode = "backPfpE"; // 服开阶段性回复
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_695, phaseTg);
        }

        IDataset mainTradeList = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, cancelTag);

        // chengxf2:由于PBOSS不管返销撤销都是返回2,增加撤单的处理。
        if (StringUtils.equals("2", cancelTag) && IDataUtil.isEmpty(mainTradeList))
        {
            cancelTag = "3";
            mainTradeList = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, cancelTag);
        }

        // 如果不为空，可能是开环，也可能是闭环
        if (IDataUtil.isNotEmpty(mainTradeList))
        {
            IData mainTrade = mainTradeList.getData(0);

            // 开闭环标记：1-闭环，0-开环
            String wait = mainTrade.getString("PF_WAIT", "");

            if ("1".equals(wait))
            {
                // 默认等待完工状态
                String subscribeState = "P";

                if ("0".equals(execCode)) // 成功
                {
                    // 回单时动作，同时返回订单后续状态
                    subscribeState = pfBackActions(mainTrade, pfBackData);
                }
                else
                {
                    // 服务开通错单
                    subscribeState = "M";
                    //更新订单优先级表状态
                    TradePriority.updStateByTrade(tradeId, TradePriority.BACK_PF_FAIL);
                    
                    wlwDealBackActions(mainTrade, pfBackData);
                }

                // 更新订单状态
                TradeMag.updTradeStateBack(tradeId, acceptMonth, cancelTag, subscribeState);

                // 批量业务标识
                String batchId = mainTrade.getString("BATCH_ID");

                if (StringUtils.isNotBlank(batchId))
                {
                	if ("0".equals(execCode)){ // 成功
	                    // 回写批量表状态，已回单
	                    TradeMag.updTradeBatDealByBatchId(batchId, "B", "服开回单成功");
                	}else{
                		// 回写批量表状态，已回单
	                    TradeMag.updTradeBatDealByBatchId(batchId, "M", "服务开通错单");
                	}
                }
            }
        }
        else
        {
            String  businessType = "";
            
            if (IDataUtil.isNotEmpty(pfBackData.getDataset("BUSINESS_TYPE")))
            {
                businessType = (String) pfBackData.getDataset("BUSINESS_TYPE").get(0);
            }
            
            //无手机宽带开户
            if ("680".equals(businessType))
            {
                IData tradeBhInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId,"0", "0898");
                
                if (IDataUtil.isNotEmpty(tradeBhInfo))
                {
                    IData inParam = new DataMap();
                    
                    String deviceId = "";
                    String portId = "";
                    String standAddress = "";
                    
                    String finishDate = (String) pfBackData.getDataset("FINISH_DATE").get(0);
                    String construction_addr = (String) pfBackData.getDataset("CONSTRUCTION_ADDR").get(0);
                    String const_staff_id = (String) pfBackData.getDataset("CONST_STAFF_ID").get(0);
                    String const_phone = (String) pfBackData.getDataset("CONST_PHONE").get(0);
                    String rsrv_tag3 = (String) pfBackData.getDataset("RSRV_TAG3").get(0);
                    
                    
                    IDataset addressInfos = pfBackData.getDataset("RM_INSTALL_ADDRESS");
                    
                    String pfRtnResultCode = pfBackData.getString("EXEC_CODE");
                    
                    if (IDataUtil.isNotEmpty(pfBackData.getDataset("PFCRM_DEVICE_ID")))
                    {
                        deviceId = (String) pfBackData.getDataset("PFCRM_DEVICE_ID").get(0);
                    }
                    
                    if (IDataUtil.isNotEmpty(pfBackData.getDataset("PFCRM_PORT_ID")))
                    {
                        portId = (String) pfBackData.getDataset("PFCRM_PORT_ID").get(0);
                    }
                    
                    if (IDataUtil.isNotEmpty(addressInfos))
                    {
                        standAddress = (String) addressInfos.get(0);
                    }
                    
                    TradeMag.intTradePbossFinish(tradeId, acceptMonth, cancelTag, pfRtnResultCode, finishDate);
                    
                    
                    //跳过校验规则
                    inParam.put("SKIP_RULE", "TRUE");
                    
                    //无手机宽带开户激活
                    inParam.put("TRADE_TYPE_CODE", "688");
                    inParam.put("ORDER_TYPE_CODE", "688");
                    
                    inParam.put("SERIAL_NUMBER", tradeBhInfo.getString("SERIAL_NUMBER"));
                    inParam.put("CREATE_USER_TRADE_ID", tradeId);
                    inParam.put("FINISH_DATE", finishDate);
                    
                    inParam.put("DEVICE_ID", deviceId);
                    inParam.put("PORT_ID", portId);
                    inParam.put("STAND_ADDRESS", standAddress);
                    inParam.put("CONSTRUCTION_ADDR", construction_addr);
                    inParam.put("CONST_STAFF_ID", const_staff_id);
                    inParam.put("CONST_PHONE", const_phone);
                    inParam.put("RSRV_TAG3", rsrv_tag3);
                    
                    IDataset hisTrade = BroadBandInfoQry.qryTradeHisInfoByTradeId(tradeId);
                    if (IDataUtil.isNotEmpty(hisTrade)) {
                        CSBizBean.getVisit().setStaffId(hisTrade.getData(0).getString("TRADE_STAFF_ID"));
                        CSBizBean.getVisit().setDepartId(hisTrade.getData(0).getString("TRADE_DEPART_ID"));
                        CSBizBean.getVisit().setCityCode(hisTrade.getData(0).getString("TRADE_CITY_CODE"));
                    }
                    //生成无手机宽带激活工单
                    CSAppCall.call("SS.NoPhoneWideUserActiveRegSVC.tradeReg", inParam);
                }
            }
        }

        // 记录服开回单结果
        TradeMag.traceLog(orderId, tradeId, acceptMonth, null, activityCode, execCode, execDetailDesc);

        return new DatasetList();
    }

    private static void backPfBBoss(IData mainTrade, IData pfbackdata) throws Exception
    {
        // bboss服务开通是否有回单数据
        boolean bboss = BizEnv.getEnvBoolean("crm.backpf.bboss", true);

        if (bboss == true)
        {
            CSAppCall.call("CS.BBossOrderDealSVC.dealBbossRspMessage", pfbackdata);
        }
    }

    private static void backPfBBossCM(IData mainTrade, IData pfbackdata) throws Exception
    {
        CSAppCall.call("CM_DealBbossRsp", pfbackdata);
    }

    private static void backPfBDTradeSync(IData pfBackData) throws Exception
    {
        CSAppCall.call("SS.BroadBandCreateSVC.updBDTrade", pfBackData);
    }

    private static void backPfIotECSync(IData mainTrade) throws Exception
    {
        CSAppCall.call("SS.IotECSyncBackpfSVC.updSyncState", mainTrade);
    }

    private static void backPfIotECFailSync(IData mainTrade) throws Exception
    {
        CSAppCall.call("SS.IotECSyncBackpfSVC.updSyncFailState", mainTrade);
    }
    
    public static IData getPf(String orderId, String acceptMonth, String cancelTag) throws Exception
    {
        return GetPfInfo.getPfData(orderId, acceptMonth, cancelTag);
    }

    private static String getPfLog(IData pfData) throws Exception
    {
        String pfObjs[] = pfData.getNames();

        StringBuilder pfLog = new StringBuilder(200);

        pfLog.append("size=");

        for (int i = 0, len = pfObjs.length; i < len; i++)
        {
            Object data = pfData.get(pfObjs[i]);

            if (data instanceof IDataset)
            {
                pfLog.append(pfObjs[i]);
                pfLog.append(":");
                pfLog.append(((IDataset) data).size());
                pfLog.append(",");
            }
        }

        return pfLog.toString();
    }

    private static boolean isOrderWaitTrade(IData order, IDataset tradeAll) throws Exception
    {
        if (IDataUtil.isEmpty(tradeAll))
        {
            return false;
        }

        IData trade = null;
        String userId = "";
        String orderId = "";
        String routeId = "";
        String execTime = order.getString("EXEC_TIME");

        for (int i = 0, isize = tradeAll.size(); i < isize; i++)
        {
            trade = tradeAll.getData(i);

            userId = trade.getString("USER_ID");
            orderId = trade.getString("ORDER_ID");
            routeId = trade.getString("ROUTE_ID");

            IDataset tradesByUser = TradeInfoQry.getTradeWaitByUser(userId, execTime, orderId, routeId);

            if (IDataUtil.isEmpty(tradesByUser))
            {
                continue;
            }

            int iCount = tradesByUser.getData(0).getInt("COUNT", 0);

            if (iCount > 0)
            {
                return true;
            }
        }

        return false;
    }

    public static boolean isPf(String tradeTypeCode, IData bd) throws Exception
    {
        // 是否服务开通,Y确定发，N确定不发，U不确定发(没有配置)
        String isPfTag = TradeCtrl.getCtrlString(tradeTypeCode, TradeCtrl.CTRL_TYPE.IS_PF, TradeCtrl.CTRL_TYPE.IS_PS_YESORNO.getValue());

        if (isPfTag.equals(TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue()))
        {
            return true;

        }
        else if (isPfTag.equals(TradeCtrl.CTRL_TYPE.IS_PF_NO.getValue()))
        {

            return false;

        }
        else
        {

            PfCtrlInfo pfCtrlInfo = PfCtrlBean.getPfCtrlInfo();
            IData pfData = pfCtrlInfo.getPfCtrlInfo();

            Iterator it = bd.keySet().iterator();

            IDataset idsRecord = null;

            while (it.hasNext())
            {
                String tableName = (String) it.next();

                idsRecord = bd.getDataset(tableName);

                if (IDataUtil.isEmpty(idsRecord))
                {
                    continue;
                }

                // svc
                if (tableName.equals(TradeTableEnum.TRADE_SVC.getValue()))
                {
                    isPfTag = isPfSvc(idsRecord);

                    if (isPfTag.equals(TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue()))
                    {
                        return true;
                    }
                }

                // svssate
                if (tableName.equals(TradeTableEnum.TRADE_SVCSTATE.getValue()))
                {
                    isPfTag = isPfSvcState(idsRecord);

                    if (isPfTag.equals(TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue()))
                    {
                        return true;
                    }
                }

                // discnt
                if (tableName.equals(TradeTableEnum.TRADE_DISCNT.getValue()))
                {
                    // discnt
                    isPfTag = isPfDiscnt(idsRecord);

                    if (isPfTag.equals(TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue()))
                    {
                        return true;
                    }
                }

                // other
                IData pfConfData = pfData.getData(tableName);

                if (IDataUtil.isEmpty(pfConfData))// 没有配置
                    continue;
                else
                {
                    String field = pfCtrlInfo.getPfField(tableName);
                    String fieldExpre = pfCtrlInfo.getFieldExpre(tableName);// 表达式

                    if (!"-1".equals(field))
                    {
                        // 单个字段值匹配
                        String value = pfCtrlInfo.getFieldValue(tableName);

                        isPfTag = isPfOther(idsRecord, field, value);

                        if (isPfTag.equals(TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue()))
                        {
                            return true;
                        }
                    }
                    else
                    {
                        if (!StringUtils.isBlank(fieldExpre))
                        {
                            // 走表达式
                            IDataset mvelData = PfCtrlFilterByExpr.filterPfCtrlInfos(IDataUtil.idToIds(pfConfData), idsRecord);

                            if (IDataUtil.isNotEmpty(mvelData))
                            {
                                return true;
                            }
                        }
                        else
                        {
                            // 整表
                            if (IDataUtil.isNotEmpty(pfConfData) && IDataUtil.isNotEmpty(idsRecord))
                            {
                                return true;
                            }
                        }
                    }

                }
            }

            // other

            return false;
        }
    }

    private static String isPfDiscnt(IDataset ids) throws Exception
    {
        for (int i = 0, isize = ids.size(); i < isize; i++)
        {
            IData map = ids.getData(i);
            String discntCode = map.getString("DISCNT_CODE", "");
            String isNeedPf = map.getString("IS_NEED_PF", "");
            if (BofConst.IS_NEED_PF_YES.equals(isNeedPf))
            {
                return TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue();
            }

            IData out = UDiscntInfoQry.getDiscntInfoByPk(discntCode);

            if (IDataUtil.isNotEmpty(out))
            {
                // 服务接口方式：0-无接口,1-联机指令接口
                String intf_mode = out.getString("INTF_MODE", "0");

                if (StringUtils.isNotEmpty(intf_mode) && !"0".equals(intf_mode))
                {
                    return TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue();
                }
            }
        }

        return TradeCtrl.CTRL_TYPE.IS_PF_NO.getValue();
    }

    private static String isPfOther(IDataset ids, String field, String value) throws Exception
    {
        for (int i = 0, isize = ids.size(); i < isize; i++)
        {
            IData map = ids.getData(i);
            String fieldValue = map.getString(field, "");

            if (value.indexOf("," + fieldValue + ",") > -1)
            {
                return TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue();
            }

        }

        return TradeCtrl.CTRL_TYPE.IS_PF_NO.getValue();
    }

    private static String isPfSvc(IDataset ids) throws Exception
    {
        for (int i = 0, isize = ids.size(); i < isize; i++)
        {
            IData map = ids.getData(i);
            String serviceId = map.getString("SERVICE_ID", "");
            String isNeedPf = map.getString("IS_NEED_PF", "");
            if (BofConst.IS_NEED_PF_YES.equals(isNeedPf))
            {
                return TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue();
            }

            IData out = USvcInfoQry.qryServInfoBySvcId(serviceId);

            if (IDataUtil.isNotEmpty(out))
            {
                // 服务接口方式：0-无接口,1-联机指令接口
                String intf_mode = out.getString("INTF_MODE");

                if (StringUtils.isNotEmpty(intf_mode) && !"0".equals(intf_mode))
                {
                    return TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue();
                }
            }
        }

        return TradeCtrl.CTRL_TYPE.IS_PF_NO.getValue();
    }

    private static String isPfSvcState(IDataset ids) throws Exception
    {
        for (int i = 0, isize = ids.size(); i < isize; i++)
        {
            IData map = ids.getData(i);
            String serviceId = map.getString("SERVICE_ID", "");
            String stateCode = map.getString("STATE_CODE", "");
            // 如果td_s_servicestate配置了intf_mode为0则也不发服务开通
            String intfMode = USvcStateInfoQry.getSvcIntfModeBySvcIdStateCode(serviceId, stateCode);

            if (StringUtils.isBlank(intfMode))
            {
                continue;
            }
            if (StringUtils.isNotEmpty(intfMode) && "0".equals(intfMode))
            {
                continue;
            }
            else
            {
                return TradeCtrl.CTRL_TYPE.IS_PF_YES.getValue();
            }
        }

        return TradeCtrl.CTRL_TYPE.IS_PF_NO.getValue();
    }

    private static String pfBackActions(IData mainTrade, IData pfBackData) throws Exception
    {
        // 默认等待完工状态
        String subscribeState = "P";

        // 订单标识
        String orderId = mainTrade.getString("ORDER_ID");
        String tradeId = mainTrade.getString("TRADE_ID");

        // 业务类型
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        String cancelTag = mainTrade.getString("CANCEL_TAG");

        // 受理月份
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);

        if ("4690".equals(tradeTypeCode) || "4691".equals(tradeTypeCode) || "4693".equals(tradeTypeCode) || "4694".equals(tradeTypeCode) || "4695".equals(tradeTypeCode) || "4697".equals(tradeTypeCode) || "2310".equals(tradeTypeCode) || "2330".equals(tradeTypeCode)
        		|| "2311".equals(tradeTypeCode) || "2331".equals(tradeTypeCode) || "2312".equals(tradeTypeCode) || "2332".equals(tradeTypeCode) || "2325".equals(tradeTypeCode) || "2346".equals(tradeTypeCode) || "2351".equals(tradeTypeCode) || "2352".equals(tradeTypeCode))
        {
            // BBOSS特殊处理
            backPfBBoss(mainTrade, pfBackData);
            subscribeState = "W";
            // 对于和对讲业务
            if (DealPocBizBean.isPocBiz(mainTrade.getString("TRADE_ID")))
            {
                subscribeState = "P";
            }
            //国际流量统付叠加包发服开的特殊处理（如果是一级BOSS过来的数据）
        	if("6".equals(mainTrade.getString("IN_MODE_CODE")))
        	{
        		IDataset tradeGrpMerchMbDisInfos = TradeGrpMerchMbDisInfoQry.getTradeGrpMerchMbDisByTradeId(mainTrade.getString("TRADE_ID"));
        		if(IDataUtil.isNotEmpty(tradeGrpMerchMbDisInfos))
        		{
        			String productSpecCode = tradeGrpMerchMbDisInfos.getData(0).getString("PRODUCT_SPEC_CODE");
        			if("99910".equals(productSpecCode))
        			{
        				subscribeState = "P";
        			}
        		}
        	}

        }
        else if ("5025".equals(tradeTypeCode))
        {
            // 集团资料同步
            backPfBBossCM(mainTrade, pfBackData);
        }
        else if ("2026".equals(tradeTypeCode))
        {
            subscribeState = "W";
        }
        else if ("5777".equals(tradeTypeCode))
        {
            backPfIotECSync(mainTrade);
        }
        else if ("1002".equals(tradeTypeCode))// 移动光宽带开户
        {
            backPfBDTradeSync(pfBackData);
            String resultCode = pfBackData.getString("resultCode");
            if (resultCode.startsWith("[\"") && resultCode.endsWith("\"]"))
            {
                resultCode = resultCode.substring(2, resultCode.length() - 2);
            }
            if ("1".equals(resultCode))
            {
                subscribeState = "W";// 人工干预状态，等待营业员宽带开户返销退款
                String failReason = pfBackData.getString("failReason");
                if (failReason.startsWith("[\"") && failReason.endsWith("\"]"))
                {
                    failReason = failReason.substring(2, failReason.length() - 2);
                }

                // 记录回单动作轨迹
                TradeMag.traceLog(orderId, tradeId, acceptMonth, null, "backPfAct1002", resultCode, failReason);
            }
        }else if (("606".equals(tradeTypeCode) || "622".equals(tradeTypeCode) || "623".equals(tradeTypeCode) || "686".equals(tradeTypeCode)) && "0".equals(cancelTag)){
        	
        	String finishDate = "";
        	String construction_addr = "";
        	String const_staff_id = "";
        	String const_phone = "";
        	String rsrv_tag3 = "";
        	String deviceId = "";
        	String portId = "";
        	
        	finishDate = (String) pfBackData.getDataset("FINISH_DATE").get(0);
        	construction_addr = (String) pfBackData.getDataset("CONSTRUCTION_ADDR").get(0);
        	const_staff_id = (String) pfBackData.getDataset("CONST_STAFF_ID").get(0);
        	const_phone = (String) pfBackData.getDataset("CONST_PHONE").get(0);
        	rsrv_tag3 = (String) pfBackData.getDataset("RSRV_TAG3").get(0);
        	if (IDataUtil.isNotEmpty(pfBackData.getDataset("PFCRM_DEVICE_ID")))
        	{
        	    deviceId = (String) pfBackData.getDataset("PFCRM_DEVICE_ID").get(0);
        	}
        	if (IDataUtil.isNotEmpty(pfBackData.getDataset("PFCRM_PORT_ID")))
        	{
        	    portId = (String) pfBackData.getDataset("PFCRM_PORT_ID").get(0);
        	}
        	
            TradeMag.upWideNetTradeByTradeId(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), mainTrade.getString("CANCEL_TAG"), construction_addr, const_staff_id,const_phone,rsrv_tag3, finishDate, deviceId, portId);
            if("606".equals(tradeTypeCode) || "686".equals(tradeTypeCode)){
                String pfRtnResultCode = pfBackData.getString("EXEC_CODE");
                TradeMag.intTradePbossFinish(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), mainTrade.getString("CANCEL_TAG"), pfRtnResultCode, finishDate);
            }

        	
        }else if ("600".equals(tradeTypeCode) || "611".equals(tradeTypeCode) || "612".equals(tradeTypeCode) || "613".equals(tradeTypeCode) || "9701".equals(tradeTypeCode) || "9702".equals(tradeTypeCode) || "9703".equals(tradeTypeCode)
                || "9705".equals(tradeTypeCode) || "9706".equals(tradeTypeCode)|| "9750".equals(tradeTypeCode)|| "9751".equals(tradeTypeCode) || "9711".equals(tradeTypeCode))
        { // add by chenzm 宽带长流程处理
        	
        	
        	if (("600".equals(tradeTypeCode) || "612".equals(tradeTypeCode) || "613".equals(tradeTypeCode)) && "0".equals(cancelTag))
        	{
        		String finishDate = "";
            	String construction_addr = "";
            	String const_staff_id = "";
            	String const_phone = "";
            	String rsrv_tag3 = "";
            	String deviceId = "";
            	String portId = "";
            	
            	finishDate = (String) pfBackData.getDataset("FINISH_DATE").get(0);
            	construction_addr = (String) pfBackData.getDataset("CONSTRUCTION_ADDR").get(0);
            	const_staff_id = (String) pfBackData.getDataset("CONST_STAFF_ID").get(0);
            	const_phone = (String) pfBackData.getDataset("CONST_PHONE").get(0);
            	rsrv_tag3 = (String) pfBackData.getDataset("RSRV_TAG3").get(0);
            	if (IDataUtil.isNotEmpty(pfBackData.getDataset("PFCRM_DEVICE_ID")))
            	{
            	    deviceId = (String) pfBackData.getDataset("PFCRM_DEVICE_ID").get(0);
            	}
            	
            	if (IDataUtil.isNotEmpty(pfBackData.getDataset("PFCRM_PORT_ID")))
            	{
            	    portId = (String) pfBackData.getDataset("PFCRM_PORT_ID").get(0);
            	}
            	  
                TradeMag.upWideNetTradeByTradeId(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), mainTrade.getString("CANCEL_TAG"), construction_addr, const_staff_id,const_phone,rsrv_tag3, finishDate, deviceId, portId);
        	}
        	
            if (StringUtils.equals("3", cancelTag))
            {
                return subscribeState;
            }
            String pfRtnResultCode = pfBackData.getString("EXEC_CODE");
            if(!"9750".equals(tradeTypeCode)&&!"9751".equals(tradeTypeCode)){
            	String finishDate = (String) pfBackData.getDataset("FINISH_DATE").get(0);
                TradeMag.intTradePbossFinish(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), mainTrade.getString("CANCEL_TAG"), pfRtnResultCode, finishDate);

            }
            IDataset addressCodes = pfBackData.getDataset("RM_INSTALL_ADDRESS_CODE");
            IDataset addressInfos = pfBackData.getDataset("RM_INSTALL_ADDRESS");
            IDataset cfRoom = pfBackData.getDataset("CF_ROOM");
            IDataset cfPort = pfBackData.getDataset("CF_PORT");
            IDataset cfArea = pfBackData.getDataset("CF_TTAREA");
            IDataset trunkId = pfBackData.getDataset("RM_TRUNKID");
            IDataset boardId = pfBackData.getDataset("RM_BOARDID");
            if (IDataUtil.isNotEmpty(cfRoom))
            {

                String cfRoomString = (String) cfRoom.get(0);
                int attrCnt = TradeMag.getTradeMgrPbossAttrCountById(mainTrade.getString("TRADE_ID"), "CF_ROOM");
                if (attrCnt < 1)
                {
                    TradeMag.intTradeMgrPbossAttr(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), "CF_ROOM", "1", "", cfRoomString);
                }

            }
            if (IDataUtil.isNotEmpty(cfPort))
            {

                String cfPortString = (String) cfPort.get(0);
                int attrCnt = TradeMag.getTradeMgrPbossAttrCountById(mainTrade.getString("TRADE_ID"), "CF_PORT");
                if (attrCnt < 1)
                {
                    TradeMag.intTradeMgrPbossAttr(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), "CF_PORT", "1", "", cfPortString);
                }

            }
            if (IDataUtil.isNotEmpty(cfArea))
            {

                String cfAreaString = (String) cfArea.get(0);
                int attrCnt = TradeMag.getTradeMgrPbossAttrCountById(mainTrade.getString("TRADE_ID"), "CF_TTAREA");
                if (attrCnt < 1)
                {
                    TradeMag.intTradeMgrPbossAttr(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), "CF_TTAREA", "1", "", cfAreaString);
                }

            }
            if (IDataUtil.isNotEmpty(trunkId))
            {

                String trunkIdString = (String) trunkId.get(0);
                int attrCnt = TradeMag.getTradeMgrPbossAttrCountById(mainTrade.getString("TRADE_ID"), "RM_TRUNKID");
                if (attrCnt < 1)
                {
                    TradeMag.intTradeMgrPbossAttr(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), "RM_TRUNKID", "1", "", trunkIdString);
                }

            }
            if (IDataUtil.isNotEmpty(boardId))
            {

                String boardIdString = (String) boardId.get(0);
                int attrCnt = TradeMag.getTradeMgrPbossAttrCountById(mainTrade.getString("TRADE_ID"), "RM_BOARDID");
                if (attrCnt < 1)
                {
                    TradeMag.intTradeMgrPbossAttr(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), "RM_BOARDID", "1", "", boardIdString);
                }

            }
            if (IDataUtil.isNotEmpty(addressCodes))
            {

                String attrCode = (String) addressCodes.get(0);
                int attrCnt = TradeMag.getTradeMgrPbossAttrCountById(mainTrade.getString("TRADE_ID"), "RM_INSTALL_ADDRESS_CODE");
                if (attrCnt < 1)
                {
                    TradeMag.intTradeMgrPbossAttr(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), "RM_INSTALL_ADDRESS_CODE", "1", "", attrCode);
                }

            }
            if (IDataUtil.isNotEmpty(addressInfos))
            {
                String address = (String) addressInfos.get(0);
                int attrCnt = TradeMag.getTradeMgrPbossAttrCountById(mainTrade.getString("TRADE_ID"), "RM_INSTALL_ADDRESS");
                if (attrCnt < 1)
                {
                    TradeMag.intTradeMgrPbossAttr(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), "RM_INSTALL_ADDRESS", "1", "", address);
                }
            }
        }
        else if ("9715".equals(tradeTypeCode) || "9713".equals(tradeTypeCode) || "6800".equals(tradeTypeCode) || "6900".equals(tradeTypeCode))
        {
            if (StringUtils.equals("3", cancelTag))
            {
                return subscribeState;
            }
            String pfRtnResultCode = pfBackData.getString("EXEC_CODE");
            String finishDate="";
            if("6900".equals(tradeTypeCode)||"6800".equals(tradeTypeCode)){//6900工单配置短流程无法获取TI_C_PARAM_RSLT表中的finishDate数据   6800接口调用时没有finishDate
            	finishDate=SysDateMgr.getSysTime();
            }else{
            finishDate = (String) pfBackData.getDataset("FINISH_DATE").get(0);
            }
            TradeMag.intTradePbossFinish(mainTrade.getString("TRADE_ID"), mainTrade.getString("ACCEPT_MONTH"), mainTrade.getString("CANCEL_TAG"), pfRtnResultCode, finishDate);
        }
        /** 专线回单处理  */
       /* else if("2990".equals(tradeTypeCode) || "2991".equals(tradeTypeCode) || "2993".equals(tradeTypeCode) ||
        		"3080".equals(tradeTypeCode) || "3081".equals(tradeTypeCode) || "3083".equals(tradeTypeCode) ||
        		"3010".equals(tradeTypeCode) || "3011".equals(tradeTypeCode) || "3013".equals(tradeTypeCode)){
        	
        	IData params = new DataMap();
        	params.put("MAINTRADE", mainTrade);
        	params.put("PFBACKDATA", pfBackData);
        	CSAppCall.call("SS.BookTradeSVC.updateDatalineTradeByTradeId", params);
        }*/

        return subscribeState;
    }
    

    private static IDataset sendCall(String orderId, String acceptMonth, IData pfData, String cancelTag, String nextDealTag) throws Exception
    {
        // 是否送服务开通
        boolean sendpf = BizEnv.getEnvBoolean("crm.trade.sendpf", true);

        IDataset ids = null;

        // 不送服务开通
        if (sendpf == false)
        {
            ids = new DatasetList();

            return ids;
        }

        // 送服务开通
        try
        {
            // 先后顺序不能动

            if ("1".equals(nextDealTag)) // NEXT_DEAL_TAG=1时，表示以前发过服务开通，调修改接口
            {
                // 订单修改，重跑订单
                ids = CSAppCall.call("PF_ORDER_MOTIFY", pfData);
                // ids = CSAppCall.call("http://10.200.130.83:10000/service","PF_ORDER_MOTIFY", pfData, true);
            }
            else if ("0".equals(cancelTag) || "C".equals(cancelTag))
            {
                // 普通订单
                ids = CSAppCall.call("PF_ORDER_SYNC", pfData);
                // ids = CSAppCall.call("http://10.200.130.83:10000/service","PF_ORDER_SYNC", pfData, true);
            }
            else if ("2".equals(cancelTag) || "3".equals(cancelTag))
            {
                // 返销订单
                ids = CSAppCall.call("PF_ORDER_CANCEL", pfData);
                // ids = CSAppCall.call("http://10.200.130.83:10000/service","PF_ORDER_CANCEL", pfData, true);
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(TradeException.CRM_TRADE_65b, Utility.getStackTrace(e));
        }

        // 是否记录送了哪些表给服开
        boolean logData = BizEnv.getEnvBoolean("crm.trade.sendpf.log", false);
        String pfLog = "ok";

        if (logData == true)
        {
            pfLog = getPfLog(pfData);
        }

        // 送开通成功
        TradeMag.traceLog(orderId, "0", acceptMonth, null, "sendPf", "0", pfLog);

        return ids;
    }

    public static void sendPf(String orderId, String acceptMonth, String cancelTag) throws Exception
    {
        // 得到订单/行信息
        IData order = new DataMap();
        IDataset tradeAll = new DatasetList();

        GetPfInfo.getAllTradeByOrder(orderId, acceptMonth, cancelTag, order, tradeAll);

        String nextDealTag = order.getString("NEXT_DEAL_TAG");

        // 是否order等待trade
        boolean wait = isOrderWaitTrade(order, tradeAll);

        if (wait == true)
        {
        	//优先总开关是否开启
        	Boolean priorityAllowed = TradeCtrl.getCtrlBoolean("-1", TradeCtrl.CTRL_TYPE.IS_PRIORYTYOPEN, false);
            if (priorityAllowed == true)
            {
            	 //本业务是否是否支持队列优先
                String tradeTypeCode = order.getString("TRADE_TYPE_CODE");
                Boolean tradePrioried = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.IS_PRIORYTYTRADE, false);
                if(tradePrioried == true)
                {
                	if(TradePriority.isOrderPriorityNotNumLimited(TradePriority.INIT))
                    {
                		//获取用户工单执行时间小于推送业务工单执行时间
                    	IDataset firstOrder = TradePriority.getFirstOrder(order, tradeAll);
                    	if(IDataUtil.isNotEmpty(firstOrder))
                    	{
                    		for (int i = 0; i < firstOrder.size(); i++) {
                    			IData orderF = firstOrder.getData(i);
                    			//判断符合条件的工单插入优先表
                    			if(TradePriority.isNotLimited(orderF) && TradePriority.isNotExistsPriority(orderF,orderId))
                        		{
                        			TradePriority.insTradePriority(orderF, tradeAll);
                        		}
							}
                    	}
                    }
                }
            }
            
            TradeMag.updOrdStateByOrderId(orderId, acceptMonth, cancelTag, "B", nextDealTag);

            return;
        }

        // 是否批量处理
        boolean pfBat = TradePfBat.isPfBat(order);

        if (pfBat == true)
        {
            // 进批量发服务开通处理表,当批量报文生成失败时，批量进程会将ORDER表状态改为3 ，成功则不处理order表
            TradePfBat.pfBat(order, tradeAll);

            nextDealTag = "1";
        }
        else
        {
            // 得到服务开通数据
            IData pfData = GetPfInfo.getPfData(order, tradeAll);

            if (IDataUtil.isNotEmpty(pfData))
            {
                // !!sendCall之后，pfData被服务开通改变了，比如ACCEPT_MONTH都变成当月。引用传递让tradeAll对象也变了
                sendCall(orderId, acceptMonth, pfData, cancelTag, nextDealTag);

                // 已单个发服务开通
                nextDealTag = "1";
            }
        }

        // 订单已处理
        TradeMag.updOrdStateByOrderId(orderId, acceptMonth, cancelTag, "2", nextDealTag);
        
        //记录订单优先表状态
        TradePriority.updStateByOrder(orderId, TradePriority.PF_OK);

        // 是否融合订单
        String orderKindCode = order.getString("ORDER_KIND_CODE", "0");

        for (int i = 0, isize = tradeAll.size(); i < isize; i++)
        {
            IData trade = tradeAll.getData(i);

            if (GetPfInfo.tradeNoDeal(trade))
            {
                continue;
            }

            String tradeId = trade.getString("TRADE_ID");
            String olcomTag = trade.getString("OLCOM_TAG", "0");
            String pfWait = trade.getString("PF_WAIT", "0");
            String routeId = trade.getString("ROUTE_ID", "");
            String subscribeType = trade.getString("SUBSCRIBE_TYPE", "0");

            // 订单状态,默认P等待完工
            String subscribeState = "P";

            boolean sendpf = BizEnv.getEnvBoolean("crm.trade.sendpf", true);

            // 批量报文时，状态为H，当批量文件生成后,再subscribeState改成P或者F
            if (pfBat == true)
            {
                subscribeState = "H";
            }
            else if ("1".equals(olcomTag) && "1".equals(pfWait) && sendpf)
            {
                subscribeState = "F";
            }

            StringBuilder info = new StringBuilder(500);

            info.append("orderKindCode[").append(orderKindCode).append("]");
            info.append("subscribeType[").append(subscribeType).append("]");
            info.append("subscribeState[").append(subscribeState).append("]");
            info.append("olcomTag[").append(olcomTag).append("]");
            info.append("pfWait[").append(pfWait).append("]");

            // 修改订单状态
            // !!下面的 acceptMonth不能从tradeAll取，那个对象在sendCall之后被服务开通改变了，上月订单的ACCEPT_MONTH也会变成当月
            TradeMag.updTradeStateByTradeId(tradeId, acceptMonth, cancelTag, subscribeState, info.toString(), routeId);

            // 批量业务标识
            String batchId = trade.getString("BATCH_ID");

            if (StringUtils.isNotBlank(batchId))
            {
                // 回写批量表状态，已送开通
                TradeMag.updTradeBatDealByBatchId(batchId, "S", "发送服务开通成功");
            }
        }
    }
    
    private static void wlwDealBackActions(IData mainTrade, IData pfBackData) throws Exception
    {
        // 业务类型
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");

        if ("5777".equals(tradeTypeCode))
        {
        	String execFailDesc = pfBackData.getString("EXEC_DESC");
        	String resultFailInfo = StrUtil.strLimit(execFailDesc, 300);
        	mainTrade.put("FAIL_DESCINFO", resultFailInfo);
            backPfIotECFailSync(mainTrade);
        }
    }
    
}
