
package com.asiainfo.veris.crm.order.soa.group.fee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

public class CreateOneOffFeeCloudBean extends CSBizBean
{

    /**
     * 保存费用信息
     * @param map
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2017-11-24
     */
    public IDataset crtTrade(IData map) throws Exception
    {
        IData retData = new DataMap();

        String custId = map.getString("CUST_ID");

        // 查询集团客户信息
        IData grpCustData = UcaInfoQry.qryGrpInfoByCustId(custId);

        if (IDataUtil.isEmpty(grpCustData))
        {
            CSAppException.apperr(CustException.CRM_CUST_996, custId);
        }

        String flowId = SeqMgr.getFeeFlowId();

        // 保存费用信息
        IData feeData = new DataMap();
        feeData.put("FLOWID", flowId);
        feeData.put("GROUP_ID", grpCustData.getString("GROUP_ID"));
        feeData.put("CUST_NAME", grpCustData.getString("CUST_NAME"));
        feeData.put("ACCEPT_TIME", SysDateMgr.getSysTime());
        feeData.put("UPDATE_TIME", SysDateMgr.getSysTime());
        feeData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        feeData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        feeData.put("RSRV_TAG1", "1");			//0-非移动云费用，1-移动云费用

        Dao.insert("TF_B_GRP_ONEOFF_FEE", feeData);

        // 保存费用明细信息
        IDataset feeSubList = map.getDataset("FEE_SUB_LIST");
        for (int i = 0, row = feeSubList.size(); i < row; i++)
        {
            IData feeSubData = feeSubList.getData(i);

            String tag = feeSubData.getString("tag");

            feeSubData.put("FLOWID", flowId);
            feeSubData.put("UPDATE_TIME", SysDateMgr.getSysTime());
            feeSubData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            feeSubData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

            if (TRADE_MODIFY_TAG.Add.getValue().equals(tag)) // 新增
            {
                String noteId = SeqMgr.getFeeNoteId();
                feeSubData.put("NOTE_ID", noteId);

                Dao.insert("TF_B_GRP_ONEOFF_FEE_SUB", feeSubData);
            }
            else if (TRADE_MODIFY_TAG.MODI.getValue().equals(tag)) // 修改
            {
                Dao.update("TF_B_GRP_ONEOFF_FEE_SUB", feeSubData, new String[]
                { "NOTE_ID" });
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(tag)) // 删除
            {
                Dao.delete("TF_B_GRP_ONEOFF_FEE_SUB", feeSubData, new String[]
                { "NOTE_ID" });
            }
        }

        // 设置返回数据
        retData.put("ORDER_ID", flowId);

        return IDataUtil.idToIds(retData);
    }
}
