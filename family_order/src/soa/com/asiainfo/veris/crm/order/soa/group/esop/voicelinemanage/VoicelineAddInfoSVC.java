package com.asiainfo.veris.crm.order.soa.group.esop.voicelinemanage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esop.busicheck.BusiCheckVoiceBean;

public class VoicelineAddInfoSVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     * 根据条件查询
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset queryIbsysId(IData inparams) throws Exception
    {
    	  return VoicelineAddInfoBean.queryIbsysId(inparams);
    }
	
}
