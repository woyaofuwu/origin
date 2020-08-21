
package com.asiainfo.veris.crm.order.soa.person.busi.imslandline;

 
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.other.rsaEncryptDecrypt.util.Base64Util;

/**
 * REQ201607050007 关于移动电视尝鲜活动的需求
 * chenxy3 20160720
 * */
public class IMSLandLineBean extends CSBizBean
{
	private static transient Logger logger = Logger.getLogger(IMSLandLineBean.class);
	 
	
	/**
	 * 获取用户是否存在已完工（未完工）的宽带信息。
	 * */
	public static IDataset getUserWilenInfos(IData param) throws Exception
    { 
	    IDataset resultDataset = new DatasetList();
	    
        IDataset dataInfos = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_WILEN_BY_SN_PRODID", param);
        
        if (IDataUtil.isNotEmpty(dataInfos))
        {
            return dataInfos;
        }
        else
        {
            dataInfos = TradeInfoQry.getTradeInfoBySn(param.getString("SERIAL_NUMBER"));
            
            if (IDataUtil.isNotEmpty(dataInfos))
            {
                if (param.getString("PRODUCT_ID").equals(dataInfos.getData(0).getString("PRODUCT_ID")))
                {
                    return dataInfos;
                }
            }
        }
        
        return null;
    }

	/**
     * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
     * mengqx 20190916
     * 给新大陆、电渠提供校验家庭IMS固话证件号码和姓名是否与对应的手机号码的证件号码、姓名一致的接口，新大陆的APP会把身份证号码，姓名，通过base64进行加密
     * 接口入参：手机号码、证件号码、姓名
     * 接口出参：0 表示证件号码和姓名与手机的一致 ，-1 表证件号码和姓名与手机的不一致
     */
	public IData checkIMSPhoneCustInfo(IData input) throws Exception {
		String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String cardNumber = IDataUtil.chkParam(input, "CARD_NUMBER");
    	String custName = IDataUtil.chkParam(input, "CUST_NAME");
		IData resultData = new DataMap();

		logger.debug("----IMSLandLineBean-----mqx----begin");
		IDataset commpara = CommparaInfoQry.getCommparaByCode1("CSM", "925", "1", "0898");// 拦截提示增加配置功能，有问题时改配置即可关闭，不再进行校验，也不再拦截提示。
		if (IDataUtil.isEmpty(commpara))
		{
			resultData.put("RESULTCODE", "0");
			resultData.put("RESULTDESC", "校验通过！");
			return resultData;
		}

    	resultData.put("RESULTCODE", "-1");
    	resultData.put("RESULTDESC", "开户失败，代付手机号码与和家固话号码的实名信息不一致!");
    	
    	IDataset custInfos = CustomerInfoQry.getNormalCustInfoBySN(serialNumber);
    	if(IDataUtil.isNotEmpty(custInfos)){
    		IData custInfo = custInfos.getData(0);
    		String psptId = custInfo.getString("PSPT_ID");
    		String custInfoName = custInfo.getString("CUST_NAME");
    		
    		logger.debug("----IMSLandLineBean-----mqx------psptId="+psptId+",custInfoName="+custInfoName+",加密后psptId="+Base64Util.encode(psptId)+",加密后custInfoName="+Base64Util.encode(custInfoName));
    		
    		if(cardNumber.equals(Base64Util.encode(psptId)) && custName.equals(Base64Util.encode(custInfoName))){
    	    	resultData.put("RESULTCODE", "0");
    	    	resultData.put("RESULTDESC", "校验通过！");
    		}
    	}
    	
    	return resultData;
	}  
	
}
