
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.chnl.ChnlInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.DepartInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;
import org.apache.log4j.Logger;

/**
 * 有异常开户标志"DIFF_AREA_TAG" 记录到other表
 * 
 * @author sunxin
 */
    public class CheckOpenAreaAction implements ITradeAction
{
    private static transient Logger logger = Logger.getLogger(CheckOpenAreaAction.class);

    public void executeAction(BusiTradeData btd) throws Exception
    {
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckOpenAreaAction#executeAction >>>>>>>>>>>>>>>>>>");
        try {
            UcaData uca = btd.getRD().getUca();
            logger.debug("=========PageRequestData=========" + btd.getRD().getPageRequestData());
            //异地开户标志 为1时则为异地开户
            String DIFF_AREA_TAG = btd.getRD().getPageRequestData().getString("DIFF_AREA_TAG", "0");
            if ("1".equals(DIFF_AREA_TAG)) {

                OtherTradeData otherTradeData = new OtherTradeData();
                otherTradeData.setUserId(uca.getUserId());
                //异地开户标志
                otherTradeData.setRsrvValueCode("DIFF_AREA_TAG");
                //开户号码
                otherTradeData.setRsrvValue(btd.getRD().getUca().getSerialNumber());
                //开户工号
                otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
                //工号部门
                otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
                //开户时间
                otherTradeData.setRsrvStr1(SysDateMgr.getSysDate());
                //开户工号
                otherTradeData.setRsrvStr2(CSBizBean.getVisit().getStaffId());

                IDataset chnlInfos = ChnlInfoQry.getGlobalChlId(CSBizBean.getVisit().getDepartId());
                IData chlInfo = new DataMap();
                if (IDataUtil.isNotEmpty(chnlInfos)) {
                    chlInfo = chnlInfos.getData(0);
                    //渠道编码
                    otherTradeData.setRsrvStr3(chlInfo.getString("CHNL_KIND_ID"));
                    //渠道名称
                    otherTradeData.setRsrvStr4(chlInfo.getString("CHNL_NAME"));
                } else {
                    otherTradeData.setRsrvStr3("NOCHNLINFO");
                }
                IDataset areaInfos = StaffInfoQry.getStaffAreaInfoByID(CSBizBean.getVisit().getStaffId());
                IData areaInfo = new DataMap();
                if (IDataUtil.isNotEmpty(areaInfos)) {
                    areaInfo = areaInfos.getData(0);
                    //工号归属市县
                    otherTradeData.setRsrvStr5(areaInfo.getString("AREA_NAME"));
                    //工号归属市县编码
                    IData cityInfo = UStaffInfoQry.qryStaffInfoByPK(CSBizBean.getVisit().getStaffId());
                    otherTradeData.setRsrvStr7(cityInfo.getString("CITY_CODE"));
                } else {
                    otherTradeData.setRsrvStr5("NOAREAINFO");
                }
                //工号的部门编号,对应渠道编号,发送短信的依据
                IDataset departInfos = DepartInfoQry.getDepartById(CSBizBean.getVisit().getDepartId());
                if (IDataUtil.isNotEmpty(departInfos)) {
                    IData departInfo = departInfos.getData(0);
                    otherTradeData.setRsrvStr8(departInfo.getString("DEPART_CODE"));
                } else {
                    otherTradeData.setRsrvStr8("NODEPARTINFO");
                }
                //工号开户所在市县
                otherTradeData.setRsrvStr6(btd.getRD().getPageRequestData().getString("OPEN_AREA_NAME"));
                //更改标志
                otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                //记录有效时间
                otherTradeData.setStartDate(SysDateMgr.getSysDate());
                otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
                //记录的序号
                otherTradeData.setInstId(SeqMgr.getInstId());
                logger.debug("=========otherTradeData=========" + otherTradeData);
                btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
            }
        }catch (Exception e){
            logger.debug("=========CheckOpenAreaAction ERROR MSG=========" + e.getMessage());
        }
        logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 退出 CheckOpenAreaAction#executeAction >>>>>>>>>>>>>>>>>>");
    }
}
