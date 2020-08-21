
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.nfcpinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.plat.PlatUtil;

/**
 * @CREATED by gongp@2013-10-9 修改历史 Revision 2013-10-9 下午05:18:12
 */
public class QryPlatSvcBean extends CSBizBean
{

    public IDataset qryNFCPInfo(IData param) throws Exception
    {

        String serialNumber = param.getString("SERIAL_NUMBER");
        if (serialNumber == null || "".equals(serialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_172);
        }

        // 第一步，查询用户信息
        IData users = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(users))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_906);
        }

        IData ibossparam = new DataMap();//
        ibossparam.put("KIND_ID", "BIP2B783_T2001783_0_0");
        ibossparam.put("MSISDN", param.getString("SERIAL_NUMBER"));
        ibossparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        ibossparam.put("IDTYPE", "01");// 标识类型:01-手机号码
        ibossparam.put("BIZ_TYPE", "73");
        ibossparam.put("OPR_NUMB", PlatUtil.getOperNumb(SeqMgr.getTradeId()));

        /*
         * ibossparam.put("TRADE_EPARCHY_CODE", this.getVisit().getStaffEparchyCode());//交易地州编码
         * ibossparam.put("TRADE_CITY_CODE", this.getVisit().getCityCode());//交易城市代码
         * ibossparam.put("TRADE_DEPART_ID",this.getVisit().getDepartId());//员工部门编码 ibossparam.put("STAFF_ID",
         * this.getVisit().getStaffId());//员工编码
         * ibossparam.put(Route.ROUTE_EPARCHY_CODE,CSBizBean.getTradeEparchyCode());//路由地州编码
           ibossparam.put("ROUTETYPE", "00");// 路由类型 00-省代码，01-手机号
           ibossparam.put("ROUTEVALUE", "998");*/

        return IBossCall.callHttpIBOSS6("IBOSS", ibossparam);

    }

    public IDataset qryPayState(IData param) throws Exception
    {

        String serialNumber = param.getString("SERIAL_NUMBER");
        if (serialNumber == null || "".equals(serialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_172);
        }

        // 第一步，查询用户信息
        if ("1".equals(param.getString("QueryPayState_TYPE")))
        {
            IData users = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
            if (IDataUtil.isEmpty(users))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_906);
            }
        }

        IData ibossparam = new DataMap();//
        ibossparam.put("KIND_ID", "BIP1C005_X8000005_0_0");
        ibossparam.put("MSISDN", param.getString("SERIAL_NUMBER"));
        ibossparam.put("ReqType", param.getString("QueryPayState_TYPE"));
        ibossparam.put("ChgDate", Utility.decodeTimestamp("yyyyMMdd", param.getString("PAY_DATE")));
        ibossparam.put("TRANS_ID", SysDateMgr.getSysTime().concat(param.getString("SERIAL_NUMBER")));
        ibossparam.put("ACCEPT_DATE", SysDateMgr.getSysDate());
        ibossparam.put("TRADE_EPARCHY_CODE", getVisit().getStaffEparchyCode());// 交易地州编码
        ibossparam.put("TRADE_CITY_CODE", getVisit().getCityCode());// 交易城市代码

        ibossparam.put("TRADE_DEPART_ID", getVisit().getDepartId());// 员工部门编码
        ibossparam.put("STAFF_ID", getVisit().getStaffId());// 员工编码
        ibossparam.put("TRADE_DEPART_PASSWD", "");// 渠道接入密码
        ibossparam.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());// 路由地州编码
        ibossparam.put("ROUTETYPE", "00");// 路由类型 00-省代码，01-手机号
        ibossparam.put("ROUTEVALUE", "000");

        return IBossCall.dealIboss(getVisit(), ibossparam, "BIP1C005_X8000005_0_0");

    }
    public static IDataset queryFamilyCircle(IData param) throws Exception
    {
    	IData inparam = new DataMap();
		inparam.put("SUBSYS_CODE", "CSM");
		inparam.put("PARAM_ATTR", "6995");
		inparam.put("PARAM_CODE", "3");
		inparam.put("EPARCHY_CODE",getVisit().getStaffEparchyCode());

		IDataset timeInfo = Dao.qryByCodeParser("TD_S_COMMPARA",
				"SEL_BY_ATTRPARAM_CODE", inparam, Route.CONN_CRM_CEN);
		String service_id = "";
		IDataset userSvc = new DatasetList();
		
		//param.put("EPARCHY_CODE",getVisit().getStaffEparchyCode());

		boolean flag = true;
		if (null != param.getString("USER_ID") && !"".equals(param.getString("USER_ID"))){
			StringBuilder sql = new StringBuilder(
					"SELECT A.PARTITION_ID,TO_CHAR(A.USER_ID) USER_ID,A.SERVICE_ID,A.BIZ_STATE_CODE " +
					"FROM TF_F_USER_PLATSVC A WHERE A.USER_ID = :USER_ID" +
					" AND A.PARTITION_ID = MOD(:USER_ID, 10000) AND SYSDATE BETWEEN A.START_DATE AND A.END_DATE");
			userSvc = Dao.qryBySql(sql, param);
			
		}else {
			flag = false;
		}

		
		if (timeInfo.size() > 0)
		{
			for(int i = 0 ; i < timeInfo.size() ; i++){
				service_id = timeInfo.getData(i).getString("PARA_CODE1");
				for(int j = 0 ; j < userSvc.size() ; j++){
					if (service_id.equals(userSvc.getData(j).getString("SERVICE_ID"))){
						
						IDataset datas = UpcCall.querySpServiceAndProdByCond(null, null, null, service_id);
						if(IDataUtil.isNotEmpty(datas))
						{
							IData temp = datas.getData(0);
							userSvc.getData(j).put("SP_CODE", temp.getString("SP_CODE"));
							userSvc.getData(j).put("BIZ_CODE", temp.getString("BIZ_CODE"));
							userSvc.getData(j).put("BIZ_TYPE_CODE", temp.getString("BIZ_TYPE_CODE"));
							userSvc.getData(j).put("PRODUCT_NO", temp.getString("PRODUCT_NO"));
							userSvc.getData(j).put("ORG_DOMAIN", temp.getString("ORG_DOMAIN"));
							userSvc.getData(j).put("OPR_SOURCE", temp.getString("OPR_SOURCE"));
						}
						
						flag = false;
						break;
					}
				}
				if (!flag){break;}
			}
		} else
		{
			CSAppException.appError("6995", "亲情圈服务参数配置异常");
			return null;
		}
		if (flag){
			CSAppException.appError("6995", "用户暂未订购亲情圈服务，无法进行亲情圈成员管理");
			return null;
		}
		
		IDataset result = UserPlatSvcInfoQry.queryFamilyCircle(param);
		return result;
    }
    
    public IDataset synFamilyCircleInfoForIBoss(IData param) throws Exception{
        IDataset resultlist = new DatasetList();
        IData result = new DataMap();
        boolean succFlag = this.callIBoss(param);
        
        //2、设置回调结果
    	if(succFlag)
    	{
    		result.put("result", "true");  //0代表成功
    	}
    	else
    	{
    		result.put("result", "false"); //1代表失败
    	}
    	
    	resultlist.add(result);
    	return resultlist;
    }

    public static IDataset querySafeGroup(IData param) throws Exception
    {
		return UserPlatSvcInfoQry.querySafeGroup(param);
    }
    
	public boolean callIBoss(IData inparam)throws Exception{
		IData inData=new DataMap();
		IDataset result=new DatasetList();
		
		String errStr="";
		try
		{
			inData.put("PKG_SEQ", this.getProvCode()+"BIP2B153"+SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT)+SeqMgr.getPlanFamilyCircleSeq());	//交易包流水号
			inData.put("ROUTETYPE", "00");
			inData.put("ROUTEVALUE", "000");
			inData.put("KIND_ID","BIP2B153_T2001050_0_0");//交易唯一标识
			inData.put("X_TRANS_CODE","");//交易编码-IBOSS

			inData.put("ID_TYPE", "01");
			inData.put("ID_VALUE", inparam.getString("SERIAL_NUMBER"));
			inData.put("SERIAL_NUMBER", inparam.getString("SERIAL_NUMBER"));
			inData.put("USER_LIST", inparam.getDataset("USER_LIST"));		//同步的操作列表
			result=IBossCall.callHttpIBOSS("IBOSS",inData);
			
			if ((result == null))
	        {
	            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
	        }
	        if (result.isEmpty())
	            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
	        if (result.size() < 1)
	            CSAppException.apperr(CrmCardException.CRM_CARD_233);// 一级BOSS未返回
	        if (!"0000".equals(result.getData(0).getString("X_RSPCODE", "")))
	        {
	            CSAppException.apperr(CrmCommException.CRM_COMM_103, result.getData(0).getString("X_RSPDESC", ""));
	        }
	        if(result!=null && "0".equals(result.getData(0).getString("X_RESULTCODE","")))
			{
				return true;
			}
		}
		catch(Exception e)
		{
				errStr=e.toString();
		}
		return false;
	}
	

    public String getProvCode() throws Exception
    {
        String provCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
        { "PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode() });

        if (provCode == null || provCode.length() == 0)
        {
            // common.error("查询省代码无资料！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询省代码无资料！");
        }
        return provCode;
    }
}
