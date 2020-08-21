
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.SvcPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetTradeQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ProductUtil;

public class QueryWidenetInfoBean extends CSBizBean
{

    /**
     * 查询所有宽带产品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getAllWideNetProduct(IData data) throws Exception
    {

        IData param = new DataMap();

        String strSn = data.getString("SERIAL_NUMBER");
        String strEparchy_code = CSBizBean.getTradeEparchyCode();
        String strTrade_staff_id = CSBizBean.getVisit().getStaffId();
        if (strSn == null || "".equals(strSn))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_7);
        }
        String strWideType;
        if (strSn.length() >= 11)
        {
            strSn = "KD_" + strSn;
        }
        IData result = WidenetTradeQuery.getUserWidenetInfo(strSn);
        if (result == null || result.size() == 0)
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_4);
        }
        strWideType = result.getString("RSRV_STR2");
        String mode = ""; // 处理不同产品
        if ("2".equals(strWideType))
        {
            mode = "09";// 针对adsl
        }
        else if ("3".equals(strWideType))
        {
            mode = "11";// 针对光纤
        }
        else
        {
            mode = "07";// 针对gpon
        }
        param.put("PRODUCT_MODE", mode);
        param.put("EPARCHY_CODE", strEparchy_code);
        param.put("TRADE_STAFF_ID", strTrade_staff_id);
        IDataset wideProductSet = WidenetTradeQuery.getProductParam(param);

        return wideProductSet;
    }

    /**
     * 宽带用户根据产品查询产品服务接口
     * 
     * @param params
     * @return IDataset
     * @throws Exception
     * @author zhouwu
     * @date 2014-06-24 11:03:02
     */
    public IDataset getServiceByProduct(IData params) throws Exception
    {
        String serialNumber = params.getString("SERIAL_NUMBER");// 宽带帐号
        String eparchyCode = params.getString("EPARCHY_CODE");
        String productId = params.getString("PRODUCT_ID");
        String tradeStaffId = params.getString("TRADE_STAFF_ID");

        if (StringUtils.isEmpty(serialNumber))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_7);
        }

        if (StringUtils.isEmpty(eparchyCode))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_8);
        }

        if (StringUtils.isEmpty(productId))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_9);
        }

        if (StringUtils.isEmpty(tradeStaffId))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_10);
        }

        IDataset widnetUsers = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
        if (IDataUtil.isEmpty(widnetUsers))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_4);
        }

        IData widnetUser = widnetUsers.getData(0);
        String userId = widnetUser.getString("USER_ID");
        boolean privForEle = params.getString("PRIV_FOR_ELE", "").equals("TRUE");
//        boolean privForPack = params.getString("PRIV_FOR_PACK", "").equals("TRUE");

//        IDataset productPackages = ProductUtil.getPackageByProduct(productId, userId, eparchyCode, privForPack);
//        String packageId = "";
        IDataset servTempElements = new DatasetList();
//
//        IDataset tmp = ProductUtil.getServElementByPackage(packageId, userId, privForEle);
//        if (IDataUtil.isNotEmpty(tmp))
//        {
//            servTempElements.addAll(tmp);// 老系统addAll()?
//        }
        
        
        String svcIds = "";
        
        IDataset elementLists = UpcCall.queryAllOfferEnablesByOfferIdAndRelType(productId, "2");
        
        if (IDataUtil.isNotEmpty(elementLists))
        {
            for (int i = 0; i < elementLists.size(); i++)
            {
                
                if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementLists.getData(i).getString("ELEMENT_TYPE_CODE")))
                {
                    servTempElements.add(elementLists.getData(i));
                    
                    svcIds += elementLists.getData(i).getString("ELEMENT_ID")+",";
                }
            }
        }
        
        IDataset userValidSvcs = UserSvcInfoQry.qryUserSvcByUserIdProId(userId, productId);
        
        //查询查询产品下所有的元素，包括已经失效的
        IDataset eglecElementLists = UpcCall.queryNeglectDateAllOfferEnablesByOfferId(productId, "2");
        
        if (IDataUtil.isNotEmpty(userValidSvcs) && IDataUtil.isNotEmpty(eglecElementLists))
        {
            for (int j = 0; j < userValidSvcs.size(); j++)
            {
                //如果用户当前有效的优惠，但优惠配置已经下架，则到所有优惠配置中去关联，包括已经失效下架的
                if (!svcIds.contains(userValidSvcs.getData(j).getString("DISCNT_CODE")))
                {
                    for (int k = 0; k < eglecElementLists.size(); k++)
                    {
                        if (userValidSvcs.getData(j).getString("SERVICE_ID").equals(eglecElementLists.getData(k).getString("ELEMENT_ID"))
                                && BofConst.ELEMENT_TYPE_CODE_SVC.equals(eglecElementLists.getData(k).getString("ELEMENT_TYPE_CODE")))
                        {
                            servTempElements.add(eglecElementLists.getData(k));
                        }
                    }
                }
            }
        }
        
        
        if (privForEle)
        {

            // 增加权限 add by zhouwu
            SvcPrivUtil.filterSvcListByPriv(CSBizBean.getVisit().getStaffId(), servTempElements);
        }

        return servTempElements;
    }

    /**
     * 查询用户宽带账号资料
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getWidenetCustAcctInfo(IData data) throws Exception
    {
        IDataset resultInfo = new DatasetList();
        if ("".equals(data.getString("MAIN_TAG", "")))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_30);
        }
        if ("".equals(data.getString("ACCT_ID", "")))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_31);
        }
        String acctId = data.getString("ACCT_ID", "");
        IDataset dataset = WidenetInfoQry.getUserWidenetActInfos(acctId);
        if (dataset != null && !dataset.isEmpty())
        {
            IData result = new DataMap();
            IData widenetactinfo = dataset.getData(0);

            String userId = widenetactinfo.getString("USER_ID");
            IData userdata = UcaInfoQry.qryUserInfoByUserId(userId);

            String custId = userdata.getString("CUST_ID");

            IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
            IData custdata = custInfo;

            IData personInfo = UcaInfoQry.qryPerInfoByCustId(custId);
            IData persondata = personInfo;

            IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
            IData acctdata = acctInfo;

            IDataset widenetInfo = WidenetInfoQry.getWidenetInfosByUserId(userId);
            IData widenetdata = widenetInfo.getData(0);

            result.put("USER_ID", userdata.getString("USER_ID"));
            result.put("CUST_ID", userdata.getString("CUST_ID"));
            result.put("USECUST_ID", userdata.getString("USECUST_ID"));
            result.put("BRAND_CODE", userdata.getString("BRAND_CODE"));
            result.put("PRODUCT_ID", userdata.getInt("PRODUCT_ID"));
            result.put("EPARCHY_CODE", userdata.getString("EPARCHY_CODE"));
            result.put("CITY_CODE", userdata.getString("CITY_CODE"));
            result.put("USER_PASSWD", userdata.getString("USER_PASSWD"));
            result.put("USER_TYPE_CODE", userdata.getString("USER_TYPE_CODE"));
            result.put("SERIAL_NUMBER", userdata.getString("SERIAL_NUMBER"));
            result.put("SCORE_VALUE", userdata.getString("SCORE_VALUE"));
            result.put("CREDIT_CLASS", userdata.getString("CREDIT_CLASS"));
            result.put("BASIC_CREDIT_VALUE", userdata.getString("BASIC_CREDIT_VALUE"));
            result.put("CREDIT_VALUE", userdata.getString("CREDIT_VALUE"));
            result.put("ACCT_TAG", userdata.getString("ACCT_TAG"));
            result.put("PREPAY_TAG", userdata.getString("PREPAY_TAG"));
            result.put("IN_DATE", userdata.getString("IN_DATE"));
            result.put("OPEN_DATE", userdata.getString("OPEN_DATE"));
            result.put("REMOVE_TAG", userdata.getString("REMOVE_TAG"));
            result.put("DESTROY_TIME", userdata.getString("DESTROY_TIME"));
            result.put("PRE_DESTROY_TIME", userdata.getString("PRE_DESTROY_TIME"));
            result.put("FIRST_CALL_TIME", userdata.getString("FIRST_CALL_TIME"));
            result.put("LAST_STOP_TIME", userdata.getString("LAST_STOP_TIME"));
            result.put("OPEN_MODE", userdata.getString("OPEN_MODE"));
            result.put("USER_STATE_CODESET", userdata.getString("USER_STATE_CODESET"));
            result.put("MPUTE_MONTH_FEE", userdata.getString("MPUTE_MONTH_FEE"));
            result.put("MPUTE_DATE", userdata.getString("MPUTE_DATE"));
            result.put("RSRV_STR1", userdata.getString("RSRV_STR1"));
            result.put("RSRV_STR2", userdata.getString("RSRV_STR2"));
            result.put("RSRV_STR3", userdata.getString("RSRV_STR3"));
            result.put("RSRV_STR4", userdata.getString("RSRV_STR4"));
            result.put("RSRV_STR5", userdata.getString("RSRV_STR5"));
            result.put("RSRV_STR6", userdata.getString("RSRV_STR6"));
            result.put("RSRV_STR7", userdata.getString("RSRV_STR7"));
            result.put("RSRV_STR8", userdata.getString("RSRV_STR8"));
            result.put("RSRV_STR9", userdata.getString("RSRV_STR9"));
            result.put("RSRV_STR10", userdata.getString("RSRV_STR10"));
            result.put("UPDATE_TIME", userdata.getString("UPDATE_TIME"));
            result.put("ASSURE_CUST_ID", userdata.getString("ASSURE_CUST_ID"));
            result.put("ASSURE_TYPE_CODE", userdata.getString("ASSURE_TYPE_CODE"));
            result.put("ASSURE_DATE", userdata.getString("ASSURE_DATE"));
            result.put("DEVELOP_EPARCHY_CODE", userdata.getString("DEVELOP_EPARCHY_CODE"));
            result.put("DEVELOP_CITY_CODE", userdata.getString("DEVELOP_CITY_CODE"));
            result.put("DEVELOP_DEPART_ID", userdata.getString("DEVELOP_DEPART_ID"));
            result.put("DEVELOP_STAFF_ID", userdata.getString("DEVELOP_STAFF_ID"));
            result.put("DEVELOP_DATE", userdata.getString("DEVELOP_DATE"));
            result.put("DEVELOP_NO", userdata.getString("DEVELOP_NO"));
            result.put("IN_DEPART_ID", userdata.getString("IN_DEPART_ID"));
            result.put("IN_STAFF_ID", userdata.getString("IN_STAFF_ID"));
            result.put("REMOVE_EPARCHY_CODE", userdata.getString("REMOVE_EPARCHY_CODE"));
            result.put("REMOVE_CITY_CODE", userdata.getString("REMOVE_CITY_CODE"));
            result.put("REMOVE_DEPART_ID", userdata.getString("REMOVE_DEPART_ID"));
            result.put("REMOVE_REASON_CODE", userdata.getString("REMOVE_REASON_CODE"));
            result.put("REMARK", userdata.getString("REMARK"));

            result.put("LAN_CUST_NAME", custdata.getString("CUST_NAME"));
            result.put("CUST_TYPE", custdata.getString("CUST_TYPE"));
            result.put("CUST_STATE", custdata.getString("CUST_STATE"));
            result.put("OPEN_LIMIT", custdata.getInt("OPEN_LIMIT"));
            result.put("CUST_PASSWD", custdata.getString("CUST_PASSWD"));
            result.put("PSPT_TYPE_CODE", custdata.getString("PSPT_TYPE_CODE"));
            result.put("PSPT_ID", custdata.getString("PSPT_ID"));

            result.put("PSPT_END_DATE", persondata.getString("PSPT_END_DATE"));
            result.put("PSPT_ADDR", persondata.getString("PSPT_ADDR"));
            result.put("SEX", persondata.getString("SEX"));
            result.put("BIRTHDAY", persondata.getString("BIRTHDAY"));
            result.put("NATIONALITY_CODE", persondata.getString("NATIONALITY_CODE"));
            result.put("LOCAL_NATIVE_CODE", persondata.getString("LOCAL_NATIVE_CODE"));
            result.put("POPULATION", persondata.getString("POPULATION"));
            result.put("LANGUAGE_CODE", persondata.getString("LANGUAGE_CODE"));
            result.put("FOLK_CODE", persondata.getString("FOLK_CODE"));
            result.put("PHONE", persondata.getString("PHONE"));
            result.put("POST_CODE", persondata.getString("POST_CODE"));
            result.put("LINK_ADDRESS", persondata.getString("POST_ADDRESS"));
            result.put("FAX_NBR", persondata.getString("FAX_NBR"));
            result.put("E_MAIL", persondata.getString("EMAIL"));
            result.put("LINK_NAME", persondata.getString("CONTACT"));
            result.put("LINK_PHONE_CODE", persondata.getString("CONTACT_PHONE"));
            result.put("HOME_ADDRESS", persondata.getString("HOME_ADDRESS"));
            result.put("WORK_NAME", persondata.getString("WORK_NAME"));
            result.put("WORK_DEPART", persondata.getString("WORK_DEPART"));
            result.put("JOB", persondata.getString("JOB"));
            result.put("JOB_TYPE_CODE", persondata.getString("JOB_TYPE_CODE"));
            result.put("EDUCATE_DEGREE_CODE", persondata.getString("EDUCATE_DEGREE_CODE"));
            result.put("RELIGION_CODE", persondata.getString("RELIGION_CODE"));
            result.put("REVENUE_LEVEL_CODE", persondata.getString("REVENUE_LEVEL_CODE"));
            result.put("MARRIAGE", persondata.getString("MARRIAGE"));
            result.put("CHARACTER_TYPE_CODE", persondata.getString("CHARACTER_TYPE_CODE"));
            result.put("WEBUSER_ID", persondata.getString("WEBUSER_ID"));
            result.put("WEB_PASSWD", persondata.getString("WEB_PASSWD"));
            result.put("CONTACT_TYPE_CODE", persondata.getString("CONTACT_TYPE_CODE"));
            result.put("COMMUNITY_ID", persondata.getString("COMMUNITY_ID"));
            result.put("ACCT_ID", acctdata.getString("ACCT_ID"));
            result.put("PAY_NAME", acctdata.getString("PAY_NAME"));
            result.put("PAY_MODE_CODE", acctdata.getString("PAY_MODE_CODE"));
            result.put("BANK_CODE", acctdata.getString("BANK_CODE"));
            result.put("BANK_ACCT_NO", acctdata.getString("BANK_ACCT_NO"));
            result.put("DEBUTY_USER_ID", acctdata.getString("DEBUTY_USER_ID"));
            result.put("DEBUTY_CODE", acctdata.getString("DEBUTY_CODE"));
            result.put("CONTRACT_NO", acctdata.getString("CONTRACT_NO"));
            result.put("DEPOSIT_PRIOR_RULE_ID", acctdata.getString("DEPOSIT_PRIOR_RULE_ID"));
            result.put("ITEM_PRIOR_RULE_ID", acctdata.getString("ITEM_PRIOR_RULE_ID"));

            result.put("STANDARD_FIX_ADDRESS", widenetdata.getString("STAND_ADDRESS"));
            result.put("DETAIL_FIX_ADDRESS", widenetdata.getString("DETAIL_ADDRESS"));
            result.put("ADDRESS_CODE", widenetdata.getString("RSRV_STR1"));
            result.put("SIGN_PATH", widenetdata.getString("SIGN_PATH"));
            result.put("PORT_TYPE", widenetdata.getString("PORT_TYPE"));

            result.put("WPASSWD0", widenetactinfo.getString("ACCT_PASSWD"));
            result.put("LAN_MAIN_ACCOUNT", widenetactinfo.getString("ACCT_ID"));

            resultInfo.add(result);
            return resultInfo;
        }
        else
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_22);
        }
        return resultInfo;
    }
}
