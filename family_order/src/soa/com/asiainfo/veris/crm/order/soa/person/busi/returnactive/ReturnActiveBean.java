
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.exception.ReturnActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.GGCardInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SaleservParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr;

public class ReturnActiveBean extends CSBizBean
{

    public static IDataset checkExchangeCard(String userId, String cardCode, String cardPassWord, String productId) throws Exception
    {
        if (cardCode.length() < 6)
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_3, cardCode);
        }
        IData ggCardInfo = GGCardInfoQry.getGGCardInfoByPK(cardCode.substring(0, 6), cardPassWord, "0");
        if (IDataUtil.isEmpty(ggCardInfo))
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_4, cardCode, cardPassWord);
        }
        String encryption = ggCardInfo.getString("RSRV_STR1", "");// 加密编码
        // 判断加密编码是否正确
        String temp = PasswdMgr.encryptPassWD(cardPassWord, cardCode);
        if (!temp.equals(encryption))
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_18);
        }

        String itemCode = ggCardInfo.getString("ITEM_CODE");
        IDataset giftList = SaleservParamInfoQry.queryByCode1("RETURNACTIVE_GIFT", itemCode, CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(giftList))
        {
            CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_19, itemCode);
        }

        IDataset userOtherList = UserOtherInfoQry.getUserOverProvinceInfo(userId, "RAGGCARD", cardCode, null);
        if (IDataUtil.isEmpty(userOtherList))
        {

        }

        IDataset results = new DatasetList();
        String doMode = userOtherList.getData(0).getString("RSRV_STR4", "0");
        if ("4".equals(doMode))
        {
            for (int i = 0, size = giftList.size(); i < size; i++)
            {
                IData tmp = giftList.getData(i);
                if ("4".equals(tmp.getString("PARA_CODE2")))
                {
                    results.add(tmp);
                }
            }
        }
        else
        {
            for (int i = 0, size = giftList.size(); i < size; i++)
            {
                IData tmp = giftList.getData(i);
                if (!"4".equals(tmp.getString("PARA_CODE2")))
                {
                    results.add(tmp);
                }
            }
        }

        /**
         * 一等奖礼品：ipad (0104) 二等奖礼品：1、充电宝(0204)；2、电脑桌 (0205) 只适用于 10001005-动感地带校园卡,10000701-校园卡,10001139-动感地带校园音乐套餐
         */
        IDataset rtGiftList = new DatasetList();
        if (!"10001005".equals(productId) && !"10000701".equals(productId) && !"10001139".equals(productId))
        {
            for (int i = 0, size = results.size(); i < size; i++)
            {
                IData giftInfo = results.getData(i);
                String giftCode = giftInfo.getString("PARA_CODE");
                if (!"0104".equals(giftCode) && !"0204".equals(giftCode) && !"0205".equals(giftCode))
                {
                    rtGiftList.add(giftInfo);
                }
            }
        }
        else
        {
            rtGiftList = results;
        }

        rtGiftList.getData(0).put("ITEM_NAME", ggCardInfo.getString("ITEM_NAME"));

        return rtGiftList;
    }

    public IData getReturnActiveInfo(IData data) throws Exception
    {
        IData rtData = new DataMap();
        // 调账务接口获取各个账户余额
        IDataset results = new DatasetList();// 后面要改
        // 测试代码
        IData info = new DataMap();
        info.put("DEPOSIT_CODE", "0");
        info.put("ODD_MONEY", "10000");
        info.put("ALL_NEW_BALANCE", "10000");
        info.put("ACCT_BALANCE_ID", "12321321");
        results.add(info);

        if (IDataUtil.isEmpty(results))
        {
            CSAppException.apperr(FeeException.CRM_FEE_157);
        }

        int limitMoney = 5000;// 以后这里改成读参数

        int cash = 0;// 现金账户余额
        int cardFee = 0;// 充值卡账务余额
        int allNewBalance = 0;// 实时结余
        for (int i = 0, size = results.size(); i < size; i++)
        {
            IData tmp = results.getData(i);

            String depositCode = tmp.getString("DEPOSIT_CODE");
            if (StringUtils.isBlank(depositCode))
            {
                CSAppException.apperr(FeeException.CRM_FEE_156);
            }

            if ("0".equals(depositCode))
            {
                cash += tmp.getInt("ODD_MONEY") + tmp.getInt("EVEN_MONEY");
                rtData.put("CASH_ACCT_BALANCE_ID", tmp.getString("ACCT_BALANCE_ID"));
            }
            else if ("1".equals(depositCode))
            {
                cardFee += tmp.getInt("ODD_MONEY") + tmp.getInt("EVEN_MONEY");
                rtData.put("CARD_ACCT_BALANCE_ID", tmp.getString("ACCT_BALANCE_ID"));
            }
        }

        allNewBalance = results.getData(0).getInt("ALL_NEW_BALANCE");
        if ((cash + cardFee) < limitMoney)
        {
            CSAppException.apperr(FeeException.CRM_FEE_158, limitMoney / 100);
        }

        if (allNewBalance < limitMoney)
        {
            CSAppException.apperr(FeeException.CRM_FEE_159, limitMoney / 100);
        }

        if ((cash + cardFee) < allNewBalance)
        {
            rtData.put("HAVE_NUM", (cash + cardFee) / limitMoney);
        }
        else
        {
            rtData.put("HAVE_NUM", allNewBalance / limitMoney);
        }
        rtData.put("CASH", cash);
        rtData.put("CARDFEE", cardFee);
        rtData.put("LIMIT_MONEY", limitMoney);

        return rtData;
    }
}
