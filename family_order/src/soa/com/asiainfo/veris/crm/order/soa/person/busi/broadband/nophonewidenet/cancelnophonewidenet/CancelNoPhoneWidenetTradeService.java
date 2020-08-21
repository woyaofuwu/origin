package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.cancelnophonewidenet;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.out.TradeCancelFee;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.StateTaxUtil;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.NoPhoneWideUserCreateBean;
import com.asiainfo.veris.crm.order.soa.person.busi.canceltrade.CancelTradeBean;
import com.asiainfo.veris.crm.order.soa.person.common.query.broadband.WidenetTradeQuery;


/**
 * 无手机宽带业务取消
 * @author yuyj3
 *
 */
public class CancelNoPhoneWidenetTradeService extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * 调用服开接口校验能否撤单
     * @param pubData
     * @param tradeInfo
     * @param cancelType
     * @return
     * @throws Exception
     * @author yuyj3
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
     * 业务取消提交
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset cancelTradeReg(IData input) throws Exception
    {
        String tradeId = input.getString("TRADE_ID");
        IData tradeInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId,"0", getTradeEparchyCode());
        
        //无手机宽带移机撤单，需要从现表取数据
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE","");
        if("686".equals(tradeTypeCode)&&StringUtils.isNotEmpty(tradeTypeCode)){
        	String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
            IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");
            if (IDataUtil.isEmpty(tradeInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_67, tradeId);// 在TF_B_TRADE未找到有效记录,TRADE_ID:%s
            }
            tradeInfo = tradeInfos.getData(0);
        }else if("4910".equals(tradeTypeCode)){	//如果是无手机魔百和开户撤单
        	
        	/*
        	 * 形成退款的客户资源信息，插入一条魔百和的数据，用作退库使用
        	 */
        	String isOpenTopsetbox=tradeInfo.getString("RSRV_STR2",""); //魔百和正式受理or换机(4910)
        	if(isOpenTopsetbox.equals("1")){
        		IDataset resTradeInfo=TradeResInfoQry.getTradeRes(tradeId, "4", BofConst.MODIFY_TAG_ADD);
        		if(IDataUtil.isNotEmpty(resTradeInfo)){
        			String rsrvTag2=resTradeInfo.getData(0).getString("RSRV_TAG2","");
        			if(rsrvTag2.equals("1")){		//如果是新用户的魔百和业务
        				IDataset rollBackTopsetboxs = UserResInfoQry.queryRollbackTopSetBox(tradeInfo.getString("USER_ID"));
        				
        				if(IDataUtil.isNotEmpty(rollBackTopsetboxs)){
        					CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户的机顶盒没有做退库操作，请先做退库操作，再进行撤单!");
        				}
        			}
        		}
        	}
        	
        	/*
        	 * 如果是魔百和业务，需要取消魔百和的机顶盒的预占
        	 */
        	String serialNumber=tradeInfo.getString("RSRV_STR8");
        	topsetboxReleaseTempOccupy(tradeId, serialNumber);
        	
        }else if ("680".equals(tradeTypeCode)) {
        	
        	/*
             * 如果存在生效的魔百和业务
             * */
        	String wnSerialNumber=tradeInfo.getString("RSRV_STR6");
        	if(StringUtils.isEmpty(wnSerialNumber))
        	{
        		String userId = tradeInfo.getString("USER_ID");
        		//查询UU关系表
        		IDataset tradeRelas = TradeRelaInfoQry.getTradeUUByUserIdBAndTypecode(userId,"47","1");
        		if(!IDataUtil.isEmpty(tradeRelas))
        		{
        			wnSerialNumber = tradeRelas.first().getString("SERIAL_NUMBER_A");
        		}
        	}
        	
        	IData userinfo=UcaInfoQry.qryUserInfoBySn(wnSerialNumber);
        	
        	if(IDataUtil.isNotEmpty(userinfo))
        	{
        		String userId=userinfo.getString("USER_ID");
        		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
        		if(IDataUtil.isNotEmpty(boxInfos))
        		{
        			String rsrvTag2=boxInfos.getData(0).getString("RSRV_TAG2","");
        			
        			if(rsrvTag2.equals("1"))
        			{		//如果是新用户的魔百和业务，在宽带撤单时，需要终止掉魔百和业务
        				/*
            			 * 如果用户存在有效的魔百和业务，就生成异步订单来终止用户的魔百和
            			 */
            			IData param=new DataMap();
                		param.put("SERIAL_NUMBER", wnSerialNumber);
                		
                		//标识为宽带开户撤单调用的
                		param.put("IS_MERGE_WIDE_CANCEL", "1");
                        
                        //标识为进行退机顶盒
                		param.put("IS_RETURN_TOPSETBOX", "1");
                		param.put(Route.ROUTE_EPARCHY_CODE, tradeInfo.getString("TRADE_EPARCHY_CODE","0898"));
                		
                		/*
                		 * 进行无手机魔百和拆机
                		 */
                    	IDataset destroyResult=CSAppCall.call("SS.NoPhoneTopSetBoxDestroyRegSVC.tradeReg", param);
                    	
                		if(IDataUtil.isNotEmpty(destroyResult)){
                			String orderId=destroyResult.getData(0).getString("ORDER_ID","");
                			if(!orderId.equals(""))
                			{
                			  //更新魔百和已撤单标记
                                IDataset topSetBoxInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId, "TOPSETBOX");   
                                if(IDataUtil.isNotEmpty(topSetBoxInfos))
                                {
                                    IData topSetBox = topSetBoxInfos.getData(0);
                                    String rsrvTag1 = topSetBox.getString("RSRV_TAG1","");
                                    if("Y".equals(rsrvTag1))
                                    {
                                        IData otherData = new DataMap();
                                        otherData.put("RSRV_TAG1", "C");
                                        otherData.put("RSRV_TAG2", "1");  //RSRV_TAG2 = 1 已撤单
                                        otherData.put("RSRV_STR19", destroyResult.getData(0).getString("ORDER_ID","")); //退订魔百和订单ID
                                        otherData.put("RSRV_STR20", destroyResult.getData(0).getString("TRADE_ID","")); //退订魔百和台账ID
                                        otherData.put("RSRV_VALUE_CODE", "TOPSETBOX");
                                        otherData.put("TRADE_ID", tradeId);
                                        //更新TF_B_TRADE_OHTER表，标记魔百和已撤单
                                        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TOPSETBOX_CANCELTAG", otherData, Route.getJourDb(BizRoute.getTradeEparchyCode()));
                                    }
                                }

                			}else{
                				CSAppException.apperr(CrmCommException.CRM_COMM_103, "退订魔百和业务失败，失败原因："+destroyResult.getData(0).getString("X_RESULTINFO",""));
                			}
                		}else{
                			CSAppException.apperr(CrmCommException.CRM_COMM_103, "退订魔百和业务失败!");
                		}
        			}
        		}
        	}
		}
        //end
        if (IDataUtil.isEmpty(tradeInfo))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "在TF_BH_TRADE未找到"+tradeId+"有效记录!");// 在TF_B_TRADE未找到有效记录,TRADE_ID:%s
        }
        
        //校验是否需要发票冲红
        String needCHReceipt = StateTaxUtil.needCHReceipt(tradeInfo.getString("TRADE_ID",""));
        if(StringUtils.isNotEmpty(needCHReceipt))
        {
            CSAppException.appError("-1", "请先冲红原发票再做撤销,发票号为:"+needCHReceipt);
        }
        
       
            
        /**************************** 数据准备 *************************/
        IData pubData = this.getPublicData(tradeInfo);// 操作员/trade_id/cancel_tag等相关信息

        
        /********************** 相关资料处理 **************************/
        dealTradeCancel(pubData, input, tradeInfo);
        
        /**************************** 发送PBOSS撤单申请 *************************/
        String cancelType = input.getString("CANCEL_TYPE", "0");
        IDataOutput dataOutput = applyCancelPbossTrade(input, pubData, tradeInfo, cancelType);
        IData head = dataOutput.getHead();//服开返回报文头
        String resultCode = head.getString("X_RESULTCODE", "-1");
        
        if (!"0".equals(resultCode))
        {
            String resultInfo = head.getString("X_RESULTINFO");
            CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
        }
        
        IDataset result = dataOutput.getData();
        
        if (IDataUtil.isNotEmpty(result))
        {
            IData tmpData = result.getData(0);
            String xResultCode = tmpData.getString("X_RESULTTYPE", "-1");
            
            if (!"0".equals(xResultCode))
            {
            	String resultInfo = tmpData.getString("X_RESULTINFO");
            	CSAppException.apperr(TradeException.CRM_TRADE_95, resultInfo);
            }
        }
        
        if("686".equals(tradeTypeCode)&&StringUtils.isNotEmpty(tradeTypeCode)){
        	//如果是宽带移机撤单，则不进行资料处理
        }else{
        	IData inParam = new DataMap();
        
        	inParam.put("SERIAL_NUMBER", tradeInfo.getString("SERIAL_NUMBER"));
        	inParam.put("CREATE_WIDEUSER_TRADE_ID", tradeId);
        
        	//退还光猫
        	inParam.put("MODEM_RETUAN", "1");
        
        	inParam.put("REMARK", "无手机宽带开户撤单资料注销");
        
        	//无手机宽带开户撤单资料注销
        	inParam.put("TRADE_TYPE_CODE", "689");
        	inParam.put("ORDER_TYPE_CODE", "689");
        
        	//跳过校验规则
        	inParam.put("SKIP_RULE", "TRUE");
        
        	//无手机宽带拆机
        	CSAppCall.call("SS.DestroyNoPhoneWidenetUserNowRegSVC.tradeReg", inParam);
        }
        
        //撤销费用票据等处理
        dealCancelFeeTicket(tradeInfo);
        
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
        if (!Dao.insert("TF_BH_ORDER", orderData, Route.getJourDb()))
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
        if (!Dao.insert("TF_BH_TRADE", tradeData, Route.getJourDb()))
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
        if (!Dao.insert("TF_BH_TRADE_STAFF", tradeData, Route.getJourDb()))
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
        Dao.executeUpdateByCodeCode("TF_BH_TRADE_STAFF", "UPD_STAFF_CANCEL_TAG", param, Route.getJourDb());

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

        Dao.insert("TF_BH_TRADE_STAFF", newData, Route.getJourDb());
    }

    /**
     * 生成新的撤销订单
     * @param pubData
     * @param pgData
     * @param orderInfo
     * @throws Exception
     * @author yuyj3
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
        if (!Dao.insert("TF_B_ORDER", orderData, Route.getJourDb()))
        {
            String msg = "生成返销订单【" + orderInfo.getString("ORDER_ID") + "】失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
    }

    /**
     * 新建trade
     * @param pubData
     * @param pgData
     * @param tradeInfo
     * @throws Exception
     * @author yuyj3
     */
    private void createNewTrade(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        IData tradeData = new DataMap();
        tradeData.putAll(tradeInfo);
        tradeData.put("ACCEPT_DATE", pubData.getString("SYS_TIME"));
        tradeData.put("ACCEPT_MONTH", pubData.getString("NEW_ORDER_ID").substring(4, 6)); //修改跨月撤单月份不一致的问题
        tradeData.put("ORDER_ID", pubData.getString("NEW_ORDER_ID"));
        tradeData.put("CANCEL_TAG", "3"); // 撤销
        //tradeData.put("CANCEL_DATE", pubData.getString("SYS_TIME"));  //撤单时间
        tradeData.put("CANCEL_DATE", tradeInfo.getString("ACCEPT_DATE")); //被返销的业务受理时间
        tradeData.put("SUBSCRIBE_STATE", "0");
        tradeData.put("CANCEL_STAFF_ID", tradeInfo.getString("TRADE_STAFF_ID"));
        tradeData.put("CANCEL_DEPART_ID", tradeInfo.getString("TRADE_DEPART_ID"));
        tradeData.put("CANCEL_CITY_CODE", tradeInfo.getString("TRADE_CITY_CODE"));
        tradeData.put("CANCEL_EPARCHY_CODE", tradeInfo.getString("TRADE_EPARCHY_CODE"));
        tradeData.put("OPER_FEE", tradeInfo.getString("OPER_FEE"));    //更新20170111
        tradeData.put("ADVANCE_PAY", tradeInfo.getString("ADVANCE_PAY"));//更新20170111
        tradeData.put("FOREGIFT",  tradeInfo.getString("FOREGIFT"));//更新20170111
        tradeData.put("FEE_STATE", tradeInfo.getString("FEE_STATE"));//更新20170111
        tradeData.put("FEE_TIME", "");
        tradeData.put("FEE_STAFF_ID", "");
        tradeData.put("TRADE_STAFF_ID", pubData.getString("TRADE_STAFF_ID"));
        tradeData.put("TRADE_DEPART_ID", pubData.getString("TRADE_DEPART_ID"));
        tradeData.put("TRADE_CITY_CODE", pubData.getString("TRADE_CITY_CODE"));
        tradeData.put("TRADE_EPARCHY_CODE", pubData.getString("TRADE_EPARCHY_CODE"));
        tradeData.put("EXEC_TIME", pubData.getString("SYS_TIME"));
        tradeData.put("PF_WAIT", "1"); //返销要配置成闭环
        tradeData.put("REMARK", pgData.getString("CANCEL_REASON_ONE","")+"|"+pgData.getString("REMARK",""));
        
        if (!Dao.insert("TF_B_TRADE", tradeData, Route.getJourDb()))
        {
            String msg = "生成返销订单【" + tradeInfo.getString("TRADE_ID") + "】失败!";
            CSAppException.apperr(TradeException.CRM_TRADE_95, msg);
        }
    }

    /**
     * 无手机宽带业务撤单订单处理
     * @param pubData
     * @param pgData
     * @param tradeInfo
     * @throws Exception
     * @author yuyj3
     */
    private void dealOrderCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        String orderId = tradeInfo.getString("ORDER_ID");
        IData orderInfo = UOrderInfoQry.qryOrderAllByOrderId(orderId);
        
        String[] keys = new String[3];
        keys[0]  = "ORDER_ID";
        keys[1]  = "ACCEPT_MONTH";
        keys[2]  = "CANCEL_TAG";
        
        boolean delFlag = Dao.delete("TF_B_ORDER", orderInfo, keys, Route.getJourDb());
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
        
        createHisOrder(pubData, pgData, orderInfo);
        
        String orderKindCode = orderInfo.getString("ORDER_KIND_CODE", "");
        if("1".equals(orderKindCode))
        {
        	//融合业务撤单时，单条撤单，作为单笔业务处理
        	orderInfo.put("ORDER_KIND_CODE", "");
        }
        createNewOrder(pubData, pgData, orderInfo);
    }

    
    /**
     * 无手机宽带业务撤单处理
     * @param pubData
     * @param pgData
     * @param tradeInfo
     * @throws Exception
     * @author yuyj3
     */
    private void dealTradeCancel(IData pubData, IData pgData, IData tradeInfo) throws Exception
    {
        dealOrderCancel(pubData, pgData, tradeInfo); // 处理TF_B_ORDER
        
        String[] keys = new String[3];
        keys[0]  = "ORDER_ID";
        keys[1]  = "ACCEPT_MONTH";
        keys[2]  = "CANCEL_TAG";
        
        boolean delFlag = Dao.delete("TF_B_TRADE", tradeInfo, keys, Route.getJourDb());
        if(!delFlag)
        {
            NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);
            
            bean.updateTradeBhInfo(tradeInfo.getString("TRADE_ID"),pubData.getString("SYS_TIME"),pubData.getString("TRADE_STAFF_ID"),pubData.getString("TRADE_DEPART_ID"),pubData.getString("TRADE_CITY_CODE"),pubData.getString("TRADE_EPARCHY_CODE"));
        }
        else
        {
            createHisTrade(pubData, pgData, tradeInfo);
        }
        
        createCancelStaffTrade(pubData, pgData, tradeInfo);
        
        createNewTrade(pubData, pgData, tradeInfo);
    }
    
    
    /**
     * 无手机宽带业务撤单处理
     * @param pubData
     * @param pgData
     * @param tradeInfo
     * @throws Exception
     * @author yuyj3
     */
    private void dealCancelFeeTicket(IData tradeInfo) throws Exception
    {
    	String tradeTypeCode = tradeInfo.getString("TRADE_TYPE_CODE","");
        if("686".equals(tradeTypeCode)&&StringUtils.isNotEmpty(tradeTypeCode)){
        	//无手机宽带移机撤单不需要费用返销
        }else{
        	IData pgData = new DataMap();
        	pgData.put("CANCEL_TYPE", "0");
        
        	// 费用返销
        	TradeCancelFee.cancelRecvFee(tradeInfo, pgData);
        }
        
        CancelTradeBean bean = BeanManager.createBean(CancelTradeBean.class);
        
        //返销票据
        bean.cancelTicketTrade(tradeInfo.getString("TRADE_ID"));
        bean.cancelEInvoice(tradeInfo);//新增:电子发票返销
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
     * 退还费用查询
     * @param input
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IData queryTradeBackFee(IData input) throws Exception
    {
        IData feeInfo = new DataMap();
        String tradeId = input.getString("TRADE_ID");
        IData trade = UTradeHisInfoQry.qryTradeHisByPk(tradeId,"0", getTradeEparchyCode());
        //无手机宽带移机撤单，需要从现表取数据
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE","");
        if("686".equals(tradeTypeCode)&&StringUtils.isNotEmpty(tradeTypeCode)){
        	String acceptMonth = StrUtil.getAcceptMonthById(tradeId);
            IDataset tradeInfos = UTradeInfoQry.qryTradeByTradeIdMonth(tradeId, acceptMonth, "0");
            if (IDataUtil.isEmpty(tradeInfos))
            {
                CSAppException.apperr(TradeException.CRM_TRADE_67, tradeId);// 在TF_B_TRADE未找到有效记录,TRADE_ID:%s
            }
            trade = tradeInfos.getData(0);
        }
        //end
        if (IDataUtil.isEmpty(trade))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_67, tradeId);// 在TF_B_TRADE未找到有效记录,TRADE_ID:%s
        }
        
        double operFee = trade.getDouble("OPER_FEE", 0.0);
        double foregift = trade.getDouble("FOREGIFT", 0.0);
        double advancePay = trade.getDouble("ADVANCE_PAY", 0.0);
        double backFee = operFee + foregift + advancePay;
        feeInfo.put("backOperFee", operFee / 100);
        feeInfo.put("backForeGift", foregift / 100);
        feeInfo.put("backAdvancePay", advancePay / 100);
        feeInfo.put("backFee", backFee / 100);
        return feeInfo;
    }

    
    /**
     * @Function: 无手机宽带业务撤单
     * @Description: 渠道营业厅只能撤该厅办理的业务，移动自建营业厅不限制
     * @version: v1.0.0
     * @author: yuyj4
     */
    public IDataset queryUserCancelTradeNoPhone(IData inData) throws Exception
    {
        String serialNumber = inData.getString("SERIAL_NUMBER");
        
        //支持前台输入KD_也能查询到
        if ("KD_".equals(serialNumber.substring(0, 3)))
        {
            serialNumber = serialNumber.substring(3);
        }
        
        String tradeTypeCode = inData.getString("TRADE_TYPE_CODE");
        IDataset cancelTradeInfos = new DatasetList();
        IDataset tradeInfos = new DatasetList();
        
        if("686".equals(tradeTypeCode)){//如果是无手机宽带移机，从TF_B_TRADE取
        	tradeInfos = WidenetTradeQuery.queryUserCancelTrade("KD_" + serialNumber, tradeTypeCode);
        }else{
        	tradeInfos = TradeBhQry.queryCancelNoPhoneTrade("KD_" + serialNumber, tradeTypeCode);
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
    
    
}
