/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata.FilteIncomePhoneSendSMSTradeReqData;

/**
 * @CREATED by gongp@2014-4-28 修改历史 Revision 2014-4-28 上午11:31:29
 */
public class BuildFilteIncomePhoneSendSMSTradeReqData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub

        FilteIncomePhoneSendSMSTradeReqData reqData = (FilteIncomePhoneSendSMSTradeReqData) brd;

        reqData.setOpenfuncRadio(param.getString("cond_OPENFUNC_RADIO"));
        reqData.setSendSmsTag(param.getString("SENDSMS_TAG"));

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new FilteIncomePhoneSendSMSTradeReqData();
    }

}
