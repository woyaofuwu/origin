package com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.staffdept.StaffDeptInfoQry;

public class SynStaff4Boss { 
	private final static Logger loger = Logger.getLogger(SynStaff4Boss.class);
	/**
	 * REQ201506170019   两级界面的跨省集团处理的用户信息自动同步需求 
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static void synSysDataRight4BBOSS(IData input) throws Exception{
		//IDataset dataset = StaffDeptInfoQry.getBbossStaffId();
		//if(null!=dataset&&dataset.size()>0){
		String staffId = String.valueOf(input.get("STAFF_ID"));//默认只处理一条，AEE那边循环再调
		IData tdMStaff=new DataMap();
		tdMStaff.put("RSRV_TAG2", "1");
		//String staffId = String.valueOf(dataValue.get("STAFF_ID"));
		tdMStaff.put("STAFF_ID", staffId);
		StaffDeptInfoQry.saveTdMStaff(tdMStaff);
		IData staffValue =StaffDeptInfoQry.getTdMStaffValue(tdMStaff);
		if(null!=staffValue&&staffValue.size()>0){
			staffValue.put("NEED_REMIND", "1");//是否需要总部发起的待办短信提醒，1-需要，2-不需要
			staffValue.put("BBOSS_ROLE", "1_3");
			staffValue.put("CITY_CODE", staffValue.getString("CITY_CODE"));
			staffValue.put("DEPART_ID", staffValue.getString("DEPART_ID"));
			staffValue.put("EMAIL", staffValue.getString("EMAIL"));
			createBBossStaff(staffValue);
		}else{

		}
	}
	
	/**
	 * REQ201506010007     自动同步全网集团的客户经理的跨省业务处理权限
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public static void synSysDataRight5BBOSS(IData input) throws Exception{
		//IDataset dataset = StaffDeptInfoQry.getBbossStaffId();
		//if(null!=dataset&&dataset.size()>0){
		String staffId = String.valueOf(input.get("CUST_MANAGER_ID"));//默认只处理一条，AEE那边循环再调
		IData tdMStaff=new DataMap();
		tdMStaff.put("RSRV_TAG2", "1");
		//String staffId = String.valueOf(dataValue.get("STAFF_ID"));
		tdMStaff.put("STAFF_ID", staffId);
		StaffDeptInfoQry.saveTdMStaff(tdMStaff);
		IData staffValue =StaffDeptInfoQry.getTdMStaffValue(tdMStaff);
		if(null!=staffValue&&staffValue.size()>0){
			staffValue.put("NEED_REMIND", "1");//是否需要总部发起的待办短信提醒，1-需要，2-不需要
			staffValue.put("BBOSS_ROLE", "1_3");
			staffValue.put("CITY_CODE", staffValue.getString("CITY_CODE"));
			staffValue.put("DEPART_ID", staffValue.getString("DEPART_ID"));
			staffValue.put("EMAIL", staffValue.getString("EMAIL"));
			createBBossStaff(staffValue);
		}else{

		}
	}
	
	public static void createBBossStaff(IData data) throws Exception{   
		IData bbossStaff=new DataMap();
		bbossStaff.put("STAFF_NUMBER","898"+data.getString("STAFF_ID"));//总部用户
		bbossStaff.put("STAFF_ID",data.getString("STAFF_ID"));
		bbossStaff.put("STAFF_NAME",data.getString("STAFF_NAME"));
		bbossStaff.put("MOBILE",data.getString("SERIAL_NUMBER"));
		bbossStaff.put("DEPART_ID",data.getString("DEPART_ID"));
		bbossStaff.put("CITY_CODE",data.getString("CITY_CODE"));
		bbossStaff.put("EMAIL",data.getString("EMAIL"));
		bbossStaff.put("COMPANY_ID","898");
		bbossStaff.put("ROLE_CODE",data.getString("BBOSS_ROLE"));
		String eparchyCode=data.getString("EPARCHY_CODE");
		String staffType="2";
		if(data.getString("CITY_CODE").equals("HNSJ")){
			staffType="1";
		} 
		bbossStaff.put("STAFF_TYPE",staffType);   
		if("2".equals(staffType)){
			bbossStaff.put("EPARCHY_CODE",eparchyCode);
		}
		bbossStaff.put("NEED_REMIND", data.getString("NEED_REMIND","2"));
		bbossStaff.put("NOTES", data.getString("NOTES",""));

		bbossStaff.put("UPDATE_STAFF_ID", "SUPERUSR");
		bbossStaff.put("UPDATE_DEPART_ID", "36601");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		bbossStaff.put("UPDATE_TIME",sdf.format(new Timestamp(System.currentTimeMillis())));
		bbossStaff.put("RSRV_TAG1", "1");//工号状态
		StaffDeptInfoQry.deleteTdMStaffValue(data.getString("STAFF_ID"));//先删除2失效的数据
		StaffDeptInfoQry.addTdMStaffValue(bbossStaff);

		synStaff(bbossStaff,"1");
	}
	/**
	 * 同步工号
	 * @param staffInfo
	 * @return
	 * @throws Exception
	 */
	public static void synStaff(IData bbossStaff,String operType) throws Exception{
		IData inparam = new DataMap();
		inparam.put("ROUTETYPE", "00");
		inparam.put("ROUTEVALUE","000");
		inparam.put("ACTION", operType);//1-新增，2-修改，3-删除
		inparam.put("STAFF_NUMBER",bbossStaff.getString("STAFF_NUMBER"));//总部用户名
		inparam.put("COMPANY_ID",bbossStaff.getString("COMPANY_ID"));//用户所属省
		inparam.put("MOBILE",bbossStaff.getString("MOBILE"));///用户手机号码
		String roleCode=bbossStaff.getString("ROLE_CODE");
		String[] roleList=roleCode.split("_");
		IDataset roles=new DatasetList();
		for(int i=0;i<roleList.length;i++){
			roles.add(roleList[i]);
		}
		inparam.put("ROLE",roles);//角色
		inparam.put("TYPE",bbossStaff.getString("STAFF_TYPE"));//用户类型
		String location = "";
		String staffid = bbossStaff.getString("STAFF_ID"); 
		String citycode = bbossStaff.getString("CITY_CODE","HNHK"); 
		IData locations = changeLocation(citycode);
		//Log.debug("locations:"+locations);
		if(locations != null){
			location = locations.getString("DATA_ID","8980");
		}else {
			location = "8980";
		} 
		inparam.put("LOCATION",location);//用户所属地市
		inparam.put("NEED_REMIND",bbossStaff.getString("NEED_REMIND"));//是否需要总部发起的待办短信提醒
		String notes=bbossStaff.getString("NOTES","");
		if("".equals(notes)){
			notes="两级联调";
		}
		inparam.put("NOTES",notes);//备注
		inparam.put("DISPLAY_NAME",bbossStaff.getString("STAFF_NAME"));//用户姓名,二期新增该字段  bbossStaff.getString("STAFF_NAME")
		// 集客大厅需求新增部分 start xiecx2
		String departId = bbossStaff.getString("DEPART_ID");
		inparam.put("COUNTRY_ID", bbossStaff.getString("CITY_CODE"));
		inparam.put("COUNTRY_NAME", StaticUtil.getStaticValue("COP_AREA_CODE", bbossStaff.getString("CITY_CODE")));
		inparam.put("EMAIL", bbossStaff.getString("EMAIL"));
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String now = df.format(new Date());
		inparam.put("EFF_TIME", now);
		inparam.put("EXP_TIME", "20991231");
		//新增拼装staff_info参数
		IData newInparam= new DataMap(); 
		IDataset realStaffinfos =new DatasetList();
		realStaffinfos.add(inparam);
		newInparam.put("STAFF_INFO", realStaffinfos);
		// 集客大厅需求新增部分 end xiecx2
		newInparam.put("KIND_ID","StaffInfoService_BBOSS_0_0");
		newInparam.put("X_TRANS_CODE", "IBOSS");
		newInparam.put("TRADE_EPARCHY_CODE", "0898");
		newInparam.put("TRADE_CITY_CODE", "HNSJ");
		newInparam.put("ROUTE_EPARCHY_CODE", "0898");
		newInparam.put("X_TRANS_TYPE", "HTTP");
		newInparam.put("TRADE_STAFF_ID", "SUPERUSR");
		newInparam.put("PROVINCE_CODE", "HAIN");
		newInparam.put("TRADE_DEPART_ID", "36601");    
		newInparam.put("syn_SysDataRight4BBOSS", "1111");//两级界面的跨省集团处理的用户信息自动同步 处理标志
		newInparam.put("ORDER_ID", "898"+new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+getStaffNumber());//两级界面的跨省集团处理的用户信息自动同步 处理标志
		try{
			IDataset resultData = IBossCall.callHttpIBOSS4SynBossStaffId("StaffInfoService_BBOSS_0_0",newInparam);//CSAppCall.call("CS.BBossTaskSVC.synSysDataRight4BBOSS", inparam);
			if (resultData != null) {
				String resultCode =resultData.first().getString("X_RESULTCODE");
				String reslutInfo=resultData.first().getString("X_RESULTINFO");
				if (!"0".equals(resultCode)) {
					if("01".equals(resultCode)){
						loger.error("调用跨省业务处理的用户信息同步接口同步员工失败！请求报文内容错误或重复。"+reslutInfo);
					}else if("99".equals(resultCode)){
						loger.error("调用跨省业务处理的用户信息同步接口同步员工失败！其他错误。" + reslutInfo);
					}else{
						loger.error("调用跨省业务处理的用户信息同步接口同步员工失败！" + reslutInfo);
					}
				}else{
					loger.error("调用跨省业务处理的用户信息同步接口同步员工成功！");
				}
			} else {
				//throw new Exception("调用跨省业务处理的用户信息同步接口同步员工失败！");
				loger.error("调用跨省业务处理的用户信息同步接口同步员工失败！");
			}
		}catch(Exception e){
			throw e;
		}
	}
	
	/**
	 * 获取同步省BOSS的归属地编码
	 * @return
	 * @throws Exception
	 */
	public static IData changeLocation(String city_code) throws Exception{
		/*select *
		  from td_s_static a, td_s_static b
		 where a.type_id = 'COP_SI_SERVICE_CITY'
		   and b.type_id = 'COP_AREA_CODE'
		   and a.data_name=b.data_name
		   and b.data_id='HNSY';*/
		IData param = new DataMap();
		param.put("CITY_CODE", city_code);
		SQLParser parser = new SQLParser(param);
		parser.addSQL(" select * from td_s_static a, td_s_static b ");
		parser.addSQL(" where 1 = 1");
		parser.addSQL(" and  a.type_id = 'COP_SI_SERVICE_CITY'  and b.type_id = 'COP_AREA_CODE' ");
		parser.addSQL(" and a.data_name=b.data_name ");
		parser.addSQL(" and b.data_id= :CITY_CODE ");
		IDataset data = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
		return data.size()>0?data.getData(0):null;
	}
	/**
	 * 获取总部用户名
	 * @param provCode
	 * @return
	 * @throws Exception
	 */
	public static String getStaffNumber()throws Exception{
		String sql="select lpad(SEQ_BBOSS_ORDER.nextval,3,'0') SEQ_ID from dual";
		SQLParser parser = new SQLParser(null);
		parser.addSQL(sql);
		IDataset dataset = Dao.qryByParse(parser,Route.CONN_SYS);
		IData data = (IData) dataset.get(0);
		return (String) data.get("SEQ_ID");
	}
}
