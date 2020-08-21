
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.trade;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.plat.order.requestdata.PlatReqData;

public class PlatTrade extends BaseTrade implements ITrade
{
	private static Logger logger = Logger.getLogger(PlatTrade.class);
    private PlatSvcData buildAllCancelData(PlatSvcTradeData pstd, PlatOfficeData officeData, UcaData uca) throws Exception
    {
        PlatSvcData psd = new PlatSvcData();
        psd.setElementId(pstd.getElementId());
        psd.setInstId(pstd.getInstId());
        psd.setOperCode(PlatConstants.OPER_CANCEL_ORDER);
        psd.setOfficeData(officeData);
        psd.setAllTag("01");// 全退订
        psd.setActiveTag("0");
        psd.setOprSource("08");
        return psd;
    }

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        PlatReqData prd = (PlatReqData) btd.getRD();
        UcaData uca = prd.getUca();

        List<ProductModuleData> psds = new ArrayList<ProductModuleData>();
        psds.addAll(prd.getPlatSvcDatas());

        List<String> allCancels = prd.getAllPlatCancels();
        if (allCancels != null && allCancels.size() > 0)
        {
            int size = allCancels.size();
            for (int i = 0; i < size; i++)
            {

                String allCancelBizTypeCode = allCancels.get(i);
                psds.addAll(this.getAllCancelPlatSvcs(allCancelBizTypeCode, uca));
            }
        }
        ProductModuleCreator.createProductModuleTradeData(psds, btd);
		/**
		 * REQ201709210005_关于新增一级电渠套餐办理记录入库的需求
		 * @author zhuoyingzhi
		 * @date 20171013
		 */
		IData  pageReq=btd.getRD().getPageRequestData();
		logger.debug("-----PlatTrade-----"+pageReq);
		logger.debug("-----PlatTrade-RSRV_STR9----"+pageReq.getString("RSRV_STR9",""));
		if("BIP3A211_T3000214_1_0".equals(pageReq.getString("RSRV_STR9",""))){
			//移动商城
			//业务类型标识
			btd.getMainTradeData().setRsrvStr9("BIP3A211_T3000214_1_0");
			//渠道标识
			btd.getMainTradeData().setRsrvStr8(pageReq.getString("RSRV_STR8",""));
            //全网资费编码
            btd.getMainTradeData().setRsrvStr10(pageReq.getString("RSRV_STR10",""));
		}
		/***************end*************************/	
    }

    private List<PlatSvcData> getAllCancelPlatSvcs(String bizTypeCode, UcaData uca) throws Exception
    {
        List<PlatSvcTradeData> pstds = uca.getUserPlatSvcs();
        List<PlatSvcData> result = new ArrayList<PlatSvcData>();
        if (pstds != null && pstds.size() > 0)
        {
            int size = pstds.size();
            for (int i = 0; i < size; i++)
            {
                PlatSvcTradeData pstd = pstds.get(i);
                PlatOfficeData officeData = null;
                
                try
                {
                	 officeData = PlatOfficeData.getInstance(pstd.getElementId());
                }catch(Exception e)
                {
                	
                }
                
                if(officeData == null)
                {
                	continue;
                }
                
                String bizCode = officeData.getBizCode();
                // 默认开通不能退订
                if ("IIC".equals(bizCode) || "+MAILBZ".equals(bizCode) || "YDCY".equals(bizCode))
                {
                    continue;
                }

                if ("DSMP".equals(bizTypeCode) || "MUSC".equals(bizTypeCode))
                {
                    if (officeData.getOrgDomain().equals(bizTypeCode))
                    {
                        result.add(this.buildAllCancelData(pstd, officeData, uca));
                    }
                }
                else if (bizTypeCode.indexOf("SP") >= 0)
                {
                    String spCode = bizTypeCode.split(":")[1];
                    if (officeData.getSpCode().equals(spCode) && "DSMP".equals(officeData.getOrgDomain()))
                    {
                        PlatSvcData psd = this.buildAllCancelData(pstd, officeData, uca);
                        psd.setAllTag("02");
                        result.add(psd);
                    }
                }
                else if (bizTypeCode.equals("ALL_CANCEL")) // 如果是全部退订,处理接口SS.PlatCancelAll.tradeReg
                {
                    result.add(this.buildAllCancelData(pstd, officeData, uca));
                }
                else if (bizTypeCode.equals(officeData.getBizTypeCode()))
                {
                    result.add(this.buildAllCancelData(pstd, officeData, uca));
                }
            }
            if (result.size() <= 0)
            {
                // 抛错
            }
        }
        return result;
    }

}
