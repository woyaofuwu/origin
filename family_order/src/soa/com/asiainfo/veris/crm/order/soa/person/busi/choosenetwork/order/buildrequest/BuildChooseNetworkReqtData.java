/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.choosenetwork.order.requestdata.ChooseNetworkReqData;

/**
 */
public class BuildChooseNetworkReqtData extends BaseBuilder implements IBuilder
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        ChooseNetworkReqData reqData = (ChooseNetworkReqData) brd;

        reqData.setOperType(param.getString("OPER_TYPE"));
        reqData.setCooperArea(param.getString("COOPER_AREA"));
        reqData.setCooperNet(param.getString("COOPER_NET"));

    }

    /*
     * (non-Javadoc)
     */
    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChooseNetworkReqData();
    }

}
