
package com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.singlenummultidevicemanage.order.requestdata.SingleNumMultiDeviceManageRequestData;

public class BuildSingleNumMultiDeviceManageReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	SingleNumMultiDeviceManageRequestData reqData = (SingleNumMultiDeviceManageRequestData) brd;
        reqData.setAuxNickName(param.getString("AUX_NICK_NAME"));
        reqData.setSerialNmberB(param.getString("SERIAL_NUMBER_B"));
        reqData.setSimCardNoB(param.getString("SIM_CARD_NO_B"));
        reqData.setAuxType(param.getString("AUX_TYPE"));
        reqData.setAuxICCID(param.getString("SIM_CARD_NO_B"));
        reqData.setAuxEID(param.getString("EID"));
        reqData.setAuxIMEI(param.getString("AUX_IMEI"));
        reqData.setOperCode(param.getString("OPER_CODE"));
        reqData.setReqType(param.getString("REQ_TYPE",""));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new SingleNumMultiDeviceManageRequestData();
    }

}
