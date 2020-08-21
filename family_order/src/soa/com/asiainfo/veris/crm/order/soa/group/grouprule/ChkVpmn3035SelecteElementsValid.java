
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChkVpmn3035SelecteElementsValid extends BreBase implements IBREScript
{
    /**
     * 成员Vpmn变更，tradetypecode=3035的元素操作规则
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

        String strSpecDiscnt = "1285,1286,1391,";
        if (StringUtils.isBlank(userElementsStr))
            return false;
        IDataset userElements = new DatasetList(userElementsStr);
        if (IDataUtil.isEmpty(userElements))
            return false;
        // 1. 如果对优惠有操作，且已有下账期第一天生效的358优惠，就报错 start
        IDataset grpMemOrderDisInfos = UserDiscntInfoQry.getUserDiscntInfoByUserIdAB(userId, userIdA); // 成员订购的集团产品优惠
        String nextMonthFirstDay = DiversifyAcctUtil.getFirstTimeNextAcct(userId); // 下账期第一时间
        boolean isfound = false;// 是否已有下账期第一时间生效的358套餐
        /*
        if (IDataUtil.isNotEmpty(grpMemOrderDisInfos))
        {
            for (int i = 0; i < grpMemOrderDisInfos.size(); i++)
            {
                IData map = grpMemOrderDisInfos.getData(i);
                String discntCode = map.getString("DISCNT_CODE", "");
                String startDate = map.getString("START_DATE", "");
                if (strSpecDiscnt.indexOf(discntCode + ",") >= 0 && nextMonthFirstDay.compareTo(startDate) == 0)
                { // 如果资费是358，而且开始时间是下账期第一天
                    isfound = true;
                    break;
                }
            }

            boolean changeDiscnt = false; // 判读是否对优惠有增删操作
            if (IDataUtil.isNotEmpty(userElements) && isfound)
            {
                for (int i = 0; i < userElements.size(); i++)
                {
                    IData element = userElements.getData(i);
                    String state = element.getString("MODIFY_TAG");
                    String packageId = element.getString("PACKAGE_ID");// j2ee-该逻辑应该是针对80000102集团VPMN成员产品优惠包的，所以加上包id判断，防止获取集团VPMN成员产品跨省优惠包
                    if ("80000102".equals(packageId) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")) && (BofConst.MODIFY_TAG_ADD.equals(state) || BofConst.MODIFY_TAG_DEL.equals(state)))
                    {
                        // 如果对优惠有操作
                        changeDiscnt = true;
                        break;
                    }

                }
            }
            // 如果对优惠有操作，且已有下账期第一天生效的358优惠，就报错
            if (isfound == true && changeDiscnt == true)
            {
                err = "用户有下账期" + nextMonthFirstDay.substring(0, 10) + "生效的V网优惠，不允许在该页面变更优惠！请在VPN特殊优惠变更界面办理！";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                return true;
            }

        }
        */
        // 1. 如果对优惠有操作，且已有下账期第一天生效的358优惠，就报错 end

        // 2.如果已有358优惠，且存在个人资费4020、4021、4022；则如果新增的优惠不是（1285，1286，1391和662）的话，报依赖互斥错误 start
        // 增加358优惠变更规则判断：358优惠变更到普通优惠的时候，需要判断是或存在资费，存在资费不允许变更
        String discnt358 = "";
        if (IDataUtil.isNotEmpty(grpMemOrderDisInfos))
        {
            // 判断是否已订购3，5，8资费
            isfound = false;
            for (int i = 0; i < grpMemOrderDisInfos.size(); i++)
            {
                IData map = grpMemOrderDisInfos.getData(i);
                String discntCode = map.getString("DISCNT_CODE", "");
                if (strSpecDiscnt.indexOf(discntCode + ",") >= 0)
                {
                    discnt358 = discntCode;
                    isfound = true;
                    break;
                }
            }

            // 判断是否存在个人资费4020、4021、4022
            String discntPerson = "";
            boolean ifpersondis = false;
            if (isfound)
            { // 如果有358优惠，则查是否存在个人资费4020、4021、4022

                IDataset memOrderDisInfos = UserDiscntInfoQry.getUserDiscntInfoByUserIdAB(userId, "-1"); // 成员订购的个人优惠
                if (IDataUtil.isNotEmpty(memOrderDisInfos))
                {
                    for (int k = 0; k < memOrderDisInfos.size(); k++)
                    {
                        IData discntinfo = memOrderDisInfos.getData(k);
                        String discntscode = discntinfo.getString("DISCNT_CODE");
                        if ("4021".equals(discntscode) || "4022".equals(discntscode) || "4020".equals(discntscode))
                        {
                            discntPerson = discntscode;
                            ifpersondis = true;
                            break;
                        }
                    }
                }

            }

            // 判断是否做了358到普通资费的变更,做了就报错
            if (ifpersondis)
            {// 如果有358优惠，且存在个人资费4020、4021、4022；则如果新增的优惠不是（1285，1286，1391和662）的话，报错

                if (IDataUtil.isNotEmpty(userElements))
                {
                    for (int i = 0; i < userElements.size(); i++)
                    {
                        IData element = userElements.getData(i);
                        String state = element.getString("MODIFY_TAG");
                        String discntcode = element.getString("ELEMENT_ID", "");
                        String packageId = element.getString("PACKAGE_ID");// j2ee-该逻辑应该是针对80000102集团VPMN成员产品优惠包的，所以加上包id判断，防止获取集团VPMN成员产品跨省优惠包
                        if ("80000102".equals(packageId) && BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")) && BofConst.MODIFY_TAG_ADD.equals(state))
                        {
                            if (!discntcode.equals("1285") && !discntcode.equals("1286") && !discntcode.equals("1391") && !discntcode.equals("662"))
                            {
                                String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discnt358);
                                String discntNamePerson = UDiscntInfoQry.getDiscntNameByDiscntCode(discntPerson);
                                err = "产品依赖互斥判断:优惠[" + discnt358 + "|" + discntName + "]不能删除, 因为它被用户的另一个优惠[" + discntPerson + "|" + discntNamePerson + "]所依赖， 业务不能继续办理";

                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err.toString());
                                return true;

                            }
                        }

                    }
                }

            }

        }
        // 2.如果有358优惠，且存在个人资费4020、4021、4022；则如果新增的优惠不是（1285，1286，1391和662）的话，报依赖互斥错误end

        // 3.办理家庭网短号（831）的用户不能办理一号通锚定业务（86128） start add by lixiuyu@20110922
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

        // 3.办理家庭网短号（831）的用户不能办理一号通锚定业务（86128） end

        // 4.验证是否是4G产品 start modify by wangyf6 for lte
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
        // 4.验证是否是4G产品 end

        return false;
    }
}
