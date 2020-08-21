
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmemberinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class SelectMemberInfoHttpHandler extends CSBizHttpHandler
{

    /**
     * 认证校验
     * 
     * @throws Exception
     */
    public void checkUser() throws Exception
    {
        IData data = getData();
        IData authData = new DataMap();
        boolean flag = true;
        String checkMode = data.getString("CHECK_MODE");
        if(StringUtils.isNotEmpty(checkMode))
        {
            authData.put("CHECK_MODE", checkMode);
        }
        IDataset outputData = CSViewCall.call(this, "CS.AuthCheckSVC.authCheck", data);
        if (null != outputData && outputData.size() > 0)
        {
            IData result = outputData.getData(0);
            authData.putAll(result);
            if (result != null && !("0").equals(result.getString("RESULT_CODE", "")))
            {
                flag = false;
            }
        }
        if (flag)
        {
            authData.put("RESULT_CODE", "0");
        }
        setAjax(authData);
    }

    /**
     * 成员用户客户信息查询
     * 
     * @throws Exception
     */
    public void queryMemberInfo() throws Exception
    {

        IData resultInfo = new DataMap();
        IData conParams = getData("cond", true);

        // 查询成员用户信息
        String strMebSn = conParams.getString("SERIAL_NUMBER");

        if (StringUtils.isEmpty(strMebSn))
            return;
        String judgeUserState = conParams.getString("JUDGE_USERSTATE", "true");

        resultInfo = UCAInfoIntfViewUtil.qryMebUCAAndAcctDayInfoBySn(this, strMebSn, true, judgeUserState.equals("false") ? false : true);
        if (IDataUtil.isEmpty(resultInfo))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_125);
            return;
        }
        this.setAjax(resultInfo);

    }

    /**
     * 查询用户
     * 
     * @throws Exception
     */
    public void queryUser() throws Exception
    {
        IData data = getData();
        String strMebSn = data.getString("SERIAL_NUMBER", "");
        IData resultInfo = UCAInfoIntfViewUtil.checkMebUserInfoBySn(this, strMebSn, true);
        String resultCode = resultInfo.getString("USER_RESULT_CODE", "");
        if (resultCode.equals("2"))
        {
            CSViewException.apperr(GrpException.CRM_GRP_120, strMebSn);
        }

        resultInfo.put("RESULT_CODE", resultCode);

        if (resultCode.equals("0"))
        {
            String resultCodeDetail = resultInfo.getString("RESULT_CODE_DETAIL", "");
            if (resultCodeDetail.equals("2"))
            { // 非移动号码做特殊处理不需要做身份验证
                resultInfo.put("RESULT_CODE", "3");
            }

        }

        setAjax(resultInfo);
    }
}
