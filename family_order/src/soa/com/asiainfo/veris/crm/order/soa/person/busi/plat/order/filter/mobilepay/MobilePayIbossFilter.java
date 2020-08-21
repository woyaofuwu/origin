
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.mobilepay;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class MobilePayIbossFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        if (!"".equals(input.getString("CARD_NO", "")))
        {
            input.put("IN_CARD_NO", input.getString("CARD_NO"));// 保存贴片卡和公交一卡通的卡号
        }
        // 用户通过手机支付业务系统的主动销户流程
        // 该业务流程只需要接口T2040036（平台发给省），手机支付平台接到省BOSS成功的应答后即销户,不需走服务开通T2001060（省发给平台）
        if ("BIP2B260_T2040036_1_0".equals(input.getString("KIND_ID")))
        {
            input.put("IS_NEED_PF", "0");
        }
    }

}
