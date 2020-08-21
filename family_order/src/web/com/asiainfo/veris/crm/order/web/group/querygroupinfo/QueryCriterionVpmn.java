
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QueryCriterionVpmn extends GroupBasePage
{

    public abstract IData getCondition();

    public abstract IDataset getInfos();

    /**
     * @Description: 初始化页面方法
     * @author lixiuyu
     * @date 2011-7-20
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
        setHintInfo("请输入查询条件~~!");
    }

    /**
     * @Description: 非规范子VPMN查询
     * @author lixiuyu
     * @date 2011-7-20
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData inparams = getData("cond", true);

        IDataset vpnDataSet = new DatasetList();
        String vpmnNoType = inparams.getString("QueryType"); // 0:母 1：子
        String serialNumber = "";
        String userIdA = "";
        String userIdB = "";
        if ("1".equals(vpmnNoType))
        {
            serialNumber = inparams.getString("SERIAL_NUMBER_A", ""); // 子
        }
        else if ("0".equals(vpmnNoType))
        {
            serialNumber = inparams.getString("SERIAL_NUMBER", ""); // 母
        }

        // 根据集团编号获取集团用户信息
        inparams.clear();
        inparams.put("SERIAL_NUMBER", serialNumber);
        IData userInfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(this, serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            setHintInfo("没有符合条件的集团用户~~！");
            return;
        }
        String userId = userInfo.getString("USER_ID", "");
        String productId = userInfo.getString("PRODUCT_ID", "");
        String brandCode = userInfo.getString("BRAND_CODE", "");

        if ("0".equals(vpmnNoType))
        {
            if (!"8050".equals(productId) || !"VPMN".equals(brandCode))
            {
                setHintInfo("您输入的不是母VPMN编号，请确认~~！");
                return;
            }
            userIdA = userId;

            // 查询母集团下的子集团，根据子集团查询非规范子VPMN
            IData parentparam = new DataMap();
            IData inparam = new DataMap();
            parentparam.put("USER_ID_A", userId);
            parentparam.put("RELATION_TYPE_CODE", "40");
            parentparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
            IDataset parentdataset = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this, userId, "40");
            if (IDataUtil.isNotEmpty(parentdataset))
            {
                for (int i = 0; i < parentdataset.size(); i++)
                {

                    IData parent = parentdataset.getData(i);
                    userIdB = parent.getString("USER_ID_B", "");

                    inparam.put("USER_ID", userIdB);
                    IDataset vpnInfos = CSViewCall.call(this, "CS.UserVpnInfoQrySVC.qryCriterionVpnInfoByUserId", inparam); // 子VPMN的vpn信息
                    if (IDataUtil.isEmpty(vpnInfos))
                    {
                        continue;
                    }
                    IData vpnInfo = vpnInfos.getData(0);

                    String vpmnType = vpnInfo.getString("VPN_SCARE_CODE", "");// 由ng的VPMN_TYPE改为j2ee的VPN_SCARE_CODE
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
                        rsrvStr2 = "本地化省级集团";
                    }
                    vpnInfo.put("RSRV_STR2", rsrvStr2);

                    // 查询用户是否有802服务，有则为规范集团
                    IData svcparams = new DataMap();
                    svcparams.put("USER_ID", userIdB);
                    svcparams.put("SERVICE_ID", "802");
                    svcparams.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                    IDataset tmpSvc = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getSvcUserId", svcparams);
                    if (IDataUtil.isEmpty(tmpSvc))
                    {
                        vpnInfo.put("RSRV_STR1", "非规范集团");
                    }
                    else
                    {
                        vpnInfo.put("RSRV_STR1", "规范集团");
                    }

                    vpnDataSet.add(vpnInfo);
                }
            }
            else
            {
                setHintInfo("您输入的母VPMN不存在子母关系，请确认~~！");
                return;
            }

        }
        else if ("1".equals(vpmnNoType))
        {
            if (!"8000".equals(productId) || !"VPMN".equals(brandCode))
            {
                setHintInfo("子VPMN必须是VPMN集团，请确认~~！");
                return;
            }
            userIdB = userId;

            // 获取母集团ID
            IDataset parentVpmnInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(this, userIdB, "40", false);
            if (IDataUtil.isEmpty(parentVpmnInfos))
            {
                IData inparam = new DataMap();
                inparam.clear();
                inparam.put("USER_ID", userIdB);
                IDataset vpnInfos = CSViewCall.call(this, "CS.UserVpnInfoQrySVC.qryCriterionVpnInfoByUserId", inparam);
                if (IDataUtil.isEmpty(vpnInfos))
                {
                    setHintInfo("输入子VPMN资料不存在，请确认~~！");
                    return;
                }
                IData vpnInfo = vpnInfos.getData(0);

                String vpmnType = vpnInfo.getString("VPN_SCARE_CODE", "");// 由ng的VPMN_TYPE改为j2ee的VPN_SCARE_CODE
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
                    rsrvStr2 = "本地化省级集团";
                }
                vpnInfo.put("RSRV_STR2", rsrvStr2);

                // 查询用户是否有802服务，有则为规范集团
                IData svcparams = new DataMap();
                svcparams.put("USER_ID", userIdB);
                svcparams.put("SERVICE_ID", "802");
                svcparams.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                IDataset tmpSvc = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getSvcUserId", svcparams);
                if (IDataUtil.isEmpty(tmpSvc))
                {
                    vpnInfo.put("RSRV_STR1", "非规范集团");
                }
                else
                {
                    vpnInfo.put("RSRV_STR1", "规范集团");
                }

                vpnDataSet.add(vpnInfo);
            }
            else
            {// 存在子母关系
                IData groupVpmn = (IData) parentVpmnInfos.get(0);
                userIdA = groupVpmn.getString("USER_ID_A", "");

                // 查询母集团下的子集团，根据子集团查询非规范子VPMN
                IData parentparam = new DataMap();
                IData inparam = new DataMap();
                parentparam.put("USER_ID_A", userIdA);
                parentparam.put("RELATION_TYPE_CODE", "40");
                parentparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                IDataset parentdataset = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this, userIdA, "40");
                if (IDataUtil.isNotEmpty(parentdataset))
                {
                    for (int i = 0; i < parentdataset.size(); i++)
                    {
                        IData parent = parentdataset.getData(i);
                        userIdB = parent.getString("USER_ID_B", "");

                        inparam.clear();
                        inparam.put("USER_ID", userIdB);
                        IDataset vpnInfos = CSViewCall.call(this, "CS.UserVpnInfoQrySVC.qryCriterionVpnInfoByUserId", inparam);
                        if (IDataUtil.isEmpty(vpnInfos))
                        {
                            continue;
                        }
                        IData vpnInfo = vpnInfos.getData(0);

                        String vpmnType = vpnInfo.getString("VPN_SCARE_CODE", "");// 由ng的VPMN_TYPE改为j2ee的VPN_SCARE_CODE
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
                            rsrvStr2 = "本地化省级集团";
                        }
                        vpnInfo.put("RSRV_STR2", rsrvStr2);

                        // 查询用户是否有802服务，有则为规范集团
                        IData svcparams = new DataMap();
                        svcparams.put("USER_ID", userIdB);
                        svcparams.put("SERVICE_ID", "802");
                        svcparams.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                        IDataset tmpSvc = CSViewCall.call(this, "CS.UserSvcInfoQrySVC.getSvcUserId", svcparams);
                        if (IDataUtil.isEmpty(tmpSvc))
                        {
                            vpnInfo.put("RSRV_STR1", "非规范集团");
                        }
                        else
                        {
                            vpnInfo.put("RSRV_STR1", "规范集团");
                        }

                        vpnDataSet.add(vpnInfo);
                    }
                }
            }
        }

        if (IDataUtil.isEmpty(vpnDataSet))
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }

        setCondition(getData());
        setInfos(vpnDataSet);

    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);

}
