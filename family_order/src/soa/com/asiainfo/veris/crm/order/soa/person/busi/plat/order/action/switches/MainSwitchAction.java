
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.switches;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

/**
 * 谢康林 总开关处理动作
 * 
 * @author xiekl
 */
public class MainSwitchAction implements IProductModuleAction
{

    public final void dealFirstTime(PlatSvcTradeData pstd, UcaData uca) throws Exception
    {
        String userId = pstd.getUserId();
        String serviceId = pstd.getElementId();
        String firstDate = "";
        String firstMonthDate = "";

        // 查询首次订购
        IDataset firstTimeDatass = BofQuery.getUserPlatSvcFirstDate(userId, serviceId, uca.getUserEparchyCode());
        if (firstTimeDatass != null && firstTimeDatass.size() > 0)
        {
            IData firstData = firstTimeDatass.getData(0);
            firstDate = firstData.getString("FIRST_DATE", "");
            if (!"".equals(firstDate))
            {
                pstd.setFirstDate(firstDate);
            }
            else
            {
                pstd.setFirstDate(pstd.getStartDate());
            }
        }
        else
        {
            pstd.setFirstDate(pstd.getStartDate());
        }

        // 查询本月首次订购
        IDataset firstMonthDatas = BofQuery.getUserPlatSvcFirstDateMon(userId, serviceId, uca.getUserEparchyCode());
        if (firstMonthDatas != null && firstMonthDatas.size() > 0)
        {
            IData firstData = firstMonthDatas.getData(0);
            firstMonthDate = firstData.getString("FIRST_DATE_MON", "");
            if (!"".equals(firstMonthDate))
            {
                pstd.setFirstDateMon(firstMonthDate);
            }
            else
            {
                pstd.setFirstDateMon(pstd.getStartDate());
            }
        }
        else
        {
            pstd.setFirstDateMon(pstd.getStartDate());
        }
    }

    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        PlatSvcData psd = (PlatSvcData) pstd.getPmd();

        if (PlatConstants.OPER_SERVICE_OPEN.equals(pstd.getOperCode()))
        {
            return;
        }
        // 针对分开关进行处理
        else if (PlatConstants.OPER_SERVICE_CLOSE.equals(pstd.getOperCode()))
        {
            IDataset switches = StaticUtil.getStaticList("PLAT_SWITCH");
            int size = switches.size();
            for (int i = 0; i < size; i++)
            {
                IData platSwitch = switches.getData(i);
                String serviceId = platSwitch.getString("DATA_ID");

                if (!"98009044".equals(serviceId))
                {
                    List userPlatSvcList = uca.getUserPlatSvcByServiceId(serviceId);
                    PlatSvcTradeData userPlatSvc = null;
                    if (userPlatSvcList != null && !userPlatSvcList.isEmpty())
                    {
                        userPlatSvc = (PlatSvcTradeData) userPlatSvcList.get(0);
                    }

                    if (userPlatSvc == null || BofConst.MODIFY_TAG_DEL.equals(userPlatSvc.getModifyTag()))
                    {
                        IData temp = new DataMap();
                        temp.put("SERVICE_ID", serviceId);
                        temp.put("OPER_CODE", PlatConstants.OPER_SERVICE_CLOSE);
                        PlatSvcData platSvcData = new PlatSvcData(temp);

                        PlatSvcTradeData platSvcSwitch = new PlatSvcTradeData();
                        platSvcSwitch.setElementId(serviceId);
                        platSvcSwitch.setOperCode(PlatConstants.OPER_SERVICE_CLOSE);
                        platSvcSwitch.setUserId(uca.getUserId());
                        platSvcSwitch.setModifyTag(BofConst.MODIFY_TAG_ADD);
                        platSvcSwitch.setBizStateCode(PlatConstants.STATE_OK);
                        platSvcSwitch.setProductId(PlatConstants.PRODUCT_ID);
                        platSvcSwitch.setPackageId(PlatConstants.PACKAGE_ID);
                        platSvcSwitch.setStartDate(btd.getRD().getAcceptTime());
                        platSvcSwitch.setEndDate(SysDateMgr.END_DATE_FOREVER);
                        String instId = SeqMgr.getInstId();
                        platSvcSwitch.setInstId(instId);
                        platSvcSwitch.setRemark(psd.getRemark());
                        platSvcSwitch.setActiveTag(psd.getActiveTag());// 主被动标记
                        platSvcSwitch.setAllTag(psd.getAllTag());
                        platSvcSwitch.setOperTime(btd.getRD().getAcceptTime());
                        platSvcSwitch.setPkgSeq(psd.getPkgSeq());// 批次号，批量业务时传值
                        platSvcSwitch.setUdsum(psd.getUdSum());// 批次数量
                        platSvcSwitch.setIntfTradeId(psd.getIntfTradeId());
                        platSvcSwitch.setOprSource(psd.getOprSource());
                        platSvcSwitch.setIsNeedPf("0");// 总开关关时，分开关关不需要发报文
                        this.dealFirstTime(platSvcSwitch, uca);
                        platSvcSwitch.setPmd(platSvcData);

                        btd.add(uca.getSerialNumber(), platSvcSwitch);

                        new SwitchAction().executeProductModuleAction(platSvcSwitch, uca, btd);
                    }

                }

            }
        }
    }

}
