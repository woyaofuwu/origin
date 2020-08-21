package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.order.finish;  

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
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.TopSetBoxSVC;

/**
 * REQ201505210004 FTTH光猫管理
 * 对于管理的光猫需要提供光猫串号信息给华为接口
 * @CREATED by lijun17@2016-4-7
 */
public class OccupySupplementModemAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(OccupySupplementModemAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        IData param = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String rsrv_value_code = "FTTH";
        IDataset tradeOtherInfoSet = null;
        tradeOtherInfoSet = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
        if(DataSetUtils.isBlank(tradeOtherInfoSet)){
        	rsrv_value_code = "FTTH_GROUP";
        	tradeOtherInfoSet = TradeOtherInfoQry.getTradeOtherByTradeIdRsrvCode(tradeId,rsrv_value_code);
        }
        if(DataSetUtils.isNotBlank(tradeOtherInfoSet)){
        	for(int i = 0 ; i < tradeOtherInfoSet.size() ; i++){
        		String res_id = tradeOtherInfoSet.getData(i).getString("RSRV_STR1","");
        		if(StringUtils.isNotEmpty(res_id)){
        			if(StringUtils.equals(tradeOtherInfoSet.getData(i).getString("RSRV_TAG5",""), "0") && BofConst.MODIFY_TAG_UPD.equals(tradeOtherInfoSet.getData(i).getString("MODIFY_TAG"))){
        				//实占新光猫
        				if(StringUtils.equals(rsrv_value_code, "FTTH_GROUP")){
        		        	serialNumber = tradeOtherInfoSet.getData(i).getString("RSRV_STR3");
        				}
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
            			param.put("TRADE_ID",tradeId);
                        param.put("RES_ID", res_id);//串号 
                        param.put("SERIAL_NUMBER",serialNumber);
                        TopSetBoxSVC topSetBoxSvc = new TopSetBoxSVC();
                        param.put("RES_NO", res_id);
                		param.put("BILL_ID", serialNumber);
                		param.put("CUST_NAME",  custName);
                		topSetBoxSvc.updateModem(param);
        			}
        		}else{
        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户"+tradeOtherInfoSet.getData(i).getString("USER_ID")+"光猫串号未录入，不允许做此操作"); 
        		}
        	}
        } 
    }
}
