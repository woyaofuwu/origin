package com.asiainfo.veris.crm.order.soa.group.mobilecloudinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.group.esop.query.OpTaskInfoQry;

public class MobileCloudInfoSVC extends CSBizService {

	private static final long serialVersionUID = 1L;

	public IDataset selMobileCloudInfoByConditionByPagination(IData param) throws Exception {
		MobileCloudInfoBean mobileCloudInfoBean = new MobileCloudInfoBean();
        return mobileCloudInfoBean.selMobileCloudInfoByCondition(param, getPagination());
    }
	public IDataset selMobileCloudInfoByCondition(IData param) throws Exception {
		MobileCloudInfoBean mobileCloudInfoBean = new MobileCloudInfoBean();
        return mobileCloudInfoBean.selMobileCloudInfoByCondition(param);
    }
	public IDataset updMobileCloudInfo(IData param) throws Exception{
		IDataset mobileCloudList=param.getDataset("MobileCloudList");
		if(IDataUtil.isNotEmpty(mobileCloudList)){
			int[] infoListFlag=MobileCloudInfoBean.updMobileCloudInfo(mobileCloudList);
			System.out.println("MobileCloudInfoSVC-updMobileCloudInfo-infoListFlag:"+infoListFlag);
			for(int j:infoListFlag){
				if(j==0){
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "批量更新TF_F_INFO_INSTANCE时异常，已自动停止");
				}
			}
		}
		return null;
		
	}

}
