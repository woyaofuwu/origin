package com.asiainfo.veris.crm.order.soa.person.busi.terminalbind;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.consts.ResTypeEnum;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UDepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;


public class TerminalBindBean extends CSBizBean {

	static Logger logger=Logger.getLogger(TerminalBindBean.class);
	
	
	 /**
	 *  
	 * */
	public static IData checkOpenDate(IData inParam ) throws Exception
    { 
		 
//		select  TO_CHAR( t.open_date, 'YYYY-MM-DD HH:MM:SS')  from tf_f_user t where t.remove_tag=0 and t.serial_number=:SERIAL_NUMBER ;
//		String sql="select  TO_CHAR( t.open_date, 'YYYY-MM-DD HH:MM:SS')  from tf_f_user t where t.remove_tag=0 and t.serial_number=:SERIAL_NUMBER";
		
		IData result =new DataMap();
		
		String sql="select TO_CHAR( t.open_date, 'YYYY-MM-DD hh24:mi:ss') OPEN_DATE from tf_f_user t where t.remove_tag=0 and t.serial_number=:SERIAL_NUMBER";
		
		
		IDataset res = Dao.qryBySql(new StringBuilder(sql), inParam); 
		String open_date = "" ; 
		if(IDataUtil.isNotEmpty(res) && res.size()>0){
			IData idata =  (IData)res.get(0); 
			open_date =  idata.getString("OPEN_DATE"); 
		} 
		
		SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        String yesterdaytime = "";  
        Calendar calc =Calendar.getInstance();  
        
        try {  
            calc.add(calc.DATE, -1);  
            Date minDate = calc.getTime();   
            yesterdaytime = sdf.format(minDate);   
             
        } catch (Exception e1) {  
             
        }
		
		result.put("OPEN_DATE", open_date);
		result.put("YESTERDAY_TIME", yesterdaytime); 
		
		if( open_date.compareTo(yesterdaytime) < 0){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"开户时间不在24小时之内");
		}
		
		return  result;
 
    }
	
	
	
	public static IData checkSellDay(IData inParam ) throws Exception
    { 
		
		IData result = new DataMap();
		 
		String sql="select TO_CHAR( t.accept_date, 'YYYY-MM-DD') ACCEPT_DATE  from ucr_crm1.tf_f_user_sale_goods t where t.res_code=:RES_CODE and t.cancel_date >sysdate";
			
		
		IDataset res = Dao.qryBySql(new StringBuilder(sql), inParam); 
		String accept_date = "" ; 
		if(IDataUtil.isNotEmpty(res) && res.size()>0){
			IData idata =  (IData)res.get(0); 
			accept_date =  idata.getString("ACCEPT_DATE"); 
		}else{//如果不在tf_f_sale_goods表中，就查资源系统的裸机销售时间
			IData resData = HwTerminalCall.getTerminalInfoByIMEI(inParam.getString("RES_CODE"));
			if(IDataUtil.isNotEmpty(resData)){
				accept_date =  resData.getString("DONE_TIME"); 
			}
			
		}
		 
		    SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");  
	        String currDateStr =  SysDateMgr.getSysDate();  
	        String beforeDateStr = "";  
	        Calendar calc =Calendar.getInstance();  
	        
	        try {  
	            calc.setTime(sdf.parse(currDateStr));   
	            calc.add(calc.DATE, -30);  
	            Date minDate = calc.getTime();   
	            beforeDateStr = sdf.format(minDate);   
	             
	        } catch (Exception e1) {  
	             
	        }
	        
	        result.put("ACCEPT_DATE", accept_date);
	        result.put("BEFORE_DATE", beforeDateStr);
	        if( accept_date.compareTo(beforeDateStr) < 0){
				 CSAppException.apperr(CrmCommException.CRM_COMM_103,"终端串号必须30天内销售,销售时间为：" + accept_date);
			}
	        IDataset retDataset = HwTerminalCall.checkIsResRightType(inParam.getString("RES_CODE"),ResTypeEnum.PHONE.getValue());
		    if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
	        {
		    	 if(StringUtils.equals(retDataset.first().getString("retVal"), "0")){
		    		 CSAppException.apperr(CrmCommException.CRM_COMM_103, "串号不是手机终端串号，请确认！"); // 接口返回异常
		    	 }
	        }
 
		return  result;
    }
	
	public static IData checkTerminalStaff(IData inParam ) throws Exception
    { 
		IData result =new DataMap();
	    String oper_staff_id = CSBizBean.getVisit().getStaffId();
		
	    String update_staff_id = getStaffByResCode(inParam); ; 
		result.put("OPER_STAFF_ID", oper_staff_id);
		result.put("UPDATE_STAFF_ID", update_staff_id);
		 
		result.put("IS_TERMINAL_STAFF", "TRUE");
		if (!update_staff_id.equals(oper_staff_id)){
            result.put("IS_TERMINAL_STAFF", "FALSE");
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"店员录入的终端串号必须为该店员销售的终端" );
		} 
 
		return  result;
    }
	public IData checkTerminalStaffForWeb(IData inParam ) throws Exception
    { 
		IData result =new DataMap();
	    String oper_staff_id = inParam.getString("STAFF_ID");
		
	    String update_staff_id = getStaffByResCode(inParam); ; 
	    inParam.put("UPDATE_STAFF_ID", update_staff_id);
	    inParam.put("CITY_CODE", UDepartInfoQry.getDepartIdByStaffId(update_staff_id));
		result.put("OPER_STAFF_ID", oper_staff_id);
		result.put("UPDATE_STAFF_ID", update_staff_id);
		 
		result.put("IS_TERMINAL_STAFF", "TRUE");
		if (!update_staff_id.equals(oper_staff_id)){
            result.put("IS_TERMINAL_STAFF", "FALSE");
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"店员录入的终端串号必须为该店员销售的终端" );
		} 
 
		return  result;
    }
	
	 
	
	/**
	 * @Description：获取终端销售工号
	 * @param:@param inParam
	 * @param:@return
	 * @return String
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-28上午11:38:09
	 */
	private static String getStaffByResCode(IData inParam) throws Exception {
		
		String update_staff_id="";
		 String sql="  select t.update_staff_id  from ucr_crm1.tf_f_user_sale_goods t where t.res_code=:RES_CODE and t.cancel_date >sysdate" ;
			
		IDataset res = Dao.qryBySql(new StringBuilder(sql), inParam); 
		if(IDataUtil.isNotEmpty(res) && res.size()>0){ 
			IData idata =  (IData)res.get(0); 
			update_staff_id =  idata.getString("UPDATE_STAFF_ID"); 
		}else{
			IData resData = HwTerminalCall.getTerminalInfoByIMEI(inParam.getString("RES_CODE"));
			if(IDataUtil.isNotEmpty(resData)){
				update_staff_id =  resData.getString("OP_ID"); 
			}
		}
		return update_staff_id;
	}



	public static IData checkHaveBound(IData inParam ) throws Exception
    { 
		IData result =new DataMap();  
		
		IData data = new DataMap();  
		String serialNumber = inParam.getString("SERIAL_NUMBER");
		String resCode = inParam.getString("RES_CODE");
		data.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER"));
		IDataset res =  queryTerminalBindBySerialnumber(data); 
		if(IDataUtil.isNotEmpty(res) && res.size()>0){ 
			IData resData = res.first();
			if(!resCode.equals(resData.get("RES_CODE"))){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"该手机号已经和其他终端绑定" );
			}
		 }
		data.clear();
		
		data.put("RES_CODE", inParam.getString("RES_CODE"));
		IDataset rest =  queryTerminalBindBySerialnumber(data); 
		if(IDataUtil.isNotEmpty(rest) && rest.size()>0){ 
			IData resData = rest.first();
			System.out.println("resData:"+resData.toString());
			System.out.println("serialNumber:"+serialNumber+"resData.getString(SERIAL_NUMBER):"+resData.getString("SERIAL_NUMBER"));
			if(!serialNumber.equals(resData.getString("SERIAL_NUMBER"))){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"该终端已经和其他手机号绑定" );
			}
			
		 }
		  
		return  result;
    }
	
    /**
     *录入手机终端绑定信息  ，BIND_FLAG 0为绑定 1为解绑
     *
     * */
    public static IData insertTerminalBind(IData param) throws Exception{
    	
    	IData insData=new DataMap();  
    	
    	// 解绑 id 和时间也设置，当 BIND_FLAG 为1 时才算有效
    	
    	insData.put("BIND_FLAG",param.getString("BIND_FLAG",""));
    	insData.put("SERIAL_NUMBER",param.getString("SERIAL_NUMBER",""));
    	insData.put("RES_CODE",param.getString("RES_CODE",""));  
    	insData.put("OPER_STAFF_ID", param.getString("UPDATE_STAFF_ID",getVisit().getStaffId()));   
    	
    	insData.put("BIND_TIME",  SysDateMgr.getSysTime() ); 
    	insData.put("UNBIND_TIME", SysDateMgr.getSysTime()); 
    	
    	//insData.put("UNBIND_STAFF_ID", getVisit().getStaffId()); 
    	insData.put("CITY_CODE", param.getString("CITY_CODE",getVisit().getCityCode()));  
    	insData.put("RSRV_STR1",param.getString("RSRV_STR1",""));
    	insData.put("RSRV_STR2",param.getString("RSRV_STR2",""));
    	insData.put("RSRV_STR3",param.getString("RSRV_STR3",""));
    	insData.put("RSRV_STR4",param.getString("RSRV_STR4",""));
    	insData.put("RSRV_STR5",param.getString("RSRV_STR5","")); 
    	insData.put("RSRV_STR6",param.getString("RSRV_STR6","")); 
    	  
    	Dao.insert("TL_B_TERSNSJ", insData, null);
    	 
    	return insData;
    	
    }
	
    public static IDataset queryTerminalBind(IData param, Pagination pagination) throws Exception{
    	//省局工号，可以查询全部业务区，其他工号查询自己业务区的记录\
    	String cityCode = getVisit().getCityCode();
    	String paramCityCode = param.getString("CITY_CODE");
    	if(StringUtils.isBlank(paramCityCode)&&!"HNSJ".equals(cityCode)){
    		param.put("CITY_CODE", cityCode);
    	}
    	
    	SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  T.OPER_STAFF_ID,T.RES_CODE,T.SERIAL_NUMBER,TO_CHAR(T.BIND_TIME,'YYYY-MM-DD hh24:mi:ss') as BIND_TIME," +
        		"T.BIND_FLAG,TO_CHAR(T.UNBIND_TIME,'YYYY-MM-DD hh24:mi:ss') as UNBIND_TIME ,T.UNBIND_STAFF_ID,T.CITY_CODE  FROM UCR_CRM1.TL_B_TERSNSJ T WHERE 1=1 ");
        parser.addSQL(" AND T.RES_CODE=:RES_CODE ");
        parser.addSQL(" AND T.SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL(" AND T.CITY_CODE=:CITY_CODE ");
     	IDataset dataset = Dao.qryByParse(parser,pagination); 
    	return dataset ; 
    }
	
    public static IDataset queryTerminalBindBySerialnumber(IData param) throws Exception{
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT  T.*  FROM UCR_CRM1.TL_B_TERSNSJ T WHERE 1=1 ");
        parser.addSQL(" AND T.RES_CODE=:RES_CODE ");
        parser.addSQL(" AND T.SERIAL_NUMBER=:SERIAL_NUMBER ");
    	IDataset dataset = Dao.qryByParse(parser); 
    	return dataset ; 
    }
    
    public static IDataset queryTerminalBindByRescode(IData param) throws Exception{
    	 
    	// 查询 TL_B_TERSNSJ 表的所有字段
    	String sql=" select  t.*  from ucr_crm1.TL_B_TERSNSJ t where t.res_code=:RES_CODE" ;
 		
    	IDataset dataset = Dao.qryBySql(new StringBuilder(sql), param); 
    	return dataset ; 
    }



	/**
	 * @Description：
	 * @param:@param input
	 * @param:@return
	 * @return IDataset
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-5-15下午07:56:26
	 */
	public IDataset updateTerminal(IData input) throws Exception {
		IData data = new DataMap();
		data.put("BIND_FLAG", input.getString("BIND_FLAG"));
		data.put("RES_CODE", input.getString("RES_CODE"));
		int updateRes=0;
		if("0".equals(input.getString("BIND_FLAG"))){//如果是再次绑定，更新绑定时间
			StringBuilder sql = new StringBuilder("UPDATE TL_B_TERSNSJ SET BIND_FLAG=:BIND_FLAG,BIND_TIME=SYSDATE WHERE RES_CODE=:RES_CODE ");
			updateRes = Dao.executeUpdate(sql, data);
		}else{
			data.put("UNBIND_STAFF_ID", input.getString("STAFF_ID",getVisit().getStaffId()));
			StringBuilder sql = new StringBuilder("UPDATE TL_B_TERSNSJ SET BIND_FLAG=:BIND_FLAG,UNBIND_STAFF_ID=:UNBIND_STAFF_ID,UNBIND_TIME=SYSDATE WHERE RES_CODE=:RES_CODE ");
			updateRes = Dao.executeUpdate(sql, data);
			
		}
		if(updateRes<1){
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有可更新记录");
		}
		return null;
	}
	
    
	
}
