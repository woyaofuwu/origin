package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

public class CheckExistsWidenetDestroyOrder extends BreBase implements IBREScript{

	private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String errorInfo = "";
        IDataset iset = WidenetInfoQry.getWidenetOrderDestroyInfo(userId);
        if (IDataUtil.isNotEmpty(iset))
        {
        	errorInfo = "业务规则限制：该用户存在宽带预约拆机记录，不能办理该业务!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604033", errorInfo);
        }

        return false;
    	
    }
}
