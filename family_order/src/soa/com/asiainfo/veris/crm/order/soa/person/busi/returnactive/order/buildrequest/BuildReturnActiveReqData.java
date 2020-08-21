
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveReqData;

public class BuildReturnActiveReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        // TODO Auto-generated method stub
        ReturnActiveReqData reqData = (ReturnActiveReqData) brd;
        String acceptNum = param.getString("ACCEPT_NUM");
        if (StringUtils.isBlank(acceptNum))
        {
            // 报错
        }

        reqData.setAcceptNum(acceptNum);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new ReturnActiveReqData();
    }

}
