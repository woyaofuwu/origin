
package com.asiainfo.veris.crm.order.web.group.vpmnsaleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.diversifyacct.DiversifyAcctViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productinfo.ProductInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upcinfo.UpcInfoIntfViewUtil;

public abstract class VpmnSaleActive extends CSBasePage
{
    /**
     * 判断V网免费体验活动条件  2-取消
     * 
     * @author sungq3
     * @param cycle
     * @throws Exception
     */
    public void checkUuInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);
        String serial_number = data.getString("cond_SERIAL_NUMBER_IN");
        // 1、获取用户信息
        IData uisParam = new DataMap();
        uisParam.put("SERIAL_NUMBER", serial_number);
        uisParam.put("REMOVE_TAG", "0");
        uisParam.put("NET_TYPE_CODE", "00");
        IDataset userinfos = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoBySN", uisParam);
        if (IDataUtil.isNotEmpty(userinfos))
        {
            IData userinfo = (IData) userinfos.getData(0);
            userinfo.put("RSRV_STR4", data.getString("cond_ACTIVE_TYPE"));// 活动类型
            // 2、获取客户信息
            IData parentinfo = new DataMap();
            parentinfo.put("SERIAL_NUMBER", userinfo.getString("SERIAL_NUMBER"));
            parentinfo.put("OPEN_DATE", userinfo.getString("OPEN_DATE"));
            String brand = UpcInfoIntfViewUtil.queryBrandNameByChaVal(this, userinfo.getString("BRAND_CODE")); 
            parentinfo.put("BRAND", brand);
            String city_name = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", userinfo.getString("CITY_CODE"));
            parentinfo.put("CITY_NAME", city_name);
            String product_name = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, userinfo.getString("PRODUCT_ID"));
            parentinfo.put("PRODUCT_NAME", product_name);

            IData cisParam = new DataMap();
            cisParam.put("CUST_ID", userinfo.getString("CUST_ID"));
            IDataset custinfos = CSViewCall.call(this, "CS.CustPersonInfoQrySVC.getPerInfoByCustId", cisParam);
            if (IDataUtil.isNotEmpty(custinfos))
            {
                IData custinfo = custinfos.getData(0);
                parentinfo.put("CUST_NAME", custinfo.getString("CUST_NAME"));
                parentinfo.put("POST_CODE", custinfo.getString("POST_CODE"));
                parentinfo.put("PHONE", custinfo.getString("PHONE"));
                parentinfo.put("HOME_ADDRESS", custinfo.getString("HOME_ADDRESS"));
                setInfo(parentinfo);
            }
            else
            {
                // "该号码"+serial_number+"资料信息不存在，请重新输入！"
                CSViewException.apperr(VpmnUserException.VPMN_USER_100, serial_number);
            }
            // 3、获取账户信息
            IData aisParam = new DataMap();
            aisParam.put("USER_ID", userinfo.getString("USER_ID"));
            IDataset memAcctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctInfoByUserID", aisParam);
            if (IDataUtil.isEmpty(memAcctInfos))
            {
                // "该号码【"+serial_number+"】账户信息不存在，请重新输入！"
                CSViewException.apperr(VpmnUserException.VPMN_USER_101, serial_number);
            }
            // 4、判断是否办理过V网免费体验活动
            IData relaParam = new DataMap();
            relaParam.put("USER_ID_B", userinfo.getString("USER_ID"));
            relaParam.put("ACTIVE_TYPE", data.getString("cond_ACTIVE_TYPE"));
            relaParam.put(Route.ROUTE_EPARCHY_CODE, userinfo.getString("EPARCHY_CODE"));
            IDataset relaset = CSViewCall.call(this, "SS.VpmnSaleActiveQrySVC.queryVPMNSaleActiveByUserIdBActype", relaParam);
            if (IDataUtil.isNotEmpty(relaset))
            {
                // "该号码"+serial_number+"已办理过V网免费体验活动!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_102, serial_number);
            }
            // 5、判断用户是否当月新增V网用户（含当月退订，再次办理）   END_DATE < SYSDATE
            IData rcsParam = new DataMap();
            rcsParam.put("USER_ID_B", userinfo.getString("USER_ID"));
            rcsParam.put("RELATION_TYPE_CODE", "20");
            IDataset recordCounts = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getUserOrderInCurMonthByUserIdB", rcsParam);
            int existInt = ((IData) recordCounts.get(0)).getInt("RECORDCOUNT");
            if (existInt > 0)
            {
                // "该号码"+ serial_number +"是非当月新增V网用户（含当月退订，再次办理），不能办理集团3元优惠活动!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_103, serial_number);
            }
            // 6、获取用户与用户关系信息
            IDataset userrelations = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userinfo.getString("USER_ID"), "20", userinfo.getString("EPARCHY_CODE"));
            if (userrelations.size() != 1)
            {
                // "该号码【"+serial_number+"】不是VPMN集团成员，不能办理集团3元优惠活动!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_104, serial_number);
            }
            IData userrelation = (IData) userrelations.getData(0);
            // 7、判断用户是否当月加入V网集团（按多账期处理）
            IData userAccDay = DiversifyAcctViewUtil.getUserAcctDay(this, userinfo.getString("USER_ID"), userinfo.getString("EPARCHY_CODE"));
            IData uuParam = new DataMap();
            uuParam.put("USER_ID_B", userinfo.getString("USER_ID"));
            uuParam.put("RELATION_TYPE_CODE", "20");
            uuParam.put("START_DATE", userAccDay.getString("FIRST_DAY_THISACCT"));
            uuParam.put("END_DATE", userAccDay.getString("LAST_DAY_THISACCT"));
            IDataset uudataset = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserIdBAndSDate", uuParam);
            if (IDataUtil.isEmpty(uudataset))
            {
                // "该号码【"+serial_number+"】不是当前账期"+userAccDay.getString("FIRST_DAY_THISACCT")+"到"+userAccDay.getString("LAST_DAY_THISACCT")+"加入V网集团，不能办理集团3元优惠活动!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_105, serial_number, userAccDay.getString("FIRST_DAY_THISACCT"), userAccDay.getString("LAST_DAY_THISACCT"));
            }
            // 8、判断用户V网套餐必须是集团3元套餐（JDD）
            IData dtParam = new DataMap();
            dtParam.put("USER_ID", userinfo.getString("USER_ID"));// 成员
            dtParam.put("USER_ID_A", userrelation.getString("USER_ID_A")); 
//          dtParam.put("PRODUCT_ID", "800001"); // 集团VPMN成员产品
//          dtParam.put("PACKAGE_ID", "80000102"); // 集团VPMN成员产品优惠包
            IDataset discntset = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryDiscntsByUserIdProdIdPkgId", dtParam);
            if (IDataUtil.isNotEmpty(discntset))
            {
            	boolean has1285 = false;
            	for(int i=0; i<discntset.size(); i++){
                IData discnt = discntset.getData(i);
	                if ("1285".equals(discnt.getString("DISCNT_CODE", ""))){
	                	has1285 = true; 
	                	break;
	            	}
	            }
            	if (!has1285)
                {
                    CSViewException.apperr(CrmCommException.CRM_COMM_103, "该号码未订购集团3元套餐(JDD),不能办理集团3元优惠活动!");
                }
            }
	            setCustdata(parentinfo);
	        }
	        else
	        {
	            // "该号码"+serial_number+"资料信息不存在，请重新输入！"
	            CSViewException.apperr(VpmnUserException.VPMN_USER_107, serial_number);
	        }
        
    }

    /**
     * 判断双网有礼活动条件
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkVpmnAndUuInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        setCondition(data);
        String serial_number = data.getString("cond_SERIAL_NUMBER_BOTH");
        String active_type = data.getString("cond_ACTIVE_TYPE");
        // 1、获取用户信息
        IData uisParam = new DataMap();
        uisParam.put("SERIAL_NUMBER", serial_number);
        uisParam.put("REMOVE_TAG", "0");
        uisParam.put("NET_TYPE_CODE", "00");
        IDataset userInfos = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoBySN", uisParam);
        if (IDataUtil.isNotEmpty(userInfos))
        {
            IData userInfo = (IData) userInfos.getData(0);
            userInfo.put("RSRV_STR4", active_type);// 活动类型
            String userStateCodeset = userInfo.getString("USER_STATE_CODESET", "");
            if (!"0".equals(userStateCodeset) && !"N".equals(userStateCodeset) && !"00".equals(userStateCodeset))
            {
                // "该号码【" + userInfo.getString("SERIAL_NUMBER") + "】服务状态处于非正常状态！"
                CSViewException.apperr(VpmnUserException.VPMN_USER_108, serial_number);
            }
            // 2、获取客户信息
            IData parentinfo = new DataMap();
            parentinfo.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            parentinfo.put("OPEN_DATE", userInfo.getString("OPEN_DATE"));
            //String brand = StaticUtil.getStaticValue(getVisit(), "TD_S_BRAND", "BRAND_CODE", "BRAND", userInfo.getString("BRAND_CODE"));
            String brand = UpcInfoIntfViewUtil.queryBrandNameByChaVal(this, userInfo.getString("BRAND_CODE"));
            parentinfo.put("BRAND", brand);
            String city_name = StaticUtil.getStaticValue(getVisit(), "TD_M_AREA", "AREA_CODE", "AREA_NAME", userInfo.getString("CITY_CODE"));
            parentinfo.put("CITY_NAME", city_name);
            String product_name = ProductInfoIntfViewUtil.qryProductNameStrByProductId(this, userInfo.getString("PRODUCT_ID"));
            parentinfo.put("PRODUCT_NAME", product_name);

            IData cisParam = new DataMap();
            cisParam.put("CUST_ID", userInfo.getString("CUST_ID"));
            IDataset custinfos = CSViewCall.call(this, "CS.CustPersonInfoQrySVC.getPerInfoByCustId", cisParam);
            if (IDataUtil.isNotEmpty(custinfos))
            {
                IData custinfo = custinfos.getData(0);
                parentinfo.put("CUST_NAME", custinfo.getString("CUST_NAME"));
                parentinfo.put("POST_CODE", custinfo.getString("POST_CODE"));
                parentinfo.put("PHONE", custinfo.getString("PHONE"));
                parentinfo.put("HOME_ADDRESS", custinfo.getString("HOME_ADDRESS"));
                setInfo(parentinfo);
            }
            else
            {
                // "该号码"+serial_number+"资料信息不存在，请重新输入！"
                CSViewException.apperr(VpmnUserException.VPMN_USER_100, serial_number);
            }
            // 3、获取账户信息
            IData aisParam = new DataMap();
            aisParam.put("USER_ID", userInfo.getString("USER_ID"));
            IDataset memAcctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctInfoByUserID", aisParam);
            if (IDataUtil.isEmpty(memAcctInfos))
            {
                // "该号码【"+serial_number+"】账户信息不存在，请重新输入！"
                CSViewException.apperr(VpmnUserException.VPMN_USER_101, serial_number);
            }
            // 4、判断是否办理过双网有礼活动
            IData relaParam = new DataMap();
            relaParam.put("USER_ID_B", userInfo.getString("USER_ID"));
            relaParam.put("ACTIVE_TYPE", data.getString("cond_ACTIVE_TYPE"));
            relaParam.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
            IDataset relaset = CSViewCall.call(this, "SS.VpmnSaleActiveQrySVC.queryVPMNSaleActiveByUserIdBActype", relaParam);
            if (IDataUtil.isNotEmpty(relaset))
            {
                // "该号码【"+serial_number+"】已办理过双网有礼活动!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_109, serial_number);
            }
            // 5、判断是否参加其他的互斥的营销活动 XXXX TF_F_USER_SALE_NEW
            IData usaParam = new DataMap();
            usaParam.put("USER_ID", userInfo.getString("USER_ID"));
            IDataset activeSet = CSViewCall.call(this, "CS.UserSaleGoodsQrySVC.qrySaleActiveByUserId", usaParam);
            if (IDataUtil.isNotEmpty(activeSet))
            {
                IData actData = activeSet.getData(0);
                if (IDataUtil.isNotEmpty(actData))
                {
                    String proName = actData.getString("PRODUCT_NAME", "");
                    // "该号码【"+serial_number+"】已办理过"+ proName + ",不可办理双网有礼活动!"
                    CSViewException.apperr(VpmnUserException.VPMN_USER_110, serial_number, proName);
                }
            }
            // 6、用户必须是亲亲网主号码
            IData uuRelaParam = new DataMap();
            uuRelaParam.put("USER_ID_B", userInfo.getString("USER_ID"));
            uuRelaParam.put("RELATION_TYPE_CODE", "45");
            uuRelaParam.put("ROLE_CODE_B", "1");
            IDataset outSet = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelatInfosBySelUserIdA", uuRelaParam);
            if (IDataUtil.isEmpty(outSet))
            {
                // "该号码【"+serial_number+"】不是亲亲网主号码,不能办理双网有礼活动!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_111, serial_number);
            }
            String userIdA = outSet.getData(0).getString("USER_ID_A", "");
            if (StringUtils.isEmpty(userIdA))
            {
                // "获取亲亲网主号码【"+serial_number+"】失败!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_112, serial_number);
            }
            // 7、用户所组建的亲亲网是否有2个或以上副号码
            IData uuRelaParam1 = new DataMap();
            uuRelaParam1.put("USER_ID_A", userIdA);
            uuRelaParam1.put("RELATION_TYPE_CODE", "45");
            // uuRelaParam1.put("ROLE_CODE_B", "2");
            IDataset uusets = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserIdA", uuRelaParam1);
            if (IDataUtil.isEmpty(uusets))
            {
                // 该号码【"+serial_number+"】未组建亲亲网,不能办理双网有礼活动!
                CSViewException.apperr(VpmnUserException.VPMN_USER_113, serial_number);
            }
            else if (uusets.size() < 3)
            {
                // "该号码【"+serial_number+"】所组建的亲亲网无2个或以上副号码,不能办理双网有礼活动,请联系您的集团客户经理!
                CSViewException.apperr(VpmnUserException.VPMN_USER_114, serial_number);
            }
            // 8、是否加入了V网
            IDataset vpnSet = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userInfo.getString("USER_ID"), "20", userInfo.getString("EPARCHY_CODE"), false);
            if (IDataUtil.isEmpty(vpnSet))
            {
                // "该号码【"+serial_number+"】不是VPMN集团成员,不能办理双网有礼活动,请联系您的集团客户经理!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_115, serial_number);
            }

            IData vpnData = UCAInfoIntfViewUtil.qryGrpUserInfoByUserId(this, vpnSet.getData(0).getString("USER_ID_A"), false);
            if (IDataUtil.isEmpty(vpnData))
            {
                // "请确认该用户【"+serial_number+"】是否存在V网关系!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_116, serial_number);
            }
            // 9、办理某个套餐,才能办理双网有礼活动的配置,集团V网包下套餐配置
            IData pkgParam = new DataMap();
            pkgParam.put("USER_ID", userInfo.getString("USER_ID")); // 成员
            pkgParam.put("USER_ID_A", vpnSet.getData(0).getString("USER_ID_A")); // V网的USER_ID
//            pkgParam.put("PRODUCT_ID", "800001"); // 集团VPMN成员产品
//            pkgParam.put("PACKAGE_ID", "80000102"); // 集团VPMN成员产品优惠包
            pkgParam.put("SUBSYS_CODE", "CSM");
            pkgParam.put("PARAM_ATTR", "9087");
            pkgParam.put("EPARCHY_CODE", "0898");
            IDataset discntSet = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryDiscntByUserIdVpmnActive", pkgParam);
            if (IDataUtil.isEmpty(discntSet))
            {
                pkgParam.clear();
                pkgParam.put("SUBSYS_CODE", "CSM");
                pkgParam.put("PARAM_ATTR", "9087");
                pkgParam.put("EPARCHY_CODE", "0898");
                IDataset paramSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", pkgParam);
                StringBuilder sBuilder = new StringBuilder();
                String resultStr = "";
                for (int i = 0; i < paramSet.size(); i++)
                {
                    IData vpmnData = paramSet.getData(i);
                    if (vpmnData != null && vpmnData.size() > 0)
                    {
                        String paramCode = vpmnData.getString("PARAM_CODE", "");
                        String paramName = vpmnData.getString("PARAM_NAME", "");
                        sBuilder.append(paramCode + "=" + paramName + ",");
                    }
                }
                if (sBuilder.length() > 0)
                {
                    resultStr = sBuilder.substring(0, sBuilder.length() - 1);
                }
                // "该号码【"+serial_number+"】未办理如下套餐之一,优惠编码为:"+ resultStr + ",不能办理双网有礼活动,请联系您的集团客户经理!"
                CSViewException.apperr(VpmnUserException.VPMN_USER_117, serial_number, resultStr);
            }
            // 10、个人产品套餐的配置,配置后,则必须用户必须订购这些套餐
            pkgParam.clear();
            pkgParam.put("SUBSYS_CODE", "CSM");
            pkgParam.put("PARAM_ATTR", "9086");
            pkgParam.put("EPARCHY_CODE", "0898");
            IDataset paramSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", pkgParam);
            if (IDataUtil.isNotEmpty(paramSet))
            {
                pkgParam.clear();
                pkgParam.put("USER_ID", userInfo.getString("USER_ID"));// 成员
                pkgParam.put("SUBSYS_CODE", "CSM");
                pkgParam.put("PARAM_ATTR", "9086");
                pkgParam.put("EPARCHY_CODE", "0898");
                IDataset personDisSet = CSViewCall.call(this, "CS.UserDiscntInfoQrySVC.queryDiscntByUserIdVpmnActive", pkgParam);
                if (IDataUtil.isEmpty(personDisSet))
                {
                    StringBuilder sBuilder = new StringBuilder();
                    String resultStr = "";
                    for (int i = 0; i < paramSet.size(); i++)
                    {
                        IData perData = paramSet.getData(i);
                        if (perData != null && perData.size() > 0)
                        {
                            String paramCode = perData.getString("PARAM_CODE", "");
                            String paramName = perData.getString("PARAM_NAME", "");
                            sBuilder.append(paramCode + "=" + paramName + ",");
                        }
                    }
                    if (sBuilder.length() > 0)
                    {
                        resultStr = sBuilder.substring(0, sBuilder.length() - 1);
                    }
                    // "该号码【"+serial_number+"】未办理如下套餐之一,优惠编码为:"+ resultStr + ",不能办理双网有礼活动,请联系您的集团客户经理!"
                    CSViewException.apperr(VpmnUserException.VPMN_USER_117, serial_number, resultStr);
                }
            }
            parentinfo.put("CUST_NAME", custinfos.getData(0).getString("CUST_NAME", ""));
            setCustdata(parentinfo);
        }
        else
        {
            // "该号码"+serial_number+"资料信息不存在，请重新输入！"
            CSViewException.apperr(VpmnUserException.VPMN_USER_107, serial_number);
        }
    }

    /**
     * 校验推荐号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkVpnInfo(IRequestCycle cycle) throws Exception
    {
        IData param = getData();
        String serialNumber = param.getString("cond_SERIAL_NUMBER_A");
        // 1、获取用户信息
        IData uisParam = new DataMap();
        uisParam.put("SERIAL_NUMBER", serialNumber);
        uisParam.put("REMOVE_TAG", "0");
        uisParam.put("NET_TYPE_CODE", "00");
        IDataset userinfos = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoBySN", uisParam);
        if (IDataUtil.isEmpty(userinfos))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_154, serialNumber);
        }
        IData userInfo = userinfos.getData(0);
        String userStateCodeset = userInfo.getString("USER_STATE_CODESET", "");
        if (!"0".equals(userStateCodeset) && !"N".equals(userStateCodeset) && !"00".equals(userStateCodeset))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_161, serialNumber);
        }
        // 2、获取客户信息
        IData cisParam = new DataMap();
        cisParam.put("CUST_ID", userInfo.getString("CUST_ID"));
        IDataset custinfos = CSViewCall.call(this, "CS.CustPersonInfoQrySVC.getPerInfoByCustId", cisParam);
        if (IDataUtil.isEmpty(custinfos))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_155, serialNumber);
        }
        // 3、获取账户信息
        IData aisParam = new DataMap();
        aisParam.put("USER_ID", userInfo.getString("USER_ID"));
        IDataset memAcctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctInfoByUserID", aisParam);
        if (IDataUtil.isEmpty(memAcctInfos))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_155, serialNumber);
        }
        // 4、是否加入了V网
        IDataset vpnSet = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdBAndRelationTypeCode(this, userInfo.getString("USER_ID"), "20", userInfo.getString("EPARCHY_CODE"), false);
        if (IDataUtil.isEmpty(vpnSet))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_157, serialNumber);
        }
        // 5、判断推荐号码的成员级别必须是联系人
        IData grpParam = new DataMap();
        grpParam.put("USER_ID", userInfo.getString("USER_ID"));
        grpParam.put(Route.ROUTE_EPARCHY_CODE, userInfo.getString("EPARCHY_CODE"));
        IDataset mebInfos = CSViewCall.call(this, "CS.GrpMebInfoQrySVC.getGroupInfoByMember", grpParam);
        if (IDataUtil.isNotEmpty(mebInfos))
        {
            IData mebInfo = mebInfos.getData(0);
            String memberKind = mebInfo.getString("MEMBER_KIND", "");
            if (!"1".equals(memberKind))
            {
                CSViewException.apperr(VpmnUserException.VPMN_USER_158, serialNumber);
            }
        }
        else
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_159, serialNumber);
        }
        // 设置回显值
        String activeType = param.getString("cond_ACTIVE_TYPE");
        if ("2".equals(activeType))
        {
            param.put("cond_SERIAL_NUMBER_B", serialNumber);
        }
        else if ("9".equals(activeType))
        {
            param.put("cond_SERIAL_NUMBER_C", serialNumber);
        }
        setCondition(param);
    }

    /**
     * 业务提交
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String active_type = data.getString("cond_ACTIVE_TYPE");
        String serial_number = "";
        String serial_number_a = "";
        if ("2".equals(active_type))
        { // V网免费体验活动
            serial_number = data.getString("cond_SERIAL_NUMBER_IN");
            serial_number_a = data.getString("cond_SERIAL_NUMBER_B");
        }
        else if ("9".equals(active_type))
        { // 双网有礼活动
            serial_number = data.getString("cond_SERIAL_NUMBER_BOTH");
            serial_number_a = data.getString("cond_SERIAL_NUMBER_C");
        }
        // 获取用户信息
        IData uisParam = new DataMap();
        uisParam.put("SERIAL_NUMBER", serial_number);
        uisParam.put("REMOVE_TAG", "0");
        uisParam.put("NET_TYPE_CODE", "00");
        IDataset userinfos = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoBySN", uisParam);
        IData userinfo = userinfos.getData(0);
        userinfo.put("RSRV_STR4", active_type); // 活动类型
        // 获取客户信息
        IData cisParam = new DataMap();
        cisParam.put("CUST_ID", userinfo.getString("CUST_ID"));
        IDataset custinfos = CSViewCall.call(this, "CS.CustPersonInfoQrySVC.getPerInfoByCustId", cisParam);
        IData custinfo = custinfos.getData(0);
        // 获取账户信息
        IData aisParam = new DataMap();
        aisParam.put("USER_ID", userinfo.getString("USER_ID"));
        IDataset memAcctInfos = CSViewCall.call(this, "CS.AcctInfoQrySVC.getAcctInfoByUserID", aisParam);
        IData memAcctInfo = memAcctInfos.getData(0);

        IData ursParam = new DataMap();
        ursParam.put("USER_ID_B", userinfo.getString("USER_ID"));
        ursParam.put("RELATION_TYPE_CODE", "20");
        IDataset userrelations = CSViewCall.call(this, "CS.RelaUUInfoQrySVC.getRelaUUInfoByUserIdBAndRelaTypeCode", ursParam);
        IData userrelation = userrelations.getData(0);

        IData inparam = new DataMap();
        inparam.put("MEM_USER_INFO", userinfo);
        inparam.put("MEM_CUST_INFO", custinfo);
        inparam.put("MEM_ACCT_INFO", memAcctInfo);
        inparam.put("TRADE_TYPE_CODE", "3603");
        inparam.put("STAFF_ID", getVisit().getStaffId());
        inparam.put("DEPART_ID", getVisit().getDepartId());
        inparam.put("CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", getTradeEparchyCode());
        inparam.put(Route.USER_EPARCHY_CODE, userinfo.getString("EPARCHY_CODE"));
        inparam.put("SERIAL_NUMBER", serial_number); // 被推荐号码
        inparam.put("USER_ID", userrelation.getString("USER_ID_B")); // 被推荐号码用户id
        inparam.put("GRP_SERIAL_NUMBER", userrelation.getString("SERIAL_NUMBER_A")); // 集团sn
        inparam.put("GRP_USER_ID", userrelation.getString("USER_ID_A")); // 集团userId
        inparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inparam.put("ACTIVE_TYPE", active_type);
        if (StringUtils.isNotEmpty(serial_number_a) && ("9".equals(active_type) || "2".equals(active_type)))
        { // 获取推荐号码用户信息
            IData param = new DataMap();
            param.put("SERIAL_NUMBER", serial_number_a);
            param.put("REMOVE_TAG", "0");
            param.put("NET_TYPE_CODE", "00");
            IDataset userDataset = CSViewCall.call(this, "CS.UserInfoQrySVC.queryUserInfoBySN", param);
            IData userdata = userDataset.getData(0);
            inparam.put("USER_ID_A", userdata.getString("USER_ID", ""));
            inparam.put("SERIAL_NUMBER_A", serial_number_a);
        }else {
            inparam.put("USER_ID_A", "-1");
            inparam.put("SERIAL_NUMBER_A", "-1");
        }
        IDataset result = CSViewCall.call(this, "SS.VpmnSaleActiveSVC.crtTrade", inparam);
        setAjax(result);// 传递受理标示到前台
    }

    public abstract void setCondition(IData condition);

    public abstract void setCustdata(IData custdata);

    public abstract void setHintInfo(String hintInfo);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setParaminfo(IData paraminfo);
}
