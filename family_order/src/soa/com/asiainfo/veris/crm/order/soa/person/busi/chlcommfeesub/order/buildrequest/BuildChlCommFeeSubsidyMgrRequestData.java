/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.requestdata.ChlCommFeeSubsidyMgrRequestData;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午03:10:43
 */
public class BuildChlCommFeeSubsidyMgrRequestData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

        ChlCommFeeSubsidyMgrRequestData reqData = (ChlCommFeeSubsidyMgrRequestData) brd;

        reqData.setNewTrade(param.getString("chlInfo_NEW_TRADE"));
        reqData.setPhoneSub(param.getString("chlInfo_PHONE_SUB"));
        reqData.setCustType(param.getString("chlInfo_CUST_TYPE"));

        reqData.setChlCode(param.getString("chlInfo_CHL_CODE"));
        reqData.setChlLevel(param.getString("chlInfo_CHL_LEVEL"));
        reqData.setChlName(param.getString("chlInfo_CHL_NAME"));
        reqData.setChlType(param.getString("chlInfo_CHL_TYPE"));
        reqData.setStaffId(param.getString("chlInfo_STAFF_ID"));

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChlCommFeeSubsidyMgrRequestData();
    }

}
