package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.enterprise.bbosscommon.merchPage;

import java.util.List;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.BizTempComponent;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.iorder.web.igroup.bboss.common.EcBbossCommonViewUtil;
import com.asiainfo.veris.crm.iorder.web.igroup.util.CommonViewCall;
import com.asiainfo.veris.crm.iorder.web.igroup.util.IUpcViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;

public abstract class BBossOfferParam extends BizTempComponent
{
    private static final Logger logger = LoggerFactory.getLogger(BBossOfferParam.class);
    
    @Override
    public void renderComponent(StringBuilder informalParametersBuilder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {

        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {	
            includeScript(writer, "iorder/igroup/bboss/scripts/BBossOfferParam.js", false, false);
        }
        else
        {
            getPage().addResAfterBodyBegin("iorder/igroup/bboss/scripts/BBossOfferParam.js", false, false);
        }
        
        initPage(cycle);
    }

    public void initPage(IRequestCycle cycle) throws Exception
    {
        String offerId = getPage().getData().getString("OFFER_ID");
        String operCode = getPage().getData().getString("OPER_TYPE");
        Boolean isFeedBack= getPage().getData().getBoolean("isFeedBack",false);//预受理归档标记
        
        if (StringUtils.isEmpty(offerId) || StringUtils.isEmpty(operCode))
        {
            return ;
        }
//        String brand = EcUpcViewUtil.queryBrandByOfferId(offerID);
        IData offerInfo = IUpcViewCall.queryOfferByOfferId(offerId,"Y");
        String offerCode = offerInfo.getString("OFFER_CODE");
        if(StringUtils.isEmpty(offerCode)){
            return;
        }
        if(!"BOSG".equals(offerInfo.getString("BRAND_CODE")))
        {
            return ;
        }

        
        IData info = new DataMap();
        info.put("isFeedBack", isFeedBack);
        // 首先捞取特殊配置
//        setSpecialConfig(offerID,"PRO", info);
        
        if (BizCtrlType.ChangeMemberDis.equals(operCode))
        {
            String subscriberId = getPage().getData().getString("SUB_INST_ID");
            String ecSubscriberInsId = getPage().getData().getString("EC_SUBSCRIBER_INS_ID");

            initMebChg(offerCode,subscriberId,ecSubscriberInsId, info);
            info.put("OPER_CODE", operCode);
            setCompInfo(info);
            return ;
        }
        if (BizCtrlType.CreateMember.equals(operCode))
        {
            initMebCrt(offerCode, info);
            info.put("OPER_CODE", operCode);
            
            String isBatch=getPage().getData().getString("IS_BATCH");
            if("true".equals(isBatch)){
            	initbat(info);
            }
            
            setCompInfo(info);
            return ;
        }
        init(info, offerCode, operCode);
        if ("preCrt".equals(operCode))
        {
            preCrt(offerCode, info);
        }
        
        if (BizCtrlType.CreateUser.equals(operCode))
        {
            initCrt(offerCode, info);
        }
        else if (BizCtrlType.ChangeUserDis.equals(operCode))
        {
            String subscriberId = getPage().getData().getString("SUB_INST_ID");
            initChg(offerCode, subscriberId, info);
        }
        else 
        {
            initDst(offerCode, info);
        }
        info.put("OPER_CODE", operCode);
        
        //如果为esop初始化数据
        boolean isEsop=getPage().getData().getBoolean("IS_ESOP");
        if(isEsop){
          String  prodsString=getPage().getData().getString("PRODS");
          IDataset prods=new DatasetList(prodsString);
          initEsop(prods,info,offerCode);
        }
        setCompInfo(info);
    }
    /**
     * 初始化批量
     * @param info
     * @throws Exception
     */
    private void initbat(IData info) throws Exception
    {

        // 1、取得成员新增操作类型
        IDataset mebOperTypes = new DatasetList();
        IData mebOperType = new DataMap();
        mebOperType.put("OPER_NAME", "批量处理");
        mebOperType.put("OPER_VALUE", "1");
        mebOperTypes.add(mebOperType);
        
        // 2、取得成员类型
        IDataset mebTypeSet = new DatasetList();
        IData temp = new DataMap();
        temp.put("VALUE", "1");
        temp.put("NAME", "签约关系");
        mebTypeSet.add(temp);
        info.put("MEB_TYPE_LIST", mebTypeSet);
        info.put("OFFER_OPER_CODE_SEL_LIST", mebOperTypes);
    
    }
    private void initEsop(IDataset prods,IData info,String offerId) throws Exception
    {
       IDataset prodChaSpecs=prods.getData(0).getDataset("PROD_CHA_SPECS");
       if(IDataUtil.isEmpty(prodChaSpecs)){
    	   return ;
       }
       for(int i=0,sizeI=prodChaSpecs.size();i<sizeI;i++){
    	    IData spec=prodChaSpecs.getData(i);
    	    String chaSpecCode=spec.getString("CHA_SPEC_CODE");
    	    String chaValue=spec.getString("CHA_VALUE");
    	    if("PAY_MODE".equals(chaSpecCode)){
    	    	   IData payMode=new  DataMap();
   	    	       payMode.put("VALUE", chaValue);
   	    	       IDataset result =getEsoPayModes(offerId, chaValue);
    	           if (DataUtils.isNotEmpty(result))
    	           {
    	               String name=result.getData(0).getString("NAME");
    	               payMode.put("NAME", name);
    	           }
    	    	  IDataset disCntEffect =new DatasetList();
    	    	  disCntEffect.add(payMode);
    	          info.put("DISCNT_EFFECT_SEL_LIST", disCntEffect);
    	          info.put("DISCNT_EFFECT_VALUE", chaValue);
    	    }
    	    if("BUS_NEED_DEGREE".equals(chaSpecCode)){
    	    	 if (StringUtils.isEmpty(chaValue))
                 {
                     List<Object> busNeedDegree = StaticUtil.getStaticList("BUS_NEED_DEGREE");
                     info.put("bboss_BUS_NEED_DEGREE", busNeedDegree);
                     info.put("BUS_NEED_DEGREE_VALUE", "");
                 }
                 else
                 {
                     IData bus_need_degree = new DataMap();
                     bus_need_degree.put("CODE_VALUE", chaValue);
                     String map = StaticUtil.getStaticValue("BUS_NEED_DEGREE", chaValue);
                     if (StringUtils.isNotEmpty(map))
                     {
                         bus_need_degree.put("CODE_NAME", map);
                     }
                     IDataset bus_need_degrees = new DatasetList();
                     bus_need_degrees.add(bus_need_degree);
                     info.put("bboss_BUS_NEED_DEGREE", bus_need_degrees);
                     info.put("BUS_NEED_DEGREE_VALUE", chaValue);
                 }
    	    }
    	    if("OPERTYPE".equals(chaSpecCode)){
    	    	  IData operType=new  DataMap();
    	    	  operType.put("OPER_VALUE", chaValue);
    	    	  if(EcConstants.MERCH_STATUS.MERCH_ADD.getValue().equals(chaValue)&&hasPreOption(offerId)){
    	    		    info.put("IS_PRE", "true");
    	    	        operType.put("OPER_NAME", "商品预受理");
    	    	        
    	    	  }else if(EcConstants.MERCH_STATUS.MERCH_ADD.getValue().equals(chaValue)){
    	    		  info.put("IS_PRE", "false");
  	    	          operType.put("OPER_NAME", "商品受理");
    	    	  }else{
    	    		  operType.put("OPER_NAME", EcBbossCommonViewUtil.getOperNameByOperType(chaValue));
    	    	  }
    	    	  IDataset operTypes =new DatasetList();
    	    	  operTypes.add(operType);
    	          info.put("OFFER_OPER_CODE_SEL_LIST", operTypes);
    	          info.put("OFFER_OPER_CODE_VALUE", chaValue);
    	    }
    	    if("BIZ_MODE".equals(chaSpecCode)){
  	    	  IData bizMode=new  DataMap();
  	    	  bizMode.put("BIZ_MODE", chaValue);
  	    	  String modeName = StaticUtil.getStaticValue("BBOSS_BIZ_MODE",chaValue);
  	    	  bizMode.put("MODE_NAME", modeName);
  	    	  IDataset bizModeS =new DatasetList();
  	    	  bizModeS.add( bizMode);
  	          info.put("BIZ_MODE_SEL_LIST", bizModeS);
  	          info.put("BIZ_MODE_VALUE", chaValue);
  	    }
    	    
       }
       
    }
    /**
     * @throws Exception  
    * @Title: setSpecialConfig 
    * @Description: 根据特殊配置，来设置初始化页面的元素和必选性
    * @param offerID
    * @param operCode  
    * @return void    
    * @author chenkh
    * @throws 
    */
    private void setSpecialConfig(String offerID, String operCode, IData info) throws Exception
    {

        // 1、捞取配置
        // 1.1 产品有测试资费的时候不需要必填合同。后台再重新校验合同配置。
        IDataset testDisConfig = CommonViewCall.getAttrsFromAttrBiz(this,"1", "B", operCode, offerID);
        String isTestDis = "";
  
        if (IDataUtil.isNotEmpty(testDisConfig))
        {
            isTestDis = "1";
        }

        // 2、设置属性，用户初始化页面时候判断
        IData specialProperties = new DataMap();
        specialProperties.put("TEST_DIS", isTestDis);

        // 3、放入info信息集中
        info.put("SPEC_INFO", specialProperties);
    }

    private void initMebCrt(String offerID, IData info) throws Exception
    {
        // 1、取得成员新增操作类型
        IDataset mebOperTypes = new DatasetList();
        IData mebOperType = new DataMap();
        mebOperType.put("OPER_NAME", "新增");
        mebOperType.put("OPER_VALUE", "1");
        mebOperTypes.add(mebOperType);
        
        // 2、取得成员类型
        MebPageInfo mebPageInfo = new MebPageInfo(offerID).invoke();
        String mebType = mebPageInfo.getMebType();
        IDataset mebTypeSet = mebPageInfo.getMebTypeSet();
        
        info.put("MEB_TYPE", mebType);
        info.put("MEB_TYPE_LIST", mebTypeSet);
        info.put("OPER_VALUE", "1");

        info.put("OFFER_OPER_CODE_SEL_LIST", mebOperTypes);
    }

    private void initMebChg(String offerId,String subscriberInsId,String relSubscriberInsId, IData info) throws Exception
    {
        // 1- 从实例表中取得OPERTYPE  TODO 变更需要查inst_id
        if (StringUtils.isNotEmpty(subscriberInsId))
        {
            IData input = new DataMap();
            input.put("USER_ID", subscriberInsId);
            input.put("GRP_USER_ID", relSubscriberInsId);

            IDataset retData = CSViewCall.call(this,"CS.UserGrpMerchInfoQrySVC.getSEL_BY_USERID_USERIDA", input);
            if (IDataUtil.isNotEmpty(retData))
            {
                IData merchInfo = retData.getData(0);
                String merchStatus = merchInfo.getString("STATUS");
                info.put("OFFER_OPER_CODE_SEL_LIST", EcBbossCommonViewUtil.getMebOperCode(this,offerId, merchStatus));
            }
        }
        // 否则放一个空dataset
        else 
        {
            info.put("OFFER_OPER_CODE_SEL_LIST", new DatasetList());
        }
        
        // 2、取得成员类型
        MebPageInfo mebPageInfo = new MebPageInfo(offerId).invoke();
        String mebType = mebPageInfo.getMebType();
        IDataset mebTypeSet = mebPageInfo.getMebTypeSet();
        
        info.put("MEB_TYPE", mebType);
        info.put("MEB_TYPE_LIST", mebTypeSet);
    }

    private void initDst(String offerID, IData info)
    {
        // TODO 注销逻辑
        
    }

    private void initChg(String offerCode, String subscriberId, IData info) throws Exception
    {
        // 1- 从实例表中取得OPERTYPE
        if (StringUtils.isNotEmpty(subscriberId))
        {
            IData input = new DataMap();
            input.put("USER_ID", subscriberId);
            IDataset retData = CSViewCall.call(this,"CS.UserGrpMerchInfoQrySVC.qryMerchInfoByUserIdMerchSpecStatus", input);
            if (IDataUtil.isNotEmpty(retData))
            {
                IData merchInfo = retData.getData(0);
                String merchStatus = merchInfo.getString("STATUS");
                IDataset operList = EcBbossCommonViewUtil.initOperTypesChg(this,merchStatus, offerCode);//EcBbossCommonViewUtil.queryMerchOperType(this,offerCode, merchStatus);
                info.put("OFFER_OPER_CODE_SEL_LIST", operList);
                if (operList !=null && operList.size()==1)
                {
                	if(operList.getData(0).getString("OPER_TYPE")!=null){
                        info.put("OPER_VALUE", operList.getData(0).getString("OPER_TYPE"));
                	}else{
                        info.put("OPER_VALUE", operList.getData(0).getString("OPER_VALUE"));

                	}
                }
            }

            // 2- 从other表中将集团的合同信息取出来展示在前台   
            input.put("RSRV_VALUE_CODE","ATT_INFOS");
            IDataset contractInfos = null;//CSViewCall.call(this,"OC.enterprise.IUmOtherInfoQuerySV.queryContractInfo", input);
            if (IDataUtil.isNotEmpty(contractInfos))
            {
                IData OtherInfo = contractInfos.getData(0);
                IData contractInfo = new DataMap();
                contractInfo.put("ATT_TYPE_CODE",OtherInfo.getString("RSRV_STR2"));
                contractInfo.put("ATT_CODE",OtherInfo.getString("RSRV_STR3"));
                contractInfo.put("CONT_NAME",OtherInfo.getString("RSRV_STR4"));
                contractInfo.put("ATT_NAME",OtherInfo.getString("RSRV_STR5"));
                contractInfo.put("START_TIME",OtherInfo.getString("RSRV_STR6"));
                contractInfo.put("END_TIME",OtherInfo.getString("RSRV_STR7"));
                contractInfo.put("RECONT_TIME",OtherInfo.getString("RSRV_STR9"));
                contractInfo.put("CONT_FEE",OtherInfo.getString("RSRV_STR10"));
                contractInfo.put("PERFER_PLAN",OtherInfo.getString("RSRV_STR11"));
                contractInfo.put("AUTO_RECONT",OtherInfo.getString("RSRV_STR8"));
                contractInfo.put("IS_RECONT",OtherInfo.getString("RSRV_STR13"));
                contractInfo.put("AUTO_RECONT_CYC",OtherInfo.getString("RSRV_STR12"));
                setAddAttInfo(contractInfo);
            }
        }
        // 否则放一个空dataset
        else
        {
            info.put("OFFER_OPER_CODE_SEL_LIST", new DatasetList());
        }
    }

    protected void initCrt(String offerCode, IData info) throws Exception
    {
        IData oper = new DataMap();
        oper.put("OPER_NAME", "商品受理");
        oper.put("OPER_VALUE", EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue());
        info.put("OPER_VALUE", EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue());

        IDataset opers = new DatasetList();
        
        if (hasPreOption(offerCode))
        {
            info.put("IS_PRE", "true");
            oper.put("OPER_NAME", "商品预受理");
            oper.put("OPER_VALUE", EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue());
            info.put("OPER_VALUE", EcConstants.MERCH_PRODUCT_STATUS.PRODUCT_PREDEAL.getValue());
        }
        else 
        {
            info.put("IS_PRE", "false");
        }
        opers.add(oper);
        info.put("OFFER_OPER_CODE_SEL_LIST", opers);
    }
    
    protected void preCrt(String offerId, IData info) throws Exception
    {
        IData oper = new DataMap();
        oper.put("OPER_NAME", "商品受理");
        oper.put("OPER_VALUE", "1");
        IDataset opers = new DatasetList();
        opers.add(oper);
        
        info.put("IS_PRE", "false");
        
        info.put("OFFER_OPER_CODE_SEL_LIST", opers);
    }

    /** 
    * @Title: hasPreOption 
    * @Description: 判断商品下是否有产品预受理
    * @param offerId
    * @return
    * @throws Exception  
    * @return boolean    
    * @author chenkh
    * @throws 
    */
    private boolean hasPreOption(String offerId) throws Exception
    {
        // 根据商品编号获取产品信息
        IData inparams = new DataMap();
        inparams.put("PRODUCT_ID", offerId);
        inparams.put("RELATION_TYPE_CODE", "4");
        IDataset ds = CSViewCall.call(this, "CS.ProductInfoQrySVC.queryProductByComp", inparams);
        String offerCode = ds.getData(0).getString("PRODUCT_ID_B"); 
        IDataset preData = CommonViewCall.getAttrsFromAttrBiz(this,"1", "B", "AHEAD", offerCode);
        if (IDataUtil.isEmpty(preData))
        {
            return false;
        }
        return true;
    }

    protected void init(IData info, String offerId, String operCode) throws Exception
    {
        // 1-初始化合同附件
        List<Object> attType = StaticUtil.getStaticList("POATT_TYPE");
        info.put("ATT_TYPE_SEL_LIST", attType);
        //合同附件新增需求
        //1-2 合同附件是否自动续约
        List<Object> IsAutoRecont = StaticUtil.getStaticList("IsAutoRecont");
        info.put("IsAutoRecont_SEL_LIST", IsAutoRecont);
        
        List<Object> IsRecont = StaticUtil.getStaticList("IsRecont");
        info.put("IsRecont_SEL_LIST", IsRecont);
        
        List<Object> AutoRecontCyc = StaticUtil.getStaticList("AutoRecontCyc");
        info.put("AutoRecontCyc_SEL_LIST", AutoRecontCyc);
        
        // 2-初始联系人
        List<Object> conType = StaticUtil.getStaticList("CONTACTOR_TYPE");
        info.put("CONTACT_SEL_LIST", conType);

        // 3-初始化bboss_BUS_NEED_DEGREE
        List<Object> busNeedDegree = StaticUtil.getStaticList("BUS_NEED_DEGREE");
        info.put("bboss_BUS_NEED_DEGREE", busNeedDegree);

        // 4-初始化bboss_DISCNT_EFFECT
        IDataset payResult = new DatasetList();
        if(BizCtrlType.ChangeUserDis.equals(operCode)){
        	payResult = getPayModes(offerId,"init", "ChgUs");
        	
        }else{            
        	payResult=getPayModes(offerId,"init", "CrtUs") ;        	
        }

        if (DataUtils.isNotEmpty(payResult))
        {
            info.put("DISCNT_EFFECT_SEL_LIST", payResult);
            info.put("DISCNT_EFFECT_VALUE", payResult.getData(0).getString("ATTR_VALUE"));
        }

        // 5-初始化 bboss_BIZ_MODE
        IDataset bizModeList = new DatasetList();
        String bizModes = AttrBizInfoIntfViewUtil.qryBBossBizCodeByGrpProductId(this, offerId);

        //if (DataUtils.isNotEmpty(result))
        //{

            if (StringUtils.isBlank(bizModes))
            {
                // 如果没有配置业务开展模式，默认为 5 ，本省受理，本省支付
                IData tmp = new DataMap();
                tmp.put("BIZ_MODE", "5");
                tmp.put("MODE_NAME", "本省受理，本省支付");

                bizModeList.add(tmp);
            }
            else
            {
                String[] payList = bizModes.split(",");
                for (int i = 0; i < payList.length; i++)
                {
                    IData tmp = new DataMap();
                    tmp.put("BIZ_MODE", payList[i]);
                    String modeName = StaticUtil.getStaticValue("BBOSS_BIZ_MODE", payList[i]);
                    tmp.put("MODE_NAME", modeName);
                    bizModeList.add(tmp);
                }
            }
            if(IDataUtil.isNotEmpty(bizModeList)&&bizModeList.size()==1){
            	info.put("BIZ_MODE_VALUE", bizModeList.getData(0).getString("BIZ_MODE"));
            }
            info.put("BIZ_MODE_SEL_LIST", bizModeList);
        }
/*        else {
            // 如果没有配置业务开展模式，默认为 5 ，本省受理，本省支付
            IData tmp = new DataMap();
            tmp.put("BIZ_MODE", "5");
            tmp.put("MODE_NAME", "本省受理，本省支付");
            bizModeList.add(tmp);
            info.put("BIZ_MODE_SEL_LIST", bizModeList);
            info.put("BIZ_MODE_VALUE", "5");
        }*/
    //}
    protected IDataset getPayModes( String offerId, String code, String tag) throws Exception
    {
        String attrCode="";
        if (!"init".equals(code))
        {
            attrCode=code;
        }  
        IDataset  result = new DatasetList();
        if(tag.equals("ChgUs")){
        	
        	result = CommonViewCall.getAttrsFromAttrBiz(this,offerId,"B","CHGPYMDS",attrCode);
        }else{
            
        	result = CommonViewCall.getAttrsFromAttrBiz(this,offerId,"B","OPNPYMDS",attrCode);
        	
        }
        return result;
    }
    
    
    protected IDataset getEsoPayModes( String offerId, String code) throws Exception
    {
        String attrCode="";
        if (!"init".equals(code))
        {
            attrCode=code;
        }  
        IDataset  result = CommonViewCall.getAttrsFromAttrBiz(this,offerId,"B","OPNPYMDS",attrCode);
       
        return result;
        
    }
  
//   
    public abstract void setCompInfo(IData compInfo);
    
    public abstract void setAddAttInfo(IData info);

    public abstract void setAddAuditorInfo(IData info);

    public abstract void setAddContactorInfo(IData info);

    public abstract void setAttInfo(IData info);

    public abstract void setAttInfos(IDataset infos);

    public abstract void setAttInfoTag(boolean tag);

    public abstract void setAuditorInfos(IDataset info);

    public abstract void setAuditorInfoTag(boolean tag);

    public abstract void setContactorInfo(IData info);

    public abstract void setContactorInfos(IDataset infos);

    public abstract void setContactorInfoTag(boolean tag);

    public abstract String getOfferId();

    private class MebPageInfo
    {
        private String offerID;
        private IDataset mebTypeSet;
        private String mebType;

        public MebPageInfo(String offerID)
        {
            this.offerID = offerID;
        }

        public IDataset getMebTypeSet()
        {
            return mebTypeSet;
        }

        public String getMebType()
        {
            return mebType;
        }

        public MebPageInfo invoke() throws Exception
        {
            mebTypeSet = EcBbossCommonViewUtil.queryAttrBizInfosByIdTypeObjCode(offerID, "B", "MTYPE", offerID);
            mebType = "";
            if (mebTypeSet != null && mebTypeSet.size() == 1)
            {
                mebType = mebTypeSet.getData(0).getString("ATTR_VALUE");
            }
            else
            {
                mebTypeSet = new DatasetList();
                IData temp = new DataMap();
                temp.put("VALUE", "1");
                temp.put("NAME", "签约关系");
                mebTypeSet.add(temp);
                mebType = "1";
            }
            return this;
        }
    }

    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        super.cleanupAfterRender(cycle);
        setAddAttInfo(null);
        setCompInfo(null);
        setAddAuditorInfo(null);
        setAddContactorInfo(null);
        setAttInfo(null);
        setAttInfos(null);
        setAuditorInfos(null);
        setContactorInfo(null);
        setContactorInfos(null);
    }
}
