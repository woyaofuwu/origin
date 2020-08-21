package com.asiainfo.veris.crm.order.soa.person.busi.changepassword;

import java.util.ArrayList;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

/**
 * 
 * REQ201705270006_关于人像比对业务优化需求
 * @author zhuoyingzhi
 * @date 20170629
 */
public class ModifyUserPwdInfoSVC extends CSBizService{

	private static final long serialVersionUID = 1L;

	
	static Logger logger=Logger.getLogger(ModifyUserPwdInfoSVC.class);
    
	/**
	 *  查询携入号码信息
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IDataset queryNpQry(IData inparam) throws Exception{
    	
    	try {
    		String serialNumber = inparam.getString("SERIAL_NUMBER", "");
    		IDataset result = TradeNpQry.getValidTradeNpBySn(serialNumber);
        	return result;
		} catch (Exception e) {
			logger.info(e);
			throw e;
		}

    }

	
	/**
	 *  查询brand code 信息
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IDataset qryBrandCode(IData inparam) throws Exception{
    	
    	try {
    		IDataset result = new DatasetList();
    		IData brandCode = new DataMap();
    		String serialNumber = inparam.getString("SERIAL_NUMBER", "");
    		UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
    		brandCode.put("BRAND_CODE", ucaData.getBrandCode());
    		result.add(brandCode);
        	return result;
		} catch (Exception e) {
			logger.info(e);
			throw e;
		}

    }


	/**
	 * 获取用户信息
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IData qryUserInfo(IData inparam) throws Exception{
    	try {
    		String serialNumber = inparam.getString("SERIAL_NUMBER", "");
    		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
        	return userInfo;
		} catch (Exception e) {
			logger.info(e);
			throw e;
		}

    }
	
	/**
	 * 获取客户证件信息
	 * @author zhuoyingzhi
	 * @date 20171020
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IData qryCustomerInfo(IData inparam) throws Exception{
    	try {
    		String custId = inparam.getString("CUST_ID", "");
        	return UcaInfoQry.qryCustomerInfoByCustId(custId);
		} catch (Exception e) {
			logger.info(e);
			throw e;
		}

    } 
   
}
