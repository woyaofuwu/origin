
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.infomanage.InformationManageBean;
import com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata.InformationManageData;
import com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata.InformationManageRequestData;

public class InformationManageTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        InformationManageRequestData reqData = (InformationManageRequestData) btd.getRD();

        List<InformationManageData> infos = reqData.getFormationInfo();
        ;
        for (InformationManageData info : infos)
        {
            // 动态表格必须字段，区别提交数据的操作行为：(0：新增 1：删除 2:修改)
            // 信息管理界面只支持新增和删除两种操作
            String xTag = info.getTag();
            InformationManageBean bean = (InformationManageBean) BeanManager.createBean(InformationManageBean.class);
            // 新增
            if ("0".equals(xTag))
            {
                IData param = new DataMap();
                param.put("TRADE_ID", SeqMgr.getTradeId());// TRADE_ID，新增的数据，采用的新生成的trade_id
                param.put("USER_ID", btd.getRD().getUca().getUser().getUserId());
                param.put("NOTICE_CONTENT", info.getNoticeContent());// 提示内容
                param.put("TRADE_ATTR", "0");// trade_attr:业务属性 默认为： 0 - 普通业务
                param.put("ENABLE_TAG", info.getEnableTag());// ENABLE_TAG，提示标志： 0 - 不提示, 1 - 提示
                param.put("REMARK", info.getRemark());// 备注
                param.put("START_DATE", SysDateMgr.getSysTime());
                param.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
                param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                param.put("UPDATE_TIME", SysDateMgr.getSysTime());
                bean.dealinsertInformationInfo(param);
            }
            else if ("1".equals(xTag))// 删除
            {
                IData param = new DataMap();
                param.put("END_DATE", SysDateMgr.getSysTime());// 删除时只是将终止时间改为当前时间
                param.put("TRADE_ID", info.getTradeId());// 删除时只记录TRADE_ID,完工流程直接根据TRADE_ID去删
                bean.dealupInformationInfo(param);
            }
        }

    }

}
