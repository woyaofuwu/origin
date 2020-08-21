/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct.order.requestdata.NcardsOneAcctSaleReqData;

/**
 * @CREATED by gongp@2014-5-15 修改历史 Revision 2014-5-15 下午03:27:33
 */
public class BuildNcardsOneAcctSaleReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        NcardsOneAcctSaleReqData reqData = (NcardsOneAcctSaleReqData) brd;

        reqData.setSerialNumberMain(param.getString("AUTH_SERIAL_NUMBER"));
        reqData.setSerialNumberSecond(param.getString("AUTH_SERIAL_NUMBER2"));
        reqData.setUserIdMain(param.getString("USER_ID_MAIN"));
        reqData.setUserIdSecond(param.getString("USER_ID_SECOND"));
        reqData.setStartCycId(param.getString("START_CYCLE_ID"));

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new NcardsOneAcctSaleReqData();
    }

}
