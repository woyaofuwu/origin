/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.requestdata.SyncPayPromptReqData;

/**
 * @CREATED by gongp@2014-7-14 修改历史 Revision 2014-7-14 下午09:36:38
 */
public class BuildSyncPayPromptReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        SyncPayPromptReqData reqData = (SyncPayPromptReqData) brd;

        reqData.setWarningFee(param.getString("WARNING_FEE"));
        reqData.setModifyTag(param.getString("MODIFY_TAG"));
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new SyncPayPromptReqData();
    }

}
