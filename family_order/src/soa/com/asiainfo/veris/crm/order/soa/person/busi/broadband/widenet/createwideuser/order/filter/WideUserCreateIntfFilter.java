
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class WideUserCreateIntfFilter implements IFilterIn
{

    /**
     * 宽带开户入参检查
     * 
     * @author chenzm
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "WIDE_TYPE");
    	IDataUtil.chkParam(param, "DETAIL_ADDRESS");
        IDataUtil.chkParam(param, "CONTACT");
        IDataUtil.chkParam(param, "CONTACT_PHONE");
        IDataUtil.chkParam(param, "PSPT_ID");
        IDataUtil.chkParam(param, "AREA_CODE");
        IDataUtil.chkParam(param, "SERVICE_ID");
        IDataUtil.chkParam(param, "DISCNT_CODE");
        IDataUtil.chkParam(param, "PAY_MODE");
        IDataUtil.chkParam(param, "MONEY");
        IDataUtil.chkParam(param, "SERIAL_NUMBER");
        IDataUtil.chkParam(param, "PRODUCT_ID");
    	if(!param.getString("WIDE_TYPE").equals("4"))
    	{//非校园宽带的必填字段
	        IDataUtil.chkParam(param, "STAND_ADDRESS_CODE");
	        IDataUtil.chkParam(param, "STAND_ADDRESS");
	        
	        if(param.getString("MODEM_STYLE","").equals("3"))
	        	IDataUtil.chkParam(param, "MODEM_NUMERIC");
	        
    	}else{//校园宽带的必填字段
    		IDataUtil.chkParam(param, "USER_PASSWD");
    		IDataUtil.chkParam(param, "STUDENT_NUMBER");
    	}
    	checkSerialNumber(param);
    	checkTradeinfo(param);
    }

    public void transferDataInput(IData input) throws Exception
    {

        checkInparam(input);
        input.put("PHONE", input.getString("CONTACT_PHONE"));
        input.put("WIDE_PRODUCT_ID", input.getString("PRODUCT_ID"));
        input.put("WIDE_PSPT_ID", input.getString("PSPT_ID"));
        input.put("MODEM_NUMERIC_CODE", input.getString("MODEM_NUMERIC",""));
//        input.put("SERVICE_ID", "2010,2011");
        input.put("AUTH_SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        input.put("WIDE_SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
//        if(input.getString("WIDE_TYPE").equals("GPON"))
//        	input.put("TRADE_TYPE_CODE", input.getString("600"));
//        else if(input.getString("WIDE_TYPE").equals("ADSL"))
//        	input.put("TRADE_TYPE_CODE", input.getString("612"));
//        else if(input.getString("WIDE_TYPE").equals("XIAN"))
//        	input.put("TRADE_TYPE_CODE", input.getString("613"));
//        else if(input.getString("WIDE_TYPE").equals("SCHOOL"))
//        	input.put("TRADE_TYPE_CODE", input.getString("630"));
        
        IDataset feesubs = new DatasetList();
        //未购买MODEM的情况
        if(!input.getString("MODEM_STYLE","").equals("3"))
        {
	        IData feesub = new DataMap();
	        feesub.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE"));
	        feesub.put("FEE_TYPE_CODE", "0");
	        feesub.put("FEE", input.getString("MONEY"));
	        feesub.put("OLDFEE", "0");//?固定值
	        feesub.put("FEE_MODE", "2");//?固定值
	        feesubs.add(feesub);
	        input.put("X_TRADE_FEESUB", feesubs.toString());
        }else
        {//购买MODEM的情况
        	if(Integer.parseInt(input.getString("MONEY"))<8800)
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "购买MODEN费用有误！");
        	
        	IData feesub = new DataMap();
	        feesub.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE"));
	        feesub.put("FEE_TYPE_CODE", "9205");
	        feesub.put("FEE", "8800");
	        feesub.put("OLDFEE", "8800");
	        feesub.put("FEE_MODE", "0");
	        feesubs.add(feesub);
	        
	        IData feesub1 = new DataMap();
	        feesub1.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE"));
	        feesub1.put("FEE_TYPE_CODE", "0");
	        feesub1.put("FEE", String.valueOf(Integer.parseInt(input.getString("MONEY")) - 8800));
	        feesub1.put("OLDFEE", "0");
	        feesub1.put("FEE_MODE", "2");
	        feesubs.add(feesub1);
	        
	        input.put("X_TRADE_FEESUB", feesubs.toString());
        }

        IDataset selectedelements = new DatasetList();
        String[] services = input.getString("SERVICE_ID").split(",");
        
        String packageId = "-1";
        
        for(int i=0; i<services.length; i++)
        {
	        IData element = new DataMap();
	        element.put("ELEMENT_ID", services[i]);
	        element.put("ELEMENT_TYPE_CODE", "S");
	        element.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
	        
	        IData elementCfg = ProductElementsCache.getElement(input.getString("PRODUCT_ID"), services[i], BofConst.ELEMENT_TYPE_CODE_SVC);
	        if (IDataUtil.isNotEmpty(elementCfg))
	        {
	            packageId = elementCfg.getString("GROUP_ID","-1");
	        }
	        
	        element.put("PACKAGE_ID",packageId);
	        element.put("MODIFY_TAG", "0");
	        element.put("START_DATE", SysDateMgr.getSysTime());
	        element.put("END_DATE", "2050-12-31");
	        selectedelements.add(element);
        }
        
        String[] discnts = input.getString("DISCNT_CODE").split(",");
        for(int i=0; i<discnts.length; i++)
        {
	        IData element = new DataMap();
	        element.put("ELEMENT_ID", discnts[i]);
	        element.put("ELEMENT_TYPE_CODE", "D");
	        element.put("PRODUCT_ID", input.getString("PRODUCT_ID"));
	        element.put("MODIFY_TAG", "0");
	        
	        IData elementCfg = ProductElementsCache.getElement(input.getString("PRODUCT_ID"), discnts[i], BofConst.ELEMENT_TYPE_CODE_DISCNT);
            if (IDataUtil.isNotEmpty(elementCfg))
            {
                packageId = elementCfg.getString("GROUP_ID","-1");
            }
	        
	        element.put("PACKAGE_ID", packageId);
	        element.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
	        element.put("END_DATE", "2050-12-31");
	        selectedelements.add(element);
        }
        
        input.put("SELECTED_ELEMENTS", selectedelements.toString());
        
        IDataset paymoneys = new DatasetList();
        IData paymode = new DataMap();
        paymode.put("PAY_MONEY_CODE", input.getString("PAY_MODE"));
        paymode.put("MONEY", input.getString("MONEY"));
        paymoneys.add(paymode);
        input.put("X_TRADE_PAYMONEY", paymoneys.toString());
    }
    
    public void checkSerialNumber(IData input) throws Exception
    {
    	IDataset userinfo = UserInfoQry.getUserInfoBySn("KD_"+input.getString("SERIAL_NUMBER"),"0");
    	if(!DataSetUtils.isBlank(userinfo))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已开通宽带！");
        }
    }
    
    public void checkTradeinfo(IData input) throws Exception
    {
    	
    	String tradeType ="";
    	IDataset result = TradeInfoQry.getTradeInfoBySn("KD_"+input.getString("SERIAL_NUMBER"));
        if(!DataSetUtils.isBlank(result))
        {
        	IDataset types = TradeTypeInfoQry.queryDistincByCode(result.getData(0).getString("TRADE_TYPE_CODE"));
        	if(!DataSetUtils.isBlank(types))
        	{
        		tradeType = types.getData(0).getString("TRADE_TYPE");
        	}
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户有"+tradeType+"未完工工单！");
        }
    	
    }
    
    
}
