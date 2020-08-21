
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryusernpinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class NoOweFeeBean extends CSBizBean
{
    public IDataset getNpOutInfo(IData param, Pagination page) throws Exception
    {
        String areaCode = param.getString("AREA_CODE", "");
        String start_date = param.getString("START_DATE", "");
        String serial_number = param.getString("SERIAL_NUMBER", "");
        String end_date = param.getString("END_DATE", "");
        String areaCodeh = param.getString("AREA_CODE_H", "");

        if (StringUtils.isBlank(areaCodeh))
        {
            if (!areaCode.equals("HNAL"))
            {
                String cityCode = getVisit().getCityCode();

                if ("HNSJ".equals(cityCode) || "HNYD".equals(cityCode) || "HNKF".equals(cityCode))
                {
                    if ("0898".equals(areaCode) || "HNSJ".equals(areaCode) || "HNYD".equals(areaCode) || "HNKF".equals(areaCode))
                    {
                        param.put("AREA_CODE", "HAIN");
                    }
                }
                else
                {
                    if (!areaCode.equals(cityCode))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "你归属区域[" + cityCode + "]，你没有权限查询区域[" + areaCode + "]的数据！");

                    }
                }
            }
            else if (areaCode.equals("HNAL"))
            {
                if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "CRM_QUERYNPRETURNVISITEXPORT"))
                {
                    param.put("AREA_CODE", "HAIN");
                    areaCode = "HAIN";
                }
                else
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "你没有权限查询所有区域的数据！");

                }
                // if(pd.getContext().hasPriv("CRM_QUERYNPRETURNVISITEXPORT")){
                // inparam.put("AREA_CODE", "HAIN");
                // }else{
                // common.warn("你没有权限查询所有区域的数据！");
                // return null;
                // }
            }
        }
        else
        {
            areaCode = areaCodeh;

        }

        IDataset ids = null;
        if ("HNGK".equals(areaCode))
        {
            ids = TradeNpQry.getNpOweFeeInfos(serial_number, start_date, end_date, page);
        }
        else
        {
            ids = TradeNpQry.getNpOweFeeInfos(serial_number, start_date, end_date, areaCode, page);
        }

        if (IDataUtil.isNotEmpty(ids))
        {
            IData inparam = new DataMap();
            for (int i = 0, len = ids.size(); i < len; i++)
            {

                IData data = ids.getData(i);
                data.put("AREA_CODE_H", areaCode);
                String sn = data.getString("SERIAL_NUMBER", "");
                String remove_tag = data.getString("REMOVE_TAG", "");
                String userId = data.getString("USER_ID", "");
                IData feeData = AcctCall.getOweFeeByUserId(userId);
                int mony = feeData.getInt("ALL_OWE_FEE") / 100;
                data.put("OWEFEE", mony);
                String brandName = UpcCall.queryBrandNameByChaVal(data.getString("BRAND_CODE"));
                data.put("BRAND_NAME", brandName);
                IDataset actReturns = AcctCall.getCancelAcctInfos(sn, userId, "2", "3", SysDateMgr.decodeTimestamp(start_date, SysDateMgr.PATTERN_TIME_YYYYMM), SysDateMgr.decodeTimestamp(end_date, SysDateMgr.PATTERN_TIME_YYYYMM));// QAM_MASTERBILLQRY
                if (IDataUtil.isNotEmpty(actReturns))
                {
                    IData actReturn = actReturns.getData(0);
                    if ("0".equals(actReturn.getString("BILL_PAY_TAG")))
                    {
                        data.put("CANCEL_ACCT", "未销账");
                    }
                    else if ("1".equals(actReturn.getString("BILL_PAY_TAG")))
                    {
                        data.put("CANCEL_ACCT", "销账");
                        data.put("CANCEL_ACCT_TIME", actReturn.getString("UPDATE_TIME"));
                    }
                }
            }
        }
        return ids;
    }

}
