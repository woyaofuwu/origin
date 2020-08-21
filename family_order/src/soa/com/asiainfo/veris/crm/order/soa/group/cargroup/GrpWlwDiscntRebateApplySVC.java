
package com.asiainfo.veris.crm.order.soa.group.cargroup;


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class GrpWlwDiscntRebateApplySVC extends CSBizService
{
	private static final Logger logger = Logger.getLogger(GrpWlwDiscntRebateApplySVC.class);

    private static final long serialVersionUID = 1L;
    
    /**
     * 查看折扣信息、审批信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryDiscntApplyInfo(IData input) throws Exception
    {
        return  GrpWlwDiscntRebateApplyBean.queryDiscntApplyInfo(input,getPagination());
    }
    
    /**
     * 添加折扣信息
     * @param input
     * @return
     * @throws Exception
     */
    public IData addDiscntApplyInfo(IData input) throws Exception
    {
    	GrpWlwDiscntRebateApplyBean bean = BeanManager.createBean(GrpWlwDiscntRebateApplyBean.class);
		boolean flag = bean.addDiscntApplyInfo(input);
		
		IData result = new DataMap();
		result.put("RESULT_CODE", flag ? "0000" : "9999");	
		result.put("EC_ID", input.getString("EC_ID",""));
		return result;
    }
    
    
}
