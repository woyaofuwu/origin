
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean.BatDealBBossBATMEBCENPAYBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean.BatDealBBossHYYYKBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean.BatDealBBossMebBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bat.bean.BatDealBBossYDZFBean;

/**
 * BBOSS正向批量入口，目前使用该批量的业务包括（2014-06-25） 集团BBOSS成员批量新增（BATADDBBOSSMEMBER） 集团BBOSS成员批量变更（BATMODBBOSSMEMBER）
 * 集团BBOSS成员批量暂停（BATPASBBOSSMEMBER） 集团BBOSS成员批量恢复（BATCONBBOSSMEMBER） 集团BBOSS成员批量注销（BATDELBBOSSMEMBER）
 * 主办省上传成员[行业应用卡]（BATADDHYYYKMEM） 主办省上传成员[一点支付]（BATADDYDZFMEM） 配合省反馈成员确认结果[一点支付]（BATCONFIRMYDZFMEM）
 * 配合省反馈成员开通结果[一点支付]（BATOPENYDZFMEM） 配合省反馈成员开通结果[行业应用卡]（BATOPENHYYYKMEM）
 * 
 * @author fanti3
 */

public class BatDealBBossBeanSvc extends CSBizService
{

    private static final long serialVersionUID = 1L;

    /**
     * BBOSS成员批量，包括批量新增、变更、暂停、恢复、注销
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset startBatDealBBossMember(IData inData) throws Exception
    {
        // 1- 获取批量批次号和批量业务类型
        String batchId = inData.getString("BATCH_ID");
        String batchOperType = inData.getString("BATCH_OPER_TYPE");

        // 2- 批量启动调用bean层处理类
        BatDealBBossMebBean dealbean = new BatDealBBossMebBean();
        dealbean.startBatDealBBossMember(batchId, batchOperType);

        IDataset dataset = new DatasetList();
        return dataset;
    }

    /**
     * BBOSS行业应用卡批量启动
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset startBBossHYYYKBatDeals(IData inData) throws Exception
    {
        IDataset dataset = new DatasetList();

        String batchOperType = inData.getString("BATCH_OPER_TYPE", "");
        String batchId = inData.getString("BATCH_ID");

        BatDealBBossHYYYKBean dealBean = new BatDealBBossHYYYKBean();
        dataset = dealBean.startBBossHYYYKBatDeals(batchId, batchOperType);

        return dataset;
    }

    /**
     * @Function:
     * @Description:流量叠加包批量启动
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 2014-8-18
     */
    public IDataset startBBossMEBCENPAYBatDeals(IData inData) throws Exception
    {
        IDataset dataset = new DatasetList();

        String batchOperType = inData.getString("BATCH_OPER_TYPE", "");
        String batchId = inData.getString("BATCH_ID");

        BatDealBBossBATMEBCENPAYBean dealBean = new BatDealBBossBATMEBCENPAYBean();
        dataset = dealBean.startBBossCenPayOpenBatDeals(batchId, batchOperType);

        return dataset;
    }

    /**
     * BBOSS一点支付批量启动
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset startBBossYDZFBatDeals(IData inData) throws Exception
    {
        IDataset dataset = new DatasetList();

        String batchOperType = inData.getString("BATCH_OPER_TYPE", "");
        String batchId = inData.getString("BATCH_ID");

        BatDealBBossYDZFBean dealBean = new BatDealBBossYDZFBean();
        dataset = dealBean.startBBossYDZFBatDeals(batchId, batchOperType);

        return dataset;
    }
}
