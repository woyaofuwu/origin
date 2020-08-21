
package com.asiainfo.veris.crm.order.soa.person.busi.np.npouteffective.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: NpOutApplyReqData.java
 * @Description: 携出申请 请求数据
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2014-5-5 上午10:38:22 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2014-5-5 lijm3 v1.0.0 修改原因
 */
public class NpOutEffectiveReqData extends BaseReqData
{

    public String flowId;// NP流程ID

    public String messageId;
    
    public String portInNetid;

    public final String getFlowId()
    {
        return flowId;
    }

    public final String getMessageId()
    {
        return messageId;
    }

    public final void setFlowId(String flowId)
    {
        this.flowId = flowId;
    }

    public final void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

	public String getPortInNetid() {
		return portInNetid;
	}

	public void setPortInNetid(String portInNetid) {
		this.portInNetid = portInNetid;
	}

}
