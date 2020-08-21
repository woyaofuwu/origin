
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.userpostrepair;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.QueryUserPostRepairQry;

public class QueryUserPostRepairBean extends CSBizBean
{
    /**
     * 根据邮政投递内容和Email投递内容的编码，将其用名称代替
     */
    public IDataset getRepairContent(IDataset datalist) throws Exception
    {
        String postContent = "";

        if (datalist != null && datalist.size() > 0)
        {
            for (int i = 0; i < datalist.size(); i++)
            {
                IData data = datalist.getData(i);
                postContent = processPostContent(data.getString("RSRV_STR2"));
                data.put("RSRV_STR2", postContent);
                postContent = processPostContent(data.getString("RSRV_STR3"));
                data.put("RSRV_STR3", postContent);
            }
        }

        return datalist;
    }

    /**
     * 编码转变成名称
     */
    private String processPostContent(String postContent) throws Exception
    {
        StringBuilder sb = new StringBuilder("");
        if (postContent != null && postContent.length() >= 1)
        {
            String str1 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(0, 1));
            sb.append(str1 != null && sb.length() == 0 ? str1 : "");
        }
        if (postContent != null && postContent.length() >= 2)
        {
            String str2 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(1, 2));
            sb.append(str2 != null && sb.length() != 0 ? "," + str2 : "");
        }
        if (postContent != null && postContent.length() >= 3)
        {
            String str3 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(2, 3));
            sb.append(str3 != null && sb.length() != 0 ? "," + str3 : "");
        }
        if (postContent != null && postContent.length() >= 4)
        {
            String str4 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(3));
            sb.append(str4 != null && sb.length() != 0 ? "," + str4 : "");
        }
        return sb.toString();
    }

    /**
     * 功能：用户邮寄补寄信息 作者：GongGuang
     */
    public IDataset queryUserPostRepair(IData data, Pagination page) throws Exception
    {

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String processTag = data.getString("PROCESS_TAG", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = SysDateMgr.getLastSecond(SysDateMgr.addDays(data.getString("END_DATE", ""), 1));// 在指定的日期上加1,此举
        // 是为了能查询出今天生成的补寄信息
        IDataset dataSet = QueryUserPostRepairQry.queryUserPostRepair(processTag, startDate, endDate, routeEparchyCode, page);
        return getRepairContent(dataSet);
    }

    public IDataset submitUserRepairPost(IData data, Pagination page) throws Exception
    {
        String trade_ids = data.getString("POSTREPAIR_TABLE");
        String processRemark = data.getString("PROCESS_REMARK");
        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        IDataset ids = new DatasetList(trade_ids);
        if (ids.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_783, "没有需要处理的补录信息");
        }
        int updateFlag = 0;
        String rsrvStrs = "";
        for (int i = 0; i < ids.size(); i++)
        {
            rsrvStrs += ids.getData(i).getString("RSRV_STR1");
            if (ids.size() > 1 && i < ids.size() - 1)
                rsrvStrs += ",";
        }
        updateFlag = QueryUserPostRepairQry.updPostRepair(rsrvStrs, processRemark, routeEparchyCode);
        // return TuxedoHelper.callTuxSvc(pd, "TCS_ModifyRepairPostInfo", datalist);
        IDataset dataset = new DatasetList();
        IData rtnDate = new DataMap();
        if (updateFlag > 0)
        {
            rtnDate.put("X_RESULTCODE", 0);
        }
        else
        {
            rtnDate.put("X_RESULTCODE", -999);
            rtnDate.put("X_RSPTYPE", "2");
            rtnDate.put("X_RSPCODE", "2998");
        }
        dataset.add(rtnDate);
        return dataset;
    }
}
