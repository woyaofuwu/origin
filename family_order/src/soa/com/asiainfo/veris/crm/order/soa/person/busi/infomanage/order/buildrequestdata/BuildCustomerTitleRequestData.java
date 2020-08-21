
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.buildrequestdata;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata.CustomerTitleRequestData;

public class BuildCustomerTitleRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildCustomerTitleRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        CustomerTitleRequestData reqData = (CustomerTitleRequestData) brd;
        reqData.setCustTitle(data.getString("Cust_Title"));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new CustomerTitleRequestData();
    }

}
