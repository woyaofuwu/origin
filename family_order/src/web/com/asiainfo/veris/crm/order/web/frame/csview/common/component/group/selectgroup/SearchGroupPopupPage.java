
package com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectgroup;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.search.SearchResponse;
import com.ailk.search.client.SearchClient;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class SearchGroupPopupPage extends GroupBasePage
{
    public void init(IRequestCycle cycle) throws Throwable
    {
        setCondition(getData("cond"));
    }

    /**
     * 作用：查询集团客户信息
     * 
     * @author luoy
     * @param cycle
     * @throws Throwable
     */
    public void queryGroupCusts(IRequestCycle cycle) throws Throwable
    {

        IDataset result = new DatasetList();
        IData conParams = getData("cond", true);
        String id = conParams.getString("groupId");
        String strQueryType = getData().getString("QueryType");
        String groupType = conParams.getString("IS_TTGRP", "");// true是铁通集团
        long tt = 0;

        if (strQueryType.equals("0")) // 按集团客户编码
        {
            IData grpData = null;
            if ("true".equals(groupType))
            {// 铁通融合
                grpData = UCAInfoIntfViewUtil.qryTTGrpCustInfoByGrpId(this, id, false);
            }
            else
            {
                grpData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, id, false);
            }
            if (IDataUtil.isNotEmpty(grpData))
            {
                result.add(grpData);
            }
        }
        else if (strQueryType.equals("1"))// 按集团客户名称
        {
            IDataOutput dd = null;
            if ("true".equals(groupType))
            {
                dd = UCAInfoIntfViewUtil.qryTTGrpCustInfoByCustName(this, id, getPagination("ActiveNav"));
            }
            else
            {
                dd = UCAInfoIntfViewUtil.qryGrpCustInfoByCustName(this, id, getPagination("ActiveNav"));
            }
            result = dd.getData();
            tt = dd.getDataCount();
        }
        else if (strQueryType.equals("4")) // 检索
        {
            String searchText = id;
            Pagination pagination = getPagination("ActiveNav");
            int start = pagination.getStart();
            int end = pagination.getEnd();
            if (StringUtils.isNotBlank(searchText) && searchText.length() >= 2)
            {
                SearchResponse resp = SearchClient.search("TF_F_CUST_GROUP", searchText, start, end);
                result = resp.getDatas();
                tt = resp.getNumTotalHits();
            }

        }
        else if (strQueryType.equals("2"))// 按证件号
        {
            if ("true".equals(groupType))
            {
                result = UCAInfoIntfViewUtil.qryTTGrpCustInfoByPsptId(this, conParams.getString("pstType"), conParams.getString("pstNum"), false);
            }
            else
            {
                result = UCAInfoIntfViewUtil.qryGrpCustInfoByPsptId(this, conParams.getString("pstType"), conParams.getString("pstNum"), false);
            }
        }
        else if (strQueryType.equals("3"))// 按集团服务号码
        {
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", id);

            // 查用户信息
            IData grpUCA = null;
            if ("true".equals(groupType))
            {
                grpUCA = UCAInfoIntfViewUtil.qryTTGrpUCAInfoByGrpSn(this, id, false);
            }
            else
            {
                grpUCA = UCAInfoIntfViewUtil.qryGrpUCAInfoByGrpSn(this, id, false);
            }

            if (IDataUtil.isNotEmpty(grpUCA))
            {
                result.add(grpUCA.getData("GRP_CUST_INFO"));
            }

        }
        else if (strQueryType.equals("5"))// 按成员号码
        {
            // 查成员路由
            IData memUser = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, id);
            if (null != memUser)
            {
                // 查客户信息 第三个参数 1查询UU 2查询BB 否则全部查,此处全部查
                IDataset custInfos = UCAInfoIntfViewUtil.qryMebOrderedGroupInfosByMebUserIdAndRelationCode(this, memUser.getString("USER_ID"), "", memUser.getString("EPARCHY_CODE"), false);// 1查询UU
                // 2查询BB
                // 否则全部查,此处全部查

                if (null != custInfos && custInfos.size() > 0)
                {
                    // 过滤重复集团
                    IData delData = new DataMap();
                    for (int i = 0, size = custInfos.size(); i < size; i++)
                    {
                        IData custInfo = custInfos.getData(i);
                        String custId = custInfo.getString("CUST_ID");
                        if ("true".equals(groupType))
                        {
                            String ttrhTag = custInfo.getString("RSRV_NUM3", "");
                            if (!"1".equals(ttrhTag))
                            {
                                continue;
                            }
                        }
                        if (delData.containsKey(custId))
                        {
                            continue;
                        }
                        delData.put(custId, custId);
                        result.add(custInfo);
                    }
                }
            }
        }
        setInfos(result);
        setInfosCount(tt);
    }

    public abstract void setCondition(IData condition);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setInfosCount(long count);
}
