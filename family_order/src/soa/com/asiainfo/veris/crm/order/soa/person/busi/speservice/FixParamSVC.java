
package com.asiainfo.veris.crm.order.soa.person.busi.speservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.FixParmaQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.speservice.FixParamQry;

public class FixParamSVC extends CSBizService
{
    // private String paramAttr="2012";
    // private String subsysCode="CSM";//newsvcrec
    // private String PARA_CODE1="newsvcrec";

    private String SALE_ACT_TYPE = "1";

    /**
     * 查询SP信息
     * 
     * @param pd
     * @param eparchy_code
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryCommParamByEparchy(IData input) throws Exception
    {
        input.put("SALE_ACT_TYPE", SALE_ACT_TYPE);
        IDataset dataset = FixParamQry.queryBusimanage(input);
        return dataset;
    }

    /**
     * 提交批量部门操作
     * 
     * @param pd
     * @param parent_depart_id
     * @param dataset
     * @throws Exception
     */
    public IDataset submitDepts(IData input) throws Exception
    {
        IDataset dataset = input.getDataset("edit_table");
        IData params = new DataMap();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = (IData) dataset.get(i);
            /* 获取操作标记 */
            String x_tag = (String) data.get("tag");
            /* 1表示删除 */
            params.clear();
            params.put("SALE_ACT_TYPE", this.SALE_ACT_TYPE);
            params.put("MATCH_CODE", data.getString("PARAM_CODE"));
            params.put("BP_SERIAL_NO", data.getString("TYPE_CODE"));
            params.put("SMS_SCRIPT", data.getString("PARA_CODE23"));
            params.put("DATA_TIME", SysDateMgr.getSysDate());
            if ("1".equals(x_tag))
            {
                Dao.executeUpdateByCodeCode("TF_SM_BI_BUSIMANAGE", "DELETE_BY_PARAM_CODE", params);
                dataset.remove(i);
            }
        }

        for (int i = 0, len = dataset.size(); i < len; i++)
        {
            IData data = (IData) dataset.get(i);
            /* 获取操作标记 */
            String x_tag = (String) data.get("tag");
            params.clear();
            params.put("SALE_ACT_TYPE", this.SALE_ACT_TYPE);
            params.put("MATCH_CODE", data.getString("PARAM_CODE"));
            params.put("BP_SERIAL_NO", data.getString("TYPE_CODE"));
            params.put("SMS_SCRIPT", data.getString("PARA_CODE23"));
            params.put("DATA_TIME", SysDateMgr.getSysDate());
            /* 0表示新增 */
            if ("0".equals(x_tag))
            {
                // 首先判断推荐业务编码号是否已存在
                IDataset resultDataset = FixParmaQry.queryBusimanage(params);

                if (resultDataset != null && resultDataset.size() > 0)
                {
                    CSAppException.apperr(ElementException.CRM_ELEMENT_2104, data.getString("TYPE_CODE"), data.getString("PARAM_CODE"));
                }
                params.put("BP_NAME", data.getString("PARAM_NAME"));
                params.put("START_TIME", SysDateMgr.getSysDate());
                params.put("END_TIME", SysDateMgr.END_TIME_FOREVER);
                params.put("ATT_STR1", "");
                params.put("ATT_STR2", "");
                params.put("SALE_ACT_SCRIPT", "");
                Dao.executeUpdateByCodeCode("TF_SM_BI_BUSIMANAGE", "INSERT_BUSIMANAGE", params);
            }

            /* 2表示修改 */
            if ("2".equals(x_tag))
            {
                Dao.executeUpdateByCodeCode("TF_SM_BI_BUSIMANAGE", "UPD_BY_PARAM_CODE", params);
            }
        }

        return null;
    }
}
