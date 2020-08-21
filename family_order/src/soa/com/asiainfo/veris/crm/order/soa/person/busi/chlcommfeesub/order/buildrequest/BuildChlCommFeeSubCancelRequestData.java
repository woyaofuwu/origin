/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.chlcommfeesub.order.requestdata.ChlCommFeeSubCancelRequestData;

/**
 * @CREATED by gongp@2014-4-15 修改历史 Revision 2014-4-15 下午03:10:24
 */
public class BuildChlCommFeeSubCancelRequestData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        ChlCommFeeSubCancelRequestData reqData = (ChlCommFeeSubCancelRequestData) brd;

        reqData.setNewTrade(param.getString("chlInfo_NEW_TRADE"));
        reqData.setPhoneSub(param.getString("chlInfo_PHONE_SUB"));
        reqData.setCustType(param.getString("chlInfo_CUST_TYPE"));

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChlCommFeeSubCancelRequestData();
    }

}
