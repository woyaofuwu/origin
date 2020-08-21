
package com.asiainfo.veris.crm.order.soa.frame.bcf.demotest;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public final class J2eeTestBean
{
    public static IDataset testBat(IData idata) throws Exception
    {

        String dealId = idata.getString("DEAL_ID");
        String execMonth = idata.getString("EXEC_MONTH");

        IData param = new DataMap();
        param.put("DEAL_ID", dealId);
        param.put("EXEC_MONTH", execMonth);

        StringBuilder sql = new StringBuilder(300);
        sql.append("SELECT T.DEAL_ID, T.SERIAL_NUMBER, T.DATA19, T.DATA20 ");
        sql.append("FROM TF_F_EXPIRE_DEAL T ");
        sql.append("WHERE T.DEAL_ID = :DEAL_ID ");
        sql.append(" AND  T.EXEC_MONTH = :EXEC_MONTH ");

        IDataset ids = Dao.qryBySql(sql, param);

        if (IDataUtil.isEmpty(ids))
        {
            throw new BaseException("TF_F_EXPIRE_DEAL.dealId[" + dealId + "]没数据");
        }

        IData mapBat = ids.getData(0);

        param.put("SERIAL_NUMBER", mapBat.getString("SERIAL_NUMBER"));

        // insert sql
        sql = new StringBuilder(300);
        sql.append("insert into J2EE_TEST_DATA(DEAL_ID,SERIAL_NUMBER) values (:DEAL_ID, :SERIAL_NUMBER) ");

        // insert crm
        Dao.executeUpdate(sql, param);

        // insert cen
        Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);

        // 是否lock
        String lockObj = mapBat.getString("DATA19");

        if (StringUtils.isNotBlank(lockObj))
        {
            param = new DataMap();
            param.put("LOCK_OBJ", lockObj);

            sql = new StringBuilder(300);

            sql.append("SELECT * ");
            sql.append("FROM J2EE_TEST_LOCK T ");
            sql.append("WHERE T.LOCK_OBJ = :LOCK_OBJ ");

            // sel crm
            ids = Dao.qryBySql(sql, param);

            if (IDataUtil.isEmpty(ids))
            {
                throw new BaseException("select crm J2EE_TEST_LOCK.lockObj[" + lockObj + "]没数据");
            }

            // sel log
            ids = Dao.qryBySql(sql, param, Route.CONN_CRM_CEN);

            if (IDataUtil.isEmpty(ids))
            {
                throw new BaseException("select cen J2EE_TEST_LOCK.lockObj[" + lockObj + "]没数据");
            }

            sql = new StringBuilder(300);

            sql.append("UPDATE J2EE_TEST_LOCK T ");
            sql.append("SET T.LOCK_DATA = T.LOCK_DATA + 1 ");

            // lock crm
            Dao.executeUpdate(sql, param);

            // insert cen
            Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
        }

        // 是否提交事务
        String isRollBack = mapBat.getString("DATA20");

        if (StringUtils.isNotBlank(isRollBack) && "1".equals(isRollBack))
        {
            throw new BaseException("TF_F_EXPIRE_DEAL.dealId[" + dealId + "]事物回滚");
        }

        param = new DataMap();
        param.put("TRADE_ID", "11");
        param.put("ORDER_ID", "22");

        return IDataUtil.idToIds(param);
    }
}
