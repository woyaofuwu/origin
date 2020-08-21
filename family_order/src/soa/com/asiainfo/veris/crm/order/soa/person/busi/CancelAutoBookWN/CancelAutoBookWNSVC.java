
package com.asiainfo.veris.crm.order.soa.person.busi.CancelAutoBookWN;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqTradeId;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.custinfomgr.CustPsptLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.ailk.common.data.impl.Pagination;

public class CancelAutoBookWNSVC extends CSBizService
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
        Dao.insert("TL_CACNEL_AUTO_BOOK_WN", dataset);
    }

    public IDataset queryLimitInfo(IData input) throws Exception
    {
        IDataset result = null;
        StringBuilder sql = new StringBuilder(" SELECT cancel_serial_number, ");
        sql.append("      cancel_start_date, ");
        sql.append("      cancel_end_date, ");
        sql.append("      inst_id ");
        sql.append(" FROM TF_CANCEL_AUTO_BOOK_WN L ");
        sql.append(" WHERE 1 = 1 ");
        sql.append("  AND L.EPARCHY_CODE = :EPARCHY_CODE ");
        sql.append(" AND  (L.CANCEL_SERIAL_NUMBER = :SERIAL_NUMBER or :SERIAL_NUMBER is null) ");

        result = Dao.qryBySql(sql, input, this.getPagination());

        return result;
    }

    public IDataset submitBookWNLimit(IData input) throws Exception
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
            /*data.put("LIMIT_TAG", "0");
            data.put("START_DATE", "2012-06-01");
            data.put("END_DATE", SysDateMgr.END_TIME_FOREVER);*/
            data.put("EPARCHY_CODE", getTradeEparchyCode());
            data.put("UPDATE_DEPART_ID", getVisit().getDepartId());
            data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
            
            //获取用户UCA数据
            UcaData uca = null;
            String Serial_number = data.getString("CANCEL_SERIAL_NUMBER");
            if(!"".equals(Serial_number))
            {
            	uca = UcaDataFactory.getNormalUca(Serial_number);
                data.put("USER_ID", uca.getUserId());
            }else
            {
            	CSAppException.apperr(ElementException.CRM_ELEMENT_310, "获取用户数据出错!");
            }
            

            if ("0".equals(data.getString("tag", "")))
            {// 新增
            	data.put("INST_ID", Dao.getSequence(SeqTradeId.class));
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
                int[] countInsert = Dao.insert("TF_CANCEL_AUTO_BOOK_WN", addDataset);
                counts.put("ADD_COOUNTS", countInsert.length);
                insertOperLog(addDataset);
            }
            if (delDataset.size() != 0)
            {
                int[] countDelete = Dao.delete("TF_CANCEL_AUTO_BOOK_WN", delDataset, new String[]
                { "CANCEL_SERIAL_NUMBER", "USER_ID","INST_ID" }, input.getString(Route.ROUTE_EPARCHY_CODE));
                counts.put("DELETE_COOUNTS", countDelete.length);
                insertOperLog(delDataset);
            }
            if (uptDataset.size() != 0)
            {
                int count = 0;
                for (Object tdata : uptDataset)
                {
                    boolean flag = Dao.update("TF_CANCEL_AUTO_BOOK_WN", (IData) tdata, new String[]
                    { "CANCEL_SERIAL_NUMBER", "USER_ID", "INST_ID" }, input.getString(Route.ROUTE_EPARCHY_CODE));
                    if (flag)
                    {
                        count++;
                    }
                }
                counts.put("UPDATE_COOUNTS", count);
//                insertOperLog(uptDataset);
            }
        }
        catch (Exception e)
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_2125);
        }

        resultDataset.add(counts);

        return resultDataset;
    }
}
