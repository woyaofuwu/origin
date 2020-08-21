/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.imslandline;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.order.UOrderInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartKindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.WidenetTradeQuery;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CancelWidenetTradeService.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-5-24 上午09:00:29 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-5-24 chengxf2 v1.0.0 修改原因
 */

public class CancelIMSLandLineService extends CSBizService
{
	
	private Logger log=Logger.getLogger(CancelIMSLandLineService.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-6-19 下午09:39:48 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    private IDataOutput applyCancelPbossTrade(IData reason, IData pubData, IData tradeInfo, String cancelType) throws Exception
    {
        String tradeId = tradeInfo.getString("TRADE_ID");
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        //REQ201811010006关于NGBOSS系统装机撤单原因推送至装机系统的需求 wuhao5
        String cancelReasonType = reason.getString("CANCEL_REASON_ONE");
        String cancesedReasonType = reason.getString("CANCEL_REASON_TWO");
        String remark = reason.getString("REMARK");
        return PBossCall.orderCancelApply(tradeId, "0", acceptMonth, cancelType, cancelReasonType, cancesedReasonType, remark);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-19 下午05:56:19 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    public IDataset cancelTradeReg(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");
        String cityCode = getVisit().getCityCode();
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");
        if (IDataUtil.isEmpty(tradeInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_67, tradeId);// 在TF_B_TRADE未找到有效记录,TRADE_ID:%s
        }
        
        /**************************** 数据准备 *************************/
        IData tradeInfo = tradeInfos.getData(0);// 当前界面选择的台账信息

        IData pubData = this.getPublicData(tradeInfo);// 操作员/trade_id/cancel_tag等相关信息
        
        /**************************** 规则校验 *************************/
        chkTradeBeforeCancel(pubData, tradeInfo);

        String tradeTypeCode=tradeInfo.getString("TRADE_TYPE_CODE","");
        
        /********************** 相关资料处理 **************************/
        dealTradeCancel(pubData, input, tradeInfo);
        
        /**************************** 发送PBOSS撤单申请 *************************/
        String cancelType = input.getString("CANCEL_TYPE", "0");
        IDataOutput dataOutput = applyCancelPbossTrade(input, pubData, tradeInfo, cancelType);
        IData head = dataOutput.getHead();//服开返回报文头
        IDataset dataresutl = dataOutput.getData();//服开返回报文体
        String resultCode = head.getString("X_RESULTCODE", "-1");
        if (!"0".equals(resultCode)){
            String resultInfo = head.getString("X_RESULTINFO");
            CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
        }
        IDataset result = dataOutput.getData();
        if (IDataUtil.isNotEmpty(result)){
            IData tmpData = result.getData(0);
            String xResultCode = tmpData.getString("X_RESULTTYPE", "-1");
            if (!"0".equals(xResultCode)){
            	String resultInfo = tmpData.getString("X_RESULTINFO");
            	CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
            }
        }
        String canceltype = input.getString("CANCEL_TYPE");
        String staffcityCode = null;
        String custName = null;
        String installAddr = null;
        String recvObject = null;
        String serialNumberbak = null;
        IDataset dataset = this.queryWidenet(input);
        if(IDataUtil.isNotEmpty(dataset)){
            custName = dataset.first().getString("CUST_NAME");
            installAddr = dataset.first().getString("DETAIL_ADDRESS");
        }
        if("1".equals(canceltype)){
        	staffcityCode = input.getString("STAFF_CITY_CODE");
        	recvObject = input.getString("STAFF_PHONE");
        	serialNumberbak = input.getString("SERIAL_NUMBER");
        }else{
        	if(IDataUtil.isNotEmpty(dataresutl)){
            	staffcityCode = dataresutl.first().getString("STAFF_CITY_CODE");
            	recvObject = dataresutl.first().getString("STAFF_PHONE");
            	serialNumberbak = dataresutl.first().getString("SERIAL_NUMBER");
        	}
        }
        
        if("HNKF".equals(cityCode) && (recvObject != null && !"".equals(recvObject))) {
        	IData smsData = new DataMap();
			String noticeContent = "家庭IMS固话账户：" + serialNumberbak + "; 用户名：" + custName + "; 装机地址：" + installAddr + "。 10086已撤单，如果该工单已派发至安装师傅，请及时通知装机师傅无须上门，请知晓! " + "撤单时间：" + SysDateMgr.getSysTime();
            smsData.put("RECV_OBJECT", recvObject);
            smsData.put("NOTICE_CONTENT", noticeContent);
            smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(recvObject).getString("USER_ID"));
            smsData.put("PRIORITY", "50");
            smsData.put("REMARK", "10086撤单");
            SmsSend.insSms(smsData);
            //增加撤单短信发送给B角员工.TD_M_STAFF_B
            IData Bdata = new DataMap();
            Bdata.put("CITY_CODE", staffcityCode);
            IDataset Bdataset = this.queryStaffB(Bdata);
            
            if (!IDataUtil.isEmpty(Bdataset)){
            	for(int i = 0; i < Bdataset.size(); i++){
            		IData data = Bdataset.getData(i);
        			noticeContent = "家庭IMS固话账户：" + serialNumberbak + "; 用户名：" + custName + "; 装机地址：" + installAddr + "。 10086已撤单，如果该工单已派发至安装师傅，请及时通知装机师傅无须上门，请知晓! " + "撤单时间：" + SysDateMgr.getSysTime();
                    smsData.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
                    smsData.put("NOTICE_CONTENT", noticeContent);
                    smsData.put("RECV_ID", UcaInfoQry.qryUserInfoBySn(recvObject).getString("USER_ID"));
                    smsData.put("PRIORITY", "50");
                    smsData.put("REMARK", "10086撤单");
                    SmsSend.insSms(smsData);
            	}
            }
        }
        if(!"1".equals(cancelType))
        {
        	//CRM撤单需要短信通知客户
        	smsNotifyCustomer(tradeInfo);
        }
        
        return dataOutput.getData();
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-19 下午08:01:45 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    private void chkTradeBeforeCancel(IData pubData, IData tradeInfo) throws Exception
    {
        IData inRuleParam = new DataMap();
        inRuleParam.put("TRADE_TYPE_CODE", tradeInfo.getString("TRADE_TYPE_CODE"));
        inRuleParam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        inRuleParam.put("TRADE_EPARCHY_CODE", pubData.getString("LOGIN_EPARCHY_CODE"));
        inRuleParam.put("TRADE_STAFF_ID", pubData.getString("STAFF_ID"));
        inRuleParam.put("TRADE_CITY_CODE", pubData.getString("CITY_CODE"));
        inRuleParam.put("TRADE_DEPART_ID", pubData.getString("DEPART_ID"));
        inRuleParam.put("TRADE_ID", pubData.getString("TRADE_ID"));
        inRuleParam.put("RULE_BIZ_TYPE_CODE", "TradeCheckBeforeWidenetCancel");
        inRuleParam.put("RULE_BIZ_KIND_CODE", "TradeCheckBeforeWidenetCancel");
        inRuleParam.put("ACTION_TYPE", "TradeCheckBeforeWidenetCancel");
        inRuleParam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        inRuleParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inRuleParam.put("UNDO_TIME", pubData.getString("SYS_TIME"));// 返销时间
        inRuleParam.put("TRADE_INFO", tradeInfo);// 将trade信息传入
        IData data = BizRule.bre4SuperLimit(inRuleParam);
        CSAppException.breerr(data);

    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-8-8 下午07:53:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-8 chengxf2 v1.0.0 修改原因
     */
    private void createHisOrder(IData pubData, IData pgData, IData orderInfo) throws Exception
    {
        IData orderData = new DataMap();
        orderData.putAll(orderInfo);
        orderData.put("CANCEL_TAG", "1");
        orderData.put("SUBSCRIBE_STATE", "A");
        orderData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
        orderData.put("CANCEL_TAG", "1");
        orderData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        orderData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        orderData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        orderData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        orderData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        if (!Dao.insert("TF_BH_ORDER", orderData, Route.getJourDb(BizRoute.getTradeEparchyCode())))
        {
            String msg = "搬迁订单【" + orderInfo.getString("ORDER_ID") + "】至订单历史表失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }

    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-6-19 下午09:45:15 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    private void createHisTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        IData tradeData = new DataMap();
        tradeData.putAll(tradeInfo);
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("SUBSCRIBE_STATE", "A");
        tradeData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        tradeData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        tradeData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        tradeData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        if (!Dao.insert("TF_BH_TRADE", tradeData, Route.getJourDb(BizRoute.getTradeEparchyCode())))
        {
            String msg = "搬迁订单【" + tradeInfo.getString("TRADE_ID") + "】至历史表失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-6-19 下午09:45:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    public void createHisTradeStaff(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        IData tradeData = new DataMap();
        tradeData.putAll(tradeInfo);
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("SUBSCRIBE_STATE", "A");
        tradeData.put("FINISH_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_TAG", "1");
        tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        tradeData.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        tradeData.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        tradeData.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        tradeData.put("DAY", tradeInfo.getString("TRADE_ID").substring(6, 8));// 为了bh_trade_staff表用
        if (!Dao.insert("TF_BH_TRADE_STAFF", tradeData, Route.getJourDb(BizRoute.getTradeEparchyCode())))
        {
            String msg = "搬迁员工订单【" + tradeInfo.getString("TRADE_ID") + "】到历史表失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
    }
    
    public void createCancelStaffTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        String tradeId = pubData.getString("TRADE_ID");
        IDataset tradeStaffInfos = TradeStaffInfoQry.queryTradeStaffByTradeId(tradeId, "0");

        if (IDataUtil.isEmpty(tradeStaffInfos))
        {
            return;
        }

        // 修改原单据的状态
        IData param = new DataMap();

        param.put("TRADE_ID", tradeId);
        param.put("DAY", tradeStaffInfos.getData(0).getString("DAY"));
        param.put("CANCEL_TAG", "1");// 被返销
        param.put("CANCEL_DATE", pubData.getString("SYS_TIME"));
        param.put("CANCEL_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        param.put("CANCEL_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        param.put("CANCEL_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        param.put("CANCEL_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        // 更新失败不报错
        Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "UPD_STAFF_CANCEL_TAG", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));

        // 新增一条cancel_tag=2的单据
        IData newData = tradeStaffInfos.getData(0);

        if (newData.getLong("OPER_FEE", 0) > 0)
        {
        	long lOperFee = -(long) pgData.getDouble("OPER_FEE", 0);
        	newData.put("OPER_FEE", String.valueOf(lOperFee * 100));
        }

        if (newData.getLong("FOREGIFT", 0) > 0)
        {
        	long lforegift = -(long) pgData.getDouble("FOREGIFT", 0);
        	newData.put("FOREGIFT", String.valueOf(lforegift * 100));
        }

        if (newData.getLong("ADVANCE_PAY", 0) > 0)
        {
        	long lAdvancePay = -(long) pgData.getDouble("ADVANCE_PAY", 0);
        	newData.put("ADVANCE_PAY", String.valueOf(lAdvancePay * 100));
        }

        newData.put("ORDER_ID", pubData.getString("NEW_ORDER_ID"));
        newData.put("CANCEL_TAG", "3");
        newData.put("EXEC_TIME", pubData.getString("SYS_TIME"));

        // 新单据cancel字段记录的是原单据的 受理相关信息
        newData.put("CANCEL_DATE", newData.getString("ACCEPT_DATE"));
        newData.put("CANCEL_STAFF_ID", newData.getString("TRADE_STAFF_ID"));
        newData.put("CANCEL_DEPART_ID", newData.getString("TRADE_DEPART_ID"));
        newData.put("CANCEL_CITY_CODE", newData.getString("TRADE_CITY_CODE"));
        newData.put("CANCEL_EPARCHY_CODE", newData.getString("TRADE_EPARCHY_CODE"));
        newData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        newData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        newData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        newData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        newData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));

        // 分区号
        newData.put("DAY", SysDateMgr.getSysDate("dd"));

        Dao.insert("TF_BH_TRADE_STAFF", newData, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-8-8 下午07:23:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-8 chengxf2 v1.0.0 修改原因
     */
    private void createNewOrder(IData pubData, IData pgData, IData orderInfo) throws Exception
    {
        IData orderData = new DataMap();
        orderData.putAll(orderInfo);
        String newOrderId = pubData.getString("NEW_ORDER_ID");
        orderData.put("ORDER_ID", newOrderId);
        orderData.put("ACCEPT_MONTH", newOrderId.substring(4, 6));
        orderData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        orderData.put("CANCEL_TAG", "3"); // 撤销
        orderData.put("ORDER_STATE", "0");
        orderData.put("CANCEL_STAFF_ID", orderInfo.getString("TRADE_STAFF_ID"));
        orderData.put("CANCEL_DEPART_ID", orderInfo.getString("TRADE_DEPART_ID"));
        orderData.put("CANCEL_CITY_CODE", orderInfo.getString("TRADE_CITY_CODE"));
        orderData.put("CANCEL_EPARCHY_CODE", orderInfo.getString("TRADE_EPARCHY_CODE"));
//        long lOperFee = -(long) pgData.getDouble("OPER_FEE", 0);
//        long lAdvancePay = -(long) pgData.getDouble("ADVANCE_PAY", 0);
//        long lforegift = -(long) pgData.getDouble("FOREGIFT", 0);
//        String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
        //long lSubscribeType = orderData.getLong("SUBSCRIBE_TYPE", 0);
        //orderData.put("SUBSCRIBE_TYPE", String.valueOf((lSubscribeType / 10) * 10)); // 原订单类型转为相应的立即执行类型
        orderData.put("NEXT_DEAL_TAG", "0");
        orderData.put("OPER_FEE", "0");
        orderData.put("ADVANCE_PAY", "0");
        orderData.put("FOREGIFT", "0");
        orderData.put("FEE_STATE", "0");
        orderData.put("FEE_TIME", "");
        orderData.put("FEE_STAFF_ID", "");
        orderData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        orderData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        orderData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        orderData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        orderData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        orderData.put("SUBSCRIBE_TYPE", "0");
        if (!Dao.insert("TF_B_ORDER", orderData, Route.getJourDb(getTradeEparchyCode())))
        {
            String msg = "生成返销订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-6-19 下午09:47:30 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    private void createNewTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        IData tradeData = new DataMap();
        
        long lAdvancePay = tradeInfo.getLong("ADVANCE_PAY", 0);
        /*if (lAdvancePay>0) {
        	lAdvancePay = -lAdvancePay;
		}*/
        long lOperFee = -tradeInfo.getLong("OPER_FEE", 0);
        long lforegift = -tradeInfo.getLong("FOREGIFT", 0);
        String strFeeState = (lOperFee + lAdvancePay + lforegift == 0) ? "0" : "1";
        
        tradeData.putAll(tradeInfo);
        tradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("ACCEPT_MONTH", pubData.getString("NEW_ORDER_ID").substring(4, 6)); //修改跨月撤单月份不一致的问题
        tradeData.put("ORDER_ID", pubData.getString("NEW_ORDER_ID"));
        tradeData.put("CANCEL_TAG", "3"); // 撤销
        tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));  //撤单时间
        tradeData.put("SUBSCRIBE_STATE", "0");
        tradeData.put("CANCEL_STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID"));
        tradeData.put("CANCEL_DEPART_ID", tradeInfo.getString("TRADE_DEPART_ID"));
        tradeData.put("CANCEL_CITY_CODE", tradeInfo.getString("TRADE_CITY_CODE"));
        tradeData.put("CANCEL_EPARCHY_CODE", tradeInfo.getString("TRADE_EPARCHY_CODE"));
        tradeData.put("OPER_FEE", "0");
        tradeData.put("ADVANCE_PAY", String.valueOf(-lAdvancePay));
        tradeData.put("FOREGIFT", "0");
        tradeData.put("FEE_STATE", "0");
        tradeData.put("FEE_TIME", (strFeeState == "0") ? "" : pubData.getString("SYS_TIME"));
        tradeData.put("FEE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        tradeData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        tradeData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        tradeData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        tradeData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        tradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        tradeData.put("REMARK", pgData.getString("CANCEL_REASON_ONE","")+"|"+pgData.getString("CANCEL_REASON_TWO","")+"|"+pgData.getString("REMARK",""));
        
        if (!Dao.insert("TF_B_TRADE", tradeData, Route.getJourDb(getTradeEparchyCode())))
        {
            String msg = "生成返销订单【" + tradeInfo.getString("TRADE_ID") + "】失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-8-8 下午07:38:47 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-8 chengxf2 v1.0.0 修改原因
     */
    private void dealOrderCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        String orderId = tradeInfo.getString("ORDER_ID");
        IData orderInfo = UOrderInfoQry.qryOrderAllByOrderId(orderId);
        createHisOrder(pubData, pgData, orderInfo);
        
        String[] keys = new String[3];
        keys[0]  = "ORDER_ID";
        keys[1]  = "ACCEPT_MONTH";
        keys[2]  = "CANCEL_TAG";
        
        boolean delFlag = Dao.delete("TF_B_ORDER", orderInfo, keys, Route.getJourDb(getTradeEparchyCode()));
        
        if(!delFlag)
        {
        	//融合业务，如果一笔为长流程，一笔为短流程，则短流程完工会搬迁ORDER到历史表中，撤单时，如果还删除ORDER表此处就会报错
        	//查询历史订单
        	IData orderInfoHis = UOrderHisInfoQry.qryOrderHisByOrderId(orderId);
            if(IDataUtil.isEmpty(orderInfoHis))
            {
            	//如果历史表也没有说明该订单不存在
            	String msg = "删除订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
                CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
            }
        }
        
        String orderKindCode = orderInfo.getString("ORDER_KIND_CODE", "");
        if("1".equals(orderKindCode))
        {
        	//融合业务撤单时，单条撤单，作为单笔业务处理
        	orderInfo.put("ORDER_KIND_CODE", "");
        }
        createNewOrder(pubData, pgData, orderInfo);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @throws Exception
     * @date: 2014-6-19 下午09:37:05 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    private void dealTradeCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        dealOrderCancel(pubData, pgData, tradeInfo); // 处理TF_B_ORDER
        createHisTrade(pubData, pgData, tradeInfo);
        createCancelStaffTrade(pubData, pgData, tradeInfo);
        
        String[] keys = new String[3];
        keys[0]  = "TRADE_ID";
        keys[1]  = "ACCEPT_MONTH"; 
        keys[2]  = "CANCEL_TAG"; 
        
        if (!Dao.delete("TF_B_TRADE", tradeInfo, keys, Route.getJourDb(BizRoute.getTradeEparchyCode())))
        {
            String msg = "删除订单【" + tradeInfo.getString("TRADE_ID") + "】失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
        createNewTrade(pubData, pgData, tradeInfo);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-19 下午08:01:14 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-19 chengxf2 v1.0.0 修改原因
     */
    private IData getPublicData(IData tradeInfo) throws Exception
    {
        IData pubData = new DataMap();
        pubData.put("ORDER_ID", tradeInfo.getString("ORDER_ID", ""));
        pubData.put("TRADE_ID", tradeInfo.getString("TRADE_ID", ""));
        pubData.put("NEW_ORDER_ID", SeqMgr.getOrderId());
        pubData.put("SYS_TIME", SysDateMgr.getSysTime());
        pubData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        pubData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        pubData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        pubData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        return pubData;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-20 上午09:09:48 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-20 chengxf2 v1.0.0 修改原因
     */
    public IData queryTradeBackFee(IData input) throws Exception
    {
        IData feeInfo = new DataMap();
        String tradeId = input.getString("TRADE_ID");
        String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
        IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");
        if (IDataUtil.isEmpty(tradeInfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_67, tradeId);// 在TF_B_TRADE未找到有效记录,TRADE_ID:%s
        }
        IData trade = tradeInfos.getData(0);// 当前界面选择的台账信息
        double operFee = trade.getDouble("OPER_FEE", 0.0);
        double foregift = trade.getDouble("FOREGIFT", 0.0);
        double advancePay = trade.getDouble("ADVANCE_PAY", 0.0);
        double backFee = operFee + foregift + advancePay;
        if (trade.getString("TRADE_TYPE_CODE").equals("600"))
        {
            String tradeEparchyCode = CSBizBean.getTradeEparchyCode();
            IDataset tagInfos = TagInfoQry.getTagInfosByTagCode(tradeEparchyCode, "WIDENET_PREFEE_TAG", "CSM", "0");
            if (IDataUtil.isEmpty(tagInfos))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_502);
            }
            if (tagInfos.getData(0).getString("TAG_INFO").compareTo(trade.getString("ACCEPT_DATE")) <= 0)
            {
                advancePay = 0.0;
                backFee = operFee + foregift;
            }
        }
        feeInfo.put("backOperFee", operFee / 100);
        feeInfo.put("backForeGift", foregift / 100);
        feeInfo.put("backAdvancePay", advancePay / 100);
        feeInfo.put("backFee", backFee / 100);
        return feeInfo;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-24 上午10:27:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-24 chengxf2 v1.0.0 修改原因
     */
    public IDataset queryTradeInfo(IData inData) throws Exception
    {
        String tradeId = inData.getString("TRADE_ID");
        
        IDataset tradeInfos = TradeInfoQry.queryTradeSet(tradeId, null);
        
        //查询不到就从历史表里面查
        if (IDataUtil.isEmpty(tradeInfos))
        {
            IData tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId,"0", getTradeEparchyCode());
            
            tradeInfos.add(tradeInfo);
        }
        
        return tradeInfos;
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-5-24 上午09:01:10 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-5-24 chengxf2 v1.0.0 修改原因
     */
    public IDataset queryUserCancelTrade(IData inData) throws Exception
    {
        String serialNumber = inData.getString("SERIAL_NUMBER");
        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");
        return WidenetTradeQuery.queryUserCancelTrade("KD_" + serialNumber, tradeTypeCode);
    }
    
    /**
     * @Function:互联网电视撤单
     * @date:2015-7-10
     * @author: wuhao
     *        
     */
    public IDataset queryNetTVUserCancelTrade(IData inData) throws Exception
    {
        String serialNumber = inData.getString("SERIAL_NUMBER");
        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");
        return WidenetTradeQuery.queryUserCancelTrade(serialNumber, tradeTypeCode);
    }
    
    public IDataset queryWidenet(IData inData) throws Exception
    {
        String tradeId = inData.getString("TRADE_ID");
        return WidenetTradeQuery.queryWidenet(tradeId);
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-6-20 上午09:09:14 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-6-20 chengxf2 v1.0.0 修改原因
     */
    public IData queryWideAcctType(IData input) throws Exception
    {
        String isMasterAcct = "";
        String roleCodeB = "";
        String userId = "";
        String serialNumber = input.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            userId = userInfo.getString("USER_ID");
        }
        IDataset dataset = RelaUUInfoQry.isMasterAccount(userId, "77");
        if (IDataUtil.isNotEmpty(dataset))
        {
            roleCodeB = dataset.getData(0).getString("ROLE_CODE_B");
        }
        if (StringUtils.isBlank(roleCodeB))
        {
            dataset = RelaUUInfoQry.isMasterAccount(userId, "78");
            if (IDataUtil.isNotEmpty(dataset))
            {
                roleCodeB = dataset.getData(0).getString("ROLE_CODE_B");
            }
        }
        if ("1".equals(roleCodeB))
        {
            isMasterAcct = "主账号";
        }
        else if ("2".equals(roleCodeB))
        {
            isMasterAcct = "子账号";
        }
        else
        {
            isMasterAcct = "普通账号";
        }
        IData result = new DataMap();
        result.put("isMasterAcct", isMasterAcct);
        return result;
    }
    
    public void saveUserRes(String tradeId)throws Exception{
    	IDataset resTradeDatas=TradeResInfoQry.queryAllTradeResByTradeId(tradeId);
    	if(IDataUtil.isNotEmpty(resTradeDatas)){
    		if(resTradeDatas.size()==1){
    			IData resTradeData=resTradeDatas.getData(0);
    			String modifyTag=resTradeData.getString("MODIFY_TAG");
    			
    			//如果是新增机顶盒
    			if(modifyTag.equals(BofConst.MODIFY_TAG_ADD)){
    				UserResInfoQry.saveUserRes(tradeId);
    			}
    		}    		
    	}
    }
    
    public IDataset queryWidenetCancelTradeInfo(IData input) throws Exception{
    	return TradeInfoQry.queryWidenetCancelTrade(input, this.getPagination());
    	
    }

    
    private void topsetboxReleaseTempOccupy(String tradeId, String serialNumber)throws Exception{
    	IDataset checkDatas=TradeResInfoQry.getTradeRes(tradeId, "4", BofConst.MODIFY_TAG_ADD);
    	if(IDataUtil.isNotEmpty(checkDatas)){
    		IData checkData=checkDatas.getData(0);
    		
    		String imei=checkData.getString("IMSI");
    		
    		IData data=new DataMap();
    		data.put("RES_NO", imei);
    		data.put("SERIAL_NUMBER", serialNumber);
    		IDataset retDataset =HwTerminalCall.releaseResTempOccupy(data);
    		
			if (!(IDataUtil.isNotEmpty(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0")))
	        {
				String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为释放预占终端接口调用异常！");
	            CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
	        }
    		
    	}
    	
    }
    
	/**
     * 短信通知客户已撤单
     * @param input
     * @throws Exception
     */
    public void smsNotifyCustomer(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	String userId = input.getString("USER_ID","");
    	String strContent1 = "尊敬的客户，您好！您申请办理宽带开户撤单业务系统已经受理。";
    	
    	if(serialNumber.startsWith("KD_"))
    	{
    		serialNumber = serialNumber.substring(3,serialNumber.length());
    	}
    	
		String forceObject1 = "10086235" + serialNumber;
		IData smsData = new DataMap();
		smsData.put("TRADE_ID", input.getString("TRADE_ID"));
		smsData.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		smsData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
		smsData.put("SMS_PRIORITY", "5000");
		smsData.put("CANCEL_TAG", "3");
		smsData.put("REMARK", "宽带开户撤单短信通知");
		smsData.put("NOTICE_CONTENT_TYPE", "0");
		smsData.put("SMS_TYPE_CODE", "I0");

		if (StringUtils.isNotBlank(strContent1)) {

			smsData.put("RECV_OBJECT", serialNumber);
			smsData.put("RECV_ID", userId);
			smsData.put("FORCE_OBJECT", forceObject1);
			smsData.put("NOTICE_CONTENT", strContent1);

			SmsSend.insSms(smsData);
		}
    }
    
    /**
     * @Function: 融合宽带开户撤单查询
     * @Description: 渠道营业厅只能撤该厅办理的业务，移动自建营业厅不限制
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: zhangyc5
     * @date: 2016-5-31 上午09:01:10 Modification History: Date Author Version Description
     */
    public IDataset queryUserCancelTradeMerge(IData inData) throws Exception
    {
        String serialNumber = inData.getString("SERIAL_NUMBER");
        
        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");
        IDataset cancelTradeInfos = new DatasetList();        
        //家庭IMS固话未完工校验
    	IData useuca = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	IDataset tradeInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(useuca))
        {
        	tradeInfos = TradeInfoQry.getExistUser("MS", useuca.getString("USER_ID"), "1");
        }
        
        IDataset deparKindCodeTypeDataset = UDepartKindInfoQry.qryDeparKindByDepartId(getTradeEparchyCode(), CSBizBean.getVisit().getDepartId());
        
        //如果没查询到则不过滤，正常不存在这种情况
        if (IDataUtil.isNotEmpty(deparKindCodeTypeDataset))
        {
            String codeTypeCode = deparKindCodeTypeDataset.getData(0).getString("CODE_TYPE_CODE");
            
            //CODE_TYPE_CODE 0为代理商，代理商需要过滤非本厅的宽带订单
            if ("0".equals(codeTypeCode))
            {
                String tradeDepartId = CSBizBean.getVisit().getDepartId();
                
                for(int i = 0 ; i < tradeInfos.size() ; i++)
                {
                    if(tradeDepartId.equals(tradeInfos.getData(i).getString("TRADE_DEPART_ID")))
                    {
                        cancelTradeInfos.add(tradeInfos.getData(i));
                    }
                }
            }
            else
            {
                cancelTradeInfos = tradeInfos;
            }
        }
        else
        {
            cancelTradeInfos = tradeInfos;
        }
        	
        return cancelTradeInfos;
    }
    
    /**
     * @Function:铁通外线施工B角员工信息查询
     * @date:2016-7-22
     * @author: wuhao
     *        
     */
    public IDataset queryStaffB(IData inData) throws Exception
    {
        return WidenetTradeQuery.queryStaffB(inData, this.getPagination());
    }
    
    /**
     * @Function:查询员工基本信息
     * @date:2016-7-22
     * @author: wuhao
     *        
     */
    public IDataset queryStaffInfo(IData inData) throws Exception
    {
        return WidenetTradeQuery.queryStaffInfo(inData);
    }
    
    /**
     * @Function:删除B角员工信息
     * @date:2016-7-22
     * @author: wuhao
     *        
     */
    public IDataset deleteStaffB(IData inData) throws Exception
    {
    	boolean flag = false;
    	String[] keys = {"STAFF_ID"};
    	Dao.delete("TD_M_STAFF_B", inData, keys, Route.CONN_CRM_CEN);
        flag = true;
        IDataset result = new DatasetList();
        IData data = new DataMap();
        data.put("SUCCESS", flag);
        result.add(data);
        return result;
    }
    
    /**
     * 保存B角员工信息
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset saveStaffB(IData inparams) throws Exception
    {
        boolean flag = false;
        // 新增数据
        if (inparams.getBoolean("createFlag"))
        {
            flag = Dao.insert("TD_M_STAFF_B", inparams, Route.CONN_CRM_CEN);
        }
        else
        {
            Dao.executeUpdateByCodeCode("TD_M_STAFF", "UPD_BSTAFFINFO", inparams, Route.CONN_CRM_CEN);
        }
        flag = true;
        IDataset result = new DatasetList();
        IData data = new DataMap();
        data.put("SUCCESS", flag);
        result.add(data);
        return result;
    }
    
    public IDataset getCancelReasonTwo(IData data) throws Exception
    {
        String reportType = data.getString("CANCEL_REASON_ONE");
        return StaticUtil.getStaticListByParent("WIDE_CANCEL_REASON_RELATION", reportType);
    }
    
	/**
     * 提供给外部的宽带撤单接口
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset cancelTradeRegIntf(IData input) throws Exception
    {
    	//流水号
    	IDataUtil.chkParam(input, "TRADE_ID");
    	//服务号码
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	//
    	IDataUtil.chkParam(input, "CITY_CODE");
    	//取消原因
    	IDataUtil.chkParam(input, "CANCEL_REASON");
    	return cancelTradeReg(input);
    }
}
