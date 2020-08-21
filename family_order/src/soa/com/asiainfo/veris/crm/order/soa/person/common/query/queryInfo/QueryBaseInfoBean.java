package com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.util.TimeUtil;
import com.ailk.bizcommon.util.FeeUtils;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.basequeryrecord.BaseQueryBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.IBossCovertor;

public class QueryBaseInfoBean extends BaseQueryBean{
    
    static transient final Logger logger = Logger.getLogger(QueryBaseInfoBean.class);
	public IData queryBaseInfo(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER", "");
    	String provCode = input.getString("PROV_CODE", ""); 
    	String oprTime = input.getString("OPR_TIME", "");
    	
    	IData result = new DataMap();
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    	
    	result.put("OPR_NUM", input.getString("OPR_NUM",""));
    	result.put("SERIAL_NUMBER", serialNumber);
    	result.put("PROV_CODE", provCode);
    	result.put("OPR_TIME", oprTime);
    	IDataset resInfos = UserResInfoQry.queryUserResByUserIdResType(uca.getUserId(), "1");
    	if(IDataUtil.isNotEmpty(resInfos)){
             IData resInfo = resInfos.getData(0);
             if(StringUtils.isBlank(resInfo.getString("IMSI"))){
                 CSAppException.apperr(CrmUserException.CRM_USER_891);
             }
             result.put("IMSI", resInfo.getString("IMSI")); // 国际移动用户身份识别号
             result.put("EFFTIMSI", sdf.format(TimeUtil.encodeTimestamp(resInfo.getString("START_DATE"))));//IMSI号生效时间
         }else{
             CSAppException.apperr(CrmUserException.CRM_USER_892);
         }
    	

    	//用户品牌信息
    	String brand = uca.getBrandCode();
    	if("G001".equals(brand)){
    		result.put("BRAND", "01");//01:全球通
    	}else if("GS01".equals(brand)){
    		result.put("BRAND", "02");//02:神州行
    	}else if("G010".equals(brand)){
    		result.put("BRAND", "03");//03:动感地带
    	}else{
    		result.put("BRAND", "09");//09:其他品牌
    	}
    	
    	UserTradeData tradeData = uca.getUser();
    	//用户付费类型
    	String userType = tradeData.getPrepayTag();
    	userType = "1";//海南全部为预付费需要判断余额modify by songxw 2017-04-10
    	if("0".equals(userType)){
    		result.put("USER_TYPE", "01");//后付费
    		result.put("IS_BALANCE_JUD", "00");//不需要判断余额
    	}else{
    		result.put("USER_TYPE", "00");//预付费
    		result.put("IS_BALANCE_JUD", "01");//需要判断余额
    		//用户余额
//    		IDataset oweInfo = AcctCall.queryUserEveryBalanceFee(serialNumber , "SN");
    		
    		
    	} 
        //实时结余加上信用度,海南用户需求
        double nCreditValue = 0;
        IDataset ids = AcctCall.getUserCreditInfos("0", uca.getUserId());
        if (IDataUtil.isNotEmpty(ids)) {
            IData idCreditInfo = ids.getData(0);
            nCreditValue = idCreditInfo.getInt("CREDIT_VALUE");
        }
        
        //String balance = FeeUtils.Fen2Yuan(uca.getAcctBlance()+nCreditValue);// 可用余额
        double balanceInt = Double.parseDouble(uca.getAcctBlance());//Integer.parseInt
      //  double nFee = (balanceInt + nCreditValue)/100;
        //String balance = String.valueOf(nFee);
        
        //获取实际费用
        double depositB = 0;
        IDataset db = AcctCall.QryDoRomanAccountDeposit(uca.getSerialNumber());
        if (IDataUtil.isNotEmpty(db)) {
        	IData dBalance = db.getData(0);
        	depositB = dBalance.getInt("DEPOSIT_BALANCE");
        }
        result.put("USER_BALANCE", FeeUtils.Fen2Yuan(String.valueOf(depositB)));//帐户余; 单位:元,保留小数点后两位
        
    	
    	//用户星级
    	IData creditInfo = CreditCall.queryUserCreditInfos(uca.getUserId());
        //STAR_LEVEL
        int creditClass = creditInfo.getInt("CREDIT_CLASS",-1);
    	result.put("LEVEL", tranStarLevel(creditClass));
    	
//    	//用户状态
//    	IDataset paramset = CommparaInfoQry.getCommparaByCode1("CSM", "8822", "USRESTATUS", tradeData.getUserStateCodeset(), null);
//    	if(IDataUtil.isEmpty(paramset)){
//    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "99-号码不存在");
//    	} 
    	String userStatus = IBossCovertor.getIBossUserState(tradeData.getUserStateCodeset());
    	if("".equals(userStatus)){
          CSAppException.apperr(CrmCommException.CRM_COMM_103, "99-号码不存在");
    	} 
    	result.put("USERSTAT", userStatus);//用户状态
    	
    	//判断用户国漫服务开通状态
    	//InterRoamDayTradeBean bean = new InterRoamDayTradeBean();
        //IData userparams = new DataMap();
        //userparams.put("USER_ID", uca.getUserId());
        //userparams.put("SERVICE_ID", "19");
        //IDataset userSvc = bean.getUserSvc(userparams);
        IDataset userSvc = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(uca.getUserId(), "19");
        if(userSvc.isEmpty()){
            result.put("INTERSTAT", "2");//2:未开通
        }else{
        	result.put("INTERSTAT", "1");//1:已开通
        	//result.put("SERVTYPE", "00");//00 – 全部;01 - 语音，短信业务;02 - 数据业务，
        	
        	buildServType(result,userSvc);
        	result.put("VALID_TIME", sdf.format(TimeUtil.encodeTimestamp(userSvc.getData(0).getString("START_DATE"))));//国漫开通生效时间
        	String endTime = userSvc.getData(0).getString("END_DATE");
        	String expirTime = sdf.format(TimeUtil.encodeTimestamp(endTime));
        	result.put("EXPIR_TIME", expirTime);//国漫开通失效期
        }
        
        //查询国际长途开通状态
        IDataset datasetsvc = UserSvcInfoQry.queryUserSvcByUserId(uca.getUserId(),"15",null); 
        
        result.put("INTERCALL_STATUS", "2");
        if (IDataUtil.isNotEmpty(datasetsvc))
        { 
            result.put("INTERCALL_STATUS", "1");
            result.put("INTERCALL_SERV_TYPE", "00");
            result.put("INTERCALL_VALID_DATE", sdf.format(TimeUtil.encodeTimestamp(datasetsvc.getData(0).getString("START_DATE"))));
            result.put("INTERCALL_EXPIRE_DATE", sdf.format(TimeUtil.encodeTimestamp(datasetsvc.getData(0).getString("END_DATE"))));
            
        } 
        
    	
    	//用户是否为国漫业务的黑名单 00:是; 01:不是
    	result.put("IS_BLACK_LSIT", "01");
    	
        return result;
    }
	/**
     * 转换客户星级
     */
    public String tranStarLevel(int creditClass){
        String starLevel = "13";
        if(creditClass == -1){
            starLevel = "13";
        }
        if(creditClass == 0){
            starLevel = "12";
        }
        if(creditClass == 1){
            starLevel = "11";
        }
        if(creditClass == 2){
            starLevel = "10";
        }
        if(creditClass == 3){
            starLevel = "09";
        }
        if(creditClass == 4){
            starLevel = "08";
        }
        if(creditClass == 5){
            starLevel = "07";
        }
        if(creditClass == 6){
            starLevel = "06";
        }
        if(creditClass == 7){
            starLevel = "05";
        }
        
        return starLevel;
    }
    private void buildServType(IData result,IDataset userSvc) throws Exception
    {
        String endTime = userSvc.getData(0).getString("END_DATE");
        String startTime = userSvc.getData(0).getString("UPDATE_TIME"); 
        
        if (StringUtils.isBlank(startTime))
        {
            startTime = userSvc.getData(0).getString("START_DATE"); 
        }
        
        if (30 == SysDateMgr.dayInterval(startTime,endTime))
        {
            result.put("SERVTYPE", "10");//00 – 全部;01 - 语音，短信业务;02 - 数据业务 ,10:30天 ，11:180天，
        }
        else  if (180 == SysDateMgr.dayInterval(startTime,endTime))
        {
            result.put("SERVTYPE", "11"); 
        }
        else
        {
            result.put("SERVTYPE", "00"); 
        }
    }


    private final String SMS_INTERROAM_END_TMPALET = "尊敬的客户，您的国际/港澳台漫游功能即将于%s年%s月%s日到期。如需继续使用请回复GMKT30/GMKT180/GMKTCQ到10086，再次开通国际/港澳台漫游功能。温馨提示：您的国际／港澳台漫游功能到期后，已订购国漫产品将无法使用，请您留意。中国移动";
    /**
     * 国际漫游到期提醒
     * @param input
     * @return
     * @throws Exception
     */
    public IData sendInterRoamMessage(IData input) throws Exception
    {

        SQLParser selectSQL = new SQLParser(input);
        selectSQL.addSQL(" SELECT ROW_.*,ROWID, ROWNUM ROWNUM_ FROM ");
                 selectSQL.addSQL(" ( select SERIAL_NUMBER,A.EPARCHY_CODE,B.END_DATE from TF_F_USER A,TF_F_USER_SVC B where A.REMOVE_TAG='0' AND service_id = '19'  and to_char(end_date-15,'yyyymmddhh')  =  to_char(sysdate,'yyyymmddhh') AND A.USER_ID = B.USER_ID and A.PARTITION_ID = B.PARTITION_ID) ROW_  ");
                 selectSQL.addSQL(" WHERE ROWNUM <= 10000 ");

        
        IDataset list = Dao.qryByParseAllCrm(selectSQL,Boolean.TRUE); 

        if (IDataUtil.isNotEmpty(list))
        {
            for (Iterator iterator = list.iterator(); iterator.hasNext();)
            {
                IData item = (IData) iterator.next(); 
                
                
                try
                {      
                    String endDate = item.getString("END_DATE");
                    
                    Date date = SysDateMgr.string2Date(endDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);

                    Calendar c = Calendar.getInstance();
                    c.setTime(date); 
                    
                    Object args[] = {c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH)};
                    String msg = String.format(this.SMS_INTERROAM_END_TMPALET,args);
              
                    IData inparam = new DataMap();
                    inparam.put("NOTICE_CONTENT", msg);
                    inparam.put("RECV_OBJECT", item.getString("SERIAL_NUMBER"));
                    inparam.put("RECV_ID", item.getString("SERIAL_NUMBER"));
                    inparam.put("REFER_STAFF_ID", getVisit().getStaffId());
                    inparam.put("REFER_DEPART_ID", getVisit().getDepartId());
                    inparam.put("REMARK", "国际漫游到期短信提醒");
                    
                    SmsSend.insSms(inparam, item.getString("EPARCHY_CODE")); 
                   
                } 
                catch (Exception e)
                {   
                   e.printStackTrace();
                   logger.error("",e);
                }
 
            }
        }
        IData returnData = new DataMap();

        String rspCode = "0";
        String rspDesc = "调用成功！";
        returnData.put("RSP_CODE", rspCode);
        returnData.put("RSP_DESC", rspDesc);
        return returnData;
    } 
}
