package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.widenetmove;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.query.product.UDiscntInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

public class NoPhoneWidenetMoveBean extends CSBizBean
{
	
	/**
     * 获取宽带主产品信息
     * */
	public IDataset getUserProductInfo(IData data) throws Exception
    {
    	String userId = data.getString("USER_ID");
        String userProductId = "";
        String productMode = "";
        
        //通过userid获取用户主产品信息
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);
        String sysDate = SysDateMgr.getSysTime();
        
        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                
                //已经生效的主产品
                if (userProduct.getString("START_DATE").compareTo(sysDate) < 0)
                {
                    userProductId = userProduct.getString("PRODUCT_ID");
                    productMode = userProduct.getString("PRODUCT_MODE");
                }
            }
        }
        
        IData result = new DataMap();
        
        //传出用户移机前，原主产品名称
        String userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
        result.put("USER_PRODUCT_NAME", userProductName);
        result.put("PRODUCT_MODE", productMode);
        result.put("PRODUCT_ID", userProductId);
        
        //REQ201808030011优化200M及以上的宽带产品业务流程
/*        String old_rate = WideNetUtil.getWidenetProductRate(userProductId);
        result.put("USER_WIDENET_RATE", old_rate);          
        IDataset commparas = CommparaInfoQry.getCommparaAllColByParser("CSM", "9102", "CHECK_BANDWIDTH_TAG", "0898");
        if (IDataUtil.isNotEmpty(commparas))
        {
        	if("1".equals(commparas.getData(0).getString("PARA_CODE1", "0")))
        	{
        		result.put("CHECK_BANDWIDTH_TAG", "1");  
        	}
        }*/
        //REQ201808030011优化200M及以上的宽带产品业务流程
        
        IDataset resu = new DatasetList();
        resu.add(result);
        return resu;
    }

	/**
     * 仅仅是为了得到WIDENETMOVE_FIRST或报错拦截不允许移机
     * 1、是否是宽带开户首月（a.用openDate与sysdate判断；b.用是否有生效优惠判断）不能移机，如果办理工号有WIDENETMOVE_FIRST权限则WIDENETMOVE_FIRST=1
     * 2、判断本月是否做过移机，根据widenet表的start_date与sysdate判断，widenet表的start_date这个字段在移机回单的时候，会进行更新
     * */
	public IDataset judgeIsCanMove(IData data) throws Exception
    {
    	IDataset dataset = chkWideMoveAndChgProdNum(data);
    	return dataset;
    }
	
	//judgeIsCanMove中调用
    public IDataset chkWideMoveAndChgProdNum(IData input) throws Exception
    {
    	String userId = input.getString("USER_ID");
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	
        IDataset userInfo = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);//通过SERIAL_NUMBER获取user表信息
        String inDate = userInfo.getData(0).getString("OPEN_DATE");//得到OPEN_DATE
		String inMonth = SysDateMgr.getDateForYYYYMMDD(inDate).substring(0, 6);//处理OPEN_DATE，得到月份
		String currMonth = SysDateMgr.getNowCycle().substring(0, 6);//系统当前时间的月份
		boolean bExtend = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "WIDENETMOVE_FIRST");//判断当前办理工号是否含有WIDENETMOVE_FIRST权限
		
		IData idFirst = new DataMap();
		idFirst.put("WIDENETMOVE_FIRST", "0");
		boolean bFirst = false;//初始bFirst
		
		//1、判断OPEN_DATE是不是系统当前月，即宽带办理的首月免费期内
		if (inMonth.equals(currMonth))
		{
			//如果在宽带办理的首月免费期内，并且当前办理工号不含有WIDENETMOVE_FIRST权限，则提示宽带免费期内不能移机
			if(!bExtend)
			{
				CSAppException.apperr(CrmUserException.CRM_USER_783,"宽带免费期内不能移机");
			}
			else//如果在宽带办理的首月免费期内，并且当前办理工号含有WIDENETMOVE_FIRST权限，则传出WIDENETMOVE_FIRST=1，同时bFirst=true
			{
				bFirst = true;
				idFirst.put("WIDENETMOVE_FIRST", "1");
			}
		}

        //2、查询用户有效的优惠信息，该判断应该是为了处理25日办理宽带开户之后，次月还是免费期内的情况
        IDataset discntInfo = UserDiscntInfoQry.queryUserNormalDiscntsByUserId(userId);//通过userid获取优惠信息
        
        //如果用户无有效的优惠信息，即可能优惠还未生效，或者无优惠
        if (discntInfo == null || discntInfo.size() <= 0)
        {
        	//如果用户无有效的优惠信息，即可能优惠还未生效，并且当前办理工号不含有WIDENETMOVE_FIRST权限，则提示宽带免费期内不能移机
        	if(!bExtend)
			{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带免费期内不能移机");
			}
        	else//如果用户无有效的优惠信息，即可能优惠还未生效，并且当前办理工号含有WIDENETMOVE_FIRST权限，则传出WIDENETMOVE_FIRST=1，同时bFirst=true
			{
        		bFirst = true;
				idFirst.put("WIDENETMOVE_FIRST", "1");
			}
        }
        
        //3、判断本月是否做过移机，根据widenet表的start_date判断，这个字段，在移机回单的时候，会进行更新
    	IDataset wideNetInfo = WidenetInfoQry.getUserWidenetInfo(userId);//通过userid获取widenet表信息
        if (IDataUtil.isNotEmpty(wideNetInfo))
        {
        	String startDate = wideNetInfo.getData(0).getString("START_DATE");//获取宽带widenet表的START_DATE值
    		String smonth = SysDateMgr.getDateForYYYYMMDD(startDate).substring(0, 6);//格式化到年月
    		String tmonth = SysDateMgr.getNowCycle().substring(0, 6);//取系统当前时间的年月
    		
    		//如果办理过移机，则宽带widenet表中START_DATE会与系统当前月相同
    		if (smonth.equals(tmonth))//判断是不是最后一个月
    		{
    			if(!bFirst)//同时如果当前办理工号不含有WIDENETMOVE_FIRST权限，则提示不能移机
    			{
    				CSAppException.apperr(CrmUserException.CRM_USER_783,"请勿在本月重复办理移机业务！");
    			}
    		}
        }
        
        IDataset results = new DatasetList();
        results.add(idFirst);
    	return results;
    }
	
    /**
     * 获取宽带类型列表
     * */
    public IDataset showProdMode(IData input) throws Exception
    {
    	IDataset results = CommparaInfoQry.getCommpara("CSM", "210", "WIDE_TYPE_PROD_MODE", "0898");
    	return results;
    }
	
    /**
     * 获取宽带用户other表中光猫信息  RSRV_VALUE_CODE='FTTH'
     * */
    public IDataset getModelInfo(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER","");
    	input.put("SERIAL_NUMBER", serialNumber);
    	IDataset results = WidenetInfoQry.getCommQryInfo(input);
    	return results;
    }
	
    /**
     * 作用未知，获取6131配置
     * */
    public IDataset getSaleActiveComm(IData data) throws Exception
    {
    	String eparchyCode = data.getString("EPARCHY_CODE");
    	String paramAttr = data.getString("PARAM_ATTR");
    	String paramCode = data.getString("PARAM_CODE");
    	IDataset results = CommparaInfoQry.getCommpara("CSM", paramAttr, paramCode, eparchyCode);
    	return results;
    }
    
    /**
     * 获取产品信息，同时判断是否存在预约主产品变更IS_CHG_OTHER，和判断是否存在宽带包年优惠并传出相应值IS_HAS_YEAR_DISCNT、HAS_EFF_YEAR
     * */
    public IDataset getWidenetProductInfo(IData input) throws Exception
    {
        String productMode = input.getString("NEW_PRODUCT_MODE");
        
        //增加产品规则校验，产品变更时的规则不能直接拿过来用，此处增加处理
        String isChgOther = "";
        String isOneSnManyWide = "";//是否为一机多宽用户

        IDataset chkInfos = chkProductChgRule(input);//调用chkProductChgRule
        if(chkInfos!=null&&chkInfos.size()>0){
        	if("1".equals(chkInfos.getData(0).getString("IS_CHG_OTHER"))){//如果存在未生效的主产品变更，即预约产品变更，即START_DATE>sysDate
        		isChgOther = chkInfos.getData(0).getString("IS_CHG_OTHER");//
        		return chkInfos;
        	}
        	isChgOther = chkInfos.getData(0).getString("IS_CHG_OTHER");
        	isOneSnManyWide = chkInfos.getData(0).getString("IS_ONESNMANYWIDE","");//是否为一机多宽用户

        }
        
        //修改原有方法，改为调用新增的查询产品列表的方法，新增的方法有速率信息
        IDataset widenetProductInfos = WidenetInfoQry.getWidenetProduct_RATE(productMode, CSBizBean.getTradeEparchyCode());
		
		for(int i=0;i<widenetProductInfos.size();i++){
			widenetProductInfos.getData(i).put("IS_CHG_OTHER", isChgOther);//是否存在预约产品变更
			widenetProductInfos.getData(i).put("IS_ONESNMANYWIDE", isOneSnManyWide);//是否为一机多宽用户

		}
        
        return widenetProductInfos;
    }
    
    //getWidenetProductInfo中调用 是否可以进行产品变更
    public IDataset chkProductChgRule(IData input) throws Exception
    {  	
    	IData idata = new DataMap();
    	idata.put("IS_CHG_OTHER", "0");
    	
    	//1、先判断是否存在未生效的主产品信息  即START_DATE>sysDate
    	String sysDate = SysDateMgr.getSysTime();
    	String wideUserId = input.getString("USER_ID");
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(wideUserId);
        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();
            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);
                if (SysDateMgr.compareTo(userProduct.getString("START_DATE"),sysDate) >= 0)
                {
                	idata.put("IS_CHG_OTHER", "1");
                }
            }
        }
        
        //REQ201903080003一机多宽优惠活动开发需求调整
    	boolean isFtth = input.getBoolean("IS_FTTH",true);
        //IDataset userOtherInfo = UserOtherInfoQry.getOtherInfoByCodeUserId(wideUserId, "ONESN_MANYWIDE");
        IDataset ids = RelaUUInfoQry.getUserRelationRole2(wideUserId,"58","1");
        if ( IDataUtil.isNotEmpty(ids))
        {
        	idata.put("IS_ONESNMANYWIDE", "1");////是否为一机多宽用户
        }
        //REQ201903080003一机多宽优惠活动开发需求调整
        
    	IDataset results = new DatasetList();
    	results.add(idata);
    	return results;
    }
    
    /**
     * 根据AreaCode得到光猫厂家
     * */
    public IDataset getStaticInfoOnly(IData data) throws Exception
    {
    	String dataId = data.getString("DATA_ID");
    	String typeId = data.getString("TYPE_ID");
    	String dataValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", 
        		new java.lang.String[]{ "TYPE_ID", "DATA_ID" }, "PDATA_ID", new java.lang.String[]{ typeId, dataId });
    	
    	IDataset results = new DatasetList();
    	IData idata = new DataMap();
    	idata.put("DATA_VALUE", dataValue);
    	results.add(idata);
    	return results;
    }
	public IDataset getUserDiscntInfo(IData data) throws Exception
    {
    	String userId = data.getString("USER_ID");
        String userDiscntCode = "";  
        String houniaoTag = "0";
        //通过userid获取用户优惠信息
        IDataset userDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
        String sysDate = SysDateMgr.getSysTime();
        
        if (IDataUtil.isNotEmpty(userDiscnts))
        {
            int size = userDiscnts.size();
            for (int i = 0; i < size; i++)
            {
                IData userDiscnt = userDiscnts.getData(i);
                
                //已经生效的主产品
                if (userDiscnt.getString("START_DATE").compareTo(sysDate) < 0)
                {
                	userDiscntCode = userDiscnt.getString("DISCNT_CODE");
                	if ("84014240".equals(userDiscntCode) || "84014241".equals(userDiscntCode) || "84014242".equals(userDiscntCode)
                			|| "84071448".equals(userDiscntCode) || "84071449".equals(userDiscntCode) || "84074442".equals(userDiscntCode)
                			|| "84071447".equals(userDiscntCode))
                	{
                		houniaoTag = "1";
                	}
                }
            }
            userDiscnts.getData(0).put("HOUNIAO_TAG", houniaoTag);
        }
		//System.out.println("========NoPhoneWidenetMoveBean======userDiscnts:"+userDiscnts);
        return userDiscnts;
    }
}
