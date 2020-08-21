package com.asiainfo.veris.crm.iorder.web.igroup.offercha.yht;

import com.ailk.biz.view.BizHttpHandler;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.FuncrightException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;

public class YhtHandler extends BizHttpHandler
{
    /**
     * 查询主叫一号通号码个人用户信息
     * 
     * @author luoyong
     * @throws Throwable
     */
    boolean ctFlag = true;
    
    public void querySerialnumberInfo() throws Throwable
    {
        IData paramInfo = new DataMap();
        IData data = this.getData();
        String memEparchCode = data.getString("MEB_EPARCHY_CODE"); // 成员所属地市编码
        String memSn = data.getString("MEB_SERIAL_NUMBER", "");
        String user_id = data.getString("MEB_USER_ID", "");
        IData yhtMem = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, memSn);
        if (!"05".equals(yhtMem.getString("NET_TYPE_CODE")))
        {
            CSViewException.apperr(GrpException.CRM_GRP_802);// 手机号码不能开通主叫一号通业务！
        }

        // 查询成员用户信息
        String strMebSn = data.getString("SERIAL_NUMBER");
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, strMebSn);
        String strEparchCode = mebUserInfoData.getString("EPARCHY_CODE");
        String user_id_b = mebUserInfoData.getString("USER_ID", "");
        
        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!memEparchCode.equals(strEparchCode))
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_9, memSn, strMebSn);

            }
        }

        IData inparam = new DataMap();
        inparam.put("USER_ID_B", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "S6");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset relainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isNotEmpty(relainfos))
        {
            String s6UserIdA = relainfos.getData(0).getString("USER_ID_A");
            if (!s6UserIdA.equals(user_id))
            {
                CSViewException.apperr(GrpException.CRM_GRP_803, strMebSn);// 该服务号码[" + strMebSn + "]已订购为其他成员的主叫一号通副号！
            }
        }

        inparam.clear();
        inparam.put("USER_ID_A", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "S6");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset zrelainfos = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isNotEmpty(zrelainfos))
        {
            CSViewException.apperr(GrpException.CRM_GRP_804, strMebSn);// 该服务号码[" + strMebSn + "]已订购主叫一号通业务，不能作为其他号码的主叫一号通副号！
        }

        // 如果是固话需要订购IMS业务
        if ("05".equals(mebUserInfoData.getString("NET_TYPE_CODE")))
        {
            inparam.clear();
            // 成员加入其他IMS产品前，必须加入多媒体桌面电话，所以只校验是够加入多媒体桌面电话即可
            inparam.put("USER_ID_B", user_id_b);
            inparam.put("RELATION_TYPE_CODE", "S1");
            inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);
            IDataset imsRelat = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

            if (IDataUtil.isEmpty(imsRelat))
            {
                CSViewException.apperr(GrpException.CRM_GRP_805, strMebSn);// 该号码非本集团IMS产品成员，不能添加为主叫副号！
            }
        }

        paramInfo.put("ZUSER_ID", user_id_b);
        this.setAjax(new DataMap(paramInfo));
      
    }
    
    /**
     * 查询被叫一号通号码个人用户信息
     * 
     * @author luoyong
     * @throws Throwable
     */

    public void queryBSerialnumberInfo() throws Throwable
    {
        IData paramInfo = new DataMap();
        IData data = this.getData();

        String memEparchCode = data.getString("MEB_EPARCHY_CODE"); // 成员所属地市编码
        String memSn = data.getString("MEB_SERIAL_NUMBER", "");

        // 查询成员用户信息
        String strMebSn = data.getString("BSERIAL_NUMBER");
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(this, strMebSn);
        String strEparchCode = mebUserInfoData.getString("EPARCHY_CODE");
        String user_id_b = mebUserInfoData.getString("USER_ID", "");
        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!memEparchCode.equals(strEparchCode))
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_9, memSn, strMebSn);

            }
        }
        paramInfo.put("BUSER_ID", user_id_b);

        this.setAjax(new DataMap(paramInfo));
    }
}
