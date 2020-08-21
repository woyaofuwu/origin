
package com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.fixtelusermove.order.requestdata.FixTelUserMoveRequestData;

public class BuildFixTelUserMoveRequestData extends BaseBuilder implements IBuilder
{
    /**
     * 构建登记流程 业务数据输入，后续逻辑处理从RequestData中获取数据，即这里的brd
     */

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FixTelUserMoveRequestData fixTelUserMoveRD = (FixTelUserMoveRequestData) brd;
        fixTelUserMoveRD.setChangteleNotice(param.getString("CHANGE_NUMBER_MONTH", ""));
        fixTelUserMoveRD.getTelChangeData().setDetailAddress(param.getString("DETAIL_ADDRESS", ""));
        fixTelUserMoveRD.getTelChangeData().setRemark(param.getString("REMARK", ""));
        fixTelUserMoveRD.getTelChangeData().setSignPath(param.getString("SIGN_PATH", ""));
        fixTelUserMoveRD.getTelChangeData().setStandAddress(param.getString("STAND_ADDRESS", ""));
        fixTelUserMoveRD.getTelChangeData().setStandAddressCode(param.getString("STAND_ADDRESS_CODE", ""));
        fixTelUserMoveRD.getNumChangeData().setSerialNumber(param.getString("NEW_SERIAL_NUMBER", ""));
        fixTelUserMoveRD.getNumChangeData().setResTypeCode(param.getString("RES_TYPE_CODE", "N"));
        fixTelUserMoveRD.getNumChangeData().setResKindCode(param.getString("RES_KIND_CODE", ""));

    }

    /**
     * 定义requestData对象
     */

    public BaseReqData getBlankRequestDataInstance()
    {
        return new FixTelUserMoveRequestData();
    }
}
