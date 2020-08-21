
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.log.ApproveReceiptInfoQry;

public class GrpTaxIntf
{

    /**
     * 变更增值税申请单状态
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public static IDataset changeApproveReceipt(IData inParam) throws Exception
    {
        IData retData = new DataMap();

        String receiptId = IDataUtil.getMandaData(inParam, "RECEIPT_ID"); // 申请单号

        String receiptType = inParam.getString("RECEIPT_TYPE"); // 申请单类型

        int receiptFee = Integer.parseInt(inParam.getString("RECEIPT_FEE", "0")); // 冲红或者作废金额, 账务传过来是负数, CRM记的正数

        if ("0".equals(receiptType)) // 成功
        {
            // 更新申请单状态
            int result = ApproveReceiptInfoQry.updateApproveReceipt(receiptId, "2");

            if (result < 1)
            {
                retData.put("X_RESULTCODE", "1");
                retData.put("X_RESULTINFO", "没有找到对应的申请单号!");

                return IDataUtil.idToIds(retData);
            }
        }
        else if ("1".equals(receiptType)) // 失败
        {
            // 更新申请单状态
            int result = ApproveReceiptInfoQry.updateApproveReceipt(receiptId, "1");

            if (result < 1)
            {
                retData.put("X_RESULTCODE", "1");
                retData.put("X_RESULTINFO", "没有找到对应的申请单号!");

                return IDataUtil.idToIds(retData);
            }
        }
        else
        {
            // 查询申请单信息
            IDataset receiptList = ApproveReceiptInfoQry.qryReceiptByTaskId(receiptId);

            if (IDataUtil.isEmpty(receiptList))
            {
                retData.put("X_RESULTCODE", "1");
                retData.put("X_RESULTINFO", "没有找到对应的申请单号!");

                return IDataUtil.idToIds(retData);
            }

            IData receiptData = receiptList.getData(0);

            int payFee = Integer.parseInt(receiptData.getString("PAYEE", "0"));
            int returnFee = Integer.parseInt(receiptData.getString("RETURNFEE", "0"));

            if ((payFee - returnFee) < Math.abs(receiptFee))
            {
                retData.put("X_RESULTCODE", "1");
                retData.put("X_RESULTINFO", "冲红或者作废金额大于可冲红或者作废金额！");

                return IDataUtil.idToIds(retData);

            }
            else if ((payFee - returnFee) == Math.abs(receiptFee))
            {
                // 更新申请单状态
                ApproveReceiptInfoQry.updateApproveReceipt(receiptId, "4");
            }
            else
            {
                // 更新申请单状态
                ApproveReceiptInfoQry.updateApproveReceipt(receiptId, "3");
            }
        }

        retData.put("X_RESULTCODE", "0");
        retData.put("X_RESULTINFO", "成功");

        return IDataUtil.idToIds(retData);
    }

}
