 
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.BHTradeInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.BTradeInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.CommParaDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360THInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeCardInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeDiscntInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradePlatSvcInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeProductInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeRelationInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeResInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeScoreInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeSmsBoomInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeSvcInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.THTradeSvcStateInfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.TradeCustPersonDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.TradeUserDAO;

public class Qry360THInfoBean extends CSBizBean
{
    public IData getIdentity(String tradeId, String tradeSerialNumber) throws Exception
    {
        if ("".equals(tradeId) || "".equals(tradeSerialNumber))
        {
            return null;
        }
        return new BHTradeInfoDAO().getIdentity(tradeId, tradeSerialNumber);

    }

    /**
     * 查询业务历史--受理信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IData qryThAcceptInfo(IData data) throws Exception
    {
        String tradeFlag = data.getString("TRADE_FLAG", "");
        IData acceptInfo = new DataMap();
        IDataset infos = null;
        IData info = null;
        if ("0".equals(tradeFlag))
        { // 查询tf_b_trade表
            infos = new BTradeInfoDAO().queryAcceptInfo(data);

        }
        else
        { // 查询tf_bh_trade表
            infos = new BHTradeInfoDAO().queryAcceptInfo(data);
        }

        if (infos.size() > 0)
        {
            info = infos.getData(0);
            String processTag = info.getString("PROCESS_TAG_SET", "");
            String acceptDate = info.getString("ACCEPT_DATE", "");
            String checkMode = "未知校验方式";
            if(StringUtils.isNotBlank(processTag) && processTag.length()>=20 && StringUtils.isNotBlank(acceptDate)){
            	String tag = processTag.substring(19,20);
            	//判断割接时间
            	if(Integer.parseInt("20140920") < Integer.parseInt(acceptDate.substring(0, 10).replace("-", ""))){
            		if("0".equals(tag)){
               			checkMode = "证件号码校验" ;
               		  }else if("1".equals(tag)){
               			checkMode = "服务密码校验";
               		  }
               		  else if("2".equals(tag)){
               			checkMode = "SIM卡号+服务密码校验";
               		  }
               		  else if("3".equals(tag)){
               			checkMode = "服务号码+证件号码校验";
               		  }
               		  else if("4".equals(tag)){
               			checkMode = "证件号码+服务密码校验";
               		  }
	            		/**
	                     * REQ201601050018 关于补换卡界面增加短信验证吗和服务密码的校验方式
	                     * chenxy3 20160106 增加换卡（写卡）新的认证组合方式
	                     * */
               		  else if("5".equals(tag)){
               			checkMode = "SIM卡号(或白卡号)+验证码";
               		  }
               		  else if("6".equals(tag)){
               			checkMode = "服务密码+验证码";
               		  }
            		  /**
            		   * REQ201606230019非实名用户关停改造需求
            		   * chenxy3 新的独立认证方式
            		   * */
               		  else if("7".equals(tag)){
                			checkMode = "验证码";
                	  }
               		  else if("8".equals(tag)){
                		checkMode = "SIM卡号(或白卡号)";
               		  }
	            		/**
	          		   * REQ201610200008 补换卡业务调整需求
	          		   * chenxy3 新的独立认证方式 客户证件+证件类型+验证码
	          		   * */
               		  else if("9".equals(tag)){
                		checkMode = "有效证件+验证码";
               		  }
                 	}else{
	             		if("A".equals(tag)){
	               			checkMode = "证件号码+服务密码校验" ;
	               		  }else if("B".equals(tag)){
	               			checkMode = "服务密码校验";
	               		  }
	               		  else if("C".equals(tag)){
	               			checkMode = "SIM卡号+服务密码校验";
	               		  }
	               		  else if("D".equals(tag)){
	               			checkMode = "服务号码+证件号码校验";
	               		  }
	               		  else if("E".equals(tag)){
	               			checkMode = "身份证校验";
	               		  }
                 	}
                 	 if("Z".equals(tag)){
               			checkMode = "未知认证";
               		 }
               		 else if("F".equals(tag)){
               			checkMode = "免认证";
               		 }
            }
            info.put("CHECK_MOD", checkMode);
            
            // 翻译PROCESS_TAG_SET

            String eparchyCode = BizRoute.getRouteId();
            String PROCESS_TAG_SET_STR = transProcessTagByCommPara("CSM", "3001", info.getString("TRADE_TYPE_CODE", ""), eparchyCode, processTag);

            acceptInfo.put("PROCESS_TAG_SET_STR", PROCESS_TAG_SET_STR);
            // add 2010-1-26
            String tradeId = data.getString("TRADE_ID", "");
            String tradeSerialNumber = info.getString("TRADE_SERIAL_NUMBER", "");
            IData identityauthdata = getIdentity(tradeId, tradeSerialNumber);
            if (identityauthdata == null)
            {
                acceptInfo.put("INFO_CONTENT", "");
            }
            else
            {
                acceptInfo.putAll(identityauthdata);
            }
            acceptInfo.putAll(info);
        }
        return acceptInfo;
    }

    /**
     * 这里是集体业务历史查询，连接CG库 获取受理信息，需要区分tf_b_trade表和tf_bh_trade表
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData qryThAcceptInfoCg(IData data) throws Exception
    {
        IData acceptInfo = new DataMap();
        IDataset infos = null;
        IData info = null;
        // 查询tf_bh_trade表
        infos = new BHTradeInfoDAO().queryAcceptInfoCg(data);
        if (infos.size() > 0)
        {
            info = infos.getData(0);
            String processTag = info.getString("PROCESS_TAG_SET", "");
            String checkMode = "未知校验方式";
            if(StringUtils.isNotBlank(processTag) && processTag.length()>=20){
            	String tag = processTag.substring(19,20);
            	if("A".equals(tag)){
           			checkMode = "证件号码+服务密码校验" ;
           		  }else if("B".equals(tag)){
           			checkMode = "服务密码校验";
           		  }
           		  else if("C".equals(tag)){
           			checkMode = "SIM卡号+服务密码校验";
           		  }
           		  else if("D".equals(tag)){
           			checkMode = "服务号码+证件号码校验";
           		  }
           		  else if("E".equals(tag)){
           			checkMode = "身份证校验";
           		  }else if("Z".equals(tag)){
           			checkMode = "未知认证";
           		 }
           		 else if("F".equals(tag)){
           			checkMode = "免认证";
           		 }
            }
            info.put("CHECK_MOD", checkMode);
            // 翻译PROCESS_TAG_SET

            String eparchyCode = BizRoute.getRouteId();
            String PROCESS_TAG_SET_STR = transProcessTagByCommPara("CSM", "3001", info.getString("TRADE_TYPE_CODE", ""), eparchyCode, processTag);
            acceptInfo.put("PROCESS_TAG_SET_STR", PROCESS_TAG_SET_STR);
            // add 2010-1-26
            String tradeId = data.getString("TRADE_ID", "");
            String tradeSerialNumber = info.getString("TRADE_SERIAL_NUMBER", "");
            IData identityauthdata = getIdentity(tradeId, tradeSerialNumber);
            if (identityauthdata == null)
            {
                acceptInfo.put("INFO_CONTENT", "");
            }
            else
            {
                acceptInfo.putAll(identityauthdata);
            }
            acceptInfo.putAll(info);
        }
        return acceptInfo;
    }

    /**
     * 客户资料综合查询 - 业务历史信息信息 --联系信息 获取担保信息，从tf_b_trade_user表和TF_B_TRADE_CUST_PERSON表获取
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IData qryThAssureInfo(IData data) throws Exception
    {
        String history_query_type = data.getString("HISTORY_QUERY_TYPE", "");
        IData assureInfo = new DataMap();
        String assureCustId = null; // 担保客户标识
        IData info = null;
        IDataset infos = new DatasetList();
        TradeUserDAO tradeUserDao = new TradeUserDAO();
        if ("G".equals(history_query_type))
        {
            infos = tradeUserDao.getUserInfosByCg(data);
        }
        else
        {
            infos = tradeUserDao.getUserInfos(data);
        }

        if (infos.size() > 0)
        {
            info = infos.getData(0);
            assureInfo.put("ASSURE_TYPE_CODE", info.get("ASSURE_TYPE_CODE"));
            assureInfo.put("ASSURE_DATE", info.get("ASSURE_DATE"));
            assureCustId = info.getString("ASSURE_CUST_ID");
        }

        if (null != assureCustId)
        {
            data.put("CUST_ID", assureCustId);
            // 从TF_B_TRADE_CUST_PERSON表获取担保人相关信息
            TradeCustPersonDAO tradeCustPersonDao = new TradeCustPersonDAO();
            if ("G".equals(history_query_type))
            {
                infos = tradeCustPersonDao.getCustPersonInfosByCg(data);
            }
            else
            {
                infos = tradeCustPersonDao.getCustPersonInfos(data);
            }

            if (infos.size() > 0)
            {
                info = infos.getData(0);
                assureInfo.put("CUST_NAME", info.get("CUST_NAME"));
                assureInfo.put("PSPT_TYPE_CODE", info.get("PSPT_TYPE_CODE"));
                assureInfo.put("PSPT_ID", info.get("PSPT_ID"));
                // ADD BY YIJB
                assureInfo.put("EPARCHY_CODE", info.get("EPARCHY_CODE"));
            }
        }
        return assureInfo;
    }

    /**
     * 查询业务历史--用户信息
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryThBaseInfo(IData data, Pagination pagination) throws Exception
    {
        Qry360THInfoDAO dao = new Qry360THInfoDAO();

        return dao.qryThBaseInfo(data, pagination);
    }

    /**
     * 国际漫游一卡多号查询(仅个人客户有)
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryThCardInfo(IData param) throws Exception
    {
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        THTradeCardInfoDAO dao = new THTradeCardInfoDAO();
        if ("G".equals(history_query_type))
        {
            return dao.queryTradeCardInfoCg(param);
        }
        else
        {
            return dao.queryTradeCardInfo(param);
        }
    }
    
    
    /**
     * 短信炸弹受理信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     * add  by huangzl3
     */
    public IDataset qryThSmsBoomInfo(IData param) throws Exception
    {      
    	THTradeSmsBoomInfoDAO dao = new THTradeSmsBoomInfoDAO();
        return dao.queryTradeSmsBoomInfo(param);        
    }
    /**
     * 短信炸弹受理信息(新)
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     * add  by huangzl3
     */
    public IDataset qryThSmsBoomInfoNew(IData param) throws Exception
    {      
    	THTradeSmsBoomInfoDAO dao = new THTradeSmsBoomInfoDAO();
        return dao.queryTradeSmsBoomInfoNew(param);        
    }
    /**
     * 短信炸弹防护办理历史查询接口
     * 
     * @param param
     * @return
     * @throws Exception
     * add  by wuhao5
     */
    public IData qrySMSBombStatHisInf(IData param) throws Exception
    {      
    	List<Map<String,Object>> recordInfo = new ArrayList<Map<String,Object>>();
    	if("".equals(param.getData("params").getString("userMobile","")))
    	{
    		IData result = new DataMap();
            result.put("recordInfo", recordInfo);
            
            IData object = new DataMap();
            object.put("result", result);
            object.put("respCode", "1");
            object.put("respDesc", "noSN");
            
            IData returnData = new DataMap();
            returnData.put("rtnCode", "1");
            returnData.put("rtnMsg", "未输入查询号码!");
            returnData.put("object", object);
            
            return returnData;   
    	}
    	param.put("userMobile", param.getData("params").getString("userMobile"));
    	param.put("startTime", param.getData("params").getString("startTime"));
    	param.put("endTime", param.getData("params").getString("endTime"));
    	String paging = param.getData("params").getData("crmpfPubInfo").getString("paging","");
    	Pagination pagination = null;
    	if("1".equals(paging)){
    		String pageNum = param.getData("params").getData("crmpfPubInfo").getString("pageNum","");
    		String rowsPerPage = param.getData("params").getData("crmpfPubInfo").getString("rowsPerPage","");
    		//分页参数非空校验
    		if(rowsPerPage.isEmpty() || pageNum.isEmpty()){
        		IData result = new DataMap();
                result.put("recordInfo", recordInfo);
                
                IData object = new DataMap();
                object.put("result", result);
                object.put("respCode", "2");
                object.put("respDesc", "noPageParm");
                
                IData returnData = new DataMap();
                returnData.put("rtnCode", "2");
                returnData.put("rtnMsg", "当前为分页查询,缺失分页参数!");
                returnData.put("object", object);
                
                return returnData;   
    		}
        	pagination = new Pagination(Integer.parseInt(rowsPerPage),Integer.parseInt(pageNum));
        	pagination.setNeedCount(true);
    	}    	
    	THTradeSmsBoomInfoDAO dao = new THTradeSmsBoomInfoDAO();    
    	IDataset dataset = dao.qrySMSBombStatHisInf(param,pagination);
    	if(dataset.size() > 0 && IDataUtil.isNotEmpty(dataset)){
        	for(int i = 0;i < dataset.size();i ++){
        		IData data = dataset.getData(i);
        		IData resultData = new DataMap();
    			String curProtectStat = data.getString("STATUS");
    			if("0".equals(curProtectStat)){
    				curProtectStat = "1";
    			}
    			else{
    				curProtectStat = "2";
    			}
    			String opChannelName = StaticUtil.getStaticValue("SMSPROT_CHANNEL", data.getString("CHANNEL_ID"));
    			//防护操作 1:开 2:关
    			resultData.put("protectStat", curProtectStat);
    			//流水
    			resultData.put("tradeId", data.get("RECV_ID"));
    			//生效截止时间 格式YYYYMMDDHH24MISS
    			resultData.put("effectiveTime", data.get("EXPIRE_DATE"));
    			//受理时间 格式YYYYMMDDHH24MISS
    			resultData.put("opTime", data.get("ACCEPT_TIME"));
    			//受理渠道 格式 ----渠道编码：渠道名称
    			resultData.put("opChannelName", opChannelName);
    			//受理工号
    			resultData.put("operatorId", data.get("CREATE_STAFF_ID"));
    			//受理号码	
    			resultData.put("userMobile", data.get("SERIAL_NO"));	
    			//备注---由5个预留字段拼接,以"|"符号分割
    			resultData.put("remark", "");
    			recordInfo.add(resultData);
        	}
            IData result = new DataMap();
            result.put("recordInfo", recordInfo);
            
            IData object = new DataMap();
            object.put("result", result);
            object.put("resultRows","");
            if("1".equals(paging)){
    			//符合条件的记录总条数
            	object.put("resultRows", pagination.getCount());
            }
            object.put("respCode", "0");
            object.put("respDesc", "success");
            
            IData returnData = new DataMap();
            returnData.put("rtnCode", "0");
            returnData.put("rtnMsg", "成功!");
            returnData.put("object", object);
            
            return returnData;
        }        
        IData result = new DataMap();
        result.put("recordInfo", recordInfo);
        
        IData object = new DataMap();
        object.put("result", result);
        object.put("respCode", "9");
        object.put("respDesc", "noData");
        
        IData returnData = new DataMap();
        returnData.put("rtnCode", "9");
        returnData.put("rtnMsg", "没有符合条件的记录!");
        returnData.put("object", object);
        
        return returnData;   
    }
    /**
     * 从TF_B_TRADE_CUST_PERSON表中获取客户联系信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryThContactInfo(IData param) throws Exception
    {
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");

        TradeCustPersonDAO dao = new TradeCustPersonDAO();
        IDataset infos = new DatasetList();
        if ("G".equals(history_query_type))
        {
            infos = dao.getCustPersonInfosByCg(param);
        }
        else
        {
            infos = dao.getContactInfos(param);
        }

        return infos;
    }

    /**
     * 查询业务历史用户--优惠变化
     * 
     * @param data
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset qryThDiscntInfo(IData data) throws Exception
    {
        String history_query_type = data.getString("HISTORY_QUERY_TYPE", "");
        THTradeDiscntInfoDAO dao = new THTradeDiscntInfoDAO();
        if ("G".equals(history_query_type))
        {
            return dao.queryTradeDiscntInfoCg(data);
        }
        else
        {
            return dao.queryTradeDiscntInfo(data);
        }
    }

    /**
     * 从TF_B_TRADE_CUST_PERSON表获取其他信息页对应的信息
     * 
     * @param pd
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryThOtherInfo(IData param) throws Exception
    {
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        TradeCustPersonDAO dao = new TradeCustPersonDAO();
        if ("G".equals(history_query_type))
        {
            return dao.getCustPersonInfosByCg(param);
        }
        else
        {
            return dao.getCustPersonInfos(param);
        }
    }

    /**
     * 根据TRADE_ID从表tf_b_trade_product查询产品变化信息
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     */
    public IDataset qryThProductInfo(IData param) throws Exception
    {
        String history_query_type = param.getString("HISTORY_QUERY_TYPE", "");
        THTradeProductInfoDAO dao = new THTradeProductInfoDAO();
        if ("G".equals(history_query_type))
        {
            return dao.queryTradeProductInfoCg(param);
        }
        else
        {
            return dao.queryTradeProductInfo(param);
        }
    }

    /**
     * 根据TRADE_ID查询服务变化
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     */
    public IDataset qryThSvcInfo(IData param) throws Exception
    {
        param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        THTradeSvcInfoDAO dao = new THTradeSvcInfoDAO();
        return dao.queryTradeSvcInfo(param);
    }

    /**
     * 根据TRADE_ID从表tf_b_trade_svcstate查询服务状态变化信息
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     */
    public IDataset qryThSvcStatusInfo(IData param) throws Exception
    {
        param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        THTradeSvcStateInfoDAO dao = new THTradeSvcStateInfoDAO();
        return dao.queryTradeSvcStateInfo(param);
    }

    /**
     * 根据Trade_Id查询平台业务历史
     * 
     * @param pd
     * @param param
     * @return
     */
    public IDataset queryTradePlatSvcInfo(IData param) throws Exception
    {
        param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        THTradePlatSvcInfoDAO dao = new THTradePlatSvcInfoDAO();
        return dao.queryTradePlatSvcInfo(param);

    }

    /**
     * 根据TRADE_ID查询表tf_b_trade_relation中的用户关系信息
     * 
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeRelationInfo(IData param) throws Exception
    {
        param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        THTradeRelationInfoDAO dao = new THTradeRelationInfoDAO();
        return dao.queryTradeRelationInfo(param);
    }

    /**
     * 根据TRADE_ID查询Tf_b_Trade_Res资源信息
     * 
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeResInfo(IData param) throws Exception
    {
        param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        THTradeResInfoDAO dao = new THTradeResInfoDAO();
        return dao.queryTradeResInfo(param);
    }

    /**
     * 根据TRADE_ID查询积分交易明细
     * 
     * @param pd
     * @param param
     * @param pagination
     * @return
     */
    public IDataset queryTradeScoreInfo(IData param) throws Exception
    {

        param.put(Route.ROUTE_EPARCHY_CODE, BizRoute.getRouteId());
        THTradeScoreInfoDAO dao = new THTradeScoreInfoDAO();
        return dao.queryTradeScoreInfo(param);
    }
    
    /**
     * 根据TRADE_ID查询历史库  台账历史信息
     * 
     * @param param
     * @return
     */
    public IDataset queryTradeInfoByHis(IData param) throws Exception
    {
    	TradeUserDAO dao = new TradeUserDAO();
        return dao.getTradeInfosByHis(param);
    }

    /**
     * 根据普通参数表翻译业务处理方式
     * 
     * @param pd
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @param processTag
     * @return
     * @throws Exception
     */
    private String transProcessTagByCommPara(String subsysCode, String paramAttr, String paramCode, String eparchyCode, String processTag) throws Exception
    {
        IData param = new DataMap();
        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);
        IDataset infos = new CommParaDAO().getCommPara(param);
        IData info = null;
        for (int i = 0; i < infos.size(); i++)
        {
            info = infos.getData(i);
            String flag = processTag.substring(info.getInt("PARA_CODE2") - 1, 1);
            if (info.getString("PARA_CODE3").equals(flag))
            {
                return info.getString("PARA_CODE4");
            }
        }

        return "";
    }

}
