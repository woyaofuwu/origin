/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.requestdata.NcardsOneAcctCancelReqData;

/**
 * @CREATED by gongp@2014-5-20 修改历史 Revision 2014-5-20 上午09:44:57
 */
public class BuildNcardsOneAcctCancelReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        NcardsOneAcctCancelReqData reqData = new NcardsOneAcctCancelReqData();

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new NcardsOneAcctCancelReqData();
    }

}
