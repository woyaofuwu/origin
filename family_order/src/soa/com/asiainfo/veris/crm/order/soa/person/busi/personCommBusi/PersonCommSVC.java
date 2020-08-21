package com.asiainfo.veris.crm.order.soa.person.busi.personCommBusi;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;  
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService; 

/** 
 * 通用的app后台类。
 * 一些零散的业务需要查数据库的都可以丢这里。省得次次都加文件夹加文件。
 */
public class PersonCommSVC extends CSBizService
{
  
    /**
     *批量导入网厅开户信息
     */
    public IDataset importNetOpenUserData(IData input) throws Exception
    {
    	return PersonCommBean.importNetOpenUserData(input);
    }
}