
package com.asiainfo.veris.crm.iorder.web.merch.merchorder;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class MerchOrder extends PersonBasePage
{
    public abstract void setInfo(IData info);

    /**
     * 号码查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MerchChangeProductSVC.getUserProduct", data);
//        this.send4ALog(data.getString("SERIAL_NUMBER"), "crm9211110QUERY", "", "0");
        this.setAjax(result);
    }

    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String regsvc = data.getString("REG_SVC");
        IDataset dataset = CSViewCall.call(this, regsvc, data);
        String tradeId = dataset.getData(0).getString("TRADE_ID");

        String tradeTypeCode = data.getString("TRADE_TYPE_CODE", "110");

        if ("110".equals(tradeTypeCode))
        {
//            this.send4ALog(data.getString("SERIAL_NUMBER"), "crm9211110SUBMIT", tradeId, "0");
        }
        else
        {
//            this.send4ALog(data.getString("SERIAL_NUMBER"), getMenuId() + tradeTypeCode + "SUBMIT", tradeId, "0");
        }
        setAjax(dataset);
    }

    public void addShoppingCartConfirm(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = CSViewCall.callone(this, "SS.MerchChangeProductSVC.addShoppingCartConfirm", data);
        this.setAjax(result);
    }

    public void getOrderAntinomyOffers(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset result = CSViewCall.call(this, "SS.MerchChangeProductSVC.getOrderAntinomyOffers", data);
        this.setAjax(result);
    }
    public void changeProductForStrom(IRequestCycle cycle) throws Exception {
		IData data = this.getData();

		IData result = CSViewCall.callone(this, "SS.ChangeProductSVC.changeProductElementForStrom", data);

		this.setAjax(result);

	}
    
    /**
     * 业务规则校验
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkBeforeTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        data.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));

        IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
        
        if("110".equals(data.getString("TRADE_TYPE_CODE")))
        {
        	IDataset result = CSViewCall.call(this, "SS.ChangeProductSVC.afterSubmitSnTipsInfo", data);

            if (IDataUtil.isNotEmpty(result))
            {
                for (int i = 0; i < result.size(); i++)
                {
                    infos.getData(0).getDataset("TIPS_TYPE_TIP").add(result.getData(i));
                }
            }
        }

        setAjax(infos.getData(0));
    }
    
    public void checkElementParamIntegrality(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	IData result = CSViewCall.callone(this, "SS.MerchChangeProductSVC.checkElementParamIntegrality", data);
    	setAjax(result);
    }
    
    /**
     * 平台服务属性信息
     * @param cycle
     * @throws Exception
     */
    public void addPlatAttr(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	IDataset attrInfo=new DatasetList();
        // 元素添加
        String serviceId = data.getString("OFFER_CODE");

        IData map = new DataMap();
        map.put("SERVICE_ID", serviceId);
        IDataset results = CSViewCall.call(this, "CS.PlatComponentSVC.getPlatSvcByServiceId", map);
        
        if (IDataUtil.isEmpty(results))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_74, "根据平台服务 ID，查询不到对应的平台服务，可能原因是搜索服务配置的数据库地址与APP配置不一致造成");
        }

        IData service = results.getData(0);
        
        /**校验该服务是否为过渡期服务  mod:zhangbo18 begin*/
        map.clear();
        map.put("OLD_SP_CODE", service.get("SP_CODE"));
        map.put("OLD_BIZ_CODE", service.get("BIZ_CODE"));
        IDataset offData = CSViewCall.call(this, "CS.PlatComponentSVC.getOffData", map);
        //如果存在配置数据，说明此服务为过渡服务，不能进行订购
        if (null != offData && offData.size() > 0){
        	CSViewException.apperr(PlatException.CRM_PLAT_90);
        }
        
        map.clear();
        map.put("NEW_SP_CODE", service.get("SP_CODE"));
        map.put("NEW_BIZ_CODE", service.get("BIZ_CODE"));
        offData = CSViewCall.call(this, "CS.PlatComponentSVC.getNewOffData", map);
        //如果存在配置数据，说明此服务为过渡服务，需要检查是否存在老服务的订购关系
        if (null != offData && offData.size() > 0){
        	IData officeD = offData.getData(0);
        	map.clear();
            map.put("SP_CODE", officeD.getString("OLD_SP_CODE"));
            map.put("BIZ_CODE", officeD.getString("OLD_BIZ_CODE"));
            map.put("BIZ_TYPE_CODE", officeD.getString("OLD_BIZ_TYPE"));
            offData = CSViewCall.call(this, "CS.UserPlatSvcInfoQrySVC.querySvcAllBySpCodeAndBizCode", map);
            
            if (null != offData && offData.size() > 0){
            	officeD = offData.getData(0);
            	data.put("SERVICE_ID", officeD.getString("SERVICE_ID", officeD.getString("OFFER_CODE")));
            	offData = CSViewCall.call(this, "CS.UserPlatSvcInfoQrySVC.querySvcInfoByUserIdAndSvcIdPf", data);
            	
            	if (null != offData && offData.size() > 0){
                	CSViewException.apperr(PlatException.CRM_PLAT_91);
            	}
            }
        }

        /**校验该服务是否为过渡期服务  mod:zhangbo18 end*/


        if("74".equals(service.getString("BIZ_TYPE_CODE"))){
        	CSViewException.apperr(PlatException.CRM_PLAT_3032);
        }
        //和教育不允许订购 end
        
        if ("17".equals(service.getString("BIZ_TYPE_CODE")))
        {
            if ("BJDP".equals(service.getString("ORG_DOMAIN")))
            {
                service.put("BIZ_TYPE_NAME", "北京通用下载");
            }
            else
            {
                service.put("BIZ_TYPE_NAME", "广东通用下载");
            }
        }
        else
        {
            service.put("BIZ_TYPE_NAME", this.getPageUtil().getStaticValue("BIZ_TYPE_CODE", service.getString("BIZ_TYPE_CODE")));
        }
        IDataset opers = new DatasetList();

        // 查询TD_B_PLATSVC.RSRV_STR3，如果是0,2允许订购，其他不允许订购
        String registerOper = service.getString("BIZ_PROCESS_TAG", "000000000000000000000000000000").substring(0, 1);// 第一位注册
        String orderOper = service.getString("BIZ_PROCESS_TAG", "000000000000000000000000000000").substring(5, 6);// 第6位订购
        // 如果订购和注册都不支持，则抛异常
        if (!"1".equals(registerOper) && !"1".equals(orderOper))
        {
            CSViewException.apperr(PlatException.CRM_PLAT_84);
        }

        String rsrvStr4 = service.getString("RSRV_STR4", "");
        String staffIdChar = String.valueOf(getVisit().getStaffId().charAt(4));
        if (!"".equals(rsrvStr4) && rsrvStr4.indexOf(staffIdChar) >= 0)
        {
            CSViewException.apperr(PlatException.CRM_PLAT_86);
        }

//        IData oper = new DataMap();
//        oper.put("OPER_CODE", "06");
//        oper.put("OPER_NAME", "订购");
//        opers.add(oper);
//
//        if ("DSMP".equals(service.getString("ORG_DOMAIN")) || "MRBT".equals(service.getString("ORG_DOMAIN")) || "MEBP".equals(service.getString("ORG_DOMAIN")))
//        {
//            IData giftOper = new DataMap();
//            giftOper.put("OPER_CODE", "GIFT");
//            giftOper.put("OPER_NAME", "赠送");
//            opers.add(giftOper);
//        }
        service.put("SUPPORT_OPERS", opers);
        service.put("OPER_NAME", "订购");
        service.put("BIZ_STATE", "正常");
        service.put("SERVICE_ID", serviceId);
        attrInfo=service.getDataset("ATTR_PARAM");

        this.setAjax(attrInfo);
    }
    
    public void checkShoppingCartForProduct(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
    	CSViewCall.call(this, "SS.MerchChangeProductSVC.checkShoppingCartForProduct", data);
    }
}
