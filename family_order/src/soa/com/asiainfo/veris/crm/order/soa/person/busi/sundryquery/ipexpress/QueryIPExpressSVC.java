
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.ipexpress;

import java.util.Map;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;

public class QueryIPExpressSVC extends CSBizService
{
    private static final long serialVersionUID = 4829236996987004886L;

    /**
     * 获得用户的服务状态资源,该方法返回服务状态
     */
    public String getSvcStateNew(IData inparams, QueryIPExpressBean bean, String routeEparchyCode) throws Exception
    {

        StringBuilder userStateCode = new StringBuilder(inparams.getString("USER_STATE_CODESET", ""));
        StringBuilder svcState = new StringBuilder();

        IData comData = new DataMap();

        IDataset usersvc = bean.getUserMainSvc(routeEparchyCode, inparams.getString("USER_ID", ""));

        if (IDataUtil.isEmpty(usersvc))
        {
            usersvc = bean.getUserMainSvcLast(routeEparchyCode, inparams.getString("USER_ID", ""));
            if (usersvc == null || usersvc.size() <= 0)
            {
                return svcState.toString();
            }
        }

        if (userStateCode.length() == 0)
        {
            IDataset vUserSvcstate = bean.getUserMainSvcState(routeEparchyCode, usersvc.get(0, "SERVICE_ID", "").toString());
            if (IDataUtil.isEmpty(vUserSvcstate))
            {
                return svcState.toString();
            }
            // 将服务状态编码列表拼成状态编码集
            for (int i = 0; i < vUserSvcstate.size(); i++)
            {
                userStateCode.append(((IData) vUserSvcstate.get(i)).getString("STATE_CODE", ""));
            }
        }
        IDataset vServicestate = bean.getUserStateCodeset(routeEparchyCode, userStateCode.toString());

        boolean firstState = true;

        for (int i = 0; i < vServicestate.size(); i++)
        {
            if (!firstState)
            {
                svcState.append(",");
            }
            svcState.append(((IData) vServicestate.get(i)).getString("STATE_NAME", ""));
            firstState = false;
        }
        return svcState.toString();
    }

    /**
     * 根据用户唯一标识（user_id）查询用户最近三年累计积分,异常或出错返回积分为0
     */
    public String getUserScore(IData inparam) throws Exception
    {
        // 此处需要调用接口
        // IDataset dataset = TuxedoHelper.callTuxSvc(pd, "QAM_SERIALNUMSCORE", inparam);
        // if (dataset!=null&&dataset.size()>0) {
        // return dataset.getData(0).getString("SCORE", "0");
        // }

        return "0";
    }

    /**
     * 功能：IP直通车查询 作者：GongGuang
     */
    public IDataset queryIPExpress(IData data) throws Exception
    {
        QueryIPExpressBean bean = (QueryIPExpressBean) BeanManager.createBean(QueryIPExpressBean.class);

        String routeEparchyCode = this.getTradeEparchyCode();// BizRoute.getRouteId();
        String serialNumber = data.getString("SERIAL_NUMBER", "");// 手机号码或固定号码
        String relationTypeCode = "50";// IP直通车捆绑
        // 根据号码查询用户信息
        IDataset infos = bean.getUserInfoBySN(serialNumber, routeEparchyCode);
        if (IDataUtil.isEmpty(infos))
        {
            return null;
        }
        // 根据user_id_b查询用户关系信息
        String userId = infos.get(0, "USER_ID").toString();
        IDataset uurelainfosB = bean.getUUByIDB(routeEparchyCode, userId, relationTypeCode);
        if (IDataUtil.isEmpty(uurelainfosB))
        {
            return null;
        }
        // 根据user_id_a查询用户关系信息
        String userIdA = uurelainfosB.get(0, "USER_ID_A").toString();
        IDataset uurelainfosA = bean.getUUUserinfo(routeEparchyCode, userIdA, relationTypeCode);
        if (IDataUtil.isEmpty(uurelainfosA))
        {
            return null;
        }

        IDataset retdst = new DatasetList();
        IData userInfo = new DataMap();
        // IData params4 = new DataMap();
        // IData params5 = new DataMap();
        for (Object anUurelainfosA : uurelainfosA)
        {
            // 关系信息
            IData relainfo = (IData) anUurelainfosA;
            // String update_staff_id =relainfo.getString("UPDATE_STAFF_ID");
            // String update_depart_id =relainfo.getString("UPDATE_DEPART_ID");

            // 用户信息
            String userIdB = relainfo.get("USER_ID_B").toString();
            IDataset infosData = bean.getUserinfo(routeEparchyCode, userIdB);
            if (IDataUtil.isEmpty(infosData))
            {
                // String abc = "545007:不存在该服务号码的用户资料";
            }
            relainfo.putAll((IData) infosData.get(0));
            userInfo.put("IN_DEPART_ID", relainfo.getString("IN_DEPART_ID"));
            userInfo.put("IN_STAFF_ID", relainfo.getString("IN_STAFF_ID"));
            userInfo.put("IN_DATE", relainfo.getString("IN_DATE"));
            // 客户信息
            String custId = infos.get(0, "CUST_ID", "").toString();
            infos = IDataUtil.idToIds(UcaInfoQry.qryCustomerInfoByCustId(custId, routeEparchyCode));
            if (IDataUtil.isEmpty(infos))
            {
                // String abc = "545008:不存在该服务号码的客户资料";
            }
            relainfo.putAll((Map) infos.get(0));
            relainfo.putAll(userInfo);
            retdst.add(relainfo);
        }
        if (IDataUtil.isNotEmpty(retdst))
        {
            IData inparam = new DataMap();
            for (int i = 0; i < retdst.size(); i++)
            {
                ((IData) retdst.get(i)).put("X_SVCSTATE_EXPLAIN", getSvcStateNew((IData) retdst.get(i), bean, routeEparchyCode));
                inparam.clear();
                inparam.put("SERIAL_NUMBER", retdst.get(i, "SERIAL_NUMBER", ""));
                ((IData) retdst.get(i)).put("SCORE_VALUE", getUserScore(inparam));
            }
        }

        if(IDataUtil.isNotEmpty(retdst)){
        	for(int i=0;i<retdst.size();i++){
            	IData res=retdst.getData(i);
            	String productId=res.getString("PRODUCT_ID");
            	String brandCode=res.getString("BRAND_CODE");
                //获取产品名称，调用产商品接口,本次改造duhj 2017/03/08
                res.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                res.put("BRAND_NAME", UBrandInfoQry.getBrandNameByBrandCode(brandCode));

               
        	}
        }
        
        return retdst;
    }
}
