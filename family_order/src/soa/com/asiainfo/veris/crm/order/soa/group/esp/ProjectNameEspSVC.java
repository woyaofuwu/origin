package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.GroupProjectNameSVC;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrSVC;

public class ProjectNameEspSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public void saveProjectName(IData map) throws Exception
    {
		IData input = new DataMap();
		input.put("IBSYSID",  map.getString("IBSYSID"));
		input.put("NODE_ID",  "apply");
		
		IDataset attrInfos = WorkformAttrSVC.qryMaxEopAttrByIbsysidNodeid(input);
		if(IDataUtil.isNotEmpty(attrInfos)) {
			IData input2 = new DataMap();
			for (int i = 0; i < attrInfos.size(); i++) {
				IData attrInfo = attrInfos.getData(i);
	        	String attrCode = attrInfo.getString("ATTR_CODE");
	        	String attrValue = attrInfo.getString("ATTR_VALUE");
	        	if(attrCode.equals("CUST_ID")||attrCode.equals("GROUP_ID")||attrCode.equals("PROJECTNAME")) {
	        		if(attrCode.equals("PROJECTNAME") && attrValue!=null && attrValue!="") {
	        			input2.put("PROJECT_NAME", attrValue);
	        		}else if(attrCode.equals("GROUP_ID")){
	        			input2.put("GROUP_ID", attrValue);
	        		}else if(attrCode.equals("CUST_ID")) {
	        			input2.put("CUST_ID", attrValue);
	        		}
	        	}
			}
			Boolean flag = true;
			IDataset projectInfos = GroupProjectNameSVC.qryProjectName(input2);
			for (int i = 0; i < projectInfos.size(); i++) {
				IData projectInfo = projectInfos.getData(i);
				String projectName = projectInfo.getString("PROJECT_NAME");
				if(input2.containsKey("PROJECT_NAME") && input2.getString("PROJECT_NAME").equals(projectName)) {
					flag = false;
					break;
				}
			}

			if(input2.containsKey("PROJECT_NAME") && flag) {
				GroupProjectNameSVC.insertProjectName(input2);
			}
		}
    }
}
