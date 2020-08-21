
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class BatDealBeanSvc extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * BBOSS成员批量接口调用
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset startBatDealBBOSSOther(IData inData) throws Exception
    {
        String batchId = inData.getString("BATCH_ID");
        String batchOperType = inData.getString("BATCH_OPER_TYPE");
        BatDealBean dealBean = new BatDealBean();
        dealBean.startBatDealBBOSSOther(batchId, batchOperType);

        IDataset dataset = new DatasetList();
        return dataset;
    }

    /**
     * BBOSS行业应用卡批量接口调用
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset startBBossHYYYKBatDeals(IData inData) throws Exception
    {
        IDataset dataset = new DatasetList();

        BatDealBean dealBean = new BatDealBean();
        dataset = dealBean.startBBossHYYYKBatDeals(inData);

        return dataset;
    }

    /**
     * BBOSS一点支付批量接口调用
     * 
     * @param inData
     * @return
     * @throws Exception
     */
    public IDataset startBBossYDZFBatDeals(IData inData) throws Exception
    {
        IDataset dataset = new DatasetList();

        BatDealBean dealBean = new BatDealBean();
        dataset = dealBean.startBBossYDZFBatDeals(inData);

        return dataset;
    }

    public IDataset queryOfferComChaByCond(IData inData) throws Exception{
    	String offerType = inData.getString("OFFER_TYPE","");
    	String offerCode = inData.getString("OFFER_CODE","");
    	String fieldName = inData.getString("FIELD_NAME","");
    	return UpcCall.queryOfferComChaByCond(offerType, offerCode, fieldName);
    }
}
