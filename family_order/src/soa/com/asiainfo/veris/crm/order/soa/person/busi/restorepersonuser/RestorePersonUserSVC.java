
package com.asiainfo.veris.crm.order.soa.person.busi.restorepersonuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherservQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage O
 * 
 * @ClassName: RestoreUserSVC.java
 * @Description: 复机服务类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-22 上午10:34:17
 */
public class RestorePersonUserSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;
    private static final Logger log = Logger.getLogger(RestorePersonUserSVC.class);

    /**
     * 校验手机号码资源并且配置号费，并且保存手机号码信息到td
     */
    public IData checkPhoneResource(IData input) throws Exception
    {
        IData returnData = new DataMap();
        returnData.put("PHONE_CHECK_TAG", "0");// 设置手机号码校验不通过

        String oldPhoneNo = input.getString("OLD_PHONE_NO", ""); // 记录用户上一次新换的号码
        String oldSimCardNo = input.getString("OLD_SIM_CARD_NO", "");// 记录用户上一次新换的sim卡
        String newPhoneNo = input.getString("NEW_PHONE_NO", ""); // 新手机号码
        String newSimCardNo = input.getString("NEW_SIM_CARD_NO", "");// 新sim卡
        IData newPhoneUserData = UcaInfoQry.qryUserInfoBySn(newPhoneNo);
        if (IDataUtil.isNotEmpty(newPhoneUserData))
        {
            returnData.put("CHECK_MSG", "该号码存在在网用户，请重新输入新号码校验！");
            return returnData;
        }
        //log.info("("**********cxy***********oldPhoneNo="+oldPhoneNo+"*********newPhoneNo="+newPhoneNo);
        /**
         * REQ201608230012 关于2016年下半年吉祥号码优化需求（三）
         * chenxy3 20161009
         * */
        if(!"".equals(newPhoneNo)){
        	//吉祥号码必须配置绑定优惠
            IDataset dataSet = ResCall.getMphonecodeInfo(newPhoneNo);
    		if (IDataUtil.isNotEmpty(dataSet))
    		{
    			String beautifulTag = dataSet.first().getString("BEAUTIFUAL_TAG");
    			if (StringUtils.equals("1", beautifulTag)){
		        	IData checkPara=new DataMap();
		        	checkPara.put("SERIAL_NUMBER", newPhoneNo);
		        	IDataset checkResult=CreatePersonUserBean.checkIfNoOverOneYear(checkPara);
		        	if(IDataUtil.isNotEmpty(checkResult)){
		        		String flag=checkResult.getData(0).getString("RECORDCOUNT");
		        		if("1".equals(flag)){
		        			returnData.put("CHECK_MSG", "该号码【"+newPhoneNo+"】销户超过一年，不允许复机，请重新输入新号码校验！");
		        			returnData.put("NEED_CHANGE_PHONE", "true");
		                    return returnData;
		        		}
		        	}
    			}
    		}
        }
        // 新号码的city_code必须与原用户city_code相同
        IData userInfoData = new DataMap(input.getString("USER_INFO"));
        if (IDataUtil.isEmpty(userInfoData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userCityCode = userInfoData.getString("CITY_CODE", "");
        IDataset newPhoneDataset = ResCall.getMphonecodeInfo(newPhoneNo);
        if (IDataUtil.isEmpty(newPhoneDataset))
        {
            returnData.put("CHECK_MSG", "该号码信息不存在，请重新输入新号码校验！");
            return returnData;
        }
        else
        {
            String phoneCityCode = newPhoneDataset.getData(0).getString("CITY_CODE");
            if (!StringUtils.equalsIgnoreCase(userCityCode, phoneCityCode))
            {
                StringBuilder msg = new StringBuilder(100);
                String userCityName = UAreaInfoQry.getAreaNameByAreaCode(userCityCode);
                String phoneCityName = UAreaInfoQry.getAreaNameByAreaCode(phoneCityCode);
                msg.append("该号码").append(newPhoneNo).append("归属于【").append(phoneCityName).append("】，");
                msg.append("不符合业务要求，请输入归属于【").append(userCityName).append("】的新号码校验！");
                returnData.put("CHECK_MSG", msg.toString());
                return returnData;
            }
        }
        // checkResourceForIOTMphone
        IDataset checkPhoneDataset = ResCall.checkResourceForMphone("0", newPhoneNo, "0");
        if (IDataUtil.isEmpty(checkPhoneDataset))
        {
            return returnData;
        }
        IData checkPhoneData = checkPhoneDataset.getData(0);
        // SIM卡号
        String strSimCardNo = checkPhoneData.getString("SIM_CARD_NO", "");
        // 预配
        String strPreCodeTag = checkPhoneData.getString("PRECODE_TAG", "0");
        // 获取号费
        String strModeCode = checkPhoneData.getString("RSRV_DATE1", "");// 费号类型
        String strItemCodeMp = checkPhoneData.getString("RSRV_DATE2", "");// 费号子类型
        String strCodeFee = checkPhoneData.getString("RSRV_DATE3", "");// 费号金额

        if (StringUtils.isNotEmpty(strSimCardNo) && StringUtils.equals("1", strPreCodeTag))
        {
            returnData.put("CHECK_MSG", "该号码属于预配号码，不能做为复机号码！");
            return returnData;
        }

        // 根据号段或者号码类型获取默认产品
        String defaultProduct = checkPhoneData.getString("PRODUCT_ID");
        if (StringUtils.isNotEmpty(defaultProduct))
        {
            returnData.put("EXISTS_SINGLE_PRODUCT", defaultProduct);// 强制使用该产品，在产品信息页面检查是否存在绑定产品
        }

        // 选号费
        String phoneFee = checkPhoneData.getString("RESERVE_FEE", "0");
        returnData.put("FEE_CODE_FEE", phoneFee); // 选号费 作为营业费 需确认
        returnData.put("PHONE_CHECK_TAG", "1");// 设置手机号码校验通过

        return returnData;
    }

    /**
     * 校验用户复机新资源
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkRestoreNewRes(IData input) throws Exception
    {
        IData returnData = new DataMap();
        String resTypeCode = input.getString("RES_TYPE_CODE");
        String oldPhoneNo = input.getString("OLD_PHONE_NO", ""); // 记录用户上一次新换的号码
        String oldSimCardNo = input.getString("OLD_SIM_CARD_NO", "");// 记录用户上一次新换的sim卡
        String newPhoneNo = input.getString("NEW_PHONE_NO", ""); // 新手机号码
        String newSimCardNo = input.getString("NEW_SIM_CARD_NO", "");// 新sim卡
        if (StringUtils.equals("1", resTypeCode))// sim卡校验
        {
            // 选择新sim卡
            returnData = this.checkSimResource(input);
        }
        else if (StringUtils.equals("0", resTypeCode)) // 新的手机号码校验
        {
            returnData = this.checkPhoneResource(input);
        }

        returnData.put("START_DATE", SysDateMgr.getSysDate());
        returnData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);

        return IDataUtil.idToIds(returnData);
    }

    /**
     * 复机业务前校验原号码和sim卡是否可用
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    private void checkRestoreOldRes(IData userInfo, IData result) throws Exception
    {
        String serialNumber = userInfo.getString("SERIAL_NUMBER");
        IDataset resDataset = ResCall.restoreCheckMPhone(serialNumber);
        if (IDataUtil.isEmpty(resDataset))
        {
            CSAppException.apperr(TradeException.CRM_TRADE_95, "ResCall.restoreCheckMPhone接口返回空！");
        }
        IData data = resDataset.getData(0);
        result.put("RESET_REASON", "");
        result.put("NEED_REPOSSESS", ""); // 原号码是否需要重新占用，可能已经被回收，此时需要重新占用
        result.put("NEED_CHANGE_NUMBER", false);
        result.put("NEED_CHANGE_SIM", false); 
        if (StringUtils.equals("1",data.getString("KI_STATE"))
                || StringUtils.equals("7",data.getString("KI_STATE")))
        {
            result.put("NEED_CHANGE_NUMBER", "MUST_MAITAIN");
            result.put("NEED_CHANGE_SIM", false);
            result.put("RESET_REASON", "老号码可用，老卡可用！");
        }
        else if (StringUtils.equals("2",data.getString("KI_STATE")))
        {
            result.put("NEED_CHANGE_NUMBER", true);
            result.put("NEED_CHANGE_SIM", true);
            result.put("RESET_REASON", "用户服务号码已经被回收，请换新号码和新卡复机！");
        }
        else if (StringUtils.equals("3",data.getString("KI_STATE")))
        {
            result.put("RESET_REASON", "该号码为正常已用号码，不能复机！");
            result.put("DISABLE_RESTORE", "1");
        }
        else if (StringUtils.equals("5",data.getString("KI_STATE")))
        {
            result.put("NEED_CHANGE_NUMBER", false);
            result.put("NEED_CHANGE_SIM", true);
            result.put("NEED_REPOSSESS", "1");
            result.put("RESET_REASON", "该号码为空闲号，请换卡并重新校验服务号码！");
        }
        else if (StringUtils.equals("6",data.getString("KI_STATE")))
        {
            result.put("NEED_CHANGE_NUMBER", true);
            result.put("NEED_CHANGE_SIM", true);
            result.put("RESET_REASON", "该号码状态未使用，请换新号码和新卡复机！");
        }
        else if ("8".equalsIgnoreCase(data.getString("KI_STATE")))
        {
            result.put("NEED_CHANGE_NUMBER", "MUST_MAITAIN");
            result.put("NEED_CHANGE_SIM", true);
            result.put("RESET_REASON", "可以使用原号码复机，但老卡已被交换机清除，请换新卡！");
        }
        else
        {
            result.put("RESET_REASON", "资源校验返回值错误，请联系管理员！");
            result.put("DISABLE_RESTORE", "1");
        }
        
        /**
         * REQ201608230012 关于2016年下半年吉祥号码优化需求（三）
         * chenxy3 20161009
         * */
      //吉祥号码必须配置绑定优惠
        IDataset dataSet = ResCall.getMphonecodeInfo(serialNumber);
		if (IDataUtil.isNotEmpty(dataSet))
		{
			String beautifulTag = dataSet.first().getString("BEAUTIFUAL_TAG");
			if (StringUtils.equals("1", beautifulTag)){
		        IData checkPara=new DataMap();
		        checkPara.put("SERIAL_NUMBER", serialNumber);
		        IDataset checkResult=CreatePersonUserBean.checkIfNoOverOneYear(checkPara);
		        if(IDataUtil.isNotEmpty(checkResult)){
		        	String flag=checkResult.getData(0).getString("RECORDCOUNT");
		        	if("1".equals(flag)){
		        		result.put("NEED_CHANGE_NUMBER", true);
		                result.put("NEED_CHANGE_SIM", false); 
		                result.put("RESET_REASON", "该号码【"+serialNumber+"】销户超过一年，不允许复机！请更换号码进行办理。");
		        	}
		        }
			}
		}        
    }

    private IData checkSimResource(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        returnInfo.put("SIM_CHECK_TAG", "0");

        String orgSimCardNo = input.getString("ORG_SIM_CARD_NO", "");// 记录用户原始sim卡
        String oldSimCardNo = input.getString("OLD_SIM_CARD_NO", "");// 记录用户上一次新换的sim卡
        String newPhoneNo = input.getString("NEW_PHONE_NO", ""); // 新手机号码
        String newSimCardNo = input.getString("NEW_SIM_CARD_NO", "");// 新sim卡
        String writeTag = input.getString("WRITE_TAG"); //写卡标记
        IData userInfoData = new DataMap(input.getString("USER_INFO"));
        if (IDataUtil.isEmpty(userInfoData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String usernetTypeCode = userInfoData.getString("NET_TYPE_CODE");
        IDataset newSimCardInfoDataset = ResCall.checkResourceForSim(newSimCardNo, newPhoneNo, "0", "", "",writeTag, "1", "0", "", usernetTypeCode);
        if (IDataUtil.isNotEmpty(newSimCardInfoDataset))
        {
            IData newSimCardInfoData = newSimCardInfoDataset.getData(0);
            // -------2010-12-20---HNYD-REQ-20101201-005--begin-------
            /*boolean is4GUser = PersonUtil.isLteCardUser(userInfoData.getString("USER_ID"));
            IDataset newSimCardNetTypeDataset = ResCall.qrySimCardTypeByTypeCode(
                    newSimCardInfoData.getString("RES_TYPE_CODE", ""));
            // 添加4G卡校验
            if (IDataUtil.isNotEmpty(newSimCardNetTypeDataset))
            {
                // 01为4g卡
                String newSimCardNetType = newSimCardNetTypeDataset.getData(0).getString("NET_TYPE_CODE");
                if (!is4GUser && StringUtils.equals("01", newSimCardNetType))
                {
                    returnInfo.put("TIPS_INFO", CrmUserException.CRM_USER_1083);
                    return returnInfo;
                }
                
                else if (is4GUser && !StringUtils.equals("01", newSimCardNetType))
                {
                    returnInfo.put("TIPS_INFO", CrmUserException.CRM_USER_1084);
                    return returnInfo;
                }
                
            }*/
            returnInfo.putAll(newSimCardInfoData);
            returnInfo.put("SIM_FEE_TAG", newSimCardInfoData.getString("FEE_TAG", "")); //是否买断卡标记
            returnInfo.put("SIM_CARD_SALE_MONEY", newSimCardInfoData.getString("SALE_MONEY", "0")); //买断卡费用
            String emptyCardId = newSimCardInfoData.getString("EMPTY_CARD_ID", "");
            if (StringUtils.isNotEmpty(emptyCardId)) //空卡
            {
                IDataset emptyCardDataset = ResCall.getEmptycardInfo(emptyCardId, "", "");
                if (IDataUtil.isNotEmpty(emptyCardDataset))
                {
                    String rsrvTag = emptyCardDataset.getData(0).getString("RSRV_TAG1");
                    if (StringUtils.equals("3",rsrvTag)){
                        returnInfo.put("SIM_FEE_TAG", "1");
                        returnInfo.put("SIM_CARD_SALE_MONEY", emptyCardDataset.getData(0).getString("RSRV_NUM1"));
                    }
                }
            }

            // 查询是否要卡费
            SimCardBean cBean = BeanManager.createBean(SimCardBean.class);
            String productId = userInfoData.getString("PRODUCT_ID");
            IData pData = cBean.getSimCardPrice(orgSimCardNo, newSimCardNo, newPhoneNo,"310",userInfoData,productId);
            returnInfo.put("FEE_DATA", pData);
            returnInfo.put("SIM_CHECK_TAG", "1");
        }

        return returnInfo;
    }

    // 过滤产品类型，复机只能恢复为原有产品类型
    private IDataset filterProductType(String productId, IData userInfo) throws Exception
    {
    	
    	IDataset dataset = null;
    	IDataset returnData = new DatasetList();
    	String netTypeCode = userInfo.getString("NET_TYPE_CODE", "00");
        if (StringUtils.equals("18", netTypeCode))
        {
        	dataset = UProductInfoQry.getProductsType("5000", null);// 无线固话产品类型;
        	IDataset productTypes = ProductTypeInfoQry.getProductTypeByProductId(productId);
        	if (!productTypes.isEmpty())
            {
                for (int i = 0; i < dataset.size(); i++)
                {
                    if (productTypes.getData(0).getString("PRODUCT_TYPE_CODE", "").equals(dataset.getData(i).getString("PRODUCT_TYPE_CODE", "")))
                    {
                        IData data = dataset.getData(i);
                        returnData.add(data);
                        return returnData;
                    }
                }
            }

        }
        else
        {
        	dataset = ProductTypeInfoQry.getProductsTypeByParentTypeCode("0000");// 个人产品类型
            //只要神州行，全球通，动感地带
            if (IDataUtil.isNotEmpty(dataset))
            {
                for (int i = 0, size = dataset.size(); i < size; i++)
                {
                    IData catalog = dataset.getData(i);
                    if(IDataUtil.isNotEmpty(catalog)){
                    	if("0300".equals(catalog.getString("PRODUCT_TYPE_CODE")) || "0100".equals(catalog.getString("PRODUCT_TYPE_CODE")) || "0400".equals(catalog.getString("PRODUCT_TYPE_CODE"))){
                        	returnData.add(catalog);
                        }
                    }
                    
                }
            }
        }
        return returnData;
    }
    

    /**
     * @methodName: getRestoreUserDiscntInfo
     * @Description: 查询复机用户当前生效的优惠信息
     * @param userId
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-9-9 上午11:31:30
     */
    private IDataset getRestoreUserDiscntInfo(String userId) throws Exception
    {
        IDataset returnDataset = new DatasetList();
        returnDataset = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);

        if (IDataUtil.isNotEmpty(returnDataset))
        {
            for (Object data : returnDataset)
            {
                IData tempDiscntData = (IData) data;
                String discntCode = tempDiscntData.getString("DISCNT_CODE");
                IData discntData = DiscntInfoQry.getDiscntInfoByCode2(discntCode);
                if (IDataUtil.isNotEmpty(discntData)) // 手机号吗
                {
                    tempDiscntData.put("DISCNT_NAME", discntData.getString("DISCNT_NAME", ""));
                }
            }
        }
        return returnDataset;
    }

    public IData getRestoreUserInfo(IData inputData) throws Exception
    {
        String userId = inputData.getString("USER_ID", "0");
        IData returnData = new DataMap();
        IData resData = getRestoreUserResInfo(userId);
        returnData.put("RES_INFO", resData);

        IDataset userDiscntDataset = getRestoreUserDiscntInfo(userId);
        returnData.put("DISCNT_INFO", userDiscntDataset);

        String productId = inputData.getString("PRODUCT_ID", "0");
        IDataset productTypeList = this.filterProductType(productId, inputData);
        returnData.put("PRODUCT_TYPE_LIST", productTypeList);

        IData editResInfoData = this.prepareForRes(inputData);
        editResInfoData.put("PRODUCT_ID", productId);
        editResInfoData.put("BRAND_CODE", inputData.getString("BRAND_CODE", ""));
        IDataset productDataset = ProductTypeInfoQry.getProductTypeByProductId(productId);
        if (IDataUtil.isEmpty(productDataset))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_504, productId);
        }
        editResInfoData.put("PRODUCT_TYPE_CODE", productDataset.getData(0).getString("PRODUCT_TYPE_CODE", ""));
        editResInfoData.put("OLD_PHONE_NO", resData.getString("OLD_PHONE_NO", ""));
        editResInfoData.put("OLD_SIM_CARD_NO", resData.getString("OLD_SIM_CARD_NO", ""));

        returnData.put("EDIT_INFO", editResInfoData);
        return returnData;
    }

    private IData getRestoreUserResInfo(String userId) throws Exception
    {
        IDataset dataset = UserResInfoQry.getUserResMaxDateByUserId(userId);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_235);
        }
        IData returnData = new DataMap();
        IDataset resset = new DatasetList();
        // 处理逻辑：用户最后的关系截止时间去获取关系记录
        // 通过这条记录的user_id_a,short_code和资源的user_id_a,res_code比较，不相等则去掉
        // 由于没有改变的资源类型也要拼串，于是添加一个标记
        for (Object data : dataset)
        {
            IData resData = (IData) data;
            String resTypeCode = resData.getString("RES_TYPE_CODE", "");
            if (StringUtils.equals("0", resTypeCode)) // 手机号吗
            {
                returnData.put("OLD_PHONE_NO", resData.getString("RES_CODE", ""));
                resset.add(resData);
            }
            else if (StringUtils.equals("1", resTypeCode)) // sim卡
            {
                returnData.put("OLD_SIM_CARD_NO", resData.getString("RES_CODE", ""));
                resset.add(resData);
            }
        }
        returnData.put("RES_INFOS", resset);
        return returnData;
    }

    /**
     * 获取sim卡卡费
     * 
     * @throws Exception
     */
    private IData getSimCardFee(IData simCardInfo, IData pageData) throws Exception
    {
        IData returnData = new DataMap();
        String userId = pageData.getString("USER_ID", "0");
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if (IDataUtil.isEmpty(userInfo))
        {
            return returnData;
        }
        int vipFreeChgCard = 0;
        boolean freeChgCard = false;
        // 获取大客户免费换卡次数
        IDataset tagInfoDataset = TagInfoQry.getTagInfosByTagCode(CSBizBean.getTradeEparchyCode(), "CS_NUM_VIPFREECHGCARDS", "CSM", "0");
        if (IDataUtil.isNotEmpty(tagInfoDataset))
        {
            vipFreeChgCard = Integer.parseInt(tagInfoDataset.getData(0).getString("TAG_NUMBER"));
        }
        else
        {
            CSAppException.apperr(CustException.CRM_CUST_1004);
        }

        // 获取全球通商旅、上网套餐388/588/888每年免费换卡次数
        int productFreeChgCard = 0;
        tagInfoDataset.clear();
        String productId = pageData.getString("PRODUCT_ID", "-1");
        tagInfoDataset = TagInfoQry.getTagInfosByTagCode(CSBizBean.getTradeEparchyCode(), "CS_NUM_PRODUCTFREECHGCARDS", "CSM", "0");
        if (IDataUtil.isNotEmpty(tagInfoDataset))
        {
            String strTagInfo = tagInfoDataset.getData(0).getString("TAG_INFO");
            String[] p = strTagInfo.split(",");
            for (int i = 0; i < p.length; i = i + 2)
            {
                if (productId.equals(p[i]))
                {
                    productFreeChgCard = Integer.parseInt(p[i + 1]);
                }
            }
        }

        // 获取大客户是否需要缴费：大客户免费换卡次数 减掉 本年换卡次数 大于 零 免费；否则交费
        IData vipInfoData = CustVipInfoQry.qryIsCanRecoverVip(userId);
        String userVipTypeCode = vipInfoData.getString("VIP_TYPE_CODE", "");
        String userVipClassId = vipInfoData.getString("VIP_CLASS_ID", "");
        String nowYear = SysDateMgr.getNowYear();

        // 如果可以恢复大客户身份，且vip大客户免费换卡次数大于全球通商旅、上网套餐免费换卡次数
        if ((userVipTypeCode.equals("2") || userVipClassId.equals("1") || userVipClassId.equals("2") || userVipClassId.equals("3") || userVipClassId.equals("4") || userVipClassId.equals("5")) && (vipFreeChgCard > productFreeChgCard))
        {
            IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userId, "05", "0");
            int iCount = 0;
            for (int i = 0; IDataUtil.isNotEmpty(userOtherserv) && i < userOtherserv.size(); i++)
            {
                IData tmp = userOtherserv.getData(i);
                if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
                {
                    iCount++;
                }
            }
            if (iCount < vipFreeChgCard || vipFreeChgCard == 0)
            {
                freeChgCard = true;
                String erroInfo = "";
                if (freeChgCard)
                {
                    if (vipFreeChgCard > 0)
                    {
                        erroInfo = "该VIP用户在今年可以免费补换" + vipFreeChgCard + "次！";
                    }
                    else
                    {
                        erroInfo = "该VIP用户在今年可以不限次数免费补换卡！";
                    }
                }
                returnData.put("FREE_SIMCARD_FEE_TAG", "1");
                returnData.put("TIPS_INFO", erroInfo);
                // 需要返回界面提示，不收费
                return returnData;
            }
        }
        // 全球通商旅、上网套餐888/588/388每年免费换卡2次，全球通商旅、上网套餐288每年免费换卡1次
        else if (productFreeChgCard > 0)
        {
            IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userId, "07", "0");
            int iCount = 0;
            for (int i = 0; i < userOtherserv.size(); i++)
            {
                IData tmp = (IData) userOtherserv.get(i);
                if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
                {
                    iCount++;
                }
            }
            if (iCount < productFreeChgCard)
            {
                String errorInfo = "该全球通商旅、上网套餐用户在今年可以免费补换" + productFreeChgCard + "次！";
                returnData.put("FREE_SIMCARD_FEE_TAG", "2");
                returnData.put("TIPS_INFO", errorInfo);
                // 需要返回界面提示，不收费
                return returnData;
            }
        }

        // SIM卡补换卡费用调整,在网3年以上的老客户可以免费换卡,需求号：HNYD-REQ-20100208-011
        // 获取用户开户月份数
        String strOpenDate = userInfo.getString("OPEN_DATE", "");
        int OpenMoths = SysDateMgr.monthInterval(strOpenDate, SysDateMgr.getSysDate());// 获取两个时间的月差
        if (OpenMoths >= 36 && freeChgCard == false)
        {
            // 获取3年老客户免费换卡次数
            int oldCustFreeChgCard = 0;
            tagInfoDataset.clear();
            tagInfoDataset = TagInfoQry.getTagInfosByTagCode(CSBizBean.getTradeEparchyCode(), "CS_NUM_OLDCUSTFREECHGCARDS", "CSM", "0");
            if (tagInfoDataset != null && tagInfoDataset.size() > 0)
            {
                oldCustFreeChgCard = Integer.parseInt(tagInfoDataset.getData(0).getString("TAG_NUMBER"));
            }
            else
            {
                CSAppException.apperr(CustException.CRM_CUST_1005);
            }

            IDataset userOtherserv = UserOtherservQry.qryServInfoByPk(userId, "03", "0");
            int iCount = 0;
            for (int i = 0; i < userOtherserv.size(); i++)
            {
                IData tmp = (IData) userOtherserv.get(i);
                if (tmp.getString("START_DATE").substring(0, 4).equals(nowYear))
                {
                    iCount++;
                }
            }
            if (iCount < oldCustFreeChgCard || oldCustFreeChgCard == 0)
            {
                freeChgCard = true;
                returnData.put("FREE_SIMCARD_FEE_TAG", "3");
                String erroInfo = "";
                if (oldCustFreeChgCard == 0)
                    erroInfo = "该用户在网年限大于3年,可以不限次数免费换卡！";
                else
                    erroInfo = "该用户在网年限大于3年,可以一年免费换卡" + oldCustFreeChgCard + "次！";
                returnData.put("TIPS_INFO", erroInfo);
                // 需要返回界面提示，不收费
                return returnData;
            }
        }

        IData inParam = new DataMap();
        inParam.put("TRADE_TYPE_CODE", "310");
        inParam.put("RES_TYPE_CODE", "1");
        inParam.put("RES_KIND_CODE", simCardInfo.getString("RES_KIND_CODE", "-1"));
        inParam.put("CARD_KIND_CODE", "-1");
        inParam.put("CAPACITY_TYPE_CODE", simCardInfo.getString("CAPACITY_TYPE_CODE", "%"));
        inParam.put("PRODUCT_ID", pageData.getString("PRODUCT_ID", "-1"));
        inParam.put("CLASS_ID", "Z");
        inParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        IDataset resDevFeelist = DevicePriceQry.getDevicePrice(inParam);
        if (IDataUtil.isNotEmpty(resDevFeelist))
        {
            simCardInfo.put("FEE_MODE", "0");
            simCardInfo.put("FEE", resDevFeelist.getData(0).getString("DEVICE_PRICE", ""));
            simCardInfo.put("FEE_TYPE_CODE", resDevFeelist.getData(0).getString("FEEITEM_CODE", ""));
        }

        return returnData;
    }

    /**
     * 判断现有的销户用户和在网用户的状态
     * 
     * @param pd
     * @param td
     * @return
     * @throws Exception
     */
    public IData prepareForRes(IData restoreUserData) throws Exception
    {
        String serialNumber = restoreUserData.getString("SERIAL_NUMBER");
        String netTypeCode = restoreUserData.getString("NET_TYPE_CODE");
        String userId = restoreUserData.getString("USER_ID");
        IData result = new DataMap();
        // 通过SEL_BY_SN_DESTROY，查询最后销户的用户（如果有在网用户则返回为空）
        IDataset dataset = UserInfoQry.getAllDestroyUserInfoBySn(serialNumber);
        // 如果上面查询的结果为空则查询是否存在在网用户（因为存在没有任何用户的可能）
        if (IDataUtil.isEmpty(dataset))
        {
            IData onlineUserData = UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(onlineUserData))
            {
                result.put("HAVE_ONLINE_USER", false);
                dataset = UserInfoQry.qryAllUserInfoBySnFromHis(serialNumber);
                if (IDataUtil.isNotEmpty(dataset))
                {
                    DataHelper.sort(dataset, "DESTROY_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
                    result.put("LAST_DESTROYED_USER", dataset.getData(0).getString("USER_ID"));
                }
            }
            else
            {
                result.put("HAVE_ONLINE_USER", true);
            }
        }
        else
        // 如果查询有记录则将userid保存起来
        {
            result.put("LAST_DESTROYED_USER", dataset.getData(0).getString("USER_ID"));
        }

        if (result.getBoolean("HAVE_ONLINE_USER"))
        {
            result.put("NEED_CHANGE_NUMBER", true);
            result.put("NEED_CHANGE_SIM", true);
            result.put("RESET_REASON", "该号码存在在网用户，请改号并换卡！");
        }
        else
        {
            // 该用户不是最后一个使用该号的用户
            if (!StringUtils.equals(userId, result.getString("LAST_DESTROYED_USER")))
            {
                result.put("NEED_CHANGE_NUMBER", true);
                result.put("NEED_CHANGE_SIM", true);
                result.put("RESET_REASON", "该号码已经被其他用户使用过，无优先使用该号码等级，请改号并换卡！");
            }
            else
            {
                checkRestoreOldRes(restoreUserData, result);
            }
        }

        String userCityId = restoreUserData.getString("CITY_CODE");
        String visitCityCode = getVisit().getCityCode();
        String userCityName = UAreaInfoQry.getAreaNameByAreaCode(userCityId);
        String visitCityName = UAreaInfoQry.getAreaNameByAreaCode(visitCityCode);
        if (!StringUtils.equals(visitCityCode, userCityId))
        {
            StringBuilder msg = new StringBuilder(100);
            if (!result.getBoolean("NEED_CHANGE_NUMBER"))
            {
                msg.append("当前业务区是【").append(visitCityName).append("】，号码归属业务区是【").append(userCityName).append("】");
                result.put("MESSAGE_CONTENT", msg.toString());
            }
            else
            {
                msg.append("该号码不能在【").append(visitCityName).append("】办理改号复机，如复机，请回【").append(userCityName).append("】办理，谢谢！");
                result.put("DISABLE_RESTORE", "1");
                result.put("RESET_REASON", msg.toString());
            }
        }

        return result;
    }

    /**
     * @methodName: releaseSingleResOnClose
     * @Description: 业务受理过程中浏览器关闭释放资源
     * @param input
     * @throws Exception
     * @version: v1.0.0
     * @author: xiaozb
     * @date: 2014-8-12 上午11:20:09
     */
    public void releaseSingleResOnClose(IData input) throws Exception
    {
        String newPhoneNo = input.getString("NEW_PHONE_NO", ""); // 新手机号码
        String newSimCardNo = input.getString("NEW_SIM_CARD_NO", "");// 新sim卡
        if (StringUtils.isNotEmpty(newPhoneNo)) // 释放选占的号码
        {
            ResCall.releaseRes("2", newPhoneNo, "0", CSBizBean.getVisit().getStaffId(),"0");
        }
        if (StringUtils.isNotEmpty(newSimCardNo)) // 释放选占的sim卡
        {
            ResCall.releaseRes("2", newSimCardNo, "1", CSBizBean.getVisit().getStaffId(),"0");
        }
    }
    
    /**
   	 * 通过productId获取主产品下元素
   	 * @author lizj
   	 * @date 20200225
   	 * @param param
   	 * @return
   	 * @throws Exception
   	 */
   public IDataset qryProductInfos(IData input)throws Exception
   {
	   //IDataUtil.chkParam(input, "PRODUCT_ID"); 
	   String iv_product_id = input.getString("PRODUCT_ID");
	   String elementType = input.getString("ELEMENT_TYPE","");
       IDataset newProductElements = ProductElementsCache.getProductElements(iv_product_id);
       IDataset result = new DatasetList();
       if(IDataUtil.isNotEmpty(newProductElements))
       	{
           	for (int i = 0; i < newProductElements.size(); i++)
       		{
           		IData ProductElement = newProductElements.getData(i);
           		//String strElementID = ProductElement.getString("ELEMENT_ID", "");
       			String strElementTypeCode = ProductElement.getString("ELEMENT_TYPE_CODE", "");
       			String strElementForceTag = ProductElement.getString("ELEMENT_FORCE_TAG", "");
       			String strGroupForceTag = ProductElement.getString("PACKAGE_FORCE_TAG", "");
       			String strElementDefaultTag = ProductElement.getString("ELEMENT_DEFAULT_TAG", "");
       			
       			//取主产品下的构成必选和默认勾选，或者主产品下必选和默认勾选组的元素
       			if(StringUtils.isNotBlank(elementType)){
       				if(elementType.equals(strElementTypeCode) && ("1".equals(strElementForceTag) || "1".equals(strGroupForceTag)|| "1".equals(strElementDefaultTag)))
	   				{
       					result.add(ProductElement);
	   				}
       			}else{
       				if("1".equals(strElementForceTag) || "1".equals(strGroupForceTag)|| "1".equals(strElementDefaultTag))
	   				{
       					result.add(ProductElement);
	   				}
       			}
   		        
       		}
       	}
   	return result;
   }
   
   public IData qryRestoreFamily(IData input)throws Exception
   {
	   String userId = input.getString("USER_ID");
	   String serialNumber = input.getString("SERIAL_NUMBER");
	   IData result = new DataMap();
	   result.put("FAMILY_TAG", "0");
	   IDataset tradeDatas = TradeHistoryInfoQry.getInfosOrderByDesc(serialNumber, userId, null);  
	   if(IDataUtil.isNotEmpty(tradeDatas)){
		   IData tradeData = tradeDatas.first();
		   String tradeId = tradeData.getString("TRADE_ID");
		   IDataset tradeRelaInfos = TradeRelaInfoQry.queryTradeRelaByTradeIdModTag(tradeId,"45",BofConst.MODIFY_TAG_DEL);
		   if(IDataUtil.isNotEmpty(tradeRelaInfos)){
//			    IData param = new DataMap();
//				for(int i=0;i<tradeRelaInfos.size();i++){
//					IData tradeRelaInfoData = tradeRelaInfos.getData(i);
//					String roleCodeB = tradeRelaInfoData.getString("ROLE_CODE_B");
//					String serialNumberB = tradeRelaInfoData.getString("SERIAL_NUMBER_B");
//					String shortCode = tradeRelaInfoData.getString("SHORT_CODE");
//					if("1".equals(roleCodeB)&&serialNumber.equals(serialNumberB)){
//						param.put("SERIAL_NUMBER", serialNumber);
//						param.put("PRODUCT_ID", "99000001");
//						param.put("DISCNT_CODE", "3410");
//						param.put("SHORT_CODE", shortCode);
//						break;
//					}
//					
//				}
//				IDataset elements = new DatasetList();
//				for(int i=0;i<tradeRelaInfos.size();i++){
//					IData tradeRelaInfoData = tradeRelaInfos.getData(i);
//					String roleCodeB = tradeRelaInfoData.getString("ROLE_CODE_B");
//					String serialNumberB = tradeRelaInfoData.getString("SERIAL_NUMBER_B");
//					String shortCode = tradeRelaInfoData.getString("SHORT_CODE");
//					IDataset elements2 = new DatasetList();
//					if("2".equals(roleCodeB)){
//						IData para = new DataMap();
//						para.put("SERIAL_NUMBER_B", serialNumberB);
//						para.put("DISCNT_CODE_B", "3411");
//						para.put("SHORT_CODE_B", shortCode);
//						para.put("checkTag", "1");
//						para.put("MEB_VERIFY_MODE", "2");
//						para.put("tag", "0");
//						System.out.println("标志RestoreFamilyAction2"+para);
//						elements2.add(para);
//						param.put("MEB_LIST", elements2);
//						try 
//						{
//							param.put("PRE_TYPE", "1");
//							IDataset set  =  CSAppCall.call("SS.FamilyCreateRegSVC.tradeReg", param);
//							IData retnData = (set != null && set.size()>0)?set.getData(0):new DataMap();
//							if (StringUtils.isNotBlank(retnData.getString("ORDER_ID")) && !"-1".equals(retnData.getString("ORDER_ID")))
//		                    {
//								param.remove("MEB_LIST");
//								param.remove("PRE_TYPE");
//								elements.add(para);
//		                    }
//						}catch(Exception e){
//							System.out.println("RestoreFamilyAction复机恢复亲亲网校验失败号码："+serialNumberB);
//							result.put("FAMILY_NUM"+i, "RestoreFamilyAction复机恢复亲亲网校验失败号码："+serialNumberB);
//						}
//						
//					}
//					
//				}
//			    
//				if(elements.size()>0){
//					result.put("FAMILY_TAG", "1");
//				}
			   result.put("FAMILY_TAG", "1");
		   }
	   }
	   return result;
   }
   
   public IDataset qryResDiscntInfos(IData input)throws Exception
   {
	   String userId = input.getString("USER_ID");
	   String serialNumber = input.getString("SERIAL_NUMBER");
	   input.put("ELEMENT_TYPE", "D");
	   IDataset results = new DatasetList();
	   IDataset tradeDatas = TradeHistoryInfoQry.getInfosOrderByDesc(serialNumber, userId, null);  
	   if(IDataUtil.isNotEmpty(tradeDatas)){
		   IData tradeData = tradeDatas.first();
		   String tradeId = tradeData.getString("TRADE_ID");
		   IDataset discntTrades = TradeDiscntInfoQry.qryTradeDiscntInfos(tradeId,BofConst.MODIFY_TAG_DEL,null);
		   IDataset commparaInfos2323 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","2323",null,null);
		   IDataset productInfos = this.qryProductInfos(input);
		   boolean elemetFlg = true;
		   for(int i=0;i<discntTrades.size();i++){
			   IData discntTrade = discntTrades.getData(i);
			   String discntCode = discntTrade.getString("DISCNT_CODE");
			   String specTag = discntTrade.getString("SPEC_TAG","");//关联优惠标志 2
			   IData discntData = DiscntInfoQry.getDiscntInfoByCode2(discntCode);
			   
			   for(int k = 0; k < commparaInfos2323.size(); k++){
       				String paraCode = commparaInfos2323.getData(k).getString("PARAM_CODE");
       				if(paraCode.equals(discntCode)){
       					elemetFlg = false; 
       					break;
       				}
       		   }
			   
			   if(elemetFlg){//配置下的元素跳过不返回
			       if (IDataUtil.isNotEmpty(discntData)) 
			       {
			    	   discntTrade.put("DISCNT_NAME", discntData.getString("DISCNT_NAME", ""));
			       }
			       if(!"2".equals(specTag)){
			    	   if(IDataUtil.isNotEmpty(productInfos)){
				    	   boolean flg = true;//判断销户时元素是否为产品构成元素
				    	   for(int j=0;j<productInfos.size();j++){
							   String elementId = productInfos.getData(j).getString("ELEMENT_ID");
							   if(discntCode.equals(elementId)){
								   flg = false;
								   break;
							   }
						   }
						   if(flg){
							   results.add(discntTrade);
						   }
	
				       }else{
				    	   results.add(discntTrade);
					   }    
			       }
			   }
			   elemetFlg = true;
		   }
		   
	   }
   	   return results;
   }
   
   public IDataset qryResSvcInfos(IData input)throws Exception
   {
	   String userId = input.getString("USER_ID");
	   String serialNumber = input.getString("SERIAL_NUMBER");
	   input.put("ELEMENT_TYPE", "S");
	   IDataset results = new DatasetList();
	   IDataset tradeDatas = TradeHistoryInfoQry.getInfosOrderByDesc(serialNumber, userId, null);  
	   if(IDataUtil.isNotEmpty(tradeDatas)){
		   IData tradeData = tradeDatas.first();
		   String tradeId = tradeData.getString("TRADE_ID");
		   IDataset svcTrades = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
		   IDataset productInfos = this.qryProductInfos(input);
		   for(int i=0;i<svcTrades.size();i++){
			   IData svcTrade = svcTrades.getData(i);
			   String svcCode = svcTrade.getString("SERVICE_ID");
			   String specTag = svcTrade.getString("USER_ID_A","");//关联优惠标志 2
			   if(BofConst.MODIFY_TAG_DEL.equals(svcTrade.getString("MODIFY_TAG"))&&"-1".equals(specTag)){
				   IData svc = USvcInfoQry.qryServInfoBySvcId(svcCode);
				   if (IDataUtil.isNotEmpty(svc)) 
			       {
					   svcTrade.put("SERVICE_NAME", svc.getString("SERVICE_NAME", ""));
			       }
				   if(IDataUtil.isNotEmpty(productInfos)){
					   boolean flg = true;//判断销户时元素是否为产品构成元素
					   for(int j=0;j<productInfos.size();j++){
						   String elementId = productInfos.getData(j).getString("ELEMENT_ID");
						   if(svcCode.equals(elementId)){
							   flg = false;
							   break;
						   }
						   
					   } 
					   if(flg){
						   results.add(svcTrade);
					   }
				   }else{
					   results.add(svcTrade);
				   }
				   
			   }
		   }
	   }
   	   return results;
   }
   
   public IDataset qryResplatSvcInfos(IData input)throws Exception
   {
	   String userId = input.getString("USER_ID");
	   String serialNumber = input.getString("SERIAL_NUMBER");
	   IDataset results = new DatasetList();
	   IDataset tradeDatas = TradeHistoryInfoQry.getInfosOrderByDesc(serialNumber, userId, null);  
	   if(IDataUtil.isNotEmpty(tradeDatas)){
		   IData tradeData = tradeDatas.first();
		   String tradeId = tradeData.getString("TRADE_ID");
		   IDataset platSvcTrades = TradePlatSvcInfoQry.getTradePlatSvcByTradeId(tradeId);
		   for(int i=0;i<platSvcTrades.size();i++){
			   IData platSvcTrade = platSvcTrades.getData(i);
			   String platSvcCode = platSvcTrade.getString("SERVICE_ID");
			   IData platSvc = UPlatSvcInfoQry.qryServInfoBySvcId(platSvcCode);
			   if (IDataUtil.isNotEmpty(platSvc)) 
		       {
				   platSvcTrade.put("SERVICE_NAME", platSvc.getString("SERVICE_NAME", ""));
		       }
			   if(BofConst.MODIFY_TAG_DEL.equals(platSvcTrade.getString("MODIFY_TAG"))){
				   IDataset bindDefaultOpenSvcConfigs = CommparaInfoQry.getCommparaByAttrCode1("CSM", "1110", "defaultopen", Route.CONN_CRM_CEN, null);
				   boolean flg = true;
				   for(int j=0;j<bindDefaultOpenSvcConfigs.size();j++){
					   IData bindConfig = bindDefaultOpenSvcConfigs.getData(j);
                       String bindServiceId = bindConfig.getString("PARAM_CODE", "0");
                       if(platSvcCode.equals(bindServiceId)){
                    	   flg = false;
						   break;
                       }
                       
				   }
				   if(flg){
					   results.add(platSvcTrade);
				   }
			   }
			   
		   }
		   
	   }
   	   return results;
   }
}
