package com.asiainfo.veris.crm.order.soa.person.busi.ivrmail;


import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MailCustManageSVC extends CSBizService {
	
	private static final long serialVersionUID = 1L;

	public IDataset queryList(IData param) throws Exception{
		
		
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		IDataset list = bean.queryList(param,getPagination());
		for(int i = 0; i < list.size(); i++){
			list.getData(i).put("SECTION_NAME", StaticUtil.getStaticValue("IVR_139MAIL_CUST_SECTIONLIST", list.getData(i).getString("SECTION_ID", "")));
		}
		
		return list;
	}
	
	public IDataset addRecordList(IData param) throws Exception{
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		
		String staffid = getVisit().getStaffId();
		
		IDataset section_list = StaticUtil.getStaticList("IVR_139MAIL_CUST_SECTIONLIST");
		
		IDataset rec_list = new DatasetList();
		for( int i = 0 ;i < section_list.size(); i++){
			IData rec = new DataMap();
			IData section = section_list.getData(i);
			String section_id = section.getString("DATA_ID", "");
			if( !"".equals(section_id) ){
				rec.putAll(param);
				rec.put("SECTION_ID", section_id);
				rec.put("CREATE_STAFFID", staffid);
				rec.put("CREATE_DATE", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
				rec_list.add(rec);
			}
		}
		
		bean.delRecordList(rec_list);
		bean.addRecordList(rec_list);
		
		
		IData result_info = new DataMap();
		
		result_info.put("X_RESULTCODE", 0);
		IDataset r = new DatasetList();
		r.add(result_info);
		return r;
	}
	
	public IDataset addRecord(IData param) throws Exception{
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		String staffid = getVisit().getStaffId();
		param.put("CREATE_STAFFID", staffid);
		param.put("CREATE_DATE", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
		
		boolean result = bean.addRecord(param);
		IData result_info = new DataMap();
		if( result ){
			result_info.put("X_RESULTCODE", 0);
		}else{
			result_info.put("X_RESULTCODE", -1);
			result_info.put("X_RESULTINFO", "插入失败，已经存在该记录");
		}
		
		IDataset r = new DatasetList();
		r.add(result_info);
		return r;
	}
	
	public IDataset updateRecord(IData param) throws Exception{
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		String staffid = getVisit().getStaffId();
		param.put("UPDATE_STAFFID", staffid);
		param.put("UPDATE_DATE", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
		param.put("OPERATOR_TYPE", "MODIFY");
		
		boolean result = bean.updateRecord(param);
		IData result_info = new DataMap();
		if( result ){
			result_info.put("X_RESULTCODE", 0);
		}else{
			result_info.put("X_RESULTCODE", -1);
			result_info.put("X_RESULTINFO", "更新失败，无此记录");
		}
		
		IDataset r = new DatasetList();
		r.add(result_info);
		return r;
	}
	
	public IDataset delRecord(IData param) throws Exception{
		
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		String sectionid = param.getString("SECTION_ID", "");
		boolean result = false;
		IData result_info = new DataMap();
		
		String serial_number = param.getString("SERIAL_NUMBER", "");
		if( !"".equals(serial_number) ){
			if( "".equals(sectionid) ){
				result = bean.delAllSectionRecord(param);
			}else{
				result = bean.delRecord(param);
			}
			
			if( result ){
				result_info.put("X_RESULTCODE", 0);
			}else{
				result_info.put("X_RESULTCODE", -1);
				result_info.put("X_RESULTINFO", "删除失败");
			}
		}else{
			result_info.put("X_RESULTCODE", -2);
			result_info.put("X_RESULTINFO", "客户号码不能为空");
		}

		IDataset r = new DatasetList();
		r.add(result_info);
		return r;
		
	}
	
	public IDataset querySendLog(IData param) throws Exception{
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		IDataset list = bean.querySendLog(param,getPagination());
		for(int i = 0; i < list.size(); i++){
			list.getData(i).put("SECTION_NAME", StaticUtil.getStaticValue("IVR_139MAIL_CUST_SECTIONLIST", list.getData(i).getString("SECTION_ID", "")));
		}
		return list;
	}
	
	public IDataset delAllRecord(IData param) throws Exception{
		MailCustManageBean bean = BeanManager.createBean(MailCustManageBean.class);
		int flag = bean.deleteAllRecord(param);
		IData result = new DataMap();
		if(flag == 0){
			result.put("X_RESULTCODE", 0);
			result.put("X_RESULTINFO", "删除成功");
		}else{
			result.put("X_RESULTCODE", 0);
			result.put("X_RESULTINFO", "删除失败");
		}
		IDataset r = new DatasetList();
		r.add(result);
		return r;
	}
}
