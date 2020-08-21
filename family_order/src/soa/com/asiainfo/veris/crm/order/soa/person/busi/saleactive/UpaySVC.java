
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


public class UpaySVC extends CSBizService
{
  
    public IData tradeDMSIntf(IData params) throws Exception
    {
    	IData intfResult = new DataMap();
        String serialNumber=params.getString("SERIAL_NUMBER","");
        if(serialNumber.isEmpty())
        {
            CSAppException.appError("2005", "手机号码不能为空");
        }
        
		IData data = UcaInfoQry.qryUserInfoBySn(serialNumber);
		
		if(IDataUtil.isNotEmpty(data))
		{				
			IDataset activecomm = CommparaInfoQry.getCommByParaAttr("CSM", "1920",CSBizBean.getTradeEparchyCode());
	        if(IDataUtil.isNotEmpty(activecomm))
	        {
		        String productId = activecomm.getData(0).getString("PARAM_CODE");//产品id
		        String packageId = activecomm.getData(0).getString("PARA_CODE1");//活动id
		        int limit_number = Integer.parseInt(activecomm.getData(0).getString("PARA_CODE2","0"));//客户名额
	        	String custgrouparm = activecomm.getData(0).getString("PARA_CODE3");//目标客户群
	        	String  userid = data.getString("USER_ID","");
				IData param1 = new DataMap();
				param1.put("USER_ID", userid);
				param1.put("TROOP_ID",custgrouparm);
				IDataset isaim = Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID", param1);
				
				if(IDataUtil.isEmpty(isaim))
		        {							
					 intfResult.put("X_RESULTCODE", "2001");
		             intfResult.put("X_RESULTINFO", "非目标客户群");
		             intfResult.put("TRADE_ID","-1");	 
		             return intfResult;
		        }
				
				param1.clear();
				param1.put("USER_ID", userid);
				param1.put("TROOP_ID",custgrouparm);
				IDataset isaims = Dao.qryByCode("TF_SM_TROOP_MEMBER", "SEL_BY_USERID_TROOPID_DATE", param1);
				//order by start_date
				if(IDataUtil.isEmpty(isaims))
		        {							
					 intfResult.put("X_RESULTCODE", "2003");
		             intfResult.put("X_RESULTINFO", "不在活动期间");
		             intfResult.put("TRADE_ID","-1");
		             return intfResult;
		        }
				
				param1.clear();
				param1.put("PRODUCT_ID", productId);
				param1.put("USER_ID",userid);
				param1.put("PACKAGE_ID",packageId);
				IDataset saleactive = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_VALIDSALEACTIVE_PRODUCTID", param1);
				//sysdate<end_date
				if(IDataUtil.isNotEmpty(saleactive))
				{
					intfResult.put("X_RESULTCODE", "2002");
		            intfResult.put("X_RESULTINFO", "已经办理过");
		            intfResult.put("TRADE_ID","-1");
		            return intfResult;
				}
				
				if(limit_number>0)
				{
					param1.clear();
					param1.put("PRODUCT_ID", productId);
					param1.put("PACKAGE_ID",packageId);
					IDataset saleactivenum = Dao.qryByCode("TF_F_USER_SALE_ACTIVE", "SEL_BY_PROID_PACID", param1);	
					//sysdate<end_date
					if(IDataUtil.isNotEmpty(saleactivenum))
					{
						if(saleactivenum.size()==limit_number||saleactivenum.size()>limit_number)
						{
							intfResult.put("X_RESULTCODE", "2004");
				            intfResult.put("X_RESULTINFO", "客户名额已满");
				            intfResult.put("TRADE_ID","-1");
				            return intfResult;
						}
					}
				}
		 				
	            try
	            {  
	            	param1.clear();
	            	param1.put("SERIAL_NUMBER", serialNumber);
	            	param1.put("PRODUCT_ID", productId);
	            	param1.put("PACKAGE_ID", packageId);
	            	String inModeCode = CSBizBean.getVisit().getInModeCode();
	            	String TRADE_STAFF_ID = CSBizBean.getVisit().getStaffId();
	            	String TRADE_DEPART_ID = CSBizBean.getVisit().getDepartId();
	            	param1.put("IN_MODE_CODE", inModeCode);
	            	param1.put("TRADE_STAFF_ID", TRADE_STAFF_ID);
	            	param1.put("TRADE_DEPART_ID", TRADE_DEPART_ID);
	            	
	                IData head = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", param1).getData(0);
	                if(!head.getString("TRADE_ID").isEmpty()&&!head.getString("TRADE_ID").equals("-1"))
	                {         	
	                	intfResult.put("X_RESULTCODE", "0");
	                	intfResult.put("X_RESULTINFO", "成功");
	                	intfResult.put("TRADE_ID",head.getString("TRADE_ID"));
	                    return intfResult;
	                }
	            }
	            catch (Exception ex)
	            {
	                intfResult.put("X_RESULTCODE", "2005");
	                intfResult.put("X_RESULTINFO", "其他异常"); 
	                intfResult.put("TRADE_ID","-1");
	                return intfResult;
	            }

	        }
	        else
	        {
	            CSAppException.appError("2005", "用户没有可办理的营销活动");
	        } 
		}
		else
		{
			  CSAppException.appError("2005", "用户信息不存在");
		}
		
		return intfResult;
    }

}
