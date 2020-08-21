package com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


public class DredgeSmartNetworkIntfSVC extends CSBizService{
	
	 private static Logger log = Logger.getLogger(DredgeSmartNetworkIntfSVC.class);
	
	/*
	* @Function: checkQualificate
    * @Description:	校验能否办理业务接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData checkQualificate(IData input) throws Exception {

		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.checkQualificate(input);
		return result;
	}
	
	/**
	 * 校验能否办理业务接口
	 * @author: wuwangfeng
	 * @date: 2020/5/12 9:42
	 */
	public IData checkQualificateNew(IData input) throws Exception {
		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.checkQualificateNew(input);
		return result;
	}
	
	/*
	* @Function: getDiscnts
    * @Description:	获取可办理套餐接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData getDiscnts(IData input) throws Exception {

		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.getDiscnts(input);
		return result;
	}
	
	/**
	 * 获取可办理套餐接口
	 * @author: wuwangfeng
	 * @date: 2020/5/12 9:42
	 */
	public IData getDiscntsNew(IData input) throws Exception {
		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.getDiscntsNew(input);
		return result;
	}
	
	/*
	* @Function: checkFeeBeforeSubmit
    * @Description:	提交前费用校验接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData checkFeeBeforeSubmit(IData input) throws Exception {

		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.checkFeeBeforeSubmit(input);
		return result;
	}	
	
	/*
	* @Function: dredgeSubmit
    * @Description:	提交业务接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData dredgeSubmit(IData input) throws Exception {

		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.dredgeSubmit(input);
		return result;
	}
	
	/**
	 * 提交业务接口
	 * @author: wuwangfeng
	 * @date: 2020/5/12 9:42
	 */
	public IData dredgeSubmitNew(IData input) throws Exception {
		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.dredgeSubmitNew(input);
		return result;
	}
	
	/*
	* @Function: queryTradedBySn
    * @Description:	是否办理相关业务办理查询接口
    * @param: @param data
    * @param: @return
    * @param: @throws Exception
    * @return：IData
    */
	public IData queryTradedBySn(IData input) throws Exception {

		DredgeSmartNetworkIntfBean  bean = new DredgeSmartNetworkIntfBean();
		IData result = bean.queryTradedBySn(input);
		return result;
	}
	
	
	
}
