
package com.asiainfo.veris.crm.order.web.person.bat;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * 批量平台业务
 * 
 * @author xiekl
 */
public abstract class BatPlatDeal extends PersonBasePage
{

    /**
     * 创建平台业务批量任务，同时创建批量批次，导入批量数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void createPlatBatch(IRequestCycle cycle) throws Exception
    {

        IDataset resultList = null;
        IData param = this.getData();
        IData data = new DataMap();
        param.put("END_DATE", SysDateMgr.getEndDate(param.getString("END_DATE", "")));
        String codingStr1 = param.getString("CODING1");
        String codingStr2 = param.getString("CODING2");
        String codingStr3 = param.getString("CODING3");
        String codingStr4 = param.getString("CODING4");
        String batchId1 = "", batchId2 = "", batchId3 = "", batchId4 = "";
        // bean.getCommBatInfo(pd, data);

        // 获取导入数据
        String fileId = param.getString("FILE_ID");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        IData array = ImpExpUtil.beginImport(null, fileId, ExcelConfig.getSheets("import/bat/BATPLATFORM.xml"));
        IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
        IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
        int sucCount = array.getInt("rightCount");// 解析成功的数据总条数
        int errCount = array.getInt("errorCount");// 解析失败的数据总条数
        if (sucCount == 0 || suc.length == 0)
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "导入正确的数据为0，请检查导入文件！");
        }

        // IDataset failds = fileCheckData.getDataset("FAILDS");
        //		
        // //如果效验结果是超过了月限量，则会返回提示信息
        // String hint_message = bean.importNumCheck(data, succds.size(), data.getInt("LIMIT_NUM_BATCH"),
        // data.getInt("LIMIT_NUM_DAY"),
        // data.getInt("LIMIT_NUM_MON"), data.getInt("SUMS"), data.getInt("MONTH_SUM"));
        //		

        data.put("BAT_DEAL_LIST", suc[0]);
        data.put("USER_EPARCHY_CODE", this.getTradeEparchyCode());
        if (codingStr1 != null && !"".equals(codingStr1))
        {

            IData inParam = new DataMap();
            inParam.putAll(this.getData());
            inParam.put("SMS_FLAG", "0");// 不需要发短信
            inParam.put("CODING_STR", codingStr1);
            data.put("IN_PARAM", inParam);

            resultList = CSViewCall.call(this, "CS.BatDealSVC.singlePagecreateBat", data);

            if (resultList != null && !resultList.isEmpty())
            {
                batchId1 = resultList.getData(0).getString("BATCH_ID");
            }

        }
        if (codingStr2 != null && !"".equals(codingStr2))
        {
            IData inParam = new DataMap();
            inParam.putAll(this.getData());
            inParam.put("SMS_FLAG", "0");// 不需要发短信
            inParam.put("CODING_STR", codingStr2);
            data.put("IN_PARAM", inParam);
            resultList = CSViewCall.call(this, "CS.BatDealSVC.singlePagecreateBat", data);

            if (resultList != null && !resultList.isEmpty())
            {
                batchId2 = resultList.getData(0).getString("BATCH_ID");
            }
        }
        if (codingStr3 != null && !"".equals(codingStr3))
        {
            IData inParam = new DataMap();
            inParam.putAll(this.getData());
            inParam.put("SMS_FLAG", "0");// 不需要发短信
            inParam.put("CODING_STR", codingStr3);
            data.put("IN_PARAM", inParam);
            resultList = CSViewCall.call(this, "CS.BatDealSVC.singlePagecreateBat", data);

            if (resultList != null && !resultList.isEmpty())
            {
                batchId3 = resultList.getData(0).getString("BATCH_ID");
            }
        }
        if (codingStr4 != null && !"".equals(codingStr4))
        {
            IData inParam = new DataMap();
            inParam.putAll(this.getData());
            inParam.put("SMS_FLAG", "0");// 不需要发短信
            inParam.put("CODING_STR", codingStr4);
            data.put("IN_PARAM", inParam);
            resultList = CSViewCall.call(this, "CS.BatDealSVC.singlePagecreateBat", data);

            if (resultList != null && !resultList.isEmpty())
            {
                batchId4 = resultList.getData(0).getString("BATCH_ID");
            }
        }

        StringBuilder bufBatchIds = new StringBuilder();
        if (StringUtils.isNotEmpty(batchId1))
        {
            bufBatchIds.append(batchId1);
            bufBatchIds.append(",");
        }
        if (StringUtils.isNotEmpty(batchId2))
        {
            IData relationParam = new DataMap();
            relationParam.put("BATCH_ID", batchId2);
            relationParam.put("RELA_BATCH_ID", batchId1);
            relationParam.put("RELATION_TYPE_CODE", "1");
            CSViewCall.callone(this, "CS.BatDealSVC.createBatRelaiton", relationParam);

            bufBatchIds.append(batchId2);
            bufBatchIds.append(",");
        }
        if (StringUtils.isNotEmpty(batchId3))
        {
            IData relationParam = new DataMap();
            relationParam.put("BATCH_ID", batchId3);
            relationParam.put("RELA_BATCH_ID", batchId2);
            relationParam.put("RELATION_TYPE_CODE", "1");
            CSViewCall.callone(this, "CS.BatDealSVC.createBatRelaiton", relationParam);

            bufBatchIds.append(batchId3);
            bufBatchIds.append(",");
        }
        if (StringUtils.isNotEmpty(batchId4))
        {
            IData relationParam = new DataMap();
            if (StringUtils.isNotEmpty(batchId3))
            {
                relationParam.put("BATCH_ID", batchId4);
                relationParam.put("RELA_BATCH_ID", batchId3);
                relationParam.put("RELATION_TYPE_CODE", "1");
            }
            else
            {
                relationParam.put("BATCH_ID", batchId4);
                relationParam.put("RELA_BATCH_ID", batchId2);
                relationParam.put("RELATION_TYPE_CODE", "1");
            }

            CSViewCall.callone(this, "CS.BatDealSVC.createBatRelaiton", relationParam);

            bufBatchIds.append(batchId4);
            bufBatchIds.append(",");
        }

        IData result = new DataMap();
        result.put("BATCH_TASK_IDS", bufBatchIds.toString());

        this.setAjax(result);
    }

    /**
     * 初始化
     * 
     * @param cycle
     * @throws Exception
     */
    public void init(IRequestCycle cycle) throws Exception
    {
        IData data = new DataMap();
        String startDate = SysDateMgr.getSysTime();
        data.put("START_DATE", startDate);
        data.put("END_DATE", SysDateMgr.addDays(startDate, 6));
        setInfo(data);
    }

    public abstract void setInfo(IData info);
}
