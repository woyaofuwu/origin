
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityRuleCheck;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.userident.UserIdentBean;


public class QueryProductInfoIntfSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(QueryProductInfoIntfSVC.class);

    public String getMofficeBySN(String serialNumber) throws Exception
    {
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);

        IDataset tmp = UserInfoQry.getMofficeBySN(data);
        if (IDataUtil.isNotEmpty(tmp))
        {
            IData data2 = tmp.getData(0);
            return data2.getString("EPARCHY_CODE");
        }
        else
        {// 携转号码无moffice信息
            IDataset out = TradeNpQry.getValidTradeNpBySn(serialNumber);
            if (IDataUtil.isNotEmpty(out))
            {
                return out.getData(0).getString("AREA_CODE");
            }
            else
            {
                return null;
            }
        }
    }

    /**
     * 已订购业务查询
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData getOrderSvc(IData data) throws Exception
    {
        String routeEparchyCode = data.getString(Route.ROUTE_EPARCHY_CODE);
        // 设置路由信息
        String serialNumber = data.getString("IDVALUE");
        if (StringUtils.isNotBlank(serialNumber))
        {
            String route = getMofficeBySN(serialNumber);
            if (StringUtils.isNotBlank(route))
                routeEparchyCode = route;
        }
        if (StringUtils.isBlank(routeEparchyCode))
        {
            // 711002:无法获取正确的路由信息!"
            CSAppException.apperr(CrmUserException.CRM_USER_476);
        }
        this.setRouteId(routeEparchyCode);

        String bizCodeType = data.getString("BIZ_TYPE_CODE");//渠道编码
        String channelId= data.getString("CHANNEL_ID","");//微信微博渠道编码
        // 身份鉴权
        if ("62".equals(channelId) || "76".equals(channelId)||("62".equals(bizCodeType)&&!"UMMP".equals(data.getString("ORIGDOMAIN"))) || "76".equals(bizCodeType)){
				IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		    	
		        if (IDataUtil.isEmpty(userInfo))
		        {
		        	CSAppException.apperr(CrmUserException.CRM_USER_112);
		        }
				
		        String identCode = data.getString("IDENT_CODE");
				//校验客户凭证
				IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, serialNumber);
				if(IDataUtil.isEmpty(dataset)){
					CSAppException.apperr(CrmUserException.CRM_USER_938);
				}
        }else  if(CustServiceHelper.isCustomerServiceChannel(bizCodeType)){//一级客服升级业务能力开放平台身份鉴权
            String identCode = data.getString("IDENT_CODE");
            IData identPara =  new DataMap();
            identPara.put("SERIAL_NUMBER", serialNumber);
            identPara.put("IDENT_CODE", identCode);
            CustServiceHelper.checkCertificate(identPara);
		} else{
			IDataset res = CommparaInfoQry.queryCommInfos("7777", "IDENT_AUTH_CONFG", data.getString("KIND_ID", ""), data.getString("BIZ_TYPE_CODE", ""));
			if (IDataUtil.isNotEmpty(res))
		    {
		        data.put("SERIAL_NUMBER", data.getString("IDVALUE", ""));
		        UserIdentBean identBean = new UserIdentBean();
		        identBean.identAuth(data);
		    }
		}
        

       

        IData result = queryOrderSvc(data);
        result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        return result;
    }

    /**
     * 已订购业务查询
     * 
     * @param
     * @param data
     * @return
     * @throws Exception
     */
    public IData queryOrderSvc(IData data) throws Exception
    {
        IData resultData = new DataMap();
        IDataset myDataset = new DatasetList();
        String idType = data.getString("IDTYPE");
        String idValue = data.getString("IDVALUE");
        if (StringUtils.isBlank(idType) || !"01".equals(idType))
        {
            // 标识类型错误
            CSAppException.apperr(CrmUserException.CRM_USER_1190);
        }
        if (StringUtils.isBlank(idValue))
        {
            // 标识号码错误
            CSAppException.apperr(CrmUserException.CRM_USER_1191);
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(idValue);
        if (IDataUtil.isEmpty(userInfo))
        {
            // 用户资料不存在
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String userId = userInfo.getString("USER_ID");
        IDataset productDataset = new DatasetList();
        IData productData = new DataMap();
        IDataset sdata = UserPlatSvcInfoQry.queryUserSvcForIntf2(userId);
        if(IDataUtil.isNotEmpty(sdata))
        {
        	for(int i=0;i<sdata.size();i++)
        	{
        		IData temp = sdata.getData(i);
        		String serviceId = temp.getString("SERVICE_ID");
        		IDataset svcParas = UpcCall.querySpServiceAndProdByCond("", "", "", serviceId);
        		if(IDataUtil.isNotEmpty(svcParas))
        		{
        			IData svcPara = svcParas.getData(0);
        			String billType = svcPara.getString("BILL_TYPE");
        			if(StringUtils.isNotBlank(billType)){
        				temp.put("DATA_NAME", StaticUtil.getStaticValue("SPBIZ_BILL_TYPE", billType));
        				temp.put("SERVICE_NAME",svcPara.getString("OFFER_NAME"));
        				temp.put("BIZ_TYPE_CODE",svcPara.getString("BIZ_TYPE_CODE"));
        				temp.put("PRICE",svcPara.getString("PRICE"));
        				temp.put("SP_CODE",svcPara.getString("SP_CODE"));
        				temp.put("BIZ_CODE",svcPara.getString("BIZ_CODE"));
        			}
        		}
        		//
                IDataset spServices = UpcCall.querySpServiceByIdAndBizStateCode(serviceId, "Z", "A");
                if(IDataUtil.isNotEmpty(spServices)){
                	IData spService = spServices.getData(0);
                	temp.put("SERV_MODE", spService.getString("SERV_MODE",""));
                }
        	}
        }
        
        int sdataSize = sdata.size();
        IData temp = null;
        if (IDataUtil.isEmpty(sdata))
        {
            if (logger.isDebugEnabled())
                logger.debug("没有增值业务！");
        }
        else
        {
            IData tempData = new DataMap();
            IDataset tmpDataset = new DatasetList();
            for (int i = 0; i < sdataSize; i++)
            {
                temp = sdata.getData(i);
                IData d = new DataMap();
                d.put("BUNESS_TYPE", "02");
                d.put("BUNESS_CODE", "");

                d.put("SP_ID", temp.getString("SP_CODE"));
                // 由于注册类平台业务无法区分，所以接口的BIZ_CODE重新定义为BIZ_CODE和BIZ_TYPE_CODE的组合，这个在业务退订时需要拆分处理
                d.put("BIZ_CODE", temp.getString("BIZ_CODE") + "|" + temp.getString("BIZ_TYPE_CODE"));
                d.put("BUNESS_NAME", temp.getString("SERVICE_NAME"));

                String price = temp.getString("PRICE","0");
                if ("0".equals(price))
                {
                    d.put("BUNESS_FREE", "0元/月");
                }
                else if(Integer.parseInt(price)%1000!=0)
                {
                	double price1 = Double.parseDouble(price);
                	d.put("BUNESS_FREE", price1 / 1000 + "元/月");
                }
                else
                {
                	int price1 = Integer.parseInt(price);
                	d.put("BUNESS_FREE", price1 / 1000 + "元/月");
                	
                }
                IDataset attrInfos=queryAttr(userId, temp.getString("INST_ID",""), temp.getString("SERVICE_ID",""));
                if(IDataUtil.isNotEmpty(attrInfos)){
                	d.put("PRO_ATTR",attrInfos );	
                }              
                d.put("ORDERING_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                d.put("START_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                d.put("DEAD_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("END_DATE").substring(0, 10)));
                d.put("FEE_TYPE", temp.getString("DATA_NAME",""));
                
             // 移动商城2.8 出参信息，新增2个字段，省份短信0000模板分类和是否可退订功能--通过该接口 查询的 都为可退订产品 add by huangyq
				d.put("CANCEL_FLAG", "0");// 0：可退订    1：不可退
				String servMode = temp.getString("SERV_MODE","");
        		if("0".equals(servMode)){	//0,梦网业务，1,自有业务
        			d.put("BUSI_TYPE", "1");	// 0：中国移动业务	1：中国移动代收费业务
        			d.put("REGION_BUSI_TYPE", "中国移动代收费业务");
        		}else if("1".equals(servMode)){
        			d.put("BUSI_TYPE", "0");	
        			d.put("REGION_BUSI_TYPE", "中国移动业务");
        		}
        		
                tmpDataset.add(d);
            }
            tempData.put("PRODUCT_TYPE", "02");// 增值类
            //移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
       	 	if(!tmpDataset.isEmpty())
       	 		DataHelper.sort(tmpDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            
            tempData.put("SUB", tmpDataset.toData());
            myDataset.add(tempData);
        }

        // 优惠信息
        IData forceDiscnts = new DataMap();

        String kind_id = data.getString("KIND_ID","");
        if(StringUtils.isNotBlank(kind_id)&&"BIP3A210_T3000212_1_0".equals(kind_id)){  //移动商城统一退订不查询服务功能 
        	//移动商城统一退订查询基础套餐
            IDataset userDiscnts = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);
            IDataset mainProducts = UserProductInfoQry.queryMainProductNow(userId);
            String productId = "";
            if (IDataUtil.isNotEmpty(mainProducts)){
            	productId = mainProducts.getData(0).getString("PRODUCT_ID","");
            }

            IDataset forceDiscntInfos = PkgElemInfoQry.queryDiscntOfForcePackage(productId);
            //IDataset forceDiscntInfos = PkgElemInfoQry.queryDiscntOfForcePackage(userInfo.getString("PRODUCT_ID"));
            if(IDataUtil.isNotEmpty(forceDiscntInfos)){
            	for(int j=0; j<forceDiscntInfos.size();j++){
            		forceDiscnts.put(forceDiscntInfos.getData(j).getString("ELEMENT_ID"), "1");
            	}
            }else{
                //如果构成为空就查必选组优惠
                IDataset groupList = UpcCall.queryOfferGroups(productId);
                for(Object obj :groupList){
                    IData data2 = (IData)obj;
                    String selectFlag = data2.getString("SELECT_FLAG");
                    //如果是必选组
                    if("0".equals(selectFlag)){
                        IDataset offerList = UpcCall.queryGroupComRelOfferByGroupId(data2.getString("GROUP_ID"), "");
                        for(Object temp2 :offerList){
                            IData data3 = (IData)temp2;
                            String discntCode = data3.getString("OFFER_CODE");
                            forceDiscnts.put(discntCode, "1");
                        }
                    }
                }
            }

            //4949配置不可退订
            IData param = new DataMap();
            param.put("SUBSYS_CODE", "CSM");
            param.put("PARAM_ATTR", "4949");
            param.put("PARAM_CODE", "INTERESTS");
            IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
            for(Object obj :dataset){
                IData objData = (IData)obj;
                forceDiscnts.put(objData.getString("PARA_CODE1"), "1");
            }




            if (IDataUtil.isEmpty(userDiscnts))
            {
                if (logger.isDebugEnabled())
                    logger.debug("没有订购基础业务！");
            }
            else
    		{
                IData tempData = new DataMap();
                IDataset tmpDataset = new DatasetList();
    			for(int i = 0; i <userDiscnts.size(); i++){
    		      	IData userDiscnt = userDiscnts.getData(i);
    		      	String startDate = userDiscnt.getString("START_DATE");
    	        	String endDate  = userDiscnt.getString("END_DATE");
    	        	String discntCode = userDiscnt.getString("DISCNT_CODE");
    	        	if(!StringUtils.equals("-1", userDiscnt.getString("USER_ID_A"))){	//集团成员业务，不展示
    	        		continue;
    	        	}
    	        	
    	        	if(SysDateMgr.compareTo(endDate, SysDateMgr.END_DATE_FOREVER)<0){ //截止时间小于2050代表非连续包月,不展示
    	        		continue;
    	        	}
    	        	
    	        	if(StringUtils.isNotEmpty(forceDiscnts.getString(discntCode))){ //主产品下必选元素，不展示
    	        		continue;
    	        	}
    	        	
    	        	//获取0000是否可查询及基础价格信息
    	        	IDataset discntInfo = UpcCall.queryOfferChaByOfferCode(discntCode, "D");
    	        	if(IDataUtil.isEmpty(discntInfo)){	//查不到产商品0000配置信息，不展示
    	        		continue;
    	        	}       	
    	        	String offerName = discntInfo.first().getString("OFFER_NAME");
    	        	String fee = discntInfo.first().getString("FEE","");
    	        	if(StringUtils.isEmpty(offerName) || StringUtils.isEmpty(fee)){
    	                CSAppException.apperr(CrmCommException.CRM_COMM_103, "产商品配置错误，产品编码："+ discntCode);
    	        	}
    				String startTime = DateFormatUtils.format(SysDateMgr.string2Date(startDate,"yyyy-MM-dd HH:mm:ss"),"yyyyMMdd");
    				String deadTime = DateFormatUtils.format(SysDateMgr.string2Date(endDate,"yyyy-MM-dd HH:mm:ss"),"yyyyMMdd");

    				IData item = new DataMap();
    				item.put("BUNESS_TYPE", "01");
    				item.put("BUNESS_CODE", "D" + discntCode);
    				item.put("SP_ID", "");
    				item.put("BIZ_CODE", "");
    				item.put("BUNESS_NAME", offerName);
    				item.put("ORDERING_TIME", startTime);
    				item.put("START_TIME", startTime);
    				item.put("DEAD_TIME", deadTime);
    				// 移动商城2,8 add by huangyq
    				// 根据 price_id 查询 fee_type 
    				IDataset pricePlaninfos = UpcCall.queryPricePricePlanRelPricePlanByPriceId(discntInfo.first().getString("PRICE_ID"));
    				String fee_type = "";
    				if(IDataUtil.isNotEmpty(pricePlaninfos)){
    					fee_type = pricePlaninfos.first().getString("FEE_TYPE");
    				}
    				item.put("FEE_TYPE", fee_type);
    				
    	    		int priceInt = Integer.parseInt(fee);
	                if(priceInt > 0){
	                	item.put("BUNESS_FREE", priceInt / 100 + "元/月");
	                	item.put("FEE_TYPE", "包月计费");
	                }else{
//	                	item.put("BUNESS_FREE", "");
	                	item.put("BUNESS_FREE", "0元/月");
	                	item.put("FEE_TYPE", "免费");
	                }
	                // 移动商城2.8 add by huangyq
	                item.put("BUSI_TYPE", "2");    //0：中国移动业务 1：中国移动代收费业务;2：基础业务
	                item.put("REGION_BUSI_TYPE", "基础业务");
	                item.put("CANCEL_FLAG", "0");
    				tmpDataset.add(item);
    			}
                //移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
           	 	if(!tmpDataset.isEmpty()){
                    tempData.put("PRODUCT_TYPE", "01");//套餐类
           	 		DataHelper.sort(tmpDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
           	 		tempData.put("SUB", tmpDataset.toData());
           	 		myDataset.add(tempData);
           	 	}
            }
        }else{
        sdata = UserSvcInfoQry.querySerivceByUserIdWithLimit(userId);
        sdataSize = sdata.size();
        if (sdata == null || sdata.size() <= 0)
        {
            if (logger.isDebugEnabled())
                logger.debug("没有服务功能！");
        }
        else
        {
            IData tempData = new DataMap();
            IDataset tmpDataset = new DatasetList();
            for (int i = 0; i < sdata.size(); i++)
            {
                temp = sdata.getData(i);
                IData d = new DataMap();
                d.put("BUNESS_TYPE", "01");
                d.put("BUNESS_CODE", "S" + temp.getString("SERVICE_ID"));
                d.put("SP_ID", "");
                d.put("BIZ_CODE", "");
                d.put("BUNESS_NAME", USvcInfoQry.getSvcNameBySvcId(temp.getString("SERVICE_ID")));
                // 移动商城2.8 add by huangyq
                // // 根据 offerCode offerTYPE 查询定价计划
                IDataset priceInfos = UpcCall.qryPricePlanInfoByOfferId(temp.getString("SERVICE_ID"), "S");
                String price = "0";
                String feeType = "";
                if(IDataUtil.isNotEmpty(priceInfos)){
                	price = priceInfos.first().getString("FEE","0");
                	feeType = priceInfos.first().getString("FEE_TYPE");
                }
                d.put("FEE_TYPE", feeType);
                int priceInt = Integer.parseInt(price);
                if(priceInt > 0){
                	d.put("BUNESS_FREE", priceInt / 100 + "元/月");
                	d.put("FEE_TYPE", "包月计费");
                }else{
                	d.put("BUNESS_FREE", "0元/月");
                	d.put("FEE_TYPE", "免费");
                }
//                d.put("BUNESS_FREE", ""); 
                
                d.put("ORDERING_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                d.put("START_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                d.put("DEAD_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("END_DATE").substring(0, 10)));
                
                // 移动商城2.8 add by huangyq
                //d.put("FEE_TYPE", feeType);
                
                IDataset attrInfos=queryAttr(userId, temp.getString("INST_ID"), temp.getString("SERVICE_ID"));
                if(IDataUtil.isNotEmpty(attrInfos)){
                    d.put("PRO_ATTR",attrInfos );	
                }
                // 移动商城2.8 add by huangyq
                d.put("BUSI_TYPE", "0");    //0：中国移动业务 1：中国移动代收费业务
                d.put("REGION_BUSI_TYPE", "中国移动业务");
                d.put("CANCEL_FLAG", "0");
                
                tmpDataset.add(d);
            }
            tempData.put("PRODUCT_TYPE", "03");// 服务类
            //移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
       	 	if(!tmpDataset.isEmpty())
       	 		DataHelper.sort(tmpDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
            
            tempData.put("SUB", tmpDataset.toData());
            myDataset.add(tempData);
        }
        }

        if (!data.getString("QUERY_TAG", "").equals("01"))
        {// 01只查询增值业务和服务类业务
            // 套餐
            IDataset tdata = UserDiscntInfoQry.queryDiscntByUserIdForIntf2(userId);
            int tdataSize = tdata.size();
            if (IDataUtil.isEmpty(tdata))
            {
                if (logger.isDebugEnabled())
                    logger.debug("没有套餐业务！");
            }
            else
            {
                IDataset tmpDataset = new DatasetList();
                for (int i = 0; i < tdataSize; i++)
                {
                    temp = tdata.getData(i);
                    String dis = temp.getString("DISCNT_CODE", "");
                    String explain = UDiscntInfoQry.getDiscntExplainByDiscntCode(dis);
                    if (explain != null && explain.length() > 128)
                    {
                        explain = explain.substring(0, 128);// 接口范围长度128，实际可能更长些
                    }
                    if(StringUtils.isNotBlank(forceDiscnts.getString(dis))){
                        continue;
                    }
                    IDataset comset = CommparaInfoQry.getCommparaByAttrCode2("CSM", "3700", dis, this.getTradeEparchyCode(), null);
                    int tag = 0;
                    String tempdis = "";
                    String spId = "";
                    if (IDataUtil.isNotEmpty(comset) && IDataUtil.isNotEmpty(sdata))
                    {
                        for (int s = 0; s < sdata.size(); s++)
                        {
                            IData pal = sdata.getData(s);
                            for (int c = 0; c < comset.size(); c++)
                            {
                                IData com = comset.getData(c);
                                if (pal.getString("SERVICE_ID").equals(com.getString("PARAM_CODE", "")))
                                {
                                    tag++;
                                    tempdis = com.getString("PARA_CODE1", "");
                                    spId = pal.getString("SP_CODE", "");
                                    break;
                                }
                            }
                        }
                    }

                    IData d = new DataMap();
                    if (tag > 0)
                    {
                        d.put("BUNESS_TYPE", "02");
                        d.put("BIZ_CODE", tempdis + "|" + "02");
                    }
                    else
                    {
                        d.put("BUNESS_TYPE", "01");
                        d.put("BIZ_CODE", "");
                    }
                    d.put("BUNESS_CODE", "D" + temp.getString("DISCNT_CODE"));
                    d.put("SP_ID", spId);
                    d.put("BUNESS_NAME", UDiscntInfoQry.getDiscntNameByDiscntCode(dis));
                    
                 // 移动商城2.8 add by huangyq
                 // 根据 offerCode offerTYPE 查询定价计划
                    IDataset priceInfos = UpcCall.qryPricePlanInfoByOfferId(temp.getString("DISCNT_CODE"), "D");
                    String price = "0";
                    String feeType = "";
                    if(IDataUtil.isNotEmpty(priceInfos)){
                    	price = priceInfos.first().getString("FEE","0");
                    	feeType = priceInfos.first().getString("FEE_TYPE");
                    }
                    int priceInt = Integer.parseInt(price);
                    if(priceInt > 0){
                    	d.put("BUNESS_FREE", priceInt / 100 + "元/月");
                    	d.put("FEE_TYPE", "包月计费");
                    }else{
                    	d.put("BUNESS_FREE", "0元/月");
                    	d.put("FEE_TYPE", "免费");
                    }
//                    d.put("BUNESS_FREE", "");
                    
                    d.put("ORDERING_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                    d.put("START_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                    d.put("DEAD_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("END_DATE").substring(0, 10)));
                    
                 // 移动商城2.8 add by huangyq
                    //d.put("FEE_TYPE", feeType);
                    
                    IDataset attrInfos=queryAttr(userId, temp.getString("INST_ID"), temp.getString("DISCNT_CODE"));
                    if(IDataUtil.isNotEmpty(attrInfos)){
                    	d.put("PRO_ATTR",attrInfos);	
                    }        
                 // 移动商城2.8 add by huangyq
                    d.put("BUSI_TYPE", "2");    //0：中国移动业务 1：中国移动代收费业务
                    d.put("REGION_BUSI_TYPE", "基础业务");
                    d.put("CANCEL_FLAG", "0");
                    
                    tmpDataset.add(d);
                }
                productDataset.addAll(tmpDataset);
            }
            //查询家庭网产品
            IDataset Familydata=queryFamilyProductInfoByUserId(userId);
            int Fdatasize=Familydata.size();
            if (IDataUtil.isEmpty(tdata))
            {
                if (logger.isDebugEnabled())
                    logger.debug("没有订购家庭网产品！");
            }
            else
            {
            	IDataset tmpDataset = new DatasetList();
            	for(int i=0;i<Fdatasize;i++){
            		IData userProduct = Familydata.getData(i);
                    String productId = userProduct.getString("PRODUCT_ID");
                    String productName = UProductInfoQry.getProductNameByProductId(productId);                   
                    IData d = new DataMap();
                    d.put("BUNESS_TYPE", "01");
                    d.put("BUNESS_CODE", productId);
                    d.put("SP_ID", "");
                    d.put("BIZ_CODE", "");
                    d.put("BUNESS_NAME", productName);
                    
                 // 移动商城2.8 add by huangyq
                    // 根据 offerCode offerTYPE 查询定价计划
                    IDataset priceInfos = UpcCall.qryPricePlanInfoByOfferId(productId, "P");
                    String price = "0";
                    String feeType = "";
                    if(IDataUtil.isNotEmpty(priceInfos)){
                    	price = priceInfos.first().getString("FEE","0");
                    	feeType = priceInfos.first().getString("FEE_TYPE");
                    }
                    int priceInt = Integer.parseInt(price);
                    if(priceInt > 0){
                    	d.put("BUNESS_FREE", priceInt / 100 + "元/月");
                    	d.put("FEE_TYPE", "包月计费");
                    }else{
                    	d.put("BUNESS_FREE", "0元/月");
                    	d.put("FEE_TYPE", "免费");
                    }
//                    d.put("BUNESS_FREE", "");
                    d.put("ORDERING_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, userProduct.getString("START_DATE").substring(0, 10)));
                    d.put("START_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, userProduct.getString("START_DATE").substring(0, 10)));
                    d.put("DEAD_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, userProduct.getString("END_DATE").substring(0, 10)));
                    
                 // 移动商城2.8 add by huangyq
                    //d.put("FEE_TYPE", feeType);
                    
                 // 移动商城2.8 add by huangyq
                    d.put("BUSI_TYPE", "0");    //0：中国移动业务 1：中国移动代收费业务
                    d.put("REGION_BUSI_TYPE", "中国移动业务");
                    d.put("CANCEL_FLAG", "1");
                    
                    tmpDataset.add(d);
            	}
            	 productDataset.addAll(tmpDataset);
            }
            tdata = UserProductInfoQry.queryIntfProdInfoByUserId(userId);
            tdataSize = tdata.size();
            if (IDataUtil.isEmpty(tdata))
            {
                if (logger.isDebugEnabled())
                    logger.debug("没有订购产品！");
            }
            else
            {
                IDataset tmpDataset = new DatasetList();
                for (int i = 0; i < tdataSize; i++)
                {
                    temp = tdata.getData(i);
                    String productId = temp.getString("PRODUCT_ID");
                    String explain = UProductInfoQry.getProductExplainByProductId(productId);
                    String prodMode = UProductInfoQry.getProductModeByProductId(productId);
                    if("00,01,02,03".indexOf(prodMode)!=-1) continue;
                    if (explain != null && explain.length() > 128)
                    {
                        explain = explain.substring(0, 128);// 接口范围长度128，实际可能更长些
                    }
                    IData d = new DataMap();
                    d.put("BUNESS_TYPE", "01");
                    d.put("BUNESS_CODE", "P" + temp.getString("PRODUCT_ID"));
                    d.put("SP_ID", "");
                    d.put("BIZ_CODE", "");
                    d.put("BUNESS_NAME", UProductInfoQry.getProductNameByProductId(productId));
                 // 移动商城2.8 add by huangyq
                 // 根据 offerCode offerTYPE 查询定价计划
                    IDataset priceInfos = UpcCall.qryPricePlanInfoByOfferId(productId, "P");
                    String price = "0";
                    String feeType = "";
                    if(IDataUtil.isNotEmpty(priceInfos)){
                    	price = priceInfos.first().getString("FEE","0");
                    	feeType = priceInfos.first().getString("FEE_TYPE");
                    }
                    int priceInt = Integer.parseInt(price);
                    if(priceInt > 0){
                    	d.put("BUNESS_FREE", priceInt / 100 + "元/月");
                    	d.put("FEE_TYPE", "包月计费");
                    }else{
                    	d.put("BUNESS_FREE", "0元/月");
                    	d.put("FEE_TYPE", "免费");
                    }
//                    d.put("BUNESS_FREE", "");
                    d.put("ORDERING_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                    d.put("START_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                    d.put("DEAD_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("END_DATE").substring(0, 10)));
                 // 移动商城2.8 edit by huangyq
                    //d.put("FEE_TYPE", feeType);
                    // 移动商城2.8 add by huangyq
                    d.put("BUSI_TYPE", "2");    //0：中国移动业务 1：中国移动代收费业务
                    d.put("REGION_BUSI_TYPE", "基础业务");
                    d.put("CANCEL_FLAG", "0");
                    tmpDataset.add(d);
                }
                productDataset.addAll(tmpDataset);
            }
            
            // 01类的要有数据才拼
            if (IDataUtil.isNotEmpty(productDataset))
            {
                productData.put("PRODUCT_TYPE", "01");// 套餐类
                //移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
           	 	if(!productDataset.isEmpty())
           	 		DataHelper.sort(productDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
                
                productData.put("SUB", productDataset.toData());
                myDataset.add(productData);
            }

            sdata = UserSaleActiveInfoQry.querySaleActiveInfoByUserId(userId);
            sdataSize = sdata.size();
            if (IDataUtil.isEmpty(sdata))
            {
                if (logger.isDebugEnabled())
                    logger.debug("没有营销活动！");
            }
            else
            {
                IData tempData = new DataMap();
                IDataset tmpDataset = new DatasetList();
                for (int i = 0; i < sdataSize; i++)
                {
                    temp = sdata.getData(i);
                    IData d = new DataMap();
                    d.put("BUNESS_TYPE", "01");
                    d.put("BUNESS_CODE", temp.getString("PACKAGE_ID"));
                    d.put("SP_ID", "");
                    d.put("BIZ_CODE", "");
                    d.put("BUNESS_NAME", temp.getString("PRODUCT_NAME") + "-" + temp.getString("PACKAGE_NAME"));
                 // 移动商城2.8 add by huangyq
                 // 根据 offerCode offerTYPE 查询定价计划
                    IDataset priceInfos = UpcCall.qryPricePlanInfoByOfferId(temp.getString("PACKAGE_ID"), "K");
                    String price = "0";
                    String feeType = "";
                    if(IDataUtil.isNotEmpty(priceInfos)){
                    	price = priceInfos.first().getString("FEE","0");
                    	feeType = priceInfos.first().getString("FEE_TYPE");
                    }
                    int priceInt = Integer.parseInt(price);
                    if(priceInt > 0){
                    	d.put("BUNESS_FREE", priceInt / 100 + "元/月");
                    	d.put("FEE_TYPE", "包月计费");
                    }else{
                    	d.put("BUNESS_FREE", "0元/月");
                    	d.put("FEE_TYPE", "免费");
                    }
//                    d.put("BUNESS_FREE", "");
                    d.put("ORDERING_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                    d.put("START_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("START_DATE").substring(0, 10)));
                    d.put("DEAD_TIME", Utility.decodeTimestamp(SysDateMgr.PATTERN_TIME_YYYYMMDD, temp.getString("END_DATE").substring(0, 10)));
                    
                 // 移动商城2.8 edit by huangyq
                    //d.put("FEE_TYPE", feeType);
                    
                 // 移动商城2.8 add by huangyq
                    d.put("BUSI_TYPE", "0");    //0：中国移动业务 1：中国移动代收费业务
                    d.put("REGION_BUSI_TYPE", "中国移动业务");
                    d.put("CANCEL_FLAG", "0");
                    
                    tmpDataset.add(d);
                }
                tempData.put("PRODUCT_TYPE", "04");// 营销活动等其他类
                //移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
           	 	if(!tmpDataset.isEmpty())
           	 		DataHelper.sort(tmpDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
                
                tempData.put("SUB", tmpDataset.toData());
                myDataset.add(tempData);
            }
            //查询视频流量类
            IDataset discnInfos = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userInfo.getString("USER_ID"));
            String isVideoTag="";
            if (IDataUtil.isNotEmpty(discnInfos))
            {
             IDataset tmpDataset = new DatasetList();
             IData tempData = new DataMap();
             for (int i = 0; i < discnInfos.size(); i++)
             {        		        
            	 String eparchyCode = RouteInfoQry.getEparchyCodeBySnForCrm(idValue);
                 IData queryData=discnInfos.getData(i);
                 queryData.put("EPARCHY_CODE", eparchyCode);
                 queryData.put("BIZ_TYPE_CODE", data.getString("BIZ_TYPE_CODE"));
                 IDataset bunssInfos=AbilityRuleCheck.isVideoPackage(queryData);
                 if(IDataUtil.isNotEmpty(bunssInfos)){  //视频流量包特殊处理 add by cy
                     for(int b=0;b<bunssInfos.size();b++){
                    	IData d = new DataMap();
                        d.put("BUNESS_TYPE", "01");
                        d.put("BUNESS_CODE", bunssInfos.getData(b).getString("BUNESS_CODE")); 
                        
                        String bizCode = data.getString("BIPCODE","").trim();
                        if ("uniQry".equals(bizCode)){//能力平台
                        	d.put("BUNESS_CODE", queryData.getString("DISCNT_CODE","")); 
                        }else{
                        	d.put("BUNESS_CODE", bunssInfos.getData(b).getString("BUNESS_CODE")); 
                        }
                        
                        d.put("BUNESS_NAME", bunssInfos.getData(b).getString("BUNESS_NAME"));
                        d.put("ORDERING_TIME", discnInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
                        d.put("START_TIME", discnInfos.getData(i).getString("START_DATE").substring(0, 10).replaceAll("-", ""));
                        d.put("DEAD_TIME", discnInfos.getData(i).getString("END_DATE").substring(0, 10).replaceAll("-", ""));
                     // 移动商城2.8 add by huangyq
                     // 根据 offerCode offerTYPE 查询定价计划
                        IDataset priceInfos = UpcCall.qryPricePlanInfoByOfferId(queryData.getString("DISCNT_CODE"), "D");
                        String price = "0";
                        String feeType = "";
                        if(IDataUtil.isNotEmpty(priceInfos)){
                        	price = priceInfos.first().getString("FEE","0");
                        	feeType = priceInfos.first().getString("FEE_TYPE");
                        }
                        d.put("FEE_TYPE", feeType);
                        
//                        String price = discnInfos.getData(i).getString("PRICE", "0");
                        int price1 = Integer.parseInt(price);
                        if(price1 > 0){
                           d.put("BUNESS_FREE", price1 / 100 + "元/月");
                           d.put("FEE_TYPE", "包月计费");
                        }else{
//                           d.put("BUNESS_FREE", "");
                        	d.put("BUNESS_FREE", "0元/月");
                        	d.put("FEE_TYPE", "免费");
                        }
                     // 移动商城2.8 add by huangyq
                        d.put("BUSI_TYPE", "0");    //0：中国移动业务 1：中国移动代收费业务
                        d.put("REGION_BUSI_TYPE", "中国移动业务");
                        d.put("CANCEL_FLAG", "0");
                        
                        tmpDataset.add(d);
                      }
                        isVideoTag="Y"; 
                  }
             }
             if("Y".equals(isVideoTag)){//本次只增加视频流量定向包类型
                 String bizCode = data.getString("BIPCODE","").trim();
                 if ("uniQry".equals(bizCode)){//能力平台
                     tempData.put("PRODUCT_TYPE", "05");
                 }else{
                     tempData.put("PRODUCT_TYPE", "02");//增值类 
                 }
             }
             
         	//移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
    	   		if(!tmpDataset.isEmpty()){
    	   	      DataHelper.sort(tmpDataset, "START_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
    			  tempData.put("SUB", tmpDataset.toData());
    	   		}
    			myDataset.add(tempData);
            }
        }

        resultData.putAll(myDataset.toData());
        resultData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        return resultData;
    }

    @Override
    public final void setTrans(IData input)
    {
        if (StringUtils.isNotBlank(input.getString("IDVALUE")))
        {
            input.put("SERIAL_NUMBER", input.getString("IDVALUE"));
            return;
        }
    }
    //属性查询
    private IDataset queryAttr(String userId,String inst_id,String serviceId) throws Exception {
    	IDataset returnValue = new DatasetList();
    	IDataset attrs = UserAttrInfoQry.getUserAttrByPK(userId, inst_id,null);
    	if(IDataUtil.isNotEmpty(attrs)){   
    	 for(int i = 0;i<attrs.size();i++){
    		String attr_code = attrs.getData(i).getString("ATTR_CODE");
            String attr_value = attrs.getData(i).getString("ATTR_VALUE");
            String inst_type = attrs.getData(i).getString("INST_TYPE");
            IData value = new DataMap();
            value.put("ATTR_ID", attr_code);//属性编码
            value.put("ATTR_VALUE", attr_value);//属性值
            IDataset result = UpcCallIntf.queryOfferChaByCond(inst_type, serviceId,attr_code, "","","",null);
            if(IDataUtil.isNotEmpty(result)){
            	value.put("ATTR_NAME", result.getData(0).getString("CHA_SPEC_NAME"));
            }else{
            	value.put("ATTR_NAME", "");
            }   		
            returnValue.add(value);
    	}
    	}
    	return returnValue;
    }
    /**
     * 查询家庭网信息
     * @param userId
     * @return
     * @throws Exception
     */
    private IDataset queryFamilyProductInfoByUserId(String userId) throws Exception {
    	IDataset list=new DatasetList();
    	IDataset infos = RelaUUInfoQry.getEnableRelationUusByUserIdBTypeCode(userId,"45");		
    	 if(IDataUtil.isNotEmpty(infos)){
    		 for(int i=0;i<infos.size();i++){
    			IData data=infos.getData(i);
    			String user_id_a = data.getString("USER_ID_A");
    			list= UserProductInfoQry.queryProductByUserId(user_id_a);
    		 }
        	
    	 }
         return list;
    }
}
