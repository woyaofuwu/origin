
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import java.math.BigDecimal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.group.cenpayspecial.CenpayGfffUserLogQry;
import com.asiainfo.veris.crm.order.soa.group.cenpayspecial.CenpayTimeUtils;

public class ChangeGfffGroupDingEMemElement extends ChangeMemElement
{

    public ChangeGfffGroupDingEMemElement()
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
       
        //infoRegDataAttr();
        
        infoUserDiscnt();
    }

    /**
     * 
     * @throws Exception
     */
    private void infoRegDataAttr() throws Exception
    {
        boolean firstElement = false;
        boolean needModify = false;
        String firstDiscntCode = "";
//        String endDate = "";
//        
//        IDataset discntDatas = reqData.cd.getDiscnt();
//        if(IDataUtil.isNotEmpty(discntDatas)){
//            for(int i=0; i < discntDatas.size(); i++){
//                IData discntData = discntDatas.getData(i);
//                if(IDataUtil.isNotEmpty(discntData)){
//                    String packageId = discntData.getString("PACKAGE_ID","");
//                    String eleTypeCode =  discntData.getString("ELEMENT_TYPE_CODE","");
//                    String modifyTag =  discntData.getString("MODIFY_TAG","");
//                    if("73430002".equals(packageId) && "D".equals(eleTypeCode) 
//                            && modifyTag.equals(TRADE_MODIFY_TAG.MODI.getValue())){
//                        endDate = discntData.getString("END_DATE","");
//                        break;
//                    }
//                }
//            }
//        }
        
        //对只变更优惠折扣的，原来的折扣截止到上月底、新的从现在时间开始算起到2050
        IDataset paramDatas = reqData.cd.getElementParam();
        if(IDataUtil.isNotEmpty(paramDatas)){
            for(int i=0; i < paramDatas.size(); i++){
                IData paramData = paramDatas.getData(i);
                if(IDataUtil.isNotEmpty(paramData)){
                   String discntCode = paramData.getString("ELEMENT_ID","");
                   String attrCode =  paramData.getString("ATTR_CODE","");
                   //String attrValue =  paramData.getString("ATTR_VALUE","");
                   //String attrModifyTag = paramData.getString("MODIFY_TAG","");
                   
                   if(StringUtils.isNotEmpty(attrCode) && "7343".equals(attrCode) && !firstElement){
                       firstElement = true;
                       firstDiscntCode = discntCode;
                       continue;
                   }
                   
                   //需要做修改一下时间
                   if(StringUtils.isNotEmpty(attrCode) && "7343".equals(attrCode) && firstElement){
                       if(firstDiscntCode.equals(discntCode)){
                           needModify = true;
                       }
                   }
                }
            }
            
            for(int i=0; i < paramDatas.size(); i++){
                IData paramData = paramDatas.getData(i);
                if(IDataUtil.isNotEmpty(paramData)){
                   //String discntCode = paramData.getString("ELEMENT_ID","");
                   String attrCode =  paramData.getString("ATTR_CODE","");
                   //String attrValue =  paramData.getString("ATTR_VALUE","");
                   String attrModifyTag = paramData.getString("MODIFY_TAG","");
                   
                   if(needModify){
                       if(StringUtils.isNotEmpty(attrCode) && "7343".equals(attrCode) &&
                               attrModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue())){
                           
                           //paramData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(-1));
                           paramData.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                           
                       } else if(StringUtils.isNotEmpty(attrCode) && "7343".equals(attrCode) &&
                               attrModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                           
                           paramData.put("START_DATE", getAcceptTime());
                       }
                   }                   
                }
            }
        }
    }
    
    
    /**
     * 获取新增优惠的结束时间、
     * @param endDate
     * @throws Exception
     */
    private void infoUserDiscnt() throws Exception
    {        
        boolean addNeed = false;
        boolean delNeed = false;
        
        String delEndDate = "";
        String addEndDate = "";
        IDataset discntDatas = reqData.cd.getDiscnt();
        
        if(IDataUtil.isNotEmpty(discntDatas)){
        	IDataset packageInfos2 = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430002");
        	IDataset packageInfos3 = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430003");
            for(int i=0; i < discntDatas.size(); i++){
                IData discntData = discntDatas.getData(i);
                if(IDataUtil.isNotEmpty(discntData)){
                    
                    //String packageId = discntData.getString("PACKAGE_ID","");
                    String eleTypeCode =  discntData.getString("ELEMENT_TYPE_CODE","");
                    String modifyTag =  discntData.getString("MODIFY_TAG","");
                    String elementId =  discntData.getString("ELEMENT_ID","");
                    
                    if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos2, "ELEMENT_ID="+elementId))&& "D".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                    	//集团自由充(定额统付)成员产品优惠包优惠不允许做变更，因为新增时要求改为办理当月底失效
                    	CSAppException.apperr(CrmCommException.CRM_COMM_103, "集团自由充(定额统付)成员产品优惠包优惠不允许做变更,如有疑问,请联系业务管理员");
                    	
                        IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "7347", "GRP_GFFF_DINGE",
                                   elementId, "0898");
                        if(IDataUtil.isEmpty(commInfos)){
                            //获取定额统付流量叠加包转换异常
                            CSAppException.apperr(GrpException.CRM_GRP_852);
                        }
                        IData commData = commInfos.getData(0);
                        if(IDataUtil.isNotEmpty(commData)){
                            discntData.put("PARA_CODE1", commData.getString("PARA_CODE1",""));
                            discntData.put("PARA_CODE2", commData.getString("PARA_CODE2",""));
                            discntData.put("PARA_CODE3", commData.getString("PARA_CODE3",""));
                            discntData.put("PARAM_NAME", commData.getString("PARAM_NAME",""));
                        }
                        
                        addEndDate = discntData.getString("END_DATE","");
                        infoMebCenPay(discntData,"0","1");
                        
                        addNeed = true;
                        continue;
                        
                    } else if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos2, "ELEMENT_ID="+elementId)) && "D".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue())){
                        
                        delEndDate = discntData.getString("END_DATE","");
                        delNeed = true;
                        continue;
                        
                    } else if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos3, "ELEMENT_ID="+elementId)) && "D".equals(eleTypeCode) 
                            && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue())){
                        
                     IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "7347", "GRP_GFFF_DINGE_LL",
                                elementId, "0898");
                     if(IDataUtil.isEmpty(commInfos)){
                         //获取定额统付流量叠加包转换异常
                         CSAppException.apperr(GrpException.CRM_GRP_853);
                     }
                     IData commData = commInfos.getData(0);
                     if(IDataUtil.isNotEmpty(commData)){
                         discntData.put("PARA_CODE1", commData.getString("PARA_CODE1",""));
                         discntData.put("PARA_CODE2", commData.getString("PARA_CODE2",""));
                         discntData.put("PARA_CODE3", commData.getString("PARA_CODE3",""));
                         discntData.put("PARAM_NAME", commData.getString("PARAM_NAME",""));
                     }
                     
                     infoMebCenPay(discntData,"0","1");
                     
                    }
                }
            }
        }
        
        //如果有做优惠变更的时候，处理UU表和meb_cenpay
        if(addNeed && delNeed && StringUtils.isNotBlank(addEndDate) && StringUtils.isNotBlank(delEndDate)){
            
            String userId = reqData.getUca().getUserId();
            IDataset uuInfos = RelaUUInfoQry.qryRelaByUserIdbAndRelaTypeCode(userId,"T6");
            if(IDataUtil.isEmpty(uuInfos)){
                //获取不到定额统付成员关系
                CSAppException.apperr(GrpException.CRM_GRP_862);
            }
            if(IDataUtil.isNotEmpty(uuInfos)){
                IData userInfo = uuInfos.getData(0);
                userInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                userInfo.put("END_DATE", addEndDate);
                userInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                
                this.addTradeRelation(userInfo);
                
            }
            
            IData mebcenpayParam = new DataMap();
            mebcenpayParam.put("USER_ID", userId);
            mebcenpayParam.put("MP_GROUP_CUST_CODE", reqData.getGrpUca().getUserId());
            mebcenpayParam.put("PRODUCT_OFFER_ID", "7343");
            mebcenpayParam.put("OPER_TYPE", "4");
            
            IDataset mebCenPayInfo = UserGrpInfoQry.queryMebCenPayInfoByUserIdAll(mebcenpayParam);
            if (IDataUtil.isNotEmpty(mebCenPayInfo)){
                for(int j=0; j < mebCenPayInfo.size(); j++){
                    IData mebCenpay = mebCenPayInfo.getData(j);
                    mebCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    mebCenpay.put("END_DATE", delEndDate);
                    mebCenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
                    
                    this.addTradeMebCenpay(mebCenpay);
                }
                
            }
            
        }
        
        //只做删除优惠时，处理UU表和meb_cenpay
        /*
        if(!addNeed && delNeed && StringUtils.isNotBlank(delEndDate)){
            
            String userId = reqData.getUca().getUserId();
            IDataset uuInfos = RelaUUInfoQry.qryRelaByUserIdbAndRelaTypeCode(userId,"T6");
            if(IDataUtil.isEmpty(uuInfos)){
                //获取不到定额统付成员关系
                CSAppException.apperr(GrpException.CRM_GRP_862);
            }
            if(IDataUtil.isNotEmpty(uuInfos)){
                IData userInfo = uuInfos.getData(0);
                userInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                userInfo.put("END_DATE", delEndDate);
                userInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                
                this.addTradeRelation(userInfo);
                
            }
            
            IDataset mebCenPayInfo = UserGrpInfoQry.queryMebCenPayInfo(userId,"0");
            if (IDataUtil.isNotEmpty(mebCenPayInfo)){
                IData mebCenpay = mebCenPayInfo.getData(0);
                mebCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                mebCenpay.put("END_DATE", delEndDate);
                mebCenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
                
                this.addTradeMebCenpay(mebCenpay);
            }
            
        }*/
        
        //只做新增优惠时，处理UU表和meb_cenpay
        /*
        if(addNeed && !delNeed && StringUtils.isNotBlank(addEndDate)){
            
            String userId = reqData.getUca().getUserId();
            IDataset uuInfos = RelaUUInfoQry.qryRelaByUserIdbAndRelaTypeCode(userId,"T6");
            if(IDataUtil.isEmpty(uuInfos)){
                //获取不到定额统付成员关系
                CSAppException.apperr(GrpException.CRM_GRP_862);
            }
            if(IDataUtil.isNotEmpty(uuInfos)){
                IData userInfo = uuInfos.getData(0);
                userInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                userInfo.put("END_DATE", addEndDate);
                userInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                
                this.addTradeRelation(userInfo);
                
            }
            
            IDataset mebCenPayInfo = UserGrpInfoQry.queryMebCenPayInfo(userId,"0");
            if (IDataUtil.isNotEmpty(mebCenPayInfo)){
                IData mebCenpay = mebCenPayInfo.getData(0);
                mebCenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                mebCenpay.put("END_DATE", addEndDate);
                mebCenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
                
                this.addTradeMebCenpay(mebCenpay);
            }
            
        }*/
        
    }
    
    private void infoMebCenPay(IData data,String flag,String dieFlag) throws Exception
    {
        IData mebcenpay = new DataMap();
        
        mebcenpay.put("USER_ID", reqData.getUca().getUserId());
        mebcenpay.put("INST_ID", SeqMgr.getInstId());
        //mebcenpay.put("MP_GROUP_CUST_CODE", "0");
        if(StringUtils.isNotEmpty(dieFlag) && "1".equals(dieFlag)){
            mebcenpay.put("MP_GROUP_CUST_CODE", reqData.getGrpUca().getUserId());
        } else {
            mebcenpay.put("MP_GROUP_CUST_CODE", "0");
        }
        
        
        mebcenpay.put("PAY_TYPE", "1");
        mebcenpay.put("OPER_TYPE", "4");//4:指定用户、定额统付
        //mebcenpay.put("PRODUCT_OFFER_ID", "0");
        if(StringUtils.isNotEmpty(dieFlag) && "1".equals(dieFlag)){
            mebcenpay.put("PRODUCT_OFFER_ID", reqData.getGrpUca().getProductId());
        } else {
            mebcenpay.put("PRODUCT_OFFER_ID", "0");
        }
        mebcenpay.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
        
        //mebcenpay.put("LIMIT_FEE", "");
        
        if(StringUtils.isNotEmpty(flag) && "0".equals(flag)){//新增的优惠
            String startDate = data.getString("START_DATE","");
            String endDate = data.getString("END_DATE","");
            
            mebcenpay.put("START_DATE", startDate);
            mebcenpay.put("END_DATE", endDate);
            mebcenpay.put("ELEMENT_ID", data.getString("PARA_CODE2",""));//个人流量包ID
            
            mebcenpay.put("RSRV_STR1", data.getString("PARA_CODE1",""));
            mebcenpay.put("RSRV_STR2", data.getString("PARAM_NAME",""));
        }
        
        mebcenpay.put("UPDATE_TIME", SysDateMgr.getSysTime());
        //mebcenpay.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        mebcenpay.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        
        this.addTradeMebCenpay(mebcenpay);
       
    }
    
    /**
     * 生成其它台帐数据（生成台帐后）
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        
        insertRecordLog();

    }
    
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

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
        	IDataset packageInfos2 = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430002");
        	IDataset packageInfos3 = UPackageElementInfoQry.getPackageElementInfoByPackageId("73430003");
            for(int i=0; i < discntDatas.size(); i++)
            {
                IData discntData = discntDatas.getData(i);
                String eleTypeCode =  discntData.getString("ELEMENT_TYPE_CODE","");
                String modifyTag =  discntData.getString("MODIFY_TAG","");
                String elementId =  discntData.getString("ELEMENT_ID","");
                if(IDataUtil.isNotEmpty(DataHelper.filter(packageInfos2, "ELEMENT_ID="+elementId)) && "D".equals(eleTypeCode) 
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
                else if (IDataUtil.isNotEmpty(DataHelper.filter(packageInfos3, "ELEMENT_ID="+elementId)) && "D".equals(eleTypeCode) 
                        && modifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                	//获取套餐对应的价格并计算
                    IDataset commInfos = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "7347", 
                    		"GRP_GFFF_DINGE_LL",elementId, "0898");
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
