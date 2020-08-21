package com.asiainfo.veris.crm.order.soa.person.busi.np.npcheck;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class NpCheckBean extends CSBizBean{
    public IDataset queryImportData(IData data, Pagination pagination) throws Exception
    {    	
    	return Dao.qryByCodeParser("TF_B_NPCHECK", "SEL_BY_CONDS", data, pagination);
    }
    
    public int modifyState(IData data) throws Exception
    {   
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
    	param.put("STATE", "1");
    	
    	return Dao.executeUpdateByCodeCode("TF_B_NPCHECK", "UPD_STATE_BY_SN", param);
    }
    
    public void importBatData(IData input) throws Exception
    {
    	 IDataset set = new DatasetList(); 
         String fileId = input.getString("cond_STICK_LIST");  
         String[] fileIds = fileId.split(",");
         ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
         for (String strfileId : fileIds)
         {
             IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/NpCheckImp.xml"));
             IDataset[] suc = (IDataset[]) array.get("right");
             set.addAll(suc[0]);
         }
         IData inparam = new DataMap();
         String state = "";
         for (int i = 0; i < set.size(); i++)
         {
				inparam.put("SERIAL_NUMBER",set.getData(i).getString("SERIAL_NUMBER") );
				state = set.getData(i).getString("STATE");
				if("已通过审核".equals(state)){
					inparam.put("STATE", "1");
				}else{
					inparam.put("STATE", "2");
				}
				Dao.executeUpdateByCodeCode("TF_B_NPCHECK", "UPD_STATE_BY_SN", inparam);
				inparam.clear();
         }
    }

}
