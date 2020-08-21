
package com.asiainfo.veris.crm.order.soa.person.rule.run.changecustowner;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

/**
 * 过户时验证客户是否存在手机支付功能 ===script_id='SEL_USERPLATSVCINFO_BY_BIZTYPECODE'; 
 */
public class CheckCustPhonePay extends BreBase implements IBREScript
{

    /**
     *  bizTypeCode="54"
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");
        //UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
        //String userId = uca.getUserId();
        IDataset dataSet = UserPlatSvcInfoQry.getPlatSvcService(userId);
       
        if (IDataUtil.isNotEmpty(dataSet))
        {
        	for(int i=0;i<dataSet.size();i++){
        		
                IDataset datas = UpcCall.querySpServiceAndProdByCond(null, null, "54", dataSet.getData(i).getString("SERVICE_ID"));
                if (IDataUtil.isNotEmpty(datas))
                {
                	   String erroInfo = "该用户已经订购了手机支付业务，请先去注销手机支付业务之后再来办理过户业务。";
                       BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "0", erroInfo);
                       return true;
                }
        }
        }
        return false;
    }

}
