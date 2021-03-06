package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem.order.finish;

import org.apache.log4j.Logger;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.org.apache.commons.lang3.StringUtils; 
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHBusiModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.TopSetBoxSVC;

/**
 * REQ201505210004 FTTH光猫管理
 * 对于管理的光猫需要提供光猫串号信息给华为接口 
 */
public class OccupyModemTerminalAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(OccupyModemTerminalAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        IData param = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String rsrv_value_code = "FTTH";
        IDataset tradeOtherInfoSet = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
        if(DataSetUtils.isNotBlank(tradeOtherInfoSet)){
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
	        String custName = String.valueOf(assureCustData.getString("CUST_NAME"));
        	for(int i = 0 ; i < tradeOtherInfoSet.size() ; i++){
        		String res_id = tradeOtherInfoSet.getData(i).getString("RSRV_STR1","");
        		if(StringUtils.isNotEmpty(res_id)){
        			if(BofConst.MODIFY_TAG_ADD.equals(tradeOtherInfoSet.getData(i).getString("MODIFY_TAG"))){
        				logger.info("============cxy============MODIFY_TAG_ADD 实占新光猫");
        				//实占新光猫
            			param.put("TRADE_ID",tradeId);
                        param.put("RES_ID", res_id);//串号 
                        param.put("SERIAL_NUMBER",serialNumber);
                        TopSetBoxSVC topSetBoxSvc = new TopSetBoxSVC();
                        param.put("RES_NO", res_id);
                		param.put("BILL_ID", serialNumber);
                		param.put("CUST_NAME",  custName);
                		topSetBoxSvc.updateModem(param);
        			}else if(BofConst.MODIFY_TAG_DEL.equals(tradeOtherInfoSet.getData(i).getString("MODIFY_TAG"))){
        				logger.info("============cxy============MODIFY_TAG_DEL 退还老光猫");
        				//退还老光猫
            			param.put("TRADE_ID",tradeOtherInfoSet.getData(i).getString("RSRV_STR12",tradeId));
                        param.put("RES_ID", res_id);//串号 
                        param.put("SERIAL_NUMBER",serialNumber);
                        param.put("RES_NO", res_id);
                		param.put("BILL_ID", serialNumber);
                		param.put("CUST_NAME",  custName);
                		param.put("OTHER_TRADE_ID", tradeOtherInfoSet.getData(i).getString("RSRV_STR12",tradeId));
                		param.put("UPDATE_STAFF_ID", tradeOtherInfoSet.getData(i).getString("UPDATE_STAFF_ID"));
                		param.put("UPDATE_DEPART_ID", tradeOtherInfoSet.getData(i).getString("UPDATE_DEPART_ID"));
                		if("3".equals(tradeOtherInfoSet.getData(i).getString("RSRV_TAG1"))){//申领模式  0租赁，1购买，2赠送 ,3自备
                			CSAppException.apperr(CrmCommException.CRM_COMM_103, "自备的光猫不予更换"); 
                		}
                		boolean rtnFlag = FTTHBusiModemManageBean.returnFtthModem(param);
                		if(!rtnFlag){
                			CSAppException.apperr(CrmCommException.CRM_COMM_103 , "调终端接口返回失败，请找终端厂商核查！");
			   			}
        			}
        		}else{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户"+tradeOtherInfoSet.getData(i).getString("USER_ID")+"光猫串号未录入，不允许做此操作"); 
        		}
        	}
        } 
    }
}
