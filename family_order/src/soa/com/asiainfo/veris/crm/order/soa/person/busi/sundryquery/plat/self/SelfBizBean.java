
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.self;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.PlatUtil;

public class SelfBizBean extends CSBizBean
{

    public IDataset qrySelfBizInfo(IData param, Pagination pagination) throws Exception
    {
        /**
         * String serialNumber = param.getString("SERIAL_NUMBER"); if (serialNumber == null || "".equals(serialNumber))
         * { CSAppException.apperr(CrmCommException.CRM_COMM_172); } IData users =
         * UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER")); if (IDataUtil.isEmpty(users)) {
         * CSAppException.apperr(CrmCommException.CRM_COMM_906); } int i = pagination.getCurrent(); String operCode =
         * param.getString("OPER_CODE"); String bizTypeCode = param.getString("BIZ_TYPE_CODE"); String indictSEQ =
         * param.getString("INDICTSEQ"); IData ibossparam = new DataMap();// ibossparam.put("KIND_ID",
         * "BIP2B783_T2001783_0_0"); ibossparam.put("MSISDN", param.getString("SERIAL_NUMBER"));
         * ibossparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER")); ibossparam.put("IDTYPE", "01");//
         * 标识类型:01-手机号码 ibossparam.put("BIZ_TYPE", "73"); ibossparam.put("OPR_NUMB",
         * PlatUtil.getOperNumb(SeqMgr.getTradeId())); ibossparam.put("ROUTETYPE", "00");// 路由类型 00-省代码，01-手机号
         * ibossparam.put("ROUTEVALUE", "000"); return IBossCall.dealIboss(getVisit(), ibossparam,
         * "BIP2B783_T2001783_0_0");
         */
        String serialNumber = param.getString("SERIAL_NUMBER");
        if (serialNumber == null || "".equals(serialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_172);
        }
        // 第一步，查询用户信息
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_906);
        }

        // 第二步，查询客户信息
        String custId = userInfo.getString("CUST_ID", "");

        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(custInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_1134);
        }

        String tradeId = SeqMgr.getTradeId();
        String proCode = PlatUtil.getProvCode();
        if (StringUtils.isBlank(proCode))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_310);
        }
        String opernumb = PlatUtil.getOperNumb(tradeId);
        String svcTypeId = "0303";
        String cond = "";
        String bizTypeCode = param.getString("BIZ_TYPE_CODE");
        String operCode = param.getString("OPER_CODE");
        String bizType = bizTypeCode + operCode;
        if ("4001001".equals(bizType) || "4001011".equals(bizType))
        {
            cond = serialNumber;
        }
        else if ("4001002".equals(bizType))
        {
            cond = serialNumber + "|" + param.getString("START_DATE") + "|" + param.getString("END_DATE");
        }
        else
        {
            cond = serialNumber + "|" + param.getString("START_DATE") + "|" + param.getString("END_DATE") + "|40";
        }

        IDataset result = IBossCall.queryFromIBOSS(opernumb, svcTypeId, cond, serialNumber, custInfo.getString("CUST_NAME"), param.getString("OPR_SOURCE", "08"), bizTypeCode, operCode);
        if (pagination.getCurrent() != 1)
        {
            result = IBossCall.queryFromIBOSSLeft(opernumb, pagination.getCurrent());
        }
        resultConvert(bizType, result);
        if (IDataUtil.isEmpty(result) || (result.getData(0).getInt("RSLTTOTALCNT") <= pagination.getCurrent() * result.getData(0).getInt("RSLTPAGEMAXCNT")))
        {
            String remark = "ok";
            IBossCall.BussToHisIBOSS(opernumb, remark);
        }
        return result;
    }

    private IDataset resultConvert(String bizType, IDataset result) throws Exception
    {
        IData data = new DataMap();
        for (int i = 0; i < result.size(); i++)
        {
            data = result.getData(i);
            String str = data.getString("QRYRSLTLIST", "");
            str = str.substring(1, str.length() - 1);
            str = str.replace("\"", "");
            if (str.indexOf("|") > 0)
            {
                String[] strs = str.split("\\|");
                for (int j = 0; j < strs.length; j++)
                {
                    data.put("DATA" + (j + 1), strs[j]);
                }
            }
            else
            {
                data.put("DATA1", str);
            }
        }
        return result;
    }

}
