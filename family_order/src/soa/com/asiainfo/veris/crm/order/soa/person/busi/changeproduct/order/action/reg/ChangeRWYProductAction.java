package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;



/**
*如果有新增任我用188、288、238元，（20170451 市场套卡188元套餐、 20170452 市场套卡288元套餐、 20170453 市场套卡238元套餐  ，判断是否有tf_f_user_other记录 ，没有则登记tf_b_trade_other（modify_tag=0）   ---送开户和签约     
 */
public class ChangeRWYProductAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        UcaData uca = btd.getRD().getUca();
        List<ProductTradeData> productTrades = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);//获取产品子台帐
        String newProductId = "";
        String oldProductId = "";
        if (productTrades != null && productTrades.size() > 0) {//说明做了主产品的变更            
            for (ProductTradeData productTrade : productTrades) {
                if (BofConst.MODIFY_TAG_ADD.equals(productTrade.getModifyTag())) {
                    newProductId = productTrade.getProductId();
                } else if (BofConst.MODIFY_TAG_DEL.equals(productTrade.getModifyTag())) {
                    oldProductId = productTrade.getProductId();
                }
            }
        }

        if (newProductId != null && !"".equals(newProductId.trim()) && oldProductId != null && !"".equals(oldProductId.trim())) {

            IDataset newproductds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1086", newProductId, "1");//是否是任我用产品
            IDataset oldproductds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1086", oldProductId, "1");//是否是任我用产品

            IData param = new DataMap();
            param.put("USER_ID", uca.getUserId());
            param.put("RSRV_VALUE_CODE", "RWYPRODUCT");
            IDataset ds = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_USERID", param);
            String instId = "";
            String modifyTag = "";
            String rsrvvalue = "";
            if (IDataUtil.isNotEmpty(ds)) {//采用更新的方式
                IData data = ds.first();
                instId = data.getString("INST_ID");
                modifyTag = BofConst.MODIFY_TAG_UPD;
            } else {
                instId = SeqMgr.getInstId();
                modifyTag = BofConst.MODIFY_TAG_ADD;
            }

            String rwyproductId = "";
            if (IDataUtil.isNotEmpty(newproductds) && IDataUtil.isEmpty(oldproductds)) {//如新产品是任我用系列并且老产品不是任我用系列，开户+签约
                rsrvvalue = "10";//  10 开户+签约 ; 11 取消开户+取消签约
                rwyproductId  = newProductId;
            } else if (IDataUtil.isEmpty(newproductds) && IDataUtil.isNotEmpty(oldproductds)) {//如新产品不是任我用系列并且老产品是任我用系列，取消开户+签约
                rsrvvalue = "";//  10 开户+签约 ; 11 取消开户+取消签约
                rwyproductId  = oldProductId;
            }


            if (rsrvvalue != null && !"".equals(rsrvvalue)) {

                OtherTradeData otherTD = new OtherTradeData();
                otherTD.setModifyTag(modifyTag);
                otherTD.setInstId(instId);
                otherTD.setRsrvValue(rsrvvalue);//  0 开户+签约 ; 1 取消开户+取消签约

                otherTD.setUserId(uca.getUserId());
                otherTD.setRsrvValueCode("RWYPRODUCT");//任我用产品
                otherTD.setRsrvStr1(uca.getSerialNumber());//手机号码
                otherTD.setRsrvStr2("11000010000000000000000000000010");//策略编码
                otherTD.setRsrvStr3(uca.getCustId());
                otherTD.setRsrvStr4("任我用产品ID: " + rwyproductId);
                otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
                otherTD.setRsrvStr9("20170429");
                otherTD.setRsrvStr11(CSBizBean.getVisit().getStaffId());
                otherTD.setStartDate(SysDateMgr.getSysTime());
                otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
                otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
                otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
                otherTD.setIsNeedPf("1");//发送给服开
                btd.add(uca.getSerialNumber(), otherTD);
            }
        }

    }

}
