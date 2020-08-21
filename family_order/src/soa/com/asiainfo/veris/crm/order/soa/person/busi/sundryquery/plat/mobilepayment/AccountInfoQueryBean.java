
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.mobilepayment;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

public class AccountInfoQueryBean extends CSBizBean
{

    /**
     * 修改手机支付的实名
     * 
     * @param trueName
     * @return
     * @throws Exception
     */
    public static IData changeRealName(String serialNumber, String trueName) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String transId = getOperNumb(tradeId, "BIP2B089");

        IDataset resultList = IBossCall.changeMobilePayAccountTrueName(serialNumber, trueName, tradeId, transId);
        IData result = resultList.isEmpty() ? null : resultList.getData(0);
        if (result != null && "0".equals(result.getString("X_RSPTYPE")))
        {
            // 修改实名
            realNameInset(result);
            if ("01".equals(result.getString("X_RSPCODE")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "实名信息更新失败！用户不存在");
            }
            if ("99".equals(result.getString("X_RSPCODE")))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "实名信息更新失败！其它错误");
            }
            return result;
        }
        else
        {
            String message = "调用一级BOSS接口失败，返回错误信息为：";
            if (result != null)
            {
                message += result.getString("X_RSPDESC");
            }
            else
            {
                message += "接口返回null";
            }
            CSAppException.apperr(PlatException.CRM_PLAT_74, message);
        }
        return null;
    }

    /**
     * 省BOSS的编码规则－3位省代码+8位业务编码（BIPCode）+14位组包时间YYYYMMDDHH24MMSS+6位流水号（定长） ，序号从000001开始，增量步长为1。
     * 
     * @param tradeId
     * @param bipCode
     * @return
     * @throws Exception
     */
    private static String getOperNumb(String tradeId, String bipCode) throws Exception
    {
        String provCode = getProvCode();
        String operNumb = provCode.substring(provCode.length() - 3) + bipCode + SysDateMgr.getSysDate("yyyyMMddHHmmss") + tradeId.substring(tradeId.length() - 6);
        return operNumb;
    }

    /**
     * 获取省代码
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public static String getProvCode() throws Exception
    {

        String provCode = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());

        if (provCode == null || provCode.length() == 0)
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "查询省代码无资料！");
        }

        return provCode;
    }

    /**
     * 查询手机支付的账户信息
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountInfo(String serialNumber) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String transId = getOperNumb(tradeId, "BIP2B092");
        IDataset resultList = IBossCall.queryMobilePayAccountInfo(serialNumber, tradeId, transId);
        IData result = null;
        if (resultList != null && !resultList.isEmpty())
        {
            result = resultList.getData(0);
        }

        if (result != null && "0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            return resultList;
        }
        else
        {
            String message = "调用一级BOSS接口失败，返回错误信息为：";
            if (result != null)
            {
                message += result.getString("X_RSPDESC");
            }
            else
            {
                message += "接口返回null";

            }

            CSAppException.apperr(PlatException.CRM_PLAT_74, message);
        }
        return null;
    }

    /**
     * 查询手机支付账户交易信息
     * 
     * @param serialNumber
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset queryAccountTransactionInfo(String serialNumber, String startDate, String endDate) throws Exception
    {
        IDataset returnList = new DatasetList();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (userInfo == null)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String custId = userInfo.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (custInfo == null)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_397);
        }
        else
        {
            IData custPerson = UcaInfoQry.qryCustInfoByCustId(custId);
            if (IDataUtil.isNotEmpty(custPerson))
            {
                custInfo.putAll(custPerson);
            }
        }
        returnList.add(custInfo);

        String tradeId = SeqMgr.getTradeId();
        String transId = getOperNumb(tradeId, "BIP2B094");

        IDataset resultList = IBossCall.queryMobilePayTransactionInfo(serialNumber, tradeId, transId, startDate, endDate);
        IData result = (resultList == null || resultList.isEmpty()) ? null : resultList.getData(0);
        if (result != null && "0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            returnList.add(DataHelper.toDataset(result));
            return returnList;
        }
        else
        {
            String message = "调用一级BOSS接口失败，返回错误信息为：";
            if (result != null)
            {
                message += result.getString("X_RSPDESC");
            }
            else
            {
                message += "接口返回null";
            }
            CSAppException.apperr(PlatException.CRM_PLAT_74, message);
        }
        return null;
    }

    public static void realNameInset(IData param) throws Exception
    {
        IData inparam = new DataMap();
        inparam.putAll(param);
        String sysDate = SysDateMgr.getSysTime();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }

        String userId = userInfo.getString("USER_ID");

        inparam.put("PARTITION_ID", userId.substring(userId.length() - 4));
        inparam.put("USER_ID", userId);
        inparam.put("RSRV_VALUE_CODE", "BIP2B093_T2040010");
        inparam.put("RSRV_VALUE", "ACCOUNTOPEN");
        inparam.put("RSRV_STR1", param.getString("REQ_NUM"));
        inparam.put("RSRV_STR2", param.getString("BOSS_SEQ"));
        inparam.put("RSRV_STR3", param.getString("SERIAL_NUMBER"));
        inparam.put("RSRV_STR4", param.getString("ACTION_ID"));
        inparam.put("RSRV_STR5", param.getString("ACTION_USERID"));
        inparam.put("RSRV_STR6", param.getString("CARD_TYPE"));
        inparam.put("RSRV_STR7", param.getString("CARD_NUM"));
        inparam.put("RSRV_STR8", param.getString("NAME"));
        inparam.put("RSRV_STR9", param.getString("REG_VER"));
        inparam.put("INST_ID", SeqMgr.getInstId());
        inparam.put("UPDATE_TIME", sysDate);
        inparam.put("START_DATE", sysDate);
        inparam.put("END_DATE", SysDateMgr.END_DATE_FOREVER);

        boolean b = false;
        try
        {
            b = Dao.save("TF_F_USER_OTHER", inparam, new String[]
            { "PARTITION_ID", "USER_ID", "RSRV_VALUE_CODE" });
        }
        catch (Exception e)
        {

        }

        if (!b)
        {
            Dao.insert("TF_F_USER_OTHER", inparam);
        }
    }
}
