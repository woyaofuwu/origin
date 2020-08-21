
package com.asiainfo.veris.crm.order.soa.person.busi.changesvcstate.order.action.trade;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: OpenSmsServiceAction.java
 * @Description: 开通用户的短信服务
 * @version: v1.0.0
 * @author: zhuyu
 * @date: 2014-6-18 下午3:10:00
 */
public class OpenSmsServiceAction implements ITradeAction
{
    private static final String SMS_SERVICE = "5";

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        String userId = btd.getRD().getUca().getUserId();
        String productId = btd.getRD().getUca().getProductId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
        List<SvcTradeData> userSvcs = btd.getRD().getUca().getUserSvcBySvcId(SMS_SERVICE);
        
        if (userSvcs.size() <= 0)
        {
            SvcTradeData svcTradeParam = new SvcTradeData();
            svcTradeParam.setUserId(userId);
            svcTradeParam.setUserIdA("-1");
            svcTradeParam.setProductId(productId);
            IDataset packageInfo = PkgElemInfoQry.getPackageIdByElementIdAndProductId(SMS_SERVICE, "S", productId);
            if (IDataUtil.isEmpty(packageInfo))
            {
                CSAppException.apperr(ElementException.CRM_ELEMENT_301, SMS_SERVICE);
            }
            svcTradeParam.setPackageId(packageInfo.getData(0).getString("PACKAGE_ID", ""));
            svcTradeParam.setMainTag(packageInfo.getData(0).getString("MAIN_TAG", ""));
            svcTradeParam.setElementId(SMS_SERVICE);
            svcTradeParam.setInstId(SeqMgr.getInstId());
            svcTradeParam.setStartDate(SysDateMgr.getSysTime());
            svcTradeParam.setEndDate(SysDateMgr.END_DATE_FOREVER);
            svcTradeParam.setCampnId("0");
            svcTradeParam.setRemark(btd.getRD().getRemark());
            svcTradeParam.setModifyTag(TRADE_MODIFY_TAG.Add.getValue());
            btd.add(serialNumber, svcTradeParam);// 加入btd
        }
    }

}
