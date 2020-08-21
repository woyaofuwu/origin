
package com.asiainfo.veris.crm.order.soa.person.common.action.trade.plat;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.extend.PlatSvcTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;

public class NewUserDefaultOpenAction implements ITradeAction
{

    private void bindDefaultOpen(IDataset serviceList, UcaData uca, BusiTradeData btd) throws Exception
    {
    	String tradeTypeCode = btd.getTradeTypeCode() ;
    	String brandCode = uca.getBrandCode();
    	//复机处理，默认开通飞信等三大基础服务，
    	if("310".equals(tradeTypeCode) && !brandCode.startsWith("G"))
    	{
    		return ;
    	}

        for (int i = 0; i < serviceList.size(); i++)
        {
            String serviceId = serviceList.getData(i).getString("PARAM_CODE", "");
            if (StringUtils.isBlank(serviceId))
            {
                continue;
            }

            PlatSvcTradeData newPstd = new PlatSvcTradeData();
            newPstd.setElementId(serviceId);
            newPstd.setModifyTag(BofConst.MODIFY_TAG_ADD);
            newPstd.setUserId(uca.getUserId());
            newPstd.setBizStateCode(PlatConstants.STATE_OK);
            newPstd.setProductId(PlatConstants.PRODUCT_ID);
            newPstd.setPackageId(PlatConstants.PACKAGE_ID);
            newPstd.setStartDate(btd.getRD().getAcceptTime());
            newPstd.setEndDate(SysDateMgr.END_DATE_FOREVER);
            String instId = SeqMgr.getInstId();
            newPstd.setInstId(instId);
            newPstd.setActiveTag("");// 主被动标记
            newPstd.setOperTime(SysDateMgr.getSysTime());
            newPstd.setPkgSeq("");// 批次号，批量业务时传值
            newPstd.setUdsum("");// 批次数量
            newPstd.setIntfTradeId("");
            newPstd.setOperCode(PlatConstants.OPER_ORDER);
            newPstd.setOprSource("08");
            newPstd.setIsNeedPf("1");
            PlatSvcTrade.dealFirstTime(newPstd, uca); // 处理首次订购时间，连带开的可能首次订购时间为空
            btd.add(uca.getSerialNumber(), newPstd);
        }

    }

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {

        UcaData uca = btd.getRD().getUca();

        if ("PWLW".equals(uca.getBrandCode()))
        {
            return;
        }
        
        if ("MOSP".equals(uca.getBrandCode()))
        {
            return;
        }
        
        if ("CPE1".equals(uca.getBrandCode()))
        {
            return;
        }
        
        IDataset m2mInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(uca.getUserId(), "HYYYKBATCHOPEN");
        if (IDataUtil.isNotEmpty(m2mInfos))
        {
          return;
        }
        
        String pType=btd.getRD().getPageRequestData().getString("PERSON_BATCH_TYPE", "");
        if("CREATEPREUSER_M2M".equals(pType)){
        	return;
        }
        
        List<PlatSvcTradeData> platSvcTradeDatas = uca.getUserPlatSvcs();
        IDataset bindDefaultOpenSvcConfigs = CommparaInfoQry.getCommparaByAttrCode1("CSM", "1110", "defaultopen", Route.CONN_CRM_CEN, null);

        if (platSvcTradeDatas.size() > 0)
        {
            // 查询开户默认需要绑定开通的serviceId
            for (int i = 0; i < platSvcTradeDatas.size(); i++)
            {
                PlatSvcTradeData pstd = platSvcTradeDatas.get(i);
                if (IDataUtil.isNotEmpty(bindDefaultOpenSvcConfigs))
                {

                    for (int j = 0; j < bindDefaultOpenSvcConfigs.size(); j++)
                    {
                        IData bindConfig = bindDefaultOpenSvcConfigs.getData(j);
                        String bindServiceId = bindConfig.getString("PARAM_CODE", "0");
                        // 新增的优惠
                        if (bindServiceId.equals(pstd.getElementId()))
                        {
                            bindDefaultOpenSvcConfigs.remove(j);
                            j--;
                        }

                    }
                    // 查询开户默认需要绑定开通的serviceId
                }
            }
        }

        this.bindDefaultOpen(bindDefaultOpenSvcConfigs, uca, btd);

    }

}
