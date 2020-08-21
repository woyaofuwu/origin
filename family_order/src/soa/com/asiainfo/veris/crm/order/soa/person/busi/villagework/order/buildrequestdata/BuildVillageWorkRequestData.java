
package com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.buildrequestdata;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.requestdata.VillageWorkData;
import com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.requestdata.VillageWorkRequestData;

public class BuildVillageWorkRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildVillageWorkRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        VillageWorkRequestData reqData = (VillageWorkRequestData) brd;
        IDataset serialInfos = new DatasetList(data.getString("serialData"));
        if (IDataUtil.isNotEmpty(serialInfos))
        {
            List<VillageWorkData> SerialInfoList = new ArrayList<VillageWorkData>();
            for (int i = 0; i < serialInfos.size(); i++)
            {
                IData serialInfo = serialInfos.getData(i);

                VillageWorkData newSerialInfo = new VillageWorkData();
                newSerialInfo.setSERIAL_NUMBER(serialInfo.getString("SERIAL_NUMBER"));
                newSerialInfo.setFlag(serialInfo.getString("tag"));
                SerialInfoList.add(newSerialInfo);

            }
            reqData.setSerNumInfo(SerialInfoList);
        }

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new VillageWorkRequestData();
    }

}
