package com.asiainfo.veris.crm.order.soa.person.busi.destroyuser.order.action.finishaction;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.TradeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeUserInfoQry;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.query.BreQry;

/** 
 * @ClassName:  
 * @Description: 用户销号时，判断是否吉祥号码，且在吉祥号码免约定消费的目标客户群表里存在，则修改目标用户记录状态为失效。
 * @version: v1.0.0 《REQ201608100017 关于吉祥号码免约定消费号码池优化的需求》
 * @author: liquan
 * @date: 2016-8-16 下午9:07:22
 */
public class DestroyUpdateTroopMember implements ITradeFinishAction {    

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");

        IDataset numberInfo = ResCall.getMphonecodeInfo(serialNumber);// 查询号码信息
        if (IDataUtil.isNotEmpty(numberInfo)) {
            String jxNumber = numberInfo.getData(0).getString("BEAUTIFUAL_TAG");// BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
            if ("1".equals(jxNumber)) {//吉祥号码

                IData param = new DataMap();
                param.put("TROOP_ID", "20160112");
                param.put("CUST_CODE", serialNumber);

                SQLParser parser = new SQLParser(param);
                parser.addSQL(" select * ");
                parser.addSQL(" from TF_SM_TROOP_MEMBER A ");
                parser.addSQL(" where A.TROOP_ID = :TROOP_ID ");
                parser.addSQL(" and A.MEMBER_STATUS = '1' ");
                parser.addSQL(" and A.CUST_CODE = :CUST_CODE ");
                IDataset troopMemberSet = Dao.qryByParse(parser);

                if (IDataUtil.isNotEmpty(troopMemberSet)) {
                    IData data = troopMemberSet.first();
                    
                    data.put("TRADE_ID", mainTrade.getString("TRADE_ID",""));
                    data.put("ORDER_ID", mainTrade.getString("ORDER_ID",""));
                    data.put("TRADE_TYPE_CODE", mainTrade.getString("TRADE_TYPE_CODE",""));
                    data.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER",""));
                    data.put("ACCEPT_DATE", mainTrade.getString("ACCEPT_DATE",""));
                    data.put("TRADE_STAFF_ID", mainTrade.getString("TRADE_STAFF_ID",""));
                    data.put("TRADE_DEPART_ID", mainTrade.getString("TRADE_DEPART_ID",""));
                    data.put("TRADE_CITY_CODE", mainTrade.getString("TRADE_CITY_CODE",""));
                    data.put("TRADE_EPARCHY_CODE", mainTrade.getString("TRADE_EPARCHY_CODE",""));
                    data.put("UPDATE_TIME", mainTrade.getString("UPDATE_TIME",""));
                    data.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID",""));
                    data.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID",""));
                    data.put("REMARK_TRADE", "吉祥号码销户删除");
                    
                    Dao.insert("TL_B_DELTROOPMEMBER", data);//记录日志      
                    Dao.delete("TF_SM_TROOP_MEMBER", param, new String[]{"TROOP_ID","CUST_CODE"});//物理删除
                    
                }
            }
        }

    }
}
