
package com.asiainfo.veris.crm.order.web.group.changevpmnshortcode;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class ChangeVpmnShortCode extends CSBasePage
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

        String newShortCode = getData().getString("NEW_SHORT_CODE", "");
        String oldShortCode = getData().getString("OLD_SHORT_COD", "");

        String grpUserId = getData().getString("GRP_USER_ID", "");
        String memUserId = getData().getString("MEB_USER_ID", "");
        String memEparchyCode = getData().getString("MEB_EPARCHY_CODE", "");
        inparam.put("USER_ID", grpUserId);
        inparam.put("SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        inparam.put("MEM_USER_ID", memUserId);
        inparam.put("PRODUCT_ID", "8000");

        IDataset productParam = new DatasetList();// 产品参数
        IData proInfo = new DataMap();
        IDataset proarams = new DatasetList();
        IData paramInfo = new DataMap();
        paramInfo.put("ATTR_VALUE", newShortCode);
        paramInfo.put("ATTR_CODE", "SHORT_CODE");
        proarams.add(paramInfo);
        IData paramInfo2 = new DataMap();
        paramInfo2.put("ATTR_VALUE", oldShortCode);
        paramInfo2.put("ATTR_CODE", "OLD_SHORT_CODE");
        proarams.add(paramInfo2);
        proInfo.put("PRODUCT_PARAM", proarams);
        proInfo.put("PRODUCT_ID", "8000");
        productParam.add(proInfo); // 产品参数

        IDataset resinfos = new DatasetList(); // 资源信息
        IData inparams = new DataMap();
        inparams.put("USER_ID", memUserId);// 成员USER_ID
        inparams.put("USER_ID_A", grpUserId);// 用户USER_ID
        inparams.put("RES_TYPE_CODE", "S");
        inparams.put("RES_CODE", oldShortCode);
        inparams.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
        IDataset mebress = CSViewCall.call(this, "CS.UserResInfoQrySVC.getResByUserIdResType", inparams);

        if (IDataUtil.isEmpty(mebress))
        {
            CSViewException.apperr(ResException.CRM_RES_84);

        }

        IData mebres = mebress.getData(0);
        mebres.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 状态属性：0-增加，1-删除，2-变更
        mebres.put("END_DATE", SysDateMgr.getSysTime());
        resinfos.add(mebres);

        if (!"".equals(newShortCode))
        {
            IData addres = new DataMap();
            addres.put("MODIFY_TAG", "0");
            addres.put("RES_TYPE_CODE", "S");
            addres.put("RES_CODE", newShortCode);
            resinfos.add(addres);
        }

        inparam.put("RES_INFO", resinfos);

        if (IDataUtil.isNotEmpty(productParam))
            inparam.put("PRODUCT_PARAM_INFO", productParam);
        inparam.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
        IDataset result = CSViewCall.call(this, "SS.ChangeVpmnShortCodeSVC.crtTrade", inparam);
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

        // 获取用户与用户关系信息
        IDataset userrelations = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, getData().getString("MEB_USER_ID"), "20", getData().getString("MEB_EPARCHY_CODE"), false);

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
        IData uuinfo = (IData) userrelations.getData(0);

        setUuinfo(uuinfo);

        IData relaparam = new DataMap();
        relaparam.put("RELATION_DATA", uuinfo.toString());
        setRelainfo(relaparam);

        // 查询VPN信息
        String userIdA = uuinfo.getString("USER_ID_A");

        UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(this, userIdA);

        // 查询VPMN用户资料
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, userIdA, false);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_30);
        }

        // 查询VPMN客户资料
        IData custparams = new DataMap();
        custparams.put("CUST_ID", userInfo.getString("CUST_ID"));
        custparams.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset parentcustinfos = CSViewCall.call(this, "CS.CustomerInfoQrySVC.getCustInfoByCId", custparams);
        if (IDataUtil.isEmpty(parentcustinfos))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_31);
        }
        IData custinfo = (IData) parentcustinfos.get(0);
        setCustinfo(custinfo);

        // 查询898集团客户资料
        IData groupparams = new DataMap();
        groupparams.put("CUST_ID", userInfo.getString("CUST_ID"));
        IDataset groupinfos = CSViewCall.call(this, "CS.UcaInfoQrySVC.qryGrpInfoByCustId", groupparams);

        if (IDataUtil.isEmpty(groupinfos))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_32);
        }
        IData groupinfo = (IData) groupinfos.get(0);
        groupinfo.put("GRP_USER_ID", userInfo.getString("USER_ID"));
        setGroupinfo(groupinfo);
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

    public abstract void setCustData(IData data);

    public abstract void setCustinfo(IData custinfo);

    public abstract void setGroupinfo(IData groupinfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setRelainfo(IData relainfo);

    public abstract void setUuinfo(IData uuinfo);
}
