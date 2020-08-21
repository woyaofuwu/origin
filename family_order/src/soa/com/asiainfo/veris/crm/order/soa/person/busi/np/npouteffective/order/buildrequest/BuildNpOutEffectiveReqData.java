
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.requestdata.NpOutEffectiveReqData;

public class BuildNpOutEffectiveReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        NpOutEffectiveReqData reqData = (NpOutEffectiveReqData) brd;

        String flowId = param.getString("FLOWID");// 验证是否传入
        String messageId = param.getString("MESSAGEID");// 验证是否传入
        String portInNetid = param.getString("PORT_IN_NETID");//获取携入运营商 add by panyu5
        reqData.setPortInNetid(portInNetid);
        reqData.setFlowId(flowId);
        reqData.setMessageId(messageId);

        reqData.setRemark("NP立即销户积分清零台账");

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new NpOutEffectiveReqData();
    }

}
