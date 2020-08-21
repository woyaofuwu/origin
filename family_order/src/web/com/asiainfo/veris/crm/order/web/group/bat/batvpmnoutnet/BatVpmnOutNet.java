
package com.asiainfo.veris.crm.order.web.group.bat.batvpmnoutnet;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public abstract class BatVpmnOutNet extends CSBasePage
{
    /**
     * 批量初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        // 设置初始条件
        setCondition(getData());
    }

    /**
     * 查询VPMN信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryVpmn(IRequestCycle cycle) throws Exception
    {
        String batchOperType = getData().getString("BATCH_OPER_TYPE");

        String serialNumber = getData().getString("cond_SERIAL_NUMBER");

        // 查询VPMN用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber);

        // 查询VPN信息
        String userId = userData.getString("USER_ID");

        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userId);

        // 查询VPMN客户信息
        IData custData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, userData.getString("CUST_ID", ""));

        // 判断是否VPMN用户
        if (!"VPMN".equals(userData.getString("BRAND_CODE")))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_15, serialNumber);
        }

        // 获取匹配的产品ID
        String matchProductId = StaticUtil.getStaticValue("GROUP_BAT_PRODUCT", batchOperType);

        // 匹配产品信息
        if (StringUtils.isNotBlank(matchProductId))
        {
            if (!userData.getString("PRODUCT_ID", "").matches(matchProductId))
            {
                if (batchOperType.matches("ADDPARENTVPMNOUTNET|DELPARENTVPMNOUTNET")) // 母VPMN号码判断
                {
                    CSViewException.apperr(VpmnUserException.VPMN_USER_3, serialNumber);
                }
                else if (batchOperType.matches("ADDVPMNOUTNET|DELVPMNOUTNET")) // 子VPMN号码判断
                {
                    CSViewException.apperr(VpmnUserException.VPMN_USER_12, serialNumber);
                }
            }
        }

        IData vpmnData = new DataMap();

        vpmnData.put("SERIAL_NUMBER", userData.getString("SERIAL_NUMBER"));
        vpmnData.put("VPN_NAME", userVpnData.getString("VPN_NAME"));
        vpmnData.put("GROUP_ADDR", custData.getString("GROUP_ADDR"));
        vpmnData.put("GROUP_CONTACT_PHONE", custData.getString("GROUP_CONTACT_PHONE"));

        setVpmn(vpmnData);
    }

    public abstract void setCondition(IData condition);

    public abstract void setVpmn(IData vpmnData);

}
