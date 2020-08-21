package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPayPlanInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.group.esp.DataLineDiscntConst;

public class ChangeDataLineGroup extends MemberBean{
    protected GrpModuleData moduleData = new GrpModuleData();
    protected ChangeDataLineGroupReqData reqData = null;
	private String productIdOld=null;
	private String productId=null;
    
    @Override
    public void actTradeSub() throws Exception {
        super.actTradeSub();
        // 产品子表
        actTradePrdAndPrdParam();
        actTradeRelationUU();
        actUserInfo();
        actTradeOtherInfo();
        actgetPayPlanInfo();
        actTradeProduct();
        actTradeDiscnt();
    }
	
	protected void actTradeProduct() throws Exception
    {
		String userIdB=reqData.getUca().getUserId();
   	 	
   	 	IDataset productInfos=UserProductInfoQry.getUserAllProducts(userIdB);
   	 	System.out.println("0214productInfos:"+productInfos);
		if(productInfos!=null&&!productId.equals(productIdOld)){
			String productIdMember=null;
			String productIdMemberOld=null;

			if(DataLineDiscntConst.internetProductId.equals(productIdOld)){
				productIdMemberOld=DataLineDiscntConst.internetProductIdMember;
			}else if(DataLineDiscntConst.internet1ProductId.equals(productIdOld)){
				productIdMemberOld=DataLineDiscntConst.internet1ProductIdMember;
			}else if(DataLineDiscntConst.internet2ProductId.equals(productIdOld)){
				productIdMemberOld=DataLineDiscntConst.internet2ProductIdMember;
			}else if(DataLineDiscntConst.datalineProductId.equals(productIdOld)){
				productIdMemberOld=DataLineDiscntConst.datalineProductIdMember;
			}else if(DataLineDiscntConst.dataline1ProductId.equals(productIdOld)){
				productIdMemberOld=DataLineDiscntConst.dataline1ProductIdMember;
			}else if(DataLineDiscntConst.dataline2ProductId.equals(productIdOld)){
				productIdMemberOld=DataLineDiscntConst.dataline2ProductIdMember;
			}else{
				productIdMemberOld=productIdOld;
			}
			if(DataLineDiscntConst.internetProductId.equals(productId)){
				productIdMember=DataLineDiscntConst.internetProductIdMember;
			}else if(DataLineDiscntConst.internet1ProductId.equals(productId)){
				productIdMember=DataLineDiscntConst.internet1ProductIdMember;
			}else if(DataLineDiscntConst.internet2ProductId.equals(productId)){
				productIdMember=DataLineDiscntConst.internet2ProductIdMember;
			}else if(DataLineDiscntConst.datalineProductId.equals(productId)){
				productIdMember=DataLineDiscntConst.datalineProductIdMember;
			}else if(DataLineDiscntConst.dataline1ProductId.equals(productId)){
				productIdMember=DataLineDiscntConst.dataline1ProductIdMember;
			}else if(DataLineDiscntConst.dataline2ProductId.equals(productId)){
				productIdMember=DataLineDiscntConst.dataline2ProductIdMember;
			}else{
				productIdMember=productId;
			}
			for(int i=0;i<productInfos.size();i++){
				IData productTradeData=productInfos.getData(i);
	        	if(productTradeData.getString("PRODUCT_ID").equals(productIdMemberOld)){
	        		IData productTradeDataNew=new DataMap(productTradeData.toString());
	        		productTradeDataNew.put("PRODUCT_ID",productIdMember);
	        		productTradeDataNew.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
	        		productTradeDataNew.put("START_DATE", getAcceptTime());
	        		productTradeDataNew.put("INST_ID", SeqMgr.getInstId());
	        		super.addTradeProduct(productTradeDataNew);

	        		productTradeData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
	        		productTradeData.put("END_DATE", getAcceptTime());
	        		super.addTradeProduct(productTradeData);
	        	}
			}
		}
    }
	protected void actTradeDiscnt() throws Exception
    {
		System.out.println("0214userId:"+reqData.getUca().getUserId());
		String userIdB=reqData.getUca().getUserId();
    
   	 	IDataset discntInfos = UserDiscntInfoQry.getAllDiscntInfo1(userIdB);
		String nowTime=getAcceptTime();
		if(discntInfos!=null){
			if(productId.equals(productIdOld)){
			for(int i=0;i<discntInfos.size();i++){
				IData discntTradeData=discntInfos.getData(i);
		        if (discntTradeData!=null){
		        	if(discntTradeData.getString("USER_ID_A")!=null&&!"-1".equals(discntTradeData.getString("USER_ID_A"))){
						discntTradeData.put("USER_ID_A",reqData.getGrpUca().getUserId());
						discntTradeData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
			            super.addTradeDiscnt(discntTradeData);
					}
		        }
				}
			}else{
				String discntCode=null;
				String discntCodeOld=null;

				if(DataLineDiscntConst.internetProductId.equals(productId)){
					discntCode=DataLineDiscntConst.internetElementId;
				}else if(DataLineDiscntConst.internet1ProductId.equals(productId)){
					discntCode=DataLineDiscntConst.internet1ElementId;
				}else if(DataLineDiscntConst.internet2ProductId.equals(productId)){
					discntCode=DataLineDiscntConst.internet2ElementId;
				}else if(DataLineDiscntConst.datalineProductId.equals(productId)){
					discntCode=DataLineDiscntConst.datalineElementId;
				}else if(DataLineDiscntConst.dataline1ProductId.equals(productId)){
					discntCode=DataLineDiscntConst.dataline1ElementId;
				}else if(DataLineDiscntConst.dataline2ProductId.equals(productId)){
					discntCode=DataLineDiscntConst.dataline2ElementId;
				}
				if(DataLineDiscntConst.internetProductId.equals(productIdOld)){
					discntCodeOld=DataLineDiscntConst.internetElementId;
				}else if(DataLineDiscntConst.internet1ProductId.equals(productIdOld)){
					discntCodeOld=DataLineDiscntConst.internet1ElementId;
				}else if(DataLineDiscntConst.internet2ProductId.equals(productIdOld)){
					discntCodeOld=DataLineDiscntConst.internet2ElementId;
				}else if(DataLineDiscntConst.datalineProductId.equals(productIdOld)){
					discntCodeOld=DataLineDiscntConst.datalineElementId;
				}else if(DataLineDiscntConst.dataline1ProductId.equals(productIdOld)){
					discntCodeOld=DataLineDiscntConst.dataline1ElementId;
				}else if(DataLineDiscntConst.dataline2ProductId.equals(productIdOld)){
					discntCodeOld=DataLineDiscntConst.dataline2ElementId;
				}
				
				for(int i=0;i<discntInfos.size();i++){
					IData discntTradeData=discntInfos.getData(i);
			        if (discntTradeData!=null){
			        	if(discntTradeData.getString("DISCNT_CODE").equals(discntCodeOld)){
			        		String instIdOld=discntTradeData.getString("INST_ID");
			        		String instId=SeqMgr.getInstId();
			        		IData discntTradeDataNew=new DataMap(discntTradeData.toString());
			        		if(discntTradeDataNew.getString("USER_ID_A")!=null&&!"-1".equals(discntTradeDataNew.getString("USER_ID_A"))){
			        			discntTradeDataNew.put("USER_ID_A",reqData.getGrpUca().getUserId());
							}
			        		discntTradeDataNew.put("DISCNT_CODE",discntCode);
			        		discntTradeDataNew.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
			        		//开始时间如果大于当前时间则不更改
			        		discntTradeDataNew.put("START_DATE", nowTime.compareTo(discntTradeData.getString("START_DATE"))<0?discntTradeData.getString("START_DATE"):nowTime);
			        		discntTradeDataNew.put("INST_ID", instId);
				            super.addTradeDiscnt(discntTradeDataNew);
				    		System.out.println("0310discntTradeDataNew:"+discntTradeDataNew);

			        		discntTradeData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
			        		discntTradeData.put("END_DATE", SysDateMgr.getLastMonthLastDate());
				            super.addTradeDiscnt(discntTradeData);
				            IDataset arrtInfos =UserAttrInfoQry.getAttrInfoByUserIdToInstType(userIdB,"D",instIdOld);
				            
				    		if(arrtInfos!=null){
								for(int j=0;j<arrtInfos.size();j++){
									IData arrtInfoData=arrtInfos.getData(j);
							        if (arrtInfoData!=null){
						        		IData arrtInfoDataNew=new DataMap(arrtInfoData.toString());
						        		arrtInfoDataNew.put("INST_ID", SeqMgr.getInstId());
						        		arrtInfoDataNew.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
						        		//开始时间如果大于当前时间则不更改
						        		arrtInfoDataNew.put("START_DATE", nowTime.compareTo(arrtInfoData.getString("START_DATE"))<0?arrtInfoData.getString("START_DATE"):nowTime);
						        		arrtInfoDataNew.put("RELA_INST_ID", instId);
						        		arrtInfoDataNew.put("ELEMENT_ID", discntCode);
							            super.addTradeAttr(arrtInfoDataNew);

							            arrtInfoData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
							            arrtInfoData.put("END_DATE", SysDateMgr.getLastMonthLastDate());
							            super.addTradeAttr(arrtInfoData);
							        }
								}
				    		}

			        	}
			        	
			        	
			        	
			        	
			        }
					
				}
			}
		}
		
		
    }
    protected void actTradeRelationUU() throws Exception
    {
        IData relaData = new DataMap();
        relaData.put("USER_ID_A", reqData.getGrpUca().getUserId());
        relaData.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
        relaData.put("USER_ID_B", reqData.getUca().getUserId());
        relaData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
        relaData.put("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId()));
        relaData.put("ROLE_CODE_A", "0");
        relaData.put("ROLE_CODE_B", "2");
        relaData.put("INST_ID", SeqMgr.getInstId());
        relaData.put("START_DATE", diversifyBooking ? reqData.getFirstTimeNextAcct() : getAcceptTime());
        relaData.put("END_DATE", SysDateMgr.getTheLastTime());
        relaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        super.addTradeRelation(relaData);
        
        
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
//getRelationUUByPk getMemForOther
        IData relaList = RelaUUInfoQry.getRelationUUByPk(reqData.getOldGrpUser(), reqData.getUca().getUserId(), relationTypeCode,null);

        if (IDataUtil.isEmpty(relaList))
        {
            return;
        }

        IData oldrelaData = relaList;
        oldrelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        oldrelaData.put("END_DATE", getAcceptTime());

        super.addTradeRelation(oldrelaData);
    }

     /**
     * 处理产品和产品参数信息
     * 
     * @throws Exception
     */
    protected void actTradePrdAndPrdParam() throws Exception
    {
        
    }
    protected void actUserInfo() throws Exception
    {
         IData userData = reqData.getUca().getUser().toData();
         
         userData.put("CUST_ID", reqData.getGrpUca().getCustId());
         userData.put("USECUST_ID", reqData.getGrpUca().getCustId());
         userData.put("CONTRACT_ID", reqData.getGrpUca().getUser().getContractId());
         userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
         super.addTradeUser(userData);
    }
    protected void  actTradeOtherInfo() throws Exception
    {
        IDataset otherInfoList = new DatasetList();
        IDataset otherInfo = UserOtherInfoQry.queryUserOtherInfos(reqData.getUca().getUserId(),"NPRI","N001");
        if(IDataUtil.isNotEmpty(otherInfo)){
            for(int i=0;i<otherInfo.size();i++){
                IData infos  = otherInfo.getData(i);
                infos.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                infos.put("END_DATE", getAcceptTime());
                otherInfoList.add(infos);
            }
        }
        IDataset newGroupotherInfo = UserOtherInfoQry.queryUserOtherInfos(reqData.getGrpUca().getUserId(),"NPRI","N001");
        if(IDataUtil.isNotEmpty(newGroupotherInfo)){
            for(int i=0;i<newGroupotherInfo.size();i++){
                IData infos  = newGroupotherInfo.getData(i);
                infos.put("USER_ID", reqData.getUca().getUserId());
                infos.put("MODIFY_TAG",  TRADE_MODIFY_TAG.Add.getValue());
                infos.put("START_DATE", getAcceptTime());
                infos.put("END_DATE", SysDateMgr.getTheLastTime());
                otherInfoList.add(infos);
            }
        }
        if(IDataUtil.isNotEmpty(otherInfoList)){
            addTradeOther(otherInfoList);
        }
    }
   
    protected void actgetPayPlanInfo() throws Exception{
        //新增付费计划
        IDataset planInfos = getPayPlanInfo("P");
        IData palanInfo =  planInfos.first();
        palanInfo.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
        palanInfo.put("USER_ID", reqData.getUca().getUserId());
        String planTypeCode = "P";
        String planName = StaticUtil.getStaticValue("PAYPLAN_PLANTYPE", planTypeCode);
        palanInfo.put("PLAN_ID", SeqMgr.getPlanId());
        palanInfo.put("PLAN_TYPE_CODE", planTypeCode);
        palanInfo.put("PLAN_NAME", planName);
        palanInfo.put("PLAN_DESC", planName);
        palanInfo.put("START_DATE", SysDateMgr.getSysTime());
        palanInfo.put("END_DATE", SysDateMgr.getTheLastTime());
       
        //新增付费关系表
        IDataset payRelaList = new DatasetList(); // 付费关系
        IData addPayRelaData = new DataMap();
        String payItemCode = "-1"; //-1表示代付所有付费项
        addPayRelaData.put("USER_ID", reqData.getUca().getUserId());
        addPayRelaData.put("ACCT_ID", reqData.getGrpUca().getAcctId());
        addPayRelaData.put("PAYITEM_CODE", payItemCode);
        addPayRelaData.put("ACCT_PRIORITY", "0"); // 账户优先级
        addPayRelaData.put("USER_PRIORITY", "0"); // 用户优先级
        addPayRelaData.put("BIND_TYPE", "0"); // 账户绑定方式
        addPayRelaData.put("DEFAULT_TAG", "1"); // 默认标志
        addPayRelaData.put("ACT_TAG", "1"); // 作用标志
        addPayRelaData.put("LIMIT_TYPE", "0"); // 限定方式
        addPayRelaData.put("LIMIT", "0"); // 限定值
        addPayRelaData.put("COMPLEMENT_TAG", "0"); // 限定值
        addPayRelaData.put("INST_ID", SeqMgr.getInstId()); // inst_id
        addPayRelaData.put("START_CYCLE_ID", SysDateMgr.getNextCycle()); // 开始账期下账期
        if(!productId.equals(productIdOld)){
            addPayRelaData.put("START_CYCLE_ID", SysDateMgr.getNowCyc()); // 变更产品当月立即生效
		}
        // 基类有对6位的处理
        addPayRelaData.put("END_CYCLE_ID", SysDateMgr.getEndCycle20501231()); // 开始账期
        addPayRelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        // 添加付费关系
        payRelaList.add(addPayRelaData);
        
        
        
        //删除原有的付费计划
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());
        IData relaList = RelaUUInfoQry.getRelationUUByPk(reqData.getOldGrpUser(), reqData.getUca().getUserId(), relationTypeCode,null);
        String userIdA =  relaList.getString("USER_ID_A");
        IDataset payPlans = UserPayPlanInfoQry.getAttrGrpMemPayPlanByUserId(reqData.getUca().getUserId(), userIdA);
        if(IDataUtil.isNotEmpty(payPlans)){
            for(int i = 0 ;i<payPlans.size();i++){
                IData oldrelaData = payPlans.getData(i);
                oldrelaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                oldrelaData.put("END_DATE", SysDateMgr.getSysTime());
                planInfos.add(oldrelaData);
            }
            
        }
        
        
        
        
        //删除原有付费关系
        IDataset payRelas =  PayRelaInfoQry.getPayrelaByUserId(reqData.getUca().getUserId());
        if(IDataUtil.isNotEmpty(payRelas)){
            for(int i=0;i<payRelas.size();i++){
                IData payRela = payRelas.getData(i);
                payRela.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                payRela.put("END_CYCLE_ID",SysDateMgr.getNowCyc());
                if(!productId.equals(productIdOld)){
                	payRela.put("END_CYCLE_ID", SysDateMgr.getLastCycle()); // 产品过户当月立即生效
        		}
                payRelaList.add(payRela);
            }
        }
        
        
        addTradeUserPayplan(planInfos); //付费计划
        addTradePayrelation(payRelaList); //付费关系
    }
    /*protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        String custId = reqData.getGrpUca().getCustId();
        reqData.getUca().getUser().setCustId(custId);
    }*/
    
/*  @Override
    protected void setTradeUser(IData map) throws Exception
    {
        // reqData.getUca().getCustPerson().setCustId(reqData.getGrpUca().getCustId());
         super.setTradeUser(map);
         map.put("CUST_ID", reqData.getGrpUca().getCustId());
         String custId = reqData.getGrpUca().getCustId();
         reqData.getUca().getUser().setCustId(custId);
    }*/

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUcaForMebNormal(map);
    }

    @Override
    protected String setOrderTypeCode() throws Exception {
         return "0";
    }

    @Override
    protected String setTradeTypeCode() throws Exception {
        // 设置业务类型
        return "3002";
    }
    
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setOldGrpUser(map.getString("OLD_GRP_USERID",""));
        reqData.setGrpProductId(map.getString("PRODUCT_ID"));
    }
    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
        reqData.setOldGrpUser(map.getString("OLD_GRP_USERID",""));
        reqData.setGrpProductId(map.getString("PRODUCT_ID"));
        productIdOld=map.getString("PRODUCT_ID_OLD");
        productId=map.getString("PRODUCT_ID","");
    }
    
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeDataLineGroupReqData) getBaseReqData();
    }
    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception {

        super.actTradeBefore();

    }
    protected void setTradeBase() throws Exception {
        super.setTradeBase();
    }
    
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeDataLineGroupReqData();
    }
    
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("PRODUCT_ID", reqData.getGrpProductId());
    }

}
