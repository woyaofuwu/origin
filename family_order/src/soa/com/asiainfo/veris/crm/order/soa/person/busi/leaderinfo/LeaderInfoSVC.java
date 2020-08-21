package com.asiainfo.veris.crm.order.soa.person.busi.leaderinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class LeaderInfoSVC extends CSBizService
{
    
    public IDataset queryLeaderInfo(IData input) throws Exception
    {
    	
    	LeaderInfoBean bean = (LeaderInfoBean) BeanManager.createBean(LeaderInfoBean.class);
        return bean.queryLeaderInfo(input, getPagination());
    }
    
    /**
     * 新增
     */
    public IData insertLeaderInfo(IData input) throws Exception
    {
    	IData retData = new DataMap();
    	retData.put("X_RESULTCODE", "0");
		retData.put("X_RESULTINFO", "");
		
		
    	LeaderInfoBean bean = (LeaderInfoBean) BeanManager.createBean(LeaderInfoBean.class);
    	
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
    	IDataset results = bean.queryLeaderInfo(param, getPagination());
    	if(IDataUtil.isNotEmpty(results)){
    		CSAppException.apperr(FamilyException.CRM_FAMILY_6985);
    	}
    	
    	input.put("STATE", 0);//初始化，0=有效
    	bean.insertLeaderInfo(input);
    	
        return retData;
    }
    
    
    /**
     * 更新
     * @param input
     * @return
     * @throws Exception
     */
    public IData updateLeaderInfo(IData input) throws Exception
    {
    	IData retData = new DataMap();
    	retData.put("X_RESULTCODE", "0");
		retData.put("X_RESULTINFO", "");
    	LeaderInfoBean bean = (LeaderInfoBean) BeanManager.createBean(LeaderInfoBean.class);
    	bean.updateLeaderInfo(input);
        return retData;
    }
    
    /**
     * 删除，实际上是修改state字段值，0=有效，1=删除
     */
    public IData deleteLeaderInfo(IData input) throws Exception
    {
    	IData retData = new DataMap();
    	retData.put("X_RESULTCODE", "0");
		retData.put("X_RESULTINFO", "");
    	LeaderInfoBean bean = (LeaderInfoBean) BeanManager.createBean(LeaderInfoBean.class);
    	bean.deleteLeaderInfo(input);
        return retData;
    }
    
    /**
     * 号码校验 & 权限判断
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY mengqx@2018-6-26
     */
    public IData checkBySerialNumberAndPermission(IData input) throws Exception
    {
    	IData retData = new DataMap();
    	retData.put("X_RESULTCODE", "-1");
		retData.put("X_RESULTINFO", "");
    	LeaderInfoBean bean = BeanManager.createBean(LeaderInfoBean.class);
    	IDataset sets = bean.checkBySerialNumberAndPermission(input);
    	if(IDataUtil.isNotEmpty(sets)){
    		String staffId = getVisit().getStaffId();
    		boolean hasLimit = StaffPrivUtil.isFuncDataPriv(staffId, "PRIV_TF_LEADER");
    		if(hasLimit){
    			retData.put("X_RESULTCODE", "0");
    		}
        }
        return retData;
    }
}
