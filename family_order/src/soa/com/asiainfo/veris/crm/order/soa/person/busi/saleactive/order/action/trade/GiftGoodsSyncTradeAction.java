
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tm.SaleActiveQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class GiftGoodsSyncTradeAction implements ITradeAction
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
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm(); 

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
            return;

        List<SaleGoodsTradeData> saleGoodsTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);

        for (SaleGoodsTradeData saleGoodsTradeData : saleGoodsTradeList)
        {
            String resTypeCode = saleGoodsTradeData.getResTypeCode();
            String resTag = saleGoodsTradeData.getResTag();

            if (!"D".equals(resTypeCode))
                continue;

            String resCheckTag = getResCheckTag(saleGoodsTradeData);
            
            //资源管理标记：0-资源无管理 ，不同步；1-资源管理，需同步
            if (!"1".equals(resTag) || "-1".equals(resCheckTag))
                continue;
            
            /**
             * REQ201504080009优惠活动礼品配置界面缺失修复
             * 查询礼品配置信息
             * chenxy3 2015-11-11 调接口使用礼品 HNSJ0213
             * */
            String resId="";
            if(saleGoodsTradeData.getResId() == null || "".equals(saleGoodsTradeData.getResId())){ 
            	IData ext_inparam=new DataMap();
            	String cityCode=CSBizBean.getVisit().getCityCode();//btd.getMainTradeData().getCityCode();
            	ext_inparam.put("PRODUCT_ID", saleGoodsTradeData.getProductId());
            	ext_inparam.put("PACKAGE_ID", saleGoodsTradeData.getPackageId());
            	ext_inparam.put("GOODS_ID", saleGoodsTradeData.getElementId());
            	ext_inparam.put("CITY_CODE", cityCode);  
            	IDataset extset=SaleActiveQry.querySaleActiveExt(ext_inparam);
            	if(extset!=null && extset.size()>0){
            		resId=extset.getData(0).getString("RES_ID","");
            		saleGoodsTradeData.setResId(resId);
            	}else{
            		//如果配置D类型的礼品且RES_ID为空，则要求TD_B_SALE_GOODS_EXT表有配置，否则无法办理
            		CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_55);
            	}
            }
            
            
            IData data = ResCall.occupyGiftGoods4Sale(saleGoodsTradeData.getResId(), saleActiveReqData.getTradeId(), Integer.parseInt(saleGoodsTradeData.getGoodsNum()));

            if (!"0".equals(data.getString("X_RESULTCODE", "1")))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_37);
            }
        }
    }

    private String getResCheckTag(SaleGoodsTradeData saleGoodsTradeData)
    {
        String modifyTag = saleGoodsTradeData.getModifyTag();

        if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            return "0"; // 销售

        if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            return "1"; // 返销

        return "-1";
    }
}
