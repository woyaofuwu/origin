package com.asiainfo.veris.crm.order.soa.person.busi.hemuterminal.order.action;  

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHBusiModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.TopSetBoxSVC;

/**
 * 
 * @author Administrator 和目终端占用、退机、换机
 *
 */
public class OccupyBusiTerminalAction implements ITradeFinishAction
{
	 private static Logger logger = Logger.getLogger(OccupyBusiTerminalAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String actionType = mainTrade.getString("RSRV_STR4");//1:换，2：退，3：领
    	if("3".equals(actionType)){//申领
    		occupyTerminal(mainTrade);
			//实占终端
    	}else if("2".equals(actionType)){//退机
    		returnTerminal(mainTrade);
    	}else if("1".equals(actionType)){//换机
    		returnTerminal(mainTrade);
    		occupyTerminal(mainTrade);
    	}
        
        
        
        
        
       
    }
	/**
	 * @Description：
	 * @param:@param mainTrade
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-23上午10:24:54
	 */
	private void returnTerminal(IData mainTrade) throws Exception {
	   	IData param = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("RSRV_STR3");
        String res_id = mainTrade.getString("RSRV_STR5");
        param.put("TRADE_ID",tradeId);
        param.put("RES_ID", res_id);//串号 
        param.put("SERIAL_NUMBER",serialNumber);
        param.put("RES_NO", res_id);
		param.put("BILL_ID", serialNumber);
		IData assureUserData = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (assureUserData.isEmpty())
		{
			CSAppException.apperr(CustException.CRM_CUST_134, serialNumber);
		}
		String cust_id = assureUserData.getString("CUST_ID");
		IData assureCustData = UcaInfoQry.qryCustInfoByCustId(cust_id);
		if (assureCustData.isEmpty())
		{
			CSAppException.apperr(CustException.CRM_CUST_134, serialNumber);
		}
		String custName =  String.valueOf(assureCustData.getString("CUST_NAME"));
		param.put("CUST_NAME",  custName);
	
		param.put("UPDATE_DEPART_ID", "");
		logger.info("调用资源接口退还终端接口参数："+param.toString());
		boolean rtnFlag = FTTHBusiModemManageBean.returnFtthModem(param);
		if(!rtnFlag){
			CSAppException.apperr(CrmCommException.CRM_COMM_103 , "调终端接口返回失败，请找终端厂商核查！");
		}
    		
		
	}
	/**
	 * @Description：
	 * @param:@param mainTrade
	 * @return void
	 * @throws Exception 
	 * @throws
	 * @Author :tanzheng
	 * @date :2018-4-23上午10:24:30
	 */
	private void occupyTerminal(IData mainTrade) throws Exception {
	    IData param = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("RSRV_STR3");
        String res_id = mainTrade.getString("RSRV_STR1");
		param.put("TRADE_ID",tradeId);
        param.put("RES_ID", res_id);//串号 
        param.put("SERIAL_NUMBER",serialNumber);
        TopSetBoxSVC topSetBoxSvc = new TopSetBoxSVC();
        param.put("RES_NO", res_id);
		param.put("BILL_ID", serialNumber);
		IData assureUserData = UcaInfoQry.qryUserInfoBySn(serialNumber.substring(0,11));
		if (assureUserData.isEmpty())
		{
			CSAppException.apperr(CustException.CRM_CUST_134, serialNumber.substring(0,11));
		}
		String cust_id = assureUserData.getString("CUST_ID");
		IData assureCustData = UcaInfoQry.qryCustInfoByCustId(cust_id);
		if (assureCustData.isEmpty())
		{
			CSAppException.apperr(CustException.CRM_CUST_134, serialNumber.substring(0,11));
		}
		String custName =  String.valueOf(assureCustData.getString("CUST_NAME"));
		param.put("CUST_NAME",  custName);
		logger.info("调用资源接口实占终端接口参数："+param.toString());
		topSetBoxSvc.updateModem(param);
		
	}
}
