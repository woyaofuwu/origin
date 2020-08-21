package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.undofinish;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.NoPhoneWideUserCreateBean;

/**
 * 宽带撤单释放宽带账号资源
 * @author yuyj3
 *
 */
public class cancelWideAccountReleaseAction implements ITradeFinishAction {

	@Override
	public void executeAction(IData mainTrade) throws Exception {
		
		String widenetAcctId = mainTrade.getString("SERIAL_NUMBER").substring(3);

        NoPhoneWideUserCreateBean bean= BeanManager.createBean(NoPhoneWideUserCreateBean.class);
        
        //将宽带账号修改为空闲状态
        bean.updateWideNetAccoutState(widenetAcctId,"0","","","",null);
    	
	}

}
