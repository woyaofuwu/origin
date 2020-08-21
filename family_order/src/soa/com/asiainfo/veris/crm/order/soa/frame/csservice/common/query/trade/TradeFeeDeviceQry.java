
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade;

import org.apache.log4j.Logger;
import org.mvel2.util.StaticStub;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqBookingId;

public class TradeFeeDeviceQry extends CSBizBean
{
	
	static  Logger logger=Logger.getLogger(TradeFeeDeviceQry.class);
	
    /**
     * chenyi 2014-3-4 bboss一次费用入表
     * 
     * @param inparam
     * @throws Exception
     */
    public static void insertFeeInfo(IData inparam) throws Exception
    {

        Dao.insert("TF_B_TRADEFEE_DEFER", inparam, Route.getJourDb(CSBizBean.getTradeEparchyCode()));
    }

    public static IDataset queryTradeFeeDeviceByTradeId(String tradeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        return Dao.qryByCode("TF_B_TRADEFEE_DEVICE", "SEL_BY_PK", param,Route.getJourDb(CSBizBean.getTradeEparchyCode()));//修改为订单库jour,duhj 2017/03/07
    }
    /**
     * 有价卡销售信息查询
     * @param tradeTypeCode 业务类型
     * @param cityCode 销售业务区
     * @param staffIdS 起始工号
     * @param staffIdE 截止工号
     * @param startSaleTime 销售起始日期
     * @param endSaleTime 销售截止日期
     * @param resNoS 起始卡号
     * @param resNoE 终止卡号
     * @return
     * @throws Exception
     */
    public static IDataset queryValueCardByCondition(String tradeTypeCode,String cityCode,String staffIdS,String staffIdE,String startSaleTime,String endSaleTime,String resNoS,String resNoE,Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        if(StringUtils.isNotBlank(tradeTypeCode)) param.put("TRADE_TYPE_CODE", tradeTypeCode);        
        if(StringUtils.isNotBlank(cityCode)) param.put("CITY_CODE", cityCode);
        if(StringUtils.isNotBlank(staffIdS)) param.put("STAFF_ID_S", staffIdS);
        if(StringUtils.isNotBlank(staffIdE)) param.put("STAFF_ID_E", staffIdE);
        if(StringUtils.isNotBlank(startSaleTime)) {
        	param.put("START_VALUECARD_SALE_TIME", startSaleTime);
        	String acceptMonth = SysDateMgr.getMonthForDate(startSaleTime);
        	param.put("ACCEPT_MONTH", acceptMonth);
        }
        if(StringUtils.isNotBlank(endSaleTime)) param.put("END_VALUECARD_SALE_TIME", endSaleTime);
        
        if(StringUtils.isNotBlank(resNoS) && StringUtils.isBlank(resNoE))resNoE=resNoS;
        if(StringUtils.isNotBlank(resNoE) && StringUtils.isBlank(resNoS))resNoS=resNoE;
        
        if(StringUtils.isNotBlank(resNoS)) param.put("X_RES_NO_S", resNoS);
        if(StringUtils.isNotBlank(resNoE)) param.put("X_RES_NO_E", resNoE);
        return Dao.qryByCodeParser("TF_B_TRADEFEE_DEVICE", "SEL_VALUE_CARD_BY_CONDITION", param, pagination,Route.getJourDbDefault());
    }
    
    /**
     * 
     * @param inparam
     * @throws Exception
     */
    public void updateValueCardNotPhoneInfoByCond(IData inparam)  throws Exception{
    		try {
    			
            	String cityCode = inparam.getString("CITY_CODE","");
            	String staffIdS = inparam.getString("START_STAFF_ID","");
            	String staffIdE = inparam.getString("END_STAFF_ID","");
            	String startSaleTime = inparam.getString("START_DATE")+SysDateMgr.START_DATE_FOREVER;
            	String endSaleTime = inparam.getString("END_DATE")+SysDateMgr.END_DATE;
            	String resNoS = inparam.getString("X_RES_NO_S","");
            	String resNoE = inparam.getString("X_RES_NO_E","");
            	
                IData param = new DataMap();
                
                if(StringUtils.isNotBlank(cityCode)) param.put("CITY_CODE", cityCode);
                if(StringUtils.isNotBlank(staffIdS)) param.put("STAFF_ID_S", staffIdS);
                if(StringUtils.isNotBlank(staffIdE)) param.put("STAFF_ID_E", staffIdE);
                if(StringUtils.isNotBlank(startSaleTime)) {
                	param.put("START_VALUECARD_SALE_TIME", startSaleTime);
                	String acceptMonth = SysDateMgr.getMonthForDate(startSaleTime);
                	param.put("ACCEPT_MONTH", acceptMonth);
                }
                if(StringUtils.isNotBlank(endSaleTime)) param.put("END_VALUECARD_SALE_TIME", endSaleTime);
                
                if(StringUtils.isNotBlank(resNoS) && StringUtils.isBlank(resNoE))resNoE=resNoS;
                if(StringUtils.isNotBlank(resNoE) && StringUtils.isBlank(resNoS))resNoS=resNoE;
                
                if(StringUtils.isNotBlank(resNoS)) param.put("X_RES_NO_S", resNoS);
                if(StringUtils.isNotBlank(resNoE)) param.put("X_RES_NO_E", resNoE);
                
                
//                Dao.executeUpdateByCodeCode("", sqlref, param);
                
			} catch (Exception e) {
				// TODO: handle exception
				if(logger.isInfoEnabled()) logger.info(e);
			  throw e;
			}
    	
    }
    
    /**
     * 
     * 有价卡赠送无客户号码信息查询
     * @param tradeTypeCode 业务类型
     * @param cityCode 销售业务区
     * @param staffIdS 起始工号
     * @param staffIdE 截止工号
     * @param startSaleTime 销售起始日期
     * @param endSaleTime 销售截止日期
     * @param resNoS 起始卡号
     * @param resNoE 终止卡号
     * @return
     * @throws Exception
     */
    public  static IDataset qryValueCardNotPhoneByCondition(String cityCode,
    		String staffId,String startSaleTime,String endSaleTime,String updateDepartId,String rsrvStr7,
    		Pagination pagination) throws Exception{
    	try {
    		 IData param = new DataMap();
             
             //按分公司（可查全部或单个分公司）
             if(StringUtils.isNotBlank(cityCode)) param.put("CITY_CODE", cityCode);
             //按部门编码查询
             if(StringUtils.isNotEmpty(updateDepartId)){
             	param.put("UPDATE_DEPART_ID", updateDepartId);
             }
             //审批工单号
             if(StringUtils.isNotBlank(staffId)){
             	 param.put("STAFF_ID", staffId);
             }
             
             //客户号码
             if(StringUtils.isNotBlank(rsrvStr7)){
             	param.put("RSRV_STR7", rsrvStr7);
             }
             
            /**
             * 补录操作开始时间（可选）、补录操作结束时间（可选）
             * 在数据库中为RSRV_STR10
             */
             if(StringUtils.isNotBlank(startSaleTime)) {
             	param.put("START_VALUECARD_SALE_TIME", startSaleTime);
             }
             if(StringUtils.isNotBlank(endSaleTime)){
             	param.put("END_VALUECARD_SALE_TIME", endSaleTime);
             } 
             
             return Dao.qryByCodeParser("TF_B_TRADEFEE_DEVICE", "SEL_VALUE_CARD_NOT_PHONE_BY_CONDITION", param, pagination);
             
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled())	logger.info(e.getMessage());
			throw e;
		}
    }
    
    /**
     * 
     * @param tradeId
     * @param feeTypeCode
     * @param deviceTypeCode
     * @param deviceNoS
     * @param acceptMonth
     * @param custPhone
     * @return
     * @throws Exception
     */
    public static boolean  updateValueCardNotPhoneInfoByCond(String cardNumber,String custPhone
    		,String customerName,String groupName,String giveName) throws Exception{
    	try {
			StringBuilder sql=new StringBuilder();
			 sql.append("update TL_B_VALUECARD_DETAILED t set  ");
			 sql.append(" t.cust_number='"+custPhone+"',");
			 sql.append(" t.record_time='"+SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)+"'");
			 
			 if(!"".equals(customerName)||customerName != null){
				 sql.append(" ,t.rsrv_str2='"+customerName+"'");
			 }
			 if(!"".equals(groupName)||groupName != null){
				 sql.append(" ,t.rsrv_str3='"+groupName+"'");
			 }
			 if(!"".equals(giveName)||giveName !=null){
				 sql.append(" ,t.rsrv_str4='"+giveName+"'");
			 }
			 
    		 sql.append(" where t.card_number='"+cardNumber+"'");
    		 sql.append(" and t.state_code='0' ");
    		 IData param = new DataMap();
    		 Dao.executeUpdate(sql, param);
    		 return true;
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    
    /**
     * 获取有价卡补录信息
     * @param input
     * @throws Exception
     */
    public static  IDataset getValueCardNotPhoneinfo(IData input) throws Exception{
    	try {
        	IDataset set = new DatasetList(); // 上传excel文件内容明细
            String fileId = input.getString("cond_STICK_LIST"); // 上传有价卡excelL文件的编号
            String[] fileIds = fileId.split(",");
            ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
            for (String strfileId : fileIds)
            {
                IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/GiveValueCardNotPhoneImport.xml"));
                IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
                set.addAll(suc[0]);
            }
            
            if(IDataUtil.isNotEmpty(set)){
                for (int i = 0; i < set.size(); i++)
                {
                    IData result = new DataMap();
                    result.clear();
                    IData  b=new DataMap();
                    	   b.clear();
                    	   b=set.getData(i);
                    
                    if("".equals(b.getString("START_CARD_NO"))||b.getString("START_CARD_NO")==null){
                    	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                    }
/*                  if("".equals(b.getString("END_CARD_NO"))||b.getString("END_CARD_NO")==null){
                    	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                    }*/
                    //客户号码
                    if("".equals(b.getString("RSRV_STR7"))||b.getString("RSRV_STR7")==null){
                    	CSAppException.apperr(CrmCommException.CRM_COMM_1165);
                    }
                }
            	return set;
            }else{
            	//模版为空提示错误
            	CSAppException.apperr(CrmCommException.CRM_COMM_1166);
            	return null;
            }
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}

    }
    
    /**
     * 有价卡补录信息导入
     * @param set
     * @throws Exception
     */
    public  static  String importValueCardNotPhoneinfo(IDataset set) throws Exception{
    	try {
    		
            String cityId = getVisit().getCityCode().toUpperCase();
            String staffId = getVisit().getStaffId();
    		
    		//补录成功条数
    	    int count=0;
    	    //无权限，又不是当前操作人员部门的个数
    	    int notRightCount=0;
           //当前员工工号
            String staffid=getVisit().getStaffId();
           //当前员工所在的部门编码
           String departCode=getVisit().getDepartCode();
           boolean isValueCradRightPriv = StaffPrivUtil.isFuncDataPriv(staffid, "isValueCradRight");
           
            for (int i = 0; i < set.size(); i++)
            {
            	IData data=set.getData(i);
                //模版中的客户号码
            	String tmpCustNumber=data.getString("RSRV_STR7");
    	        if (cityId.equals("HNSJ") || staffId.startsWith("HNSJ")){
    	        	//省局帐号
    	        }else{
    	        	//不是省局帐号
    	        	if("".equals(tmpCustNumber)){
    	        		//客户号码:为必填项！
    	        		continue;
    	        	}
    	        }
    	         //卡号
    	         String  cardNo=set.getData(i).getString("START_CARD_NO").trim();
    	         
    	         /**
    	          * 判断是否具有权限
    	          */
    	         if (!isValueCradRightPriv)
    	         {
    	            //无权限，只能操作自己部门的有价卡
    	        	 IDataset oldInfo=getOldGiveValueNotPhoneInfoByCardNo(cardNo);
    	        	if(IDataUtil.isNotEmpty(oldInfo)){
        	            String  oldDepartCode=oldInfo.getData(0).getString("DEPART_CODE");
        	        	if(!departCode.equals(oldDepartCode)){
        	        		//不是当前员工所在的部门,不能补录
        	        		notRightCount=notRightCount+1;
        	        		continue;
        	        	}
    	        	}
    	         }
    	        
                StringBuilder sql=new StringBuilder();
                sql.append("update TL_B_VALUECARD_DETAILED t set");
   			    sql.append(" t.cust_number='"+set.getData(i).getString("RSRV_STR7").trim()+"',");
			    sql.append(" t.record_time='"+SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND)+"'");//系统当前时间
			    //客户姓名
			    String customerName=data.getString("customerName");
			    //对应集团名称
			    String groupName=data.getString("groupName");
			    //赠送人名称
			    String giveName=data.getString("giveName");
			    
				 if(!"".equals(customerName)||customerName != null){
					 sql.append(" ,t.rsrv_str2='"+customerName+"'");
				 }
				 if(!"".equals(groupName)||groupName != null){
					 sql.append(" ,t.rsrv_str3='"+groupName+"'");
				 }
				 if(!"".equals(giveName)||giveName !=null){
					 sql.append(" ,t.rsrv_str4='"+giveName+"'");
				 }
			    
			    sql.append(" where 1=1 ");
			    sql.append(" and t.card_number='"+cardNo+"'");
			    sql.append(" and t.state_code='0' ");
			    sql.append(" and t.trade_type_code='418' ");
			    sql.append(" and t.cust_number is null");
	    		 IData param = new DataMap();
	    		int flag=Dao.executeUpdate(sql, param);
                if(flag >0){
                	count=count+1;
                }
            }
            String msg="";
            if(count==0){
            	msg="模板无匹配的数据";
            }else{
//            	 select * from  TL_B_VALUECARD_DETAILED t 
//            	 where 1=1  
//            	 and 
//            	 t.state_code='0'  and 
//            	 t.trade_type_code='418'  and 
//            	 t.cust_number is null
            	msg="导入"+count+"条数据";
            }
            
            if(notRightCount > 0){
            	msg=msg+",无权限"+notRightCount+"条。";
            }
            return msg;
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}
    }
    

    /**
     * 赠送有价卡清单
     * @param cityCode
     * @param staffId
     * @param startSaleTime
     * @param endSaleTime
     * @param updateDepartId
     * @param rsrvStr7
     * @param pagination
     * @return
     * @throws Exception
     */
    public  static IDataset qryValueCardDetailedByCondition(String cityCode,
    		String staffId,String startSaleTime,String endSaleTime,String updateDepartId,String rsrvStr7,
    		String startTime5,String endTime5,
    		Pagination pagination) throws Exception{
    	try {
            IData param = new DataMap();
            
            //按分公司（可查全部或单个分公司）
            if(StringUtils.isNotBlank(cityCode)) param.put("CITY_CODE", cityCode);
            //按部门编码查询
            if(StringUtils.isNotEmpty(updateDepartId)){
            	param.put("UPDATE_DEPART_ID", updateDepartId);
            }
            //审批工单号
            if(StringUtils.isNotBlank(staffId)){
            	 param.put("STAFF_ID", staffId);
            }
            
            //客户号码
            if(StringUtils.isNotBlank(rsrvStr7)){
            	param.put("RSRV_STR7", rsrvStr7);
            }
            
           /**
            * 补录操作开始时间（可选）、补录操作结束时间（可选）
            * 在数据库中为RSRV_STR10
            */
            if(StringUtils.isNotBlank(startSaleTime)) {
            	param.put("START_VALUECARD_SALE_TIME", startSaleTime);
            }
            if(StringUtils.isNotBlank(endSaleTime)){
            	param.put("END_VALUECARD_SALE_TIME", endSaleTime);
            } 
            //录入时间
            if(StringUtils.isNotBlank(startTime5)) {
            	param.put("START_TIME5", startTime5);
            }
            if(StringUtils.isNotBlank(endTime5)){
            	param.put("END_TIME5", endTime5);
            } 
            
            
            return Dao.qryByCodeParser("TF_B_TRADEFEE_DEVICE", "SEL_VALUE_CARD_DETAILED_BY_CONDITION", param, pagination);
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e.getMessage());
			throw e;
		}
    }
    

    /**
     * 获取老的赠送有价卡信息
     * @param cardNo
     * @return
     * @throws Exception
     */
    public static IDataset  getOldGiveValueNotPhoneInfoByCardNo(String cardNo) throws Exception{
	    try {
	    	IData param = new DataMap();
	    	//卡号
	    	param.put("CARD_NUMBER", cardNo);
	    	return Dao.qryByCode("TL_B_VALUECARD_DETAILED", "SEL_OLD_GIVE_VALUE_CARD_INTFO_BY_CARDNO", param);
		} catch (Exception e) {
			if(logger.isInfoEnabled()) logger.info(e.getMessage());
			throw e;
		}
    	
    }
    
}
