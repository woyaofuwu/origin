
package com.asiainfo.veris.crm.order.soa.person.busi.np.destroynp;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * 
 * @ClassName: InNpUserDestroyBean.java
 * @Description: 192--携入销户(49工单)后发起清楚HLR上数据的销户，只登记主台账和资源台账，便于发送指令用
 * @version: v1.0.0
 * @author: tanjl
 * @date: 2015-3-18 
 */
public class InNpUserDestroyBean extends CSBizBean {
	/**
	 * 登记订单信息
	 * 
	 * @param param
	 * @param orderInfo
	 * @throws Exception
	 */
    private void createOrderData(IData param, IData orderInfo) throws Exception {
        MainOrderData mainOrderData = new MainOrderData();
        IData resultSet = null;

        // 查找tradeTypeInfo
        TradeTypeData tradeType = null;
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        resultSet = UTradeTypeInfoQry.getTradeType(tradeTypeCode, BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(resultSet)){
            tradeType = new TradeTypeData(resultSet);
        }

        String inModeCode = CSBizBean.getVisit().getInModeCode();
        String order_id = SeqMgr.getOrderId();
        param.put("ORDER_ID", order_id);
        param.put("PRIORITY", tradeType.getPriority());
        param.put("EPARCHY_CODE", orderInfo.getString("EPARCHY_CODE"));
        mainOrderData.setOrderTypeCode(tradeTypeCode);
        mainOrderData.setOrderId(order_id);
        mainOrderData.setTradeTypeCode(tradeTypeCode);
        mainOrderData.setOrderState("0");
        mainOrderData.setPriority(tradeType.getPriority());
        mainOrderData.setNextDealTag("0");
        mainOrderData.setInModeCode(inModeCode);
        mainOrderData.setTradeStaffId(CSBizBean.getVisit().getStaffId());
        mainOrderData.setTradeDepartId(CSBizBean.getVisit().getDepartId());
        mainOrderData.setTradeCityCode(CSBizBean.getVisit().getCityCode());
        mainOrderData.setTradeEparchyCode(CSBizBean.getTradeEparchyCode());
        mainOrderData.setFeeState("0");
        mainOrderData.setOperFee("0");
        mainOrderData.setForegift("0");
        mainOrderData.setAdvancePay("0");
        mainOrderData.setExecTime(param.getString("ExecTime"));
        mainOrderData.setCancelTag("0");
        mainOrderData.setCustId(orderInfo.getString("CUST_ID"));
        mainOrderData.setCustName(orderInfo.getString("CUST_NAME"));
        mainOrderData.setPsptTypeCode(orderInfo.getString("PSPT_TYPE_CODE"));
        mainOrderData.setPsptId(orderInfo.getString("PSPT_ID"));
        mainOrderData.setEparchyCode(orderInfo.getString("EPARCHY_CODE"));
        mainOrderData.setCityCode(orderInfo.getString("CITY_CODE"));
        mainOrderData.setSubscribeType("0");

        IData data = mainOrderData.toData();
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(order_id));
        data.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        Dao.insert("TF_B_ORDER", data, Route.getJourDb());
    }

    private void regBusiMainTradeData(IData param, IData tradeInfo) throws Exception {
        MainTradeData mainTradeData = new MainTradeData();
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        mainTradeData.setTradeTypeCode(tradeTypeCode);

        String tradeId = SeqMgr.getTradeId();
        param.put("NEW_TRADE_ID", tradeId);
        mainTradeData.setOrderId(param.getString("ORDER_ID"));
        mainTradeData.setUserId(tradeInfo.getString("USER_ID"));
        mainTradeData.setCustId(tradeInfo.getString("CUST_ID"));
        mainTradeData.setAcctId(tradeInfo.getString("ACCT_ID"));
        mainTradeData.setCustName(tradeInfo.getString("CUST_NAME"));
        mainTradeData.setPriority(param.getString("PRIORITY"));
        mainTradeData.setOlcomTag("2");// 0不发指令
        mainTradeData.setExecTime(param.getString("ExecTime"));
        mainTradeData.setSerialNumber(tradeInfo.getString("SERIAL_NUMBER"));
        mainTradeData.setSubscribeState(BofConst.SUBSCRIBE_TYPE_NORMAL_NOW);
        mainTradeData.setSubscribeType(param.getString("SUBSCRIBE_TYPE"));
        mainTradeData.setNextDealTag("0");
        mainTradeData.setInModeCode(CSBizBean.getVisit().getInModeCode());
        mainTradeData.setProcessTagSet(BofConst.PROCESS_TAG_SET);
        mainTradeData.setCancelTag(BofConst.CANCEL_TAG_NO);
        mainTradeData.setNetTypeCode("00");
        mainTradeData.setEparchyCode(CSBizBean.getTradeEparchyCode());
        mainTradeData.setProductId(tradeInfo.getString("PRODUCT_ID"));
        mainTradeData.setBrandCode(tradeInfo.getString("BRAND_CODE"));
        mainTradeData.setCityCode(CSBizBean.getVisit().getCityCode());

        IData data = mainTradeData.toData();
        data.put("TRADE_ID", tradeId);
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        data.put("ACCEPT_DATE", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        data.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        data.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("UPDATE_TIME", SysDateMgr.getSysDate("yyyy-MM-dd HH:mm:ss"));
        data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

        Dao.insert("TF_B_TRADE", data, Route.getJourDb());
    }

    /**
     * 登记资源子台账，主要便于发送指令使用
     * 
     * @param param
     * @param userResInfos
     * @throws Exception
     */
    private void regResUserTradeData(IData param,IDataset userResInfos) throws Exception{
        IData tradeRes = new DataMap();
        String trade_id = param.getString("NEW_TRADE_ID");
        
        IData userResInfo = new DataMap();  //用户资源表中的信息
        for(int i=0;i<userResInfos.size();i++){
    		userResInfo = userResInfos.getData(i);
    		//处理和SIM卡的信息就可以了，主要是方便来发送联指
    		if("0".equals(userResInfo.getString("RES_TYPE_CODE")) || "1".equals(userResInfo.getString("RES_TYPE_CODE"))){
    			tradeRes.put("TRADE_ID", 		trade_id);
    			tradeRes.put("ACCEPT_MONTH", 	StrUtil.getAcceptMonthById(trade_id));
    			tradeRes.put("MODIFY_TAG", 		"1");
    			tradeRes.putAll(userResInfo);
    			Dao.insert("TF_B_TRADE_RES", tradeRes, Route.getJourDb());
    		}
    	}
    }

    /**
     * @Function: tradeReg
     * @Description: 49工单成功后
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: tanjl
     * @date: 2015年3月20日 下午2:23:49
     */
    public IDataset tradeReg(IData param) throws Exception{
        String tradeTypeCode = "409";
        String SubscribeType = "0";// 1是预约
        String npSerialNumber = param.getString("SERIAL_NUMBER");     //获取对应的手机号码
        
        param.put("ExecTime", SysDateMgr.getSysTime());
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SUBSCRIBE_TYPE", SubscribeType);
        //查询用户最后一次销户的数据   携入销户
        IData userInfo = new DataMap();
        
        IDataset lastDestroyUserInfo = UserInfoQry.getDestroyUserInfoBySn(npSerialNumber);
        if (lastDestroyUserInfo != null && lastDestroyUserInfo.size() > 0){
            userInfo = (IData) lastDestroyUserInfo.get(0);
            //查询携转信息
            IData customerInfo = CustomerInfoQry.qryCustInfo(userInfo.getString("CUST_ID"));
            if(IDataUtil.isEmpty(customerInfo)){
            	CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据CUST_ID=【" + userInfo.getString("CUST_ID") + "】获取客户资料失败！");
            }
            //查询付费关系
            IData payRelaInfo = UcaInfoQry.qryPayRelaByUserId(userInfo.getString("USER_ID"));   //付费关系信息获取ACCT_ID
            if (IDataUtil.isEmpty(payRelaInfo)){
            	CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据USER_ID=【" + userInfo.getString("USER_ID") + "】获取用户付费关系表资料失败！");
            }
            //查询产品信息
            IData userProductInfo = UserProductInfoQry.qryLasterMainProdInfoByUserId(userInfo.getString("USER_ID"));
            if (IDataUtil.isEmpty(userProductInfo)){
            	CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据USER_ID=【" + userInfo.getString("USER_ID") + "】获取用户产品信息失败！");
            }
            IData orderMap = new DataMap();
            IData tradeMap = new DataMap();
            
            orderMap.put("EPARCHY_CODE", 	userInfo.getString("EPARCHY_CODE"));
            orderMap.put("CITY_CODE", 		userInfo.getString("CITY_CODE"));
            orderMap.put("CUST_ID", 		userInfo.getString("CUST_ID"));
            orderMap.put("CUST_NAME", 		customerInfo.getString("CUST_NAME"));
            orderMap.put("PSPT_TYPE_CODE", 	customerInfo.getString("PSPT_TYPE_CODE"));
            orderMap.put("PSPT_ID", 		customerInfo.getString("PSPT_ID"));
            
            tradeMap.put("USER_ID",			userInfo.getString("USER_ID"));
            tradeMap.put("CUST_ID",			userInfo.getString("CUST_ID"));
            tradeMap.put("ACCT_ID",			payRelaInfo.getString("ACCT_ID"));
            tradeMap.put("CUST_NAME",		customerInfo.getString("CUST_NAME"));
            tradeMap.put("CUST_NAME",		customerInfo.getString("CUST_NAME"));
            tradeMap.put("NET_TYPE_CODE",	"00");
            tradeMap.put("SERIAL_NUMBER",	npSerialNumber);
            tradeMap.put("PRODUCT_ID",		userProductInfo.getString("PRODUCT_ID"));
            tradeMap.put("BRAND_CODE",		userProductInfo.getString("BRAND_CODE"));

            createOrderData(param, orderMap);
            regBusiMainTradeData(param, tradeMap);
            
            //获取对应的资源信息，主要用来后面登记资源台账进行发送销户指令
            IDataset userResInfos = UserResInfoQry.getUserResMaxDateByUserId(userInfo.getString("USER_ID"));
            if(userResInfos !=null && userResInfos.size() > 0){
            	regResUserTradeData(param,userResInfos);
            }
        }else{
            CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "根据USER_ID=【" + userInfo.getString("USER_ID") + "】获取用户资料失败！");
        }
        IDataset set = new DatasetList();
        IData d = new DataMap();
        d.put("TRADE_ID", param.getString("NEW_TRADE_ID", ""));
        d.put("ORDER_ID", param.getString("ORDER_ID", ""));
        set.add(d);
        return set;
    }
}
