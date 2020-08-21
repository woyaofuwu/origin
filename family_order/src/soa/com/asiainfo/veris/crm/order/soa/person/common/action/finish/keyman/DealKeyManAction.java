/**
 * 
 */
package com.asiainfo.veris.crm.order.soa.person.common.action.finish.keyman;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 *
 * 
 * @CREATED by gongp@2014-10-29
 * 修改历史
 * Revision  2014-10-29 下午02:47:58
 */
public class DealKeyManAction implements ITradeFinishAction
{

    /* (non-Javadoc)
     */
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        IData param = new DataMap();

        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        
        param.put("SERIAL_NUMBER", serialNumber);
        
        
        CSAppCall.call("CM.GroupMemberSVC.qryGroupKeyMan", param);
        
    }

    
    
}
