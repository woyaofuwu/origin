
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage;
import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeResInfoQry;

/**
 * REQ201505210004关于新增FTTH光猫办理流程的需求
 * CHENXY3
 * 2015-06-08
 * */
public class FTTHModemManageBean extends CSBizBean
{
	/**
	 * 获取用户是否存在光猫信息TF_F_USER_OTHER
	 * 
	 * */
	public static IDataset getUserModermInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_FTTHMODERM1", param);
        return userModerms;
    }
	
	/**
	 * 获取旧开户流程用户是否存在光猫信息TF_F_USER_OTHER
	 * 
	 * */
	public static IDataset getOldUserModermInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_FTTHMODERM", param);
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在租赁光猫信息TF_F_USER_OTHER
	 * 
	 * */
	public static IDataset getUserModermInfoByLease(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_FTTHMODERM_BY_LEASE", param);
        return userModerms;
    }
	/**
	 * 查询用户光猫信息TF_F_USER_OTHER
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryModemInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        param.put("RSRV_STR1", inParam.getString("MODERM_ID",""));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_MODERM", param);
        return userModerms;
    }
	
	/**
	 * 查询用户有效光猫信息TF_F_USER_OTHER
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryUserModemInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        param.put("RSRV_STR1", inParam.getString("MODERM_ID",""));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_MODERM_BY_STR1", param);
        return userModerms;
    }
	
	/**
	 * 查询用户可补录光猫信息TF_F_USER_OTHER
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryModemSupplementInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_SUPPLEMENT_MODEM", param);
    }
	
	/**
	 * 根据User_ID、Inst_id查询用户光猫信息TF_F_USER_OTHER
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset queryModermInfoByInstId(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("INST_ID", inParam.getString("INST_ID"));
        param.put("RSRV_STR1", inParam.getString("MODERM_ID"));
        param.put("RSRV_VALUE_CODE",inParam.getString("RSRV_VALUE_CODE"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_MODERM_BY_INSTID", param);
        return userModerms;
    }
	
	/**
	 * 获取客户是否存在光猫信息 TF_F_USER_OTHER
	 * @param param
	 * @return
	 */
	public IDataset getCustModermInfo(IData inParam) throws Exception
	{
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_CUST_OTHER_FTTHMODERM", param);
        return userModerms;
	}
	
	/**
	 * 判断宽带用户是否是FTTH用户
	 * */
	public static IDataset getUserFTTHProd(IData inParam) throws Exception
    {
	    IDataset resultDataset = null;
	    
	    String serialNum = inParam.getString("SERIAL_NUMBER");
        
        if (!serialNum.startsWith("KD_"))
        {
            serialNum = "KD_"+serialNum;
        }
        
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNum);
        
//        IDataset userModerms = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_FTTH_BY_SERIALNUMBER1", param);
        
        if (IDataUtil.isNotEmpty(widenetInfos))
        {
            //3：移动FTTH，5：铁通FTTH
            if ("3".equals(widenetInfos.getData(0).getString("RSRV_STR2")) || "5".equals(widenetInfos.getData(0).getString("RSRV_STR2")))
            {
                resultDataset = widenetInfos;
            }
        }
        
        return resultDataset;
    }
	
	/**
	 * 获取用户是否存在FTTH宽带信息 
	 * TF_B_TRADE TF_BH_TRADE
	 * 
	 * */
	public static IDataset getUserFTTHProdWWG(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_BH_TRADE", "SEL_TRADE_FTTH_BY_SERIALNUMBER1", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        return userModerms;
    }
	
	/**
	 * 获取用户是否有开户且未租赁光猫光猫信息
	 * TF_B_TRADE TF_BH_TRADE
	 * 
	 * */
	public static IDataset getUserTradeFTTHProdWWG(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_FTTH_BY_SERIALNUMBER1_OLD", param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在宽带1+信息
	 * TF_F_USER_SALE_ACTIVE
	 * 
	 * */
	public static IDataset getUserWilenInfoAct(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_ACTIVE_WILEN_BY_SERINUM", param);
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在宽带1+信息
	 * TF_F_USER_SALEACTIVE_BOOK
	 * 
	 * */
	public static IDataset getUserWilenInfoBook(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_SALEACTIVE_BOOK", "SEL_BOOK_WILEN_BY_SERINUM", param);
        return userModerms;
    }
	
	/**
	 * 获取用户是否存在宽带1+信息
	 * TF_F_USER_SALEACTIVE_BOOK
	 * 
	 * */
	public static IDataset getCustByIdPsptid(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("CUST_ID", inParam.getString("CUST_ID"));
        param.put("PSPT_ID", inParam.getString("PSPT_ID"));
        IDataset userModerms = Dao.qryByCode("TF_F_CUSTOMER", "SEL_BY_CUSTID_PSPTID", param);
        return userModerms;
    }
	
	/**
	 * 判断用户是否办理FTTH宽带
	 * 如果存在FTTH宽带，判断是否存在宽带1+活动
	 * 如果存在宽带1+活动返回1
	 * 如果只存在FTTH宽带返回2
	 * 不存在返回0
	 * */
	public  String getPayMoneyType(IData inParam) throws Exception{
		boolean isExist=false;
		String payType="0";//该状态对应TD_S_COMMPARA表的param_attr=6131的param_code
		IDataset wilens=this.getUserFTTHProd(inParam);
		if(wilens!=null && wilens.size()>0){
			isExist=true;
		}else{
			wilens=this.getUserFTTHProdWWG(inParam);
			if(wilens!=null && wilens.size()>0){
				if("0".equals(wilens.getData(0).getString("RSRV_TAG1",""))){
					String tradeTypeCode = wilens.getData(0).getString("TRADE_TYPE_CODE","");
					
					CSAppException.appError("6131", "该用户"+inParam.getString("SERIAL_NUMBER")+"有未完工工单"+"并且办理了租赁光猫业务,不能再次租赁");
				}else{
					isExist=true;
				}
			}else{
				if(DataSetUtils.isBlank(FTTHModemManageBean.getUserTradeFTTHProdWWG(inParam))){
	  				String serialNumber = inParam.getString("SERIAL_NUMBER");
	  				IData userInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
	        		if(IDataUtil.isEmpty(userInfo)){
	        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户宽带已拆机或未办理宽带，不能办理光猫申领业务！");
	        		}
	        		String userId = userInfo.getString("USER_ID");
	        		IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("605", userId, "0");
	        		if(DataSetUtils.isNotBlank(outDataset)){//有未完工拆机业务，业务不能继续
	    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"您已办理拆机业务，不能办理光猫申领业务!");
	    			}
	    			IDataset destorySpecDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("615", userId, "0");
	    			if(DataSetUtils.isNotBlank(destorySpecDataset)){//有未完工特殊拆机业务，业务不能继续
	    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"您已办理特殊拆机业务，不能办理光猫申领业务!");
	    			}
	  			}else{
	    			isExist=true;
	  			}
			}
		}
		
		if(isExist){//判断是否优惠租赁过一次
			IDataset wilen1s=this.getUserWilenInfoAct(inParam);
			if(wilen1s!=null && wilen1s.size()>0){
				payType="1";//存在宽带1+，状态为1，扣100块押金
			}else{
				wilen1s=this.getUserWilenInfoBook(inParam);
				if(wilen1s!=null && wilen1s.size()>0){
					payType="1";//存在宽带1+，状态为1，扣100块押金
				}else{
					payType="2";//不存在宽带1+，状态为2，扣200块押金
				}
			}
		}
		return payType;
	}
	
	/**
	 * 获取用户优惠租赁记录
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset getUserOtherInfoByRsrvTag3(IData inParam)throws Exception
	{
		IData param = new DataMap();
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_FAVOURABLE_FTTH_INFO", param);
        return userModerms;
	}
	
	/**
	 * 
	 * 更新TF_B_TRADE_OTHER用户光猫串号
	 * */
    public void updModemNumber(IData inParam) throws Exception
    {
    	IData param = new DataMap();
    	
    	IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(inParam.getString("SERIAL_NUMBER"));
        param.put("RES_NO", inParam.getString("RES_NO"));
        param.put("RES_KIND_CODE", inParam.getString("RES_KIND_CODE"));
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        param.put("USER_ID", userInfo.getString("USER_ID",inParam.getString("USER_ID","")));
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));
        param.put("UPDATE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", inParam.getString("TRADE_DEPART_ID"));
        Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TRADE_OTHER_FTTHMODERM1", param, Route.getJourDb(BizRoute.getTradeEparchyCode())); 
    }  
    
    
    /**
	 * 
	 * 更新TF_F_USER_OTHER旧开户流程用户光猫串号
	 * */
    public void updOldModemNumber(IData inParam) throws Exception
    {
    	IData param = new DataMap();
        param.put("RES_NO", inParam.getString("RES_NO"));
        param.put("RES_KIND_CODE", inParam.getString("RES_KIND_CODE"));
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        param.put("UPDATE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", inParam.getString("TRADE_DEPART_ID"));
        Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_USER_OTHER_FTTHMODERM_OLD", param); 
    }    
    
    /**
	 * 1、获取“CRM > 个人业务 > 宽带业务 > FTTH宽带业务 > FTTH宽带用户开户”开户失败的用户
	 * 根据用户的TF_B_TRADE_OTHER表，判断用户（手机号码）是否申领了光猫；
	 * 如果是申领了光猫则调账务接口，将宽带光猫押金存折里的钱转到现金存折；
	 * 结束用户的TF_B_TRADE_OTHER资料  
	 * */
    public void checkWilenFailUser(IData inParam) throws Exception
    {
    	IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
    	String serialNumber = dealParam.getString("SERIAL_NUMBER");
    	String tradeId = dealParam.getString("TRADE_ID");
		String userId=dealParam.getString("USER_ID");
		String modermNum=dealParam.getString("RSRV_STR1");
		String deposit=dealParam.getString("RSRV_STR2","0");//押金  
		String custId=dealParam.getString("CUST_ID","");//押金  
		
		if(Integer.parseInt(deposit) > 0){//有押金才退还押金
			try{
				IData params=new DataMap();
				params.put("SERIAL_NUMBER", serialNumber);
				params.put("OUTER_TRADE_ID", dealParam.getString("RSRV_STR8",""));
				params.put("DEPOSIT_CODE_OUT", "9002");
				params.put("TRADE_FEE", deposit);
				params.put("CHANNEL_ID", "15000");
				params.put("UPDATE_DEPART_ID", "36601");
		   		params.put("UPDATE_STAFF_ID", "SUPERUSR"); 
				params.put("TRADE_DEPART_ID", "36601");
		   		params.put("TRADE_STAFF_ID", "SUPERUSR");
		   		params.put("TRADE_CITY_CODE", "HNSJ");
		   		params.put("SUB_SYS", "RESSERV_TF_RH_SALE_DEAL");
		   		params.put("TRADE_ID", tradeId);
		        CSBizBean.getVisit().setStaffEparchyCode("0898");
				//将“宽带光猫押金存折”转移到现金存折
		   		IData inAcct=AcctCall.transFeeOutADSL(params);
				String resultCode=inAcct.getString("RESULT_CODE","");
				if(!"".equals(resultCode)&&"0".equals(resultCode)){
					if(StringUtils.isNotBlank(modermNum) && "1".equals(dealParam.getString("RSRV_TAG2"))){//已录入光猫，需退还光猫
						/**
	   					 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
	   					 * 退还光猫
	   					 * */ 
   						IData param=new DataMap();
   						param.put("SERIAL_NUMBER", serialNumber);//PARA_VALUE1
   						param.put("TRADE_ID", tradeId);//台账流水
   						param.put("RES_NO", modermNum);//终端串号 
   						param.put("CUST_ID", custId);//客户名称
   						param.put("UPDATE_STAFF_ID", dealParam.getString("UPDATE_STAFF_ID"));//销售员工
   						boolean rtnFlag=this.returnFtthModem(param); 
	   					if(rtnFlag){
	   						params.put("RESULT_INFO", "押金返还成功，光猫退还成功!");
	   						params.put("MODEM_STATUS", "3");//已退还
	   					}else{
	   						params.put("RESULT_INFO", "押金返还成功，光猫退还失败！");
	   						params.put("MODEM_STATUS", "1");//申领
	   					}
					}
					//if接口调用成功，修改用户的TF_B_TRADE_OTHER资料   	更换押金状态、光猫状态
   					this.updateTradeOtherEnddate(params);
   					IData smsData = new DataMap();
   					smsData.put("SERIAL_NUMBER",serialNumber);
   					smsData.put("USER_ID",userId);
   					String sysTime=SysDateMgr.getSysTime();
   					sysTime=sysTime.substring(sysTime.indexOf(":")-2);
   					String content="尊敬的客户，您之前缴纳的光猫押金"+Integer.parseInt(deposit)*0.01+"元，已于"+SysDateMgr.getSysDateYYYYMMDD().substring(0,4)+"年"+SysDateMgr.getCurMonth()+"月"+SysDateMgr.getCurDay()+"日 "+sysTime+" 释放并存入您的话费账户，请及时查询，详情请咨询当地移动营业厅或拨打10086。中国移动海南公司。";
   					smsData.put("NOTICE_CONTENT",content);
   					smsData.put("STAFF_ID","SUPERUSR");
   					smsData.put("DEPART_ID","36601");
   					smsData.put("REMARK","FTTH退押金短信");
   					smsData.put("BRAND_CODE","");
   					smsData.put("FORCE_START_TIME","");
   					smsData.put("FORCE_END_TIME","");
   					smsSent(smsData);

				}else{
					String rtnInfo=inAcct.getString("RESULT_INFO","");
					if(!"".equals(rtnInfo)&& rtnInfo.length()>95){
						rtnInfo=rtnInfo.substring(0,95);
					}
					params.put("RESULT_INFO", rtnInfo);
					params.put("CALL_INTEFACE", "AM_CRM_TransFeeOutFTTH");
					this.updateTradeOtherCallFalse(params);
					CSAppException.apperr(CrmCommException.CRM_COMM_103,params.toString());
				} 
			}catch(Exception e){
				IData errParam=new DataMap();
				errParam.put("USER_ID", userId);
				errParam.put("RESULT_INFO", "SS.FTTHModemManageSVC.checkWilenFailUser执行错误。");
				errParam.put("TRADE_ID", tradeId);
				this.updateTradeOtherCallFalse(errParam);
				errParam.put("CALL_INTEFACE", e.toString());
				CSAppException.apperr(CrmCommException.CRM_COMM_103,errParam.toString());
			}
		}	
    	
    }
    
    /**
     * 获取开户失败用户信息 
     */
    public static IDataset getWilenFailUser(IData inparams) throws Exception
    {  
    	IData param = new DataMap();
    	param.put("USER_ID", inparams.getString("USER_ID",""));
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" select t.*  ");
        parser.addSQL(" from tf_bh_trade t,tf_b_trade_other a");
        parser.addSQL(" where t.subscribe_STATE='A' ");
        parser.addSQL(" AND T.TRADE_TYPE_CODE='600' "); 
        parser.addSQL(" AND T.RSRV_STR2='0' "); 
        parser.addSQL(" AND a.rsrv_value_code = 'FTTH' ");
        parser.addSQL(" AND a.rsrv_tag1 = '0' ");
        parser.addSQL(" AND a.rsrv_tag2 = '1' ");
        parser.addSQL(" AND a.rsrv_str7 = '0' ");
        parser.addSQL(" and a.trade_id=t.trade_id ");
        parser.addSQL(" AND a.user_id=:USER_ID ");
        parser.addSQL(" AND and to_number(a.rsrv_str2)>0");
        parser.addSQL(" AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");
        
    	return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 获取开户失败用户信息 
     */
    public static IDataset getFtthTempBhTrade(IData inparams) throws Exception
    {  
        
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t2.*  ");
        parser.addSQL(" from tf_bh_trade_ftth t ,tf_bh_trade t2");
        parser.addSQL(" where t.trade_id=t2.trade_id and t.deal_tag=:DEAL_TAG "); 
        parser.addSQL(" and t2.subscribe_STATE='A' and t2.TRADE_TYPE_CODE='600' "); 
    	return Dao.qryByParse(parser, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 宽带开户撤单光猫、魔百和处理
     * @param inParam
     * @throws Exception
     * @author lijun17 2016-5-31
     */
    public IDataset checkWidenetWilenFailUser(IData inParam) throws Exception
    {
    	String tradeId = inParam.getString("TRADE_ID");
    	String serialNumber=inParam.getString("SERIAL_NUMBER");
    	String custId = inParam.getString("CUST_ID");
    	IData endParams=new DataMap();
    	if(serialNumber.length() == 11){
    		//光猫处理
    		String rsrv_value_code = "FTTH";
        	IDataset userModerms = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
        	if(DataSetUtils.isNotBlank(userModerms)){
        		String modermNum=userModerms.getData(0).getString("RSRV_STR1");//光猫串号
    			String deposit=userModerms.getData(0).getString("RSRV_STR2","0");//押金   
    			if(Integer.parseInt(deposit)>0){
    				IData params = new DataMap();
    				params.put("SERIAL_NUMBER", serialNumber);
    				params.put("OUTER_TRADE_ID", "");
    				params.put("DEPOSIT_CODE_OUT", "9002");
    				params.put("TRADE_FEE", deposit);
    				params.put("CHANNEL_ID", "15000");
    				params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    		   		params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    				params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    		   		params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    		   		params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
    				//将“宽带光猫押金存折”转移到现金存折
    		   		IData inAcct=AcctCall.transFeeOutADSL(params);
    				String resultCode=inAcct.getString("RESULT_CODE","");
    				if(!"".equals(resultCode) && "0".equals(resultCode)){
    					//修改Other表记录
    					IData tradeParam = new DataMap();
    					tradeParam.put("TRADE_ID",tradeId);
    					this.updateTradeOtherEnddate(tradeParam);
    					if(StringUtils.isNotBlank(modermNum)){//已录入光猫，需退还光猫
    							/**
    	   					 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
    	   					 * 退还光猫
    	   					 * */ 
       						IData param=new DataMap();
       						param.put("SERIAL_NUMBER", serialNumber);//PARA_VALUE1
       						param.put("TRADE_ID", tradeId);//台账流水
       						param.put("RES_NO", modermNum);//终端串号 
       						param.put("CUST_ID", custId);//客户名称
       						param.put("UPDATE_STAFF_ID", userModerms.getData(0).getString("UPDATE_STAFF_ID"));//销售员工
       						boolean rtnFlag=this.returnFtthModem(param); 
    	   					if(rtnFlag){
    	   						endParams.put("CALL_INFO", "FTTH光猫押金返还成功，光猫退还失败！");
    	   						endParams.put("RESULT_INFO", "1");//FTTH光猫押金返还成功押金返还成功，光猫退还失败
    	   					}else{
    	   						endParams.put("RESULT_INFO", "0");//FTTH光猫押金返还成功押金返还成功，光猫退还成功
    	   					}
    					}
    	    		}else{
    	    			CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金错误:"+inAcct.getString("RESULT_INFO"));
    	    		}
    			}
        	}
    	}else{
    		//商务光猫处理  只需要退光猫终端
    		 String rsrv_value_code = "FTTH_GROUP";
    	     IDataset tradeOtherInfos = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
    	     String modermNum=tradeOtherInfos.getData(0).getString("RSRV_STR1");//光猫串号
    	     if(StringUtils.isNotBlank(modermNum)){//已录入光猫，需退还光猫
					/**
				 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
				 * 退还光猫
				 * */ 
				IData param=new DataMap();
				param.put("SERIAL_NUMBER", "KD_"+serialNumber);//PARA_VALUE1
				param.put("TRADE_ID", tradeId);//台账流水
				param.put("RES_NO", modermNum);//终端串号 
				param.put("CUST_ID", custId);//客户名称
				param.put("UPDATE_STAFF_ID", tradeOtherInfos.getData(0).getString("UPDATE_STAFF_ID"));//销售员工
				boolean rtnFlag=this.returnFtthModem(param); 
				if(rtnFlag){
					endParams.put("CALL_INFO", "FTTH商务光猫退还失败！");
					endParams.put("RESULT_INFO", "3");//FTTH商务光猫退还失败
				}else{
					endParams.put("RESULT_INFO", "2");//FTTH光猫光猫退还成功
				}
			}
    	}
    	
    	//魔百和处理
    	IDataset userTopSetBoxs = TradeResInfoQry.queryAllTradeResByTradeId(tradeId);
    	if(DataSetUtils.isNotBlank(userTopSetBoxs)){
    		String modermNum=userTopSetBoxs.getData(0).getString("IMSI");//魔百和终端串号4
			String deposit=userTopSetBoxs.getData(0).getString("RSRV_NUM2","0");//押金   
			if(Integer.parseInt(deposit)>0){
				IData params=new DataMap(); 
    			String money = String.valueOf(Integer.parseInt(deposit)*100);
		   		params.put("SERIAL_NUMBER", serialNumber);
				params.put("OUTER_TRADE_ID", "");
				params.put("DEPOSIT_CODE_OUT", "9016");
				params.put("TRADE_FEE", money);
				params.put("CHANNEL_ID", "15000");
				params.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
				params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
				params.put("TRADE_DEPART_ID",CSBizBean.getVisit().getDepartId());
				params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
				params.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		   		//调用接口，将【押金】——>【现金】
		   		IData inAcct = AcctCall.transFeeOutADSL(params);
		   		if(IDataUtil.isEmpty(inAcct)){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口报错！");
				}else{
					String resultCode=inAcct.getString("RESULT_CODE","");
					if(!resultCode.equals("0")){
						CSAppException.apperr(CrmCommException.CRM_COMM_103,"调用账务接口错误："+inAcct.getString("RESULT_INFO",""));
					}
				}
		   		if(StringUtils.isNotBlank(modermNum)){//已录入魔百和终端，需退还魔百和终端
		   			IData boxInfo = userTopSetBoxs.first();
		   			FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
		   			IData returnParam = new DataMap();
	    			returnParam.put("RES_NO", boxInfo.getString("IMSI"));
					returnParam.put("PARA_VALUE1", serialNumber);
					returnParam.put("SALE_FEE", boxInfo.getString("RSRV_NUM5",""));
					returnParam.put("PARA_VALUE7", "0");
					returnParam.put("DEVICE_COST", boxInfo.getString("RSRV_NUM4","0"));
					returnParam.put("X_CHOICE_TAG", "1");
					returnParam.put("RES_TYPE_CODE", "4");
					returnParam.put("PARA_VALUE11", boxInfo.getString("UPDATE_TIME"));
					returnParam.put("PARA_VALUE14", boxInfo.getString("RSRV_NUM5","0"));
					returnParam.put("PARA_VALUE17", boxInfo.getString("RSRV_NUM5","0"));
					returnParam.put("PARA_VALUE1", serialNumber);
					IDataset custInfo=bean.getCustInfoByCustid(custId);
					if(custInfo!=null && custInfo.size()>0){
						String custName=custInfo.getData(0).getString("CUST_NAME");
						returnParam.put("CUST_NAME", custName);
					}
					returnParam.put("STAFF_ID", boxInfo.getString("UPDATE_STAFF_ID"));
					returnParam.put("TRADE_ID", boxInfo.getString("INST_ID"));
	    			IDataset returnResult=HwTerminalCall.returnTopSetBoxTerminal(returnParam);
	    			if(IDataUtil.isEmpty(returnResult)){
	    				endParams.put("CALL_INFO", "魔百和押金返还成功，魔百和终端退还失败！");
	    		   		endParams.put("RESULT_INFO", "5");//魔百和押金返还成功押金返还成功，魔百和机顶盒退还失败
					}else{
						String resultCode=returnResult.getData(0).getString("X_RESULTCODE","");
						if(!resultCode.equals("0")){
							endParams.put("CALL_INFO", "魔百和押金返还成功，魔百和终端退还失败！"+returnResult.
									getData(0).getString("X_RESULTINFO",""));
					   		endParams.put("RESULT_INFO", "5");//魔百和押金返还成功押金返还成功，魔百和机顶盒退还失败
						}else{
					   		endParams.put("RESULT_INFO", "4");//魔百和押金返还成功押金返还成功，魔百和机顶盒退还成功
						}
					}
		   		}
			}
    	}
    	IDataset results = new DatasetList();
    	results.add(endParams);
    	return results;
    }
    
   
    
    /**
     * 终止用户的光猫领取记录     *  
     * */
    public static void updateFtthTempTrade(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", params.getString("TRADE_ID")); 
    	param.put("DEAL_TAG", params.getString("DEAL_TAG")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update tf_bh_trade_ftth t ");
    	sql.append(" set t.DEAL_TAG=:DEAL_TAG,T.DEAL_TIME=SYSDATE ");
    	sql.append(" where  t.trade_id=:TRADE_ID");  
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
	 * 2、获取申请光猫已满3年的用户
	 * 根据用户的TF_F_USER_OTHER表，判断用户（手机号码）是否申领了光猫；
	 * 如果是申领了光猫则调账务接口，将宽带光猫押金存折里的钱转到现金存折；
	 * 结束用户的TF_F_USER_OTHER资料 
	 * */
   public void checkThreeYearsUser(IData inParam) throws Exception
   {
	   IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
		String serialNumber=dealParam.getString("SERIAL_NUMBER");
		String userId = dealParam.getString("USER_ID");
    	String instId = dealParam.getString("INST_ID");
		String modermNum=dealParam.getString("RSRV_STR1");
		String deposit=dealParam.getString("RSRV_STR2","0");//押金
		try{		
			if(Integer.parseInt(deposit) > 0){
				IData params=new DataMap(); 
				params.put("SERIAL_NUMBER", serialNumber);
				params.put("OUTER_TRADE_ID", dealParam.getString("RSRV_STR8",""));
				params.put("DEPOSIT_CODE_OUT", "9002");
				params.put("TRADE_FEE", deposit);
				params.put("CHANNEL_ID", "15000");
				params.put("UPDATE_DEPART_ID", "36601");
		   		params.put("UPDATE_STAFF_ID", "SUPERUSR"); 
				params.put("TRADE_DEPART_ID", "36601");
		   		params.put("TRADE_STAFF_ID", "SUPERUSR");
		   		params.put("TRADE_CITY_CODE", "HNSJ");
		   		params.put("SUB_SYS", "RESSERV_TF_RH_SALE_DEAL");
		        CSBizBean.getVisit().setStaffEparchyCode("0898");
	    		//调用接口，将【押金】——>【现金】
		   		IData inAcct = AcctCall.transFeeOutADSL(params);//将宽带光猫押金存折里的钱转到现金存折
				String callRtnType=inAcct.getString("RESULT_CODE","");//0-成功 1-失败
				if(!"".equals(callRtnType)&&"0".equals(callRtnType)){
   					//if接口调用成功，清空押金,修改押金状态	
					IData updModemParam = new DataMap();
					updModemParam.put("USER_ID", userId);
					updModemParam.put("INST_ID", instId);
   					this.updateUserOtherDeposit(updModemParam); 
   					/**
   					 * REQ201512160017 光猫押金释放后下发告知短信的开发需求
   					 * 退押金成功下发短信： 
   					 * 20151218 chenxy3
   					 * */ 
   		            
   		            //插短信
   					IData smsData = new DataMap();
   					smsData.put("SERIAL_NUMBER",serialNumber);
   					smsData.put("USER_ID",userId);
   					String sysTime=SysDateMgr.getSysTime();
   					sysTime=sysTime.substring(sysTime.indexOf(":")-2);
   					String content="尊敬的客户，您之前缴纳的光猫押金"+Integer.parseInt(deposit)*0.01+"元，已于"+SysDateMgr.getSysDateYYYYMMDD().substring(0,4)+"年"+SysDateMgr.getCurMonth()+"月"+SysDateMgr.getCurDay()+"日 "+sysTime+" 释放并存入您的话费账户，请及时查询，详情请咨询当地移动营业厅或拨打10086。中国移动海南公司。";
   					smsData.put("NOTICE_CONTENT",content);
   					smsData.put("STAFF_ID","SUPERUSR");
   					smsData.put("DEPART_ID","36601");
   					smsData.put("REMARK","FTTH退押金短信");
   					smsData.put("BRAND_CODE","");
   					smsData.put("FORCE_START_TIME","");
   					smsData.put("FORCE_END_TIME","");
   					smsSent(smsData);
				}else{
					String rtnInfo=inAcct.getString("RESULT_INFO","");
					if(!"".equals(rtnInfo) && rtnInfo.length()>98){
						rtnInfo=rtnInfo.substring(0,98);
					}
					IData errParam=new DataMap();
					errParam.put("USER_ID", userId);
   					errParam.put("INST_ID", instId);
					errParam.put("RESULT_INFO", rtnInfo);
					errParam.put("CALL_INTEFACE", "AM_CRM_TransFeeOutFTTH");
					this.updateUserOtherCallFalse(errParam);
					CSAppException.apperr(CrmCommException.CRM_COMM_103,errParam.toString());
				} 
			}
		}catch(Exception e){
			IData errParam=new DataMap();
			errParam.put("USER_ID", userId);
			errParam.put("INST_ID", instId);
			errParam.put("RESULT_INFO", "SS.FTTHModemManageSVC.checkThreeYearsUser错误。");
			this.updateUserOtherCallFalse(errParam);
			errParam.put("CALL_INTEFACE", e.toString());
			CSAppException.apperr(CrmCommException.CRM_COMM_103,errParam.toString());
		}
   }
   
   /**
    * 获取移机、拆机未归还满3个月且光猫申领模式为租赁的方式的的光猫
    * 做押金沉淀处理
    * @param inParam
    * @throws Exception
    */
   public void checkThreeMonthNotReturnModermUser(IData inParam) throws Exception
   {
	   	IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
		String serialNumber=dealParam.getString("SERIAL_NUMBER");
		String userid=dealParam.getString("USER_ID");
		String custid=dealParam.getString("CUST_ID");
		String modermNum=dealParam.getString("RSRV_STR1");
		String deposit=dealParam.getString("RSRV_STR2");//押金
		String inst_id = dealParam.getString("INST_ID");
		String returnType = dealParam.getString("RSRV_STR9");
		try{		
			//3、获取默认账户  （acct_id)
	    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
	    	String acctId=accts.getData(0).getString("ACCT_ID"); 
			if(Integer.parseInt(deposit) > 0){
				IData params=new DataMap(); 
				params.put("USER_ID", userid);
				params.put("ACCT_ID", acctId);
				params.put("TRADE_FEE", deposit);
	    		params.put("SERIAL_NUMBER", serialNumber); 
		        CSBizBean.getVisit().setStaffEparchyCode("0898");
	    		//调用接口，押金沉淀
				IData callRtn=AcctCall.AMBackFee(params);//将宽带光猫押金存折里的钱沉淀
				String callRtnType=callRtn.getString("RESULT_CODE","");//0-成功 1-失败
				if(!"".equals(callRtnType)&&"0".equals(callRtnType)){
					IData updModemParam = new DataMap();
					updModemParam.put("USER_ID", userid);
					updModemParam.put("INST_ID", inst_id);
					updModemParam.put("RETURN_TYPE",returnType);
   					//if接口调用成功，清空押金 	押金沉淀处理，修改光猫状态为丢失	
   					this.updateUserOtherModermStatus(updModemParam); 
				}else{
					String rtnInfo=callRtn.getString("RESULT_INFO","");
					if(!"".equals(rtnInfo) && rtnInfo.length()>98){
						rtnInfo=rtnInfo.substring(0,98);
					}
					IData errParam=new DataMap();
					errParam.put("USER_ID", userid);
  					errParam.put("INST_ID", inst_id);
  					errParam.put("RESULT_INFO", rtnInfo);
  					errParam.put("CALL_INTEFACE", "AM_CRM_GMFeeDeposit");
					this.updateUserOtherCallFalse(errParam);
					CSAppException.apperr(CrmCommException.CRM_COMM_103,errParam.toString());
				} 
			}
		}catch(Exception e){
			IData errParam=new DataMap();
			errParam.put("USER_ID", userid);
			errParam.put("INST_ID", inst_id);
			errParam.put("RESULT_INFO", "SS.FTTHModemManageSVC.checkThreeMonthNotReturnModermUser错误。");
			this.updateUserOtherCallFalse(errParam);
			errParam.put("CALL_INTEFACE", e.toString());
			CSAppException.apperr(CrmCommException.CRM_COMM_103,errParam.toString());
		}
		  
   }
    
   /**
    * 获取移机、拆机未归还满3个月且光猫申领模式为租赁的方式的的光猫
    * @return
    */
   public static IDataset getThreeMonthNotReturnModermUser() throws Exception
   {
	   IData param = new DataMap(); 
	   IDataset users = Dao.qryByCode("TF_F_USER_OTHER", "SEL_FTTH_FULL3MONTH_BY_NOTRETURN", param);
       return users;
	}
    
	/**
     * 获取申请光猫已满3年的用户
     * （未完工）SEL_FTTH_FULL3YEAR
     */
    public static IDataset getThreeYearsUser(String userId) throws Exception
    {  
        IData param = new DataMap(); 
        IDataset users = Dao.qryByCode("TF_F_USER_OTHER", "SEL_FTTH_FULL3YEAR1", param);
        return users;
    }
    
    /**
     * 终止用户的光猫领取记录     *  TF_F_USER_OTHER
     * */
    public static void updateUserOtherEnddate(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("INST_ID", params.getString("INST_ID")); 
    	
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.end_date=sysdate ,t.UPDATE_TIME=sysdate,t.rsrv_str4=:CALL_INFO,t.rsrv_tag2='3',t.rsrv_str7='2',t.rsrv_str2='0',t.rsrv_tag1='3'");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.rsrv_tag2 = '1' ");//光猫状态为 1申领
    	sql.append(" and t.rsrv_tag1 = '0' ");//光猫状态为 0租赁
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.inst_id=:INST_ID ");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 终止用户的光猫领取记录     *  TF_B_TRADE_OTHER
     * */
    public static void updateTradeOtherEnddate(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", params.getString("TRADE_ID")); 
    	param.put("MODEM_STATUS", params.getString("MODEM_STATUS"));
    	param.put("RESULT_INFO", params.getString("RESULT_INFO"));
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_B_TRADE_OTHER t ");
    	sql.append(" set t.end_date=sysdate ,t.UPDATE_TIME=sysdate,t.rsrv_tag2=:MODEM_STATUS,t.rsrv_str4=:RESULT_INFO,t.rsrv_str7='2',t.rsrv_str2='0',t.rsrv_tag1='0'");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.rsrv_tag2 = '1' ");//光猫状态为 1申领
    	sql.append(" and t.rsrv_tag1 = '0' ");//光猫状态为 0租赁
    	sql.append(" and t.trade_id=:TRADE_ID ");
        Dao.executeUpdate(sql, param, Route.getJourDb());
    }
    
    /**
     * 满3年退押金问题：考虑到宽带拆机还要用这条记录，不允许终止     *  
     * 只是把押金的金额改了清空。
     * chenxy3 20160126
     * */
    public static void updateUserOtherDeposit(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("INST_ID", params.getString("INST_ID"));
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.rsrv_str2='0' ,t.UPDATE_TIME=sysdate,t.remark='满3年退押金',t.rsrv_str7='2'");//押金状态改为2：已退还
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.rsrv_str7='0' "); //押金状态为 0押金
    	sql.append(" and t.rsrv_tag2='1' "); //光猫状态为 1申领
    	sql.append(" and t.rsrv_tag1='0' "); //光猫状态为 0租赁
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.inst_id=:INST_ID ");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 移机、拆机满3个月未退光猫，押金沉淀处理，修改光猫状态为丢失
     * @throws Exception
     */
    public static void updateUserOtherModermStatus(IData inParams) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", inParams.getString("USER_ID")); 
    	param.put("INST_ID", inParams.getString("INST_ID"));
    	StringBuilder sql = new StringBuilder(1000);
    	String returnTpye = inParams.getString("RETURN_TYPE");
    	String remarkInfo = "";
    	if("1".equals(returnTpye)){//1.移机未退光猫  2.拆机未退光猫
    		remarkInfo = "移机未退光猫";
    	}else if("2".equals(returnTpye)){
    		remarkInfo = "拆机未退光猫";
    	}
    	param.put("REMARK_INFO", remarkInfo+"满3个月押金沉淀");
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.rsrv_str2='0' ,t.rsrv_str7='3',t.rsrv_tag2='4',t.end_date=sysdate,t.UPDATE_TIME=sysdate,t.rsrv_str4=:CALL_INFO,t.remark=:REMARK_INFO");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.inst_id=:INST_ID ");
        Dao.executeUpdate(sql, param);
    }
    
    /**
     * 对于宽带开户失败的用户，捞取一次后必须打上标记，不然会反复捞取。
     * 记录于TF_BH_TRADE的RSRV_STR10字段
     * */
    public static void updateBhTradeFlag(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("TRADE_ID", params.getString("TRADE_ID")); 
    	param.put("RSRV_STR10", params.getString("RSRV_STR10"));
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" UPDATE tf_bh_trade t ");
    	sql.append(" SET T.RSRV_STR10=:RSRV_STR10");
    	sql.append(" where t.subscribe_STATE='A' AND T.TRADE_TYPE_CODE='600'");
    	sql.append(" AND t.trade_id=:TRADE_ID");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    
    /**
     * 更新调用接口失败信息  *  TF_F_USER_OTHER
     * */
    public static void updateUserOtherCallFalse(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("INST_ID", params.getString("INST_ID"));
    	
    	param.put("CALL_INTEFACE", params.getString("CALL_INTEFACE")); 
    	param.put("RESULT_INFO", params.getString("RESULT_INFO")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.rsrv_str3=:CALL_INTEFACE,t.rsrv_str4=:RESULT_INFO ");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.inst_id=:INST_ID ");
        Dao.executeUpdate(sql, param);
    }
    /**
     * 更新调用接口失败信息  *  TF_B_TRADE_OTHER
     * */
    public static void updateTradeOtherCallFalse(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("TRADE_ID", params.getString("TRADE_ID"));
    	
    	param.put("CALL_INTEFACE", params.getString("CALL_INTEFACE")); 
    	param.put("RESULT_INFO", params.getString("RESULT_INFO")); 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_B_TRADE_OTHER t ");
    	sql.append(" set t.rsrv_str3=:CALL_INTEFACE,t.rsrv_str4=:RESULT_INFO ");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.trade_id=:TRADE_ID ");
        Dao.executeUpdate(sql, param, Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    
    /**
     * 修改用户的开始时间为当前时间     *  
     * */
    public static void updateUserOtherStartdate(IData params) throws Exception
    {
    	IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_OTHER t ");
    	sql.append(" set t.start_date=sysdate ");
    	sql.append(" where  t.rsrv_value_code = 'FTTH' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
        Dao.executeUpdate(sql, param);
    } 
    
    /**
	 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
	 * 单独退还光猫接口
	 * */ 
	public IDataset getTempFtthUserReturnModem(IData inparam) throws Exception{
		//取临时表数据
		IDataset rtnFlagset=new DatasetList();
    	IData tempData=new DataMap();
    	tempData.put("DEAL_TAG","5");
    	IDataset tempUsers=this.getFtthTempBhTrade(tempData);
    	for(int i=0;i<tempUsers.size();i++){
    		IData user=tempUsers.getData(i);
			String tradeId=user.getString("TRADE_ID");
			String custId=user.getString("CUST_ID");
			//注意这里取的都是宽带的NUMBER，要替换
			String serialNumber=user.getString("SERIAL_NUMBER").replace("KD_", "");
			String eparchyCode=user.getString("EPARCHY_CODE");
			String cityCode=user.getString("CITY_CODE");
			//判断是否申领了光猫，取TF_F_USER_OTHER表的RSRV_STR1存在串号
			IData getParam=new DataMap();
			getParam.put("SERIAL_NUMBER", serialNumber);
			getParam.put("TRADE_ID", tradeId);
			IDataset userModerms=this.getUserModermInfoByLease(getParam);
			//应注意，光猫申领保存时候已经扣押金，而不是在外部掉接口的时候才扣，因此无论是否存在串号都要返还
			if(userModerms!=null && userModerms.size()>0){
				IData endParams=new DataMap();
   				String userId=userModerms.getData(0).getString("USER_ID");
   				String modermNum=userModerms.getData(0).getString("RSRV_STR1");//串号
   				IData param=new DataMap();
   				param.put("SERIAL_NUMBER", serialNumber);//PARA_VALUE1
   				param.put("TRADE_ID", tradeId);//台账流水
   				param.put("RES_NO", modermNum);//终端串号 
   				param.put("CUST_ID", custId);//客户名称
   				param.put("UPDATE_STAFF_ID", user.getString("UPDATE_STAFF_ID"));//销售员工 
   				boolean rtnFlag=this.returnFtthModem(param); 
   				rtnFlagset.add(rtnFlag);
   				IData updData=new DataMap();
   				if(rtnFlag){ 
   					updData.put("TRADE_ID", tradeId);
   					updData.put("DEAL_TAG", "1");
   					this.updateFtthTempTrade(updData); 
   				}else{
   					updData.put("TRADE_ID", tradeId);
   					updData.put("DEAL_TAG", "9");
   					this.updateFtthTempTrade(updData);
   				}
			}    
    	}    	
    	return rtnFlagset;
	}
	
	/**
	 * 获取开户时间小于2016-6-28 00:00:00的用户
	 * @return
	 */
	public static IDataset getTradeMainInfoByTradeId(String tradeId) throws Exception{
		IData param = new DataMap();
		param.put("TRADE_ID", tradeId);//开户台账流水
		return Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_FTTH_CREATE_DATE", param);
	}
	 
    /**
		 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
		 * 退还光猫
		 * */ 
    public static boolean returnFtthModem(IData inparam) throws Exception{
    	boolean rtnFlag=true;
    	FTTHBusiModemManageBean bean= BeanManager.createBean(FTTHBusiModemManageBean.class);
    	
    	String custId=inparam.getString("CUST_ID","");
    	IDataset custInfo=bean.getCustInfoByCustid(custId);
		if(custInfo!=null && custInfo.size()>0){
			String custName=custInfo.getData(0).getString("CUST_NAME");
			inparam.put("CUST_NAME", custName);
		}
		return bean.returnFtthModem(inparam); 
    }
    /**
	 * REQ201512160017 光猫押金释放后下发告知短信的开发需求
	 * 退押金成功下发短信：code_code where sql_ref=INSERT_TI_O_SMS
	 * 20151218 chenxy3
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
           smsData.put("EPARCHY_CODE",CSBizBean.getUserEparchyCode());
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
    
    public static IDataset getResNoByOhter(String resNo) throws Exception{
    	IData param = new DataMap();
    	param.put("RSRV_STR1", resNo);
    	return Dao.qryByCode("TF_F_USER_OTHER", "SEL_FTTH_RESNOINFO_BY_OTHER", param);
    }
   
}
