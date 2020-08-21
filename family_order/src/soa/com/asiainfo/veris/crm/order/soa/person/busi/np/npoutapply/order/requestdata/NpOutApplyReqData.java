
package com.asiainfo.veris.crm.order.soa.person.busi.np.npoutapply.order.requestdata;

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
public class NpOutApplyReqData extends BaseReqData
{

    public String npServiceType;// np 业务类型

    public String credType;// 证件类型

    public String flowId;// NP流程ID

    public String messageId;// 消息ID

    public String portOutNetID;// 网络ID(携出方)

    public String portInNetID;// 网络ID(携入方)

    public String homeNetID;// 网络ID(号码拥有网络)

    public String commandCode;// 消息命令码

    public String actorCustName;// 办理人姓名

    public String actorCredType;// 办理人证件类型

    public String actorPsptId;// 办理人证件号码

    public String phone;// 联系电话

    public String custName;

    public String psptId;// 证件号码

    public String portInDate;// 携入时间

    public String rsrvNum1;// 人工审核时间配置，默认为3分钟
    
    public String rsrvStr1;
    
	public String rsrvStr2;// 记录日志信息
    
    public String rsrvStr3;// 快速携回标记

    public String xResultinfo;
    
    public String authTag;//授权码申请标志
    public String getRsrvStr1() {
		return rsrvStr1;
	}

	public void setRsrvStr1(String rsrvStr1) {
		this.rsrvStr1 = rsrvStr1;
	}

	public String getRsrvStr2() {
		return rsrvStr2;
	}

	public void setRsrvStr2(String rsrvStr2) {
		this.rsrvStr2 = rsrvStr2;
	}

	public String getRsrvStr3() {
		return rsrvStr3;
	}

	public void setRsrvStr3(String rsrvStr3) {
		this.rsrvStr3 = rsrvStr3;
	}

	public String getAuthTag() {
		return authTag;
	}

	public void setAuthTag(String authTag) {
		this.authTag = authTag;
	}



    public final String getActorCredType()
    {
        return actorCredType;
    }

    public final String getActorCustName()
    {
        return actorCustName;
    }

    public final String getActorPsptId()
    {
        return actorPsptId;
    }

    public final String getCommandCode()
    {
        return commandCode;
    }

    public final String getCredType()
    {
        return credType;
    }

    public final String getCustName()
    {
        return custName;
    }

    public final String getFlowId()
    {
        return flowId;
    }

    public final String getHomeNetID()
    {
        return homeNetID;
    }

    public final String getMessageId()
    {
        return messageId;
    }

    public final String getNpServiceType()
    {
        return npServiceType;
    }

    public final String getPhone()
    {
        return phone;
    }

    public final String getPortInDate()
    {
        return portInDate;
    }

    public final String getPortInNetID()
    {
        return portInNetID;
    }

    public final String getPortOutNetID()
    {
        return portOutNetID;
    }

    public final String getPsptId()
    {
        return psptId;
    }

    public final String getRsrvNum1()
    {
        return rsrvNum1;
    }

    public final String getxResultinfo()
    {
        return xResultinfo;
    }

    public final void setActorCredType(String actorCredType)
    {
        this.actorCredType = actorCredType;
    }

    public final void setActorCustName(String actorCustName)
    {
        this.actorCustName = actorCustName;
    }

    public final void setActorPsptId(String actorPsptId)
    {
        this.actorPsptId = actorPsptId;
    }

    public final void setCommandCode(String commandCode)
    {
        this.commandCode = commandCode;
    }

    public final void setCredType(String credType)
    {
        this.credType = credType;
    }

    public final void setCustName(String custName)
    {
        this.custName = custName;
    }

    public final void setFlowId(String flowId)
    {
        this.flowId = flowId;
    }

    public final void setHomeNetID(String homeNetID)
    {
        this.homeNetID = homeNetID;
    }

    public final void setMessageId(String messageId)
    {
        this.messageId = messageId;
    }

    public final void setNpServiceType(String npServiceType)
    {
        this.npServiceType = npServiceType;
    }

    public final void setPhone(String phone)
    {
        this.phone = phone;
    }

    public final void setPortInDate(String portInDate)
    {
        this.portInDate = portInDate;
    }

    public final void setPortInNetID(String portInNetID)
    {
        this.portInNetID = portInNetID;
    }

    public final void setPortOutNetID(String portOutNetID)
    {
        this.portOutNetID = portOutNetID;
    }

    public final void setPsptId(String psptId)
    {
        this.psptId = psptId;
    }

    public final void setRsrvNum1(String rsrvNum1)
    {
        this.rsrvNum1 = rsrvNum1;
    }

    public final void setxResultinfo(String xResultinfo)
    {
        this.xResultinfo = xResultinfo;
    }

}
