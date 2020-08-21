
package com.asiainfo.veris.crm.order.soa.person.busi.speservice.order.requestdata;

/**
 * Copyright: Copyright (c) 2013 Asiainfo-Linkage
 * 
 * @ClassName: MemberData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: lijm3
 * @date: 2013-7-12 下午2:10:03 Modification History: Date Author Version Description
 *        ---------------------------------------------------------* 2013-7-12 zhuyu v1.0.0 修改原因
 */
public class NewSvcRecomdInfoData
{
    private String recomd_campn_id; // 推荐编号

    private String recomd_campn_name;// 推荐项目名称

    private String recomd_reply;// 推荐类型

    private String recomd_object_type_desc;// 业务类型

    private String recomd_object_id;// 业务编码

    private String recomd_refuse_remark;// 备注

    private String recomd_refuse_reason_code;// 拒绝原因

    private String recomd_object_type;// 业务类型编码

    private String recomd_other_refuse_reason;// 其他拒绝原因

    private String recomd_source_id;// 经分编码

    public final String getRecomd_campn_id()
    {
        return recomd_campn_id;
    }

    public final String getRecomd_campn_name()
    {
        return recomd_campn_name;
    }

    public final String getRecomd_object_id()
    {
        return recomd_object_id;
    }

    public final String getRecomd_object_type()
    {
        return recomd_object_type;
    }

    public final String getRecomd_object_type_desc()
    {
        return recomd_object_type_desc;
    }

    public final String getRecomd_other_refuse_reason()
    {
        return recomd_other_refuse_reason;
    }

    public final String getRecomd_refuse_reason_code()
    {
        return recomd_refuse_reason_code;
    }

    public final String getRecomd_refuse_remark()
    {
        return recomd_refuse_remark;
    }

    public final String getRecomd_reply()
    {
        return recomd_reply;
    }

    public final String getRecomd_source_id()
    {
        return recomd_source_id;
    }

    public final void setRecomd_campn_id(String recomdCampnId)
    {
        recomd_campn_id = recomdCampnId;
    }

    public final void setRecomd_campn_name(String recomdCampnName)
    {
        recomd_campn_name = recomdCampnName;
    }

    public final void setRecomd_object_id(String recomdObjectId)
    {
        recomd_object_id = recomdObjectId;
    }

    public final void setRecomd_object_type(String recomdObjectType)
    {
        recomd_object_type = recomdObjectType;
    }

    public final void setRecomd_object_type_desc(String recomdObjectTypeDesc)
    {
        recomd_object_type_desc = recomdObjectTypeDesc;
    }

    public final void setRecomd_other_refuse_reason(String recomdOtherRefuseReason)
    {
        recomd_other_refuse_reason = recomdOtherRefuseReason;
    }

    public final void setRecomd_refuse_reason_code(String recomdRefuseReasonCode)
    {
        recomd_refuse_reason_code = recomdRefuseReasonCode;
    }

    public final void setRecomd_refuse_remark(String recomdRefuseRemark)
    {
        recomd_refuse_remark = recomdRefuseRemark;
    }

    public final void setRecomd_reply(String recomdReply)
    {
        recomd_reply = recomdReply;
    }

    public final void setRecomd_source_id(String recomdSourceId)
    {
        recomd_source_id = recomdSourceId;
    }

}
