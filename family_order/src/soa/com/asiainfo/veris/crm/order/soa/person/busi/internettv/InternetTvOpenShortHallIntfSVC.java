package com.asiainfo.veris.crm.order.soa.person.busi.internettv;

import java.util.StringTokenizer;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;


public class InternetTvOpenShortHallIntfSVC extends CSBizService
{
	private static final long serialVersionUID = 1L;


	/**
	 * 校验用户信息（包括产品信息初始化、宽带信息）
	 */
    public IData checkUserForOpenInternetTV(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER");
        String wSerialNumber = "KD_" + serialNumber;
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        
        //判断用户是否含有有效的平台业务
        IDataset platSvcInfos = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "51");//biz_type_code=51为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfos))
        {
        	CSAppException.appError("2017091201", "用户当前存在生效的魔百和平台业务，不能再办理。");
        }
        
        IDataset platSvcInfostow = UserPlatSvcInfoQry.getPlatSvcByUserBizType(userId, "86");//biz_type_code=51为互联网电视类的平台服务
        if (IDataUtil.isNotEmpty(platSvcInfostow))
        {
        	CSAppException.appError("2018113001", "用户当前存在生效的魔百和平台业务，不能再办理。");
        }

        // 是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
        String wideState = "0"; // 0-系统异常
        if (IDataUtil.isEmpty(wideInfos))
        {
            // .1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
            	CSAppException.appError("2017091202", "用户没有开通宽带, 不能办理该业务。");
            }
            
            // .2校园宽带不能受理
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
            
            if (IDataUtil.isEmpty(wideNetInfo))
            {
            	CSAppException.appError("2017091203", "该用户宽带资料信息不存在！");
            }
            
            if (StringUtils.equals("4", wideNetInfo.getString("RSRV_STR2","")))
            {
            	CSAppException.appError("2017091204", "校园宽带不能办理互联网电视业务！");
            }
            
            // .3校验是否为不允许的带宽
            if (IDataUtil.isNotEmpty(UserSvcInfoQry.checkInternetTvWide(wSerialNumber)))
            {
            	CSAppException.appError("2017091205", "该宽带服务不允许办理魔百和开户！");
            }
            
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
            userInfo.put("RSRV_STR4", wideNetInfo.getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            
            if (IDataUtil.isNotEmpty(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
                userInfo.put("RSRV_STR4", addrTD.first().getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
            }
            
            // 校验在途工单是否符合速率
            if (!this.checkService(wideTD.getString("TRADE_ID")))
            {
            	CSAppException.appError("2017091206", "该宽带服务不允许办理魔百和开户！");
            }
            
            //校验是否是宽带开户在途，含魔百和开户
            IDataset topSetBoxInfos = TradeOtherInfoQry.queryTradeOtherByTradeIdAndRsrvValueCode(wideTD.getString("TRADE_ID"), "TOPSETBOX");
            if (IDataUtil.isNotEmpty(topSetBoxInfos))
            {
            	CSAppException.appError("2017091207", "宽带开户未完工单中已选择过魔百和开户，无需重复办理！");
            }
        }
		
        
        // 设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        
        // 可选魔百和产品信息
        IDataset topSetBoxProducts = ProductInfoQry.queryTopSetBoxProducts("182", "600");
        userInfo.put("PRODUCT_INFO_SET", topSetBoxProducts);
  	  
        try
		{
        	checkSaleActive(input);
		} catch (Exception e)
		{
			CSAppException.appError("2017091209", "营销活动预受理不满足要求！");
		}
        
        
      	return userInfo;
    }
    
    /**
	 * 校验是否复合互联网电视要求的速率
	 */
    private boolean checkService(String tradeId) throws Exception
    {
        //取不允许的服务配置
        IDataset wideLimitSet = CommparaInfoQry.getCommpara("CSM", "4800", "WIDELIMIT", CSBizBean.getTradeEparchyCode());
        //取服务台帐信息：SERVICE_ID
        IDataset tradeSvcSet = TradeSvcInfoQry.getTradeSvcByTradeId(tradeId);
        boolean result = true;
        for (Object obj : wideLimitSet)
        {
            IData wideLimit = (IData) obj;
            String limitValue = wideLimit.getString("PARA_CODE1");
            for (Object objSvc : tradeSvcSet)
            {
                IData svcData = (IData) objSvc;
                String svcId = svcData.getString("SERVICE_ID");
                if (StringUtils.indexOf(limitValue, "|"+svcId+"|") >= 0)
                {
                	result = false;//如果匹配，则代表是不允许的服务
                }
            }
        }
        return result;
    }
    
    /**
     *  校验用户在魔百和开户时，能否办理魔百和营销活动
     *  input中含有SERIAL_NUMBER、PACKAGE_ID
     */
    public IDataset checkSaleActive(IData input) throws Exception
    {
    	//先取出包的td_b_package表的str5配置，判断是否为depend开头，是则表示有依赖的活动，调用判断
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	//魔百和营销活动
    	IData map = new DataMap();
    	map.put("SUBSYS_CODE", "CSM");
    	map.put("PARAM_ATTR", "178");
    	map.put("PARAM_CODE", "3800");
    	map.put("PARA_CODE1", "0");
    	map.put("PARA_CODE5", input.getString("PACKAGE_ID",""));
    	map.put("EPARCHY_CODE", "0898");
    	String depProdIds="";
    	String ruleTag="1";
        IDataset topSetBoxSaleActiveList = CommparaInfoQry.getCommparaInfoBy1To7(map);
        if(IDataUtil.isNotEmpty(topSetBoxSaleActiveList))
        {
        	depProdIds = topSetBoxSaleActiveList.getData(0).getString("PARA_CODE23", "");
        	ruleTag = topSetBoxSaleActiveList.getData(0).getString("PARA_CODE22", "");
        }
    	/**
    	 * REQ201607050007 关于移动电视尝鲜活动的需求
    	 * chenxy3 20160720
    	 * */
		String flag="0";
		if("1".equals(ruleTag)){
			StringTokenizer st=new StringTokenizer(depProdIds,"|"); 
			while(st.hasMoreElements()){ 
				String prodId=st.nextToken();
				IData param=new DataMap();
				param.put("PRODUCT_ID", prodId);
				param.put("SERIAL_NUMBER", "KD_"+serialNumber);
				IDataset userWilens=InternetTvOpenBean.getUserWilenInfos(param);
				if(IDataUtil.isNotEmpty(userWilens)){
					flag="1";
					break;
				}
			}
			if("0".equals(flag)){
            	CSAppException.appError("2017091208", "用户不存在办理该活动的宽带产品！");
			}
		}
    	
        input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");//为了跳CheckPackageExtConfig.java的一段规则  if (!isNoFinishTrade && !isWideUserCreateSaleActive)

        // 预受理校验，不写台账
        input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);
        input.put("TRADE_TYPE_CODE", "240");
        IDataset result = CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", input);
        return result;
    }

}
