
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.undo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;

/**
 * 
 * REQ201609050007_手机商城o2o功能需求
 * @author zhuoyingzhi
 * 20161101
 * 返销业务处理
 *
 */
public class UndoCancelCommendstaffLogInfoAction implements ITradeFinishAction
{

    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        String tradeId = mainTrade.getString("TRADE_ID");
        String userId = mainTrade.getString("USER_ID");
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId); 
        param.put("USER_ID", userId); 
        IDataset commendInfo = Dao.qryByCode("TL_B_COMMENDSTAFF_LOG", "SEL_COMMENDSTAFFLOG_BY_TRADE_ID", param);
        if(IDataUtil.isNotEmpty(commendInfo)){
            param.put("CANCEL_UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId()); 
            param.put("CANCEL_DEPART_ID", CSBizBean.getVisit().getDepartId());
            //返销时间
            param.put("CANCEL_TIME", SysDateMgr.getSysDate());
            param.put("REMARK", "已返销"); 
            Dao.executeUpdateByCodeCode("TL_B_COMMENDSTAFF_LOG", "UPD_COMMENDSTAFFLOG_BY_TRADE_ID", param);
        } 
    }

}
