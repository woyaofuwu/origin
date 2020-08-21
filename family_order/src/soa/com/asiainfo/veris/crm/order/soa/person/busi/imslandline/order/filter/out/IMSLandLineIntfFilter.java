
package com.asiainfo.veris.crm.order.soa.person.busi.imslandline.order.filter.out;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpInfoQry;

public class IMSLandLineIntfFilter implements IFilterIn
{

    /**
     * 家庭IMS开户入参检查
     * 
     * @author yuyj3
     * @param param
     * @throws Exception
     */
    public void checkInparam(IData param) throws Exception
    {
    	IDataUtil.chkParam(param, "RSRV_STR4");//宽带市县
    	IDataUtil.chkParam(param, "SERIAL_NUMBER");//办理号码
    	IDataUtil.chkParam(param, "WIDE_SERIAL_NUMBER");//家庭IMS固话
    	IDataUtil.chkParam(param, "PRODUCT_ID");//84004439
        IDataUtil.chkParam(param, "PRODUCT_NAME");//家庭IMS固话
        IDataUtil.chkParam(param, "SERVICE_ID");//84004236,84004237,84004238,84004239,84004240
        IDataUtil.chkParam(param, "DISCNT_CODE");//84003861
        
        IDataUtil.chkParam(param, "WIDE_START_DATE"); 
        IDataUtil.chkParam(param, "ROUTE_EPARCHY_CODE");  
        IDataUtil.chkParam(param, "WIDE_STATE");  
        IDataUtil.chkParam(param, "WIDE_PRODUCT_NAME");  
        IDataUtil.chkParam(param, "WIDE_ADDRESS"); 
        IDataUtil.chkParam(param, "AUTH_SERIAL_NUMBER");  
        IDataUtil.chkParam(param, "WIDE_STATE_NAME"); 
        IDataUtil.chkParam(param, "WIDE_USER_ID");  
        IDataUtil.chkParam(param, "WIDE_END_DATE");  
        
    	checkTradeinfo(param);
    	checkFixPhoneNum(param);
    }

    public void transferDataInput(IData input) throws Exception
    {
        //判断是否是掌厅网厅的
    	String ZTWT_OPEN_IMS = input.getString("ZTWT_OPEN_IMS");
    	String bz = "N";
    	if(StringUtils.isNotBlank(ZTWT_OPEN_IMS)){
    		if("ZTWT_OPEN_IMS".equals(ZTWT_OPEN_IMS)){
    			bz = "Y";
    		}
    		
    	}
    	//REQ201903010002+关于掌厅与网厅办理家庭IMS固话的需求 这个接口过来的IMS不需要再校验，拼接参数。
    	if(bz.equals("N")){
    		checkInparam(input);        
            IDataset selectedelements = new DatasetList();
            String[] services = input.getString("SERVICE_ID").split(",");
            
            for(int i=0; i<services.length; i++)
            {
    	        IData element = new DataMap();
    	        element.put("ELEMENT_ID", services[i]);
    	        element.put("ELEMENT_TYPE_CODE", "S");
    	        element.put("PRODUCT_ID", input.getString("PRODUCT_ID"));        
    	        element.put("PACKAGE_ID", "0");
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
    	        element.put("PACKAGE_ID", "0");
    	        element.put("START_DATE", SysDateMgr.getSysTime());
    	        element.put("END_DATE", "2050-12-31");
    	        selectedelements.add(element);
            }
            
            input.put("SELECTED_ELEMENTS", selectedelements.toString());
    	}
    }
    
    
    public void checkTradeinfo(IData input) throws Exception
    {
    	 //家庭IMS固话未完工校验
    	IData useuca = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
    	if(IDataUtil.isNotEmpty(useuca))
        {
        	IDataset id2 = TradeInfoQry.getExistUser("MS", useuca.getString("USER_ID"), "1");	

	        if(!DataSetUtils.isBlank(id2))
	        {
	        	IDataset types = TradeTypeInfoQry.queryDistincByCode(id2.getData(0).getString("TRADE_TYPE_CODE"));
	        	if(!DataSetUtils.isBlank(types))
	        	{
	        		String tradeType = types.getData(0).getString("TRADE_TYPE");
	        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户有"+tradeType+"未完工工单！");
	        	}
	        	
	        }
        }
    	
    }
    
    public IData checkFixPhoneNum(IData param)throws Exception{
		IData rtnData=new DataMap();
		String fixNum=param.getString("WIDE_SERIAL_NUMBER","");
		//     固话号码检验接口
		IData params=new DataMap(); 
		params.put("RES_VALUE", fixNum);//固话号码 0898开头
		params.put("RES_TYPE_CODE","0");
		params.put("RES_TYPE","固话号码");
		params.put("CITYCODE_RSRVSTR4",param.getString("RSRV_STR4", ""));
		try{
			rtnData=checkFixNumber(params);
		}catch(Exception e){
	    	String error =  Utility.parseExceptionMessage(e);
	    	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			if(errorArray.length >= 2)
			{
				String strExceptionMessage = errorArray[1];
				CSAppException.appError("2017120500", "号码【"+fixNum+"】预占失败:"+strExceptionMessage);
			}
			else
			{
				CSAppException.appError("2017120501", "固话号码【"+fixNum+"】预占失败:"+error);
			}  
         }
		return rtnData; 
	}

    public IData checkFixNumber(IData param) throws Exception
    { 
        String serialNumber = param.getString("RES_VALUE", "");
        String restypecode = param.getString("RES_TYPE_CODE");
        String restype = param.getString("RES_TYPE");

        IData result = new DataMap();

        IDataset userInfos = UserGrpInfoQry.getMemberUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(userInfos))
        {
        	CSAppException.appError("2017120502", serialNumber + "号码已经生成了资料，请输入新号码！");
        }
        else
        {
        	String str = serialNumber.substring(0, 4);
            if (!"0898".equals(str))
            {
            	CSAppException.appError("2017120503", serialNumber + "号码非固话号码，IMS语音成员用户开户必须为固话号码，请输入新号码！");
            }
            else
            {
                // 选占
                IDataset callset=ResCall.checkResourceForMphone("0", serialNumber, "0");
                
                if(IDataUtil.isNotEmpty(callset) && !"0G".equals(callset.getData(0).getString("RES_KIND_CODE","")))
                {
                	CSAppException.appError("2017120504", "号码非家庭IMS固话，请确认重新输入！");
                }
                
                if(IDataUtil.isNotEmpty(callset) && !param.getString("CITYCODE_RSRVSTR4").equals(callset.getData(0).getString("CITY_CODE","")))
                {
                	CSAppException.appError("2017120505", "固话归属业务区"+callset.getData(0).getString("CITY_CODE","")+"与宽带号码业务区"+param.getString("CITYCODE_RSRVSTR4")+"不一致！");
                }
                
                result.put("RESULT_CODE", "1");
                result.put("RESULT_INFO", "号码【"+serialNumber+"】预占成功！");
                IDataset resDataSet = new DatasetList();
                IData resTmp = new DataMap();
                resTmp.put("RES_CODE", serialNumber);
                resTmp.put("RES_TYPE_CODE", restypecode);
                resTmp.put("RES_TYPE", restype);
                resDataSet.add(resTmp);

                result.put("RES_LIST", resDataSet);
            }

        }
        return result;
    }
    
}
