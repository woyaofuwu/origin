
package com.asiainfo.veris.crm.order.web.group.param.supertele;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public class MebParamInfo extends IProductParamDynamic
{
    @Override
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);

        IDataset attrItemset = getAttrItemSet();

        String userIdB = data.getString("MEM_USER_ID", "");
        String userIdA = data.getString("USER_ID", "");
        String mebEparchCode = data.getString("MEB_EPARCHY_CODE", "");

        IData uuInfo = RelationUUInfoIntfViewUtil.qryRelaUUInfoByUserIdBAndUserIdARelationTypeCode(bp, userIdB, userIdA, "25", mebEparchCode, false);
        if (IDataUtil.isNotEmpty(uuInfo))
        {
            IData attrItem = new DataMap();
            attrItem.put("ATTR_VALUE", uuInfo.getString("SHORT_CODE", ""));
            attrItem.put("ATTR_CODE", "hidden_SHORT_CODE");
            attrItemset.add(attrItem);
        }

        IData userattritem = IDataUtil.hTable2STable(attrItemset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        transComboBoxValue(userattritem, getAttrItem());
        result.put("ATTRITEM", userattritem);

        setAttrItemSet(attrItemset);
        return result;
    }

    @Override
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData paramInfo = result.getData("PARAM_INFO");

        IData uuInfo_20 = RelationUUInfoIntfViewUtil.qryRelaUUInfoByUserIdBAndRelationTypeCode(bp, data.getString("MEB_USER_ID", ""), "20", data.getString("MEB_EPARCHY_CODE"), false);

        // 湖南判断分机号码是否属于VPMN集团: 如果是,取VPMN短号
        IData vpnInfo = new DataMap();
        if (IDataUtil.isNotEmpty(uuInfo_20))
        {
            vpnInfo.put("SHORT_CODE", uuInfo_20.getString("SHORT_CODE", ""));
            // vpnInfo.put("FLAG","1");//看老系统是就取V网短号，但廖翊说很多人反应应该可以修改
        }
        else
        {
            vpnInfo.put("FLAG", "0");
        }

        vpnInfo.put("PERFEE_PLAY_BACK", "1");
        vpnInfo.put("M_SINWORD_TYPE_CODE", "1");
        vpnInfo.put("CALL_DISP_MODE", "1");
        vpnInfo.put("M_CALL_AREA_TYPE", "4");
        vpnInfo.put("MON_FEE_LIMIT", "0");
        vpnInfo.put("M_CALL_NET_TYPE1", "1");
        vpnInfo.put("M_CALL_NET_TYPE2", "1");
        vpnInfo.put("M_CALL_NET_TYPE3", "1");
        vpnInfo.put("M_CALL_NET_TYPE4", "1");

        paramInfo.put("VPN_INFO", vpnInfo);
        result.put("PARAM_INFO", paramInfo);

        IData vpnAttrItem = IDataUtil.iDataA2iDataB(vpnInfo, "ATTR_VALUE");
        IData attrItem = this.getAttrItem();
        attrItem.putAll(vpnAttrItem);
        this.setAttrItem(attrItem);

        return result;

    }

    /**
     * 成员新增验证成员短号码
     * 
     * @param bp
     * @param data
     * @return
     * @throws Exception
     */
    public IData shortNumValidateSuperTeleMember(IBizCommon bp, IData data) throws Exception
    {
        IData param = new DataMap();
        IData returnData = new DataMap();
        param.put("SHORT_CODE", data.getString("SHORT_NUM"));
        param.put("USER_ID_A", data.getString("GRP_USER_ID"));
        // 验证短号
        IData validateData = CSViewCall.callone(bp, "SS.GroupValidateSVC.shortCodeValidateSupTeleMeb", param);
        returnData.put("AJAX_DATA", validateData);
        return returnData;
    }
}
