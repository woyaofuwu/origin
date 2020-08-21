
package com.asiainfo.veris.crm.order.soa.group.groupTrans.util;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class GroupProductUtil
{

    public static void checkProductCanDo(String operType, String productId) throws Exception
    {
        // PARA_CODE2-受理;PARA_CODE3-变更;PARA_CODE4-注销;
        IDataset switchDs = CommparaInfoQry.getCommPkInfo("CGM", "1216", "0", CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(switchDs))
            CSAppException.apperr(ProductException.CRM_PRODUCT_52);

        boolean canDo = false;

        for (int i = 0, iSize = switchDs.size(); i < iSize; i++)
        {
            if (productId.equals(switchDs.getData(i).getString("PARA_CODE1")))
            {
                if (BizCtrlType.CreateUser.equals(operType) && "1".equals(switchDs.getData(i).getString("PARA_CODE2", "")))
                {// 开户
                    canDo = true;
                }
                else if (BizCtrlType.ChangeUserDis.equals(operType) && "1".equals(switchDs.getData(i).getString("PARA_CODE3", "")))
                {
                    canDo = true;
                }
                else if (BizCtrlType.DestoryUser.equals(operType) && "1".equals(switchDs.getData(i).getString("PARA_CODE4", "")))
                {
                    canDo = true;
                }
                else if (BizCtrlType.CreateMember.equals(operType) && "1".equals(switchDs.getData(i).getString("PARA_CODE5", "")))
                {
                    canDo = true;
                }
                else if (BizCtrlType.ChangeMemberDis.equals(operType) && "1".equals(switchDs.getData(i).getString("PARA_CODE6", "")))
                {
                    canDo = true;
                }
                else if (BizCtrlType.DestoryMember.equals(operType) && "1".equals(switchDs.getData(i).getString("PARA_CODE7", "")))
                {
                    canDo = true;
                }

                // 变更to-do
                // 退订to-do
                if (canDo)
                {
                    break;
                }
            }
        }
        if (!canDo)
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_52);
        }

    }
}
