
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.auth;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.acct.UAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class TradeInfoBean extends CSBizBean
{
    public static final String MOBILE_USER = "00";

    public static final String KD_USER = "04";

    public static final String TD_USER = "18";

    public static final String PWLW_USER = "07";

    /**
     * 获取UserCheck组件用户
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public static IDataset getUserForUserCheck(IData input) throws Exception
    {
        IData userInfo = new DataMap();
        IDataset dataset = new DatasetList();

        setNonMobileUser(input);
        String userId = input.getString("USER_ID", "");
        if (StringUtils.isNotBlank(userId))
        {
            userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        }
        else
        {
            userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        }

        dataset.add(userInfo);
        return dataset;
    }

    public static IData getUserProductInfo(String userId, String removeTag) throws Exception
    {
        IData result = null;

        if (removeTag.equals("0"))
        {
            result = UcaInfoQry.qryMainProdInfoByUserId(userId);
        }
        else
        {
            IDataset pdInfos = UserProductInfoQry.queryMainProduct(userId);
            if (IDataUtil.isEmpty(pdInfos))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_75);
            }
            result = pdInfos.getData(0);
        }

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_75);
        }

        return result;
    }

    /**
     * 判断非手机接入号码是否为绑定的个人手机号码，如果是，需要做一下转换
     * 
     * @param input
     * @throws Exception
     */
    public static void setNonMobileUser(IData input) throws Exception
    {
        String userId = input.getString("USER_ID", "");
        String authType = input.getString("AUTH_TYPE", MOBILE_USER);
        // 如果存在用户ID或非宽带用户则返回按照原来逻辑处理
        if (!StringUtils.equals(userId, "") || StringUtils.equals(authType, MOBILE_USER))
        {
            return;
        }
        String sn = input.getString("SERIAL_NUMBER");
        if (StringUtils.equals(authType, KD_USER))
        { // 宽带用户
            if (!sn.startsWith("KD_"))
            {
                String convertSn = "KD_" + sn;
                IData data = UcaInfoQry.qryUserInfoBySn(convertSn);
                if (IDataUtil.isNotEmpty(data))
                {
                    userId = data.getString("USER_ID");
                    input.put("USER_ID", userId); // 设置入参记录宽带用户USER_ID
                }
                else
                {
                    CSAppException.apperr(WidenetException.CRM_WIDENET_4);
                }
                input.put("SERIAL_NUMBER", convertSn);
            } else if(sn.startsWith("KD_") && sn.length() == 18){ //集团商务宽带
            	
            	IData data = UcaInfoQry.qryUserInfoBySn(sn);
                if (IDataUtil.isNotEmpty(data))
                {
                	if(StringUtils.equals("BNBD", data.getString("RSRV_STR10",""))){
                		 userId = data.getString("USER_ID");
                         input.put("USER_ID", userId); // 设置入参记录宽带用户USER_ID
                	}
                }
                else
                {
                    CSAppException.apperr(WidenetException.CRM_WIDENET_4);
                }
            }
        }
        else if (StringUtils.equals(authType, TD_USER))
        { // 无线固话用户
            if (sn.length() == 8)
            {
                input.put("SERIAL_NUMBER", "898" + sn);
            }
        }
    }

    /**
     * 检查号码是否在号段公共参数里
     * 
     * @param serialNumber
     * @return true:在;false:不在
     * @throws Exception
     */
    public boolean checkSnExistCodeArea(String serialNumber) throws Exception
    {
        boolean flag = false;
        String route = BizRoute.getRouteId();
        IDataset results = ParamInfoQry.getTagInfoBySubSys("PUB_INF_CODEAREA", "PUB", "0", route);
        if (results.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_388, route);

        }
        else
        {
            String codeArea = results.getData(0).getString("TAG_INFO", "");
            if (codeArea.equals(""))
            {
                CSAppException.apperr(ParamException.CRM_PARAM_389, route);
            }
            // 拿手机号码前3位与号段公共参数作比较
            if (codeArea.indexOf(serialNumber.substring(0, 3)) != -1)
            {
                flag = true;
            }
            else
            {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 查询帐户资料
     * 
     * @param userId
     * @param custId
     * @param tradeTypeCode
     * @return
     * @throws Exception
     */
    private IData getAcctInfo(String userId, String custId, String tradeTypeCode) throws Exception
    {
        String acctId = "";

        // 获取默认的付费关系
        IData payrela = UcaInfoQry.qryDefaultPayRelaByUserId(userId);

        if (payrela == null || payrela.isEmpty())
        {
            payrela = UcaInfoQry.qryLastPayRelaByUserId(userId);

            if (IDataUtil.isEmpty(payrela))
            {
                // 复机用户资料可能被移到历史表了，直接查询客户默认账号
                if (StringUtils.equals("310", tradeTypeCode))
                {
                    IData custInfo = UcaInfoQry.qryCustomerInfoByCustId(custId);
                    if (IDataUtil.isNotEmpty(custInfo))
                    {
                        IDataset acctInfoDataset = UAcctInfoQry.qryAcctInfoByCustId(custInfo.getString("CUST_ID"));
                        if (IDataUtil.isNotEmpty(acctInfoDataset))
                        {
                            acctId = acctInfoDataset.getData(0).getString("ACCT_ID");
                        }
                    }
                }
                else
                {
                    CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_106);
                }
            }
            else
            {
                acctId = payrela.getString("ACCT_ID");
            }
        }
        else
        {
            acctId = payrela.getString("ACCT_ID");
        }
        return UcaInfoQry.qryAcctInfoByAcctId(acctId);
    }

    /**
     * 获取认证用户
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getAuthUser(IData input) throws Exception
    {
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");

        String infoTagSet = input.getString("INFO_TAG_SET");
        String extendTag = input.getString("EXTEND_TAG");

        String userCanBeNull = input.getString("USER_CAN_BE_NULL", "");
        // 校验是否异地业务
        if (!(CSBizBean.getTradeEparchyCode().equals(BizRoute.getRouteId())) && "0".equals(extendTag))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_177);
        }
        String authType = input.getString("AUTH_TYPE", MOBILE_USER);
        // 设置宽带用户
        setNonMobileUser(input);

        /**
         * 目前用户认证已经前置，这里只获取用户信息 资料获取标志集：每一位标识是否获取某类资料，非'0'表示获取。 第1位：是否获取用户资料(0-否,1-获取正常用户,2-获取正常或最后一个销户用户,3-获取所有用户)；
         * 第2位：是否获取客户资料； 第3位：是否获取帐户资料； 其它：预留
         */
        IData params = new DataMap();
        params.putAll(input);
        params.put("INFO_TAG_SET", infoTagSet);
        IData userInfo = getTradeUser(params);
        if (IDataUtil.isEmpty(userInfo))
        {
            // 如果没有用户资料 不抛出异常 IP直通车等业务 没有用户资料也要继续
            if ("true".equals(userCanBeNull))
                return userInfo;

            // 业务中断
            IDataset destroyUsers = UserInfoQry.getAllDestroyUserInfoBySn(input.getString("SERIAL_NUMBER"));
            if (destroyUsers == null || destroyUsers.size() == 0)
            {
                CSAppException.apperr(CrmUserException.CRM_USER_273);
            }
            else if (tradeTypeCode.equals("310"))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_921);

            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_323);
            }
        }
        /*
         * if(!StringUtils.equals(authType, KD_USER) &&
         * !StringUtils.equals(PWLW_USER,userInfo.getString("NET_TYPE_CODE")) && !StringUtils.equals(authType,
         * userInfo.getString("NET_TYPE_CODE"))){ CSAppException.apperr(CrmUserException.CRM_USER_1142); }
         */
        return userInfo;
    }

    /**
     * 获取业务类型数据
     * 
     * @param inputData
     * @return
     * @throws Exception
     */
    public IData getTradeType(IData inputData) throws Exception
    {
        String eparchCode;
        String tradeTypeCode = inputData.getString("TRADE_TYPE_CODE");
        if ("1".equals(CSBizBean.getVisit().getInModeCode()))
        {
            eparchCode = BizRoute.getRouteId();
        }
        else
        {
            eparchCode = CSBizBean.getTradeEparchyCode();
        }

        IData tradeType = UTradeTypeInfoQry.getTradeType(tradeTypeCode, eparchCode);

        if (IDataUtil.isEmpty(tradeType))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_200, tradeTypeCode);
        }

        return tradeType;

    }

    /**
     * 获取业务受理三户资料
     * 
     * @param inputData
     * @return 返回用户，客户，帐户，VIP客户，用户帐期,用户开户标记信息
     * @throws Exception
     */
    public IData getTradeUCAInfo(IData inputData) throws Exception
    {
        IData infoData = new DataMap();
        IData userInfo = new DataMap(); // 存放用户信息
        IData custInfo = new DataMap(); // 存放客户信息
        IData acctInfo = new DataMap(); // 存放帐户信息
        IData acctDayInfo = new DataMap(); // 存放用户帐期信息
        IData vipInfo = new DataMap(); // 存放VIP客户信息

        String openUserTag = "0", userId = "", custId = "";

        IData tradePara = getTradeType(inputData);
        String tradeTypeCode = inputData.getString("TRADE_TYPE_CODE");
        String infoTagSet = tradePara.getString("INFO_TAG_SET", "");
        IData params = new DataMap();
        params.putAll(inputData);

        params.put("INFO_TAG_SET", infoTagSet);
        userId = inputData.getString("USER_ID", "");
        // 如果已经校验过用户，且存在校验用户ID，则直接根据用户ID获取用户，否则根据业务类型走
        if (!"".equals(userId))
        {
            userInfo = getUserInfoByUserId(userId);
        }
        else
        {
            // 如果USER_ID丢失则重新按照业务用户方式获取
            userInfo = getAuthUser(params);
        }

        if (null == userInfo || userInfo.size() == 0)
        {
            if (StringUtils.equals("310", tradeTypeCode) || StringUtils.equals("3813", tradeTypeCode))
            {
                userInfo = UserInfoQry.qryUserInfoByUserIdFromHis(userId);
                IData hisProductData = UserProductInfoQry.qryLasterMainProdInfoByUserIdFromHis(userId);
                if (IDataUtil.isNotEmpty(hisProductData))
                {
                    userInfo.put("BRAND_CODE", hisProductData.getString("BRAND_CODE"));
                    userInfo.put("PRODUCT_ID", hisProductData.getString("PRODUCT_ID"));
                }
            }
            else
            {
                return infoData;
            }
        }

        custId = userInfo.getString("CUST_ID");
        userId = userInfo.getString("USER_ID");
        /**
         * 兼容没有品牌编码和产品编码处理
         */
        if (!userInfo.containsKey("BRAND_CODE") || !userInfo.containsKey("PRODUCT_ID"))
        {
            IData productInfo = getUserProductInfo(userId, userInfo.getString("REMOVE_TAG", "0"));
            if (null != productInfo && productInfo.size() > 0)
            {
                userInfo.put("BRAND_CODE", productInfo.getString("BRAND_CODE"));
                userInfo.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
            }
        }

        // 设置用户归属地州
        userInfo.put("EPARCHY_NAME", UAreaInfoQry.getAreaNameByAreaCode(userInfo.getString("EPARCHY_CODE")));

        // 获取客户资料
        if (infoTagSet.charAt(1) == '1')
        {
            IData custData = UcaInfoQry.qryCustomerInfoByCustId(custId);
            if (IDataUtil.isEmpty(custData))
            {
                // 业务中断
                CSAppException.apperr(CustException.CRM_CUST_201);
            }
            else
            {
                IData custPersonInfo = UcaInfoQry.qryPerInfoByCustId(custId);
                if (IDataUtil.isNotEmpty(custPersonInfo))
                {
                    custInfo.putAll(custPersonInfo);
                }else {
					// 如果是商务宽带用户，不获取个人客户资料
                	if (StringUtils.equals("BNBD", userInfo.getString("BRAND_CODE"))){
                    	
                    }else if(StringUtils.equals("BNBD", userInfo.getString("RSRV_STR10",""))){
                    	
                    } else 
                    // 业务中断
                    CSAppException.apperr(CustException.CRM_CUST_201);
                }
                custInfo.putAll(custData);
            }
            setNotMaskField(custInfo, new String[]
            { "PSPT_ID", "CUST_NAME", "PSPT_ADDR", "HOME_ADDRESS", "WORK_ADDRESS"});
        }

        // 获取账户资料
        if (infoTagSet.charAt(2) == '1')
        {
            acctInfo = getAcctInfo(userId, custId, tradeTypeCode);

            if (acctInfo == null || acctInfo.size() <= 0)
            {
                CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_20);
            }
            setNotMaskField(acctInfo, new String[]
            { "PAY_NAME" });
        }

        // 获取用户的结帐日和首次结帐日
        IDataset userAcctDays = UserAcctDayInfoQry.getUserAcctDay(userId);
        if (userAcctDays != null && userAcctDays.size() > 0)
        {
            acctDayInfo.put("ACCT_DAY", userAcctDays.getData(0).getString("ACCT_DAY"));
            acctDayInfo.put("FIRST_DATE", userAcctDays.getData(0).getString("FIRST_DATE"));
            if (userAcctDays.size() > 1)
            {
                acctDayInfo.put("NEXT_ACCT_DAY", userAcctDays.getData(1).getString("ACCT_DAY"));
                acctDayInfo.put("NEXT_FIRST_DATE", userAcctDays.getData(1).getString("FIRST_DATE"));
            }
        }
        else
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_137);
        }

        if (!"3700".equals(tradeTypeCode))
        {
            /**
             * 检查用户是否当天开户、复机
             */
            if (infoTagSet.charAt(4) == '1')
            {
                openUserTag = getUserOpenFinish(userId);
            }
            vipInfo = getVipInfo(userId);

        }
        userInfo.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(userInfo.getString("PRODUCT_ID")));
        userInfo.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(userInfo.getString("BRAND_CODE"))); 
        infoData.put("USER_INFO", userInfo);
        infoData.put("CUST_INFO", custInfo);
        infoData.put("ACCT_INFO", acctInfo);
        infoData.put("ACCTDAY_INFO", acctDayInfo);
        infoData.put("VIP_INFO", vipInfo);
        infoData.put("OPEN_USER_TAG", openUserTag);

        return infoData;

    }

    /**
     * 获取业务受理用户资料
     * 
     * @param inputData
     * @return
     * @throws Exception
     */
    public IData getTradeUser(IData inputData) throws Exception
    {

        IData userInfo = new DataMap();

        IData param = new DataMap();

        String tradeTypeCode = inputData.getString("TRADE_TYPE_CODE");
        String infoTagSet = inputData.getString("INFO_TAG_SET", "");
        String userId = inputData.getString("USER_ID", "");

        /*REQ201707280024关于支持物联网号码和销号号码设置并打印电子发票的优化需求--业支找茬问题
        由于还有einvoicehistory.EInvoiceHistory页面也是使用的TRADE_TYPE_CODE=2016的配置，为了不影响原逻辑，这里特殊处理下INFO_TAG_SET字段值*/
        if ("2016".equals(tradeTypeCode) && !"changeuserinfo.ModifyEPostInfo".equals(inputData.getString("page"))) {
            infoTagSet = "110       ";
        }

        param.put("INFO_TAG_SET", infoTagSet);
        param.put("NET_TYPE_CODE", inputData.getString("NET_TYPE_CODE", MOBILE_USER));
        param.put("EPARCHY_CODE", inputData.getString("EPARCHY_CODE", CSBizBean.getUserEparchyCode()));
        param.put("SERIAL_NUMBER", inputData.getString("SERIAL_NUMBER"));
        param.put("TRADE_TYPE_CODE", tradeTypeCode);

        /**
         * 针对多用户，前台已经选定了用户情况 由于历史遗留问题，INFO_TAG_SET配置不正确 需要保证业务认证通过，比如复机业务
         */
        if ((infoTagSet.charAt(0) == '1' || infoTagSet.charAt(0) == '2') && !"".equals(userId))
        {
            userInfo = getUserInfoByUserId(userId);
        }
        /**
         * 是否获取用户资料(0-否,1-获取正常用户,2-获取正常或最后一个销户用户,3-获取所有用户)
         */
        else if (infoTagSet.charAt(0) == '1' || infoTagSet.charAt(0) == '2')
        {
            userInfo = getUserInfo(param);
        }
        /**
         * 获取选中处理的用户资料 目前押金业务有这个配置
         */
        else if (infoTagSet.charAt(0) == '3')
        {
            userInfo = getUserInfoByUserId(userId);
        }
        /**
         * 获取选中处理的用户资料 优先使用user_id 否则使用serial_number
         */
        else if (infoTagSet.charAt(0) == '4')
        {
            userInfo = getUserInfoByUserId(userId);
            if (null == userInfo || userInfo.isEmpty())
            {
                if (StringUtils.equals("310", tradeTypeCode)) // 复机查一下历史用户表
                {
                    userInfo = UserInfoQry.qryUserInfoByUserIdFromHis(userId);
                }
                else
                {
                    userInfo = getUserInfo(param);
                }
            }
        }
        /**
         * 获取正常或最后一个非正常状态用户
         */
        else if (infoTagSet.charAt(0) == '5')
        {
            userInfo = getUserInfo(param);
        }
        else
        {
            // 业务中断
            CSAppException.apperr(CrmCommException.CRM_COMM_693, tradeTypeCode);
        }

        return userInfo;
    }

    /**
     * 查询用户资料
     * 
     * @param iparam
     * @return
     * @throws Exception
     */
    private IData getUserInfo(IData iparam) throws Exception
    {
        IData userInfo = new DataMap();
        int userInfoTag = 0;
        String infoTagSet = iparam.getString("INFO_TAG_SET");
        String tradeTypeCode = iparam.getString("TRADE_TYPE_CODE");
        String userId = iparam.getString("USER_ID");
        if ("310".equals(tradeTypeCode) || "3813".equals(tradeTypeCode))
        {
            userInfo = this.getUserInfoByUserId(userId);
            if (IDataUtil.isEmpty(userInfo))
            {
                userInfo = UserInfoQry.qryUserInfoByUserIdFromHis(iparam.getString("USER_ID"));
            }
        }
        else
        {
            userInfo.clear();
            userInfo = UcaInfoQry.qryUserInfoBySn(iparam.getString("SERIAL_NUMBER"));

            if (IDataUtil.isEmpty(userInfo))
            {
                userInfoTag = 1;
            }
        }

        // 获取正常用户无资料时，再获取最后销户用户
        if ((infoTagSet.charAt(0) == '2') && (userInfoTag == 1))
        {
            IDataset lastDestroyUserInfo = UserInfoQry.getDestroyUserInfoBySn(iparam.getString("SERIAL_NUMBER"));

            if (lastDestroyUserInfo != null && lastDestroyUserInfo.size() > 0)
            {
                userInfo = (IData) lastDestroyUserInfo.get(0);
            }
        }

        // 获取正常用户无资料时，再获取非正常状态用户(REMOVE_TAG!='0')
        if ((infoTagSet.charAt(0) == '5') && (userInfoTag == 1)) {
            IDataset unnormalUserInfo = UserInfoQry.getUnnormalUserInfoBySn(iparam.getString("SERIAL_NUMBER"));
            if (IDataUtil.isNotEmpty(unnormalUserInfo)) {
                userInfo = unnormalUserInfo.first();
            }
        }

        return userInfo;
    }

    /**
     * 根据用户ID查询用户资料
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    private IData getUserInfoByUserId(String userId) throws Exception
    {
        IData result = UcaInfoQry.qryUserInfoByUserId(userId);
        /* 对于查询用户为空由上层决定是否抛异常,此处不抛,因为有些逻辑是先根据userid来查找用户,查找不存在的话,再根据serial_number查找 modify by dengyong3 */
        /*
         * if (null == result || result.isEmpty()) { CSAppException.apperr(CrmUserException.CRM_USER_189, userId); }
         */
        return result;
    }

    /**
     * 获取用户当天是否开户，复机标识
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    protected String getUserOpenFinish(String userId) throws Exception
    {
        IData param = new DataMap();

        IDataset results = new DatasetList();

        String openUserTag = "0";

        param.put("USER_ID", userId);
        param.put("TYPE", "1");
        param.put("NUM", "0");

        param.put("TRADE_TYPE_CODE", "10");

        results = TradeInfoQry.getHisMainTrade(param, null);
        if (results != null && results.size() > 0)
        {
            IData result = new DataMap();
            result = (IData) results.get(0);
            if (result != null && result.size() > 0)
            {
                int count = result.getInt("RECORDCOUNT");

                if (count > 0)
                {
                    return "1";
                }
            }
        }

        results.clear();
        param.put("TRADE_TYPE_CODE", "11");
        results = TradeInfoQry.getHisMainTrade(param, null);

        if (results != null && results.size() > 0)
        {
            IData result = new DataMap();
            result = (IData) results.get(0);
            if (result != null && result.size() > 0)
            {
                int count = result.getInt("RECORDCOUNT");

                if (count > 0)
                {
                    return "2";
                }
            }
        }

        results.clear();
        param.put("TRADE_TYPE_CODE", "310");
        results = TradeInfoQry.getHisMainTrade(param, null);

        if (results != null && results.size() > 0)
        {
            IData result = new DataMap();
            result = (IData) results.get(0);
            if (result != null && result.size() > 0)
            {
                int count = result.getInt("RECORDCOUNT");

                if (count > 0)
                {
                    return "3";
                }
            }
        }

        results.clear();
        param.put("TRADE_TYPE_CODE", "7302");
        results = TradeInfoQry.getHisMainTrade(param, null);
        if (results != null && results.size() > 0)
        {
            IData result = new DataMap();
            result = (IData) results.get(0);
            if (result != null && result.size() > 0)
            {
                int count = result.getInt("RECORDCOUNT");

                if (count > 0)
                {
                    return "4";
                }
            }
        }
        return openUserTag;
    }

    /**
     * 查询VIP客户资料
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    private IData getVipInfo(String userId) throws Exception
    {
        IData result = new DataMap();

        IDataset results = CustVipInfoQry.qryVipInfoByUserId(userId);
        if (results != null && results.size() > 0)
        {
            result = (IData) results.get(0);
        }
        return result;
    }

    /**
     * 设置不模糊化字段
     * 
     * @param data
     * @param keys
     * @throws Exception
     */
    private void setNotMaskField(IData data, String[] keys) throws Exception
    {
        if (IDataUtil.isEmpty(data) || null == keys || keys.length < 1)
        {
            return;
        }
        for (String key : keys)
        {
            data.put("ORIGIN_" + key, data.getString(key, ""));
        }
    }

}
