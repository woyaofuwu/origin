
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.interboss.dmbusisel;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class DMBusiCardSelBean extends CSBizBean
{
    private static final transient Logger logger = Logger.getLogger(DMBusiCardSelBean.class);

    public IDataset getBusiCardSelInfo(IData conParams) throws Exception
    {
        IDataset spinfos = new DatasetList();

        String phonenum = conParams.getString("PHONENUM", "");
        String imeinum = conParams.getString("IMEINUM", "");
        String kindid = conParams.getString("DMCARD_KIND_ID", "");

        IData dataTemp = IBossCall.queryDMBusiCardSelIBOSS(phonenum, imeinum, kindid);

        // IData dataTemp = new DataMap(); dataTemp.put("RESULTSTATUS", "0"); dataTemp.put("SOFTWAREEDITIONNUM",
        // "09876543211234567890"); dataTemp.put("MATCHTIME", "20130713"); dataTemp.put("PHONENUM", "13619240267");
        // dataTemp.put("IMEINUM", "860143005681702"); dataTemp.put("TERMINALID", "352094000161619");
        // dataTemp.put("FAILTYPE", "1010"); dataTemp.put("FAILREASON", "失败原因");

        if (IDataUtil.isNull(dataTemp))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_316);
        }

        String resultStatus = (String) dataTemp.get("RESULTSTATUS");
        String viewResult = "失败";
        if ("0".equalsIgnoreCase(resultStatus))
        {
            viewResult = "成功";
        }
        IDataset dataset1 = new DatasetList();
        IDataset dataset2 = new DatasetList();
        IDataset dataset3 = new DatasetList();
        IDataset dataset4 = new DatasetList();
        IDataset dataset5 = new DatasetList();

        if ("BIP1A114_T1000114_0_0".equalsIgnoreCase(kindid))
        {
            /************** IBOSS数据转换 start *******************/
            // String 类型 例如：{PHONENUM =["001"]}
            if (dataTemp.get("PHONENUM") instanceof String)
            {

                dataset1.add(dataTemp.get("PHONENUM"));
                dataset2.add(dataTemp.get("IMEINUM"));
                dataset3.add(dataTemp.get("TERMINALID"));
                dataset4.add(dataTemp.get("SOFTWAREEDITIONNUM"));
                dataset5.add(dataTemp.get("MATCHTIME"));
            }
            // JSONArray类型 例如：{PHONENUM=["001","002"]}
            else
            {
                dataset1 = dataTemp.getDataset("PHONENUM");//
                dataset2 = dataTemp.getDataset("IMEINUM");//
                dataset3 = dataTemp.getDataset("TERMINALID");// 
                dataset4 = dataTemp.getDataset("SOFTWAREEDITIONNUM");//
                dataset5 = dataTemp.getDataset("MATCHTIME");//
            }
            /************** IBOSS数据转换 end *******************/
        }

        if (IDataUtil.isNotEmpty(dataset1))
        {
            for (int i = 0; i < dataset1.size(); i++)
            {
                IData data1 = new DataMap();

                data1.put("RESULTSTATUS", viewResult);
                data1.put("FAILTYPE", dataTemp.get("FAILTYPE"));

                data1.put("PHONENUM", dataset1.get(i));
                data1.put("IMEINUM", dataset2.get(i));
                data1.put("TERMINALID", dataset3.get(i));
                data1.put("SOFTWAREEDITIONNUM", dataset4.get(i));
                data1.put("MATCHTIME", dataset5.get(i));

                spinfos.add(data1);
            }
        }
        else
        {
            dataTemp.put("RESULTSTATUS", viewResult);
            spinfos.add(dataTemp);
        }

        return spinfos;
    }
}
