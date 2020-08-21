
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changepasswd.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.orderdata.MainOrderData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class WidenetPswChgRegSVC extends OrderService
{
    private static final long serialVersionUID = 1L;

    public String getOrderTypeCode() throws Exception
    {
        if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
        	String serialNumber = input.getString("SERIAL_NUMBER");
        	String wSerialNumber = serialNumber.indexOf("KD_")>-1 ? serialNumber:"KD_" + serialNumber;
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber);
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
                if ("4".equals(wideType))
                {
                    input.put("ORDER_TYPE_CODE", "634");// 校园
                }
                else
                {
                    input.put("ORDER_TYPE_CODE", "607");
                }
            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_4);
            }
        }
        else
        {
            return this.input.getString("TRADE_TYPE_CODE", "607");
        }
        return this.input.getString("ORDER_TYPE_CODE", "");

    }

    public String getTradeTypeCode() throws Exception
    {
        if (this.input.getString("TRADE_TYPE_CODE", "").equals(""))
        {
        	String serialNumber = input.getString("SERIAL_NUMBER");
        	String wSerialNumber = serialNumber.indexOf("KD_")>-1 ? serialNumber:"KD_" + serialNumber;
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber);
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
                if ("4".equals(wideType))
                {
                    input.put("TRADE_TYPE_CODE", "634");// 校园
                }
                else
                {
                    input.put("TRADE_TYPE_CODE", "607");
                }

            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_4);
            }
        }
        return this.input.getString("TRADE_TYPE_CODE", "");
    }

    public void resetMainOrderData(MainOrderData orderData, BusiTradeData btd) throws Exception
    {

        orderData.setSubscribeType("300");
    }

    /**
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset tradeReg(IData input) throws Exception
    {

        if (!"KD_".equals(input.getString("SERIAL_NUMBER").substring(0, 3)))
        {
            input.put("SERIAL_NUMBER", "KD_" + input.getString("SERIAL_NUMBER"));
        }
        
        //短厅不能做校园宽带密码变更
        if ("5".equals( getVisit().getInModeCode()))
        {
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(input.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(widenetInfos))
            {
                String mima = widenetInfos.getData(0).getString("RSRV_STR2");
                
                if(mima.equals("4"))
                {
                    CSAppException.apperr(WidenetException.CRM_WIDENET_33);         
                }
            }
        }
        
        return super.tradeReg(input);      
    }
    
    public IData changeWidePassWord(IData data) throws Exception
    {
    	IData returnData = new DataMap();
    	String xMsg= "密码修改成功！";
    	String xCode= "0";
    	try {
            IDataset ds = CSAppCall.call( "SS.WidenetPswChgRegSVC.tradeReg", data);

    		if(IDataUtil.isNotEmpty(ds)){
    			if(!"".equals(ds.getData(0).getString("TRADE_ID","")))
    			{
    				returnData.put("X_RESULTCODE", xCode);
                	returnData.put("X_RECORDNUM", "1");
                	returnData.put("X_RESULTINFO", xMsg);
                	returnData.put("TRADE_ID", ds.getData(0).getString("TRADE_ID",""));
    			}
    			
    		}else{
    			returnData.put("X_RESULTCODE", "-1");
            	returnData.put("X_RECORDNUM", "1");
            	returnData.put("X_RESULTINFO", "密码修改失败！");
    		}
		} catch (Exception e) {
			returnData.put("X_RESULTCODE", "-1");
        	returnData.put("X_RECORDNUM", "1");
        	returnData.put("X_RESULTINFO", "错误信息："+e);
		}
    	

    	return returnData;
    }
    
    
}
