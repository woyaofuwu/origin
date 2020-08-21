
package com.asiainfo.veris.crm.order.soa.person.busi.VPMNFilteIncomePhone.order.requestdata;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;

public class UnicomTransferReqData extends BaseReqData
{
    // 操作手机号
    private String phone_code_a;

    // 外网手机号
    private String phone_code_b;

    // 生效日期
    private String start_date;

    // 结束日期
    private String end_date;

    // 修改状态
    private String modify_state;

    // 新增
    public static String MODIFY_ADD = "1";

    // 修改
    public static String MODIFY_UPDATE = "2";

    // 删除
    public static String MODIFY_DELETE = "3";

    public String getEnd_date()
    {
        return end_date;
    }

    public String getModify_state()
    {
        return modify_state;
    }

    public String getPhone_code_a()
    {
        return phone_code_a;
    }

    public String getPhone_code_b()
    {
        return phone_code_b;
    }

    public String getStart_date()
    {
        return start_date;
    }

    public void setEnd_date(String end_date)
    {
        this.end_date = end_date;
    }

    public void setModify_state(String modify_state)
    {
        this.modify_state = modify_state;
    }

    public void setPhone_code_a(String phone_code_a)
    {
        this.phone_code_a = phone_code_a;
    }

    public void setPhone_code_b(String phone_code_b)
    {
        this.phone_code_b = phone_code_b;
    }

    public void setStart_date(String start_date)
    {
        this.start_date = start_date;
    }

}
