
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyRejectRemindSvcBusiReqData;

public class BuildFamilyRejectRemindSvcBusi extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FamilyRejectRemindSvcBusiReqData reqData = (FamilyRejectRemindSvcBusiReqData) brd;
        reqData.setRejectMode(param.getString("REJECT_MODE"));
        if ("2".equals(param.getString("REJECT_MODE")))
        {
            IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));
            for (int i = 0, size = mebList.size(); i < size; i++)
            {
                IData meb = mebList.getData(i);
                String mebSn = meb.getString("SERIAL_NUMBER_B");
                UcaData mebUca = UcaDataFactory.getNormalUca(mebSn);
                reqData.addMebUca(mebUca);
            }
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyRejectRemindSvcBusiReqData();
    }

}
