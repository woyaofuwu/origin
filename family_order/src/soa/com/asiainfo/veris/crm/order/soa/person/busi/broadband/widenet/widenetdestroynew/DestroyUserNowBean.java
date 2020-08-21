
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew;
 
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;
import org.apache.log4j.Logger;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.database.util.SQLParser; 
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao; 
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;

import java.math.BigDecimal;

/**
 * REQ201612080012_优化手机销户关联宽带销号的相关规则
 * @author zhuoyingzhi
 * @date 20180126
 */
public class DestroyUserNowBean extends CSBizBean
{
	private static final Logger log = Logger.getLogger(DestroyUserNowBean.class);
	 
    
    /**
	 * 2、判断该号码下有没有宽带，如果有宽带则在到期处理表里插入一条工单（立即执行的），并且返回成功。如果该号码下没有宽带也返回成功；
		3、到期工单执行时：
		1）生成宽带拆机工单；
		2）对光猫押金做沉淀处理；
		3）对包年剩余费用做沉淀处理；
		
		入参：SERIAL_NUBER(手机号）
	 */
	public static IData checkWideInfoAndDestroy(IData input) throws Exception
	{
		// 设置返回信息
		IData rtnData=new DataMap();
		String result_code="9";//返回标记 1=成功
		
        IData param = new DataMap();
        String tradeId = "";
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        if(serialNumber==null || "".equals(serialNumber)){
        	rtnData.put("RESULT_CODE", result_code);
        	rtnData.put("RESULT_INFO", "手机号SERIAL_NUMBER不能为空。");
        	return rtnData;
        }
        
        if(!serialNumber.startsWith("KD_")){
        	serialNumber="KD_"+serialNumber;
        }
        IData inparam=new DataMap();
        inparam.put("KD_SERIAL_NUMBER", serialNumber);
        IDataset kdinfos=checkWideUserExist(inparam);
        
        if(kdinfos!=null && kdinfos.size()>0){
        	 
	        IDataset usersvcs=checkWideUserSvcstate(inparam);
	        //除了6、9等宽带销户的状态，其余的均插到期处理表处理宽带拆机
	        if(usersvcs!=null && usersvcs.size()>0){ 
	        	IData insparam = new DataMap();
	        	String userId=usersvcs.getData(0).getString("USER_ID");
	        	String execTime=SysDateMgr.getSysTime();  
	        	
	        	param.put("SKIP_RULE", "TRUE");  //传入SKIP_RULE 跳过规则校验
        		param.put("PHONE_DESTROY_TYPE", "7240");
        		param.put("TRADE_TYPE_CODE", "615");
                param.put("SERIAL_NUMBER", serialNumber);
	        	
	        	insparam.put("DEAL_ID", SeqMgr.getTradeId());
	        	insparam.put("USER_ID", userId);
	            insparam.put("PARTITION_ID", userId.substring(userId.length() - 4));
	            insparam.put("SERIAL_NUMBER", serialNumber);
	            insparam.put("EPARCHY_CODE", "0898");
	            insparam.put("IN_TIME", SysDateMgr.getSysTime());
	            insparam.put("DEAL_STATE", "0");//处理状态:  0未处理,L处理队列中,N正在处理,F处理完成,E处理失败
	            insparam.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_WIDENETDESTROY);
	            insparam.put("EXEC_TIME", execTime);//预约执行时间(为系统当前时间则是立即执行)
	            insparam.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
	            insparam.put("TRADE_ID", tradeId);
	            insparam.put("DEAL_COND", param.toString());
	            
	            Dao.insert("TF_F_EXPIRE_DEAL", insparam); 
	            
	            result_code="1";//返回标记 1=成功
	        }else{
	        	result_code="1";//返回标记 1=成功
	        } 
        }else{
        	result_code="1";//返回标记 1=成功
        }
        rtnData.put("RESULT_CODE", result_code);
        return rtnData;
	}
	 
	/**
	 * 查询用户下是否存在宽带
	 * */
	public static IDataset checkWideUserExist(IData inparams) throws Exception
    {
        SQLParser parser = new SQLParser(inparams); 
        parser.addSQL(" select t.* from tf_F_user t ");
        parser.addSQL(" where t.serial_number=:KD_SERIAL_NUMBER  "); 
        parser.addSQL(" and t.remove_tag='0' "); 
    	return Dao.qryByParse(parser);
    }
	
	/**
	 * 查询用户下宽带是否需要拆机
	 * CHECK_WIDEUSER_SVCSTATE
	 * */
	public static IDataset checkWideUserSvcstate(IData inparams) throws Exception
    { 
        return Dao.qryByCode("TF_F_USER_SVCSTATE", "CHECK_WIDEUSER_SVCSTATE", inparams);
    }
	
	/**
	 * 到期处理宽带拆机接口
	 * SS.WidenetDestroyNewRegSVC.tradeReg  这个接口已经存在
	 * */
	public static IDataset wideInfoAndDestroyExpireDeal(IData inparams) throws Exception
    { 
		IData dealParam = new DataMap(inparams.getString("DEAL_COND"));
		//SS.DestroyWidenetUserNowRegSVC.tradeReg
		return CSAppCall.call("SS.WidenetDestroyNewRegSVC.tradeReg", dealParam);
    }

    /**
     *  REQ202003180001 “共同战疫宽带助力”活动开发需求
     *  终止营销活动
     * @param saleActive
     * @throws Exception
     */
    public static void cancelWideOtherSaleActive(IData saleActive) throws Exception{

        IData params=new DataMap();
        params.put("USER_ID", saleActive.getString("USER_ID"));
        params.put("PRODUCT_ID", saleActive.getString("PRODUCT_ID"));
        IDataset iDataset= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);

        if(iDataset.size()>0){
            IData endActiveParam = new DataMap();
            endActiveParam.put("SERIAL_NUMBER", saleActive.getString("SERIAL_NUMBER"));
            endActiveParam.put("PRODUCT_ID", saleActive.getString("PRODUCT_ID"));
            endActiveParam.put("PACKAGE_ID", iDataset.getData(0).getString("PACKAGE_ID"));
            endActiveParam.put("RELATION_TRADE_ID", iDataset.getData(0).getString("RELATION_TRADE_ID"));
            endActiveParam.put("CHECK_MODE", "F");
            endActiveParam.put("CAMPN_TYPE", iDataset.getData(0).getString("CAMPN_TYPE"));
            endActiveParam.put("RETURNFEE", "0");
            endActiveParam.put("YSRETURNFEE", "0");
            endActiveParam.put("TRUE_RETURNFEE_COST", "0");
            endActiveParam.put("TRUE_RETURNFEE_PRICE", "0");
            endActiveParam.put("END_DATE_VALUE", "0");
            endActiveParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
            endActiveParam.put("END_MONTH_LAST", "Y");
            endActiveParam.put("NO_TRADE_LIMIT", "TRUE");
            endActiveParam.put("SKIP_RULE", "TRUE");

            String fee = saleActive.getString("FEE","");
            if(StringUtils.isBlank(fee)){
                fee = DestroyUserNowBean.refundMoney(saleActive);//计算违约金
            }

            if(fee.compareTo("0")>0){
                BigDecimal returnFeeD=new BigDecimal(fee);
                int finalReturnFee=returnFeeD.multiply(new BigDecimal(100)).intValue();

                endActiveParam.put("RETURNFEE",finalReturnFee);

                IData tradeFeeSub = new DataMap();
                tradeFeeSub.put("TRADE_TYPE_CODE", "237");
                tradeFeeSub.put("FEE_TYPE_CODE", "602");
                tradeFeeSub.put("FEE", finalReturnFee);
                tradeFeeSub.put("OLDFEE", finalReturnFee);
                tradeFeeSub.put("FEE_MODE", "0");    //营业费用
                IDataset tradeFeeSubs = new DatasetList();
                tradeFeeSubs.add(tradeFeeSub);
                endActiveParam.put("X_TRADE_FEESUB", tradeFeeSubs);
            }
            CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
        }
    }

	/**
	 * REQ202003180001 “共同战疫宽带助力”活动开发需求
	 * 用于查询非宽带类营销活动,注:家宽类有别的方法校验
	 * @param inparams
	 * @return
	 * @throws Exception
	 */
	public static IDataset checkWideOtherSaleActive(IData inparams) throws Exception{
		//配置活动
        IData widenetinfo=new DataMap();
        if("1".equals(inparams.getString("CHECK_SN_TAG"))){
            widenetinfo.put("USER_ID",inparams.getString("USER_ID"));
        }else{
        	//仅需要查userID,不校验号码,商务宽带拆机进来会报错
            //widenetinfo = WideNetUtil.getWideNetTypeInfo(inparams.getString("SERIAL_NUMBER"));
            String serialNum=inparams.getString("SERIAL_NUMBER");
			if(StringUtils.isBlank(serialNum)){
				CSAppException.appError("210008001", "宽带服务号码为空！");
			}
			String widenetSerialNum="";
			String phoneSerialNum="";
			if (serialNum.startsWith("KD_")){
				widenetSerialNum = serialNum;
				phoneSerialNum = serialNum.substring(3);
			}else{
				widenetSerialNum = "KD_" + serialNum;
				phoneSerialNum = serialNum;
			}
			//0开头为商务宽带，不涉及非宽带类营销活动
			if (phoneSerialNum.startsWith("0")){
				return null;
			}
			IData userInfo = UcaInfoQry.qryUserInfoBySn(widenetSerialNum);
			if (IDataUtil.isEmpty(userInfo)){
				CSAppException.appError("210008002", "该宽带号码不存在有效的用户信息！");
			}
			//RSRV_TAG1为N表示无手机宽带用户
			if ("N".equals(userInfo.getString("RSRV_TAG1",""))){
				//无手机,不涉及非宽带类营销活动
				return null;
			}else{
				IData phoneUserInfo = UcaInfoQry.qryUserInfoBySn(phoneSerialNum);
				if (IDataUtil.isEmpty(phoneUserInfo)){
					CSAppException.appError("210008002", "该手机号码不存在有效的用户信息！");
				}
				if(StringUtils.isBlank(phoneUserInfo.getString("USER_ID"))){
					CSAppException.appError("210008002", "该手机号码未找到USER_ID！");
				}
				widenetinfo.put("USER_ID", phoneUserInfo.getString("USER_ID"));
			}
		}
		if(StringUtils.isBlank(widenetinfo.getString("USER_ID"))){
			CSAppException.appError("210008003", "该手机号码未找到USER_ID！");
		}
		IDataset otherSaleActives = UserSaleActiveInfoQry.getUserSaleActiveByUserIdPackageId(widenetinfo);
        IDataset resultList = new DatasetList();
        if (IDataUtil.isNotEmpty(otherSaleActives)){
			IDataset commparaInfos9924 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9924","SALEACTIVE",null);
			if(IDataUtil.isNotEmpty(commparaInfos9924)){
				for(int i=0;i<otherSaleActives.size();i++){
					String productId = otherSaleActives.getData(i).getString("PRODUCT_ID");
					String packageId = otherSaleActives.getData(i).getString("PACKAGE_ID");
                    for(int j=0;j<commparaInfos9924.size();j++){
                        IData infos9924Data = commparaInfos9924.getData(j);
                        if(productId.equals(infos9924Data.getString("PARA_CODE1"))){
                            if("-1".equals(infos9924Data.getString("PARA_CODE2"))||packageId.equals(infos9924Data.getString("PARA_CODE2"))){
                                IData result = new DataMap();
                                String refundMoney = refundMoney(otherSaleActives.getData(i));
                                result.put("OTHER_ACTIVE_NAME",infos9924Data.getString("PARA_CODE3"));
                                result.put("OTHER_ACTIVE_FEE",refundMoney);
                                result.put("FEE",refundMoney);
                                result.put("PRODUCT_ID",productId);
                                result.put("PACKAGE_ID",packageId);
                                result.put("RELATION_TRADE_ID",otherSaleActives.getData(i).getString("RELATION_TRADE_ID"));
                                result.put("CAMPN_TYPE",otherSaleActives.getData(i).getString("CAMPN_TYPE"));
                                result.put("PRODUCT_MODE",otherSaleActives.getData(i).getString("PRODUCT_MODE"));
                                resultList.add(result);
                                break;
                            }
						}
					}
				}
			}
		}
		return resultList;
	}


	/**
	 * REQ202003180001 “共同战疫宽带助力”活动开发需求
	 * @param saleActive
	 * @return
	 * @throws Exception
	 */
	public static String refundMoney(IData saleActive) throws Exception
	{
		IData svcParam = new DataMap();
		svcParam.put("SERIAL_NUMBER", saleActive.getString("SERIAL_NUMBER"));
		svcParam.put("USER_ID", saleActive.getString("USER_ID"));
		svcParam.put("PRODUCT_ID", saleActive.getString("PRODUCT_ID"));
		svcParam.put("PACKAGE_ID", saleActive.getString("PACKAGE_ID"));
		svcParam.put("RELATION_TRADE_ID", saleActive.getString("RELATION_TRADE_ID"));
		svcParam.put(Route.ROUTE_EPARCHY_CODE, "0898");
		svcParam.put("PRODUCT_MODE", saleActive.getString("PRODUCT_MODE"));
		IDataset refundMoneyData = CSAppCall.call("SS.SaleActiveEndSVC.newcalculateSaleActiveReturnMoney", svcParam);
		if (IDataUtil.isNotEmpty(refundMoneyData)) {
			return refundMoneyData.first().getString("REFUND_MONEY");
		}
		return "0";
	}
}
