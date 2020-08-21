
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class GrpMemFeeUtil
{
    public static void dealGrpMemFee(IDataset elementAttr) throws Exception
    {
        if (IDataUtil.isEmpty(elementAttr))
            return;

        Pattern p = Pattern.compile("[^0-9]+");

        for (int i = 0, iSize = elementAttr.size(); i < iSize; i++)
        {
            IData idata = elementAttr.getData(i);
            String byFeeParam_key = idata.getString("ATTR_CODE", "");
            String byFeeParam_value = idata.getString("ATTR_VALUE", "0");

            // 校验包月费
            if ("FEE_MON_SHORT".equals(byFeeParam_key))
            {
                String byParamvalue = idata.getString("ATTR_VALUE", "");
                Matcher m = p.matcher(byParamvalue);
                boolean result = m.find();
                boolean paramFlag = false;
                while (result)
                {
                    // 如果找到了非法字符那么就设下标记
                    paramFlag = true;
                    result = m.find();
                }

                if (paramFlag)
                {
                    CSAppException.apperr(FeeException.CRM_FEE_17);
                }

                /*
                 * if (byParamvalue != null && byFeeParam_key.equals("FEE_MON_SHORT") && Integer.parseInt(byParamvalue)
                 * == 0) { idata.remove("FEE_MON_SHORT"); idata.put("ATTR_VALUE", ""); }
                 */
            }

            // 处理费用
            if (!"".equals(byFeeParam_key) && byFeeParam_key.length() > 3 && byFeeParam_key.substring(0, 3).equals("FEE"))
            {
                idata.put("ATTR_VALUE", Integer.parseInt(byFeeParam_value) * 100);
            }
        }
    }

    public void chkGrpMemFee(String value) throws Exception
    {

    }
}
