
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.adjustscore;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class QueryAdjustScoreBean extends CSBizBean
{

    /**
     * 查询调整积分
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getAdjustScore(IData data, Pagination pagination) throws Exception
    {
        data.put("TRADE_STAFF_ID", data.getString("X_TRADE_STAFF_ID"));
        String sn = data.getString("SERIAL_NUMBER");
        IDataset dataset = new DatasetList();
        if (StringUtils.isNotEmpty(sn))
        {

            IData userInfos = UcaInfoQry.qryUserInfoBySn(sn);

            if (IDataUtil.isNotEmpty(userInfos))
            {
                IData userInfo = userInfos;
                String userEpachyCode = userInfo.getString("EPARCHY_CODE", "ZZZZ");
                String staffEparchyCode = CSBizBean.getTradeEparchyCode();

                /* 业务受理时条件检查 */
                if (!userEpachyCode.equals(staffEparchyCode))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_114);
                }

                data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
                dataset = queryAdjustScore(data, pagination);

            }
            else
            {
                IDataset destroyInfos = UserInfoQry.getUserInfoBySnDestroyAll(sn, null);
                if (IDataUtil.isNotEmpty(destroyInfos))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_267);
                }
                else
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_269);
                }

            }
        }
        else
        {
            data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
            dataset = queryAdjustScore(data, pagination);
        }
        return dataset;
    }

    public IDataset queryAdjustScore(IData data, Pagination pagination) throws Exception
    {
        IDataset dataset = new DatasetList();
        if ("2".equals(data.getString("QUERY_MODE")))
        {
            dataset = TradeScoreInfoQry.queryAdjustScore_2(data, pagination);
        }
        else
        {
            dataset = TradeScoreInfoQry.queryAdjustScore(data, pagination);
        }

        return dataset;
    }

    /**
     *数据导出
     * 
     * @param data
     * @param eparchy_code
     * @return
     * @throws Exception
     */
    public IDataset queryAdjustScore(IData data, String eparchy_code) throws Exception
    {

        IDataset dataset = new DatasetList();
        if ("2".equals(data.getString("QUERY_MODE")))
        {
            dataset = TradeScoreInfoQry.queryAdjustScore_2(data, null, eparchy_code);
        }
        else
        {
            dataset = TradeScoreInfoQry.queryAdjustScore(data, null, eparchy_code);
        }

        return dataset;
    }

}
