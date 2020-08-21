/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.entitycard.order.requestdata.ChangEntityCardRequestData;

/**
 * @CREATED by gongp@2014-5-29 修改历史 Revision 2014-5-29 下午05:24:48
 */
public class BuildChangEntityCardRequestData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangEntityCardRequestData reqData = (ChangEntityCardRequestData) brd;

        reqData.setOldCardNo(param.getString("OLD_CARD_NO"));
        reqData.setNewCardNo(param.getString("NEW_CARD_NO"));
        reqData.setRemark(param.getString("REMARK"));
    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChangEntityCardRequestData();
    }

}
