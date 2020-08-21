
package com.asiainfo.veris.crm.order.soa.person.busi.ecardgprsbind;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BrandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ECardGprsBindBean extends CSBizBean
{

    public IDataset checkEcardPhone(IData input) throws Exception
    {
        IDataset dsResult = new DatasetList();
        String serialNumEcard = input.getString("SERIAL_NUMBER");
        UcaData ucaData = UcaDataFactory.getNormalUcaByQry(serialNumEcard, true, false);
        String nextAcctDay = ucaData.getNextAcctDay();// 有值就代表有预约账期
        String acctDay = ucaData.getAcctDay();

        if (StringUtils.isNotBlank(acctDay) && StringUtils.isNotBlank(nextAcctDay) && !StringUtils.equals(acctDay, nextAcctDay))// 存在预约账期且新老账期日不一致
        {
            CSAppException.apperr(CrmUserException.CRM_USER_983, serialNumEcard, acctDay, nextAcctDay);
        }

        if (!StringUtils.equals("G005", ucaData.getBrandCode()))
        {
            CSAppException.apperr(BrandException.CRM_BRAND_39);
        }

        IDataset relationInfos = RelaUUInfoQry.qryRelaByUserIdB(ucaData.getUserId(), "80", "2");
        if (IDataUtil.isNotEmpty(relationInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "副卡已经存在绑定关系，不能再次绑定！");
        }
        if (!StringUtils.equals("1", acctDay))
        {
            String mesg = "该用户是非自然月账期用户，办理该业务将变更为自然月用户！";
            dsResult.add(getTipsInfoData("1", mesg, "245"));
        }
        dsResult.add(getTipsInfoData("0", "", "245"));
        return dsResult;
    }

    /*
     * 随e行手机号,资料获取
     */
    public IDataset checkPhone(IData iuput) throws Exception
    {
        IDataset dsResult = new DatasetList();
        String serialNumEcard = iuput.getString("SERIAL_NUMBER");
        UcaData ucaData = UcaDataFactory.getNormalUcaByQry(serialNumEcard, true, false);

        CustomerTradeData customerData = ucaData.getCustomer();
        IData custData = customerData.toData();

        custData.put("SERIAL_NUMBER", serialNumEcard);
        dsResult.add(custData);
        getAcctInfo(ucaData.getUserId());
        return dsResult;
    }

    /**
     * 获取账户资料数据 分散账期新增 可放到规则中
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IData getAcctInfo(String userId) throws Exception
    {
        IData account = null;
        // 获取用户当前的默认帐户
        account = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
        if (account == null || account.size() == 0)
        {
            // iparam.put("DEFAULT_TAG", 1);
            // 目前这个查询条件不具备。
            // accounts=dao.queryListByCodeCode("TF_A_PAYRELATION", "SEL_BY_USER_MAX", iparam);
            if (account == null || account.isEmpty())
            {
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_103);
            }
        }
        /*if (account.size() >= 2)
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_104);
        }*/

        IData accountInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
        if (accountInfo == null || accountInfo.size() == 0)
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_105);
        }

        return accountInfo;
    }

    /**
     * @Description: 该函数的功能描述
     * @param tipsType
     * @param tipsInfo
     * @param tipsCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: May 19, 2014 8:37:08 PM
     */
    public IData getTipsInfoData(String tipsType, String tipsInfo, String tipsCode) throws Exception
    {
        IData tipsInfoData = new DataMap();
        tipsInfoData.clear();

        tipsInfoData.put("TIPS_TYPE", tipsType);
        tipsInfoData.put("TIPS_INFO", tipsInfo);
        tipsInfoData.put("TIPS_CODE", tipsCode);

        return tipsInfoData;
    }

    /*
     * 获取绑定信息
     */
    public IDataset queryCrmInfos(IData input) throws Exception
    {

        // 获取用户当前信息
        IDataset userChangeParas = UserInfoQry.getUserInfoChgByUserIdCurvalid(input.getString("USER_ID"));
        IData userChange = userChangeParas.size() > 0 ? (IData) userChangeParas.get(0) : null;

        String curBrand = null;
        String curProduct = null;
        String nextBrand = "";
        String nextProduct = "";

        if (IDataUtil.isNotEmpty(userChange))
        {
            curBrand = userChange.getString("BRAND_CODE", "");
            curProduct = userChange.getString("PRODUCT_ID", "");
        }

        // 获取用户预约产品资料

        IDataset bookTrades = TradeInfoQry.getTradeBookByUserId(input.getString("USER_ID"),CSBizBean.getUserEparchyCode());
        if (IDataUtil.isNotEmpty(bookTrades))
        {
            IData bookTrade = bookTrades.getData(0);
            nextBrand = bookTrade.getString("BRAND_CODE", "");
            nextProduct = bookTrade.getString("PRODUCT_ID", "");
            if (!StringUtils.equals("G001", nextBrand))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "下月品牌不是全球通品牌，不能办理该业务");
            }
        }
        else
        {
            // 获取下月生效产品、品牌
            IDataset nextChanges = UserInfoQry.getUserInfoChgByUserIdNxtvalid(input.getString("USER_ID"));
            if (nextChanges.size() > 0)
            {
                IData nextChange = nextChanges.getData(0);
                nextBrand = nextChange.getString("BRAND_CODE", "");
                nextProduct = nextChange.getString("PRODUCT_ID", "");
                if (!StringUtils.equals("G001", nextBrand))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "下月品牌不是全球通品牌，不能办理该业务");
                }
            }
            else
            {
                CSAppException.apperr(CustException.CRM_CUST_73);
            }
        }

        IDataset dsResult = new DatasetList();
        IData result = new DataMap();
        result.put("CUR_BRAND", curBrand);
        result.put("CUR_BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(curBrand));
        result.put("NEXT_BRAND", nextBrand);
        result.put("NEXT_BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(nextBrand));
        result.put("CUR_PRODUCT_ID", curProduct);
        result.put("CUR_PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(curProduct));
        result.put("NEXT_PRODUCT_ID", nextProduct);
        result.put("NEXT_PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(nextProduct));
        dsResult.add(result);

        return dsResult;
    }

}
