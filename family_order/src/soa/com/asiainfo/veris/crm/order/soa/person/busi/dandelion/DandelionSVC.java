
package com.asiainfo.veris.crm.order.soa.person.busi.dandelion;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class DandelionSVC extends CSBizService
{

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = -1756769134328846009L;

    public IDataset dandelionRecvDeal(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");// 回复号码（被推荐号码）
        IDataUtil.chkParam(data, "FORCE_OBJECT");// 推荐短信下发码
        IDataUtil.chkParam(data, "NOTICE_CONTENT");// YES-办理、NO-不办理
        DandelionBean bean = (DandelionBean) BeanManager.createBean(DandelionBean.class);
        IData result = new DataMap();
        IDataset results = new DatasetList();
        result = bean.dandelionRecvDeal(data);
        result.put("TRADE_ID", data.getString("TRADE_ID", "")); // 推荐业务流水
        result.put("BIZ_NAME", data.getString("BIZ_NAME", "")); // 推荐业务名称，接口调用方使用
        results.add(result);
        return results;
    }

    public IDataset dandelionSendDeal(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");// 推荐号码
        IDataUtil.chkParam(data, "SERIAL_NUMBER_B");// 被推荐号码
        IDataUtil.chkParam(data, "BIZ_TYPE_CODE");// 推荐业务
        DandelionBean bean = (DandelionBean) BeanManager.createBean(DandelionBean.class);
        IData result = new DataMap();
        IDataset results = new DatasetList();
        result = bean.dandelionSendDeal(data);
        result.put("TRADE_ID", data.getString("TRADE_ID", "")); // 推荐业务流水
        result.put("BIZ_NAME", data.getString("BIZ_NAME", "")); // 推荐业务名称，接口调用方使用
        results.add(result);
        return results;
    }

}
