
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.cttnet.cttnetcreateuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.BroadBandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserTelephoeInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CttBroadbandSVC.java
 * @Description: 铁通宽带业务view服务
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-3-3 上午10:41:40 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-3-3 yxd v1.0.0 修改原因
 */
public class CttBroadbandSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * 查询欠费信息
     * 
     * @param pd
     * @param inparams
     * @param page
     * @return 欠费信息，RSRV_NUM1:往月欠费 RSRV_NUM2:实时欠费 RSRV_NUM3:实时结余
     * @throws Exception
     * @author
     */
    public static IData getOweFeeByUserId(IData inparam) throws Exception
    {
        // 按证件号码，用户查询欠费通过getMode 在QCS_CheckOweUser实现
        // IDataset results = TuxedoHelper.callTuxSvc(pd, "QCS_CheckOweUser", inparam, true);
        IData result = AcctCall.getOweFeeByUserId(inparam.getString("USER_ID"));

        // IData result = new DataMap();
        if (IDataUtil.isNotEmpty(result))
        {
            // result = (IData) results.get(0);
            int resultCode = result.getInt("X_RESULTCODE");
            String resultInfo = result.getString("X_RESULTINFO");

            if (resultCode != 0)
            {
                result.put("RSRV_NUM1", "0");
                result.put("RSRV_NUM2", "0");
                result.put("RSRV_NUM3", "0");
                return result;
            }

            String rsrvNum1 = result.getString("RSRV_NUM1");
            String rsrvNum2 = result.getString("RSRV_NUM2");
            String rsrvNum3 = result.getString("RSRV_NUM3");

            if (rsrvNum1 == null || "".equals(rsrvNum1) || rsrvNum1.length() <= 0)
            {
                result.put("RSRV_NUM1", "0");
            }
            if (rsrvNum2 == null || "".equals(rsrvNum2) || rsrvNum2.length() <= 0)
            {
                result.put("RSRV_NUM2", "0");
            }
            if (rsrvNum3 == null || "".equals(rsrvNum3) || rsrvNum3.length() <= 0)
            {
                result.put("RSRV_NUM3", "0");
            }
        }
        else
        {
            /* ============没有查询到欠费的用户默认欠费为0======== */
            result.put("RSRV_NUM1", "0");
            result.put("RSRV_NUM2", "0");
            result.put("RSRV_NUM3", "0");

            /* ======================END======================== */

        }
        return result;
    }

    /**
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkAcctId(IData param) throws Exception
    {
        IDataset result = new DatasetList();
        String acctId = param.getString("widenetAcctId");

        result = WidenetInfoQry.getUserWidenetActInfos(acctId);
        // result = UserAccessAcctInfoQry.qryInfoByAccessAcctId(acctId);
        if (!result.isEmpty())
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_8);

        IDataset tradeinfos = TradeInfoQry.qryTradeInfosBySnTrade(acctId, "0", "9711", "9711");
        if (!IDataUtil.isEmpty(tradeinfos))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_0, acctId);
        }

        /** 若为军区用户,需要预占军区用户宽带帐号表 **/
        if (param.getString("ARAM_USER_TAG", "").equals("yes"))
        {
            boolean flag = BroadBandInfoQry.updateAccountIp(acctId, "2");// 预占状态.1为正式占用

            if (!flag)
            {
                CSAppException.apperr(BroadBandException.CRM_BROADBAND_102);
            }
        }
        else
        {
            int num = BroadBandInfoQry.checkAccountIdInAccountIp("0", acctId);
            if (num > 0)
            {
                CSAppException.apperr(BroadBandException.CRM_BROADBAND_103);
            }
        }
        IDataset returnDataset = new DatasetList();
        IData data = new DataMap();
        data.put("checkAccountFlag", "1");
        returnDataset.add(data);
        return returnDataset;
    }

    public IDataset checkCttPhone(IData param) throws Exception
    {
        IDataset tradetelinfos = null;
        String serialNumber = param.getString("SERIAL_NUMBER");
        int flag = 0;
        /** 先查询固话号码是否在未完工的工单信息中 **/

        IDataset tradeinfos = TradeInfoQry.qryTradeInfosBySnTrade(serialNumber, "0", "9701", "9701");

        if (tradeinfos.size() > 0)
        {
            String strTradeId = tradeinfos.getData(0).getString("TRADE_ID");
            tradetelinfos = TradeInfoQry.qryTradeTelephoneInfosByTradeId(strTradeId);
            if (tradetelinfos.size() > 0)
            {
                flag = 1;
                tradetelinfos.getData(0).put("SERIAL_NUMBER", serialNumber);
                tradetelinfos.getData(0).put("FINISH_STATE", "未完工");
            }
        }
        if (flag == 0)
        {
            /** 再查询固话号码是否在已完工的用户表中 **/
            IDataset userinfos = UserInfoQry.getUserInfoBySnNetTypeCode(serialNumber, "0", "12");// 固话网别
            if (userinfos.size() > 0)
            {
                String strUserId = userinfos.getData(0).getString("USER_ID");
                tradetelinfos = UserTelephoeInfoQry.qryUserTelephoneByUserId(strUserId);
                if (tradetelinfos.size() > 0)
                {
                    tradetelinfos.getData(0).put("SERIAL_NUMBER", serialNumber);
                    tradetelinfos.getData(0).put("FINISH_STATE", "已完工");
                }
            }
        }
        IDataset result = new DatasetList();
        if (IDataUtil.isNotEmpty(tradetelinfos))
            result.add(tradetelinfos.getData(0));
        return result;

    }

    /**
     * 证件号校验
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset checkPsptLimit(IData param) throws Exception
    {
        // 校验是否是黑名单用户
        String psptId = param.getString("PSPT_ID");
        String psptTypeCode = param.getString("PSPT_TYPE_CODE");
        IDataset blackCustInfos = UCustBlackInfoQry.qryBlackCustInfo(psptTypeCode, psptId);
        if (blackCustInfos.size() > 0)
        {
            CSAppException.apperr(CustException.CRM_CUST_48);
        }

        // 欠费标记
        int ownFeeCount = 0;
        String message = "";
        IData result = new DataMap();

        String cityCode = "";
        IDataset custInfoPspt = CustomerInfoQry.getCustInfoByPspt(psptTypeCode, psptId);
        if (IDataUtil.isNotEmpty(custInfoPspt))
        {
            IData custParam = new DataMap();
            IData custInfo = null;
            for (int i = 0; i < custInfoPspt.size(); i++)
            {
                custInfo = custInfoPspt.getData(i);
                cityCode = custInfo.getString("CITY_CODE");
                String custId = custInfo.getString("CUST_ID");
                IData custPersonInfos = UcaInfoQry.qryPerInfoByCustId(custId);
                if (IDataUtil.isNotEmpty(custPersonInfos))
                {

                    custInfo.putAll(custPersonInfos);
                    custInfo.put("CITY_CODE", cityCode);
                }
                else
                {
                    custInfoPspt.remove(i);
                    i--;
                }
            }

            if (IDataUtil.isEmpty(custInfoPspt))
            {
                CSAppException.apperr(CustException.CRM_CUST_45);
            }

            IData userParam = new DataMap();
            IDataset userQResult = new DatasetList();
            for (int i = 0; i < custInfoPspt.size(); i++)
            {
                userParam = custInfoPspt.getData(i);
                userQResult = UserInfoQry.getUserInfoByCustID(userParam.getString("CUST_ID"), CSBizService.getVisit().getStaffEparchyCode());

                if (userQResult.size() > 0)
                {
                    for (int j = 0; j < userQResult.size(); j++)
                    {
                        IData userQData = userQResult.getData(j);
                        IData oweFeePara = new DataMap();
                        oweFeePara.clear();

                        oweFeePara.put("EPARCHY_CODE", userQData.get("EPARCHY_CODE"));
                        oweFeePara.put(Route.ROUTE_EPARCHY_CODE, CSBizService.getVisit().getStaffEparchyCode());
                        oweFeePara.put("GET_MODE", "GETOWEFEEBYUSERID");
                        oweFeePara.put("USER_ID", userQData.get("USER_ID"));
                        oweFeePara.put("ID", userQData.get("USER_ID"));
                        oweFeePara.put("ID_TYPE", "1");
                        IData userOweFee = getOweFeeByUserId(oweFeePara);
                        // IData userOweFee = new DataMap();
                        // userOweFee.put("RSRV_NUM1", "100");
                        // userOweFee.put("RSRV_NUM2", "200");
                        // userOweFee.put("RSRV_NUM3", "300");
                        if (userOweFee.size() > 0)
                        {
                            int fee2 = userOweFee.getInt("RSRV_NUM1");
                            if (fee2 > 0)
                            {
                                ownFeeCount++;
                                String strFee = userOweFee.getString("RSRV_NUM2");
                                String fee = FeeUtils.Fen2Yuan(strFee);
                                message = message + "用户:" + userQResult.get(j, "SERIAL_NUMBER") + "有实时欠费" + fee + ";";
                            }
                        }
                    }
                }

            }

            int hadOwefeeUser = 0;
            if (ownFeeCount > 0)
            {
                message = "当前用户证件下共有【" + ownFeeCount + "】个欠费在网用户！" + "\r\n" + message + "\r\n" + "是否继续？";

                hadOwefeeUser = 1;
            }

            result.put("MESSAGE", message);
            result.put("HAS_OWEFEE_USER", hadOwefeeUser);
        }
        result.put("CUST_INFO", custInfoPspt);
        IDataset results = new DatasetList();
        results.add(result);
        return results;
    }

    /**
     * 根据配置的账号规则生成默认账号
     * 
     * @return
     * @throws Exception
     */
    public IDataset getAcctList(IData param) throws Exception
    {
        IDataset result = new DatasetList();
        IDataset paramset = new DatasetList();
        IData accountinfo = new DataMap();
        String productSpec = param.getString("PRODUCT_SPEC");
        String productId = param.getString("PRODUCT_ID");
        /** start */

        paramset = ParamInfoQry.getCommparaByCode("CSM", "1309", productId, CSBizBean.getTradeEparchyCode());
        if (!paramset.isEmpty())
        {// 判断是否为军区产品

            IDataset acctinfos = BroadBandInfoQry.qryAccountIp("0", CSBizBean.getTradeEparchyCode());
            if (acctinfos.isEmpty())
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1003, "该业务区[" + CSBizBean.getTradeEparchyCode() + "]没有帐号及IP地址可分配的资源!");
            }
            accountinfo.put("WIDENET_ACCT_ID", acctinfos.getData(0).getString("ACCT_ID"));
            accountinfo.put("WIDENET_IP_ADDRESS", acctinfos.getData(0).getString("IP_ADDRESS"));
            accountinfo.put("WIDENET_ACCT_PASSWD", "123456");// 默认密码
            accountinfo.put("ARAM_USER_TAG", "yes");// 设置军区用户标志
        }
        else
        {
            int count = 1;
            String acctNo = "";

            paramset = ParamInfoQry.getCommparaByCode("CSM", "1311", "", CSBizBean.getTradeEparchyCode());
            if (!paramset.isEmpty())
                count = paramset.getData(0).getInt("PARA_CODE1", 1);
            for (int i = 0; i < count; i++)
            {
                paramset.clear();
                IData temp = new DataMap();
                paramset = ParamInfoQry.getCommparaByCode("CSM", "1310", "-1", CSBizBean.getTradeEparchyCode());
                if (paramset.isEmpty())
                {
                    paramset = ParamInfoQry.getCommparaByCode("CSM", "1310", productSpec, CSBizBean.getTradeEparchyCode());
                }
                if (paramset.isEmpty())
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_1003, "该地州[" + CSBizBean.getTradeEparchyCode() + "]没有配置账号规则");
                }
                if (!paramset.getData(0).getString("PARA_CODE2").equals(""))
                {
                    acctNo = paramset.getData(0).getString("PARA_CODE1") + "";
                    accountinfo.put("ACCT_PREFIX", paramset.getData(0).getString("PARA_CODE1"));// 指定以什么数字开头
                    accountinfo.put("ACCT_TAIL", paramset.getData(0).getString("PARA_CODE2"));// 指定的长度
                }
                accountinfo.put("WIDENET_ACCT_PASSWD", "123456");// 默认密码
                accountinfo.put("WIDENET_ACCT_ID", acctNo);// 帐号

            }
        }
        result.add(accountinfo);
        return result;
    }

    /**
     * 获得银行名称
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getBankBySuperBank(IData param) throws Exception
    {
        String superBankCode = param.getString("SUPER_BANK_CODE");
        return BankInfoQry.getBankBySuperBankCtt(superBankCode, null);
    }

    /**
     * @Function: getCttBroadBandPackageElements()
     * @Description: 获取铁通宽带元素
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-3-11 下午3:26:49 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-3-11 yxd v1.0.0 修改原因
     */
    public IDataset getCttBroadBandPackageElements(IData param) throws Exception
    {
        return ProductInfoQry.getBroadBandPackageElements(param.getString("PACKAGE_ID"), param.getString("EPARCHY_CODE"));
    }

    public IDataset getProductBySpec(IData param) throws Exception
    {
        CttBroadbandBean bean = BeanManager.createBean(CttBroadbandBean.class);
        return bean.getProductBySpec(param.getString("PROD_SPEC_TYPE"));
    }

}
