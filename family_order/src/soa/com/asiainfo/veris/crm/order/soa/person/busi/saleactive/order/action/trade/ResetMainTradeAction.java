
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.trade;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class ResetMainTradeAction implements ITradeAction
{
    @SuppressWarnings("unchecked")
    public void executeAction(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
        String tradeTypeCode =  btd.getMainTradeData().getTradeTypeCode();
        
        if("240".endsWith(tradeTypeCode) || "3814".equals(tradeTypeCode) || "3815".equals(tradeTypeCode))
    	{
        	List<SaleActiveTradeData> saleActiveTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
        	for (int i = 0, size = saleActiveTradeDatas.size(); i < size; i++)
            {
                SaleActiveTradeData saleActiveTradeData = saleActiveTradeDatas.get(i);
                if ("1".equals(saleActiveTradeData.getRsrvTag1()))
                    continue;

                MainTradeData mainTradeData = btd.getMainTradeData();

                mainTradeData.setCampnId(saleActiveTradeData.getCampnId());
                mainTradeData.setSerialNumberB(req.getGiftSerialNumber());

                mainTradeData.setRsrvStr1(saleActiveTradeData.getProductId());
                mainTradeData.setRsrvStr2(saleActiveTradeData.getPackageId());
                mainTradeData.setRsrvStr3(saleActiveTradeData.getProductName());
                mainTradeData.setRsrvStr4(saleActiveTradeData.getPackageName());
                mainTradeData.setRsrvStr5(saleActiveTradeData.getStartDate());
                mainTradeData.setRsrvStr6(saleActiveTradeData.getEndDate());
                mainTradeData.setRsrvStr7(saleActiveTradeData.getCampnType());
                mainTradeData.setRsrvStr8(saleActiveTradeData.getMonths());
                mainTradeData.setRsrvStr9(saleActiveTradeData.getRsrvDate1());
                mainTradeData.setRsrvStr10(saleActiveTradeData.getRsrvDate2());
                mainTradeData.setRemark(saleActiveTradeData.getRsrvStr23());

                resetProcessTagSet(btd);
            }
    	}
        
        if("230".endsWith(tradeTypeCode)||"257".endsWith(tradeTypeCode))
        {
            MainTradeData mainTradeData = btd.getMainTradeData();
            mainTradeData.setCampnId(req.getCampnId());
            mainTradeData.setSerialNumberB(req.getGiftSerialNumber());
            mainTradeData.setRsrvStr1(req.getProductId());
            mainTradeData.setRsrvStr2(req.getPackageId());
            IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(req.getProductId());
            mainTradeData.setRsrvStr3(productInfo.getString("PRODUCT_NAME"));
            IData packageInfo = UPackageInfoQry.getPackageByPK(req.getPackageId());
            mainTradeData.setRsrvStr4(packageInfo.getString("PACKAGE_NAME"));
            mainTradeData.setRsrvStr5(req.getStartDate());
            mainTradeData.setRsrvStr6(req.getEndDate());
            mainTradeData.setRsrvStr7(req.getCampnType());
            mainTradeData.setRsrvStr8(String.valueOf(SysDateMgr.monthInterval(req.getStartDate(), req.getEndDate())));
            mainTradeData.setRsrvStr9(req.getOnNetStartDate());
            mainTradeData.setRsrvStr10(req.getOnNetEndDate());
        }

    }

    @SuppressWarnings("unchecked")
    private void resetProcessTagSet(BusiTradeData btd) throws Exception
    {
        SaleActiveReqData req = (SaleActiveReqData) btd.getRD();
        String campnType = req.getCampnType();
        String tag = StaticUtil.getStaticValue("CAMPN_TYPE_2_TAGSET", campnType);
        if (StringUtils.isNotEmpty(tag))
        {
            btd.setMainTradeProcessTagSet(1, tag);
        }
    }
}
