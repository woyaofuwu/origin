
package com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changephone.order.requestdata.ChangePhonePreRegisterRequestData;

public class BuildChangePhonePreRegisterRequestData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        ChangePhonePreRegisterRequestData cpReqData = (ChangePhonePreRegisterRequestData) brd;

        cpReqData.setOldSerialNum(data.getString("OLD_SN"));
        cpReqData.setNewSerialNum(data.getString("NEW_SN"));
        cpReqData.setPsptId(data.getString("custInfo_PSPT_ID"));
        cpReqData.setPsptTypeCode(data.getString("custInfo_PSPT_TYPE_CODE"));
        cpReqData.setSynTag(data.getString("SYN_TAG"));
        cpReqData.setRemark(data.getString("REMARK"));
        cpReqData.setRsrvstr1(data.getString("REMARK", data.getString("RSRV_STR6")));
        cpReqData.setWhHandle(data.getString("WH_HANDLE"));
        IData syncInfo = (IData) data.get("SYNC_INFO");
        String moveInfo = data.getString("MOVE_INFO");
        cpReqData.setNewProvince(syncInfo.getString("NEW_PROVINCE"));
        cpReqData.setOldProvince(syncInfo.getString("OLD_PROVINCE"));
        cpReqData.setNewEparchy(syncInfo.getString("NEW_EPARCHY"));
        cpReqData.setOldEparchy(syncInfo.getString("OLD_EPARCHY"));
        cpReqData.setMoveInfo(moveInfo);

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new ChangePhonePreRegisterRequestData();
    }

}
