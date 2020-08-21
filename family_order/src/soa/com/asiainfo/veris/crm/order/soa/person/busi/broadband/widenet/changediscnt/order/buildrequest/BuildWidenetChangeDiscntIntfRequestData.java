
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changediscnt.order.requestdata.WidenetChangeDiscntRequestData;

public class BuildWidenetChangeDiscntIntfRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        WidenetChangeDiscntRequestData widenetChgDiscntReqData = (WidenetChangeDiscntRequestData) brd;
        widenetChgDiscntReqData.setSpcUserDiscnts(new DatasetList(param.getString("SPC_USER_DISCNTS")));
        widenetChgDiscntReqData.setSpcDelDiscntFee(param.getString("SPC_DEL_DISCNT_FEE", ""));
        widenetChgDiscntReqData.setSpcDelDiscntPaymentId(param.getString("SPC_DEL_DISCNT_PAYMENTID", ""));
        widenetChgDiscntReqData.setCampusTag(param.getString("CAMPUS_TAG", ""));
        makeFeeData(param, widenetChgDiscntReqData);
    }

    public BaseReqData getBlankRequestDataInstance()
    {

        return new WidenetChangeDiscntRequestData();
    }

    public void makeFeeData(IData param, WidenetChangeDiscntRequestData brd) throws Exception
    {
        FeeData feeData = new FeeData();
        feeData.setFeeMode("2");
        feeData.setFeeTypeCode(param.getString("DATA8"));
        feeData.setFee(param.getString("DATA4"));
        feeData.setOldFee(param.getString("DATA4"));
        brd.addFeeData(feeData);
    }
}
