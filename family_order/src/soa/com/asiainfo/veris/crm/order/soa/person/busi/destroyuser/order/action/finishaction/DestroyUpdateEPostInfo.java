package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.changeepostinfo.ModifyEPostInfoBean;

/** 
 * @ClassName:  
 * @Description: 用户销号时， 按陈悦茜要求清理邮件信息。BUG20170505153416个人电子发票设置界面已设置但看不到推送信息的BUG
 * @version: v1.0.0 
 * @author: wukw3
 * @date: 2017-05-08
 */
public class DestroyUpdateEPostInfo implements ITradeFinishAction {    

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String user_id = mainTrade.getString("USER_ID","");
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("USER_ID", user_id);
        IDataset EpostInfo = ModifyEPostInfoBean.qryEPostInfo(param);//是否有电子发票推送设置信息
        if (IDataUtil.isNotEmpty(EpostInfo)) {
        	ModifyEPostInfoBean.destroyUserdelMonPost(serialNumber, user_id); //清除电子发票推送设置信息
        }
    }
}
