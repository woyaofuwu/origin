
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMbDisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;


public class AddJkdtAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(AddJkdtAction.class);
	public void executeAction(IData mainTrade) throws Exception
    {
		logger.info("jjj01:"+mainTrade);
		String discntTradeTableName = "TF_B_TRADE_DISCNT";
		String intfId = mainTrade.getString("INTF_ID","");
		String tradeId = mainTrade.getString("TRADE_ID");
		String tradeTypeCode = mainTrade.getString("TRADE_TYPE_CODE");
		IDataset tradeGrpMerchMbInfos = TradeGrpMerchMbDisInfoQry.getTradeGrpMerchMbByTradeId(tradeId);
		if(IDataUtil.isEmpty(tradeGrpMerchMbInfos)){
			return;
		}
		
		if("5002501".equals(tradeGrpMerchMbInfos.getData(0).getString("SERVICE_ID"))){
		//6、组装数据
		
		if("2352".equals(tradeTypeCode)){//成员新增
			IData discntTradeData = new DataMap();
			discntTradeData.put("TRADE_ID", tradeId);
			discntTradeData.put("ACCEPT_MONTH", tradeId.substring(4, 6));
			discntTradeData.put("USER_ID", mainTrade.getString("USER_ID"));
			discntTradeData.put("USER_ID_A", mainTrade.getString("USER_ID_B"));
			discntTradeData.put("DISCNT_CODE", "2038");
			//特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
			discntTradeData.put("SPEC_TAG", "2");
			discntTradeData.put("RELATION_TYPE_CODE", "V3");
			discntTradeData.put("INST_ID", SeqMgr.getInstId());
			discntTradeData.put("CAMPN_ID", "0");
			discntTradeData.put("START_DATE", SysDateMgr.getSysTime());
	        discntTradeData.put("END_DATE", "2050-12-31");
	        discntTradeData.put("MODIFY_TAG", "0");
	        discntTradeData.put("UPDATE_TIME", SysDateMgr.getSysTime());
			discntTradeData.put("UPDATE_STAFF_ID", mainTrade.getString("UPDATE_STAFF_ID"));
			discntTradeData.put("UPDATE_DEPART_ID", mainTrade.getString("UPDATE_DEPART_ID"));
			discntTradeData.put("IS_NEED_PF", "0");//不发服开
			IDataset list=TradeAttrInfoQry.getTrade(tradeId);
			if(list.size()!=0){
				String tabList=list.getData(0).getString("INTF_ID", "");//获取表名称
				String INTF_ID=tabList+"TF_B_TRADE_DISCNT,";
				IData map=new DataMap();
				map.put("TRADE_ID",tradeId);
				map.put("INTF_ID",INTF_ID);
				TradeAttrInfoQry.updateByTradeId(map);//更新trade表信息
			}
			Dao.insert(discntTradeTableName, discntTradeData,Route.getJourDb());
		}else if("2351".equals(tradeTypeCode))//删除成员
		{ 
			IData discntTradeData = new DataMap();
			IData map = new DataMap();
			map.put("USER_ID", mainTrade.getString("USER_ID"));
			map.put("USER_ID_A", mainTrade.getString("USER_ID_B"));
			map.put("DISCNT_CODE", "2038");
			IDataset discntDatas = UserDiscntInfoQry.getJkdtUserDis(map);
			discntTradeData=discntDatas.getData(0);
			discntTradeData.put("TRADE_ID", tradeId);
			discntTradeData.put("END_DATE",SysDateMgr.getSysTime());
			discntTradeData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
			discntTradeData.put("IS_NEED_PF", "0");
			IDataset list=TradeAttrInfoQry.getTrade(tradeId);
			if(list.size()!=0){
				String tabList=list.getData(0).getString("INTF_ID", "");//获取表名称
				String INTF_ID=tabList+"TF_B_TRADE_DISCNT,";
				IData map1=new DataMap();
				map.put("TRADE_ID",tradeId);
				map.put("INTF_ID",INTF_ID);
				TradeAttrInfoQry.updateByTradeId(map1);//更新trade表信息
			}
			Dao.insert(discntTradeTableName, discntTradeData,Route.getJourDb());
			
			
		  }
		
		}
		
		
    }
}
