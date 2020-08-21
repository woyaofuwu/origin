
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bboss;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * TODO BBOSS工单状态业务DAO
 * 
 * @author jch
 */

public class GrpProvCprtDAO
{

    /**
     * @Description:根据SYNC_SEQUENCE 更改 IF_ANS 是否反馈,和 保存RSRV_STR1 字段为 下一个节点的序列号
     * @author jch
     * @date
     * @param
     * @param
     * @param
     * @return
     * @throws Exception
     */
    public static void updatePoTradeState(String if_ans, String next_sync_sequence, String sync_sequence) throws Exception
    {
        IData parm = new DataMap();
        parm.put("IF_ANS", if_ans);
        parm.put("NEXT_SYNC_SEQUENCE", next_sync_sequence);
        parm.put("SYNC_SEQUENCE", sync_sequence);
        SQLParser parser = new SQLParser(parm);
        parser.addSQL(" update TF_B_POTRADE_STATE ");
        parser.addSQL(" set IF_ANS=:IF_ANS ");
        parser.addSQL(" ,RSRV_STR1=:NEXT_SYNC_SEQUENCE ");
        parser.addSQL(" where SYNC_SEQUENCE=:SYNC_SEQUENCE ");

        Dao.executeUpdate(parser, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /**
     * 构造函数
     * 
     * @throws Exception
     */
    public GrpProvCprtDAO() throws Exception
    {

        super();
    }

}
