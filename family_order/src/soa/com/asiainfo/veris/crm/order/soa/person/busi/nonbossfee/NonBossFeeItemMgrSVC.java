/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.nonbossfee;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.NonBossFeeUserItemMgrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

/**
 * @CREATED by gongp@2014-4-24 修改历史 Revision 2014-4-24 上午09:28:04
 * @chenxy3@2015-2-11 修改 
 */
public class NonBossFeeItemMgrSVC extends CSBizService
{

    private static final long serialVersionUID = 1838322381138748859L;
    
    
    
    /**
     * 根据条件查询代码项下拉列表
     * @CREATE BY CHENXY3@2015-2-9
     */
    public IDataset getListType(IData inparams) throws Exception
    {

    	return NonBossFeeUserItemMgrQry.getDicLists(inparams);
    }
    /**
     * 根据条件查询费用项目
     * 
     * @param inparams
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-4-24
     */
    public IDataset getNonBossFeeItems(IData inparams) throws Exception
    {

    	return NonBossFeeUserItemMgrQry.getFeeLists(inparams);
    }
    
    /**
     * 界面新增的代码项参数，录入只有中文，需要往TD_S_NONBOSSPARA表插入新参数值，并且返回其新的DATA_ID
     *  
     * @CREATE BY chenxy3@2015-2-11
     */
    public String insNewsCommpara(IData inparams) throws Exception
    {
    	String new_data_id="";
    	//获取最大的DATA_ID并+1作为新ID
    	IDataset idset = NonBossFeeUserItemMgrQry.getParamMaxSequence(inparams);
    	if(idset!=null&&idset.size()>0){
    		new_data_id=idset.getData(0).getString("MAX_ID");
    		//new_data_id=""+(Integer.parseInt(new_data_id)+1);
    	}
    	//插入新数据
    	inparams.put("DATA_ID", new_data_id);
    	inparams.put("START_DATE", SysDateMgr.getSysDate());
    	inparams.put("END_DATE", "2050-12-31");
    	inparams.put("REMARK", "界面新增");
    	inparams.put("UPDATE_STAFF_ID", getVisit().getStaffId());
    	inparams.put("UPDATE_DEPART_ID", getVisit().getDepartId());
    	NonBossFeeItemMgrBean bean = BeanManager.createBean(NonBossFeeItemMgrBean.class);
    	bean.insertNonBossFeeItem(inparams);
    	
    	return new_data_id;
    }

    /** 
     * 提交数据
     * @change edit CHENXY3@2015-2-11
     */
    public void nonBossFeeItemsMgr(IData inparams) throws Exception
    {
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
                // 新增
                if ("0".equals(xTag))
                {
                    IData insertData = new DataMap();
                    insertData.put("TYPE_ID", "NONBOSS_FEE_ITEM");
                    insertData.put("DATA_ID",data.getString("DATA_ID",""));
                    insertData.put("DATA_NAME",data.getString("DATA_NAME",""));
                    if(data.getString("PARAM_CODE","")!=null&&!"".equals(data.getString("PARAM_CODE",""))){
                    	insertData.put("PARAM_NAME",data.getString("PARAM_CODE",""));//要取代码值
                    }else{
                    	IData newParas=new DataMap();
                    	newParas.put("TYPE_ID", "INCOME_TYPE");
                    	newParas.put("DATA_NAME",data.getString("PARAM_NAME",""));
                    	String newDataId=this.insNewsCommpara(newParas); 
                    	insertData.put("PARAM_NAME",newDataId);                        
                    }
                    
                    if(data.getString("PARA_CODE1","")!=null&&!"".equals(data.getString("PARA_CODE1",""))){
                    	insertData.put("PARA_CODE1",data.getString("PARA_CODE1",""));
                    }else{
                    	IData newParas=new DataMap();
                    	newParas.put("TYPE_ID", "ADDED_TAX_TYPE");
                    	newParas.put("DATA_NAME",data.getString("PARA_CODE1_NAME",""));
                    	String newDataId=this.insNewsCommpara(newParas); 
                    	insertData.put("PARA_CODE1",newDataId);                        
                    }
                    
                    insertData.put("PARA_CODE2",data.getString("PARA_CODE2",""));
                    if(data.getString("PARA_CODE3","")!=null&&!"".equals(data.getString("PARA_CODE3",""))){
                    	insertData.put("PARA_CODE3",data.getString("PARA_CODE3",""));
                    }else{
                    	IData newParas=new DataMap();
                    	newParas.put("TYPE_ID", "INVOICE_TYPE");
                    	newParas.put("DATA_NAME",data.getString("PARA_CODE3_NAME",""));
                    	String newDataId=this.insNewsCommpara(newParas); 
                    	insertData.put("PARA_CODE3",newDataId);                        
                    }
                    
                    //chen20180211
                    if(data.getString("PARA_CODE10","")!=null&&!"".equals(data.getString("PARA_CODE10",""))){
                    	insertData.put("PARA_CODE10",data.getString("PARA_CODE10",""));
                    }else{
                    	IData newParas=new DataMap();
                    	newParas.put("TYPE_ID", "MARKET_TYPE");
                    	newParas.put("DATA_NAME",data.getString("PARA_CODE10_NAME",""));
                    	String newDataId=this.insNewsCommpara(newParas); 
                    	insertData.put("PARA_CODE10",newDataId);                        
                    }
                    
                    insertData.put("PARA_CODE4",data.getString("PARA_CODE4","无"));
                    insertData.put("PARA_CODE5",data.getString("PARA_CODE5","无"));
                    insertData.put("PARA_CODE6",data.getString("PARA_CODE6","无"));
                    insertData.put("PARA_CODE7",data.getString("PARA_CODE7","无"));
                    insertData.put("PARA_CODE8",data.getString("PARA_CODE8","无"));
                    insertData.put("PARA_CODE9",data.getString("PARA_CODE9","无"));
                    //insertData.put("PARA_CODE10",data.getString("PARA_CODE10",""));
                    insertData.put("PARA_CODE11",data.getString("PARA_CODE11",""));
                    insertData.put("PARA_CODE12",data.getString("PARA_CODE12",""));
                    insertData.put("PARA_CODE13",data.getString("PARA_CODE13",""));
                    insertData.put("PARA_CODE14",data.getString("PARA_CODE14",""));
                    insertData.put("PARA_CODE15",data.getString("PARA_CODE15",""));
                    insertData.put("PARA_CODE16",data.getString("PARA_CODE16",""));
                    insertData.put("PARA_CODE17",data.getString("PARA_CODE17",""));
                    insertData.put("PARA_CODE18",data.getString("PARA_CODE18",""));
                    insertData.put("PARA_CODE19",data.getString("PARA_CODE19",""));
                    insertData.put("PARA_CODE20",data.getString("PARA_CODE20",""));
                    insertData.put("PARA_CODE21",data.getString("PARA_CODE21",""));
                    insertData.put("PARA_CODE22",data.getString("PARA_CODE22",""));
                    insertData.put("PARA_CODE23",data.getString("PARA_CODE23",""));
                    insertData.put("PARA_CODE24",data.getString("PARA_CODE24",""));
                    insertData.put("PARA_CODE25",data.getString("PARA_CODE25",""));
                    insertData.put("PARA_CODE26",data.getString("PARA_CODE26",""));
                    insertData.put("PARA_CODE27",data.getString("PARA_CODE27",""));
                    insertData.put("PARA_CODE28",data.getString("PARA_CODE28",""));
                    insertData.put("PARA_CODE29",data.getString("PARA_CODE29",""));
                    insertData.put("PARA_CODE30",data.getString("PARA_CODE30",""));
                    insertData.put("START_DATE",data.getString("START_DATE",""));
                    insertData.put("END_DATE",data.getString("END_DATE",""));
                    insertData.put("REMARK", data.getString("REMARK", ""));
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
                    deleteData.put("TYPE_ID", "NONBOSS_FEE_ITEM");
                    deleteData.put("DATA_ID", dataId);
                    deleteData.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                    deleteData.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                    bean.deleteNonBossFeeItem(deleteData);
                }
                // 修改
                else if ("2".equals(xTag))
                {
                    IData updata = new DataMap();
                    String dataId = data.getString("DATA_ID", "");
                    if (dataId == null || dataId.trim().length() == 0)
                    {
                        CSAppException.apperr(ParamException.CRM_PARAM_454);
                    }
                    updata.put("TYPE_ID", "NONBOSS_FEE_ITEM");
                    updata.put("DATA_ID", dataId);
                    updata.put("DATA_ID_OLD",data.getString("DATA_ID_OLD",""));
                    updata.put("DATA_NAME",data.getString("DATA_NAME",""));
                    if(data.getString("PARAM_CODE","")!=null&&!"".equals(data.getString("PARAM_CODE",""))){
                    	updata.put("PARAM_NAME",data.getString("PARAM_CODE",""));//要取代码值
                    }else{
                    	IData newParas=new DataMap();
                    	newParas.put("TYPE_ID", "INCOME_TYPE");
                    	newParas.put("DATA_NAME",data.getString("PARAM_NAME",""));
                    	String newDataId=this.insNewsCommpara(newParas); 
                    	updata.put("PARAM_NAME",newDataId);                        
                    }
                    
                    if(data.getString("PARA_CODE1","")!=null&&!"".equals(data.getString("PARA_CODE1",""))){
                    	updata.put("PARA_CODE1",data.getString("PARA_CODE1",""));
                    }else{
                    	IData newParas=new DataMap();
                    	newParas.put("TYPE_ID", "ADDED_TAX_TYPE");
                    	newParas.put("DATA_NAME",data.getString("PARA_CODE1_NAME",""));
                    	String newDataId=this.insNewsCommpara(newParas); 
                    	updata.put("PARA_CODE1",newDataId);                        
                    }
                    
                    updata.put("PARA_CODE2",data.getString("PARA_CODE2",""));
                    if(data.getString("PARA_CODE3","")!=null&&!"".equals(data.getString("PARA_CODE3",""))){
                    	updata.put("PARA_CODE3",data.getString("PARA_CODE3",""));
                    }else{
                    	IData newParas=new DataMap();
                    	newParas.put("TYPE_ID", "INVOICE_TYPE");
                    	newParas.put("DATA_NAME",data.getString("PARA_CODE3_NAME",""));
                    	String newDataId=this.insNewsCommpara(newParas); 
                    	updata.put("PARA_CODE3",newDataId);                        
                    }
                    updata.put("PARA_CODE4",data.getString("PARA_CODE4","无"));
                    updata.put("PARA_CODE5",data.getString("PARA_CODE5","无"));
                    updata.put("PARA_CODE6",data.getString("PARA_CODE6","无"));
                    updata.put("PARA_CODE7",data.getString("PARA_CODE7","无"));
                    updata.put("PARA_CODE8",data.getString("PARA_CODE8","无"));
                    updata.put("PARA_CODE9",data.getString("PARA_CODE9","无"));
                    updata.put("PARA_CODE10",data.getString("PARA_CODE10",""));
                    updata.put("PARA_CODE11",data.getString("PARA_CODE11",""));
                    updata.put("PARA_CODE12",data.getString("PARA_CODE12",""));
                    updata.put("PARA_CODE13",data.getString("PARA_CODE13",""));
                    updata.put("PARA_CODE14",data.getString("PARA_CODE14",""));
                    updata.put("PARA_CODE15",data.getString("PARA_CODE15",""));
                    updata.put("PARA_CODE16",data.getString("PARA_CODE16",""));
                    updata.put("PARA_CODE17",data.getString("PARA_CODE17",""));
                    updata.put("PARA_CODE18",data.getString("PARA_CODE18",""));
                    updata.put("PARA_CODE19",data.getString("PARA_CODE19",""));
                    updata.put("PARA_CODE20",data.getString("PARA_CODE20",""));
                    updata.put("PARA_CODE21",data.getString("PARA_CODE21",""));
                    updata.put("PARA_CODE22",data.getString("PARA_CODE22",""));
                    updata.put("PARA_CODE23",data.getString("PARA_CODE23",""));
                    updata.put("PARA_CODE24",data.getString("PARA_CODE24",""));
                    updata.put("PARA_CODE25",data.getString("PARA_CODE25",""));
                    updata.put("PARA_CODE26",data.getString("PARA_CODE26",""));
                    updata.put("PARA_CODE27",data.getString("PARA_CODE27",""));
                    updata.put("PARA_CODE28",data.getString("PARA_CODE28",""));
                    updata.put("PARA_CODE29",data.getString("PARA_CODE29",""));
                    updata.put("PARA_CODE30",data.getString("PARA_CODE30",""));
                    updata.put("START_DATE",data.getString("START_DATE",""));
                    updata.put("END_DATE",data.getString("END_DATE",""));
                    updata.put("REMARK", data.getString("REMARK", ""));
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
