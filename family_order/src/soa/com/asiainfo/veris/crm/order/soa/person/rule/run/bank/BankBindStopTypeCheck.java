/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.rule.run.bank;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by wukw3
 */
public class BankBindStopTypeCheck extends BreBase implements IBREScript
{
    private static final long serialVersionUID = -2721232911313840082L;

    /*
     * (non-Javadoc)
     */
    @Override
    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        // TODO Auto-generated method stub
    	
        String urltime = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_COMMPARA", 
				 new String[]{ "SUBSYS_CODE", "PARAM_ATTR", "PARAM_CODE" }, "PARA_CODE1", 
				 new String[]{ "CSM", "511", "STOP_TYPE" });
        if(urltime.indexOf(","+databus.getString("USER_STATE_CODESET")+",")>=0)
        {
   		
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 525007, "该手机处于停机状态，不能办理银联卡绑定业务！");
        	return true;
        }

        return false;
    }

}
