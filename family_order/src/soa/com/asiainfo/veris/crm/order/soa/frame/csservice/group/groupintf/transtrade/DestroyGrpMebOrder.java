
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;

public class DestroyGrpMebOrder extends CSBizBean
{

    private static final String BATCH_OPER_TYPE = "GROUPMEMCANCEL";// 批量成员注销

    /**
     * 处理成员注销, 插批量表
     * 
     * @param inParam
     *            接口传入数据
     * @return
     * @throws Exception
     */
    public static void destroyGrpMebOrder(IData inParam) throws Exception
    {

        IDataset dealDataset = new DatasetList();

        parseMap2List(inParam, dealDataset, "GROUP_ID");
        parseMap2List(inParam, dealDataset, "USER_ID_A");
        parseMap2List(inParam, dealDataset, "PRODUCT_ID");

        String serialNumber = IDataUtil.chkParam(inParam, "SERIAL_NUMBER");// 用户手机号码

        for (int i = 0; i < dealDataset.size(); i++)
        {
            IData dealData = dealDataset.getData(i);

            String groupId = IDataUtil.chkParam(dealData, "GROUP_ID");// 集团编码
            String userId = IDataUtil.chkParam(dealData, "USER_ID_A");// 集团用户编码
            String productId = IDataUtil.chkParam(dealData, "PRODUCT_ID");// 集团用户产品编码

            String productName = UProductInfoQry.getProductNameByProductId(productId);

            // 批量数据
            IData batData = new DataMap();

            // 批量任务条件
            IData condData = new DataMap();
            condData.put("GROUP_ID", groupId);
            condData.put("USER_ID", userId);
            condData.put("PRODUCT_ID", productId);
            batData.put("CODING_STR", condData.toString());

            batData.put("BATCH_OPER_CODE", BATCH_OPER_TYPE);
            batData.put("START_DATE", SysDateMgr.getSysDate());
            batData.put("END_DATE", SysDateMgr.getLastMonthLastDate());
            batData.put("BATCH_TASK_NAME", productName + "成员注销(电子渠道统一查询退订)");
            batData.put("BATCH_OPER_NAME", productName + "成员注销(电子渠道统一查询退订)");
            batData.put("SMS_FLAG", "0");
            batData.put("REMARK", "电子渠道退订");

            // 创建批量任务
            BatDealBean.createBatTask(batData);

            // 优先级
            String priority = StaticUtil.getStaticValue(getVisit(), "TD_B_BATCHTYPE", "BATCH_OPER_TYPE", "PRIORITY", BATCH_OPER_TYPE);
            // 是否可返销标志
            String cancelableFlag = StaticUtil.getStaticValue(getVisit(), "TD_B_BATCHTYPE", "CANCELABLE_FLAG", "PRIORITY", BATCH_OPER_TYPE);

            batData.put("BATCH_OPER_TYPE", BATCH_OPER_TYPE);
            batData.put("PRIORITY", priority);
            batData.put("CANCELABLE_FLAG", cancelableFlag);
            batData.put("AUDIT_STATE", "0");
            batData.put("DEAL_STATE", "1");

            IData mebData = new DataMap();
            mebData.put("SERIAL_NUMBER", serialNumber);

            IDataset mebList = new DatasetList();
            mebList.add(mebData);

            // 导入批量数据
            BatDealBean batDealBean = new BatDealBean();
            batDealBean.importDataForImp(mebList, batData);
        }
    }

    /**
     * 解析输入参数
     * 
     * @param inParam
     *            接口传入数据
     * @param retDataset
     * @param key
     * @throws Exception
     */
    public static void parseMap2List(IData inParam, IDataset retDataset, String key) throws Exception
    {
        String dataStr = inParam.get(key).toString();
        if (dataStr.indexOf("[") != -1)
        {
            IDataset dataSet = inParam.getDataset(key);
            if (dataSet != null)
            {
                for (int i = 0; i < dataSet.size(); i++)
                {
                    String keyValue = dataSet.get(i).toString();
                    IData keyMap;
                    if (retDataset.size() < i + 1)
                    {
                        keyMap = new DataMap();
                    }
                    else
                    {
                        keyMap = retDataset.getData(i);
                    }
                    keyMap.put(key, keyValue);
                    if (retDataset.size() != dataSet.size())
                    {
                        retDataset.add(i, keyMap);
                    }
                }
            }
        }
        else
        {
            IData keyMap;
            if (retDataset.size() < 1)
            {
                keyMap = new DataMap();
            }
            else
            {
                keyMap = retDataset.getData(0);
            }
            keyMap.put(key, inParam.getString(key));
            if (retDataset.size() != 1)
            {
                retDataset.add(0, keyMap);
            }
        }
    }
}
