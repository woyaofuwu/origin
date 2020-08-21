
package com.asiainfo.veris.crm.order.soa.frame.bcf.query;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;

public final class RouteInfoQry
{
    /**
     * 根据号码查询路由地州
     * 
     * @param sn
     * @return
     * @throws Exception
     */
    public static String getEparchyCodeBySn(String sn) throws Exception
    {
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN) || ProvinceUtil.isProvince(ProvinceUtil.QHAI))
        {
            return Route.getCrmDefaultDb();
        }

        IData data = getEparchyInfoBySn(sn);

        if (IDataUtil.isEmpty(data))
        {
            return null;
        }

        return data.getString("EPARCHY_CODE");
    }

    /**
     * 根据号码查询路由地州,支持网外号码
     * 
     * @param sn
     * @return
     * @throws Exception
     */
    public static String getEparchyCodeBySnForCrm(String sn) throws Exception
    {
        //此次取消青海判斷,青海雖然一個CRM庫,但是有多個地州,如果按照默認邏輯,則所有用戶的地州都是0971
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN) || ProvinceUtil.isProvince(ProvinceUtil.TJIN))
        {
            return Route.getCrmDefaultDb();
        }

        IData data = getEparchyInfoBySnForCrm(sn);

        if (IDataUtil.isEmpty(data))
        {
            return null;
        }

        return data.getString("EPARCHY_CODE");
    }

    /**
     * 根据号码查询路由地州
     * 
     * @author luoying
     * @param inparams
     *            查询所需参数：SERIAL_NUMBER 增加网段外号码处理 modify by lim
     * @return IDataset
     * @throws Exception
     */
    public static IData getEparchyInfoBySn(String sn) throws Exception
    {
        // 先查携号表
        IData moffice = getUserNetNpInfoLast(sn);

        if (IDataUtil.isNotEmpty(moffice))
        {
            return moffice;
        }

        // 再查moffice参数表
        moffice = getMofficeInfoBySn(sn);

        return moffice;
    }

    /**
     * 根据号码查询路由地州
     * 
     * @author luoying
     * @param inparams
     *            查询所需参数：SERIAL_NUMBER 增加网段外号码处理 modify by lim
     * @return IDataset
     * @throws Exception
     */
    private static IData getEparchyInfoBySnForCrm(String sn) throws Exception
    {
        IData moffice = getEparchyInfoBySn(sn);

        if (IDataUtil.isNotEmpty(moffice))
        {
            return moffice;
        }
        
        //陕西特殊处理，增加根据宽带账号路由方法
        if (ProvinceUtil.isProvince(ProvinceUtil.SHXI))
        {
        	 IDataset acctCommparaInfo = getCommparaByCode("CSM", "618", "6618","ZZZZ");

             // 从参数表中查询各地州的账号规则，比较宽带账号起始规则，判断宽带所属地州
             if (IDataUtil.isNotEmpty(acctCommparaInfo))
             {
                 String eparchyCode = "";
                 
                 IData resultData = new DataMap();
            	 
            	 for (int i = 0; i < acctCommparaInfo.size(); i++)
                 {
                     IData acct = acctCommparaInfo.getData(i);
                     String paraCode1 = acct.getString("PARA_CODE1", "");
                     String paraCode2 = acct.getString("PARA_CODE2", "");
                     String paraCode3 = acct.getString("PARA_CODE3", "");
                     
                     if ((StringUtils.isNotBlank(paraCode1) && sn.startsWith(paraCode1)) || (StringUtils.isNotBlank(paraCode2) && sn.startsWith(paraCode2)))
                     {
                    	 eparchyCode =  paraCode3;
                    	 
                    	 break;
                     }
                 }
            	 
            	 if (StringUtils.isNotBlank(eparchyCode))
            	 {
            		 resultData.put("EPARCHY_CODE", eparchyCode);
                	 
                	 return resultData;
            	 }
            	
             }
        }
        
     // 如果没有，遍历所有CRM库查
        String[] connNames = Route.getAllCrmDb();

        if (connNames == null)
        {
            return null;
        }
        
        String routeId = "";
        int count = connNames.length;

        for (int index = 0; index < count; index++)
        {
            routeId = connNames[index];

            moffice = UcaInfoQry.qryUserInfoBySn(sn, routeId);

            if (IDataUtil.isNotEmpty(moffice))
            {
                return moffice;
            }
        }

        return null;
    }

    /**
     * 根据归属地州查Moffice表 eparchyCode
     * 
     * @author
     * @throws Exception
     */
    public static IDataset getMofficeInfoByEparchy(String eparchyCode, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", eparchyCode);

        return Dao.qryByCode("TD_M_MOFFICE", "SEL_BY_EPARCHY", inparam, pagination, Route.CONN_RES);
    }

    /**
     * 根据归属地州，Mofficd_id查Moffice表 eparchyCode moffice_id
     * 
     * @author
     * @throws Exception
     */
    public static IDataset getMofficeInfoByPk(String eparchyCode, String moffice_id, Pagination pagination) throws Exception
    {
        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", eparchyCode);
        inparam.put("MOFFICE_ID", moffice_id);

        return Dao.qryByCode("TD_M_MOFFICE", "SEL_BY_PK", inparam, pagination, Route.CONN_RES);
    }

    /**
     * 根据号码查询归属地址
     * 
     * @author luoying
     * @throws Exception
     */
    public static IData getMofficeInfoBySn(String sn) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("SERIAL_NUMBER", sn);

        IDataset idataset = Dao.qryByCode("TD_M_MOFFICE", "SEL_BY_NUM", inparam, Route.CONN_RES);

        if (IDataUtil.isEmpty(idataset))
        {
            return null;
        }

        return idataset.getData(0);
    }

    public static IDataset getMofficeInfoBySn(String snBegin, String snEnd, Pagination page) throws Exception
    {
        IData inparam = new DataMap();

        inparam.put("SERIAL_NUMBERS", snBegin);// SERIALNUMBER_S
        inparam.put("SERIAL_NUMBERE", snEnd); // SERIALNUMBER_E

        // return Dao.qryByCode("TD_M_MOFFICE", "SEL_BY_RES_AREA", inparam, page, Route.CONN_RES);
        return Dao.qryByCode("TD_M_MOFFICE", "SEL_SEG_MOFFICE_BY_SN", inparam, page, Route.CONN_RES);
    }

    /**
     * 查询该用户携转关系表中最后一条记录
     */
    public static IData getUserNetNpInfoLast(String sn) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", sn);

        IDataset dataset = Dao.qryByCode("TF_F_USER_NETNP", "SEL_BY_NETNP_NUM_LAST", param, Route.CONN_CRM_CEN);

        if (IDataUtil.isEmpty(dataset))
        {
            return null;
        }

        return dataset.getData(0);
    }

    /**
     * 判断当前号码是否是移动的号码
     * 
     * @param sn
     * @return
     * @throws Exception
     */
    public static boolean isChinaMobile(String sn) throws Exception
    {
        IData mofficeInfo = getMofficeInfoBySn(sn);

        // 判断当前号码是否是移动的号码
        if (IDataUtil.isEmpty(mofficeInfo))
        {
            return false;
        }

        return true;
    }
     //lisw3
    public static boolean isMfcChinaMobile(String sn) throws Exception
    {
    	// 先查携号表
        IDataset moffice = UserNpInfoQry.qryUserNpInfosBySn(sn);
        if (IDataUtil.isNotEmpty(moffice))
        {
            return false;
        }
        if(!isChinaMobile(sn)){
          	 return false;
          }
			
        return true;
    }
    /**
     * @Function: isChinaMobileNumber
     * @Description: 作用：判断是否是移动的手机号码
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 下午2:58:35 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static boolean isChinaMobileNumber(String sn) throws Exception
    {
        if (sn.length() < 11)
        {
            return false;
        }

        String phoneCode = sn.substring(0, 3);

        IData data = new DataMap();

        IDataset codeArea = Dao.qryByCode("NormalPara", "td_m_codearea", data, Route.CONN_RES);

        if (DataHelper.filter(codeArea, "PARACODE=" + phoneCode).size() > 0)
        {
            return true;
        }

        return false;
    }
    
    
    /**
     * 获取commpara表参数
     * 
     * @param subsysCode
     * @param paramAttr
     * @param paramCode
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getCommparaByCode(String subsysCode, String paramAttr, String paramCode, String eparchyCode) throws Exception
    {
        // 查询td_s_commpara表
        IData param = new DataMap();

        param.put("SUBSYS_CODE", subsysCode);
        param.put("PARAM_ATTR", paramAttr);
        param.put("PARAM_CODE", paramCode);
        param.put("EPARCHY_CODE", eparchyCode);
        return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }

}
