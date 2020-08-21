package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;


public class NoPhoneWideDestroyUserSVC extends CSBizService
{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 获取无手机宽带用户预约拆机信息
	 */
	public IDataset getDestroyInfo(IData params) throws Exception
    {
		NoPhoneWideDestroyUserBean bean = BeanManager.createBean(NoPhoneWideDestroyUserBean.class);
        IDataset infos = bean.queryInfo(params);
        return infos;
    }
	
	/**
	 * 无手机宽带-调拆机接口
	 */
	public IDataset callNoPhoneDestroyService(IData params) throws Exception
    {
		NoPhoneWideDestroyUserBean bean = BeanManager.createBean(NoPhoneWideDestroyUserBean.class);
		IDataset data = bean.callNoPhoneDestroyService(params);
		return data;
    } 
	/**
     * 校验预约销号时间
     * @param param
     * @return
     * @throws Exception
     */
    public IData checkDestroyTime(IData param) throws Exception
    {
    	String serialNumber = param.getString("SERIAL_NUMBER","");
    	String destroyTime = param.getString("DESTROY_TIME",""); 
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0");
    	result.put("X_RESULTINFO", "OK");
    	 
    	//校验预约拆机时间不能为空
    	if(destroyTime == null || "".equals(destroyTime))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "预约拆机时间不能为空，请选择!");
    	}
    	
    	if(serialNumber == null || "".equals(serialNumber))
    	{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码不能为空，请检查!");
    	}
    	
    	//校验营销活动时间
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(IDataUtil.isNotEmpty(userInfo))
    	{
    		String userId = userInfo.getString("USER_ID");
        	IData params = new DataMap();
            params.put("USER_ID", userId);
            params.put("PARAM_CODE", param.getString("TRADE_TYPE_CODE"));
            IDataset dt = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_ACTIVES_NEW", params);
            
            if(dt != null && dt.size() > 0)
            {
            	for(int i = 0 ; i < dt.size() ; i++)
            	{
            		//判断是结束时间
            		String endTime = dt.getData(i).getString("END_DATE");
            		if(SysDateMgr.compareTo(destroyTime, endTime) <= 0 )
            		{
            			//不允许
            			result.put("X_RESULTCODE", "-1");
            			result.put("X_RESULTINFO", "您选择的预约拆机时间为[" + destroyTime + "]您当前有营销活动结束时间为[" + endTime + "],不能办理预约拆机!");
            			break;
            		}
            		
            		String startDate = dt.getData(i).getString("START_DATE");
            		//预约时间不能大于首免期
            		if(SysDateMgr.compareTo(destroyTime, startDate) < 0)
                	{
                		//销户时间不能大于首免期
                		result.put("X_RESULTCODE", "-1");
            			result.put("X_RESULTINFO", "首免期内不能办理预约拆机!");
            			break;
                	}
            	}
            }
    	}

        //判断包年套餐
        if(!serialNumber.startsWith("KD_") && serialNumber.length() == 11)
    	{
        	serialNumber = "KD_" + serialNumber;
    	}
        
        IData kdUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(kdUserInfo))
        {
        	String kdUserId = kdUserInfo.getString("USER_ID");
        	IData inParam = new DataMap();
        	inParam.put("USER_ID", kdUserId);
        	inParam.put("PARAM_CODE", param.getString("TRADE_TYPE_CODE"));
            IDataset userDiscntInfos = Dao.qryByCode("TD_S_CPARAM", "SEL_TRADETYPE_LIMIT_DISCNT3", inParam);
            if(userDiscntInfos != null && userDiscntInfos.size() >0)
            {
                
            	String endTime = userDiscntInfos.getData(0).getString("MAX_END_DATE");
            	
            	if (StringUtils.isNotBlank(endTime))
            	{
            	    String nextMonthFirst = SysDateMgr.getDateNextMonthFirstDay(endTime);
                    if(SysDateMgr.compareTo(destroyTime, nextMonthFirst) != 0 )
                    {
                        //不允许
                        result.put("X_RESULTCODE", "-1");
                        result.put("X_RESULTINFO", "您选择的预约拆机时间为[" + destroyTime + "]，您当前的包年套餐结束时间为[" + endTime + "]，无法办理预约拆机。请选择日期["+nextMonthFirst+"]进行拆机。");                  
                    }
            	}
            }
        }
        
        String openDate = kdUserInfo.getString("OPEN_DATE","");
        if(openDate != null && !"".equals(openDate))
        {
        	if(SysDateMgr.compareTo(destroyTime, openDate) < 0 )
        	{
        		result.put("X_RESULTCODE", "-1");
    			result.put("X_RESULTINFO", "首免期内不能办理预约拆机!");
        	}
        }
        
        return result;
    }
    
    public IData calRemainFee(IData param)throws Exception {
    	IData feeData=new DataMap();
    	int feeAll=0;
    	String userId=param.getString("USER_ID","");
    	//1、取宽带产品信息
    	IData qryParam=new DataMap();
    	qryParam.put("USER_ID", userId);
    	IDataset productSet=Dao.qryByCode("TF_F_USER_DISCNT", "SEL_NOPHONE_USER_DISCNT_BY_USERID", qryParam);
    	
    	if(productSet!=null && productSet.size()>0){
    		
    	}else{
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "[SS.NoPhoneWideDestroyUserSVC.calRemainFee]查询无手机宽带产品为空，请确认!");
    	} 
    	for(int k=0;k<productSet.size();k++){
    	    String productId = productSet.getData(k).getString("PRODUCT_ID");
    	    String packageId = productSet.getData(k).getString("PACKAGE_ID");
    	    String discntCode = productSet.getData(k).getString("DISCNT_CODE");
    		String startDate_old = productSet.getData(k).getString("START_DATE");
	    	//取宽带金额
	    	String fee_old="";
	    	
	    	IDataset ds=ProductFeeInfoQry.getElementFeeList("681", BofConst.ELEMENT_TYPE_CODE_PRODUCT, productId, BofConst.ELEMENT_TYPE_CODE_DISCNT, discntCode, packageId);
	    	
	    	if (IDataUtil.isNotEmpty(ds)){
	    		fee_old = ds.getData(0).getString("FEE","0");
	    	}
	    	
	    	//剩余金额=原宽带包年费用 -（原宽带包年费用/12）*新宽带包年产品生效之日前已经出账的月份 v 
	    	
	        String today = SysDateMgr.getSysDate();
	        today = SysDateMgr.getDateNextMonthFirstDay(today);//只能在结束前1个月的时候可以预约
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
			Calendar c1=Calendar.getInstance();
			Calendar c2=Calendar.getInstance();
			c1.setTime(sdf.parse(today));
			c2.setTime(sdf.parse(startDate_old));
			int year1=c1.get(Calendar.YEAR);
			int month1=c1.get(Calendar.MONTH);
			int year2=c2.get(Calendar.YEAR);
			int month2=c2.get(Calendar.MONTH);
			int useMon=0; 
	        if(year1==year2){
	        	useMon=month1-month2;
	        }else{
	        	useMon=(year1-year2)*12+(month1-month2);
	        }
	        //月份大于0才计算
	        if(useMon>=0){
	        	//二货为什么不能弄整除的费用
	        	String paraCode8="";
				String paraCode9="";
				String paraCode10="";
				int monFee = 0;
	        	IDataset commparaInfos = CommparaInfoQry.getCommparaByCodeCode1("CSM","532","681",discntCode);
	        	
	            boolean flag = true;   
                if(DataSetUtils.isNotBlank(commparaInfos))
                {
                	paraCode8=commparaInfos.getData(0).getString("PARA_CODE8","");
    				paraCode9=commparaInfos.getData(0).getString("PARA_CODE9","0");
    				paraCode10=commparaInfos.getData(0).getString("PARA_CODE10","0");
                    if("1".equals(paraCode8))
                    {
                    	flag = false;
                    }
                }
                if(flag)
                {
                	int fee_final=Integer.parseInt(fee_old)-(Integer.parseInt(fee_old)/12)*useMon;
                	feeAll=feeAll+fee_final;
                }else
                {
                	if(useMon<=11)
                    {
                    	monFee = Integer.parseInt(paraCode9);
                    }else
                    {
                    	monFee = Integer.parseInt(paraCode10);
                    }
                	int fee_final=Integer.parseInt(fee_old)-monFee*useMon;
                	feeAll=feeAll+fee_final;
                }
                //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
                if("2".equals(paraCode8))
                {
                	feeAll = 0;
                }
                //add by zhangxing3 for 候鸟月、季、半年套餐（海南）
	        	
	    	}else{
	    		feeAll=feeAll+Integer.parseInt(fee_old);//如果月份小于0，全额退款
	    	}  
    	}
    	if(feeAll!=0){
    		feeData.put("BACK_FEE", feeAll);  
    	}
        return feeData;
    }
    
    /**
	 * 停机3个月后自动拆机 
	 * SS.NoPhoneWideDestroyUserSVC.stop3MonDestroyUser
	 */
	public IDataset stop3MonDestroyUser(IData params) throws Exception
    {
		NoPhoneWideDestroyUserBean bean = BeanManager.createBean(NoPhoneWideDestroyUserBean.class);
		IDataset data = bean.stop3MonDestroyUser(params);
		return data;
    } 
    /**
	 * 无手机宽带拆机时，获取是否收安装费信息：
	 * 
	 */
	public IDataset queryWidenetInstallFee(IData params) throws Exception
    {
		NoPhoneWideDestroyUserBean bean = BeanManager.createBean(NoPhoneWideDestroyUserBean.class);
		IDataset ids = bean.queryWidenetInstallFee(params);
		return ids;
    }
    /**
     * 
     * 到期处理无手机宽带停机接口
     * SS.NoPhoneWideDestroyUserSVC.nophoneWideStopServiceDeal
     * zhangxing3
     * */
    public IDataset nophoneWideStopServiceDeal(IData input) throws Exception
    {
    	NoPhoneWideDestroyUserBean bean = BeanManager.createBean(NoPhoneWideDestroyUserBean.class);
    	return bean.nophoneWideStopServiceDeal(input); 
    }
}
