package com.asiainfo.veris.crm.order.soa.person.busi.onecardmultino;


import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import org.apache.log4j.Logger;

import com.ailk.bizcommon.route.Route;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.OneCardMultiNoQry;


public class OneCardMultiNoSVC extends CSBizService {
    private static final long serialVersionUID = 2866237306813883199L;

    private final static transient Logger logger = Logger.getLogger(OneCardMultiNoSVC.class);

	/**
	 * 海南本地化改造 获取副号码池 给前台选择
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset qrySubSerialNumber(IData input) throws Exception {
		IDataset resultList = new DatasetList();
		IDataset followList = new DatasetList();
		//判断搜索字段是否为空（新增）
		String startNum = input.getString("START_NUM");
		IData resultData = new DataMap();
		try{
			//不为空的话走搜索参数方法（新增）
			if(StringUtils.isNotBlank(startNum)){
				resultList = ResCall.getSubPhone(input.getString("EPARCHY_CODE"),input.getString("START_NUM"));
			}else{
				resultList = ResCall.getSubPhone(input.getString("EPARCHY_CODE"));
			}
			
		} catch (Exception e){
		}
		if(IDataUtil.isNotEmpty(resultList)){
			resultData = resultList.getData(0);
			followList = resultData.getDataset("OUTDATA");
		}
		return followList;
	}
	/**
	 *
	 * @param input 未被选择的前台下拉框里面的号码
	 * @throws Exception
	 */

	public void releaseVirtualUseNum(IData input) throws Exception{
		ResCall.releaseVirtualUseNum(input);
	}
    /**
     * 受理、取消接口：ITF_CRM_OneCardMosp
     */
    public IDataset relationApplyOrCancel(IData input) throws Exception {
        IDataset resultList = new DatasetList();
        //参数处理
        isEmptyData(input, "IN_MODE_CODE");// 接入编码
        input.put("TRADE_EPARCHY_CODE", "0898");
        isEmptyData(input, "TRADE_EPARCHY_CODE");// 工号所在地州路由
        isEmptyData(input, "TRADE_CITY_CODE");// 工号所在地州
        isEmptyData(input, "TRADE_DEPART_ID");// 操作部门
        isEmptyData(input, "TRADE_STAFF_ID");// 操作工号
//		isEmptyData(input, Route.ROUTE_EPARCHY_CODE);// 业务号码归属的地州

        isEmptyData(input, "MSISDN");// 主号码
        isEmptyData(input, "FOLLOW_MSISDN");// 副号码
        isEmptyData(input, "SEQ");// 本次操作的流水号
        isEmptyData(input, "OPR_CODE");// 操作代码 06-副号码申请 07-取消副号码
        isEmptyData(input, "BIZ_TYPE");// 业务类型代码 74-国内一卡多号业务
        isEmptyData(input, "CHANNEL_ID");// 受理渠道  01-WEB， 03-WAP，04-SMS，70-客户端
        isEmptyData(input, "CHRG_TYPE");// 计费类型 0-免费 1-按次  2-包月
        isEmptyData(input, "CATEGORY");// 副号码类型 0：虚拟副号码；1：实体副号码
//      isEmptyData(input, "SERIAL_NUM");// 表示副号码序列号 数字1－3
//		isEmptyData(inputData, "EFFECTIVE_TIME");// 副号码配置生效日期和时间
        isEmptyData(input, "SPID");// SP企业代码
        isEmptyData(input, "BIZ_CODE");// SP业务代码

        String serial_number = input.getString("MSISDN"); // 主号码
        String follow_msisdn = input.getString("FOLLOW_MSISDN"); // 副号码
        input.put("ORDERNO", input.getString("SERIAL_NUM","")); // 副号码序号

        IData callResult = new DataMap();
        IData resultData = new DataMap();
        resultData.put("X_RESULTCODE", "0");
        resultData.put("MSISDN", serial_number);
        resultData.put("FOLLOW_MSISDN", follow_msisdn);
        resultData.put("SEQ", input.getString("SEQ"));
        input.put("SERIAL_NUMBER", serial_number);
        input.put("SERIAL_NUMBER_B", follow_msisdn);
        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        if (OneCardMultiNoBean.OPER_CODE_CANCEL.equals(input.getString("OPR_CODE")) && "".equals(serial_number) && "0".equals(input.getString("CATEGORY", "0"))) {
        	resultData.put("X_RSPTYPE", "2");
            resultData.put("X_RSPCODE", "2998");
            resultData.put("X_RESULTCODE", "-1");
            resultData.put("X_RESULTINFO", "【" + follow_msisdn + "】是虚拟副号码，不能发起业务取消！");
            resultList.add(resultData);
            return resultList;
        }
        //三户资料  主体服务状态校验
        callResult = bean.isNormalMainService(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //主号是否为实名制用户
        callResult = bean.isRealNameUser(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //申请校验重复绑定
        if (OneCardMultiNoBean.OPER_CODE_APPLY.equals(input.getString("OPR_CODE"))) {
            IDataset relationList = OneCardMultiNoQry.qryRelationListNew(input.getString("USER_ID", ""), OneCardMultiNoBean.RELATION_TYPE_CODE, follow_msisdn);
//            IDataset relationList=OneCardMultiNoQry.qryRelationList(input.getString("USER_ID",""),OneCardMultiNoBean.RELATION_TYPE_CODE,follow_msisdn,input.getString("SERIAL_NUM"));
            if (null != relationList && !relationList.isEmpty()) {
                if (2 <= relationList.size()) {
                    resultData.put("X_RSPTYPE", "2");
                    resultData.put("X_RSPCODE", "2998");
                    resultData.put("X_RSPDESC", "已存在订购关系");
                    resultData.put("X_RESULTCODE", "2000");
                    resultData.put("X_RESULTINFO", "已存在订购关系");
                    resultList.add(resultData);
                    return resultList;
                }
                if (relationList.size() == 1) {
                    String service_id = relationList.getData(0).getString("RSRV_STR3");
                    String sp_code = input.getString("SPID", "");
                    String biz_code = input.getString("BIZ_CODE", "");
                    IData inparams = new DataMap();
                    inparams.put("SP_CODE", sp_code);
                    inparams.put("BIZ_CODE", biz_code);
                    //TODO huanghua 42 与产商品解耦---已解决
//                    IDataset serviceInfo = Dao.qryByCode("TD_B_PLATSVC", "SEL_BY_SP_BIZ_CODE", inparams);
            		IDataset serviceInfo = PlatInfoQry.getBizInfo(inparams);
                    if (IDataUtil.isEmpty(serviceInfo)) {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "未找到相应SPID,BIZ_CODE所对应的服务！");
                    }
                    String service_id_new = serviceInfo.getData(0).getString("OFFER_CODE");
                    if (service_id_new.equals(service_id)) {
                        resultData.put("X_RSPTYPE", "2");
                        resultData.put("X_RSPCODE", "2998");
                        resultData.put("X_RSPDESC", "已存在订购关系");
                        resultData.put("X_RESULTCODE", "2000");
                        resultData.put("X_RESULTINFO", "已存在订购关系");
                        resultList.add(resultData);
                        return resultList;
                    }
                }
            }
            UcaData ucaA = UcaDataFactory.getNormalUca(serial_number);
        	String snaName = ucaA.getCustPerson().getCustName();
        	String snaIdType = ucaA.getCustPerson().getPsptTypeCode();
        	if("1".equals(snaIdType))//如果是外地身份证，则转化为本地身份证
        	{
        		snaIdType ="0";
        		
        	}
        	String snaId = ucaA.getCustPerson().getPsptId();
        	
         // 绑定虚拟副号码,如果为申请虚拟副号码 // 主号码就需要走一证五号查验
            if ("0".equals(input.getString("CATEGORY"))) 
            {
//            	bean.idCheck(input);//续订不校验一证五号
				logger.debug("----------relationApplyOrCancel----serialNumber:"+serial_number+";--follow_msisdn:"+follow_msisdn);

				if(IDataUtil.isEmpty(relationList)) {
					bean.idCheck(input);
					
					String snbName = input.getString("MSISDN_NAME");
					String snbIdType = input.getString("IDCARD_TYPE");
					String snbIdValue = input.getString("IDCARD_NUM");

					if (!StringUtils.equals(snaName, snbName) || !StringUtils.equals(snaIdType, this.encodeIdType(snbIdType)) || !StringUtils.equals(snaId, snbIdValue)) {
						resultData.put("X_RSPTYPE", "2");
						resultData.put("X_RSPCODE", "2998");
						resultData.put("X_RSPDESC", "主副号码实名制信息不一致");
						resultData.put("X_RESULTCODE", "3040");
						resultData.put("X_RESULTINFO", "主副号码实名制信息不一致");
						resultList.add(resultData);
						return resultList;
					}
				}
            }
            //实体副号码实名制校验
            if ("1".equals(input.getString("CATEGORY"))) 
            {

            	IData snbInfo = UcaInfoQry.qryUserInfoBySn(follow_msisdn);
                if(IDataUtil.isNotEmpty(snbInfo))//本省副号码
                {            	
                	UcaData ucaB = UcaDataFactory.getNormalUca(follow_msisdn);
                	
                	if(!"0".equals(ucaB.getUser().getUserStateCodeset()))
                	{
                		resultData.put("X_RSPTYPE", "2");
                        resultData.put("X_RSPCODE", "2998");
                        resultData.put("X_RSPDESC", "实体副号码用户状态非法");
                		resultData.put("X_RESULTCODE", "2009");
                		resultData.put("X_RESULTINFO", "实体副号码用户状态非法");
                		resultList.add(resultData);
                        return resultList;
                	}
                	
                	String snbName = ucaB.getCustPerson().getCustName();
                	String snbIdType = ucaB.getCustPerson().getPsptTypeCode();
                	if("1".equals(snaIdType))//如果是外地身份证，则转化为本地身份证
                	{
                		snaIdType ="0";
                		
                	}
                	String snbId = ucaB.getCustPerson().getPsptId();
                	
                	if(!StringUtils.equals(snaName, snbName) || !StringUtils.equals(snaIdType, snbIdType) || !StringUtils.equals(snaId, snbId))
            		{
                		resultData.put("X_RSPTYPE", "2");
                        resultData.put("X_RSPCODE", "2998");
                        resultData.put("X_RSPDESC", "主副号码实名制信息不一致");
                		resultData.put("X_RESULTCODE", "3040");
                		resultData.put("X_RESULTINFO", "主副号码实名制信息不一致");
                        resultList.add(resultData);
                        return resultList;
            		}
                }
                else//外省走IBOSS
                {
                	IData inParam = new DataMap();
                	inParam.put("SERIAL_NUMBER", serial_number);
                	inParam.put("MSISDN_NAME", snaName);
                	inParam.put("IDCARD_TYPE", LanuchUtil.decodeIdType2(snaIdType));
                	inParam.put("IDCARD_NUM", snaId);
                	inParam.put("MPROVINCE", OneCardMultiNoBean.PROV_CODE);
                	inParam.put("CHANNEL", input.getString("CHANNEL_ID"));
                	inParam.put("FOLLOW_MSISDN", input.getString("FOLLOW_MSISDN"));
                	inParam.put("ROUTEVALUE", input.getString("FOLLOW_MSISDN"));
                	inParam.put("ROUTETYPE", "01");
                	inParam.put("CATEGORY", input.getString("CATEGORY"));
                	inParam.put("PSEQ", input.getString("SEQ"));
                	inParam.put("SEQ", input.getString("SEQ"));
                	
                	inParam.put("KIND_ID", "BIP5A011_T5000004_0_0");// 接口标识
            		IDataset results = IBossCall.callHttpIBOSS7("IBOSS", inParam);
            		if(IDataUtil.isEmpty(results))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP5A011_T5000004_0_0！");
                    }
                    IData tmpData = results.getData(0);
                    if (!StringUtils.equals("0", tmpData.getString("X_RESULTCODE"))) {
                    	resultData.put("X_RSPTYPE", "2");
                        resultData.put("X_RSPCODE", "2998");
                        resultData.put("X_RSPDESC", "主副号码实名制信息不一致");
                        resultData.put("X_RESULTCODE", "3040");
                		resultData.put("X_RESULTINFO", "主副号码实名制信息不一致");
                		realCardResult(resultData);
                        resultList.add(resultData);
                        return resultList;
                    }
                    else
                    {
                    	resultData.put("X_RESULTCODE", "0");
                		resultData.put("X_RESULTINFO", "主副号码实名制信息一致");
                		this.realCardResult(resultData);
                    }
                }
            }
            input.put("FLAG", "2");
        }
        
        
     // --------------------------------------------如果为补录操作---------------------
 		if ("08".equals(input.getString("OPR_CODE"))) {
 			// 判断虚拟副号码是否同省,
 			String followddr = input.getString("ADDRESS");
 			IData virUserInfo = UcaInfoQry.qryUserInfoBySn(follow_msisdn);
 			
 			
 			if (IDataUtil.isNotEmpty(virUserInfo)) {
 				// 判断副号码客户信息是否存在，即判断副号码信息是否已经同步
 				// 如果为虚拟副号码
 				if ("0".equals(input.getString("CATEGORY"))) {
 					
 					// 则下面参数为必传
 					IDataUtil.chkParam(input, "MSISDN_NAME");// 副号码开户人姓名
 					IDataUtil.chkParam(input, "IDCARD_TYPE");// 副号码证件类型
 					IDataUtil.chkParam(input, "IDCARD_NUM");// 副号码证件号码
 					IDataUtil.chkParam(input, "ADDRESS");// 证件地址
 					IDataUtil.chkParam(input, "PIC_NAMET");// 用户头像图片文件名称
 					IDataUtil.chkParam(input, "PIC_NAMEZ");// 身份证正面图片文件名称
 					IDataUtil.chkParam(input, "PIC_NAMEF");// 身份证反面图片文件名称
 					String MSISDN_NAME = input.getString("MSISDN_NAME");
 					String IDCARD_TYPE = input.getString("IDCARD_TYPE");
 					String IDCARD_NUM = input.getString("IDCARD_NUM");
 					String PicNameT = input.getString("PIC_NAMET");
 					String PicNameZ = input.getString("PIC_NAMEZ");
 					String PicNameF = input.getString("PIC_NAMEF");
 					
 					String route = RouteInfoQry.getEparchyCodeBySn(serial_number);
					ViceRealInfoReRegBean vBean = BeanManager.createBean(ViceRealInfoReRegBean.class);
					IData inputparam = new DataMap();
					inputparam.put("SERIAL_NUMBER_B", follow_msisdn);
					IDataset bindInfo = vBean.qryHdhSynInfo(inputparam);
 					
					if(StringUtils.isNotBlank(route))
					{
						IData indata = new DataMap();
						indata.put("SERIAL_NUMBER_A", serial_number);
						indata.put("SERIAL_NUMBER_B", follow_msisdn);
						indata.put("RELATION_TYPE_CODE", "M2");
						
						SQLParser parser = new SQLParser(indata);
				        parser.addSQL("SELECT PARTITION_ID,USER_ID_A,SERIAL_NUMBER_A,USER_ID_B,SERIAL_NUMBER_B,RELATION_TYPE_CODE,ROLE_TYPE_CODE,ROLE_CODE_A,ROLE_CODE_B,ORDERNO,SHORT_CODE,INST_ID,to_char(START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE,to_char(UPDATE_TIME, 'yyyy-mm-dd hh24:mi:ss') UPDATE_TIME,UPDATE_STAFF_ID,UPDATE_DEPART_ID,REMARK,RSRV_NUM1,RSRV_NUM2,RSRV_NUM3,RSRV_NUM4,RSRV_NUM5,RSRV_STR1,RSRV_STR2,RSRV_STR3,RSRV_STR4,RSRV_STR5,RSRV_DATE1,RSRV_DATE2,RSRV_DATE3,RSRV_TAG1,RSRV_TAG2,RSRV_TAG3 FROM TF_F_RELATION_UU WHERE 1=1 ");
				        parser.addSQL(" AND RELATION_TYPE_CODE = :RELATION_TYPE_CODE");
				        parser.addSQL(" AND SERIAL_NUMBER_A = :SERIAL_NUMBER_A");
				        parser.addSQL(" AND SERIAL_NUMBER_B = :SERIAL_NUMBER_B");
				        parser.addSQL(" AND sysdate BETWEEN start_date AND end_date");
				        IDataset asynInfo = Dao.qryByParse(parser,route);
						
				        if(IDataUtil.isEmpty(asynInfo))
     					{
     						resultData.put("X_RESULTCODE", "-1");
         					resultData.put("X_RESULTINFO", "未查询到号码【"+follow_msisdn+"】的UU关系信息！");
         					resultList.add(resultData);
         					return resultList;
     					}
						
				        UcaData ucaA = UcaDataFactory.getNormalUca(serial_number);
						String snaName =  ucaA.getCustPerson().getCustName();
						String snaIdType =  ucaA.getCustPerson().getPsptTypeCode();
						String snaId = ucaA.getCustPerson().getPsptId();
						
						if(!StringUtils.equals(snaName, MSISDN_NAME) || !StringUtils.equals(LanuchUtil.decodeIdType2(snaIdType), IDCARD_TYPE) || !StringUtils.equals(snaId, IDCARD_NUM))
	            		{
	                		resultData.put("X_RSPTYPE", "2");
	                        resultData.put("X_RSPCODE", "2998");
	                        resultData.put("X_RSPDESC", "主副号码实名制信息不一致");
	                		resultData.put("X_RESULTCODE", "3040");
	                		resultData.put("X_RESULTINFO", "主副号码实名制信息不一致");
	                		resultList.add(resultData);
	                        return resultList;
	            		}
						
						
						IData custInfoChargeSys = new DataMap();
						custInfoChargeSys.put("CUST_NAME", snaName);
						custInfoChargeSys.put("PSPT_TYPE_CODE", snaIdType);
						custInfoChargeSys.put("PSPT_ID", snaId);
						custInfoChargeSys.put("ADDRESS", followddr);
						custInfoChargeSys.put("F_PICNAME_T", PicNameT);
						custInfoChargeSys.put("F_PICNAME_Z", PicNameZ);
						custInfoChargeSys.put("F_PICNAME_F", PicNameF);
						custInfoChargeSys.put("SERIAL_NUMBER", serial_number);
						custInfoChargeSys.put("SERIAL_NUMBER_B", follow_msisdn);
						
						if(IDataUtil.isNotEmpty(bindInfo))
    					{
    						vBean.updateViceAsynInfo(custInfoChargeSys);
    					}
    					else
    					{
    						custInfoChargeSys.put("SEQ_ID",SeqMgr.getOperId());
    						custInfoChargeSys.put("USER_ID",ucaA.getUserId());
    						custInfoChargeSys.put("SERIAL_NUMBER",ucaA.getSerialNumber());
    						custInfoChargeSys.put("OPR_CODE","08");
    						custInfoChargeSys.put("CUST_NAME",MSISDN_NAME);
    						custInfoChargeSys.put("PROVINCE_CODE","898");
    						custInfoChargeSys.put("CUST_TYPE","0");
    						custInfoChargeSys.put("CATEGORY","0");
    						custInfoChargeSys.put("PSPT_TYPE_CODE",snaIdType);
    						custInfoChargeSys.put("PSPT_ID",snaId);
    						custInfoChargeSys.put("ADDRESS",followddr);
    						custInfoChargeSys.put("BRAND_CODE","MOSP");
    						custInfoChargeSys.put("F_PROVINCE_CODE","898");
    						custInfoChargeSys.put("U_PARTITION_ID",StrUtil.getPartition4ById(follow_msisdn+"0"));
    						custInfoChargeSys.put("USER_ID_B",follow_msisdn+"0");
    						custInfoChargeSys.put("SERIAL_NUMBER_B",follow_msisdn);
    						custInfoChargeSys.put("RELATION_TYPE_CODE","M2");
    						custInfoChargeSys.put("ROLE_TYPE_CODE","0");
    						custInfoChargeSys.put("ROLE_CODE_A","1");
    						custInfoChargeSys.put("ROLE_CODE_B","2");
    						custInfoChargeSys.put("ORDERNO",asynInfo.getData(0).getString("ORDERNO"));
    						custInfoChargeSys.put("SHORT_CODE","");
    						custInfoChargeSys.put("INST_ID",asynInfo.getData(0).getString("INST_ID"));
    						custInfoChargeSys.put("U_START_DATE",asynInfo.getData(0).getString("START_DATE"));
    						custInfoChargeSys.put("U_END_DATE",asynInfo.getData(0).getString("END_DATE"));
    						custInfoChargeSys.put("START_DATE",asynInfo.getData(0).getString("START_DATE"));
    						custInfoChargeSys.put("END_DATE",asynInfo.getData(0).getString("END_DATE"));
    						custInfoChargeSys.put("IN_STAFF_ID",CSBizBean.getVisit().getStaffId());
    						custInfoChargeSys.put("IN_DEPART_ID",CSBizBean.getVisit().getDepartId());
    						custInfoChargeSys.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
    						custInfoChargeSys.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
    						custInfoChargeSys.put("PLAT_SEQ_ID",input.getString("SEQ"));
    						
    						vBean.addViceAsynInfo(custInfoChargeSys);
    					}
						
					}
					else
 					{
						if(IDataUtil.isNotEmpty(bindInfo))
 						{
							String snaName = bindInfo.getData(0).getString("CUST_NAME");
        		            String snaIdType = bindInfo.getData(0).getString("PSPT_TYPE_CODE");
        		            String snaId = bindInfo.getData(0).getString("PSPT_ID");
                        	
                        	if(!StringUtils.equals(snaName, MSISDN_NAME) || !StringUtils.equals(LanuchUtil.decodeIdType2(snaIdType), IDCARD_TYPE) || !StringUtils.equals(snaId, IDCARD_NUM))
                    		{
                        		resultData.put("X_RSPTYPE", "2");
    	                        resultData.put("X_RSPCODE", "2998");
    	                        resultData.put("X_RSPDESC", "主副号码实名制信息不一致");
    	                		resultData.put("X_RESULTCODE", "3040");
    	                		resultData.put("X_RESULTINFO", "主副号码实名制信息不一致");
    	                		resultList.add(resultData);
    	                        return resultList;
                    		}
 						}
						else
 						{
 							resultData.put("X_RESULTCODE", "-1");
 	     					resultData.put("X_RESULTINFO", "未找到主号码信息，无法补录！");
 	     					resultList.add(resultData);
	                        return resultList;
 						}
 					}
					vBean.changeViceRealInfo(follow_msisdn, followddr, MSISDN_NAME, this.encodeIdType(IDCARD_TYPE), IDCARD_NUM);
					// 插入主副信息同步表
					resultData.put("X_RESULTCODE", "0");
					resultData.put("X_RESULTINFO", "业务受理成功");
					resultList.add(resultData);
					return resultList;
 				} else {
 					// 调用反馈接口告诉和多号平台处理结果
 					resultData.put("X_RSPTYPE", "2");
                    resultData.put("X_RSPCODE", "2998");
                    resultData.put("X_RSPDESC", "实体副号码不允许补录操作");
 					resultData.put("X_RESULTCODE", "-1");
 					resultData.put("X_RESULTINFO", "实体副号码不允许补录操作");
 					resultList.add(resultData);
 					return resultList;
 				}

 			} else {
 				// 调用反馈接口告诉和多号平台处理结果
 				resultData.put("X_RSPTYPE", "2");
                resultData.put("X_RSPCODE", "2998");
                resultData.put("X_RSPDESC", "虚拟副号码不是本省号码");
 				resultData.put("X_RESULTCODE", "-1");
 				resultData.put("X_RESULTINFO", "虚拟副号码不是本省号码");
 				resultList.add(resultData);
 				return resultList;
 			}
 		}
 		// -------------------------------------------------------------------------------
        
        //取消校验是否存在
        if (OneCardMultiNoBean.OPER_CODE_CANCEL.equals(input.getString("OPR_CODE"))) {
            IDataset relationList = OneCardMultiNoQry.qryRelationList(input.getString("USER_ID"), OneCardMultiNoBean.RELATION_TYPE_CODE, follow_msisdn, input.getString("SERIAL_NUM"));
            if (null == relationList || relationList.isEmpty()) {
                resultData.put("X_RSPTYPE", "2");
                resultData.put("X_RSPCODE", "2998");
                resultData.put("X_RSPDESC", "订购关系不存在");
                resultData.put("X_RESULTCODE", "2001");
                resultData.put("X_RESULTINFO", "订购关系不存在");
                resultList.add(resultData);
                return resultList;
            }
            input.put("RELATION", relationList.getData(0));
            input.put("FLAG", "3");
        }
        callResult = CSAppCall.call("SS.OneCardMultiNoRegSVC.tradeReg", input).getData(0);
        resultData.putAll(callResult);

        //增加主号码开户人姓名与主号码归属省省代码
        UcaData uca = UcaDataFactory.getNormalUca(serial_number);
        resultData.put("MSISDN_NAME", uca.getCustomer().getCustName());
        resultData.put("MPROVINCE", OneCardMultiNoBean.PROV_CODE);
        if (OneCardMultiNoBean.OPER_CODE_APPLY.equals(input.getString("OPR_CODE"))) {//绑定外省实体副号码，通过BIP5A011_T5000005_0_0反馈
			if ("1".equals(input.getString("CATEGORY"))) {
				String route = RouteInfoQry.getEparchyCodeBySnForCrm(follow_msisdn);
                if(StringUtils.isBlank(route))//本省副号码
                {
                	resultData.put("X_RESULTCODE", "0");
                	resultData.put("X_RESULTINFO", "业务受理成功");
                	//this.realCardResult(resultData);
                	resultData.put("X_RESULTCODE", "0015");
                }
			}
		}
        resultList.add(resultData);
        return resultList;
    }
    
    /**
     * 实体副号码校验，落地方接口
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset realCardCheck(IData input) throws Exception {
    	
    	OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
		// 第二步校验参数非空
		isEmptyData(input, "SERIAL_NUMBER");// 主号码
		isEmptyData(input, "MSISDN_NAME");// 主号姓名
		isEmptyData(input, "IDCARD_TYPE");// 证件类型
		isEmptyData(input, "IDCARD_NUM");// 证件号码
		isEmptyData(input, "MPROVINCE");// 归属省
		isEmptyData(input, "CHANNEL");// 接入渠道
		isEmptyData(input, "FOLLOW_MSISDN");// 副号码
		isEmptyData(input, "CATEGORY");// 类型编号
		isEmptyData(input, "PSEQ");// 流水
		// 第三步获取参数
		String sna = input.getString("MSISDN");
		String snaName = input.getString("MSISDN_NAME");
		String snaIdType = input.getString("IDCARDTYPE");
		String snaIdNum = input.getString("IDCARDNUM");
		String snb = input.getString("FOLLOW_MSISDN");
		String pSeq = input.getString("PSEQ");
		IDataset resultList = new DatasetList();
		// 拼接返回参数
		IData resultData = new DataMap();
		resultData.put("MSISDN", sna);
		resultData.put("FOLLOW_MSISDN", snb);
		resultData.put("SEQ", pSeq);
		// 实体副号状态未停机或销户校验
		IData isNormal = new DataMap();
		isNormal.put("SERIAL_NUMBER", snb);
		
		UcaData ucaB = UcaDataFactory.getNormalUca(snb);
		String userStateCode = ucaB.getUser().getUserStateCodeset();
		
		if (!"0".equals(userStateCode)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "状态非正常，不能进行办理！");
		}
		// 主副号码实名信息是否一致性对比
		String snbName = ucaB.getCustPerson().getCustName();
		String snbIdType = ucaB.getCustPerson().getPsptTypeCode();
		if("1".equals(snaIdType))//如果是外地身份证，则转化为本地身份证
    	{
    		snaIdType ="0";
    		
    	}
		String snbId = ucaB.getCustPerson().getPsptId();
		
		String provIdType = this.encodeIdType(snaIdType);
		if(StringUtils.equals(snaName, snbName) && StringUtils.equals(provIdType, snbIdType) && StringUtils.equals(snaIdNum, snbId))
		{
			resultData.put("X_RESULTCODE", "0");
            resultData.put("X_RESULTINFO", "号码证件校验成功");
		}
		else
		{
			resultData.put("X_RSPTYPE", "2");
            resultData.put("X_RSPCODE", "2998");
            resultData.put("X_RSPDESC", "主副号码实名制信息不一致");
            resultData.put("X_RESULTCODE", "3040");
            resultData.put("X_RESULTINFO", "主副号码实名制信息不一致");
		}

		resultList.add(resultData);
		// 调用反馈接口告诉和多号平台处理结果
		//realCardResult(resultData);
		return resultList;
    }
    
    public void realCardResult(IData input) throws Exception {
    	
    	IData inParam = new DataMap();
		// 调用IBOSS
    	inParam.putAll(input);
  
		inParam.put("KIND_ID", "BIP5A011_T5000005_0_0");// 接口标识
		IBossCall.callHttpIBOSS7("IBOSS", inParam);
    }
    
    public String decodeIdType(String IdType)
    {
        String iBossTdType = null;

        if ("0".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("1".equals(IdType))
        {
            iBossTdType = "01";
        }
        else if ("A".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("C".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("K".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }
    
    private String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    /**
     * 二次短信取消接口：提供给外围渠道，接收到确认后，生成台帐取消关系，之后调用网状网接口同步解除关系
     *
     * @param pd ITF_CRM_CancelOneCardMosp
     */
    public IDataset cancelRelationForSMS(IData input) throws Exception {
        IDataset resultList = new DatasetList();
        //参数处理
        isEmptyData(input, "IN_MODE_CODE");// 接入编码
        isEmptyData(input, "TRADE_EPARCHY_CODE");// 工号所在地州路由
        isEmptyData(input, "TRADE_CITY_CODE");// 工号所在地州
        isEmptyData(input, "TRADE_DEPART_ID");// 操作部门
        isEmptyData(input, "TRADE_STAFF_ID");// 操作工号
//		isEmptyData(input, Route.ROUTE_EPARCHY_CODE);// 业务号码归属的地州

        isEmptyData(input, "SERIAL_NUMBER");// 主号码
        isEmptyData(input, "SERIAL_NUM");// 主号码
        isEmptyData(input, "FORCE_OBJECT");// 发送号码

        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        IData callResult = new DataMap();
        IData resultData = new DataMap();
        resultData.put("X_RESULTCODE", "0");
        //三户资料  主体服务状态校验
        callResult = bean.isNormalMainService(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //主号是否为实名制用户
        callResult = bean.isRealNameUser(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }

        //收到确认短信进行取消变更资料
        input.put("ORDERNO", input.getString("SERIAL_NUM")); // 副号码序号
        input.put("FLAG", "4");
        try {
            callResult = CSAppCall.call("SS.OneCardMultiNoRegSVC.tradeReg", input).getData(0);
        } catch (Exception e) {
            // TODO: handle exception
            String[] errorMessage = e.getMessage().split("●");
            resultData.put("X_RSPTYPE", "2");
            if (errorMessage[0].contains("不是一卡多号用户")) {
                resultData.put("X_RESULTCODE", "1001");
                resultData.put("X_RSPCODE", "1001");
                resultData.put("X_RESULTINFO", errorMessage[0]);
                resultData.putAll(resultData);
                resultList.add(resultData);
                return resultList;
            } else if (errorMessage[0].contains("不存在副号为序号")) {
                resultData.put("X_RESULTCODE", "1003");
                resultData.put("X_RSPCODE", "1003");
                resultData.put("X_RESULTINFO", errorMessage[0]);
                resultData.putAll(resultData);
                resultList.add(resultData);
                return resultList;
            } else if (errorMessage[0].contains("不存在短信二次确认的信息")) {
                resultData.put("X_RESULTCODE", "1004");
                resultData.put("X_RSPCODE", "1004");
                resultData.put("X_RESULTINFO", errorMessage[0]);
                resultData.putAll(resultData);
                resultList.add(resultData);
                return resultList;
            } else {
                throw e;
            }
        }

        resultData.putAll(callResult);
        resultList.add(resultData);
        return resultList;
    }

    /**
     * 取消前发送二次短信确认接口：提供给渠道
     *
     * @param pd ITF_CRM_UserTwoSmsCheck
     */
    public IDataset cancelRelationSendSMS(IData input) throws Exception {
        IDataset resultList = new DatasetList();
        //参数处理
        isEmptyData(input, "IN_MODE_CODE");// 接入编码
        isEmptyData(input, "TRADE_EPARCHY_CODE");// 工号所在地州路由
        isEmptyData(input, "TRADE_CITY_CODE");// 工号所在地州
        isEmptyData(input, "TRADE_DEPART_ID");// 操作部门
        isEmptyData(input, "TRADE_STAFF_ID");// 操作工号
//		isEmptyData(inputParam, Route.ROUTE_EPARCHY_CODE);// 业务号码归属的地州
        isEmptyData(input, "SERIAL_NUMBER");// 主号码

        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        IData callResult = new DataMap();
        IData resultData = new DataMap();
        resultData.put("X_RESULTCODE", "0");
        //三户资料  主体服务状态校验
        callResult = bean.isNormalMainService(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //主号是否为实名制用户
        callResult = bean.isRealNameUser(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //查询所有的订购关系，发送二次确认短信
        callResult = bean.sendSMSForCancel(input);
        resultData.putAll(callResult);
        resultList.add(resultData);
        return resultList;
    }

    /**
     * 一卡多号查询接口:提供给外围渠道  用户（主号）通过10086短信提交业务查询申请,主号侧根据号码查询对应的绑定结果信息进行返回。
     *
     * @param pd ITF_CRM_QueryOneCardMosp
     */
    public IDataset qryRelationList(IData input) throws Exception {
        IDataset resultList = new DatasetList();
        //参数处理
        isEmptyData(input, "IN_MODE_CODE");// 接入编码
        isEmptyData(input, "TRADE_EPARCHY_CODE");// 工号所在地州路由
        isEmptyData(input, "TRADE_CITY_CODE");// 工号所在地州
        isEmptyData(input, "TRADE_DEPART_ID");// 操作部门
        isEmptyData(input, "TRADE_STAFF_ID");// 操作工号
//		isEmptyData(input, Route.ROUTE_EPARCHY_CODE);// 业务号码归属的地州
        isEmptyData(input, "SERIAL_NUMBER");// 主号码

        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        IData callResult = new DataMap();
        IData resultData = new DataMap();
        resultData.put("X_RESULTCODE", "0");
        //三户资料  主体服务状态校验
        callResult = bean.isNormalMainService(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //主号是否为实名制用户
        callResult = bean.isRealNameUser(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //查询订购关系
        callResult = bean.qryRelationListForEC(input);
        resultData.putAll(callResult);
        resultList.add(resultData);
        return resultList;
    }

    /**
     * 一卡多号查询（提供给营业厅）
     */
    public IDataset qryRelationListForCRM(IData input) throws Exception {
        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        return bean.qryRelationListForCRM(input);
    }

    /**
     * 待销户虚拟副号码同步接口 ITF_CRM_MSISDNSYNC
     */
    public IDataset syncFollowMsisdnDestroy(IData input) throws Exception {
        isEmptyData(input, "MSISDN"); // 虚拟副号码信息
        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        bean.syncFollowMsisdnDestroy(input);
        return null;
    }

    public void setTrans(IData input) throws Exception {
        // 没有传SERIAL_NUMBER，必须进行转换
        String serial_number = input.getString("SERIAL_NUMBER");
        if (null == serial_number || "".equals(serial_number)) {
            serial_number = input.getString("MSISDN");
        }
        input.put("SERIAL_NUMBER", serial_number);
    }

    /**
     * 参数必传，同时不能为空
     */
    protected boolean isEmptyData(IData data, String name) throws Exception {
        String value = data.getString(name);
        if (value == null || value.trim().length() == 0) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[" + name + "]不能为空！");
        }
        return false;
    }

    /**
     * 一卡多号查询接口:提供给外围渠道  用户（主号）通过10086短信提交业务查询申请,主号侧根据号码查询对应的绑定结果信息进行返回。
     *
     * @param pd ITF_CRM_QueryOneCardMosp
     */
    public IDataset qryServiceList(IData input) throws Exception {
        IDataset resultList = new DatasetList();
        //参数处理
        isEmptyData(input, "IN_MODE_CODE");// 接入编码
        isEmptyData(input, "TRADE_EPARCHY_CODE");// 工号所在地州路由
        isEmptyData(input, "TRADE_CITY_CODE");// 工号所在地州
        isEmptyData(input, "TRADE_DEPART_ID");// 操作部门
        isEmptyData(input, "TRADE_STAFF_ID");// 操作工号
//		isEmptyData(input, Route.ROUTE_EPARCHY_CODE);// 业务号码归属的地州
        isEmptyData(input, "SERIAL_NUMBER");// 主号码

        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        IData callResult = new DataMap();
        IData resultData = new DataMap();
        resultData.put("X_RESULTCODE", "0");
        //三户资料  主体服务状态校验
        callResult = bean.isNormalMainService(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //主号是否为实名制用户
        callResult = bean.isRealNameUser(input);
        if (!"0".equals(callResult.getString("X_RESULTCODE"))) {
            resultData.putAll(callResult);
            resultList.add(resultData);
            return resultList;
        }
        //查询订购关系
        callResult = bean.qryServiceList(input);
        resultData.putAll(callResult);
        resultList.add(resultData);
        return resultList;
    }

    /**
     * 一卡多号办理（提供给营业厅）
     */
    public IDataset OneCardApply(IData input) throws Exception {
        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        return bean.applyOneForCRM(input);
    }

    /**
     * 主号码信息同步
     * ITF_CRM_OneCardInfoSyn 6.7	Master number information synchronization
     */
    public IData OneCardInfoSyn(IData input) throws Exception {
        IData resultData = new DataMap();
        resultData.put("X_RESULTCODE", "0");

        isEmptyData(input, "MSISDN");// 主号码
        isEmptyData(input, "FOLLOW_MSISDN");// 副号码
       // isEmptyData(input, "MSISDN_NAME");//
        isEmptyData(input, "OPR_CODE");// 操作代码 06-副号码申请 07-取消副号码

        ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);

		String fMsisdn = input.getString("FOLLOW_MSISDN"); // 副号码
		String msisdn = input.getString("MSISDN"); // 主号码
		String msisdnName = input.getString("MSISDN_NAME");
		String mProvince = input.getString("MPROVINCE");
		String category = input.getString("CATEGORY");
		String idType = input.getString("IDCARD_TYPE");
		String idNum = input.getString("IDCARD_NUM");
		String address = input.getString("ADDRESS");
		String synTime = input.getString("SYNTIME");
		String picNameT = input.getString("PIC_NAMET");
		String picNameZ = input.getString("PIC_NAMEZ");
		String picNameF = input.getString("PIC_NAMEF");
		String custType = IDataUtil.chkParam(input, "CUST_TYPE");// 客户类型
		String oprCode = input.getString("OPR_CODE", "");
		LanuchUtil logutil = new LanuchUtil();
		IData result = new DataMap();
		result.put("MSISDN", msisdn);
		result.put("FOLLOW_MSISDN", fMsisdn);
		result.put("SYNTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		try
		{
			if("06".equals(oprCode))
			{
				UcaData fuca = UcaDataFactory.getNormalUca(fMsisdn);
				IData param = new DataMap();
				param.put("SEQ_ID",SeqMgr.getOperId());
				param.put("USER_ID",msisdn);
				param.put("SERIAL_NUMBER",msisdn);
				param.put("OPR_CODE",oprCode);
				param.put("CUST_NAME",msisdnName);
				param.put("PROVINCE_CODE",mProvince);
				param.put("CUST_TYPE",custType);
				param.put("CATEGORY",category);
				param.put("PSPT_TYPE_CODE",logutil.encodeIdType(idType));
				param.put("PSPT_ID",idNum);
				param.put("ADDRESS",address);
				param.put("F_PICNAME_T",picNameT);
				param.put("F_PICNAME_Z",picNameZ);
				param.put("F_PICNAME_F",picNameF);
				param.put("BRAND_CODE",fuca.getBrandCode());
				param.put("F_PROVINCE_CODE","898");
				param.put("U_PARTITION_ID",StrUtil.getPartition4ById(fMsisdn+category));
				param.put("USER_ID_B",fMsisdn+category);
				param.put("SERIAL_NUMBER_B",fMsisdn);
				param.put("RELATION_TYPE_CODE",custType.equals("1")?"M3":"M2");
				param.put("ROLE_TYPE_CODE","");
				param.put("ROLE_CODE_A","1");
				param.put("ROLE_CODE_B","2");
				param.put("ORDERNO","");
				param.put("SHORT_CODE","");
				param.put("INST_ID",SeqMgr.getInstId());
				param.put("U_START_DATE",SysDateMgr.getSysDate());
				param.put("U_END_DATE",SysDateMgr.END_DATE_FOREVER);
				param.put("START_DATE",SysDateMgr.getSysDate());
				param.put("END_DATE",SysDateMgr.END_DATE_FOREVER);
				param.put("IN_STAFF_ID",CSBizBean.getVisit().getStaffId());
				param.put("IN_DEPART_ID",CSBizBean.getVisit().getDepartId());
				param.put("UPDATE_STAFF_ID",CSBizBean.getVisit().getStaffId());
				param.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
				param.put("PLAT_SEQ_ID",input.getString("SEQ"));
				if(StringUtils.equals("0", category))
				{
					param.put("RSRV_STR1", msisdnName+"|"+logutil.encodeIdType(idType)+"|"+idNum);
				}
				bean.addViceAsynInfo(param);
				
				if(StringUtils.equals("0", category))
				{
					bean.changeViceRealInfo(fMsisdn, address, msisdnName, logutil.encodeIdType(idType),idNum);
				}
			}
			else if("07".equals(oprCode))
			{
				oprCode ="07";
				IData param = new DataMap();
				param.put("SERIAL_NUMBER", msisdn);
				param.put("SERIAL_NUMBER_B", fMsisdn);
				if(StringUtils.equals("0", category))
				{
					IDataset datas = bean.qryHdhSynInfo(msisdn,fMsisdn);
					if(IDataUtil.isNotEmpty(datas))
					{
						IData temp = datas.getData(0);
						String rsrvStr1 = temp.getString("RSRV_STR1");
						if(StringUtils.isNotBlank(rsrvStr1))
						{
							String[] custInfos = rsrvStr1.split("\\|");
							if(custInfos.length >2)
							{
								String fCustName = custInfos[0];
								String fCustType = custInfos[1];
								String fCustpsId= custInfos[2];
								
								bean.chgBackViceRealInfo(fMsisdn, fCustName, fCustType, fCustpsId);
							}
						}
					}
				}
				
				param.put("U_END_DATE", SysDateMgr.getSysTime());
				bean.endViceAsynInfo(param);
			}
RegVolteTrade(input);
			result.put("RESULT", "0");
			result.put("INFO", "处理成功");
		}
		catch(Exception e)
		{
			result.put("RESULT", "1000");
			result.put("INFO", e.getMessage());
		}
		
		
		return result;
       /* String follow_msisdn = input.getString("FOLLOW_MSISDN"); // 副号码
        String msisdn = input.getString("MSISDN"); // 主号码
        String custType = IDataUtil.chkParam(input, "CUST_TYPE");//客户类型
        input.put("SERIAL_NUMBER", follow_msisdn);

        String oprCode = input.getString("OPR_CODE","");


        UcaData uca = UcaDataFactory.getNormalUca(follow_msisdn);
        input.putAll(uca.getCustPerson().toData());
        input.putAll(uca.getCustomer().toData());

        String brandCode = uca.getBrandCode();
        input.put("PHONE",follow_msisdn);
        input.put("CONTACT_PHONE",follow_msisdn);
        input.put("CONTACT",follow_msisdn);

        String custName = uca.getCustomer().getCustName();

        input.put("OLD_CUST_NAME", custName);

        if (brandCode.equals("MOSP")) {
            if (custType.equals("1"))                  //集团客户：1
            {

                if (oprCode.equals("06")) {
                    //input.put("RSRV_STR10", input.getString("CUST_NAME"));
                    custName =  input.getString("MSISDN_NAME","");
                } else if (oprCode.equals("07")) {
                    custName = uca.getCustomer().getRsrvStr6(); // 副号码取消
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:操作编码无效！");
                }

            }else if(custType.equals("0")){  //个人客户
                if (oprCode.equals("06")) {
                    //input.put("RSRV_STR10", input.getString("CUST_NAME"));
                    custName =  input.getString("MSISDN_NAME","");
                } else if (oprCode.equals("07")) {
                    custName = "一卡多号虚拟副号码";
                } else {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:操作编码无效！");
                }

            }
        }

        input.put("CUST_NAME", custName);
        input.put("MPROVINCE", input.getString("MPROVINCE",""));   //主号码归属省份
        input.put("MSISDN", msisdn);   //主号码标识
        input.put("IS_SAVEMPROVINCE", "1");   //主号码标识

        IDataset dataset = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", input);
        return dataset.getData(0);*/
    }
    
    
    private void RegVolteTrade(IData input) throws Exception
    {
        String snb = input.getString("FOLLOW_MSISDN");
        String category = input.getString("CATEGORY");
        if("1".equals(category)){
            IData snbInfo = UcaInfoQry.qryUserInfoBySn(snb);
            if(IDataUtil.isNotEmpty(snbInfo)){
               CSAppCall.call("SS.OneCardMospFollowRegSVC.tradeReg", input);
            }
        }
    }
  
	


    

    /**
     * 集团客户副号码信息同步
     * 6.8	Master number information synchronization
     */
    public IData GroupOneCardInfoSyn(IData input) throws Exception {
        IData resultData = new DataMap();
        resultData.put("X_RESULTCODE", "0");

        //参数处理
//        isEmptyData(input, "IN_MODE_CODE");// 接入编码
//        isEmptyData(input, "TRADE_EPARCHY_CODE");// 工号所在地州路由
//        isEmptyData(input, "TRADE_CITY_CODE");// 工号所在地州
//        isEmptyData(input, "TRADE_DEPART_ID");// 操作部门
//        isEmptyData(input, "TRADE_STAFF_ID");// 操作工号
        //		isEmptyData(input, Route.ROUTE_EPARCHY_CODE);// 业务号码归属的地州

        String serial_number = IDataUtil.chkParam(input, "MSISDN"); //副号码
        String custId = IDataUtil.chkParam(input, "ENTERPRISE_ID");// 集团客户ID
        input.put("SERIAL_NUMBER", serial_number);


        String oprCode = IDataUtil.chkParam(input, "OPR_CODE");// 操作代码 06-副号码申请 07-取消副号码

        UcaData uca = UcaDataFactory.getNormalUca(serial_number);
        input.putAll(uca.getCustomer().toData());
        input.put("OLD_CUST_NAME", uca.getCustomer().getCustName());

        if ("MOSP".equals(uca.getBrandCode())) {
            if (oprCode.equals("01")) {   //订购
                //input.put("RSRV_STR10", input.getString("CUST_NAME"));
                input.put("CUST_NAME", IDataUtil.chkParam(input, "ENTERPRISE_NAME")); // 副号码申请
            } else if (oprCode.equals("02")) {   //退订
                input.put("CUST_NAME", uca.getCustomer().getRsrvStr6()); // 副号码取消
                //input.put("RSRV_STR10", ""); // 副号码取消

            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:操作编码无效！");
            }
        }

        input.put("IS_SAVEMPROVINCE", "1");
        input.put("MPROVINCE", input.getString("MPROVINCE"));
        input.put("MSISDN", custId);

        IDataset dataset = CSAppCall.call("SS.ModifyCustInfoRegSVC.tradeReg", input);
        return dataset.getData(0);

    }
    
    /**
     * 端午节活动
     * 判断主号码,是否办理和多号业务
     * <br/>
     * 提供给app使用
     * CODE 1表示已经办理一证多号业务，0表示未办理一证多号业务
     * @param input
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170516
     */
    public IData  checkIsOneCardMultiNo(IData input) throws Exception {
    	IData  result=new DataMap();
        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        IDataset  into=bean.checkIsOneCardMultiNo(input);
        if(IDataUtil.isNotEmpty(into)){
        	//已经办理一证多号
        	result.put("CODE", 1);
        	result.put("MSG", "已经办理一证多号业务");
        }else{
        	//没有办理一证多号
        	result.put("CODE", 0);
        	result.put("MSG", "未办理一证多号业务");
        }
        return result;
    }
    
    /**
     * 端午节活动
     * 和多号业务办理(提供接口)
     * @param input
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20170519
     */
    public IData andMoreCardApply(IData input) throws Exception {
    	
    	//主号手机号码
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	//服务编码
    	IDataUtil.chkParam(input, "SERVICE_ID");
    	
		String serial_number=input.getString("SERIAL_NUMBER");
		IData user = UcaInfoQry.qryUserInfoBySn(serial_number);
		if(null==user||user.isEmpty()){
			CSAppException.apperr(CrmUserException.CRM_USER_112);
		}
		
        OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
        //USER_DATA
        IData  data=bean.applyOneForCRM(input).getData(0);
        	   data.remove("USER_DATA");
        return data;
    }
    
    
    /**
	 * 上线时存量和多号信息同步
	 * @param input
	 * @throws Exception
	 */
	public IDataset batUserInfoSync(IData input)throws Exception
	{
		IDataset result = new DatasetList();
		IData resultInfo = new DataMap();
		resultInfo.put("X_RESULT_CODE", "0");
		resultInfo.put("X_RESULTINFO", "业务受理成功");
		result.add(resultInfo);
		ViceRealInfoReRegBean bean = BeanManager.createBean(ViceRealInfoReRegBean.class);
		String seqId = input.getString("USER_ID");
		IDataset synInfos = bean.qryHdhSynInfoByPk(seqId);
		if(IDataUtil.isEmpty(synInfos))
		{
			resultInfo.put("X_RESULT_CODE", "-1");
			resultInfo.put("X_RESULTINFO", "根据seqID未找到要同步的记录");
		}
		IData synInfo = synInfos.getData(0);
		
		String sna = synInfo.getString("SERIAL_NUMBER");
		String snb = synInfo.getString("SERIAL_NUMBER_B");
		String category = synInfo.getString("CATEGORY");
		String addr = synInfo.getString("ADDRESS");
		String custName = synInfo.getString("CUST_NAME");
		String psptId= synInfo.getString("PSPT_ID");
		String psptTypeCode = synInfo.getString("PSPT_TYPE_CODE");
		String custType = synInfo.getString("CUST_TYPE");
		
		IData userInfo = UcaInfoQry.qryUserInfoBySn(snb);
		if(IDataUtil.isNotEmpty(userInfo))//本身号码
		{
			if(StringUtils.equals("0", category))//虚拟副号
			{
				UcaData uca = UcaDataFactory.getNormalUca(snb);
				String fCustName = uca.getCustPerson().getCustName();
				String fCustPsptId = uca.getCustPerson().getPsptId();
				String fCustPsptType = uca.getCustPerson().getPsptTypeCode();
				if(!StringUtils.equals(custName, fCustName) || !StringUtils.equals(psptId, fCustPsptId) ||!StringUtils.equals(psptTypeCode, fCustPsptType))
				{
					bean.changeViceRealInfo(snb, addr, custName, psptTypeCode, psptId);
				}
			}
		}
		else
		{
			LanuchUtil logutil = new LanuchUtil();
			String routeValue = input.getString("F_PROVINCE_CODE","898");
			IData routeData = MsisdnInfoQry.getCrmMsisonBySerialnumber(snb);
			if(IDataUtil.isNotEmpty(routeData))
			{
				routeValue = routeData.getString("PROV_CODE");
			}
			IData callIbossReq = new DataMap(); 

			callIbossReq.put("MSISDN", sna);
			callIbossReq.put("FOLLOW_MSISDN", snb);
			callIbossReq.put("ROUTEVALUE", snb);
			callIbossReq.put("ROUTETYPE", "01");
			callIbossReq.put("OPR_CODE", "06");
			
			callIbossReq.put("CUST_TYPE", custType);
			callIbossReq.put("CATEGORY", category);
			callIbossReq.put("ADDRESS", addr);	
			callIbossReq.put("SYNTIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			callIbossReq.put("MPROVINCE", "898");
			callIbossReq.put("MSISDN_NAME", custName);

			callIbossReq.put("IDCARD_TYPE", LanuchUtil.decodeIdType2(psptTypeCode));
			callIbossReq.put("IDCARD_NUM", psptId);
			callIbossReq.put("PIC_NAMET","");
			callIbossReq.put("PIC_NAMEZ", "");
			callIbossReq.put("PIC_NAMEF", "");
			
			callIbossReq.put("REC_NUM", "1");
			callIbossReq.put("PKG_SEQ", "898"+SysDateMgr.getSysDateYYYYMMDD()+SeqMgr.getLogId().substring(0, 6));
			callIbossReq.put("KIND_ID", "BIP5A013_T5101023_0_0");

			IDataset results = IBossCall.callHttpIBOSS7("IBOSS", callIbossReq);
		}
		return result;
	}
    
	/**
     * 6.9	副号码信息图片同步
     * 
     */
   public void PicInfoSyn(IData input) throws Exception 
    { 
    	OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
    	bean.picInfoSyn(input);    	
    }
   /**
	 * 集团客户副号码信息同步 (规范1.3.1改造中间号，和多号)
	 */
	public IData GroupOneCardInfoSyn1(IData input) throws Exception {
		IData resultData = new DataMap();
		resultData.put("PKG_SEQ",IDataUtil.chkParam(input, "PKG_SEQ"));
		 IDataUtil.chkParam(input, "REC_NUM");
		IData param=new DataMap();	
		param.put("PKG_SEQ", IDataUtil.chkParam(input, "PKG_SEQ"));//交易包流水号
		IDataset useInfos=input.getDataset("UD1");
		IDataset retInfos=new DatasetList();
		if(IDataUtil.isNotEmpty(useInfos)){			
			for(int i=0;i<useInfos.size();i++){
				param.put("SYNC_ID", SysDateMgr.getSysDateYYYYMMDDHHMMSS()+SeqMgr.getLogId().substring(0, 8));
				param.put("SYNC_TIME", SysDateMgr.getSysTime());
				IData userData=useInfos.getData(i);
				param.put("OPR_SEQ", IDataUtil.chkParam(userData, "SEQ"));	//本次操作的流水号			
				String serial_number=IDataUtil.chkParam(userData, "FOLLOW_MSISDN");				
				param.put("FOLLOW_ACC_NUM", serial_number);					//副号码标识
				UcaData uca = UcaDataFactory.getNormalUca(serial_number);			
				param.put("FOLLOW_USER_ID", uca.getUserId());
				param.put("FOLLOW_CUST_ID", uca.getCustId());
				String oprCode=IDataUtil.chkParam(userData, "OPR_CODE");
				param.put("OPR_CODE", oprCode);
				if("01".equals(oprCode)||"08".equals(oprCode)){
				param.put("ENTERPRISE_NAME", IDataUtil.chkParam(userData, "ENTERPRISE_NAME"));//集团客户名称
				String mpGroupCode=IDataUtil.chkParam(userData, "ENTERPRISE_ID");
				param.put("GROUP_CUST_ID", mpGroupCode);//CUST_ID?MSISDN?//集团客户ID
				IData data=new DataMap();
				data.put("MP_GROUP_CUST_CODE", mpGroupCode);
				IDataset groupInfo=GrpInfoQry.getGroupByMpGroup(data);
				if(IDataUtil.isNotEmpty(groupInfo)){
				param.put("GROUP_CUST_ID", groupInfo.getData(0).getString("CUST_ID",""));//CUST_ID?MSISDN?//集团客户ID
				}	
				param.put("ENTER_PROVINCE", IDataUtil.chkParam(userData, "MPROVINCE"));	//集团客户归属省代码
				LanuchUtil logutil = new LanuchUtil();
				IDataset zrrList=userData.getDataset("ZRR_INFO");//责任人信息
				IData zrrInfo=zrrList.getData(0);//责任人信息
				String zrrIdType=IDataUtil.chkParam(zrrInfo, "IDCARD_TYPE");
				param.put("ZRR_IDCARD_TYPE",logutil.encodeIdType(zrrIdType));//责任人证件类型
				param.put("ZRR_IDCARD_NUM", IDataUtil.chkParam(zrrInfo, "IDCARD_NUM"));//责任人证件号码
				param.put("ZRR_NAME", IDataUtil.chkParam(zrrInfo, "MSISDN_NAME"));//责任人名称
				param.put("ZRR_IDCARD_ADDR", IDataUtil.chkParam(zrrInfo, "ADDRESS"));//责任人证件地址	
				param.put("ZRR_PIC_NAME_Z", IDataUtil.chkParam(zrrInfo, "PIC_NAMEZ"));//责任人身份证正面
				param.put("ZRR_PIC_NAME_F", IDataUtil.chkParam(zrrInfo, "PIC_NAMEF"));//责任人身份证反面				
				IDataset jbrList=userData.getDataset("JBR_INFO");//责任人信息
				IData jbrInfo=jbrList.getData(0);//经办人信息
				String jbrIdType=IDataUtil.chkParam(jbrInfo, "IDCARD_TYPE");
				param.put("JBR_IDCARD_TYPE",logutil.encodeIdType(jbrIdType));//经办人证件类型
				param.put("JBR_IDCARD_NUM", IDataUtil.chkParam(jbrInfo, "IDCARD_NUM"));//经办人证件号码
				param.put("JBR_NAME", IDataUtil.chkParam(jbrInfo, "MSISDN_NAME"));//经办人名称
				param.put("JBR_IDCARD_ADDR", IDataUtil.chkParam(jbrInfo, "ADDRESS"));//经办人证件地址
				param.put("JBR_PIC_NAME_T", IDataUtil.chkParam(jbrInfo, "PIC_NAMET"));//经办人头像
				param.put("JBR_PIC_NAME_Z", IDataUtil.chkParam(jbrInfo, "PIC_NAMEZ"));//经办人身份证正面
				param.put("JBR_PIC_NAME_F", IDataUtil.chkParam(jbrInfo, "PIC_NAMEF"));//经办人身份证反面
				}
				param.put("RSRV_STR1","871");//副号码归属省
				Dao.insert("TF_F_GROUP_FOLLOW_NUM", param,Route.CONN_CRM_CG);//将客户信息插入到表CB_ENT_FOLLOW_NUM				
				IData ret=new DataMap();				
				ret.put("SEQ", userData.getString("SEQ"));
				ret.put("FOLLOW_MSISDN", userData.getString("FOLLOW_MSISDN"));
				ret.put("OPRT", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				ret.put("FBIZORDER_RESULT", "0000");
				ret.put("RESULT_DESC", "集团副号码信息同步成功！");
				retInfos.add(ret);
			}
			resultData.put("REC_NUM",useInfos.size()+"");
			resultData.put("UD_RSP", retInfos);			
		}else{
			resultData.put("REC_NUM","0");
			resultData.put("UD_RSP", "");	
		}
		return resultData;
	}

	/**
	 * 6.10 号码状态查询(向副号归属省BOSS查询副号码状态)
	 */
	public IData qryFollowMsisdnStatu(IData input) throws Exception {
		setTransInfo(input);
		String seq = input.getString("SEQ");
		String snb = input.getString("FOLLOW_MSISDN");
		String category = input.getString("CATEGORY");

		IData userInfo = UcaInfoQry.qryUserInfoBySn(snb);
		if (IDataUtil.isEmpty(userInfo)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询不到副号码的用户信息！");
		}
		String userStateCodeset = userInfo.getString("USER_STATE_CODESET");
		String userState = "";
		String userStateDesc = "";
		if ("0".equals(userStateCodeset)) {
			userState = "00";
			userStateDesc = "正常";
		} else if ("1".equals(userStateCodeset)) {
			userState = "01";
			userStateDesc = "单向停机";
		} else if ("2".equals(userStateCodeset)) {
			userState = "02";
			userStateDesc = "停机";
		} else if ("3".equals(userStateCodeset)) {
			userState = "03";
			userStateDesc = "预销户";
		} else if ("4".equals(userStateCodeset)) {
			userState = "04";
			userStateDesc = "销户";
		} else if ("5".equals(userStateCodeset)) {
			userState = "05";
			userStateDesc = "过户";
		} else if ("6".equals(userStateCodeset)) {
			userState = "06";
			userStateDesc = "改号";
		} else if ("X".equals(userStateCodeset)) {
			userState = "07";
			userStateDesc = "携号转出";
		} else if ("7".equals(userStateCodeset)) {
			userState = "90";
			userStateDesc = "神州行用户";
		} else {
			userState = "99";
			userStateDesc = "此号码不存在";
		}

		IData output = new DataMap();
		output.put("SEQ", seq);
		output.put("FOLLOW_MSISDN", snb);
		output.put("USER_STATUS", userState);
		output.put("USER_STATUS_DESC", userStateDesc);
		if ("0".equals(category)) {//虚拟副号码
			output.put("VOLTE", 0);
		} else if ("1".equals(category)) {
			IDataset userVolteB = UserSvcInfoQry.getSvcUserId(userInfo.getString("USER_ID"), "190");
			if (IDataUtil.isEmpty(userVolteB)) {
				output.put("VOLTE", 1);
			} else {
				output.put("VOLTE", 0);
			}
		}
		output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

		return output;
	}

	public void setTransInfo(IData input) throws Exception {
		// 没有传SERIAL_NUMBER，必须进行转换
		String serial_number = input.getString("SERIAL_NUMBER");
		if (null == serial_number || "".equals(serial_number)) {
			serial_number = input.getString("MSISDN");
		}
		input.put("SERIAL_NUMBER", serial_number);
		// 主号码信息同步接口，IBOSS增加入参ROUTE_FLAG，传值true，设置副号码路由
		if ("true".equals(input.getString("ROUTE_FLAG", ""))) {
			input.put("SERIAL_NUMBER", input.getString("FOLLOW_MSISDN"));
		}
		if ("SS.OneCardMultiNoSVC.qryFollowMsisdnStatu".equals(getVisit().getXTransCode())) {
			if (null == serial_number || "".equals(serial_number)) {
				serial_number = input.getString("FOLLOW_MSISDN");
			}
			input.put("SERIAL_NUMBER", serial_number);
		}
	}
	
	public IData CheckMainRealInfo(IData input) throws Exception {
		OneCardMultiNoBean bean = (OneCardMultiNoBean) BeanManager.createBean(OneCardMultiNoBean.class);
    	return bean.CheckMainRealInfo(input);
    }
	
	/**
	 * REQ201811290007  关于和多号0000查询及退订优化的需求 by mengqx 20190410
	 * 二次确认短信
	 */
	public IData twoCheck(IData input) throws Exception {
		String smsContent = "尊敬的客户：和多号包月（"+input.getString("SERIAL_NUMBER_B")+"）业务取消后，副号将无法找回。" +
				"为了您的信息安全，建议在取消前先解除副号与网络账户的绑定关系（如淘宝、微信等）。" +
				"请确认是否取消和多号包月（"+input.getString("SERIAL_NUMBER_B")+"）业务，回复“Y”确认取消，回复“N”不取消，2小时内回复有效。 中国移动";
		
        IData sendInfo = new DataMap();
        sendInfo.put("REMARK", "0000和多号查询及退订");
        sendInfo.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        sendInfo.put("SMS_CONTENT", smsContent);
        sendInfo.put("SMS_TYPE",BofConst.MOSP_CANCEL);
        sendInfo.put("OPR_SOURCE", "1");

        // 插二次短信表
        IData preOderData = new DataMap();
        preOderData.put("SVC_NAME", "SS.OneCardMultiNoRegSVC.tradeReg");
        preOderData.put("PRE_TYPE",BofConst.MOSP_CANCEL);
        preOderData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        
        preOderData.put("SERIAL_NUMBER_B", input.getString("SERIAL_NUMBER_B"));
        preOderData.put("OPR_CODE", OneCardMultiNoBean.OPER_CODE_CANCEL);
        preOderData.put("FLAG", "6");
        preOderData.put("ORDERNO", input.getString("ORDERNO"));
        preOderData.put("SPID", input.getString("SPID"));
        preOderData.put("BIZ_CODE", input.getString("BIZ_CODE"));

		/*
		 * BUG20191119153838 和多号实体副号退订资料修改异常问题 by mengqx 20191121
		 * 取消接口提供副号类型字段
		 */
		preOderData.put("CATEGORY", input.getString("CATEGORY"));

//        preOderData.put("IN_MODE_CODE", getVisit().getInModeCode());// 接入编码
//        preOderData.put("TRADE_CITY_CODE", getVisit().getCityCode());// 工号所在地州
//        preOderData.put("TRADE_DEPART_ID", getVisit().getDepartId());// 操作部门
//        preOderData.put("TRADE_STAFF_ID", getVisit().getStaffId());// 操作工号
//        preOderData.put("BIZ_TYPE", "74");// 业务类型代码 74-国内一卡多号业务
//        preOderData.put("CHANNEL_ID", "04");// 受理渠道  01-WEB， 03-WAP，04-SMS，70-客户端
//        preOderData.put("CATEGORY", input.getString("CATEGORY"));// 副号码类型 0：虚拟副号码；1：实体副号码

        return TwoCheckSms.twoCheck("-1", 2, preOderData, sendInfo);
	}
}