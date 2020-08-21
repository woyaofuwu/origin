package com.asiainfo.veris.crm.order.soa.person.busi.family.mfc;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bizctrl.BizCtrlInfo;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUserReqData;

public class DestoryVitualUser extends GroupBean
{


    protected DestroyGroupUserReqData reqData = null;

    
    protected static Logger log = Logger.getLogger(DestoryVitualUser.class);
    
    @Override
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();
    }
    
    @Override
    public void actTradeSub() throws Exception
    {	
    	super.actTradeSub();
    	
    	//虚拟家庭资料台账;
    	actVitualInfo();
    	
    	// 产品子表
    	actTradePrd();
    	
    	//  删除虚拟用户主体服务资料
        String userId = reqData.getUca().getUserId();
        IDataset svcdatas = UserSvcInfoQry.qryUserSvcByUserId(userId);
        this.dealVisualSvcInfo(svcdatas);

        // 删除虚拟用户主体服务状态资料
        this.dealVisualSvcStateInfo(svcdatas);
        
        //资费台账信息
        dealVisualTradeDiscnt();
        
        //删除虚拟用户付费关系资料
        dealVisualPayRelation();
    }
    
    /*
     * @description 删除虚拟用户付费关系资料
     * @author xunyl
     * @date 2013-07-18
     */
    protected void dealVisualPayRelation() throws Exception
    {
        String memUserId = reqData.getUca().getUserId();

        IData inparam = new DataMap();
        inparam.put("ID", memUserId);
        IData payrelaInfo = PayRelaInfoQry.getPayRelation(inparam);

        // 3- 更改用户付费关系状态
        payrelaInfo.put("MODIFY_TAG", "1");
        payrelaInfo.put("END_CYCLE_ID", SysDateMgr.getNowCyc());

        // 4- 登记成员付费关系订单表
        super.addTradePayrelation(payrelaInfo);
    }
    protected void dealVisualTradeDiscnt() throws Exception
    {
    	// 查询用户资费信息
        IDataset userDiscntList = UserDiscntInfoQry.getUserProductDis(reqData.getUca().getUserId(),"-1");
        if(IDataUtil.isEmpty(userDiscntList)){
        	return;
        }
		IData tempData = userDiscntList.getData(0);
		//资费本月月底失效
		tempData.put("END_DATE", SysDateMgr.getAddMonthsLastDay(1));
		tempData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
		tempData.put("UPDATE_TIME", SysDateMgr.getSysTime());
		tempData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		tempData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId()); 
		super.addTradeDiscnt(tempData);
    }
    
    /**
     * 注销产品子表
     * 
     * @throws Exception
     */
    protected void actTradePrd() throws Exception
    {
        // 查询用户产品信息
        IDataset userProductList =  UserProductInfoQry.getProductInfo(reqData.getUca().getUserId(), null, Route.CONN_CRM_CG);;
        if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========userProductList="+userProductList);
      	}
        if (IDataUtil.isEmpty(userProductList))
        {
            return;
        }

        IDataset userProductAttrDataset = new DatasetList();

        for (int i = 0, row = userProductList.size(); i < row; i++)
        {
            IData userProductData = userProductList.getData(i);

            userProductData.put("END_DATE", getAcceptTime());
            userProductData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }
        if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========userProductAttrDataset="+userProductAttrDataset);
      		log.debug("============DestoryVitualUser========userProductList="+userProductList);
      	}
        //super.addTradeAttr(userProductAttrDataset);

        super.addTradeProduct(userProductList);
    }
    /*
     * @desciption 删除虚拟用户主体服务资料
     */
    protected void dealVisualSvcInfo(IDataset svcdatas) throws Exception
    {
        // 1- 主体服务不存在直接退出
        if (null == svcdatas || svcdatas.size() == 0)
        {
            return;
        }

        // 2- 循环变更主体服务
        IDataset svcset = new DatasetList();
        for (int i = 0; i < svcdatas.size(); i++)
        {
            if (svcdatas.getData(i).getString("MAIN_TAG").equals("1"))
            {
                svcdatas.getData(i).put("MODIFY_TAG",TRADE_MODIFY_TAG.DEL.getValue());
                svcdatas.getData(i).put("END_DATE", getAcceptTime());
                svcset.add(svcdatas.getData(i));
            }
        }

        if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========userProductAttrDataset="+svcset);
      	}
        // 3- 登记主体服务订单表
        super.addTradeSvc(svcset);
    }

    /*
     * @description 删除虚拟用户主体服务状态资料
     */
    protected void dealVisualSvcStateInfo(IDataset svcdatas) throws Exception
    {
        // 1- 主体服务不存在直接退出
        if (null == svcdatas || svcdatas.size() == 0)
        {
            return;
        }

        // 2- 获取并且变更主体服务状态信息(服务状态信息删除时，生成两条数据)
        IDataset svcStateResult = new DatasetList();
        for (int i = 0; i < svcdatas.size(); i++)
        {
            IData svcdata = svcdatas.getData(i);
            String memUserId = svcdata.getString("USER_ID");
            String serviceId = svcdata.getString("SERVICE_ID");
            IDataset svcStateInfoList = UserSvcStateInfoQry.getUserLastStateByUserSvc(memUserId, serviceId);
            for (int j = 0; j < svcStateInfoList.size(); j++)
            {
                IData oldSvcStateInfo = svcStateInfoList.getData(j);
                oldSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                oldSvcStateInfo.put("END_DATE", getAcceptTime());

                IData newSvcStateInfo = new DataMap();
                newSvcStateInfo.putAll(oldSvcStateInfo);
                newSvcStateInfo.put("STATE_CODE", "1");// 销户
                newSvcStateInfo.put("START_DATE", getAcceptTime());
                newSvcStateInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                newSvcStateInfo.put("END_DATE", SysDateMgr.getTheLastTime());
                newSvcStateInfo.put("INST_ID", SeqMgr.getInstId());

                svcStateResult.add(oldSvcStateInfo);
                svcStateResult.add(newSvcStateInfo);
            }
        }

        if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========svcStateResult="+svcStateResult);
      	}
        // 3- 登记主体服务状态订单表
        super.addTradeSvcstate(svcStateResult);
    }
    /**
     * 注销用户资料
     * 
     * @throws Exception
     */
    protected void actTradeUser() throws Exception
    {
        IData userData = reqData.getUca().getUser().toData();

        String reasonCode = reqData.getReasonCode();
        String reasonName = StaticUtil.getStaticValue("TD_B_REMOVE_REASON_GROUP", reqData.getReasonCode());

        userData.put("REMOVE_TAG", "2"); // 注销标志：0-正常、1-主动预销号、2-主动销号、3-欠费预销号、4-欠费销号、5-开户返销、6-过户注销
        userData.put("DESTROY_TIME", getAcceptTime());

        userData.put("REMOVE_EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()); // 注销地市
        userData.put("REMOVE_CITY_CODE", CSBizBean.getVisit().getCityCode()); // 注销市县
        userData.put("REMOVE_DEPART_ID", CSBizBean.getVisit().getDepartId()); // 注销渠道
        userData.put("REMOVE_REASON_CODE", reasonCode); // 注销原因
        userData.put("USER_STATE_CODESET", "1"); // 用户主体服务状态集：见服务状态参数表

        userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 修改标志

        userData.put("REMARK", reasonName); // 暂填注销原因的中文解释

        userData.put("RSRV_STR2", reasonCode);
        userData.put("RSRV_STR8", reasonName);
        if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========userData="+userData);
      	}
        super.addTradeUser(userData);
    }
    
    
    /**
     * @description 虚拟家庭信息入表
     */
    protected void actVitualInfo() throws Exception
    {
//    	 // 客户台账入表
//        IData customerInfo = reqData.getUca().getCustomer().toData();
//        customerInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 归属地市
//        customerInfo.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
//        customerInfo.put("REMOVE_TAG", "1");// 销档标志：0-正常、1-销档
//        customerInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
//        addTradeCustomer(customerInfo);
//        
//        // 注销客户台账信息入表
//        IData custGroup = reqData.getUca().getCustPerson().toData();
//        custGroup.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
//        addTradeCustPerson(custGroup);

        
        //注销用户资料
    	actTradeUser();

    	
        // 注销账户信息
        IData accountInfo = reqData.getUca().getAccount().toData();
        accountInfo.put("REMOVE_TAG", "1"); // 注销标志：0-在用，1-已销
        accountInfo.put("MODIFY_TAG", "1");// 状态属性：0-增加，1-删除，2-变更
        accountInfo.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());//路由地州
        if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========accountInfo="+accountInfo);
      	}
        addTradeAccount(accountInfo);
    }
  
 
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (DestroyGroupUserReqData) getBaseReqData();
    }

    @Override
    protected void init() throws Exception
    {
        // 初始化产品控制信息
        initProductCtrlInfo();
    }
    protected void initProductCtrlInfo() throws Exception
    {

        String productId = reqData.getUca().getProductId();

        getProductCtrlInfo(productId, BizCtrlType.DestoryUser);
    }
    protected void getProductCtrlInfo(String productId, String ctrlType) throws Exception
    {
        // 产品控制信息
        BizCtrlInfo ctrlInfo = BizCtrlBean.qryProductCtrlInfo(productId, ctrlType);

        reqData.setValidateMethod(ctrlInfo.getAttrStr1Value("Validate"));
        reqData.setProductCtrlInfo(productId, ctrlInfo);
    }
    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        //reqData.setBusiType("DstUs");
        reqData.setReasonCode(map.getString("REASON_CODE"));
        reqData.setIfBooking(map.getBoolean("IF_BOOKING"));
        
    }


	@Override
    protected void makUca(IData map) throws Exception
    {	
    	createVitualUserData(map);
    }
    
//    /**
//     * 设置用户账期信息
//     * 
//     * @throws Exception
//     */
//    protected void makUserAcctDay() throws Exception
//    {
//    	
//    }
    
    
    
    
    
    
    /**
     * @description 创建虚拟三户信息
     */
    protected void createVitualUserData(IData map) throws Exception
    {
        // 1- 创建UCA对象
    	UcaData uca = null;
    	if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========SERIAL_NUMBER="+map.getString("SERIAL_NUMBER",""));
      	}
        IData param = new DataMap();
        String sn = map.getString("SERIAL_NUMBER","");
        IData iData = new DataMap();
        if(StringUtils.isNotBlank(map.getString("PRODUCT_OFFERING_ID",""))){
            iData.put("PRODUCT_OFFERING_ID",map.getString("PRODUCT_OFFERING_ID",""));
        }


        //主号UU关系
        IDataset relaUUDatas = MfcCommonUtil.getRelationUusByUserSnRole(sn,"MF","1",iData);
      	//
      	if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser========relaUUDatas="+relaUUDatas);
      	}
      	//最后一个副号，主号销户(UU关系被截止)
      	if(DataUtils.isEmpty(relaUUDatas)){
      		 //查询了PRODUCT_OFFERING_ID该群组的UU，按end_date时间倒序排列
      		relaUUDatas =  MfcCommonUtil.getRelationUusBySnRb(sn,"MF",null,iData);
      		//relaUUDatas =  MfcCommonUtil.getRelationUusByUserSnRole(sn,"MF",null,iData);
      	}
      	String userId ="";
      	if(DataUtils.isNotEmpty(relaUUDatas))
      	{//主号家庭网下的所有副号码UU关系
      		 userId = relaUUDatas.getData(0).getString("USER_ID_A");
      	}
      	
        if (StringUtils.isNotBlank(userId))
        {
            param.put("USER_ID", userId);

            uca = UcaDataFactory.getNormalUcaByUserIdForGrp(param);
        }
        else 
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713,"该群组已经注销！");
        }
        //
      	if (log.isDebugEnabled())
      	{
      		log.debug("============DestoryVitualUser======reqData==uca="+uca);
      	}
        reqData.setUca(uca);
    }


    
	@Override
	protected String setOrderTypeCode() throws Exception {
	  
		return "0";
	}

	@Override
	protected String setTradeId() throws Exception {
		// 生成业务流水号
        String id = SeqMgr.getTradeId();
        return id;
	}

	@Override
	protected String setTradeTypeCode() throws Exception {
		// 设置产品信息
        String productId = reqData.getUca().getProductId();

        // 得到产品控制信息
        BizCtrlInfo productCtrlInfo = reqData.getProductCtrlInfo(productId);

        // 得到业务类型
        String tradeTypeCode = productCtrlInfo.getTradeTypeCode();

        // 设置业务类型
        return tradeTypeCode;
	} 
	
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new DestroyGroupUserReqData();
    }
   

}
