
package com.asiainfo.veris.crm.order.soa.group.creategroupmember;

import java.math.BigDecimal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupmember.CreateGroupMember;
import com.asiainfo.veris.crm.order.soa.group.cenpayspecial.CenpayGfffUserLogQry;
import com.asiainfo.veris.crm.order.soa.group.cenpayspecial.CenpayTimeUtils;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;

/**
 * 集团流量自由充成员产品、指定用户，定额统付（流量包） 
 * @author think
 *
 */
public class CreateGfffGroupDingEMember extends CreateGroupMember
{

    private IData paramData = new DataMap();
    private String smsFlag = "";
    private String sendForSms = "";
    private String sendNoticeContent = "";
    private IDataset mebcenpayList = new DatasetList();
    
    public CreateGfffGroupDingEMember()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

        paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            smsFlag = paramData.getString("NOTIN_SMS_FLAG");
            if(StringUtils.isNotBlank(smsFlag) && "1".equals(smsFlag)){
                sendForSms = paramData.getString("NOTIN_sendForSms");
                if(StringUtils.isNotBlank(sendForSms) && "1".equals(sendForSms)){//编辑短信内容
                    reqData.setNeedSms(false); 
                    sendNoticeContent = paramData.getString("NOTIN_SmsInfo");
                } else {
                    reqData.setNeedSms(true);
                }
            } else {
                reqData.setNeedSms(false);
            }
        }
        
    }

    private IData getParamData() throws Exception
    {
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paramData = reqData.cd.getProductParamMap(baseMemProduct);
        if (IDataUtil.isEmpty(paramData))
        {
            return null;
        }
        return paramData;
    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        String dicntCodeEndDate = "";
        super.actTradeSub();
                
        dicntCodeEndDate = infoUserDiscnt();
        infoMebUserUU(dicntCodeEndDate);
        infoMebCenPay(dicntCodeEndDate);
        infoMebPayPlan(dicntCodeEndDate);
        infoMebProduct(dicntCodeEndDate);
        
        //手工编辑短信内容
        if(StringUtils.isNotBlank(smsFlag) && "1".equals(smsFlag) &&
                StringUtils.isNotBlank(sendForSms) && "1".equals(sendForSms)){
            if(StringUtils.isNotBlank(sendNoticeContent)){
                infoRegDataSms(); 
            }
        }
        
        insertRecordLog();
        
    }

    /**
     * 获取优惠的结束时间
     * @param endDate
     * @throws Exception
     */
    private String infoUserDiscnt() throws Exception
    {        
        String endDate = "";
        IDataset discntDatas = reqData.cd.getDiscnt();
        if(IDataUtil.isNotEmpty(discntDatas)){
            IDataset packageInfos = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430002");
            for(int i=0; i < discntDatas.size(); i++){
                IData discntData = discntDatas.getData(i);
                if(IDataUtil.isNotEmpty(discntData)){
                    String eleTypeCode =  discntData.getString("ELEMENT_TYPE_CODE","");
                    String modifyTag =  discntData.getString("MODIFY_TAG","");
                    String elementId =  discntData.getString("ELEMENT_ID","");
                    
                    if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos, "ELEMENT_ID="+elementId)) && "D".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                        endDate = discntData.getString("END_DATE","");
                        
                        IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "7347", "GRP_GFFF_DINGE",elementId, "0898");
                        if(IDataUtil.isEmpty(commInfos)){
                            //获取定额统付流量叠加包转换异常
                            CSAppException.apperr(GrpException.CRM_GRP_852);
                        }
                        IData commData = commInfos.getData(0);                        
                        mebcenpayList.add(commData);
                        
                        break;
                    }
                }
            }
        }
        
        return endDate;
    }
    
    /**
     * 修改uu关系的结束时间为优惠的时间
     * @param endDate
     * @throws Exception
     */
    private void infoMebUserUU(String endDate) throws Exception
    {
        if(StringUtils.isNotBlank(endDate)){
            IDataset tradeUUDatas = bizData.getTradeRelation();
            if(IDataUtil.isNotEmpty(tradeUUDatas)){
                for(int i=0; i < tradeUUDatas.size(); i++){
                    IData tradeUUData = tradeUUDatas.getData(i);
                    if(IDataUtil.isNotEmpty(tradeUUData)){
                       tradeUUData.put("END_DATE", endDate);
                    }
                }
            }
        }
    }
    
    /**
     * 修改结束时间为优惠的结束时间
     * @param endDate
     * @throws Exception
     */
    private void infoMebCenPay(String endDate) throws Exception
    {
        if(IDataUtil.isNotEmpty(mebcenpayList)){
            
            for (int row = 0, size = mebcenpayList.size(); row < size; row++){
                
                IData data = mebcenpayList.getData(row);
                
                IData mebcenpay = new DataMap();
                
                mebcenpay.put("USER_ID", reqData.getUca().getUserId());
                mebcenpay.put("INST_ID", SeqMgr.getInstId());
                //mebcenpay.put("MP_GROUP_CUST_CODE", "0");
                mebcenpay.put("MP_GROUP_CUST_CODE", reqData.getGrpUca().getUserId());
                
                mebcenpay.put("PAY_TYPE", "1");
                //REQ201707170012关于新增流量自由充省内通用定额套餐的需求----------------20170828 start----------
                String payType = "";
                IDataset disCnts = reqData.cd.getDiscnt();
                if(IDataUtil.isNotEmpty(disCnts)&&disCnts.size()>0){
                	for(int i=0; i< disCnts.size();i++){
                		IData disCnt = disCnts.getData(i);
                		String disCntCode = disCnt.getString("DISCNT_CODE", "");
                		String modifyTag = disCnt.getString("MODIFY_TAG", "");
                		if("0".equals(modifyTag))
                		{
                			IDataset comInfosDataset = RouteInfoQry.getCommparaByCode("CSM", "8813", disCntCode, "0898");
                			if(IDataUtil.isNotEmpty(comInfosDataset)&&comInfosDataset.size()>0){
                				payType = comInfosDataset.getData(0).getString("PARA_CODE1", "");
                				if(StringUtils.isNotBlank(payType)){
                					mebcenpay.put("PAY_TYPE", payType);
                				}
                			}
                		}
                	}
                }else{
                	IDataset grpDatas = UserGrpInfoQry.queryGrpCenPayInfo2(reqData.getGrpUca().getUserId());
                	if(IDataUtil.isNotEmpty(grpDatas)&&grpDatas.size()>0){
                		payType = grpDatas.getData(0).getString("PAY_TYPE", "");
        				if(StringUtils.isNotBlank(payType)){
        					mebcenpay.put("PAY_TYPE", payType);
        				}
                	}
                }
                //REQ201707170012关于新增流量自由充省内通用定额套餐的需求----------------20170828 end----------
                
                mebcenpay.put("OPER_TYPE", "4");//4:指定用户、定额统付
                //mebcenpay.put("PRODUCT_OFFER_ID", "0");
                mebcenpay.put("PRODUCT_OFFER_ID", reqData.getGrpUca().getProductId());
                mebcenpay.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
                
                //mebcenpay.put("LIMIT_FEE", "");
                mebcenpay.put("ELEMENT_ID", data.getString("PARA_CODE2",""));//个人流量包ID
                
                mebcenpay.put("START_DATE", SysDateMgr.getSysTime());
                if(StringUtils.isNotBlank(endDate)){
                    mebcenpay.put("END_DATE", endDate);
                } else {
                    mebcenpay.put("END_DATE", SysDateMgr.getTheLastTime());
                }
                mebcenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
                //mebcenpay.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
                mebcenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                
                mebcenpay.put("RSRV_STR1", data.getString("PARA_CODE1",""));
                mebcenpay.put("RSRV_STR2", data.getString("PARAM_NAME",""));
                
                this.addTradeMebCenpay(mebcenpay);
            }
        }
       
    }
    
    /**
     * 修改付费计划关系的结束时间为优惠的时间
     * @param endDate
     * @throws Exception
     */
    private void infoMebPayPlan(String endDate) throws Exception
    {
        if(StringUtils.isNotBlank(endDate)){
            IDataset tradePayDatas = bizData.getTradeUserPayplan();
            if(IDataUtil.isNotEmpty(tradePayDatas)){
                for(int i=0; i < tradePayDatas.size(); i++){
                    IData tradePayData = tradePayDatas.getData(i);
                    if(IDataUtil.isNotEmpty(tradePayData)){
                        tradePayData.put("END_DATE", endDate);
                    }
                }
            }
        }
    }
    
    /**
     * 修改产品关系的结束时间为优惠的时间
     * @param endDate
     * @throws Exception
     */
    private void infoMebProduct(String endDate) throws Exception
    {
        if(StringUtils.isNotBlank(endDate)){
            IDataset tradeProDatas = bizData.getTradeProduct();
            if(IDataUtil.isNotEmpty(tradeProDatas)){
                for(int i=0; i < tradeProDatas.size(); i++){
                    IData tradeProData = tradeProDatas.getData(i);
                    if(IDataUtil.isNotEmpty(tradeProData)){
                        tradeProData.put("END_DATE", endDate);
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @throws Exception
     */
    private void infoRegDataSms() throws Exception{
        IDataset smsDataSet = new DatasetList();
        IData smsData = new DataMap();
        smsData.put("TEMPLATE_REPLACED", sendNoticeContent);//短信内容
        smsDataSet.add(smsData);
        super.dealSucSms(smsDataSet);
    }
    
    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
    }
    
    /**
     * 生成记录
     * @throws Exception
     */
    private void insertRecordLog() throws Exception
    {
    	String userId = reqData.getGrpUca().getUserId();//集团产品USERID
    	
    	if(StringUtils.isNotBlank(userId))
    	{
    		//计算本次订购套餐的价格
    		double sumFee = calculateUserDiscntSum(userId);
    		sumFee = sumFee * 100; //转换成分为单位
    		
    		String acceptTime = this.getAcceptTime();
    		
    		IData paramData = new DataMap();
    		paramData.put("USER_ID", userId);
    		paramData.put("ACCEPT_DATE", acceptTime);
    		
    		String partitionId = StrUtil.getPartition4ById(userId);
    		String custId = reqData.getGrpUca().getCustGroup().getCustId();
    		
    		IData map = new DataMap();
    		    		
    		//加锁
    		CenpayGfffUserLogQry.updateForGfffMebLock(map);
    		
			//处理月记录
			IDataset monthSets = CenpayGfffUserLogQry.queryGfffMonthLogByUserId(paramData);
			if(IDataUtil.isEmpty(monthSets))
			{
				//新增当月的一条记录
    			IData monthData = new DataMap();
    			monthData.put("PARTITION_ID",  partitionId);
    			monthData.put("USER_ID",  userId);
    			monthData.put("INST_ID",  SeqMgr.getInstId());
    			monthData.put("CUST_ID",  custId);
    			monthData.put("SYNC_DAY",  "0");
    			monthData.put("START_DATE",  SysDateMgr.getFirstDayOfThisMonth());
    			monthData.put("END_DATE",  SysDateMgr.getLastDateThisMonth());
    			monthData.put("INSERT_TIME",  acceptTime);
    			monthData.put("RECORD_NUM",  "1");
    			monthData.put("CONSUME_FEE",  sumFee);
    			Dao.insert("TF_F_USER_GFFF_LOG", monthData, Route.getCrmDefaultDb());
    			
			}
			else 
			{
				IData monthSet = monthSets.getData(0);
				String recordNum = monthSet.getString("RECORD_NUM","0");
				String consumeFee = monthSet.getString("CONSUME_FEE","0");
				
				double doubleRecordNum = Double.parseDouble(recordNum) + 1;//数量
				//原来的总额加上本次的总额
				BigDecimal bigDec = new BigDecimal(consumeFee);
				double doubleConsumeFee = Double.parseDouble(bigDec.toString()) + sumFee;//总额
				
				IData paramIn = new DataMap();
				paramIn.put("USER_ID", userId);
				paramIn.put("ACCEPT_DATE", acceptTime);
				paramIn.put("RECORD_NUM", doubleRecordNum);
				paramIn.put("CONSUME_FEE", doubleConsumeFee);
				
				//修改记录
				CenpayGfffUserLogQry.updateGfffMonthLogByUserId(paramIn);
				
			}
			
			//处理每天的记录
			IDataset daySets = CenpayGfffUserLogQry.queryGfffDayLogByUserId(paramData);
			if(IDataUtil.isEmpty(daySets))//天记录没有，则新增一条
			{
				//新增当天的一条记录
    			IData dayData = new DataMap();
    			dayData.put("PARTITION_ID",  partitionId);
    			dayData.put("USER_ID",  userId);
    			dayData.put("INST_ID",  SeqMgr.getInstId());
    			dayData.put("CUST_ID",  custId);
    			dayData.put("SYNC_DAY",  "1");
    			dayData.put("START_DATE",  CenpayTimeUtils.getTodayStartOfThisDay(acceptTime));
    			dayData.put("END_DATE",  CenpayTimeUtils.getTodayEndOfThisDay(acceptTime));
    			dayData.put("INSERT_TIME",  acceptTime);
    			dayData.put("RECORD_NUM",  "1");
    			dayData.put("CONSUME_FEE",  sumFee);
    			Dao.insert("TF_F_USER_GFFF_LOG", dayData, Route.getCrmDefaultDb());
			}
			else 
			{
				IData daySet = daySets.getData(0);
				String recordNum = daySet.getString("RECORD_NUM","0");
				String consumeFee = daySet.getString("CONSUME_FEE","0");
				
				double doubleRecordNum = Double.parseDouble(recordNum) + 1;//数量
				//原来的总额加上本次的总额
				BigDecimal bigDec = new BigDecimal(consumeFee);
				double doubleConsumeFee = Double.parseDouble(bigDec.toString()) + sumFee;//总额
				
				IData paramIn = new DataMap();
				paramIn.put("USER_ID", userId);
				paramIn.put("ACCEPT_DATE", acceptTime);
				paramIn.put("RECORD_NUM", doubleRecordNum);
				paramIn.put("CONSUME_FEE", doubleConsumeFee);
				
				//修改记录
				CenpayGfffUserLogQry.updateGfffDayLogByUserId(paramIn);
				
			}
			
			//处理每小时的记录
			IDataset hourSets = CenpayGfffUserLogQry.queryGfffHourLogByUserId(paramData);
			if(IDataUtil.isEmpty(hourSets))//月记录没有，则新增一条
			{
				//新增每小时的一条记录
    			IData hourData = new DataMap();
    			hourData.put("PARTITION_ID",  partitionId);
    			hourData.put("USER_ID",  userId);
    			hourData.put("INST_ID",  SeqMgr.getInstId());
    			hourData.put("CUST_ID",  custId);
    			hourData.put("SYNC_DAY",  "2");
    			hourData.put("START_DATE",  CenpayTimeUtils.getStartHourOfEachHour(acceptTime));
    			hourData.put("END_DATE",  CenpayTimeUtils.getEndHourOfEachHour(acceptTime));
    			hourData.put("INSERT_TIME",  acceptTime);
    			hourData.put("RECORD_NUM",  "1");
    			hourData.put("CONSUME_FEE",  sumFee);
    			Dao.insert("TF_F_USER_GFFF_LOG", hourData, Route.getCrmDefaultDb());
    			
			}
			else 
			{
				IData hourSet = hourSets.getData(0);
				String recordNum = hourSet.getString("RECORD_NUM","0");
				String consumeFee = hourSet.getString("CONSUME_FEE","0");
				
				double doubleRecordNum = Double.parseDouble(recordNum) + 1;//数量
				//原来的总额加上本次的总额
				BigDecimal bigDec = new BigDecimal(consumeFee);
				double doubleConsumeFee = Double.parseDouble(bigDec.toString()) + sumFee;//总额
				
				IData paramIn = new DataMap();
				paramIn.put("USER_ID", userId);
				paramIn.put("ACCEPT_DATE", acceptTime);
				paramIn.put("RECORD_NUM", doubleRecordNum);
				paramIn.put("CONSUME_FEE", doubleConsumeFee);
				
				//修改记录
				CenpayGfffUserLogQry.updateGfffHourLogByUserId(paramIn);
				
			}
			
    	}
    	
    }
    
    /**
     * 根据折扣率,订购的优惠套餐,计算本次套餐订购的总价格
     * @param grpUserId
     * @return
     * @throws Exception
     */
    private double calculateUserDiscntSum(String grpUserId)
    	throws Exception
    {
    	double sumPrice = 0;//本次订购套餐的总价格
    	double discountRate = 1;//折扣率
    	//集团产品USERID,获取集团产品的折扣率
    	IData attrData = CenpayGfffUserLogQry.queryGfffUserAttrByUserId(grpUserId);
    	if(IDataUtil.isNotEmpty(attrData))
    	{
    		discountRate = Double.parseDouble(attrData.getString("ATTR_VALUE","1")) / 100;
    	}
    	
    	//获取定额套餐下的
        IDataset discntDatas = reqData.cd.getDiscnt();
        if(IDataUtil.isNotEmpty(discntDatas))
        {
        	IDataset packageInfos = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430002");
            for(int i=0; i < discntDatas.size(); i++)
            {
                IData discntData = discntDatas.getData(i);
                String eleTypeCode =  discntData.getString("ELEMENT_TYPE_CODE","");
                String modifyTag =  discntData.getString("MODIFY_TAG","");
                String elementId =  discntData.getString("ELEMENT_ID","");
                if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos, "ELEMENT_ID="+elementId)) && "D".equals(eleTypeCode) 
                        && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                	
                    //获取套餐对应的价格并计算
                    IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "7347", 
                    		"GRP_GFFF_DINGE",elementId, "0898");
                    if(IDataUtil.isNotEmpty(commInfos))
                    {
                    	IData commData = commInfos.getData(0);
                    	//套餐的价格
                    	String discntPrice = commData.getString("PARA_CODE4","0");
                    	sumPrice = sumPrice + Double.parseDouble(discntPrice) * discountRate;
                    }
                    
                }
            }
        }
        
    	return sumPrice;
    }

}
