
package com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.builderquest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.onecardncodes.order.requestdata.OneCardNCodesChangeCardReqData;

public class BuildOneCardNCodesChangeCardReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        OneCardNCodesChangeCardReqData oneCardRD = (OneCardNCodesChangeCardReqData) brd;
        oneCardRD.setSerialNum(param.getString("SERIAL_NUMBER", ""));
        oneCardRD.setOtherSN(param.getString("OTHERSN", ""));
        oneCardRD.setSimCardNoM(param.getString("SIM_CARD_NO_M", ""));
        oneCardRD.setSimCardNoO(param.getString("SIM_CARD_NO_O", ""));
        oneCardRD.setImsi(param.getString("commInfo_IMSI", ""));
        oneCardRD.setImsiO(param.getString("commInfo_IMSI1", ""));
        oneCardRD.setKi_a(param.getString("KI_A", ""));
        oneCardRD.setKi_b(param.getString("KI_B", ""));
        if("false".equals(param.getString("IS_MAIN", ""))){
            oneCardRD.setTag(false);
        }else{
            oneCardRD.setTag(true);

        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new OneCardNCodesChangeCardReqData();
    }

}
