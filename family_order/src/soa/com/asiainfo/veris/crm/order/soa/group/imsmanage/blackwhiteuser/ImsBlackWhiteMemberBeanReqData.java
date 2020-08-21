
package com.asiainfo.veris.crm.order.soa.group.imsmanage.blackwhiteuser;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberReqData;

public class ImsBlackWhiteMemberBeanReqData extends MemberReqData
{
    private IDataset BW_LISTS; // 提交的黑白名单表格数据

    private String grp_user_id; // 用户id

    private String mem_user_id; // 添加的服务号码user_id

    private String user_type; // 黑白名单级别 集团:U 成员：M

    private String user_type_code; // 黑白名单属性

    public IDataset getBW_LISTS()
    {
        return BW_LISTS;
    }

    public String getGrp_user_id()
    {
        return grp_user_id;
    }

    public String getMem_user_id()
    {
        return mem_user_id;
    }

    public String getUser_type()
    {
        return user_type;
    }

    public String getUser_type_code()
    {
        return user_type_code;
    }

    public void setBW_LISTS(IDataset bw_lists)
    {
        BW_LISTS = bw_lists;
    }

    public void setGrp_user_id(String grp_user_id)
    {
        this.grp_user_id = grp_user_id;
    }

    public void setMem_user_id(String mem_user_id)
    {
        this.mem_user_id = mem_user_id;
    }

    public void setUser_type(String user_type)
    {
        this.user_type = user_type;
    }

    public void setUser_type_code(String user_type_code)
    {
        this.user_type_code = user_type_code;
    }
}
