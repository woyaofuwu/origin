package com.asiainfo.veris.crm.iorder.web.igroup.param.xxt;

import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.discntinfo.DiscntInfoIntfViewUtil;

public abstract class CreateMemParamInfo   extends CSBasePage {
	
	private static final Logger logging = Logger.getLogger(CreateMemParamInfo.class);
	
	public abstract void setCond(IData cond);

	public abstract void setBusi(IData busi);
	
	public abstract void setCondition(IData condition);
	
	public abstract void setOperTypes(IDataset dataset);

	public abstract void setStuNames(IDataset dataset);


	public abstract void setInfo(IData info);

	public abstract void setInfos(IDataset dataset);
	
	public void initial(IRequestCycle cycle) throws Throwable {
		IData data = getData();
		String operType = data.getString("OPER_TYPE");
		setCond(data);
		if (EcConstants.FLOW_ID_MEMBER_CREATE.equals(operType)) {
			initCrtMb(cycle);
		}
		if (EcConstants.FLOW_ID_MEMBER_CHANGE.equals(operType)) {
			initChgMb(cycle);
		}
		
	}
    public void initCrtMb(IRequestCycle cycle) throws Throwable
    {
    	IData data=getData();
        IData parainfo = new DataMap();
        IDataset snList = new DatasetList();
        IData snData = new DataMap();

        IDataset operTypes = new DatasetList();
        IData opertype1 = new DataMap();
        opertype1.put("DATA_ID", "0");
        opertype1.put("DATA_NAME", "新增");
        operTypes.add(opertype1);

        snData.put("OPER_TYPES", operTypes);
        snList.add(snData);

        parainfo.put("SNLIST", snList);
        parainfo.put("pam_NOTIN_SERIAL_NUMBER", data.getString("MEB_SERIAL_NUMBER"));
        parainfo.put("pam_GRP_USER_ID", data.getString("EC_USER_ID"));
        parainfo.put("pam_GROUP_ID", data.getString("GROUP_ID"));
        setInfo(parainfo);
      
       
    }

    public void initChgMb(IRequestCycle cycle) throws Throwable
    {
    	IData data=getData();
        IData parainfo = new DataMap();
        IDataset snList = new DatasetList();

        IDataset operTypes1 = new DatasetList();

        IData snData = new DataMap();

        IData opertype1 = new DataMap();
        IData opertype2 = new DataMap();
        IData opertype3 = new DataMap();

        IData temp = null;
        IData map = null;

        IDataset stuParmLis = null;
        String mem_serialNumber = data.getString("MEB_SERIAL_NUMBER");
        String ec_user_id = data.getString("EC_USER_ID");
        String type = "D";

        opertype1.put("DATA_ID", "0");
        opertype1.put("DATA_NAME", "新增");
        operTypes1.add(opertype1);
        snData.put("OPER_TYPES", operTypes1);

        
        //重写PXXT表数据获取
        //IData mainSnXXTInfo = RelaXxtInfoQry.qryMemInfoBySNForUIPDestroy(mem_serialNumber, ec_user_id);
        //查询同一集团 同一个付费号下所有代付号码
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", mem_serialNumber);
        inparam.put("USER_ID_A", ec_user_id);
        IDataset mainSnXXTInfos = CSViewCall.call(this, "CS.RelaXXTInfoQrySVC.qryMemInfoBySNForUIPDestroy", inparam);
        for (int i = 0; i < mainSnXXTInfos.size(); i++) {
        	IDataset resultParmLis = new DatasetList();
            IDataset operTypes2 = new DatasetList();
            IData snParam = new DataMap();

            temp = mainSnXXTInfos.getData(i);
            String outSN = temp.getString("SERIAL_NUMBER_B");
            if (StringUtils.isNotBlank(outSN))
            {
                // 用代付号码设置操作类型
                opertype2.put("DATA_ID", "2");
                opertype2.put("DATA_NAME", "变更");

                opertype3.put("DATA_ID", "1");
                opertype3.put("DATA_NAME", "注销");

                operTypes2.add(opertype2);
                operTypes2.add(opertype3);
            }
            
            IData sutParam = new DataMap();
            sutParam.put("STUD_NAME", temp.getString("NAME", ""));// 学生姓名
            sutParam.put("STUD_KEY", temp.getString("RSRV_STR1", ""));// 学生参数
            sutParam.put("ELEMENT_ID", temp.getString("ELEMENT_ID", ""));// 学生绑定的优惠
            resultParmLis.add(sutParam);
            snParam.put("pam_NOTIN_STU_PARAM_LIST", resultParmLis);
            snParam.put("pam_NOTIN_OUT_SN", outSN);
            snParam.put("OPER_TYPES", operTypes2);
            snList.add(snParam);
            
		}
        
        /*
        // 查询同一集团 同一个付费号下所有代付号码
        IDataset outMemDataset = RelationXXTInfoIntfViewUtil.queryMemInfoBySNandUserIdA(this,mem_serialNumber, ec_user_id);

        for (int i = 0, outSize = outMemDataset.size(); i < outSize; i++)
        {
            IDataset resultParmLis = new DatasetList();
            IDataset operTypes2 = new DatasetList();
            IData snParam = new DataMap();

            temp = outMemDataset.getData(i);
            String outSN = temp.getString("SERIAL_NUMBER_B");
            if (StringUtils.isNotBlank(outSN))
            {
                // 用代付号码设置操作类型
                opertype2.put("DATA_ID", "2");
                opertype2.put("DATA_NAME", "变更");

                opertype3.put("DATA_ID", "1");
                opertype3.put("DATA_NAME", "注销");

                operTypes2.add(opertype2);
                operTypes2.add(opertype3);
                // 分别查询校讯通每个代付号下的学生参数信息
               stuParmLis = RelationXXTInfoIntfViewUtil.qryStuDisParamInfoBySnAUserIdASnBDsiType(this, mem_serialNumber, ec_user_id, outSN, type);
            }
            if (IDataUtil.isNotEmpty(stuParmLis))
            {
                for (int j = 0, jSize = stuParmLis.size(); j < jSize; j++)
                {// 转换为动态表格识别的数据
                    // {"STUD_VALUE":"ll","STUD_KEY":"stu_name1","ELEMENT_ID":"4320","tag":"0"}]
                    IData sutParam = new DataMap();
                    map = stuParmLis.getData(j);
                    sutParam.put("STUD_NAME", map.getString("NAME", ""));// 学生姓名
                    sutParam.put("STUD_KEY", map.getString("RSRV_STR1", ""));// 学生参数
                    sutParam.put("ELEMENT_ID", map.getString("ELEMENT_ID", ""));// 学生绑定的优惠
                    resultParmLis.add(sutParam);
                }

                snParam.put("pam_NOTIN_STU_PARAM_LIST", resultParmLis);
                snParam.put("pam_NOTIN_OUT_SN", outSN);
                snParam.put("OPER_TYPES", operTypes2);
                snList.add(snParam);
            }
        }*/
        if (snList.size() < 6) {
            snList.add(snData);
        }
        parainfo.put("SNLIST", snList);
        parainfo.put("pam_NOTIN_SERIAL_NUMBER", data.getString("MEB_SERIAL_NUMBER"));
        parainfo.put("pam_GRP_USER_ID", ec_user_id);
        parainfo.put("pam_GROUP_ID", data.getString("GROUP_ID"));

        setInfo(parainfo);
    }

    /**
     * 校验1个计费号码最多绑定3个家长服务号码（短信接收号码）
     */
    public void checkMaxNotinOutSn(IRequestCycle cycle) throws Throwable {
        IData result = new DataMap();
        IData results = new DataMap();
        String notinOutSn = getData().getString("NOTIN_OUT_SN", "");

        IDataset resultSet = RelationXXTInfoIntf.qryMemInfoBySNandUserIdA(this, notinOutSn);

        String flag = "false";
        if (resultSet.size() < 6) {
            flag = "true";
        }

        result.put("IS_FLAG", flag);
        this.setAjax(result);
    }

    public void checkMobileNumber(IRequestCycle cycle) throws Throwable{
     

        String serialnumber = getData().getString("SERIAL_NUMBER");

        IDataset resultSet = RelationXXTInfoIntf.qrymsisdn(this, serialnumber);
        IData result = new DataMap();
        String flag = "false";
        if (resultSet.size() > 0) {
            flag = "true";
        }

        result.put("IS_FLAG", flag);
        this.setAjax(result);
    }

    /*
     * 初始服务新增 参数界面
     */
    public void initParamInfo(IRequestCycle cycle) throws Throwable
    {
        IData data = getData();
        String discntStr = data.getString("DISCNTSET", "");
        String ec_user_id = data.getString("GRP_USER_ID");
        String ec_group_id=data.getString("GROUP_ID");

        IDataset discntSet = null;
        if (StringUtils.isNotBlank(discntStr))
        {
            discntSet = new DatasetList(discntStr);
        }
      
        // 保存传入的资费
        setStuDiscntAll(discntSet);
        
        IDataset stuDataLiData=StaticUtil.getStaticList("TD_STU_NAME");
        setStuNames(stuDataLiData);

        // 点击弹出框 加载学生参数信息
        String stuParamkey = data.getString("stuParamkey", "");
        setStuParamkey(stuParamkey);// 保存父页面的键pam_STU_PARAM_LIST+i
      
        String notinSerialNumber = data.getString("NOTIN_SERIAL_NUMBER", "");
        String notinOutSn = data.getString("NOTIN_OUT_SN", "");
        IData hiddenParam = new DataMap();
        hiddenParam.put("NOTIN_SERIAL_NUMBER", notinSerialNumber);
        hiddenParam.put("NOTIN_OUT_SN", notinOutSn);
        hiddenParam.put("GRP_USER_ID", ec_user_id);
        hiddenParam.put("GROUP_ID", ec_group_id);
        setHiddenParam(hiddenParam);

      
    }

    public void inserStuParamByajax(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String stuParam = data.getString("STUDENTPARAM", "");
        IDataset dataset = null;
        if (StringUtils.isNotBlank(stuParam))
        {
            dataset = new DatasetList(stuParam);
        }
        setStudentListInfos(dataset);
    }

    public void getStuDiscntByajax(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IDataset results = new DatasetList();

        IDataset stuDiscnt1 = new DatasetList();
        IDataset stuDiscnt2 = new DatasetList();
        IDataset stuDiscnt3 = new DatasetList();
        //新增到学生六
        IDataset stuDiscnt4 = new DatasetList();
        IDataset stuDiscnt5 = new DatasetList();
        IDataset stuDiscnt6 = new DatasetList();

        String studKey = data.getString("STUD_KEY", "");
        String discntStr = data.getString("DISCNTSET", "");

        IDataset discntSet = new DatasetList(discntStr);

        IData map = null;

        for (int i = 0, iSize = discntSet.size(); i < iSize; i++)
        {
            map = discntSet.getData(i);
            String elementId = map.getString("OFFER_CODE");
            String modfiyTag = map.getString("OPER_CODE");
            if ("1".equals(modfiyTag) || "0_1".equals(modfiyTag))// 过滤无效的资费不显示
            {
                continue;
            }
            IData discntinfo = DiscntInfoIntfViewUtil.qryDiscntInfoByDisCode(this, elementId, true);
            String flag = discntinfo.getString("RSRV_STR2", "");
            map.put("OFFER_NAME", discntinfo.getString("DISCNT_NAME", ""));

            // 如果discnt表预留字段RSRV_STR2为空或以group_1开头，则表示此优惠学生一订购
            if (StringUtils.isBlank(flag) || flag.startsWith("group_1"))
            {
                stuDiscnt1.add(map);// 保存学生一可订购的优惠
            }
            if (StringUtils.isBlank(flag) || flag.startsWith("group_2"))
            {
                stuDiscnt2.add(map);
            }
            if (StringUtils.isBlank(flag) || flag.startsWith("group_3"))
            {
                stuDiscnt3.add(map);
            }
            //新增到学生六
            if (StringUtils.isBlank(flag) || flag.startsWith("group_4"))
            {
                stuDiscnt4.add(map);
            }
            if (StringUtils.isBlank(flag) || flag.startsWith("group_5"))
            {
                stuDiscnt5.add(map);
            }
            if (StringUtils.isBlank(flag) || flag.startsWith("group_6"))
            {
                stuDiscnt6.add(map);
            }
        }
        if ("stu_name1".equals(studKey))
        {
            results.addAll(stuDiscnt1);
        }
        if ("stu_name2".equals(studKey))
        {
            results.addAll(stuDiscnt2);
        }
        if ("stu_name3".equals(studKey))
        {
            results.addAll(stuDiscnt3);
        }
        //新增到学生六
        if ("stu_name4".equals(studKey))
        {
            results.addAll(stuDiscnt4);
        }
        if ("stu_name5".equals(studKey))
        {
            results.addAll(stuDiscnt5);
        }
        if ("stu_name6".equals(studKey))
        {
            results.addAll(stuDiscnt6);
        }
        setAjax(results);
    }

    public void checkMaxstudent(IRequestCycle cycle) throws Exception {

        IData data = getData();

        String notinSerialNumber = data.getString("NOTIN_SERIAL_NUMBER", "");
        String ec_user_id = data.getString("GRP_USER_ID");
        //String notinOutSn = data.getString("NOTIN_OUT_SN", "");
        //IDataset results = RelationXXTInfoIntfViewUtil.queryXxtInfoBySnaGroup(this, notinSerialNumber, notinOutSn, ec_user_id);
        
        //根据计费号码查询是否有订购学生套餐记录
        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER", notinSerialNumber);
        inparam.put("SERIAL_NUMBER_A", notinSerialNumber);
        inparam.put("EC_USER_ID", ec_user_id);
        IDataset results = CSViewCall.call(this, "CS.RelaXXTInfoQrySVC.queryXxtInfoBySnGroup", inparam);
        setAjax(results);
    }
    
    //验证学生和家长信息在校讯通平台是否存在
    public void checkXXTPlatExists(IRequestCycle cycle) throws Exception {

        IData data = getData();
        String notinSerialNumber = data.getString("NOTIN_SERIAL_NUMBER");//付费号码
        String notinOutSn = data.getString("NOTIN_OUT_SN", "");//家长号码
        String opertype = data.getString("OPER_TYPE"); //操作类型
        String grpUserId=data.getString("GRP_USER_ID");
        String stuName=data.getString("STU_NAME");
        String groupId=data.getString("GROUP_ID");
        
       IData data2 = new DataMap();
       IData httpStr = new DataMap();
       httpStr.put("EC_ID", groupId);
       httpStr.put("MOB_NUM", notinOutSn);
       httpStr.put("FEE_MOB_NUM", notinSerialNumber);
       httpStr.put("STU_NAME", stuName);
       httpStr.put("OPR_CODE", opertype);
       httpStr.put("KIND_ID", "BIPXXT03_TX000002_0_0");
       
//       IDataset result = CSViewCall.call(this, "CS.BBossTaskSVC.callIBOSS", httpStr);
//       logging.info("test_guonj_BatNewXxtUserTrans3_result="+stuName+notinOutSn+"."+result);
//       if (IDataUtil.isEmpty(result))
//       {
//           // 执行失败
//           //data2.putAll(result.getData(0));
//           data2.put("FLAG", "false");
//           data2.put("X_RESULTCODE", "99");
//           data2.put("X_RESULTINFO", "平台校验返回失败！");
//           
//       }
//       else if (!("00".equals(result.getData(0).getString("X_RESULTCODE", ""))))
//       {
//           // 执行失败
//           data2.put("FLAG", "false");
//           data2.put("X_RESULTCODE", result.getData(0).getString("X_RESULTCODE", ""));
//           data2.put("X_RESULTINFO", result.getData(0).getString("X_RESULTINFO", ""));
//           
//       }
//       else
//       {
//           data2.put("X_RESULTCODE", result.getData(0).getString("X_RESULTCODE", ""));
//           data2.put("X_RESULTINFO", result.getData(0).getString("X_RESULTINFO", ""));
           data2.put("FLAG", "true");
//       }
        
        this.setAjax(data2);
    }
    
    public abstract void setStudentListInfos(IDataset temp);

    public abstract void setStuParamkey(String key);

    public abstract void setMoinfoDetail(IData temp);

    public abstract void setStuDiscntAll(IDataset temp);

    public abstract void setStuDiscnt1(IDataset temp);

    public abstract void setStuDiscnt2(IDataset temp);

    public abstract void setStuDiscnt3(IDataset temp);

    public abstract void setHiddenParam(IData hiddenParam);

}
