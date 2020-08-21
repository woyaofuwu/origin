
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade.esp;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationBBTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaBBInfoQry;

/**
 * 终止BB关系
 * @author thinkpad
 *提供给订购了ESP产品的成员主动消耗与信控欠费预销号使用
 */
public class DetroyEspMebBBRelationAction implements ITradeAction
{
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        String endDate = SysDateMgr.getSysDate();
        String relationTypeCode=StaticUtil.getStaticValue("ESP_RELATION_TYPE_CODE", "1");//产品关系编码
        IDataset UserBBRelaInfos =RelaBBInfoQry.qryRelationBBAllForBBossMem(null,uca.getUserId(),relationTypeCode, "1");
        if(IDataUtil.isEmpty(UserBBRelaInfos)){
          return;
        }else{
        	for(int i=0;i<UserBBRelaInfos.size();i++){
        	  IData BBRelaInfo=UserBBRelaInfos.getData(i);
        	  RelationBBTradeData TradeDataB = new RelationBBTradeData(BBRelaInfo);
        	  TradeDataB.setEndDate(endDate);
        	  TradeDataB.setModifyTag(BofConst.MODIFY_TAG_DEL);
              btd.add(TradeDataB.getSerialNumberB(), TradeDataB);        	  
        	}
        }
    }
}
