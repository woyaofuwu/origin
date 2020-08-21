
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.mobilepayment;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class AccountInfoQuerySVC extends CSBizService
{

    public static String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    /**
     * 修改手机支付账户实名信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset changeRealName(IData input) throws Exception
    {
        IData ret = AccountInfoQueryBean.changeRealName(input.getString("SERIAL_NUMBER"), input.getString("TRUE_NAME"));
        IDataset dataset = new DatasetList();
        dataset.add(ret);
        return dataset;
    }

    /**
     * 查询手机支付的账号信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryAccountInfo(IData input) throws Exception
    {
        IDataset result = AccountInfoQueryBean.queryAccountInfo(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isNotEmpty(result))
        {
            for (int i = 0; i < result.size(); i++)
            {
                IData rr = result.getData(i);
                String cardType = rr.getString("CARD_TYPE");
                rr.put("CARD_TYPE", encodeIdType(cardType));
            }
        }
        return result;
    }

    /**
     * 查询手机支付的账号交易流水信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryAccountTransactionInfo(IData param) throws Exception
    {
        return AccountInfoQueryBean.queryAccountTransactionInfo(param.getString("SERIAL_NUMBER"), param.getString("START_DATE"), param.getString("END_DATE"));
    }

    public IDataset querySmallPaymentCheckUpResult(IData param) throws Exception
    {
        return PlatInfoQry.querySmallPaymentCheckUpResult(param.getString("START_TIME"), param.getString("END_TIME"), param.getString("RECON_STATE"), param.getString("CANCEL_FLAG"), this.getPagination());
    }

    /**
     * 查询手机支付小额支付日志
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySmallPaymentLog(IData param) throws Exception
    {
        String startTime = null;
        String endTime = null;
        String month = param.getString("MONTH");
        // MONTH yyyyMM格式
        if (StringUtils.isNotBlank(month))
        {
            if (month.length() != 6)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "MONTH格式必须是yyyyMM格式");
            }
            startTime = month.substring(0, 4) + "-" + month.substring(4, 6) + "-01";
            endTime = SysDateMgr.getAddMonthsLastDay(1, startTime);
        }
        else
        {
            startTime = param.getString("START_TIME");
            endTime = param.getString("END_TIME");
        }

        return PlatInfoQry.querySmallPaymentLog(startTime, endTime, param.getString("SERIAL_NUMBER"));

    }
}
