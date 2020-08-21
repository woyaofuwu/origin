
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.buildrequestdata;

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
import com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata.InformationManageData;
import com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata.InformationManageRequestData;

public class BuildInformationManageRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildInformationManageRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        InformationManageRequestData reqData = (InformationManageRequestData) brd;
        IDataset dealList = new DatasetList(data.getString("editTable"));
        if (IDataUtil.isNotEmpty(dealList))
        {
            List<InformationManageData> infos = new ArrayList<InformationManageData>();
            for (int i = 0; i < dealList.size(); i++)
            {
                IData input = dealList.getData(i);
                InformationManageData info = new InformationManageData();
                String Tag = input.getString("ENABLE_TAG_BOX_TEXT");
                if ("提示".equals(Tag))
                {
                    Tag = "1";
                }
                else
                {
                    Tag = "0";
                }
                info.setEnableTag(Tag);
                info.setNoticeContent(input.getString("NOTICE_CONTENT"));
                info.setTag(input.getString("tag"));
                info.setRemark(input.getString("REMARK"));
                info.setTradeId(input.getString("TRADE_ID"));
                info.setStartDate(input.getString("START_DATE"));
                infos.add(info);
            }
            reqData.setFormationInfo(infos);
        }
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new InformationManageRequestData();
    }

}
