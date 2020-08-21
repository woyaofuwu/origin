
package com.asiainfo.veris.crm.order.soa.script.rule.undo;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: UndoCheckOther.java
 * @Description: 业务返销校验其他限制
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 2014-5-29 下午2:45:36
 */
public class UndoCheckOther extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(UndoCheckOther.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {

        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 UndoCheckOther() >>>>>>>>>>>>>>>>>>");

        IData idataHisTrade = databus.getData("TRADE_INFO");
        String sTradeTypeCode = idataHisTrade.getString("TRADE_TYPE_CODE");
        String sUserId = idataHisTrade.getString("USER_ID");
        String sEparchyCode = idataHisTrade.getString("EPARCHY_CODE");

        IDataset dataSet = new DatasetList();
        IData param = new DataMap();

        // 生效UU关系检查
        dataSet.clear();
        param.clear();
        param.put("TRADE_TYPE_CODE", sTradeTypeCode);
        param.put("EPARCHY_CODE", sEparchyCode);
        param.put("USER_ID", sUserId);
        dataSet = Dao.qryByCode("TD_S_CPARAM", "SELCNT_CANT_CANCEL_RELSLIMIT", param);
        if (Integer.parseInt((String) dataSet.get(0, "RECORDCOUNT")) > 0)
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751094, "业务返销校验-用户有生效的关系存在，取消关系后才能返销！");
            return true;
        }
        
        /**
         * REQ201603010009 申请新增积分兑换业务
         * 要求：1、已经领取的礼品的积分兑换业务不允许返销。
      			 2、已过期的积分兑换业务不允许返销。
      			 3、点击时候直接弹出阻止
         * */
        String tradeId = idataHisTrade.getString("TRADE_ID");
        IData inParam=new DataMap();
        inParam.put("USER_ID", sUserId);
        inParam.put("TRADE_ID", tradeId);
        IDataset InvalidGoods = Dao.qryByCode("TL_B_USER_SCORE_GOODS", "SEL_USER_SCORE_GOODS_INVALID", inParam);
        if(InvalidGoods!=null &&InvalidGoods.size()>0){
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751095, "业务返销校验-用户积分兑换的礼品已经过期或者已经领取过，不允许返销！");
            return true;
        }
        
        /**--end--*/
        
        return false;
    }

}
