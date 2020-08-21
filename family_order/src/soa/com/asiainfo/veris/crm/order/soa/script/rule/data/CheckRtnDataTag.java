package com.asiainfo.veris.crm.order.soa.script.rule.data;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;



public class CheckRtnDataTag extends BreBase implements IBREScript {

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        boolean bResult = false;
        int balance = 0;
        String serialNumber = databus.getString("SERIAL_NUMBER","");
        if("".equals(serialNumber))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户号码信息为空！");
        }
        // 判断用户流量是否存在余额
        balance = getUserDataBalanceBySN(serialNumber);
        if (balance > 0)
        {
            databus.put("X_CHECK_TAG", 1);
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, "该用户流量有结余（如需清退流量请先到账管界面办理），是否要继续业务的办理？<br/>选择【确定】继续办理业务，选择【取消】终止办理业务。");
        }

        return bResult;
    }
    
	public int getUserDataBalanceBySN(String serialNumber) throws Exception {
		 		
		IData param = new DataMap();
		int balance = 0 ;
		param.put("SERIAL_NUMBER", serialNumber);
		IDataOutput resultSetOut = CSAppCall.callAcct(
				"AM_OUT_queryBalanceDetail", param, false);
        if ("0".equals(resultSetOut.getHead().getString("X_RESULTCODE")))
        {
			IDataset dataset = resultSetOut.getData();
			if (IDataUtil.isNotEmpty(dataset)) {
			    balance = Integer.parseInt(dataset.getData(0).getString("TOTAL_BALANCE","0"));
				return balance;
			}
			else{
				CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_OUT_queryBalanceDetail异常:"+resultSetOut);
				return balance;
			}
        }
        else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用接口AM_OUT_queryBalanceDetail异常:"+resultSetOut);
        	return balance;
        }
        
	}
}
