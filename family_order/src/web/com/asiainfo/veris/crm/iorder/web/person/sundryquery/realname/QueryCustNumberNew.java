
package com.asiainfo.veris.crm.iorder.web.person.sundryquery.realname;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonQueryPage;

public abstract class QueryCustNumberNew extends PersonQueryPage
{	
	//查询间隔时间30s
	public static int AUTH_CACHE_TIMEOUT = 30;
	private final static String character = "*";
    /**
     * 一证多号信息查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryCustNumber(IRequestCycle cycle) throws Exception
    {
    	/*
         * REQ201903210033  关于一证五号查询界面优化需求
         * 对于NGBOSS前台工号的查询频次进行限制。对于NGBOSS前台工号（接口使用的工号不纳入此范围）使用该界面查询时距离上一次查询需间隔30秒及以上，外围接口工号不限制。
		 * 保存工号到cache中，过期时间30s
         */
    	String cache = String.valueOf(SharedCache.get(getVisit().getStaffId()+"_QueryCustNumber"));
    	if(cache != null && cache.length() != 0 && cache != "null"){
            CSViewException.apperr(CrmCommException.CRM_COMM_103, "使用该界面查询需间隔30秒及以上!");
    	}
        //end REQ201903210033  关于一证五号查询界面优化需求

        IData cond = getData();
        IData param = new DataMap();
        param.put("PSPT_TYPE_CODE", cond.getString("PSPT_TYPE_CODE"));
        param.put("PSPT_ID", cond.getString("PSPT_ID"));
        param.put("ROUTE_EPARCHY_CODE", getVisit().getStaffEparchyCode());
        param.put("VISIT_CONTROL", true);
        IData resultsMap = CSViewCall.callone(this, "SS.NationalOpenLimitSVC.queryCustNumberNew", param);
        IDataset results=resultsMap.getDataset("CUST_NUMBERS");
        IData ajaxResult=new DataMap();
        ajaxResult.put("VISIT_ALARM", resultsMap.getBoolean("VISIT_ALARM", false)?"1":"0");
        if (IDataUtil.isNotEmpty(results)) {
            for (int i = 0; i < results.size(); i++) {
                String province = results.getData(i).getString("HOME_PROV");
                String provinceName = StaticUtil.getStaticValue(this.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_ID" }, "DATA_NAME", new String[] { "GLOBAL_PROVINCE_CODE", province });
                if (provinceName != null && provinceName.trim().length() > 0) {
                    results.getData(i).put("HOME_PROV", provinceName);
                }
            }
        }

        String strSYS012 = cond.getString("X_DATA_NOT_FUZZY", "");
        //模糊化处理
        if (!"true".equals(strSYS012)) {
            if (IDataUtil.isNotEmpty(results)) {
                //空服务，用于审计区别模糊查询和查询
                IData info = CSViewCall.callone(this, "SS.NationalOpenLimitSVC.queryCustNumberNewForMod", param);
                for (int i = 0; i < results.size(); i++) {
                    String serialNumber = results.getData(i).getString("IDV");
                    String psptId = results.getData(i).getString("IDCARD_NUM");
                    
                    //手机号中间4位模糊
                    if (StringUtils.isNotBlank(serialNumber)) {
                        if (serialNumber.length() == 11) {
                            StringBuilder mask = new StringBuilder(11);
                            for (int j = 0; j < 3; j++) {
                                mask.append(serialNumber.charAt(j));
                            }
                            for (int j = 3; j < 7; j++) {
                                mask.append("*");
                            }
                            for (int j = 7; j < serialNumber.length(); j++) {
                                mask.append(serialNumber.charAt(j));
                            }
                            results.getData(i).put("IDV", mask.toString());
                        }
                    }
                    
                    //证件号码模糊
                    String fuzzyPsptId = this.fuzzyPsptId(cond.getString("PSPT_TYPE_CODE"), psptId);
                    results.getData(i).put("IDCARD_NUM", fuzzyPsptId);
                }
            }
        }
        
        /*
         * REQ201903210033  关于一证五号查询界面优化需求
         * 对于NGBOSS前台工号的查询频次进行限制。对于NGBOSS前台工号（接口使用的工号不纳入此范围）使用该界面查询时距离上一次查询需间隔30秒及以上，外围接口工号不限制。
		 * 保存工号到cache中，过期时间30s
         */
        SharedCache.set(getVisit().getStaffId()+"_QueryCustNumber", SysDateMgr.getSysTime(), AUTH_CACHE_TIMEOUT);
        //end REQ201903210033  关于一证五号查询界面优化需求

        setAjax(ajaxResult);
        setNumberInfos(results);
        setNumberCount(IDataUtil.isEmpty(results)?0:results.size());
    }
    
    /**
     * REQ201903210033  关于一证五号查询界面优化需求
     * 3、查询增加人像比对功能。对于无免人像比对权限工号使用该界面查询数据，普通居民身份证号码（含本地身份证、外地身份证、户口本）证件类型，需通过人像比对后才能查询   
     * 
     */
    public void verifyNOTCMPPriv(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	String staffid=getVisit().getStaffId();
    	
    	if (StaffPrivUtil.isFuncDataPriv(staffid, "NOTCMP_STFPRV"))
		{	// 有免比对权限
    		data.put("hasNOTCMPPriv", "true");
    		setAjax(data);
		} else
		{
			data.put("hasNOTCMPPriv", "false");
    		setAjax(data);
		}
    } 
    
    /**
     * 模糊化证件号码
     * 
     * @param psptType
     * @param pstpId
     * @return
     * @throws Exception
     */
    private String fuzzyPsptId(String psptType, String pstpId) throws Exception
    {
        if (StringUtils.isBlank(pstpId))
        {
            return "";
        }

        pstpId = pstpId.trim();

        StringBuilder mask = new StringBuilder(20);

        if (("0".equals(psptType) || "1".equals(psptType) || "2".equals(psptType)))
        {
            if (pstpId.length() == 18)
            {
                // 出生年月日用*代替，最后一位用*代替
                //String ss = pstpId.substring(0, 6);
            	String ss = pstpId.substring(0, 4);
                //String sm = StringUtils.repeat(character, 8);
            	String sm = StringUtils.repeat(character, 11);
                //String se = pstpId.substring(14, pstpId.length() - 1);
                String se = pstpId.substring(15, pstpId.length() - 1);

                mask.append(ss).append(sm).append(se).append(character);
            }
            else if (pstpId.length() == 15)
            {
                // 出生年月日用*代替，最后一位用*代替
                //String ss = pstpId.substring(0, 6);
                String ss = pstpId.substring(0, 4);
                //String sm = StringUtils.repeat(character, 6);
                String sm = StringUtils.repeat(character, 9);
                //String se = pstpId.substring(12, pstpId.length() - 1);
                String se = pstpId.substring(13, pstpId.length() - 1);

                mask.append(ss).append(sm).append(se).append(character);
            }
            else
            {
                mask.append(pstpId);
            }
        }
        else if (pstpId.length() >= 4)
        {
            // 末4位用*代替，
            String ss = pstpId.substring(0, pstpId.length() - 4);
            String se = StringUtils.repeat(character, 4);

            mask.append(ss).append(se);
        }
        else
        {
            mask.append(pstpId);
        }

        return mask.toString();
    }


    public abstract void setNumberCount(long discntCount);

    public abstract void setNumberInfos(IDataset infos);
}
