
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.crtMeb;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.CreateGroupMemberTransHAIN;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.parse.ElementInfoParse;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CreateGroupMemberTrans5 extends CreateGroupMemberTransHAIN
{
    // 重载基类
    public void checkRequestData(IData iData) throws Exception
    {
        super.checkRequestData(iData);

        IDataUtil.chkParam(iData, "SERIAL_NUMBER_A");

        IDataUtil.chkParam(iData, "DISCNT_CODE");

        String serialNumberA = iData.getString("SERIAL_NUMBER_A", "");

        IData grpUserData = UcaInfoQry.qryUserInfoBySnForGrp(serialNumberA);

        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberA);
        }

        String userId = grpUserData.getString("USER_ID");

        iData.put("USER_ID", userId);
    }

    // 重写基类
    // SELECTED_ELEMENTS=["[{'SVCELEMENT1':[{'SVC_CODE1=861S1'}]},{'DISCNTELEMENT1':[{'DISCNT_CODE1=1285D1'}]}]"],
    protected void parseElement(IData idata) throws Exception
    {

        String grpProductId = IDataUtil.chkParam(idata, "PRODUCT_ID");
        int svcSize = Integer.parseInt(IDataUtil.chkParam(idata, "SVC_COUNT"));

        int disnctSize = Integer.parseInt(IDataUtil.chkParam(idata, "DISCNT_COUNT"));

        IData data = new DataMap();

        data.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
        data.put("USER_ID_A", idata.getString("USER_ID"));
        data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

        // 生成短号
        String shortCode = VpnUnit.createShortCode(data);

        idata.put("SHORT_CODE", shortCode);

        String selectedStr = "";
        if (shortCode.equals(""))
        {
            selectedStr = "[{'SVCELEMENT1':[{'SVC_CODE1=860S1'}]},{'DISCNTELEMENT1':[{'DISCNT_CODE1=" + idata.getString("DISCNT_CODE") + "D1'}]}]";
            // idata.put("SELECTED_ELEMENTS", elements);

        }
        else
        {
            selectedStr = "[{'SVCELEMENT1':[{'SVC_CODE1=861S1'}]},{'DISCNTELEMENT1':[{'DISCNT_CODE1=" + idata.getString("DISCNT_CODE") + "D1'}]}]";
            // idata.put("SELECTED_ELEMENTS", elements);
        }

        IData svc = getChanelSvcOrDisnt(selectedStr, "SVC_CODE", "S", svcSize, 11);// 11 是解析 服务串 的 步长

        IData discnt = getChanelSvcOrDisnt(selectedStr, "DISCNT_CODE", "D", disnctSize, 14);// 14 是解析 资费串 的 步长

        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(grpProductId);

        Set svcIt = svc.entrySet();
        if (svcIt != null)
        {
            StringBuilder sb = new StringBuilder();
            Iterator iterator = svcIt.iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                // 查服务对应的包信息
                IDataset packages = PkgElemInfoQry.getElementByPIdElemId(memProductId, "S", value, CSBizBean.getUserEparchyCode());
                if (IDataUtil.isEmpty(packages))
                    continue;

                sb.append(memProductId);
                sb.append(",");
                sb.append(packages.getData(0).getString("PACKAGE_ID"));
                sb.append(",");
                sb.append(value);
            }

            idata.put("SERVICE_CODE", sb.toString());
        }

        Set discntIt = discnt.entrySet();
        if (discntIt != null)
        {
            StringBuilder sb = new StringBuilder();
            Iterator iterator = discntIt.iterator();
            while (iterator.hasNext())
            {
                Map.Entry entry = (Entry) iterator.next();
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                // 查资费对应的包信息
                IDataset packages = PkgElemInfoQry.getElementByPIdElemId(memProductId, "D", value, CSBizBean.getUserEparchyCode());
                if (IDataUtil.isEmpty(packages))
                    continue;

                sb.append(memProductId);
                sb.append(",");
                sb.append(packages.getData(0).getString("PACKAGE_ID"));
                sb.append(",");
                sb.append(value);
                sb.append(";");
            }

            idata.put("DISCNT_CODE", sb.toString());
        }

        idata.remove("SELECTED_ELEMENTS");
        idata.remove("SVC_COUNT");
        idata.remove("DISCNT_COUNT");
        ElementInfoParse.parseElmentInfoCrtMeb(idata);

    }

    /*
     * 电子渠道传入 服务 和 优惠
     */
    private IData getChanelSvcOrDisnt(String selectedStr, String svcOrDiscntStr, String svcDiscFlag, int size, int stepSize) throws Exception
    {

        IData dt = new DataMap();

        for (int svcSize = 1; svcSize <= size; svcSize++)
        {
            int indexStart = selectedStr.indexOf(svcOrDiscntStr + svcSize);
            int indexEnd = selectedStr.lastIndexOf(svcDiscFlag + svcSize);

            String svcOrDiscntCode = selectedStr.substring(indexStart - 1 + stepSize, indexEnd);
            // String svcOrDiscntCode = selectedStr.substring(indexStart, indexEnd);

            if (!"".equals(svcOrDiscntCode))
            {
                dt.put(svcOrDiscntStr + svcSize, svcOrDiscntCode);
            }
        }

        return dt;
    }
}
