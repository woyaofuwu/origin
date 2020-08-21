
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UpdateTradeInfoBean
{
    /**
     * @param
     * @desciption oldTradeId 表示需要update的trade_id intfId = 主台账表的INTF_ID字段，记录该笔业务登记的台账信息, isSpec 表示是否需要做特殊处理
     * @author fanti
     * @version 创建时间：2014年9月3日 下午10:48:57
     */
    public static void updateTradeInfo(String oldTradeId, String intfId, boolean isSpec) throws Exception
    {
        String[] tableNameList = intfId.split(",");

        // 循环子台账表，intfID中标记的台账记录
        for (int i = 0; i < tableNameList.length; ++i)
        {

            String tableName = tableNameList[i];

            updateTradeInfoByTradeId(oldTradeId, tableName);
        }
    }

    /**
     * @param
     * @desciption 更新台账信息
     * @author fanti
     * @version 创建时间：2014年9月3日 下午10:26:28
     */
    public static void updateTradeInfoByTradeId(String tradeId, String tableName) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SYS_DATE", SysDateMgr.getSysDate());

        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE ");
        sql.append(tableName);
        sql.append(" T ");
        sql.append(" SET T.START_DATE=TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        sql.append(" WHERE T.START_DATE < TO_DATE(:SYS_DATE, 'YYYY-MM-DD HH24:MI:SS')");
        sql.append(" AND T.TRADE_ID=TO_NUMBER(:TRADE_ID) ");
        sql.append(" AND T.accept_month = TO_NUMBER(SUBSTR(:TRADE_ID,5,2))");

        Dao.executeUpdate(sql, param);
    }
}
