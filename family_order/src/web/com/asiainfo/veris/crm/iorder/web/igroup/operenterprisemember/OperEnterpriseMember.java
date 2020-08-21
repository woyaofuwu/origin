package com.asiainfo.veris.crm.iorder.web.igroup.operenterprisemember;

import java.util.Iterator;
import java.util.Map;

import org.apache.tapestry.IRequestCycle;
import org.mvel2.util.Varargs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.annotation.varargs;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.web.util.CookieUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.FrontProdConverter;
import com.asiainfo.veris.crm.iorder.web.igroup.ecbasepage.EcBasePage;
import com.asiainfo.veris.crm.iorder.web.igroup.pagedata.PageDataTrans;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationuuinfo.RelationUUInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userpayplan.UserPayPlanInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class OperEnterpriseMember extends EcBasePage
{
    private static final Logger logger = LoggerFactory.getLogger(OperEnterpriseMember.class);

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData info = new DataMap();
        String productTree = getData().getString("PRODUCTTREE_LIMIT_PRODUCTS","");//渠道调集团VPMN成员受理标记
        String showFuncNavigation = createFuncNaviCookie(cycle);
        info.put("SHOW_FUNC_NAVIGATION", showFuncNavigation);

        //是否客服接入
        info.put("KF_FLAG", getData().getBoolean("KF_FLAG", false));
        info.put("ESOP_TAG", getData().getString("ESOP_TAG", ""));
        info.put("PRODUCTTREE_LIMIT_PRODUCTS", productTree);
        
        //是否是无外框登陆的
        info.put("NOLogin_FLAG", getData().getBoolean("NOLogin_FLAG", false));
        setInfo(info);
    }

    /**
     * 根据集团商品编码查询成员商品信息
     *
     * @param cycle
     * @throws Exception
     */
    public void queryOfferInfo(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        String ecOfferId = data.getString("OFFER_ID", "");
        String ecSubInsId = data.getString("EC_USER_ID");
        String mebSn = data.getString("MEB_SERIAL_NUMBER");
        String ifCentreType = data.getString("IF_CENTRETYPE","");
        String brand = data.getString("BRAND_CODE","");
        String operType = data.getString("OPER_TYPE","");

        String ecOfferCode=IUpcViewCall.getOfferCodeByOfferId(ecOfferId);
        
        //BBOSS集团产品状态异常时不可进行成员新增
        if(brand.equals("BOSG") && operType.equals("CrtMb")){
        	IData input = new DataMap();
        	input.put("USER_ID", ecSubInsId);
        	input.put("ROUTE_ID", Route.CONN_CRM_CG);

	        IDataset userInfos =   CSViewCall.call(this, "CS.UserGrpMerchInfoNewQrySVC.getAllBySvcIdStateCode", input);
	        if (IDataUtil.isNotEmpty(userInfos))
	        {
	            for (int i = 0; i < userInfos.size(); i++)
	            {
	                IData userInfo = (IData) userInfos.get(i);
	                String stateCode = userInfo.getString("STATE_CODE");
	                if ("3".equals(stateCode))
	                {
	                	CSViewException.apperr(CrmCommException.CRM_COMM_103, "该用户服务状态处于非正常状态！");

	                }
	            }
	        }
        }
        // 成员商品编码
        String offerId = IUpcViewCall.queryMemOfferIdByOfferId(ecOfferId);

        IData offerInfo = IUpcViewCall.queryOfferByOfferId(offerId,"Y");
        if (DataUtils.isEmpty(offerInfo))
        {
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "根据OFFER_ID"+offerId+"没有查询到商品信息！");
        }
        offerInfo.put("OPER_TYPE", BizCtrlType.CreateMember);
        offerInfo.put("BRAND_CODE", offerInfo.getString("BRAND_CODE"));
        IData ecOfferInfo = IUpcViewCall.queryOfferByOfferId(ecOfferId,"Y");
        offerInfo.put("EC_OFFER_NAME", ecOfferInfo.getString("OFFER_NAME"));

        //查询TRADE_TYPE_CODE
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(ecOfferInfo.getString("OFFER_CODE"), "P", BizCtrlType.CreateMember, "TradeTypeCode");
        if(StringUtils.isEmpty(tradeTypeCode)){
            CSViewException.apperr(ProductException.CRM_PRODUCT_97);
        }
        offerInfo.put("TRADE_TYPE_CODE", tradeTypeCode);



        setInitOffer(offerInfo);
        IData info = new DataMap();
        String relationTypeCode = IUpcViewCall.getRelationTypeCodeByOfferId(ecOfferId);
        //设置成员角色
        IDataset roleList = queryRoleList(relationTypeCode, offerId);
        info.put("ROLE_INFO", roleList);

        //设置付费方式
        IDataset payPlans = UserPayPlanInfoIntfViewUtil.qryPayPlanInfosByGrpUserIdForGrp(this, ecSubInsId);
        IDataset payTypeSet=comsisData(payPlans);
        info.put("PAY_TYPE_SET", payTypeSet);


        //是否展示文件上传
        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", ecOfferCode);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());

        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
            info.put("MEB_FILE_SHOW",true);
        }
        /*************************REQ201810100001优化政企集中稽核相关功能需求 begin*************************/
        String authCheckMode = data.getString("cond_CHECK_MODE", "");
        boolean flag = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_OPERENTERPRISE");//有权限免稽核
        //免身份校验
        System.out.println("========wuhaodata=="+data);
        System.out.println("========authCheckMode=="+authCheckMode);
        if(StringUtils.isBlank(authCheckMode) || "F".equals(authCheckMode) || "false".equals(authCheckMode)){
        	IData iparamCheck = new DataMap();
        	iparamCheck.put("SUBSYS_CODE", "CSM");
        	iparamCheck.put("PARAM_ATTR", "839");
        	iparamCheck.put("PARAM_CODE", ecOfferCode);
        	iparamCheck.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        	System.out.println("========iparamCheck=="+iparamCheck);
            IDataset resultSetList = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparamCheck);
            System.out.println("========resultSetList=="+resultSetList);
            System.out.println("========flag=="+flag);
            if (IDataUtil.isNotEmpty(resultSetList)){
            	info.put("MEB_VOUCHER_FILE_SHOW","false");
            }else{
            	if(flag){
            		info.put("MEB_VOUCHER_FILE_SHOW","false");
            	}else{
            		info.put("MEB_VOUCHER_FILE_SHOW","true");
            	}
            }
        }
        //需要身份校验则不显示凭证上传
        else{
        	info.put("MEB_VOUCHER_FILE_SHOW","false");
        }
        
        /*************************REQ201810100001优化政企集中稽核相关功能需求  end*************************/
        
        setInfo(info);
        /*************************调用规则 开始*************************/
        checkMebBaseInfoRule(ifCentreType,ecSubInsId,mebSn,"CrtMb",tradeTypeCode,ecOfferCode);

        /*************************调用规则 结束*************************/

        IData ajaxData = new DataMap();
        ajaxData.put("OFFER_DATA", offerInfo);
        if(StringUtils.isBlank(offerId)){
            //不存在成员产品，不显示待设置
            ajaxData.put("IS_SHOW_SET_TAG", false);
        }
        this.setAjax(ajaxData);

    }

    /**
     * 查询成员订购的商品
     *
     * @param cycle
     * @throws Exception
     */
    public void queryInsOfferInfoForMeb(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData();
        String ecSubInsId = data.getString("EC_USER_ID", "");
        String mebSubInsId = data.getString("MEB_USER_ID", "");
        String ecOfferId = data.getString("OFFER_ID", "");
        String operType = data.getString("OPER_TYPE", "");
        String brand = data.getString("BRAND_CODE");
        String mebSn = data.getString("MEB_SERIAL_NUMBER");
        String mebEparchyCode = data.getString("MEB_EPARCHY_CODE");
        String ecOfferCode=IUpcViewCall.getOfferCodeByOfferId(ecOfferId);


        IData offerInfo = IUpcViewCall.queryOfferByOfferId(ecOfferId,"Y");

        // 成员商品编码
        String offerId = IUpcViewCall.queryMemOfferIdByOfferId(ecOfferId);

        IData mebOffer = IUpcViewCall.queryOfferByOfferId(offerId,"");

        String mebOfferId = mebOffer.getString("OFFER_ID");
        String mebOfferCode = mebOffer.getString("OFFER_CODE");
        // 成员商品实例
        IData insOfferData=new DataMap();

        //查询TRADE_TYPE_CODE
        String tradeTypeCode = CommonViewCall.getAttrValueFromAttrBiz(offerInfo.getString("OFFER_CODE"), "P", operType, "TradeTypeCode");
        if(StringUtils.isEmpty(tradeTypeCode)){
            CSViewException.apperr(ProductException.CRM_PRODUCT_97);
        }

        insOfferData.put("TRADE_TYPE_CODE", tradeTypeCode);

        IDataset insPriceOfferDataset = new DatasetList();
        IDataset insServiceOfferDataset = new DatasetList();

        // 查询子销售品实例
        IDataset insSubOfferDataset = queryMebInsBundleOfferDataset(ecSubInsId, mebSubInsId, mebOfferCode,mebOfferId);
        setServicePriceDataset(insSubOfferDataset, insServiceOfferDataset, insPriceOfferDataset);

        setServiceOfferList(insServiceOfferDataset);
        setPriceOfferList(insPriceOfferDataset);

        insOfferData.put("OFFER_CODE", mebOfferCode);
        insOfferData.put("OFFER_ID", mebOfferId);
        insOfferData.put("OFFER_NAME", mebOffer.getString("OFFER_NAME"));
        insOfferData.put("OPER_TYPE", operType);
        insOfferData.put("BRAND_CODE", brand);
        insOfferData.put("EC_OFFER_CODE", offerInfo.getString("OFFER_CODE"));
        insOfferData.put("EC_OFFER_ID", ecOfferId);
        insOfferData.put("EC_OFFER_NAME", offerInfo.getString("OFFER_NAME"));
        insOfferData.put("OFFER_TYPE", mebOffer.getString("OFFER_TYPE"));
        insOfferData.put("USER_ID", mebSubInsId);

        IData mebProduct = queryMebProduct(mebSubInsId, ecSubInsId, mebOfferCode, "12", mebEparchyCode);
        if(IDataUtil.isNotEmpty(mebProduct))
        {
            insOfferData.put("OFFER_INS_ID", mebProduct.getString("INST_ID"));
        }

        setInitOffer(insOfferData);

        IData info = new DataMap();
        //设置成员角色
        String relationTypeCode = IUpcViewCall.getRelationTypeCodeByOfferId(ecOfferId);
        IDataset roleList = queryRoleList(relationTypeCode, offerId);
        info.put("ROLE_INFO", roleList);
        
        //做变更操作时,读取加入集团的成员角色
        if(operType.equals("ChgMb")){
        	IDataset relationList = RelationUUInfoIntf.qryRelaUUInfosByUserIdBAndUserIdA(this, mebSubInsId, ecSubInsId, Route.CONN_CRM_CG);
        	if(IDataUtil.isNotEmpty(relationList)){
        		info.put("REL_SUBSCRIBER_ROLE", relationList.first().getString("ROLE_CODE_B"));
        	}
        }

        //是否展示文件上传

        IData iparam = new DataMap();
        iparam.put("SUBSYS_CODE", "CSM");
        iparam.put("PARAM_ATTR", "7354");
        iparam.put("PARAM_CODE", ecOfferCode);
        iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
        if (IDataUtil.isNotEmpty(resultSet)){
            info.put("MEB_FILE_SHOW",true);
        }
        
        /*************************REQ201810100001优化政企集中稽核相关功能需求 begin*************************/
        String authCheckMode = data.getString("cond_CHECK_MODE", "");
        boolean flag = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_OPERENTERPRISE");//有权限免稽核
        //免身份校验
        System.out.println("========wuhaodata=="+data);
        System.out.println("========authCheckMode=="+authCheckMode);
        if(StringUtils.isBlank(authCheckMode) || "F".equals(authCheckMode) || "false".equals(authCheckMode)){
        	IData iparamCheck = new DataMap();
        	iparamCheck.put("SUBSYS_CODE", "CSM");
        	iparamCheck.put("PARAM_ATTR", "839");
        	iparamCheck.put("PARAM_CODE", ecOfferCode);
        	iparamCheck.put("EPARCHY_CODE", Route.getCrmDefaultDb());
        	System.out.println("========iparamCheck=="+iparamCheck);
            IDataset resultSetList = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparamCheck);
            System.out.println("========resultSetList=="+resultSetList);
            System.out.println("========flag=="+flag);
            if (IDataUtil.isNotEmpty(resultSetList)){
            	info.put("MEB_VOUCHER_FILE_SHOW","false");
            }else{
            	if(flag){
            		info.put("MEB_VOUCHER_FILE_SHOW","false");
            	}else{
            		info.put("MEB_VOUCHER_FILE_SHOW","true");
            	}
            }
        }
        //需要身份校验则不显示凭证上传
        else{
        	info.put("MEB_VOUCHER_FILE_SHOW","false");
        }
        
        /*************************REQ201810100001优化政企集中稽核相关功能需求  end*************************/
        
        
        setInfo(info);

        /*************************调用规则 开始*************************/
        checkMebBaseInfoRule("",ecSubInsId,mebSn,operType,tradeTypeCode,ecOfferCode);

        /*************************调用规则 结束*************************/

        IData group = new DataMap(); //订购的子商品归属组信息

        // 构造商品数据对象
        IData mainOfferData = buildOfferData(insOfferData, insSubOfferDataset, operType, "MEM", group);

        IData ajaxData = new DataMap();
        IDataset ajaxDataset = new DatasetList();

        IData ajaxOfferData = new DataMap();
        if(StringUtils.isBlank(mebOfferId)){
            //不存在成员产品，不显示待设置
            ajaxData.put("IS_SHOW_SET_TAG", false);
            ajaxOfferData.put("OFFER_ID", ecOfferId);

        }else{

            ajaxOfferData.put("OFFER_ID", mebOfferId);
        }
        ajaxOfferData.put("OFFER_DATA", mainOfferData);
        ajaxDataset.add(ajaxOfferData);
        ajaxData.put("SELECT_GROUP_OFFER", group);
        ajaxData.put("OFFER_DATAS", ajaxDataset);
        this.setAjax(ajaxData);
        
    }

    /**
     *将集团产品用户id转换成集团商品用户id
     * @param merchpSubInstID 集团产品用户id
     * @param ecOfferID集团商品offerid
     * @throws Exception
     */
    public String transBBossSubscriberId(String merchpSubInstID,String ecOfferID) throws Exception
    {
        String merchSubInstID="";
        String relationTypeCode = IUpcViewCall.getRelationTypeCodeByOfferId(ecOfferID);

        IData param = new DataMap();
        param.put("SUBSCRIBER_REL_TYPE", relationTypeCode);
        param.put("REL_USER_ID", merchpSubInstID);
        IDataset subRelBB = CSViewCall.call(this,"OC.enterprise.IUmSubscriberRelBBQuerySV.querySubscriberRelByRelTypeRelSubInsId", param);

        if (DataUtils.isNotEmpty(subRelBB)){
            merchSubInstID=subRelBB.first().getString("USER_ID");
        }


        return merchSubInstID;
    }

    /**
     * 初始化产品特征规格区域
     *
     * @param cycle
     * @throws Exception
     */
    public void initOfferChaSpecByOfferId(IRequestCycle cycle) throws Exception
    {
        IData pageData = this.getData();
        String custId = pageData.getString("CUST_ID");
        String offerId = pageData.getString("OFFER_ID");
        String offerCode = pageData.getString("OFFER_CODE");
        String curOfferId = pageData.getString("SUB_OFFER_ID");//当前设置的商品id
        String curOfferCode = pageData.getString("SUB_OFFER_CODE");//当前设置的商品Code
        String curOfferType = pageData.getString("SUB_OFFER_TYPE");//当前设置的商品类型
        String brandCode = pageData.getString("BRAND_CODE");
        String subscriberInsId = pageData.getString("USER_ID");
        String ecSubscriberInsId = pageData.getString("EC_USER_ID");
        String userEparchyCode = pageData.getString("USER_EPARCHY_CODE");
        String groupID = pageData.getString("GROUP_ID");
        String subOfferCode = pageData.getString("SUB_OFFER_CODE");
        String ifCentreType = pageData.getString("IF_CENTRETYPE","");
        String ecOfferCode = pageData.getString("OFFER_CODE","");

        String operType = PageDataTrans.transOperCodeToOperType(pageData.getString("OPER_CODE"), "MEM");
        String mebOfferId = "";

        String realOperType = operType;
        if("2".equals(ifCentreType)){//8000产品即可为普通v网也可为融合V网，两者参数不一样
            //如果是融合V网，为了跟普通V网区分，加Centre
            realOperType = "Centre"+operType;
        }
        IData inAttr = new DataMap();
        inAttr.put("FLOW_ID", curOfferId); //POINT_ONE110000008000
        inAttr.put("NODE_ID", realOperType); //POINT_TWO 
        if ("BOSG".equals(brandCode))
        {
            // BBOSS本地产品编码转换为全网产品编码
            IData input = new DataMap();
            input.put("OPER_TYPE", inAttr.getString("NODE_ID"));
            input.put("PROD_SPEC_ID", curOfferCode);
            input.put("MERCHP_OPER_TYPE", pageData.getString("MERCHP_OPER_TYPE"));
            FrontProdConverter.prodConverter(this,input, false);

            // 操作类型转换为全网操作类型
            inAttr.put("FLOW_ID", input.getString("PROD_SPEC_ID"));
            inAttr.put("NODE_ID", input.getString("OPER_TYPE"));
            mebOfferId = FrontProdConverter.prodIdConvert(curOfferId);
        }
        setInAttr(inAttr);

        //BBoss 静态表加载的数据 通过 产品编码 拿到商品编码
        if("BOSG".equals(brandCode)){
            IDataset upOfferIdList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(null,offerId,"4");
            if(DataUtils.isNotEmpty(upOfferIdList)){
                String upOfferId = upOfferIdList.first().getString("OFFER_ID");
                offerCode = IUpcViewCall.getOfferCodeByOfferId(upOfferId);
            }
        }

        String svcName ="";
        // 初始化产品特征(非静态表加载的数据) 
        // 存在既有特殊产品参数又有服务参数的情况,产品参数配置P,服务参数配置S,通过当前设置的商品类型来做区分
        if("S".equals(curOfferType)){
            svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, "S", realOperType, "InitOfferCha");
        }else{
            svcName = CommonViewCall.getAttrValueFromAttrBiz(this, offerCode, "P", realOperType, "InitOfferCha");
        }
        if(StringUtils.isBlank(svcName))
        {//没有配置，取默认服务初始化
            if(BizCtrlType.CreateMember.equals(operType))
            {
                svcName = "SS.QueryAttrParamSVC.queryOfferChaForInit";
            }
            else if(BizCtrlType.ChangeMemberDis.equals(operType))
            {
                svcName = "SS.QueryAttrParamSVC.queryUserAttrForChgInit";
            }
            else
            {
                CSViewException.apperr(CrmCommException.CRM_COMM_103, "操作类型异常！OPER_TYPE="+operType);
            }
        }

        IData busi = new DataMap();
        IData input = new DataMap();
        input.put("EC_OFFER_ID", offerId); //集团主商品编码
        input.put("OFFER_ID", curOfferId);
        input.put("OFFER_CODE", curOfferCode);
        input.put("ATTR_OBJ", "1"); //成员是1
        input.put("EPARCHY_CODE", userEparchyCode);
        input.put("MEM_OFFER_ID", mebOfferId);
        input.put("USER_ID", subscriberInsId);
        input.put("EC_USER_ID", ecSubscriberInsId);
        input.put("CUST_ID", custId);
        input.put("OFFER_INS_ID", pageData.getString("OFFER_INS_ID"));
        input.put("INST_TYPE", curOfferType);//USER_ATTR表中的INST_TYPE属性
        input.put("IS_MEB", true);
        input.put("GROUP_ID", groupID);
        input.put("SUB_OFFER_CODE", subOfferCode);
        input.put(Route.ROUTE_EPARCHY_CODE, userEparchyCode);
        input.put("OPER_TYPE", operType);
        input.put("EC_OFFER_CODE", ecOfferCode);
        IDataset result = CSViewCall.call(this, svcName, input);
        if (IDataUtil.isNotEmpty(result))
        {
            IData param = result.getData(0);
            Iterator itr = param.keySet().iterator();
            while (itr.hasNext())
            {
                String key = itr.next().toString();
                IData paramData = param.getData(key);

                busi.put(key, paramData);
            }
        }
        setBusi(busi);
    }

    /**
     * 前台数据提交
     *
     * @param cycle
     * @throws Exception
     */
    public void submit(IRequestCycle cycle) throws Exception
    {
        IDataset datas = new DatasetList(getData().getString("SUBMIT_DATA"));

        debug("==============================前台传入数据" + datas);
        
        IData cond = new DataMap();
        cond.put("IN_PARAMS", datas);
        cond.put("OPER_TYPE", datas.first().getString("OPER_TYPE"));
        cond.put("OFFER_CODE", getData().getString("OFFER_CODE"));

        PageDataTrans pageTransData = PageDataTrans.getInstance(cond);

        IData svcParam = pageTransData.transformData();
		
        //判断该用户是否有免二次确认权限 update by zhuwj		//取消单个成员办理界面必选功能	chenhh6
        //if("10009805".equals(getData().getString("OFFER_CODE"))){
        //boolean twoCHECK = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "TWOCHECK");
        // if(!twoCHECK && "false".equals(svcParam.getString("PAGE_SELECTED_TC","0"))){
        //	 //如果为假则二次确认为必选，否则非必选
        //	 throw new Exception("成员是否二次确认为必选项，请选择！");		
        // }
        //	
        //}

        //特殊产品参数校验,因为界面做了改造,部分原来的验证逻辑并不能在参数界面点击确定的时候就能做出正确的逻辑判断,所以在这里加个方法,有需要添加个性产品逻辑验证的可以写在里面的分支情况中
        //一般成员新增和变更才有特殊产品参数校验
        if("CrtMb".equals(cond.getString("OPER_TYPE"))||"ChgMb".equals(cond.getString("OPER_TYPE")))
          checkProductParam(cond.getString("OFFER_CODE"),svcParam,cond.getString("OPER_TYPE"));

        // checkProductInfoRule(datas.first().getData("MEM_SUBSCRIBER").getString("USER_ID",""),svcParam,datas.first().getString("OPER_TYPE"));
        String svc = pageTransData.getSvcName();
        String brand =  getData().getString("BRAND_CODE");
        logger.debug("=============================获取品牌:", brand);
   
        //BBOSS成员新增，在成员已订购某商品下产品的情况下，若需要订购该商品下未被订购的产品，需要走成员变更逻辑
        if("BOSG".equals(brand) && "CrtMb".equals(cond.getString("OPER_TYPE"))){
            String merchUserId = svcParam.getString("USER_ID");
        	IData param = new DataMap();
        	param.put("USER_ID_A", merchUserId);
        	param.put("RELATION_TYPE_CODE", "90");
        	param.put("USER_ID_B", datas.first().getData("MEM_SUBSCRIBER").getString("USER_ID",""));
        	param.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());

        	IDataset mebBBbOffers = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.qryBBInfoByUserIdAB", param);
        	if(IDataUtil.isNotEmpty(mebBBbOffers)){
        		svc = "CS.ChangeBBossMemSVC.crtOrder";
        	}
        }

        if(IDataUtil.isNotEmpty(datas.first().getData("AUDIT_INFO"))){
        	IData auditInfo =datas.first().getData("AUDIT_INFO");
        	if(!StringUtils.isBlank(auditInfo.getString("AUDIT_STAFF_ID"))){
        		svcParam.put("AUDIT_STAFF_ID", auditInfo.getString("AUDIT_STAFF_ID",""));
        	}
        	if(!StringUtils.isBlank(auditInfo.getString("MEB_VOUCHER_FILE_LIST"))){
        		svcParam.put("MEB_VOUCHER_FILE_LIST", auditInfo.getString("MEB_VOUCHER_FILE_LIST",""));
        	}
        }

        logger.debug("=============================调用服务 {} 数据 {}", svc, svcParam);

        // 调登记服务
        IDataset result = CSViewCall.call(this,svc, svcParam);

        logger.debug("=============================服务返回结果:{}", result);
        logger.debug("=============================服务流水号:{}, 请求入参:{}", result.first().getString("ORDER_ID"), svcParam);

        setAjax(result);
    }

    //特殊产品参数校验
    public void checkProductParam(String productId,IData elementParam,String operType) throws Exception
    {
        //如果是ADC学护卡产品
        if("10005744".equals(productId)){
            IDataset elementList = elementParam.getDataset("ELEMENT_INFO");
            if(IDataUtil.isNotEmpty(elementList)){
                for(int i =0;i<elementList.size();i++){
                    IData elementMap = elementList.getData(i);
                    if(("0".equals(elementMap.getString("MODIFY_TAG")))&&("32000001".equals(elementMap.getString("ELEMENT_ID")))&&("D".equals(elementMap.getString("ELEMENT_TYPE_CODE")))){
                        IDataset productParamInfo = elementParam.getDataset("PRODUCT_PARAM_INFO",new DatasetList());
                        if(IDataUtil.isEmpty(productParamInfo)) CSViewException.apperr(	CrmCommException.CRM_COMM_103, "家长号码为空,请填写家长号码");
                        String famSn = productParamInfo.getData(0).getDataset("PRODUCT_PARAM").getData(0).getString("ATTR_VALUE");
                        IData famData = new DataMap();
                        famData.put("SERIAL_NUMBER", famSn);

                        boolean flag = false ;
                        IDataset infosDataset = CSViewCall.call(this, "CS.RelaXXTInfoQrySVC.qryMemInfoBySNandUserIdA", famData);
                        if (IDataUtil.isNotEmpty(infosDataset))
                        {
                            IDataset limitDiscnt = StaticUtil.getStaticList("EDC_DISCNT_15_LIMIT");
                            for (int j = 0, jsize = infosDataset.size(); j < jsize; j++)
                            {
                                IData map = infosDataset.getData(j);
                                String disId = map.getString("ELEMENT_ID", "");
                                IDataset filterResult = DataHelper.filter(limitDiscnt, "DATA_ID=" + disId);
                                if (IDataUtil.isNotEmpty(filterResult))
                                {
                                    flag = true;
                                    break;
                                }

                            }
                        }
                        if(!flag)
                            CSViewException.apperr(	CrmCommException.CRM_COMM_103, "要订购学护卡15元包,家长号：【"+ famSn +"】必须先订购和校园10元套餐或者8元套餐 ！");
                    }
                }
                
                for(int j =0;j<elementList.size();j++){
                	IData elementMap = elementList.getData(j);
                	if(("0".equals(elementMap.getString("MODIFY_TAG")))&&("574401".equals(elementMap.getString("ELEMENT_ID")))&&("S".equals(elementMap.getString("ELEMENT_TYPE_CODE")))){
                		break;
                	}
                	
                	String famSn = "";
                	if(IDataUtil.isNotEmpty(elementParam.getDataset("PRODUCT_PARAM_INFO",new DatasetList()))){
                		IDataset productParamInfo = elementParam.getDataset("PRODUCT_PARAM_INFO",new DatasetList());
                		famSn = productParamInfo.getData(0).getDataset("PRODUCT_PARAM").getData(0).getString("ATTR_VALUE");
                	}
                   
                	if(("1".equals(elementMap.getString("MODIFY_TAG")))&&("574401".equals(elementMap.getString("ELEMENT_ID")))&&(StringUtils.isNotBlank(famSn))){
                		CSViewException.apperr(	CrmCommException.CRM_COMM_103, "校星卡服务不能填写家长号码！");
                	}
                }
            }
        }//物联网成员校验
        else if("20005013".equals(productId)||"20005015".equals(productId)||"20161122".equals(productId)||"20161124".equals(productId)||"20171214".equals(productId)){
        	IDataset elementInfo = elementParam.getDataset("ELEMENT_INFO");
        	checkWlwMebElement(elementInfo,operType);
        }
    }

    //物联网成员校验
    public void checkWlwMebElement(IDataset elementInfo,String operType) throws Exception{

		
		boolean discount = false;//true:折扣率低于6折
		int length = elementInfo.size();
		
		for(int i=0;i<length;i++){//服务策略的拦截校验
			IData allSelectedElements = elementInfo.getData(i);
			String elementType = allSelectedElements.getString("ELEMENT_TYPE_CODE");
			String elementId = allSelectedElements.getString("ELEMENT_ID");
			String state = allSelectedElements.getString("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2"))
			{
				String serviceCodeValue = "";
				String operTypeValue = "";
				String serviceBillingTypeValue = "";
				String serviceUsageStateValue = "";
				if(elementId == "99011005" || elementId == "99011012" || elementId == "99011021" ||
					elementId == "99011022" || elementId == "99011028" || elementId == "99011029" ||
					elementId == "99011024" || elementId == "99011025")
				{
					IDataset attrParams = allSelectedElements.getDataset("ATTR_PARAM");
					if(IDataUtil.isNotEmpty(attrParams)){
						int size = attrParams.size();
						for(int j=0;j<size;j++){
							IData oneAttr = attrParams.getData(j);
							String oneAttrCode = oneAttr.getString("ATTR_CODE");
							String oneAttrValue = oneAttr.getString("ATTR_VALUE");
							if(oneAttrCode.equals("ServiceCode")){
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceCodeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode.equals("OperType"))
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									operTypeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode.equals("ServiceBillingType"))
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceBillingTypeValue = oneAttrValue;
								}
							}
							else if(oneAttrCode.equals("ServiceUsageState"))
							{
								if(oneAttrValue != null && oneAttrValue != "")
								{
									serviceUsageStateValue = oneAttrValue;
								}
							}
						}
					}
					
					if(serviceCodeValue != "" || operTypeValue != "" || 
						serviceBillingTypeValue!="" || serviceUsageStateValue !="")
					{
						if(!(serviceCodeValue != "" && operTypeValue != "" 
							&& serviceBillingTypeValue != "" && serviceUsageStateValue !=""))
						{
							CSViewException.apperr(CrmCommException.CRM_COMM_103, "请把该服务" + elementId + "策略的属性值填写完,订购业务唯一代码、操作类型、计费方式、配额状态!");
						}
					}
				}
			}
		}
		
		//先校验对应服务的appname
		for(int i=0;i<length;i++){
			IData allSelectedElements = elementInfo.getData(i);
			String elementType = allSelectedElements.getString("ELEMENT_TYPE_CODE");
			String elementId = allSelectedElements.getString("ELEMENT_ID");
			String state = allSelectedElements.getString("MODIFY_TAG");
			if(elementType == "S" && (state == "0" || state == "2" || state == "exist"))
			{
				if(elementId == "99011022" || elementId ==  "99011028"
					|| elementId == "99011021" || elementId == "99011029")
				{
					IDataset attrParams = allSelectedElements.getDataset("ATTR_PARAM");
					if(IDataUtil.isNotEmpty(attrParams)){
						int size = attrParams.size();
						for(int j=0;j<size;j++){
							IData oneAttr = attrParams.getData(j);
							String oneAttrCode = oneAttr.getString("ATTR_CODE");
							String oneAttrValue = oneAttr.getString("ATTR_VALUE");
							if(oneAttrCode.equals("APNNAME")){
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103, "APNNAME不能为空,请填写!");
								}
							}
						}
					}
					
				}
				//-------add by chenzg@20171210--REQ201712080006NB-IoT业务支撑系统改造方案（全网需求）--begin-------
				else if(elementId == "1218301") {
					IDataset attrParams = allSelectedElements.getDataset("ATTR_PARAM");
					if(IDataUtil.isNotEmpty(attrParams)){
						String modeVal = getAttrVale(attrParams, "LOWPOWERMODE");
						if(modeVal.equals("PSM")){
							String timerVal = getAttrVale(attrParams, "RAUTAUTIMER");
							if(timerVal==null || timerVal==""){
								CSViewException.apperr(CrmCommException.CRM_COMM_103,"RAU/TAU定时器不能为空,请填写!");
							}
							setAttrVale(attrParams, "RAUTAUTIMER", timerVal);
						}else{
							setAttrVale(attrParams, "RAUTAUTIMER", "");
						}
					}
				}
				//-------add by chenzg@20171210--REQ201712080006NB-IoT业务支撑系统改造方案（全网需求）--end-------
			}
		}
		
		//定向专线的apnname校验-重写
		for(int i=0;i<length;i++){
			IData allSelectedElements = elementInfo.getData(i);
			String elementType = allSelectedElements.getString("ELEMENT_TYPE_CODE");
			String elementId = allSelectedElements.getString("ELEMENT_ID");
			String state = allSelectedElements.getString("MODIFY_TAG"); 
			String packageId = allSelectedElements.getString("PACKAGE_ID");
		
			if(elementType == "D" && (state == "0" || state == "2" || state == "exist")) {
				if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"
					|| packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){
					IDataset attrParams = allSelectedElements.getDataset("ATTR_PARAM");
					if(IDataUtil.isNotEmpty(attrParams)){
						int size = attrParams.size();
						for(int j=0;j<size;j++){
							IData oneAttr = attrParams.getData(j);
							String oneAttrCode = oneAttr.getString("ATTR_CODE");
							String oneAttrValue = oneAttr.getString("ATTR_VALUE");//优惠的apnname
							if(oneAttrCode.equals("APNNAME"))
							{//--1--
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103,"APNNAME不能为空,请填写!");
								}
								
								if(packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){//定向优惠包
									boolean breakFlag = false;//标识
									
									//优惠中的appname,在对应的服务中一定要存在
									for(int k=0;k<length;k++){
										IData allSelectedElementSVC = elementInfo.getData(k);
										String elementTypeSVC = allSelectedElementSVC.getString("ELEMENT_TYPE_CODE");
										String elementIdSVC = allSelectedElementSVC.getString("ELEMENT_ID");
										String stateSVC = allSelectedElementSVC.getString("MODIFY_TAG"); 
										if(elementTypeSVC == "S" && (stateSVC == "0" || stateSVC == "2" || stateSVC == "exist"))
										{
											if(elementIdSVC == "99011021" || elementIdSVC == "99011029")
											{
												IDataset attrSvcParams = allSelectedElementSVC.getDataset("ATTR_PARAM");
												if(IDataUtil.isNotEmpty(attrSvcParams)){
													int sizeSVC = attrSvcParams.size();
													for(int h=0;h<sizeSVC;h++){
														IData oneAttrSvc = attrSvcParams.getData(h);
														String oneAttrCodeSvc = oneAttrSvc.getString("ATTR_CODE");
														String oneAttrValueSvc = oneAttrSvc.getString("ATTR_VALUE");
														if(oneAttrCodeSvc.equals("APNNAME")){
															if(oneAttrValueSvc.equals(oneAttrValue)){
																breakFlag = true;//优惠的appname在服务中存在
																break;
															}
														}
														
													}
												}
											}
										}
									}
									if(breakFlag == true){
										break;//退出外循环
									}
									else 
									{
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"定向流量包下的优惠的APPNAME与对应的服务的APPNAME不一样!请核实重新填写!");
									}
								
								}
								else if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"){//通用优惠包
									boolean breakFlag = false;//标识
									
									//优惠中的appname,在对应的服务中一定要存在
									for(int k=0;k<length;k++){
										IData allSelectedElementSVC = elementInfo.getData(k);
										String elementTypeSVC = allSelectedElementSVC.getString("ELEMENT_TYPE_CODE");
										String elementIdSVC = allSelectedElementSVC.getString("ELEMENT_ID");
										String stateSVC = allSelectedElementSVC.getString("MODIFY_TAG"); 
										if(elementTypeSVC == "S" && (stateSVC == "0" || stateSVC == "2" || stateSVC == "exist"))
										{
											if(elementIdSVC == "99011022" || elementIdSVC == "99011028")
											{
												IDataset attrSvcParams = allSelectedElementSVC.getDataset("ATTR_PARAM");
												if(IDataUtil.isNotEmpty(attrSvcParams)){
													int sizeSVC = attrSvcParams.size();
													for(int h=0;h<sizeSVC;h++){
														IData oneAttrSvc = attrSvcParams.getData(h);
														String oneAttrCodeSvc = oneAttrSvc.getString("ATTR_CODE");
														String oneAttrValueSvc = oneAttrSvc.getString("ATTR_VALUE");
														if(oneAttrCodeSvc.equals("APNNAME")){
															if(oneAttrValueSvc.equals(oneAttrValue)){
																breakFlag = true;//优惠的appname在服务中存在
																break;
															}
														}
														
													}
												}
											}
										}
									}
									if(breakFlag == true){
										break;//退出外循环
									}
									else 
									{
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"通用流量包下的优惠的APPNAME与对应的服务的APPNAME不一样!请核实重新填写!");
									}
								}
								
								
							}//--1--
						}
					}
				}
			}
			
		}
		
		for(int i=0;i<length;i++){
			IData allSelectedElements = elementInfo.getData(i);
			String elementType = allSelectedElements.getString("ELEMENT_TYPE_CODE");
			String elementId = allSelectedElements.getString("ELEMENT_ID");
			String state = allSelectedElements.getString("MODIFY_TAG"); 
			String packageId = allSelectedElements.getString("PACKAGE_ID");
		
			if(elementType == "D" && (state == "0" || state == "2" || state == "exist")) {
				if(packageId == "70000005" || packageId == "70000009" || packageId == "70000012"
					|| packageId == "70000008" || packageId == "70000011" || packageId == "70000013"){
					IDataset attrParams = allSelectedElements.getDataset("ATTR_PARAM");
					if(IDataUtil.isNotEmpty(attrParams)){
						int size = attrParams.size();
						for(int j=0;j<size;j++){ //先找折扣率
							IData oneAttr = attrParams.getData(j);
							String oneAttrCode = oneAttr.getString("ATTR_CODE");
							String oneAttrValue = oneAttr.getString("ATTR_VALUE");
							if(oneAttrCode.equals("Discount")){
								discount = false;
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103,"优惠的折扣率不能为空,请填写!");
								} else {
									boolean flag = oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是整数");
									}
									int oneValue = Integer.valueOf(oneAttrValue);
									if(oneValue > 100 || oneValue < 1){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是1到100之间的整数!");
									}
									if(oneValue < 60){
										discount = true;
									}
								}
							}
							//-------add by chenzg@20171211---REQ201711150003关于新增物联卡本省折扣需求---begin---------
							/*本省折扣率校验*/
							else if(oneAttrCode == "20171211"){
								//本省折扣率有值才做校验，没值则不做校验
								if(StringUtils.isNotBlank(oneAttrValue)){
									boolean flag = oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"本省折扣率必须是整数");
									}
									int oneValue = Integer.valueOf(oneAttrValue);
									if(oneValue > 100 || oneValue < 0){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"本省折扣率必须是0到100之间的整数!");
									}
									//当本省折扣内填写折扣后，原有折扣窗口默认100
									setAttrVale(attrParams, "Discount", "100");
									//当本省折扣低于6折时，判断审批工单号是否填写
									if(oneValue<60){
										String audiInfo = getAttrVale(attrParams, "AudiInfo0898");
										if(StringUtils.isBlank(audiInfo)){
											CSViewException.apperr(CrmCommException.CRM_COMM_103,"本省折扣低于6折，请填写审批工单号及名称信息！");
										}
									}
								}
								
							}
							//-------add by chenzg@20171211---REQ201711150003关于新增物联卡本省折扣需求---end-----------
						}
						for(int j=0;j<size;j++){
							IData oneAttr = attrParams.getData(j);
							String oneAttrCode = oneAttr.getString("ATTR_CODE");
							String oneAttrValue = oneAttr.getString("ATTR_VALUE");
							if(oneAttrCode.equals("Discount")){ //折扣率校验
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103,"优惠的折扣率不能为空,请填写!");
								} else {
									boolean flag = oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是整数");
									}
									int oneValue = Integer.valueOf(oneAttrValue);
									if(oneValue > 100 || oneValue < 1){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是1到100之间的整数!");
									}
									if(oneValue < 60){
										discount = true;
									}
								}
							} else if(oneAttrCode == "PromiseUseMonths"){//在网时间校验
								if(oneAttrValue != null && oneAttrValue != ""){
									boolean flag = oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"在网时间必须是整数");
									}
									int values = Integer.valueOf(oneAttrValue);
									if(values < 24){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"在网时间必须是大于等于24的整数!请重新填写");
									}
								} else {
									if(discount == true){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率低于6折!在网时间必须填写");
									}
								}
							} else if(oneAttrCode == "CanShare"){//是否共享校验
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103,"是否可共享不能为空,请选择!");
								}
							} else if(oneAttrCode == "MinimumOfYear"){//年承诺收入（元）
								if(oneAttrValue != null && oneAttrValue != ""){
									boolean flag =  oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"年承诺收入(元)必须是整数!");
									}
									int values = Integer.valueOf(oneAttrValue);
									if(values < 50000){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"年承诺收入(元)必须是大于等于50000!请重新填写");
									}
								} else {
									if(discount == true){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率低于6折!年承诺收入(元)必须填写");
									}
								}
							} else if(oneAttrCode == "BatchAccounts"){//入网用户数(张)
								if(oneAttrValue != null && oneAttrValue != ""){
									boolean flag =  oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"入网用户数(张)必须是整数!");
									}
									int values = Integer.valueOf(oneAttrValue);
									if(values < 50000){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"入网用户数(张)必须是大于等于50000!请重新填写");
									}
								} else {
									if(discount == true){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率低于6折!入网用户数(张)必须填写");
									}
								}
							} else if(oneAttrCode == "ApprovalNum"){//审批文号
								if(oneAttrValue == null || oneAttrValue == ""){
									if(discount == true){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率低于6折!审批文号必须填写");
									}
								}
							}
							
						}
					}
				}
				//-----------add by chenzg@20171220--NB-IOT流量产品包属性校验,41003605----begin-------
				if(packageId == "41003605"){
					IDataset attrParams = allSelectedElements.getDataset("ATTR_PARAM");
					if(IDataUtil.isNotEmpty(attrParams)){
						int size = attrParams.size();
						for(int j=0;j<size;j++){ //先找折扣率
							IData oneAttr = attrParams.getData(j);
							String oneAttrCode = oneAttr.getString("ATTR_CODE");
							String oneAttrValue = oneAttr.getString("ATTR_VALUE");
							if(oneAttrCode.equals("Discount")){
								discount = false;
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103,"优惠的折扣率不能为空,请填写!");
								} else {
									boolean flag =  oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是整数");
									}
									int oneValue = Integer.valueOf(oneAttrValue);
									if(oneValue > 100 || oneValue < 1){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是1到100之间的整数!");
									}
									if(oneValue < 60){
										discount = true;
									}
								}
							}
						}
						for(int j=0;j<size;j++){
							IData oneAttr = attrParams.getData(j);
							String oneAttrCode = oneAttr.getString("ATTR_CODE");
							String oneAttrValue = oneAttr.getString("ATTR_VALUE");
							if(oneAttrCode.equals("APNNAME")){//appname的校验
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103,"APNNAME不能为空,请填写!");
								}
							} else if(oneAttrCode == "Discount"){ //折扣率校验
								if(oneAttrValue == null || oneAttrValue == ""){
									CSViewException.apperr(CrmCommException.CRM_COMM_103,"优惠的折扣率不能为空,请填写!");
								} else {
									boolean flag =  oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是整数");
									}
									int oneValue = Integer.valueOf(oneAttrValue);
									if(oneValue > 100 || oneValue < 1){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率必须是1到100之间的整数!");
									}
									if(oneValue < 60){
										discount = true;
									}
								}
							} else if(oneAttrCode == "PromiseUseMonths"){//在网时间校验
								if(oneAttrValue != null && oneAttrValue != ""){
									boolean flag =  oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"在网时间必须是整数");
									}
									int values = Integer.valueOf(oneAttrValue);
									if(values < 24){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"在网时间必须是大于等于24的整数!请重新填写");
									}
								} else {
									if(discount == true){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率低于6折!在网时间必须填写");
									}
								}
							} else if(oneAttrCode == "CanShare"){//是否共享校验
								oneAttr.put("ATTR_VALUE", "0"); //填0,暂不支持流量共享
							} else if(oneAttrCode.equals("BatchAccounts")){//入网用户数(张)
								if(oneAttrValue != null && oneAttrValue != ""){
									boolean flag =  oneAttrValue.matches("[0-9]+");
									if(!flag){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"入网用户数(张)必须是整数!");
									}
									int values = Integer.valueOf(oneAttrValue);
									if(values < 0){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"入网用户数(张)必须是大于0!请重新填写");
									}
								} else {
									if(discount == true){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率低于6折!入网用户数(张)必须填写");
									}
								}
							} else if(oneAttrCode == "ApprovalNum"){//审批文号
								if(oneAttrValue == null || oneAttrValue == ""){
									if(discount == true){
										CSViewException.apperr(CrmCommException.CRM_COMM_103,"折扣率低于6折!审批文号必须填写");
									}
								}
							}
							
						}
					}
				}
				//-----------add by chenzg@20171220--NB-IOT流量产品包属性校验,41003605----end-------
				
			}
			
			
		}
		
		//---------------add by chenzg@20180712-BUG20180706172625关于物联网NB-IOT短信基础通信服务依赖拦截的优化-begin---------------
		/*校验218302,NB-IOT短信基础通信服务依赖的优惠是否办理，
		 * 短信基础通信服务依赖于NB-IOT短信年包或测试期套餐*/
		if("CrtMb".equals(operType)){
			for(int i=0;i<length;i++){
				IData allSelectedElements = elementInfo.getData(i);
				String elementType = allSelectedElements.getString("ELEMENT_TYPE_CODE");
				String elementId = allSelectedElements.getString("ELEMENT_ID");
				String state = allSelectedElements.getString("MODIFY_TAG"); 
				if(elementType == "S" && elementId == "1218302" && state == "0"){
					boolean selDiscnts = false;
					for(int j=0;j<elementInfo.size();j++){
						IData each = elementInfo.getData(j);
						String eType = each.getString("ELEMENT_TYPE_CODE");
						String eId = each.getString("ELEMENT_ID");
						String eState = each.getString("MODIFY_TAG"); 
						String ePkgId = each.getString("PACKAGE_ID");
						if(eType == "D" && (ePkgId=="41003608" || ePkgId=="41003606") && (eState == "0" || eState == "2" || eState == "exist")) {
							selDiscnts = true;	//选了NB-IOT短信年包或测试期套餐
							break;
						}
					}
					if(selDiscnts == false){
						CSViewException.apperr(CrmCommException.CRM_COMM_103,"短信基础通信服务依赖于NB-IOT短信年包或测试期套餐，请选择！");
					}
				} 
				//NB-IOT数据通信服务依赖于NB-IOT流量年包（A档或B档）或测试期套餐
				else if(elementType == "S" && elementId == "1218301" && state == "0"){
					boolean selDiscnts = false;
					for(int j=0;j<elementInfo.size();j++){
						IData each = elementInfo.getData(j);
						String eType = each.getString("ELEMENT_TYPE_CODE");
						String eId = each.getString("ELEMENT_ID");
						String eState = each.getString("MODIFY_TAG"); 
						String ePkgId = each.getString("PACKAGE_ID");
						if(eType == "D" && (ePkgId=="41003605" || ePkgId=="41003608") && (eState == "0" || eState == "2" || eState == "exist")) {
							selDiscnts = true;	//选了NB-IOT流量年包（A档或B档）或测试期套餐
							break;
						}
					}
					if(selDiscnts == false){
						CSViewException.apperr(CrmCommException.CRM_COMM_103,"数据通信服务依赖于流量年包（A档或B档）或测试期套餐，请选择！");

					}
				}
			}
		}		
		//---------------add by chenzg@20180712-BUG20180706172625关于物联网NB-IOT短信基础通信服务依赖拦截的优化-end-----------------
    }
    
    /**
     * 取属性值
     * @param attrParams
     * @param attrCode
     * @return
     */
    public String getAttrVale(IDataset attrParams, String attrCode){
    	String attrVal = "";
    	for(int i=0;i<attrParams.size();i++){
    		IData param = attrParams.getData(i);
    		String oneAttrCode = param.getString("ATTR_CODE");
    		String oneAttrValue = param.getString("ATTR_VALUE");
    		if(oneAttrCode.equals(attrCode)){
    			attrVal = oneAttrValue;
    			break;
    		}
    	}
    	
    	return attrVal;
    }
    
    /**
     * 修改属性值
     * @param attrParams
     * @param attrCode
     * @param attrVal
     * @return
     */
    public void setAttrVale(IDataset attrParams,String attrCode,String attrVal){
    	for(int i=0;i<attrParams.size();i++){
    		IData param = attrParams.getData(i);
    		String oneAttrCode = param.getString("ATTR_CODE");
    		String oneAttrValue = param.getString("ATTR_VALUE");
    		if(oneAttrCode.equals(attrCode)){
    			param.put("ATTR_VALUE",attrVal);
    		}
    	}
    }
    
    /**
     * 批量业务设置金库参数
     * @throws Exception
     */
/*    private IData setCashBoxParam(String ecOfferId, String batOperType) throws Exception
    {
        String rightCode = CashBoxConsts.RIGHTCODE_EC_BAT_ADD_MEMBER;
        String totalSwitch = CashBoxConsts.CASHBOX_SWITCH_OFF;
        boolean isNeedCashBox = false;
        IData cashBoxData = EcParamViewUtil.queryCfgProdAttrItemByIdIdTypeObjCode(ecOfferId, "P", "0", "GoldAudit");
        if(DataUtils.isNotEmpty(cashBoxData))
        {
            if(cashBoxData.getString("VALUE").contains(batOperType))
            {
                isNeedCashBox = true;
                
                //查询金库总开关
                IData totalSwitchData = StaticParamUtil.getStaticData(CashBoxConsts.CASHBOX_TOTAL_SWITCH, CashBoxConsts.CASHBOX_TOTAL_SWITCH);
                if(DataUtils.isNotEmpty(totalSwitchData) && CashBoxConsts.CASHBOX_SWITCH_STATE_ON.equals(totalSwitchData.getString("STATE")))
                {
                    totalSwitch = CashBoxConsts.CASHBOX_SWITCH_ON;
                }
                
                if(CashBoxConsts.CASHBOX_SWITCH_ON.equals(totalSwitch))
                {
                    //查询功能开关
                    IData funcSwitchData = StaticParamUtil.getStaticData(CashBoxConsts.CASHBOX_FUNCTION_SWITCH, rightCode);
                    if(DataUtils.isEmpty(funcSwitchData) || !CashBoxConsts.CASHBOX_SWITCH_STATE_ON.equals(funcSwitchData.getString("STATE")))
                    {//没有查到功能开关或者开关关闭
                        totalSwitch = CashBoxConsts.CASHBOX_SWITCH_OFF;
                    }
                }
            }
        }
        IData info = new DataMap();
        info.put("CASHBOX_SWITCH", totalSwitch);
        info.put("RIGHT_CODE", rightCode);
        info.put("NEED_CASHBOX", isNeedCashBox);
        return info;
    }*/

/*    private IData queryInsOfferData(String subscriberInsId, String offerId, String ecSubInsId) throws Exception
    {
        //查询成员订购的商品实例
        IData insOffer = EcOfferViewUtil.queryMebOfferInsBySubInsIdOfferIdEcSubInsId(subscriberInsId, offerId, ecSubInsId,TableSplitConsts.PRODUCT);
        if (DataUtils.isEmpty(insOffer))
        {
            BizException.bizerr(EnterpriseException.CRM_EC_8, subscriberInsId, offerId);
        }
        return insOffer;
    }*/

//    private IDataset queryInsBundleOfferDataset(String offerInsId, String subscriberInsId, String mainOfferId) throws Exception
//    {
//        IDataset insOptOfferDataset = EcOfferViewUtil.queryBundleOfferInstanceByOfferInsIdRoleId(offerInsId, EcConstants.OFFER_COM_REL_ROLE_ID_SUBOFFER, subscriberInsId);
//        if (DataUtils.isNotEmpty(insOptOfferDataset))
//        {
//            for (int i = 0, size = insOptOfferDataset.size(); i < size; i++)
//            {
//                IData insOptOfferData = insOptOfferDataset.getData(i);
//                insOptOfferData.put("MAIN_OFFER_ID", mainOfferId);
////                insOptOfferData.put("SELECT_FLAG", getSelectFlag(mainOfferId, insOptOfferData.getString("OFFER_ID")));
//            }
//        }
//        return insOptOfferDataset;
//    }

  

    /**
     * @Title: getBbossMerchPageConfig
     * @Description: 取得bboss产品配置
     * @param @param cycle
     * @param @throws Exception
     * @return void
     * @throws
     */
    public void getBbossMerchPageConfig(IRequestCycle cycle) throws Exception
    {
    	/*String offerId = getData().getString("OFFER_ID", "0");//成员商品id
    	
        //IData relinfoData=EcUpcViewUtil.queryOfferJoinRelBy2OfferIdRelType(null, offerId, "1");//集团商品id
    	IData relinfoData = new DataMap();
    	IDataset relinfoDataset=IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType (null,offerId,"1");
    	if(DataUtils.isNotEmpty(relinfoDataset)){
    		relinfoData = relinfoDataset.first();
    	}
    	String ecOfferId=relinfoData.getString("OFFER_ID");
        IData param = new DataMap();
        param.put("hasMerchPage", "false");
        
        //IData data = EcParamViewUtil.queryCfgProdAttrItemByIdIdTypeObjCode(ecOfferId, "P", "BbossMerchPage", "-1");
        String value = CommonViewCall.getAttrValueFromAttrBiz(this,ecOfferId, "P", "BbossMerchPage", "-1");
        if (StringUtils.isNotBlank(value))
        {
            param.put("hasMerchPage", "true");
        }*/
        IData param = new DataMap();
        param.put("hasMerchPage", "false");
        setAjax(param);
    }

    /**
     * 查询成员指定Bboss商品下的子商品
     *
     * @param cycle
     * @throws Exception
     */
    public void initBBossMebOpenedSubOffers(IRequestCycle cycle) throws Exception
    {
        String ecSubInsId = this.getData().getString("EC_USER_ID");
        String memSubInsId = this.getData().getString("MEM_USER_ID");
        String memEparchyCode = this.getData().getString("MEM_EPARCHY_CODE");
        IData input = new DataMap();     
        //根据BBOSS商品ID获取BBOSS产品
        IDataset merchPOfferList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(this.getData().getString("OFFER_ID"),null,"4");       
        //根据BBOSS产品获取BBOSS成员产品
		IDataset memProOfferList = IUpcViewCall.queryOfferJoinRelBy2OfferIdRelType(merchPOfferList.first().getString("OFFER_ID"),null,"1");

        input.put("USER_ID_A", ecSubInsId);
        input.put("ROLE_CODE_B", "0");
        input.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);
        input.put("RELATION_TYPE_CODE", IUpcViewCall.getRelationTypeCodeByOfferId(memProOfferList.first().getString("OFFER_ID")));       
        IDataset subOffersInfos = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.qryRelaBBInfoByRoleCodeBForGrp", input);
        IDataset subOffers = new DatasetList();
        if (DataUtils.isNotEmpty(subOffersInfos))
        {
            for (int s = 0, ssize = subOffersInfos.size(); s < ssize; s++)
            {
                String userIdb = subOffersInfos.getData(s).getString("USER_ID_B");

                input.clear();
                input.put("USER_ID_A", userIdb);
                input.put("USER_ID_B", memSubInsId);
                input.put(Route.ROUTE_EPARCHY_CODE, memEparchyCode);

                IDataset uu = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.qryBBInfoByUserIdAB", input);
                if(DataUtils.isEmpty(uu)){
                    continue;
                }
                // 获取子商品服务号码
                IData subData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this,userIdb);
                if (DataUtils.isEmpty(subData))
                {
                    continue;
                }
                //获取集团产品配置
                IData offer = UpcViewCall.queryOfferByOfferId(this, "P", subData.getString("PRODUCT_ID"), "Y");
                if(DataUtils.isEmpty(offer))
                {
                    continue;
                }

                IData subInfo = new DataMap();
                subInfo.put("USER_ID", userIdb);
                subInfo.put("SERIAL_NUMBER", subData.getString("SERIAL_NUMBER"));
                subInfo.put("OFFER_ID", offer.getString("OFFER_ID"));
                subInfo.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                subInfo.put("OFFER_TYPE", offer.getString("OFFER_TYPE"));
                subInfo.put("OFFER_CODE", offer.getString("OFFER_CODE"));

                subOffers.add(subInfo);

            }
        }
        this.setAjax(subOffers);
    }

    /**
     * 查询客户已订购集团BBOSS子商品
     *
     * @param offer
     * @throws Exception
     */
    public void initBBossOpenedSubOffers(IRequestCycle cycle) throws Exception
    {
        String subInsId = this.getData().getString("USER_ID");
        String mebSubInsId = this.getData().getString("MEB_USER_ID");
        IDataset subOffers = new DatasetList();

        IData input = new DataMap();
        input.put("USER_ID_A", subInsId);
        input.put("ROLE_CODE_B", "0");
        input.put("RELATION_TYPE_CODE", IUpcViewCall.getRelationTypeCodeByOfferId(this.getData().getString("OFFER_ID")));
        IDataset subOffersInfos = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.qryRelaBBInfoByRoleCodeBForGrp", input);

        if (DataUtils.isNotEmpty(subOffersInfos))
        {
            for (int s = 0, ssize = subOffersInfos.size(); s < ssize; s++)
            {
                String userIdb = subOffersInfos.getData(s).getString("USER_ID_B");

            	IData param = new DataMap();
            	param.put("USER_ID_A", userIdb);
            	param.put("RELATION_TYPE_CODE", "81");
            	param.put("USER_ID_B", mebSubInsId);
            	param.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getLoginEparchyCode());

            	IDataset mebBBbOffers = CSViewCall.call(this, "CS.RelaBBInfoQrySVC.qryBBInfoByUserIdAB", param);
                if (DataUtils.isNotEmpty(mebBBbOffers))
                {
                    continue;
                }
                
                // 获取子商品服务号码
                IData subData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this,userIdb);
                if (DataUtils.isEmpty(subData))
                {
                    continue;
                }
                //获取集团产品配置
                IData offer = UpcViewCall.queryOfferByOfferId(this, "P", subData.getString("PRODUCT_ID"), "Y");
                if(DataUtils.isEmpty(offer))
                {
                    continue;
                }
                String mebOfferId = IUpcViewCall.queryMemOfferIdByOfferId(offer.getString("OFFER_ID"));
                //不存在成员产品
                if (StringUtils.isBlank(mebOfferId))
                {//集团子商品如果没有对应的成员子商品，则不予展示
                    continue;
                }
                IData subInfo = new DataMap();
                subInfo.put("USER_ID", userIdb);
                subInfo.put("SERIAL_NUMBER", subData.getString("SERIAL_NUMBER"));
                subInfo.put("OFFER_ID", offer.getString("OFFER_ID"));
                subInfo.put("OFFER_NAME", offer.getString("OFFER_NAME"));
                subInfo.put("OFFER_CODE", offer.getString("OFFER_CODE"));
                subOffers.add(subInfo);
            }
        }
        this.setAjax(subOffers);
    }
    private IDataset comsisData(IDataset source) throws Exception
    {

        IDataset payTypeSet = new DatasetList();
        for (int i = 0; i < source.size(); i++)
        {
            IData tmp = source.getData(i);
            String payTypeCode = tmp.getString("PLAN_TYPE_CODE", "");
            String payTypeName = tmp.getString("PLAN_NAME", "");

            boolean found = false;
            for (int j = 0; j < payTypeSet.size(); j++)
            {
                IData data = payTypeSet.getData(j);
                if (data.getString("PAY_TYPE_CODE").equals(payTypeCode))
                {
                    found = true;
                    break;
                }
            }
            if (!found)
            {
                IData map = new DataMap();
                map.put("PAY_TYPE_CODE", payTypeCode);
                map.put("PAY_TYPE", payTypeName);
                payTypeSet.add(map);
            }
        }
        return payTypeSet;
    }

    /**
     * 获取成员角色列表
     * @param offerId
     * @param ecSubscriberInsId
     * @return
     * @throws Exception
     */
    private IDataset queryRoleList(String relationTypeCode, String offerId) throws Exception
    {
        IData param = new DataMap();
        param.put("RELATION_TYPE_CODE", relationTypeCode);
        IDataset roleList = CSViewCall.call(this, "CS.StaticInfoQrySVC.getRoleCodeList", param);

        if(IDataUtil.isNotEmpty(roleList))
        {
            if ("110000006100".equals(offerId))
            {
                IDataset filterList = DataHelper.filter(roleList, "ROLE_CODE_B=1");
                filterList.addAll(DataHelper.filter(roleList, "ROLE_CODE_B=8"));
                roleList.clear();
                roleList.addAll(filterList);
            }
            else if ("110000009048".equals(offerId))
            {
                roleList.clear();
                IData roleData = new DataMap();
                roleData.put("ROLE_CODE_B", "2");
                roleData.put("ROLE_B", "商户管家终端");
                roleList.add(roleData);
            }
        }
        return roleList;
    }


    private String createFuncNaviCookie(IRequestCycle cycle) throws Exception
    {
        CookieUtil cookie = new CookieUtil(getRequest(),getResponse(), "CRM_ECNAVIGATION_COOKIE", 24 * 7);
        String showFuncNavigation = "1";
        if( cookie.load() )
        {
            showFuncNavigation = cookie.get("FUNC_NAVIGATION_MEMBER_OFFER");
        }

        if(StringUtils.isEmpty(showFuncNavigation))
        {
            showFuncNavigation = "1";
        }
        cookie.put("FUNC_NAVIGATION_MEMBER_OFFER", showFuncNavigation);
        cookie.store();

        return showFuncNavigation;
    }

    public IData queryMebProduct(String userId, String userIdA, String offerCode, String productMode, String eparchyCode) throws Exception
    {
        IData mebProduct = new DataMap();
        IData data = new DataMap();
        data.put("USER_ID", userId);
        data.put("USER_ID_A", userIdA);
        data.put("PRODUCT_ID", offerCode);
        data.put("PRODUCT_MODE", productMode);
        data.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset dataset = CSViewCall.call(this, "CS.UserProductInfoQrySVC.GetUserProductInfo", data);
        if(IDataUtil.isNotEmpty(dataset))
        {
            mebProduct = dataset.first();
        }
        return mebProduct;
    }
    
    
    /**
     * 查询稽核信息
     *
     * @param cycle
     * @throws Exception
     */
    public void queryAuditInfo(IRequestCycle cycle) throws Exception
    {
        String staffId = this.getData().getString("STAFF_ID", "");
        String staffName = this.getData().getString("STAFF_NAME", "");
        
        String myStaffId = this.getVisit().getStaffId();
        
        IDataset staffs = CommonViewCall.qryAuditInfo(staffId, staffName);
      //过滤掉自己
        for(int i=0;i<staffs.size();i++){
        	IData each = staffs.getData(i);
        	if(myStaffId.equals(each.getString("STAFF_ID", ""))){
        		staffs.remove(i);
        		i--;
        	}
        }
        setAuditInfoList(staffs);
    }

    public abstract void setInitOffer(IData initOffer);

    public abstract void setInfo(IData info);

    public abstract void setServiceOfferList(IDataset serviceOfferList);

    public abstract void setPriceOfferList(IDataset priceOfferList);

    public abstract void setCompOffers(IDataset compOffers);

    public abstract void setInAttr(IData inAttr);

    public abstract void setBusi(IData busi);
    
    public abstract void setAuditInfoList(IDataset auditList);

    
    public void queryTwoCheck(IRequestCycle cycle) throws Throwable
    {
        IData twoCheck = new DataMap();
        twoCheck.put("TWOCHECK", "0");
        boolean twoCHECK = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "TWOCHECK");
        if (twoCHECK) {
        	twoCheck.put("TWOCHECK", "1");
		}
    	setAjax(twoCheck);
    }
}