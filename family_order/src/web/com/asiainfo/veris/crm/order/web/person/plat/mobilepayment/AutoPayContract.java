
package com.asiainfo.veris.crm.order.web.person.plat.mobilepayment;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 手机支付自动支付管理
 * 
 * @author xiekl
 */
public abstract class AutoPayContract extends PersonBasePage
{

    /**
     * 业务提交时校验
     * 
     * @param data
     * @throws Exception
     */
    public void checkBeforeSubmit(IData data) throws Exception
    {
        IData iParam = new DataMap();
        iParam.putAll(data);
        iParam.put(Route.ROUTE_EPARCHY_CODE, data.getString(Route.ROUTE_EPARCHY_CODE));
        iParam.put("USER_ID", data.getString("USER_ID"));
        iParam.put("SERVICE_ID", "171717");
        String operType = data.getString("cond_PAYOPR");

        IDataset userSvcList = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.qryUserSvcByUserSvcId", iParam);
        IDataset userList = CSViewCall.call(this, "CS.UserInfoQrySVC.getUserInfoBySerialNumber", iParam);
        boolean existAutoContractService = false;
        if (userSvcList != null && !userSvcList.isEmpty())
        {
            existAutoContractService = true;
        }

        if ("01".equals(operType))
        {
            iParam.put("SERVICE_ID", "171717");
            if (existAutoContractService)
            {
                CSViewException.apperr(PlatException.CRM_PLAT_74, "手机已经签约手机支付自动交费");
            }
        }

        if ("02".equals(operType))
        {
            if (IDataUtil.isEmpty(userList))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_1);
            }

            IData user = userList.getData(0);

            if ("0".equals(user.getString("PREPAY_TAG")))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_74, "后付费用户不能签约变更");
            }
        }

        // 校验是否开通了手机代扣签约
        if (operType.equals("02") || operType.equals("03"))
        {
            if (!existAutoContractService)
                CSViewException.apperr(PlatException.CRM_PLAT_74, "该手机没有开通手机代扣签约服务");
        }
    }

    /**
     * 业务规则校验
     * 
     * @param data
     * @throws Exception
     */
    @Override
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
        data.put("SERVICE_ID", "99166951");
        IDataset userPlatSvcList = CSViewCall.call(this, "CS.UserPlatSvcInfoQrySVC.queryPlatSvcByUserIdAndServiceId", data);
        if (userPlatSvcList != null && !userPlatSvcList.isEmpty())
        {
            IData userPlatSvc = userPlatSvcList.getData(0);
            if (!"A".equals(userPlatSvc.getString("BIZ_STATE_CODE")))
            {
                CSViewException.apperr(PlatException.CRM_PLAT_74, "该用户没有开通手机支付业务,或手机支付业务已被暂停！");
            }
        }
        else
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "该用户没有开通手机支付业务,或手机支付业务已被暂停！");
        }

        super.checkBeforeTrade(cycle);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRowIndex(int rowIndex);

    /**
     * 提交订单时触发
     * 
     * @param cycle
     * @throws Exception
     */
    public void submitTrade(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        this.checkBeforeSubmit(data);
        IDataset resultList = null;

        // 如果做的不是查询操作
        String operType = data.getString("cond_PAYOPR");
        if (!"04".equals(operType))
        {
            IDataset changeElementList = new DatasetList();
            IData changeElement = new DataMap();
            changeElement.put("ELEMENT_ID", "171717");
            changeElement.put("ELEMENT_TYPE_CODE", "S");
            changeElement.put("PACKAGE_ID", "-1");
            changeElement.put("PRODUCT_ID", data.getString("PRODUCT_ID"));

            StringBuilder sb = new StringBuilder();
            sb.append("1|").append(data.getString("cond_PAYTYPE")).append("|").append(data.getString("cond_ACCOUNTPERIOD")).append("|").append(data.getString("cond_PAYQUOTA"));
            if (!operType.equals("03"))// 解约不需要传参数所以不需要校验value
            {
                this.verifyAutoContractVariable(data);
            }

            IDataset attrParamList = new DatasetList();
            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", "171717_PARAM");
            attrParam.put("ATTR_VALUE", sb.toString());
            attrParamList.add(attrParam);
            changeElement.put("ATTR_PARAM", attrParamList);

            if (operType.equals("01")) // 签约 -PAYOPR
            {
                changeElement.put("MODIFY_TAG", "0");
            }
            if (operType.equals("03")) // 解约 -PAYOPR
            {
                changeElement.put("MODIFY_TAG", "1");
            }
            if (operType.equals("02")) // 变更 - PAYOPR
            {
                changeElement.put("MODIFY_TAG", "2");
            }

            changeElementList.add(changeElement);
            data.put("TRADE_TYPE_CODE", "110");
            data.put("SELECTED_ELEMENTS", changeElementList);
            resultList = CSViewCall.call(this, "SS.ChangeProductRegSVC.tradeReg", data);
        }
        else
        {
            resultList = new DatasetList();
            IData contractResult = new DataMap();
            data.put("SERVICE_ID", "171717");
            IDataset userSvcList = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.qryUserSvcByUserSvcId", data);
            if (userSvcList != null && !userSvcList.isEmpty())
            {
                IData userSvc = userSvcList.getData(0);
                data.put("RELA_INST_ID", userSvc.getString("INST_ID"));
                IDataset svcAttrList = CSViewCall.call(this, "CS.UserAttrInfoQrySVC.getUserAttrByRelaInstId", data);
                if (svcAttrList != null && !svcAttrList.isEmpty())
                {
                    IData svcAttr = svcAttrList.getData(0);
                    String attrValue = svcAttr.getString("ATTR_VALUE", "");
                    String[] attrArray = attrValue.split("\\|");

                    contractResult.put("PAY_TYPE", "0".equals(attrArray[1]) ? "按时间触发" : "按金额触发");
                    contractResult.put("PAY_TIME", attrArray[2]);
                    contractResult.put("PAYBNUM", attrArray[3]);
                    contractResult.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                    contractResult.put("CONTRACTSTATE", "已签约");
                    resultList.add(contractResult);
                }
            }
            else
            {
                contractResult.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                contractResult.put("CONTRACTSTATE", "未签约");
                resultList.add(contractResult);
            }
        }
        this.setAjax(resultList);
        setInfos(resultList);
        setCondition(this.getData("cond"));
    }

    private void verifyAutoContractVariable(IData data) throws Exception
    {
        try
        {
            String payType = data.getString("cond_PAYTYPE");
            int value = Integer.valueOf(data.getString("cond_ACCOUNTPERIOD"));
            int quota = Integer.valueOf(data.getString("cond_PAYQUOTA"));
            if (payType != null && !"".equals(payType) && "0".equals(payType))
            {
                if (0 >= value || value > 28)
                {
                    CSViewException.apperr(PlatException.CRM_PLAT_74, "触发值超范围");

                }
            }
            if (payType != null && !"".equals(payType) && "1".equals(payType))
            {
                if (0 >= value || value > 999)
                {
                    CSViewException.apperr(PlatException.CRM_PLAT_74, "触发值超范围");
                }
            }
            if (0 >= quota || quota > 9999)
                CSViewException.apperr(PlatException.CRM_PLAT_74, "支付额度超范围");
        }
        catch (NumberFormatException e)
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "触发值和支付额度必须是整数");
        }

    }

}
