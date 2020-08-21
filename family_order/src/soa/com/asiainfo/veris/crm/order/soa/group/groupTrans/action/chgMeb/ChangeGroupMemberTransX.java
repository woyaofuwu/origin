
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.chgMeb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.ChangeGroupMemberTransHAIN;

public class ChangeGroupMemberTransX extends ChangeGroupMemberTransHAIN
{

    // 子类重载
    protected void addSubDataBefore(IData idata) throws Exception
    {
        transChangeVpmnGroupMemberRequestData(idata);
    }

    // 子类重载
    protected void addSubDataAfter(IData idata) throws Exception
    {

    }

    private void transChangeVpmnGroupMemberRequestData(IData data) throws Exception
    {
        checkRequestData(data);

        String serialNumberA = data.getString("SERIAL_NUMBER_A", "");

        IData grpUserData = UcaInfoQry.qryUserInfoBySnForGrp(serialNumberA);

        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberA);
        }

        String grpUserId = grpUserData.getString("USER_ID");

        data.put("USER_ID", grpUserId);

        IDataset discntList = new DatasetList();
        

        String discntCodeOld = IDataUtil.getMandaData(data, "OLD_DISCNT_CODE");// 原有优惠编码
        String discntCodeNew = IDataUtil.getMandaData(data, "NEW_DISCNT_CODE");// 新优惠编码
        String effectTime = IDataUtil.getMandaData(data, "EFFECT_TIME");// 生效类型：0.立即生效 1.下月生效

        String effectNow = "false";
        if ("0".equals(effectTime)) // 立即生效
        {
            effectNow = "true";
        }

        String[] discntCodeOldArray = discntCodeOld.split(",");
        
        for (int i = 0, iSize = discntCodeOldArray.length; i < iSize; i++)
        {
            IData discntOld = new DataMap();
            discntOld.put("MODIFY_TAG", "1");
            discntOld.put("DISCNT_CODE", discntCodeOldArray[i]);
            discntList.add(discntOld);
        }
        
        String[] discntCodeNewArray = discntCodeNew.split(",");
        for (int i = 0, iSize = discntCodeNewArray.length; i < iSize; i++)
        {
            IData discntNew = new DataMap();
            discntNew.put("MODIFY_TAG", "0");
            discntNew.put("DISCNT_CODE", discntCodeNewArray[i]);
            discntList.add(discntNew);
        }
        

        data.put("LIST_INFOS", discntList);

        data.put("EFFECT_NOW", effectNow);
    }

    public void checkRequestData(IData data) throws Exception
    {
        IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");
        IDataUtil.getMandaData(data, "PRODUCT_ID");// 8000
        IDataUtil.getMandaData(data, "OLD_DISCNT_CODE");// 原有优惠编码
        IDataUtil.getMandaData(data, "NEW_DISCNT_CODE");// 新优惠编码
        IDataUtil.getMandaData(data, "EFFECT_TIME");// 生效类型：0.立即生效 1.下月生效
    }
}
