
package com.asiainfo.veris.crm.order.web.group.querygroupinfo;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QryRemoveSixVpmnMember extends GroupBasePage
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
        // ExcelParser.exportExcel(pd, "group/querygroupinfo/QryRemoveSixVpmnMember.xml", "VPMN非6短号用户查询记录导出.xls",
    }

    /**
     * @Description: VPMN非6用户查询
     * @author lixiuyu
     * @date 2011-7-20
     * @param cycle
     * @throws Exception
     */
    public void queryInfos(IRequestCycle cycle) throws Exception
    {
        IData inparams = getData("cond", true);

        IDataset uuDataSet = new DatasetList();
        IDataOutput uuInfos = null;
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
            if (!productId.equals("8050") || !brandCode.equals("VPMN"))
            {
                setHintInfo("您输入的不是母VPMN编号，请确认~~！");
                return;
            }

            // 查询母集团下的子集团，根据子集团查询非6用户
            IDataset parentdataset = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this, userId, "40");

            if (IDataUtil.isNotEmpty(parentdataset))
            {
                StringBuilder userIdAs = new StringBuilder();
                int size = parentdataset.size();
                for (int i = 0; i < size; i++)
                {
                    IData parent = parentdataset.getData(i);
                    userIdB = parent.getString("USER_ID_B");
                    if (i != 0)
                    {
                        userIdAs.append(",");
                    }
                    userIdAs.append(userIdB);
                }
                IData inparam = new DataMap();
                inparam.put("USER_ID_A", userIdAs.toString());
                inparam.put("RELATION_TYPE_CODE", "20");
                inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
                if (size > 1)
                {
                    inparam.put("SQL_BY_IN", "1"); // 如果有多个userIdA，就使用in做条件，否则用=
                }
                uuInfos = CSViewCall.callPage(this, "CS.RelaUUInfoQrySVC.qryRelaUUByUIdaRemoveSix", inparam, getPagination("pageNav"));

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

            // 获取母集团ID
            IDataset parentVpmnInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(this, userId, "40", false);
            if (IDataUtil.isEmpty(parentVpmnInfos)) // 无母集团
            {
                IData inparam = new DataMap();
                inparam.clear();
                inparam.put("USER_ID_A", userId);
                inparam.put("RELATION_TYPE_CODE", "20");
                inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
                uuInfos = CSViewCall.callPage(this, "CS.RelaUUInfoQrySVC.qryRelaUUByUIdaRemoveSix", inparam, getPagination("pageNav"));
            }
            else
            {// 存在子母关系
                IData groupVpmn = (IData) parentVpmnInfos.get(0);
                userIdA = groupVpmn.getString("USER_ID_A", "");

                // 查询母集团下的子集团，根据子集团查询非6用户
                IData parentparam = new DataMap();
                IData inparam = new DataMap();
                parentparam.put("USER_ID_A", userIdA);
                parentparam.put("RELATION_TYPE_CODE", "40");
                parentparam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);
                IDataset parentdataset = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.qryRelaUUByUIdAAllDB", parentparam);
                if (IDataUtil.isNotEmpty(parentdataset))
                {
                    StringBuilder userIdAs = new StringBuilder();
                    int size = parentdataset.size();
                    for (int i = 0; i < size; i++)
                    {

                        IData parent = parentdataset.getData(i);
                        userIdB = parent.getString("USER_ID_B", "");
                        if (i != 0)
                        {
                            userIdAs.append(",");
                        }
                        userIdAs.append(userIdB);

                    }

                    inparam.put("USER_ID_A", userIdAs.toString());
                    inparam.put("RELATION_TYPE_CODE", "20");
                    inparam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
                    if (size > 1)
                    {
                        inparam.put("SQL_BY_IN", "1"); // 如果有多个userIdA，就使用in做条件，否则用=
                    }
                    uuInfos = CSViewCall.callPage(this, "CS.RelaUUInfoQrySVC.qryRelaUUByUIdaRemoveSix", inparam, getPagination("pageNav"));
                }
            }
        }
        uuDataSet = uuInfos.getData();
        if (IDataUtil.isEmpty(uuDataSet))
        {
            setHintInfo("没有符合条件的查询结果~~！");
        }
        else
        {
            setHintInfo("查询成功~~！");
        }

        long tt = 0;
        tt = uuInfos.getDataCount();
        setPageCounts(tt);
        setCondition(getData());
        setInfos(uuDataSet);
    }

    public abstract void setCondition(IData condition);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCounts(long pageCounts);
}
