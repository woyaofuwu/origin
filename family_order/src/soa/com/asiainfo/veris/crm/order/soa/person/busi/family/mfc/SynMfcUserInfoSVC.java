package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import com.ailk.common.data.IData;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class SynMfcUserInfoSVC extends CSBizService
{

	private static final long serialVersionUID = -8359798408142522304L;

	/**
     * 3.1跨省家庭网同步接口
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData synUser(IData inParam)throws Exception
    {
        SynMfcUserInfoBean bean = BeanManager.createBean(SynMfcUserInfoBean.class);
        IData resultDataSet = bean.synUser(inParam);
        return resultDataSet;
    }
    
}
