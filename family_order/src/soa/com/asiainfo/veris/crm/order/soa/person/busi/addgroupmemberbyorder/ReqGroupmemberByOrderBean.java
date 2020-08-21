package com.asiainfo.veris.crm.order.soa.person.busi.addgroupmemberbyorder;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class ReqGroupmemberByOrderBean extends CSBizBean
{
    public IDataset queryGrpBooktInfos(IData inparams, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_ADDRESSBOOK_MEMBER", "SEL_GROUPBOOKS", inparams,pagination);
    }
  	
  	public void submitCancel(IData data) throws Exception{
  		Dao.executeUpdateByCodeCode("TF_F_ADDRESSBOOK_MEMBER", "UPDATE_GROUPBOOKS_BY_USERID", data);
  	
    }
  	
  	public IDataset queryFileInfos(IData inparams) throws Exception
    {
        return Dao.qryByCodeParser("WD_F_FTPFILE", "SEL_BY_FILE_ID", inparams,Route.CONN_CRM_CEN);
    }
  	
  	public void updateUpload(IData data) throws Exception{
  		Dao.executeUpdateByCodeCode("TF_F_ADDRESSBOOK_MEMBER", "UPDATE_FILED_BY_USERID", data);
  	
    }
}

