
package com.asiainfo.veris.crm.order.web.group.bat.batdeal;

import net.sf.json.JSONObject;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.BizEnv;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productcompinfo.ProductCompInfoIntfViewUtil;

public abstract class GrpBatTradeDeal extends CSBasePage
{
    /**
     * 通过后台自动导入数据 创建批量
     * 
     * @param cycle
     * @throws Exception
     */
    public void createBatDealInfo(IRequestCycle cycle) throws Exception
    {

        IData data = getData();
        String batchOperType = data.getString("BATCH_OPER_CODE");
        if ("GRPDESTROYONEKEY".equals(batchOperType))
        {
            IData hintMessageData = grpUserMemberDestroyOneKey(data);// 对一键注销成员时的处理
            this.setAjax(hintMessageData);
        }
        else
        {
            ImportBatchByDB(cycle);// 后台导入数据
        }
    }

    /**
     * 删除批次信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void delBatTrades(IRequestCycle cycle) throws Exception
    {

        String batchIds = getParameter("BATCH_IDS");

        IData data = new DataMap();

        data.put("BATCH_IDS", batchIds);

        CSViewCall.call(this, "CS.BatDealSVC.batTaskNowCancelRunForGrp", data);

        queryStartTaskInfo(cycle);// 初始化批量查询界面
    }

    public abstract IDataset getBatchOperTypes();

    /**
     * 集团成员一键注销 插批量表 只注销成员
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData grpMemberDestroyOneKey(IData inParam) throws Exception
    {
        IData retData = new DataMap();
        retData = CSViewCall.call(this, "CS.BatDestroyOneKeySvc.destroyMemberOneKeyByProc", inParam).getData(0);
        return retData;
    }

    /**
     * 集团成员一键注销 插批量表 注销成员同时注销集团
     * 
     * @param inParam
     * @return
     * @throws Exception
     */
    public IData grpUserMemberDestroyOneKey(IData inParam) throws Exception
    {
        IData retData = new DataMap();
        retData = CSViewCall.call(this, "CS.BatDestroyOneKeySvc.destroyUserMemberOneKeyByProc", inParam).getData(0);
        return retData;
    }

    /**
     * 页面提交后台导入数据
     * 
     * @param cycle
     * @throws Exception
     */
    public void ImportBatchByDB(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
       // System.out.print("myInfo1"+data);
        String batchOperType = data.getString("BATCH_OPER_CODE");
        data.put("BATCH_OPER_TYPE", batchOperType);
        data.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
        String grpBat = getParameter("GRP_BAT", "");// 导入方式：【0:文件导入 1：后台导入】
        String openBat = getParameter("OPEN_BAT", "");// M2M导入数据方式 【0:导入文件 1:导入号段】
        if (grpBat != null && !grpBat.equals("") && grpBat.equals("1") && batchOperType.equals("GROUPMEMCANCEL1111"))// 海南不用过程处理
        // 批量类型随便写个
        // add
        // by
        // phb
        {// 当操作类型为后台数据提取数据时 通过存储过程处理
            IData hintMessageData = grpMemberDestroyOneKey(data);
            this.setAjax(hintMessageData);
        }
        else
        {
            IData param = CSViewCall.call(this, "CS.BatDealSVC.createBatTask", data).getData(0); // 创建批量任务
            param.putAll(data);
            getData().put("IN_PARAM", param);// 将创建批量任务返回参数放入getData()中成为批量明细的入参
            if (grpBat != null && !grpBat.equals("") && "1".equals(grpBat))
            {// 后台导入批量
                importGrpByAuto(cycle);
            }
            else if (openBat != null && !openBat.equals("") && "1".equals(openBat))
            {// 通过导入号段方式导入 M2M批量导入 暂时未提供此功能
                // importOpenMebByAuto(cycle);
            }
        }
    }

    /**
     * 根据条件从后台数据库捞出数据创建批量信息
     * 
     * @param cycle
     * @throws Exception
     */
    private void importGrpByAuto(IRequestCycle cycle) throws Exception
    {
        String batOperType = getParameter("BATCH_OPER_CODE", "");
        String conStr = getParameter("CODING_STR", "");

        String esopTag = getParameter("EOS", "");// 标记是否为ESOP系统调入集团批量
        IData data = getData().getData("IN_PARAM");

        if (StringUtils.isNotEmpty(esopTag))
        {
            data.put("ESOP_TAG", "ESOP");
        }
        data.put("BATCH_OPER_TYPE", batOperType);

        IDataset dataset = new DatasetList();

        if (batOperType.equals("GROUPMEMCANCEL") || batOperType.equals("GRPCHANGEDISCOUNT")) // 集团成员批量退订或者批量优惠变更
        {
            dataset = insertAllUserInfo(this, conStr);
        }

        if (IDataUtil.isEmpty(dataset))// 没有成员直接告知前台页面
        {
            this.setAjax("error_message", "该集团用户下没有成员, 请检查");
            return;
        }

        /** 查询批量参数表 */
        IDataset idataset = CSViewCall.call(this, "CS.BatDealSVC.queryBatchTypeParamsEx", data);
        IData params = idataset.getData(0);

        int limit = params.getInt("LIMIT_NUM_BATCH"); // 导入条数限制
        int limit_day = params.getInt("LIMIT_NUM_DAY"); // 导入日条数限制

        int limit_month = params.getInt("LIMIT_NUM_MON"); // 导入月条数限制

        int priority = params.getInt("PRIORITY"); // 优先级

        String cancelable_flags = params.getString("CANCELABLE_FLAG"); // 可否返销标志

        data.put("PRIORITY", priority);
        data.put("CANCELABLE_FLAG", cancelable_flags);
        data.put("AUDIT_STATE", "0");

        String hint_message = "";
        String returnBatchId = data.getString("BATCH_TASK_ID");
        /** 导入条数上限控制 */
        if (dataset.size() > limit && limit != 0)
        {
            CSViewException.apperr(BatException.CRM_BAT_45, dataset.size(), limit);
        }

        // 没有日或者月限制则没必要去查询一次统计sql了
        if (limit_day > 0 || limit_month > 0)
        {
            idataset = CSViewCall.call(this, "CS.BatDealSVC.getNowDayCount", data);

            IData importedCount = idataset.getData(0);

            int day_count = Integer.parseInt(importedCount.getString("SUMS", "0"));
            int month_count = Integer.parseInt(importedCount.getString("MONTH_SUM", "0"));

            if (dataset.size() + day_count > limit_day && limit_day > 0)
            {
                CSViewException.apperr(BatException.CRM_BAT_34, dataset.size(), limit_day, day_count);
            }

            if (dataset.size() + month_count > limit_month && limit_month > 0)
            {
                hint_message = "导入条数过多：" + dataset.size() + ", <br/>" + "本月最大可导入条数为：" + limit_month + ", <br/>" + "本月已导入条数为：" + month_count + "! <br/>" + "导入的数据将需要审批! <br/>";
                data.put("AUDIT_STATE", "1");
                data.put("AUDIT_REMARK", hint_message);
            }
        }

        if (dataset.size() != 0)
        {
            IData idata = new DataMap();
            idata.put("DATA_SET", dataset);
            idata.put("IN_PARAM", data);
            idata.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
            CSViewCall.call(this, "CS.BatDealSVC.importData", idata);
        }

        this.setAjax("hint_message", hint_message + "批量业务流水号：" + returnBatchId);
    }

    /**
     * 初始化集团批量(查询)页面
     * 
     * @param cycle
     * @throws Exception
     */
    public void initalDeal(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();
        String start_date = SysDateMgr.getSysDate();
        String end_date = SysDateMgr.getTomorrowDate();
        cond.put("cond_START_DATE", start_date);
        cond.put("cond_END_DATE", end_date);
        cond.put("cond_CREATE_STAFF_ID", getVisit().getStaffId());
        cond.put("POP_cond_CREATE_STAFF_ID", getVisit().getStaffName());

        boolean grpbat4AFlag = BizEnv.getEnvBoolean("crm.grpbat.need4A", false); // 集团批量业务金库开关 false不进行4A校验,true则需要校验
        cond.put("GRPBAT4AFLAG", grpbat4AFlag);

        setCondition(cond);
        setBatchOperTypeInfo(cycle); // 初始化批量类型是拉框
        setOperType("0"); // 设置页面operType值：默认操作为启动操作

        setHintInfo("请输入查询条件！");
    }

    /**
     * 初始化批量业务新增模块
     * 
     * @param cycle
     * @throws Exception
     */
    public void initBatCreatePoup(IRequestCycle cycle) throws Exception
    {
        getData().put("initCreatePoup", "true");// 设置初始化新增模块标识为True
        setBatchOperTypeInfo(cycle); // 初始化批量类型是拉框
        IData data = new DataMap();
        data.put("SMS_FLAG", "0");// 设置短信发送标记为不发送

        data.put("INPUT_START_DATE", SysDateMgr.getSysDate());
        data.put("INPUT_END_DATE", SysDateMgr.getLastDateThisMonth4WEB());

        setInfo(data);

    }

    /**
     * 查询集团用户下所有成员
     * 
     * @version 1.0.0
     * @throws Throwable
     */
    public IDataset insertAllUserInfo(IBizCommon bc, String conStr) throws Exception
    {
        IData databiz = new DataMap(conStr);
        String groupId = databiz.getString("GROUP_ID");
        String productId = databiz.getString("PRODUCT_ID");
        String userIdA = databiz.getString("USER_ID");

        // 集团user_id没传 根据group_id获取
        if (StringUtils.isBlank(userIdA))
        {
            // 查询集团信息
            IData grpCustInfoData = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

            // 查询集团用户信息
            IDataset dataset = UCAInfoIntfViewUtil.qryGrpUserInfoByCustIdAndProId(this, grpCustInfoData.getString("CUST_ID"), productId, false);

            userIdA = dataset.getData(0).getString("USER_ID");
        }

        String relationTypeCode = ProductCompInfoIntfViewUtil.qryRelationTypeCodeStrByProductId(this, productId);

        // 查询该订购关系下所有成员
        IDataset memInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCodeAllCrm(this, userIdA, relationTypeCode);

        // 如果为空可能是ADC产品
        if (IDataUtil.isEmpty(memInfos))
        {
            memInfos = RelationBBInfoIntfViewUtil.qryRelaBBInfosByUserIdAAndRelationTypeCodeAllCrm(this, userIdA, relationTypeCode);
        }

        IDataset infos = new DatasetList();
        if (IDataUtil.isNotEmpty(memInfos))
        {
            for (int i = 0, size = memInfos.size(); i < size; i++)
            {
                IData data = new DataMap();
                data.put("SERIAL_NUMBER", memInfos.getData(i).getString("SERIAL_NUMBER_B"));
                infos.add(data);
            }
        }
        return infos;
    }

    /**
     * 根据业务类型初始化批量条件组件
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryComps(IRequestCycle cycle) throws Exception
    {
        String batchOperType = getParameter("BATCH_OPER_TYPE", "");
        /** 获取页面组件 */
        if (!batchOperType.equals(""))
        {
            IData inParam = new DataMap();
            inParam.put("BATCH_OPER_TYPE", batchOperType);

            IData comp = CSViewCall.callone(this, "CS.BatDealSVC.queryBatTypeByPK", inParam);

            comp.put("TEMPLATE_DATA_XLS", "template/bat/group/" + batchOperType + ".xls");
            comp.put("TEMPLATE_FORMART_XML", "import/bat/group/" + batchOperType + ".xml");
            comp.put("BATCH_OPER_TYPE", batchOperType);

            inParam.clear();
            inParam.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
            IData taskIdData = CSViewCall.call(this, "CS.SeqMgrSVC.getBatchId", inParam).getData(0);
            String batTaskId = taskIdData.getString("seq_id");

            comp.put("BATCH_TASK_ID", batTaskId);
            comp.put("TERM_IP", getVisit().getRemoteAddr());
            setComp(comp);
        }
    }

    /**
     * 批量业务列表查询
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryStartTaskInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData("cond", true);
        String operType = getParameter("operType", "");
        setOperType(operType);
        data.put("operType", operType);
        data.put("TRADE_ATTR", "2"); // 查询集团批量

        IDataset dataSet = new DatasetList();
        long tt = 0;
        if (data.getString("BATCH_OPER_TYPE").equals("GRPDESTROYONEKEY"))
        {// GRPDESTROYONEKEY,当操作类型为一键注销查询时 进行特殊处理
            data.put("BATCH_OPER_TYPE", "");
            data.put("DST_ONE_KEY_FLAG", "GRPDESTROYONEKEY");
            IDataOutput outPut1 = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatTrades", data, getPagination("PageNav"));
            dataSet = outPut1.getData();
            tt = outPut1.getDataCount();
        }
        else
        {
            IDataOutput outPut2 = CSViewCall.callPage(this, "CS.BatDealSVC.queryBatTrades", data, getPagination("PageNav"));
            dataSet = outPut2.getData();
            tt = outPut2.getDataCount();
        }

        setInfos(dataSet);
        setInfosCount(tt);

        setCondition(getData("cond", false));

        String hintInfo = "批量任务查询成功！";
        if (dataSet.size() == 0)
        {
            hintInfo = "未查询到符合条件的批量任务！";
        }
        setHintInfo(hintInfo);
    }

    /**
     * 获取批量类型下拉列表
     * 
     * @param cycle
     * @throws Exception
     */
    private void setBatchOperTypeInfo(IRequestCycle cycle) throws Exception
    {
        String initCreatePoup = (String) getData().getString("initCreatePoup", "false");

        IData inParam = new DataMap();
        inParam.put("TRADE_ATTR", "2");

        IDataset batchOpertypes = CSViewCall.call(this, "CS.BatDealSVC.queryBatchTypes", inParam);// 查批量类型表

        IDataset infos = new DatasetList();

        infos.addAll(batchOpertypes);

        if ("true".equals(initCreatePoup))// 批量新增页面过滤掉BBOSS下发业务,这些业务只需要查询功能
        {
            IDataset limit_batchtypes = StaticUtil.getStaticList("GROUP_BAT_LIMIT_BATCHTYPE"); // 配置限制批量前台办理的业务

            if (IDataUtil.isNotEmpty(limit_batchtypes))
            {

                int count = infos.size();
                for (int j = count - 1; j >= 0; j--)
                {
                    IData data = infos.getData(j);

                    for (int k = 0, size = limit_batchtypes.size(); k < size; k++)
                    {

                        if (StringUtils.equals(limit_batchtypes.getData(k).getString("DATA_ID"), data.getString("BATCH_OPER_TYPE")))
                        {
                            infos.remove(j);
                            break;
                        }
                    }
                }
            }
        }

        setBatchOperTypes(infos);
    }

    public abstract void setBatchOperTypes(IDataset set);

    public abstract void setComp(IData comp);

    public abstract void setCondition(IData cond);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset dataset);

    public abstract void setInfosCount(long count);

    public abstract void setOpenInfo(IData openInfo);

    public abstract void setOperType(String operType);

    /**
     * 启动批量业务信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void startBatDeals(IRequestCycle cycle) throws Exception
    {
        String batchIds = getParameter("BATCH_IDS");
        String[] batchIdSet = StringUtils.split(batchIds, ",");
        //有没有多选择一起启动，有则遍历
        for (int i = 0, size = batchIdSet.length; i < size; i++)
        {
            String batchId = batchIdSet[i];
            IData data = new DataMap();
            data.put("BATCH_ID", batchId);
            data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
            data.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
            IDataset batDataSet = CSViewCall.call(this, "CS.BatDealSVC.queryBatchType", data);
            IData batData = batDataSet.getData(0);
           
            //提交工号
            String staffId=batData.getString("STAFF_ID","0");
            
            //批量任务批次号
            String batchTaskId=batData.getString("BATCH_TASK_ID","0");
            //受理月份
            String acceptMonth=batData.getString("ACCEPT_MONTH","0");
            IData map = new DataMap();
            map.put("BATCH_TASK_ID",batchTaskId);
            map.put("ACCEPT_MONTH",acceptMonth);
            IDataset list = new DatasetList();
            IDataset list1 = new DatasetList();
            IDataset list2 = new DatasetList();
            IDataset list3 = new DatasetList();
            list=CSViewCall.call(this, "CS.BatDealSVC.queryTaskId", map);
            String operType=list.getData(0).getString("BATCH_OPER_CODE","");//获取处理类型
            //如果有文件上传则添加凭证信息参数 
            if(!list.getData(0).getString("CONDITION2","0").equals("0")){
            //获取集团客户编码
            JSONObject jsonObject = JSONObject.fromObject(list.getData(0).getString("CODING_STR1","0"));
            String groupId ="";
            String prodectId="";
            String operCode="";
            if(jsonObject.has("GROUP_ID")){
            	groupId= jsonObject.getString("GROUP_ID");
            }
            if(jsonObject.has("PRODUCT_ID")){
            	prodectId =jsonObject.getString("PRODUCT_ID");
            }
            //操作类型
            if(jsonObject.has("OPER_CODE")){
            	operCode=jsonObject.getString("OPER_CODE");
            }
            map.put("GROUP_ID",groupId);
            map.put("PRODUCT_ID",prodectId);
            //根据集团编码获取集团客户名称
            list1=CSViewCall.call(this, "CS.BatDealSVC.queryGroupName", map);
            String groupName="";
            if(list1.size()!=0){
            	 groupName=list1.getData(0).getString("CUST_NAME","");
            }
            
            
            //获取集团产品编码
            if(!prodectId.equals("") && !groupId.equals("")){
            	 list3=CSViewCall.call(this, "CS.BatDealSVC.queryGroupSn", map);
            }
            String tel="";
           if(list3.size()!=0){
        	   tel=list3.getData(0).getString("SERIAL_NUMBER","");
           }
            
            
            //获取交易类型编码
           String tradeTypeCode="";
          
           if(!operCode.equals("") && !prodectId.equals("")){
        	     if(operCode.equals("01")){//新增
        	    	  map.put("ATTR_OBJ","CrtMb");
        	     }else if(operCode.equals("02")){//退出
        	    	 map.put("ATTR_OBJ","DstMb");
        	     }else{//变更
        	    	 map.put("ATTR_OBJ","ChgMb");
        	     }
        	   map.put("ID",prodectId);
        	 
               list2=CSViewCall.call(this, "CS.BatDealSVC.queryTypeCode", map);
               if(list2.size()!=0){
            	   tradeTypeCode=list2.getData(0).getString("ATTR_VALUE","");
               }
               
           }
           //没有操作类型的，暂时这么做
           if(operType.equals("BATADDYDZFMEM")){
        	   tradeTypeCode="9990";
           }else if(operType.equals("BATADDHYYYKMEM")){
        	   tradeTypeCode="9991";
           }else if(operType.equals("BATMODBBOSSMEMBER") || operType.equals("BATADDBBOSSMEMBER")
        		   || operType.equals("BATPASBBOSSMEMBER") || operType.equals("BATCONBBOSSMEMBER") ||
        		   operType.equals("BATDELBBOSSMEMBER") ){
        	   map.put("BATCH_OPER_TYPE", operType);
        	   list2=CSViewCall.call(this, "CS.BatDealSVC.queryTypeCodeByType", map);
        	   if(list2.size()!=0){
            	   tradeTypeCode=list2.getData(0).getString("TRADE_TYPE_CODE","");
               }
        	   
           }
            	 
               IData param = new DataMap();
   			   param.put("AUDIT_ID", batchId);		      //批量业务的批次号或业务流水号trade_id
   			   param.put("ACCEPT_MONTH", acceptMonth);	 //受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
   			   param.put("BIZ_TYPE", "2");			     //业务工单类型：1-单条，2-批量业务
   			   param.put("TRADE_TYPE_CODE",tradeTypeCode.equals("")?"9998":tradeTypeCode);	     //业务类型编码：见参数表TD_S_TRADETYPE
   			   param.put("GROUP_ID", groupId);		         //集团客户编码
   			   param.put("CUST_NAME", groupName);	         //集团客户名称
   			   param.put("GRP_SN", tel);				 //集团产品编码
   			   param.put("CONTRACT_ID", "");													//合同编号
   			   param.put("VOUCHER_FILE_LIST", list.getData(0).getString("CONDITION2","0"));		//凭证信息上传文件ID
   			   param.put("ADD_DISCNTS", "");											//新增优惠
   			   param.put("DEL_DISCNTS", "");											//删除优惠
   			   param.put("MOD_DISCNTS", "");											//变更优惠
   			   param.put("STATE", "0");															//稽核单状态:0-初始，1-稽核通过，2-稽核不通过
   			   param.put("IN_DATE", SysDateMgr.getSysTime());									//提交时间
   			   param.put("IN_STAFF_ID", staffId);						//提交工号
   			   param.put("AUDIT_STAFF_ID", list.getData(0).getString("CONDITION1","0"));//稽核人工号
   			   //生成集团业务稽核工单
   			   CSViewCall.call(this, "CS.BatDealSVC.insertOrder", param);
   			
                
            }
           
           
            
            if (IDataUtil.isEmpty(batData))
            {
                CSViewException.apperr(BatException.CRM_BAT_48, batchId);
            }
            String batch_oper_type = batData.getString("BATCH_OPER_TYPE", "");
            data.put("BATCH_OPER_TYPE", batch_oper_type);
            String showInfo = "批量数据启动成功!";
            if (batch_oper_type.equals("BATADDBBOSSMEMBER") || batch_oper_type.equals("BATDELBBOSSMEMBER") || batch_oper_type.equals("BATCONBBOSSMEMBER") || batch_oper_type.equals("BATPASBBOSSMEMBER") || batch_oper_type.equals("BATMODBBOSSMEMBER"))
            {// BOSS成员批量操作
                CSViewCall.call(this, "SS.BatDealBBossBeanSvc.startBatDealBBossMember", data);
            }
            else if (batch_oper_type.equals("BATADDYDZFMEM") || batch_oper_type.equals("BATCONFIRMYDZFMEM") || batch_oper_type.equals("BATOPENYDZFMEM"))
            {// BBOSS一点支付
                IDataset fileNameList = CSViewCall.call(this, "SS.BatDealBBossBeanSvc.startBBossYDZFBatDeals", data);
                showInfo = "成员附件已生成，请保存附件名【" + fileNameList.get(0).toString() + "】!";
            }
            else if (batch_oper_type.equals("BATADDHYYYKMEM") || batch_oper_type.equals("BATOPENHYYYKMEM"))
            {// BBOSS行业应用卡
                IDataset fileNameList = CSViewCall.call(this, "SS.BatDealBBossBeanSvc.startBBossHYYYKBatDeals", data);
                showInfo = "成员附件已生成，请保存附件名【" + fileNameList.get(0).toString() + "】!";
            }
            else if (batch_oper_type.equals("BATMEBCENPAY"))
            {// 集团流量统付成员订购流量包
                IDataset fileNameList = CSViewCall.call(this, "SS.BatDealBBossBeanSvc.startBBossMEBCENPAYBatDeals", data);
                showInfo = "成员附件已生成，请保存附件名【" + fileNameList.get(0).toString() + "】!";
            }
            else
            {
                boolean nowRunFlag = BizEnv.getEnvBoolean("crm.bat.nowrun", false); // 批量业务是否立即启动开关 true表示立即执行

                if (!nowRunFlag)
                {
                    showInfo = "批量数据启动成功! 批量执行时间为当天20:00以后!";
                }

                CSViewCall.call(this, "CS.BatDealSVC.batTaskNowRunForGrp", data);
            }
            IData idata = new DataMap();
            idata.put("result", showInfo);
            setAjax(idata);
        }

        queryStartTaskInfo(cycle);// 初始化批量查询页面
    }
    
    /**
     * REQ201801150022_新增IMS号码开户人像比对功能
     * <br/>
     * 获取电子工单内容
     * @param cycle
     * @throws Exception
     */
    public void queryBuildBatchBillInfo(IRequestCycle cycle) throws Exception
    {
        String batchTaskId=getParameter("BATCH_TASK_ID", "");
        
        IData data=new DataMap();
        	  data.put("BATCH_TASK_ID", batchTaskId);
        	  
        //获取打印信息
        IData  returnInfo=CSViewCall.call(this, "CS.BatDealSVC.qryBatTaskByBatchTaskId", data).getData(0);
        
		// 受理业务时间
		String acceptDate = SysDateMgr.getSysTime();

		returnInfo.put("TRADE_STAFF_ID", getVisit().getStaffId());
		returnInfo.put("ACCEPT_DATE", acceptDate);// 业务受理时间
		returnInfo.put("TRADE_STAFF_NAME", getVisit().getStaffName());
		
		if(("BATOPENGROUPMEM").equals(returnInfo.getString("BATCH_OPER_CODE", ""))){
			returnInfo.put("TRADE_TYPE_NAME", "IMS固话批量开户");
		}
		setAjax(returnInfo);
    }   
    
    /*
     * REQ201807240010++新增批量开户界面人像比对、受理单打印优化需求 by mqx 20190117
     * 记录信息到tf_b_trade_cnote_info表
     */
    public void insertIntoTradeCnoteInfoBat(IRequestCycle cycle) throws Exception{
        
    	String batchTaskId=getParameter("BATCH_TASK_ID", "");
    	String batchOperType=getParameter("BATCH_OPER_TYPE", "");

    	
    	IData data=new DataMap();
  	  	data.put("BATCH_TASK_ID", batchTaskId);
  	  	data.put("BATCH_OPER_TYPE", batchOperType);
  	  	System.out.println("==mqx=GrpBatTradeDeal==insertIntoTradeCnoteInfoBat==data="+data);
    	
    	//记录信息到tf_b_trade_cnote_info表
        CSViewCall.call(this, "CS.BatDealSVC.insertIntoTradeCnoteInfoBat", data);

    	
    }
    
    /**
     * 判断批量是否已经打印
     * @param cycle
     * @throws Exception
     * @author mengqx
     * @date 20180724
     */
    public void chexkBatTask(IRequestCycle cycle) throws Exception {
    	String batchIds = getParameter("BATCH_IDS");
        String[] batchIdSet = StringUtils.split(batchIds, ",");
        for (int i = 0, size = batchIdSet.length; i < size; i++)
        {
            String batchId = batchIdSet[i];
            IData data = new DataMap();
            data.put("BATCH_ID", batchId);
            data.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(batchId));
            data.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());
            IDataset batDataSet = CSViewCall.call(this, "CS.BatDealSVC.queryBatchType", data);
            IData batData = batDataSet.getData(0);
            if (IDataUtil.isEmpty(batData))
            {
                CSViewException.apperr(BatException.CRM_BAT_48, batchId);
            }
            String batch_oper_type = batData.getString("BATCH_OPER_TYPE", "");
            if (batch_oper_type.equals("BATOPENGROUPMEM"))
            {
            	IData result=CSViewCall.callone(this, "CS.BatDealSVC.checkBatTask", data);
                setAjax(result);
            }

        }
    }
    
    /**
     * REQ201904260020新增物联网批量开户界面权限控制需求
     * 免人像比对权限判断
     * @author mengqx
     * @date 20190515
     * @param clcle
     * @throws Exception
     */
    public void isBatCmpPic(IRequestCycle clcle) throws Exception
    {
    	IData data = getData();
    	IData param = new DataMap();
    	
    	param.putAll(data);
    	param.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	IDataset dataset = CSViewCall.call(this, "SS.CreatePersonUserSVC.isBatCmpPic", param);
    	setAjax(dataset.getData(0));
    }
}
