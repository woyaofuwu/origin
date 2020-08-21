
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatDealInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTaskInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatchTypeInfoQry;

public class BatTradeFinishSVC extends CSBizService
{

    private static final long serialVersionUID = 4164266860074900841L;

    protected IData batCondData = new DataMap();

    protected String serviceName = "";

    public IDataset finish(IData input) throws Exception
    {
        IDataset iDataset = new DatasetList();

        String batch_task_id = IDataUtil.chkParam(input, "BATCH_TASK_ID");

        String serial_number = input.getString("SERIAL_NUMBER", "");

        // 获取bat_task
        IData batTaskData = BatTaskInfoQry.qryBatTaskByBatchTaskId(batch_task_id);
        batCondData.putAll(batTaskData);

        String condStr = BatTradeInfoQry.getTaskCondString(batch_task_id);
        batCondData.put("CODING_STR", new DataMap(condStr));

        // 获取批量类型表
        IDataset batTypeList = BatchTypeInfoQry.qryBatchTypeByOperType(batCondData.getString("BATCH_OPER_CODE"));
        if (IDataUtil.isEmpty(batTypeList))
        {
            CSAppException.apperr(BatException.CRM_BAT_76);
            return iDataset;
        }

        serviceName = batTypeList.getData(0).getString("SS_SERVICE", "");
        if (StringUtils.isBlank(serviceName))
        {
            CSAppException.apperr(BatException.CRM_BAT_51);
            return iDataset;
        }

        // 获取批量bat主表
        IDataset batMainDataList = BatInfoQry.qryBatByBatchTaskId(batch_task_id);
        if (IDataUtil.isEmpty(batMainDataList))
        {
            CSAppException.apperr(BatException.CRM_BAT_16);
            return iDataset;
        }

        IData batMainData = batMainDataList.getData(0);
        batCondData.putAll(batMainData);

        // 获取批量明细batdeal表
        IDataset batDealList = BatDealInfoQry.qryBatDealByBatchId(batCondData.getString("BATCH_ID"), null);
        if (IDataUtil.isEmpty(batDealList))
        {
            CSAppException.apperr(BatException.CRM_BAT_73);
            return iDataset;
        }

        BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);

        for (int i = 0, size = batDealList.size(); i < size; i++)
        {
            IData batData = (IData) Clone.deepClone(batCondData);

            IData batDealData = batDealList.getData(i);
            batData.putAll(batDealData);

            // 传了serial_number 则只处理这个serial_number
            if (StringUtils.isNotBlank(serial_number) && !StringUtils.equals(serial_number, batDealData.getString("SERIAL_NUMBER")))
            {
                continue;
            }

            try
            {
                IDataset resultList = CSAppCall.call(serviceName, batData);

                batDealData.put("DEAL_STATE", "5");
                batDealData.put("DEAL_RESULT", "调用成功");
                batDealData.put("TRADE_ID", resultList.getData(0).getString("ORDER_ID"));
            }
            catch (Exception e)
            {
                batDealData.put("DEAL_STATE", "6");
                batDealData.put("DEAL_RESULT", "接口调用处理失败");
                batDealData.put("DEAL_DESC", e.getMessage());
            }
            finally
            {
                bean.updateBatDealByBatchIdAndSn(batDealData);
            }

        }

        return iDataset;
    }

}
