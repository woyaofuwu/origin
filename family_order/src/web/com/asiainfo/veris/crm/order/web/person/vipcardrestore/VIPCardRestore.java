
package com.asiainfo.veris.crm.order.web.person.vipcardrestore;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class VIPCardRestore extends PersonBasePage
{
    public void getCustInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();

        IDataset custInfos = CSViewCall.call(this, "SS.VIPCardRestoreSVC.getCustInfo", param);

        if (IDataUtil.isNotEmpty(custInfos))
        {
            // this.setCustInfo(custInfos.getData(0));
            refresh(custInfos.getData(0));
        }
        else
        {
            CSViewException.apperr(IBossException.CRM_IBOSS_4, "", "");
        }

    }

    public void initData(IRequestCycle cycle) throws Exception
    {
        IData info = new DataMap();
        info.put("PARAM_CODE", "0");
        info.put("PARAM_NAME", "证件校验");
        IData info1 = new DataMap();
        info1.put("PARAM_CODE", "1");
        info1.put("PARAM_NAME", "密码校验");

        IDataset verifyType = new DatasetList();
        verifyType.add(info);
        verifyType.add(info1);
        setVerifyType(verifyType);

        IData commInfo = new DataMap();
        // commInfo.put("PARAM_CODE", "0");
        commInfo.put("cond_ROUTE_TYPE", "01");
        // commInfo.put("cond_PROV_CODE", "898");
        // commInfo.put("cond_USER_IDTYPE", "01");
        setCommInfo(commInfo);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData idata = getData();

        IDataset dataset = CSViewCall.call(this, "SS.VIPCardRestoreSVC.cardRestore", idata);
        if (IDataUtil.isNotEmpty(dataset))
        {
            setAjax(dataset);
        }
    }

    public void refresh(IData data) throws Exception
    {
        IData info = new DataMap();

        if ("0".equals(data.getString("X_RSPTYPE")) && "0000".equals(data.getString("X_RSPCODE")))
        {
            info.put("DISABLED_FLAG", "1");
        }
        else
        {
            info.put("DISABLED_FLAG", "0");
        }

        String crmBalance = data.getString("CRM_BALANCE", "");
        if (!data.getString("CRM_BALANCE", "").equals(""))
        {
            double balance = Double.parseDouble(data.getString("CRM_BALANCE", ""));
            balance = balance / 1000.0;
            crmBalance = Double.toString(balance);
        }
        String debtBalance = data.getString("DEBT_BALANCE", "");
        if (!data.getString("DEBT_BALANCE", "").equals(""))
        {
            double dbalance = Double.parseDouble(data.getString("DEBT_BALANCE", ""));
            dbalance = dbalance / 1000.0;
            debtBalance = Double.toString(dbalance);
        }

        info.put("BRAND_CODE", data.getString("BRAND_CODE", ""));
        info.put("OPEN_DATE", data.getString("OPEN_DATE", ""));
        info.put("SCORE", data.getString("SCORE", ""));
        info.put("PUK", data.getString("PUK", ""));
        info.put("USER_STATE_CODESET", data.getString("USER_STATE_CODESET", ""));

        info.put("CUST_NAME", data.getString("CUST_NAME"));
        info.put("DEBT_BALANCE", debtBalance);
        info.put("BALANCE", crmBalance);
        info.put("IDCARDTYPE", data.getString("IDCARDTYPE"));
        info.put("IDCARDNUM", data.getString("IDCARDNUM"));
        info.put("PSPT_ADDR", data.getString("PSPT_ADDR"));
        info.put("GPRS_TAG", data.getString("GPRS_TAG"));
        info.put("ROAM_TYPE", data.getString("ROAM_TYPE"));
        info.put("OPER_FEE", "0");

        info.put("LEVEL", data.getString("LEVEL", ""));
        info.put("USER_MGR", data.getString("USER_MGR", ""));
        info.put("USER_MGR_NUM", data.getString("USER_MGR_NUM", ""));
        info.put("SERV_OPR", data.getString("SERV_OPR", ""));
        this.setCustInfo(info);
        if ("0".equals(data.getString("X_RSPTYPE")) && "0000".equals(data.getString("X_RSPCODE")))
        {
            if (!"00".equals(data.getString("USER_STATE_CODESET")))
            {
                if ("02".equals(data.getString("USER_STATE_CODESET")))
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "该用户处于停机状态，不能办理此业务！");
                    this.setCustInfo(null);
                }
                else
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "只有正常在网才能办理此业务！");
                    setCustInfo(null);
                }
            }
        }
        else
        {
            if ("2998".equals(data.getString("X_RSPCODE")))
            {
                data.put("X_RSPDESC", "落地方：" + data.getString("X_RSPDESC"));
            }
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "查询数据错误！<br>" + data.getString("X_RSPDESC"));
            setCustInfo(null);
        }
    }

    public abstract void setCommInfo(IData commInfo);

    public abstract void setCustInfo(IData custInfo);

    public abstract void setVerifyType(IDataset verifyType);
}
