
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.SaleDepsoitException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;

public class SaleDepositSVC extends CSBizService
{
    private static final long serialVersionUID = -8670525685462940999L;

    public IData checkDepositGiftUser(IData input) throws Exception
    {
        String giftSerialNumber = input.getString("GIFT_SERIAL_NUMBER", "");
        String activeUserId = input.getString("USER_ID", "");
        String giftUserId = checkDepositGiftUser(activeUserId, giftSerialNumber);
        String productId = input.getString("PRODUCT_ID");
        String packageId = input.getString("PACKAGE_ID");
        String campnType = input.getString("CAMPN_TYPE");
        return checkDepositGiftUserByBre(giftUserId, productId, packageId, campnType);
    }

    private String checkDepositGiftUser(String activeUserId, String giftSerialNumber) throws Exception
    {
        IData giftUser = UcaInfoQry.qryUserInfoBySn(giftSerialNumber);
        if (IDataUtil.isEmpty(giftUser))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        if (StringUtils.isNotBlank(activeUserId))
        {
            if (activeUserId.equals(giftUser.getString("USER_ID")))
            {
                CSAppException.apperr(SaleDepsoitException.CRM_SALEDEPOSIT_1);
            }
        }

        return giftUser.getString("USER_ID");
    }

    private IData checkDepositGiftUserByBre(String giftUserId, String productId, String packageId, String campnType) throws Exception
    {
        IData ruleParam = new DataMap();
        ruleParam.put("TF_B_TRADE_SALEACTIVE", "");
        ruleParam.put("ACTION_TYPE", SaleActiveBreConst.ACTION_TYPE_CHK_DEPOSIT_GIFT_USER);
        ruleParam.put("RULE_BIZ_TWIG_CODE", "0");
        ruleParam.put("TRADE_TYPE_CODE", "240");

        UcaData uca = UcaDataFactory.getUcaByUserId(giftUserId);
        ruleParam.put("SERIAL_NUMBER", uca.getSerialNumber());
        ruleParam.put("USER_ID", uca.getUserId());
        ruleParam.put("BRAND_CODE", uca.getUserNewBrandCode());
        ruleParam.put("OPEN_DATE", uca.getUser().getOpenDate());
        ruleParam.put("ACCT_ID", uca.getAcctId());
        ruleParam.put("ACCT_DAY", uca.getAcctDay());
        ruleParam.put("FIRST_DATE", uca.getFirstDate());
        ruleParam.put("NEXT_ACCT_DAY", uca.getNextAcctDay());
        ruleParam.put("NEXT_FIRST_DATE", uca.getNextFirstDate());
        ruleParam.put("EPARCHY_CODE", uca.getUserEparchyCode());

        ruleParam.put("PRODUCT_ID", productId);
        ruleParam.put("PACKAGE_ID", packageId);
        ruleParam.put("CAMPN_TYPE", campnType);

        ruleParam.put("IN_MODE_CODE", getVisit().getInModeCode());
        ruleParam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        ruleParam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        ruleParam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        ruleParam.put("TRADE_CITY_CODE", getVisit().getCityCode());

        IData ruleResult = BizRule.bre4SuperLimit(ruleParam);
        CSAppException.breerr(ruleResult);

        IData rtReuslt = new DataMap();
        IDataset choiceInfo = ruleResult.getDataset("TIPS_TYPE_CHOICE");
        if (IDataUtil.isNotEmpty(choiceInfo))
        {
            rtReuslt.put("TIPS_TYPE_CHOICE", choiceInfo);
        }
        IDataset tipInfo = ruleResult.getDataset("TIPS_TYPE_TIP");
        if (IDataUtil.isNotEmpty(tipInfo))
        {
            rtReuslt.put("TIPS_TYPE_TIP", tipInfo);
        }

        rtReuslt.put("GIFT_USER_ID", giftUserId);
        rtReuslt.put("GIFT_START_DATE", ruleResult.getString("GIFT_START_DATE"));
        rtReuslt.put("GIFT_END_DATE", ruleResult.getString("GIFT_END_DATE"));

        return rtReuslt;
    }
}
