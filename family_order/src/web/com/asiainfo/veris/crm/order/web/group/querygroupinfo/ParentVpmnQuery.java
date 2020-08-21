
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class ParentVpmnQuery extends CSBasePage
{

    /**
     * 页面初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setMessage("请输入查询条件~~");
    }

    /**
     * 查询VPMN信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryVpmnList(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String queryType = condData.getString("QueryType");

        String serialNumber = "";

        if ("0".equals(queryType))
        {
            serialNumber = condData.getString("PARENT_SERIAL_NUMBER");
        }
        else if ("1".equals(queryType))
        {
            serialNumber = condData.getString("SUB_SERIAL_NUMBER");
        }

        // 查询VPMN用户信息
        IData userData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber, false);

        if (IDataUtil.isEmpty(userData))
        {
            setMessage("没有符合条件的集团用户~~");
            return;
        }

        String userId = userData.getString("USER_ID");
        String productId = userData.getString("PRODUCT_ID");
        String brandCode = userData.getString("BRAND_CODE");

        if ("0".equals(queryType)) // 根据母VPMN信息查询
        {
            if (!"8050".equals(productId) || !"VPMN".equals(brandCode))
            {
                setMessage("您输入的不是母VPMN编号, 请确认~~");
                return;
            }
        }
        else if ("1".equals(queryType)) // 根据子VPMN信息查询
        {
            if (!"VPMN".equals(brandCode))
            {
                setMessage("子VPMN必须是VPMN集团, 请确认~~");
                return;
            }
            IData relaData = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfoByUserIdBAndRelationTypeCode(this, userId, "40");
            if (IDataUtil.isEmpty(relaData))
            {
                setMessage("没有符合条件的子母集团资料~~");
                return;
            }

            userId = relaData.getString("USER_ID_A", "");
        }

        // 查询VPMN信息
        IData svcData = new DataMap();
        svcData.put("USER_ID", userId);
        svcData.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
        IDataset userVpnList = CSViewCall.call(this, "CS.UserVpnInfoQrySVC.qryParentUserVpnByUserId", svcData);

        if (IDataUtil.isEmpty(userVpnList))
        {
            setMessage("没有符合条件的查询结果~~");
            return;
        }

        // 数据转换
        for (int i = 0, row = userVpnList.size(); i < row; i++)
        {
            IData userVpnData = userVpnList.getData(i);

            String vpmnType = userVpnData.getString("VPMN_TYPE", ""); // VPMN类型

            String rsrvStr2 = "其它";

            if ("0".equals(vpmnType))
            {
                rsrvStr2 = "本地集团";
            }
            else if ("1".equals(vpmnType))
            {
                rsrvStr2 = "全省集团";
            }
            else if ("2".equals(vpmnType))
            {
                rsrvStr2 = "全国集团";
            }
            else if ("3".equals(vpmnType))
            {
                rsrvStr2 = "本地化全省级集团";
            }

            userVpnData.put("RSRV_STR2", rsrvStr2);
        }

        setVpmnList(userVpnList);
        setMessage("查询结果");
    }

    public abstract void setCondition(IData condition);

    public abstract void setMessage(String message);

    public abstract void setVpmnList(IDataset condition);

}
