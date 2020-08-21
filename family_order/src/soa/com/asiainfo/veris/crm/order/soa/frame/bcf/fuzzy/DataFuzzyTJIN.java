
package com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy;

import java.util.Iterator;
import java.util.Set;

import com.ailk.biz.BizEnv;
import com.ailk.cache.localcache.CacheFactory;
import com.ailk.cache.localcache.interfaces.IReadOnlyCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.fuzzy.DataFuzzyCache.FuzzyRule;

/**
 * 
 * @author wangdl
 * 天津模糊化
 *
 */
public final class DataFuzzyTJIN
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

        if (StringUtils.isNotBlank(rightCodeSpe)&&StringUtils.isNotBlank(CSBizBean.getVisit().getStaffId()))
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
        if (StringUtils.isNotBlank(rightCode)&&StringUtils.isNotBlank(CSBizBean.getVisit().getStaffId()))
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
        if (StringUtils.isNotBlank(CSBizBean.getVisit().getInModeCode())&&StringUtils.isNotBlank(inModeCodePZ) && !StringUtils.equals(inModeCodePZ, "-1") && !StringUtils.contains(inModeCodePZ, inModeCode)) 
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
        
        boolean is_army_grp = false;
        // 集团特殊处理, 没权限的工号只对部队集团模糊化，其它集团不模糊化
        if(fuzzyRuleMap.containsKey("IS_ARMY_GRP"))
        {
        	is_army_grp = true;
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

            boolean is_army_list[] = new boolean[fData.size()];
            while (column.hasNext())
            {
                columnKey = (String) column.next();

                // if (logger.isDebugEnabled())
                // {
                // logger.debug("columnKey=[" + columnKey + "]");
                // }

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
                    
                    // 集团特殊处理, 没权限的工号只对部队集团模糊化，其它集团不模糊化
                    if (is_army_grp && !is_army_list[iIndex]) 
                    {
                    	if(is_army_grp && (StringUtils.indexOf(map.getString("CUST_NAME", ""), "部队") > -1 || StringUtils.indexOf(map.getString("PAY_NAME", ""), "部队") > -1))
                    	{
                    		is_army_list[iIndex] = true; 
                    	}
                    	else 
                    	{
                    		continue;
						}
					}

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
                    else if (columnType.equals("address"))
                    {
                        columnValue = fuzzyAddress(columnValue);
                    }
                    else if (columnType.equals("psptid"))
                    {
                        String psptType = map.getString("PSPT_TYPE_CODE", "");
                        columnValue = fuzzyPsptId(psptType, columnValue);
                    }
                    else if (columnType.equals("phone"))
                    {
                        columnValue = fuzzyPhone(columnValue);
                    }
                    else if (columnType.equals("email"))
                    {
                        columnValue = fuzzyEmail(columnValue);
                    }

                    // 替换模糊的数
                    map.put(columnKey, columnValue);
                }
            }
        }
    }

    /**
     * 模糊化地址
     * 
     * @param address
     * @return
     * @throws Exception
     */
    private static String fuzzyAddress(String address) 
    {
    	if (StringUtils.isBlank(address))
        {
            return "";
        }
    	
    	if(address.length() < 5)
    	{
    		return address;
    	}
    	StringBuilder mask = new StringBuilder();
    	
    	mask.append(address.charAt(0));
    	mask.append(address.charAt(1));
		for (int i = 0; i < address.length()-4; i++)
		{
			mask.append(character);				
		}			
		mask.append(address.charAt(address.length()-2));
		mask.append(address.charAt(address.length()-1));
    	
		return mask.toString();
	}

	/**
     * 模糊化账户账号
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

        // 保留前五位和末四位，中间用*代替
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

        if (name.length() == 1||name.length() == 2)
        {
        	 //x*
        	 mask.append(name.charAt(0));
             mask.append(character);
        }
        else if (name.length() == 3)
        {
        	//*x*
            mask.append(name.charAt(0));
            mask.append(character);
            mask.append(name.charAt(2));
        }
        else if (name.length() == 4)
        {
            //x**x
            mask.append(name.charAt(0));
            mask.append(character);
            mask.append(character);
            mask.append(name.charAt(3));
        }
        else
        {
        	//xx****XX
            mask.append(name.charAt(0));
            mask.append(name.charAt(1));
            for (int i = 0; i < name.length()-4; i++)
            {
            	mask.append(character);				
  			}			
            mask.append(name.charAt(name.length()-2));
            mask.append(name.charAt(name.length()-1));
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
        
        if(StringUtils.isBlank(psptType))
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
                String ss = pstpId.substring(0, 6);
                String sm = StringUtils.repeat(character, 8);
                String se = pstpId.substring(14, pstpId.length() - 1);

                mask.append(ss).append(sm).append(se).append(character);
            }
            else if (pstpId.length() == 15)
            {
                // 6位号码+6位*+剩余的号码
                String ss = pstpId.substring(0, 6);
                String sm = StringUtils.repeat(character, 6);
                String se = pstpId.substring(12, pstpId.length());

                mask.append(ss).append(sm).append(se);
            }
        }
        else
        {
        	if(pstpId.length() < 4)
        	{
        		return fuzzyAll(pstpId);
        	}
            //最后四位用*代替
            String ss = pstpId.substring(0, pstpId.length() - 4);
            String se = StringUtils.repeat(character, 4);

            mask.append(ss).append(se);
        }

        return mask.toString();
    }
    
    /**
     * 获得模糊化phone
     * 
     * @param phone
     * @return
     * @throws Exception
     */
    public static String fuzzyPhone(String phone)throws Exception
	{
		StringBuilder mask = new StringBuilder();
		
		if(StringUtils.isBlank(phone))
		{
			return "";
		}
		
		phone = phone.trim();
		
		if(phone.length() < 4)
		{
			return fuzzyAll(phone);
		}
		
		String ss = phone.substring(0, phone.length() - 4);
		String ee = StringUtils.repeat(character, 4);
		mask.append(ss).append(ee);
		
		return mask.toString();
	}
    
    /**
     * 获得模糊化email
     * 
     * @param email
     * @return
     * @throws Exception
     */
	public static String fuzzyEmail(String email)throws Exception
	{
		StringBuilder mask = new StringBuilder();
		if(StringUtils.isBlank(email))
		{
			return "";
		}
		email = email.trim();
		
		String[] emailArray = email.split("@");
		
		mask.append("***");
		mask.append("@");
		mask.append(emailArray[1]);
		
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
}
