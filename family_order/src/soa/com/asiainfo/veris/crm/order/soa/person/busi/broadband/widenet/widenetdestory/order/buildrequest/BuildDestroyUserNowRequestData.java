
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestory.order.requestdata.DestroyUserNowRequestData;

public class BuildDestroyUserNowRequestData extends BaseBuilder implements IBuilder
{
    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {
        DestroyUserNowRequestData reqData = (DestroyUserNowRequestData) brd;
        reqData.setRemove_reason_code(data.getString("REMOVE_REASON_CODE", "")); // 销户原因
        reqData.setRemove_reason(data.getString("REMOVE_REASON", ""));
        reqData.setScore(data.getString("SCORE", "0"));
        reqData.setYHFH(data.getBoolean("IS_YHFH_USER", false));// 是否影号副号
        String serialNumber = data.getString("AUTH_SERIAL_NUMBER","");
        if(serialNumber.indexOf("KD_")>-1) {//宽带账号
    		if(serialNumber.split("_")[1].length()>11)
    			reqData.setSerialNumberA(serialNumber);//商务宽带
    		else
    			reqData.setSerialNumberA(serialNumber.split("_")[1]);//个人账号
    	}
    	else {
    		if(serialNumber.length()>11)
    			reqData.setSerialNumberA("KD_"+serialNumber);//商务宽带
    		else
    			reqData.setSerialNumberA(serialNumber);
    	}
        reqData.setModemFee(data.getString("MODEM_FEE","0"));
        reqData.setModermReturn(data.getString("MODEM_RETUAN","0"));    //默认不退
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new DestroyUserNowRequestData();
    }

}
