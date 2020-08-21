
package com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy;

import java.util.Iterator;
import java.util.Set;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzyCache.FuzzyRule;

public final class DataFuzzy
{
    // private final static Logger logger = Logger.getLogger(DataFuzzy.class);

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

        //只对特定的渠道进行模糊化 in_mode_code不配置或者配置-1表示全部模糊化
        String inModeCode = "," + CSBizBean.getVisit().getInModeCode() + ",";
        String inModeCodePZ = fuzzyRuleObj.getInModeCode();//表里面in_mode_code的配置
        if (StringUtils.isNotBlank(inModeCodePZ) && !StringUtils.equals(inModeCodePZ, "-1") && !StringUtils.contains(inModeCodePZ, inModeCode)) 
        {
			//表里面in_mode_code的配置不为空 且没匹配上
        	return;
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

                columnType = groupData.getString(columnKey, "");

                // 如果无这个键，就继续下一个
                if (!setColumn.contains(columnKey))
                {
                    continue;
                }

                // 遍历需要模糊化的数据，依次替换
                for (int iIndex = 0, iSize = fData.size(); iIndex < iSize; iIndex++)
                {
                    map = fData.getData(iIndex);

                    // 得到模糊字段的值
                    columnValue = map.getString(columnKey);

                    if (StringUtils.isBlank(columnValue))
                    {
                        continue;
                    }

                    if (StringUtils.isBlank(columnType))
                    {
                        columnValue = fuzzyAll(columnValue);
                    }
                    else if (columnType.equals("name"))
                    {
                        columnValue = fuzzyName(columnValue);
                    }
                    else if (columnType.equals("acctno"))
                    {
                        columnValue = fuzzyAcctNo(columnValue);
                    }
                    else if (columnType.equals("date"))
                    {
                        columnValue = "";
                    }
                    else if (columnType.equals("mail"))
                    {
                        columnValue = fuzzyMail(columnValue);
                    }
                    else if (columnType.equals("psptid"))
                    {
                        String psptType = map.getString("PSPT_TYPE_CODE", "");
                        columnValue = fuzzyPsptId(psptType, columnValue);
                    }

                    // 替换模糊的数据
                    map.put(columnKey, columnValue);
                }
            }
        }
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

        String ss = StringUtils.repeat(character, 3);
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
        else if (name.length() == 4)
        {
            // 欧XX宝
            mask.append(name.charAt(0));
            mask.append(character);
            mask.append(character);
            mask.append(name.charAt(3));
        }
        else
        {
            if (ProvinceUtil.isProvince(ProvinceUtil.HAIN))
            {
                // 广**** Hain
                mask.append(name.charAt(0));

                for (int i = 1, iSize = name.length(); i < iSize; i++)
                {
                    mask.append(character);
                }
            }
            else
            {
                // 广**税局
                mask.append(name.charAt(0));
                mask.append(character);
                mask.append(character);
                mask.append(name.substring(3));
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
    private static String fuzzyPsptId(String psptType, String pstpId) throws Exception
    {
        if (StringUtils.isBlank(pstpId))
        {
            return "";
        }

        pstpId = pstpId.trim();

        StringBuilder mask = new StringBuilder(20);

        if (("0".equals(psptType) || "1".equals(psptType) || "2".equals(psptType)))
        {
            if (pstpId.length() == 18)
            {
                // 出生年月日用*代替，最后一位用*代替
                //String ss = pstpId.substring(0, 6);
            	String ss = pstpId.substring(0, 4);
                //String sm = StringUtils.repeat(character, 8);
            	String sm = StringUtils.repeat(character, 11);
                //String se = pstpId.substring(14, pstpId.length() - 1);
                String se = pstpId.substring(15, pstpId.length() - 1);

                mask.append(ss).append(sm).append(se).append(character);
            }
            else if (pstpId.length() == 15)
            {
                // 出生年月日用*代替，最后一位用*代替
                //String ss = pstpId.substring(0, 6);
                String ss = pstpId.substring(0, 4);
                //String sm = StringUtils.repeat(character, 6);
                String sm = StringUtils.repeat(character, 9);
                //String se = pstpId.substring(12, pstpId.length() - 1);
                String se = pstpId.substring(13, pstpId.length() - 1);

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

    private static String getFuzzyRuleTest(String svcName) throws Exception
    {
        return "USER_INFO=USER_NAME:name,USER_ID;CUST_NAME:name,PSPT_ID;CUST_INFO=CUST_NAME:name,PSPT_ID";
    }

    public static void main(String[] args) throws Exception
    {
        // String s = "广西地税局";
        // String f = fuzzyName(s);

        String s = "430103780829301";
        String f = fuzzyPsptId("0", s);

        // String s = "43001520074050001185";
        // String f = fuzzyAcctNo(s);

        // String s = "13901234567@139.com";
        // String f = fuzzyMail(s);
    }

    public static void main2(String[] args) throws Exception
    {
        IDataset outDataset = new DatasetList();

        IData input = new DataMap();

        IDataset uflist = new DatasetList();

        IData uf = new DataMap();
        uf.put("USER_NAME", "九九九");
        uf.put("USER_ID", "11111111");
        uf.put("USER_TT", "11111111");

        uflist.add(uf);

        uf = new DataMap();
        uf.put("USER_NAME", "哈哈哈");
        uf.put("USER_ID", "2222222");
        uf.put("USER_TT", "2222222");

        uflist.add(uf);

        input.put("USER_INFO", uflist);

        input.put("CUST_NAME", "发发发");
        input.put("PSPT_ID", "3333333");
        input.put("LINK_SN", "44444444");

        IData cf = new DataMap();
        cf.put("CUST_NAME", "不不不");
        cf.put("PSPT_ID", "777777");

        input.put("CUST_INFO", cf);
        outDataset.add(input);

        IData indata = new DataMap();
        fuzzy("", indata, outDataset);
    }
}
