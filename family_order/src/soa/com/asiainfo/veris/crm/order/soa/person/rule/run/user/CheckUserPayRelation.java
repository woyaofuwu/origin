package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;

public class CheckUserPayRelation extends BreBase implements IBREScript
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String acctId = databus.getString("ACCT_ID");
        String startCycId = SysDateMgr.getSysDateYYYYMMDD().substring(0, 6)+"01";
        IDataset dataset = PayRelaInfoQry.queryNowValidPayByAcctId(acctId, startCycId);// 查询用户宽带固话共线信息

        if (DataSetUtils.isNotBlank(dataset)&&dataset.size()>1)
        {
        	return false;
        }else{
        	return true;
        }

    }
}


