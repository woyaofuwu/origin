
package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UStaffInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpAllInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;

public class QryNPInfoSVC extends CSBizService
{

    /**
     * @Function: getAgentBusi
     * @Description: 携入携出查询校验
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: fusr
     * @date: 
     */
	private static String intel_name1 = "中国移动网络";
	private static String intel_name2 = "中国联通网络";
	private static String intel_name3 = "中国电信网络";
	private static String operator_name1 = "中国移动";
	private static String operator_name2 = "中国联通";
	private static String operator_name3 = "中国电信";
	
	  public IData queryNPInfo(IData param) throws Exception
    {
    	IData result = new DataMap();
    	IDataset ids = new DatasetList();
    	IData outData = new DataMap();
        try{
	        String busiCode = param.getString("userMobile", "");
	
	        if ( "".equals(busiCode))
	        {
	        	busiCode = getParams(param).getString("userMobile", "");
	        }
        	       	
	        if("".equals(busiCode))
	        {        		            
	            result = prepareOutResult(1,"userMobile不能为空",outData);
	            return result;
	        }
	        
	       /* UcaData ucaData = UcaDataFactory.getNormalUca(busiCode);
	        IData input = new DataMap();
	        String userId = ucaData.getUserId();*/
	        IDataset userSaleActiveInfos = new DatasetList();
	       /* input.put("USER_ID", userId);
	        input.put("SERIAL_NUMBER", busiCode);*/
	        userSaleActiveInfos = UserNpInfoQry.qryUserNpInfosBySn(busiCode);
	        IData MpAreaProvinceInfo = queryMpAreaProvinceInfo(busiCode);// 获取手机号码归属地信息(全国)
	        String homeProvinceCode = MpAreaProvinceInfo.getString("PROV_CODE");// 手机号码归属地省编码
	        String provinceAndCity = MpAreaProvinceInfo.getString("AREA_NAME");// 手机用户


        	 if(!IDataUtil.isEmpty(userSaleActiveInfos)){
    		 	for (int i = 0 ; i<userSaleActiveInfos.size(); i++)
	        	{
	        		IData tmpData = new DataMap();
	        		String in_NP_ID = userSaleActiveInfos.getData(i).getString("PORT_IN_NETID", "");
	        		String out_NP_ID = userSaleActiveInfos.getData(i).getString("PORT_OUT_NETID", "");
	        		String home_NP_ID = userSaleActiveInfos.getData(i).getString("HOME_NETID", "");
	        		String state = userSaleActiveInfos.getData(i).getString("NP_TAG", "");
	        		String remark = userSaleActiveInfos.getData(i).getString("REMARK", "");
	        		String np_state = "";
	        		String np_type = userSaleActiveInfos.getData(i).getString("NP_SERVICE_TYPE", "");
	        		String np_type_name = "";
	        		String statedesc = "";
	        		String npbegindate = "";
	        		String in_code = "";
	        		String out_code = "";
	        		String home_code = "";
	        		String inoperator_name = "";
	        		String outoperator_name = "";
	        		String homeoperator_name = "";
	        		String inoperator_intel_name = "";
	        		String outoperator_intel_name = "";
	        		String homeoperator_intel_name = "";
	        		if(!in_NP_ID.isEmpty()){
	        			 in_code = in_NP_ID.substring(0, 3);
	        			if(in_code.equals("001")){
	        				inoperator_name = operator_name1;
	        				inoperator_intel_name = intel_name1;
	        			}else if(in_code.equals("002")){
	        				inoperator_name = operator_name2;
	        				inoperator_intel_name = intel_name2;
	        			}else if(in_code.equals("003")){
	        				inoperator_name = operator_name3;
	        				inoperator_intel_name = intel_name3;
	        			}
	        		}
	        		if(!out_NP_ID.isEmpty()){
	        			 out_code = out_NP_ID.substring(0, 3);
	        			if(out_code.equals("001")){
	        				outoperator_name = operator_name1;
	        				outoperator_intel_name = intel_name1;
	        			}else if(out_code.equals("002")){
	        				outoperator_name = operator_name2;
	        				outoperator_intel_name = intel_name2;
	        			}else if(out_code.equals("003")){
	        				outoperator_name = operator_name3;
	        				outoperator_intel_name = intel_name3;
	        			}
	        		}
	        		if(!home_NP_ID.isEmpty()){
	        			 home_code = home_NP_ID.substring(0, 3);
	        			if(home_code.equals("001")){
	        				homeoperator_name = operator_name1;
	        				homeoperator_intel_name = intel_name1;
	        			}else if(home_code.equals("002")){
	        				homeoperator_name = operator_name2;
	        				homeoperator_intel_name = intel_name2;
	        			}else if(home_code.equals("003")){
	        				homeoperator_name = operator_name3;
	        				homeoperator_intel_name = intel_name3;
	        			}
	        		}
	        		if(state.equals("1") || state.equals("2")){
	        			npbegindate = userSaleActiveInfos.getData(i).getString("PORT_IN_DATE", "");
	        		}else if(state.equals("5")){
	        			npbegindate = chgFormat(userSaleActiveInfos.getData(i).getString("PORT_OUT_DATE", ""),SysDateMgr.PATTERN_STAND_YYYYMMDD,SysDateMgr.PATTERN_STAND);
	        		}else{
	        			npbegindate = userSaleActiveInfos.getData(i).getString("APPLY_DATE", "");
	        		}
	        		
	        		if(np_type.equals("MOBILE")){
	        			np_type_name = "移动号码携带";
	        		}else if(np_type.equals("FIXED")){
	        			np_type_name = "固定号码携带";
	        		}
	        		
	        		if(state.equals("1") || state.equals("4") || state.equals("6") || state.equals("8")){
	        			np_state = "1";
	        			statedesc = "正常";
	        		}else{
	        			np_state = "2";
	        			statedesc = "异常";
	        		}
	        		tmpData.put("serviceNumber", userSaleActiveInfos.getData(i).getString("SERIAL_NUMBER", ""));
	        		tmpData.put("state", np_state);
	        		tmpData.put("stateDesc",statedesc);
	        		tmpData.put("netId", in_code);
	        		tmpData.put("netIdDesc", inoperator_name);
	        		tmpData.put("portInId", in_NP_ID);
	        		tmpData.put("portInIdDesc", inoperator_intel_name);
	        		tmpData.put("portOutId", out_NP_ID);
	        		tmpData.put("portOutIdDesc", outoperator_intel_name);
	        		tmpData.put("homeNet", home_NP_ID);
	        		tmpData.put("homeNetDesc", homeoperator_intel_name);
	        		tmpData.put("activeTime", npbegindate);
	        		tmpData.put("serviceType", np_type);
	        		tmpData.put("serviceTypeDesc", np_type_name);
	        		tmpData.put("region", CSBizBean.getUserEparchyCode());//号码归属地市编码
	        		tmpData.put("regionName", CSBizBean.getUserEparchyCode());//号码归属地市名称
	        		tmpData.put("province", ProvinceUtil.getProvinceCode());//号码归属省编码
	        		tmpData.put("provinceName", ProvinceUtil.getProvinceCode());//号码归属省名称
	        		tmpData.put("ext1",remark);
	        		tmpData.put("ext2","");
	        		tmpData.put("ext3","");
	        		tmpData.put("ext4","");
	        		tmpData.put("ext5","");
	        		tmpData.put("ext6","");
	        		tmpData.put("ext7","");
	        		tmpData.put("ext8","");
	        		tmpData.put("ext9","");
	        		tmpData.put("ext10","");
	        		ids.add(i, tmpData);		        		
	        	}
    		 	outData.put("carryNumberUserInfo", ids);
    		 	outData.put("resultRows", ids.size());
	        	result = prepareOutResult(0,"",outData);	
        	 }
	        else
	        {	
	            result = prepareOutResult(1,"无法查询到用户携转信息！",outData);	
		    }

	        return result;
        }        
        catch (Exception e)
        {	
        	result = prepareOutResult(1,e.getMessage(),outData);
            
            return result;
        }
    }
	  
	  /**
		 * 携号转出资格校验接口
		 * @param param  SERIAL_NUMBER
	     * @return
	     * @throws Exception
	     * X_RESULTCODE
		 * 3059:号段属于卫星移动，不支持携号转网。3061:号段属于移动通信转售，不支持携号转网。3062:号段属于物联网，不支持携号转网。
		 * 3015:号码为单位所有，需过户至个人名下后办理携号转网。2031:申请携号转网的号码未在携出方办理真实身份信息登记。
		 * 2009:申请携号转网的号码处于XXXX（如停机/挂失）的非正常使用状态。2067:当前有X元费用尚未缴清影响携号转网办理。
		 * 2065:号码可能有未出账的国际漫游费用影响携号转网办理。2064:有在网协议XXX影响携号转网办理。
		 * 3060:您的号码距离上次携转时间未达规定值，暂无法办理携号转网，请您于X年X月X日后再查询。
		 * 3064: 携号转网将影响您已办理的XXX，请在申请携号转网前做好相关业务变更。(备注：各省可将因关联业务未取消导致不能转出的原因通过此字段返回，如固移融合和合账。)
		 * */
	  public IData checkNpOutMessage(IData param) throws Exception
	    {
	    	IData result = new DataMap();
	    	IDataset ids = new DatasetList();
	    	IDataset ids1 = new DatasetList();
	    	IData outData = new DataMap();
	        try{
		        String busiCode = param.getString("userMobile", "");
		
		        if ( "".equals(busiCode))
		        {
		        	busiCode = getParams(param).getString("userMobile", "");
		        }
	        	       	
		        if("".equals(busiCode))
		        {        		            
		            result = prepareOutResult1(1,"userMobile不能为空",outData);
		            return result;
		        }
		        
		        outData.put("checkResult", "1");

				param.put("ABILITY_CHECK", "true");
				param.put("SERIAL_NUMBER", busiCode);
				IDataset resultInfoLists = CSAppCall.call("SS.QueryNpMessageSVC.queryNpOutMessage", param);
				IData results = resultInfoLists.getData(0);
				IDataset resultInfo = results.getDataset("RESULTINFO");
				IDataset limitInfoList = results.getDataset("LIMITINFOLIST");

				if(DataUtils.isEmpty(resultInfo))
				{
					outData.put("checkResult", "0");
				}
				else
				{
					
					for (int i = 0 ; i<resultInfo.size(); i++)
		        	{
		        		IData tmpData = new DataMap();
		        		String result_code = resultInfo.getData(i).getString("RESULT_CODE", "");
		        		String result_desc = resultInfo.getData(i).getString("RESULT_DESC", "");
		        		
		        		tmpData.put("checkResultCode",result_code);
		        		tmpData.put("checkResultDesc",result_desc);
		        		ids.add(i, tmpData);	
		        	}
					outData.put("resultInfoList", ids);
					if(DataUtils.isNotEmpty(limitInfoList)){
						for (int i = 0 ; i<limitInfoList.size(); i++)
			        	{
			        		IData tmpData1 = new DataMap();
			        		String limit_code = limitInfoList.getData(i).getString("LIMIT_ID", "");
			        		String limit_name = limitInfoList.getData(i).getString("LIMIT_NAME", "");
			        		String limit_time = limitInfoList.getData(i).getString("LIMIT_TIME", "");
			        		String limit_desc = limitInfoList.getData(i).getString("LIMIT_DESC", "");
			        		String handle_channel = limitInfoList.getData(i).getString("HANDLE_CHANNEL", "");
			        		String handle_type = limitInfoList.getData(i).getString("HANDLE_TYPE", "");
			        		
			        		tmpData1.put("limitId",limit_code);
			        		tmpData1.put("limitName",limit_name);
			        		tmpData1.put("limitExpTime",limit_time);
			        		tmpData1.put("limitDesc",limit_desc);
			        		tmpData1.put("removeHandleChannel",handle_channel);
			        		tmpData1.put("removeHandleAuthType",handle_type);
			        		ids1.add(i, tmpData1);	
			        	}
						outData.put("limitInfoList", ids1);
					}
				}
		        	result = prepareOutResult1(0,"",outData);	
	        	

		        return result;
	        }        
	        catch (Exception e)
	        {	
	        	result = prepareOutResult1(1,e.getMessage(),outData);
	            
	            return result;
	        }
	    }
	  

		private IData getParams(IData param) throws Exception {
	    	
	    	Object o = param.get("params");
	    	if(o instanceof Map) {
	    		return new DataMap((Map) o);
			}else if(o instanceof IData) {
				return (IData) o;
			}else if (o instanceof String) {
				return new DataMap(String.valueOf(o));
			}
			throw new Exception("未识别的params参数...params=" + o +",type=" + o.getClass());
	    }
		
		 public IData prepareOutResult(int i,String rtnMsg,IData outData)
		    {
		    	IData object = new DataMap();
		    	IData result = new DataMap();

		    	if (i==0)//成功
		    	{
		        	object.put("resultRows", outData.getString("resultRows","1"));
		        	outData.remove("resultRows");
		        	object.put("result", outData);
		            object.put("respCode", "000000");
		            object.put("respDesc", "success");
		            
		            result.put("object", object);
		    		result.put("rtnCode", "0");	
		    		result.put("rtnMsg", "成功!");	
		            return result;
		    	}
		    	else if(i==1)//失败
		    	{
		        	object.put("result", outData);
		        	object.put("resultRows", 0);
		            object.put("respCode", "-1");
		            object.put("respDesc", rtnMsg);
		            
		            result.put("object", object);
		    		result.put("rtnCode", "-9999");	
		    		result.put("rtnMsg", "失败");	
		            
		            return result;
		    	}
		    	return null;
		    }
		 
		 public IData prepareOutResult1(int i,String rtnMsg,IData outData)
		    {
		    	IData object = new DataMap();
		    	IData result = new DataMap();

		    	if (i==0)//成功
		    	{
		        	object.put("result", outData);
		            object.put("respCode", "0");
		            object.put("respDesc", "success");
		            
		            result.put("object", object);
		    		result.put("rtnCode", "0");	
		    		result.put("rtnMsg", "成功!");	
		            return result;
		    	}
		    	else if(i==1)//失败
		    	{
		        	object.put("result", outData);
		        	object.put("resultRows", 0);
		            object.put("respCode", "-1");
		            object.put("respDesc", rtnMsg);
		            
		            result.put("object", object);
		    		result.put("rtnCode", "-9999");	
		    		result.put("rtnMsg", "失败");	
		            
		            return result;
		    	}
		    	return null;
		    }
		 
			private static String chgFormat(String strDate, String oldForm, String newForm) throws Exception{
				if (null == strDate)
		        {
		            throw new NullPointerException();
		        }

		        DateFormat oldDf = new SimpleDateFormat(oldForm);
		        Date date = oldDf.parse(strDate);

				String newStr = "";
		        DateFormat newDf = new SimpleDateFormat(newForm);
		        newStr = newDf.format(date);        
				return newStr;
			}
		
	  
			 /**
		     * 根据号码查询归属省份信息
		     * 
		     * @param data
		     * @return
		     * @throws Exception
		     */
		    private static IData queryMpAreaProvinceInfo(String serialNumber) throws Exception
		    {
		        IData moffice = MsisdnInfoQry.getRouteInfoBySn(serialNumber);

		        if (IDataUtil.isEmpty(moffice))
		        {
		            CSAppException.apperr(PlatException.CRM_PLAT_74, "获取该手机号码归属地无信息！");
		        }

		        String provinceName = StaticUtil.getStaticValue("PROVINCE_TYPE_CODE", moffice.getString("PROV_CODE"));
		        String cityName = StaticUtil.getStaticValue("CHINA_AREA_CODE", moffice.getString("AREA_CODE"));

		        moffice.put("AREA_NAME", provinceName + cityName);
		        return moffice;

		    }

			
    /**
     * @Function: getAgentSN
     * @Description: 代理商放号报表查询
     * @param param
     * @return
     * @throws Exception
     * @version: v1.0.0
     * @author: lijm3
     * @date: 2014年7月25日 上午11:19:09
     */
    public IDataset getAgentSN(IData param) throws Exception
    {
        String departId = param.getString("DEPART_ID");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        return TradeInfoQry.getAgentReleaseSnSumByDepartId(departId, startDate, endDate);
    }
}
