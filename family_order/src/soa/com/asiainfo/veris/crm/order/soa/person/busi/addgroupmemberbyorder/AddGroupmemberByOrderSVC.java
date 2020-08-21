package com.asiainfo.veris.crm.order.soa.person.busi.addgroupmemberbyorder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class AddGroupmemberByOrderSVC extends CSBizService
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 查询号码信息
	 */
	public IDataset querySubscriber(IData param) throws Exception {
		
		//判断是否异网号码，异网号码不做查询。直接返回异网标志
		String serialNumber = param.getString("SERIAL_NUMBER1");
		AddGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		String mobileFlag = groupmemberInfoQryBean.mobileFlag(serialNumber);
		if("NO".equals(mobileFlag)){//异网号码
			IDataset datasetRtn = new DatasetList();
			IData paramRtn = new DataMap();
			paramRtn.put("MOBILE_FLAG", "2");
			datasetRtn.add(paramRtn);
			return datasetRtn;
		}else{//移动号码
			IDataset results = groupmemberInfoQryBean.querySubscriber(param);
			return results;
		}
	}
	/**
	 * 判断号码是否属于898集团成员
	 */
	public IDataset queryGroup(IData param) throws Exception {
		AddGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		IDataset results = groupmemberInfoQryBean.queryGroup(param);
		return results;
	}
	/**
	 * 系统判断号码是否重复加集团通讯录成员queryMember
	 */
	public IDataset queryMember(IData param) throws Exception {
		AddGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		IDataset results = groupmemberInfoQryBean.queryMember(param);
		return results;
	}
	/**
	 * 判断号码是否属于集团V网成员
	 */
	public IDataset queryGroupVpmn(IData param) throws Exception {
		AddGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		IDataset results = groupmemberInfoQryBean.queryGroupVpmn(param);
		return results;
	}
	/**
	 * 批量导入成员通讯录
	 */
	public IData bookMemberImportOrder(IData input) throws Exception{
		AddGroupmemberByOrderBean bean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		IData idata = bean.bookMemberImport(input);
		return idata;
    }
	/**
	 * 批量导入成员通讯录gtm
	 */
	public IDataset gtmBookMemberImport(IData input) throws Exception{
		AddGroupmemberByOrderBean bean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		IDataset idata = bean.gtmBookMemberImport(input);
		return idata;
    }
	/**
	 * 提交录入数据submitCheck
	 */
	public IData submitCheck(IData param) throws Exception {
		AddGroupmemberByOrderBean groupmemberInfoQryBean = BeanManager.createBean(AddGroupmemberByOrderBean.class);
		IData result = groupmemberInfoQryBean.submitCheck(param);
		return result;
	}
	
}
