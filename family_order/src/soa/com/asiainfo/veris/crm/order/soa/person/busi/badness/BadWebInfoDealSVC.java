
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqBookingId;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadWebInfoQry;

public class BadWebInfoDealSVC extends CSBizService
{
	 protected static Logger log = Logger.getLogger(BadWebInfoDealSVC.class);
    /**
     * 记录操作日志
     * 
     * @param dataset
     * @throws Exception
     */
   /* private void insertOperLog(IDataset dataset) throws Exception
    {
        // 分配LOG_ID 设置OPER_TYPE
        for (int i = 0; i < dataset.size(); i++)
        {
            dataset.getData(i).put("LOG_ID", Dao.getSequence(SeqTradeId.class));
            dataset.getData(i).put("OPER_TYPE", dataset.getData(i).getString("tag", ""));
        }
        Dao.insert("TL_F_BAD_WEB_INFO", dataset);
    }*/

    public IDataset queryBadWebInfo(IData input) throws Exception
    {
        IDataset dataset = BadWebInfoQry.queryBadWebInfo(input, this.getPagination());
        return dataset;
    }

    public IDataset submitBadWebInfo(IData input) throws Exception
    {
        IDataset submitInfoSet = input.getDataset("edit_table");
        IDataset addDataset = new DatasetList();
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
            /**
             * 获取网站来源编码，赋给WEB_SOURCE
             * 因为在页面上WEB_SOURCE存的是名称,
             */
            data.put("WEB_SOURCE", data.getString("WEB_SOURCE_CODE",""));
            
            if ("0".equals(data.getString("tag", "")))
            {// 新增
            	data.put("MODIFY_TAG", "0");
            	data.put("MODIFY_TYPE", "新增");
            	data.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	String seqId=Dao.getSequence(SeqBookingId.class);
            	//log.info("("linsl seqId = " + seqId);
            	data.put("ID", seqId);
                addDataset.add(data);
            }
            if ("1".equals(data.getString("tag", "")))
            {// 删除
            	data.put("MODIFY_TAG", "1");
            	data.put("MODIFY_TYPE", "删除");
            	data.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	uptDataset.add(data);
            }
            if ("2".equals(data.getString("tag", "")))
            {// 修改
            	//log.info("("进入修改,对原记录标示为删除，插入一条新记录,id为"+data.getString("ID"));
            	/**
            	 * 20160607
            	 * 对原记录标示为删除，
            	 */
            	
            	IDataset dataset=BadWebInfoQry.queryBadWebInfoById(data);
            	if(IDataUtil.isNotEmpty(dataset)){
            		IData badwebinfo=null;
            		badwebinfo=dataset.getData(0);
            		badwebinfo.put("MODIFY_TAG", "1");
            		badwebinfo.put("MODIFY_TYPE", "删除");
            		badwebinfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
            		uptDataset.add(badwebinfo);
            	}
                /**
                 * 20160607
                 * 插入一条新记录
                 */
            	data.put("MODIFY_TAG", "0");
            	data.put("MODIFY_TYPE", "新增");
            	data.put("UPDATE_TIME", SysDateMgr.getSysTime());
            	String seqId=null;
            	seqId=Dao.getSequence(SeqBookingId.class);
            	//log.info("("linsl seqId = " + seqId);
            	data.put("ID", seqId);
                addDataset.add(data);
            }
        }
        try
        {
            if (addDataset.size() != 0)
            {
                int[] countInsert = Dao.insert("TD_S_BAD_WEB_INFO", addDataset,Route.CONN_UOP_UEC);
                counts.put("ADD_COOUNTS", countInsert.length);
                //insertOperLog(addDataset);
            }
            
            if (uptDataset.size() != 0 )
            {
                int count = 0;
                for (Object tdata : uptDataset)
                {
                	
                    boolean flag = Dao.update("TD_S_BAD_WEB_INFO", (IData) tdata, new String[]{"STAFF_ID","WEB_ADDR","WEB_NAME"}, Route.CONN_UOP_UEC);
                    if (flag)
                    {
                        count++;
                    }
                }
                counts.put("UPDATE_COOUNTS", count);
                //insertOperLog(uptDataset);
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, e.getMessage());
            //log.info("(e);
            throw e;
        }

        resultDataset.add(counts);

        return resultDataset;
    }
    
    /**
     * 
     * @param input
     * @throws Exception
     */
    public void importBadwebinfo(IData input) throws Exception {
    	try {
    		BadWebInfoQry.importBadwebinfo(input);
		} catch (Exception e) {
			// TODO: handle exception
			//log.info("(e);
			throw e;
		}
    }
    
    /**
     * 
     * @param data
     * @throws Exception
     */
    public  void  updateBadWebInfoByCond(IData data) throws Exception{
    	try {
			BadWebInfoQry.updateBadWebInfoByCond(data);
		} catch (Exception e) {
			// TODO: handle exception
			//log.info("(e);
			throw e;
		}
    }
    
}
