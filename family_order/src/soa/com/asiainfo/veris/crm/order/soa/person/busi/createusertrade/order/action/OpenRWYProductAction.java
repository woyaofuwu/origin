package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.requestdata.CreatePersonUserRequestData;

/**
 *如果有新增任我用188、288、238元，（20170451 市场套卡188元套餐、 20170452 市场套卡288元套餐、 20170453 市场套卡238元套餐  ，判断是否有tf_f_user_other记录 ，没有则登记tf_b_trade_other（modify_tag=0）   ---送开户和签约   
 */
public class OpenRWYProductAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {

        CreatePersonUserRequestData createPersonUserRD = (CreatePersonUserRequestData) btd.getRD();

        String productId = createPersonUserRD.getMainProduct().getProductId();
        if (productId != null) {
            IDataset productds = CommparaInfoQry.getCommparaByCodeCode1("CSM", "1086", productId, "1");//是否是任我用产品

            if (IDataUtil.isNotEmpty(productds)) {

                UserTradeData userTD = createPersonUserRD.getUca().getUser();

                IData param = new DataMap();
                param.put("USER_ID", userTD.getUserId());
                param.put("RSRV_VALUE_CODE", "RWYPRODUCT");
                IDataset ds = Dao.qryByCodeParser("TF_F_USER_OTHER", "SEL_BY_USERID", param);
                String instId = "";
                String modifyTag = "";

                if (IDataUtil.isNotEmpty(ds)) {//采用更新的方式
                    IData data = ds.first();
                    instId = data.getString("INST_ID");
                    modifyTag = BofConst.MODIFY_TAG_UPD;
                } else {
                    instId = SeqMgr.getInstId();
                    modifyTag = BofConst.MODIFY_TAG_ADD;
                }

                OtherTradeData otherTD = new OtherTradeData();
                otherTD.setModifyTag(modifyTag);
                otherTD.setInstId(instId);
                otherTD.setUserId(userTD.getUserId());
                otherTD.setRsrvValueCode("RWYPRODUCT");//任我用产品
                otherTD.setRsrvValue("10");//  10 开户+签约 ; 11 取消开户+取消签约
                otherTD.setRsrvStr1(userTD.getSerialNumber());//手机号码
                otherTD.setRsrvStr2("11000010000000000000000000000010");//策略编码
                otherTD.setRsrvStr3(userTD.getCustId());
                otherTD.setRsrvStr4("任我用产品" + productId);
                otherTD.setRsrvStr5(CSBizBean.getVisit().getStaffName());
                otherTD.setRsrvStr9("20170429");//固定编码
                otherTD.setRsrvStr11(CSBizBean.getVisit().getStaffId());
                otherTD.setStartDate(SysDateMgr.getSysTime());
                otherTD.setEndDate(SysDateMgr.END_DATE_FOREVER);
                otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
                otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
                otherTD.setIsNeedPf("1");//发送给服开
                btd.add(userTD.getSerialNumber(), otherTD);
            }
        }
    }

}
