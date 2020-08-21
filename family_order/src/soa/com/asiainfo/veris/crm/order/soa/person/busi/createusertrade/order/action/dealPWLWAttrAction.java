package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 关于下发《物联网资费优化业务支撑系统实施方案》的通知
 * CommparaInfoQry.getCommparaByCodeCode1("CSM", "3997", "WLWATTRCODE", attrCode) 查询出的记录，需要设置TF_B_TRADE_ATTR的remark字段为 WLW_SVC_ATTR，
 * 服开根据 TF_B_TRADE_ATTR的remark字段为 WLW_SVC_ATT 这个值，组装报文发送给平台
 *  
 */
public class dealPWLWAttrAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        List<MainTradeData> mainTradeInfos = btd.get("TF_B_TRADE");
        if (mainTradeInfos != null && mainTradeInfos.size() > 0) {
            MainTradeData maintradedata = mainTradeInfos.get(0);
            String brandCode = maintradedata.getBrandCode();
            if (brandCode != null && "PWLW".equals(brandCode)) {//如果是物联网的才进行操作
                List<AttrTradeData> attrTradeDatas = btd.get("TF_B_TRADE_ATTR");
                if (attrTradeDatas != null && attrTradeDatas.size() > 0) {
                    for (int i = 0, size = attrTradeDatas.size(); i < size; i++) {
                        AttrTradeData attrTradeData = attrTradeDatas.get(i);
                        String modifyTag = attrTradeData.getModifyTag();
                        String attrCode = attrTradeData.getAttrCode();
                        String instType = attrTradeData.getInstType();
                        if (BofConst.MODIFY_TAG_ADD.equals(modifyTag) && !"".equals(attrCode)) {                     
                            if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(instType))
                            {
                            IDataset infos = CommparaInfoQry.getCommparaByCodeCode1("CSM", "4997", "WLWATTRCODE", attrCode);
                            if (IDataUtil.isNotEmpty(infos)) {
                                attrTradeData.setRemark("WLW_SVC_ATTR");
                            }
                        }
                    }
                }
            }
        }
    }
}
}
