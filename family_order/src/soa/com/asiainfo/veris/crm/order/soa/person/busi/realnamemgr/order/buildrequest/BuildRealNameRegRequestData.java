
package com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.order.requestdata.RealNameRegReqData;

/**
 * 手机实名制预登记数据请求处理类
 * 
 * @author liutt
 */
public class BuildRealNameRegRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        RealNameRegReqData realNameRD = (RealNameRegReqData) brd;
        realNameRD.setCustName(param.getString("CUST_NAME"));
        realNameRD.setPsptTypeCode(param.getString("PSPT_TYPE_CODE"));
        realNameRD.setPsptId(param.getString("PSPT_ID"));
        realNameRD.setPsptAddr(param.getString("PSPT_ADDR"));
        realNameRD.setPhone(param.getString("PHONE"));
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new RealNameRegReqData();
    }

}
