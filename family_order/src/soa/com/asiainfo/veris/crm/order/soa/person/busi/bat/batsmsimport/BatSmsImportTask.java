package com.asiainfo.veris.crm.order.soa.person.busi.bat.batsmsimport;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;

public class BatSmsImportTask extends ImportTaskExecutor{

	@Override
	public IDataset executeImport(IData paramIData, IDataset paramIDataset)
			throws Exception {
		// TODO Auto-generated method stub
		BatSmsImportBean bean = BeanManager.createBean(BatSmsImportBean.class);
		
		String type = paramIData.getString("TYPE","1");
		String group_num = paramIData.getString("GROUP_NUM","");
		String send_object = "10086"+paramIData.getString("SEND_OBJECT","");
		String remark = paramIData.getString("REMARK","");
		//String notice_content = paramIData.getString("NOTICE_CONTENT","");
		String staff_id= getVisit().getStaffId();
		String staff_name= getVisit().getStaffName();
		IDataset userlist = new DatasetList();
		IDataset errlist = new DatasetList();
		
		
		//1、过滤拒收调查短信用户
		for( int i = 0; i < paramIDataset.size(); i++ ){
			IData data = paramIDataset.getData(i);
			boolean isRejectUser = bean.checkIsRejectUser(data);
			if( isRejectUser ){//跳过进行下一个
				data.put("IMPORT_RESULT", false);
				data.put("IMPORT_ERROR", "拒收调查短信用户");
				errlist.add(data);
				continue;
			}
			
			if( "1".equals(type) ){//需要导入目标用户表
				data.put("GROUP_NUM", group_num);
			}
			
			data.put("SEND_OBJECT", send_object);
			data.put("REMARK", remark);
			//data.put("NOTICE_CONTENT", notice_content);
			data.put("STAFF_ID", staff_id);
			data.put("STAFF_NAME", staff_name);
			data.put("DEAL_STATE", 0);
			userlist.add(data);
		}
		
		//2、如果选择了导入目标用户表，插入记录，先删除后插入
		if( "1".equals(type) ){//需要导入目标用户表
			bean.delTargetList(userlist);
			bean.addTargetList(userlist);
		}
		
		//3、插入短信表ucr_uec.ti_o_sms_manual
		bean.insertSmsManual(userlist);
		
		return errlist;
	}

}
