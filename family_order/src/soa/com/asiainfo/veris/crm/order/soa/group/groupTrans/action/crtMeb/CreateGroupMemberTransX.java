
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.crtMeb;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.CreateGroupMemberTransHAIN;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.parse.ElementInfoParse;

public class CreateGroupMemberTransX extends CreateGroupMemberTransHAIN
{
    // 重载基类
    public void checkRequestData(IData iData) throws Exception
    {
        super.checkRequestData(iData);

        IDataUtil.chkParam(iData, "SERIAL_NUMBER_A");
    }

    // 重写基类
    // SELECTED_ELEMENTS=["[{'SVCELEMENT1':[{'SVC_CODE1=861S1'}]},{'DISCNTELEMENT1':[{'DISCNT_CODE1=1285D1'}]}]"],
    protected void parseElement(IData idata) throws Exception
    {
        String grpProductId = IDataUtil.chkParam(idata, "PRODUCT_ID");

        String selectedStr = idata.getString("SELECTED_ELEMENTS");

        if (StringUtils.isEmpty(selectedStr))
        {// 掌上boss传的是PARA_CODE2
            String discntCode = IDataUtil.chkParam(idata, "PARA_CODE2");
            String[] disnctArray = discntCode.split(",");
            StringBuilder sb = new StringBuilder(100);
            sb.append("[{'SVCELEMENT1':[{'SVC_CODE1=861S1'}]},");

            for (int i = 0, iSize = disnctArray.length; i < iSize; i++)
            {
                sb.append("{'DISCNTELEMENT" + (i + 1) + "':[{'DISCNT_CODE" + (i + 1) + "=" + disnctArray[i] + "D" + (i + 1) + "'}]}");
                if (i < iSize - 1)
                {
                    sb.append(",");
                }
            }
            sb.append("]");
            selectedStr = sb.toString();

        }

        // 服务传1
        // 资费传1
        String svcSizeS = idata.getString("SVC_COUNT", "1");

        int svcSize = Integer.parseInt(svcSizeS);

        String discntSizeS = idata.getString("DISCNT_COUNT");

        if (StringUtils.isEmpty(discntSizeS))
            discntSizeS = idata.getString("PARA_CODE", "1");

        int disnctSize = Integer.parseInt(discntSizeS);

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
