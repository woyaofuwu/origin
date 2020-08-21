
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.requestdata.ChangeTrunkIdReqData;

public class BuildChangeTrunkIdReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        ChangeTrunkIdReqData changeTrunkIdRD = (ChangeTrunkIdReqData) brd;
        changeTrunkIdRD.setTrunkId(param.getString("TRUNK_ID"));
        changeTrunkIdRD.setSwitchId(param.getString("SWITCH_ID"));
        changeTrunkIdRD.setOldTrunkId(param.getString("OLD_TRUNK_ID"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new ChangeTrunkIdReqData();
    }

}
