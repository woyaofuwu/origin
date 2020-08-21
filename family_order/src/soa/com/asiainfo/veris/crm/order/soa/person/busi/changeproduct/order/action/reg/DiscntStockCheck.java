package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.ActiveStockInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;


public class DiscntStockCheck implements ITradeAction {

	@Override
	public void executeAction(BusiTradeData btd) throws Exception 
	{
		String preType = btd.getRD().getPreType();
        int k = 0;
        String tradeId = btd.getTradeId();
        //IDataset discntTrades = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);

        if( discntTrades != null && discntTrades.size() > 0 ) {
            for (int i = 0; i < discntTrades.size(); i++) {
            	
            	DiscntTradeData discntTrade = (DiscntTradeData)discntTrades.get(i);
                            
                if("84010038".equals(discntTrade.getDiscntCode()) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                    k = k +1;
                }
                if("84010039".equals(discntTrade.getDiscntCode()) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                    k = k +2;
                }
                if("84010040".equals(discntTrade.getDiscntCode()) && BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                    k = k +3;
                }
            }
        }

        String condFactor3 = "MFTY";
        if ( k <= 0)
        {
            return;
        }
        else
        {
        	String saleProductId = "";
        	String saleActiveId = btd.getRD().getPageRequestData().getString("SALE_ACTIVE_ID", "");
        	String wideProductId = btd.getRD().getPageRequestData().getString("WIDE_PRODUCT_ID", "");

        	if(!"".equals(saleActiveId) && !"".equals(wideProductId))
        	{
	            IDataset saleActiveIDataset = CommparaInfoQry.getEnableCommparaInfoByCode12("CSM", "178", "600", wideProductId, saleActiveId, "0898");
		        if(!IDataUtil.isEmpty(saleActiveIDataset)){
		        	saleProductId = saleActiveIDataset.first().getString("PARA_CODE4", "");
		        }
        	}
	       	if ("".equals(saleProductId) )
	        {
	            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"您选择的优惠体检套餐，只支持宽带1+活动！");
	        }
        	if (!"".equals(saleProductId) && !"69908001".equals(saleProductId))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"您选择的优惠体检套餐，只支持宽带1+活动！");
            }
        	
        	
        	String serialNumberKD = btd.getMainTradeData().getSerialNumber();
        	if(!"".equals(serialNumberKD) && 14 == serialNumberKD.length())
        	{
        		IDataset ids = getUserMFTYDiscntBySNATTR(serialNumberKD, "8523", "0898");
            	if (IDataUtil.isNotEmpty(ids))
                {
                    CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"您已经办理过优惠体检套餐，一个用户只能享受一次！");
                }
        	}
        	
        	IDataset result = ActiveStockInfoQry.queryByResKind(condFactor3, CSBizBean.getVisit().getStaffId(), CSBizBean.getVisit().getCityCode(), CSBizBean.getTradeEparchyCode());
        	if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"您不具备办理优惠体检套餐的岗位");
            }
            IData activeStockInfo = result.getData(0);
            int warnningValueU = activeStockInfo.getInt("WARNNING_VALUE_U");
            int warnningValueD = activeStockInfo.getInt("WARNNING_VALUE_D");
            if (warnningValueU >= warnningValueD)
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"您已经达到优惠体检套餐的办理限额！");
            }
            if (warnningValueU+k > warnningValueD)
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_0,"您已经达到优惠体检套餐的办理限额！");
            }

            IData cond = new DataMap();
            cond.put("RES_KIND_CODE", condFactor3);
            cond.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            cond.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
            cond.put("CITY_CODE", CSBizBean.getVisit().getCityCode());
            StringBuilder sql = new StringBuilder(200);
            sql.append(" UPDATE TF_F_ACTIVE_STOCK");
            if (k==2){
            	sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U + 2");
            }
            else if (k==3)
            {
            	sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U + 3");
            }
            else
            {
            	sql.append(" SET WARNNING_VALUE_U = WARNNING_VALUE_U + 1");

            }
            sql.append(" WHERE STAFF_ID = :STAFF_ID");
            sql.append(" AND EPARCHY_CODE = :EPARCHY_CODE");
            sql.append(" AND RES_KIND_CODE = :RES_KIND_CODE");
            sql.append(" AND CITY_CODE = :CITY_CODE");
            Dao.executeUpdate(sql, cond);
        }
        
	}
	
	//查询用户是否办理过免费体验套餐
	public static  IDataset getUserMFTYDiscntBySNATTR(String serialNumber,String param_attr,String eparchy_code) throws Exception
	{

		IData iData = new DataMap();
		iData.put("SERIAL_NUMBER", serialNumber);
		iData.put("PARAM_ATTR", param_attr);
		iData.put("EPARCHY_CODE", eparchy_code);
		SQLParser dctparser = new SQLParser(iData);

		dctparser.addSQL(" SELECT a.* FROM tf_f_user_discnt a,tf_F_user b ");
		dctparser.addSQL("  WHERE a.partition_id=b.partition_id  AND a.user_id=b.user_id ");
		dctparser.addSQL("  and b.serial_number=:SERIAL_NUMBER ");
		dctparser.addSQL("     AND EXISTS(SELECT 1 FROM td_s_commpara  WHERE param_attr = :PARAM_ATTR ");
		dctparser.addSQL("   AND para_code3 = a.discnt_code AND (eparchy_code = :EPARCHY_CODE OR eparchy_code = 'ZZZZ') ");
		dctparser.addSQL("   AND sysdate BETWEEN start_date AND end_date) ");
		IDataset resultset = Dao.qryByParse(dctparser);
		return resultset;
	}
}
