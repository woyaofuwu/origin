
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.DelFamilyNetMemberReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;

public class BuildDelFamilyNetMemberReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        DelFamilyNetMemberReqData reqData = (DelFamilyNetMemberReqData) brd;
        // TODO Auto-generated method stub
        // reqData.setEffectNow(param.getString("EFFECT_NOW", "NO"));
        reqData.setIsInterface(param.getString("IS_INTERFACE", "0"));
        IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));

        for (int i = 0, size = mebList.size(); i < size; i++)
        {
            IData mebData = mebList.getData(i);
            String serialNumberB = mebData.getString("SERIAL_NUMBER_B");
            String effectNow = mebData.getString("EFFECT_NOW", "");
            UcaData mebUca = UcaDataFactory.getNormalUca(serialNumberB);

            FamilyMemberData familyMeb = new FamilyMemberData();
            familyMeb.setUca(mebUca);
            familyMeb.setEffectNow(effectNow);

            reqData.addMebUca(familyMeb);
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new DelFamilyNetMemberReqData();
    }

}
