
package com.asiainfo.veris.crm.iorder.soa.family.busi.manage.member.order.requestdata;

import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.BaseFamilyBusiReqData;

/**
 * @Description 单个成员角色请求对象
 * @Auther: zhenggang
 * @Date: 2020/7/31 10:30
 * @version: V1.0
 */
public class FamilyMemberSingleReqData extends BaseFamilyBusiReqData
{
    private String printContent;

    public String getPrintContent()
    {
        return printContent;
    }

    public void setPrintContent(String printContent)
    {
        this.printContent = printContent;
    }
}
