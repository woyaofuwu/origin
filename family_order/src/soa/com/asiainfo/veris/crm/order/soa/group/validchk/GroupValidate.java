
package com.asiainfo.veris.crm.order.soa.group.validchk;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.checkmgr.CheckForGrp;

public class GroupValidate
{
    /**
     * 验证移动总机成员短号码
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean shortCodeValidateSupTeleMeb(IData data) throws Exception
    {

        /*
         * 1、分机短号码必须为3-6位数字 2、分机号码的短号码不能用【1】开头 3、分机号码的短号码不能用【9】开头 4、分机号码的短号码不能用【0】开头
         */
        String shortCode = data.getString("SHORT_CODE").trim();

        if (shortCode.length() < 3)
        {
            data.put("ERROR_MESSAGE", "短号码必须为大于等于3位的数字!");
            return false;
        }

        if (!shortCode.substring(0, 1).equals("6"))
        {
            data.put("ERROR_MESSAGE", "短号码要用【6】开头!");
            return false;
        }

        if (shortCode.substring(1, 2).equals("0"))
        {
            data.put("ERROR_MESSAGE", "短号码第二位不能为【0】!");
            return false;
        }
        // 海南如果总机号码是VPMN成员，总机短号使用VPMN短号
        BizCtrlInfo bizCtrlInfo = BizCtrlBean.qryProductCtrlInfo("6100", BizCtrlType.CreateMember);

        IData checkData = new DataMap();

        checkData.put("TRADE_TYPE_CODE", bizCtrlInfo.getTradeTypeCode());
        checkData.put("PRODUCT_ID", "-1");
        checkData.put("USER_ID_A", data.getString("USER_ID_A"));
        checkData.put("SHORT_CODE", shortCode);
        checkData.put("RELATION_TYPE_CODE", "25");
        CheckForGrp.chk(checkData);

        return true;
    }

    /**
     * 移动总机管理员短号验证
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static boolean shortNumValidateSuperTeleAdmin(IData data) throws Exception
    {
        /*
         * 1、分机短号码必须为3-6位数字 2、分机号码的短号码不能用【1】开头 3、分机号码的短号码不能用【9】开头 4、分机号码的短号码不能用【0】开头
         */
        String shortCode = data.getString("SHORT_CODE").trim();
        int len = shortCode.length();
        if (len < 3)
        {
            data.put("ERROR_MESSAGE", "短号码必须为大于等于3位的数字！");
            return false;
        }
        if (len > 6)
        {
            data.put("ERROR_MESSAGE", "短号码必须为小于等于6位的数字！");
            return false;
        }
        if (shortCode.substring(0, 1).equals("1"))
        {
            data.put("ERROR_MESSAGE", "短号码不能用【1】开头！");
            return false;
        }
        if (shortCode.substring(0, 1).equals("0"))
        {
            data.put("ERROR_MESSAGE", "短号码不能用【0】开头！");
            return false;
        }
        if (shortCode.substring(0, 1).equals("9"))
        {
            data.put("ERROR_MESSAGE", "短号码不能用【9】开头！");
            return false;
        }

        return true;
    }

}
