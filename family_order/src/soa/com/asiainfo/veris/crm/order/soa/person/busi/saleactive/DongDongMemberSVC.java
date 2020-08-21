package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * DongDong会员处理 
 * @author wangmeng
 * @return 
 * @throws Exception
 */
public class DongDongMemberSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DongDongMemberSVC.class);
    /**
     * 每月初赠送10元和包电子券
     * @param cycle
     * @throws Exception
     */
    public IData giveECoupon(IData input) throws Exception {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "00");
        result.put("X_RESULT_INFO", "ok");
        logger.error(">>>>>>>>>>>>>>>DongDongMemberSVCxxxxxxxxxxxxxxxxxx36>>>>>>>>>>>>>>>>>"+input);
        
        String inModeCode = input.getString("IN_MODE_CODE","0");
        String tradeStaffId = input.getString("STAFF_ID","SUPERUSR");
        String tradeEparchyCode = input.getString("TRADE_EPARCHY_CODE","0898");
        String tradeDepartId= input.getString("DEPART_ID","36601");
        String tradeCityCode = input.getString("TRADE_CITY_CODE","HNSJ");
		CSBizBean.getVisit().setInModeCode(inModeCode);
		CSBizBean.getVisit().setStaffEparchyCode(tradeEparchyCode);
		CSBizBean.getVisit().setStaffId(tradeStaffId);
		CSBizBean.getVisit().setDepartId(tradeDepartId);
		CSBizBean.getVisit().setCityCode(tradeCityCode);
		
		String userId = input.getString("USER_ID");
		String discntCode = input.getString("DISCNT_CODE");
		String serialNumber = input.getString("SERIAL_NUMBER");
		
//		IDataset DongDongConfig = CommparaInfoQry.getCommparaByCode1("CSM","1114","DongDongMember","ZZZZ");
		
		IDataset DongDongConfig = CommparaInfoQry.getCommparaByCodeCode1("CSM","1114",discntCode,"DongDongMember");
 
        logger.error(">>>>>>>>>>>>>>>DongDongMemberSVCxxxxxxxxxxxxxxxxxx54>>>>>>>>>>>>>>>>>"+DongDongConfig);
        
		if(DataUtils.isEmpty(DongDongConfig)){
	        result.put("X_RESULTCODE", "-1");
	        result.put("X_RESULT_INFO", "缺失dongdong会员赠送电子券的活动配置");
            return result;
		}		
		
		String packageId = DongDongConfig.first().getString("PARA_CODE6");
		String productId = DongDongConfig.first().getString("PARA_CODE7"); 		
		
		if(packageId ==null || packageId.equals("null")||productId ==null || productId.equals("null")){
	        result.put("X_RESULTCODE", "-1");
	        result.put("X_RESULT_INFO", "缺失dongdong会员赠送电子券的活动配置");
            return result;
		}
		
		if (!"".equals(packageId)&&!"".equals(productId)) {
	    
		IData saleParam = new DataMap();
    	saleParam.put("PACKAGE_ID", packageId);
    	saleParam.put("SERIAL_NUMBER", serialNumber);
    	saleParam.put("PRODUCT_ID", productId);
        logger.error(">>>>>>>>>>>>>>>DongDongMemberSVCxxxxxxxxxxxxxxxxxx69>>>>>>>>>>>>>>>>>"+saleParam);

    	IDataset results = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleParam);	
        logger.error(">>>>>>>>>>>>>>>DongDongMemberSVCxxxxxxxxxxxxxxxxxx72>>>>>>>>>>>>>>>>>"+results);
        
    	if(DataUtils.isNotEmpty(results) && StringUtils.isNotBlank(results.first().getString("TRADE_ID"))){
    		IData updataParam = new DataMap();
    		updataParam.put("RSRV_STR1", "DONGDONG"+SysDateMgr.getCurMonth());
    		updataParam.put("USER_ID", userId);
    		updataParam.put("DISCNT_CODE", discntCode);
            logger.error(">>>>>>>>>>>>>>>DongDongMemberSVCxxxxxxxxxxxxxxxxxx79>>>>>>>>>>>>>>>>>"+updataParam);

    		Dao.executeUpdateByCodeCode("TF_F_USER_DISCNT", "UPD_BY_USERID_DISCNTCODE", updataParam, tradeEparchyCode);
    	}
		}
    	
		return result;
    }
  
}
