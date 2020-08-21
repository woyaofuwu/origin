
package com.asiainfo.veris.crm.order.web.group.param.desktoptel;

import org.apache.log4j.Logger;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.group.grpimsutil.GrpImsUtilView;

/**
 * @description 多媒体桌面电话成员参数页面处理类
 * @author yish
 */
public class MebParamInfo extends IProductParamDynamic
{
    private static transient Logger logger = Logger.getLogger(MebParamInfo.class);

    /**
     * @description 初始化 集团成员产品变更 参数页面
     * @author yish
     */
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {

        IData result = super.initChgMb(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");

        String meb_user_id = data.getString("MEB_USER_ID", "");
        String eparchyCode = data.getString("MEB_EPARCHY_CODE", "");
        IDataset attrItemset = getAttrItemSet();

        IData idata = new DataMap();
        idata.put("USER_ID", meb_user_id);
        idata.put("RES_TYPE_CODE", "S");
        idata.put("USER_ID_A", data.getString("GRP_USER_ID", ""));
        idata.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        String shortcode = "";
        // 防止资源表与VPN_MEB中短号不一致，导致变更不删除原有资源，短号从资源表获取。
        IDataset reslist = CSViewCall.call(bp, "CS.UserResInfoQrySVC.getUserResByUserIdA", idata);

        IData res = new DataMap();
        if (IDataUtil.isNotEmpty(reslist))
        {
            shortcode = reslist.getData(0).getString("RES_CODE", "");
            res.put("ATTR_VALUE", shortcode);
            res.put("ATTR_CODE", "SHORT_CODE");
            attrItemset.add(res);
        }
        // 短号有效
        if (StringUtils.isBlank(shortcode))
        {
            parainfo.put("IF_SHORT_CODE", "yes"); // 短号不修改
        }
        parainfo.put("METHOD_NAME", "ChgMb");

        IData userattritem = IDataUtil.hTable2STable(attrItemset, "ATTR_CODE", "ATTR_VALUE", "ATTR_VALUE");
        transComboBoxValue(userattritem, getAttrItem());
        result.put("ATTRITEM", userattritem);

        setAttrItemSet(attrItemset);
        result.put("PARAM_INFO", parainfo);

        return result;
    }

    /**
     * @description 初始化 集团成员产品受理 参数页面
     * @author yish
     */
    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {

        IData result = super.initCrtMb(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
            parainfo = result.getData("PARAM_INFO");

        parainfo.put("IF_SHORT_CODE", "yes");

        parainfo.put("METHOD_NAME", "CrtMb");

        result.put("PARAM_INFO", parainfo);

        return result;
    }

    /**
     * @description 短号码验证
     * @author yish
     * @date 2013-10-14
     * @param bp
     * @param data
     * @return
     * @throws Throwable
     */
    public IData validchk(IBizCommon bp, IData data) throws Throwable
    {

        IData result = new DataMap();

        String short_code = data.getString("SHORT_CODE"); // 短号
        String user_id_a = data.getString("USER_ID_A"); // 集团用户user_id
        String eparchy_code = data.getString("MEB_EPARCHY_CODE"); // 成员地州

        IData datatemp = new DataMap();
        datatemp.put("SHORT_CODE", short_code);
        datatemp.put("USER_ID_A", user_id_a);
        datatemp.put("EPARCHY_CODE", eparchy_code);

        GrpImsUtilView grpImsUtilView = new GrpImsUtilView();
        boolean flag = true;

        flag = grpImsUtilView.checkImsShortCode(bp, datatemp);

        datatemp.put("RESULT", flag);
        result.put("AJAX_DATA", datatemp);
        setAttrItem(new DataMap());

        return result;
    }
}
