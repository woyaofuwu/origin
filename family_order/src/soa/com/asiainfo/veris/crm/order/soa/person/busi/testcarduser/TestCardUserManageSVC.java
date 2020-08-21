package com.asiainfo.veris.crm.order.soa.person.busi.testcarduser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * REQ201609060001_2016年下半年测试卡功能优化（二）
 * @author zhuoyingzhi
 * 20160923
 *
 */
public class TestCardUserManageSVC extends CSBizService {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4000100567108046474L;
	
    private static Logger log=Logger.getLogger(TestCardUserManageSVC.class);


	/**
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset queryTestCardUserinfo(IData data) throws Exception{
		try {
			return TestCardUserBean.queryTestCardUserinfo(data, getPagination());
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}
	
	/**
	 * 查询在tf_f_user_other中的测试卡号信息
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public static IDataset qryTestCardUserInfoByInstidPartitionId(IData input)throws Exception{
		try {
			return TestCardUserBean.qryTestCardUserInfoByInstidPartitionId(input);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}
   /**
    * 修改测试卡类型，记录日志
    * @param data
    * @throws Exception
    */
	public static void insertOperLog(IData data)throws Exception{
		try {
			TestCardUserBean.insertOperLog(data);
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
    public static IDataset  getUserOtherInfoByUserId(IData data)throws Exception{
    	 try {
        	return  TestCardUserBean.getUserOtherInfoByUserId(data.getString("USER_ID"));
		  } catch (Exception e) {
			//log.info("(e);
			throw e;
		 }
    }
	
}
