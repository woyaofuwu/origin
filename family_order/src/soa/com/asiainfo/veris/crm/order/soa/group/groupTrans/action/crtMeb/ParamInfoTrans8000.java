
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.crtMeb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ParamInfoTrans8000 implements ITrans
{
    @Override
    public final void transRequestData(IData iData) throws Exception
    {
        addSubDataBefore(iData);
        transParam8000RequestData(iData);
        addSubDataAfter(iData);
    }

    protected void addSubDataAfter(IData iData) throws Exception
    {

    }

    protected void addSubDataBefore(IData iData) throws Exception
    {

    }

    public void transParam8000RequestData(IData idata) throws Exception
    {

        checkRequestData(idata);

        idata.put("MEM_ROLE_B", idata.getString("MEM_ROLE_B", "1"));

        String shortCode = idata.getString("SHORT_CODE");
        // 把短号加入产品参数中 to-do
        IDataset intfParam = idata.getDataset("PRODUCT_PARAM_INFO");
        IDataset productParam = intfParam.getData(0).getDataset("PRODUCT_PARAM");
        IData shortCodeParm = new DataMap();
        shortCodeParm.put("ATTR_CODE", "SHORT_CODE");
        shortCodeParm.put("ATTR_VALUE", shortCode);
        productParam.add(shortCodeParm);

        // 把短号加入资源中
        IDataset idsRes = idata.getDataset("RES_INFO", new DatasetList());

        IData idRes = new DataMap();
        idRes.put("RES_TYPE_CODE", "S");
        idRes.put("CHECKED", "true");
        idRes.put("DISABLED", "true");
        idRes.put("MODIFY_TAG", "0");
        idRes.put("RES_CODE", shortCode);

        idsRes.add(idRes);

        idata.put("RES_INFO", idsRes);
    }

    private void checkRequestData(IData idata) throws Exception
    {
        // 订购VPMN产品时候，必须输入
        String shortCode = idata.getString("SHORT_CODE");

        if (!StringUtils.isEmpty(shortCode))
        {

            IData data = new DataMap();
            data.put("SHORT_CODE", shortCode);
            data.put("USER_ID_A", idata.getString("USER_ID"));
            data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());

            // 校验短号
            IData result = VpnUnit.shortCodeValidateVpn(data);
            if ("false".equals(result.getString("RESULT")))
                CSAppException.apperr(GrpException.CRM_GRP_713, data.getString("ERROR_MESSAGE"));
        }
        else
        {

            IData data = new DataMap();
            data.put("SERIAL_NUMBER", idata.getString("SERIAL_NUMBER"));
            data.put("USER_ID_A", idata.getString("USER_ID"));
            data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            String resultShortCode = VpnUnit.createShortCode(data);
            idata.put("SHORT_CODE", resultShortCode);
        }

    }
}
