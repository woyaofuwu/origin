
package com.asiainfo.veris.crm.order.soa.person.busi.canceltrade;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.TicketException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeScore;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.cancelwnchangeproduct.CancelWNChangeProductBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.UndoOfferRelBakDeal;

import java.util.Iterator;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelTradeSVC.java
 * @Description: 业务返销SVC
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-23 下午7:50:06
 */
public class CancelTradeSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    private final static transient Logger logger = Logger.getLogger(CancelTradeSVC.class);

    /**
     * 返销登记
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset cancelTradeReg(IData pgData) throws Exception
    {
        String tradeIdParam = pgData.getString("TRADE_ID");
        boolean isCheckRule = pgData.getBoolean("IS_CHECKRULE",true);
        String tradeIdList = pgData.getString("TRADEID_LIST");
        if (StringUtils.isEmpty(tradeIdParam) && StringUtils.isEmpty(tradeIdList))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_68);
        }
        String[] tradeIdArray = null;
        if (StringUtils.isNotEmpty(tradeIdList))
        {
            tradeIdArray = StringUtils.split(tradeIdList, ",");
        }
        else
        {
            tradeIdArray = StringUtils.split(tradeIdParam, ",");
        }
        IDataset tradeIdDataset = new DatasetList();
        for (int i = 0; i < tradeIdArray.length; i++)
        {
            IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeIdArray[i], "0", null);
            if (IDataUtil.isEmpty(hisTradeInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_70, tradeIdArray[i]);// 获取台帐历史表资料:没有该笔业务!%s
            }
            tradeIdDataset.add(hisTradeInfos);
        }
        DataHelper.sort(tradeIdDataset, "ACCEPT_DATE", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        IData returnData = new DataMap();
        
        //如果存在发票需要冲红,用户需冲红发票后才能进行返销处理
        for (int i = 0, count = tradeIdDataset.size(); i < count; i++){
        	String needCHReceipt = StateTaxUtil.needCHReceipt(tradeIdDataset.getData(i).getString("TRADE_ID",""));
            if(StringUtils.isNotEmpty(needCHReceipt))
            	CSAppException.apperr(TicketException.CRM_TICKET_14, needCHReceipt);
        }
        
        for (int i = 0, count = tradeIdDataset.size(); i < count; i++)
        {
            /**************************** 数据准备 **********************/
            IData hisTrade = tradeIdDataset.getData(i);// 当前界面选择的历史台账信息
            IData pubData = this.getPublicData(hisTrade);// 操作员/trade_id/cancel_tag等相关信息
            pubData.put("RENT_TAG", pgData.getString("RENT_TAG", "0"));// 电子渠道对账标记
            UndoOfferRelBakDeal.dealOfferRelBak(hisTrade);
            CancelTradeBean bean = BeanManager.createBean(CancelTradeBean.class);

            /**************************** 规则校验 *************************/
            if (isCheckRule)
            {
                bean.chkTradeBeforeUndo(pubData, hisTrade);
            }
            /********************** 相关资料处理 **************************/
            String newOrderId = bean.createCancelOrder(hisTrade, pubData);// 生成新的返销订单信息--并返回新的order_id
            
            //start 针对铁通宽带续费返销需要送服开数据的特殊处理 位置请勿调整，必须在dealTradeCancel之前  by songlm 20150522
            String new9000OrderId = "";
            if("9712".equals(hisTrade.getString("TRADE_TYPE_CODE")))
            {
            	//生成9000业务类型的   新tf_b_order 新tf_b_trade 新tf_b_discnt，专门为了送数据给服开
            	new9000OrderId = bean.deal9000TradeTypeCancel(hisTrade, pubData);
            	
            	//对宽带续费自有的9712业务类型，返销时不再发服开处理，把OLCOM_TAG置为0，不等服开回单完工，把PF_WAIT置为0
            	hisTrade.put("OLCOM_TAG", "0");
            	hisTrade.put("PF_WAIT", "0");
            }
            //end
            
            this.dealTradeCancel(newOrderId, hisTrade, pgData, pubData);
            
            //start 针对铁通宽带续费返销需要送服开数据的特殊处理 by songlm 20150522
            if(StringUtils.isNotBlank(new9000OrderId))
            {
            	newOrderId = newOrderId + "," + new9000OrderId;
            }
            //end
            //资源预占释放
            if("230".equals(hisTrade.getString("TRADE_TYPE_CODE")))
            {
            	IData map = new DataMap();
            	map.put("RELATION_TRADE_ID", hisTrade.getString("TRADE_ID"));
            	IDataset ds = UserSaleGoodsInfoQry.getByRelationTradeIdComm(map);
    			if(IDataUtil.isNotEmpty(ds))
    			{
    				IData data = ds.getData(0);
    				data.put("RES_NO", data.getString("RES_CODE",""));
    	    		data.put("SERIAL_NUMBER", hisTrade.getString("SERIAL_NUMBER"));
    	    		//释放终端预占
    	    		IDataset retDataset =HwTerminalCall.releaseResTempOccupy(data);
    			}
            }
            
            //REQ201904240008宽带资费的优化：返销240时，如果有237且没有601时，也一起返销掉237.
            if("240".equals(hisTrade.getString("TRADE_TYPE_CODE","")))
            {
            	//1、处理营销活动终止日期更新回来
                //2、处理优惠活动插入中间表
            	String orderId = hisTrade.getString("ORDER_ID","");
            	IData oldSaleActiveTradeInfos = UTradeHisInfoQry.qryTradeHisByOID(orderId, "0", null);
            	//System.out.println(">>>>>>>>>>>>>>>>>>>>>oldSaleActiveTradeInfos:"+oldSaleActiveTradeInfos);
            	if (IDataUtil.isNotEmpty(oldSaleActiveTradeInfos))
            	{
            		String oldSaleactivetradeId = oldSaleActiveTradeInfos.getString("TRADE_ID", "");
            		CancelWNChangeProductBean.doActiveEndDateAndInsTiDiscnt(oldSaleactivetradeId); 
            		
            		//老系统的操作，生成offerRel备份信息
                    UndoOfferRelBakDeal.dealOfferRelBak(oldSaleActiveTradeInfos);
                    
                    
                    IData pdData = new DataMap();
                    pdData.put("REMARKS", "营销活动取消返销");
                    pdData.put("CANCEL_TYPE", "1");  //如果该值传1 则会强制返销费用
                    pdData.put("TRADE_ID", oldSaleactivetradeId);
                    pdData.put("IS_CHECKRULE", false);
                    pdData.put(Route.ROUTE_EPARCHY_CODE,hisTrade.getString("TRADE_EPARCHY_CODE"));
                    CSAppCall.call("SS.CancelTradeSVC.cancelTradeReg", pdData);
            	}
                
            }

            returnData.put("TRADE_ID", hisTrade.getString("TRADE_ID"));
            returnData.put("ORDER_ID", newOrderId);
        }
        return IDataUtil.idToIds(returnData);
    }

    /**
     * 规则校验
     */
    public IDataset commitBeforeCheck(IData input) throws Exception
    {
        String tradeIdParam = input.getString("TRADE_ID");
        String tradeIdList = input.getString("TRADEID_LIST");
        if (StringUtils.isEmpty(tradeIdParam) && StringUtils.isEmpty(tradeIdList))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_68);
        }
        String[] tradeIdArray = null;
        if (StringUtils.isNotEmpty(tradeIdList))
        {
            tradeIdArray = StringUtils.split(tradeIdList, ",");
        }
        else
        {
            tradeIdArray = StringUtils.split(tradeIdParam, ",");
        }
        IDataset tradeIdDataset = new DatasetList();
        for (int i = 0; i < tradeIdArray.length; i++)
        {
            IData tempData = new DataMap();
            tempData.put("TRADE_ID", tradeIdArray[i]);
            tradeIdDataset.add(tempData);
        }
        DataHelper.sort(tradeIdDataset, "TRADE_ID", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
        for (int i = 0, count = tradeIdDataset.size(); i < count; i++)
        {
            IData hisTradeInfos = UTradeHisInfoQry.qryTradeHisByPk(tradeIdDataset.getData(i).getString("TRADE_ID"), "0", null);
            if (IDataUtil.isEmpty(hisTradeInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_70, tradeIdParam);// 获取台帐历史表资料:没有该笔业务!%s
            }
            /********************** 规则校验 **************************/
            CancelTradeBean bean = BeanManager.createBean(CancelTradeBean.class);
            IData pubData = this.getPublicData(hisTradeInfos);// 操作员/trade_id/cancel_tag等相关信息
            bean.chkTradeBeforeUndo(pubData, hisTradeInfos);
        }
        return new DatasetList();
    }

    private void dealOldSystemTrade(IData hisTrade) throws Exception
    {
        IDataset cancelParamDataset = StaticUtil.getStaticList("NEW_NGBOSS_CANCEL_OLDTRADE", "NEW_NGBOSS_TIME");
        if (IDataUtil.isEmpty(cancelParamDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "返销老系统工单参数配置缺失，请联系系统管理员！");
        }
        String newNgbossDateStr = SysDateMgr.getDateForSTANDYYYYMMDD(cancelParamDataset.getData(0).getString("DATA_NAME"));
        // 目前只能返销老系统经过处理的订单
        String tradeAcceptDateStr = SysDateMgr.getDateForSTANDYYYYMMDD(hisTrade.getString("ACCEPT_DATE"));
        String tradeId = hisTrade.getString("TRADE_ID");
        String olcomTag = hisTrade.getString("OLCOM_TAG");
        
        if (tradeAcceptDateStr.compareTo(newNgbossDateStr) >= 0)
        {
            // 新系统工单直接返回
            return;
        }
        
        if (!StringUtils.equals("97", hisTrade.getString("SUBSCRIBE_TYPE")))
        { 
            // 老系统工单
            StringBuilder msg = new StringBuilder(200);
            msg.append("订单[").append(tradeId).append("]的业务受理时间为[");
            msg.append(tradeAcceptDateStr).append("],是老系统工单,但是未经处理不能在新系统返销,请联系系统管理员处理！");
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg.toString());
        }
        
        String tradeTypeCode = hisTrade.getString("TRADE_TYPE_CODE");
        IDataset cancelTradeTypeCodeDataset = StaticUtil.getStaticList("NEW_NGBOSS_CANCEL_OLDTRADE", tradeTypeCode);
        if (IDataUtil.isEmpty(cancelTradeTypeCodeDataset))
        {
            StringBuilder msg = new StringBuilder(100);
            msg.append("老系统订单[").append(tradeId).append("]的业务类型为[");
            msg.append(tradeTypeCode).append("]").append(",新系统不支持返销此业务类型的老系统工单！");
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg.toString());
        }
        
        if (!StringUtils.equals("1", olcomTag))
        {
            return;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug("老系统工单返销之前调用CS.TradePfSVC.sendPf送一次正向老工单");
        }
        // 老系统工单返销重新送正向的台帐数据给服开
        IData input = new DataMap();
        input.put(Route.ROUTE_EPARCHY_CODE, CSBizService.getVisit().getStaffEparchyCode());
        input.put("ORDER_ID", hisTrade.getString("ORDER_ID"));
        input.put("ACCEPT_MONTH", hisTrade.getString("ACCEPT_MONTH"));
        input.put("CANCEL_TAG", "C");
        IDataset ids = CSAppCall.call("CS.TradePfSVC.sendPf", input);
    }

    /**
     * 根据orderId来进行返销 newOrderId ：新生成order_id oldOrderId : 当前界面选择的trade中对应的order_id pubData ：
     * 一些公关信息，操作员/trade_id/cancel_tag等相关信息 pgData ： 页面调服务传入的参数
     * 
     * @throws Exception
     */
    private void dealOrderCancel(String newOrderId, String oldOrderId, IData pubData, IData pgData) throws Exception
    {
        // 根据orderId查询下面的trade信息
        IDataset tradeHisInfos = TradeHistoryInfoQry.qryTradeByOrderId(oldOrderId, "0", BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(tradeHisInfos))
        {
            for (int i = 0; i < tradeHisInfos.size(); i++)// 循环处理
            {
                IData tempHisTrade = tradeHisInfos.getData(i);
                pubData.put("TRADE_ID", tempHisTrade.getString("TRADE_ID"));// 当前这笔台账的trade_id
                this.dealTradeCancel(newOrderId, tempHisTrade, pgData, pubData);
            }
        }
    }

    /*
     * 处理trade返销 newOrderId ：新生成order_id hisTrade ： 当前界面选择的台账历史信息 pubData ： 一些公关信息，操作员/trade_id/cancel_tag等相关信息 pgData ：
     * 页面调服务传入的参数
     */
    private void dealTradeCancel(String newOrderId, IData hisTrade, IData pgData, IData pubData) throws Exception
    {
        CancelTradeBean bean = BeanManager.createBean(CancelTradeBean.class);

        // 老系统工单特殊处理
        this.dealOldSystemTrade(hisTrade);

        IData newCancelTrade = bean.createCancelTrade(newOrderId, hisTrade, pgData, pubData);// 生成新的返销台账信息
        bean.modifyHisTradeAndTradeStaff(hisTrade, pubData);// 修改原台账状态
        bean.createCancelStaffTrade(newOrderId, pubData);

        // 异地返销 --台账表中的地市编号和登陆员工的地市编号不一致
        if (!StringUtils.equals(hisTrade.getString("EPARCHY_CODE"), CSBizBean.getTradeEparchyCode()))
        {
            bean.createCenterUndoTrade(newCancelTrade, pubData);
        }
        
        // TODO:原流程中的二维码返销需要确认是否还需要处理?
        // 积分和费用的处理放在登记处理[避免完工时积分和费用不足，不能返销]
        TradeScore.tradeScore(newCancelTrade);// 积分返销--入参中的cancel_tag非常重要
        TradeCancelFee.cancelRecvFee(newCancelTrade, pgData);// 费用返销
        
        //返销票据
        bean.cancelTicketTrade(hisTrade.getString("TRADE_ID"));
        bean.cancelEInvoice(hisTrade);//新增:电子发票返销
    }

    /**
     * 获取一些公共信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    private IData getPublicData(IData hisTrade) throws Exception
    {
        IData pubData = new DataMap();
        pubData.put("CANCEL_TAG", "2");// 2=被返销
        pubData.put("ORDER_ID", hisTrade.getString("ORDER_ID", ""));
        pubData.put("TRADE_ID", hisTrade.getString("TRADE_ID", ""));
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("LOGIN_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return pubData;
    }

    public IData queryCancelTrade(IData pdData) throws Exception
    {
        String serialNumber = pdData.getString("SERIAL_NUMBER", "");
        String tradeTypeCode = pdData.getString("TRADE_TYPE_CODE", "");
        String startDate = pdData.getString("START_DATE", "");
        String endDate = pdData.getString("END_DATE", "");
        IData returnData = new DataMap();
        if("141".equals(tradeTypeCode)){
        	String qryTradeEaprchyCode = getVisit().getStaffEparchyCode();
            IDataset tradeInfos = TradeHistoryInfoQry.queryCanBackTradeYDXK(serialNumber, tradeTypeCode, startDate, endDate);
            if (IDataUtil.isNotEmpty(tradeInfos))
            {
                for (int i = 0, count = tradeInfos.size(); i < count; i++)
                {
                    IData tempData = tradeInfos.getData(i);
                    IData tradeTypeData = UTradeTypeInfoQry.getTradeType(tempData.getString("TRADE_TYPE_CODE"), qryTradeEaprchyCode);
                    if (IDataUtil.isEmpty(tradeTypeData) || StringUtils.equals("0", tradeTypeData.getString("BACK_TAG")))
                    {
                        tempData.put("CANCEL_TAG", "-1");
                    }
                    
                    tempData.put("TRADE_STAFF_NAME", UStaffInfoQry.getStaffNameByStaffId(tempData.getString("TRADE_STAFF_ID")));
                    tempData.put("TRADE_DEPART_NAME",UDepartInfoQry.getDepartNameByDepartId(tempData.getString("TRADE_DEPART_ID")));
                    tempData.put("TRADE_CITY_NAME", UAreaInfoQry.getAreaNameByAreaCode(tempData.getString("TRADE_CITY_CODE")));
                    tempData.put("RSRV_STR1", UTradeTypeInfoQry.getTradeTypeName(tempData.getString("TRADE_TYPE_CODE")));
                    
                }
                returnData.put("TRADE_INFO", tradeInfos);
            }
        }else{
        // 查询用户信息
        IData userInfos = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            IData userInfoData = userInfos;
            returnData.put("USER_INFO", userInfos);
            String userId = userInfoData.getString("USER_ID", "0");

            String qryTradeEaprchyCode = getVisit().getStaffEparchyCode();
            IDataset tradeInfos = TradeHistoryInfoQry.queryCanBackTradeBySnAndTypeCodeAndDate(userId, tradeTypeCode, qryTradeEaprchyCode, startDate, endDate);
            if (IDataUtil.isNotEmpty(tradeInfos))
            {
                //剔除不是吉祥号码营销活动终止
                for(Iterator<Object> iterator = tradeInfos.iterator(); iterator.hasNext();){
                    IData tempData = (IData) iterator.next();
                    tradeTypeCode = tempData.getString("TRADE_TYPE_CODE");
                    String productId = tempData.getString("RSRV_STR1");
                    if(StringUtils.equals("237", tradeTypeCode) && !StringUtils.equals("69900703", productId)){
                        iterator.remove();
                    }
                }
                for (int i = 0, count = tradeInfos.size(); i < count; i++)
                {
                    IData tempData = tradeInfos.getData(i);
                    IData tradeTypeData = UTradeTypeInfoQry.getTradeType(tempData.getString("TRADE_TYPE_CODE"), qryTradeEaprchyCode);
                    if (IDataUtil.isEmpty(tradeTypeData) || StringUtils.equals("0", tradeTypeData.getString("BACK_TAG")))
                    {
                        tempData.put("CANCEL_TAG", "-1");
                    }
                }
                returnData.put("TRADE_INFO", tradeInfos);
            }
        }
        }
        return returnData;
    }
    
    /**
     * 获取票据作废信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public IDataset ticketCancelCheck(IData pgData) throws Exception{
    	CancelTradeBean bean = BeanManager.createBean(CancelTradeBean.class);
    	return bean.cancelTicketCheck(pgData);
    }
}
