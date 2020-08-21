
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.synBBossOrderState;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;

public class SynBbossOrderStateSVC extends CSBizService
{
    private static final long serialVersionUID = 960583074078078331L;

    /*
     * @description 工单状态同步落地接口(配合省协助工单处理也走此接口)
     * @author xunyl
     * @date 2013-10-31
     */
    public final IDataset insetBbossOrderState(IData map) throws Exception
    {
        // 1- 获取报文类型
        String busiSign = map.getString("BUSI_SIGN", "");// 报文类型

        // 2- 落地报文为配合省协助工单处理，数据特殊处理
        if (IntfField.SubTransCode.BbossAssistOrderBiz.value.equals(busiSign))
        {
            SynBbossOrderState.dealAssistOrder(map);
        }

        // 3- 获取返回结果
        IDataset result = SynBbossOrderState.insetBbossOrderState(map);

        // 4- 返回结果
        return result;
    }

    /*
     * @description 工单反馈
     * @author xunyl
     * @date 2013-10-31
     */
    public final IDataset rspDealOrderResult(IData map) throws Exception
    {
        return SynBbossOrderState.rspDealOrderResult(map);
    }

}
