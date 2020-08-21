
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser;

public class CreateInternetGroupUserReqData extends CreateGroupUserReqData
{
    private String OPERATOR_NAME;// 经办人姓名

    private String OPERATOR_IDCARD;// 经办人身份证

    private String OPERATOR_PHONE;// 经办人联系电话

    private String OPERATOR_ADDRESS;// 经办人地址

    private String MANAGER_NAME;// 网络负责人姓名

    private String MANAGER_PHONE;// 网络负责人电话

    private String MANAGER_ADDRESS;// 网络负责人地址

    private String LINE_COUNT;// 专线条数

    private String GROUPLINE_TYPE;// 组网类型

    private String LINE_TYPE;// 业务类型

    public String getGROUPLINE_TYPE()
    {
        return GROUPLINE_TYPE;
    }

    public String getLINE_COUNT()
    {
        return LINE_COUNT;
    }

    public String getLINE_TYPE()
    {
        return LINE_TYPE;
    }

    public String getMANAGER_ADDRESS()
    {
        return MANAGER_ADDRESS;
    }

    public String getMANAGER_NAME()
    {
        return MANAGER_NAME;
    }

    public String getMANAGER_PHONE()
    {
        return MANAGER_PHONE;
    }

    public String getOPERATOR_ADDRESS()
    {
        return OPERATOR_ADDRESS;
    }

    public String getOPERATOR_IDCARD()
    {
        return OPERATOR_IDCARD;
    }

    public String getOPERATOR_NAME()
    {
        return OPERATOR_NAME;
    }

    public String getOPERATOR_PHONE()
    {
        return OPERATOR_PHONE;
    }

    public void setGROUPLINE_TYPE(String groupline_type)
    {
        GROUPLINE_TYPE = groupline_type;
    }

    public void setLINE_COUNT(String line_count)
    {
        LINE_COUNT = line_count;
    }

    public void setLINE_TYPE(String line_type)
    {
        LINE_TYPE = line_type;
    }

    public void setMANAGER_ADDRESS(String manager_address)
    {
        MANAGER_ADDRESS = manager_address;
    }

    public void setMANAGER_NAME(String manager_name)
    {
        MANAGER_NAME = manager_name;
    }

    public void setMANAGER_PHONE(String manager_phone)
    {
        MANAGER_PHONE = manager_phone;
    }

    public void setOPERATOR_ADDRESS(String operator_address)
    {
        OPERATOR_ADDRESS = operator_address;
    }

    public void setOPERATOR_IDCARD(String operator_idcard)
    {
        OPERATOR_IDCARD = operator_idcard;
    }

    public void setOPERATOR_NAME(String operator_name)
    {
        OPERATOR_NAME = operator_name;
    }

    public void setOPERATOR_PHONE(String operator_phone)
    {
        OPERATOR_PHONE = operator_phone;
    }

}
