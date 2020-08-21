
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaXxtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpMebPlatSvcInfoQry;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.Iterator;

//extends GroupOrderService
public class BatHeEduForSchoolsChangeTrans implements ITrans {
    private static Logger logger = Logger.getLogger(BatHeEduForSchoolsChangeTrans.class);

    @Override
    public void transRequestData(IData batData) throws Exception {
        // 初始化数据


        System.out.println("BatHeEduForSchoolsChangeTrans-svcData" + batData);
        // 校验请求参数
        checkRequestDataSub(batData);
        // 构建服务请求数据
        builderSvcData(batData);
        // 根据条件判断调用服务
        setSVC(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception {
        JSONObject jsonObject = JSONObject.fromObject(batData.getString("CONDITION4"));
        IData users = UcaInfoQry.qryUserInfoBySn(batData.getString("SERIAL_NUMBER"));
        IDataset relatinonxxtInfos = RelaXxtInfoQry.qryMemInfoByECANDUserIdA(batData.getData("condData", new DataMap()).getString("USER_ID"), users.getString("USER_ID", ""), "915001");
        IDataset qryMebPlatSvcInfos = UserGrpMebPlatSvcInfoQry.getMemPlatSvcByecUserIdServiceId(jsonObject.getString("USER_ID"),"915001");
        if (IDataUtil.isEmpty(relatinonxxtInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_919, jsonObject, batData.getString("SERIAL_NUMBER"), jsonObject.getString("GROUP_ID"));
        }

        if (IDataUtil.isEmpty(qryMebPlatSvcInfos))
        {
            CSAppException.apperr(GrpException.CRM_GRP_920, jsonObject, batData.getString("SERIAL_NUMBER"), jsonObject.getString("GROUP_ID"));
        }

    }


    protected void builderSvcData(IData batData) throws Exception {

        IData svcData = batData.getData("svcData", new DataMap());
        svcData.put("condDataA", batData.getData("condData", new DataMap()));
        JSONObject jsonObject = JSONObject.fromObject(batData.getString("CONDITION4"));
        svcData.put("condDataB",getDataMapFromJson(jsonObject.toString()) );//数据B
        svcData.put("PRODUCT_ID", jsonObject.getString("PRODUCT_ID"));//产品id
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));//产品id
        svcData.put("ACTIVE_TIME", batData.getString("ACTIVE_TIME"));//产品id
        svcData.put("SELECT_TYPE", "1");//设置一个order_id两个trade_id对应类型
        IData users = UcaInfoQry.qryUserInfoBySn(batData.getString("SERIAL_NUMBER"));
        svcData.put("USER_ID", users.getString("USER_ID", ""));//用户id
        svcData.put("REAL_SVC_NAME", "SS.HeEduForSchoolsChangeSVC.tradeReg");//用户id
        System.out.println("BatHeEduForSchoolsChangeTrans-svcData" + svcData);



    }

    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception {
        String svcName = "";
        IData svcData = batData.getData("svcData", new DataMap());
        svcName = "SS.HeEduForSchoolsChangeSVC.tradeReg";
        svcData.put("REAL_SVC_NAME", svcName);

    }

    public IData getDataMapFromJson(String jsonString) {
        JSONObject jsonObject = JSONObject.fromObject(jsonString);
        IData map = new DataMap();
        for (Iterator<?> iter = jsonObject.keys(); iter.hasNext();) {
            String key = (String) iter.next();
            String value=jsonObject.getString(key);
            if(org.apache.commons.lang.StringUtils.isNotBlank(value)&&!"null".equalsIgnoreCase(value))
            {
                map.put(key, jsonObject.getString(key));
            }
        }
        return map;
    }

}
