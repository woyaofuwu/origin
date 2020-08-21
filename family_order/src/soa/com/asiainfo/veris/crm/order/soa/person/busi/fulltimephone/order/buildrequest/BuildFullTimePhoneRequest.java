
package com.asiainfo.veris.crm.order.soa.person.busi.fulltimephone.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.fulltimephone.order.requestdata.FullTimePhoneReqData;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildFullTimePhoneRequest.java
 * @Description: 全时通buildrequest
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 10, 2014 2:42:02 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 10, 2014 maoke v1.0.0 修改原因
 */
public class BuildFullTimePhoneRequest extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FullTimePhoneReqData();
    }
}
