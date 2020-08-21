package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxmanage;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

public class NoPhoneTopSetBoxManageBean extends CSBizBean{
	 private static Logger logger = Logger.getLogger(NoPhoneTopSetBoxManageBean.class);
	 
	 /**
	    * 获取魔百和业务取消3个月未归还机顶盒
	    * 做押金沉淀处理
	    * @param inParam
	    * @throws Exception
	    */
	   public void checkThreeMonthNotReturnTopSetBoxUser(IData inParam) throws Exception{
		   IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
		    String serialNumber = dealParam.getString("SERIAL_NUMBER");
			String deposit= dealParam.getString("RSRV_NUM2");//押金
			String instId = dealParam.getString("INST_ID");
			String userId = dealParam.getString("USER_ID");
			int fee=Integer.parseInt(deposit);
			String money = String.valueOf(fee*100);
			try{		
   				//3、获取默认账户  （acct_id)
				IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
		    	String acctId=accts.first().getString("ACCT_ID");
		    	IData params=new DataMap(); 
				params.put("ACCT_ID", acctId);
				params.put("CHANNEL_ID", "15000");
				params.put("PAYMENT_ID", "100021");
				params.put("PAY_FEE_MODE_CODE", "0");
				params.put("REMARK", "无手机魔百和业务取消3个月未归还机顶盒，押金进行沉淀！");
				IData depositeInfo=new DataMap();
				depositeInfo.put("DEPOSIT_CODE", "9016");
				depositeInfo.put("TRANS_FEE", money);
				
				IDataset depositeInfos=new DatasetList();
				depositeInfos.add(depositeInfo);
				params.put("DEPOSIT_INFOS", depositeInfos);
		        CSBizBean.getVisit().setStaffEparchyCode("0898");
		        
		   		//调用接口，将【押金】——>【沉淀】
				IData inAcct =AcctCall.foregiftDeposite(params);
				String callRtnType=inAcct.getString("X_RESULTCODE","");
				if(!"".equals(callRtnType)&&"0".equals(callRtnType)){
					params.put("INST_ID", instId);
					params.put("USER_ID", userId);
   					//if接口调用成功，清空押金 	终止用户机顶盒记录		
   					this.updateUserResTopSetBoxStatus(params); 
				}else{
					params.put("INST_ID", instId);
					String rtnInfo=inAcct.getString("X_RESULTINFO","");
					if(!"".equals(rtnInfo) && rtnInfo.length()>98){
						rtnInfo=rtnInfo.substring(0,98);
					}
					params.put("RESULT_INFO", rtnInfo);
					params.put("CALL_INTEFACE", "AM_CRM_GMFeeDeposit");
					this.updateUserResCallFalse(params);
					CSAppException.apperr(CrmCommException.CRM_COMM_103,params.toString());
				} 
			}catch(Exception e){
				IData errParam=new DataMap();
				errParam.put("USER_ID", userId);
				errParam.put("INST_ID", instId);
				errParam.put("CALL_INTEFACE", "SS.NoPhoneTopSetBoxManageSVC.checkThreeMonthNotReturnTopSetBoxUser错误。");
				errParam.put("RESULT_INFO", e.toString());
				this.updateUserResCallFalse(errParam);
				CSAppException.apperr(CrmCommException.CRM_COMM_103,errParam.toString());
			} 
		    
	   }

	   /**
		 * 2、获取申请魔百和已满3年的用户
		 * 根据用户的TF_F_USER_RES表，判断用户（手机号码）是否申领了光猫；
		 * 如果是申领了光猫则调账务接口，将宽带光猫押金存折里的钱转到现金存折；
		 * 结束用户的TF_F_USER_REF资料 
		 * */
	   public void checkThreeYearsTopSetBoxUser(IData inParam) throws Exception
	   {
		   	IData dealParam = new DataMap(inParam.getString("DEAL_COND"));
			String serialNumber=dealParam.getString("SERIAL_NUMBER");
			String userId=dealParam.getString("USER_ID");
			String deposit=dealParam.getString("RSRV_NUM2");//押金
			String instId = dealParam.getString("INST_ID");
			int fee=Integer.parseInt(deposit);
			if(fee > 0){
				String money = String.valueOf(fee*100);
				try{
	   				//3、获取默认账户  （acct_id)
	    			IData params=new DataMap(); 
	    			params.put("SERIAL_NUMBER", serialNumber);
					params.put("OUTER_TRADE_ID", "");
					params.put("DEPOSIT_CODE_OUT", "9016");
					params.put("TRADE_FEE", money);
					params.put("CHANNEL_ID", "15000");
					params.put("UPDATE_DEPART_ID", "36601");
			   		params.put("UPDATE_STAFF_ID", "SUPERUSR"); 
					params.put("TRADE_DEPART_ID", "36601");
			   		params.put("TRADE_STAFF_ID", "SUPERUSR"); 
			   		params.put("TRADE_CITY_CODE", "HNSJ");
			   		params.put("SUB_SYS", "RESSERV_TF_RH_SALE_DEAL");
			        CSBizBean.getVisit().setStaffEparchyCode("0898");
			        
			   		//调用接口，将【押金】——>【现金】
			        //无手机宽带魔百和业务调用退费接口
                    IDataset inAccts = AcctCall.backFee(userId, inParam.getString("TRADE_ID"), "15000", "9016", "16001", money);
                    IData inAcct = inAccts.first();
					String callRtnType=inAcct.getString("RESULT_CODE","");//0-成功 1-失败
					if(!"".equals(callRtnType)&&"0".equals(callRtnType)){					
						params.put("INST_ID", instId);
						params.put("USER_ID", userId);
	   					//if接口调用成功，清空押金 	终止用户机顶盒记录		
	   					this.updateUserResDeposit(params);
					}else{
						params.put("INST_ID", instId);
						String rtnInfo=inAcct.getString("RESULT_INFO","");
						if(!"".equals(rtnInfo) && rtnInfo.length()>98){
							rtnInfo=rtnInfo.substring(0,98);
						}
						params.put("RESULT_INFO", rtnInfo);
						params.put("CALL_INTEFACE", "AM_CRM_TransFee");
						this.updateUserResCallFalse(params);
						CSAppException.apperr(CrmCommException.CRM_COMM_103,params.toString());
					} 
				}catch(Exception e){
					IData errParam=new DataMap();
					errParam.put("USER_ID", userId);
					errParam.put("INST_ID", instId);
					errParam.put("RESULT_INFO", "SS.NoPhoneTopSetBoxManageSVC.checkThreeYearsTopSetBoxUser错误。");
					this.updateUserResCallFalse(errParam);
					errParam.put("CALL_INTEFACE", e.toString());
					CSAppException.apperr(CrmCommException.CRM_COMM_103,errParam.toString());
				}
			}
	   }   
	   
   /**
     * 获取申请魔百和机顶盒已满3年的用户
     * SEL_TOPSETBOX_FULL3YEAR
     */
    public static IDataset getThreeYearsUser(IData inparams) throws Exception
    {  
        IData param = new DataMap(); 
        IDataset users = Dao.qryByCode("TF_F_USER_RES", "SEL_TOPSETBOX_FULL3YEAR", param);
        return users;
    }
    
    /**
     * 更新调用接口失败信息  *  
     * */
	public static void updateUserResCallFalse(IData params) throws Exception
	{
		IData param = new DataMap();
    	param.put("USER_ID", params.getString("USER_ID")); 
    	param.put("INST_ID", params.getString("INST_ID"));
    	
    	StringBuilder errInfo = new StringBuilder(1000);
    	errInfo.append(params.getString("CALL_INTEFACE"));
    	errInfo.append(",");
    	errInfo.append(params.getString("RESULT_INFO"));
    	String err_info = errInfo.toString();
    	if(!"".equals(err_info) && err_info.length()>95){
    		err_info=err_info.substring(0,95);
		}
    	param.put("ERR_INFO", err_info);
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" update TF_F_USER_RES t ");
    	sql.append(" set t.remark = :ERR_INFO ");
    	sql.append(" where  t.rsrv_tag1 = 'J' "); 
    	sql.append(" and t.res_type_code= '4' ");
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.inst_id=:INST_ID ");
        Dao.executeUpdate(sql, param);
		
	}

	/**
	 * 接口调用成功，修改机顶盒结束时间,终止用户机顶盒记录,清空押金 
	 * @param inParams
	 * @throws Exception
	 */
	public static void updateUserResTopSetBoxStatus(IData inParams) throws Exception
	{
		IData param = new DataMap();
    	param.put("USER_ID", inParams.getString("USER_ID")); 
    	param.put("INST_ID", inParams.getString("INST_ID"));
    	StringBuilder sql = new StringBuilder(1000);
    	sql.append(" update TF_F_USER_RES t ");
    	sql.append(" set t.rsrv_num2 = '0',t.end_date=sysdate,t.UPDATE_TIME=sysdate,t.remark='无手机魔百和业务取消满3个月未归还机顶盒押金沉淀'");
    	sql.append(" where  t.rsrv_tag1 = 'J' "); 
    	sql.append(" and t.res_type_code= '4' ");
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.inst_id=:INST_ID ");
        Dao.executeUpdate(sql, param);
		
	}
	
	/**
	 * 押金退还成功，更新机顶盒信息
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static void updateUserResDeposit(IData inParams) throws Exception
	{
		IData param = new DataMap();
    	param.put("USER_ID", inParams.getString("USER_ID")); 
    	param.put("INST_ID", inParams.getString("INST_ID"));
    	StringBuilder sql = new StringBuilder(1000);
    	sql.append(" update TF_F_USER_RES t ");
    	sql.append(" set t.rsrv_num2 = '0',t.UPDATE_TIME=sysdate,t.remark='无手机魔百和机顶盒申领满三年，退还押金'");
    	sql.append(" where  t.rsrv_tag1 = 'J' "); 
    	sql.append(" and t.res_type_code= '4' ");
    	sql.append(" and sysdate between t.start_date and t.end_date ");
    	sql.append(" and t.user_id=:USER_ID ");
    	sql.append(" and t.inst_id=:INST_ID ");
        Dao.executeUpdate(sql, param);
	}
	
	/**
	 * 获取有效机顶盒信息
	 * @return
	 * @throws Exception
	 */
	public static IDataset getTopSetBoxInfos() throws Exception
	{
		IData param = new DataMap();
		IDataset users = Dao.qryByCode("TF_F_USER_RES", "SEL_TOPSETBOX_BY_DATE", param);
		return users;
	}
	
	/**
	 * 获取用户魔百和业务信息
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static IDataset getPalySvcInfos(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		IDataset users = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_TOPSETBOX_BY_DATE", param);
		return users;
	}
	
	/**
	 * 根据USER_ID获得正常用户的SERIAL_NUMBER
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static String getSerialNumberByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		SQLParser parser = new SQLParser(param);     
		parser.addSQL("select serial_number from TF_F_USER where remove_tag='0' and user_id = :USER_ID");
		return Dao.qryByParse(parser).getData(0).getString("SERIAL_NUMBER");
	}
	
	  /**
     * @Function: qryAllSetTopBoxByUserIdAndTag1()
     * @Description: 查询用户失效机顶盒信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: zhengkai5
     * @date: 2017-9-14 
     *    
     */
    public static IDataset qryAllSetTopBoxByUserIdAndTag1(String userId, String resTypeCode, String rsrvTag1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RSRV_TAG1", rsrvTag1); // J:机顶盒
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_UID_RESTYPE_INVALID", param);
    }
    
    /**
     * @Function: qrySetTopBoxByUserIdAndTag1()
     * @Description: 查询用户机顶盒信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: zhengkai5
     * @date: 2017-9-10 
     *   
     */
    public static IDataset qrySetTopBoxByUserIdAndTag1AllColumns(String userId, String resTypeCode, String rsrvTag1) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RSRV_TAG1", rsrvTag1); // J:机顶盒
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_UID_RESTYPE_ALL_COLUMNS", param);
    }
    
    /**
     * 获取宽带用户信息
     * @param wSerialNumber
     * @return
     * @throws Exception
     */
    public static IDataset getUserWidenetInfoBySerialNumber(String serialNumber) throws Exception
    {
    	IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_WIDENET", "SEL_BY_WIDENET_ALL_COLUMNS_BY_SERIALNUMBER", param);
    }
}
