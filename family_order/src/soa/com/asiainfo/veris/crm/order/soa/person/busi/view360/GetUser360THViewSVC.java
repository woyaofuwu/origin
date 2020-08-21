
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradePlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSaleActive;

public class GetUser360THViewSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 查询业务历史用户--受理信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThAcceptInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        IData acceptInfo = new DataMap();
        String trade_id = input.getString("TRADE_ID", "").trim();
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        input.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        String history_query_type = input.getString("HISTORY_QUERY_TYPE", "");
        if ("G".equals(history_query_type))
        {
            acceptInfo = bean.qryThAcceptInfoCg(input);
        }
        else
        {
            acceptInfo = bean.qryThAcceptInfo(input);
        }

        outDataset.add(acceptInfo);
        return outDataset;
    }

    /**
     * 客户资料综合查询 - 业务历史信息信息 --联系信息 获取担保信息，从tf_b_trade_user表和TF_B_TRADE_CUST_PERSON表获取
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryThAssureInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        IData temp = new DataMap();
        String tradeId = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if (StringUtils.isBlank(tradeId))
        {
            return outDataset;
        }
        temp = bean.qryThAssureInfo(input);
        outDataset.add(temp);
        return outDataset;

    }

    /**
     * 查询业务历史用户--基本信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThBaseInfo(IData input) throws Exception
    {
        String trade_id = input.getString("TRADE_ID", "").trim();
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return new DatasetList();
        }
        IDataset outDataset = new DatasetList();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        outDataset = bean.qryThBaseInfo(input, null);
        return outDataset;
    }

    /**
     * 查询业务历史用户--国际漫游一卡多号
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThCardInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        outDataset = bean.qryThCardInfo(input);
        return outDataset;
    }
    
    /**
     * 查询业务历史用户--短信炸弹受理信息
     * 
     * @param input
     * @return IDataset
     * @throws Exception
     * add by  huangzl3
     */
    public IDataset qryThSmsBoomInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        outDataset = bean.qryThSmsBoomInfo(input);
        return outDataset;
    }
    
    /**
     * 查询业务历史用户--短信炸弹受理信息(新)
     * 
     * @param input
     * @return IDataset
     * @throws Exception
     * add by  huangzl3
     */
    public IDataset qryThSmsBoomInfoNew(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        outDataset = bean.qryThSmsBoomInfoNew(input);
        return outDataset;
    }
    /**
     * 短信炸弹防护办理历史查询接口
     * 
     * @param input
     * @return IDataset
     * @throws Exception
     * add by  wuhao5
     */
    public IData qrySMSBombStatHisInf(IData input) throws Exception
    {
    	IData result = new DataMap();
    	Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
    	result = bean.qrySMSBombStatHisInf(input); 
        return result;
    }
    
    


    /**
     * 查询业务历史用户-- 联系信息 从TF_B_TRADE_CUST_PERSON表中获取客户联系信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryThContactInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.qryThContactInfo(input);
        return outDataset;

    }

    /**
     * 查询业务历史用户--优惠变化
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThDiscntInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.qryThDiscntInfo(input);
        return outDataset;
    }

    /**
     * 查询业务历史用户--其他信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThOtherInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        input.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(trade_id));
        outDataset = bean.qryThOtherInfo(input);
        return outDataset;
    }

    /**
     * 查询业务历史用户--平台业务
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThPlatSvcInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.queryTradePlatSvcInfo(input);
        if(IDataUtil.isNotEmpty(outDataset)){
        	for(Object obj : outDataset){
        		IData data = (IData) obj;
        		IDataset spInfos = UpcCall.qrySpServiceSpInfo(data.getString("SERVICE_ID",""), BofConst.ELEMENT_TYPE_CODE_PLATSVC);
        		if(IDataUtil.isNotEmpty(spInfos)){
        			data.put("SP_CODE",spInfos.getData(0).getString("SP_CODE"));
        			data.put("SP_NAME",spInfos.getData(0).getString("SP_NAME"));
        			data.put("BIZ_CODE",spInfos.getData(0).getString("BIZ_CODE"));
        			data.put("BIZ_TYPE_CODE",spInfos.getData(0).getString("BIZ_TYPE_CODE"));
        		}
        	}
        }
        return outDataset;
    }

    /**
     * 查询业务历史用户--产品变化
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThProductInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.qryThProductInfo(input);
        return outDataset;
    }

    /**
     * 查询业务历史用户--关系变化
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThRelationInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.queryTradeRelationInfo(input);
        return outDataset;
    }

    /**
     * 查询业务历史用户--资源变化
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThResInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.queryTradeResInfo(input);
        return outDataset;
    }

    /**
     * 查询业务历史用户--积分交易明细
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThScoreInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.queryTradeScoreInfo(input);
        return outDataset;

    }

    /**
     * 查询业务历史用户--服务变化
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThSvcInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.qryThSvcInfo(input);
        return outDataset;
    }

    /**
     * 查询业务历史用户--服务状态变化
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryThSvcStatusInfo(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = input.getString("TRADE_ID", "").trim();
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        outDataset = bean.qryThSvcStatusInfo(input);
        return outDataset;
    }

    /**
     * 查询业务历史用户--其他台帐信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryTradeOtherInfo(IData param) throws Exception
    {
        IDataset outDataset = new DatasetList();
        String trade_id = param.getString("TRADE_ID", "").trim();

        if ("".equals(trade_id) || "null".equals(trade_id))
        {
            return outDataset;
        }
        Qry360THInfoBean bean = BeanManager.createBean(Qry360THInfoBean.class);
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        if ("G".equals(history_query_type))
        {
        	return bean.queryTradeInfoByHis(param);
        }
        else
        {
            return IDataUtil.idToIds(UTradeHisInfoQry.qryTradeHisByPk(trade_id, "0", null));
        }
    }

    /**
     * 根据Trade_Id,trade_id查询平台业务属性
     * 
     * @param pd
     * @param param
     * @return
     */
    public IDataset queryPlatAttrInfo(IData param) throws Exception
    {
        String tradeId = param.getString("TRADE_ID", "");
        if (StringUtils.isBlank(tradeId) || "null".equals(tradeId))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "TRADE_ID参数丢失，请重试!");
        }
        String serviceId = param.getString("SERVICE_ID", "");
        if (StringUtils.isBlank(serviceId))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "SERVICE_ID参数丢失，请重试!");
        }
        return TradePlatSvcInfoQry.queryPlatAttrInfo(tradeId.trim(), serviceId.trim());
    }

    /**
     * 根据Trade_Id查询预约宽带营销活动信息
     * 
     * @param pd
     * @param param
     * @return
     */
    public IDataset queryTradePreSaleActive(IData param) throws Exception
    {
        String tradeId = param.getString("TRADE_ID", "");
        if (StringUtils.isBlank(tradeId) || "null".equals(tradeId))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "TRADE_ID参数丢失，请重试!");
        }
        return TradeSaleActive.queryTradePreSaleActive(tradeId, this.getPagination());
    }
    
}
