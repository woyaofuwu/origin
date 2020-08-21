
package com.asiainfo.veris.crm.order.soa.person.busi.wap.plat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.WapException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.person.busi.wap.BaseServiceForWap;
import com.asiainfo.veris.crm.order.soa.person.busi.wap.WapUtil;

public class ChangePlatSvcForWap extends BaseServiceForWap
{

    @Override
    protected void checkChildParams(IData param) throws Exception
    {
        IDataset productInfoList = new DatasetList(param.getString("PRODUNCTINFO"));
        if ((productInfoList == null) || (productInfoList.size() == 0))
        {
            CSAppException.apperr(WapException.CRM_WAP_100001, "PRODUNCTINFO");
        }
        else
        {
            int size = productInfoList.size();
            for (int i = 0; i < size; i++)
            {
                IData dataTmp = productInfoList.getData(i);

                if ("".equals(dataTmp.getString("ENCODTYPE", "")))
                {
                    CSAppException.apperr(WapException.CRM_WAP_100001, "ENCODTYPE");
                }

                if ("".equals(dataTmp.getString("SPID", "")))
                {
                    CSAppException.apperr(WapException.CRM_WAP_100001, "SPID");
                }

                if ("".equals(dataTmp.getString("BIZCODE", "")))
                {
                    CSAppException.apperr(WapException.CRM_WAP_100001, "BIZCODE");
                }

                if ("".equals(dataTmp.getString("OPRCODE", "")))
                {
                    CSAppException.apperr(WapException.CRM_WAP_100001, "OPRCODE");
                }

                // 简单数据校验
                // if ((!"01".equals(dataTmp.getString("OPRCODE", ""))) && (!"02".equals(dataTmp.getString("OPRCODE",
                // ""))) && (!"03".equals(dataTmp.getString("OPRCODE", ""))))
                // {
                // CSAppException.apperr(WapException.CRM_WAP_100001, "OPRCODE");
                // }
            }
        }
    }

    @Override
    public IDataset handleBiz(IData param) throws Exception
    {
        // TODO Auto-generated method stub
        IDataset resulstlist = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
        return resulstlist;
    }

    @Override
    protected void transParam(IData param)
    {
        super.transParam(param);
        // 业务类型
        String bizType = param.getString("BIZTYPE");
        String bizTypeCode = "";

        IDataset productInfoList = new DatasetList(param.getString("PRODUNCTINFO"));
        String spId = productInfoList.getData(0).getString("SPID", "");
        String bizCode = productInfoList.getData(0).getString("BIZCODE", "");
        String productId = productInfoList.getData(0).getString("PRODUNCTID", "");
        bizCode = WapUtil.convertStr(bizCode, 2);

        if ("WLAN".equals(bizType))
        {
            bizTypeCode = bizCode;
            bizCode = "REG_SP";

            if ("92".equals(bizTypeCode))
            {
                bizCode = productId;
            }

            // 业务类型编码
            param.put("CHECK_REAL_NAME", "true");

        }

        param.put("BIZ_TYPE_CODE", bizTypeCode);
        param.put("OPER_CODE", productInfoList.getData(0).getString("OPRCODE", ""));
        param.put("SP_CODE", spId);
        param.put("BIZ_CODE", bizCode);

        // 平台希望生效时间 YYYYMMDDHHMISS
        param.put("START_DATE", "");
        // 操作来源 01-WEB，03-WAP，04-SMS，07-10086，09-SP ，65-NIAP客户端
        param.put("OPR_SOURCE", "03");
        // 渠道编码 IBOSS过来的都是6,电子渠道5
        param.put("IN_MODE_CODE", "6");
        // 平台
        param.put("ORGDOMAIN", "PLAT");
    }
}
