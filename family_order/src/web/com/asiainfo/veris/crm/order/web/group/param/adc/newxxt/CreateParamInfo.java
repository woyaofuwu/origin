
package com.asiainfo.veris.crm.order.web.group.param.adc.newxxt;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationxxtinfo.RelationXXTInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.discntinfo.DiscntInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.group.param.GroupParamPage;

public abstract class CreateParamInfo extends GroupParamPage
{

    public CreateParamInfo()
    {
        super();
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

        IData map = null;
        for (int i = 0, iSize = discntSet.size(); i < iSize; i++)
        {// 这里查询的目的用于动态表格显示资费名称
            map = discntSet.getData(i);
            String elementId = map.getString("ELEMENT_ID");
            IData discntinfo = DiscntInfoIntfViewUtil.qryDiscntInfoByDisCode(this, elementId, true);
            map.put("ELEMENT_NAME", discntinfo.getString("DISCNT_NAME", ""));
        }
        // 保存传入的资费
        setStuDiscntAll(discntSet);

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

        String studKey = data.getString("STUD_KEY", "");
        String discntStr = data.getString("DISCNTSET", "");

        IDataset discntSet = new DatasetList(discntStr);

        IData map = null;

        for (int i = 0, iSize = discntSet.size(); i < iSize; i++)
        {
            map = discntSet.getData(i);
            String elementId = map.getString("ELEMENT_ID");
            String modfiyTag = map.getString("MODIFY_TAG");
            if ("1".equals(modfiyTag) || "0_1".equals(modfiyTag))// 过滤无效的资费不显示
            {
                continue;
            }
            IData discntinfo = DiscntInfoIntfViewUtil.qryDiscntInfoByDisCode(this, elementId, true);
            String flag = discntinfo.getString("RSRV_STR2", "");
            map.put("ELEMENT_NAME", discntinfo.getString("DISCNT_NAME", ""));

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
        setAjax(results);
    }

    public void checkMaxstudent(IRequestCycle cycle) throws Exception {

        IData data = getData();

        String notinSerialNumber = data.getString("NOTIN_SERIAL_NUMBER", "");
        String notinOutSn = data.getString("NOTIN_OUT_SN", "");
        String ec_user_id = data.getString("GRP_USER_ID");

        IDataset results = RelationXXTInfoIntfViewUtil.queryXxtInfoBySnaGroup(this, notinSerialNumber, notinOutSn, ec_user_id);

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
       
     // IDataset result = CSViewCall.call(this, "CS.BBossTaskSVC.callIBOSS", httpStr);
       
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
    //   }
        
        this.setAjax(data2);
    }

    public abstract void setStudentListInfos(IDataset temp);

    public abstract void setInfo(IData info);

    public abstract void setStuParamkey(String key);

    public abstract void setMoinfoDetail(IData temp);

    public abstract void setStuDiscntAll(IDataset temp);

    public abstract void setStuDiscnt1(IDataset temp);

    public abstract void setStuDiscnt2(IDataset temp);

    public abstract void setStuDiscnt3(IDataset temp);

    public abstract void setHiddenParam(IData hiddenParam);

}
