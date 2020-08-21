
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage;

 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeBhQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
 * chenxy3
 * 2015-12-8 
 * */
public class FTTHBusiModemManageBean extends CSBizBean
{
	private static transient Logger logger = Logger.getLogger(FTTHBusiModemManageBean.class);
	/**
	 * 判断集团产品编码（即办理业务的号码）的product_id 是否 （7341 集团商务宽带产品） ，
	 * 如果不是不能办理
	 * */
	public static IDataset checkFTTHBusi(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));
        param.put("PRODUCT_ID", inParam.getString("PRODUCT_ID","7341"));
        IDataset userProd = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_FTTHBUSI_SN_PRODUCTID", param);
        return userProd;
    } 
	
	/**
	 * 判断录入的宽带号码是否存在TRADE表未完工记录 
	 * */
	public static IDataset checkKDNumInTrade(IData inParam) throws Exception
    {
	    IDataset resultDataset = null;
	    
	    IData param = new DataMap();
        param.put("KD_NUMBER", inParam.getString("KD_NUMBER")); 
        param.put("CUSTID_GROUP", inParam.getString("CUSTID_GROUP")); 
        IDataset kdTrade = Dao.qryByCode("TF_B_TRADE", "SEL_TRADE_BY_KD_NUMBER1", param, Route.getJourDb(getTradeEparchyCode()));
        
        if (IDataUtil.isNotEmpty(resultDataset))
        {
            String userId = kdTrade.getData(0).getString("USER_ID");
            
            param = new DataMap();
            param.put("USER_ID", userId);
            
            IDataset modemInfos = checkApplyModem(param);
            
            //没有申领过光猫才返回
            if (IDataUtil.isEmpty(modemInfos))
            {
                resultDataset = kdTrade;
            }
        }
        
        return kdTrade;
    } 
	
	/**
	 * 判断录入的宽带号码是否存在光猫信息
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkKDNumInOtherTrade(IData inParam) throws Exception
	{
		IData param = new DataMap();
        param.put("KD_NUMBER", inParam.getString("KD_NUMBER")); 
        param.put("CUSTID_GROUP", inParam.getString("CUSTID_GROUP")); 
        param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
        IDataset kdTrade = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_MODEM_BY_KD_NUMBER", param);
        
        if (IDataUtil.isEmpty(kdTrade))
        {
            kdTrade = Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_TRADE_OTHER_MODEM_BY_KD_NUMBER", param, Route.getJourDb(getTradeEparchyCode()));
        }
        
        return kdTrade;
	}
	
	/**
	 * 判断录入的宽带号码是否已经申领过光猫
	 * 含TRADE表关联。
	 * */
	public static IDataset checkKDNumInOther(IData inParam) throws Exception
    {
		IData param = new DataMap();
		
		IDataset resultDataset = null;
		
		param.put("SERIAL_NUMBER", inParam.getString("KD_NUMBER")); 
        param.put("CANCEL_TAG", "0");
        param.put("TRADE_TYPE_CODE", "613");
        
        IDataset mainTrade = TradeInfoQry.queryTradeBySnAndTypeCode(param);
        
        if (IDataUtil.isNotEmpty(mainTrade))
        {
            param.clear();
            param.put("KD_NUMBER", inParam.getString("KD_NUMBER")); 
            param.put("USER_ID", mainTrade.getData(0).getString("USER_ID")); 
            
            resultDataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_BY_KD_NUMBER", param);
            
        }
        
        return resultDataset;
    } 
	
	/**
	 * 判断录入的宽带号码是否已经申领过光猫
	 * 单独查OTHER表。
	 * */
	public static IDataset checkApplyModem(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));  
        IDataset otherInfos = Dao.qryByCode("TF_F_USER_OTHER", "SEL_OTHER_FTTH_BUSI_BY_USERID1", param);
        return otherInfos;
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
     * 更新申领光猫串号
     * RSRV_STR1
     * 入参：USER_ID RES_NO
     * */
	public static int updFtthBusiResNO(IData inParam) throws Exception
    {
		IData param = new DataMap();  
        param.put("RES_NO", inParam.getString("RES_NO"));  
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));  
        param.put("RES_KIND_CODE", inParam.getString("RES_KIND_CODE")); 
        param.put("UPDATE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", inParam.getString("TRADE_DEPART_ID"));
        int updResult=Dao.executeUpdateByCodeCode("TF_B_TRADE_OTHER", "UPD_TRADE_FTTH_BUSI_RESID1", param, Route.getJourDb(getTradeEparchyCode()));
        return updResult;
    } 
	
	/**
     * 更新申领光猫串号
     * RSRV_STR1
     * 入参：USER_ID RES_NO
     * */
	public static int updOldFtthBusiResNO(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("USER_ID", inParam.getString("USER_ID"));  
        param.put("RES_NO", inParam.getString("RES_NO")); 
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));  
        param.put("RES_KIND_CODE", inParam.getString("RES_KIND_CODE")); 
        param.put("UPDATE_STAFF_ID", inParam.getString("TRADE_STAFF_ID"));
        param.put("UPDATE_DEPART_ID", inParam.getString("TRADE_DEPART_ID"));
        int updResult=Dao.executeUpdateByCodeCode("TF_F_USER_OTHER", "UPD_FTTH_BUSI_RESID_OLD", param);
        return updResult;
    } 
	
	public static IDataset queryBusiModemInfo(IData inParam) throws Exception
	{
		IData param = new DataMap();
		String serial_number = inParam.getString("SERIAL_NUMBER");
        param.put("SERIAL_NUMBER",serial_number);
        param.put("RSRV_STR1", inParam.getString("MODERM_ID",""));
        String number = inParam.getString("KD_NUMBER","").trim();
        //判断是否拆机
        String operType = inParam.getString("OPER_TYPE");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(number);
        if("2".equals(operType)){
        	if(IDataUtil.isEmpty(userInfo)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"该用户宽带已拆机或未办理宽带，不能办理光猫更换业务！");
        	}else{
        		String userId = userInfo.getString("USER_ID");
        		param.put("USER_ID1",userId);
        		IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("605", userId, "0");
    			if(DataSetUtils.isNotBlank(outDataset)){//有未完工拆机业务
    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"您有未完工拆机业务不能再次办理光猫更换!");
    			}
    			IDataset destorySpecDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("615", userId, "0");
    			if(DataSetUtils.isNotBlank(destorySpecDataset)){//有未完工特殊拆机业务
    				CSAppException.apperr(CrmCommException.CRM_COMM_103,"您有未完工特殊拆机业务不能再次办理光猫更换!");
    			}
        	}
        	
        	IData userInfo2 = UcaInfoQry.qryUserInfoBySn(serial_number);
            if(IDataUtil.isNotEmpty(userInfo2)){
                String userId = userInfo2.getString("USER_ID");
                param.put("USER_ID2",userId);
            }
        	
        	param.put("KD_NUMBER",number);
            //先查询该宽带号码是否有正在申领、更换、退还、丢失业务的工单
            IDataset tradeModem = Dao.qryByCode("TF_B_TRADE","SEL_TRADE_OTHER_MODEM", param, Route.getJourDb(getTradeEparchyCode()));
            if(DataSetUtils.isBlank(tradeModem)){
            	IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_BUSIMODERM_BY_STR3", param);
                return userModerms;
            }else{
            	CSAppException.appError("71321", "该宽带号码存在未完工光猫管理工单["+tradeModem.first().getString("TRADE_ID")+"],不能做光猫管理业务！");
            	return new DatasetList();
            }
        }
		if(StringUtils.equals("3", operType) || StringUtils.equals("4", operType)){
			String userId = "";
    		if(IDataUtil.isEmpty(userInfo)){
    			IDataset wideUserInfo = FTTHBusiModemManageBean.qryUserInfoBySn(number);
        		if(DataSetUtils.isBlank(wideUserInfo)){
        			CSAppException.apperr(CrmCommException.CRM_COMM_103,"查询不到用户数据!");
        		}else{
        			userId = wideUserInfo.getData(0).getString("USER_ID");
        		}
    		}else{
    			userId = userInfo.getString("USER_ID");
    		}
    		String errInfos = StringUtils.equals("3", operType) ? "退还" : "丢失" ;
			IDataset outDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("605", userId, "0");
			if(DataSetUtils.isNotBlank(outDataset)){//有未完工拆机业务
				if(StringUtils.equals("1", outDataset.getData(0).getString("RSRV_STR1"))){//有未完工拆机业务并且已退还光猫
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"您在办理拆机业务时已退还光猫，不能再次办理"+errInfos+"业务!");
				}
			}
			IDataset destorySpecDataset = TradeInfoQry.getTradeInfosBySelTradeByUserIdCode("615", userId, "0");
			if(DataSetUtils.isNotBlank(destorySpecDataset)){//有未完工特殊拆机业务
				if(StringUtils.equals("1", destorySpecDataset.getData(0).getString("RSRV_STR1"))){//有未完工拆机业务并且已退还光猫
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"您在办理特殊拆机业务时已退还光猫，不能再次办理"+errInfos+"业务!");
				}
			}
		}
        param.put("KD_NUMBER",number);
        //先查询该宽带号码是否有正在申领、更换、退还、丢失业务的工单
        IDataset tradeModem = Dao.qryByCode("TF_B_TRADE","SEL_TRADE_OTHER_MODEM", param,Route.getJourDb(getTradeEparchyCode()));
        if(DataSetUtils.isBlank(tradeModem)){
        	IDataset userModerms = Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_BUSIMODERM", param);
            return userModerms;
        }else{
        	CSAppException.appError("71321", "该宽带号码存在未完工光猫管理工单["+tradeModem.first().getString("TRADE_ID")+"],不能做光猫管理业务！");
        	return new DatasetList();
        }
	}
	
	public static IDataset queryBusiModemSupplementInfo(IData inParam) throws Exception
	{
		IData param = new DataMap();
        param.put("SERIAL_NUMBER",inParam.getString("SERIAL_NUMBER"));
        param.put("KD_NUMBER",inParam.getString("KD_NUMBER",""));
        return Dao.qryByCode("TF_F_USER_OTHER", "SEL_USER_OTHER_SUPPLEMENT_BUSIMODEM", param);
	}
	/**
	 * FTTH_GROUP 获取“CRM > 个人业务 > 宽带业务 > FTTH宽带业务 > FTTH宽带用户开户”开户失败的用户 
	 * 流程：
	 * 1、取TF_BH_TRADE表A状态的记录。
	 * 2、取TF_BH_TRADE_FTTH临时表标记4的记录。
	 * 3、查看OTHER表如果存在光猫串号，就调用华为接口退光猫
	 * 4、退光猫成功：A 更新other表的end_date字段，终止它。B 如果存在临时表记录，状态置为1
	 * 5、退光猫失败：A 如果是TRADE查出来的，插入FTTH临时表，等下次再跑。 B 如果是临时表查的，那就不做更新，等下次还跑。
	 * */
    public void returnResByWilenFailUser(IData inParam) throws Exception
    {
    	IDataset users=this.getTradeFtthFail(inParam);
    	if(users!=null && users.size()>0){
	   		for(int i=0;i<users.size();i++){
	   			IData user=users.getData(i); 
	   			String tradeId=user.getString("TRADE_ID");
	   			IDataset userModerms = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,"FTTH_GROUP");
	   			if(DataSetUtils.isNotBlank(userModerms)){
	   				String userId=userModerms.getData(0).getString("USER_ID");
	   				String modermNum=userModerms.getData(0).getString("RSRV_STR1");
	   				try{ 
			   			//集团的只退光猫 
			   			if(modermNum!=null && !"".equals(modermNum) && "1".equals(userModerms.getData(0).getString("RSRV_TAG2"))){
			   				String custId=user.getString("CUST_ID");
				   			IDataset custInfo=this.getCustInfoByCustid(custId);
				   			if(custInfo!=null && custInfo.size()>0){
				   				String custName=custInfo.getData(0).getString("CUST_NAME");
				   				user.put("CUST_NAME", custName);
				   			}
				   			user.put("RES_NO", modermNum);
			   				boolean rtnFlag=this.returnFtthModem(user);
			   				if(rtnFlag){
			   					IData updData=new DataMap();
			   					updData.put("USER_ID", userId);
			   					updData.put("TRADE_ID", tradeId);
				   				//退还成功 1、更新OTHER表
			   					this.updFtthFailTradeEnd(updData); 
				   			}
			   			}
		   			}catch(Exception e){
		   				IData updData=new DataMap();
	   					updData.put("RESULT_INFO", "SS.FTTHBusiModemManageSVC.returnResByWilenFailUser执行错误。");
	   					updData.put("TRADE_ID", tradeId);
	   					updData.put("CALL_INTEFACE", "");
		   				//退还失败 1、更新OTHER表
	   					this.updFtthFailTradeCallFalse(updData); 
		   			}
	   			}
	   			
	   		}
	   	} 
    }
    
    /**
     * 调用华为接口，退还光猫
     * */
	public static boolean returnFtthModem(IData inParam) throws Exception{
		Boolean rtnFlag=true;
		IData param = new DataMap();
    	param.put("RES_NO", inParam.getString("RES_NO"));//终端串号
//    	param.put("REMARK", "");//备注
    	param.put("PARA_VALUE1", inParam.getString("SERIAL_NUMBER"));//PARA_VALUE1
    	param.put("SALE_FEE", 0);//销售费用 非销售时为0
//    	param.put("PARA_VALUE3", "");//活动标识
//    	param.put("PARA_VALUE4", "");//用户编码
//    	param.put("PARA_VALUE5", "");//返销时间
    	param.put("PARA_VALUE7", 0);//代办费
//    	param.put("DEVICE_COST", "");//进货价格
//    	param.put("PARA_VALUE8", "");//销售政策
    	param.put("TRADE_ID", SeqMgr.getTradeIdFromDb());//台账流水
    	param.put("X_CHOICE_TAG", "1");//0-终端销售,1—终端销售退货
    	param.put("ES_TYPE_CODE", "4");//资源类型,终端的传入4
    	param.put("CONTRACT_ID", inParam.getString("TRADE_ID"));//销售订单号
    	param.put("PRODUCT_MODE", "0");//全网统一操盘合约机销售标志
    	param.put("X_RES_NO_S", inParam.getString("RES_NO"));//终端串号
    	param.put("X_RES_NO_E", inParam.getString("RES_NO"));//终端串号
    	param.put("PARA_VALUE13", "0");//是否有销售酬金  0-没有 1-有
    	param.put("PARA_VALUE14", "0");//裸机价格
    	param.put("PARA_VALUE15", "0");//客户购机折让价格
    	param.put("PARA_VALUE16", "0");//客户预存话费
    	param.put("PARA_VALUE17", "0");//客户实际购机款
    	param.put("PARA_VALUE18", "0");//客户实缴费用总额
    	param.put("PARA_VALUE9", "03");//客户捆绑合约类型
    	param.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));//客户号码
    	param.put("PARA_VALUE1", inParam.getString("SERIAL_NUMBER"));//客户号码 
    	param.put("USER_NAME", new String(inParam.getString("CUST_NAME").getBytes("UTF-8"),"GBK"));//客户名称
    	param.put("STAFF_ID", inParam.getString("UPDATE_STAFF_ID"));//销售员工
    		
    	IDataset results = HwTerminalCall.returnTopSetBoxTerminal(param); 
    	if(!"0".equals(results.getData(0).getString("X_RESULTCODE")))
    	{
    		rtnFlag=false; 
    	    if (logger.isDebugEnabled())
            {
                 logger.error("FTTH宽带装机失败，光猫退机失败！rtnset:"+results);
            }
    	}else{
    		rtnFlag=true;
            if (logger.isDebugEnabled())
            {
               logger.error("FTTH宽带装机失败，光猫退机成功！rtnset:"+results);
            }
        } 
    	return rtnFlag;
	}
	
	/**
	 * 获取装机失败TRADE表A状态的用户信息
	 * 查TF_BH_TRADE。
	 * 用于处理装机失败退光猫的信息。
	 * */
	public static IDataset getTradeFtthFail(IData inparams) throws Exception
    {
		 SQLParser parser = new SQLParser(inparams);
	        parser.addSQL(" select t.*  ");
	        parser.addSQL(" from tf_bh_trade t ,tf_b_trade_other a");
	        parser.addSQL(" where t.subscribe_STATE='A' ");
	        parser.addSQL(" AND T.TRADE_TYPE_CODE='600' "); 
	        parser.addSQL(" AND T.ACCEPT_DATE>=trunc(sysdate,'dd') "); 
	        parser.addSQL(" AND T.ACCEPT_DATE<trunc(sysdate,'dd')+1 "); 
	        parser.addSQL(" AND T.ACCEPT_MONTH=TO_NUMBER(TO_CHAR(SYSDATE,'MM')) "); 
	        parser.addSQL(" AND T.RSRV_STR2='0' "); 
	        parser.addSQL(" AND T.TRADE_ID=a.trade_id "); 
	        parser.addSQL(" AND a.rsrv_value_code = 'FTTH_GROUP' ");
	        parser.addSQL(" AND a.rsrv_tag1 = '0' ");
	        parser.addSQL(" AND a.rsrv_tag2 = '1' ");
	        parser.addSQL(" AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE ");
	        return Dao.qryByParse(parser, Route.getJourDb(getTradeEparchyCode()));
    } 
	
	/**
	 * 获取TF_BH_TRADE_FTTH临时表数据
	 * 用于一起处理装机失败退光猫的信息。
	 * */
	public static IDataset getTempTabFtthFail() throws Exception
    {
//		IData param = new DataMap();
//        IDataset otherInfos = Dao.qryByCode("TF_BH_TRADE", "SEL_FTTH_BUSI_TRADE_FAIL_TEMPTAB1", param);
	    IDataset resuleDataset = new DatasetList();
	    
        IDataset bhTradeFtthInfos = qryTfBhTradeFtthInfo();
        
        if (IDataUtil.isNotEmpty(bhTradeFtthInfos))
        {
            String tradeId = "";
            
            for (int i = 0; i < bhTradeFtthInfos.size(); i++)
            {
                tradeId = bhTradeFtthInfos.getData(i).getString("TRADE_ID");
 
                if (StringUtils.isNotEmpty(tradeId))
                {
                    IDataset resultDataTemps = TradeBhQry.queryBhTradeInfoByTradeIdAndTradeCode(tradeId, "600", "A");
                    
                    if (IDataUtil.isNotEmpty(resultDataTemps))
                    {
                        IData resultDataTemp = resultDataTemps.getData(0);
                        
                        IDataset userOtherInfos = UserOtherInfoQry.getUserOther(resultDataTemp.getString("USER_ID"), "FTTH_GROUP");
                        
                        if (IDataUtil.isNotEmpty(userOtherInfos))
                        {
                            if ("1".equals(userOtherInfos.getData(0).getString("RSRV_TAG2")))
                            {
                                resultDataTemp.put("RSRV_STR1", userOtherInfos.getData(0).getString("RSRV_STR1"));
                                resultDataTemp.put("UPDATE_STAFF_ID", userOtherInfos.getData(0).getString("UPDATE_STAFF_ID"));
                                resultDataTemp.put("OTHER_TRADE_ID", userOtherInfos.getData(0).getString("TRADE_ID"));
                                
                                resuleDataset.add(resultDataTemp);
                            }
                        }
                        
                    }
                } 
            }
        }
        
        return resuleDataset;
        
    } 
	
	
	/**
     * 获取TF_BH_TRADE_FTTH临时表数据
     * 
     * */
    public static IDataset qryTfBhTradeFtthInfo() throws Exception
    {
        IData param = new DataMap();
        
        param.put("DEAL_TAG", "4");
        
        IDataset bhTradeFtthInfos = Dao.qryByCode("TF_BH_TRADE_FTTH", "SEL_TF_BH_TRADE_FTTH_DEAL_TAG", param);
        return bhTradeFtthInfos;
    } 
	
	/**
     * 装机失败终止光猫OTHER表记录
     * */
	public static void updFtthFailTradeEnd(IData inParam) throws Exception
    {
		IData param = new DataMap();
    	param.put("TRADE_ID", inParam.getString("TRADE_ID")); 
    	param.put("RESULT_INFO", inParam.getString("RESULT_INFO"));
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_B_TRADE_OTHER t ");
    	sql.append(" set t.end_date=sysdate ,t.UPDATE_TIME=sysdate,t.rsrv_tag2='3',t.rsrv_str4=:RESULT_INFO");
    	sql.append(" where  t.rsrv_value_code = 'FTTH_GROUP' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.rsrv_tag2 = '1' ");//光猫状态为 1申领
    	sql.append(" and t.rsrv_tag1 = '0' ");//光猫状态为 0租赁
    	sql.append(" and t.trade_id=:TRADE_ID ");
        Dao.executeUpdate(sql, param, Route.getJourDb(getTradeEparchyCode()));
    } 
	
	/**
     * 装机失败终止光猫OTHER表记录
     * */
	public static void updFtthFailTradeCallFalse(IData inParam) throws Exception
    {
		IData param = new DataMap();
    	param.put("TRADE_ID", inParam.getString("TRADE_ID")); 
    	param.put("CALL_INTEFACE", inParam.getString("CALL_INTEFACE"));
    	param.put("RESULT_INFO", inParam.getString("RESULT_INFO"));
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_B_TRADE_OTHER t ");
    	sql.append(" set t.rsrv_str3=:CALL_INTEFACE,t.rsrv_str4=:RESULT_INFO");
    	sql.append(" where  t.rsrv_value_code = 'FTTH_GROUP' "); 
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.trade_id=:TRADE_ID ");
        Dao.executeUpdate(sql, param, Route.getJourDb(getTradeEparchyCode()));
    } 
	
	
	/**
     * 装机失败--对于存在TRADEID的终止TF_BH_TRADE_FTTH临时表记录
     * */
	public static int updFtthFailTempTab(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));  
        param.put("DEAL_TAG", inParam.getString("DEAL_TAG"));  
        int updResult=Dao.executeUpdateByCodeCode("TF_BH_TRADE_FTTH", "UPD_FTTH_BUSI_TEMPTAB", param);
        return updResult;
    } 
	
	/**
     * 装机失败--对于存在TRADEID的终止TF_BH_TRADE_FTTH临时表记录
     * */
	public static int insFtthFailTempTab(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("TRADE_ID", inParam.getString("TRADE_ID"));  
        param.put("DEAL_TAG", inParam.getString("DEAL_TAG"));  
        int updResult=Dao.executeUpdateByCodeCode("TF_BH_TRADE_FTTH", "INS_FTTH_BUSI_TEMPTAB", param);
        return updResult;
    } 
	
	/**
	 * 取cust_name
	 * */
	public static IDataset getCustInfoByCustid(String custId) throws Exception
    {

        IData param = new DataMap();
        param.put("CUST_ID", custId);  
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* FROM TF_F_CUSTOMER T  WHERE  T.CUST_ID=:CUST_ID ");

        return Dao.qryByParse(parser);
    }
	
	/**
	 * 获取用户有效的FTTH以及没有有效光猫终端的记录
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset getWidenetAndNotFTTHModem(IData inParam) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", inParam.getString("KD_NUMBER"));
		return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_WIDENET_BY_NOT_FTTHMODEM", param);
	}
	
	/**
	 * 根据服务号码查询用户信息
	 * @param inParam
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryUserInfoBySn(String serialNumber) throws Exception
	{
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		SQLParser parser = new SQLParser(param);     
		parser.addSQL("select * from TF_F_USER where serial_number = :SERIAL_NUMBER order by destroy_time desc");
		return Dao.qryByParse(parser);
	}
	

}
