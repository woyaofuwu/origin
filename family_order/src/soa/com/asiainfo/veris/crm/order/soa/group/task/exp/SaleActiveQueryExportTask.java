
package com.asiainfo.veris.crm.order.soa.group.task.exp;

import com.ailk.biz.impexp.ExportTaskExecutor;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.FeeUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class SaleActiveQueryExportTask extends ExportTaskExecutor
{

    @Override
    public IDataset executeExport(IData inParam, Pagination pg) throws Exception
    {
        String serialNumber = inParam.getString("cond_GROUP_SERIAL_NUMBER");
        if (serialNumber.length() == 8)
        {
            serialNumber = "898" + serialNumber;
        }

        String staffId = inParam.getString("cond_STAFF_ID");
        String deptId = inParam.getString("cond_DEPART_ID");
        String campnType = inParam.getString("cond_CAMPN_TYPE");
        String startDate = inParam.getString("cond_START_DATE");
        String endDate = inParam.getString("cond_END_DATE");

        IDataset saleactives = UserSaleActiveInfoQry.qrySaleActiveBySnStaffDeptCampnSDateEDate(serialNumber, staffId, deptId, campnType, startDate, endDate,pg);
        if (IDataUtil.isNotEmpty(saleactives))
        {
            for (int i = 0, size = saleactives.size(); i < size; i++)
            {
                IData saleactive = saleactives.getData(i);

                String oper_fee = dataMoneyDeal(saleactive.getString("OPER_FEE"));
                saleactive.put("OPER_FEE", oper_fee);

                String foregift = dataMoneyDeal(saleactive.getString("FOREGIFT"));
                saleactive.put("FOREGIFT", foregift);

                String advance_pay = dataMoneyDeal(saleactive.getString("ADVANCE_PAY"));
                saleactive.put("ADVANCE_PAY", advance_pay);

                // 判断是否是无返还类活动
                String productId = saleactive.getString("PRODUCT_ID");
                String packageId = saleactive.getString("PACKAGE_ID");
                boolean isReturn = CommparaInfoQry.queryCommparaByParaCode1ParaAttr(productId, packageId);
                if (isReturn)
                { // 无返还类
                    saleactive.put("ACTIVE_TYPE", "约定在网类");
                }
                else
                { // 返还类
                    saleactive.put("ACTIVE_TYPE", "返还/约定消费类");
                }
            }
        }
        return saleactives;
    }

    /**
     * 金额处理 分转化成元
     * 
     * @param str
     * @throws Exception
     */
    private String dataMoneyDeal(String str) throws Exception
    {
        String string = null;
        if (str == null || "".equals(str))
        {
            str = "0";
        }
        string = FeeUtils.Fen2Yuan(str);
        return string;
    }
}
