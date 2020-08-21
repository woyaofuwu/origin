package com.asiainfo.veris.crm.order.soa.group.esop.tapmarketing;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class TapMarketingSVC extends CSBizService {

	private static final long serialVersionUID = 1L;

	public IDataset selTapMarketingByCondition(IData param) throws Exception {
		TapMarketingBean tapMarketingBean = new TapMarketingBean();
        return tapMarketingBean.selTapMarketingByCondition(param, getPagination());
    }
	public IDataset selTapMarketingByConditionAll(IData param) throws Exception {
		TapMarketingBean tapMarketingBean = new TapMarketingBean();
        return tapMarketingBean.selTapMarketingByCondition(param);
    }
	
	public IDataset selTapMarketingByIbsysidMarketing(IData param) throws Exception {
		return TapMarketingBean.selTapMarketingByIbsysidMarketing(param,getPagination());
    }
	
    public static void updateTapMarketingByIbsysidMarketing(IData param) throws Exception
    {
		TapMarketingBean.updateTapMarketingByIbsysidMarketing(param);
    }
    
    public static void updateTapMarketingByIbsysidMarketingAudit(IData param) throws Exception
    {
		TapMarketingBean.updateTapMarketingByIbsysidMarketingAudit(param);
    }
}
