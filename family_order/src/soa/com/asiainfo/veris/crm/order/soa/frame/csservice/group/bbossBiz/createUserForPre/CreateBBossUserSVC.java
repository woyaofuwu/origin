
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUserForPre;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.SendDataToEsopBean;

public class CreateBBossUserSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /*
     * @description 变更预受理转正式受理的台账信息
     * @author xunyl
     * @date 2013-10-19
     */
    public final IDataset saveChgData(IData map) throws Exception
    {
        // 1- 定义返回结果
        IDataset saveResult = new DatasetList();

        // 2- 保存数据
        String result = "受理数据保存成功!";

        // 更新预受理台账信息
        PreUserToBBossUserBean.updatePreTradeInfo(map);

        // 3- 返回处理结果
        saveResult.add(result);
        return saveResult;
    }

    /**
     * chenyi 最后一笔工单发esop
     * 
     * @param map
     * @throws Exception
     */
    public final void sendEsopSLBefore(IData map) throws Exception
    {

        SendDataToEsopBean.sendEsopSLBefore(map);
    }

    /*
     * @description 集团BBOSS业务预受理用户转正式受理
     * @author xunyl
     * @date 2013-10-19
     */
    public final IDataset sendTradeInfoToBboss(IData map) throws Exception
    {
        // 1- 定义返回结果
        IDataset sendResult = new DatasetList();

        // 2- 发送数据
        String result = "受理报文发送成功!";

        SendTradeInfoToBbossBean.sendTradeInfo(map);

        // 3- 返回处理结果
        sendResult.add(result);
        return sendResult;
    }
}
