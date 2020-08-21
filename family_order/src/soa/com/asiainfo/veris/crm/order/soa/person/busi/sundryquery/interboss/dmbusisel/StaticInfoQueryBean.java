
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.DMBusiException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;

public class StaticInfoQueryBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(StaticInfoQueryBean.class);

    /**
     * 获取省代码
     * 
     * @param pd
     * @return
     * @throws Exception
     */
    public String getProvCode() throws Exception
    {

        String provInfo = StaticInfoQry.qryProvCode(getVisit().getProvinceCode());

        if (StringUtils.isBlank(provInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_310);
        }

        return provInfo;
    }

    public IDataset getStaticInfo(IData inparam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug("---------###------StaticInfoQueryBean------###--------:" + this.getProvCode());

        String terminalId = inparam.getString("cond_TERMID", "");
        String prov_code = this.getProvCode();

        IData data = IBossCall.queryStaticInfoIBOSS(terminalId, prov_code);

        // IData data = new DataMap(); data.put("X_RSPTYPE", "0"); data.put("RESULTSTATUS", "2"); IDataset dataset2
        // = new DatasetList(); IDataset dataset3 = new DatasetList(); IDataset dataset4 = new DatasetList(); IData
        // data2 = new DataMap(); IData data3 = new DataMap(); IData data4 = new DataMap(); data2.put("ITEMID",
        // "11111111111"); data3.put("ITEMNAME", "222222222222"); data4.put("ITEMVALUE", "33333333333");
        // dataset2.add(data2); dataset3.add(data3); dataset4.add(data4); data.put("ITEMID", dataset2);
        // data.put("ITEMNAME", dataset3); data.put("ITEMVALUE", dataset4); //临时end

        if (IDataUtil.isNull(data) || !(data.getString("X_RSPTYPE", "2").equalsIgnoreCase("0")))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_102);
        }
        else if (data.getString("X_RSPTYPE", "").equalsIgnoreCase("0") && data.getString("RESULTSTATUS", "").equalsIgnoreCase("1"))
        {
            CSAppException.apperr(DMBusiException.CRM_DM_2, ((IData) data).getString("FAILTYPE", ""), ((IData) data).getString("FAILREASON", ""));
        }

        /************** IBOSS数据转换 start *******************/
        IDataset itemIds = new DatasetList();
        IDataset itemNames = new DatasetList();
        IDataset itemValues = new DatasetList();
        // String 类型 例如：{ITEMID =["001"]}
        if (data.get("ITEMID") instanceof String)
        {

            itemIds.add(data.get("ITEMID"));
            itemNames.add(data.get("ITEMNAME"));
            itemValues.add(data.get("ITEMVALUE"));
        }
        // JSONArray类型 例如：{ITEMID=["001","002"]}
        else
        {
            itemIds = data.getDataset("ITEMID");
            itemNames = data.getDataset("ITEMNAME");
            itemValues = data.getDataset("ITEMVALUE");
        }
        /************** IBOSS数据转换 end *******************/

        IDataset dataset = new DatasetList();
        if (IDataUtil.isNotEmpty(itemIds))
        {
            if ((itemIds.size() != itemNames.size()) || (itemNames.size() != itemValues.size()))
            {
                CSAppException.apperr(DMBusiException.CRM_DM_103);
            }

            for (int i = 0; i < itemIds.size(); i++)
            {
                IData temp = new DataMap();
                temp.put("ITEMID", itemIds.get(i));
                temp.put("ITEMNAME", itemNames.get(i));
                temp.put("ITEMVALUE", itemValues.get(i));
                dataset.add(temp);
            }
        }

        return dataset;
    }
}
