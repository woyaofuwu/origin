
package com.asiainfo.veris.crm.order.soa.person.busi.custinfomgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqTradeId;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custinfomgr.CustPsptLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.PsptUtils;

public class CustPsptLimitInfoSVC extends CSBizService
{

    /**
     * 记录操作日志
     * 
     * @param dataset
     * @throws Exception
     */
    private void insertOperLog(IDataset dataset) throws Exception
    {
        // 分配LOG_ID 设置OPER_TYPE
        for (int i = 0; i < dataset.size(); i++)
        {
            dataset.getData(i).put("LOG_ID", Dao.getSequence(SeqTradeId.class));
            dataset.getData(i).put("OPER_TYPE", dataset.getData(i).getString("tag", ""));
        }
        Dao.insert("TL_F_CUST_PSPT_LIMIT", dataset);
    }

    public IDataset queryLimitInfo(IData input) throws Exception
    {
        IDataset dataset = CustPsptLimitInfoQry.queryLimitInfo(input, this.getPagination());
        return dataset;
    }

    public IDataset submitPsptLimit(IData input) throws Exception
    {
        IDataset submitInfoSet = input.getDataset("edit_table");
        IDataset addDataset = new DatasetList();
        IDataset delDataset = new DatasetList();
        IDataset uptDataset = new DatasetList();
        IDataset resultDataset = new DatasetList();
        IData counts = new DataMap();// 新增记录数, 删除记录数, 修改记录数

        for (int i = 0; i < submitInfoSet.size(); i++)
        {
            IData data = submitInfoSet.getData(i);
            // data.put("PSPT_TYPE_CODE", "");
            // data.put("PSPT_ID", "");
            // data.put("CUST_NAME", "");
            // data.put("LIMIT_COUNT", "");
            // 以上字段已在前台js中拼入, 下面拼入其他字段
            data.put("LIMIT_TAG", "0");
            data.put("START_DATE", "2012-06-01");
            data.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
            data.put("EPARCHY_CODE", getTradeEparchyCode());
            data.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            data.put("UPDATE_TIME", SysDateMgr.getSysTime());
            
            data.put("RSRV_STR1",data.getString("ADJUST_TYPE_CODE", ""));

            if ("0".equals(data.getString("tag", "")))
            {// 新增
                addDataset.add(data);
            }
            if ("1".equals(data.getString("tag", "")))
            {// 删除
                delDataset.add(data);
            }
            if ("2".equals(data.getString("tag", "")))
            {// 修改
                uptDataset.add(data);
            }
        }
        try
        {
            if (addDataset.size() != 0)
            {
            	/**
            	 * 集团证件
            	 * 1、单位证明和营业执照的名称相同也是不能重复设置
            	 * 2、证件号码相同也不可以
            	 */
            	for (int i = 0; i < addDataset.size(); i++){
            		IData addData = addDataset.getData(i);
            		String psptTypeCode = addData.getString("PSPT_TYPE_CODE");
            		
            		if("D".equals(psptTypeCode)||"E".equals(psptTypeCode)
            	    	||"G".equals(psptTypeCode)||"L".equals(psptTypeCode)||"M".equals(psptTypeCode)){

            	        IDataset psptId = Dao.qryByCode("TF_F_CUST_PSPT_LIMIT", "SEL_BY_PSPT_ID", addData);
            	        IDataset custName = Dao.qryByCode("TF_F_CUST_PSPT_LIMIT", "SEL_BY_CUST_NAME", addData);

            	        if(IDataUtil.isNotEmpty(psptId) || IDataUtil.isNotEmpty(custName)){
            	        	CSAppException.apperr(ElementException.CRM_ELEMENT_2125);
            	        }
            	        
            		}
            	}
            	
                int[] countInsert = Dao.insert("TF_F_CUST_PSPT_LIMIT", addDataset);
                counts.put("ADD_COOUNTS", countInsert.length);
                insertOperLog(addDataset);
            }
            if (delDataset.size() != 0)
            {
                int[] countDelete = Dao.delete("TF_F_CUST_PSPT_LIMIT", delDataset, new String[]
                { "PSPT_TYPE_CODE", "PSPT_ID", "RSRV_STR1" }, input.getString(Route.ROUTE_EPARCHY_CODE));
                counts.put("DELETE_COOUNTS", countDelete.length);
                insertOperLog(delDataset);
            }
            if (uptDataset.size() != 0)
            {
                int count = 0;
                for (Object tdata : uptDataset)
                {
                    boolean flag = Dao.update("TF_F_CUST_PSPT_LIMIT", (IData) tdata, new String[]
                    { "PSPT_TYPE_CODE", "PSPT_ID", "RSRV_STR1" }, input.getString(Route.ROUTE_EPARCHY_CODE));
                    if (flag)
                    {
                        count++;
                    }
                }
                counts.put("UPDATE_COOUNTS", count);
                insertOperLog(uptDataset);
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2125);
        }

        resultDataset.add(counts);

        return resultDataset;
    }
    
    public IData checkPsptForApp(IData input) throws Exception
    {
    	IData data=new DataMap();
    	data.put("X_RESULTCODE", "2998");
 		data.put("X_RESULTINFO", "未知异常");
    	if(input!=null){
    		if(input.getString("PSPT_TYPE_CODE", "").equals("")||input.getString("PSPT_ID", "").equals("")
    				||input.getString("CUST_NAME", "").equals("")){
    			data.put("X_RESULTCODE", "2999");
    	 		data.put("X_RESULTINFO", "相关入参不能为空！");
    	 		return data;
    		}
    		if(!input.getString("PSPT_TYPE_CODE", "").equals("A")&&!input.getString("PSPT_TYPE_CODE", "").equals("O")
    				&&!input.getString("PSPT_TYPE_CODE", "").equals("N")){
    			data.put("X_RESULTCODE", "2999");
    	 		data.put("X_RESULTINFO", "PSPT_TYPE_CODE入参无法识别或支持的证件类型！");
    	 		return data;
    		}
    		PsptUtils psptUtils=new PsptUtils();
    		data=psptUtils.checkPspt(input.getString("CUST_NAME", ""), input.getString("PSPT_ID", ""), input.getString("PSPT_TYPE_CODE", ""));
    		
    	}
    	return data;
    }
}
