
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.wireless;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class WirelessForColorRingFilter implements IFilterIn
{

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        // TODO Auto-generated method stub
        input.put("BIZ_TYPE_CODE", "19");
        input.put("SP_CODE", "REG_SP");
        input.put("BIZ_CODE", "REG_SP");

        String operCode = input.getString("OPER_CODE");
        if ("11".equals(operCode))
        {
            operCode = PlatConstants.OPER_ORDER;
        }
        else if ("12".equals(operCode))
        {
            operCode = PlatConstants.OPER_CANCEL_ORDER;
        }
        else if ("13".equals(operCode))
        {
            operCode = PlatConstants.OPER_USER_DATA_MODIFY;
        }
        input.put("OPER_CODE", operCode);
        if (!PlatConstants.OPER_CANCEL_ORDER.equals(operCode))
        {
            String level = input.getString("MEMBER_LEVEL");// 会员级别 0－非会员1－普通会员 2－高级会员 3－VIP会员
            if (StringUtils.isNotBlank(level))
            {
                IDataset attrs = new DatasetList();
                IData attr = new DataMap();
                attr.put("ATTR_CODE", "302");// 兼容老接口 302代表会员级别 MEMBER_LEVEL
                attr.put("ATTR_VALUE", level);
                attrs.add(attr);
                input.put("ATTR_PARAM", attrs);
            }
        }
        input.put("IS_NEED_PF", "1");
    }

}
