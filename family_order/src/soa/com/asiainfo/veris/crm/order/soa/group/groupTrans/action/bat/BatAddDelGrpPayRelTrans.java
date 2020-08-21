
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatAddDelGrpPayRelTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);
        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IDataUtil.chkParam(condData, "MARK_FLAG");			//处理类型标识
        IDataUtil.chkParam(batData, "SERIAL_NUMBER");		//集团产品编码
        IDataUtil.chkParam(batData, "DATA1");				//截止时间
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());
        
        String serialNumber = batData.getString("SERIAL_NUMBER");
        //查询集团产品用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serialNumber);
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }
        svcData.put("MARK_FLAG", condData.getString("MARK_FLAG"));	//
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("USER_ID", userinfo.getString("USER_ID"));
        svcData.put("START_DATES", SysDateMgr.getSysTime());
        svcData.put("END_DATES", batData.getString("DATA1")+" 23:59:59");		//截止时间
        svcData.put("REMARK_OTHER", "批量办理集团产品欠费不截止代付关系");

    }

}
