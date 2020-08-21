package com.asiainfo.veris.crm.order.soa.person.busi.nophonetopsetbox.nophonetopsetboxdestroy;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;



public class NoPhoneTopSetBoxDestroySVC extends CSBizService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 核对拆机用户，并获取相关信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset checkUserValid(IData param)throws Exception{
		
		IData result=new DataMap();
		
		String serialNumber=param.getString("AUTH_SERIAL_NUMBER");
		
		if (!serialNumber.startsWith("KD_"))
		{
			serialNumber = "KD_" + serialNumber; 
		}
		
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        if(IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息！"); 
        }
        
        String userIdB = userInfo.getString("USER_ID");  //宽带号码userID
        IData userInfoA = getRelaUUInfoByUserIdB(userIdB);
        String userIdA = userInfoA.getString("USER_ID_A"); //147号码userID
        
		IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userIdA, "4", "J");
		
		if(IDataUtil.isEmpty(boxInfos))
		{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通互联网电视，无法办理互联网拆机！");
        }
		
		IDataset tradeInfos = TradeInfoQry.getMainTradeByUserId(userIdA);
		if(!IDataUtil.isEmpty(tradeInfos))
		{
			for(int i= 0;i<tradeInfos.size();i++){
				IData tradeInfo = tradeInfos.getData(i);
				if("4906".equals(tradeInfo.getString("TRADE_TYPE_CODE")))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户存在无手机魔百和拆机未完工工单！");
				}
				if("4901".equals(tradeInfo.getString("TRADE_TYPE_CODE")))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户存在无手机魔百和停机未完工工单！");
				}
				if("4907".equals(tradeInfo.getString("TRADE_TYPE_CODE")))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户存在无手机魔百和退库未完工工单！");
				}
				if("4909".equals(tradeInfo.getString("TRADE_TYPE_CODE")))
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户存在无手机魔百和管理未完工工单！");
				}
			}
        }
        /*
         * 处理机顶盒信息
         */
        IData resInfo = new DataMap();
        
        IData boxInfo=boxInfos.first();
        String resKindCode = boxInfo.getString("RES_CODE");
        resInfo.put("RES_ID", boxInfo.getString("IMSI"));
        resInfo.put("OLD_RESNO", boxInfo.getString("IMSI")); // 老终端号
        resInfo.put("RES_NO", boxInfo.getString("IMSI")); // 老终端号 -- 为了不换机顶盒校验用的【终端串一致】
        resInfo.put("RES_BRAND_NAME", boxInfo.getString("RSRV_STR4").split(",")[0]);
        resInfo.put("RES_KIND_NAME", boxInfo.getString("RSRV_STR4").split(",")[1]);
        resInfo.put("RES_STATE_NAME", "已销售");
        resInfo.put("RES_FEE", boxInfo.getString("RSRV_NUM5"));
        resInfo.put("RES_SUPPLY_COOPID", boxInfo.getString("KI"));
        resInfo.put("RES_TYPE_CODE", boxInfo.getString("RES_TYPE_CODE"));
        resInfo.put("RES_KIND_CODE", resKindCode);
        resInfo.put("PRODUCTS", boxInfo.getString("RSRV_STR1"));
        resInfo.put("BASEPACKAGES", boxInfo.getString("RSRV_STR2"));
        resInfo.put("OPTIONPACKAGES", boxInfo.getString("RSRV_STR3"));
        resInfo.put("ARTIFICIAL_SERVICES", boxInfo.getString("RSRV_NUM1","0").equals("0")?"否":"是");
        
        resInfo.put("IS_HAS_FEE", "0");	//默认是没收押金
        String rsrvNum2=boxInfo.getString("RSRV_NUM2","");
        
		if(rsrvNum2!=null&&!rsrvNum2.equals(""))
		{
			int rsrvNum2Int=Integer.parseInt(rsrvNum2);
			if(rsrvNum2Int>0)
			{
				resInfo.put("IS_HAS_FEE", "1");	//收取了押金信息
			}
		}
        
        result.put("RES_INFO", resInfo);
        
        IDataset analyseResult=new DatasetList();
        analyseResult.add(result);
        
		return analyseResult;
	}
	
	//根据USER_ID 获取147手机号码
    public IData getRelaUUInfoByUserIdB(String userIdB) throws Exception
    {
        IDataset relaUUInfos = RelaUUInfoQry.getAllRelationByUidBRelaTypeRoleB(userIdB,"47","1");
        if(IDataUtil.isEmpty(relaUUInfos))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户未开通无手机魔百和业务！"); 
        }
        return relaUUInfos.first();
    }
}
