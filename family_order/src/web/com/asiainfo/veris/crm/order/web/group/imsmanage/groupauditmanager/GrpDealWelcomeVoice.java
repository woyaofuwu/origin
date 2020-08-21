
package com.asiainfo.veris.crm.order.web.group.imsmanage.groupauditmanager;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class GrpDealWelcomeVoice extends GroupBasePage
{

    /**
     * 初始化
     * 
     * @author xiaosp 2009-8-25
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        String param = this.getParameter("AUDIT_WELCOME_PARAM");
        IData cond = new DataMap(param);
        // IData cond = getData("cond", true);
        setCondition(cond);
    }

    /**
     * 响应确定提交参数，进行逻辑处理
     */
    public void sendAuditMessage(IRequestCycle cycle) throws Exception
    {
        IData cond = getData();
        IData temp = new DataMap();
        temp.put("BG_NAME", cond.getString("VPN_NO"));
        temp.put("WORDSID", cond.getString("WORDSID"));
        temp.put("CHECK_STATUS", cond.getString("CHECK_RESULT"));
        temp.put("REASON", cond.getString("REMARK"));
        temp.put("KIND_ID", "CTX1A019_T1000001_0_0");

        IDataset result = CSViewCall.call(this, "SS.GrpDealWelcomeVoiceSVC.DealGrpAuditWelcomeVoice", temp);
        // 调一级BOSS服务获取欢迎词信息 result
        if (IDataUtil.isEmpty(result))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_39);
        }
        else if (!"0".equals(result.getData(0).getString("X_RESULTCODE")))
        {
            CSViewException.apperr(BizException.CRM_BIZ_5, result.getData(0).getString("X_RESULTINFO"));
        }
        else
        {
            IData errresult = new DataMap();
            errresult.put("RESULT_CODE", "true");
            errresult.put("X_MESSAGE", "业务受理成功!");
            setAjax(errresult);
        }
    }

    public abstract void setCondition(IData cond);
}
