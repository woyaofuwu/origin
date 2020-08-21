
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetopen;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

public class CTTBroadBandOpenSVC extends CSBizService
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public IDataset checkBroadbandOpenFree(IData param) throws Exception
    {

        IData inparam = new DataMap();
        String serialNumber = param.getString("SERIAL_NUMBER");
        IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber, CSBizService.getVisit().getStaffEparchyCode());
        IDataset vipInfos = CustVipInfoQry.queryVipInfoBySn(serialNumber, "0");
        String vipClassId = "Z";
        if (IDataUtil.isNotEmpty(vipInfos))
        {
            vipClassId = vipInfos.getData(0).getString("VIP_CLASS_ID");
        }
        inparam.put("TRADE_TYPE_CODE", "9717");
        inparam.put("PRODUCT_ID", userInfo.getString("PRODUCT_ID", "-1"));
        inparam.put("VIP_CLASS_ID", vipClassId);
        inparam.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        inparam.put("TRADE_FEE_TYPE", "0");

        IDataset results = ProductFeeInfoQry.getTradeFee(inparam);
        IData result = new DataMap();
        IDataset returnData = new DatasetList();
        if (results != null && !results.isEmpty())
        {
            IData feeList = new DataMap();
            for (int i = 0; i < results.size(); i++)
            {
                if ("9203".equals(results.getData(i).getString("FEE_TYPE_CODE", "")))
                    ;// 宽带停机费
                feeList = results.getData(i);
                break;
            }

            String strLastStopTime = userInfo.getString("LAST_STOP_TIME", "");

            if ("".equals(strLastStopTime))
            {
                // 没有最后停机时间退出
                CSAppException.apperr(CrmUserException.CRM_USER_17, "没有最后停机时间");
            }

            int months = SysDateMgr.monthInterval(strLastStopTime, SysDateMgr.getSysTime());// 计算报停时间到报开时间之间的自然月

            if (months > 1)
            { // 如果相隔月份大于1个月，增加多余月份的停机费，每月10月
                months = months - 1;
                int fee = feeList.getInt("FEE");
                String feeTypeCode = feeList.getString("FEE_TYPE_CODE");
                result.put("FEE", String.valueOf(fee * months));
                result.put("FEE_TYPE_CODE", feeTypeCode);
                result.put("FEE_MODE", "0");
                returnData.add(result);
            }
        }
        return returnData;
    }
}
