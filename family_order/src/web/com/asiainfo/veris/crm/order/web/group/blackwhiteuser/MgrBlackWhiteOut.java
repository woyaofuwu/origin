
package com.asiainfo.veris.crm.order.web.group.blackwhiteuser;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.Msisdn.MsisdnInfoViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productmebinfo.ProductMebInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class MgrBlackWhiteOut extends GroupBasePage
{
    /**
     * @description:判断是否是网外手机号码
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public void checkIsOutPhoneCode(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);

        // 调用后台服务，根据服务号码查询用户信息
        IData userInfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serialNumber, false);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_1057, serialNumber);
        }
        else
        {
            IDataset MsisdnInfo = MsisdnInfoViewUtil.qryUserMsisdnInfo(this, serialNumber);
            
            if(IDataUtil.isNotEmpty(MsisdnInfo))
            {
                IData msisdninfo =MsisdnInfo.getData(0);
                if ("1".equals(msisdninfo.getString("ASP")))// 根据号段表判断是否是移动号码
                {
                    IData UserNumberPortabilityInfo = UCAInfoIntfViewUtil.qryUserNumberPortabilityInfoByUserId(this, serialNumber, "7", false);
                    if(IDataUtil.isEmpty(UserNumberPortabilityInfo) && "1".equals(msisdninfo.getString("HOME_TYPE")))
                    {
                        CSViewException.apperr(CrmUserException.CRM_USER_1057, serialNumber);
                        
                    }
                    
                }
                
            }
            
            
        }
            
    }

    /**
     * @description:校验服务号码
     * @param cycle
     * @throws Exception
     */
    public void checkSerialNumber(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String serialNumber = param.getString("cond_SERIAL_NUMBER", "");
        //checkIsOutPhoneCode(serialNumber);
    }

    /**
     * @description 查询选择的service_id所对应的服务参数信息
     * @param cycle
     * @throws Exception
     */
    public void getCurrentServiceInfo(IRequestCycle cycle) throws Exception
    {
        IData serviceInfo = new DataMap();
        IData param = getData();
        String serviceId = param.getString("SERVICE_ID"); // 服务类型对应的服务id
        String serialNumber = param.getString("SERIAL_NUMBER", ""); // 成员服务号码
        IDataset services = new DatasetList(param.getString("serviceInfos")); // 集团用户对应的平台服务
        // 遍历该集团用户下的平台服务，匹配所选择的服务类型
        for (int i = 0, size = services.size(); i < size; i++)
        {
            IData service = services.getData(i);
            String service_id = service.getString("SERVICE_ID");
            if (serviceId.equals(service_id))
            {
                serviceInfo = service;
                break;
            }
        }

        String eparchycode = serviceInfo.getString("EPARCHY_CODE"); // 地州
        String mebServId = null ;
        IData inparam = new DataMap();
        inparam.put("SERVICE_ID", serviceId);
        IDataset servIdinfo = CSViewCall.call(this, "SS.AdcMebParamsSvc.getMebService", inparam);
        if(IDataUtil.isNotEmpty(servIdinfo))
        {
            IData mebservice = servIdinfo.getData(0);
            mebServId =  mebservice.getString("ATTR_VALUE");
            
        }
        inparam.put("EC_USER_ID", serviceInfo.getString("USER_ID"));
        inparam.put("SERIAL_NUMBER", serialNumber);
        inparam.put("SERVICE_ID", mebServId);
        inparam.put(Route.ROUTE_EPARCHY_CODE, eparchycode);

        // 查询该网外号码是否已经加入黑白名单
        IDataset blackWhiteInfo = CSViewCall.call(this, "CS.UserBlackWhiteInfoQrySVC.getBlackWhitedataByGSS", inparam);
        if (IDataUtil.isNotEmpty(blackWhiteInfo))
        {
            
            IData oldblackwhite = blackWhiteInfo.getData(0);
            serviceInfo.put("OPER_TYPE", "1"); // 退出黑白名单
            serviceInfo.put("MEB_USER_ID", oldblackwhite.getString("USER_ID", "")); // 成员USER_ID
            serviceInfo.put("START_DATE", oldblackwhite.getString("START_DATE", "")); // 开始时间
            serviceInfo.put("MODIFY_TAG", "1"); // 删除
            
            IData UserbbInfo = RelationBBInfoIntfViewUtil.qryRelaBBInfoByUserIdBAndUserIdA (this,oldblackwhite.getString("USER_ID", ""),serviceInfo.getString("USER_ID"),eparchycode);
            if(IDataUtil.isNotEmpty(UserbbInfo))
            {
                CSViewException.apperr(CrmUserException.CRM_USER_1057, serialNumber);
                
            }
        }
        else
        {
            serviceInfo.put("MEB_USER_ID", "-1"); // 成员USER_ID
            serviceInfo.put("OPER_TYPE", "0"); // 加入黑白名单
            serviceInfo.put("MODIFY_TAG", "0"); // 新增
            checkIsOutPhoneCode(serialNumber);
        }
        setServiceInfo(serviceInfo);
    }

    /**
     * @description 提交黑白名单信息
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String grp_user_id = data.getString("GRP_USER_ID"); // 集团用户ID
        String meb_user_id = data.getString("MEB_USER_ID"); // 成员用户ID
        String serial_number = data.getString("cond_SERIAL_NUMBER"); // 成员服务号码
        String serviceInfo = data.getString("SERVICE_INFO"); // 得到集团拥有的平台服务
        String eparchy_code = data.getString("EPARCHY_CODE");
        String productId = data.getString("PRODUCT_ID"); // 集团产品ID
        IData serviceData = new DataMap(serviceInfo);
        IDataset serviceInfos = new DatasetList();
        serviceInfos.add(serviceData);
        IData param = new DataMap();
        param.put("USER_ID", grp_user_id);
        param.put("MEB_USER_ID", meb_user_id);
        param.put("SERIAL_NUMBER", serial_number);
        param.put("SERVICE_INFOS", serviceInfos);
        param.put("PRODUCT_ID", productId);
        param.put(Route.ROUTE_EPARCHY_CODE, eparchy_code);

        IDataset resultInfos = CSViewCall.call(this, "SS.MgrBlackWhiteOutSVC.crtTrade", param);

        setAjax(resultInfos);

    }

    /**
     * @description 查询集团拥有的平台服务
     * @param cycle
     * @throws Exception
     */
    public void queryGrpUserSvc(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String groupId = param.getString("GROUP_ID");

        IData data = new DataMap();
        data.put("GROUP_ID", groupId);

        IDataset serviceInfos = CSViewCall.call(this, "CS.UserGrpPlatSvcInfoQrySVC.getGrpPackagePlatService", data);
        if (IDataUtil.isEmpty(serviceInfos))
        {
            setHintInfo("根据集团编码[" + groupId + "]查询集团平台服务不存在，业务不能继续!");
        }
        else
        {
            // 过滤无成员产品的平台服务
            for (int i = 0; i < serviceInfos.size(); i++)
            {
                IData serviceInfo = serviceInfos.getData(i);
                IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, serviceInfo.getString("USER_ID"));
                if (IDataUtil.isEmpty(userData))
                {
                    serviceInfos.remove(i);
                    i--;
                    continue;
                }
                String memProduct = ProductMebInfoIntfViewUtil.getMemberMainProductByProductId(this, userData.getString("PRODUCT_ID"));
                if (StringUtils.isBlank(memProduct))
                {
                    serviceInfos.remove(i);
                    i--;
                    continue;
                }
            }
            if (IDataUtil.isEmpty(serviceInfos))
            {
                setHintInfo("根据集团编码[" + groupId + "]没有获取到可添加网外黑白名单的平台服务，业务不能继续!");
            }
        }

        setServiceInfos(serviceInfos);
    }

    /**
     * @description:查询黑白名单成员信息
     * @param cycle
     * @throws Exception
     */
    public void queryMemberInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String serialNumber = param.getString("cond_SERIAL_NUMBER", "");

        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);

        //checkIsOutPhoneCode(serialNumber);

        // 调用后台服务，查询黑白名单信息
        IDataset groupinfos = CSViewCall.call(this, "CS.GrpInfoQrySVC.getGroupFromBWBySn", data);
        if (IDataUtil.isEmpty(groupinfos))
        {
            CSViewException.apperr(GrpException.CRM_GRP_627, serialNumber);
        }
        int size = groupinfos.size();

        IData retData = new DataMap();

        retData.put("GROUPINFO_NUM", size);
        retData.put("GROUPINFOS", groupinfos);
        setAjax(retData);
    }
    
    /**
     * @description:查询手机号码是否为指定号码段
     * @param cycle
     * @throws Exception
     */
    public void queryCrmMsison(IRequestCycle cycle) throws Exception
    {
    	boolean istrue = false;
        IData param = getData();
        String serialNumber = param.getString("SERIAL_NUMBER", "");
        if (serialNumber.length()==11) {
        	serialNumber = serialNumber.substring(0,3);
            IData data = new DataMap();
            data.put("SERIAL_NUMBER", serialNumber);

            // 调用后台服务，查询ADC或MAS成员号段验证
            IData groupinfos = CSViewCall.callone(this, "CS.MsisdnInfoQrySVC.getCrmMsisonBySerialnumberlimit", data);
            if (IDataUtil.isNotEmpty(groupinfos))
            {
            	istrue = true;
            }
		}
        IData retData = new DataMap();
        retData.put("IS_TRUE", istrue);
        setAjax(retData);
    }

    public abstract void setHintInfo(String hintInfo);

    public abstract void setServiceInfo(IData info);

    public abstract void setServiceInfos(IDataset infos);
}
