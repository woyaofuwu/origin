package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser.order.action.undofinish;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * 无手机宽带撤单取消统付号码付费关系
 * @author luys
 *
 */
public class CancelNoPhoneUserPayRelationAction implements ITradeFinishAction {

	protected static Logger log = Logger.getLogger(CancelNoPhoneUserPayRelationAction.class);
	@Override
	public void executeAction(IData mainTrade) throws Exception {
		// TODO Auto-generated method stub

		log.debug("=======UndoNoPhoneUserPayRelationAction====in");
		String wideUserId = mainTrade.getString("USER_ID");
		
		IDataset allRelations = RelaUUInfoQry.getUserRelationAll(wideUserId, "58");
		
		if(IDataUtil.isNotEmpty(allRelations)){
			log.debug("=======RelaUUInfoQry====in");
			IData relations = allRelations.getData(0);
			relations.put("END_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            Dao.update("TF_F_RELATION_UU", relations);
            
            
            //无手机一机多宽付费关系改为了登记的action,没必要再去改资料表了 modify_by_duhj_kd
//            IDataset dataset = PayRelaInfoQry.getPayrelaByPayItemCode(wideUserId,"40001");
//            if(IDataUtil.isNotEmpty(dataset)){
//            	log.debug("=======PayRelaInfoQry====in");
//            	IData payRelation=dataset.first();
//            	payRelation.put("END_CYCLE_ID", SysDateMgr.getNowCycle());
//            	Dao.update("TF_A_PAYRELATION", payRelation);
//            }
		}
	}

}
