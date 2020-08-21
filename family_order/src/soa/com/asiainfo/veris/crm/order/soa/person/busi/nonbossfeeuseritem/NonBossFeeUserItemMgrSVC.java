/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfeeuseritem;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.NonBossFeeUserItemMgrQry;
import com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee.NonBossFeeItemMgrBean; 

/**
 * @CREATED by cxy@2014-11-20
 */
public class NonBossFeeUserItemMgrSVC extends CSBizService
{

	static Logger logger = Logger.getLogger(NonBossFeeUserItemMgrSVC.class); 	
    private static final long serialVersionUID = 1838322381138748859L;

    /**
     * 根据条件查询费用项目
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     * @CREATED by cxy@2014-11-20
     */
    public IDataset getNonBossFeeUserItems(IData inparams) throws Exception
    {
    	 
        IDataset rtnSet= NonBossFeeUserItemMgrQry.queryNonBossFeeUserItems(inparams);
        String excTag=inparams.getString("EXC_TAG","");
        if("1".equals(excTag)){
        	for(int k=0;k<rtnSet.size();k++){
        		String paramName=rtnSet.getData(k).getString("PARAM_NAME");
        		String excParamName=StaticUtil.getStaticValue("PAY_USER_NAME", paramName);
        		rtnSet.getData(k).put("EXC_PARAM_NAME",excParamName);
        	}
        }
        return rtnSet;
    }
    
    /**
     * 初始化查询付款单位全称
     * 
     * @param inparams
     * @return IDataset
     * @throws Exception
     * @CREATED by cxy@2014-11-20
     */
    public IDataset getCompanyName(IData inparams) throws Exception
    { 
    	return NonBossFeeUserItemMgrQry.getCompanyName(inparams);
    }
    
    /**
     * @param dataset
     * @throws Exception
     * @CREATE BY CHENXY@2014-11-21
     */
    public void nonBossFeeUserItemsMgr(IData inparams) throws Exception
    {
    	//获取从前台提交的列表数据
        IDataset dataset = new DatasetList(inparams.getString("ITEM_DATASET"));
        NonBossFeeItemMgrBean bean = BeanManager.createBean(NonBossFeeItemMgrBean.class); 

        if (dataset != null && dataset.size() > 0)
        {
            for (Iterator it = dataset.iterator(); it.hasNext();)
            {
                IData data = (IData) it.next();
                // 动态表格必须字段，区别提交数据的操作行为：(0：新增 1：删除 2:修改)
                // 信息管理界面只支持新增和删除两种操作
                String xTag = data.getString("tag");
                //或者代码转中文 
                // 新增
                if ("0".equals(xTag))
                {
                    IData insertData = new DataMap(); 
                    insertData.put("TYPE_ID", "PAY_USER_NAME_ALL"); 
                    insertData.put("DATA_NAME", data.getString("DATA_NAME", "")); 
                    insertData.put("PARAM_NAME", data.getString("PARAM_NAME_CODE", ""));
                    insertData.put("PARA_CODE1", data.getString("PARA_CODE1", ""));
                    insertData.put("PARA_CODE2", data.getString("PARA_CODE2", ""));
                    insertData.put("PARA_CODE3", data.getString("PARA_CODE3", ""));
                    insertData.put("PARA_CODE4", data.getString("PARA_CODE4", ""));
                    insertData.put("START_DATE", data.getString("START_DATE", ""));
                    insertData.put("END_DATE", data.getString("END_DATE", ""));
                    insertData.put("REMARK", data.getString("REMARK", ""));
                    //获取操作员与操作员所在部门
                    insertData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    insertData.put("UPDATE_DEPART_ID", getVisit().getDepartId()); 
                    
                    
                    bean.insertNonBossFeeItem(insertData);
                }
                // 删除
                else if ("1".equals(xTag))
                {
                    IData deleteData = new DataMap();
                    String dataId = data.getString("DATA_ID", "");
                    if (dataId == null || dataId.trim().length() == 0)
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_454);
                    }
                    deleteData.put("TYPE_ID", "PAY_USER_NAME_ALL"); 
                    deleteData.put("DATA_ID", dataId);
                    bean.deleteNonBossFeeItem(deleteData);
                }
                // 修改
                else if ("2".equals(xTag))
                {
                    IData updata = new DataMap(); 
                    updata.put("TYPE_ID", "PAY_USER_NAME_ALL"); 
                    updata.put("DATA_ID_OLD", data.getString("DATA_ID", ""));
                    updata.put("DATA_NAME", data.getString("DATA_NAME", "")); 
                    updata.put("PARAM_NAME", data.getString("PARAM_NAME_CODE", ""));
                    updata.put("PARA_CODE1", data.getString("PARA_CODE1", ""));
                    updata.put("PARA_CODE2", data.getString("PARA_CODE2", ""));
                    updata.put("PARA_CODE3", data.getString("PARA_CODE3", ""));
                    updata.put("PARA_CODE4", data.getString("PARA_CODE4", ""));
                    updata.put("START_DATE", data.getString("START_DATE", ""));
                    updata.put("END_DATE", data.getString("END_DATE", ""));
                    updata.put("REMARK", data.getString("REMARK", ""));
                    //获取操作员与操作员所在部门
                    updata.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    updata.put("UPDATE_DEPART_ID", getVisit().getDepartId());  
                    
                    bean.updateNonBossFeeItem(updata);
                }
                else
                {
                    CSAppException.apperr(ParamException.CRM_PARAM_455, xTag);
                }
            }

        }
        else
        {
            // common.error("没有可以操作的数据！");
            CSAppException.apperr(ParamException.CRM_PARAM_456);
        }

    }

}
