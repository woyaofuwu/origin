
package com.asiainfo.veris.crm.order.soa.person.busi.testcarduser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * @author zhuoyingzhi
 * 20160923
 * 
 */
public class TestCardUserBean extends CSBizBean
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static Logger log=Logger.getLogger(TestCardUserBean.class);
    
   /**
    * 
    * @param input
    * @param pagination
    * @return
    * @throws Exception
    */
   public static IDataset  queryTestCardUserinfo(IData input,Pagination pagination) throws Exception{
	 
	   try {
		   IData param=new DataMap();
		   
		   String SERIAL_NUMBER=input.getString("SERIAL_NUMBER");
		   if(StringUtils.isNotEmpty(SERIAL_NUMBER)){
			   param.put("SERIAL_NUMBER", SERIAL_NUMBER.trim());//测试号码
		   }
		   
		   String RSRV_VALUE=input.getString("RSRV_VALUE");
		   if(StringUtils.isNotEmpty(RSRV_VALUE)){
			   param.put("RSRV_VALUE", input.getString("RSRV_VALUE").trim());//测试卡类型
		   }
		   
		   String NUMBER_SEGMENT=input.getString("NUMBER_SEGMENT");
		   if(StringUtils.isNotEmpty(NUMBER_SEGMENT)){
			   param.put("NUMBER_SEGMENT", NUMBER_SEGMENT.trim());//号段
		   }
		   IDataset idaDataset=Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_SERIAL_NUMBER_RSRV_VALUE", param, pagination);
		   return  idaDataset;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
   }
   
   /**
    * 查询在tf_f_user_other中的测试卡号信息
    * @param input
    * @return
    * @throws Exception
    */
   @Deprecated
   public static IDataset qryTestCardUserInfoByInstidPartitionId(IData input)throws Exception{
	   try {
		   IData param=new DataMap();
		   
		   String INST_ID=input.getString("INST_ID");
		   if(StringUtils.isNotEmpty(INST_ID)){
			   param.put("INST_ID", INST_ID.trim());
		   }
		   String PARTITION_ID=input.getString("PARTITION_ID");
		   if(StringUtils.isNotEmpty(PARTITION_ID)){
			   param.put("PARTITION_ID", PARTITION_ID.trim());
		   }
		   
		   return  Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_INST_ID_PARTITION_ID", param);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
   }
	
	/**
	 * @Description:记录测试卡操作日志
				    1	DEL  注销
				    2	INS  新增
				    3	QRY  查询
				    4	UPD  更新
				    5	EXP  导出
				    6	STP  局方停机
				    7   ALLUPD 批量更新
	 * @author 
	 * @throws Exception
	 */
   @Deprecated
	public static void insertOperLog(IData data) throws Exception{
		try {
			IData logData = new DataMap();
			
			logData.put("LOG_ID", SeqMgr.getLogId());
			
			//操作类型
			logData.put("OPER_CODE", data.getString("OPER_CODE"));
			
			logData.put("OPER_DESC", data.getString("OPER_DESC"));
			
			logData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
			logData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
			logData.put("UPDATE_TIME", SysDateMgr.getSysTime());//修改时间
			
			//手机号码
			logData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
			//记录修改前的测试卡类型
			logData.put("RSRV_STR1", data.getString("RSRV_STR1"));
			//修改后的测试卡类型
			logData.put("RSRV_STR2", data.getString("RSRV_STR2"));
			//
			logData.put("RSRV_STR3", "TEST_CARD_USER");
			
			
			Dao.insert("TL_OWN_SERIALNUMBER_LOG", logData);
			
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}

	}
	
    /**
     * 通过用户id查询TF_F_USER_OTHER信息
     * @return
     * @throws Exception
     * SEL_BY_USRID_RSRV_VALUE 这个code_code已经上线
     */
	@Deprecated
    public static IDataset  getUserOtherInfoByUserId(String userid)throws Exception{
         IData inparam=new DataMap();
         inparam.put("USER_ID", userid);
         inparam.put("RSRV_VALUE_CODE", "TEST_CARD_USER");
         
    	return  Dao.qryByCode("TF_F_USER_OTHER", "SEL_BY_USRID_RSRV_VALUE", inparam);
    }
}
