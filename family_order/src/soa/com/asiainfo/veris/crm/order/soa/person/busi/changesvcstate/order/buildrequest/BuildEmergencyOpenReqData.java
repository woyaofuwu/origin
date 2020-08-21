
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.requestdata.EmergencyOpenReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildEmergencyOpenReqData.java
 * @Description: 紧急开机 （注：担保开机和大客户担保开机也是使用的这个）
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-03-19
 */
public class BuildEmergencyOpenReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        EmergencyOpenReqData reqData = (EmergencyOpenReqData) brd;
        reqData.setCreditClass(param.getString("CREDIT_CLASS", "-1"));// 信用级别
        reqData.setOpenHours(param.getString("OPEN_HOURS", "0"));// 开机时间
        reqData.setGuaranteeUserId(param.getString("GUATANTEE_USER_ID", "-1"));// 担保开机对应的担保用户
        reqData.setOpenAmount(param.getString("OPEN_AMOUNT", "0"));// 担保开机对应的担保用户
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new EmergencyOpenReqData();
    }

}
