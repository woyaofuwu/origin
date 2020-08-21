package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.person.busi.smartaddvalue.order.DredgeSmartNetworkIntfBean;

/**
 * @author 梁端刚
 * @version V1.0
 * @date 2020/4/21 22:36
 */
public class CheckExistsWidnet extends BreBase implements IBREScript {
    @Override
    public boolean run(IData databus, BreRuleParam paramData) throws Exception {
        String serialNumber = databus.getString("SERIAL_NUMBER");
        IData iparam = new DataMap();
        iparam.put("SERIAL_NUMBER",serialNumber);
        DredgeSmartNetworkIntfBean bean = BeanManager.createBean(DredgeSmartNetworkIntfBean.class);
        IData result = bean.checkQualificate(iparam);
        String resultcode = result.getString("X_RESULTCODE");
        if(!"0000".equals(resultcode)){
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "20200421", "业务受理前条件判断："+result.getString("X_RESULTINFO"));
        }
        return false;
    }
}
