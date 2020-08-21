package com.asiainfo.veris.crm.order.soa.person.busi.goodsapply; 

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.cpe.CPEActiveBean;

/**
 * REQ201603010009 申请新增积分兑换业务
 * CHENXY3
 * 2016-3-08
 * */
public class GoodsApplyBean extends CSBizBean
{
	private static final Logger log = Logger.getLogger(GoodsApplyBean.class);
	/**
	 * 查询用户礼品信息
	 * */
	public static IDataset getUserGoodsInfos(IData inParam,Pagination pagen) throws Exception
    { 
        IDataset userGoods = Dao.qryByCode("TL_B_USER_SCORE_GOODS", "SEL_SCORE_GOODS_BY_USERID", inParam,pagen);
        return userGoods;
    }
	
	 /**
     * 领取礼品，更新TL_B_USER_SCORE_GOODS表信息
     * */
    public static void exchangeGoods(IData inparam) throws Exception
    {   
    	inparam.put("DEPART_NAME", getVisit().getDepartName());//网点
    	inparam.put("DEPART_AREA_CODE", getVisit().getCityCode());//领取分公司
    	IData userInfo = UcaInfoQry.qryUserInfoByUserId(inparam.getString("USER_ID"));
    	inparam.put("USER_CITY_CODE", userInfo.getString("CITY_CODE",""));//开卡归属
    	Dao.executeUpdateByCodeCode("TL_B_USER_SCORE_GOODS", "UPD_USER_SCORE_GOODS_STATE", inparam); 
    }
    
    /**
     * REQ201603010009 申请新增积分兑换业务
	 *  chenxy3
     * */
    public static void insertGoodsTab(IData param) throws Exception{
    	IData insData=new DataMap();
    	insData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth()); 
    	insData.put("USER_ID",param.getString("USER_ID",""));
    	insData.put("RULE_ID",param.getString("RULE_ID",""));
    	insData.put("RULE_NAME",param.getString("RULE_NAME",""));
    	insData.put("GOODS_NUM",param.getString("GOODS_NUM","1"));
    	insData.put("REMAIN_NUM",param.getString("GOODS_NUM","1"));
    	insData.put("GET_NUM",param.getString("GET_NUM","0"));
    	insData.put("SCORE",param.getString("SCORE",""));
    	insData.put("QUANCODE",param.getString("QUANCODE",""));
    	insData.put("GET_QUANCODE",param.getString("GET_QUANCODE",""));
    	insData.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER",""));
    	insData.put("STATE","0");//默认为0未领取
    	insData.put("UPDATE_STAFF_ID",param.getString("UPDATE_STAFF_ID",""));
    	insData.put("UPDATE_DEPART_ID",param.getString("UPDATE_DEPART_ID",""));
    	insData.put("EXCHANGE_STAFF_ID",param.getString("EXCHANGE_STAFF_ID",""));
    	insData.put("EXCHANGE_DEPART_ID",param.getString("EXCHANGE_DEPART_ID",""));
    	insData.put("TRADE_ID",param.getString("TRADE_ID",""));
    	insData.put("REMARK",param.getString("REMARK",""));
    	insData.put("RSRV_NUM1",param.getString("RSRV_NUM1",null));
    	insData.put("RSRV_NUM2",param.getString("RSRV_NUM2",null));
    	insData.put("RSRV_NUM3",param.getString("RSRV_NUM3",null));
    	insData.put("RSRV_NUM4",param.getString("RSRV_NUM4",null));
    	insData.put("RSRV_NUM5",param.getString("RSRV_NUM5",null));
    	insData.put("RSRV_STR1",param.getString("RSRV_STR1",""));
    	insData.put("RSRV_STR2",param.getString("RSRV_STR2",""));
    	insData.put("RSRV_STR3",param.getString("RSRV_STR3",""));
    	insData.put("RSRV_STR4",param.getString("RSRV_STR4",""));
    	insData.put("RSRV_STR5",param.getString("RSRV_STR5",""));
    	insData.put("RSRV_DATE1",param.getString("RSRV_DATE1",null));
    	insData.put("RSRV_DATE2",param.getString("RSRV_DATE2",null));
    	insData.put("RSRV_DATE3",param.getString("RSRV_DATE3",null));
    	insData.put("RSRV_TAG1",param.getString("RSRV_TAG1",""));
    	insData.put("RSRV_TAG2",param.getString("RSRV_TAG2",""));
    	insData.put("RSRV_TAG3",param.getString("RSRV_TAG3","")); 
    	Dao.executeUpdateByCodeCode("TL_B_USER_SCORE_GOODS", "INS_USER_SCORE_GOODS", insData); 
    }
    
    /**
	 * REQ201603010009 申请新增积分兑换业务
	 *  chenxy3
	 * 新增一个通用的SMS发送方法
	 * */
    public static void smsSent(IData param) throws Exception{
    	
    	   String serialNumber=param.getString("SERIAL_NUMBER","");
    	   String userId=param.getString("USER_ID","");
    	   String content=param.getString("NOTICE_CONTENT","");
    	   String staffId=param.getString("STAFF_ID",getVisit().getStaffId());
    	   String departId=param.getString("DEPART_ID",getVisit().getDepartId());
    	   String remark=param.getString("REMARK","");
    	   String brandCode=param.getString("BRAND_CODE","");
    	   String forStartTime=param.getString("FORCE_START_TIME","");
    	   String forEndTime=param.getString("FORCE_END_TIME","");
		   IData smsData = new DataMap();

			// 插入短信表
           String seq = SeqMgr.getSmsSendId();
           long seq_id = Long.parseLong(seq);
           long partition_id = seq_id % 1000;
           
           smsData.put("SMS_NOTICE_ID",seq_id);
           smsData.put("PARTITION_ID",partition_id);
           smsData.put("EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
           smsData.put("BRAND_CODE",brandCode);
           smsData.put("IN_MODE_CODE",getVisit().getInModeCode());
           smsData.put("SMS_NET_TAG","0");
           smsData.put("CHAN_ID","GTM");
           smsData.put("SEND_OBJECT_CODE","1");
           smsData.put("SEND_TIME_CODE","1");
           smsData.put("SEND_COUNT_CODE","");
           smsData.put("RECV_OBJECT_TYPE","00");
           smsData.put("RECV_OBJECT",serialNumber);
           smsData.put("RECV_ID",userId);
           smsData.put("SMS_TYPE_CODE","20");
           smsData.put("SMS_KIND_CODE","08");
           smsData.put("NOTICE_CONTENT_TYPE","0");
           smsData.put("NOTICE_CONTENT",content);
           smsData.put("REFERED_COUNT","");
           smsData.put("FORCE_REFER_COUNT","1");
           smsData.put("FORCE_OBJECT","10086");
           smsData.put("FORCE_START_TIME",forStartTime);
           smsData.put("FORCE_END_TIME",forEndTime);
           smsData.put("SMS_PRIORITY","1001");
           smsData.put("REFER_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
           smsData.put("REFER_STAFF_ID",staffId);
           smsData.put("REFER_DEPART_ID",departId);
           smsData.put("DEAL_TIME",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
           smsData.put("DEAL_STAFFID",staffId);
           smsData.put("DEAL_DEPARTID",departId);
           smsData.put("DEAL_STATE","15");
           smsData.put("REMARK",remark);
           smsData.put("REVC1","");
           smsData.put("REVC2","");
           smsData.put("REVC3","");
           smsData.put("REVC4","");
           smsData.put("MONTH",SysDateMgr.getCurMonth());
           smsData.put("DAY",SysDateMgr.getCurDay());

           Dao.insert("TI_O_SMS", smsData);
    }
    
    
    /**
     * REQ201603310017 积分兑换实物礼品报表需求  -- 获取礼品领取信息
     * CHENXY3 20160418
     */
    public static IDataset queryGoodsList(IData inparams,Pagination pagen) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        
        parser.addSQL(" select /*+ use_nl(t3 t)*/ T.RSRV_STR3 USER_CITY_CODE,t.STATE Goods_STATE,T.RSRV_STR1 DEPART_NAME,T.RSRV_STR2 DEPART_AREA_CODE,t3.para_code4 SUPPLIER,t3.para_code2 SCORE_VALUE," +
        		"t3.para_code3 NO_TAX_PRICE,t3.para_code5 TAX_RATE," +
        		"to_char(t.UPDATE_TIME,'YYYY-MM-DD HH24:MI:SS') UT_TIME,to_char(t.EXCHANGE_TIME,'YYYY-MM-DD HH24:MI:SS') ET_TIME," +
        		"t.* ");
        parser.addSQL(" from TL_B_USER_SCORE_GOODS t,TD_S_COMMPARA T3  ");
        parser.addSQL(" where 1=1 "); 
        parser.addSQL(" AND T.RULE_ID=T3.PARAM_CODE AND T3.PARAM_ATTR='1122'  ");
        //parser.addSQL(" AND T.ACCEPT_MONTH=to_number(:ACCEPT_MON) ");
        
        parser.addSQL(" AND T.UPDATE_TIME>= to_date(:START_DATE, 'YYYY-MM-DD') ");
        parser.addSQL(" AND T.UPDATE_TIME<= to_date(:END_DATE, 'YYYY-MM-DD') ");
        parser.addSQL(" AND T.UPDATE_STAFF_ID>= :START_STAFF_ID ");
        parser.addSQL(" AND T.UPDATE_STAFF_ID<= :END_STAFF_ID ");
        
        parser.addSQL(" AND T.UPDATE_DEPART_ID=:DEPART_ID ");
        parser.addSQL(" AND T.RSRV_STR2=:COMPANY ");
        parser.addSQL(" AND T3.PARA_CODE4 LIKE '%'||:SUPPLIER||'%' ");
        parser.addSQL(" AND T.RULE_NAME LIKE '%'||:RULE_NAME||'%' ");
        parser.addSQL(" order by t.start_date desc ");
    	return Dao.qryByParse(parser,pagen); 
    }
    
    /**
	 * REQ201603310017 积分兑换实物礼品报表需求  -- 获取礼品领取信息
	 * 获取用户的 ：
	 * 开卡归属	—— TF_F_USER   CITY_CODE
	 * 通话归属	—— tf_f_user_city   CITY_CODE
	 * */
	public static IDataset getUserCityCode(IData inParam) throws Exception
    { 
        IDataset userInfos = Dao.qryByCode("TF_F_USER_CITY", "SEL_USER_CITY_CODE_BY_ID", inParam);
        return userInfos;
    }
	
	/**
     * REQ201604290035 关于优化15款积分兑换实物礼品业务的需求
     * chenxy3 20160518
     * 获取用户的礼品信息
     */
    public static IDataset queryUserRemainGoods(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        
        parser.addSQL(" select t.*,TO_CHAR(T.END_DATE,'YYYY-MM-DD') ENDDATE   ");
        parser.addSQL(" from tl_b_user_score_goods t  ");
        parser.addSQL(" where  sysdate < t.end_date ");
        parser.addSQL(" and t.state in ('0', '1')  ");
        parser.addSQL(" and t.serial_number = :SERIAL_NUMBER  ");
        parser.addSQL(" and t.rule_id=:RULE_ID  ");
        parser.addSQL(" and t.trade_id=:TRADE_ID ");
        
    	return Dao.qryByParse(parser);
    }
    
    /**
     * REQ201610190011 关于积分兑换礼品延长验证码有效期的需求
     * chenxy3 20161114
     */
    public static IDataset importDelayData(IData input) throws Exception
    { 
    	IDataset rtnSet=new DatasetList();
    	IDataset dataset=new DatasetList();
    	IDataset failds = new DatasetList();
    	String fileId = input.getString("cond_STICK_LIST");  
        String[] fileIds = fileId.split(",");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        String  msg="";
        
        for (String strfileId : fileIds)
        {
            IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/BatDelayEndDate.xml"));
            IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
            IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
            dataset.addAll(suc[0]);
            //log.info("("*********cxy***********"+suc[0]);
            rtnSet.addAll(err[0]);
            //log.info("("*********cxy***********"+err[0]);
        }
        String maxDelayMon="";
        String minDelayMon="";
        IDataset commInfos=CommparaInfoQry.getCommparaAllColByParser("CSM", "1126", "MAX_MON", "0898");
        if(IDataUtil.isNotEmpty(commInfos)){
        	maxDelayMon=commInfos.getData(0).getString("PARA_CODE1","");
        	minDelayMon=commInfos.getData(0).getString("PARA_CODE2","");
        }
        //对成功数据进行解析
        String serialNumList="";
        for (int i = 0; i < dataset.size(); i++)
        {
        	String serialNum=dataset.getData(i).getString("SERIAL_NUMBER");
            String delayMon=dataset.getData(i).getString("DELAY_MON");
            String ifGO="GO";
            /**
             * 校验手机号、延期月份（延期最多几个月可以用参数表）*
             * 手机号校验：1、校验手机是否在TL_B_USER_CONSUM表里存在未完成的数据。
             * 延期月份：最大不能超过几个月。 
             * */
            if(i>0){
            	if(serialNumList.indexOf(serialNum)>-1){
            		msg="号码重复导入。";
            		ifGO="DONGO";
            	}else{
            		serialNumList=serialNumList+"|"+serialNum;
            	}
            }else{
            	serialNumList=serialNumList+"|"+serialNum;
            }
            //log.info("("*********cxy****serialNum.indexOf(serialNumList)=="+serialNumList.indexOf(serialNum));
            //log.info("("*********cxy******serialNum="+serialNum+"*****serialNumList=="+serialNumList+"******msg="+msg+"*******ifGO="+ifGO);
            
            boolean ifNum=GoodsApplyBean.checkDelayMon(delayMon);
            if(ifNum){
            	if(Integer.parseInt(delayMon)>Integer.parseInt(maxDelayMon)){
            		msg="延期月数不能大于【"+maxDelayMon+"】个月。";
            		ifGO="DONGO";
            	} 
            	if(Integer.parseInt(delayMon)<Integer.parseInt(minDelayMon)){
            		msg="延期月数不能小于【"+minDelayMon+"】个月。";
            		ifGO="DONGO";
            	}
            }else{
            	msg="延期月数必须是数字，且必须小于等于【"+maxDelayMon+"】个月。";
            	ifGO="DONGO";
            }
            if("GO".equals(ifGO)){
	            IData qryData=new DataMap();
	            qryData.put("SERIAL_NUMBER", serialNum);
	            IDataset results=GoodsApplyBean.queryUserIfOutDateGoods(qryData);
	            for(int j=0; j<results.size(); j++){
	            	String userId=results.getData(j).getString("USER_ID","");
	            	String ruleId=results.getData(j).getString("RULE_ID","");
	            	String ruleName=results.getData(j).getString("RULE_NAME","");
	            	String quancode=results.getData(j).getString("QUANCODE","");
	            	String end_date=results.getData(j).getString("END_DATE","");
	            	String inDateType=results.getData(j).getString("IN_DATE_TYPE","");
	            	//log.info("("*********cxy******serialNum="+serialNum+"***j="+j+"**ruleId="+ruleId);
	            	if("OUT".equals(inDateType)){
	            		String state=results.getData(j).getString("STATE","");//状态：0未领取 1已领取部分  2全部领取完成  3已返销
	            		//log.info("("*********cxy******state="+state);
	            		if("0".equals(state) || "1".equals(state)){
	            			SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	            			Calendar cal=Calendar.getInstance();
	            			Date nowDate=new Date();
	            			cal.setTime(nowDate);
	            			cal.add(cal.MONTH, Integer.parseInt(delayMon));
	            			Date endDate=cal.getTime();
	            			String rsrvStr5="工号："+getVisit().getStaffId()+",日期：【"+df.format(nowDate)+"】,办理延期【"+delayMon+"】个月。";
	            			//log.info("("*********cxy******rsrvStr5="+rsrvStr5);
	            			IData params = new DataMap();
	            			params.put("USER_ID", userId); 
	            			params.put("DELAY_MON", delayMon); 
	            			params.put("RULE_ID", ruleId); 
	            			params.put("STATE", state); 
	            			params.put("QUANCODE", quancode); 
	            			params.put("RSRV_STR5", rsrvStr5); 
	            			//将礼品延期
	            			GoodsApplyBean.updateGoodsDelayEndDate(params);
	            			 
	            			end_date=df.format(endDate);
	            			msg="【成功】"; 
	            		}else{
	            			//已经领完或者已经返销 
	            			String stateDesc="";
	            			if("2".equals(state)){
	            				stateDesc="礼品已全部领取完成";
	            			}else if("3".equals(state)){
	            				stateDesc="已返销";
	            			}
	            			msg="该礼品的不能延期，因其状态是【"+stateDesc+"】";
	            			//log.info("("*********cxy******stateDesc="+stateDesc);
	            		}
	            	}else{
	            		//该人员还在有效期
	            		msg="该礼品无需延期，礼品还在有效期内。";
	            		//log.info("("*********cxy******msg="+msg);
	            	}
	            	IData errData=new DataMap();
	                errData.put("SERIAL_NUMBER", serialNum);
	                errData.put("DELAY_MON", delayMon);
	                errData.put("RULE_ID", ruleId); 
	                errData.put("RULE_NAME", ruleName);
	                errData.put("QUANCODE", quancode); 
	                errData.put("END_DATE", end_date);
	                errData.put("IMPORT_ERROR", msg);
	                rtnSet.add(errData);
	            }
	            if(IDataUtil.isEmpty(results)){
	            	//该人员都没有礼品可以换
	            	msg="根据手机号【"+serialNum+"】找到不到该人员积分兑换礼品的信息。";
	            	IData errData=new DataMap();
	                errData.put("SERIAL_NUMBER", serialNum);
	                errData.put("DELAY_MON", delayMon); 
	                errData.put("IMPORT_ERROR", msg);
	                rtnSet.add(errData);
	            }
	        }else{
	        	IData errData=new DataMap();
                errData.put("SERIAL_NUMBER", serialNum);
                errData.put("DELAY_MON", delayMon); 
                errData.put("IMPORT_ERROR", msg);
                rtnSet.add(errData);
	        } 
        } 
        
        //log.info("("*********cxy******rtnSet="+rtnSet);
        return rtnSet; 
    }
    
    /**
     * REQ201610190011 关于积分兑换礼品延长验证码有效期的需求
     * chenxy3 20161114
     * 
     */
    public static IDataset queryUserIfOutDateGoods(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        
        parser.addSQL(" select case when t.end_date > sysdate then 'IN' else 'OUT' end IN_DATE_TYPE,  ");
        parser.addSQL(" t.*  from tl_b_user_score_goods t  ");
        parser.addSQL(" where t.serial_number=:SERIAL_NUMBER ");
        
    	return Dao.qryByParse(parser);
    }
    
    /**
    * REQ201610190011 关于积分兑换礼品延长验证码有效期的需求
     * chenxy3 20161114
     * 延期终止日期 
     * */
    public static void updateGoodsDelayEndDate(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("DELAY_MON", params.getString("DELAY_MON")); 
    	param.put("RULE_ID", params.getString("RULE_ID")); 
    	param.put("STATE", params.getString("STATE")); 
    	param.put("QUANCODE", params.getString("QUANCODE")); 
    	param.put("RSRV_STR5", params.getString("RSRV_STR5")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update tl_b_user_score_goods t ");
    	sql.append(" set t.end_date=add_months(sysdate,:DELAY_MON),T.RSRV_STR5=:RSRV_STR5 ");
    	sql.append(" WHERE T.USER_ID=:USER_ID ");
    	sql.append(" AND T.RULE_ID=:RULE_ID ");
    	sql.append(" AND T.STATE=:STATE ");
    	sql.append(" AND t.quancode=:QUANCODE ");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * REQ201610190011 关于积分兑换礼品延长验证码有效期的需求
     * chenxy3 20161114
     * 测试是否为数字
     */
    public static boolean checkDelayMon(String inStr) throws Exception
    {  
        Pattern pn=  Pattern.compile("[0-9]*");
        Matcher mr= pn.matcher(inStr);
        if(mr.matches()){
        	return true;
        }
        return false;
    }
}