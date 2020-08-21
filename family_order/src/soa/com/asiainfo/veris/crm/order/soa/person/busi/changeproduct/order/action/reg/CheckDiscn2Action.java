package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.wade.container.util.log.Log;



/**
开户时，不可选--20170454 15元/GB的流量加油包套餐
 */
public class CheckDiscn2Action implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
//        //log.info("("CheckDiscn2Actionxxxxxxxxxxxxxxxxxx29 " + btd);

        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐

        if (discntTrades != null && discntTrades.size() > 0) {
            check1(discntTrades);

            //不能同时新办理同一个AAP下的9元和12元优惠        
            check2(discntTrades);
        }
    }

    private void check1(List<DiscntTradeData> discntTrades) throws Exception
    {
        for (DiscntTradeData discntTrade : discntTrades) {
            if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                String discntCode = discntTrade.getDiscntCode();
                String productId = discntTrade.getProductId();
                if (discntCode != null && productId != null) {
                    // select t.* from   ucr_cen1.td_s_commpara    t  where 1=1   and t.param_attr in ( 1088) and t.para_code1='1'  ; 
                    IDataset discntds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1088", discntCode, "1");//开户业务不能办理的优惠编码
                    if (IDataUtil.isNotEmpty(discntds)) {
//                        String discntName = StaticUtil.getStaticValue(null, "TD_B_DISCNT", "DISCNT_CODE", "DISCNT_NAME", discntCode);
                        String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "开户业务不能办理[" + discntCode + "]" + discntName + "！");
                    }
                }
            }
        }
    }

    private void check2(List<DiscntTradeData> discntTrades) throws Exception
    {
        //不能同时新办理同一个AAP下的9元和12元优惠
        List<String> newDiscntCodes = new ArrayList<String>();
        for (DiscntTradeData discntTrade : discntTrades) {
            if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                String discntCode = discntTrade.getDiscntCode();
                if (discntCode != null) {
                    newDiscntCodes.add(discntCode.trim());
                }
            }
        }
        for (int i = 0; i < newDiscntCodes.size(); i++) {
            String newDiscntCode = newDiscntCodes.get(i);
            IDataset ds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1090", newDiscntCode, "1");
            if (IDataUtil.isNotEmpty(ds)) {//新办理的优惠存在同一个app的判断
                for (int j = 0; j < ds.size(); j++) {
                    if (newDiscntCodes.contains(ds.getData(j).getString("PARA_CODE2", "").trim())) {//如新办理2个优惠属于同一个app的情况
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "不能同时办理同一个APP下的不同优惠！");
                    }
                }
            }
        }
    }
}
