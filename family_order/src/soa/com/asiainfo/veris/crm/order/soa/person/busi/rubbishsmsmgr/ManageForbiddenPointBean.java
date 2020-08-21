
package com.asiainfo.veris.crm.order.soa.person.busi.rubbishsmsmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.ForbiddenPointInfoQry;

public class ManageForbiddenPointBean extends CSBizBean
{
    // 列表数据状态，失效：2
    private static final String OPRATOR_TYPE_DISABLE = "2";

    // 网外点对点短信
    public static final String OTHER_SYS_SERV_TYPE = "100101020406";

    // 省内点对点短信
    public static final String CURRENT_PROVINCE_SERV_TYPE = "100101020401";

    // 省际点对点短信
    public static final String OTHER_PROVINCE_SERV_TYPE = "100101020402";

    // 来源外部系统
    private static final String FROM_FORB_OTHERSYS = "1";

    // 网内标示
    private static final String FROM_FORB_INNERSYS = "0";

    public IDataset addForbiddenData(IData data) throws Exception
    {
        // 举报对象类型
        String requestType = data.getString("SERV_REQUEST_TYPE", "");
        // 默认，被举报手机号来源内网
        String othersysFlag = "0";

        if (OTHER_SYS_SERV_TYPE.equals(requestType))
        {
            othersysFlag = "1";
        }

        data.put("RECV_STAFF_ID", data.getString("TRADE_STAFF_ID",""));
        data.put("FORB_OTHERSYS_FLAG", othersysFlag);
        
        // 获取查询列表数据，查询SQL保存在code_code表中
        int i = Dao.executeUpdateByCodeCode("TF_F_FORBIDDEN_POINT", "INSERT_FORBIDDEN", data, Route.CONN_CRM_CEN);

        return null;
    }

    private void dealLikeParam(IData param, String key, boolean prefixFlag)
    {
        String value = param.getString(key);

        if (value != null && !"".equals(value))
        {
            StringBuilder sb = new StringBuilder();
            if (prefixFlag)
            {
                sb.append("%");
            }
            sb.append(value.trim());
            sb.append("%");
            param.put(key, sb.toString());
        }

    }

    public IDataset disableData(IData data) throws Exception
    {
        String ids = data.getString("REVC_IDS");
        String sql = "UPDATE TF_F_FORBIDDEN_POINT T SET T.FORB_OPERATE_TYPE=:OPERATE_TYPE, T.FORB_UPDATE_TIME =SYSDATE  WHERE T.INFO_RECV_ID in (:INFO_RECV_ID)";

        IData param = new DataMap();
        param.put("OPERATE_TYPE", OPRATOR_TYPE_DISABLE);

        StringBuilder sbSql = new StringBuilder();
        // in 条件选择多条记录
        if (ids.contains(","))
        {
            String[] idArray = ids.split(",");
            int i = 0;
            // 查询参数
            StringBuilder paramSql = new StringBuilder(100);

            for (String id : idArray)
            {
                if (i > 0)
                {
                    paramSql.append(",");
                }
                paramSql.append(":INFO_RECV_ID_").append(i);
                param.put("INFO_RECV_ID_" + i, id);
                i++;
            }
            ;
            // 替换SQL中原有查询条件
            sbSql.append(sql.replaceAll(":INFO_RECV_ID", paramSql.toString()));

        }
        else
        {// 单条记录处理不需要修改SQL
            param.put("INFO_RECV_ID", ids);
            // 修改当前数据状态
            sbSql.append(sql);
        }
        Dao.executeUpdate(sbSql, param, Route.CONN_CRM_CEN);

        return null;
    }

    public IDataset queryForbiddenList(IData data, Pagination page) throws Exception
    {
        dealLikeParam(data, "FORBIDDEN_NUM", true);
        dealLikeParam(data, "REPORT_NUM", true);

        String badnessSerial = data.getString("FORBIDDEN_NUM", "");
        String reportSerial = data.getString("REPORT_NUM", "");
        String type = data.getString("OPERATE_TYPE", "");
        String startDate = data.getString("START_DATE", "");
        String endDate = data.getString("END_DATE", "");
        String inforecvid = data.getString("INFO_RECV_ID");
        IDataset results = new DatasetList() ;
        results = ForbiddenPointInfoQry.queryForbiddenList(inforecvid,badnessSerial, reportSerial, type, startDate, endDate, page);
        if(IDataUtil.isNotEmpty(results))
        {
        	for(int i=0,size=results.size();i<size;i++)
        	{
        		IData tmp = results.getData(i);
        		if("".equals(tmp.getString("RECV_CONTENT", "")))
        		{
        			IData param = new DataMap();
        			param.put("INFO_RECV_ID",tmp.getString("INFO_RECV_ID"));
        			IDataset tmps= Dao.qryByCodeParser("TF_F_FORBIDDEN_POINT", "SEL_BY_RECV_ID", param , Route.CONN_CRM_CEN);
        			if(IDataUtil.isNotEmpty(tmps))
        			{
        				results.getData(i).put("RECV_CONTENT", tmps.getData(0).getString("RECV_CONTENT",""));
        			}
        		}
        	}
        }
        
        return  results ;
    }
}
