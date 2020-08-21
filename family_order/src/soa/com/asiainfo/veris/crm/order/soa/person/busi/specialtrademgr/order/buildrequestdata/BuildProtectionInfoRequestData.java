
package com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.buildrequestdata;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.specialtrademgr.order.requestdata.ProtectionInfoRequestData;

public class BuildProtectionInfoRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildProtectionInfoRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        ProtectionInfoRequestData reqData = (ProtectionInfoRequestData) brd;
        if (StringUtils.isNotEmpty(data.getString("SERV_PARAM")) && StringUtils.isNotBlank(data.getString("SERV_PARAM")))
        {
            IData svcData = new DataMap(data.getString("SERV_PARAM"));
            if (IDataUtil.isNotEmpty(svcData))
            {
                IData input = svcData;
                reqData.setAttr_value(input.getString("ATTR_VALUE"));
                reqData.setInst_id(input.getString("INST_ID"));
                reqData.setStart_date(input.getString("START_DATE"));
                reqData.setFlag("1");
                reqData.setSvc_inst_id(input.getString("RELA_INST_ID"));
            }

        }
        else
        {
            reqData.setAttr_value(data.getString("SVC_PASSWD"));
            reqData.setFlag("0");
        }

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ProtectionInfoRequestData();
    }

}
