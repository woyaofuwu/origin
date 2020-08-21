
package com.asiainfo.veris.crm.order.soa.person.busi.villagework.order.requestdata;

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
public class VillageWorkData
{
    private String SERIAL_NUMBER; // 特殊号码

    private String Flag;// 0:增加；1：删除

    public final String getFlag()
    {
        return Flag;
    }

    public final String getSERIAL_NUMBER()
    {
        return SERIAL_NUMBER;
    }

    public final void setFlag(String flag)
    {
        Flag = flag;
    }

    public final void setSERIAL_NUMBER(String sERIALNUMBER)
    {
        SERIAL_NUMBER = sERIALNUMBER;
    }

}
