
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plugin.orderspecdeal;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.plugin.IPlugin;

public class DevelopStaffRecord implements IPlugin
{

    public void deal(List<BusiTradeData> btds, IData input) throws Exception
    {
        String staffIds = input.getString("DEVELOPED_STAFF_ID");
        if (StringUtils.isNotBlank(staffIds))
        {
            IDataset developInfoList = new DatasetList();
            String[] arrDevelopStaffId = staffIds.split(",");
            int size = arrDevelopStaffId.length;
            for (int i = 0; i < size; i++)
            {
                String developStaffId = arrDevelopStaffId[i];
                IData staffInfo = UStaffInfoQry.qryStaffInfoByPK(developStaffId);
                if (IDataUtil.isEmpty(staffInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "查不到员工信息[" + developStaffId + "]");
                }

                OrderDataBus dataBus = DataBusManager.getDataBus();
                IData developInfo = new DataMap();
                developInfo.put("TRADE_ID", dataBus.getOrderId());
                developInfo.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(dataBus.getOrderId()));
                developInfo.put("INST_ID", SeqMgr.getInstId());
                developInfo.put("DEVELOP_STAFF_ID", developStaffId);
                developInfo.put("DEVELOP_DEPART_ID", staffInfo.getString("DEPART_ID"));
                developInfo.put("DEVELOP_CITY_CODE", staffInfo.getString("CITY_CODE"));
                developInfo.put("DEVELOP_EPARCHY_CODE", staffInfo.getString("EPARCHY_CODE"));
                developInfo.put("DEVELOP_DATE", dataBus.getAcceptTime());
                developInfo.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                developInfo.put("UPDATE_TIME", dataBus.getAcceptTime());
                developInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                developInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());

                developInfoList.add(developInfo);
            }

            Dao.insert("TF_B_TRADE_DEVELOP", developInfoList);
        }
    }

}
