package com.asiainfo.veris.crm.order.soa.person.busi.np.createnpusertrade.order.action;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityPlatOrderBean;

/**
 * 携入上面写白卡，工单完工
 */
public class DisOAOOrderStateAction implements ITradeFinishAction {
    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String serialNumber=mainTrade.getString("SERIAL_NUMBER");
        //查询携入上面写白卡申请执行的订单
        IDataset orderInfos=AbilityPlatOrderBean.queryOAOwriteCardOrderInfoNp(null,null,"6",serialNumber,"0");
        if (!IDataUtil.isEmpty(orderInfos)) {
            IData orderInfo = orderInfos.first();
            IData param2 = new DataMap();
            param2.put("STATE", "7");
            param2.put("ORDER_ID",  orderInfo.getString("ORDER_ID"));
            param2.put("SUBORDER_ID", orderInfo.getString("SUBORDER_ID"));
            AbilityPlatOrderBean.updateOAOwriteCardOrderInfoNP(param2);
            IData param = new DataMap();
            param.put("STATE", "AC");
            param.put("STATUS_DESC", "已激活（订购完成）");
            param.put("ORDER_ID", orderInfo.getString("ORDER_ID"));
            param.put("SUBORDER_ID", orderInfo.getString("SUBORDER_ID"));
            SQLParser sqlParser = new SQLParser(param);
            sqlParser.addSQL("  update TF_B_CTRM_GERLSUBORDER  set  STATE=:STATE,STATUS_DESC=:STATUS_DESC,RSRV_STR1=''");
            sqlParser.addSQL(" WHERE ORDER_ID = :ORDER_ID  AND SUBORDER_ID = :SUBORDER_ID ");
            Dao.executeUpdate(sqlParser, Route.CONN_CRM_CEN);
        }

    }
}
