
package com.asiainfo.veris.crm.order.soa.person.busi.tdcustpsptinfo;

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

import com.asiainfo.veris.crm.order.soa.person.common.query.tdcustpsptinfo.TDCustPsptLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.PsptUtils;
import org.apache.log4j.Logger;


public class TDCustPsptLimitInfoSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(TDCustPsptLimitInfoSVC.class);


    public IDataset queryLimitInfo(IData input) throws Exception
    {
        IDataset dataset = TDCustPsptLimitInfoQry.queryLimitInfo(input, this.getPagination());
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

        logger.debug("TDCustPsptLimitInfoSVC---------------->submitPsptLimit"+submitInfoSet.size());

        for (int i = 0; i < submitInfoSet.size(); i++)
        {
            IData data = submitInfoSet.getData(i);

            data.put("LIMIT_TAG", "0");
            data.put("START_DATE", SysDateMgr.getSysTime());
            data.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
            data.put("EPARCHY_CODE", getTradeEparchyCode());
            data.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            data.put("UPDATE_TIME", SysDateMgr.getSysTime());

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
            logger.debug("TDCustPsptLimitInfoSVC---------------->addDataset1"+addDataset.size());
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

            	        IDataset psptId = Dao.qryByCode("TF_F_FIXPHONE_PSPT_LIMT", "SEL_BY_PSPT_ID", addData);
            	        IDataset custName = Dao.qryByCode("TF_F_FIXPHONE_PSPT_LIMT", "SEL_BY_CUST_NAME", addData);

            	        if(IDataUtil.isNotEmpty(psptId) || IDataUtil.isNotEmpty(custName)){
            	        	CSAppException.apperr(ElementException.CRM_ELEMENT_310,"同一证件号码只能存在一条记录");
            	        }
            	        
            		}
            	}
                logger.debug("TDCustPsptLimitInfoSVC---------------->addDataset2"+addDataset);
                int[] countInsert = Dao.insert("TF_F_FIXPHONE_PSPT_LIMT", addDataset,Route.getCrmDefaultDb());
                logger.debug("TDCustPsptLimitInfoSVC---------------->countInsert1"+countInsert);
                counts.put("ADD_COOUNTS", countInsert.length);
            }
            logger.debug("TDCustPsptLimitInfoSVC---------------->delDataset"+delDataset.size());
            if (delDataset.size() != 0)
            {
                int[] countDelete = Dao.delete("TF_F_FIXPHONE_PSPT_LIMT", delDataset, new String[]
                { "PSPT_TYPE_CODE", "PSPT_ID"}, input.getString(Route.ROUTE_EPARCHY_CODE));
                counts.put("DELETE_COOUNTS", countDelete.length);
                logger.debug("TDCustPsptLimitInfoSVC---------------->countDelete"+countDelete);

            }
            logger.debug("TDCustPsptLimitInfoSVC---------------->uptDataset"+uptDataset.size());
            if (uptDataset.size() != 0)
            {
                int count = 0;
                for (Object tdata : uptDataset)
                {
                    boolean flag = Dao.update("TF_F_FIXPHONE_PSPT_LIMT", (IData) tdata, new String[]
                    { "PSPT_TYPE_CODE", "PSPT_ID"}, input.getString(Route.ROUTE_EPARCHY_CODE));
                    if (flag)
                    {
                        count++;
                    }
                }
                counts.put("UPDATE_COOUNTS", count);
                logger.debug("TDCustPsptLimitInfoSVC---------------->count2"+count);

            }
        }
        catch (Exception e)
        {
//            CSAppException.apperr(ElementException.CRM_ELEMENT_2125);
            CSAppException.apperr(ElementException.CRM_ELEMENT_310,"同一证件号码只能存在一条记录");
        }

        resultDataset.add(counts);

        return resultDataset;
    }

}
