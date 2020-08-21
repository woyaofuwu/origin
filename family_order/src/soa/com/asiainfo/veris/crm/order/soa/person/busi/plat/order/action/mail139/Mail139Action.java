
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.mail139;

import java.util.ArrayList;
import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.PlatReload;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

public class Mail139Action implements IProductModuleAction
{

    /**
     * 139邮箱订购时触发，做标准版或VIP版订购时，要退订已经存在的VIP版或者标准版
     */
    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        String mail139 = PlatReload.mail139Standard + "," + PlatReload.mail139Vip;
        if (mail139.indexOf(pstd.getElementId()) < 0)
        {
            return;
        }

        String inModeCode = CSBizBean.getVisit().getInModeCode();
        if ("0".equals(inModeCode) || "".equals(pstd.getOprSource()))
        {
            pstd.setOprSource("11");
        }

        List<PlatSvcTradeData> relaPstds = new ArrayList<PlatSvcTradeData>();
        relaPstds.addAll(uca.getUserPlatSvcByServiceId(PlatReload.mail139Standard));
        relaPstds.addAll(uca.getUserPlatSvcByServiceId(PlatReload.mail139Vip));

        int size = relaPstds.size();
        boolean[] addFlag = new boolean[size];// 保存订购标识
        for (int i = 0; i < size; i++)
        {
            PlatSvcTradeData mailSvc = relaPstds.get(i);

            // 139邮箱vip版本 收费版不能在界面一次订购 否则优惠绑定会出问题
            if (mailSvc.getModifyTag().equals(BofConst.MODIFY_TAG_ADD))
            {
                addFlag[i] = true;
            }

            if (mailSvc.getElementId().equals(pstd.getElementId()) || !mailSvc.getModifyTag().equals(BofConst.MODIFY_TAG_USER))
            {
                continue;
            }
            // 邮箱变更操作为资料修改操作
            pstd.setOperCode(PlatConstants.OPER_USER_DATA_MODIFY);
            pstd.setStartDate(SysDateMgr.getSysTime());

            // 将原来的退订
            PlatSvcTradeData newPstd = mailSvc.clone();
            newPstd.setModifyTag(BofConst.MODIFY_TAG_DEL);
            newPstd.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
            newPstd.setBizStateCode(PlatConstants.STATE_CANCEL);
            newPstd.setOprSource(pstd.getOprSource());
            newPstd.setIsNeedPf("0");
            newPstd.setActiveTag("2");// 被动
            newPstd.setOperTime(btd.getRD().getAcceptTime());
            newPstd.setRemark("订购高级邮箱须退订原有标准邮箱");
            newPstd.setEndDate(SysDateMgr.getLastSecond(pstd.getStartDate()));
            btd.add(uca.getSerialNumber(), newPstd);
        }

        int count = 0;
        for (int j = 0; j < addFlag.length; j++)
        {
            if (addFlag[j])
            {
                count++;
            }

            // 有vip版本 收费版同时订购
            if (count >= 2)
            {
                CSAppException.apperr(PlatException.CRM_PLAT_74, "不能同时订购邮箱标准版和邮箱VIP版");
            }
        }

    }

}
