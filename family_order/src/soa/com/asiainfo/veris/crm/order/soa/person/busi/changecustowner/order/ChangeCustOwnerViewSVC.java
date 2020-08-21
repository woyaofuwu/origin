package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order;

import com.ailk.bizservice.base.CSBizService;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class ChangeCustOwnerViewSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;
    
    
    /**
     * 获取用户所有相关信息
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryAllUserInfo(IData input)throws Exception{
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	return UserInfoQry.getAllUserInfoBySn(serialNumber);
    }
    
    /**
     * 提交前的校验规则
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkBefore(IData input)throws Exception{
    	
    	ChangeCustOwnerViewBean bean=BeanManager.createBean(ChangeCustOwnerViewBean.class);
    	
    	IDataset dataset=new DatasetList();
    	dataset.add(bean.checkBefore(input));
    	
        return dataset;
    }    
}
