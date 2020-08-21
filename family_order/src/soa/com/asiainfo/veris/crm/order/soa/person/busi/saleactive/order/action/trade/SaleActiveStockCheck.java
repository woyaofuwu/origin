package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.biz.BizEnv;
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
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;

public class SaleActiveStockCheck implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
        boolean nowRunFlag = BizEnv.getEnvBoolean("crm.merch.addShoppingCart", false); // 加购物车跳出此段逻辑,加个开关方便控制
        if (nowRunFlag)
        {
            OrderDataBus dataBus = DataBusManager.getDataBus();
            String submitType = dataBus.getSubmitType();// addShoppingCart
            if (StringUtils.equals(BofConst.SUBMIT_TYPE_SHOPPING_CART, submitType))
            {
                return;
            }
        }
		String preType = btd.getRD().getPreType();
        String isConFirm = btd.getRD().getIsConfirm();
        List<SaleActiveTradeData> saleActiveList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        if (saleActiveList == null || saleActiveList.size() == 0)
        {
            return;
        }
        String packageId = btd.getMainTradeData().getRsrvStr2();
//        IDataset result = PkgExtInfoQry.getPackageExtInfo(packageId, CSBizBean.getUserEparchyCode());
        IDataset result = UpcCall.queryTempChaByOfferTable(BofConst.ELEMENT_TYPE_CODE_PACKAGE, packageId, "TD_B_PACKAGE_EXT");
        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_23, packageId);
        }
        IData pkgExtInfo = result.getData(0);
        String condFactor3 = pkgExtInfo.getString("COND_FACTOR3");

        if (StringUtils.isBlank(condFactor3))
        {
            return;
        }

        if ("ZZZZ".equals(condFactor3))
        {

        }
        else
        {
            result = ActiveStockInfoQry.queryByResKind(condFactor3, CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_43);
            }
            IData activeStockInfo = result.getData(0);
            int warnningValueU = activeStockInfo.getInt("WARNNING_VALUE_U");
            int warnningValueD = activeStockInfo.getInt("WARNNING_VALUE_D");
            if (warnningValueU >= warnningValueD)
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_44);
            }
            
            //add by zhangxing3 for REQ201807230001
            String tradeTypeCode = btd.getTradeTypeCode();
            if(!"230".equals(tradeTypeCode))
            {
	            IData cond = new DataMap();
	            cond.put("RES_KIND_CODE", condFactor3);
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
