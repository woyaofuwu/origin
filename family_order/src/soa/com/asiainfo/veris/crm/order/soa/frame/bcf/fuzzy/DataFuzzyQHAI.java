
package com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.BaseService;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzyCache.FuzzyRule;
import com.asiainfo.veris.crm.order.soa.frame.bcf.menu.SystemGuiMenuInfo;

public final class DataFuzzyQHAI
{
    private final static Logger logger = Logger.getLogger(DataFuzzyQHAI.class);

    private final static String character = "*";

    /**
     * 模糊化
     * 
     * @param svcName
     * @param indata
     * @param outDataset
     * @throws Exception
     */
    public static void fuzzy(String svcName, IData indata, IDataset outDataset) throws Exception
    {
        // if (logger.isDebugEnabled())
        // {
        // logger.debug("svcName=" + svcName);
        // }

        // 如果数据为空则不处理
        if (IDataUtil.isEmpty(outDataset))
        {
            return;
        }

        // 是否数据模糊化，默认true
        boolean isFuzzy = BizEnv.getEnvBoolean("crm.svc.datafuzzy.fuzzy", true);

        // 否则退出
        if (isFuzzy == false)
        {
            return;
        }

        // 得到强制模糊化key
        String notFuzzyKey = StrUtil.getNotFuzzyKey();

        // 是否强制不模糊化

        boolean isNotFuzzy = indata.getBoolean(notFuzzyKey, false);

        // 是则退出
        if (isNotFuzzy)
        {
            return;
        }

        // 是否配置了模糊化特权
        String rightCodeSpe = BizEnv.getEnvString("crm.svc.datafuzzy.rightcode");

        if (StringUtils.isNotBlank(rightCodeSpe))
        {
            // 是否有数据权限
            boolean isPriv = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), rightCodeSpe);

            // 有则不模糊化
            if (isPriv == true)
            {
                return;
            }
        }

        // 根据服务名得到模糊化规则对象
        FuzzyRule fuzzyRuleObj = getFuzzyRule(svcName);

        // 如果为空则不处理
        if (fuzzyRuleObj == null)
        {
            return;
        }

        // 得到模糊化权限
        String rightCode = fuzzyRuleObj.getRightCode();

        // 配了权限则判断
        if (StringUtils.isNotBlank(rightCode))
        {
            // 是否有数据权限
            boolean isPriv = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), rightCode);

            // 有则不模糊化
            if (isPriv == true)
            {
                return;
            }
        }

        // 得到模糊化规则map
        IData fuzzyRuleMap = fuzzyRuleObj.getFuzzyRule();

        if (IDataUtil.isEmpty(fuzzyRuleMap))
        {
            return;
        }

        Iterator group = fuzzyRuleMap.keySet().iterator();

        IData groupData = null;
        String groupKey = "";
        String columnKey = "";
        String columnType = "";
        String columnValue = "";

        // 需要模糊化的数据
        IDataset fData = null;

        while (group.hasNext())
        {
            groupKey = (String) group.next();

            groupData = fuzzyRuleMap.getData(groupKey);

            // if (logger.isDebugEnabled())
            // {
            // logger.debug("groupKey=[" + groupKey + "]");
            // }

            Iterator column = groupData.keySet().iterator();

            if (groupKey.equals("myself"))
            {
                fData = outDataset;
            }
            else
            {
                IData tmp = outDataset.getData(0);

                // 得到key对象
                Object object = tmp.get(groupKey);

                // 取其下key里面的值
                if (object instanceof IDataset)
                {
                    fData = tmp.getDataset(groupKey);
                }
                else if (object instanceof IData)
                {
                    fData = new DatasetList();
                    fData.add(object);
                }
            }

            // 如果为空
            if (IDataUtil.isEmpty(fData))
            {
                continue;
            }

            // 得到需要模糊化字段的集合
            IData map = fData.getData(0);
            Set setColumn = map.keySet();

            while (column.hasNext())
            {
                columnKey = (String) column.next();

                // if (logger.isDebugEnabled())
                // {
                // logger.debug("columnKey=[" + columnKey + "]");
                // }

                columnType = groupData.getString(columnKey, "");

                // 遍历需要模糊化的数据，依次替换
                String columnOtherValue = "";
                for (int iIndex = 0, iSize = fData.size(); iIndex < iSize; iIndex++)
                {
                    columnOtherValue = "";
                    map = fData.getData(iIndex);

                    // 得到模糊字段的值
                    columnValue = map.getString(columnKey);
                    if(columnKey.indexOf("|") != -1)
                    {
                        columnValue = map.getString(columnKey.split("\\|")[0]);//证件号码
                        columnOtherValue = map.getString(columnKey.split("\\|")[1]);//证件类型
                        columnKey=columnKey.split("\\|")[0];
                    }

                    if (StringUtils.isBlank(columnValue))
                    {
                        continue;
                    }

                    if (StringUtils.isBlank(columnType))
                    {
                        columnValue = fuzzyAll(columnValue);
                    }
                    else if (columnType.equals("custname"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            columnValue = fuzzyName(columnValue);
                        }
                        
                    }
                    else if (columnType.equals("acctno"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            columnValue = fuzzyAcctNo(columnValue);
                        }
                    }
                    else if (columnType.equals("acctname"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            columnValue = fuzzyName(columnValue);
                        }
                    }
                    else if (columnType.equals("psptid"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            if(StringUtils.isNotBlank(columnOtherValue))
                            {
                                columnValue = fuzzyPsptId(columnOtherValue,columnValue);
                            }
                            else
                            {
                                String psptType = map.getString("PSPT_TYPE_CODE", "");
                                columnValue = fuzzyPsptId(psptType, columnValue);
                            }
                        }
                    }
                    if (columnType.equals("addr"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            columnValue = fuzzyAll(columnValue);
                        }
                    }
                    if (columnType.equals("birthday"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            columnValue = fuzzyAll(columnValue);
                        }
                    }
                    else if (columnType.equals("mail"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            columnValue = fuzzyMail(columnValue);
                        }
                    }
                    else if (columnType.equals("psptidspe"))
                    {
                        if(isNeedFuzzy(columnType))
                        {
                            columnValue = fuzzyPsptIdSpecial(columnValue);
                        }
                    }
                    // 替换模糊的数据
                    map.put(columnKey, columnValue);
                }
            }
        }
    }
    
    /**
     * 证件号码 特殊模糊化 模糊化成8个*
     * @param psptId
     * @return
     * @throws Exception
     */
    public static String fuzzyPsptIdSpecial(String psptId)throws Exception{
    	return StringUtils.repeat("*", 8);
    }
    
    /**
     * 模糊化账户标识
     * 
     * @param acctNo
     * @return
     * @throws Exception
     */
    public static String fuzzyAcctNo(String acctNo) throws Exception
    {
        if (StringUtils.isBlank(acctNo))
        {
            return "";
        }

        acctNo = acctNo.trim();

        if (acctNo.length() < 9)
        {
            return acctNo;
        }

        // 保留前5位和末四位，中间用*代替
        String ss = acctNo.substring(0, 5);
        String sm = StringUtils.repeat(character, acctNo.length() - 9);
        String se = acctNo.substring(acctNo.length() - 4);

        StringBuilder mask = new StringBuilder(30);
        mask.append(ss).append(sm).append(se);

        return mask.toString();
    }

    /**
     * 模糊化所有
     * 
     * @param text
     * @return
     * @throws Exception
     */
    public static String fuzzyAll(String text) throws Exception
    {
        return StringUtils.repeat("*", 8);
    }

    /**
     * 模糊化邮箱地址
     * 
     * @param mailNo
     * @return
     * @throws Exception
     */
    public static String fuzzyMail(String mailNo) throws Exception
    {
        if (StringUtils.isBlank(mailNo))
        {
            return "";
        }

        mailNo = mailNo.trim();

        int iIndex = mailNo.indexOf("@");

        if (iIndex == -1)
        {
            return mailNo;
        }

        String ss = "";
        String mailName = mailNo.substring(0, iIndex);
        if(mailName.length() > 3)
        {
        	ss = mailName.substring(0, mailName.length() - 3);
        	ss += StringUtils.repeat(character, 3);
        }
        else
        {
        	ss = StringUtils.repeat(character, mailName.length());
        }
        String se = mailNo.substring(iIndex);

        StringBuilder mask = new StringBuilder(10);
        mask.append(ss).append(se);

        return mask.toString();

    }

    /**
     * 模糊化姓名
     * 
     * @param name
     * @return
     * @throws Exception
     */
    public static String fuzzyName(String name) throws Exception
    {
        if (StringUtils.isBlank(name))
        {
            return "";
        }

        name = name.trim();

        StringBuilder mask = new StringBuilder(20);

        if (name.length() == 1)
        {
            return name;
        }
        else if (name.length() == 2)
        {
            // 张*
            mask.append(name.charAt(0));
            mask.append(character);
        }
        else if (name.length() == 3)
        {
            // 李*宝
            mask.append(name.charAt(0));
            mask.append(character);
            mask.append(name.charAt(2));
        }
        else if (name.length() > 3)
        {
            // 欧XX宝
            for(int i = 0; i < name.length(); i++)
            {
                if(i == 0)
                {
                    mask.append(name.charAt(i));
                }
                else if(i == name.length()-1)
                {
                    mask.append(name.charAt(i));
                }
                else 
                {
                    mask.append(character);
                }
            }
        }

        return mask.toString();
    }

    /**
     * 模糊化身份证号码
     * 
     * @param psptType
     * @param pstpId
     * @return
     * @throws Exception
     */
    public static String fuzzyPsptId(String psptType, String pstpId) throws Exception
    {
        if (StringUtils.isBlank(pstpId))
        {
            return "";
        }

        pstpId = pstpId.trim();

        StringBuilder mask = new StringBuilder(20);

        if (("0".equals(psptType) || "1".equals(psptType)))
        {
            if (pstpId.length() == 18)
            {
                // 出生年月日用*代替，最后一位用*代替
                String ss = pstpId.substring(0, 6);
                String sm = StringUtils.repeat(character, 8);
                String se = pstpId.substring(14, pstpId.length() - 1);

                mask.append(ss).append(sm).append(se).append(character);
            }
            else if (pstpId.length() == 15)
            {
                // 出生年月日用*代替，最后一位用*代替
                String ss = pstpId.substring(0, 6);
                String sm = StringUtils.repeat(character, 6);
                String se = pstpId.substring(12, pstpId.length() - 1);

                mask.append(ss).append(sm).append(se).append(character);
            }
        }
        else if (pstpId.length() >= 4)
        {
            // 末4位用*代替，
            String ss = pstpId.substring(0, pstpId.length() - 4);
            String se = StringUtils.repeat(character, 4);

            mask.append(ss).append(se);
        }
        else
        {
            mask.append(pstpId);
        }

        return mask.toString();
    }

    /**
     * 获得模糊化规则数据对象
     * 
     * @param svcName
     * @return
     * @throws Exception
     */
    private static FuzzyRule getFuzzyRule(String svcName) throws Exception
    {
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(DataFuzzyCache.class);
        FuzzyRule fuzzyRuleObj = (FuzzyRule) cache.get(svcName);

        return fuzzyRuleObj;
    }

    /**
     * 根据类型获得权限编码
     * 
     * @param svcName
     * @return
     * @throws Exception
     */
    private static String getRightCodeByType(String fuzzyType) throws Exception
    {
        IReadOnlyCache cache = CacheFactory.getReadOnlyCache(FuuzyTypeDataRightCache.class);
        String rightCode = (String) cache.get(fuzzyType);

        return rightCode;
    }
    
    private static boolean isNeedFuzzy(String fuzzyType) throws Exception
    {       
        return !StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), getRightCodeByType(fuzzyType));
    }
}
