package com.asiainfo.veris.crm.order.soa.person.busi.widerealnamesupplement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;

public class WideRealnameSupplementSVC extends CSBizService{
	
	protected static Logger log = Logger.getLogger(WideRealnameSupplementSVC.class);
	
	private static final long serialVersionUID = 1L;
	
    /**
     * 营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset verifyIdCardName(IData input) throws Exception{
        
    	WideRealnameSupplementBean bean = (WideRealnameSupplementBean) BeanManager.createBean(WideRealnameSupplementBean.class);
        IData data = bean.verifyIdCardName(input);
        
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }
    
    /**
     * 获取军人身份证类型
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset psptTypeCodePriv(IData input) throws Exception{
        
    	WideRealnameSupplementBean bean = (WideRealnameSupplementBean) BeanManager.createBean(WideRealnameSupplementBean.class);
        IData data = bean.psptTypeCodePriv(input);
        
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
        
    }
	
	/*
     * 全网一证5号校验
     */
     public IDataset checkGlobalMorePsptId(IData input) throws Exception
        {
    	 WideRealnameSupplementBean bean = (WideRealnameSupplementBean) BeanManager.createBean(WideRealnameSupplementBean.class);
         IDataset dataset = bean.checkGlobalMorePsptId(input);
         return dataset;         
        }
     
     public IDataset verifyEnterpriseCard(IData input) throws Exception{
    	 WideRealnameSupplementBean bean = (WideRealnameSupplementBean) BeanManager.createBean(WideRealnameSupplementBean.class);
         IData data = bean.verifyEnterpriseCard(input);
         IDataset dataset = new DatasetList();
         dataset.add(data);
         return dataset;
     }
     
     public IDataset verifyOrgCard(IData input) throws Exception{
    	 WideRealnameSupplementBean bean = (WideRealnameSupplementBean) BeanManager.createBean(WideRealnameSupplementBean.class);
         IData data = bean.verifyOrgCard(input);
         IDataset dataset = new DatasetList();
         dataset.add(data);
         return dataset;
     }
     
     /**
      * 实名认证客户二代身份证
      * @param input
      * @return
      * @throws Exception
      */
     public IDataset verifyIdCard(IData input) throws Exception{
         
    	 WideRealnameSupplementBean bean = (WideRealnameSupplementBean) BeanManager.createBean(WideRealnameSupplementBean.class);
         IData data = bean.verifyIdCard(input);
         
         IDataset dataset = new DatasetList();
         dataset.add(data);
         return dataset;
     }
     
     public IDataset queryCustInfoBySN(IData input) throws Exception{
    	 
         return CustomerInfoQry.queryCustInfoBySN(input.getString("SERIAL_NUMBER"));
     }     
     /**
      * 查询集团客户信息
      * @author wuwangfeng
      * @param input
      * @return
      */
     public IDataset queryGroupCustInfoBySN(IData input) throws Exception{    	 
         return CustomerInfoQry.queryGroupCustInfoBySN(input.getString("SERIAL_NUMBER"));
     }
     
}