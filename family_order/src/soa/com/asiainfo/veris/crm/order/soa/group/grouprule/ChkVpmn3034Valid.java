
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeHistoryInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ChkVpmn3034Valid extends BreBase implements IBREScript
{
    /**
     * 成员Vpmn新增，tradetypecode=3034的操作规则
     */
    private static final long serialVersionUID = -245534769209563115L;

    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        String userElementsStr = databus.getString("ELEMENT_INFO"); // 所有选择的元素
        String userId = databus.getString("USER_ID_B", "-1");
        String userIdA = databus.getString("USER_ID", "-1");
        String personProductId = databus.getString("PRODUCT_ID_B", "-1");
        String memEparchyCode = databus.getString("EPARCHY_CODE");
        String shortCode = databus.getString("SHORT_CODE", "");
        boolean ifBooking = databus.getBoolean("IF_BOOKING");

        String strSpecDiscnt = "1285,1286,1391,";
        if (StringUtils.isBlank(userElementsStr))
            userElementsStr = "[]";
        IDataset userElements = new DatasetList(userElementsStr);
        // 0.验证是否能够加入VPMN start
        IData mebUserInfo = UcaInfoQry.qryUserInfoByUserId(userId, memEparchyCode);
        if (IDataUtil.isNotEmpty(mebUserInfo))
        {
            String netTypeCode = mebUserInfo.getString("NET_TYPE_CODE");
            if (!"00".equals(netTypeCode))
            {
                err = "网别不是（00）手机号的用户不能加入VPMN集团！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return true;
            }
        }
        // 0.验证是否能够加入VPMN end
        // 1.验证是否是4G产品 start modify by wangyf6 for lte
        if (IDataUtil.isNotEmpty(userElements))
        {
            for (int i = 0; i < userElements.size(); i++)
            {
                IData element = userElements.getData(i);
                String elementId = element.getString("ELEMENT_ID", "");
                String elementType = element.getString("ELEMENT_TYPE_CODE", "");
                String packageId = element.getString("PACKAGE_ID", "");
                String state = element.getString("MODIFY_TAG", "");
                if ("80000102".equals(packageId) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementType) && state.equals(TRADE_MODIFY_TAG.Add.getValue()))
                {

                    IDataset dataset = ParamInfoQry.getCommparaByCode1("CSM", "8555", "4G", personProductId, CSBizBean.getTradeEparchyCode());
                    if (IDataUtil.isNotEmpty(dataset))
                    {
                        IData productInfo4G = dataset.getData(0);
                        if ("1".equals(productInfo4G.getString("PARA_CODE2", "")))
                        {// 是4G套餐用户
                            IDataset vpmn4GSet = ParamInfoQry.getCommparaByCode1("CSM", "8556", personProductId, elementId, CSBizBean.getTradeEparchyCode());
                            if (IDataUtil.isEmpty(vpmn4GSet))
                            {
                                err = "由于您是4G套餐用户，如需加入VPMN集团必须办理规定的集团套餐之一。";
                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                                return true;
                            }
                        }
                        else if ("0".equals(productInfo4G.getString("PARA_CODE2", ""))) // 4G随E行产品
                        {
                            err = "4G随E行用户不能加入VPMN集团。";
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                            return true;
                        }
                    }
                }
            }
        }
        // 1.验证是否是4G产品 end

        // 2.VPN最大用户数判断 start
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
        if (IDataUtil.isEmpty(userVpnList))
        {
            err = "该集团VPN资料无数据，业务不能继续！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
            return true;
        }
        IData vpnInfo = userVpnList.getData(0);
        int maxUsers = vpnInfo.getInt("MAX_USERS");

        IDataset vpnRela = RelaUUInfoQry.getRelaCoutByPK(userIdA, "20");
        int vpnAllNum = vpnRela.getData(0).getInt("RECORDCOUNT");
        if (vpnAllNum + 1 > maxUsers)
        {
            err = "此集团用户数已达到最大用户数，不能再新增用户，请注销部分用户或修改此集团最大用户数后办理成员新增业务！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
            return true;
        }
        // 2.VPN最大用户数判断 end

        // 3. 如果有本账期删除的358套餐 ，则不能新增其他的358套餐 ---start
        if (IDataUtil.isNotEmpty(userElements))
        {
            for (int i = 0; i < userElements.size(); i++)
            {
                IData element = userElements.getData(i);
                String elementId = element.getString("ELEMENT_ID", "");
                String elementType = element.getString("ELEMENT_TYPE_CODE", "");
                String state = element.getString("MODIFY_TAG", "");
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementType) && state.equals(TRADE_MODIFY_TAG.Add.getValue()))
                {
                    if ("1285".equals(elementId) || "1286".equals(elementId) || "1391".equals(elementId))
                    {
                        // 查本账期删除的358套餐
                        IDataset usrDiscnts = UserDiscntInfoQry.qryUserDiscntbyThisMonth(userId, "20", null, null, DiversifyAcctUtil.getFirstDayNextAcct(userId), memEparchyCode);
                        if (IDataUtil.isNotEmpty(usrDiscnts))
                        {
                            IData discnt = usrDiscnts.getData(0);

                            String discnt_code = discnt.getString("DISCNT_CODE", "");
                            String errorStr = DiversifyAcctUtil.getUserAcctDescMessage(userId, "0");

                            if ("1285".equals(discnt_code))
                            {
                                if ("1286".equals(elementId) || "1391".equals(elementId))
                                {
                                    err = "您" + errorStr + "已经登记集团3元套餐（JDD）,在本界面不能选择其它套餐集团5元套餐(JDE）/集团8元套餐（JDF）。";
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                                    return true;
                                }
                            }
                            else if ("1286".equals(discnt_code))
                            {
                                if ("1285".equals(elementId) || "1391".equals(elementId))
                                {
                                    err = "您" + errorStr + "已经登记集团5元套餐（JDE）,在本界面不能选择其它套餐集团3元套餐（JDD）/集团8元套餐（JDF）。";
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                                    return true;

                                }
                            }
                            else if ("1391".equals(discnt_code))
                            {
                                if ("1285".equals(elementId) || "1286".equals(elementId))
                                {
                                    err = "您" + errorStr + "已经登记集团8元套餐（JDF）,在本界面不能选择其它套餐集团3元套餐（JDD）/集团5元套餐(JDE）。";
                                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. end

        // 4.办理家庭网短号（831）的用户不能办理一号通锚定业务（86128） start add by lixiuyu@20110922
        if (IDataUtil.isNotEmpty(userElements))
        {
            for (int i = 0; i < userElements.size(); i++)
            {
                IData element = userElements.getData(i);
                String state = element.getString("MODIFY_TAG");
                String elementId = element.getString("ELEMENT_ID", "");
                if ("86128".equals(elementId) && BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")) && BofConst.MODIFY_TAG_ADD.equals(state))
                {
                    IDataset svcDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userId, "831");
                    if (IDataUtil.isNotEmpty(svcDataset))
                    {
                        err = "家庭网短号用户不能订购国开行用户开通锚定功能服务!";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                        return true;
                    }
                }
            }
        }

        // 4.办理家庭网短号（831）的用户不能办理一号通锚定业务（86128） end

        // 5. 判断vpmn成员本账期新增业务（1025和3034）是否已达2次 start
        if (!ifBooking)
        {
            String startDate = DiversifyAcctUtil.getFirstTimeThisAcct(userId);
            String endDate = DiversifyAcctUtil.getLastTimeThisAcctday(userId, null);
            // IDataset ds1025infos = TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode("1025", userId, startDate,
            // endDate);// 集团用户成员开户
            IDataset ds3034infos = TradeHistoryInfoQry.getInfosByUserIdTradeTypeCode("3034", userId, startDate, endDate);// 集团VPMN成员新增
            // if (IDataUtil.isNotEmpty(ds1025infos) && IDataUtil.isNotEmpty(ds3034infos) && (ds1025infos.size() +
            // ds3034infos.size()) > 1)
            if (IDataUtil.isNotEmpty(ds3034infos) && ds3034infos.size() > 1)
            {
                err = "您当前账期办理VPMN成员新增业务已达2次，请于下账期办理。";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return true;
            }
        }
        // 5. end
        // 6.短号验证 start
        if (IDataUtil.isNotEmpty(userElements))
        {
            if (StringUtils.isNotBlank(shortCode))
            {
                IData inData = new DataMap();
                inData.put("USER_ID_A", userIdA);
                inData.put("SHORT_CODE", shortCode);
                inData.put("EPARCHY_CODE", memEparchyCode);
                VpnUnit.shortCodeValidateVpn(inData);
                if ("false".equals(inData.getString("RESULT")))
                {

                    if (StringUtils.isNotBlank(inData.getString("ERROR_MESSAGE")))
                    {
                        err = inData.getString("ERROR_MESSAGE");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                        return true;
                    }
                }
            }
            // add by lixiuyu@20100816 集团订购“漫游短号服务（发指令）（801）”,成员短号不能为空
            else
            {
                IDataset svcDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(userIdA, "801");
                if (IDataUtil.isNotEmpty(svcDataset) && StringUtils.isBlank(shortCode))
                {
                    err = "集团已订购“漫游短号服务（发指令）（801）”,成员短号不能为空!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                    return true;

                }
            }
        }
        // 6.end
        return false;
    }
}
