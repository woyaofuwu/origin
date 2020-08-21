package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder; 
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData; 
import com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.familyfixphone.order.requestdata.FamilyFixPhoneReqData;

public class BuildFamilyFixPhoneReqData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
    	FamilyFixPhoneReqData familyData = (FamilyFixPhoneReqData) brd;
    	familyData.setPageInfo(param);
//        // 添加新号码的三户资料
//        String sn = param.getString("SERIAL_NUMBER_PRE");
//        UcaData uca = DataBusManager.getDataBus().getUca(sn);
//        if (uca == null)
//        {
//            uca = UcaDataFactory.getNormalUca(sn);
//        }
//        DataBusManager.getDataBus().setUca(uca);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new FamilyFixPhoneReqData();
    }

}
