/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: TipsForDestroyTDUser.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: wuhao5
 * @date: 2019-3-18 下午04:40:30 Modification History: Date Author Version Description
 *        -----* 2019-3-18 wuhao5 v1.0.0 修改原因
 */

public class TipsForDestroyEasyTD extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: wuhao5
     * @date: 2019-3-18 下午04:40:30 Modification History: Date Author Version Description
     *        -------------* 2019-3-18 wuhao5 v1.0.0 修改原因
     */
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String serialNumberA = databus.getString("SERIAL_NUMBER");
        IDataset TDUUinfos = RelaUUInfoQry.qryRelaUUBySerialNumberA(serialNumberA,"T2");
        if (IDataUtil.isNotEmpty(TDUUinfos) && TDUUinfos.size() > 0)
        {
            String msg = "该号码为TD一代无线固话业务对应的157号码，不能单独销户，如需对该业务销户请到固话拆机界面对该号码对应的0898+8位号码销户";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, -1, msg);
            return true;
        }
        return false;
    }

}
