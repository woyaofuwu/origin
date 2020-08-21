
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.WideUserCreateSVC;

public class MergeWideUserCreateIntfFilter implements IFilterIn
{

    /**
     * 宽带开户入参检查
     * 
     * @author yuyj3
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "WIDE_TYPE");
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	IDataUtil.chkParam(param, "DETAIL_ADDRESS");
    	IDataUtil.chkParam(param, "STAND_ADDRESS");
        IDataUtil.chkParam(param, "CONTACT");
        IDataUtil.chkParam(param, "CONTACT_PHONE");
        IDataUtil.chkParam(param, "AREA_CODE");
        
        IDataUtil.chkParam(param, "PRODUCT_ID");
        IDataUtil.chkParam(param, "SERVICE_ID");
        IDataUtil.chkParam(param, "DISCNT_CODE");
        
        IDataUtil.chkParam(param, "FLOOR_AND_ROOM_NUM");
        IDataUtil.chkParam(param, "DEVICE_ID");
        
        
    	if(param.getString("WIDE_TYPE").equals("FTTH") || param.getString("WIDE_TYPE").equals("TTFTTH"))
    	{
    	    //FTTH MODEM_STYLE为必选
	        IDataUtil.chkParam(param, "MODEM_STYLE");
    	}
    	
    	//如果选了魔百和产品，魔百和基础包则为必选
    	if (StringUtils.isNotBlank(param.getString("TOP_SET_BOX_PRODUCT_ID")))
    	{
    	    IDataUtil.chkParam(param, "BASE_PACKAGES");
    	}
    	
    	if (StringUtils.isNotBlank(param.getString("FIX_NUMBER")))
    	{
    		if (!param.getString("WIDE_TYPE").equals("FTTH") && !param.getString("WIDE_TYPE").equals("TTFTTH"))
    		{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, "非FTTH宽带用户不能办理IMS固话业务！");
    		}
    		
    		IDataUtil.chkParam(param, "IMS_PRODUCT_ID");
    	}
    	
    	if (StringUtils.isNotBlank(param.getString("HEMU_SALE_ACTIVE_ID")))
    	{
    		IDataUtil.chkParam(param, "HEMU_RES_ID");
    	}
    	
    	checkSerialNumber(param);
    	checkTradeinfo(param);
    }

    public void transferDataInput(IData input) throws Exception
    {
        checkInparam(input);
        input.put("WIDE_PRODUCT_ID", input.getString("PRODUCT_ID"));
        input.put("WIDE_PSPT_ID", input.getString("PSPT_ID"));
        input.put("AUTH_SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        
        //新大陆传过来是以分为单位  但 校验接口以元为单位会*100，所以传过去是先除100
        input.put("MODEM_DEPOSIT", Integer.parseInt(input.getString("MODEM_DEPOSIT","0"))/100);
        input.put("TOP_SET_BOX_DEPOSIT", Integer.parseInt(input.getString("TOP_SET_BOX_DEPOSIT","0"))/100);
        
        //获取宽带开户用户新号码
        IData wideSNdataset = new WideUserCreateSVC().getWideSerialNumber(input);
        
        input.put("WIDE_SERIAL_NUMBER", wideSNdataset.getString("WIDE_SERIAL_NUMBER"));
        
        String wideType =  input.getString("WIDE_TYPE");
        String wideProductType = "";
        
        if ("FTTH".equals(wideType))
        {
            wideProductType = "3";
        }
        else if ("TTFTTH".equals(wideType))
        {
            wideProductType = "5";
        }
        else if ("GPON".equals(wideType))
        {
            wideProductType = "1";
        }
        else if ("TTFTTB".equals(wideType))
        {
            wideProductType = "6";
        }
        else if ("TTADSL".equals(wideType))
        {
            wideProductType = "2";
        }
        
        input.put("WIDE_PRODUCT_TYPE", wideProductType);
        
        if (StringUtils.isNotBlank(input.getString("IMS_SALE_ACTIVE_ID")))
        {
        	IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "6800", null, null, input.getString("IMS_SALE_ACTIVE_ID"), null);
        	
        	if (IDataUtil.isNotEmpty(saleActiveList))
        	{
        		//设置IMS固话营销活动产品ID
        		input.put("IMS_SALE_ACTIVE_PRODUCT_ID", saleActiveList.getData(0).getString("PARA_CODE4"));
        	}
        	else
        	{
        		CSAppException.appError("201800003", "该营销活动["+input.getString("IMS_SALE_ACTIVE_ID")+"]参数置信息不存在，请联系管理员！");
        	}
        }
        
        if (StringUtils.isNotBlank(input.getString("HEMU_SALE_ACTIVE_ID")))
        {
        	IDataset saleActiveList = CommparaInfoQry.getCommparaByParaCode("CSM", "178", "HEMU", null, null, input.getString("HEMU_SALE_ACTIVE_ID"), null);
        	
        	if (IDataUtil.isNotEmpty(saleActiveList))
        	{
        		//设置和目营销活动产品ID
        		input.put("HEMU_SALE_ACTIVE_PRODUCT_ID", saleActiveList.getData(0).getString("PARA_CODE4"));
        	}
        	else
        	{
        		CSAppException.appError("201800005", "该营销活动["+input.getString("HEMU_SALE_ACTIVE_ID")+"]参数置信息不存在，请联系管理员！");
        	}
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
	        
	        element.put("PACKAGE_ID", packageId);
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
