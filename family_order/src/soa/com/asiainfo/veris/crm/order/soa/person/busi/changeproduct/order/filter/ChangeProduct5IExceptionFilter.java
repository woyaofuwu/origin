
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.filter;

import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

/**
 * 
 * 
 * 
 */
public class ChangeProduct5IExceptionFilter implements IFilterException
{
	
	protected static final Logger log = Logger.getLogger(ChangeProduct5IExceptionFilter.class);
	
    public IData transferException(Throwable e, IData input) throws Exception
    {
    	//String strInModeCode = CSBizBean.getVisit().getInModeCode();
    	
    	try {
    		String error =  Utility.parseExceptionMessage(e); 
			String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			if(errorArray.length < 2)
			{
				throw new Exception(e);
			}
			String strException = errorArray[0];
			String strExceptionMessage = errorArray[1];

			boolean bEM = strExceptionMessage.contains("#产品依赖互斥判断");
			String[] errorMessage = strExceptionMessage.split("\\|");
			if(bEM && errorMessage.length == 9)
			{
				IData IDKey = new DataMap();
				IDKey.put("服务", "0");
				IDKey.put("优惠", "1");
				
				String strKeyA = errorMessage[1];
				String strElementIdA = errorMessage[2];
				String strElementNameA = errorMessage[3];
				
				String strKeyB = errorMessage[5];
				String strElementIdB = errorMessage[6];
				String strElementNameB = errorMessage[7];
				
				String strExceptionMessage0 = errorMessage[8];
				
				String strValueA = IDKey.getString(strKeyA);
				String strValueB = IDKey.getString(strKeyB);
				
				boolean bEM0 = strExceptionMessage0.contains("互斥");
				if(bEM0)
				{
					IDKey.put("X_LIMITTAG", "1");
				}else
				{
					IDKey.put("X_LIMITTAG", "0");
				}
				IDKey.put("X_ELEMEMTYPEA", strValueA);
				IDKey.put("X_ELEMEMTYPEB", strValueB);
				IDKey.put("X_ELEMEMTA", strElementIdA);
				IDKey.put("X_NAMEA", strElementNameA);
				IDKey.put("X_ELEMEMTB", strElementIdB);
				IDKey.put("X_NAMEB", strElementNameB);
				/*X_LIMITTAG={0}  					0 依赖，1互斥，
				  X_ELEMEMTYPEA={0}  				A元素类型：0 服务，1 优惠
				  X_ELEMEMTYPEB={1}  				B元素类型：0 服务，1 优惠
				  X_ELEMEMTA={22}   				A元素ID
			      X_ELEMEMTB={902}  				B元素ID
				  X_NAMEA={手机上网} 				A元素名称
				  X_NAMEB={移动数据流量标准套餐}		B元素名称
				  X_TAG3={0}						0：不可退订，1：可退订
				  */
				
				String strModifyTag = input.getString("MODIFY_TAG", "");
				if("0".equals(strModifyTag) || "1".equals(strModifyTag))
				{
					
					String strXtag3 = "0";
					String strOderId = "";
					IDataset results = new DatasetList();
					
					try {
						
						String strSn= input.getString("SERIAL_NUMBER", "");
						String strElementTypeCode = "D";
						if("0".equals(strValueB))
						{
							strElementTypeCode = "S";
						}
						
						DataBusManager.removeDataBus();
						/*UcaData uca = UcaDataFactory.getNormalUca(strSn);
						uca.setUserSvcs(null);
						uca.setUserDiscnts(null);
						uca.setUserAttrs(null);*/
						 
						/*OrderDataBus dataBus = DataBusManager.getDataBus();
						dataBus.setOrderId("");*/
						
						/*IData param = new DataMap();
				        param.put("SERIAL_NUMBER", strSn);
				        param.put("CLEAR_TYPE", "0");
				        IDataset dataset = CSAppCall.call("CS.UcaCookieClearSVC.clear", param);*/
						
						IData dtElement = new DataMap();
						//dtElement.put("ORDER_TYPE_CODE", "110");
						//dtElement.put("TRADE_TYPE_CODE", "110");
						dtElement.put("SERIAL_NUMBER", strSn);
						dtElement.put("ELEMENT_ID", strElementIdB);
						dtElement.put("ELEMENT_TYPE_CODE", strElementTypeCode);
						dtElement.put("MODIFY_TAG", "1");
						dtElement.put("BOOKING_TAG", "0");
						dtElement.put("PRE_TYPE", "1");
						//dtElement.put("X_TRANS_CODE", "SS.ChangeProductRegSVC.ChangeProduct");
						CSBizBean.getVisit().setInModeCode("0");
						
						results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", dtElement);
						IData temp = results.first();
				        strOderId = temp.getString("ORDER_ID", "");
				        if (StringUtils.isBlank(strOderId))//如果未包含ORDER_ID，则代表失败了   0：不可退订，1：可退订
			        	{
				        	strXtag3 = "0";
			        	}else
			        	{
			        		strXtag3 = "1";
			        	}
					} catch (Exception e2) 
					{
						strXtag3 = "0";
//						//log.info("(e);
					 if (log.isDebugEnabled())
		     	        {
		            		 //log.info("(e);
		     	        }   
					}
					
					IDKey.put("X_TAG3", strXtag3); // 0：不可退订，1：可退订
					
				}
				
				input.put("X_ELEMEMT", IDKey);
				input.putAll(IDKey);
				input.put("X_RESULTCODE", strException);
		    	input.put("X_RESULTINFO", strExceptionMessage);
	
			}else
			{
				throw new Exception(e);
			}			
		} catch (Exception e2) 
		{	
			throw new Exception(e);
		}
	    
    	return input;
        
    }

}
