
package com.asiainfo.veris.crm.order.soa.person.common.util;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradefeeSubInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * @ClassName NoPhoneTradeUtil
 * @Description 无手机业务工具类
 * @Author ApeJungle
 * @Date 2019/7/8 10:23
 * @Version 1.0
 */
public final class NoPhoneTradeUtil
{

    /**
     * @param serialNumber
     * @return boolean
     * @description 校验开户号码的有效性 1) 校验是否在配置号段内；
     * @author ApeJungle
     * @date 2019/7/8 10:45
     */
    public static boolean checkSerialNumberValidation(String serialNumber) throws Exception
    {
        if (!StringUtils.isNumeric(serialNumber))
        {
            return false;
        }

        long sn = Long.parseLong(serialNumber);
        IDataset paraInfos = CommparaInfoQry.getCommpara("CSM", "2828", "680", CSBizBean.getUserEparchyCode());
        if (IDataUtil.isEmpty(paraInfos))
        {
            CSAppException.apperr(BizException.CRM_BIZ_5, "TD_S_COMMPARA表[PARAM_ATTR=2828][PARAM_CODE=680]参数配置信息不存在！");
        }

        boolean inPeriod = false;
        for (int i = 0, s = paraInfos.size(); i < s; i++)
        {
            IData para = paraInfos.getData(i);
            long minSn = para.getLong("PARA_CODE1");
            long maxSn = para.getLong("PARA_CODE2");

            if (sn > minSn && sn < maxSn)
            {
                inPeriod = true;
                break;
            }
        }

        return inPeriod;
    }

    /**
     * 判断是否无手机宽带
     *
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static boolean checkIfNoPhoneUserNew(String serialNumber) throws Exception
    {
        if (StringUtils.isNotBlank(serialNumber))
        {
            if (!StringUtils.startsWith(serialNumber, "KD_"))
            {
                serialNumber = "KD_" + serialNumber;
            }
            IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
            if (IDataUtil.isNotEmpty(userInfos))
            {
                String rsrvTag1 = userInfos.getData(0).getString("RSRV_TAG1");
                if (StringUtils.equals("N", rsrvTag1))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 判断无手机宽带号码是否为新数据（178号段）
     *
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static boolean isNewNoPhoneUser(String serialNumber) throws Exception
    {
        boolean falg = checkIfNoPhoneUserNew(serialNumber);
        if (falg)
        {
            if (StringUtils.startsWith(serialNumber, "KD_"))
            {
                serialNumber = StringUtils.substring(serialNumber, 3);
            }
            IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
            if (IDataUtil.isNotEmpty(userInfos))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断用户有没有办理光猫调测费（438）或者魔百和调测费（439）
     *
     * @param tradeId
     * @return
     * @throws Exception
     * @author guohuan
     */
    public static boolean checkWideUserFeeTest(String tradeId) throws Exception
    {
        IDataset tradeFeeInfos = TradefeeSubInfoQry.getTradefeeSubByTradeMode(tradeId, BofConst.FEE_MODE_ADVANCEFEE);
        if (IDataUtil.isNotEmpty(tradeFeeInfos))
        {
            for (int i = 0; i < tradeFeeInfos.size(); i++)
            {
                IData tradeFeeInfo = tradeFeeInfos.getData(i);
                if (StringUtils.equals("438", tradeFeeInfo.getString("FEE_TYPE_CODE")) || StringUtils.equals("439", tradeFeeInfo.getString("FEE_TYPE_CODE")))
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否无手机宽带,仅规则【CheckNoPhoneLimitTradeTypeRule|无手机宽带账号不能办理非无手机业务】使用
     *
     * @param serialNumber
     * @author guohuan
     * @return
     * @throws Exception
     * @author guohuan
     */
    public static boolean checkIfNoPhoneUserNewForRule(String serialNumber) throws Exception
    {
        String wideSerialNumber = "";
        if (StringUtils.isNotBlank(serialNumber))
        {
            if (!StringUtils.startsWith(serialNumber, "KD_"))
            {
                wideSerialNumber = "KD_" + serialNumber;
            }
            else
            {
                wideSerialNumber = serialNumber;
            }
            IDataset wideUserInfos = UserInfoQry.getUserInfoBySn(wideSerialNumber, "0");
            if (IDataUtil.isNotEmpty(wideUserInfos))
            {
                String rsrvTag1 = wideUserInfos.getData(0).getString("RSRV_TAG1");
                if (StringUtils.equals("N", rsrvTag1))
                {
                    return true;
                }
            }
            else
            {
                // 存在宽带用户已销户，但是虚拟手机号码在数据库中还正常的垃圾数据，用该用户主产品拦截（这类用户主产品只有三个）
                IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
                if (IDataUtil.isNotEmpty(userInfos))
                {
                    String userId = userInfos.getData(0).getString("USER_ID");
                    IDataset userMainProduct = UserProductInfoQry.queryMainProductNow(userId);
                    if (IDataUtil.isNotEmpty(userMainProduct))
                    {
                        String productId = userMainProduct.getData(0).getString("PRODUCT_ID");
                        IDataset productCheckConfig = StaticUtil.getStaticList("NO_PHONE_PRODUCT_CHECK");// 无手机宽带虚拟号码使用过的主产品采用静态参数配置，方便修改维护
                        if (IDataUtil.isEmpty(productCheckConfig))
                        {
                            CSAppException.appError("-1", "无手机宽带手机虚拟用户校验产品没有配置,请联系管理员!");
                        }
                        for (int i = 0; i < productCheckConfig.size(); i++)
                        {
                            String productCheck = productCheckConfig.getData(i).getString("DATA_ID");
                            if (StringUtils.equals(productCheck, productId))
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static boolean checkNophoneWideCreate(String tradeId) throws Exception
    {
        boolean nophoneWidetag = false;
        String noPhoneProductId = "";
        IDataset productConfig = StaticUtil.getStaticList("NO_PHONE_PRODUCT");
        if (IDataUtil.isEmpty(productConfig))
        {
            noPhoneProductId = "20191209";
        }

        noPhoneProductId = productConfig.first().getString("DATA_ID", "20191209");
        IDataset tradeProductInfos = TradeProductInfoQry.getTradeProductByTradeId(tradeId);
        if (IDataUtil.isNotEmpty(tradeProductInfos))
        {
            for (int i = 0; i < tradeProductInfos.size(); i++)
            {
                IData tempProduct = tradeProductInfos.getData(i);
                String productId = tempProduct.getString("PRODUCT_ID");
                String mainTag = tempProduct.getString("MAIN_TAG");
                if (StringUtils.equals(noPhoneProductId, productId) && StringUtils.equals("1", mainTag))
                {
                    nophoneWidetag = true;
                    break;
                }
            }
        }
        return nophoneWidetag;
    }

}
