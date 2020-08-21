package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementModel;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpPfUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;
import com.asiainfo.veris.crm.order.soa.group.dataline.DatalineUtil;

public class DestoryDatalineGroupMemberBean  extends DestroyGroupMember{

    private String tradeId = "";
    
    private String reasonName = "";

    private IDataset commonData = null;
    
    private IDataset lineData = null;
    public DestoryDatalineGroupMemberBean()
    {

    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        //处理专线信息
        actTradeDataline();
        
        //处理专线属性
        actTradeDatalineAttr();
        
        //处理用户资料
        actTradeUser();
        
        //处理用户其他信息
        actTradeOther();
        
        //处理服务状态
        actTradeSvcState();
        
        if(StringUtils.isNotBlank(tradeId)){
         // 处理工单依赖
         infoRegDataTradeLimit();
        }
        
    }
   
    /**
     * 处理服务状态
     * 
     * @throws Exception
     */
    protected void actTradeSvcState() throws Exception
    {
        IDataset svcList = reqData.cd.getSvc();

        if (IDataUtil.isEmpty(svcList))
        {
            return;
        }

        IDataset svcStateList = new DatasetList();

        for (int i = 0, row = svcList.size(); i < row; i++)
        {
            IData svcData = svcList.getData(i);
            String modifyTag = svcData.getString("MODIFY_TAG", "");

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag))
            {
                // 处理新增服务的服务状态
                addSvcState(svcData, svcStateList);
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
            {
                // 处理删除服务的服务状态
                delSvcState(svcData, svcStateList);
            }
        }

        super.addTradeSvcstate(svcStateList);
    }
    
    /**
     * 处理工单依赖
     * 
     * @throws Exception
     */
    public void infoRegDataTradeLimit() throws Exception
    {
        IData tradeLimitData = new DataMap();

        tradeLimitData.put("TRADE_ID", tradeId);
        tradeLimitData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
        tradeLimitData.put("LIMIT_TRADE_ID", getTradeId());
        tradeLimitData.put("LIMIT_TYPE", "0");
        tradeLimitData.put("ROUTE_ID", Route.CONN_CRM_CG);
        

        Dao.insert("TF_B_TRADE_LIMIT", tradeLimitData, Route.getJourDb(Route.CONN_CRM_CG));
    }
    
    /**
     * 处理专线信息
     * @throws Exception
     */
    private void actTradeDataline() throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        String productId = reqData.getGrpUca().getProductId();
        if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
        	inparam.put("SHEET_TYPE", "6");
        }else if("7010".equals(productId)){
        	inparam.put("SHEET_TYPE", "7");
        }else if ("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
        	inparam.put("SHEET_TYPE", "4");
        }else if ("7016".equals(productId)){
        	inparam.put("SHEET_TYPE", "8");
        }
        
        IDataset datalineList = TradeOtherInfoQry.queryUserDataLineByUserIdAndProductNo(inparam);

        if (null != datalineList && datalineList.size() > 0)
        {
            for (int i = 0; i < datalineList.size(); i++)
            {
                IData userLine = datalineList.getData(i);
                userLine.put("UPDATE_TIME", getAcceptTime());
                userLine.put("END_DATE", getAcceptTime());
                userLine.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
        }

        super.addTradeDataLine(datalineList);
    }
    
    /**
     * 注销用户资料
     * 
     * @throws Exception
     */
    protected void actTradeUser() throws Exception
    {
        IData userData = reqData.getUca().getUser().toData();
      /*  String reasonName = "";
        if (null != commonData && commonData.size() > 0){
        	for(int m=commonData.size()-1;m >=0;m--){
    			String attrCodeTemp = commonData.getData(m).getString("ATTR_CODE","");
    			if(attrCodeTemp.equals("BACKOUTCAUSE")){
    				reasonName = commonData.getData(m).getString("ATTR_VALUE","");
    			}
    		}
        }*/
        
       // String reasonName = StaticUtil.getStaticValue("TD_B_REMOVE_REASON_GROUP", "5");

        userData.put("REMOVE_TAG", "2"); // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        userData.put("DESTROY_TIME", getAcceptTime());

        userData.put("REMOVE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 注销地市
        userData.put("REMOVE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 注销市县
        userData.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
        userData.put("REMOVE_REASON_CODE", "5"); // 注销原因
        userData.put("USER_STATE_CODESET", "1"); // 用户主体服务状态集：见服务状态参数表

        userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 修改标志

        userData.put("REMARK", reasonName); // 暂填注销原因的中文解释

        userData.put("RSRV_STR2", "5");
        userData.put("RSRV_STR8", reasonName);

        super.addTradeUser(userData);
    }
    
    /**
     * 注销other表信息
     * @throws Exception
     */
    protected void actTradeOther() throws Exception
    {
        String user_id = reqData.getUca().getUser().getUserId();
        IData inparams = new DataMap();
        inparams.put("USER_ID", user_id);
        inparams.put("PARTITION_ID", user_id.substring(user_id.length() - 4));

        IDataset otherList = UserOtherInfoQry.getUserOtherInfoByAllUserId(inparams);

        if (IDataUtil.isNotEmpty(otherList))
        {
            for (int i = 0, sz = otherList.size(); i < sz; i++)
            {
                IData data = otherList.getData(i);

                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                if (reqData.isIfBooking())
                {
                    data.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                }
                else
                {
                    data.put("END_DATE", getAcceptTime());
                }  
            }
        }

        addTradeOther(otherList);
    }
    
    private void actTradeDatalineAttr() throws Exception
    {
        IDataset dataset = new DatasetList();
        if (null != commonData && commonData.size() > 0){
			if (null != lineData && lineData.size() > 0){
				String productNoListStr = "";
				String tradeIdListStr = "";
				for(int i = 0; i < lineData.size();i++){
					String tradeIdTemp = lineData.getData(i).getString("TRADEID","");
					String productNOTemp = lineData.getData(i).getString("PRODUCTNO","");
					if(i == 0){
						productNoListStr = productNOTemp;
						tradeIdListStr = tradeIdTemp;
					}else{
						productNoListStr = productNoListStr +","+productNOTemp;
						tradeIdListStr = tradeIdListStr+","+productNOTemp;
					}
				}
				
				for(int m=commonData.size()-1;m >=0;m--){
					String attrCodeTemp = commonData.getData(m).getString("ATTR_CODE","");
					if(attrCodeTemp.equals("TRADEID") || attrCodeTemp.equals("PRODUCTNO") ){
						commonData.remove(m);
					}
				}
				
				IData tradeIdData = new DataMap();
				tradeIdData.put("ATTR_CODE","TRADEID");
				tradeIdData.put("ATTR_VALUE",tradeIdListStr);
				
				IData productNoData = new DataMap();
				productNoData.put("ATTR_CODE","PRODUCTNO");
				productNoData.put("ATTR_VALUE",productNoListStr);
				
				commonData.add(tradeIdData);
				commonData.add(productNoData);
				
			}
		
            IData userData = new DataMap();
            userData.put("USER_ID", reqData.getUca().getUserId());
            userData.put("START_DATE", getAcceptTime());
            userData.put("SHEET_TYPE", "6");
            userData.put("PRODUCT_NO", "-1");

            dataset = DatalineUtil.addTradeUserDataLineAttr(commonData, null, userData);
         }
   
        

        super.addTradeDataLineAttr(dataset);
    }
    
    @Override
    protected void makInit(IData data) throws Exception
    {
        super.makInit(data);
        tradeId = data.getString("TRADE_ID","");
        reasonName = data.getString("REMARK","");
        commonData = data.getDataset("COMMON_DATA");
        lineData = data.getDataset("LINE_DATA");
    }
    
    protected void initReqData() throws Exception
    {
        super.initReqData();
    }
    
    @Override
    protected String setTradeTypeCode() throws Exception
    {
    	String productId = reqData.getGrpUca().getProductId();;
    	String tradeTypeCode = "";
    	if("7010".equals(productId)){
    		tradeTypeCode = "3089";
    	}else if("7011".equals(productId)||"70111".equals(productId)||"70112".equals(productId)){
    		tradeTypeCode = "3092";
    	}else if("7012".equals(productId)||"70121".equals(productId)||"70122".equals(productId)){
    		tradeTypeCode = "3095";
    	}else if("7016".equals(productId)){
    		tradeTypeCode = "3848";
    	}
        return tradeTypeCode;

    }
    
    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

    }
    
    /**
     * 注销产品元素
     * 
     * @throws Exception
     */
    public void makReqDataElement() throws Exception
    {
    	super.makReqDataElement();
        IDataset svcList = new DatasetList();
        IDataset discntList = new DatasetList();
        IDataset spSvcList = new DatasetList();
        IDataset resList = new DatasetList();
        IDataset paramList = new DatasetList();

        // 成员基本产品
        String baseMebProductId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "MEM_PRODUCT_ID", reqData.getGrpUca().getProductId()});

        reqData.setBaseMebProductId(baseMebProductId);
        
        String strBrandCode = reqData.getGrpUca().getBrandCode();
        IDataset userElementList =  new DatasetList();
    	if(StringUtils.equals("PWLW", strBrandCode) || StringUtils.equals("WLWG", strBrandCode))
    	{
    		// 物联网品牌
    		userElementList = ProductInfoQry.qryPwlwProductElement(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
    		
    	}else{
    		// 查询成员用户服务、资费和资源信息
    		userElementList = ProductInfoQry.qryUserProductElement(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
    		
    	}

        // 查询成员用户平台服务信息
        IDataset userSpSvcList = UserPlatSvcInfoQry.getGrpPlatSvcByUserId(reqData.getUca().getUserId(), reqData.getGrpUca().getProductId());

        if (IDataUtil.isNotEmpty(userSpSvcList))
        {
            userElementList.addAll(userSpSvcList);
        }

        // 处理元素信息
        for (int i = 0, row = userElementList.size(); i < row; i++)
        {
            IData userElementData = userElementList.getData(i); // 取每个元素

            if (StringUtils.equals(BofConst.ELEMENT_TYPE_CODE_RES, userElementData.getString("ELEMENT_TYPE_CODE")))
            {
                userElementData.put("END_DATE", getAcceptTime());
            }
            else 
            {
                ElementModel model = new ElementModel(userElementData);

                // 获取元素结束时间
                String cancelDate = ElementUtil.getCancelDateForDstMb(model, SysDateMgr.getSysTime());
                //String cancelDate = ElementUtil.getCancelDateForDstMb(model, getAcceptTime());

                // 关于优化M2M套餐生效规则的需求 start
                if (("D".equals(userElementData.getString("ELEMENT_TYPE_CODE"))))
                { // 优惠
                	
                    userElementData.put("END_DATE", SysDateMgr.getLastMonthLastDate());
                }
                else
                {
                    userElementData.put("END_DATE", diversifyBooking ? SysDateMgr.getLastDateThisMonth() : cancelDate);
                }
            }
            //关于优化M2M套餐生效规则的需求  end  
            userElementData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            userElementData.put("DIVERSIFY_ACCT_TAG", "1"); // 加入分散账期处理标记

            String elementType = userElementData.getString("ELEMENT_TYPE_CODE", "");
            String elementId = userElementData.getString("ELEMENT_ID");

            if (elementType.equals("S")) // 服务
            {
                String isNeedPf = GrpPfUtil.getSvcPfState(userElementData.getString("MODIFY_TAG"), reqData.getUca().getUserId(), elementId);

                userElementData.put("IS_NEED_PF", isNeedPf);

                svcList.add(userElementData);
            }
            else if (elementType.equals("D")) // 优惠
            {
                discntList.add(userElementData);
            }
            else if (elementType.equals("R")) // 资源
            {
                resList.add(userElementData);
            }

            else if (elementType.equals("Z")) // SP服务
            {
                userElementData.put("SERVICE_ID", elementId);

                spSvcList.add(userElementData);
            }

            String instId = userElementData.getString("INST_ID");

            // 处理元素参数信息
            if (StringUtils.isNotBlank(instId))
            {
                // 查询用户参数信息
                IDataset userAttrList = UserAttrInfoQry.qryUserAttrByUserRelaInstId(reqData.getUca().getUserId(), instId);

                // 注销用户参数信息
                if (IDataUtil.isNotEmpty(userAttrList))
                {
                    for (int j = 0, jRow = userAttrList.size(); j < jRow; j++)
                    {
                        IData userAttrData = userAttrList.getData(j);
                        userAttrData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        userAttrData.put("END_DATE", userElementData.getString("END_DATE"));
                    }

                    paramList.addAll(userAttrList);
                }
            }
        }

        // 设置数据
        reqData.cd.putSvc(svcList);
        reqData.cd.putSpSvc(spSvcList);
        reqData.cd.putDiscnt(discntList);
        reqData.cd.putRes(resList);
        reqData.cd.putElementParam(paramList);
    }
    
    @Override
    protected void regTrade() throws Exception
    {
    	super.regTrade();
    	 // 成员基本产品
        String baseMebProductId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_EWE_CONFIG", new String[] { "CONFIGNAME", "PARAMNAME" }, "PARAMVALUE", new String[] { "MEM_PRODUCT_ID", reqData.getGrpUca().getProductId()});
    	IData data = bizData.getTrade();
    	data.put("PRODUCT_ID", baseMebProductId);
    }
    
}
