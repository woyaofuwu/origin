
package com.asiainfo.veris.crm.order.soa.person.busi.bat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.bat.bean.BatDealBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTaskInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatchTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.batactivecancel.BatActiveCancelBean;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.CreatePersonUserBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;
import com.asiainfo.veris.crm.order.soa.person.common.util.PsptUtils;
import net.sf.json.JSONArray;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BatDataImportSVC extends CSBizService
{
	protected static Logger log = Logger.getLogger(BatDataImportSVC.class);

    /**
     * @Fields serialVersionUID :
     */
    private static final long serialVersionUID = -1756769134328846009L;

    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {

        String batOperType = data.getString("BATCH_OPER_CODE");
        IDataset returnInfos = new DatasetList();
        if (batOperType.equals("CREATEPRETDUSER"))
        {
            String strImportMode = data.getString("TD_IMPORT_MODE");
            String strStartNum = data.getString("TD_BEGIN_NUM");
            String strEndNum = data.getString("TD_END_NUM");

            data.put("IMPORT_MODE", strImportMode);
            data.put("BEGIN_NUM", strStartNum.length() == 8 ? "898" + strStartNum : strStartNum);
            data.put("END_NUM", strEndNum.length() == 8 ? "898" + strEndNum : strEndNum);
        }
        
        if (batOperType.equals("CREATEPREUSER_PWLW"))
        {
            String strImportMode = data.getString("PWLW_IMPORT_MODE");
            String strStartNum = data.getString("PWLW_BEGIN_NUM");
            String strEndNum = data.getString("PWLW_END_NUM");

            data.put("IMPORT_MODE", strImportMode);
            data.put("BEGIN_NUM", strStartNum);
            data.put("END_NUM", strEndNum);
        }

        if (batOperType.equals("BATCREATETRUNKUSER") || batOperType.equals("BATAPPENDTRUNKUSER"))
        {
            String strImportMode = data.getString("TRUNK_IMPORT_MODE");
            String strStartNum = data.getString("TRUNK_BEGIN_NUM");
            String strEndNum = data.getString("TRUNK_END_NUM");

            data.put("IMPORT_MODE", strImportMode);
            data.put("BEGIN_NUM", strStartNum);
            data.put("END_NUM", strEndNum);
        }
        
        importDateCheck(data.getString("START_DATE"), data.getString("END_DATE"));

        if (batOperType.equals("CREATEPREUSER") || batOperType.equals("CREATEPRETDUSER") || batOperType.equals("BATACTIVECREATEUSER") || batOperType.equals("CREATEPREUSER_PWLW") || batOperType.equals("BATCREATETRUNKUSER")
                || batOperType.equals("BATAPPENDTRUNKUSER") || batOperType.equals("CREATEPREUSER_SCHOOL") )
        {
        	if(batOperType.equals("CREATEPREUSER_SCHOOL")){
        		data.put("IMPORT_MODE", "0");
			}
            returnInfos = registerTradeInfo(data, dataset);
            /**
             * REQ201707170020_新增物联卡开户人像采集功能
             * <br/>
             * 返回批量打印电子工单需要的信息
             * @author zhuoyingzhi
             * @date 20170828
             */
            if(batOperType.equals("CREATEPREUSER_PWLW")){
                IData batchTaskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(data.getString("BATCH_TASK_ID"));
                if (IDataUtil.isEmpty(batchTaskInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量任务不存在");
                }
                JSONArray array_rx = new JSONArray();
                
                StringBuffer sb = new StringBuffer();
                sb.append(batchTaskInfo.getString("CODING_STR1", "")).append(batchTaskInfo.getString("CODING_STR2", "")).
                	append(batchTaskInfo.getString("CODING_STR3", "")).append(batchTaskInfo.getString("CODING_STR4", "")).
                		append(batchTaskInfo.getString("CODING_STR5", ""));
                array_rx.element(sb.toString());
                
                DatasetList ds_rx = new DatasetList(array_rx.toString());
                if (DataSetUtils.isNotBlank(ds_rx)&&DataSetUtils.isNotBlank(returnInfos)) {

                	//记录信息到tf_b_trade_cnote_info表
                	BatDataImportBean.insertIntoTradeCnoteInfoBat(returnInfos.getData(0), batOperType);
                	
                    //客户名称
                	String  custName=ds_rx.getData(0).getString("CUST_NAME", "");
                	returnInfos.getData(0).put("CUST_NAME", custName);
                	
                	//证件号码
                	String psptid=ds_rx.getData(0).getString("PSPT_ID", "");
                	returnInfos.getData(0).put("ID_CARD", psptid);
                	
                	//受理工号
                	String staffid=getVisit().getStaffId();
                	returnInfos.getData(0).put("TRADE_STAFF_ID", staffid);
                	
                	//受理工单名称
                	String staffName=getVisit().getStaffName();
                	returnInfos.getData(0).put("TRADE_STAFF_NAME", staffName);
                	
                	//部门编码
                	returnInfos.getData(0).put("ORG_INFO", getVisit().getDepartId());
                	//部门名称
                	returnInfos.getData(0).put("ORG_NAME", getVisit().getDepartName());

                	String serialNumber=ds_rx.getData(0).getString("PHONE", "");
                	//联系电话
                	returnInfos.getData(0).put("SERIAL_NUMBER", serialNumber);
                	
                	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
                	//用户id
                	returnInfos.getData(0).put("USER_ID", ucaData.getUserId());
                	
                	//受理业务时间
                	String acceptDate=SysDateMgr.getSysTime();
                	returnInfos.getData(0).put("ACCEPT_DATE", acceptDate);
                	
                	//受理品牌
                	String productName=ds_rx.getData(0).getString("PRODUCT_NAME", "");
                	returnInfos.getData(0).put("PRODUCT_NAME", productName);
                	
                	//业务类型
                	returnInfos.getData(0).put("TRADE_TYPE_NAME", "物联网卡批量开户");
                	
                	//客户摄像标识
                	String custInfoPicId=ds_rx.getData(0).getString("custInfo_PIC_ID", "");
                	
                	//经办人摄像标识
                	String agentPicId=ds_rx.getData(0).getString("custInfo_AGENT_PIC_ID", "");
                	//0-已采集，1-未采集
                	if(!"".equals(custInfoPicId)&&custInfoPicId != null){
                		//客户已经摄像
                		returnInfos.getData(0).put("PIC_ID", "0");
                	}else{
                    	if(!"".equals(agentPicId)&&agentPicId != null){
                    		//经办人已经摄像
                    		returnInfos.getData(0).put("PIC_ID", "0");
                    	}else{
                    		//未摄像
                    		returnInfos.getData(0).put("PIC_ID", "1");
                    	}
                	}
                }
            	
            }
            /*****************************/
        }
        else if (batOperType.equals("MODIFYACYCINFO"))
        {
            returnInfos = importDataForAcycInfo(data, dataset);
        }
        else
        {
            returnInfos = importDataByFile(data, dataset);
            if(batOperType.equals("CREATEPREUSER_M2M")){
                IData batchTaskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(data.getString("BATCH_TASK_ID"));
                if (IDataUtil.isEmpty(batchTaskInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量任务不存在");
                }
                JSONArray array_rx = new JSONArray();
                StringBuffer sb = new StringBuffer();
                sb.append(batchTaskInfo.getString("CODING_STR1", "")).append(batchTaskInfo.getString("CODING_STR2", "")).
                	append(batchTaskInfo.getString("CODING_STR3", "")).append(batchTaskInfo.getString("CODING_STR4", "")).
                		append(batchTaskInfo.getString("CODING_STR5", ""));
                array_rx.element(sb.toString());
                DatasetList ds_rx = new DatasetList(array_rx.toString());
                if (DataSetUtils.isNotBlank(ds_rx)&&DataSetUtils.isNotBlank(returnInfos)) {
                	BatDataImportBean.insertIntoTradeCnoteInfoBat(returnInfos.getData(0), batOperType);
                	String  custName=ds_rx.getData(0).getString("CUST_NAME", "");
                	returnInfos.getData(0).put("CUST_NAME", custName);
                	String psptid=ds_rx.getData(0).getString("PSPT_ID", "");
                	returnInfos.getData(0).put("ID_CARD", psptid);
                	String staffid=getVisit().getStaffId();
                	returnInfos.getData(0).put("TRADE_STAFF_ID", staffid);
                	String staffName=getVisit().getStaffName();
                	returnInfos.getData(0).put("TRADE_STAFF_NAME", staffName);
                	returnInfos.getData(0).put("ORG_INFO", getVisit().getDepartId());
                	returnInfos.getData(0).put("ORG_NAME", getVisit().getDepartName());
                	String serialNumber=ds_rx.getData(0).getString("PHONE", "");
                	returnInfos.getData(0).put("SERIAL_NUMBER", serialNumber);
                	if(!"".equals(serialNumber)&&serialNumber!=null){
                	 	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
                    	returnInfos.getData(0).put("USER_ID", ucaData.getUserId());
                	}else{
                    	returnInfos.getData(0).put("USER_ID", "");
                	}
                	String acceptDate=SysDateMgr.getSysTime();
                	returnInfos.getData(0).put("ACCEPT_DATE", acceptDate);
                	String productName=ds_rx.getData(0).getString("PRODUCT_NAME", "");
                	returnInfos.getData(0).put("PRODUCT_NAME", productName);
                	returnInfos.getData(0).put("TRADE_TYPE_NAME", "行业应用卡批量开户");
                	String custInfoPicId=ds_rx.getData(0).getString("custInfo_RSRV_STR4_PIC_ID", "");
                	String agentPicId=ds_rx.getData(0).getString("custInfo_AGENT_PIC_ID", "");
                	if(!"".equals(custInfoPicId)&&custInfoPicId != null){
                		returnInfos.getData(0).put("PIC_ID", "0");
                	}else{
                    	if(!"".equals(agentPicId)&&agentPicId != null){
                    		returnInfos.getData(0).put("PIC_ID", "0");
                    	}else{
                    		returnInfos.getData(0).put("PIC_ID", "1");
                    	}
                	}
                }            	
            }
            // 返回批量打印电子工单需要的信息
            //添加批量无线固话业务受理单：无线固话单位证件实名制登记，批量修改密码，批量登记实名制业务 by wuhao5 20191126
            if(batOperType.equals("TT_FIXED_PHONE_STOP") || batOperType.equals("TD_FIXED_PHONE_STOP") || batOperType.equals("BATREALNAME") || batOperType.equals("BATUPDATEPSW") || batOperType.equals("MODIFYTDPSPTINFO")){
                IData batchTaskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(data.getString("BATCH_TASK_ID"));
                if (IDataUtil.isEmpty(batchTaskInfo))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量任务不存在");
                }
                //记录信息到tf_b_trade_cnote_info表
                BatDataImportBean.insertIntoTradeCnoteInfoBat(returnInfos.getData(0), batOperType);

                //受理工号
                String staffid=getVisit().getStaffId();
                returnInfos.getData(0).put("TRADE_STAFF_ID", staffid);

                //受理工单名称
                String staffName=getVisit().getStaffName();
                returnInfos.getData(0).put("TRADE_STAFF_NAME", staffName);

                //部门编码
                returnInfos.getData(0).put("ORG_INFO", getVisit().getDepartId());
                //部门名称
                returnInfos.getData(0).put("ORG_NAME", getVisit().getDepartName());

                // 受理品牌
               if (batOperType.equals("TD_FIXED_PHONE_STOP")){
                   returnInfos.getData(0).put("PRODUCT_NAME", "无线固话");

               }else {
                   returnInfos.getData(0).put("PRODUCT_NAME", "固话业务");
               }

                //联系电话 -- 暂时不填写
                returnInfos.getData(0).put("SERIAL_NUMBER", "");

                //用户id  -- 暂时不填写
                returnInfos.getData(0).put("USER_ID", "");

                //受理业务时间
                String acceptDate=SysDateMgr.getSysTime();
                returnInfos.getData(0).put("ACCEPT_DATE", acceptDate);

                //业务类型
                if(batOperType.equals("TT_FIXED_PHONE_STOP")){
                    returnInfos.getData(0).put("TRADE_TYPE_NAME", "铁通固话批量停机");
                    returnInfos.getData(0).put("TRADE_TYPE_CODE", "9734");
                }else {
                    returnInfos.getData(0).put("TRADE_TYPE_NAME", "TD二代无线固话批量停机");
                    returnInfos.getData(0).put("TRADE_TYPE_CODE", "3808");
                }
            }
        }
        return returnInfos;

    }

    public IData fileImportCheckForCampusBroadband(IData pageParam, IDataset dataset) throws Exception
    {
        IDataset discntParamSet = DiscntInfoQry.queryDiscntInfoByTypeCode("C");
        IData discntParam = new DataMap();
        String s = "";
        // 把 discntParamSet 分散到 discntParam ，以便后面根据 DISCNT_CODE 查找
        for (int i = 0; i < discntParamSet.size(); i++)
        {
            s = discntParamSet.getData(i).getString("DISCNT_CODE");
            discntParam.put("DISCNT_CODE" + s, discntParamSet.getData(i).getString("DISCNT_CODE"));
            discntParam.put("ENABLE_TAG" + s, discntParamSet.getData(i).getString("ENABLE_TAG"));
            discntParam.put("DEFINE_MONTHS" + s, discntParamSet.getData(i).getString("DEFINE_MONTHS"));
            discntParam.put("RSRV_STR2" + s, discntParamSet.getData(i).getString("RSRV_STR2"));
            discntParam.put("RSRV_STR3" + s, discntParamSet.getData(i).getString("RSRV_STR3"));
            discntParam.put("RSRV_STR4" + s, discntParamSet.getData(i).getString("RSRV_STR4"));
        }

        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        String s0, s1, s2, s3, s4;
        String firstDayThisMonth = SysDateMgr.getFirstDayOfThisMonth();
        Date d1, d2, d3;
        d1 = SysDateMgr.string2Date(firstDayThisMonth, SysDateMgr.PATTERN_STAND_YYYYMMDD); // 本月一号
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            s0 = data.getString("SERIAL_NUMBER");
            s1 = data.getString("DATA1");
            s2 = data.getString("DATA2");
            s3 = data.getString("DATA3");
            s4 = data.getString("DATA4");

            boolean importResult = Boolean.valueOf((String) data.getString("IMPORT_RESULT", "true")).booleanValue();
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(s0))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            if ("".equals(discntParam.getString("DISCNT_CODE" + s1, "")))
            {
                data.put("IMPORT_ERROR", "校园宽带套餐编码有误!");
                failds.add(data);
                continue;
            }
            try
            {
                // 将输入数据 String 转为 Date 类型以便下面作日期比较
                d2 = SysDateMgr.string2Date(s2, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.parse(s2); //开始时间
                d3 = SysDateMgr.string2Date(s3, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.parse(s3); //结束时间

                // 输入数据 String类型 s2, s3 此处需做格式转换，如 : 由 2012-9-5 转为 2012-09-05
                s2 = SysDateMgr.date2String(d2, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.format(d2);
                s3 = SysDateMgr.date2String(d3, SysDateMgr.PATTERN_STAND_YYYYMMDD);// df.format(d3);

                if (d2.compareTo(d3) > 0)
                {
                    data.put("IMPORT_ERROR", "开始日期不应大于结束日期!");
                    failds.add(data);
                    continue;
                }
                if (d2.compareTo(d1) < 0)
                {
                    data.put("IMPORT_ERROR", "开始日期不应早于本月1号!");
                    failds.add(data);
                    continue;
                }
            }
            catch (Exception e)
            {
                data.put("IMPORT_ERROR", "开始日期或结束日期格式不对!");
                failds.add(data);
                if (log.isDebugEnabled())
                {
                	//log.info("(e);
                }
                continue;
            }
            if (!StringUtils.isNumeric(s4))
            {
                data.put("IMPORT_ERROR", "实缴金额格式不对!");
                failds.add(data);
                continue;
            }
            if (s4.length() > 9)
            {
                data.put("IMPORT_ERROR", "实缴金额数值超出范围!");
                failds.add(data);
                continue;
            }
            if (!s3.equals(SysDateMgr.getDateLastMonthSec(s3).substring(0, 10)))
            {
                data.put("IMPORT_ERROR", "结束日期必须是月底最后一天!");
                failds.add(data);
                continue;
            }

            // 将开始结束日期字符串填充时分秒后放入data
            data.put("DATA2", s2 + SysDateMgr.START_DATE_FOREVER);
            data.put("DATA3", s3 + SysDateMgr.END_DATE);

            // 把套餐参数信息放到 tf_b_trade_batdeal 表剩余的 DATA 字段中
            data.put("DATA5", discntParam.getString("ENABLE_TAG" + s1));
            data.put("DATA6", discntParam.getString("DEFINE_MONTHS" + s1));
            data.put("DATA7", discntParam.getString("RSRV_STR2" + s1));
            data.put("DATA8", discntParam.getString("RSRV_STR3" + s1));
            data.put("DATA9", discntParam.getString("RSRV_STR4" + s1));
            succds.add(data);
        }

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        // inParam = new DataMap();
        if (succds.size() == 0)
        {
            return returnData;
        }
        else
        {
            String serial_number = (String) succds.get(0, "SERIAL_NUMBER");
            if (serial_number == null || serial_number.equals(""))
            {
                return returnData;
            }

            /*
             * 这一段没有意义，暂时去掉 //判断该任务导入的第一个号码是否在数据库中已经存在 inParam.put("BATCH_TASK_ID",
             * pageParam.getString("BATCH_TASK_ID")); inParam.put("SERIAL_NUMBER", serial_number);
             * if(dao.queryListByCodeCode("TF_B_TRADE_BATDEAL", "CHECK_SN_IS_EXIST", inParam).size() != 0) {
             * CSAppException.apperr(CrmCommException.CRM_COMM_103, "一批文件不允许重复导入!"); }
             */

        }

        return returnData;
    }
    
    public IData fileImportCheckForNumber(IData pageParam, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        IData returnData = new DataMap();
        
        if(IDataUtil.isEmpty(dataset)){	 	 
            IData error=new DataMap();	 	 
            error.put("IMPORT_ERROR", "内容为空");	 	 
            failds.add(error);	 
            returnData.put("SUCCDS", succds);	 	 
            returnData.put("FAILDS", failds);	
            return returnData;	 	 
        }	
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            boolean importResult = Boolean.valueOf(data.getString("IMPORT_RESULT", "true")).booleanValue();
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
          //手机号码	 	 
            String serial_number=data.getString("SERIAL_NUMBER");	 	 
           	 	 
           	 	 
            if(serial_number == null||"".equals(serial_number)){	 	 
                    data.put("IMPORT_ERROR", "手机号码  不能为空!");	 	 
                    failds.add(data);	 	 
                    continue;	 	 
               }else{	 	 
                  serial_number=serial_number.trim();
                  if(!BatDataImportBean.validPhoneNum("0", serial_number)){
                      data.put("IMPORT_ERROR", "手机号码  格式错误");	 	 
                      failds.add(data);	 	 
                      continue;
                  }
               }
            
            /**
           	 * 判断是否为测试号码
           	 */
            try {
                UcaData uca = UcaDataFactory.getNormalUca(serial_number);
                String city_code = uca.getUser().getCityCode();
                if (!"HNSJ".equals(city_code) && !"HNHN".equals(city_code))
                {
                    data.put("IMPORT_ERROR", "非测试卡用户！");	 	 
                    failds.add(data);
                    continue;
                } 
			} catch (Exception e) {
				  //log.info("(e);
	              data.put("IMPORT_ERROR", "未找到该服务号码对应的用户资料信息");	 	 
                  failds.add(data);
                  continue;
			}

            succds.add(data);
        }

        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        // IData inParam = new DataMap();
        if (succds.size() == 0)
        {
            return returnData;
        }
        else
        {
            String serial_number = (String) succds.get(0, "SERIAL_NUMBER");
            if (serial_number == null || serial_number.equals(""))
            {
                return returnData;
            }
            // 判断该任务导入的第一个号码是否在数据库中已经存在
            // inParam.put("BATCH_TASK_ID", pageParam.getString("BATCH_TASK_ID"));
            // inParam.put("SERIAL_NUMBER", serial_number);
            // if(dao.queryListByCodeCode("TF_B_TRADE_BATDEAL", "CHECK_SN_IS_EXIST", inParam).size() != 0) {
            // CSAppException.apperr(CrmCommException.CRM_COMM_103, "一批文件不允许重复导入!");
            // }
        }

        return returnData;
    }
    
    public IData fileImportCheckOpenStop(IData pageParam, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        if(dataset.size()>0){
            for (int i = 0; i < dataset.size(); i++)
            {
                IData data = new DataMap();
                data = dataset.getData(i);
                String serialNumber=data.getString("SERIAL_NUMBER");

                UcaData ucaData=null;
                try{
                    ucaData = UcaDataFactory.getNormalUca(serialNumber); //可能输入不存在数据的用户导致报错
                }catch(Exception e){
                    data.put("IMPORT_ERROR", "获取用户数据出错!");
                    failds.add(data);
                    continue;
                }
                if(ucaData==null){
                    data.put("IMPORT_ERROR", "获取用户数据出错!");
                    failds.add(data);
                    continue;
                }
                if(!StringUtils.equals(ucaData.getUser().getAcctTag(), "2") && !StringUtils.equals(ucaData.getUser().getOpenMode(), "2")) {
                    data.put("IMPORT_ERROR",  "号码【"+ serialNumber + "】不是买断批开到期未激活状态!");
                    failds.add(data);
                    continue;
                }
                succds.add(data);
            }
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;
    }
	
    public IData fileImportCheckForProductChg(IData pageParam, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);

            boolean importResult = Boolean.valueOf(data.getString("IMPORT_RESULT", "true")).booleanValue();
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            //BUG20190117173111 修改批量修改账户银行资料服务号码长度限制为11位的问题 add by mengqx
            //修改账户银行资料,集团V网产品一般都是10位数。如V0HK001355。
            if (!StringUtils.isNumeric(data.getString("SERIAL_NUMBER")) && !"MODIFYACYCINFO".equals(pageParam.getString("BATCH_OPER_CODE")))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }

            succds.add(data);
        }

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        // IData inParam = new DataMap();
        if (succds.size() == 0)
        {
            return returnData;
        }
        else
        {
            String serial_number = (String) succds.get(0, "SERIAL_NUMBER");
            if (serial_number == null || serial_number.equals(""))
            {
                return returnData;
            }
            // 判断该任务导入的第一个号码是否在数据库中已经存在
            // inParam.put("BATCH_TASK_ID", pageParam.getString("BATCH_TASK_ID"));
            // inParam.put("SERIAL_NUMBER", serial_number);
            // if(dao.queryListByCodeCode("TF_B_TRADE_BATDEAL", "CHECK_SN_IS_EXIST", inParam).size() != 0) {
            // CSAppException.apperr(CrmCommException.CRM_COMM_103, "一批文件不允许重复导入!");
            // }
        }

        return returnData;
    }

    public IData fileImportCheckForSaleActive(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        IData checkResultData=new DataMap();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);

            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            String serialNum=data.getString("SERIAL_NUMBER");
            if (!StringUtils.isNumeric(serialNum))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            String batchTaskId = pdData.getString("BATCH_TASK_ID");
            IData task = BatTaskInfoQry.qryBatTaskByBatchTaskId(batchTaskId);
            IData cond = new DataMap(task.getString("CODING_STR1"));
            if (cond != null && !cond.isEmpty())
            {
                String campnType = cond.getString("CAMPN_TYPE", "");
                String terminalId = (data.getString("DATA1", "")).trim();
                if (("YX03".equals(campnType) || "YX07".equals(campnType) || "YX08".equals(campnType) || "YX09".equals(campnType)))
                {
                    if ("".equals(terminalId))
                    {
                        data.put("IMPORT_ERROR", "购机类活动终端串号不能为空!");
                        failds.add(data);
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "购机类活动终端串号不能为空!");
                    }
                }
                else
                {
                    if (!"".equals(terminalId))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_103, "礼包类活动不能有终端串号!");
                    }
                } 
                /**
                 * 2017年老客户感恩大派送活动开发需求
                 * chenxy  20170912
                 * */
                checkResultData=check2017ActiveUser(cond,serialNum,data.getString("DATA2"));
                if(checkResultData !=null && !checkResultData.isEmpty()){
                	data.putAll(checkResultData);
                	failds.add(data);
                }
            }
            if(checkResultData !=null && !checkResultData.isEmpty()){
            	
            }else{
            	succds.add(data);
            }
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;

    }
    /**
     * 2017年老客户感恩大派送活动开发需求
     * chenxy  20170912
     * */
    public IData check2017ActiveUser(IData cond,String serialNum,String checkSN)throws Exception{
    	IData data=new DataMap();
    	String prodId=cond.getString("PRODUCT_ID");
    	String packId=cond.getString("PACKAGE_ID");
    	IDataset comms=CommparaInfoQry.getCommparaInfoBy5("CSM", "9957", prodId, packId, "0898", null);
    	if(comms!=null && comms.size()>0){
    		
    		String checkErrorInfo="";
    		IData saleactiveData = new DataMap();
 	        saleactiveData.put("SERIAL_NUMBER", serialNum);
 	        saleactiveData.put("PRODUCT_ID", prodId);
 	        saleactiveData.put("PACKAGE_ID", packId);
 	        saleactiveData.put("PRE_TYPE", "1");
 	        try{
 	        	IDataset actives=CSAppCall.call("SS.SaleActiveRegSVC.tradeReg4Intf", saleactiveData); 
 	        }catch(Exception e){
	    		String error =  Utility.parseExceptionMessage(e);
	    		String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
				if(errorArray.length >= 2)
				{
					String strException = errorArray[0];
					String strExceptionMessage = errorArray[1];
					data.put("IMPORT_ERROR", "号码【"+serialNum+"】营销活动校验失败:"+strExceptionMessage);
				}
				else
				{
					data.put("IMPORT_ERROR", "号码【"+serialNum+"】营销活动校验失败:"+error);
				}  
                
                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【"+serialNum+"】营销活动校验失败:"+error);
 	        }
 	        String userId="";
 	        try {
 	            UcaData uca = UcaDataFactory.getNormalUca(serialNum);
 	            userId= uca.getUserId();  
 	        } catch (Exception e) {
 	    	  data.put("IMPORT_ERROR", "号码【"+serialNum+"】不存在用户信息。");
 	        } 
 	        if(userId==null || "".equals(userId)){
 	        	//CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【"+serialNum+"】不存在用户信息。");
 	        	data.put("IMPORT_ERROR", "号码【"+serialNum+"】不存在用户信息。");
 	        }
 	        String newProdId=comms.getData(0).getString("PARA_CODE3");
	        String newPackId=comms.getData(0).getString("PARA_CODE4");
	        
 	        String checkUserId="";
 	        try {
	            UcaData uca = UcaDataFactory.getNormalUca(checkSN);
	            checkUserId= uca.getUserId();  
	        } catch (Exception e) {
	        	data.put("IMPORT_ERROR", "号码【"+checkSN+"】不存在用户信息。");
	        } 
	        if(checkUserId==null || "".equals(checkUserId)){
	        	//CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【"+serialNum+"】不存在用户信息。");
	        	data.put("IMPORT_ERROR", "号码【"+checkSN+"】不存在用户信息。");
	        }
 	       
	        String mainNumCheckTag="1";//主号校验状态 主号没问题才校验新号码
 	        if(data!=null && !data.isEmpty()){
 	        	mainNumCheckTag="9";
 	        }else{
 	        	IData params=new DataMap(); 
 	 	        params.put("CHECK_SERIAL_NUMBER", checkSN);
 	 	        params.put("PRODUCT_ID", prodId);
 	 	        params.put("PACKAGE_ID", packId);
 	 	        params.put("USER_ID", userId);
 	 	        params.put("AUTH_SERIAL_NUMBER", serialNum); 
 	 	        params.put("MAIN_SERINUM_CHECK_TAG", mainNumCheckTag);
 	 	        params.put("BAT_TAG", "TRUE");
 	 	        IDataset newUserChecks=CSAppCall.call("SS.SaleActiveCheckSnSVC.check2017ActiveNewUser", params);
 	 	        if(newUserChecks!=null && newUserChecks.size()>0){
 	 	        	/**
 	 		 		 * * 新号码校验：
 	 			     * 1、是否办理过该活动   SN_HAVE_PRODUCT  Y:办理过   N:没办理过
 	 			     * 2、0存折不能大于0  SN_FEE_TYPE    Y:大于0   N：不大于0
 	 			     * 3、不能是多终端共享业务数据     SHARE_INFO_TYPE  Y:属于   N:不属于
 	 			     * 4、不能统一付费业务数据。          RELATION_UU_TYPE  Y:属于   N:不属于
 	 			     * 5、与新号码的身份证是否相同      PSPT_ID_SAME Y:相同    N：不相同
 	 		 		 * */
 	 	            String haveTag=newUserChecks.getData(0).getString("SN_HAVE_PRODUCT");
 	 	            String feeTag =newUserChecks.getData(0).getString("SN_FEE_TYPE");
 	 	            String shareTag =newUserChecks.getData(0).getString("SHARE_INFO_TYPE");
 	 	            String relaTag=newUserChecks.getData(0).getString("RELATION_UU_TYPE");
 	 	            String psptIdTag=newUserChecks.getData(0).getString("PSPT_ID_SAME");
 	 	            String open48tag=newUserChecks.getData(0).getString("OPEN_48_HOUR");
 	 	            String checkTag="1";
 	 	            String checkErrInfo="";
 	 	            if("N".equals(open48tag)){
 	 	            	checkErrInfo="批量办理该活动的用户手机号必须是未激活的号码，该号码不满足条件！";
 	 	            	checkTag="0";
 	 	            }
 	 	            if("Y".equals(haveTag)){
 	 	        		checkErrInfo=checkErrInfo+"&&输入的号码已经办理过该活动，不能再次办理！";
 	 	            	checkTag="0";
 	 	        	}
 	 	        	if("Y".equals(shareTag)){
 	 	        		checkErrInfo=checkErrInfo+"&&输入的号码办理过多终端共享业务，不能办理该活动！";
 	 	            	checkTag="0";
 	 	        	}
 	 	        	if("Y".equals(relaTag)){
 	 	        		checkErrInfo=checkErrInfo+"&&输入的号码办理过统一付费业务，不能办理该活动！";
 	 	            	checkTag="0";
 	 	        	}
 	 	        	if("N".equals(psptIdTag)){
 	 	        		checkErrInfo=checkErrInfo+"&&输入的号码在新号码网上预约工单中填的证件号与主号码的身份证不一致，不能办理该活动！";
 	 	            	checkTag="0";
 	 	        	}
 	 	        	if("Y".equals(feeTag)){
 	 	        		checkErrInfo=checkErrInfo+"&&输入的号码预存款大于0，不能办理该活动！";
 	 	            	checkTag="0";
 	 	        	} 
 	 	        	if("0".equals(checkTag)){
 	 	        		data.put("IMPORT_ERROR", data.getString("IMPORT_ERROR","")+"&&号码【"+checkSN+"】校验不通过："+checkErrInfo);
 	 	        	}
 	 	        } 
 	        } 
    	}
    	return data;
    }
    
    public IData fileImportCheckForBatActiveCancel(IData pdData, IDataset dataset) throws Exception
    {
    	/**
    	 * 批量营销活动返销
    	 * chenxy3 20160127
    	 * */
    	IData returnData = new DataMap();
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        
        String taskId=pdData.getString("BATCH_TASK_ID");
    	String month=SysDateMgr.getSysDateYYYYMMDDHHMMSS().substring(4, 6);
    	IData selData=new DataMap();
    	selData.put("BATCH_TASK_ID", taskId);
    	selData.put("ACCEPT_MONTH", Integer.parseInt(month));
    	BatActiveCancelBean bean= BeanManager.createBean(BatActiveCancelBean.class);
        IDataset results = bean.queryBatTaskInfo(selData); 
        if(results!=null && results.size()>0){
        	IData codStr1=new DataMap(results.getData(0).getString("CODING_STR1"));
        	if(!"".equals(codStr1)){
        		String campnType=codStr1.getString("SALE_CAMPN_TYPE");
            	String prodId=codStr1.getString("SALE_PRODUCT_ID");
            	String packId=codStr1.getString("SALE_PACKAGE_ID");
        		for(int i = 0; i < dataset.size(); i++){
                	IData data=dataset.getData(i);
                	String serialNum=data.getString("SERIAL_NUMBER"); 
                	IData saleData=new DataMap();
                	saleData.put("SERIAL_NUMBER", serialNum); 
                	saleData.put("CAMPN_TYPE", campnType); 
                	saleData.put("PRODUCT_ID", prodId); 
                	saleData.put("PACKAGE_ID", packId); 
                	IDataset userSaleActiveInfo=CSAppCall.call("SS.BatActiveCancelSVC.queryUserSaleActiveInfo", saleData);//this.queryUserSaleActiveInfo(saleData); 
                	
                	if(userSaleActiveInfo!=null &&userSaleActiveInfo.size()>0){
                		succds.add(data);
                	}else{
                		data.put("IMPORT_ERROR", "该号码找不到选择的营销活动返销!");
                        failds.add(data);
                	}
                }
        	}else{
        		failds=dataset;
        	}
        }else{
        	failds=dataset;
        }
        
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        
        return returnData;
    } 
    
    /**
	 * 批量添加行业应用卡标识
	 * zhangxing3
	 * */
    public IData fileImportCheckForBatCreateM2MTag(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        
        for (int i = 0; i < dataset.size(); i++)
        {
        	IData data = new DataMap();
        	data = dataset.getData(i); 
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }

            if (!StringUtils.isNumeric(data.getString("SERIAL_NUMBER","")))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!"+data.getString("SERIAL_NUMBER"));
                failds.add(data);
                continue;
            }
            String serialNumber = data.getString("SERIAL_NUMBER","");
            try
            {

            UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);          
            
            String userId = ucaData.getUserId();
            
            //0.已打行业应用卡标识,不需要再处理!
            IData m2mInfos = UserOtherInfoQry.qryUserOthInfoForGrp("HYYYKBATCHOPEN",userId);
            if(IDataUtil.isNotEmpty(m2mInfos))
            {
            	data.put("IMPORT_ERROR", "该用户已打行业应用卡标识,不需要再处理!");
            	failds.add(data);
            	continue;
            }
            //1.非实名制,不打标记.
	        String isRealName = ucaData.getCustomer().getIsRealName();
	        if (!"1".equals(isRealName))
	        {
	        	data.put("IMPORT_ERROR", "非实名制用户不允许打行业应用卡标识!");
	        	failds.add(data);
                continue;
	        }

	        
	       
	        //2.证件类型：个人证件开户，则证件类型只包括本地身份证、外地身份证、护照；
	        //           集团证件开户，则证件类型只包括单位证明、营业执照、事业单位法人证书、社会团体法人登记证书和组织机构代码证
	        String psptTypeCode = ucaData.getCustomer().getPsptTypeCode();
	        if (!"0".equals(psptTypeCode) && !"1".equals(psptTypeCode) && !"A".equals(psptTypeCode) && !"D".equals(psptTypeCode) 
	        		&& !"E".equals(psptTypeCode)&& !"G".equals(psptTypeCode) && !"L".equals(psptTypeCode) && !"M".equals(psptTypeCode) )
	        {
	        	data.put("IMPORT_ERROR", "证件类型非本地身份证、外地身份证、护照及集团证件,不打行业应用卡标识!");
            	failds.add(data);
                continue;
	        }
	        //3.如果开户证件类型是集团证件，则判断经办人、责任人信息是否均已录入，包含姓名（或单位名称）、证件类型、证件号码、地址四个信息，
	        //  且证件类型只能是身份证或者护照，经办人、责任人证件号码不能相同。如果不满足，不能打标识。 
	        if ( "D".equals(psptTypeCode) || "E".equals(psptTypeCode)|| "G".equals(psptTypeCode) 
	        		|| "L".equals(psptTypeCode) || "M".equals(psptTypeCode))
	        {
	        	//经办人资料
	        	String custName1=ucaData.getCustomer().getRsrvStr7();
	        	String psptType1=ucaData.getCustomer().getRsrvStr8();
	        	String psptID1=ucaData.getCustomer().getRsrvStr9();
	        	String psptAddr1=ucaData.getCustomer().getRsrvStr10();
	        	if(StringUtils.isBlank(custName1) || StringUtils.isBlank(psptType1) 
	        			|| StringUtils.isBlank(psptID1)|| StringUtils.isBlank(psptAddr1))
	        	{
	        		data.put("IMPORT_ERROR", "集团证件开户,经办人资料未完全录入,不打行业应用卡标识!");
	            	failds.add(data);
	                continue;
	        	}
	        	
	        	//责任人资料
	        	IDataset custPersonother = CustPersonInfoQry.qryCustPersonOtherByCustRouteId(ucaData.getCustId());
	        	if(IDataUtil.isEmpty(custPersonother))
	        	{
	        		data.put("IMPORT_ERROR", "集团证件开户,责任人资料不存在,不打行业应用卡标识!");
	            	failds.add(data);
	                continue;
	        	}
	        	String custName2=custPersonother.getData(0).getString("RSRV_STR2", "");
	        	String psptType2=custPersonother.getData(0).getString("RSRV_STR3", "");
	        	String psptID2=custPersonother.getData(0).getString("RSRV_STR4", "");
	        	String psptAddr2=custPersonother.getData(0).getString("RSRV_STR5", "");
	        	if(StringUtils.isBlank(custName2) || StringUtils.isBlank(psptType2)
	        			|| StringUtils.isBlank(psptID2)|| StringUtils.isBlank(psptAddr2))
	        	{
	        		data.put("IMPORT_ERROR", "集团证件开户,责任人资料未完全录入,不打行业应用卡标识!");
	            	failds.add(data);
	                continue;
	        	}
	        	
	        	if( psptID1.equals(psptID2) )
	        	{
	        		data.put("IMPORT_ERROR", "集团证件开户,经办人、责任人证件号码不能相同,不打行业应用卡标识!");
	            	failds.add(data);
	                continue;
	        	}
	        	
	        }
	        	        	
	        }catch (Exception e)
            {
                //System.out.print("---------zx1794-----------"+e.getLocalizedMessage()+e.getMessage()+e.getCause()+e.getClass()+e.getStackTrace());
	        	data.put("IMPORT_ERROR", "查询用户资料异常！"+e.getMessage()+e.getStackTrace()+e.getCause());
                failds.add(data);
                continue;
            }
	        
            succds.add(data);
        }
 

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;

    }
    
 public IData fileImportCheckForBatMobileStop(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
        	IData data = new DataMap();
        	data = dataset.getData(i); 
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(data.getString("SERIAL_NUMBER")))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            if (StringUtils.isBlank(data.getString("DATA1")))
            {
                data.put("IMPORT_ERROR", "业务类型不能为空!");
                failds.add(data);
                continue;
            }
            IDataset commparaInfos9688 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9688",data.getString("DATA1"),null);
            if(IDataUtil.isEmpty(commparaInfos9688)){
            	data.put("IMPORT_ERROR", "该业务类型不支持!");
                failds.add(data);
                continue;
            }
            if (StringUtils.isBlank(data.getString("DATA2")))
            {
                data.put("IMPORT_ERROR", "OA工单编号不能为空!");
                failds.add(data);
                continue;
            }
            if (StringUtils.isBlank(data.getString("DATA3")))
            {
                data.put("IMPORT_ERROR", "停机销号原因不能为空!");
                failds.add(data);
                continue;
            }
            if (StringUtils.isBlank(data.getString("DATA4")))
            {
                data.put("IMPORT_ERROR", "备注不能为空!");
                failds.add(data);
                continue;
            }
            IDataset ReduserInfo = UserInfoQry.getTfOReduserInfo(data);
            if(IDataUtil.isNotEmpty(ReduserInfo)){
            	data.put("IMPORT_ERROR", "红名单号码不能做停机业务！");
                failds.add(data);
            	if(StringUtils.isNotBlank(ReduserInfo.getData(0).getString("CONTACT_PHONE"))){
            		try{
            			System.out.println("CONTACT_PHONE"+ReduserInfo.getData(0).getString("CONTACT_PHONE"));
            			UcaData ucaData = UcaDataFactory.getNormalUca(ReduserInfo.getData(0).getString("CONTACT_PHONE")); //可能输入不存在数据的用户导致报错
            			IData sms = new DataMap();
                		sms.put("RECV_OBJECT", ReduserInfo.getData(0).getString("CONTACT_PHONE"));
                		sms.put("RECV_ID", ucaData.getUserId());
                		sms.put("BRAND_CODE", "");
                		sms.put("DEAL_STAFFID", getVisit().getStaffId());
                		sms.put("DEAL_DEPARTID", getVisit().getDepartId());
                		sms.put("REVC1", "");
                		sms.put("REVC2", "");
                		sms.put("REVC3", "");
                		sms.put("REVC4", "");
                		String content = "您申请的红名单号码"+data.getString("SERIAL_NUMBER")+"现由于"+data.getString("DATA3")+"原因" +
                				"正在被"+getVisit().getStaffId()+"操作停机或销号，请及时联系处理人核实确认，如需停机请先移除红名单后再进行操作！" ;
                		sms.put("NOTICE_CONTENT", content);
                		sms.put("REMARK", "红名单批量停机提醒");
                        SmsSend.insSms(sms,RouteInfoQry.getEparchyCodeBySn(ReduserInfo.getData(0).getString("CONTACT_PHONE")));
     	 	        }catch(Exception e){
     	 	        	log.info("停机短信发送失败："+e);
     	 	        	continue;
     	 	        }
            	}
                continue;
            }
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;
    }
    public IData fileImportCheckForBatRealName(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        IData    data0 = dataset.getData(0);
        
        for (int i = 0; i < dataset.size(); i++)
        {
        	IData data = new DataMap();
        	
        	
        	//如果是一证多号功能需要把第一行的实名制数据信息配置到其他行
            if("是".equals(data0.getString("DATA12")) && (0 != i))
            {
            	IData tempData = new DataMap(data0);
            	tempData.put("SERIAL_NUMBER", dataset.getData(i).getString("SERIAL_NUMBER"));
            	data = tempData;
            }
            else
            {
            	data = dataset.getData(i);  
            	if(null == data.getString("DATA1") || null == data.getString("DATA2") ||
            			null == data.getString("DATA3") || null == data.getString("DATA4") ||
            			null == data.getString("DATA5") || null == data.getString("DATA6"))
            	{
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户资料不能为空，如果是一证多号，请选是，谢谢!");
            	}
            }


            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(data.getString("SERIAL_NUMBER")))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            String strPSPTType = data.getString("DATA2");
            String strAgentPSPTType = data.getString("DATA8");
            String strUserPSPTType=data.getString("DATA15");// add for fufn -REQ201703070003
            if(("是".equals(data0.getString("DATA12")) && (0 == i)) || "否".equals(data0.getString("DATA12")))
            {

                 String strPSPTTypeCode = StaticUtil.getStaticValue(null,"TD_S_PASSPORTTYPE", new String []{"PSPT_TYPE","EPARCHY_CODE"}, "PSPT_TYPE_CODE", new String []{strPSPTType, "0898"});
                 String strAgentPSPTTypeCode = StaticUtil.getStaticValue(null,"TD_S_PASSPORTTYPE", new String []{"PSPT_TYPE","EPARCHY_CODE"}, "PSPT_TYPE_CODE", new String []{strAgentPSPTType, "0898"});
                 String strUserPSPTTypeCODE = StaticUtil.getStaticValue(null,"TD_S_PASSPORTTYPE", new String []{"PSPT_TYPE","EPARCHY_CODE"}, "PSPT_TYPE_CODE", new String []{strUserPSPTType, "0898"});
                 // add for fufn -REQ201703070003
                 if( "".equals(strPSPTTypeCode) || "".equals(strAgentPSPTTypeCode)|| "".equals(strUserPSPTTypeCODE))
                 {
                 	CSAppException.apperr(CrmCommException.CRM_COMM_103, "证件类型不正确，请检查导入文件!");
                 }
                 data.put("DATA2", strPSPTTypeCode);
                 data.put("DATA8", strAgentPSPTTypeCode);
                 data.put("DATA15", strUserPSPTTypeCODE);// add for fufn -REQ201703070003
            }
           
            
            //DATA2、DATA8是证件类型
            String psptTypeCode = data.getString("DATA2", "").trim();
            
            if(StringUtils.equals("E",psptTypeCode )||StringUtils.equals("G",psptTypeCode )||StringUtils.equals("M",psptTypeCode )||StringUtils.equals("D",psptTypeCode )||StringUtils.equals("L",psptTypeCode ))
            {
            	//如果是集团证件，经办人信息不能为空,data7至data11存放经办人信息，data10字段用于存放productId，不能存放数据
            	if((null == data.getString("DATA7")) || (null == data.getString("DATA8")) || (null== data.getString("DATA9")) || (null == data.getString("DATA11")))
            	{
            		data.put("IMPORT_ERROR", "集团类证件，经办人信息不能为空!");
                    failds.add(data);
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "CUST_NAME = " + data.getString("DATA1", "") + " 集团类证件，经办人信息不能为空!");
            	}
            	//Begin: REQ201703070003 批量实名制登记业务优化--集团证件，使用人信息不能为空,data14至data16存放使用人信息   add for fufn
            	if((null == data.getString("DATA14")) || (null == data.getString("DATA15")) || (null== data.getString("DATA16")))
            	{
            		data.put("IMPORT_ERROR", "集团类证件，使用人信息不能为空!");
                    failds.add(data);
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "CUST_NAME = " + data.getString("DATA1", "") + " 集团类证件，使用人信息不能为空!");
            	}
            	//End: REQ201703070003 批量实名制登记业务优化--集团证件，使用人信息不能为空,data14至data16存放使用人信息
                if(data.getString("DATA13","").trim().equals("")){//出生日期
                    data.put("DATA13", "1900-01-01");
                }
            } else {
                if (data.getString("DATA13", "").trim().equals("")) {
                    if (StringUtils.equals("0", psptTypeCode) || StringUtils.equals("1", psptTypeCode) || StringUtils.equals("2", psptTypeCode)) {//本地、外地身份、户口
                        String psptId = data.getString("DATA4", "").trim();
                        String tmpStr = "";
                        if (psptId.length() == 15 || psptId.length() == 18) {
                            if (psptId.length() == 15) {
                                tmpStr = "19" + psptId.substring(6, 12);
                            } else {
                                tmpStr = psptId.substring(6, 14);
                            }
                            String birthday = tmpStr.substring(0, 4) + "-" + tmpStr.substring(4, 6) + "-" + tmpStr.substring(6);
                            data.put("DATA13", birthday);
                        }else{
                            data.put("IMPORT_ERROR", "个人类证件，出生日期必须填写!");
                            data.put("DATA2", strPSPTType);
                            data.put("DATA8", strAgentPSPTType);
                            failds.add(data);
                            continue;                        
                        }
                    } else {
                        data.put("IMPORT_ERROR", "个人类证件，出生日期必须填写!");
                        data.put("DATA2", strPSPTType);
                        data.put("DATA8", strAgentPSPTType);
                        failds.add(data);
                        continue;
                    }
                }
            }
                        
            succds.add(data);
        }
 

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;

    }
    /**
     * @author fufn
     * @param pdData
     * @param dataset
     * @return 批量密码修改密码校验
     * @throws Exception
     */
    public IData fileImportCheckForBatUpdatePsw(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        
        for (int i = 0; i < dataset.size(); i++)
        {
        	IData data = new DataMap();        	        	
        	
        	data = dataset.getData(i);  
        	String passWordStr=data.getString("DATA1");
        	String serialNumber=data.getString("SERIAL_NUMBER");
        	UcaData ucaData=null;
        	try{
        		 ucaData = UcaDataFactory.getNormalUca(serialNumber);  
        	}catch(Exception e1){
        		
        		if(e1.getMessage().indexOf("CRM_BOF_002")>=0){
        			data.put("IMPORT_ERROR", "根据手机号码未找到用户资料");
                    failds.add(data);
                    continue;
        		}else{
        			throw new Exception(e1);
        		}
        		
        	}
        	UserTradeData userData=ucaData.getUser().clone();

        	if(!userData.getRemoveTag().equals("0")){
        		data.put("IMPORT_ERROR", "服务号码状态不正常!");
                failds.add(data);
                continue;
        	}else if(!"HNSJ".equals(userData.getCityCode()) && !"HNHN".equals(userData.getCityCode())){
        		if(ucaData.getBrandCode().equals("TDYD") ){

        		}else{
        			data.put("IMPORT_ERROR", "只能批量修改号码业务区为HNSJ或HNHN的服务号码!");
                    failds.add(data);
                    continue;
        		}
        	}else if(passWordStr.length()<6){
        		data.put("IMPORT_ERROR", "服务密码长度不足6位!");
                failds.add(data);
                continue;
        	}
        	//判断是否递增或递减的序列号
    		boolean isSerialCode=true;
    		for(int k=0; k<passWordStr.length()-1; k++){
				int step = Integer.parseInt(""+passWordStr.charAt(k+1))-Integer.parseInt(""+passWordStr.charAt(k));
				if(step != 1 && step !=-1){
					isSerialCode = false;
					break;
				}
			}
    		if(isSerialCode){
    			data.put("IMPORT_ERROR", "服务密码不能是递增或递减的序列!");
                failds.add(data);
                continue;
    		}

    		
    		boolean isRepeatCode = true;
			for(int k=0; k<passWordStr.length()-1; k++){
				if(passWordStr.charAt(k+1) != passWordStr.charAt(k)){
					isRepeatCode = false;
					break;
				}
			}
			if(isRepeatCode){
    			data.put("IMPORT_ERROR", "服务密码不能是重复的连续数字!");
                failds.add(data);
                continue;
    		}
			
			

			boolean isHalfSame = false;
			int len = (int) Math.floor(passWordStr.length()/2);
			int mod = (passWordStr.length()%2==0)?0:1;
			String prefix = passWordStr.substring(0, len);
			String suffix = passWordStr.substring(mod+len);
			if(prefix.equals(suffix)){
				isHalfSame = true;
			}
			if(isHalfSame){
    			data.put("IMPORT_ERROR", "服务密码的前后三位一致!!");
                failds.add(data);
                continue;
    		}
			
			
			int repeatCount = 0;
			String target = "_";
			String sub = "";
			for(int k=0; k<passWordStr.length(); k++){
				sub = ""+passWordStr.charAt(k);
				if(target.indexOf(sub) == -1){
					repeatCount++;
					target += sub+"_";
				}
			};
			if(repeatCount<4){
				data.put("IMPORT_ERROR", "服务密码不同数字数必须大于3个!");
                failds.add(data);
                continue;
			}
			
			
//			if(!$.verifylib.checkPInteger(code)){
//				return false;
//			}
			int count = 0;
			int number = 0;
			for(int k=0; k<passWordStr.length(); k++){
				number = Integer.parseInt(""+passWordStr.charAt(k));
				count += (number%2==0)?1:0;
			}
			if(!(count==passWordStr.length() || count!=0)){
				data.put("IMPORT_ERROR", "服务密码不能全为偶数或奇数!");
                failds.add(data);
                continue;
			}
			//add by fufn REQ201710120004
			boolean prefix_flag = true;
			boolean suffix_flag = true;
			int lenArithmetic =(new Double(Math.floor(passWordStr.length()/2))).intValue();
			int modArithmetic=0;
			if(passWordStr.length()%2!=0){
				modArithmetic=1;
			}
			String prefixlenArithmetic = passWordStr.substring(0, lenArithmetic);
			String suffixlenArithmetic = passWordStr.substring(modArithmetic+lenArithmetic);
			for(int z=0; z<(prefixlenArithmetic.length()-2); z++){
				if((Integer.parseInt(""+prefixlenArithmetic.charAt(z))-Integer.parseInt(""+prefixlenArithmetic.charAt(z+1)))
						!=(Integer.parseInt(""+prefixlenArithmetic.charAt(z+1))-Integer.parseInt(""+prefixlenArithmetic.charAt(z+2))))
				{
					prefix_flag=false;
					break;
				}
			}//前半段校验
			for(int y=0; y<(suffixlenArithmetic.length()-2); y++){
				if((Integer.parseInt(""+suffixlenArithmetic.charAt(y))-Integer.parseInt(""+suffixlenArithmetic.charAt(y+1)))
						!=(Integer.parseInt(""+suffixlenArithmetic.charAt(y+1))-Integer.parseInt(""+suffixlenArithmetic.charAt(y+2))))
				{
					suffix_flag=false;
					break;
				}
			}//后半段校验
			if(prefix_flag&&suffix_flag){
				data.put("IMPORT_ERROR", "服务密码不能前三位后三位都是等差数列!");
                failds.add(data);
                continue;
			}
			String psptId=ucaData.getCustomer().getPsptId();
			if(psptId.indexOf(passWordStr)>-1){
				data.put("IMPORT_ERROR", "身份证件的连续数字不能作为服务密码!");
                failds.add(data);
                continue;
			}
			
			if(serialNumber.indexOf(passWordStr)>-1){
				data.put("IMPORT_ERROR", "手机号码中的连续数字不能作为服务密码!");
                failds.add(data);
                continue;
			}
			
			
			String prefix1 = serialNumber.substring(0, 3);			
			String suffix1 = serialNumber.substring(serialNumber.length()-3);
			if((prefix1+suffix1).equals(passWordStr) || (suffix1+prefix1).equals(passWordStr)){
				data.put("IMPORT_ERROR", "手机号码中前三位+后三位或后三位+前三位的组合不能作为服务密码!");
                failds.add(data);
                continue;
			}
			
            succds.add(data);
        }
        
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        
        return returnData;

    }
    /**
     * @author fufn
     * @param pdData
     * @param dataset
     * @return 批量销户校验
     * @throws Exception
     */
    public IData fileImportCheckForBatDestroyUser(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        if(dataset.size()>0){
    		IData data0 = null;
	   	 	String user0SerialNumber=null;
		 	UcaData ucaData0 = null; 
		 	String user0PsptTypeCode=null;//实际处理的第一位证件类型
		 	String user0PsptId=null;
       	    
//        	 IData data0 = dataset.getData(0);
//        	 String user0SerialNumber=data0.getString("SERIAL_NUMBER");
//        	 UcaData ucaData0 = UcaDataFactory.getNormalUca(user0SerialNumber); 
//        	 String user0PsptTypeCode=ucaData0.getCustomer().getPsptTypeCode();//第一位证件类型
//        	 String user0PsptId=ucaData0.getCustomer().getPsptId();
//		 	if(!StringUtils.equals("E",user0PsptTypeCode )&&!StringUtils.equals("G",user0PsptTypeCode )&&!StringUtils.equals("M",user0PsptTypeCode )
//        			 &&!StringUtils.equals("D",user0PsptTypeCode )&&!StringUtils.equals("L",user0PsptTypeCode ))
//		 	{
//		 		CSAppException.apperr(CrmCommException.CRM_COMM_103, "第一个号码证件类型不是单位证件,批量销户仅支持单位证件!");
//		 	}
        	
		 	for (int i = 0; i < dataset.size(); i++)
		 	{
		 		IData data = new DataMap();
             	
             	data = dataset.getData(i);  
//             	String passWordStr=data.getString("DATA1");
             	String serialNumber=data.getString("SERIAL_NUMBER");
             	UcaData ucaData=null;
             	try{
             		ucaData = UcaDataFactory.getNormalUca(serialNumber); //可能输入不存在数据的用户导致报错
 	 	        }catch(Exception e){
 		    		String error =  Utility.parseExceptionMessage(e);
 		    		String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
 					if(errorArray.length >= 2)
 					{
 						String strException = errorArray[0];
 						String strExceptionMessage = errorArray[1];
 						data.put("IMPORT_ERROR", "号码【"+serialNumber+"】销户校验失败:"+strExceptionMessage);
 					}
 					else
 					{
 						data.put("IMPORT_ERROR", "号码【"+serialNumber+"】销户校验失败:"+error);
 					}  
 					failds.add(data);
                     continue;
 	                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【"+serialNum+"】营销活动校验失败:"+error);
 	 	        }
             	if(ucaData==null){
             		data.put("IMPORT_ERROR", "获取用户数据出错!");
             		failds.add(data);
             		continue;
             	}
				String userPsptTypeCode=ucaData.getCustomer().getPsptTypeCode();//证件类型
				String userPsptId=ucaData.getCustomer().getPsptId();
				String removeReasonCodeName = data.getString("DATA1","");
 	        	String removeReasonCode = StaticUtil.getStaticValue(null,"TD_B_REMOVE_REASON", new String []{"REMOVE_REASON"}, "REMOVE_REASON_CODE", new String []{removeReasonCodeName});
				if( removeReasonCode==null||"".equals(removeReasonCode) )
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "电话【"+serialNumber+"】销户原因为空或不正确，请检查导入文件!");
				}
				data.put("DATA1", removeReasonCode);
				
				
				if(user0PsptTypeCode!=null&&user0PsptId!=null){
					if(!StringUtils.equals(userPsptId,user0PsptId)){
						data.put("IMPORT_ERROR", "批量销户只能处理相同证件的号码，该号码证件为："+userPsptTypeCode+"!实际处理的第一个号码为："+user0SerialNumber+",证件号码为："+user0PsptId);
	                    failds.add(data);
	                    continue;
					}else if(!StringUtils.equals(userPsptTypeCode,user0PsptTypeCode)){
						data.put("IMPORT_ERROR", "批量销户只能处理相同证件的号码，该号码证件类型为："+userPsptTypeCode+"!实际处理的第一个号码为："+user0SerialNumber+",证件类型为："+user0PsptTypeCode);
	                    failds.add(data);
	                    continue;
					}
				}else{
					String  strPsptTypeName=data.getString("DATA3","");//证件类型
		            String  strPsptTypeCode= StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_PASSPORTTYPE", 
		            		new String []{"PSPT_TYPE","EPARCHY_CODE"}, "PSPT_TYPE_CODE", new String []{strPsptTypeName, "0898"});
		            if(strPsptTypeCode!=null&&!strPsptTypeCode.equals("")){
		            	if(!StringUtils.equals(userPsptTypeCode,strPsptTypeCode)){
		            		data.put("IMPORT_ERROR", "该号码所填证件类型与系统登记的证件类型不一致!");
		                    failds.add(data);
		                    continue;
		            	}
		            }else{
		            	data.put("IMPORT_ERROR", "该号码证件类型为空或无法识别，请检查导入文件!");
	                    failds.add(data);
	                    continue;
		            }
		            String  strPsptTypeId=data.getString("DATA4","");//证件号码
		            if(!StringUtils.equals(strPsptTypeId,userPsptId)){
		            	data.put("IMPORT_ERROR", "该号码证件号码与系统登记的证件号码不匹配!");
	                    failds.add(data);
	                    continue;
		            }
				}
				
	            
				if(!StringUtils.equals("E",userPsptTypeCode )&&!StringUtils.equals("G",userPsptTypeCode )&&!StringUtils.equals("M",userPsptTypeCode )
	        			 &&!StringUtils.equals("D",userPsptTypeCode )&&!StringUtils.equals("L",userPsptTypeCode ))
				{
					data.put("IMPORT_ERROR", "该号码证件类型不是单位证件,批量销户仅支持单位证件!");
					failds.add(data);
					continue;
//	        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码证件类型不是单位证件,批量销户仅支持单位证件!");
				}
				
				IData saleactiveData = new DataMap();
	 	        saleactiveData.put("SERIAL_NUMBER", serialNumber);
	 	        saleactiveData.put("PASSWD_TYPE", "2");
	 	        saleactiveData.put("X_MANAGEMODE", "");
	 	        saleactiveData.put("TRADE_TYPE_CODE","192");
	 	        saleactiveData.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
	 	        saleactiveData.put("PRE_TYPE", "1");
	 	        try{
	 	        	CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", saleactiveData); 
	 	        }catch(Exception e){
		    		String error =  Utility.parseExceptionMessage(e);
		    		String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
					if(errorArray.length >= 2)
					{
						String strException = errorArray[0];
						String strExceptionMessage = errorArray[1];
						data.put("IMPORT_ERROR", "号码【"+serialNumber+"】销户校验失败:"+strExceptionMessage);
					}
					else
					{
						data.put("IMPORT_ERROR", "号码【"+serialNumber+"】销户校验失败:"+error);
					}  
					failds.add(data);
                    continue;
	                //CSAppException.apperr(CrmCommException.CRM_COMM_103, "号码【"+serialNum+"】营销活动校验失败:"+error);
	 	        }
	 	        if(data0==null||user0SerialNumber==null||ucaData0==null||user0PsptTypeCode==null||user0PsptId==null){//当确认可执行后才确定其为实际第一个可执行号码
	        		 data0 = data;
	            	 user0SerialNumber=serialNumber;
	            	 ucaData0 = ucaData; 
	            	 user0PsptTypeCode=userPsptTypeCode;//第一位证件类型
	            	 user0PsptId=userPsptId;
		            	 
				}
				succds.add(data);
             }
        }

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;

    }
    
    /**
     * @author guonj
     * @param pdData
     * @param dataset
     * @return 批量企业宽带套餐变更
     * @throws Exception
     */
    public IData fileImportWidePackageChange(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        //System.out.println("=======fileImportWideProductChange=========dSize:"+dSize);
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER","");
            String productId = data.getString("PRODUCT_ID","");
            String packageId = data.getString("PACKAGE_ID","");
            data.put("DATA1", productId);
            data.put("DATA2", packageId);
            //System.out.println("=======fileImportWideProductChange=========data1:"+data);

            boolean importResult = data.getBoolean("IMPORT_RESULT", true);

            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(productId))
            {
                data.put("IMPORT_ERROR", "宽带产品格式不对!");
                failds.add(data);
                continue;
            }
            
            try 
            {
            	UcaDataFactory.getNormalUca("KD_"+mainSN);
            	data.put("SERIAL_NUMBER", "KD_"+mainSN);
			} 
            catch (Exception e) 
            {
            	log.info(e);
				data.put("IMPORT_ERROR", "服务号码无效!");
                failds.add(data);
                continue;
			}
            
/*            try 
            {
            	UcaData uca = UcaDataFactory.getNormalUca(mainSN);
            	String userId = uca.getUserId();
            	IDataset ids = UserDiscntInfoQry.getUserCOMDiscnt(userId,"9711","RHTC",SysDateMgr.getSysTime(),"0898");
            	if (IDataUtil.isEmpty(ids))
            	{
            		data.put("IMPORT_ERROR", "用户没有宽带权益主套餐，不能办理批量宽带产品变更！");
                    failds.add(data);
                    continue;
            	}
			} 
            catch (Exception e) 
            {
            	log.info(e);
				data.put("IMPORT_ERROR", "查询用户优惠异常!");
                failds.add(data);
                continue;
			}*/
            
            	
            //System.out.println("=======fileImportWideProductChange=========data2:"+data);
            succds.add(data);
        }
        //System.out.println("=======fileImportWideProductChange=========succds:"+succds);
        //System.out.println("=======fileImportWideProductChange=========failds:"+failds);

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    /**
     * @author zhangxing3
     * @param pdData
     * @param dataset
     * @return 批量宽带产品变更
     * @throws Exception
     */
    public IData fileImportWideProductChange(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        System.out.println("=======fileImportWideProductChange=========dSize:"+dSize);
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER","");
            String productId = data.getString("PRODUCT_ID","");
            String packageId = data.getString("PACKAGE_ID","");
            data.put("DATA1", productId);
            data.put("DATA2", packageId);
            //System.out.println("=======fileImportWideProductChange=========data1:"+data);

            boolean importResult = data.getBoolean("IMPORT_RESULT", true);

            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(productId))
            {
                data.put("IMPORT_ERROR", "宽带产品格式不对!");
                failds.add(data);
                continue;
            }

            try
            {
            	UcaData uca = UcaDataFactory.getNormalUca("KD_"+mainSN);
			}
            catch (Exception e)
            {
            	log.info(e);
				data.put("IMPORT_ERROR", "服务号码无效!");
                failds.add(data);
                continue;
			}

/*            try
            {
            	UcaData uca = UcaDataFactory.getNormalUca(mainSN);
            	String userId = uca.getUserId();
            	IDataset ids = UserDiscntInfoQry.getUserCOMDiscnt(userId,"9711","RHTC",SysDateMgr.getSysTime(),"0898");
            	if (IDataUtil.isEmpty(ids))
            	{
            		data.put("IMPORT_ERROR", "用户没有宽带权益主套餐，不能办理批量宽带产品变更！");
                    failds.add(data);
                    continue;
            	}
			}
            catch (Exception e)
            {
            	log.info(e);
				data.put("IMPORT_ERROR", "查询用户优惠异常!");
                failds.add(data);
                continue;
			}*/


            //System.out.println("=======fileImportWideProductChange=========data2:"+data);
            succds.add(data);
        }
        //System.out.println("=======fileImportWideProductChange=========succds:"+succds);
        //System.out.println("=======fileImportWideProductChange=========failds:"+failds);

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }

    /**
     * @author yanwu
     * @param pdData
     * @param dataset
     * @return 批量主套餐办理
     * @throws Exception
     */
    public IData fileImportChangeProduct(IData pdData, IDataset dataset) throws Exception
    {
    	
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            //IData dataBak = new DataMap(data);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            
            try 
            {
            	UcaData uca = UcaDataFactory.getNormalUca(mainSN);
			} 
            catch (Exception e) 
            {
            	log.info(e);
				data.put("IMPORT_ERROR", "服务号码无效!");
                failds.add(data);
                continue;
			}
            	
            
        	String strElementId = data.getString("DATA1");
        	try 
        	{
        		IDataset Commpara9011 = CommparaInfoQry.getCommparaInfoByCode("CSM", "9011", "MODIFYPRODUCT_MAIN", strElementId, "0898");
        		if(IDataUtil.isEmpty(Commpara9011))
        		{
        	IData ups = new DataMap();
        	ups.put("ELEMENT_ID", strElementId);
        	ups.put("SERIAL_NUMBER", mainSN);
        		IDataset idsOfferInfo = CSAppCall.call("SS.CreatePersonUserSVC.qryComRelOffersByOfferCode", ups);
        		if(IDataUtil.isNotEmpty(idsOfferInfo))
        		{
        			for (int j = 0; j < idsOfferInfo.size(); j++) 
        			{
        				IData idOfferInfo = idsOfferInfo.getData(j);
        				strElementId = idOfferInfo.getString("ELEMENT_ID");
        				int n = j + 4;
        				String strKey = "DATA" + n;
        				data.put(strKey, strElementId);
					}
        		}
        		}
        		String strStartDate = data.getString("DATA2");
        		try 
        		{
            		Date dtStartDate = SysDateMgr.string2Date(strStartDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
            		strStartDate = SysDateMgr.date2String(dtStartDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
            		data.put("DATA2", strStartDate);
				} 
        		catch (Exception e) 
				{
        			log.info(e);
        			data.put("IMPORT_ERROR", strStartDate + " 生效时间无效，格式请填写 yyyy-MM-dd!");
                    failds.add(data);
                    continue;
				}
        		String strBookingTag = data.getString("DATA3");
        		if("是".equals(strBookingTag))
        		{
        			data.put("DATA3", "0");
        		}
        		else
        		{
        			data.put("DATA3", "1");
				}
			} 
        	catch (Exception e) 
			{
				log.info(e);
				data.put("IMPORT_ERROR", e);
                    failds.add(data);
                    continue;
			}
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;
    }
    public IData fileImportChangeProductName(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            
            try 
            {
            	UcaData uca = UcaDataFactory.getNormalUca(mainSN);
            	
            	if(uca!=null&&uca.getUser()!=null&&"0".equals(uca.getUser().getAcctTag())){
                    data.put("IMPORT_ERROR", "该号码为激活用户，不允许变更!");
                    failds.add(data);
                    continue;
                }
			} 
            catch (Exception e) 
            {
            	log.info(e);
				data.put("IMPORT_ERROR", "服务号码无效!");
                failds.add(data);
                continue;
			}
            	
            
        	String strElementId = data.getString("DATA1");
        	try 
        	{
        		//9011 TD_S_COMMPARA配置编码
        		IDataset Commpara9011 = CommparaInfoQry.getCommparaInfoByCode("CSM", "9011", "MODIFYPRODUCT_MAIN", strElementId, "0898");
        		if(IDataUtil.isEmpty(Commpara9011))
        		{
        			IData ups = new DataMap();
                	ups.put("ELEMENT_ID", strElementId);
                	ups.put("SERIAL_NUMBER", mainSN);
            		IDataset idsOfferInfo = CSAppCall.call("SS.CreatePersonUserSVC.qryComRelOffersByOfferCode", ups);
            		if(IDataUtil.isNotEmpty(idsOfferInfo))
            		{
            			for (int j = 0; j < idsOfferInfo.size(); j++) 
            			{
            				IData idOfferInfo = idsOfferInfo.getData(j);
            				strElementId = idOfferInfo.getString("ELEMENT_ID");
            				int n = j + 4;
            				String strKey = "DATA" + n;
            				data.put(strKey, strElementId);
    					}
            		}
				}
        		
        		String strStartDate = data.getString("DATA2");
        		try 
        		{
            		Date dtStartDate = SysDateMgr.string2Date(strStartDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
            		strStartDate = SysDateMgr.date2String(dtStartDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
            		data.put("DATA2", strStartDate);
				} 
        		catch (Exception e) 
				{
        			log.info(e);
        			data.put("IMPORT_ERROR", strStartDate + " 生效时间无效，格式请填写 yyyy-MM-dd!");
                    failds.add(data);
                    continue;
				}
        		String strBookingTag = data.getString("DATA3");
        		if("是".equals(strBookingTag))
        		{
        			data.put("DATA3", "0");
        		}
        		else
        		{
        			data.put("DATA3", "1");
				}
			} 
        	catch (Exception e) 
			{
				log.info(e);
				data.put("IMPORT_ERROR", e);
                failds.add(data);
                continue;
			}
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    /**
     * @author 
     * @param pdData
     * @param dataset
     * @return 物联网沉默用户批量激活
     * @throws Exception
     */
    public IData fileImportCheckForSilencePWLW(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            String strSn = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(strSn))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            
            UcaData uca = null;
            try
            {
            	uca = UcaDataFactory.getNormalUca(strSn); 
            }
            catch(Exception e)
            {
            	if(e.getMessage().indexOf("CRM_BOF_002") >= 0)
            	{
            		data.put("IMPORT_ERROR", "根据手机号码未找到用户资料");
            		failds.add(data);
            		continue;
            	}
            	else
            	{
	                String msg = e.getMessage();
	                msg = (msg == null) ? "根据手机号码未找到用户资料" : msg;
	                String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
	                data.put("IMPORT_ERROR", rspDesc);
            		failds.add(data);
            		continue;
            		//throw new Exception(e);
            	}
            }
            String strBrandCode = uca.getBrandCode();
            String strAcctTag = uca.getUser().getAcctTag();
            if(!"PWLW".equals(strBrandCode))
            {
            	data.put("IMPORT_ERROR", strSn+ "不是物联网号码");
        		failds.add(data);
        		continue;
            }
            if(!"2".equals(strAcctTag))
            {
            	data.put("IMPORT_ERROR", strSn+ "不是待激活物联网号码");
        		failds.add(data);
        		continue;
            }
            
            succds.add(data);
        }
    	
 
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    

    /**
     * @author 
     * @param pdData
     * @param dataset
     * @return 服务质量监督员
     * @throws Exception
     */
    public IData fileImportCheckForSVCQSUPERVISOR(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            String strSn = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(strSn))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            
            UcaData uca = null;
            try
            {
            	uca = UcaDataFactory.getNormalUca(strSn); 
            }
            catch(Exception e)
            {
            	if(e.getMessage().indexOf("CRM_BOF_002") >= 0)
            	{
            		data.put("IMPORT_ERROR", "根据手机号码未找到用户资料");
            		failds.add(data);
            		continue;
            	}
            	else
            	{
	                String msg = e.getMessage();
	                msg = (msg == null) ? "根据手机号码未找到用户资料" : msg;
	                String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
	                data.put("IMPORT_ERROR", rspDesc);
            		failds.add(data);
            		continue;
            	}
            }             
            
            succds.add(data);
        }
    	
 
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
    	log.error("BatDealDispatchSVCxxxxxxxxxxxx1848 "+returnData);

        return returnData;
    }
    
    /**
     * @author yanwu
     * @param pdData
     * @param dataset
     * @return 行业应用卡类用户资料变更
     * @throws Exception
     */
    public IData fileImportCheckForM2mName(IData pdData, IDataset dataset) throws Exception
    {
    	
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            IData dataBak = new DataMap(data);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            
            /*
             * REQ201706150007 关于增加行业应用卡标识办理界面的需求,增加一个功能点：
				修改行业应用卡类用户资料变更批量任务：
				1.该界面不判断导入的号码是否有行业应用卡标识，均允许进行资料修改（目前是只有行业应用卡标志的用户才可以在改界面修改客户资料）
				2.资料修改完成后，打上行业应用卡标识。（目前没有打标识）
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", mainSN);
            param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            IData rd = CSAppCall.call("SS.CreatePersonUserSVC.checkProductsForM2M", param).first();
            String strXResultCode = rd.getString("X_RESULTCODE","-1");
        	String strXResultInfo = rd.getString("X_RESULTINFO","号码使用的产品不允许导入变更资料");
            if(!"0".equals(strXResultCode))
            {
            	data.put("IMPORT_ERROR", strXResultInfo);
                failds.add(data);
                continue;
            }
            */
            UcaData uca = null;
            try{
            	uca = UcaDataFactory.getNormalUca(mainSN); 
            }catch(Exception e1){
            	if(e1.getMessage().indexOf("CRM_BOF_002")>=0){
            		data.put("IMPORT_ERROR", "根据手机号码未找到用户资料");
            		failds.add(data);
            		continue;
            	}else{
            		throw new Exception(e1);
            	}
            }
            String strUserID = uca.getUserId();
            String strCustID = uca.getCustId();
            //UcaData uca = UcaDataFactory.getNormalUca(data.getString("SERIAL_NUMBER"));
            IData ups = new DataMap();
        	ups.put("USER_ID", strUserID);
        	ups.put("CUST_ID", strCustID);
        	ups.put("SERIAL_NUMBER", mainSN);
        	
        	IDataset custInfos = new DatasetList();
        	try 
        	{
        		custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", ups);
			} 
        	catch (Exception e) 
			{
				//log.info("(e);
				data.put("IMPORT_ERROR", e);
                failds.add(data);
                continue;
			}
        	
        	IData params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            String strPsptTypeCode = params.getString("PSPT_TYPE_CODE");
            String strPsptTypeName = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_PASSPORTTYPE", 
            		new String []{"PSPT_TYPE_CODE","EPARCHY_CODE"}, "PSPT_TYPE", new String []{strPsptTypeCode, "0898"});
            
            if(StringUtils.isNotEmpty(strPsptTypeCode) &&
              ("D".equals(strPsptTypeCode) || "E".equals(strPsptTypeCode) ||
               "G".equals(strPsptTypeCode) || "L".equals(strPsptTypeCode) ||
               "M".equals(strPsptTypeCode))
              )
            {
            	//客户名称校验
            	String strcustName = data.getString("DATA1");
            	/*if(strcustName.indexOf("校园")>-1 || 
            	   strcustName.indexOf("海南通")>-1 || 
            	   strcustName.indexOf("神州行")>-1 || 
            	   strcustName.indexOf("动感地带")>-1 || 
            	   strcustName.indexOf("套餐")>-1) 
            	{
    				
            		data.put("IMPORT_ERROR", "客户名称不能包含【校园、海南通、神州行、动感地带、套餐】!");
    				failds.add(data);
    				continue;
    				
    		    }else */
				if(!specialStr(strcustName))
    		    {
            		data.put("IMPORT_ERROR", "客户名称包含特殊字符，请检查!");
    				failds.add(data);
    				continue;
            	}
            	
            	if(!"D".equals(strPsptTypeCode))
            	{
            		
            		if(isInDigitChinese(strcustName))
    		    	{//strcustName.matches("[a-zA-Z0-9]")
    		    		data.put("IMPORT_ERROR", "证件类型是" + strPsptTypeName + ",客户名称不能包含数字和字母!");
    					failds.add(data);
    					continue;
    		    	}
                	else if(strcustName.length()<2)
    				{
    					data.put("IMPORT_ERROR", "证件类型是" + strPsptTypeName + ",客户名称不能少于2个中文字符!");
    					failds.add(data);
    					continue;
    				}
                	else if(!isInChinese(strcustName))
    				{
    					data.put("IMPORT_ERROR", "证件类型是" + strPsptTypeName + ",客户名称不能少于2个中文字符!");
    					failds.add(data);
    					continue;
    				}
            	}
            }
            else
            {
            	data.put("IMPORT_ERROR", "证件类型非集团证件，不允许导入变更资料!");
                failds.add(data);
                continue;
            }
        	
        	//经办人名称校验
        	String strAgentCustName = data.getString("DATA2");
        	/*if(strAgentCustName.indexOf("校园")>-1 || 
        	   strAgentCustName.indexOf("海南通")>-1 || 
        	   strAgentCustName.indexOf("神州行")>-1 || 
        	   strAgentCustName.indexOf("动感地带")>-1 || 
        	   strAgentCustName.indexOf("套餐")>-1) 
        	{
 					
             		data.put("IMPORT_ERROR", "经办人名称不能包含【校园、海南通、神州行、动感地带、套餐】!");
 					failds.add(data);
 					continue;
 					
        	}
        	else */
			if(!specialStr(strAgentCustName))
        	{
        		data.put("IMPORT_ERROR", "经办人名称包含特殊字符，请检查!");
				failds.add(data);
				continue;
        	}
        	
        	//经办人证据类型校验
            String strAgentPsptType = data.getString("DATA3");
            String strAgentPsptTypeCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_PASSPORTTYPE", 
            		new String []{"PSPT_TYPE","EPARCHY_CODE"}, "PSPT_TYPE_CODE", new String []{strAgentPsptType, "0898"});       
            
            if(!"0".equals(strAgentPsptTypeCode) && !"1".equals(strAgentPsptTypeCode) && !"A".equals(strAgentPsptTypeCode))
            {
            	data.put("IMPORT_ERROR", "经办人证件类型只能为个人证件，包括本地身份证、外省身份证、护照");
				failds.add(data);
				continue;
            }
            
        	/*if(!"A".equals(strAgentPsptTypeCode) && !"D".equals(strAgentPsptTypeCode))
        	{
		    	if(isInDigitChinese(strAgentCustName))
		    	{//strcustName.matches("[a-zA-Z0-9]")
		    		data.put("IMPORT_ERROR", "经办人证件类型是" + strAgentPsptType + ",经办人名称不能包含数字和字母!");
					failds.add(data);
					continue;
		    	}
				if(strAgentCustName.length()<2)
				{
					data.put("IMPORT_ERROR", "经办人证件类型是" + strAgentPsptType + ",经办人名称不能少于2个中文字符!");
					failds.add(data);
					continue;
				}
				if(!isInChinese(strAgentCustName))
				{
					data.put("IMPORT_ERROR", "经办人证件类型是" + strAgentPsptType + ",经办人名称不能少于2个中文字符!");
					failds.add(data);
					continue;
				}
			}*/
        	
        	//经办人证件号码校验
        	String strAgentPsptID = data.getString("DATA4");
            if("A".equals(strAgentPsptTypeCode))//经办人护照
            {
                if(strAgentCustName.trim().length()<3){
                    data.put("IMPORT_ERROR", "经办人名称必大于等于3位，请重新输入!");
                    failds.add(data);
                    continue;
                } 
                 if(strAgentPsptID.trim().length()<6){
                     data.put("IMPORT_ERROR", "经办人证件号码必须大于等于6位，请重新输入!");
                     failds.add(data);
                     continue;
                 } 
            }
        	if(isRepeatCode(strAgentPsptID))
        	{
        		
        		data.put("IMPORT_ERROR", "经办人证件号码不能全为同一个数字，请重新输入!");
				failds.add(data);
				continue;
				
        	}
        	else if(isSerialCode(strAgentPsptID))
        	{
        		
        		data.put("IMPORT_ERROR", "经办人证件号码不能连续数字，请重新输入!");
				failds.add(data);
				continue;
        		
        	}/*else if("E".equals(strAgentPsptTypeCode)){
				//营业执照：证件号码长度需满足15位或者18位 
				if(strAgentPsptID.length() != 15 && strAgentPsptID.length() != 18){
					data.put("IMPORT_ERROR", "营业执照类型校验：长度需满足15位或者18位！当前："+strAgentPsptID.length()+"位。");
					failds.add(data);
					continue;
				}
			}else if("M".equals(strAgentPsptTypeCode)){
				//组织机构代码校验
				if(strAgentPsptID.length() != 10){
					data.put("IMPORT_ERROR", "组织机构代码证类型校验：长度需满足10位。");
					failds.add(data);
					continue;
				}
				if("-".equals(strAgentPsptID.charAt(8))){
					data.put("IMPORT_ERROR", "组织机构代码证类型校验：规则为XXXXXXXX-X，倒数第2位是-");
					failds.add(data);
					continue;
				}
			}else if("G".equals(strAgentPsptTypeCode) && strAgentPsptID.length() != 12){
				//事业单位法人登记证书：证件号码长度需满足12位
				data.put("IMPORT_ERROR", "事业单位法人登记证书类型校验：长度需满足12位。");
				failds.add(data);
				continue;
				
			}*/
            
            //经办人证件地址校验
            String strAgentPsptAddr = data.getString("DATA5");
        	if("".equals(strAgentPsptAddr) || strAgentPsptAddr.length() < 8)
        	{
        		dataBak.put("IMPORT_ERROR", "经办人证件地址录入文字需大于8位!");
				failds.add(dataBak);
				continue;
        	}
        	//boolean isNum = strAgentPsptAddr.matches("[0-9]+");
        	if (isInDigit(strAgentPsptAddr)) 
        	{
        		dataBak.put("IMPORT_ERROR", "经办人证件地址录入不能全部为数字!");
				failds.add(dataBak);
				continue;
        	}
        	
        	//经办人证件地址校验
            String strZrrCustName = data.getString("DATA6");
            /*if(strZrrCustName.indexOf("校园")>-1 || 
               strZrrCustName.indexOf("海南通")>-1 || 
               strZrrCustName.indexOf("神州行")>-1 || 
               strZrrCustName.indexOf("动感地带")>-1 || 
               strZrrCustName.indexOf("套餐")>-1) 
            {	
          		data.put("IMPORT_ERROR", "责任人名称不能包含【校园、海南通、神州行、动感地带、套餐】!");
				failds.add(data);
				continue;
            }
            else */
			if(!specialStr(strZrrCustName))
            {
         		data.put("IMPORT_ERROR", "责任人名称包含特殊字符，请检查!");
 				failds.add(data);
 				continue;
            }
            
            //经办人证据类型校验
            String strZrrPsptType = data.getString("DATA7");
            String strZrrPsptTypeCode = StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_S_PASSPORTTYPE", 
            		new String []{"PSPT_TYPE","EPARCHY_CODE"}, "PSPT_TYPE_CODE", new String []{strZrrPsptType, "0898"});  
            
            if(!"0".equals(strZrrPsptTypeCode) && !"1".equals(strZrrPsptTypeCode) && !"A".equals(strZrrPsptTypeCode))
            {
            	data.put("IMPORT_ERROR", "责任人证件类型只能为个人证件，包括本地身份证、外省身份证、护照");
				failds.add(data);
				continue;
            }
            
            //经办人证件号码校验
            String strZrrPsptID = data.getString("DATA8");
            if(isRepeatCode(strZrrPsptID))
        	{
        		
        		data.put("IMPORT_ERROR", "责任人证件号码不能全为同一个数字，请重新输入!");
				failds.add(data);
				continue;
				
        	}
        	else if(isSerialCode(strZrrPsptID))
        	{
        		
        		data.put("IMPORT_ERROR", "责任人证件号码不能连续数字，请重新输入!");
				failds.add(data);
				continue;
        		
        	}
            
            if("A".equals(strZrrPsptTypeCode))//责任人人护照
            {
                if(strZrrCustName.trim().length()<3){
                    data.put("IMPORT_ERROR", "责任人名称必大于等于3位，请重新输入!");
                    failds.add(data);
                    continue;
                } 
                 if(strZrrPsptID.trim().length()<6){
                     data.put("IMPORT_ERROR", "责任人证件号码必须大于等于6位，请重新输入!");
                     failds.add(data);
                     continue;
                 } 
            }
            //经办人证件地址校验
            String strZrrPsptAddr = data.getString("DATA9");
            if("".equals(strZrrPsptAddr) || strZrrPsptAddr.length() < 8)
            {
        		dataBak.put("IMPORT_ERROR", "责任人证件地址录入文字需大于8位!");
				failds.add(dataBak);
				continue;
        	}
        	//boolean isNum = strAgentPsptAddr.matches("[0-9]+");
        	if (isInDigit(strZrrPsptAddr)) 
        	{
        		dataBak.put("IMPORT_ERROR", "责任人证件地址录入不能全部为数字!");
				failds.add(dataBak);
				continue;
        	}
        	
        	if(strAgentPsptID.equals(strZrrPsptID))
        	{
        		data.put("IMPORT_ERROR", "经办人和责任人证件号码不能相同!");
				failds.add(data);
				continue;
        	}
        	
        	if("0".equals(strAgentPsptTypeCode)|| 
                    "1".equals(strAgentPsptTypeCode) || 
                    "2".equals(strAgentPsptTypeCode)) {
            if (strAgentPsptID.length() == 15 || strAgentPsptID.length() == 18)
            {
                if (StringUtils.isNotBlank(checkPspt(strAgentPsptID, "", strAgentPsptTypeCode)))
                {
                    data.put("IMPORT_ERROR", "经办人证件号码格式不对");
                    failds.add(data);
                    continue;
                }
            }else{
                    data.put("IMPORT_ERROR", "经办人证件号码格式不对");
                    failds.add(data);
                    continue;
            }        
            IData input = new DataMap();
            input.put("CERT_ID",strAgentPsptID); 
            input.put("CERT_NAME",strAgentCustName);
            IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);
            if(!ds.isEmpty()){
                String reCode =  ds.getData(0).getString("X_RESULTCODE");
                if(!reCode.equals("0")){//证件信息不合法
                    data.put("IMPORT_ERROR", "经办人证件信息不合法");
                    failds.add(data);
                    continue;
                }
            }                        
        }
            if("0".equals(strZrrPsptTypeCode)|| 
                    "1".equals(strZrrPsptTypeCode) || 
                    "2".equals(strZrrPsptTypeCode)) {
            if (strZrrPsptID.length() == 15 || strZrrPsptID.length() == 18)
            {
                if (StringUtils.isNotBlank(checkPspt(strZrrPsptID, "", strZrrPsptTypeCode)))
                {
                    data.put("IMPORT_ERROR", "责任人证件号码格式不对");
                    failds.add(data);
                    continue;
                }
            }else{
                data.put("IMPORT_ERROR", "责任人证件号码格式不对");
                    failds.add(data);
                    continue;
            }   
            IData input = new DataMap();
            input.put("CERT_ID",strZrrPsptID); 
            input.put("CERT_NAME",strZrrCustName);
            IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);
            if(!ds.isEmpty()){
                String reCode =  ds.getData(0).getString("X_RESULTCODE");
                if(!reCode.equals("0")){//证件信息不合法
                    data.put("IMPORT_ERROR", "责任人证件信息不合法");
                    failds.add(data);
                    continue;
                }
            }
        }
        	data.put("DATA3", strAgentPsptTypeCode);
        	data.put("DATA7", strZrrPsptTypeCode);
        	
            
            succds.add(data);
        }
    	
 
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    /**
     * @author yanwu 
     * @param code
     * @return 判断是否递增或递减的序列号
     */
    public boolean isSerialCode(String code){
		if(!this.isInDigit(code)){
			return false;
		}
		boolean flag = true;
		int step = 0;
		for(int i=0; i < (code.length()-1); i++){
			int n1 = code.charAt(i+1);
			int n2 = code.charAt(i);
			step = n1- n2;
			if(step != 1 && step !=-1){
				flag = false;
				break;
			}
		}
		step=0;
		return flag;
	}
    
    /**
     * @author yanwu
     * @param code
     * @return 判断是否重复连号
     */
    public boolean isRepeatCode(String code)
    {
		for(int i=0; i< (code.length()-1); i++){
			if(code.charAt(i+1) != code.charAt(i)){
				return false;
			}
		}
		return true;
	}
    
    /**
     * @author yanwu
     * @param strName
     * @return
     */
    public boolean specialStr(String strName) 
    {
    	String specialStr ="`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
		for(int i=0; i < specialStr.length();i++)
		{
			if (strName.indexOf(specialStr.charAt(i)) > -1)
			{
				return false;
			}
		}
    	
    	return true;
    }
    
    /**
     * @author yanwu
     * @param strName
     * @return 是否含有数字或者字母
     */
    public boolean isInDigitChinese(String strName) 
    {
    	if(Pattern.compile("(?i)[a-z]").matcher(strName).find()){
    		return true;
    	}
    	char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) 
        {
            char c = ch[i];
            if (isDigit(c)) 
            {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @author yanwu 
     * @param strName
     * @return
     */
    public boolean isInDigit(String strName) 
    {
    	char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) 
        {
            char c = ch[i];
            if (!isDigit(c)) 
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * @author yanwu 
     * @param c
     * @return
     */
    private boolean isDigit(char c) 
    {
    	if (!Character.isDigit(c)) 
    	{
    		return false;
    	}
    	 return true;
    }
    
    /**
     * @author yanwu 
     * @param strName
     * @return
     */
    public boolean isInChinese(String strName) 
    {
        char[] ch = strName.toCharArray();
        for (int i = 0; i < ch.length; i++) 
        {
            char c = ch[i];
            if (!isChinese(c)) 
            {
                return false;
            }
        }
        return true;
    }

    
    /**
     * @author yanwu
     * @param c
     * @return
     */
    private boolean isChinese(char c) 
    {

        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);

        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS

                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A

                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION

                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION

                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) 
        {

            return true;

        }

        return false;

    }
    
    /**
     * @author yanwu
     * @param pdData
     * @param dataset
     * @return 批量办理亲亲网业务，导入校验
     * @throws Exception
     */
    public IData fileImportCheckForQQNet(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            IData faildData = new DataMap();
            IData succdData = new DataMap();
            //int[] nIndex = new int[]{0,0,0,0,0,0,0,0,0,0};
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            String strImortResult = "主号：" + mainSN + " 失败原因";
            if (!importResult)
            {
            	faildData.putAll(data);
                failds.add(faildData);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "主号号码格式不对!");
                faildData.putAll(data);
                failds.add(faildData);
                continue;
            }
            String strName = "副号";
            String strKey = "DATA";
            for(int j = 1; j <= 9; j++){
            	String strNamej = strName + j;
            	String strKeyj = strKey + j;
            	String strValue = data.getString(strKeyj, "");
            	if( StringUtils.isBlank(strValue) ){
    				continue;
    			}
            	if(!StringUtils.isNumeric(strValue)){
                	//data.put("IMPORT_ERROR", strName + j +"号码格式不对!");
                    //failds.add(data);
            		strImortResult = strImortResult + "," + strNamej +"号码格式不对!";
                	faildData.put(strKeyj, strValue);
                	faildData.put("IMPORT_ERROR", strImortResult);
                    continue;
                    //Exit = true;
                    //break;
                }
            	if( mainSN.equals(strValue) ){
            		//data.put("IMPORT_ERROR", strName + j +"与主号号码相同!");
                    //failds.add(data);
            		strImortResult = strImortResult + "," + strNamej +"与主号号码相同!";
            		faildData.put(strKeyj, strValue);
                	faildData.put("IMPORT_ERROR", strImortResult);
                    continue;
                    //Exit = true;
                    //break;
            	}
            	boolean Exit = false;
				for(int k = 1; k <= 9; k++){
					String strNamek = strName + k;
					String strKeyk = strKey + k;
					String strValueC = data.getString(strKeyk, "");
					if( StringUtils.isBlank(strValueC) ){
						continue;
					}
					if( strValue.equals(strValueC) && j != k ){
						//data.put("IMPORT_ERROR", strNamej+"与"+strNamek+"号码相同！");
		                //failds.add(data);
						strImortResult = strImortResult + "," + strNamej+"与"+strNamek+"号码相同！";
						faildData.put(strKeyj, strValue);
	                	faildData.put("IMPORT_ERROR", strImortResult);
						Exit = true; 
						break;
					}
				}
				if(Exit){ 
					continue;
				}
				IData input = new DataMap();
				input.put("SERIAL_NUMBER", strValue);
				IData rs = new DataMap();
				try {
					rs = CSAppCall.call("SS.FamilyMemberQuerySVC.queryFamilyMeb", input).getData(0);
					rs.put("X_RESULTCODE", "0");
					rs.put("X_RESULTINFO", strNamej + "存在短号！");
				} catch (Exception e) {
					//您还未开通任何亲亲网套餐,不能办理该业务！
					if(e.getMessage().contains("您还未开通任何亲亲网套餐")){
						rs.put("X_RESULTCODE", "-1");
						rs.put("X_RESULTINFO", e.getMessage());
		            }else if(e.getMessage().contains("号码[%s]无效")){
		            	rs.put("X_RESULTCODE", "0");
						rs.put("X_RESULTINFO", strNamej + "号码无效！");
		            }else{
		            	rs.put("X_RESULTCODE", "0");
						rs.put("X_RESULTINFO", strNamej + "存在短号！");
		            }
					if (log.isDebugEnabled())
	                {
	                	//log.info("(e);
	                }
				}
				String strXresultcode = rs.getString("X_RESULTCODE");
				String strXresultinfo = rs.getString("X_RESULTINFO");
				if( "0".equals(strXresultcode) ){
					strImortResult = strImortResult + "," + strXresultinfo;
					faildData.put(strKeyj, strValue);
                	faildData.put("IMPORT_ERROR", strImortResult);
					continue;
				}
				succdData.put(strKeyj, strValue);
				String strSn = succdData.getString("SERIAL_NUMBER", "");
				if( StringUtils.isBlank(strSn) ){
					succdData.put("SERIAL_NUMBER", mainSN);
		            succdData.put("IMPORT_RESULT", importResult);
				}
            }
            
            if( IDataUtil.isNotEmpty(faildData) ){
            	faildData.put("IMPORT_RESULT", importResult);
            	failds.add(faildData);
       	 	}
            if( IDataUtil.isNotEmpty(succdData) ){
            	succds.add(succdData);
            }
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    } 
    /**
     * 批量服务变更，导入校验@REQ201803020015 关于视频彩铃业务优惠办理活动的开发需求
     * @param pdData
     * @param dataset
     * @return IData
     * @throws Exception
     */
    public IData fileImportCheckForSPCL(IData pdData, IDataset dataset) throws Exception
    {
    	String batchTaskID = pdData.getString("BATCH_TASK_ID"); // 创建任务ID
    	IDataset batchTasks = BatchTypeInfoQry.queryBatchDataTASK(batchTaskID);
    	IData ransmapin =null;
    	if( IDataUtil.isNotEmpty(batchTasks)){
    		IData batchTask = batchTasks.getData(0);
    		String oc1 = batchTask.getString("CODING_STR1");
    		if(StringUtils.isNotEmpty(oc1)){
    			ransmapin = new DataMap(oc1);
    		}
    	}
    	
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            //非视频彩铃服务跳过
            if(ransmapin==null||(!("20171201".equals(ransmapin.getString("SERVICE_ID"))))){
            	succds.add(data);
            	continue;
            }
            if("0".equals(ransmapin.getString("MODIFY_TAG"))){
	            //查询三户信息
//	            UcaData uca = UcaDataFactory.getNormalUca(mainSN);
//	            if(uca==null){
//	            	 data.put("IMPORT_ERROR", "手机号不存在三户信息!");
//	                 failds.add(data);
//	                 continue;
//	            }
//	            List<SvcTradeData> spclSvc=uca.getUserSvcBySvcId("20171201");
//	            if(spclSvc!=null&&spclSvc.size()>0){
//	            	 data.put("IMPORT_ERROR", "已办理视频彩铃服务!");
//	                 failds.add(data);
//	                 continue;
//	            }
//	            List<SvcTradeData> volteSvc=uca.getUserSvcBySvcId("190");
//	            if(volteSvc==null||volteSvc.size()==0){
//	            	 data.put("IMPORT_ERROR", "办理视频彩铃必须有VoLTE服务!");
//	                 failds.add(data);
//	                 continue;
//	            }
//	            List<SvcTradeData> clSvc=uca.getUserSvcBySvcId("20");
//	            if(clSvc==null||clSvc.size()==0){
//	            	 data.put("IMPORT_ERROR", "办理视频彩铃必须有彩铃服务!");
//	                 failds.add(data);
//	                 continue;
//	            }
            }
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    /**
     * 批量预开户，导入校验@REQ201711240002 产品变更、预约产品变更等界面，增加任我用、任我看套餐的办理
     * @param pdData
     * @param dataset
     * @return IData
     * @throws Exception
     */
    public IData fileImportCheckForRWYK(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            
            String simCardNo=data.getString("DATA1");
            String productId="";
            String batchTaskID = pdData.getString("BATCH_TASK_ID"); // 创建任务ID
        	IDataset batchTasks = BatchTypeInfoQry.queryBatchDataTASK(batchTaskID);
        	if( IDataUtil.isNotEmpty(batchTasks) ){
        		IData batchTask = batchTasks.getData(0);
        		productId = batchTask.getString("CONDITION1");
        	}
            //调资源接口判断是否4G卡
            
            IDataset commpara8555 = CommparaInfoQry.getCommParas("CSM", "8555", "4G", productId, CSBizBean.getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(commpara8555)){
            	IData dataP=commpara8555.getData(0);
            	if("1".equals(dataP.getString("PARA_CODE10", ""))){
            		//调资源接口判断是否4G卡
            		IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", null);
            		if (IDataUtil.isNotEmpty(simCardDatas))
                    {
                        String cardModeType = simCardDatas.getData(0).getString("CARD_MODE_TYPE", "");
                        String opc = simCardDatas.getData(0).getString("OPC", "");
                        boolean is4G=true;
                        if("1".equals(cardModeType)||((!("2".equals(cardModeType)))&&"".equals(opc))){
                        	is4G=false;
                        }
                        
                        if (!is4G){
                        	data.put("IMPORT_ERROR", "非4G不能办理该产品【"+productId+"】");
                            failds.add(data);
                            continue;
                        }
                    }
                    else
                    {
                        data.put("IMPORT_ERROR", "查询资源接口根据SIM卡号【"+simCardNo+"】获取SIM卡信息失败!!");
                        failds.add(data);
                        continue;
                    }
                   
            	}
            }
            IDataset numberInfos = ResCall.getMphonecodeInfo(mainSN, "0");
            if(IDataUtil.isNotEmpty(numberInfos) && IDataUtil.isNotEmpty(numberInfos.getData(0))){
            	String precodeTag = numberInfos.getData(0).getString("PRECODE_TAG", "");
            	if("1".equals(precodeTag)){
            		String iccId = numberInfos.getData(0).getString("ICC_ID","");
            		if(!simCardNo.equals(iccId)){
            			data.put("IMPORT_ERROR", "办理失败！该号码已经预配的SIM卡号为【"+iccId+"】，当前导入的卡号为【"+simCardNo+"】！");
                        failds.add(data);
                        continue;
            		}
            	}
            }
            IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "0");
            if(IDataUtil.isNotEmpty(simCardDatas) && IDataUtil.isNotEmpty(simCardDatas.getData(0))){
            	String precodeTag = simCardDatas.getData(0).getString("PRECODE_TAG", "");
            	if("1".equals(precodeTag)){
            		String accessNumber = simCardDatas.getData(0).getString("ACCESS_NUMBER","");
            		if(!mainSN.equals(accessNumber)){
            			data.put("IMPORT_ERROR", "办理失败！该卡已经预配的手机号码为【"+accessNumber+"】，当前导入的手机号为【"+mainSN+"】！");
                        failds.add(data);
                        continue;
            		}
            	}
            }
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    /**
     * 批量物联网开户，导入校验@yanwu
     * @param pdData
     * @param dataset
     * @return IData
     * @throws Exception
     */
    public IData fileImportCheckForPWLW(IData pdData, IDataset dataset) throws Exception
    { 
    	String strPsptID = "";
    	String strCustName = "";
    	String strPsptTypeCode = "";
    	String batchTaskID = pdData.getString("BATCH_TASK_ID"); // 创建任务ID
    	IDataset batchTasks = BatchTypeInfoQry.queryBatchDataTASK(batchTaskID);
    	String batOperType = "";
    	if( IDataUtil.isNotEmpty(batchTasks) ){
    		IData batchTask = batchTasks.getData(0);
    		Object oc1 = batchTask.getString("CODING_STR1");
    		Object oc2 = batchTask.getString("CODING_STR2");
    		Object oc3 = batchTask.getString("CODING_STR3");
    		Object oc4 = batchTask.getString("CODING_STR4");
    		Object oc5 = batchTask.getString("CODING_STR5");
    		batOperType = batchTask.getString("BATCH_OPER_CODE");
    		StringBuilder sbc = new StringBuilder();
    		if(oc1 != null) 
			{
				sbc.append(((String)oc1));
			}
			if(oc2 != null) 
			{
				sbc.append(((String)oc2));
			}
			if(oc3 != null) 
			{
				sbc.append(((String)oc3));
			}
			if(oc4 != null) 
			{
				sbc.append(((String)oc4));
			}
			if(oc5 != null) 
			{
				sbc.append(((String)oc5));
			}
			IData ransmapin = new DataMap(sbc.toString());
			strPsptID = ransmapin.getString("PSPT_ID", "");
			strCustName = ransmapin.getString("CUST_NAME", "");
        	strPsptTypeCode = ransmapin.getString("PSPT_TYPE_CODE", "");
    	}
    	boolean isLimitByPspt = false;
    	int rCount = 0;
    	int rLimit = 5;
    	if ( !"".equals(strCustName) && !"".equals(strPsptID) )
        {
            String strCityCode = CSBizBean.getVisit().getCityCode();
            if( !"HNSJ".equalsIgnoreCase(strCityCode) && !"HNHN".equalsIgnoreCase(strCityCode) ){
            	
            	IData param = new DataMap();
                param.put("PSPT_ID", strPsptID);
                param.put("CUST_NAME", strCustName);
                param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
                
                /**
                 * BUG20171129150222_物联网批量开户经常报错用户取消当前操作，请在判断条件去掉PWLW
                 * <br/>
                 * 物联网批量开户不需要，调本地一证五号逻辑
                 * @author zhuoyingzhi
                 * @date 20180109
                 */
                IDataset commpare = CommparaInfoQry.getCommparaAllCol("CSM", "43", "0", "ZZZZ");
                if("CREATEPREUSER_PWLW".equals(batOperType) && IDataUtil.isNotEmpty(commpare) && "1".equals(commpare.getData(0).getString("PARA_CODE1"))){
                	log.debug("----------CREATEPREUSER_PWLW------:物联网批量开户不需要，调本地一证五号逻辑");
                }else{
                	if ( !"".equals(strPsptTypeCode) && ("D".equals(strPsptTypeCode)||"E".equals(strPsptTypeCode)||"G".equals(strPsptTypeCode)
                	    	||"L".equals(strPsptTypeCode)||"M".equals(strPsptTypeCode)) ){
                		param.put("USER_TYPE", "1");//目前只有物联网卡开户和行业应用卡批量开户有调这方法，不是物联网卡就是行业应用卡，
                	}
                	IDataset rs = CSAppCall.call("SS.CreatePersonUserSVC.checkRealNameLimitByPspt", param);
    	            if( IDataUtil.isNotEmpty(rs) ){
    	            	IData r = rs.getData(0);
    	            	String strrCount = r.getString("rCount");
    	            	String strrLimit = r.getString("rLimit");
    	            	rCount = Integer.parseInt(strrCount);	//证件办理数
    	            	rLimit = Integer.parseInt(strrLimit);	//证件办理最大数
    	            	isLimitByPspt = true;
    	            }                 	
                }
            }
        }
    	int iCount = rLimit - rCount;	//导入剩余数
    	
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int dSize = dataset.size();
        for (int i = 0; i < dSize; i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            if ( isLimitByPspt ){
            	if ( i >= iCount ){
                	data.put("IMPORT_ERROR", "证件号码【" + strPsptID + "】实名制开户的数量已达到最大值【" + rLimit + "个】，请更换其它证件！");
                    failds.add(data);
                    continue;
                }
            }
            
           
            if("CREATEPREUSER_PWLW".equals(batOperType))//物联网开户 //liquan REQ201611280018 关于物联卡开户界面关于实名制的补充开发需求涉及实名制工作               
            {
                
                String serialNumber = data.getString("SERIAL_NUMBER", "");
                String simCardNo=data.getString("DATA1");
                if (serialNumber.length() >= 3 && serialNumber.startsWith("147")) {

                    String privTag = "0";
                    if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_OPENUSER_WLW_147")) {
                        privTag = "1";
                    }
                    if ("0".equals(privTag)) {
                        data.put("IMPORT_ERROR", "您没有权限开户147号段的号码！");
                        failds.add(data);
                        continue;
                    }
                    
                    String psptTypeCode = "";
                    if (IDataUtil.isNotEmpty(batchTasks)) {
                        JSONArray array = new JSONArray();
                        array.element(batchTasks.getData(0).getString("CODING_STR1", ""));
                        DatasetList ds = new DatasetList(array.toString());
                        if (DataSetUtils.isNotBlank(ds)) {
                            psptTypeCode = ds.getData(0).getString("PSPT_TYPE_CODE", "");
                        }

                        if (psptTypeCode.length() > 0 && !"0".equals(psptTypeCode) && !"1".equals(psptTypeCode) && !"A".equals(psptTypeCode)) {//147开头的号码，客户证件类型必须为个人证件, 本地0 外地1 护照A
                            IDataset elementDs = ds.getData(0).getDataset("SELECTED_ELEMENTS");
                            boolean continueFlag = false;
                            for (int j = 0; j < elementDs.size(); j++) {
                                IData elementDm = elementDs.getData(j);
                                if ("99012000".equals(elementDm.getString("ELEMENT_ID", "").trim())) {// 99012000 物联网语音服务 
                                    data.put("IMPORT_ERROR", "147号段号码开户并且选择了语音服务，开户证件类型必须是个人证件：本地身份证或外地身份证或护照！");
                                    failds.add(data);
                                    continueFlag = true;
                                    break;
                                }
                            }
                            if (continueFlag) {
                                continue;
                            }
                        }
                    }
                }//liquan REQ201611280018 关于物联卡开户界面关于实名制的补充开发需求涉及实名制工作      
                //add by fufn REQ201709050002 begin 物联网吉祥号码不允许批量开户
                IData inData = new DataMap();
        		inData.put("SERIAL_NUMBER", mainSN);
        		inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 用户归属地州
    	        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
    	        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
    	        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
    	        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
    	        inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
    	        IDataset rss = ResCall.callRes("RCF.resource.INumberIntfOperateSV.getPhoneInfoByNum", inData);
    	        //modify by fufn BUG20180301162150 批量物联网开户报号码不存在的BUG
    	        if( IDataUtil.isNotEmpty(rss) )
                {
                	IData checkMphoneData = rss.getData(0);
                	String saleProduct = checkMphoneData.getString("BEAUTIFUAL_TAG", ""); //modify by fufn BUG20180301162150 批量物联网开户报号码不存在的BUG
                	if(StringUtils.isNotBlank(saleProduct)&&saleProduct.equals("1")){
                		data.put("IMPORT_ERROR", "号码"+mainSN+"为吉祥号码，不允许批量开户");
                		failds.add(data);
                		continue;
                	}
                }
    	        //add by fufn REQ201709050002 end
            	IData param = new DataMap();
                param.put("SERIAL_NUMBER", mainSN);
                param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
            	IDataset rs = CSAppCall.call("SS.CreatePersonUserSVC.checkSerialNumberByOperate", param);
                if( IDataUtil.isNotEmpty(rs) )
                {
                	IData r = rs.getData(0);
                	String strXresultcode = r.getString("X_RESULTCODE");
    				String strXresultinfo = r.getString("X_RESULTINFO");
    				if("-1".equals(strXresultcode)){
    					data.put("IMPORT_ERROR", strXresultinfo);
                        failds.add(data);
    					continue;
    				}
                }
              if(IDataUtil.isNotEmpty(rss) && IDataUtil.isNotEmpty(rss.getData(0))){
              	String precodeTag = rss.getData(0).getString("PRECODE_TAG", "");
              	if("1".equals(precodeTag)){
              		String iccId = rss.getData(0).getString("ICC_ID","");
              		if(!simCardNo.equals(iccId)){
              			data.put("IMPORT_ERROR", "办理失败！该号码已经预配的SIM卡号为【"+iccId+"】，当前导入的卡号为【"+simCardNo+"】！");
              			failds.add(data);
              			continue;
              		}
              	}
              }
              IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "0");
              if(IDataUtil.isNotEmpty(simCardDatas) && IDataUtil.isNotEmpty(simCardDatas.getData(0))){
              	String precodeTag = simCardDatas.getData(0).getString("PRECODE_TAG", "");
              	if("1".equals(precodeTag)){
              		String accessNumber = simCardDatas.getData(0).getString("ACCESS_NUMBER","");
              		if(!mainSN.equals(accessNumber)){
              			data.put("IMPORT_ERROR", "办理失败！该卡已经预配的手机号码为【"+accessNumber+"】，当前导入的手机号为【"+mainSN+"】！");
              			failds.add(data);
              			continue;
              		}
              	}
              }
            }
            else if("CREATEPREUSER_M2M".equals(batOperType))
            {
            	/**
                 * REQ201608170013_2016年下半年吉祥号码优化需求（二）
                 * 20160909
                 * @author zhuoyingzhi
                 * “行业应用卡批量开户”批量任务，新增吉祥号码不能开户的限制。系统在导入号码后做吉祥号码判断并拦截
                 */
                IDataset numberInfo = ResCall.getMphonecodeInfo(mainSN);// 查询号码信息
                if(IDataUtil.isNotEmpty(numberInfo)){
                	//BEAUTIFUAL_TAG：是否是吉祥号：0-非；1-是
                   String mpMark=numberInfo.getData(0).getString("BEAUTIFUAL_TAG");
                   if("1".equals(mpMark)){
                	   //是吉祥号
                       data.put("IMPORT_ERROR", "吉祥号码,不能开户.");
                       failds.add(data);
                       continue;
                   }
                }
            }
             
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    /**
     * 商务宽带批量开户，导入校验@yanwu
     * @param pdData
     * @param dataset
     * @return IData
     * @throws Exception
     */
    public IData fileImportCheckForBNBD(IData pdData, IDataset dataset) throws Exception
    {
    	String strSerialNumber = "";
    	String batchTaskID = pdData.getString("BATCH_TASK_ID"); // 创建任务ID
    	IDataset batchTasks = BatchTypeInfoQry.queryBatchDataTASK(batchTaskID);
    	if( IDataUtil.isNotEmpty(batchTasks) ){
    		IData batchTask = batchTasks.getData(0);
    		Object oc1 = batchTask.getString("CODING_STR1");
    		Object oc2 = batchTask.getString("CODING_STR2");
    		Object oc3 = batchTask.getString("CODING_STR3");
    		Object oc4 = batchTask.getString("CODING_STR4");
    		Object oc5 = batchTask.getString("CODING_STR5");
    		StringBuilder sbc = new StringBuilder();
    		if(oc1 != null) 
			{
				sbc.append(((String)oc1));
			}
			if(oc2 != null) 
			{
				sbc.append(((String)oc2));
			}
			if(oc3 != null) 
			{
				sbc.append(((String)oc3));
			}
			if(oc4 != null) 
			{
				sbc.append(((String)oc4));
			}
			if(oc5 != null) 
			{
				sbc.append(((String)oc5));
			}
			IData ransmapin = new DataMap(sbc.toString());
			strSerialNumber = ransmapin.getString("SERIAL_NUMBER", "");
    	}
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        int Num = 0;
        for (int i = 0; i < dataset.size(); i++)
        {
        	IData data = dataset.getData(i);
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            
            String strDATA19 = data.getString("DATA19");
            IData param = new DataMap();
            param.put("REGION_SP", strDATA19);
            param.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        	IDataset rs = CSAppCall.call("PB.AddressManageSvc.queryAddressForHTML", param);
            if(IDataUtil.isNotEmpty(rs))
            {
            	if(rs.size() != 1)
            	{
            		data.put("IMPORT_ERROR", "标准地址填写错误，请重新填写");
                    failds.add(data);
					continue;
            	}
            	else
            	{
            		IData r = rs.first();
            		String strDEVICE_ID = r.getString("DEVICE_ID");
            		String strAREA_CODE = r.getString("AREA_CODE");
            		String strPORT_NUM = r.getString("PORT_NUM");
                	String strOPEN_TYPE = r.getString("OPEN_TYPE");
                	
                	if("0".equals(strPORT_NUM))
                	{
                		data.put("IMPORT_ERROR", "标准地址所选设备无可用端口！");
    	                failds.add(data);
    	                continue;
                	}
                	
    				IData input1 = new DataMap();
    				input1.put("OPEN_TYPE", strOPEN_TYPE);
    				input1.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
    	            IData idADD = CSAppCall.call("SS.CreatePersonUserSVC.checkProductsForADD", input1).first();
    	            String strXResultCode = idADD.getString("X_RESULTCODE", "-1");
    	        	String strXResultInfo = idADD.getString("X_RESULTINFO", "标准地址填写错误，仅支持FTTB和FTTH开户,请重新填写。");
    	            if(!"0".equals(strXResultCode))
    	            {
    	            	data.put("IMPORT_ERROR", strXResultInfo);
    	                failds.add(data);
    	                continue;
    	            }
    	            
    	            String strDATA2 = data.getString("DATA2");
    	            IData input2 = new DataMap();
    				input2.put("PRODUCT_ID", strDATA2);
    				input2.put("OPEN_TYPE", strOPEN_TYPE);
    				input2.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
    	            IData idSpeed = CSAppCall.call("SS.CreatePersonUserSVC.checkProductsForSpeed", input2).first();
    	            strXResultCode = idSpeed.getString("X_RESULTCODE", "-1");
    	        	strXResultInfo = idSpeed.getString("X_RESULTINFO", "速率填写错误,请重新填写");
    	            if("0".equals(strXResultCode))
    	            {
    	            	String strWIDE_PRODUCT_ID =  idSpeed.getString("WIDE_PRODUCT_ID");
    	            	String strDiscntCode = idSpeed.getString("DISCNT_CODE", "");
    	            	String strServiceID = idSpeed.getString("SERVICE_ID", "");
    	            	
    	            	//获取集团号码的子号码
    	            	IData input4 = new DataMap();
    	                input4.put("SERIAL_NUMBER", strSerialNumber);
    	                input4.put("WIDE_PRODUCT_ID", strWIDE_PRODUCT_ID);
    	                input4.put("WIDE_NUM", Num);
    	                input4.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
    	                IDataset idsBatSnCnt = CSAppCall.call("SS.WideUserCreateSVC.getBatWideSerialNumber", input4);
    	    			if(IDataUtil.isNotEmpty(idsBatSnCnt))
    	    			{
    	    				String strMaxSn = idsBatSnCnt.first().getString("WIDE_SERIAL_NUMBER", "");
    	    				data.put("DATA3", strMaxSn);
    	    			}
    	    			else
    	    			{
    	    		        IDataset wideSNdataset = CSAppCall.call("SS.WideUserCreateSVC.getWideSerialNumber", input4);
    	    		        if(IDataUtil.isNotEmpty(wideSNdataset))
        	    			{
    	    		        	String strMaxSn = wideSNdataset.first().getString("WIDE_SERIAL_NUMBER", "");
        	    				data.put("DATA3", strMaxSn);
    	    		        }
    	    			}
    	            	
    	            	IData input3 = new DataMap();
        				input3.put("NEW_PRODUCT_ID", strWIDE_PRODUCT_ID);
        				input3.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        				IDataset idsElements = CSAppCall.call("CS.SelectedElementSVC.getWidenetUserOpenElements", input3);
        				if(IDataUtil.isNotEmpty(idsElements))
        				{
        					String strSELECTED_ELEMENTS = idsElements.first().getString("SELECTED_ELEMENTS", "");
        					if(StringUtils.isNotBlank(strSELECTED_ELEMENTS))
        					{
        						IDataset idsSelectedElements = new DatasetList(strSELECTED_ELEMENTS);
        						for (int j = 0; j < idsSelectedElements.size(); j++) 
            					{
            						IData idElement = idsSelectedElements.getData(j);
            						String strElementTypeCode = idElement.getString("ELEMENT_TYPE_CODE", "");
            						String strElementID = idElement.getString("ELEMENT_ID", "");
            						if("D".equals(strElementTypeCode))
            						{
            							if(StringUtils.isBlank(strDiscntCode))
            							{
            								strDiscntCode = strElementID;
            							}
            							else
            							{
            								strDiscntCode = strDiscntCode + "," + strElementID;
            							}
            						}
            						else if("S".equals(strElementTypeCode))
            						{
            							if(StringUtils.isBlank(strServiceID))
            							{
            								strServiceID = strElementID;
            							}
            							else
            							{
            								strServiceID = strServiceID + "," + strElementID;
            							}
            						}
    							}
        					}
        				}
        				else
        				{
        					data.put("IMPORT_ERROR", strDATA2+"速率下找不到产品配置");
        	                failds.add(data);
        	                continue;
        				}
        				
        				if(strOPEN_TYPE.equals("FTTH") || strOPEN_TYPE.equals("TTFTTH"))
        		    	{
        					data.put("DATA9", "0");
        		    	}
        				
        				data.put("DATA11", strWIDE_PRODUCT_ID);
    	            	data.put("DATA12", strDEVICE_ID);
    	            	data.put("DATA13", strAREA_CODE);
    	            	data.put("DATA14", strPORT_NUM);
    	            	data.put("DATA15", strOPEN_TYPE);
        				if(StringUtils.isNotBlank(strDiscntCode))
        				{
        					data.put("DATA16", strDiscntCode);
        				}
        				if(StringUtils.isNotBlank(strServiceID))
        				{
        					data.put("DATA17", strServiceID);
        				}
    	            }
    	            else
    	            {
    	            	data.put("IMPORT_ERROR", strXResultInfo);
    	                failds.add(data);
    	                continue;
    	            }
				}
            }
            else
            {
            	data.put("IMPORT_ERROR", "标准地址填写错误，仅支持FTTB和FTTH开户,请重新填写");
                failds.add(data);
                continue;
            }
            data.put("SERIAL_NUMBER", strSerialNumber);
            succds.add(data);
            Num++;
        }
        
    	IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    public IData fileImportCheckForBatHbPay(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            if(StringUtils.isEmpty(data.getString("DATA1"))){
            	data.put("IMPORT_ERROR", "客户姓名不能为空");
                failds.add(data);
                continue;
            }  
            if(StringUtils.isEmpty(data.getString("DATA2"))){
            	data.put("IMPORT_ERROR", "证件号码不能为空");
                failds.add(data);
                continue;
            } 
            if(StringUtils.isEmpty(data.getString("DATA3"))){
            	data.put("IMPORT_ERROR", "开户协议版本不能为空");
                failds.add(data);
                continue;
            } 
            if(StringUtils.isEmpty(data.getString("DATA4"))){
            	data.put("IMPORT_ERROR", "证件类别不能为空");
                failds.add(data);
                continue;
            }            
            String strPSPTType = data.getString("DATA4") ;
            String strPSPTTypeCode = StaticUtil.getStaticValue(null,"TD_S_PASSPORTTYPE", new String []{"PSPT_TYPE","EPARCHY_CODE"}, "PSPT_TYPE_CODE", new String []{strPSPTType, "0898"});
            if( "".equals(strPSPTTypeCode) || strPSPTTypeCode==null )
	        {
	         	CSAppException.apperr(CrmCommException.CRM_COMM_103, "证件类别不正确，请检查导入文件!");
	        }
            data.put("DATA4", strPSPTTypeCode);
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    public void getCommBatInfo(IData param) throws Exception
    {

        IData batchtypeParam = queryBatchTypeParams(param);

        param.put("LIMIT_NUM_BATCH", batchtypeParam.getInt("LIMIT_NUM_BATCH"));// 导入条数限制
        param.put("LIMIT_NUM_DAY", batchtypeParam.getInt("LIMIT_NUM_DAY"));// 导入日条数限制
        param.put("LIMIT_NUM_MON", batchtypeParam.getInt("LIMIT_NUM_MON"));// 导入月条数限制
        param.put("PRIORITY", batchtypeParam.getString("PRIORITY"));
        param.put("AUDIT_STATE", "0");
        param.put("NEED_ACTIVENOW_TAG", batchtypeParam.getString("NEED_ACTIVENOW_TAG", "0"));// 默认为0，即不需立即启动

        IData importedCount = getNowDayCount(param.getString("BATCH_OPER_CODE"));
        param.put("SUMS", Integer.parseInt(importedCount.getString("SUMS", "0")));
        param.put("MONTH_SUM", Integer.parseInt(importedCount.getString("MONTH_SUM", "0")));
    }

    public IData getNowDayCount(String batch_oper_type) throws Exception
    {
        IData param = new DataMap();

        param.put("BATCH_OPER_TYPE", batch_oper_type);
        param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());

        IData set = BatInfoQry.getNowDayCount(batch_oper_type, SysDateMgr.getSysTime());
        if (IDataUtil.isEmpty(set))
        {
            return new DataMap();
        }
        else
        {
            return set;
        }
    }

    /** 插批量主表信息 */
    public void importBatMain(IDataset dataset, IData inParam) throws Exception
    {
        IData data = new DataMap();
        if (inParam.getString("NEED_ACTIVENOW_TAG", "0").equals("1") && inParam.getString("AUDIT_STATE").equals("0"))
        {
            // 需立即启动并且不需要审核则DEAL_STATE改成已启动状态
            data.put("ACTIVE_FLAG", "1");
            data.put("ACTIVE_TIME", SysDateMgr.getSysTime());
        }
        else
        {
            data.put("ACTIVE_FLAG", "0");
        }
        data.put("AUDIT_STATE", inParam.getString("AUDIT_STATE"));
        data.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID"));
        data.put("BATCH_ID", inParam.getString("BATCH_ID"));
        data.put("BATCH_OPER_TYPE", inParam.getString("BATCH_OPER_CODE"));
        data.put("BATCH_OPER_NAME", inParam.getString("BATCH_OPER_NAME"));
        data.put("BATCH_COUNT", inParam.getString("BATCH_COUNT"));
        data.put("ACCEPT_DATE", SysDateMgr.getSysTime());
        data.put("STAFF_ID", inParam.getString("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId()));
        data.put("DEPART_ID", inParam.getString("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId()));
        data.put("CITY_CODE", inParam.getString("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode()));
        data.put("EPARCHY_CODE", inParam.getString("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode()));
        data.put("TERM_IP", inParam.getString("TERM_IP", CSBizBean.getVisit().getRemoteAddr()));
        data.put("REMOVE_TAG", "0");
        data.put("AUDIT_STAFF_ID", inParam.getString("AUDIT_STAFF_ID", ""));
        if("GRPDISCNTCHGSPEC".equals(inParam.getString("BATCH_OPER_CODE",""))){
        	data.put("AUDIT_INFO", inParam.getString("MEB_VOUCHER_FILE_LIST", ""));
        }else{
        	data.put("AUDIT_INFO", inParam.getString("AUDIT_INFO"));
        }
        data.put("IN_MODE_CODE", "0");
        data.put("REMARK", inParam.getString("REMARK"));
        data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(inParam.getString("BATCH_ID")));
        data.put("AUDIT_REMARK", inParam.getString("AUDIT_REMARK", ""));
        data.put("AUDIT_DATE", inParam.getString("AUDIT_DATE", ""));
        data.put("AUDIT_DEPART_ID", inParam.getString("AUDIT_DEPART_ID", ""));

        Dao.insert("TF_B_TRADE_BAT", data, Route.getJourDb(Route.CONN_CRM_CG));
    }

    /** 导入批量预开户,指定号段方式 */
    public String importBatOpenUserRange(IDataset dataset, IData inParam) throws Exception
    {
        IDataset succds = new DatasetList();

        if ("1".equals(inParam.getString("IMPORT_MODE")))
        { // 服务号码段
            String prefix = inParam.getString("BEGIN_NUM").substring(0, 7);

            for (int i = inParam.getInt("START"); i <= inParam.getInt("END"); i++)
            {
                IData data = new DataMap();
                data.put("SERIAL_NUMBER", prefix + StringUtils.leftPad(String.valueOf(i), 4, '0'));
                succds.add(data);
            }
        }
        else if ("2".equals(inParam.getString("IMPORT_MODE")))
        { // SIM卡号段
            String prefix = inParam.getString("BEGIN_NUM").substring(0, 16);

            for (int i = inParam.getInt("START"); i <= inParam.getInt("END"); i++)
            {
                IData data = new DataMap();
                data.put("DATA1", prefix + StringUtils.leftPad(String.valueOf(i), 4, '0'));
                succds.add(data);
            }
        }

        // 插入详情表和批次表
        return insertImportData(succds, inParam);
    }

    public IDataset importData(IData input) throws Exception
    {
        setUserEparchyCode(CSBizBean.getTradeEparchyCode());
        IDataset set = new DatasetList(); // 上传excel文件内容明细
        return executeImport(input, set);
    }

    public IDataset importDataByFile(IData data, IDataset dataset) throws Exception
    {
        String batch_id = "";
        String batOperType = data.getString("BATCH_OPER_CODE");
        IDataset dataSet = BatchTypeInfoQry.qryBatchTypeByOperType(batOperType);
        String batOperName = "";
        if (IDataUtil.isNotEmpty(dataSet))
        {
            batOperName = dataSet.getData(0).getString("BATCH_OPER_NAME");
        }
        IDataset returnInfos = new DatasetList();
        getCommBatInfo(data);
        // 获取上传到服务器上的excel的文件id
        String fileId = data.getString("cond_STICK_LIST"); // 上传OCS监控excelL文件的编号
        String[] fileIds = fileId.split(",");
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        IDataset failds = new DatasetList();
        int allCount = 0; 
        for (String strfileId : fileIds)
        {
            IData array = ImpExpUtil.beginImport(null, strfileId, ExcelConfig.getSheets("import/bat/" + batOperType + ".xml"));
            IDataset[] suc = (IDataset[]) array.get("right");// 解析成功的数据
            IDataset[] err = (IDataset[]) array.get("error");// 解析失败的数据
            //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxxxx3353 "+batOperType);
            if("MODIFYGROUPPSPTINFO".equals(batOperType)||"MODIFYTDPSPTINFO".equals(batOperType)){
                //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxxxx3355 "+err);

                if(err.length>0){
                    IDataset dataseterr = (IDataset)err[0];
                    if(IDataUtil.isNotEmpty(dataseterr)){                        
                        for (int i = 0; i < dataseterr.size(); i++) {
                            IData dataerr  = (DataMap)dataseterr.get(i);
                            if(IDataUtil.isNotEmpty(dataerr)){
                                //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxxxx3361 "+dataerr);

                                if(dataerr.getString("SERIAL_NUMBER","").trim().equals("手机号码")){//移除“以单位证件开户的开户证件变更”模版文件的字段名称行
                                    dataseterr.remove(i);
                                    break;
                                }
                            }
                        }
                    } 
                }                
            }else if("MODIFYCUSTINFO_M2M".equals(batOperType)){//REQ201801080006 行业应用卡类用户资料变更界面放开手机号码长度必须为11位的限制 add by fufn
            	if(err.length>0){
                    IDataset dataseterr = (IDataset)err[0];
                    IDataset dataseterrold=new DatasetList(dataseterr);
                    if(IDataUtil.isNotEmpty(dataseterr)){ 
                    	 IDataset datasetsuc = (IDataset)suc[0];
                        for (int i = dataseterrold.size()-1; i>=0; i--) {
                            IData dataerr  = (DataMap)dataseterrold.get(i);
                            if(IDataUtil.isNotEmpty(dataerr)){
                                if(dataerr.getString("SERIAL_NUMBER","").length()==13){
                                	if(!dataerr.getBoolean("IMPORT_RESULT", true)){
                                		dataerr.put("IMPORT_RESULT", true);
                                	} 
                                	datasetsuc.add(dataerr);
                                	dataseterr.remove(i);
                                	 
                                }
                            }
                        }
                    } 
                }     
            }else if("BATBNBDWIDENETDELETE".equals(batOperType)){//商务宽带拆机不检验号码长度 add by fufn
            	if(err.length>0){
                    IDataset dataseterr = (IDataset)err[0];
                    IDataset dataseterrold=new DatasetList(dataseterr);
                    if(IDataUtil.isNotEmpty(dataseterr)){ 
                    	 IDataset datasetsuc = (IDataset)suc[0];
                        for (int i = dataseterrold.size()-1; i>=0; i--) {
                            IData dataerr  = (DataMap)dataseterrold.get(i);
                            if(IDataUtil.isNotEmpty(dataerr)){
                            	if(dataerr.getString("SERIAL_NUMBER","").length()>0){
		                        	if(!dataerr.getBoolean("IMPORT_RESULT", true)){
		                        		dataerr.put("IMPORT_RESULT", true);
		                        	} 
		                        	datasetsuc.add(dataerr);
		                        	dataseterr.remove(i);
                            	}
                            }
                        }
                    } 
                }     
            }else if("MODIFYACYCINFO".equals(batOperType)){//BUG20190117173111 修改批量修改账户银行资料服务号码长度限制为11位的问题 add by mengqx
            	if(err.length>0){
                    IDataset dataseterr = (IDataset)err[0];
                    IDataset dataseterrold=new DatasetList(dataseterr);
                    if(IDataUtil.isNotEmpty(dataseterr)){ 
                    	 IDataset datasetsuc = (IDataset)suc[0];
                        for (int i = dataseterrold.size()-1; i>=0; i--) {
                            IData dataerr  = (DataMap)dataseterrold.get(i);
                            if(IDataUtil.isNotEmpty(dataerr)){
                                if(dataerr.getString("SERIAL_NUMBER","").length()==10){
                                	if(!dataerr.getBoolean("IMPORT_RESULT", true)){
                                		dataerr.put("IMPORT_RESULT", true);
                                	} 
                                	datasetsuc.add(dataerr);
                                	dataseterr.remove(i);
                                	 
                                }
                            }
                        }
                    } 
                }     
            }
            dataset.addAll(suc[0]);
            failds.addAll(err[0]);
            if(IDataUtil.isNotEmpty(dataset)){
            	allCount = dataset.size()+failds.size();
            }else{
            	allCount = failds.size();
            }
        }

        // 效验导入的数据是否正确
        IData fileCheckData = null;
        if (batOperType.equals("CampusBroadband"))
        {
            fileCheckData = fileImportCheckForCampusBroadband(data, dataset);
        }
        else if ("OFFICESTOPOPEN".equals(batOperType))
        {
            dataset = DataHelper.distinct(dataset, "SERIAL_NUMBER", ""); // 排重服务号码
            fileCheckData = fileImportCheckForProductChg(data, dataset);
        }else if ("TD_FIXED_PHONE_STOP".equals(batOperType))
        {// TD二代无线固话批量停机
            dataset = DataHelper.distinct(dataset, "SERIAL_NUMBER", ""); // 排重服务号码
            fileCheckData = fileImportCheckForTDFixedPhone(data, dataset);
        }else if ("TT_FIXED_PHONE_STOP".equals(batOperType))
        {// 铁通固话批量停机
            dataset = DataHelper.distinct(dataset, "SERIAL_NUMBER", ""); // 排重服务号码
            fileCheckData = fileImportCheckForTTFixedPhone(data, dataset);
        }

        else if ("SALEACTIVE".equals(batOperType))
        {
            fileCheckData = fileImportCheckForSaleActive(data, dataset);
        }
        else if("BATREALNAME".equals(batOperType))
        {//批量登记实名制业务入参效验
        	fileCheckData=fileImportCheckForBatRealName(data, dataset);
        }
        else if("BATMOBILESTOP".equals(batOperType))
        {//批量停机业务入参效验
        	fileCheckData=fileImportCheckForBatMobileStop(data, dataset);
        }
        else if("BATUPDATEPSW".equals(batOperType))
        {//批量修改密码入参校验
        	fileCheckData=fileImportCheckForBatUpdatePsw(data, dataset);
        }else if("BATDESTROYUSER".equals(batOperType)){
        	//批量销户
        	fileCheckData=fileImportCheckForBatDestroyUser(data, dataset);
        }else if("BATBNBDWIDENETDELETE".equals(batOperType)){
        	fileCheckData=fileImportCheckForBatBNBDWidenetDestroy(data, dataset);
        }
        else if("BATCREATEM2MTAG".equals(batOperType))
        {//批量增加行业应用卡标识
        	fileCheckData=fileImportCheckForBatCreateM2MTag(data, dataset);
        }
        else if ("BATQQNET".equals(batOperType))
        {
            fileCheckData = fileImportCheckForQQNet(data, dataset);
        }
        
        else if ("BATHBPAY".equals(batOperType))
        {
            fileCheckData = fileImportCheckForBatHbPay(data, dataset);
        }
        else if ("SERVICECHG".equals(batOperType))
        {
        	/**
        	 * REQ201803020015 关于视频彩铃业务优惠办理活动的开发需求
        	 * 批量任务变更办理视频彩铃校验
        	 */
        	fileCheckData = fileImportCheckForSPCL(data, dataset);
        }
        else if ("CREATEPREUSER".equals(batOperType))
        {
        	/**
        	 * REQ201711240002 产品变更、预约产品变更等界面，增加任我用、任我看套餐的办理
        	 */
        	fileCheckData = fileImportCheckForRWYK(data, dataset);
        }
        else if ("CREATEPREUSER_PWLW".equals(batOperType))
        {
        	fileCheckData = fileImportCheckForPWLW(data, dataset);
        }
        else if ("CREATEPREUSER_M2M".equals(batOperType))
        {
        	fileCheckData = fileImportCheckForPWLW(data, dataset);
        }
        else if ("CREATEPREUSER_BNBD".equals(batOperType))
        {
        	fileCheckData = fileImportCheckForBNBD(data, dataset);
        }
        //add by guonj for REQ201910310002 关于企业宽带套餐批量变更的开发需求 
        else if ("BATMODWIDEPACKAGE".equals(batOperType))
        {
        	fileCheckData = fileImportWidePackageChange(data, dataset);
        }
        //add by zhangxing3 for REQ201904180053新增批量宽带产品变更界面
        else if ("BATWIDEPRODUCTCHANGE".equals(batOperType))
        {
        	fileCheckData = fileImportWideProductChange(data, dataset);
        }
        //add by zhangxing3 for REQ201904180053新增批量宽带产品变更界面
        else if ("MODIFYPRODUCT_MAIN".equals(batOperType))
        {
        	fileCheckData = fileImportChangeProduct(data, dataset);
        }
        else if ("MODIFYPRODUCT_NAME".equals(batOperType))
        {
        	fileCheckData = fileImportChangeProductName(data, dataset);
        }
        else if ("SILENCECALLDEAL_PWLW".equals(batOperType))
        {
        	fileCheckData = fileImportCheckForSilencePWLW(data, dataset);
        }
        else if ("SVCQSUPERVISOR".equals(batOperType))
        {
        	fileCheckData = fileImportCheckForSVCQSUPERVISOR(data, dataset);
        }
        else if ("MODIFYCUSTINFO_M2M".equals(batOperType))
        {
        	fileCheckData = fileImportCheckForM2mName(data, dataset);
        }else if("MODIFY_GROUPMEMBER".equals(batOperType)){
        	/**
        	 * REQ201608150016 批量以单位证件开户集团成员用户实名资料变更业务
        	 * 
        	 */
        	fileCheckData = fileImportCheckModifyGroupmember(data, dataset);
        }else if("TESTCARDUSER".equals(batOperType)){
            /**	 	 
             * REQ201609060001_2016年下半年测试卡功能优化（二）		 
             * @author zhuoyingzhi 
             * 20160930 	 
             */ 	 
           fileCheckData = fileImportCheckTestCardUser(data, dataset);	 	 
        }else if("TESTCARDCUSTNAME".equals(batOperType)){
        	/**
        	 * REQ201610200010_关于测试卡管理的三点优化
        	 * @author zhuoyingzhi
        	 * 批量修改测试卡客户名称
        	 */
        	fileCheckData=fileImportCheckTestCardCustName(data, dataset);        	
        }else if("TESTCARDMAXFLOWVALUE".equals(batOperType)){
        	/**
        	 * REQ201610200010_关于测试卡管理的三点优化
        	 * @author zhuoyingzhi
        	 * 批量修改测试卡流量封顶值
        	 */
        	fileCheckData=fileImportCheckTestCardMaxFlowValue(data, dataset);
        }else if("MODIFYGROUPPSPTINFO".equals(batOperType)){
            /**
             * REQ201701160014关于开发批量修改单位证件开户资料界面的需求             
             */
            fileCheckData = fileImportCheckModifyGroupPspt(data, dataset);
        }else if("MODIFYTDPSPTINFO".equals(batOperType)){
            /**
             * REQ201901280015申请开发非实名无线固话批量登记实名           
             */
            fileCheckData = fileImportCheckModifyTdPspt(data, dataset);
        }
        else if("BATPCRFCHG".equals(batOperType)){
            
            fileCheckData = fileImportCheckForPCRF(data, dataset);
        }else if("BATOTHERCREATEUSER".equals(batOperType)){
            fileCheckData = fileImportCheckForOther(data, dataset);
        }else if("TESTPHONERETURN".equals(batOperType)){
            /**
             * REQ201707260013关于新增测试号码批量销号的功能的需求             
             */
            fileCheckData = fileImportCheckForNumber(data, dataset);
        }else if("TESTCARDOFFICEOPEN".equals(batOperType))
        {
            /**
             * REQ201612010002_关于2016年下半年测试卡规则优化需求（一）
             * @author zhuoyingzhi
             * 20170110
             * 测试号码批量局开
             */
              fileCheckData=fileImportCheckTestOfficeOpenMobileile(data, dataset);          	
        }else if("GRPDISCNTCHGSPEC".equals(batOperType))
        {
              fileCheckData=fileImportCheckGrpDiscntChgSpec(data, dataset);          	
        }else if("DISCNTCHG".equals(batOperType))
        {
            fileCheckData=fileImportCheckDiscntChg(data, dataset);          	
        }else if("BATBENEFITADDUSENUM".equals(batOperType))
        {
            fileCheckData=fileImportCheckBenefitAddUseNum(data, dataset);
        }else if("BATOPENSTOP".equals(batOperType))
        {
            fileCheckData=fileImportCheckOpenStop(data, dataset);
        }
        else{
            fileCheckData = fileImportCheckForProductChg(data, dataset);
        }
        IDataset succds = fileCheckData.getDataset("SUCCDS");
        IDataset failds2 = fileCheckData.getDataset("FAILDS");
        failds.addAll(failds2);

        // 如果效验结果是超过了月限量，则会返回提示信息
        String hint_message = importNumCheck(data, succds.size(), data.getInt("LIMIT_NUM_BATCH"), data.getInt("LIMIT_NUM_DAY"), data.getInt("LIMIT_NUM_MON"), data.getInt("SUMS"), data.getInt("MONTH_SUM"));

        // 将正确的
        if (succds.size() != 0)
        {
            specSuccdListForBatchType(data, succds);
            batch_id = insertImportData(succds, data);
        }
        /*else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "350000:导入正确的数据为0，请检查导入文件！");
        }*/

        IData returnInfo = new DataMap();
        returnInfo.put("cond_BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
        returnInfo.put("POP_cond_BATCH_TASK_ID", data.getString("POP_cond_BATCH_TASK_ID"));
        returnInfo.put("BATCH_OPER_CODE", batOperType);

        if (data.getString("NEED_ACTIVENOW_TAG").equals("1") && data.getString("AUDIT_STATE").equals("0"))
        {
            returnInfo.put("DEAL_TYPE", "1");
            returnInfo.put("BATCH_ID", batch_id);

        }
        else
        {
            returnInfo.put("DEAL_TYPE", "2");
            returnInfo.put("BATCH_ID", batch_id);
        }
        returnInfo.put("HINT_MESSAGE", hint_message);

        if (failds != null && failds.size() > 0)
        {
            String fileIdE = ImpExpUtil.getImpExpManager().getFileAction().createFileId();
            String fileName = batOperName + "导入失败文件.xls";
            File errorFile = ImpExpUtil.writeDataToFile("xls", new IDataset[]
            { failds }, "personserv", fileIdE, null, "import/bat/" + data.getString("BATCH_OPER_CODE") + ".xml");
            String errorFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(new FileInputStream(errorFile), "personserv", "upload/import", fileName);
            String errorUrl = ImpExpUtil.getDownloadPath(errorFileId, fileName);

            returnInfo.put("FAILED_TYPE", "1");
            returnInfo.put("DATASET_SIZE", allCount);
            returnInfo.put("SUCC_SIZE", succds.size());
            returnInfo.put("FAILD_SIZE", failds.size());
            returnInfo.put("FILE_ID", errorFileId);
            returnInfo.put("ERROR_URL", errorUrl);
        }
        returnInfos.add(returnInfo);
        return returnInfos;
    }
	/**
	 * @description 批量优惠变更，判断
	 * @param @param data
	 * @param @param dataset
	 * @param @return
	 * @return IData
	 * @author tanzheng
	 * @date 2019年7月26日
	 * @param data
	 * @param dataset
	 * @return
	 * @throws Exception
	 */
	private IData fileImportCheckDiscntChg(IData data, IDataset dataset) throws Exception {
		log.debug("DiscntChg import data start!!");
		log.debug("DiscntChg import data :"+data.toString());
		String batchTaskId = data.getString("BATCH_TASK_ID");
		IData cond = new DataMap();
		cond.put("BATCH_TASK_ID", batchTaskId);
		IDataset returnResult = BatTradeInfoQry.queryPopuTaskInfoByTaskId(cond, null);
		IData condStr1 = new DataMap(returnResult.first().getString("CODING_STR1"));
		String discntCode = condStr1.getString("DISCNT_CODE");
		String modifyTag = condStr1.getString("MODIFY_TAG");

        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        IData param = new DataMap();
        param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "96");
		param.put("PARAM_CODE", discntCode);
        IDataset commparaInfo = CommparaInfoQry.getCommparaInfoByPara(param);
        IData compara = commparaInfo.first();
        String productId = compara.getString("PARA_CODE3");

        if(StringUtils.isBlank(productId)){
        	log.debug("DiscntChg commparaInfo is empty!!");
        	succds.addAll(dataset);
        }else{
        	for (int i = 0; i < dataset.size(); i++)
            {
            	IData temp = dataset.getData(i);
            	if(!BofConst.MODIFY_TAG_ADD.equals(modifyTag)){
            		temp.put("IMPORT_ERROR", "该套餐仅支持新增操作");
                    failds.add(temp);
                    continue;
            	}
            	String serialNumber=temp.getString("SERIAL_NUMBER");
            	UcaData ucaData = null;
            	String userProductId = "";
				try {
					ucaData = UcaDataFactory.getNormalUca(serialNumber);
					userProductId = ucaData.getProductId();
					IDataset discntList = UserDiscntInfoQry.getAllDiscntByUserRoute(ucaData.getUserId(),discntCode);
					if(discntList.size()>0){
						for(Object discntTradeData : discntList){
							if(((IData)discntTradeData).getString("END_DATE").startsWith("2050")){
								temp.put("IMPORT_ERROR","用户已存在该优惠");
			                    failds.add(temp);

							}
						}
					}else{

						if(productId.equals(userProductId)){
							succds.add(temp);
						}else{
							temp.put("IMPORT_ERROR", "用户现用主套餐不能办理该优惠");
							failds.add(temp);
						}

					}
				} catch (Exception e) {
				    log.error("fileImportCheckDiscntChg 校验异常",e);
					temp.put("IMPORT_ERROR", e.getMessage());
                    failds.add(temp);
                    continue;
				}
            }
        }
        log.debug("fileImportCheckDiscntChg result:"+failds.toString());
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;

	}

    // 检查TD无线固话号码
    private IData fileImportCheckForTDFixedPhone(IData data, IDataset dataset) {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++) {
            IData iData = dataset.getData(i);
            String serialNumber = iData.getString("SERIAL_NUMBER");

            // 号码长度为11位必须以898开头。
            String tempNumber = serialNumber.substring(0, 3);
            System.out.println("tempNumber-------" + tempNumber);
            if (tempNumber.equals("898")) {
                succds.add(iData);
                continue;
            } else {
                iData.put("IMPORT_ERROR", "固话号码格式错误，需898开头！");
                failds.add(iData);
                continue;
            }
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;
    }

    // 检查TD无线固话号码
    private IData fileImportCheckForTTFixedPhone(IData data, IDataset dataset) {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData iData = dataset.getData(i);
            String serialNumber=iData.getString("SERIAL_NUMBER");

            // 号码长度为12位必须以0898开头。
            String tempNumber = serialNumber.substring(0,4);
            System.out.println("tempNumber-------"+tempNumber);
            if (tempNumber.equals("0898")){
                succds.add(iData);
                continue;
            }else {
                iData.put("IMPORT_ERROR", "固话号码格式错误，需0898开头！");
                failds.add(iData);
                continue;
            }
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;
    }


    private IData fileImportCheckForOther(IData pdata, IDataset dataset) {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
        	IData data = dataset.getData(i); 
        	String serialNumber=data.getString("SERIAL_NUMBER");
        	String psptId = data.getString("PSPT_ID");
        	String custName = data.getString("CUST_NAME");
        	String psptType = data.getString("PSPT_TYPE");
        	String adress = data.getString("ADRESS");
        	String contactPhone = data.getString("CONTACT_PHONE");
        	data.put("DATA1", psptId);
        	data.put("DATA2", custName);
        	data.put("DATA3", psptType);
        	data.put("DATA4", adress);
        	data.put("DATA5", contactPhone);
        	IData param = new DataMap();
        	try{
        		param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        		param.put("SERIAL_NUMBER", serialNumber);
        		CSAppCall.callOne( "SS.CreateHPersonUserSVC.checkHSerialNumber", param);			
        	}catch (Exception e) {
        		data.put("IMPORT_ERROR", e.getMessage());
                failds.add(data);
                continue;
			}
        	IData input = new DataMap();
            input.put("CERT_ID",psptId); 
            input.put("CERT_NAME",custName);
            IDataset ds;
			try {
				ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);
				if(!ds.isEmpty()){
					String reCode =  ds.getData(0).getString("X_RESULTCODE");
					if(reCode.equals("1")){//证件信息不合法
						data.put("IMPORT_ERROR", "使用人证件信息不合法");
						failds.add(data);
						continue;
					}
				}
			} catch (Exception e) {
				data.put("IMPORT_ERROR", e.getMessage());
                failds.add(data);
                continue;
			}
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;
    }

    public IDataset importDataForAcycInfo(IData data, IDataset dataset) throws Exception
    {
        String importMode = data.getString("MODIFYACYCINFO_IMPORT_MODE");
        IDataset returnInfos = new DatasetList();
        if ("2".equals(importMode))
        {
            returnInfos = importDataByFile(data, dataset);
        }
        else
        {
            String batch_id = "";
            getCommBatInfo(data);
            String batchTaskId = data.getString("cond_BATCH_TASK_ID");
            IData task = BatTaskInfoQry.qryBatTaskByBatchTaskId(batchTaskId);
            String condition2 = task.getString("CONDITION2");
            String codingStr = task.getString("CODING_STR1");
            IData oldAcctInfoMap = null;
            if (StringUtils.isNotBlank(condition2))
            {
                oldAcctInfoMap = new DataMap(condition2);
            }
            else
            {
                oldAcctInfoMap = new DataMap(codingStr);
            }

            BatDealBean bean = (BatDealBean) BeanManager.createBean(BatDealBean.class);
            IDataset succds = bean.queryUsersByBank2(oldAcctInfoMap, null);

            String hint_message = importNumCheck(data, succds.size(), data.getInt("LIMIT_NUM_BATCH"), data.getInt("LIMIT_NUM_DAY"), data.getInt("LIMIT_NUM_MON"), data.getInt("SUMS"), data.getInt("MONTH_SUM"));

            if (succds.size() != 0)
            {
                specSuccdListForBatchType(data, succds);
                batch_id = insertImportData(succds, data);
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据银行账号没有找到用户！");
            }

            // 设置初始化页面数据
            IData returnInfo = new DataMap();
            returnInfo.put("cond_BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
            returnInfo.put("POP_cond_BATCH_TASK_ID", data.getString("POP_cond_BATCH_TASK_ID"));
            returnInfo.put("BATCH_OPER_CODE", data.getString("BATCH_OPER_CODE"));

            if (data.getString("NEED_ACTIVENOW_TAG").equals("1") && data.getString("AUDIT_STATE").equals("0"))
            {
                returnInfo.put("DEAL_TYPE", "1");
                returnInfo.put("BATCH_ID", batch_id);
            }
            else
            {
                returnInfo.put("DEAL_TYPE", "2");
                returnInfo.put("BATCH_ID", batch_id);
            }
            returnInfo.put("HINT_MESSAGE", hint_message);

            returnInfos.add(returnInfo);

        }
        return returnInfos;
    }

    public void importDateCheck(String start_date, String end_date) throws Exception
    {
        Date startDate = SysDateMgr.string2Date(start_date, SysDateMgr.PATTERN_STAND);
        Date endDate = SysDateMgr.string2Date(end_date, SysDateMgr.PATTERN_STAND);
        Date sysDate = SysDateMgr.string2Date(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND);
        ;

        if (sysDate.getTime() - startDate.getTime() < 0 || sysDate.getTime() - endDate.getTime() > 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前时间已经不在起始时间和终止时间之间，不能导入");
        }
    }

    public String importNumCheck(IData data, int succds_count, int limit_once, int limit_day, int limit_month, int day_count, int month_count) throws Exception
    {
        String hint_message = "";
        if (succds_count > limit_once && limit_once != 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "导入条数过多：" + succds_count + ", 最大可导入条数为：" + limit_once + "!");
        }
        if (succds_count + day_count > limit_day && limit_day != 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "导入条数过多：" + succds_count + ", 今日最大可导入条数为：" + limit_day + "今日已导入条数为：" + day_count + "!");
        }
        if (succds_count + month_count > limit_month && limit_month != 0)
        {
            hint_message = "导入条数过多：" + succds_count + ", <br/>" + "本月最大可导入条数为：" + limit_month + ", <br/>" + "本月已导入条数为：" + month_count + "! <br/>" + "导入的数据将需要审批! <br/>";
        }

        if (hint_message != null && !hint_message.equals(""))
        {
            // 如果提示信息不为空或"",则表示需要审批
            setNeedApprParam(data, hint_message);
        }

        return hint_message;
    }

    public void importNumRangCheck(IData inParam) throws Exception
    {
        int end_post = 0;
        if ("1".equals(inParam.getString("IMPORT_MODE")))
        {
            // 服务号码段
            end_post = 7;
        }
        else if ("2".equals(inParam.getString("IMPORT_MODE")))
        {
            // SIM卡号段
            end_post = 16;
        }

        String prefix = inParam.getString("BEGIN_NUM").substring(0, end_post);
        String endNumber = inParam.getString("END_NUM").substring(0, end_post);

        if (!prefix.equals(endNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "前" + end_post + "位必须一样");
        }
        if ((inParam.getInt("END") - inParam.getInt("START")) < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "终止号必须大于等于起始号");
        }
    }

    public String insertImportData(IDataset dataset, IData inParam) throws Exception
    {

        String strSysDate = SysDateMgr.getSysTime();
        IDataset succds = new DatasetList();
        String batch_id = SeqMgr.getBatchId();
        inParam.put("BATCH_ID", batch_id);
        
        //客户图片id
        String custInfoPicId="";
        //经办人图片id
        String custInfoAgentPicId="";
        //联系人电话
        String phone="";
        
        // 获取批量任务所选择的产品id

        IData batchTaskInfo = BatTaskInfoQry.qryBatTaskByBatchTaskId(inParam.getString("BATCH_TASK_ID"));
        if (IDataUtil.isEmpty(batchTaskInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该批量任务不存在");
        }
        String productId = batchTaskInfo.getString("CONDITION1", "");
        String batOperType = batchTaskInfo.getString("BATCH_OPER_CODE", "");

        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            if (batOperType.equals("CREATEPREUSER_PWLW")) {//物联网开户   //liquan REQ201611280018 关于物联卡开户界面关于实名制的补充开发需求涉及实名制工作             
                String serialNumber = data.getString("SERIAL_NUMBER", "");
                if (serialNumber.length() >= 3 && serialNumber.startsWith("147")) {

                    String privTag = "0";
                    if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS_OPENUSER_WLW_147")) {
                        privTag = "1";
                    }
                    if ("0".equals(privTag)) {
                        CSAppException.apperr(BizException.CRM_BIZ_5, PersonConst.IS_NOT_FOR_OPENUSER_WLW147);
                    }

                    String psptTypeCode = "";
                    JSONArray array = new JSONArray();
                    array.element(batchTaskInfo.getString("CODING_STR1", ""));
                    DatasetList ds = new DatasetList(array.toString());
                    if (DataSetUtils.isNotBlank(ds)) {
                        psptTypeCode = ds.getData(0).getString("PSPT_TYPE_CODE", "");
                    }

                    if (psptTypeCode.length() > 0 && !"0".equals(psptTypeCode) && !"1".equals(psptTypeCode) && !"A".equals(psptTypeCode)) {//147开头的号码，客户证件类型必须为个人证件, 本地0 外地1 护照A
                        IDataset elementDs = ds.getData(0).getDataset("SELECTED_ELEMENTS");
                        for (int j = 0; j < elementDs.size(); j++) {
                            IData elementDm = elementDs.getData(j);
                            if ("99012000".equals(elementDm.getString("ELEMENT_ID", "").trim())) {// 99012000 物联网语音服务 
                                CSAppException.apperr(CrmCommException.CRM_COMM_888, "147号段号码开户并且选择了语音服务，开户证件类型必须是个人证件：本地身份证或外地身份证或护照！");
                            }
                        }
                    }                  
                }
                
                /**
                 * REQ201707170020_新增物联卡开户人像采集功能
                 * @author zhuoyingzhi
                 * @date  20170824
                 */
                JSONArray array_rx = new JSONArray();
                array_rx.element(batchTaskInfo.getString("CODING_STR1", ""));
                DatasetList ds_rx = new DatasetList(array_rx.toString());
                if (DataSetUtils.isNotBlank(ds_rx)) {
                    //经办人图片
                    custInfoPicId=ds_rx.getData(0).getString("custInfo_PIC_ID", "");
                    //经办人图片
                    custInfoAgentPicId=ds_rx.getData(0).getString("custInfo_AGENT_PIC_ID", "");
                    //联系人电话
                    phone=ds_rx.getData(0).getString("PHONE", "");
                }
                /**********************************************/
                
            }  //liquan REQ201611280018 关于物联卡开户界面关于实名制的补充开发需求涉及实名制工作
            
            //add by zhangxing3 for REQ201811260011批量预开号码导入数据优化
            if (batOperType.equals("CREATEPREUSER") && "2".equals(inParam.getString("IMPORT_MODE","")))
            {//批量预开户
            	//System.out.println("============insertImportData=========data:"+data);
            	String simCardNo = data.getString("DATA1", "");
            	String serialNumber = data.getString("SERIAL_NUMBER", "");

            	if("".equals(serialNumber))
            	{
            		serialNumber = BatDataImportBean.getSerialNumberBySimCardNo(simCardNo);
            	}
        		//System.out.println("============insertImportData=========serialNumber:"+serialNumber);

            	data.put("SERIAL_NUMBER", serialNumber);
            }
            //add by zhangxing3 for REQ201811260011批量预开号码导入数据优化
            
            IData newdata = new DataMap();
            newdata.put("BATCH_OPER_TYPE", inParam.getString("BATCH_OPER_CODE"));
            newdata.put("BATCH_TASK_ID", inParam.getString("BATCH_TASK_ID").trim());
            newdata.put("OPERATE_ID", SeqMgr.getBatchId());
            newdata.put("BATCH_ID", batch_id);
            newdata.put("PRIORITY", inParam.getString("PRIORITY", "1"));
            newdata.put("EXEC_TIME", strSysDate);
            if (inParam.getString("NEED_ACTIVENOW_TAG", "0").equals("1") && inParam.getString("AUDIT_STATE").equals("0"))
            {
                // 需立即启动并且不需要审核则DEAL_STATE改成已启动状态
                newdata.put("DEAL_STATE", "1");
            }
            else
            {
                newdata.put("DEAL_STATE", "0");
            }
            newdata.put("DEAL_TIME", strSysDate);
            newdata.put("REFER_TIME", strSysDate);
            newdata.put("CANCEL_TAG", "0"); // 未返销
            newdata.put("DATA10", productId);
            newdata.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batch_id));
          //和多号业务需求的改造
            if(StringUtils.isNotEmpty(inParam.getString("MSISDN_TYPE",""))){
                newdata.put("DATA3", inParam.getString("MSISDN_TYPE",""));
            }else{
            	newdata.put("DATA3", data.getString("DATA3",""));
            }
            newdata.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
            newdata.put(Route.ROUTE_EPARCHY_CODE, inParam.getString(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode()));
            newdata.put("DB_SOURCE", data.getString("DB_SOURCE", ""));
            newdata.put("DATA1", data.getString("DATA1", ""));
            newdata.put("DATA2", data.getString("DATA2", ""));
            newdata.put("DATA4", data.getString("DATA4", ""));
            newdata.put("DATA5", data.getString("DATA5", ""));
            newdata.put("DATA6", data.getString("DATA6", ""));
            newdata.put("DATA7", data.getString("DATA7", ""));
            newdata.put("DATA8", data.getString("DATA8", ""));
            newdata.put("DATA9", data.getString("DATA9", ""));
            newdata.put("DATA11", data.getString("DATA11", ""));
            newdata.put("DATA12", data.getString("DATA12", ""));
            newdata.put("DATA13", data.getString("DATA13", ""));
            newdata.put("DATA14", data.getString("DATA14", ""));
            newdata.put("DATA15", data.getString("DATA15", ""));
            newdata.put("DATA16", data.getString("DATA16", ""));
            newdata.put("DATA17", data.getString("DATA17", ""));
            newdata.put("DATA18", data.getString("DATA18", ""));
            newdata.put("DATA19", data.getString("DATA19", ""));
            newdata.put("DATA20", data.getString("DATA20", ""));
            newdata.put("CANCEL_DATE", data.getString("CANCEL_DATE", ""));
            newdata.put("CANCEL_STAFF_ID", data.getString("CANCEL_STAFF_ID", ""));
            newdata.put("CANCEL_DEPART_ID", data.getString("CANCEL_DEPART_ID", ""));
            newdata.put("CANCEL_CITY_CODE", data.getString("CANCEL_CITY_CODE", ""));
            newdata.put("CANCEL_EPARCHY_CODE", data.getString("CANCEL_EPARCHY_CODE", ""));
            newdata.put("DEAL_RESULT", data.getString("DEAL_RESULT", ""));
            newdata.put("DEAL_DESC", data.getString("DEAL_DESC", ""));
            newdata.put("TRADE_ID", data.getString("TRADE_ID", ""));

            succds.add(newdata);
        }

        /** 插明细表信息 */
        Dao.insert("TF_B_TRADE_BATDEAL", succds, Route.getJourDb(Route.CONN_CRM_CG));

        /** 插批量主表信息 */
        inParam.put("BATCH_COUNT", succds.size());
        importBatMain(dataset, inParam);

        return batch_id;
    }

    /** 查询批量业务参数信息 */
    public IData queryBatchTypeParams(IData param) throws Exception
    {

        IData data = BatchTypeInfoQry.queryBatchTypeParamsEx(param.getString("BATCH_OPER_CODE", ""), CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isEmpty(data))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有配置该批量类型参数");
        }

        return data;
    }

    public IDataset registerTradeInfo(IData data, IDataset dataset) throws Exception
    {
        String importMode = data.getString("IMPORT_MODE");
        IDataset returnInfos = new DatasetList();
        String msisdnType = data.getString("MSISDN_TYPE","");
        if ("0".equals(importMode))
        {
            // 文件导入
            returnInfos = importDataByFile(data, dataset);
        }
        else
        {
            // 号码段导入
            String batch_id = "";
            getCommBatInfo(data);
            data.put("IMPORT_MODE", importMode);
            int start = 0;
            int end = 0;
            try
            {
                if (importMode.equals("1"))
                {
                    start = Integer.parseInt(data.getString("BEGIN_NUM").substring(7));
                    end = Integer.parseInt(data.getString("END_NUM").substring(7));
                }
                else if (importMode.equals("2"))
                {
                    start = Integer.parseInt(data.getString("BEGIN_NUM").substring(16));
                    end = Integer.parseInt(data.getString("END_NUM").substring(16));
                }
            }
            catch (Exception NumberFormatException)
            {
            	if (log.isDebugEnabled())
                {
                	//log.info("(NumberFormatException);
                }
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "后4位必须是数字");
            }

            data.put("START", start);
            data.put("END", end);
            data.put("DATA3", msisdnType);
            int count = end - start + 1;

            // 如果效验结果是超过了月限量，则会返回提示信息
            String hint_message = importNumCheck(data, count, data.getInt("LIMIT_NUM_BATCH"), data.getInt("LIMIT_NUM_DAY"), data.getInt("LIMIT_NUM_MON"), data.getInt("SUMS"), data.getInt("MONTH_SUM"));

            importNumRangCheck(data);
            batch_id = importBatOpenUserRange(dataset, data);

            IData returnInfo = new DataMap();
            returnInfo.put("cond_BATCH_TASK_ID", data.getString("cond_BATCH_TASK_ID"));
            returnInfo.put("POP_cond_BATCH_TASK_ID", data.getString("POP_cond_BATCH_TASK_ID"));
            returnInfo.put("BATCH_OPER_CODE", data.getString("BATCH_OPER_CODE"));

            if (data.getString("NEED_ACTIVENOW_TAG").equals("1") && data.getString("AUDIT_STATE").equals("0"))
            {
                returnInfo.put("DEAL_TYPE", "1");
                returnInfo.put("BATCH_ID", batch_id);
            }
            else
            {
                returnInfo.put("DEAL_TYPE", "2");
                returnInfo.put("BATCH_ID", batch_id);
            }
            returnInfo.put("HINT_MESSAGE", hint_message);

            returnInfos.add(returnInfo);
        }
        return returnInfos;
    }

    private void setNeedApprParam(IData data, String hint_message) throws Exception
    {
        data.put("AUDIT_STATE", "1");
        data.put("AUDIT_REMARK", hint_message);
    }

    protected void specSuccdListForBatchType(IData data, IDataset succds) throws Exception
    {

    }
    /**
     * REQ201608150016_新增“以单位证件开户集团成员实名资料维护界面”需求    验证
     * @param pdData
     * @param dataset
     * @return
     * @throws Exception
     */
    public IData fileImportCheckModifyGroupmember(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        IData returnData = new DataMap(); 
        
        IDataset serialNumberList=new DatasetList();
        
        //记录  使用人证件类型 为   身份证 的使用人证件号码
        IDataset psptList=new DatasetList();
        
        if(IDataUtil.isEmpty(dataset)){
        	IData error=new DataMap();
        	error.put("IMPORT_ERROR", "内容为空");
            failds.add(error);
            returnData.put("SUCCDS", succds);
            returnData.put("FAILDS", failds);
            return returnData;
            
        }
        //
        List<Map<String, Object>> cidList=new ArrayList<Map<String,Object>>();
        //初始化
        BatDataImportBean.initCanUseCount(cidList, dataset);
        for (int i = 0; i < dataset.size(); i++)
        {
        	IData data = dataset.getData(i);
        	boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
        	//手机号码
        	String serial_number=data.getString("SERIAL_NUMBER");
        	
        	
        	if("".equals(serial_number)||serial_number == null){
	                data.put("IMPORT_ERROR", "手机号码 不能为空!");
	                failds.add(data);
	                continue;
      	     }else{
      	    	serial_number=serial_number.trim();
      	     }
        	//使用人名称
        	     	String use=data.getString("DATA1");
        	if("".equals(use)||use == null){
	              data.put("IMPORT_ERROR", "使用人名称 不能为空");
	              failds.add(data);
	              continue;
    	    }
        	//使用人证件类型
        	String use_pspt_type_code=data.getString("DATA2");
        	
        	if("".equals(use_pspt_type_code)||use_pspt_type_code == null){
	              data.put("IMPORT_ERROR", "使用人证件类型 不能为空");
	              failds.add(data);
	              continue;
  	        }else{
  	            //类型转换
  	        	String[] key=new String[3];
  	        	         key[0]="TYPE_ID";
  	        	         key[1]="SUBSYS_CODE";
  	        	         key[2]="DATA_NAME";
  	     	    String[] value=new String[3];
  	     	    		 value[0]="TD_S_PASSPORTTYPE2";
  	     	    		 value[1]="CUSTMGR";
  	     	    		 value[2]=use_pspt_type_code.trim();
  	     	    		 
  	        	data.put("DATA2", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
  	        }
        	//使用人证件号码
        	String use_pspt_id=data.getString("DATA3").trim();
        	if("".equals(use_pspt_id)||use_pspt_id == null){
	              data.put("IMPORT_ERROR", "使用人证件号码   不能为空");
	              failds.add(data);
	              continue;
        	}
        	//使用人证件地址
        	/**
        	 * 由于1到15中的长度小于500,跟数据库不一样 ,数据库中为500
        	 */
        	String use_pspt_addr=data.getString("DATA16");
        	if("".equals(use_pspt_addr)||use_pspt_addr == null){
	              data.put("IMPORT_ERROR", "使用人证件地址   不能为空");
	              failds.add(data);
	              continue;
        	}else{
        		String use_pspt_addr_max=BatDataImportBean.maxLen("使用人证件地址", use_pspt_addr,80);
        		if(!"0000".equals(use_pspt_addr_max)){
  	              data.put("IMPORT_ERROR", use_pspt_addr_max);
	              failds.add(data);
	              continue;
        		}
        	}       	
        	//经办人名称
        	String agent_cust_name=data.getString("DATA5");
        	if("".equals(agent_cust_name)||agent_cust_name == null){
	              data.put("IMPORT_ERROR", "经办人名称   不能为空");
	              failds.add(data);
	              continue;
      	     }        	
        	//经办人证件类型
        	String agent_pspt_type_code=data.getString("DATA6");
        	if("".equals(agent_pspt_type_code)||agent_pspt_type_code == null){
	              data.put("IMPORT_ERROR", "经办人证件类型   不能为空");
	              failds.add(data);
	              continue;
    	     }else{
    	    	 //类型转换
				String[] key = new String[3];
					key[0] = "TYPE_ID";
					key[1] = "SUBSYS_CODE";
					key[2] = "DATA_NAME";
				String[] value = new String[3];
					value[0] = "TD_S_PASSPORTTYPE2";
					value[1] = "CUSTMGR";
					value[2] = agent_pspt_type_code.trim();
  	    		 
  	    		 data.put("DATA6", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
    	     }       	
        	//经办人证件号码
        	String agent_pspt_id=data.getString("DATA7").trim();
        	if("".equals(agent_pspt_id)||agent_pspt_id == null){
	              data.put("IMPORT_ERROR", "经办人证件号码   不能为空");
	              failds.add(data);
	              continue;
  	        }           	
        	//经办人证件地址
        	/**
        	 * 由于1到15中的长度小于500,跟数据库不一样 ,数据库中为500
        	 */
        	String agent_pspt_addr=data.getString("DATA17");
        	if("".equals(agent_pspt_addr)||agent_pspt_addr == null){
	              data.put("IMPORT_ERROR", "经办人证件地址   不能为空");
	              failds.add(data);
	              continue;
	        }else{
        		String agent_pspt_addr_max=BatDataImportBean.maxLen("经办人证件地址", agent_pspt_addr,200);
        		if(!"0000".equals(agent_pspt_addr_max)){
  	              data.put("IMPORT_ERROR", agent_pspt_addr_max);
	              failds.add(data);
	              continue;
        		}
	        }

        	/**
        	 *  导入的号码必须是以单位证件开户，否则不允许导入
        	 */
			try {
				UcaData uca = null;
				uca = UcaDataFactory.getNormalUca(serial_number);
	        	if(BatDataImportBean.isPersonCertificate(uca.getCustomer().getPsptTypeCode())){
	        		  //个人
		              data.put("IMPORT_ERROR", "号码必须是以单位证件开户");
		              failds.add(data);
		              continue;
	        	}
			}catch (Exception e) {
	              data.put("IMPORT_ERROR",e.getMessage());
	              failds.add(data);
	              continue;
			}
        	

        	
        	/**
        	 * 判断用户是否集团成员
        	 * <br/>
        	 * REQ201610210009 - 取消“以单位证件开户集团成员用户实名资料变更 ”界面集团成员限制
        	 * 20161021
        	 * @author zhuoyingzhi
        	 */
/*      	    if(!BatDataImportBean.is89custGroupmember(serial_number)){
	              data.put("IMPORT_ERROR", "手机号码不是898集团的成员");
	              failds.add(data);
	              continue;
      	    }*/
      	    
      	    
      	    /***************************使用人***************************************/
      	    /**
      	     * 使用人及经办人的证件类型需为个人证件，证件及证件号码按照目前个人证件及号码的校验规则进行校验。 
      	     * （本地身份证 0、外地身份证 1、户口本 2、护照  A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
      	     */
      	     //个人证件为 useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M"
      	     //使用人证件类型 
      	    String  useObjValue=data.getString("DATA2");
      	     if(!BatDataImportBean.isPersonCertificate(useObjValue)){
	              data.put("IMPORT_ERROR", "使用人的证件类型需为个人证件");
	              failds.add(data);
	              continue;
      	     }else{
      	    	 //个人证件类型
      	    	 
      	    	//使用人姓名验证
      	    	String checkUseName=BatDataImportBean.checkCustName("使用人姓名", use, useObjValue);
      	    	if(!"0000".equals(checkUseName)){
	  	              data.put("IMPORT_ERROR", checkUseName);
		              failds.add(data);
		              continue;
      	    	}
      	    	
      	    	//使用人地址验证
      	    	String checkUseAddr=BatDataImportBean.checkAddr("使用人证件地址", use_pspt_addr);
      	    	if(!"0000".equals(checkUseAddr)){
	  	              data.put("IMPORT_ERROR", checkUseAddr);
		              failds.add(data);
		              continue;
      	    	}
      	    	
      	    	 //使用人证件号码
            	if(isRepeatCode(use_pspt_id)){
            		
            		data.put("IMPORT_ERROR", "使用人证件号码不能全为同一个数字，请重新输入!");
					failds.add(data);
					continue;
            	}else if(isSerialCode(use_pspt_id)){
            		
            		data.put("IMPORT_ERROR", "使用人证件号码不能连续数字，请重新输入!");
					failds.add(data);
					continue;
            	}
      	    	//电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码
            	
            	if(use_pspt_id.length() >=4
            			 &&serial_number.length()>=use_pspt_id.length()
            			 &&(serial_number.indexOf(use_pspt_id)==0
            				 ||serial_number.lastIndexOf(use_pspt_id) == (serial_number.length()-use_pspt_id.length()))
            	     ){
              		data.put("IMPORT_ERROR", "电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码!");
					failds.add(data);
					continue;
				}
				// （本地身份证 0、外地身份证 1、户口本 2、护照 A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
				// 港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
				if ("H".equals(useObjValue)) {
					if (use_pspt_id.length() != 9 && use_pspt_id.length() != 11) {
						data.put("IMPORT_ERROR", "港澳居民回乡证校验：使用人证件号码必须为9位或11位！!");
						failds.add(data);
						continue;
					}
            		if(!(use_pspt_id.charAt(0)=='H' || use_pspt_id.charAt(0)=='M') 
            				|| !StringUtils.isNumeric(use_pspt_id.substring(1)) ){
                  		data.put("IMPORT_ERROR", "港澳居民回乡证校验：使用人证件号码首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字！");
    					failds.add(data);
    					continue;
            		}
            	}else if("I".equals(useObjValue)){
					// 台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
					if (use_pspt_id.length() != 8 && use_pspt_id.length() != 11) {
						data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码必须为8位或11位！");
						failds.add(data);
						continue;
					}
					if (use_pspt_id.length() == 8) {
						if (!StringUtils.isNumeric(use_pspt_id)) {
							data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码为8位时，必须均为阿拉伯数字！");
							failds.add(data);
							continue;
						}
					}
					if (use_pspt_id.length() == 11) {
						if (!StringUtils.isNumeric(use_pspt_id.substring(0, 10))) {
							data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码为11位时，前10位必须均为阿拉伯数字。");
							failds.add(data);
							continue;
						}
					}
				} else if ("C".equals(useObjValue) || "A".equals(useObjValue)) {
					 
	                if (use_pspt_id.trim().indexOf(" ")!=-1) {
						String tmpName = useObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
						data.put("IMPORT_ERROR", tmpName + "证件号码中间不能有空格。");
						failds.add(data);
						continue;
					}
					// 军官证、警官证、护照：证件号码须大于等于6位字符
					if (use_pspt_id.length() < 6) {
						String tmpName = useObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
						data.put("IMPORT_ERROR", tmpName + "使用人证件号码须大于等于6位字符！");
						failds.add(data);
						continue;
					}
            	}else if("0".equals(useObjValue)|| 
            				"1".equals(useObjValue) || 
            				"2".equals(useObjValue)) {
            	 
            		
            		//身份证相关检查 
                    if (use_pspt_id.length() == 15 || use_pspt_id.length() == 18)
                    {
                        if (StringUtils.isNotBlank(checkPspt(use_pspt_id, "", useObjValue)))
                        {
                           // 证件号码不正确
                        	data.put("IMPORT_ERROR", "使用人证件号码格式不对");
	     					failds.add(data);
	     					continue;
                        }
                    }else{
                    	    data.put("IMPORT_ERROR", "使用人证件号码位数不对");
	     					failds.add(data);
	     					continue;
                    }                    
                    
                    String temppsptid = use_pspt_id;
                    if(use_pspt_id.length()==15){
                    	temppsptid = IdcardUtils.conver15CardTo18(temppsptid);
                    }                    
					int age = IdcardUtils.getExactAgeByIdCard(temppsptid);
					 if (age < 16 || age > 120) {
					 	 data.put("IMPORT_ERROR", "年龄范围必须在16-120岁之间");
						 failds.add(data);
						 continue;
					 }
					 /*if (age > 120) {
					 	 data.put("IMPORT_ERROR", "年龄范围必须在120岁之内");
						 failds.add(data);
						 continue;
					 }*/
					
					IData input = new DataMap();
					input.put("CERT_ID", use_pspt_id);
					input.put("CERT_NAME", use);
					IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);
					if (!ds.isEmpty()) {
						String reCode = ds.getData(0).getString("X_RESULTCODE");
						if (reCode.equals("1")) {// 证件信息不合法
							data.put("IMPORT_ERROR", "使用人证件信息不合法");
							failds.add(data);
							continue;
						}
					}
					
				} else if ("N".equals(useObjValue)) {// 台湾居民来往大陆通行证
					String psptname = "台湾居民来往大陆通行证校验";
					String desc = "证件号码";
					String psptIdtemp = replacespace(use_pspt_id);
					if (!psptIdtemp.equals(use_pspt_id)) {
						data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
						failds.add(data);
						continue;
					}
					
					int psptlength = use_pspt_id.length();
					
					if (psptlength < 4) {
						data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
						failds.add(data);
						continue;
					}
					
					if (!use_pspt_id.substring(0, 2).equals("TW") && !use_pspt_id.substring(0, 4).equals("LXZH")) {
						if (psptlength == 11 || psptlength == 12) {
							if (!StringUtils.isNumeric(use_pspt_id.substring(0, 10))) {
								data.put("IMPORT_ERROR", psptname + "：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
								failds.add(data);
								continue;
							}
						} else if (psptlength == 8 || psptlength == 7) {
							if (!StringUtils.isNumeric(use_pspt_id)) {
								data.put("IMPORT_ERROR", psptname + "：" + desc + "为" + psptlength + "位时，必须均为阿拉伯数字。");
								failds.add(data);
								continue;
							}
						} else {
							if (!StringUtils.isNumeric(use_pspt_id)) {
								data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
								failds.add(data);
								continue;
							}
						}
					}else {						
					    String psptIdsub=null; 
					    String psptIdsub2=null; 
					    if(use_pspt_id.substring(0, 2).equals("TW")){
					    	psptIdsub="TW";
					    	psptIdsub2=use_pspt_id.substring(2);
					    }else if(use_pspt_id.substring(0, 4).equals("LXZH")){
					    	psptIdsub="LXZH";
					    	psptIdsub2=use_pspt_id.substring(4);
					    }
					    if(psptIdsub!=null){
					    	
					    	PsptUtils  psptus = new PsptUtils();
					    	if(!psptus.isHaveUpperCase(psptIdsub2)||!psptus.isHaveNumeric(psptIdsub2)
					    			||psptIdsub2.indexOf("(")==-1||psptIdsub2.indexOf(")")==-1){//有大写					    		 			       			   	
								data.put("IMPORT_ERROR", psptname + "：" + desc + "前2位为“TW”或 “LXZH”字符时，后面需是阿拉伯数字、英文大写字母与半角“()”的组合");
								failds.add(data);
								continue;
					    	}
					    }						
					}																			
				} else if ("O".equals(useObjValue)) {
					String psptname = "港澳居民来往内地通行证校验";
					String desc = "证件号码";					
					
					String psptIdtemp = replacespace(use_pspt_id);
					if (!psptIdtemp.equals(use_pspt_id)) {
						data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
						failds.add(data);
						continue;
					}
					
					int psptlength = use_pspt_id.length();			 
	                //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
	                if (psptlength != 9 && psptlength!= 11) {	                    
						data.put("IMPORT_ERROR",  psptname + "：" + desc + "必须为9位或11位。");
						failds.add(data);
						continue;	                    
	                }
	                if (!(use_pspt_id.substring(0,1).equals("H") || use_pspt_id.substring(0,1).equals("M")) || !StringUtils.isNumeric(use_pspt_id.substring(1))) {
	                	data.put("IMPORT_ERROR",  psptname + "：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
						failds.add(data);
						continue;
	                }	            					
				}				
				
			}
      	     
	            //使用人证件(检查同一证件号已开实名制用户的数量是否已超出预定值)
	         	if(!BatDataImportBean.updateCanCount(cidList, use_pspt_id)){
               	    data.put("IMPORT_ERROR", "证件号【"+use_pspt_id+"】已开实名制用户的数量已超出预定值");
 					failds.add(data);
 					continue;
	         	}
	         	
      	     /**************************经办人**************************************/
		      	   /**
		       	     * 使用人及经办人的证件类型需为个人证件，证件及证件号码按照目前个人证件及号码的校验规则进行校验。 
		       	     * （本地身份证 0、外地身份证 1、户口本 2、护照  A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
		       	     */
		       	     //个人证件为 useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M"
		       	     //经办人证件类型 agent_pspt_type_code
		       	    String  agentPsptObjValue=data.getString("DATA6");
		       	   
		       	    if(!BatDataImportBean.isPersonCertificate(agentPsptObjValue)){
		 	              data.put("IMPORT_ERROR", "经办人的证件类型需为个人证件");
		 	              failds.add(data);
		 	              continue;
		       	     }else{
		       	    	 //个人证件类型
		       	    	 
		       	    	//经办人姓名验证
		       	    	String checkAgentName=BatDataImportBean.checkCustName("经办人姓名", agent_cust_name, agentPsptObjValue);
		       	    	if(!"0000".equals(checkAgentName)){
		 	  	              data.put("IMPORT_ERROR", checkAgentName);
		 		              failds.add(data);
		 		              continue;
		       	    	}
		       	    	
		       	    	//经办人地址验证
		       	    	String checkAgentAddr=BatDataImportBean.checkAddr("经办人证件地址", agent_pspt_addr);
		       	    	if(!"0000".equals(checkAgentAddr)){
		 	  	              data.put("IMPORT_ERROR", checkAgentAddr);
		 		              failds.add(data);
		 		              continue;
		       	    	}
		       	    	 
		       	    	 //经办人证件号码
		             	if(isRepeatCode(agent_pspt_id)){
		             		
		             		data.put("IMPORT_ERROR", "经办人证件号码不能全为同一个数字，请重新输入!");
		 					failds.add(data);
		 					continue;
		             	}else if(isSerialCode(agent_pspt_id)){
		             		
		             		data.put("IMPORT_ERROR", "经办人证件号码不能连续数字，请重新输入!");
		 					failds.add(data);
		 					continue;
		             	}
		       	    	//电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为经办人证件号码
		             	
		             	if(agent_pspt_id.length() >=4
		             			 &&serial_number.length()>=agent_pspt_id.length()
		             			 &&(serial_number.indexOf(agent_pspt_id)==0
		             				 ||serial_number.lastIndexOf(agent_pspt_id) == (serial_number.length()-agent_pspt_id.length()))
		             	     ){
		               		data.put("IMPORT_ERROR", "电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码!");
		 					failds.add(data);
		 					continue;
		             	}
		             	//（本地身份证 0、外地身份证 1、户口本 2、护照  A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
		             	//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
		             	if("H".equals(agentPsptObjValue)){
		             		if(agent_pspt_id.length()!=9 && agent_pspt_id.length()!=11){
		                   		data.put("IMPORT_ERROR", "港澳居民回乡证校验：经办人证件号码必须为9位或11位！!");
		     					failds.add(data);
		     					continue;
		             		}
		             		if(!(agent_pspt_id.charAt(0)=='H' || agent_pspt_id.charAt(0)=='M')
		             				|| !StringUtils.isNumeric(agent_pspt_id.substring(1)) ){
		                   		data.put("IMPORT_ERROR", "港澳居民回乡证校验：经办人证件号码首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字！");
		     					failds.add(data);
		     					continue;
		             		}
		             	}else if("I".equals(agentPsptObjValue)){
		             		//台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
		             		if(agent_pspt_id.length()!=8 && agent_pspt_id.length()!=11){
		                   		data.put("IMPORT_ERROR", "台湾居民回乡校验：经办人证件号码必须为8位或11位！");
		     					failds.add(data);
		     					continue;
		             		}
		             		if(agent_pspt_id.length()==8){
		             			if(!StringUtils.isNumeric(agent_pspt_id)){
		                      		data.put("IMPORT_ERROR", "台湾居民回乡校验：经办人证件号码为8位时，必须均为阿拉伯数字！");
		         					failds.add(data);
		         					continue;
		             			}
		             		}
		             		if(agent_pspt_id.length()==11){
		             			if(!StringUtils.isNumeric(agent_pspt_id.substring(0,10))){
		                     		data.put("IMPORT_ERROR", "台湾居民回乡校验：经办人证件号码为11位时，前10位必须均为阿拉伯数字。");
		         					failds.add(data);
		         					continue;
		             			}
		             		}
		             	}else if("C".equals(agentPsptObjValue) || "A".equals(agentPsptObjValue)){
		             		  if (agent_pspt_id.trim().indexOf(" ")!=-1) {
		  						String tmpName = agentPsptObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
		  						data.put("IMPORT_ERROR", tmpName + "证件号码中间不能有空格。");
		  						failds.add(data);
		  						continue;
		  					}
		             		//军官证、警官证、护照：证件号码须大于等于6位字符
		             		if(agent_pspt_id.length() < 6){
		             			String tmpName= agentPsptObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
		                 		data.put("IMPORT_ERROR", tmpName+"经办人证件号码须大于等于6位字符！");
		     					failds.add(data);
		     					continue;
		             		}
		             	}else if("0".equals(agentPsptObjValue)|| 
	            				"1".equals(agentPsptObjValue) || 
	            				"2".equals(agentPsptObjValue)) {
		             		//身份证相关检查 
		                     if (agent_pspt_id.length() == 15 || agent_pspt_id.length() == 18)
		                     {
		                         if (StringUtils.isNotBlank(checkPspt(agent_pspt_id, "", agentPsptObjValue)))
		                         {
	                                // 证件号码不正确
			                 		data.put("IMPORT_ERROR", "经办人证件号码格式不对");
			     					failds.add(data);
			     					continue;
		                         }
		                     }else{
			                 		data.put("IMPORT_ERROR", "经办人证件号码位数不对");
			     					failds.add(data);
			     					continue;
		                     }
		                     
		                     String temppsptid = agent_pspt_id;
		                     if(agent_pspt_id.length()==15){
		                     	temppsptid = IdcardUtils.conver15CardTo18(temppsptid);

		                     }
		 					 int age = IdcardUtils.getExactAgeByIdCard(temppsptid);
		 					 if (age < 16 || age > 120) {
		 					 	 data.put("IMPORT_ERROR", "年龄范围必须在16-120岁之间");
		 						 failds.add(data);
		 						 continue;
		 					 }
		 					 /*if (age > 120) {
		 					 	 data.put("IMPORT_ERROR", "年龄范围必须在120岁之内");
		 						 failds.add(data);
		 						 continue;
		 					 }*/
		                     
		                     IData input = new DataMap();
		                     input.put("CERT_ID",agent_pspt_id); 
		                     input.put("CERT_NAME",agent_cust_name);
		                     IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);
			                  
		                     if(!ds.isEmpty()){
		                         String reCode =  ds.getData(0).getString("X_RESULTCODE");
		                         if(reCode.equals("1")){//证件信息不合法
	                                 data.put("IMPORT_ERROR", "经办人证件信息不合法");
	                                 failds.add(data);
	                                 continue;
		                         }
		                     }
		                     
		                     
		             	}else if ("N".equals(agentPsptObjValue)) {// 台湾居民来往大陆通行证
							String psptname = "台湾居民来往大陆通行证校验";
							String desc = "证件号码";
							String psptIdtemp = replacespace(agent_pspt_id);
							if (!psptIdtemp.equals(agent_pspt_id)) {
								data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
								failds.add(data);
								continue;
							}
							
							int psptlength = agent_pspt_id.length();
							
							if (psptlength < 4) {
								data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
								failds.add(data);
								continue;
							}
							
							if (!agent_pspt_id.substring(0, 2).equals("TW") && !agent_pspt_id.substring(0, 4).equals("LXZH")) {
								if (psptlength == 11 || psptlength == 12) {
									if (!StringUtils.isNumeric(agent_pspt_id.substring(0, 10))) {
										data.put("IMPORT_ERROR", psptname + "：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
										failds.add(data);
										continue;
									}
								} else if (psptlength == 8 || psptlength == 7) {
									if (!StringUtils.isNumeric(agent_pspt_id)) {
										data.put("IMPORT_ERROR", psptname + "：" + desc + "为" + psptlength + "位时，必须均为阿拉伯数字。");
										failds.add(data);
										continue;
									}
								} else {
									if (!StringUtils.isNumeric(agent_pspt_id)) {
										data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
										failds.add(data);
										continue;
									}
								}
							}else {								
							    String psptIdsub=null; 
							    String psptIdsub2=null; 
							    if(agent_pspt_id.substring(0, 2).equals("TW")){
							    	psptIdsub="TW";
							    	psptIdsub2=agent_pspt_id.substring(2);
							    }else if(agent_pspt_id.substring(0, 4).equals("LXZH")){
							    	psptIdsub="LXZH";
							    	psptIdsub2=agent_pspt_id.substring(4);
							    }
							    if(psptIdsub!=null){
							    	
							    	PsptUtils  psptus = new PsptUtils();
							    	if(!psptus.isHaveUpperCase(psptIdsub2)||!psptus.isHaveNumeric(psptIdsub2)
							    			||psptIdsub2.indexOf("(")==-1||psptIdsub2.indexOf(")")==-1){//有大写					    		 			       			   	
										data.put("IMPORT_ERROR", psptname + "：" + desc + "前2位为“TW”或 “LXZH”字符时，后面需是阿拉伯数字、英文大写字母与半角“()”的组合");
										failds.add(data);
										continue;
							    	}
							    }								
							}																			
						} else if ("O".equals(agentPsptObjValue)) {
							String psptname = "港澳居民来往内地通行证校验";
							String desc = "证件号码";					
							
							String psptIdtemp = replacespace(agent_pspt_id);
							if (!psptIdtemp.equals(agent_pspt_id)) {
								data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
								failds.add(data);
								continue;
							}
							
							int psptlength = agent_pspt_id.length();					 
			                //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
			                if (psptlength != 9 && psptlength!= 11) {	                    
								data.put("IMPORT_ERROR",  psptname + "：" + desc + "必须为9位或11位。");
								failds.add(data);
								continue;	                    
			                }
			                if (!(agent_pspt_id.substring(0,1).equals("H") || agent_pspt_id.substring(0,1).equals("M")) || !StringUtils.isNumeric(agent_pspt_id.substring(1))) {
			                	data.put("IMPORT_ERROR",  psptname + "：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
								failds.add(data);
								continue;
			                }	            
			                
						}
		             	
		             	
		             	
		             	
		             	
		             	
		       	     }
	         	//验证本地导入的手机号码是否存在重复
	         	String check_serial_number=BatDataImportBean.checkSerialNumberCount(serial_number, serialNumberList);
	         	
	         	if(!"0000".equals(check_serial_number)){
	                 data.put("IMPORT_ERROR", check_serial_number);
	                 failds.add(data);
	                 continue;
	         	}
	         	
         	/**
         	 * 20161008  不允许一证多名(使用人)
         	 * 
         	 */
	         String check_use_pspt_id=BatDataImportBean.checkPsptIdCount(use,useObjValue, use_pspt_id, psptList);
	         if(!"0000".equals(check_use_pspt_id)){
                 data.put("IMPORT_ERROR", check_use_pspt_id);
                 failds.add(data);
                 continue;
	         }
             IData input = new DataMap();
             input.put("CERT_ID",use_pspt_id); 
             input.put("CERT_NAME",use);
             input.put("CERT_TYPE",useObjValue);             
             IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCardName", input);                       
             if(!ds.isEmpty()){
                 String reCode =  ds.getData(0).getString("X_RESULTCODE");
                 if(!reCode.equals("0")){// 
                     data.put("IMPORT_ERROR", "使用人证件开户的号码已经达到5个，不能再开户");
                     failds.add(data);
                     continue;
                 }
             }
            succds.add(data);
        }
        //特殊处理
        if (failds != null && failds.size() > 0){
        	for(int k=0;k<failds.size();k++){
        		if(k==0){
                	IData error=new DataMap();
                	error=failds.getData(0);
                	error.put("WADE_TRANSFORM_ERROR_DATA", true);
                	failds.remove(0);
                	failds.add(0, error);
        		}
        		//将证件类型转换为中文名
        		failds.getData(k).put("DATA2", BatDataImportBean.getTypeNameByTypeCode(failds.getData(k).getString("DATA2")));
        		failds.getData(k).put("DATA6", BatDataImportBean.getTypeNameByTypeCode(failds.getData(k).getString("DATA6")));
        	}
        }
        
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    
    /*
     * REQ201701160014关于开发批量修改单位证件开户资料界面的需求
     * liquan
     */
    
    public IData fileImportCheckModifyGroupPspt(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();

        IDataset serialNumberList = new DatasetList();

        if (IDataUtil.isEmpty(dataset)) {
            IData error = new DataMap();
            error.put("IMPORT_ERROR", "内容为空");
            failds.add(error);
            return null;
        }
        
        //避免重复校验
        //List<IData> enterpriselist = new ArrayList<IData>();//已经过校验的营业执照
        //List<IData> orglist = new ArrayList<IData>();//已经过校验的组织机构代码证
        List<IData> idcardlist = new ArrayList<IData>();//已经过校验的身份证
        
        for (int i = 0; i < dataset.size(); i++) {
            IData data = dataset.getData(i);
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult) {
                failds.add(data);
                continue;
            }
            if (data.getString("SERIAL_NUMBER","").trim().equals("手机号码")) {
                continue;
            }
            //获取所有字段，校验不能为空
        	//DATA13使用人名称，DATA14使用人证件类型，DATA15使用人证件号码，DATA16使用人证件地址    	
            String pspt_type_code_name = data.getString("DATA2");                        
            String d13 = data.getString("DATA13","").trim();
            String d14 = data.getString("DATA14","").trim();
            String d15 = data.getString("DATA15","").trim();
            String d16 = data.getString("DATA16","").trim();
          //证件类型
            String pspt_type_code = "";
            if (pspt_type_code_name == null || "".equals(pspt_type_code_name)) {
                data.put("IMPORT_ERROR", "证件类型不能为空");
                failds.add(data);
                continue;
            } else {
                //类型转换
                String[] key = new String[3];
                key[0] = "TYPE_ID";
                key[1] = "SUBSYS_CODE";
                key[2] = "DATA_NAME";
                String[] value = new String[3];
                value[0] = "TD_S_PASSPORTTYPE2";
                value[1] = "CUSTMGR";
                value[2] = pspt_type_code_name.trim();
                data.put("DATA2", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
                pspt_type_code = data.getString("DATA2", "").trim();
            }
            
            //手机号码
            String serial_number = data.getString("SERIAL_NUMBER");

            if (serial_number == null || "".equals(serial_number)) {
                data.put("IMPORT_ERROR", "手机号码不能为空!");
                failds.add(data);
                continue;
            } else {
                serial_number = serial_number.trim();
            }

            //查询手机号码是否有对应的客户信息，开户证件是否是集团证件
            IData ups = new DataMap();
            try {
            UcaData uca = UcaDataFactory.getNormalUca(serial_number);
            ups.put("USER_ID", uca.getUserId());
            ups.put("CUST_ID", uca.getCustId());
            ups.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));   
            } catch (Exception e) {
                data.put("IMPORT_ERROR", e.getMessage());
                failds.add(data);
                continue;
            }

            
            IDataset custInfos = new DatasetList();
            try {
                custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", ups);
            } catch (Exception e) {
                data.put("IMPORT_ERROR", e);
                failds.add(data);
                continue;
            }
            
            IData params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            String strPsptTypeCode = params.getString("PSPT_TYPE_CODE","").trim();
            UcaData uca = UcaDataFactory.getNormalUca(serial_number);
            //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxx4776 " + params);
			//System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxx4777 " + uca.getBrandCode());
			if (uca.getBrandCode().equals("TDYD")) {
			} else {
				if (!"D".equals(strPsptTypeCode) && !"E".equals(strPsptTypeCode) && !"G".equals(strPsptTypeCode) && !"L".equals(strPsptTypeCode) && !"M".equals(strPsptTypeCode)) {
					data.put("IMPORT_ERROR", "开户证件必须是集团证件才可变更");
					failds.add(data);
					continue;
				}
			}
            
            //客户名称
            String name = data.getString("DATA1");
            if (name == null || "".equals(name)) {
                data.put("IMPORT_ERROR", "客户名称不能为空");
                failds.add(data);
                continue;
            }          

            if (BatDataImportBean.isPersonCertificate(pspt_type_code)) {//单位证件 返回 false， 个人证件返回true                    
                data.put("IMPORT_ERROR", "证件类型必须是单位证件");
                failds.add(data);
                continue;
            }

            //证件号码
            String pspt_id = data.getString("DATA3", "").trim();
            if (pspt_id == null || "".equals(pspt_id)) {
                data.put("IMPORT_ERROR", "证件号码不能为空");
                failds.add(data);
                continue;
            } else {
				Map<String, String> checkfiledmap = new HashMap<String, String>();

                if (pspt_type_code.equals("E")) {//营业执照
                    if (pspt_id.length() != 13 && pspt_id.length() != 15 && pspt_id.length() != 18 && pspt_id.length() != 20 && pspt_id.length() != 22 && pspt_id.length() != 24) {
                        data.put("IMPORT_ERROR", "营业执照长度需满足13位、15位、18位、20位、22位或24位。");
                        failds.add(data);
                        continue;
                    }
                } else if (pspt_type_code.equals("M")) {
                    if (pspt_id.length() != 10 && pspt_id.length() != 18) {
                        data.put("IMPORT_ERROR", "组织机构代码证长度需满足10位或18位。");
                        failds.add(data);
                        continue;
                    }
                    if (pspt_id.length() == 10 && !pspt_id.substring(8, 9).equals("-")) {
                        data.put("IMPORT_ERROR", "组织机构代码证规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
                        failds.add(data);
                        continue;
                    }
                } else if (pspt_type_code.equals("G")) { //事业单位法人登记证书：证件号码长度需满足12位
                    if (pspt_id.length() != 12 && pspt_id.length() != 18) {
                        data.put("IMPORT_ERROR", "事业单位法人登记证书长度需满足12位或者18位。");
                        failds.add(data);
                        continue;
                    }
                }
            	//DATA13使用人名称，DATA14使用人证件类型，DATA15使用人证件号码，DATA16使用人证件地址    	
            
                checkfiledmap.clear();

				checkfiledmap.put("DATA13", d13);
				checkfiledmap.put("DATA14", d14);
				checkfiledmap.put("DATA15", d15);
				checkfiledmap.put("DATA16", d16);  
				     
                                    
                String checkinfo =   checkfieldForModifyTdPspt(checkfiledmap).trim();
                if(!"".equals(checkinfo)){
                	  data.put("IMPORT_ERROR", checkinfo);
                      failds.add(data);
                      continue;
                }
                
                                
            }
            String pspt_addr = data.getString("DATA4");
            if (pspt_addr == null || "".equals(pspt_addr)) {
                data.put("IMPORT_ERROR", "证件地址不能为空");
                failds.add(data);
                continue;
            } else {
                String pspt_addr_max = BatDataImportBean.maxLen("证件地址", pspt_addr, 80);
                if (!"0000".equals(pspt_addr_max)) {
                    data.put("IMPORT_ERROR", pspt_addr_max);
                    failds.add(data);
                    continue;
                }
            }

            //验证本地导入的手机号码是否存在重复
            String check_serial_number = BatDataImportBean.checkSerialNumberCount(serial_number, serialNumberList);
            if(!"0000".equals(check_serial_number)){
                data.put("IMPORT_ERROR", check_serial_number);
                failds.add(data);
                continue;
           }
            
            //应吴清茂要求，该批量类型取消一证多名验证
/*            //一证多名，不允许办理
            IData input = new DataMap();
            input.put("CERT_ID", pspt_id);
            input.put("CERT_NAME", name);
            input.put("CERT_TYPE", pspt_type_code);
            IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCardName", input);
            if (!ds.isEmpty()) {
                String reCode = ds.getData(0).getString("X_RESULTCODE");
                if (!reCode.equals("0")) {
                    data.put("IMPORT_ERROR", "同一个证件号码不能对应不同的名称");
                    failds.add(data);
                    continue;
                }
            } */           

            //调用在线公司接口，验证营业执照或组织机构代码
            IData cond = new DataMap();
            if(pspt_type_code.equals("E")){
                cond.put("regitNo", pspt_id);
                cond.put("enterpriseName", name);
                cond.put("legalperson",  data.getString("DATA5","").trim());
                cond.put("termstartdate",  data.getString("DATA6","").trim());
                cond.put("termenddate",  data.getString("DATA7","").trim());
                cond.put("startdate",  data.getString("DATA8","").trim());
                
                CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                IData result = bean.verifyEnterpriseCard(cond);
                if(!result.getString("X_RESULTCODE","").trim().equals("0")){                     
                    data.put("IMPORT_ERROR", result.getString("X_RESULTINFO",""));
                    failds.add(data);
                    continue;
                }
            }else if (pspt_type_code.equals("M")) {
                cond.put("orgCode", pspt_id);
                cond.put("orgName", name);
                cond.put("orgtype",  data.getString("DATA9","").trim());
                cond.put("effectiveDate",  data.getString("DATA11","").trim());
                cond.put("expirationDate",  data.getString("DATA12","").trim());
                                              
                CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
                IData result = bean.verifyOrgCard(cond);
                if(!result.getString("X_RESULTCODE","").trim().equals("0")){                     
                    data.put("IMPORT_ERROR", result.getString("X_RESULTINFO",""));
                    failds.add(data);
                    continue;
                }
            }                                     
            //*****************************************使用人********************************************            
		    //use=DATA13使用人名称，useObjValue=DATA14使用人证件类型，use_pspt_id=DATA15使用人证件号码，DATA16使用人证件地址

		       	//使用人名称
	        	String use=data.getString("DATA13");
	        	if("".equals(use)||use == null){
		              data.put("IMPORT_ERROR", "使用人名称不能为空");
		              failds.add(data);
		              continue;
	    	    }
	        	//使用人证件类型
	        	String use_pspt_type_code=data.getString("DATA14");
	        	
	        	if("".equals(use_pspt_type_code)||use_pspt_type_code == null){
		              data.put("IMPORT_ERROR", "使用人证件类型不能为空");
		              failds.add(data);
		              continue;
	  	        }else{
	  	            //类型转换
	  	        	String[] key=new String[3];
	  	        	         key[0]="TYPE_ID";
	  	        	         key[1]="SUBSYS_CODE";
	  	        	         key[2]="DATA_NAME";
	  	     	    String[] value=new String[3];
	  	     	    		 value[0]="TD_S_PASSPORTTYPE2";
	  	     	    		 value[1]="CUSTMGR";
	  	     	    		 value[2]=use_pspt_type_code.trim();
	  	     	    		 
	  	        	data.put("DATA14", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
	  	        }
	        	//使用人证件号码
	        	String use_pspt_id=data.getString("DATA15").trim();
	        	if("".equals(use_pspt_id)||use_pspt_id == null){
		              data.put("IMPORT_ERROR", "使用人证件号码不能为空");
		              failds.add(data);
		              continue;
	        	}
	        	//使用人证件地址
	        	/**
	        	 * 由于1到15中的长度小于500,跟数据库不一样 ,数据库中为500
	        	 */
	        	String use_pspt_addr=data.getString("DATA16");
	        	if("".equals(use_pspt_addr)||use_pspt_addr == null){
		              data.put("IMPORT_ERROR", "使用人证件地址不能为空");
		              failds.add(data);
		              continue;
	        	}else{
	        		String use_pspt_addr_max=BatDataImportBean.maxLen("使用人证件地址", use_pspt_addr,80);
	        		if(!"0000".equals(use_pspt_addr_max)){
	  	              data.put("IMPORT_ERROR", use_pspt_addr_max);
		              failds.add(data);
		              continue;
	        		}
	        	}  
            
            
                       
            
	        	String  useObjValue=data.getString("DATA14");
	      	     if(!BatDataImportBean.isPersonCertificate(useObjValue)){
		              data.put("IMPORT_ERROR", "使用人的证件类型需为个人证件");
		              failds.add(data);
		              continue;
	      	     }else{
	      	    	 //个人证件类型
	      	    	 
	      	    	//使用人姓名验证
	      	    	String checkUseName=BatDataImportBean.checkCustName("使用人姓名", use, useObjValue);
	      	    	if(!"0000".equals(checkUseName)){
		  	              data.put("IMPORT_ERROR", checkUseName);
			              failds.add(data);
			              continue;
	      	    	}
	      	    	
	      	    	//使用人地址验证
	      	    	String checkUseAddr=BatDataImportBean.checkAddr("使用人证件地址", use_pspt_addr);
	      	    	if(!"0000".equals(checkUseAddr)){
		  	              data.put("IMPORT_ERROR", checkUseAddr);
			              failds.add(data);
			              continue;
	      	    	}
	      	    	
	      	    	 //使用人证件号码
	            	if(isRepeatCode(use_pspt_id)){
	            		
	            		data.put("IMPORT_ERROR", "使用人证件号码不能全为同一个数字，请重新输入!");
						failds.add(data);
						continue;
	            	}else if(isSerialCode(use_pspt_id)){
	            		
	            		data.put("IMPORT_ERROR", "使用人证件号码不能连续数字，请重新输入!");
						failds.add(data);
						continue;
	            	}
	      	    	//电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码
	            	
	            	if(use_pspt_id.length() >=4
	            			 &&serial_number.length()>=use_pspt_id.length()
	            			 &&(serial_number.indexOf(use_pspt_id)==0
	            				 ||serial_number.lastIndexOf(use_pspt_id) == (serial_number.length()-use_pspt_id.length()))
	            	     ){
	              		data.put("IMPORT_ERROR", "电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码!");
						failds.add(data);
						continue;
					}
					// （本地身份证 0、外地身份证 1、户口本 2、护照 A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
					// 港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
					if ("H".equals(useObjValue)) {
						if (use_pspt_id.length() != 9 && use_pspt_id.length() != 11) {
							data.put("IMPORT_ERROR", "港澳居民回乡证校验：使用人证件号码必须为9位或11位！!");
							failds.add(data);
							continue;
						}
	            		if(!(use_pspt_id.charAt(0)=='H' || use_pspt_id.charAt(0)=='M') 
	            				|| !StringUtils.isNumeric(use_pspt_id.substring(1)) ){
	                  		data.put("IMPORT_ERROR", "港澳居民回乡证校验：使用人证件号码首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字！");
	    					failds.add(data);
	    					continue;
	            		}
	            	}else if("I".equals(useObjValue)){
						// 台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
						if (use_pspt_id.length() != 8 && use_pspt_id.length() != 11) {
							data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码必须为8位或11位！");
							failds.add(data);
							continue;
						}
						if (use_pspt_id.length() == 8) {
							if (!StringUtils.isNumeric(use_pspt_id)) {
								data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码为8位时，必须均为阿拉伯数字！");
								failds.add(data);
								continue;
							}
						}
						if (use_pspt_id.length() == 11) {
							if (!StringUtils.isNumeric(use_pspt_id.substring(0, 10))) {
								data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码为11位时，前10位必须均为阿拉伯数字。");
								failds.add(data);
								continue;
							}
						}
					} else if ("C".equals(useObjValue) || "A".equals(useObjValue)) {
						 
		                if (use_pspt_id.trim().indexOf(" ")!=-1) {
							String tmpName = useObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
							data.put("IMPORT_ERROR", tmpName + "证件号码中间不能有空格。");
							failds.add(data);
							continue;
						}
						// 军官证、警官证、护照：证件号码须大于等于6位字符
						if (use_pspt_id.length() < 6) {
							String tmpName = useObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
							data.put("IMPORT_ERROR", tmpName + "使用人证件号码须大于等于6位字符！");
							failds.add(data);
							continue;
						}
	            	}else if("0".equals(useObjValue)|| 
	            				"1".equals(useObjValue) || 
	            				"2".equals(useObjValue)) {
	            	 
	            		
	            		//身份证相关检查 
	                    if (use_pspt_id.length() == 15 || use_pspt_id.length() == 18)
	                    {
	                        if (StringUtils.isNotBlank(checkPspt(use_pspt_id, "", useObjValue)))
	                        {
	                           // 证件号码不正确
	                        	data.put("IMPORT_ERROR", "使用人证件号码格式不对");
		     					failds.add(data);
		     					continue;
	                        }
	                    }else{
	                    	    data.put("IMPORT_ERROR", "使用人证件号码位数不对");
		     					failds.add(data);
		     					continue;
	                    }                    
	                    
	                    String temppsptid = use_pspt_id;
	                    if(use_pspt_id.length()==15){
	                    	temppsptid = IdcardUtils.conver15CardTo18(temppsptid);
	                    }                    
						int age = IdcardUtils.getExactAgeByIdCard(temppsptid);
						 if (age < 16 || age > 120) {
						 	 data.put("IMPORT_ERROR", "年龄范围必须在16-120岁之间");
							 failds.add(data);
							 continue;
						 }
						 /*if (age > 120) {
						 	 data.put("IMPORT_ERROR", "年龄范围必须在120岁之内");
							 failds.add(data);
							 continue;
						 }*/
						
						IData input = new DataMap();
						input.put("CERT_ID", use_pspt_id);
						input.put("CERT_NAME", use);
						
						
	                    boolean iscallcheck = true;
	     				if (idcardlist != null && idcardlist.size() > 0) {
	     					for (int j = 0; j < idcardlist.size(); j++) {
	     						boolean issame = compareIData(idcardlist.get(j),input);
	     						if(issame){
	     							iscallcheck = false;
	     							break;
	     						}
	     					}
	     				} 
	     				
	     				if (iscallcheck) {
	     					log.error("BatDataImportSVCxxxxxxxxxxxxxxxxx6046 SS.CreatePersonUserSVC.verifyIdCard "+input);
							IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);
							if (!ds.isEmpty()) {
								String reCode = ds.getData(0).getString("X_RESULTCODE");
								if (reCode.equals("1")) {// 证件信息不合法
									data.put("IMPORT_ERROR", "使用人证件信息不合法");
									failds.add(data);
									continue;
								}
							}		                     
	     	                idcardlist.add(input);
	     				}
						

						
					} else if ("N".equals(useObjValue)) {// 台湾居民来往大陆通行证
						String psptname = "台湾居民来往大陆通行证校验";
						String desc = "证件号码";
						String psptIdtemp = replacespace(use_pspt_id);
						if (!psptIdtemp.equals(use_pspt_id)) {
							data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
							failds.add(data);
							continue;
						}
						
						int psptlength = use_pspt_id.length();
						
						if (psptlength < 4) {
							data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
							failds.add(data);
							continue;
						}
						
						if (!use_pspt_id.substring(0, 2).equals("TW") && !use_pspt_id.substring(0, 4).equals("LXZH")) {
							if (psptlength == 11 || psptlength == 12) {
								if (!StringUtils.isNumeric(use_pspt_id.substring(0, 10))) {
									data.put("IMPORT_ERROR", psptname + "：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
									failds.add(data);
									continue;
								}
							} else if (psptlength == 8 || psptlength == 7) {
								if (!StringUtils.isNumeric(use_pspt_id)) {
									data.put("IMPORT_ERROR", psptname + "：" + desc + "为" + psptlength + "位时，必须均为阿拉伯数字。");
									failds.add(data);
									continue;
								}
							} else {
								if (!StringUtils.isNumeric(use_pspt_id)) {
									data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
									failds.add(data);
									continue;
								}
							}
						}else {						
						    String psptIdsub=null; 
						    String psptIdsub2=null; 
						    if(use_pspt_id.substring(0, 2).equals("TW")){
						    	psptIdsub="TW";
						    	psptIdsub2=use_pspt_id.substring(2);
						    }else if(use_pspt_id.substring(0, 4).equals("LXZH")){
						    	psptIdsub="LXZH";
						    	psptIdsub2=use_pspt_id.substring(4);
						    }
						    if(psptIdsub!=null){
						    	
						    	PsptUtils  psptus = new PsptUtils();
						    	if(!psptus.isHaveUpperCase(psptIdsub2)||!psptus.isHaveNumeric(psptIdsub2)
						    			||psptIdsub2.indexOf("(")==-1||psptIdsub2.indexOf(")")==-1){//有大写					    		 			       			   	
									data.put("IMPORT_ERROR", psptname + "：" + desc + "前2位为“TW”或 “LXZH”字符时，后面需是阿拉伯数字、英文大写字母与半角“()”的组合");
									failds.add(data);
									continue;
						    	}
						    }						
						}																			
					} else if ("O".equals(useObjValue)) {
						String psptname = "港澳居民来往内地通行证校验";
						String desc = "证件号码";					
						
						String psptIdtemp = replacespace(use_pspt_id);
						if (!psptIdtemp.equals(use_pspt_id)) {
							data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
							failds.add(data);
							continue;
						}
						
						int psptlength = use_pspt_id.length();			 
		                //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
		                if (psptlength != 9 && psptlength!= 11) {	                    
							data.put("IMPORT_ERROR",  psptname + "：" + desc + "必须为9位或11位。");
							failds.add(data);
							continue;	                    
		                }
		                if (!(use_pspt_id.substring(0,1).equals("H") || use_pspt_id.substring(0,1).equals("M")) || !StringUtils.isNumeric(use_pspt_id.substring(1))) {
		                	data.put("IMPORT_ERROR",  psptname + "：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
							failds.add(data);
							continue;
		                }	            					
					}				
					
				}
	      	     
		         //一证多名验证
		         IData inputInfo = new DataMap();
		         inputInfo.put("CERT_ID",use_pspt_id); 
		         inputInfo.put("CERT_NAME",use);
		         inputInfo.put("CERT_TYPE",useObjValue); 
		         inputInfo.put("SERIAL_NUMBER",serial_number); 
		         IDataset datasetResult = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCardName", inputInfo);
		         IData resultdata = datasetResult.getData(0);
	             if(IDataUtil.isNotEmpty(resultdata) && resultdata.get("X_RESULTCODE")!= "0"){
	 				data.put("IMPORT_ERROR", "同一个证件号码不能对应不同的名称");
	                failds.add(data);
	                continue;
	 			 }
	             
		         /*
		          * 全网一证5号校验
		          */
		         inputInfo = new DataMap();
		         inputInfo.put("CUST_NAME",use); 
		         inputInfo.put("PSPT_ID",use_pspt_id);
		         inputInfo.put("PSPT_TYPE_CODE",useObjValue); 
		         inputInfo.put("SERIAL_NUMBER",serial_number); 
	             datasetResult = CSAppCall.call("SS.CreatePersonUserSVC.checkGlobalMorePsptId", inputInfo);
	             resultdata = datasetResult.getData(0);
	             if(IDataUtil.isNotEmpty(resultdata) && resultdata.get("CODE")!= "0"){
	 				data.put("IMPORT_ERROR", resultdata.get("MSG"));
	                failds.add(data);
	                continue;
	 			 }
		             
            succds.add(data);
        }
        
        //特殊处理
        if (failds != null && failds.size() > 0) {
            for (int k = 0; k < failds.size(); k++) {
                if (k == 0) {
                    IData error = new DataMap();
                    error = failds.getData(0);
                    error.put("WADE_TRANSFORM_ERROR_DATA", true);
                    failds.remove(0);
                    failds.add(0, error);
                }
                //将证件类型转换为中文名
                failds.getData(k).put("DATA2", BatDataImportBean.getTypeNameByTypeCode(failds.getData(k).getString("DATA2")));
                failds.getData(k).put("DATA14", BatDataImportBean.getTypeNameByTypeCode(failds.getData(k).getString("DATA17")));
            }
        }

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    
    /*
     * REQ201901280015申请开发非实名无线固话批量登记实名
     * liquan
     */
    
    public IData fileImportCheckModifyTdPspt(IData pdData, IDataset dataset) throws Exception
    {
    	//DATA1=客户名称,DATA2=证件类型,DATA3=证件号码,DATA4=证件地址,
    	//DATA5=法人,DATA6=营业开始日期,DATA7=营业结束日期,DATA8=成立日期,
    	//DATA9=机构类型,DATA110=有效日期,DATA11=失效日期,
    	//DATA12经办人名称，DATA13经办人证件类型，DATA14经办人证件号码，DATA15经办人证件地址，
    	//DATA16使用人名称，DATA17使用人证件类型，DATA18使用人证件号码，DATA19使用人证件地址    	
    	//DATA20联系电话
    	
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();        
        IDataset serialNumberList = new DatasetList();
       //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxx5061 "+pdData);
       //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxx5062 "+dataset);
        if (IDataUtil.isEmpty(dataset)) {
            IData error = new DataMap();
            error.put("IMPORT_ERROR", "内容为空");
            failds.add(error);
            return null;
        }
        
        //避免重复校验
        List<IData> enterpriselist = new ArrayList<IData>();//已经过校验的营业执照
        List<IData> orglist = new ArrayList<IData>();//已经过校验的组织机构代码证
        List<IData> idcardlist = new ArrayList<IData>();//已经过校验的身份证
        
        
        for (int i = 0; i < dataset.size(); i++) {
        	
        	
            IData data = dataset.getData(i);
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult) {
                failds.add(data);
                continue;
            }
            if (data.getString("SERIAL_NUMBER","").trim().equals("手机号码")) {
                continue;
            }
            //获取所有字段，校验不能为空
        	//DATA1=客户名称,DATA2=证件类型,DATA3=证件号码,DATA4=证件地址,
        	//DATA5=法人,DATA6=营业开始日期,DATA7=营业结束日期,DATA8=成立日期,
        	//DATA9=机构类型,DATA110=有效日期,DATA11=失效日期,
        	//DATA12经办人名称，DATA13经办人证件类型，DATA14经办人证件号码，DATA15经办人证件地址，
        	//DATA16使用人名称，DATA17使用人证件类型，DATA18使用人证件号码，DATA19使用人证件地址    	
        	//DATA20联系电话
            

            String pspt_type_code_name = data.getString("DATA2");                 

            String d5 = data.getString("DATA5","").trim();   
            String d6 = data.getString("DATA6","").trim();
            String d7 = data.getString("DATA7","").trim();
            String d8 = data.getString("DATA8","").trim();
            
            String d9 = data.getString("DATA9","").trim();
            String d10 = data.getString("DATA10","").trim();
            String d11 = data.getString("DATA11","").trim();
            
            String d12 = data.getString("DATA12","").trim();
            String d13 = data.getString("DATA13","").trim();
            String d14 = data.getString("DATA14","").trim();
            String d15 = data.getString("DATA15","").trim();
            
            String d16 = data.getString("DATA16","").trim();
            String d17 = data.getString("DATA17","").trim();
            String d18 = data.getString("DATA18","").trim();
            String d19 = data.getString("DATA19","").trim();
            String d20 = data.getString("DATA20","").trim();

           //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxxxxx5125 "+d6);
           //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxxxxx5126 "+d20);
           //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxxxxx5127 "+d7);
           //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxxxxx5128 "+d8);
          //证件类型
            String pspt_type_code = "";
            if (pspt_type_code_name == null || "".equals(pspt_type_code_name)) {
                data.put("IMPORT_ERROR", "证件类型不能为空");
                failds.add(data);
                continue;
            } else {
                //类型转换
                String[] key = new String[3];
                key[0] = "TYPE_ID";
                key[1] = "SUBSYS_CODE";
                key[2] = "DATA_NAME";
                String[] value = new String[3];
                value[0] = "TD_S_PASSPORTTYPE2";
                value[1] = "CUSTMGR";
                value[2] = pspt_type_code_name.trim();
                data.put("DATA2", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
                pspt_type_code = data.getString("DATA2", "").trim();
            }
            
            //手机号码
            String serial_number = data.getString("SERIAL_NUMBER");
            
            if (serial_number == null || "".equals(serial_number)) {
                data.put("IMPORT_ERROR", "手机号码不能为空!");
                failds.add(data);
                continue;
            } else {
                serial_number = serial_number.trim();
            
            }
            
            //查询手机号码是否有对应的客户信息，开户证件是否是集团证件
            IData ups = new DataMap();
            try {
            UcaData uca = UcaDataFactory.getNormalUca(serial_number);
            ups.put("USER_ID", uca.getUserId());
            ups.put("CUST_ID", uca.getCustId());
            ups.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));   
            } catch (Exception e) {
                data.put("IMPORT_ERROR", e.getMessage());
                failds.add(data);
                continue;
            }
            
            IDataset custInfos = new DatasetList();
            try {
                custInfos = CSAppCall.call("SS.ModifyCustInfoSVC.getCustInfo", ups);
            } catch (Exception e) {
                data.put("IMPORT_ERROR", e);
                failds.add(data);
                continue;
            }
            
            IData params = new DataMap(custInfos.getData(0).getData("CUST_INFO"));
            String strPsptTypeCode = params.getString("PSPT_TYPE_CODE","").trim();            
            UcaData uca = UcaDataFactory.getNormalUca(serial_number);
            //System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxx4776 " + params);
			//System.out.println("BatDataImportSVCxxxxxxxxxxxxxxxxx4777 " + uca.getBrandCode());
            //查询号码是否无线固话号码
			if (!uca.getBrandCode().equals("TDYD")) {
				data.put("IMPORT_ERROR", "该号码不是无线固话号码，不能在此入口办理证件变更！");
				failds.add(data);
				continue;
			} /*else {
				if (!"D".equals(strPsptTypeCode) && !"E".equals(strPsptTypeCode) && !"G".equals(strPsptTypeCode) && !"L".equals(strPsptTypeCode) && !"M".equals(strPsptTypeCode)) {
					data.put("IMPORT_ERROR", "开户证件必须是集团证件才可变更");
					failds.add(data);
					continue;
				}
			}*/
            
            //客户名称
            String name = data.getString("DATA1");
            if (name == null || "".equals(name)) {
                data.put("IMPORT_ERROR", "客户名称不能为空");
                failds.add(data);
                continue;
            }
            
            if (BatDataImportBean.isPersonCertificate(pspt_type_code)) {//单位证件 返回 false， 个人证件返回true                    
                data.put("IMPORT_ERROR", "证件类型必须是单位证件");
                failds.add(data);
                continue;
            }

            //证件号码
            String pspt_id = data.getString("DATA3", "").trim();
            if (pspt_id == null || "".equals(pspt_id)) {
                data.put("IMPORT_ERROR", "证件号码不能为空");
                failds.add(data);
                continue;
            } else {
				Map<String, String> checkfiledmap = new HashMap<String, String>();

                if (pspt_type_code.equals("E")) {//营业执照
                    if (pspt_id.length() != 13 && pspt_id.length() != 15 && pspt_id.length() != 18 && pspt_id.length() != 20 && pspt_id.length() != 22 && pspt_id.length() != 24) {
                        data.put("IMPORT_ERROR", "营业执照长度需满足13位、15位、18位、20位、22位或24位。");
                        failds.add(data);
                        continue;
                    }
                    
                    //如下字段，不能为空  
                    //DATA5=法人,DATA6=营业开始日期,DATA7=营业结束日期,DATA8=成立日期, 
                    checkfiledmap.clear();
					checkfiledmap.put("DATA5", d5);
					checkfiledmap.put("DATA6", d6);
					checkfiledmap.put("DATA7", d7);
					checkfiledmap.put("DATA8", d8);             
                                        
                    String checkinfo =   checkfieldForModifyTdPspt(checkfiledmap).trim();
                    if(!"".equals(checkinfo)){
                    	  data.put("IMPORT_ERROR", checkinfo);
                          failds.add(data);
                          continue;
                    }
                                        
                } else if (pspt_type_code.equals("M")) {
                    if (pspt_id.length() != 10 && pspt_id.length() != 18) {
                        data.put("IMPORT_ERROR", "组织机构代码证长度需满足10位或18位。");
                        failds.add(data);
                        continue;
                    }
                    if (pspt_id.length() == 10 && !pspt_id.substring(8, 9).equals("-")) {
                        data.put("IMPORT_ERROR", "组织机构代码证规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
                        failds.add(data);
                        continue;
                    }
                    
                  //如下字段，不能为空  
                
                	//DATA9=机构类型,DATA110=有效日期,DATA11=失效日期,
                           
                    checkfiledmap.clear();
                    checkfiledmap.put("DATA9", d9);
					checkfiledmap.put("DATA10", d10);
					checkfiledmap.put("DATA11", d11);										
                    String checkinfo =   checkfieldForModifyTdPspt(checkfiledmap).trim();
                    if(!"".equals(checkinfo)){
                    	  data.put("IMPORT_ERROR", checkinfo);
                          failds.add(data);
                          continue;
                    }
                    
                    
                } else if (pspt_type_code.equals("G")) { //事业单位法人登记证书：证件号码长度需满足12位
                    if (pspt_id.length() != 12 && pspt_id.length() != 18) {
                        data.put("IMPORT_ERROR", "事业单位法人登记证书长度需满足12位或者18位。");
                        failds.add(data);
                        continue;
                    }
                }
                
            
            	//DATA12经办人名称，DATA13经办人证件类型，DATA14经办人证件号码，DATA15经办人证件地址，
            	//DATA16使用人名称，DATA17使用人证件类型，DATA18使用人证件号码，DATA19使用人证件地址    	
            
                checkfiledmap.clear();
                checkfiledmap.put("DATA12", d12);
				checkfiledmap.put("DATA13", d13);
				checkfiledmap.put("DATA14", d14);
				checkfiledmap.put("DATA15", d15);

				checkfiledmap.put("DATA16", d16);
				checkfiledmap.put("DATA17", d17);
				checkfiledmap.put("DATA18", d18);
				checkfiledmap.put("DATA19", d19);  
				checkfiledmap.put("DATA20", d20);     
				     
                                    
                String checkinfo =   checkfieldForModifyTdPspt(checkfiledmap).trim();
                if(!"".equals(checkinfo)){
                	  data.put("IMPORT_ERROR", checkinfo);
                      failds.add(data);
                      continue;
                }
                
                
                                
            }
            
            String pspt_addr = data.getString("DATA4");
            if (pspt_addr == null || "".equals(pspt_addr)) {
                data.put("IMPORT_ERROR", "证件地址不能为空");
                failds.add(data);
                continue;
            } else {
                String pspt_addr_max = BatDataImportBean.maxLen("证件地址", pspt_addr, 80);
                if (!"0000".equals(pspt_addr_max)) {
                    data.put("IMPORT_ERROR", pspt_addr_max);
                    failds.add(data);
                    continue;
                }
            }

            //验证本地导入的手机号码是否存在重复
            String check_serial_number = BatDataImportBean.checkSerialNumberCount(serial_number, serialNumberList);
            if(!"0000".equals(check_serial_number)){
                data.put("IMPORT_ERROR", check_serial_number);
                failds.add(data);
                continue;
           }
         
            //调用在线公司接口，验证营业执照或组织机构代码
            IData cond = new DataMap();
            if(pspt_type_code.equals("E")){
                cond.put("regitNo", pspt_id);
                cond.put("enterpriseName", name);
                cond.put("legalperson",  data.getString("DATA5","").trim());
                cond.put("termstartdate",  data.getString("DATA6","").trim());
                cond.put("termenddate",  data.getString("DATA7","").trim());
                cond.put("startdate",  data.getString("DATA8","").trim());
                
                boolean iscallcheck = true;
				if (enterpriselist != null && enterpriselist.size() > 0) {
					for (int j = 0; j < enterpriselist.size(); j++) {
						boolean issame = compareIData(enterpriselist.get(j),cond);
						if(issame){
							iscallcheck = false;
							break;
						}
					}
				} 
				
				if (iscallcheck) {
 					 log.error("BatDataImportSVCxxxxxxxxxxxxxxxxx5462 SS.CreatePersonUserSVC.verifyEnterpriseCard "+cond);

					CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
					IData result = bean.verifyEnterpriseCard(cond);
					if (!result.getString("X_RESULTCODE", "").trim().equals("0")) {
						data.put("IMPORT_ERROR", result.getString("X_RESULTINFO", ""));
						failds.add(data);
						continue;
					}
					enterpriselist.add(cond);
				}
                                
            }else if (pspt_type_code.equals("M")) {
                cond.put("orgCode", pspt_id);
                cond.put("orgName", name);
                cond.put("orgtype",  data.getString("DATA9","").trim());
                cond.put("effectiveDate",  data.getString("DATA10","").trim());
                cond.put("expirationDate",  data.getString("DATA11","").trim());
                                            
                
                boolean iscallcheck = true;
				if (orglist != null && orglist.size() > 0) {
					for (int j = 0; j < orglist.size(); j++) {
						boolean issame = compareIData(orglist.get(j),cond);
						if(issame){
							iscallcheck = false;
							break;
						}
					}
				} 
				
				if (iscallcheck) {
  					 log.error("BatDataImportSVCxxxxxxxxxxxxxxxxx5492 SS.CreatePersonUserSVC.verifyOrgCard "+cond);

	                CreatePersonUserBean bean = (CreatePersonUserBean) BeanManager.createBean(CreatePersonUserBean.class);
	                IData result = bean.verifyOrgCard(cond);
	                if(!result.getString("X_RESULTCODE","").trim().equals("0")){                     
	                    data.put("IMPORT_ERROR", result.getString("X_RESULTINFO",""));
	                    failds.add(data);
	                    continue;
	                }
	                orglist.add(cond);
				}               
                
            }
            
            
            
            
            //*****************************************经办人********************************************

     	     /**************************经办人**************************************/
		      	   /**
		       	     * 使用人及经办人的证件类型需为个人证件，证件及证件号码按照目前个人证件及号码的校验规则进行校验。 
		       	     * （本地身份证 0、外地身份证 1、户口本 2、护照  A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
		       	     */
		       	     //个人证件为 useObjValue=="D" || useObjValue=="E" || useObjValue=="G" || useObjValue=="L" || useObjValue=="M"
		       	     //经办人证件类型 agent_pspt_type_code
            //DATA12经办人名称，DATA13经办人证件类型，DATA14经办人证件号码，DATA15经办人证件地址，
        	//DATA16使用人名称，DATA17使用人证件类型，DATA18使用人证件号码，DATA19使用人证件地址    
          //经办人证件类型
            String  agentPsptType=data.getString("DATA13");
         	if("".equals(agentPsptType)||agentPsptType == null){
 	              data.put("IMPORT_ERROR", "经办人证件类型不能为空");
 	              failds.add(data);
 	              continue;
     	     }else{
     	    	 //类型转换
 				String[] key = new String[3];
 					key[0] = "TYPE_ID";
 					key[1] = "SUBSYS_CODE";
 					key[2] = "DATA_NAME";
 				String[] value = new String[3];
 					value[0] = "TD_S_PASSPORTTYPE2";
 					value[1] = "CUSTMGR";
 					value[2] = agentPsptType.trim();
   	    		 
   	    		 data.put("DATA13", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
     	     }     
            
         	agentPsptType=data.getString("DATA13");
         	
		       	     if(!BatDataImportBean.isPersonCertificate(agentPsptType)){
		 	              data.put("IMPORT_ERROR", "经办人的证件类型需为个人证件");
		 	              failds.add(data);
		 	              continue;
		       	     }else{
		       	    //经办人名称
		             	String agent_cust_name=data.getString("DATA12");
		             	if("".equals(agent_cust_name)||agent_cust_name == null){
		     	              data.put("IMPORT_ERROR", "经办人名称不能为空");
		     	              failds.add(data);
		     	              continue;
		           	     }        	
		             	  	
		             	//经办人证件号码
		             	String agent_pspt_id=data.getString("DATA14").trim();
		             	if("".equals(agent_pspt_id)||agent_pspt_id == null){
		     	              data.put("IMPORT_ERROR", "经办人证件号码不能为空");
		     	              failds.add(data);
		     	              continue;
		       	        }           	
		             	//经办人证件地址
		             	/**
		             	 * 由于1到15中的长度小于500,跟数据库不一样 ,数据库中为500
		             	 */
		             	String agent_pspt_addr=data.getString("DATA15");
		             	if("".equals(agent_pspt_addr)||agent_pspt_addr == null){
		     	              data.put("IMPORT_ERROR", "经办人证件地址不能为空");
		     	              failds.add(data);
		     	              continue;
		     	        }else{
		             		String agent_pspt_addr_max=BatDataImportBean.maxLen("经办人证件地址", agent_pspt_addr,200);
		             		if(!"0000".equals(agent_pspt_addr_max)){
		       	              data.put("IMPORT_ERROR", agent_pspt_addr_max);
		     	              failds.add(data);
		     	              continue;
		             		}
		     	        }
		       	    	 
		       	    	 //个人证件类型
		       	    	 
		       	    	//经办人姓名验证
		       	    	String checkAgentName=BatDataImportBean.checkCustName("经办人姓名", agent_cust_name, agentPsptType);
		       	    	if(!"0000".equals(checkAgentName)){
		 	  	              data.put("IMPORT_ERROR", checkAgentName);
		 		              failds.add(data);
		 		              continue;
		       	    	}
		       	    	
		       	    	//经办人地址验证
		       	    	String checkAgentAddr=BatDataImportBean.checkAddr("经办人证件地址", agent_pspt_addr);
		       	    	if(!"0000".equals(checkAgentAddr)){
		 	  	              data.put("IMPORT_ERROR", checkAgentAddr);
		 		              failds.add(data);
		 		              continue;
		       	    	}
		       	    	 
		       	    	 //经办人证件号码
		             	if(isRepeatCode(agent_pspt_id)){
		             		
		             		data.put("IMPORT_ERROR", "经办人证件号码不能全为同一个数字，请重新输入!");
		 					failds.add(data);
		 					continue;
		             	}else if(isSerialCode(agent_pspt_id)){
		             		
		             		data.put("IMPORT_ERROR", "经办人证件号码不能连续数字，请重新输入!");
		 					failds.add(data);
		 					continue;
		             	}
		       	    	//电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为经办人证件号码
		             	
		             	if(agent_pspt_id.length() >=4
		             			 &&serial_number.length()>=agent_pspt_id.length()
		             			 &&(serial_number.indexOf(agent_pspt_id)==0
		             				 ||serial_number.lastIndexOf(agent_pspt_id) == (serial_number.length()-agent_pspt_id.length()))
		             	     ){
		               		data.put("IMPORT_ERROR", "电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码!");
		 					failds.add(data);
		 					continue;
		             	}
		             	//（本地身份证 0、外地身份证 1、户口本 2、护照  A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
		             	//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
		             	if("H".equals(agentPsptType)){
		             		if(agent_pspt_id.length()!=9 && agent_pspt_id.length()!=11){
		                   		data.put("IMPORT_ERROR", "港澳居民回乡证校验：经办人证件号码必须为9位或11位！!");
		     					failds.add(data);
		     					continue;
		             		}
		             		if(!(agent_pspt_id.charAt(0)=='H' || agent_pspt_id.charAt(0)=='M')
		             				|| !StringUtils.isNumeric(agent_pspt_id.substring(1)) ){
		                   		data.put("IMPORT_ERROR", "港澳居民回乡证校验：经办人证件号码首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字！");
		     					failds.add(data);
		     					continue;
		             		}
		             	}else if("I".equals(agentPsptType)){
		             		//台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
		             		if(agent_pspt_id.length()!=8 && agent_pspt_id.length()!=11){
		                   		data.put("IMPORT_ERROR", "台湾居民回乡校验：经办人证件号码必须为8位或11位！");
		     					failds.add(data);
		     					continue;
		             		}
		             		if(agent_pspt_id.length()==8){
		             			if(!StringUtils.isNumeric(agent_pspt_id)){
		                      		data.put("IMPORT_ERROR", "台湾居民回乡校验：经办人证件号码为8位时，必须均为阿拉伯数字！");
		         					failds.add(data);
		         					continue;
		             			}
		             		}
		             		if(agent_pspt_id.length()==11){
		             			if(!StringUtils.isNumeric(agent_pspt_id.substring(0,10))){
		                     		data.put("IMPORT_ERROR", "台湾居民回乡校验：经办人证件号码为11位时，前10位必须均为阿拉伯数字。");
		         					failds.add(data);
		         					continue;
		             			}
		             		}
		             	}else if("C".equals(agentPsptType) || "A".equals(agentPsptType)){
		             		  if (agent_pspt_id.trim().indexOf(" ")!=-1) {
		  						String tmpName = agentPsptType.equals("A") ? "护照校验：" : "军官证类型校验：";
		  						data.put("IMPORT_ERROR", tmpName + "证件号码中间不能有空格。");
		  						failds.add(data);
		  						continue;
		  					}
		             		//军官证、警官证、护照：证件号码须大于等于6位字符
		             		if(agent_pspt_id.length() < 6){
		             			String tmpName= agentPsptType.equals("A") ? "护照校验：" : "军官证类型校验：";
		                 		data.put("IMPORT_ERROR", tmpName+"经办人证件号码须大于等于6位字符！");
		     					failds.add(data);
		     					continue;
		             		}
		             	}else if("0".equals(agentPsptType)|| 
	            				"1".equals(agentPsptType) || 
	            				"2".equals(agentPsptType)) {
		             		//身份证相关检查 
		                     if (agent_pspt_id.length() == 15 || agent_pspt_id.length() == 18)
		                     {
		                         if (StringUtils.isNotBlank(checkPspt(agent_pspt_id, "", agentPsptType)))
		                         {
	                                // 证件号码不正确
			                 		data.put("IMPORT_ERROR", "经办人证件号码格式不对");
			     					failds.add(data);
			     					continue;
		                         }
		                     }else{
			                 		data.put("IMPORT_ERROR", "经办人证件号码位数不对");
			     					failds.add(data);
			     					continue;
		                     }
		                     
		                     String temppsptid = agent_pspt_id;
		                     if(agent_pspt_id.length()==15){
		                     	temppsptid = IdcardUtils.conver15CardTo18(temppsptid);

		                     }
		 					 int age = IdcardUtils.getExactAgeByIdCard(temppsptid);
		 					 if (age < 16 || age > 120) {
		 					 	 data.put("IMPORT_ERROR", "年龄范围必须在16-120岁之间");
		 						 failds.add(data);
		 						 continue;
		 					 }
		 					 /*if (age > 120) {
		 					 	 data.put("IMPORT_ERROR", "年龄范围必须在120岁之内");
		 						 failds.add(data);
		 						 continue;
		 					 }*/
		                     
		                     IData input = new DataMap();
		                     input.put("CERT_ID",agent_pspt_id); 
		                     input.put("CERT_NAME",agent_cust_name);
		                     
		                    boolean iscallcheck = true;
		     				if (idcardlist != null && idcardlist.size() > 0) {
		     					for (int j = 0; j < idcardlist.size(); j++) {
		     						boolean issame = compareIData(idcardlist.get(j),input);
		     						if(issame){
		     							iscallcheck = false;
		     							break;
		     						}
		     					}
		     				} 
		     				
		     				if (iscallcheck) {
 	 	     					 log.error("BatDataImportSVCxxxxxxxxxxxxxxxxx5720 SS.CreatePersonUserSVC.verifyIdCard "+input);

			                     IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);			                  
			                     if(!ds.isEmpty()){
			                         String reCode =  ds.getData(0).getString("X_RESULTCODE");
			                         if(reCode.equals("1")){//证件信息不合法
		                                 data.put("IMPORT_ERROR", "经办人证件信息不合法");
		                                 failds.add(data);
		                                 continue;
			                         }
			                     }			                     
		     	                idcardlist.add(input);
		     				}
		                   
		                     
		             	}else if ("N".equals(agentPsptType)) {// 台湾居民来往大陆通行证
							String psptname = "台湾居民来往大陆通行证校验";
							String desc = "证件号码";
							String psptIdtemp = replacespace(agent_pspt_id);
							if (!psptIdtemp.equals(agent_pspt_id)) {
								data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
								failds.add(data);
								continue;
							}
							
							int psptlength = agent_pspt_id.length();
							
							if (psptlength < 4) {
								data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
								failds.add(data);
								continue;
							}
							
							if (!agent_pspt_id.substring(0, 2).equals("TW") && !agent_pspt_id.substring(0, 4).equals("LXZH")) {
								if (psptlength == 11 || psptlength == 12) {
									if (!StringUtils.isNumeric(agent_pspt_id.substring(0, 10))) {
										data.put("IMPORT_ERROR", psptname + "：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
										failds.add(data);
										continue;
									}
								} else if (psptlength == 8 || psptlength == 7) {
									if (!StringUtils.isNumeric(agent_pspt_id)) {
										data.put("IMPORT_ERROR", psptname + "：" + desc + "为" + psptlength + "位时，必须均为阿拉伯数字。");
										failds.add(data);
										continue;
									}
								} else {
									if (!StringUtils.isNumeric(agent_pspt_id)) {
										data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
										failds.add(data);
										continue;
									}
								}
							}else {								
							    String psptIdsub=null; 
							    String psptIdsub2=null; 
							    if(agent_pspt_id.substring(0, 2).equals("TW")){
							    	psptIdsub="TW";
							    	psptIdsub2=agent_pspt_id.substring(2);
							    }else if(agent_pspt_id.substring(0, 4).equals("LXZH")){
							    	psptIdsub="LXZH";
							    	psptIdsub2=agent_pspt_id.substring(4);
							    }
							    if(psptIdsub!=null){
							    	
							    	PsptUtils  psptus = new PsptUtils();
							    	if(!psptus.isHaveUpperCase(psptIdsub2)||!psptus.isHaveNumeric(psptIdsub2)
							    			||psptIdsub2.indexOf("(")==-1||psptIdsub2.indexOf(")")==-1){//有大写					    		 			       			   	
										data.put("IMPORT_ERROR", psptname + "：" + desc + "前2位为“TW”或 “LXZH”字符时，后面需是阿拉伯数字、英文大写字母与半角“()”的组合");
										failds.add(data);
										continue;
							    	}
							    }								
							}																			
						} else if ("O".equals(agentPsptType)) {
							String psptname = "港澳居民来往内地通行证校验";
							String desc = "证件号码";					
							
							String psptIdtemp = replacespace(agent_pspt_id);
							if (!psptIdtemp.equals(agent_pspt_id)) {
								data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
								failds.add(data);
								continue;
							}
							
							int psptlength = agent_pspt_id.length();					 
			                //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
			                if (psptlength != 9 && psptlength!= 11) {	                    
								data.put("IMPORT_ERROR",  psptname + "：" + desc + "必须为9位或11位。");
								failds.add(data);
								continue;	                    
			                }
			                if (!(agent_pspt_id.substring(0,1).equals("H") || agent_pspt_id.substring(0,1).equals("M")) || !StringUtils.isNumeric(agent_pspt_id.substring(1))) {
			                	data.put("IMPORT_ERROR",  psptname + "：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
								failds.add(data);
								continue;
			                }	            
			                
						}
		             	
		             	
		             	
		             	
		             	
		             	
		       	     }
            
            
            
            
            
            
            
            
            
            
            
            
            
            //*****************************************使用人********************************************            
		    //DATA16使用人名称，DATA17使用人证件类型，DATA18使用人证件号码，DATA19使用人证件地址

		       	//使用人名称
	        	String use=data.getString("DATA16");
	        	if("".equals(use)||use == null){
		              data.put("IMPORT_ERROR", "使用人名称不能为空");
		              failds.add(data);
		              continue;
	    	    }
	        	//使用人证件类型
	        	String use_pspt_type_code=data.getString("DATA17");
	        	
	        	if("".equals(use_pspt_type_code)||use_pspt_type_code == null){
		              data.put("IMPORT_ERROR", "使用人证件类型不能为空");
		              failds.add(data);
		              continue;
	  	        }else{
	  	            //类型转换
	  	        	String[] key=new String[3];
	  	        	         key[0]="TYPE_ID";
	  	        	         key[1]="SUBSYS_CODE";
	  	        	         key[2]="DATA_NAME";
	  	     	    String[] value=new String[3];
	  	     	    		 value[0]="TD_S_PASSPORTTYPE2";
	  	     	    		 value[1]="CUSTMGR";
	  	     	    		 value[2]=use_pspt_type_code.trim();
	  	     	    		 
	  	        	data.put("DATA17", StaticUtil.getStaticValue(getVisit(), "TD_S_STATIC", key, "DATA_ID", value));
	  	        }
	        	//使用人证件号码
	        	String use_pspt_id=data.getString("DATA18").trim();
	        	if("".equals(use_pspt_id)||use_pspt_id == null){
		              data.put("IMPORT_ERROR", "使用人证件号码不能为空");
		              failds.add(data);
		              continue;
	        	}
	        	//使用人证件地址
	        	/**
	        	 * 由于1到15中的长度小于500,跟数据库不一样 ,数据库中为500
	        	 */
	        	String use_pspt_addr=data.getString("DATA19");
	        	if("".equals(use_pspt_addr)||use_pspt_addr == null){
		              data.put("IMPORT_ERROR", "使用人证件地址不能为空");
		              failds.add(data);
		              continue;
	        	}else{
	        		String use_pspt_addr_max=BatDataImportBean.maxLen("使用人证件地址", use_pspt_addr,80);
	        		if(!"0000".equals(use_pspt_addr_max)){
	  	              data.put("IMPORT_ERROR", use_pspt_addr_max);
		              failds.add(data);
		              continue;
	        		}
	        	}  
            
            
                       
            
	        	String  useObjValue=data.getString("DATA17");
	      	     if(!BatDataImportBean.isPersonCertificate(useObjValue)){
		              data.put("IMPORT_ERROR", "使用人的证件类型需为个人证件");
		              failds.add(data);
		              continue;
	      	     }else{
	      	    	 //个人证件类型
	      	    	 
	      	    	//使用人姓名验证
	      	    	String checkUseName=BatDataImportBean.checkCustName("使用人姓名", use, useObjValue);
	      	    	if(!"0000".equals(checkUseName)){
		  	              data.put("IMPORT_ERROR", checkUseName);
			              failds.add(data);
			              continue;
	      	    	}
	      	    	
	      	    	//使用人地址验证
	      	    	String checkUseAddr=BatDataImportBean.checkAddr("使用人证件地址", use_pspt_addr);
	      	    	if(!"0000".equals(checkUseAddr)){
		  	              data.put("IMPORT_ERROR", checkUseAddr);
			              failds.add(data);
			              continue;
	      	    	}
	      	    	
	      	    	 //使用人证件号码
	            	if(isRepeatCode(use_pspt_id)){
	            		
	            		data.put("IMPORT_ERROR", "使用人证件号码不能全为同一个数字，请重新输入!");
						failds.add(data);
						continue;
	            	}else if(isSerialCode(use_pspt_id)){
	            		
	            		data.put("IMPORT_ERROR", "使用人证件号码不能连续数字，请重新输入!");
						failds.add(data);
						continue;
	            	}
	      	    	//电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码
	            	
	            	if(use_pspt_id.length() >=4
	            			 &&serial_number.length()>=use_pspt_id.length()
	            			 &&(serial_number.indexOf(use_pspt_id)==0
	            				 ||serial_number.lastIndexOf(use_pspt_id) == (serial_number.length()-use_pspt_id.length()))
	            	     ){
	              		data.put("IMPORT_ERROR", "电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为使用人证件号码!");
						failds.add(data);
						continue;
					}
					// （本地身份证 0、外地身份证 1、户口本 2、护照 A、军官证 C、港澳台回乡证 H,台湾居民回乡证 I,港澳通行证J）
					// 港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
					if ("H".equals(useObjValue)) {
						if (use_pspt_id.length() != 9 && use_pspt_id.length() != 11) {
							data.put("IMPORT_ERROR", "港澳居民回乡证校验：使用人证件号码必须为9位或11位！!");
							failds.add(data);
							continue;
						}
	            		if(!(use_pspt_id.charAt(0)=='H' || use_pspt_id.charAt(0)=='M') 
	            				|| !StringUtils.isNumeric(use_pspt_id.substring(1)) ){
	                  		data.put("IMPORT_ERROR", "港澳居民回乡证校验：使用人证件号码首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字！");
	    					failds.add(data);
	    					continue;
	            		}
	            	}else if("I".equals(useObjValue)){
						// 台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
						if (use_pspt_id.length() != 8 && use_pspt_id.length() != 11) {
							data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码必须为8位或11位！");
							failds.add(data);
							continue;
						}
						if (use_pspt_id.length() == 8) {
							if (!StringUtils.isNumeric(use_pspt_id)) {
								data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码为8位时，必须均为阿拉伯数字！");
								failds.add(data);
								continue;
							}
						}
						if (use_pspt_id.length() == 11) {
							if (!StringUtils.isNumeric(use_pspt_id.substring(0, 10))) {
								data.put("IMPORT_ERROR", "台湾居民回乡校验：使用人证件号码为11位时，前10位必须均为阿拉伯数字。");
								failds.add(data);
								continue;
							}
						}
					} else if ("C".equals(useObjValue) || "A".equals(useObjValue)) {
						 
		                if (use_pspt_id.trim().indexOf(" ")!=-1) {
							String tmpName = useObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
							data.put("IMPORT_ERROR", tmpName + "证件号码中间不能有空格。");
							failds.add(data);
							continue;
						}
						// 军官证、警官证、护照：证件号码须大于等于6位字符
						if (use_pspt_id.length() < 6) {
							String tmpName = useObjValue.equals("A") ? "护照校验：" : "军官证类型校验：";
							data.put("IMPORT_ERROR", tmpName + "使用人证件号码须大于等于6位字符！");
							failds.add(data);
							continue;
						}
	            	}else if("0".equals(useObjValue)|| 
	            				"1".equals(useObjValue) || 
	            				"2".equals(useObjValue)) {
	            	 
	            		
	            		//身份证相关检查 
	                    if (use_pspt_id.length() == 15 || use_pspt_id.length() == 18)
	                    {
	                        if (StringUtils.isNotBlank(checkPspt(use_pspt_id, "", useObjValue)))
	                        {
	                           // 证件号码不正确
	                        	data.put("IMPORT_ERROR", "使用人证件号码格式不对");
		     					failds.add(data);
		     					continue;
	                        }
	                    }else{
	                    	    data.put("IMPORT_ERROR", "使用人证件号码位数不对");
		     					failds.add(data);
		     					continue;
	                    }                    
	                    
	                    String temppsptid = use_pspt_id;
	                    if(use_pspt_id.length()==15){
	                    	temppsptid = IdcardUtils.conver15CardTo18(temppsptid);
	                    }                    
						int age = IdcardUtils.getExactAgeByIdCard(temppsptid);
						 if (age < 16 || age > 120) {
						 	 data.put("IMPORT_ERROR", "年龄范围必须在16-120岁之间");
							 failds.add(data);
							 continue;
						 }
						 /*if (age > 120) {
						 	 data.put("IMPORT_ERROR", "年龄范围必须在120岁之内");
							 failds.add(data);
							 continue;
						 }*/
						
						IData input = new DataMap();
						input.put("CERT_ID", use_pspt_id);
						input.put("CERT_NAME", use);
						
						
	                    boolean iscallcheck = true;
	     				if (idcardlist != null && idcardlist.size() > 0) {
	     					for (int j = 0; j < idcardlist.size(); j++) {
	     						boolean issame = compareIData(idcardlist.get(j),input);
	     						if(issame){
	     							iscallcheck = false;
	     							break;
	     						}
	     					}
	     				} 
	     				
	     				if (iscallcheck) {
	     					log.error("BatDataImportSVCxxxxxxxxxxxxxxxxx6046 SS.CreatePersonUserSVC.verifyIdCard "+input);
							IDataset ds = CSAppCall.call("SS.CreatePersonUserSVC.verifyIdCard", input);
							if (!ds.isEmpty()) {
								String reCode = ds.getData(0).getString("X_RESULTCODE");
								if (reCode.equals("1")) {// 证件信息不合法
									data.put("IMPORT_ERROR", "使用人证件信息不合法");
									failds.add(data);
									continue;
								}
							}		                     
	     	                idcardlist.add(input);
	     				}
						

						
					} else if ("N".equals(useObjValue)) {// 台湾居民来往大陆通行证
						String psptname = "台湾居民来往大陆通行证校验";
						String desc = "证件号码";
						String psptIdtemp = replacespace(use_pspt_id);
						if (!psptIdtemp.equals(use_pspt_id)) {
							data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
							failds.add(data);
							continue;
						}
						
						int psptlength = use_pspt_id.length();
						
						if (psptlength < 4) {
							data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
							failds.add(data);
							continue;
						}
						
						if (!use_pspt_id.substring(0, 2).equals("TW") && !use_pspt_id.substring(0, 4).equals("LXZH")) {
							if (psptlength == 11 || psptlength == 12) {
								if (!StringUtils.isNumeric(use_pspt_id.substring(0, 10))) {
									data.put("IMPORT_ERROR", psptname + "：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
									failds.add(data);
									continue;
								}
							} else if (psptlength == 8 || psptlength == 7) {
								if (!StringUtils.isNumeric(use_pspt_id)) {
									data.put("IMPORT_ERROR", psptname + "：" + desc + "为" + psptlength + "位时，必须均为阿拉伯数字。");
									failds.add(data);
									continue;
								}
							} else {
								if (!StringUtils.isNumeric(use_pspt_id)) {
									data.put("IMPORT_ERROR", psptname + "：" + desc + "格式错误");
									failds.add(data);
									continue;
								}
							}
						}else {
						    String psptIdsub=null; 
						    String psptIdsub2=null; 
						    if(use_pspt_id.substring(0, 2).equals("TW")){
						    	psptIdsub="TW";
						    	psptIdsub2=use_pspt_id.substring(2);
						    }else if(use_pspt_id.substring(0, 4).equals("LXZH")){
						    	psptIdsub="LXZH";
						    	psptIdsub2=use_pspt_id.substring(4);
						    }
						    if(psptIdsub!=null){
						    	
						    	PsptUtils  psptus = new PsptUtils();
						    	if(!psptus.isHaveUpperCase(psptIdsub2)||!psptus.isHaveNumeric(psptIdsub2)
						    			||psptIdsub2.indexOf("(")==-1||psptIdsub2.indexOf(")")==-1){//有大写					    		 			       			   	
									data.put("IMPORT_ERROR", psptname + "：" + desc + "前2位为“TW”或 “LXZH”字符时，后面需是阿拉伯数字、英文大写字母与半角“()”的组合");
									failds.add(data);
									continue;
						    	}
						    }						
						}																			
					} else if ("O".equals(useObjValue)) {
						String psptname = "港澳居民来往内地通行证校验";
						String desc = "证件号码";					
						
						String psptIdtemp = replacespace(use_pspt_id);
						if (!psptIdtemp.equals(use_pspt_id)) {
							data.put("IMPORT_ERROR",  psptname + "：" + desc +"中间不能有空格。");
							failds.add(data);
							continue;
						}
						
						int psptlength = use_pspt_id.length();			 
		                //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
		                if (psptlength != 9 && psptlength!= 11) {	                    
							data.put("IMPORT_ERROR",  psptname + "：" + desc + "必须为9位或11位。");
							failds.add(data);
							continue;	                    
		                }
		                if (!(use_pspt_id.substring(0,1).equals("H") || use_pspt_id.substring(0,1).equals("M")) || !StringUtils.isNumeric(use_pspt_id.substring(1))) {
		                	data.put("IMPORT_ERROR",  psptname + "：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
							failds.add(data);
							continue;
		                }	            					
					}				
					
				}
	      	     
		           /* //使用人证件(检查同一证件号已开实名制用户的数量是否已超出预定值)
		         	if(!BatDataImportBean.updateCanCount(cidList, use_pspt_id)){
	               	    data.put("IMPORT_ERROR", "证件号【"+use_pspt_id+"】已开实名制用户的数量已超出预定值");
	 					failds.add(data);
	 					continue;
		         	}*/
            
            
            
            
            
            
            
            
            
                 
            succds.add(data);
        }
        
        //特殊处理
        if (failds != null && failds.size() > 0) {
            for (int k = 0; k < failds.size(); k++) {
                if (k == 0) {
                    IData error = new DataMap();
                    error = failds.getData(0);
                    error.put("WADE_TRANSFORM_ERROR_DATA", true);
                    failds.remove(0);
                    failds.add(0, error);
                }
                //将证件类型转换为中文名
                failds.getData(k).put("DATA2", BatDataImportBean.getTypeNameByTypeCode(failds.getData(k).getString("DATA2")));
                failds.getData(k).put("DATA13", BatDataImportBean.getTypeNameByTypeCode(failds.getData(k).getString("DATA13")));
                failds.getData(k).put("DATA17", BatDataImportBean.getTypeNameByTypeCode(failds.getData(k).getString("DATA17")));
            }
        }

        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }
    
    public IData fileImportCheckForPCRF(IData pageParam,IDataset dataset) throws Exception{

        String batchTaskId = pageParam.getString("BATCH_TASK_ID");
               
        IData batchTask = BatchTypeInfoQry.queryBatchDataTASK(batchTaskId).getData(0);
        String tradeTypeTag = "279";
        String str1 = (String) batchTask.getString("CODING_STR1");
        IData params = new  DataMap(str1);
        if(IDataUtil.isNotEmpty(params)){
        	String tempTradeType = params.getString("TRADE_TYPE_TAG","");
        	if(StringUtils.isNotBlank(tempTradeType) && StringUtils.equals("280", tempTradeType))
        	{
        		tradeTypeTag = tempTradeType;
        	}
        }
        str1 = str1.substring(str1.indexOf("["), str1.lastIndexOf("]") + 1);
        IDataset batchTask1s = new DatasetList(str1);
        //System.out.println("BatDataImportSVC.javaxxxxxxxxxx4334 " + batchTask1s);       

    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        IData returnData = new DataMap();
        if(IDataUtil.isEmpty(dataset)){
            IData error=new DataMap();	 	 
            error.put("IMPORT_ERROR", "内容为空");
            failds.add(error);
            returnData.put("SUCCDS", succds);
            returnData.put("FAILDS", failds);
            return returnData;	 	 
        }
        for (int i = 0; i < dataset.size(); i++) {
            IData data = dataset.getData(i);
            //System.out.println("BatDataImportSVC.javaxxxxxxxxxx4337 " + data.toString());
            boolean importResult = Boolean.valueOf(data.getString("IMPORT_RESULT", "true")).booleanValue();
            if (!importResult) {
                failds.add(data);
                continue;
            }
            //手机号码	 	 
            String serial_number = data.getString("SERIAL_NUMBER");

            if (serial_number == null || "".equals(serial_number)) {
                data.put("IMPORT_ERROR", "手机号码  不能为空!");
                failds.add(data);
                continue;
            } else {/*	 	 
                    serial_number=serial_number.trim();
                    if(!BatDataImportBean.validPhoneNum("0", serial_number)){
                     data.put("IMPORT_ERROR", "号码  格式错误");	 	 
                     failds.add(data);	 	 
                     continue;
                    }
                    */
            }

            UcaData uca = null;
            try {
                uca = UcaDataFactory.getNormalUca(serial_number);
            } catch (Exception e) {
                if (e.getMessage().indexOf("CRM_BOF_002") >= 0) {
                    data.put("IMPORT_ERROR", "根据手机号码未找到用户资料");
                    failds.add(data);
                    continue;
                } else {
                    String msg = e.getMessage();
                    msg = (msg == null) ? "根据手机号码未找到用户资料" : msg;
                    String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                    data.put("IMPORT_ERROR", rspDesc);
                    failds.add(data);
                    continue;
                    //throw new Exception(e);
                }
            }

          

            String strBrandCode = uca.getBrandCode();

            if (!"PWLW".equals(strBrandCode)) {
                data.put("IMPORT_ERROR", serial_number + "不是物联网号码");
                failds.add(data);
                continue;
            }

            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serial_number);
            param.put("USER_ID", uca.getUserId());
            param.put("TRADE_TYPE_CODE", tradeTypeTag);

            IDataset userSvcInfos = CSAppCall.call("SS.IOTQuerySVC.queryUserSvcInfos", param);
            //System.out.println("BatDataImportSVC.javaxxxxxxxxxx4421 " + userSvcInfos);

            if (userSvcInfos == null || userSvcInfos.size() == 0) {
                data.put("IMPORT_ERROR", serial_number + "不存在可以操作PCRF控制策略的服务!");
                failds.add(data);
                continue;
            }

            IDataset userPcrfInfos = CSAppCall.call("SS.IOTQuerySVC.queryUserPcrfInfos", param);
            //System.out.println("BatDataImportSVC.javaxxxxxxxxxx4429 " + userPcrfInfos);
            boolean continueflag = false;
            String continueStr = "";
            if (batchTask1s != null && batchTask1s.size() > 0) {
                for (int ij = 0; ij < batchTask1s.size(); ij++) {
                    IData batchTaskData = batchTask1s.getData(ij);
                    
                    //System.out.println("BatDataImportSVC.javaxxxxxxxxxx4336 " + batchTaskData);
                    if (batchTaskData.getString("MODIFY_TAG").equals("0")) {
                        String filterStr1 = "SERVICE_CODE=" + batchTaskData.getString("SERVICE_CODE", "").trim() + "";
                        IDataset filterData1 = DataHelper.filter(userPcrfInfos, filterStr1);
                        if (filterData1 != null && filterData1.size() > 0) {
                            continueStr = "[" + batchTaskData.getString("SERVICE_CODE", "").trim() + "]策略已存在，不能重复新增!";
                            continueflag = true;
                            break;
                        }
                    }
                    
                    if (batchTaskData.getString("MODIFY_TAG").equals("1")) {
                        continueStr = "[" + batchTaskData.getString("SERVICE_CODE", "").trim() + "]策略不存在，不能删除!";
                        if(userPcrfInfos==null||userPcrfInfos.size()==0){   
                            continueflag = true;
                            break;
                        }
                        String filterStr1 = "SERVICE_CODE=" + batchTaskData.getString("SERVICE_CODE", "").trim() + "";
                        IDataset filterData1 = DataHelper.filter(userPcrfInfos, filterStr1);
                        if (filterData1 == null || filterData1.size() == 0) {
                            continueflag = true;
                            break;
                        }
                    }
                    
                    if (batchTaskData.getString("MODIFY_TAG").equals("2")) {
                        continueStr = "[" + batchTaskData.getString("SERVICE_CODE", "").trim() + "]策略不存在，不能修改!";
                        if(userPcrfInfos==null||userPcrfInfos.size()==0){   
                            continueflag = true;
                            break;
                        }
                        String filterStr1 = "SERVICE_CODE=" + batchTaskData.getString("SERVICE_CODE", "").trim() + "";
                        IDataset filterData1 = DataHelper.filter(userPcrfInfos, filterStr1);
                        if (filterData1 == null || filterData1.size() == 0) {
                            continueflag = true;
                            break;
                        }
                    }
                }              
            }
            
            if (continueflag) {
                data.put("IMPORT_ERROR", continueStr);
                failds.add(data);
                continue;
            }
            succds.add(data);
            
        }

        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        // IData inParam = new DataMap();
        if (succds.size() == 0)
        {
            return returnData;
        }
        else
        {
            String serial_number = (String) succds.get(0, "SERIAL_NUMBER");
            if (serial_number == null || serial_number.equals(""))
            {
                return returnData;
            }
            
        }

        return returnData;
    }

    /**
     * check pspt
     * 
     * @param value
     * @param desc
     * @param psptTypeCode
     *            ：证件类型
     * @return 成功返回空字符串，失败则返回错误信息
     */
    private String checkPspt(String value, String desc, String psptTypeCode)
    {
        String[] errors =
        { "验证通过", "身份证号码位数不对", "身份证号码不合法", "身份证号码校验错误", "身份证地区非法", "身份证出生日期不符合要求", "身份证为空" };
        if (value == null || "".equals(value))
            return desc + "(" + value + ")" + errors[4] + ";";
        IData area = new DataMap();
        area.put("11", "\u5317\u4EAC");
        area.put("12", "\u5929\u6D25");
        area.put("13", "\u6CB3\u5317");
        area.put("14", "\u5C71\u897F");
        area.put("15", "\u5185\u8499\u53E4");
        area.put("21", "\u8FBD\u5B81");
        area.put("22", "\u5409\u6797");
        area.put("23", "\u9ED1\u9F99\u6C5F");
        area.put("31", "\u4E0A\u6D77");
        area.put("32", "\u6C5F\u82CF");
        area.put("33", "\u6D59\u6C5F");
        area.put("34", "\u5B89\u5FBD");
        area.put("35", "\u798F\u5EFA");
        area.put("36", "\u6C5F\u897F");
        area.put("37", "\u5C71\u4E1C");
        area.put("41", "\u6CB3\u5357");
        area.put("42", "\u6E56\u5317");
        area.put("43", "\u6E56\u5357");
        area.put("44", "\u5E7F\u4E1C");
        area.put("45", "\u5E7F\u897F");
        area.put("46", "\u6D77\u5357");
        area.put("50", "\u91CD\u5E86");
        area.put("51", "\u56DB\u5DDD");
        area.put("52", "\u8D35\u5DDE");
        area.put("53", "\u4E91\u5357");
        area.put("54", "\u897F\u85CF");
        area.put("61", "\u9655\u897F");
        area.put("62", "\u7518\u8083");
        area.put("63", "\u9752\u6D77");
        area.put("64", "\u5B81\u590F");
        area.put("65", "\u65B0\u7586");
        area.put("71", "\u53F0\u6E7E");
        area.put("81", "\u9999\u6E2F");
        area.put("82", "\u6FB3\u95E8");
        area.put("91", "\u56FD\u5916");

        String idcard = value, Y, JYM;
        String S, M, ereg;
        Calendar c = Calendar.getInstance();
        if (idcard.charAt(idcard.length() - 1) == '*')
            idcard = idcard.substring(0, idcard.length() - 1) + 'X';

        if (!area.containsKey(idcard.substring(0, 2)))
        {
            return desc + "(" + value + ")" + errors[4] + ";";
        }
        switch (idcard.length())
        {
            case 15:
                if ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0 || ((Integer.parseInt(idcard.substring(6, 8)) + 1900) % 100 == 0 && (Integer.parseInt(idcard.substring(6, 8)) + 1900) % 4 == 0))
                {
                    ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}$";
                }
                else
                {
                    ereg = "^[1-9][0-9]{5}([0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}$";
                }
                boolean bTemp = Pattern.compile(ereg).matcher(idcard).find();
                if (bTemp)
                {
                    Matcher matches = Pattern.compile(ereg).matcher(idcard);
                    c.setTime(new java.util.Date());
                    int nowY = c.get(Calendar.YEAR);
                    if (matches.groupCount() > 0)
                    {
                        if (Integer.parseInt(("19" + idcard.substring(6, 8))) + 100 < nowY)
                        {
                            return desc + "(" + value + ")" + errors[5] + ";";
                        }
                    }
                    return "";
                }
                else
                {
                    return desc + "(" + value + ")" + errors[2] + ";";
                }
            case 18:
                if (Integer.parseInt(idcard.substring(6, 10)) % 4 == 0 || (Integer.parseInt(idcard.substring(6, 10)) % 100 == 0 && Integer.parseInt(idcard.substring(6, 10)) % 4 == 0))
                {
                    ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|[1-2][0-9]))[0-9]{3}[0-9Xx]$";
                }
                else
                {
                    ereg = "^[1-9][0-9]{5}((19|20)[0-9]{2})((01|03|05|07|08|10|12)(0[1-9]|[1-2][0-9]|3[0-1])|(04|06|09|11)(0[1-9]|[1-2][0-9]|30)|02(0[1-9]|1[0-9]|2[0-9]))[0-9]{3}[0-9Xx]$";
                }
                boolean bTemp18 = Pattern.compile(ereg).matcher(idcard).find();
                if (bTemp18)
                {
                    Pattern pattern = Pattern.compile(ereg);
                    Matcher matches = pattern.matcher(idcard);
                    c.setTime(new java.util.Date());
                    int nowY = c.get(Calendar.YEAR);
                    if (matches.groupCount() > 0)
                    {
                        int iYear = Integer.parseInt(idcard.substring(6, 10));
                        /*------modify by chenzg@20131122--身份证件类型新增户口本(REQ201311080002)---begin----*/
                        /* 户口本证件类型，用户的也是身份证，但是校验身份证号码时，不限制必须大于15岁 */
                        if ("2".equals(psptTypeCode))
                        {
                            if ((iYear + 100) < nowY)
                            {
                                return desc + "(" + value + ")" + errors[5] + ";";
                            }
                        }
                        else
                        {
                           // if ((iYear + 15) > nowY || (iYear + 100) < nowY)
                        	if ((iYear + 100) < nowY)
                            {
                                return desc + "(" + value + ")" + errors[5] + ";";
                            }
                        }
                        /*------modify by chenzg@20131122--身份证件类型新增户口本(REQ201311080002)---end------*/
                    }
                    
                    
                    /*每位加权因子*/
	                String[] powers = new String[]{"7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2"};
	                /*第18位校检码*/
	                String[]  parityBit = new String[]{"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
	                String psptBit = value.substring(17,18).toUpperCase();
	                String id17 = value;
	                /*加权 */
	                int power = 0;
	                for (int i = 0; i < 17; i++) {
	                    power += new Integer(id17.charAt(i)-'0') * new Integer(powers[i]);
	                }
	                /*取模*/
	                int mod = power % 11;
	                String checkBit = parityBit[mod];
	                //System.out.println(power);
	                if (!psptBit.equals(checkBit)) {
	                	//System.out.println(1111);
	                	return desc + "(" + value + ")" + errors[2] + ";";
	                }
                    
	                String bit11 = value.substring(10, 11);
	                String bit13 = value.substring(12, 13);
	                if (!bit11.equals("0") && !bit11.equals("1")) {
	                	return desc + "(" + value + ")" + errors[2] + ";";
	                }
	                if (new Integer(bit13) > 3) {
	                	return desc + "(" + value + ")" + errors[2] + ";";

	                }
                    
                    
                    return "";
                }
                else
                {
                    return desc + "(" + value + ")" + errors[2] + ";";
                }
            default:
                return desc + "(" + value + ")" + errors[2] + ";";
        }
    } 
    /**	
     *	 	 
     * REQ201609060001_2016年下半年测试卡功能优化（二）	 	 
     * @author zhuoyingzhi	 	 
     * 20160930	 	 
     * @param pdData	 	 
     * @param dataset	 	 
     * @return	 	 
     * @throws Exception	 	 
     */	 	 
    public IData fileImportCheckTestCardUser(IData pdData, IDataset dataset) throws Exception	 	 
    {	 	 
        IDataset succds = new DatasetList();	 	 
        IDataset failds = new DatasetList();	 	 
        IData returnData = new DataMap();  	 
       	 	 
        if(IDataUtil.isEmpty(dataset)){	 	 
            IData error=new DataMap();	 	 
            error.put("IMPORT_ERROR", "内容为空");	 	 
            failds.add(error);	 
            returnData.put("SUCCDS", succds);	 	 
            returnData.put("FAILDS", failds);	
            return returnData;	 	 
        }	 	 
        for (int i = 0; i < dataset.size(); i++)	 	 
        {
            IData data = dataset.getData(i);	 	 
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);	 	 
            if (!importResult)	 	 
            {	 	 
                failds.add(data);	 	 
                continue;	 	 
            }	 	 
            //手机号码	 	 
            String serial_number=data.getString("SERIAL_NUMBER");
            
            if("".equals(serial_number)||serial_number == null){	 	 
                    data.put("IMPORT_ERROR", "手机号码  不能为空!");	 	 
                    failds.add(data);	 	 
                    continue;	 	 
               }else{	 	 
                  serial_number=serial_number.trim();	 	 
               }	 	 
            /**
             * BUG20161201100629_批量修改测试号码类型界面优化
             * @author zhuoyingzhi
             * 
             */
            try {
            	UcaData uca = UcaDataFactory.getNormalUca(serial_number);	
                /**
                 * 测试卡判断  修改为：通过tf_f_user_other表
                 * 
                 */
               	if(!BatDataImportBean.IsTestCradUser(serial_number, uca.getUserId())){
                    data.put("IMPORT_ERROR", "手机号码  不是测试卡用户!");	 	 
                    failds.add(data);	 	 
                    continue;	
               	}
			} catch (Exception e) {
				 //log.info("(e);
                 data.put("IMPORT_ERROR", "号码为非正常在网用户,请核查");	 	 
                 failds.add(data);	 	 
                 continue;	
			}            
           	 	 
            //测试卡类型	 	 
            String testCardUserType=data.getString("DATA1");	 	 
           	 	 
            if("".equals(testCardUserType)||testCardUserType == null){	 	 
                  data.put("IMPORT_ERROR", "测试卡类型   不能为空");	 	 
                  failds.add(data);	 	 
                  continue;	 	 
              }else{	 	 
                  //类型转换	 	 
                 if("限制办理渠道".equals(testCardUserType.trim())){	 	 
                     data.put("DATA1","0");	 	 
                 }else if("不限制办理渠道".equals(testCardUserType.trim())){	 	 
                     data.put("DATA1","1");	 	 
                 }	 	 
              }	 	 
            succds.add(data);	 	 
        }	 	 
        //特殊处理	 	 
        if (failds != null && failds.size() > 0){	 	 
            for(int k=0;k<failds.size();k++){	 	 
                if(k==0){	 	 
                    IData error=new DataMap();	 	 
                    error=failds.getData(0);	 	 
                    error.put("WADE_TRANSFORM_ERROR_DATA", true);	 	 
                    failds.remove(0);	 	 
                    failds.add(0, error);	 	 
                }	 	 
                //将测试卡类型转换为中文名	 	 
                String  str=failds.getData(k).getString("DATA1");	 	 
                 if("0".equals(str)){	 	 
                     failds.getData(k).put("DATA1", "限制办理渠道");	 	 
                 }else if("1".equals(str)){	 	 
                      failds.getData(k).put("DATA1", "不限制办理渠道");	 	 
                 }else{	 	 
                     failds.getData(k).put("DATA1", "");	 	 
                 }	 	 
 	 
            }	 	 
        }	 	 
       	 	  	 
        returnData.put("SUCCDS", succds);	 	 
        returnData.put("FAILDS", failds);	 	 
 		 
        return returnData;
    }
    /**
     * REQ201610200010_关于测试卡管理的三点优化
     * @author zhuoyingzhi
     * 20161110
     * <br/>
     * 批量修改测试卡流量封顶值
     * @param pdData
     * @param dataset
     * @return
     * @throws Exception
     */
    public IData fileImportCheckTestCardMaxFlowValue(IData pdData, IDataset dataset) throws Exception{
        IDataset succds = new DatasetList();	 	 
        IDataset failds = new DatasetList();	 	 
        IData returnData = new DataMap();	 	 
   	 
       	 	 
        if(IDataUtil.isEmpty(dataset)){	 	 
            IData error=new DataMap();	 	 
            error.put("IMPORT_ERROR", "内容为空");	 	 
            failds.add(error);
            returnData.put("SUCCDS", succds);	 	 
            returnData.put("FAILDS", failds);
            return returnData;	 	 
        }	 	 
        for (int i = 0; i < dataset.size(); i++)	 	 
        {	 	 
            IData data = dataset.getData(i);	 	 
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);	 	 
            if (!importResult)	 	 
            {	 	 
                failds.add(data);	 	 
                continue;	 	 
            }	 	 
            //手机号码	 	 
            String serial_number=data.getString("SERIAL_NUMBER");	 	 
           	 	 
           	 	 
            if(serial_number == null||"".equals(serial_number)){	 	 
                    data.put("IMPORT_ERROR", "手机号码  不能为空!");	 	 
                    failds.add(data);	 	 
                    continue;	 	 
               }else{	 	 
                  serial_number=serial_number.trim();
                  if(!BatDataImportBean.validPhoneNum("0", serial_number)){
                      data.put("IMPORT_ERROR", "手机号码  格式错误");	 	 
                      failds.add(data);	 	 
                      continue;
                  }
               }
            UcaData uca=new UcaData();
           	try {
                 uca = UcaDataFactory.getNormalUca(serial_number);
                /**
                 * 测试卡判断  修改为：通过tf_f_user_other表
                 * 
                 */
               	if(!BatDataImportBean.IsTestCradUser(serial_number, uca.getUserId())){
                    data.put("IMPORT_ERROR", "手机号码  不是测试卡用户!");	 	 
                    failds.add(data);	 	 
                    continue;	
               	}
			} catch (Exception e) {
				//log.info("(e);
                data.put("IMPORT_ERROR", "非正常在网用户不能修改流量封顶值");	 	 
                failds.add(data);	 	 
                continue;
			}
 	 
           	
            //流量封顶值	 	 
            String maxFlowValue=data.getString("DATA1"); 	 
           	 	 
            if(maxFlowValue == null||"".equals(maxFlowValue)){	 	 
                  data.put("IMPORT_ERROR", "流量封顶值  不能为空"); 	 
                  failds.add(data);	 	 
                  continue;	 	 
              }
            //判断手机号码是否有 流量封顶服务
/*            IDataset userSvcInfo=BatDataImportBean.getUserSVCInfoByEnd(uca.getUserId(), "9501");
            
            if(IDataUtil.isEmpty(userSvcInfo)){
                data.put("IMPORT_ERROR", "导入的号码没有流量封顶服务"); 	 
                failds.add(data);	 	 
                continue;
            }*/
            
            //将成功的数据   流量 转换为字节
            IDataset allInfo=BatDataImportBean.getAttrItembByAttrFieldName(data.getString("DATA1"));
            if(IDataUtil.isNotEmpty(allInfo)){
            	data.put("DATA1", allInfo.getData(0).getString("VALUE",""));
            }else{
            	//转入的    流量封顶值   不对
                data.put("IMPORT_ERROR", "流量封顶值    错误");
                failds.add(data);	 	 
                continue;
            }
            succds.add(data);	 	 
        }	 	 
        //特殊处理	 	 
        if (failds != null && failds.size() > 0){	 
            for(int k=0;k<failds.size();k++){	 	 
                if(k==0){	 	 
                    IData error=new DataMap();	 	 
                    error=failds.getData(0);	 	 
                    error.put("WADE_TRANSFORM_ERROR_DATA", true);	 	 
                    failds.remove(0);	 	 
                    failds.add(0, error);	 	 
                }	 	 
            }	 	 
        }
        
        returnData.put("SUCCDS", succds);	 	 
        returnData.put("FAILDS", failds);	 	 
 		 
        return returnData;
    }
    
    /**	
     *	 	 
     * REQ201610200010_关于测试卡管理的三点优化	 
     * @author zhuoyingzhi	 	 
     * 20161110
     * <br/>
     * 批量修改测试卡客户名称 	 
     * @param pdData	 	 
     * @param dataset	 	 
     * @return	 	 
     * @throws Exception	 	 
     */	 	 
    public IData fileImportCheckTestCardCustName(IData pdData, IDataset dataset) throws Exception	 	 
    {	 	 
        IDataset succds = new DatasetList();	 	 
        IDataset failds = new DatasetList();	 	 
        IData returnData = new DataMap(); 	 
       	 	 
        if(IDataUtil.isEmpty(dataset)){	 	 
            IData error=new DataMap();	 	 
            error.put("IMPORT_ERROR", "内容为空");	 	 
            failds.add(error);
            returnData.put("SUCCDS", succds);	 	 
            returnData.put("FAILDS", failds);	 
            return returnData;	 	 
        }
        
        for (int i = 0; i < dataset.size(); i++)	 	 
        {	 	 
            IData data = dataset.getData(i);	 	 
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);	 	 
            if (!importResult)	 	 
            {	 	 
                failds.add(data);	 	 
                continue;	 	 
            }	 	 
            //手机号码	 	 
            String serial_number=data.getString("SERIAL_NUMBER");	 	 
           	 	 
           	 	 
            if(serial_number == null||"".equals(serial_number)){	 	 
                    data.put("IMPORT_ERROR", "手机号码  不能为空!");	 	 
                    failds.add(data);	 	 
                    continue;	 	 
               }else{	 	 
                  serial_number=serial_number.trim();
                  if(!BatDataImportBean.validPhoneNum("0", serial_number)){
                      data.put("IMPORT_ERROR", "手机号码  格式错误");	 	 
                      failds.add(data);	 	 
                      continue;
                  }
               }	 	 
           	try {
                /**
                 * 测试卡判断  修改为：通过tf_f_user_other表
                 * 
                 */
                UcaData uca = UcaDataFactory.getNormalUca(serial_number);
               	if(!BatDataImportBean.IsTestCradUser(serial_number, uca.getUserId())){
                    data.put("IMPORT_ERROR", "手机号码  不是测试卡用户!");	 	 
                    failds.add(data);	 	 
                    continue;	
               	}
			} catch (Exception e) {
				//log.info("(e);
                data.put("IMPORT_ERROR", "非正常在网用户不能修改名称");	 	 
                failds.add(data);
                continue;	
			}
 	 
           	 
            
            //客户名称	 	 
            String custName=data.getString("DATA1");
			
			/******关于批量修改测试卡客户名称界面的优化需求*****/
            //使用人姓名	 
	        String userName=data.getString("DATA2");
	        //使用人证件号	 
	        String userPsid=data.getString("DATA3");
	        //经办人姓名	 
	        String agentName=data.getString("DATA4");
	        //经办人证件号	 
	        String agenPsid=data.getString("DATA5");
	        
	       if(StringUtils.isNotEmpty(userName)){
	        	data.put("USE", data.getString("DATA2"));
	        }
	        
	        if(StringUtils.isNotEmpty(userPsid)){
	        	data.put("USE_PSPT_ID", data.getString("DATA3"));
	        }
	        
	        if(StringUtils.isNotEmpty(agentName)){
	        	data.put("AGENT_CUST_NAME", data.getString("DATA4"));
	        }
	        
	        if(StringUtils.isNotEmpty(agenPsid)){
	        	data.put("AGENT_PSPT_ID", data.getString("DATA5"));
	        }
	        /******关于批量修改测试卡客户名称界面的优化需求*****/
			
            if(custName == null||"".equals(custName)){	 	 
                  data.put("IMPORT_ERROR", "客户名称   不能为空");	 	 
                  failds.add(data);	 	 
                  continue;	 	 
              }
            
            succds.add(data);	 	 
        }	 	 
        //特殊处理	 	 
        if (failds != null && failds.size() > 0){	 	 
            for(int k=0;k<failds.size();k++){	 	 
                if(k==0){	 	 
                    IData error=new DataMap();	 	 
                    error=failds.getData(0);	 	 
                    error.put("WADE_TRANSFORM_ERROR_DATA", true);	 	 
                    failds.remove(0);	 	 
                    failds.add(0, error);	 	 
                }	 	 
            }	 	 
        }	 	 
       	 	 
        	 	 
        returnData.put("SUCCDS", succds);	 	 
        returnData.put("FAILDS", failds);	 	 
 		 
        return returnData;
    }  
    
    /**
     * REQ201612010002_关于2016年下半年测试卡规则优化需求（一）
     * @author zhuoyingzhi
     * <br/>
     * 测试号码批量局开效验    
     * @param pdData
     * @param dataset
     * @return
     * @throws Exception
     */
    public IData fileImportCheckTestOfficeOpenMobileile(IData pdData, IDataset dataset) throws Exception	 	 
    {	 	 
        IDataset succds = new DatasetList();	 	 
        IDataset failds = new DatasetList();	 	 
        IData returnData = new DataMap(); 	 
       	 	 
        if(IDataUtil.isEmpty(dataset)){	 	 
            IData error=new DataMap();	 	 
            error.put("IMPORT_ERROR", "内容为空");	 	 
            failds.add(error);
            returnData.put("SUCCDS", succds);	 	 
            returnData.put("FAILDS", failds);	 
            return returnData;	 	 
        }
        
        for (int i = 0; i < dataset.size(); i++)	 	 
        {	 	 
            IData data = dataset.getData(i);	 	 
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);	 	 
            if (!importResult)	 	 
            {	 	 
                failds.add(data);	 	 
                continue;	 	 
            }	 	 
            //手机号码	 	 
            String serial_number=data.getString("SERIAL_NUMBER");	 	            	 	 
           	
            if(serial_number == null||"".equals(serial_number)){	 	 
                    data.put("IMPORT_ERROR", "手机号码  不能为空!");	 	 
                    failds.add(data);	 	 
                    continue;	 	 
               }else{	 	 
                  serial_number=serial_number.trim();
                  if(!BatDataImportBean.validPhoneNum("0", serial_number)){
                      data.put("IMPORT_ERROR", "手机号码  格式错误");	 	 
                      failds.add(data);	 	 
                      continue;
                  }
               }	 	 
              
               	/**
               	 * 判断是否为测试号码
               	 */
                try {
                    UcaData uca = UcaDataFactory.getNormalUca(serial_number);
                    String city_code = uca.getUser().getCityCode();
                    if (!"HNSJ".equals(city_code) && !"HNHN".equals(city_code))
                    {
                        data.put("IMPORT_ERROR", "非测试卡用户！");	 	 
                        failds.add(data);
                        continue;
                    } 
				} catch (Exception e) {
					  //log.info("(e);
		              data.put("IMPORT_ERROR", "未找到该服务号码对应的用户资料信息");	 	 
	                  failds.add(data);
	                  continue;
				}
				
            succds.add(data);	 	 
        }	
        
        
        //特殊处理	 	 
        if (failds != null && failds.size() > 0){	 	 
            for(int k=0;k<failds.size();k++){	 	 
                if(k==0){	 	 
                    IData error=new DataMap();	 	 
                    error=failds.getData(0);	 	 
                    error.put("WADE_TRANSFORM_ERROR_DATA", true);	 	 
                    failds.remove(0);	 	 
                    failds.add(0, error);	 	 
                }	 	 
            }	 	 
        }	 	 
       	 	 
        	 	 
        returnData.put("SUCCDS", succds);	 	 
        returnData.put("FAILDS", failds);	 	 
 		 
        return returnData;
    }   
    public IData fileImportCheckForBatBNBDWidenetDestroy(IData pdData, IDataset dataset) throws Exception
    {
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        if(dataset.size()>0){
		 	for (int i = 0; i < dataset.size(); i++)
		 	{
		 		IData data = new DataMap();
             	data = dataset.getData(i);  
             	String serialNumber=data.getString("SERIAL_NUMBER");
             	UcaData ucaData=null;
             	try{
             		ucaData = UcaDataFactory.getNormalUca(serialNumber); //可能输入不存在数据的用户导致报错
 	 	        }catch(Exception e){
 		    		String error =  Utility.parseExceptionMessage(e);
 		    		String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
 					if(errorArray.length >= 2)
 					{
 						String strExceptionMessage = errorArray[1];
 						data.put("IMPORT_ERROR", "号码【"+serialNumber+"】销户校验失败:"+strExceptionMessage);
 					}
 					else
 					{
 						data.put("IMPORT_ERROR", "号码【"+serialNumber+"】销户校验失败:"+error);
 					}  
 					failds.add(data);
                     continue;
 	 	        }
             	if(ucaData==null){
             		data.put("IMPORT_ERROR", "获取用户数据出错!");
             		failds.add(data);
             		continue;
             	}
             	String bnbdFlag=ucaData.getUser().getRsrvStr10();
             	if(bnbdFlag==null||!bnbdFlag.equals("BNBD")){
             		data.put("IMPORT_ERROR", "该号码不为商务宽带用户!");
             		failds.add(data);
             		continue;
             	}
				String removeReasonCodeName = data.getString("DATA1","");
 	        	String removeReasonCode = StaticUtil.getStaticValue(getVisit(),"TD_S_STATIC", new String []{"DATA_NAME"}, "DATA_ID", new String []{removeReasonCodeName});
				if( removeReasonCode==null||"".equals(removeReasonCode) )
				{
					CSAppException.apperr(CrmCommException.CRM_COMM_103, "该号码【"+serialNumber+"】销号原因为空或不正确，请检查导入文件!");
				}
				String DATA2 = data.getString("DATA2","");
				if(removeReasonCode.equals("9")&&(DATA2==null||DATA2.equals(""))){
					data.put("IMPORT_ERROR", "号码【"+serialNumber+"】销号原因为"+removeReasonCodeName+"时，备注不能为空!");
             		failds.add(data);
             		continue;
				}
				data.put("DATA1", removeReasonCode);
				succds.add(data);
             }
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);
        return returnData;
    }
    
    public String replacespace(String s) throws Exception{
    	while(s.indexOf(" ")!=-1){
    		s = s.replaceAll(" ", "");
    	}
    	return s;    		
    }
    
	public IData fileImportCheckGrpDiscntChgSpec(IData pdData, IDataset dataset) throws Exception
    {
    	IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();
        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER");
            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            try {
            	UcaData uca = UcaDataFactory.getNormalUca(mainSN);
            	
            	if(BatDataImportBean.IsUserGrpDiscnt(mainSN, uca.getUserId())){
	                data.put("IMPORT_ERROR", "手机号码没有可修改的集团产品优惠!");	 	 
	                failds.add(data);	 	 
	                continue;	
	           	}
			} catch (Exception e) {
				data.put("IMPORT_ERROR", "无有效手机号码数据!");	 	 
                failds.add(data);	 	 
                continue;	
			}            
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }

    
    
	public String checkfieldForModifyTdPspt(Map<String, String> map) throws Exception {
		// DATA1=客户名称,DATA2=证件类型,DATA3=证件号码,DATA4=证件地址,
		// DATA5=法人,DATA6=营业开始日期,DATA7=营业结束日期,DATA8=成立日期,
		// DATA9=机构类型,DATA11=有效日期,DATA12=失效日期,
		// DATA13经办人名称，DATA14经办人证件类型，DATA15经办人证件号码，DATA16经办人证件地址，
		// DATA17使用人名称，DATA18使用人证件类型，DATA19使用人证件号码，DATA20使用人证件地址
		System.out.println("BatDataImportSVCxxxxxxxxxxxxxx6862 "+map.size());
		String returnerrorinfo = "";
		String notnullinfo = "不能为空！";
		
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			//System.out.println("BatDataImportSVCxxxxxxxxxxxxxx6862 "+key+" ; "+value);
			
			if ("DATA1".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "客户名称" + notnullinfo;
			}
			if ("DATA2".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "证件类型" + notnullinfo;
			}
			if ("DATA3".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "证件号码" + notnullinfo;
			}
			if ("DATA4".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "证件地址" + notnullinfo;
			}
			if ("DATA5".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "法人" + notnullinfo;
			}
			if ("DATA6".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "营业开始日期" + notnullinfo;
			}
			if ("DATA7".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "营业结束日期" + notnullinfo;
			}
			if ("DATA8".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "成立日期" + notnullinfo;
			}
			if ("DATA9".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "机构类型" + notnullinfo;
			}
			if ("DATA10".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "有效日期" + notnullinfo;
			}
			if ("DATA11".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "失效日期" + notnullinfo;
			}

			if ("DATA12".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "经办人名称" + notnullinfo;
			}
			if ("DATA13".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "经办人证件类型" + notnullinfo;
			}
			if ("DATA14".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "经办人证件号码" + notnullinfo;
			}
			if ("DATA15".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "经办人证件地址" + notnullinfo;
			}
			if ("DATA16".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "使用人名称" + notnullinfo;
			}
			if ("DATA17".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "使用人证件类型" + notnullinfo;
			}
			if ("DATA18".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "使用人证件号码" + notnullinfo;
			}
			if ("DATA19".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "使用人证件地址" + notnullinfo;
			}
			if ("DATA20".equals(key) && (value==null || "null".equals(value) || "".equals(value))) {
				returnerrorinfo = "备注" + notnullinfo;
			}
			
		}

		return returnerrorinfo;
	}

    public IData fileImportCheckBenefitAddUseNum(IData pdData, IDataset dataset) throws Exception{
        IDataset succds = new DatasetList();
        IDataset failds = new DatasetList();

        for (int i = 0; i < dataset.size(); i++)
        {
            IData data = dataset.getData(i);
            String mainSN = data.getString("SERIAL_NUMBER");
            String addUseNum = data.getString("DATA1");

            boolean importResult = data.getBoolean("IMPORT_RESULT", true);
            if (!importResult)
            {
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(mainSN))
            {
                data.put("IMPORT_ERROR", "服务号码格式不对!");
                failds.add(data);
                continue;
            }
            if (!StringUtils.isNumeric(addUseNum))
            {
                data.put("IMPORT_ERROR", "增加使用次数格式不对!");
                failds.add(data);
                continue;
            }
            succds.add(data);
        }
        IData returnData = new DataMap();
        returnData.put("SUCCDS", succds);
        returnData.put("FAILDS", failds);

        return returnData;
    }

	public boolean compareIData(IData baseIdata, IData checkIData) throws Exception {
		boolean issame = true;

		for (Map.Entry<String, Object> entry : baseIdata.entrySet()) {
		 
			String basekey = entry.getKey();
			String basevalue = (String) entry.getValue();
			String checkvalue =  checkIData.getString(basekey) ;
			
			if(basevalue == null && checkvalue == null){
				continue;
			}
			
			if ( (basevalue == null && checkvalue != null) || (basevalue != null && checkvalue == null) ) {
				issame = false;	
				break; 
			}
			
			if (basevalue != null && checkvalue != null) {
				if (!basevalue.trim().equals(checkvalue.trim())) {
					issame = false;	
					break;
				}
			}else{
				
			}
			
		}

		return issame;
	}
    
}
