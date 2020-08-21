
package com.asiainfo.veris.crm.order.web.person.batNetcafeOpenUserImport;

import org.apache.tapestry.IRequestCycle;
  
import com.ailk.common.data.IData; 
import com.ailk.common.data.IDataset;  
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class BatNetcafeOpenUserImport extends PersonBasePage
{ 
    
    /**
     * 网厅开户批量信息导入
     * */
    public void importOpenUserData(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset importResults=CSViewCall.call(this, "SS.PersonCommSVC.importNetOpenUserData", pageData); 
    }
     
    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);
 
}
