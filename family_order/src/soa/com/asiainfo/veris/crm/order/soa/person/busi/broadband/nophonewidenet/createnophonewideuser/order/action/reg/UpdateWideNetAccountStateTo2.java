
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.reg;

import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.NoPhoneWideUserCreateBean;

public class UpdateWideNetAccountStateTo2 implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

        String widenetAcctId = btd.getRD().getUca().getSerialNumber();
        
        widenetAcctId = widenetAcctId.substring(3);

        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);
        
        //全部校验将新账号修改为预占
        bean.updateWideNetAccoutState(widenetAcctId,"2",null,SysDateMgr.getSysTime(),null,null);
    }

}
