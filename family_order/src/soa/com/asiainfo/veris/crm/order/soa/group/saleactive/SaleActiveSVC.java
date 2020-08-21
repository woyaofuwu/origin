
package com.asiainfo.veris.crm.order.soa.group.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleScoreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class SaleActiveSVC extends GroupOrderService
{
    /**
     * 集团营销活动提交
     */
    private static final long serialVersionUID = 1L;

    public IDataset crtTrade(IData inparam) throws Exception
    {
        SaleActiveBean saleActive = new SaleActiveBean();
        inparam.put("TRADE_TYPE_CODE", "3606");
        return saleActive.crtTrade(inparam);
    }

    public IData CheckSaleElement(IData inparam) throws Exception
    {
        SaleActiveBean saleActive = new SaleActiveBean();
        return saleActive.CheckSaleElement(inparam);
    }

    public IDataset queryDiscntsByPkgIdEparchy(IData inparam) throws Exception
    {
        IDataset infos = SaleActiveUtil.getSaleActivesByPackageIdAndElementType(inparam.getString("PACKAGE_ID"), BofConst.ELEMENT_TYPE_CODE_DISCNT);

        if (IDataUtil.isEmpty(infos))
            return null;
        
        
        for (int i = 0, size = infos.size(); i < size; i++)
        {
            IData info = infos.getData(i);
            info.putAll(inparam);
            String discntCode = info.getString("DISCNT_CODE","");
            String packageId = info.getString("PACKAGE_ID","");
            //没有看到有捞取生效方式,特殊处理一下
            IDataset commParams = CommparaInfoQry.getCommNetInfo("CSM", "4010", packageId);
            if(IDataUtil.isNotEmpty(commParams))
            {
            	String relObject = info.getString("REL_OBJECT","");
            	String relObjectId = info.getString("REL_OBJECT_ID","");
            	IDataset enableInfos = UpcCall.qryEnableModeInfoByRelObjectAndId("3", relObjectId);
            	if(IDataUtil.isNotEmpty(enableInfos))
            	{
            		IData enableInfo = enableInfos.getData(0);
            		info.putAll(enableInfo);
            	}
            }
            IData dates = setStartEndDate(info);
            info.put("DATES", dates.toString());
        }
        return infos;
    }

    public IData setStartEndDate(IData inparam) throws Exception
    {
        SaleActiveBean saleActive = new SaleActiveBean();
        return saleActive.setStartEndDate(inparam);
    }

    /**
     * 集团营销活动终止
     * 
     * @param inparam
     * @return
     * @throws Exception
     */
    public IDataset crtStopTrade(IData inparam) throws Exception
    {
        SaleActiveStopBean saleActiveStop = new SaleActiveStopBean();
        inparam.put("TRADE_TYPE_CODE", "3607");
        return saleActiveStop.crtTrade(inparam);
    }

    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset querySaleScoresByPackageId(IData input) throws Exception
    {
        IData cond = new DataMap();
        String packageId = input.getString("PACKAGE_ID");
        cond.put("PACKAGE_ID", packageId);
        String eparchyCode = input.getString("EPARCHY_CODE");
        //String serialNumber = input.getString("SERIAL_NUMBER");

        IDataset scoreInfos = SaleScoreInfoQry.queryByPkgIdEparchy(packageId, eparchyCode);
        
        if (IDataUtil.isEmpty(scoreInfos)){
            return null;
        }

        return scoreInfos;
    }
}
