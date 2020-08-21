
package com.asiainfo.veris.crm.order.soa.person.busi.fixedtelephone.restore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.AssignParaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherservQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage O
 * 
 * @ClassName: RestoreUserSVC.java
 * @Description: 复机服务类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-4-22 上午10:34:17
 */
public class RestoreUserGHSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

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
        String newPhoneNo = input.getString("NEW_PHONE_NO", ""); // 记录用户上一次新换的号码
        String newSimCardNo = input.getString("NEW_SIM_CARD_NO", "");// 记录用户上一次新换的sim卡
        String newResCode = input.getString("RES_CODE", "");
        String oldResCode = input.getString("OLD_RES_CODE", "");

        String simFlag4G = ""; // sim卡4G标志，需要调用资源接口获取？？？？
        if (StringUtils.equals("1", resTypeCode))// sim卡校验
        {
            // 非第一次时并新老资源不一样时，先释放上一次资源
            if (StringUtils.isNotEmpty(newSimCardNo) && !StringUtils.endsWithIgnoreCase(newSimCardNo, oldResCode))
            {
                // 释放上一次资源newSimCardNo
                // IData resOccupyData = bean.IResOccupyRelease(pd,newSimCardNo,"1","ReleaseResTempoccupySingle");
            }
            IDataset simCardInfos = null;// ResCall.checkResourceForSim("0", "2", phoneNo, simCardNo, "1");
            // 老系统调用资源校验sim卡接口时，先查询TF_F_USER（SEL_BY_USER_ENDDATE）表的CITY_CODE
            // "".equals(td.getUserInfo().getString("CITY_CODE",""))?pd.getContext().getCityId():td.getUserInfo().getString("CITY_CODE");
            // 如果SIM卡表中EMPTY_CARD_ID字段不为空，表明该卡由白卡写成，到白卡表中取卡类型，老系统先调用接口“QRM_IGetResInfo”查询sim卡信息，

            // 添加4G卡校验
            IDataset newUsim4G = AssignParaInfoQry.getUSIM4GInfo("", CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(newUsim4G) && !"1".equals(simFlag4G))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1083);
            }
            else if (IDataUtil.isEmpty(newUsim4G) && "1".equals(simFlag4G))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1084);
            }
            if (IDataUtil.isNotEmpty(simCardInfos))
            {
                returnData = getSimCardFee(simCardInfos.getData(0), input);// 获取sim卡费
                returnData.putAll(simCardInfos.getData(0));
                returnData.put("SIM_CHECK_TAG", true);
            }
        }
        else if (StringUtils.equals("0", resTypeCode)) // 新的手机号码校验
        {
            // 非第一次时并新老资源不一样时，先释放上一次资源
            if (StringUtils.isNotEmpty(newPhoneNo) && !StringUtils.endsWithIgnoreCase(newPhoneNo, oldResCode))
            {
                // 释放上一次资源newPhoneNo
                // IData resOccupyData = bean.IResOccupyRelease(pd,newSimCardNo,"1","ReleaseResTempoccupySingle");
            }
            String phoneNo = input.getString("RES_CODE", "");
            // return ResCall.checkResourceForMphone("0", "2", phoneNo, "0", "");
            IData tempData = new DataMap();
            tempData.put("RESULT_CODE", "1");
            returnData.put("PHONE_CHECK_TAG", true);
        }
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
    public void checkRestoreOldRes(String serialNumber, IData result) throws Exception
    {
        // 调用资源接口，校验复机用户的原号码和原sim卡是否可用
        IData data = new DataMap();// ResCall,QCS_CheckRestore

        result.put("SIM_CHECK_TAG", true);
        result.put("PHONE_CHECK_TAG", true);
        result.put("CHANGE_REASON_CODE", "-1");
        result.put("RESET_REASON", "");

        if ("2".equalsIgnoreCase(data.getString("X_CHECK_INFO")))
        {
            result.put("PHONE_CHECK_TAG", false);
            result.put("SIM_CHECK_TAG", true);
            result.put("CHANGE_REASON_CODE", "3");
            result.put("RESET_REASON", "用户服务号码已经被回收，请换卡并重新校验服务号码！");
        }
        else if ("5".equalsIgnoreCase(data.getString("X_CHECK_INFO")))
        {
            result.put("PHONE_CHECK_TAG", false);
            result.put("SIM_CHECK_TAG", true);
            result.put("NEED_CHANGE_SIM", true);
            result.put("RESET_REASON", "该号码为空闲号，请换卡并重新校验服务号码！");
        }
        else if ("6".equalsIgnoreCase(data.getString("X_CHECK_INFO")))
        {
            result.put("PHONE_CHECK_TAG", false);
            result.put("SIM_CHECK_TAG", true);
            result.put("CHANGE_REASON_CODE", "4");
            result.put("RESET_REASON", "该号码为空号，请改为空闲号复机！");
            CSAppException.apperr(CrmUserException.CRM_USER_490);
        }
        else
        {
            result.put("PHONE_CHECK_TAG", true);
            result.put("SIM_CHECK_TAG", true);
            result.put("CHANGE_REASON_CODE", "2");
        }
    }

    // 过滤产品类型，复机只能恢复为原有产品类型
    private IDataset filterProductType(String productId) throws Exception
    {
        IDataset dataset = ProductTypeInfoQry.getProductsTypeByParentTypeCode("3000");// 固话产品
        IDataset result = new DatasetList();
        IDataset productTypes = ProductTypeInfoQry.getProductTypeByProductId(productId);
        if (!productTypes.isEmpty())
        {
            for (int i = 0; i < dataset.size(); i++)
            {
                if (productTypes.getData(0).getString("PRODUCT_TYPE_CODE", "").equals(dataset.getData(i).getString("PRODUCT_TYPE_CODE", "")))
                {
                    IData data = dataset.getData(i);
                    result.add(data);
                    return result;
                }
            }
        }
        return result;
    }

    public IData getRestoreUserInfo(IData userInfo) throws Exception
    {
        String userId = userInfo.getString("USER_ID", "0");
        IData returnData = new DataMap();
        IData resData = getRestoreUserResInfo(userId);
        IData telephoneInfoData = getTelephoneInfo(userId);
        returnData.put("RES_INFO", resData);

        IDataset productDataset = UserProductInfoQry.queryMainProduct(userId);
        if (IDataUtil.isEmpty(productDataset))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_45, userId);
        }
        IData productData = productDataset.getData(0);
        String productId = productData.getString("PRODUCT_ID", "0");
        String brandCode = productData.getString("BRAND_CODE", "");
        IDataset productTypeList = this.filterProductType(productId);
        returnData.put("PRODUCT_TYPE_LIST", productTypeList);
        IData editResInfoData = this.prepareForRes(userInfo);
        editResInfoData.put("PRODUCT_ID", productId);
        editResInfoData.put("BRAND_CODE", brandCode);
        editResInfoData.put("PRODUCT_TYPE_CODE", productData.getString("PRODUCT_TYPE_CODE", ""));
        editResInfoData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
        editResInfoData.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(brandCode)); 

        // 查询用户信用度
        editResInfoData.put("CREDIT_VALUE", "");
        returnData.put("EDIT_INFO", editResInfoData);
        returnData.put("TELEPHONE", telephoneInfoData);
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
            if (StringUtils.equals("0", resTypeCode)) // 手机号吗;
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
        IData userInfo = pageData.getData("USRE_INFO");
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
        String userId = userInfo.getString("USER_ID", "0");
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
        int OpenMoths = SysDateMgr.monthInterval(strOpenDate, SysDateMgr.getCurDay());// 获取两个时间的月差
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
        inParam.put("TRADE_TYPE_CODE", "9706");
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
            simCardInfo.put("DEVICE_PRICE", resDevFeelist.getData(0).getString("DEVICE_PRICE", ""));
            simCardInfo.put("FEEITEM_CODE", resDevFeelist.getData(0).getString("FEEITEM_CODE", ""));
        }

        return returnData;
    }

    /**
     * 根据USER_ID查询用户固话信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData getTelephoneInfo(String userId) throws Exception
    {
        IDataset dataset = new DatasetList();
        dataset = UserInfoQry.getTelephoneInfoByUserId(userId);
        return dataset.size() == 0 ? null : dataset.getData(0);
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
        IDataset dataset = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
        // 如果上面查询的结果为空则查询是否存在在网用户（因为存在没有任何用户的可能）
        if (IDataUtil.isEmpty(dataset))
        {
            IDataset infos = UserInfoQry.getUserInfoBySN(serialNumber, "0", netTypeCode);
            if (IDataUtil.isEmpty(infos))
            {
                result.put("HAVE_ONLINE_USER", false);
            }
            else
            {
                result.put("HAVE_ONLINE_USER", true);
            }
        }
        // 如果查询有记录则将userid保存起来
        else
        {
            result.put("LAST_DESTROYED_USER", dataset.getData(0).getString("USER_ID"));
        }

        if (result.getBoolean("HAVE_ONLINE_USER"))
        {
            result.put("SIM_CHECK_TAG", false);
            result.put("PHONE_CHECK_TAG", false);
            result.put("CHANGE_REASON_CODE", "0");
            result.put("RESET_REASON", "该号码存在在网用户，请改号并换卡！");
        }
        else
        {
            // 该用户不是最后一个使用该号的用户
            if (!userId.equals(result.getString("LAST_DESTROYED_USER")))
            {
                result.put("SIM_CHECK_TAG", false);
                result.put("PHONE_CHECK_TAG", false);
                result.put("CHANGE_REASON_CODE", "1");
                result.put("RESET_REASON", "该号码已经被其他用户使用过，无优先使用该号码等级，请改号并换卡！");
            }
            else
            {
                checkRestoreOldRes(serialNumber, result);
            }
        }
        return result;
    }
}
