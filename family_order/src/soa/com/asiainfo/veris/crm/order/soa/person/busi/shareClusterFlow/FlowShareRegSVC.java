package com.asiainfo.veris.crm.order.soa.person.busi.shareClusterFlow;
		
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.OrderService;

public class FlowShareRegSVC extends CSBizService
{
    

    /**
     * 流量共享接口服务
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData flowShareReg(IData data) throws Exception
    {
    	FlowShareRegBean bean = BeanManager.createBean(FlowShareRegBean.class);
        return bean.flowShareReg(data);
    }

    // 查询接口
    public IData queryFlowShare(IData data) throws Exception
    {
    	FlowShareRegBean bean = BeanManager.createBean(FlowShareRegBean.class);
        return bean.queryFlowShare(data);
    }
    public void setTrans(IData idata) throws Exception{
    	idata.put("SERIAL_NUMBER", idata.getString("IDVALUE"));
    }
}
