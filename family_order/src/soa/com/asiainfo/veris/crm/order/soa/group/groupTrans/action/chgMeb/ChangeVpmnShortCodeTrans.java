
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.chgMeb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ResException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;

public class ChangeVpmnShortCodeTrans implements ITrans
{

    @Override
    public void transRequestData(IData iData) throws Exception
    {
        checkRequestData(iData);

        String oldShortCode = iData.getString("PARA_CODE3");
        String newShortCode = iData.getString("SHORT_CODE");

        String epachyCode = iData.getString("EPARCHY_CODE");

        String sn = iData.getString("SERIAL_NUMBER");
        String memUserId = iData.getString("MEM_USER_ID");

        String userId = iData.getString("USER_ID");

        iData.put("USER_ID", userId);
        iData.put("SERIAL_NUMBER", sn);

        iData.put("MEM_USER_ID", memUserId);

        iData.put("PRODUCT_ID", "8000");

        IDataset productParam = new DatasetList();// 产品参数
        IData proInfo = new DataMap();
        IDataset proarams = new DatasetList();
        IData paramInfo = new DataMap();
        paramInfo.put("ATTR_VALUE", newShortCode);
        paramInfo.put("ATTR_CODE", "SHORT_CODE");
        proarams.add(paramInfo);
        IData paramInfo2 = new DataMap();
        paramInfo2.put("ATTR_VALUE", oldShortCode);
        paramInfo2.put("ATTR_CODE", "OLD_SHORT_CODE");
        proarams.add(paramInfo2);
        proInfo.put("PRODUCT_PARAM", proarams);
        proInfo.put("PRODUCT_ID", "8000");
        productParam.add(proInfo); // 产品参数

        IDataset resinfos = new DatasetList(); // 资源信息
        IData inparams = new DataMap();
        inparams.put("USER_ID", memUserId);// 成员USER_ID
        inparams.put("USER_ID_A", userId);// 用户USER_ID
        inparams.put("RES_TYPE_CODE", "S");
        inparams.put("RES_CODE", oldShortCode);
        inparams.put(Route.ROUTE_EPARCHY_CODE, epachyCode);
        IDataset mebress = CSAppCall.call("CS.UserResInfoQrySVC.getResByUserIdResType", inparams);

        if (IDataUtil.isEmpty(mebress))
        {
            CSAppException.apperr(ResException.CRM_RES_84);

        }

        IData mebres = mebress.getData(0);
        mebres.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue()); // 状态属性：0-增加，1-删除，2-变更
        mebres.put("END_DATE", SysDateMgr.getSysTime());
        resinfos.add(mebres);

        if (!"".equals(newShortCode))
        {
            IData addres = new DataMap();
            addres.put("MODIFY_TAG", "0");
            addres.put("RES_TYPE_CODE", "S");
            addres.put("RES_CODE", newShortCode);
            resinfos.add(addres);
        }

        iData.put("RES_INFO", resinfos);

        if (IDataUtil.isNotEmpty(productParam))
            iData.put("PRODUCT_PARAM_INFO", productParam);
        iData.put(Route.ROUTE_EPARCHY_CODE, epachyCode);

    }

    public void checkRequestData(IData data) throws Exception
    {
        IDataUtil.getMandaData(data, "PARA_CODE3");
        IDataUtil.getMandaData(data, "SHORT_CODE");

        String sn = IDataUtil.getMandaData(data, "SERIAL_NUMBER");

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", sn);
        IDataset mebInfos = CSAppCall.call("CS.UcaInfoQrySVC.qryUserInfoBySn", params);

        if (IDataUtil.isEmpty(mebInfos))
            CSAppException.apperr(BofException.CRM_BOF_002);

        String memUserId = mebInfos.getData(0).getString("USER_ID");
        String epachyCode = mebInfos.getData(0).getString("EPARCHY_CODE");
        data.put("MEM_USER_ID", memUserId);
        data.put("EPARCHY_CODE", epachyCode);

        IDataUtil.getMandaData(data, "PRODUCT_ID");// 8000

        String serialNumberA = IDataUtil.getMandaData(data, "SERIAL_NUMBER_A");

        IData grpUserData = UcaInfoQry.qryUserInfoBySnForGrp(serialNumberA);

        if (IDataUtil.isEmpty(grpUserData))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_117, serialNumberA);
        }

        String grpUserId = grpUserData.getString("USER_ID");

        data.put("USER_ID", grpUserId);
    }

}
