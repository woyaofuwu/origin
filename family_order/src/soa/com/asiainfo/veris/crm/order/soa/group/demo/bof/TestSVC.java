/**
 * @Title: TestSVC.java
 * @Package com.ailk.groupservice.demo.bof
 * @Description: TODO(用一句话描述该文件做什么)
 * @author A18ccms A18ccms_gmail_com
 * @date Feb 21, 2014 3:12:02 PM
 * @version V1.0
 */

package com.asiainfo.veris.crm.order.soa.group.demo.bof;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class TestSVC extends GroupOrderService
{

    private static final long serialVersionUID = -5944842575961444060L;

    public IDataset testSvc(IData input) throws Exception
    {
        BaseGrpMemberAddReqData baseReqData = (BaseGrpMemberAddReqData) TestSVCProcess.acceptSvc(input);

        IDataset returnSet = new DatasetList();

        IData result = new DataMap();
        result.put("ORDER_ID", "123");
        result.put("DB_SOURCE", "0731");
        result.put("ORDER_TYPE_CODE", baseReqData.getOrder_type_code());
        result.put("TRADE_TYPE_CODE", baseReqData.getTrade_type_code());
        result.put("EFFECT_NOW", baseReqData.isEffectNow());

        returnSet.add(result);

        return returnSet;
    }

    public IDataset tranSvc(IData input) throws Exception
    {
        IDataset returnSet = new DatasetList();

        returnSet.add(input);

        return returnSet;
    }
}
