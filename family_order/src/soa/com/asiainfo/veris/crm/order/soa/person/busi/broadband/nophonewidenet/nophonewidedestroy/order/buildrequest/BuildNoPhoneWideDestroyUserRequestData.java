
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.requestdata.NoPhoneWideDestroyUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.buildrequest.BuildDestroyUserNowRequestData;

public class BuildNoPhoneWideDestroyUserRequestData extends BuildDestroyUserNowRequestData implements IBuilder
{
	
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(data, brd);
        
        NoPhoneWideDestroyUserRequestData reqData = (NoPhoneWideDestroyUserRequestData) brd;
        
        reqData.setCreateWideUserTradeId(data.getString("CREATE_WIDEUSER_TRADE_ID", ""));
        
        reqData.setRemark(data.getString("REMARK", ""));
        
        reqData.setRemove_reason_code(data.getString("REMOVE_REASON_CODE", "")); // 销户原因
        reqData.setRemove_reason(data.getString("REMOVE_REASON", ""));
        reqData.setScore(data.getString("SCORE", "0"));
        reqData.setYHFH(data.getBoolean("IS_YHFH_USER", false));// 是否影号副号
        
        String serialNumber = data.getString("SERIAL_NUMBER","");
        
        reqData.setSerialNumberA(serialNumber);
        reqData.setModemFee(data.getString("MODEM_FEE","0"));
        reqData.setModermReturn(data.getString("MODEM_RETUAN","0"));    //默认不退
        reqData.setModemMode(data.getString("MODEM_MODE","")); //默认空
        reqData.setModemFeeState(data.getString("MODEM_FEE_STATE","")); //默认空
        reqData.setWideType(data.getString("WIDE_TYPE_CODE","1"));
        String backFee=data.getString("BACK_FEE","0");
        if(backFee!=null &&!"0".equals(backFee)){
        	reqData.setBackFee(backFee);
        }
        
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new NoPhoneWideDestroyUserRequestData();
    }
     
}
