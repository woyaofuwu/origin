package com.asiainfo.veris.crm.order.soa.person.busi.personCommBusi; 
  

import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList; 
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil; 
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 

/** 
 * 通用的app后台类。
 * 一些零散的业务需要查数据库的都可以丢这里。省得次次都加文件夹加文件。
 */
public class PersonCommBean extends CSBizBean
{
	static Logger logger = Logger.getLogger(PersonCommBean.class);
    
    /**
     * 批量导入网厅开户用户
     * ucr_crm1.td_b_postcard_info
     */
    public static IDataset importNetOpenUserData(IData input) throws Exception
    { 
    	IDataset rtnSet=new DatasetList();
    	IDataset dataset=new DatasetList(); 
    	String fileId = input.getString("cond_STICK_LIST");  
    	logger.debug("======*cxy*==========fileId="+fileId);
        String[] fileIds = fileId.split(",");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        String  msg="";
        
        for (String strfileId : fileIds)
        {
            IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/BatNetOpenUserImport.xml"));
            
            logger.debug("======*cxy*=====fileIds="+fileIds+"====array="+array);
            IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
            IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
            dataset.addAll(suc[0]); 
            rtnSet.addAll(err[0]); 
        }
        logger.debug("======*cxy*==========dataset="+dataset);
        //对成功数据进行解析
        String serialNumList="";
        for (int i = 0; i < dataset.size(); i++)
        {
        	String checkInfo=checkImportData(dataset.getData(i));
        	String orderNo=dataset.getData(i).getString("ORDER_NO");
            String serialNum=dataset.getData(i).getString("SERIAL_NUMBER");
            String ifGO="GO";
            logger.debug("======*cxy*==========serialNum="+serialNum);
            /**
             * 校验手机号 *
             * 手机号校验：1、校验手机是否在td_b_postcard_info表里存在未完成的数据。 
             * */
            if(i>0){
            	if(serialNumList.indexOf(serialNum)>-1){
            		msg="存在重复号码导入。";
            		ifGO="DONGO";
            	}else{
            		serialNumList=serialNumList+"|"+serialNum;
            	}
            }else{
            	serialNumList=serialNumList+"|"+serialNum;
            } 
            if(!"".equals(checkInfo)){
            	ifGO="DONGO";
            	msg=msg+"**"+checkInfo;
            }
            if("GO".equals(ifGO)){ 
	            IData params = new DataMap();
	            params.putAll(dataset.getData(i));
    			//将礼品延期
	            logger.debug("======*cxy*==========params="+params);
	            PersonCommBean.insertData(params); 
    			msg="【成功】"; 
	        }else{
	        	IData errData=new DataMap();
	        	errData.put("ORDER_NO", orderNo); 
                errData.put("SERIAL_NUMBER", serialNum); 
                errData.put("IMPORT_ERROR", msg);
                errData.put("ROW_NUM", i+2);
                
                rtnSet.add(errData);
	        } 
        } 
        if(rtnSet!=null && rtnSet.size()>0){ 
        	String errInfo="";
        	for (int i=0;i<rtnSet.size();i++){
        		String ROW_NUM=rtnSet.getData(i).getString("ROW_NUM");
        		String IMPORT_ERROR=rtnSet.getData(i).getString("IMPORT_ERROR");
        		errInfo=errInfo+"第"+ROW_NUM+"行，错误说明："+IMPORT_ERROR+" & ";
        	}
        	if(!"".equals(errInfo)){
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "导入数据有错误，"+errInfo);
        	}
        }
        //log.info("("*********cxy******rtnSet="+rtnSet);
        return rtnSet; 
    } 
    
    /**
     *  校验入参
     * */
    public static String checkImportData(IData input) throws Exception
    { 
    	String msg=""; 
    	String ORDER_NO     = input.getString("ORDER_NO","");// 订单编码 
    	String SERIAL_NUMBER= input.getString("SERIAL_NUMBER","");// 开户号码 
    	String CUST_NAME    = input.getString("CUST_NAME","");// 客户名称 
    	String PSPT_ID      = input.getString("PSPT_ID","");// 客户证件 
    	String PSPT_ADDR    = input.getString("PSPT_ADDR","");// 证件地址 
    	String POST_ADDR    = input.getString("POST_ADDR","");// 邮寄地址 
    	String POST_PHONE   = input.getString("POST_PHONE","");// 联系电话  
    	if(ORDER_NO.length()>40){
    		msg="订单编码过长【40】。"; 
    	}
		if(SERIAL_NUMBER.length()>40){
			msg=msg+"开户号码过长【40】。";
    		 
		}
		if(CUST_NAME.length()>40){
			msg=msg+"客户名称过长【40】。";
		}
		if(PSPT_ID.length()>40){
			msg=msg+"客户证件过长【40】。";
		}
		if(POST_PHONE.length()>40){
			msg=msg+"联系电话过长【40】。";
		}
		if(PSPT_ADDR.length()>500){
			msg=msg+"证件地址过长【250】。";
		}
		if(POST_ADDR.length()>500){
			msg=msg+"邮寄地址过长【250】。";
		}
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", SERIAL_NUMBER);
		IDataset list=qryIfExistRepetNumber(param);
		if(list!=null && list.size()>0){
			msg=msg+"该号码【"+SERIAL_NUMBER+"】已经存在数据库表内。";
		}
		return msg;
    } 
    /**
     *  插表
     * */
    public static void insertData(IData params) throws Exception
    { 
    	StringBuilder sql = new StringBuilder(1000);
    	
    	sql.append(" insert into td_b_postcard_info(ORDER_NO,SERIAL_NUMBER,CUST_NAME,PSPT_ID,PSPT_ADDR,POST_ADDR,POST_PHONE,PICNAMERPATH,rsrt_str9 ,pspt_type_code,in_mode,state,send_sms_flag,cancel_flag,accept_date,updata_time,end_date) ");
    	sql.append(" values(:ORDER_NO,:SERIAL_NUMBER,:CUST_NAME,:PSPT_ID,:PSPT_ADDR,:POST_ADDR,:POST_PHONE,:PICNAMERPATH,'bat import','0','2','1','0','0',sysdate,sysdate,to_date(to_char(sysdate+30,'yyyy-mm-dd')||' 23:59:59','yyyy-mm-dd hh24:mi:ss'))");
        Dao.executeUpdate(sql, params);
    } 
    
    /**
     * 查询是否重复导入号码。
     * */
    public static IDataset qryIfExistRepetNumber(IData params) throws Exception{
    	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL(" select t.* from td_b_postcard_info t where t.SERIAL_NUMBER=:SERIAL_NUMBER and sysdate < t.end_date  "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
}