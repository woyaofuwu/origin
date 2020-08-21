package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * REQ201512070014 关于4G终端社会化销售模式系统开发需求
 * 更新TF_R_GGCARD表数据
 */
public class GiftSaleActiveDealAction implements ITradeAction
{

    @SuppressWarnings("unchecked")
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
        SaleActiveReqData saleActiveReq = (SaleActiveReqData) btd.getRD();
        
        String preType = saleActiveReq.getPreType();
        String isConFirm = saleActiveReq.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
        {
            return;
        }
        
        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReq.getCallType()))
            return;
        
        //根据活动产品ID，判断是否属于特殊活动
        String productId = saleActiveReq.getProductId();
        IDataset productInfos1 = CommparaInfoQry.getCommpara("CSM", "528", productId, "0898");
        IDataset productInfos2 = CommparaInfoQry.getCommpara("CSM", "531", productId, "0898");
        
        //如果属于特殊活动，才进行处理
        if(IDataUtil.isNotEmpty(productInfos1) || IDataUtil.isNotEmpty(productInfos2) )
        {
        	String giftCode = saleActiveReq.getGiftCode();
            if (StringUtils.isNotBlank(giftCode))
            {
                 IData cond = new DataMap();
                 cond.put("GIFT_CODE", giftCode);
                 cond.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
                 cond.put("TRADE_ID", saleActiveReq.getTradeId());
                 cond.put("RSRV_STR3", btd.getMainTradeData().getSerialNumber());//扩展字符字段3存手机号
                 cond.put("RSRV_STR4", saleActiveReq.getProductId());//扩展字符字段4存活动编码
                 cond.put("RSRV_STR5", saleActiveReq.getPackageId());//扩展字符字段5存包编码
                 
                 StringBuilder sql = new StringBuilder(200);
                 sql.append(" UPDATE TF_R_GGCARD ");
                 sql.append(" SET PROCESS_TAG = '1', STAFF_ID = :STAFF_ID, TRADE_ID = :TRADE_ID, ");
                 sql.append(" RSRV_STR3 = :RSRV_STR3, RSRV_STR4 = :RSRV_STR4, RSRV_STR5 = :RSRV_STR5, RSRV_DATE3 = SYSDATE ");
                 sql.append(" WHERE CARD_PASS_WORD = :GIFT_CODE");
                 sql.append(" AND SYSDATE BETWEEN START_DATE AND END_DATE ");
                 sql.append(" AND (PROCESS_TAG IS NULL OR PROCESS_TAG='0') ");
                 Dao.executeUpdate(sql, cond);
            }
            
            String imeiCode = saleActiveReq.getImeiCode();
            if (StringUtils.isNotBlank(imeiCode))
            {
            	IData param = new DataMap();
            	param.put("USER_ID", btd.getMainTradeData().getUserId());
            	param.put("SERIAL_NUMBER", btd.getMainTradeData().getSerialNumber());
            	param.put("IMEI_CODE", imeiCode);
            	param.put("PRODUCT_ID", saleActiveReq.getProductId());
            	param.put("PACKAGE_ID", saleActiveReq.getPackageId());
            	param.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
            	param.put("TRADE_ID", saleActiveReq.getTradeId());

                StringBuilder sql = new StringBuilder(200);
                sql.append(" INSERT INTO TF_B_USER_SALE_ACTIVE_IMEI ");
                sql.append(" (USER_ID, SERIAL_NUMBER, IMEI, PRODUCT_ID, PACKAGE_ID, UPDATE_TIME, STAFF_ID, RSRV_STR1) ");
                sql.append(" VALUES ");
                sql.append(" (:USER_ID, :SERIAL_NUMBER, :IMEI_CODE, :PRODUCT_ID, :PACKAGE_ID, SYSDATE, :STAFF_ID, :TRADE_ID) ");
                Dao.executeUpdate(sql, param);
            }
        }
    }
}
