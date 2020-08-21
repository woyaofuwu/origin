
package com.asiainfo.veris.crm.order.web.group.vpmn;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class VpnSpecialDistModi extends CSBasePage
{

    /**
     * @author liuy3
     * @param cycle
     * @throws Exception
     */
    public void confirm(IRequestCycle cycle) throws Exception
    {

    }

    public void onInitTrade(IRequestCycle cycle) throws Exception
    {

    }

    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {

        IData inparam = new DataMap();

        String grpUserId = getData().getString("GRP_USER_ID", "");
        String memUserId = getData().getString("MEB_USER_ID", "");
        String memEparchyCode = getData().getString("MEB_EPARCHY_CODE", "");
        inparam.put("USER_ID", grpUserId);
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));

        inparam.put("PRODUCT_ID", "8000");
        IData param = new DataMap();
        param.put("USER_ID", memUserId);
        param.put("USER_ID_A", grpUserId);
        param.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
        IDataset serviceinfos = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getUserProductSvc", param);
        if (IDataUtil.isEmpty(serviceinfos))
        {
            CSViewException.apperr(SvcException.CRM_SVC_3, "860");
        }
        boolean notHave860 = true;
        IDataset productElements = new DatasetList(); // 元素
        for (int i = 0; i < serviceinfos.size(); i++)
        {
            IData service = serviceinfos.getData(i);
            String serviceId = service.getString("SERVICE_ID");
            if ("860".equals(serviceId))
            {
                service.put("ELEMENT_ID", "860");
                service.put("MODIFY_TAG", "2");
                service.put("ELEMENT_TYPE_CODE", "S");
                productElements.add(service);
                notHave860 = false;
                break;
            }
        }
        if (notHave860)
        {
            CSViewException.apperr(SvcException.CRM_SVC_3, "860");
        }
        IDataset productParam = new DatasetList();// 产品参数

        inparam.put("ELEMENT_INFO", productElements);
        inparam.put("NEXT_ACCT_DISCODE", getData().getString("DISCNTTYPE"));// 下账期优惠
        inparam.put("THIS_ACCT_DISCODE", getData().getString("DISCNTTYPET"));// 本账期优惠
        if (IDataUtil.isNotEmpty(productParam))
            inparam.put("PRODUCT_PARAM_INFO", productParam);
        inparam.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
        IDataset result = CSViewCall.call(this, "SS.VpnSpecialDistModiSVC.crtTrade", inparam);
        setAjax(result);
    }

    /**
     * 查询VPN信息
     * 
     * @author liuy3
     * @param cycle
     * @throws Exception
     */
    public void queryVpnInfo(IRequestCycle cycle) throws Exception
    {
        String serial_number = getData().getString("cond_SERIAL_NUMBER");
        // 获取用户信息
        IData parem = new DataMap();
        parem.put("SERIAL_NUMBER", serial_number);
        parem.put("REMOVE_TAG", "0");
        parem.put("NET_TYPE_CODE", "00");
        IData userinfo = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, serial_number, true);

        // 获取用户与用户关系信息
        IDataset userrelations = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userinfo.getString("USER_ID"), "20", userinfo.getString("EPARCHY_CODE"), false);

        if (IDataUtil.isEmpty(userrelations))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_29, serial_number);
        }

        // 20的不止8000的vpmn产品，所以需要再判断
        IDataset ds8000 = getInfosByProductId(userrelations, "8000");
        if (IDataUtil.isEmpty(ds8000))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_29, serial_number);
        }
        IData userrelation = (IData) userrelations.getData(0);

        // 取用户VPN优惠
        IData userdiscnt = new DataMap();
        IData userdiscntparam = new DataMap();
        userdiscntparam.put("USER_ID", userinfo.getString("USER_ID"));
        userdiscntparam.put("USER_ID_A", userrelation.getString("USER_ID_A"));
        userdiscntparam.put(Route.ROUTE_EPARCHY_CODE, userinfo.getString("EPARCHY_CODE"));
        IDataset userdiscnts = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.getUserProductDis", userdiscntparam);

        if (IDataUtil.isNotEmpty(userdiscnts))
        {
            setInfos(userdiscnts);
        }
        else
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_40, serial_number);
        }

        // 取成员服务
        IDataset userservs = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getUserProductSvc", userdiscntparam);
        if (IDataUtil.isEmpty(userservs))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_41, serial_number);
        }

        // 查询VPN信息
        String userIdA = userrelation.getString("USER_ID_A");

        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userIdA, false);

        if (IDataUtil.isEmpty(userVpnData))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_42, serial_number, userIdA);
        }

        IData param = new DataMap();
        param.put("DISCNT_DATA", userdiscnts.toString());
        param.put("SERVICE_DATA", userservs.toString());
        param.put("RELATION_DATA", userrelation.toString());
        param.put("GRP_USER_ID", userIdA);
        setParaminfo(param);

    }

    /**
     * 从一个集团用户信息集里查出指定productId的集合
     * 
     * @param dataset
     * @param productId
     * @return
     * @throws Exception
     */

    public IDataset getInfosByProductId(IDataset source, String productId) throws Exception
    {
        IDataset ds = new DatasetList();
        for (int i = 0, cout = source.size(); i < cout; i++)
        {
            IData map = source.getData(i);
            String grpUserId = map.getString("USER_ID_A");
            IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, grpUserId);
            if (IDataUtil.isNotEmpty(userInfo))
            {
                if (productId.equals(userInfo.getString("PRODUCT_ID")))
                {
                    ds.add(map);
                }
            }
        }
        return ds;
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setParam(IData data);

    public abstract void setParaminfo(IData paraminfo);
}
