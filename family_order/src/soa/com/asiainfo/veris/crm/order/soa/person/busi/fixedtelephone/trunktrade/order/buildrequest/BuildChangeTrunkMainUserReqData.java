
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.trunktrade.order.requestdata.ChangeTrunkMainUserReqData;

public class BuildChangeTrunkMainUserReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        ChangeTrunkMainUserReqData changeTrunkMainUserRD = (ChangeTrunkMainUserReqData) brd;
        changeTrunkMainUserRD.setNewMianSn(param.getString("NEW_MAIN_SN"));
        changeTrunkMainUserRD.setNewUca(UcaDataFactory.getNormalUca(param.getString("NEW_MAIN_SN"), false, false));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {

        return new ChangeTrunkMainUserReqData();
    }

}
