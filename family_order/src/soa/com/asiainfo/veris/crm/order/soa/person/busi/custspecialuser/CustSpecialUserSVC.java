package com.asiainfo.veris.crm.order.soa.person.busi.custspecialuser;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class CustSpecialUserSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public IDataset queryList(IData param) throws Exception{
		
		CustSpecialUserBean bean = BeanManager.createBean(CustSpecialUserBean.class);
		
		IDataset list = bean.queryList(param,getPagination());
		return list;
	}
	
	public IDataset addRecord(IData param) throws Exception{
		
		CustSpecialUserBean bean = BeanManager.createBean(CustSpecialUserBean.class);
		String staffid = getVisit().getStaffId();
		param.put("UPDATE_STAFF_ID", staffid);
		param.put("UPDATE_TIME", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
		String flag = param.getString("FLAG", "");
		if( "".equals(flag) ){
			flag = "0";
		}
		param.put("FLAG", Integer.parseInt(flag));
		
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
		
		CustSpecialUserBean bean = BeanManager.createBean(CustSpecialUserBean.class);
		String staffid = getVisit().getStaffId();
		param.put("UPDATE_STAFF_ID", staffid);
		param.put("UPDATE_TIME", TimeUtil.getSysDate("yyyy-MM-dd HH:mm:ss"));
		String flag = param.getString("FLAG", "");
		if( "".equals(flag) ){
			flag = "0";
		}
		param.put("FLAG", Integer.parseInt(flag));
		
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
		
		CustSpecialUserBean bean = BeanManager.createBean(CustSpecialUserBean.class);
		
		IData result_info = new DataMap();
		
		String serial_number = param.getString("SERIAL_NUMBER", "");
		String type = param.getString("TYPE", "");
		
		if( !"".equals(serial_number) && !"".equals(type) ){
			boolean result = bean.delRecord(param);
			
			if( result ){
				result_info.put("X_RESULTCODE", 0);
			}else{
				result_info.put("X_RESULTCODE", -1);
				result_info.put("X_RESULTINFO", "删除失败");
			}
		}else{
			result_info.put("X_RESULTCODE", -2);
			result_info.put("X_RESULTINFO", "客户号码和类别不能为空");
		}

		IDataset r = new DatasetList();
		r.add(result_info);
		return r;
		
		
	}
}
