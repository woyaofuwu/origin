
package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.CreateNpUserTradeBean;

public class ModifyOpenDateFinishAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE","");
        String finishDate=SysDateMgr.getSysTime();//mainTrade.getString("FINISH_DATE");
        // 携转标志：0-非携转、1-携入未生效、2-携入被拒、3-已携入、4-携入已销、5-携出中、6-已携出、7-携出已销
         
        if("40".equals(tradeTypeCode)){
        	CreateNpUserTradeBean bean = BeanManager.createBean(CreateNpUserTradeBean.class);
        	IData inparams=new DataMap();
        	inparams.put("TRADE_ID", tradeId);
        	inparams.put("FINISH_DATE", finishDate);
        	bean.updDiscntTradeStartdate(inparams);
        	bean.updSvcTradeStartdate(inparams);
        	bean.updUserTradeOpendate(inparams);
        	bean.updCreditTradeStartdate(inparams);
        }
    } 
}
