
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class DeleteTradeInfoBean
{

    /**
     * @param
     * @desciption oldTradeId 表示需要delete的trade_id intfId = 主台账表的INTF_ID字段，记录该笔业务登记的台账信息 isSpec 表示是否需要做特殊处理
     * @author fanti
     * @version 创建时间：2014年9月3日 下午10:26:17
     */
    public static void deleteTradeInfo(String oldTradeId, String intfId, boolean isSpec) throws Exception
    {

        String[] tableNameList = intfId.split(",");

        // 循环子台账表，intfID中标记的台账记录
        for (int i = 0; i < tableNameList.length; ++i)
        {

            String tableName = tableNameList[i];

            // 1-1 主台账数据和订单数据不做删除
            if ("TF_B_TRADE".equals(tableName) || "TF_B_ORDER".equals(tableName))
            {

                continue;
            }

            delTradeInfoByTradeId(oldTradeId, tableName);
        }
    }

    /**
     * @param
     * @desciption 删除台帐信息
     * @author fanti
     * @version 创建时间：2014年9月3日 下午10:26:28
     */
    public static void delTradeInfoByTradeId(String tradeId, String tableName) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        Dao.delete(tableName, param, new String[]
        { "TRADE_ID", "ACCEPT_MONTH" }, Route.getJourDb(Route.CONN_CRM_CG));
    }

}
