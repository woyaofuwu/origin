
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

/**
 * 平台业务，互联网PassId处理 互联网默认开通 飞信，139邮箱，彩云平台需要保存PassId到TF_F_USER_OTHER
 * 
 * @author xiekl
 */
public class PlatInternetPassIdAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatOfficeData officeData = PlatOfficeData.getInstance(pstd.getElementId());

        // 非订购，跳出，或者非 飞信，139邮箱，彩云平台的业务，退出
        if (!PlatConstants.OPER_ORDER.equals(pstd.getOperCode()) || "16_23_72".indexOf(officeData.getBizTypeCode()) < 0)
        {
            return;
        }

        String serialNumber = uca.getSerialNumber();
        String passId = pstd.getRsrvStr10();
        boolean existFlag = false;// 是否已经存在NET_USER_INFO的订单数据，有则不再添加

        List<OtherTradeData> otherTradeList = btd.get("TF_B_TRADE_OTHER");
        if (otherTradeList != null && !otherTradeList.isEmpty())
        {
            for (int i = 0; i < otherTradeList.size(); i++)
            {
                OtherTradeData oldOtherTrade = otherTradeList.get(i);
                if ("NET_USER_INFO".equals(oldOtherTrade.getRsrvValueCode()))
                {
                    existFlag = true;
                }
            }
        }

        if (passId != null && passId.indexOf("PASS_ID:") > -1 && !existFlag)
        {
            IDataset passIdList = UserOtherInfoQry.getUserOtherByUseridRsrvcode(uca.getUserId(), "NET_USER_INFO", null);
           
            //有则更新，无则新增
            if (IDataUtil.isNotEmpty(passIdList) && passIdList.size() > 0)
            {
            	 OtherTradeData userOtherTradeData = new OtherTradeData(passIdList.getData(0));
            	 userOtherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            	 userOtherTradeData.setRsrvValue(passId.substring(8));
            	 btd.add(serialNumber, userOtherTradeData);
                
            }else
            {
            	 OtherTradeData otherTradeData = new OtherTradeData();
                 otherTradeData.setRsrvValueCode("NET_USER_INFO");
                 otherTradeData.setRsrvValue(passId.substring(8));
                 otherTradeData.setUserId(uca.getUserId());
                 otherTradeData.setStartDate(btd.getRD().getAcceptTime());
                 otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
                 otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
                 otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
                 otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值
                 otherTradeData.setRemark("互联网通行证号");
                 otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                 btd.add(serialNumber, otherTradeData);
            }
           
        }

    }

}
