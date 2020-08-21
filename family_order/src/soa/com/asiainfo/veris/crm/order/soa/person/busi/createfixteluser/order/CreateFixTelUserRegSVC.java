
package com.asiainfo.veris.crm.order.soa.person.busi.createfixteluser.order;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.PBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class CreateFixTelUserRegSVC extends OrderService
{

    // 批量处理时对号码进行校验
    private void BatCreateFixedUserBeforeCheck(IData data) throws Exception
    {
        // TODO Auto-generated method stub
        String sreNum = data.getString("SERIAL_NUMBER");
        if (StringUtils.isBlank(sreNum))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "电话号码不能为空！");
        }
        String strBatchOperType = data.getString("BATCH_OPER_TYPE");

        data.put("CHECK_TAG", "1"); // 需要资源校验
        if (strBatchOperType == "BATAPPENDTRUNKUSER")
        {
            String mainSeriNum = data.getString("MAIN_SERIAL_NUMBER");

            if (StringUtils.equals(sreNum, mainSeriNum)) // 代表号
            {
                data.put("CHECK_TAG", "0");
            }
        }

        if (StringUtils.equals("1", data.getString("CHECK_TAG")))
        {
            IDataset resultSet = UserInfoQry.getUserInfoBySnAll(sreNum);
            if (IDataUtil.isNotEmpty(resultSet))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码在用户资料表存在正常记录。！");
            }

            IDataset resultSetTrade = TradeInfoQry.CheckIsExistNotGHFinishedTrade(sreNum);
            if (!StringUtils.equals(resultSetTrade.getData(0).getString("ROW_COUNT"), "0"))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码还存在未完工工单！");
            }
        }
    }

    public String getOrderTypeCode() throws Exception
    {
        String isBat = "0";
        isBat = input.getString("IS_BAT", "0");
        String sreNum = input.getString("SERIAL_NUMBER");
        String mainSeriNum = input.getString("MAIN_SERIAL_NUMBER");
        // return this.input.getString("ORDER_TYPE_CODE", "9701");
        if (!StringUtils.equals("0", isBat))
        {
            String strBatchOperType = input.getString("BATCH_OPER_TYPE");
            if (StringUtils.equals("BATAPPENDTRUNKUSER", strBatchOperType))
            {
                if (StringUtils.equals(sreNum, mainSeriNum))
                {
                    input.put("MAIN_SERI_FLG", 1);
                    return this.input.getString("ORDER_TYPE_CODE", "9752");
                }
                else
                {
                    return "9751";
                }
            }
            else if (StringUtils.equals("BATCREATETRUNKUSER", strBatchOperType))
            {
                return "9751";
            }
            else
            {
                input.put("MAIN_SERI_FLG", 1);
                return this.input.getString("ORDER_TYPE_CODE", "9750");

            }
        }
        else
        {
            return this.input.getString("ORDER_TYPE_CODE", "9701");
        }

    }

    public String getTradeTypeCode() throws Exception
    {
        String isBat = "0";
        isBat = input.getString("IS_BAT", "0");
        String sreNum = input.getString("SERIAL_NUMBER");
        String mainSeriNum = input.getString("MAIN_SERIAL_NUMBER");
        // return this.input.getString("ORDER_TYPE_CODE", "9701");
        if (!StringUtils.equals("0", isBat))
        {
            String strBatchOperType = input.getString("BATCH_OPER_TYPE");
            if (StringUtils.equals("BATAPPENDTRUNKUSER", strBatchOperType))
            {
                if (StringUtils.equals(sreNum, mainSeriNum))
                {
                    return this.input.getString("TRADE_TYPE_CODE", "9752");
                }
                else
                {
                    return "9751";
                }
            }
            else if (StringUtils.equals("BATCREATETRUNKUSER", strBatchOperType))
            {
                return "9751";
            }
            else
            {
                return this.input.getString("TRADE_TYPE_CODE", "9750");
            }
        }
        else
        {
            return this.input.getString("TRADE_TYPE_CODE", "9701");
        }

    }

    public IDataset tradeReg(IData input) throws Exception
    {
        String isBat = "0";
        isBat = input.getString("IS_BAT");
        if (StringUtils.equals("1", isBat))
        {
            BatCreateFixedUserBeforeCheck(input);
            if (StringUtils.equals("1", input.getString("CHECK_TAG")))
            {
                // 暂时先屏蔽铁通接口，后面要调铁通的接口,这里写铁通的资源校验
                // b3.X_GETMODE="1";
                // b3.RES_TYPE_CODE="N";
                // b3.RES_TRADE_CODE="ICheckBatchPhoneCodeInfo";
                // QRM_IChkResInfoTT
                String xGetMode = "1";
                String resTypeCode = "N";
                String resTradeCode = "ICheckBatchPhoneCodeInfo";
                String mofficeId = input.getString("SIGN_PATH");
                String resNo = input.getString("SERIAL_NUMBER");
                PBossCall.chkResInfoTT(resTradeCode, xGetMode, resNo, resTypeCode, mofficeId);
            }

        }

        return super.tradeReg(input);

    }
}
