
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.WideNetTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;

public class DealFTTHStockAction implements ITradeAction
{

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{

        String rsrvStr2 = btd.getMainTradeData().getRsrvStr2();//RsrvStr2：光猫模式。0：租赁，2：赠送，3：自备
     
        if("2".equals(rsrvStr2)) {
        	 IDataset result = ActiveStockInfoQry.queryByResKind("FTTH", CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(result))
            {
            	IData cond = new DataMap();
            	cond.put("RES_KIND_CODE", "FTTH");
            	cond.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            	cond.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            	cond.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
            	StringBuilder sql = new StringBuilder(200);
            	sql.append(" UPDATE TF_F_ACTIVE_STOCK");
            	sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U + 1");
            	sql.append(" WHERE STAFF_ID = :STAFF_ID");
            	sql.append(" AND EPARCHY_CODE = :EPARCHY_CODE");
            	sql.append(" AND RES_KIND_CODE = :RES_KIND_CODE");
            	sql.append(" AND CITY_CODE = :CITY_CODE");
            	Dao.executeUpdate(sql, cond);
            }
        }
	}
}
