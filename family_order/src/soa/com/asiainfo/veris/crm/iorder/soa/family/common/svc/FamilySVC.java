
package com.asiainfo.veris.crm.iorder.soa.family.common.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.FamilyCallerBean;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class FamilySVC extends CSBizService
{

    /**
     * 处理新选择子商品
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset dealAddOffers(IData data) throws Exception
    {
        String type = IDataUtil.chkParam(data, "ROLE_TYPE");
        String roleCode = IDataUtil.chkParam(data, "ROLE_CODE");
        if (StringUtils.isBlank(type))
        {
            CSAppException.appError("20190514001", "TYPE类型不存在");
        }
        IDataset elements = new DatasetList(data.getString("OFFERS"));
        String tradeTypeCode = IDataUtil.chkParam(data, "TRADE_TYPE_CODE");

        FamilyBean bean = BeanManager.createBean(FamilyBean.class);

        bean.dealOfferTime(elements, type, tradeTypeCode);// 元素字段设置
        bean.dealOfferAttrs(elements);// 属性字段设置,data.getString("ROUTE_EPARCHY_CODE")
        // bean.dealFees(elements);

        IDataset errorInfos = bean.checkRule(data, elements);// 规则校验

        if (IDataUtil.isNotEmpty(errorInfos))
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < errorInfos.size(); i++)
            {
                sb.append(errorInfos.getData(i).getString("TIPS_INFO")).append("<br>");
            }
            CSAppException.appError("-1", "元素校验失败：<br>" + sb.toString());
        }

        // 校验
        data.put("SELECTED_ELEMENTS", elements);
        data.put(KeyConstants.BUSI_TYPE, tradeTypeCode);
        FamilyCallerBean.busiCheckNoCatch(data, FamilyConstants.TriggerPoint.SELECT_OFFERS.toString());

        return elements;
    }

    /**
     * 业务数据校验
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData check(IData param) throws Exception
    {
        String triggerType = IDataUtil.chkParam(param, "TRIGGER_TYPE");
        return FamilyCallerBean.busiCheck(param, triggerType);
    }

    /**
     * 业务参数转换
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public IData filterOffers(IData param) throws Exception
    {
        return FamilyCallerBean.filterOffers(param);
    }

    /**
     * @Description: 添加手机成员初始化与校验
     * @Param: [input]
     * @return: com.ailk.common.data.IDataset
     * @Author: zhenggang
     * @Date: 2020/7/27 20:04
     */
    public IDataset checkPhoneRole(IData input) throws Exception
    {
        IDataset results = new DatasetList();
        IData result = new DataMap();
        results.add(result);
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String roleCode = IDataUtil.chkParam(input, "ROLE_CODE");
        String roleType = IDataUtil.chkParam(input, "ROLE_TYPE");
        String tradeTypeCode = IDataUtil.chkParam(input, "TRADE_TYPE_CODE");
        // 1.用户基础信息
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumber);
        }
        String eparchyCode = userInfo.getString("EPARCHY_CODE");
        String userId = userInfo.getString("USER_ID");
        String custId = userInfo.getString("CUST_ID");
        IData custInfo = UcaInfoQry.qryPerInfoByCustId(custId, eparchyCode);

        input.put(KeyConstants.ROLE_CODE, roleCode);
        input.put(KeyConstants.ROLE_TYPE, roleType);
        input.put(KeyConstants.BUSI_TYPE, tradeTypeCode);
        FamilyCallerBean.busiCheckNoCatch(input, FamilyConstants.TriggerPoint.ADD_MEMBER.toString());
        result.put("MEMBER_NAME", custInfo.getString("CUST_NAME"));
        result.put("EPARCHY_CODE", eparchyCode);
        return results;
    }
    /**
     * @author yuyz
     * @Description
     * @Date 19:52 2020/8/3
     * @Param [data]
     * @return void
     **/
    public IDataset relaseImsOccupy(IData data)throws Exception{
        String serialNumber = data.getString("FIX_NUMBER");
        IDataset callset= ResCall.checkResourceForMphone("0", serialNumber, "0","1");
        return callset;
    }
    /**
    * @author yuyz
    * @Description 根据固话号码和手机号码查询固话主产品，手机主产品
    * @Date 20:08 2020/8/6
    * @Param [data]
    * @return com.ailk.common.data.IDataset
    **/
    public IDataset queryImsMainProductInfo(IData data) throws Exception {
        IDataUtil.chkParam(data, "IMS_USER_ID");
        IDataUtil.chkParam(data, "PHONE_NUMBER");
        String imsUserId = data.getString("IMS_USER_ID");
        IDataset imsProducts = UserProductInfoQry.queryMainProduct(imsUserId);
        if (IDataUtil.isNotEmpty(imsProducts)) {
            IData imsProd = imsProducts.getData(0);
            String phoneNum = data.getString("PHONE_NUMBER");
            IDataset phoneInfos = UserInfoQry.getUserInfoBySn(phoneNum, "0");
            if (IDataUtil.isNotEmpty(phoneInfos)) {
                String phoneUserId = phoneInfos.first().getString("USER_ID");
                IDataset phoneProducts = UserProductInfoQry.queryMainProduct(phoneUserId);
                if (IDataUtil.isNotEmpty(phoneProducts)) {
                    String phoneProductId = phoneProducts.first().getString("PRODUCT_ID");
                    imsProd.put("MOBILE_PRODUCT_ID", phoneProductId);
                }
            }
        }
        return imsProducts;
    }
}
