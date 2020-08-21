
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmeborderinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class SelectMebOrderInfoHttpHandler extends CSBizHttpHandler
{

    /**
     * 成员用户客户信息查询
     * 
     * @throws Exception
     */
    public void queryMemberInfo() throws Exception
    {

        IData resultInfo = new DataMap();

        // 查询成员用户信息
        String strMebSn = getData().getString("cond_SERIAL_NUMBER");
        String relationCode = getData().getString("RELATION_CODE", "");
        String limitType = getData().getString("cond_LIMIT_TYPE", "");
        String limitProducts = getData().getString("cond_LIMIT_PRODUCTS", "");
        String judgeUserState = getData().getString("cond_JUDGE_USERSTATE", "");
        if (StringUtils.isEmpty(strMebSn))
            return;
        resultInfo = UCAInfoIntfViewUtil.qryMebOrderedGroupInfosBySnAndLimitProducts(this, strMebSn, relationCode, limitType, limitProducts, true, judgeUserState.equals("false") ? false : true);

        this.setAjax(resultInfo);

    }
}
