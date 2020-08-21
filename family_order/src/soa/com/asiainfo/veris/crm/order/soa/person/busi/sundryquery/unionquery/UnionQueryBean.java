
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.unionquery;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.UnionOrderAndCancelQry;

/**
 * 统一查询服务实现类
 * 
 * @author zhouwu
 * @date 2014-07-29 21:34:40
 */
public class UnionQueryBean extends CSBizBean
{

    /**
     * 统一退订查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUnionCancelInfos(IData data, Pagination pagination) throws Exception
    {
        return UnionOrderAndCancelQry.queryUnionCancelInfos(data, pagination);
    }

    /**
     * 统一订购查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset queryUnionOrderInfos(IData data, Pagination pagination) throws Exception
    {
        return UnionOrderAndCancelQry.queryUnionOrderInfos(data, pagination);
    }

    /**
     * 统一退订业务详细信息查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset showCancelDetailInfo(IData data) throws Exception
    {
        String sessionId = data.getString("sessionId", "");
        String orderId = data.getString("orderId", "");

        return UnionOrderAndCancelQry.showCancelDetailInfo(sessionId, orderId);
    }

    /**
     * 统一订购详细信息查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset showOrderDetailInfo(IData data) throws Exception
    {
        String sessionId = data.getString("sessionId", "");
        String orderId = data.getString("orderId", "");

        return UnionOrderAndCancelQry.showOrderDetailInfo(sessionId, orderId);
    }

    /**
     * 统一退订会话详细信息查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset showSessionDetailInfo(IData data) throws Exception
    {
        String sessionId = data.getString("sessionId", "");
        return UnionOrderAndCancelQry.showSessionDetailInfo(sessionId);
    }

    /**
     * 统一退订产线保存处理意见信息
     * 
     * @param data
     * @throws Exception
     */
    public void updateUnionCancelInfos(IData data) throws Exception
    {
        String dealInfo = data.getString("dealInfo");
        String sessionId = data.getString("sessionId");

        if (StringUtils.isNotBlank(dealInfo))
        {
            IData params = new DataMap();
            params.put("SESSION_ID", sessionId);
            params.put("OPERATOR_ID", getVisit().getStaffId());
            params.put("OPERATOR_NAME", getVisit().getStaffName());
            params.put("SOLUTION", dealInfo);
            params.put("DEAL_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            params.put("DEAL_TAG", "0");

            Dao.save("TF_B_UNION_SESSION", params, new String[]
            { "SESSION_ID" });
        }
    }

    /**
     * 保存处理意见信息
     * 
     * @param data
     * @throws Exception
     */
    public void updateUnionOrderInfos(IData data) throws Exception
    {
        String dealInfo = data.getString("DEAL_INFO");
        String dealList = data.getString("DEAL_LIST");

        if (StringUtils.isNotBlank(dealInfo))
        {
            String[] checks = StringUtils.split(dealList, ";");

            for (int i = 0, size = checks.length; i < size; i++)
            {
                String[] ids = StringUtils.split(checks[i], ",");
                String sessionId = ids[0];
                String orderId = ids[1];

                IData params = new DataMap();
                params.put("SESSION_ID", sessionId);
                params.put("ORDER_ID", orderId);
                params.put("RSRV_STR2", getVisit().getStaffId());
                params.put("RSRV_STR3", getVisit().getStaffName());
                params.put("RSRV_STR4", dealInfo);
                params.put("RSRV_DATE1", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
                params.put("RSRV_TAG1", "0");

                Dao.save("TF_B_UNION_ORDER_LOG", params, new String[]
                { "SESSION_ID", "ORDER_ID" });
            }
        }
    }

}
