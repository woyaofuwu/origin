
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.filter.e3g;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;

public class E3gIbossFilter implements IFilterIn
{
    public static final String E3G_DISCNT_CODE = "WE-G3-100-B,WE-G3-200-B,WE-G3-300-B,WE-G3-50-B";

    public static final String E3G_TYGM_DISCNT_CODE = "WG-G3-5-B,WG-G3-10-B,WG-G3-20-B,WG-G3-50-B,WG-G3-100-B,WG-G3-200-B";

    @Override
    public void transferDataInput(IData input) throws Exception
    {
        input.put("SP_CODE", "REG_SP");
        input.put("BIZ_CODE", "REG_SP");

        String discntCode = "";
        if (StringUtils.isNotBlank(input.getString("MSISDN")))
        {
            input.put("IN_CARD_NO", input.getString("MSISDN"));
        }
        if (StringUtils.isNotBlank(input.getString("PACKAGE_ID")))
        {
            // 天语共模和3G上网本只有套餐不同
            if (input.getString("BIZ_TYPE_CODE", "").equals("37"))
            {
                discntCode = E3G_DISCNT_CODE;
            }
            if (input.getString("BIZ_TYPE_CODE", "").equals("67"))
            {
                discntCode = E3G_TYGM_DISCNT_CODE;
            }

            if (discntCode.indexOf(input.getString("PACKAGE_ID")) < 0)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_0977);
            }

            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", "301");
            attrParam.put("ATTR_VALUE", input.getString("PACKAGE_ID"));
            input.remove("PACKAGE_ID"); // PACKAGE_ID与TF_B_TRADE_PLATSVC相同 需要remove

            IDataset attrs = new DatasetList();
            attrs.add(attrParam);
            input.put("ATTR_PARAM", attrs);
        }
    }

}
