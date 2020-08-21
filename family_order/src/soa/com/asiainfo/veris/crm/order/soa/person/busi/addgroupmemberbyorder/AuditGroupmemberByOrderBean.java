package com.asiainfo.veris.crm.order.soa.person.busi.addgroupmemberbyorder;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class AuditGroupmemberByOrderBean extends CSBizBean
{
    public IDataset queryGrps(IData inparams, Pagination pagination) throws Exception
    {
        return Dao.qryByCodeParser("TF_F_ADDRESSBOOK_MEMBER", "SEL_GROUPS", inparams,pagination);
    }
  	
  	public IDataset queryGrpBooktInfos(IData data) throws Exception{
  		return Dao.qryByCodeParser("TF_F_ADDRESSBOOK_MEMBER", "SEL_ALLMEMBER_BY_GROUPID", data);
    }
  	public void submitAudit(IData data) throws Exception{
        Dao.insert("TF_F_ADDRESSBOOK_AUDIT", data);
    }
  	
  	public void updateMember(IData data) throws Exception{
  		Dao.executeUpdateByCodeCode("TF_F_ADDRESSBOOK_MEMBER", "UPDATE_STATE_BY_USERID", data);
    }
  	
}

