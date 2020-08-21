/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.ncardsoneacct;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.DiversifyAcctUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;

/**
 * @CREATED by gongp@2014-5-15 修改历史 Revision 2014-5-15 上午11:11:35
 */
public class NcardsOneAcctSVC extends CSBizService
{

    private static final long serialVersionUID = 930074014802292403L;

    /**
     * 获取用户状态
     * 
     * @param userinfo
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-19
     */
    public static String getUserStateNameByCode(IData userinfo) throws Exception
    {
        String userState = "";

        if (userinfo != null && userinfo.size() > 0)
        {
            userState = userinfo.getString("USER_STATE_CODESET").substring(0, 1);
        }

        if (userState != null && !"".equals(userState))
        {
            if ("0".equals(userState))
            {
                userState = "正常";
            }
            else if ("1".equals(userState))
            {
                userState = "申请停机";
            }
            else if ("2".equals(userState))
            {
                userState = "挂失停机";
            }
            else if ("3".equals(userState))
            {
                userState = "并机停机";
            }
            else if ("4".equals(userState))
            {
                userState = "局方停机";
            }
            else if ("5".equals(userState))
            {
                userState = "欠费停机";
            }
            else if ("6".equals(userState))
            {
                userState = "申请销号";
            }
            else if ("7".equals(userState))
            {
                userState = "高额停机";
            }
            else if ("8".equals(userState))
            {
                userState = "欠费预销号";
            }
            else if ("9".equals(userState))
            {
                userState = "欠费销号";
            }
            else if ("A".equals(userState))
            {
                userState = "欠费半停机";
            }
            else if ("B".equals(userState))
            {
                userState = "高额半停机";
            }
        }
        else
        {
            userState = "未知用户状态";
        }

        return userState;
    }

    /**
     * 如果业务中2个号码均是分散用户，起始时间继续向后偏移一个账期 注意：将2个号码中账期日较大的下账期传入
     * 
     * @param firstDayNextAcct
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    private String bothDiversityStartDate(String firstDayNextAcct) throws Exception
    {
        // 将YYYMMDD转换为YYYY-MM-DD格式
        if (firstDayNextAcct.length() == 8)
            firstDayNextAcct = firstDayNextAcct.substring(0, 4) + "-" + firstDayNextAcct.substring(4, 6) + "-" + firstDayNextAcct.substring(6, 8);
        String startDate = null;
        // 如果不是1号 则需要进行变更，然后获取下账期初
        String chgAcctDay = "1";
        String sysDate = SysDateMgr.getSysDate();

        // 如果当前时间小于下账期初，即还在本账期内
        if (firstDayNextAcct.compareTo(sysDate) > 0)
            startDate = DiversifyAcctUtil.getLastTimeThisAcctday(firstDayNextAcct, Integer.parseInt(chgAcctDay));
        else
            startDate = DiversifyAcctUtil.getLastTimeThisAcctday(sysDate, Integer.parseInt(chgAcctDay));

        // 获取首次结账日
        startDate = SysDateMgr.getNextSecond(startDate);

        return startDate.substring(0, 10).replaceAll("-", "");
    }

    /**
     * @param userInfo
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    public IData checkAfterNewSnInfo(IData userInfo) throws Exception
    {

        // 1.根据USERID获取用户账期相关信息
        IData acctDataMain = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userInfo.getString("USER_ID_MAIN"));

        IData acctDataNew = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userInfo.getString("USER_ID"));

        // 提示信息
        StringBuilder warnInfo = null;

        // 1.1 获取账期数据出错
        if (acctDataNew == null)
            // common.error("532009: 获取号码"+userInfo.getString("SERIAL_NUMBER")+"账期无数据！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "532009: 获取号码" + userInfo.getString("SERIAL_NUMBER") + "账期无数据！");

        // 1.2存在预约账期
        if (!"".equals(acctDataNew.getString("NEXT_ACCT_DAY", "")))
        {
            warnInfo = new StringBuilder(68);
            warnInfo.append("532006: 号码").append(userInfo.getString("SERIAL_NUMBER")).append("存在预约的帐期，").append("账期生效时间为").append(acctDataNew.getString("NEXT_FIRST_DATE")).append("，账期生效后才能办理双卡统一付费申请业务！");
            // common.error(warnInfo.toString());
            CSAppException.apperr(CrmCommException.CRM_COMM_103, warnInfo.toString());
        }
        String targetAcctDay = acctDataNew.getString("ACCT_DAY");

        // 判断目标号码和操作号码是不是自然月;若是自然月赋值为false，不用进行提示
        boolean targetFlag = "1".equals(targetAcctDay) ? false : true;
        boolean operFlag = "1".equals(acctDataMain.getString("ACCT_DAY")) ? false : true;

        // 2.如果2个号码有一个为自然月，那么起始时间为分散号码的下账期初
        String startDate = getDiversityStartDate(targetAcctDay, userInfo.getString("USER_ID"));

        String nextStartDateMain = getDiversityStartDate(acctDataMain.getString("ACCT_DAY"), userInfo.getString("USER_ID_MAIN"));

        if (Integer.parseInt(startDate) < Integer.parseInt(nextStartDateMain))
        {
            startDate = nextStartDateMain;
        }

        // 3.如果2个号码均是分散号码，开始时间需要重新处理
        if (!"1".equals(acctDataMain.getString("ACCT_DAY")) && !"1".equals(targetAcctDay))
        {
            startDate = this.bothDiversityStartDate(startDate);
        }
        IData returnInfo = new DataMap();
        returnInfo.put("START_CYCLE_ID", startDate);

        // 4. 展示提示信息
        // 4.1 如果2个号码都是非自然月
        if (targetFlag && operFlag)
        {
            warnInfo = new StringBuilder(115).append("532011: 号码");
            warnInfo.append(userInfo.getString("SERIAL_NUMBER_MAIN")).append("结账日为：").append(acctDataMain.getString("ACCT_DAY")).append("号；号码").append(userInfo.getString("SERIAL_NUMBER")).append("结账日为：").append(targetAcctDay).append("号；");
            // 4.2 如果操作号码不是自然月账期
        }
        else if (operFlag)
        {
            warnInfo = new StringBuilder(95).append("532011: 号码").append(userInfo.getString("SERIAL_NUMBER_MAIN")).append("结账日为：").append(acctDataMain.getString("ACCT_DAY")).append("号；");
            // 4.3 如果目标合账号码不是自然月账期
        }
        else if (targetFlag)
        {
            warnInfo = new StringBuilder(95).append("532011: 号码").append(userInfo.getString("SERIAL_NUMBER")).append("结账日为：").append(targetAcctDay).append("号；");
        }
        // 如果2个号码中存在号码不为自然月账期
        if (warnInfo != null)
        {
            warnInfo.append("办理双卡统一付费业务后结账日会改为1号，").append(startDate).append("开始生效!");
            returnInfo.put("WARN_MSG", warnInfo.toString());// 提示警告信息
        }
        return returnInfo;
    }

    /**
     * 判断用户是否已绑定双卡统一付费
     * 
     * @param userId
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    public void checkRelation(String userId) throws Exception
    {

        IDataset dataset = RelaUUInfoQry.getUserRelationByUR(userId, "34");

        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData temp = dataset.getData(i);
            String userIdA = temp.getString("USER_ID_A");
            if (!userIdA.equals(""))
            {
                // common.error("该用户已经是双卡统一付费号码，业务无法继续！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户已经是双卡统一付费号码，业务无法继续！");
            }
        }

    }

    public void checkUserNextAcctDay(String userId) throws Exception
    {

        UcaData ucaData = UcaDataFactory.getUcaByUserId(userId);

        if (StringUtils.isNotBlank(ucaData.getNextAcctDay()) && !ucaData.getAcctDay().equals(ucaData.getNextAcctDay()))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "509007：号码【" + ucaData.getSerialNumber() + "】存在预约帐期，账期生效时间为" + ucaData.getAcctDayStartDate() + "，账期生效后才能办理双卡统一付费取消业务！");
        }
    }

    /**
     * 根据结账日判断业务生效时间；自然月用户为月初，分散用户则需要进行变更 变更规则如下：本账期不变，从下账期开始进行变更[变更到自然月]，遵循账期在15和45之间
     * 
     * @param acctDay
     * @param userId
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    private String getDiversityStartDate(String acctDay, String userId) throws Exception
    {

        String startDate = null;
        // 如果是1号，立即生效[本账期初]
        if ("1".equals(acctDay))
        {
            startDate = DiversifyAcctUtil.getFirstDayThisAcct(userId);
        }
        else
        {
            startDate = DiversifyAcctUtil.getFirstDayNextAcct(userId);
        }
        return startDate.replaceAll("-", "");
    }

    public IData getGroupInfo(String userIda) throws Exception
    {

        IDataset userIdARelaInfos = RelaUUInfoQry.getUserRelationAll(userIda, "34");

        if (userIdARelaInfos.size() < 1)
        {
            // common.error("没有获取到有效的双卡统一付费虚拟集团资料，业务无法继续！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的双卡统一付费虚拟集团资料，业务无法继续！");
        }

        for (int i = 0; i < userIdARelaInfos.size(); i++)
        {
            String role_code_b = userIdARelaInfos.getData(i).getString("ROLE_CODE_B");
            if (role_code_b.equals("2"))
            {
                return userIdARelaInfos.getData(i);
            }
        }
        return null;

    }

    // 获取副客户资料
    public IData GetOCustInfo(String ocustId) throws Exception
    {

        IData ocustInfo = new DataMap();
        IData iparam = new DataMap();
        IData ocustInfos = null;
        IData custPersonInfos = null;

        ocustInfos = UcaInfoQry.qryCustomerInfoByCustId(ocustId);

        if (ocustInfos != null && ocustInfos.size() > 0)
        {
            custPersonInfos = UcaInfoQry.qryPerInfoByCustId(ocustId);

            if (custPersonInfos == null || custPersonInfos.size() < 1)
            {
                IData custGroupInfos = UcaInfoQry.qryGrpInfoByCustId(ocustId);

                if (custGroupInfos != null && custGroupInfos.size() > 0)
                {
                    ocustInfo.putAll((IData) custGroupInfos.get(0));
                }

            }
            else
            {

                ocustInfo.putAll((IData) custPersonInfos.get(0));
            }
            ocustInfo.putAll((IData) ocustInfos.get(0));
        }

        return ocustInfo;
    }

    // 获取副号码资料
    public IData GetOUserInfo(String userSN) throws Exception
    {

        IData iparam = new DataMap();
        iparam.put("REMOVE_TAG", "0");
        iparam.put("NET_TYPE_CODE", "00");
        iparam.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        iparam.put("SERIAL_NUMBER", userSN);

        IData epary = RouteInfoQry.getMofficeInfoBySn(userSN);

        IData userInfo = UcaInfoQry.qryUserInfoBySn(userSN, epary.getString("EPARCHY_CODE"));

        if (userInfo.size() < 1)
        {
            // common.error("获取用户有效资料无数据");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户有效资料无数据");
        }

        String userStateName = getUserStateNameByCode(userInfo);

        if (userStateName != null && !"".equals(userStateName))
        {
            userInfo.put("USER_STATE", userStateName);
        }
        else
        {
            userInfo.put("USER_STATE", "未知用户状态");
        }
        return userInfo;
    }

    public IDataset getSecondNumUcaInfo(IData param) throws Exception
    {

        String sn = param.getString("SERIAL_NUMBER");

        UcaData ucaData = UcaDataFactory.getNormalUca(sn);

        ucaData.getCustomer().toData();

        // 获取三户资料
        IData tradeTypeData = getTradeTypeParameter("323");
        String infoTagSet = tradeTypeData.getString("INFO_TAG_SET", "");
        String preopenLimitTag = tradeTypeData.getString("PREOPEN_LIMIT_TAG", "");

        IData userInfo = ucaData.getUser().toData();

        if (infoTagSet.length() > 1 && infoTagSet.charAt(0) == '1')
        {
            if ("1".equals(preopenLimitTag) && userInfo.getString("OPEN_MODE").equals("1"))
            {
                // common.error("751025","业务受理前条件判断：该用户是预开未返单用户，不能办理此业务！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务受理前条件判断：该用户是预开未返单用户，不能办理此业务！");
            }
            this.checkRelation(userInfo.getString("USER_ID"));

            IDataset actives155 = SaleActiveInfoQry.getUserSaleActiveNo155(userInfo.getString("USER_ID"));

            if (actives155 != null && actives155.size() > 0)
            {
                // common.error("业务受理前条件判断：该用户存在有效的返还/约定消费类活动，不能办理此业务！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务受理前条件判断：该用户存在有效的返还/约定消费类活动，不能办理此业务！");
            }
        }

        IData custInfo = ucaData.getCustomer().toData();
        custInfo.putAll(ucaData.getCustPerson().toData());

        IData result = new DataMap();

        userInfo.put("PRODUCT_ID", ucaData.getProductId());
        userInfo.put("BRAND_CODE", ucaData.getBrandCode());

        result.put("USER_INFO", userInfo);
        result.put("CUST_INFO", custInfo);

        userInfo.putAll(param);
        IData warn_info = this.checkAfterNewSnInfo(userInfo);

        result.put("WARN_INFO", warn_info);

        IDataset dataset = new DatasetList();
        dataset.add(result);

        return dataset;
    }

    /**
     * @param tradeTypeCode
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    public IData getTradeTypeParameter(String tradeTypeCode) throws Exception
    {

        return UTradeTypeInfoQry.getTradeType(tradeTypeCode, "0898");

    }

    /**
     * for cancel
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-19
     */
    public IDataset loadCancelChildInfo(IData input) throws Exception
    {

        IData returnData = new DataMap();

        IDataset userRelationInfos = RelaUUInfoQry.getUserRelationByUR(input.getString("USER_ID"), "34");

        if (userRelationInfos == null || userRelationInfos.size() == 0 || "".equals(userRelationInfos.getData(0).getString("USER_ID_A")))
        {
            // common.error("该用户不是双卡统一付费用户，业务无法继续！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户不是双卡统一付费用户，业务无法继续！");
        }
        IData relationInfo = userRelationInfos.getData(0);

        this.checkUserNextAcctDay(input.getString("USER_ID"));// 校验主号码

        String userIdA = relationInfo.getString("USER_ID_A");

        returnData.put("RELATION_INFO", relationInfo);

        IData groupInfo = getGroupInfo(userIdA);

        String userSN = groupInfo.getString("SERIAL_NUMBER_B");

        if (groupInfo == null || groupInfo.size() == 0)
        {
            // common.error("没有获取到有效的双卡统一付费副号码信息");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取到有效的双卡统一付费副号码信息！");
        }
        else
        {
            if (userSN.equals(input.getString("SERIAL_NUMBER")))
            {
                // common.error("您输入的服务号码不是双卡统一付费主号码，业务无法继续！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您输入的服务号码不是双卡统一付费主号码，业务无法继续！");
            }
        }

        returnData.put("ORELATION_INFO", groupInfo);

        // 副号码用户资料
        UcaData seUcaInfo = null;
        try
        {
            seUcaInfo = UcaDataFactory.getNormalUca(userSN);
        }
        catch (Exception e)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "副号码无效！");
        }

        this.checkUserNextAcctDay(seUcaInfo.getUserId());// 校验副号码

        IDataset acctInfos = AcctInfoQry.getOldAcctInfo(seUcaInfo.getCustId());

        if (IDataUtil.isEmpty(acctInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "副号码无法获取原有账户信息，业务无法继续！");
        }

        IData ouserInfo = seUcaInfo.getUser().toData();

        ouserInfo.put("PRODUCT_ID", seUcaInfo.getProductId());

        ouserInfo.put("BRAND_CODE", seUcaInfo.getBrandCode());

        returnData.put("SECOND_USER_INFO", ouserInfo);

        // 副号码客户资料

        IData ocustInfo = seUcaInfo.getCustomer().toData();
        ocustInfo.putAll(seUcaInfo.getCustPerson().toData());

        returnData.put("SECOND_CUST_INFO", ocustInfo);

        IDataset dataset = new DatasetList();

        dataset.add(returnData);

        return dataset;

    }

    /**
     * FOR SALE
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-20
     */
    public IDataset loadSaleChildInfo(IData input) throws Exception
    {

        String userId = input.getString("USER_ID");

        this.checkRelation(userId);

        IData acctDataMain = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);

        String bookAcctDay = acctDataMain.getString("NEXT_ACCT_DAY");

        String nowAcctDay = acctDataMain.getString("ACCT_DAY");

        if (bookAcctDay != null && !"".equals(bookAcctDay) && !nowAcctDay.equals(bookAcctDay))
        {
            // common.error("509007：用户存在预约帐期，账期生效时间为"+td.getData("ACCTDAY_INFO").getString("NEXT_FIRST_DATE")+"，账期生效后才能办理双卡统一付费业务！");
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "509007：用户存在预约帐期，账期生效时间为" + acctDataMain.getString("NEXT_FIRST_DATE") + "，账期生效后才能办理双卡统一付费业务！");
        }

        return new DatasetList();
    }

}
