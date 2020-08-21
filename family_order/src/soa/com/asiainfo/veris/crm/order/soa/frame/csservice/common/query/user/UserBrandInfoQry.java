
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class UserBrandInfoQry
{

    /**
     * @Function: getAllBrandChgByUserid
     * @Description 根据user_id查找用户所有品牌变更信息 modify ykx
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午9:28:41 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getAllBrandChgByUserid(String user_id) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        IDataset infos = Dao.qryByCode("TF_F_USER_BRANDCHANGE", "SEL_ALL_BY_USERID", param);
        if (infos == null || infos.size() < 1)
        {
            return null;
        }
        IData info;
        for (int i = 0; i < infos.size(); i++)
        {
            info = (IData) infos.get(i);
            String brandNo = info.getString("BRAND_NO", "");
            if (brandNo.equals("1"))
            {
                info.put("BRAND", "全球通");
            }
            else if (brandNo.equals("3"))
            {
                info.put("BRAND", "动感地带");
            }
            else if (brandNo.equals("4"))
            {
                info.put("BRAND", "神州行大众卡");
            }
            else
            {
                info.put("BRAND", "神州行");
                info.put("BRAND_NO", "2");
            }
        }
        return infos;
    }

    /**
     * @Function: getUserBrandChangeBySn
     * @Description: 根据手机号获得用户品牌信息
     * @param
     * @return：返回结果描述
     * @throws：异常描述
     * @version: v1.0.0
     * @author: updata
     * @date: 2013-4-26 上午9:32:51 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2013-4-26 updata v1.0.0 修改原因
     */
    public static IDataset getUserBrandChangeBySn(String user_id, Pagination pagination) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", user_id);
        return Dao.qryByCode("TF_F_USER_BRANDCHANGE", "SEL_BY_PK", param, pagination);
    }

    /**
     * todo 里面有很多逻辑 根据电话号码查找用户所有品牌变更信息 ykx
     */
    public static IData getUserBrandInfoBySn(IData inparams) throws Exception
    {

        IData result = new DataMap();
        IDataset results = UserInfoQry.getUserInfoBySnCrmOneDb(inparams.getString("SERIAL_NUMBER"), "0", "00");
        if (results != null && results.size() > 0)
        {
            String userID = ((IData) results.get(0)).getString("USER_ID", "");
            IData comData = new DataMap();
            comData.put("PARTITION_ID", userID.substring(userID.length() - 4));
            comData.put("USER_ID", userID);
            results = Dao.qryByCode("TF_F_USER_BRANDCHANGE", "SEL_BY_PK", comData);
            if (results != null && results.size() > 0)
            {
                result = (IData) results.get(0);
                if ("1".equals(result.get("BRAND_NO")))
                {
                    result.put("BRAND", "全球通");
                    result.put("BRAND_CODE", "1");
                }
                else if ("3".equals(result.get("BRAND_NO")))
                {
                    result.put("BRAND", "动感地带");
                    result.put("BRAND_CODE", "3");
                }
                else
                {
                    result.put("BRAND", "神州行");
                    result.put("BRAND_CODE", "2");
                }
            }
        }
        return result;
    }

    /**
     * todo code_code 里没有 查询(tf_f_user_brandchange)
     * 
     * @param inparam
     *            查询所需参数
     * @param eparchyCode
     *            地州编码
     * @return IDataset 品牌资料列表
     * @throws Exception
     */
    public static IDataset queryAllBrand(IData conParams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_BRANDCHANGE", "SEL_ALLBRAND_BY_USERID", conParams, pagination);
    }

    /**
     * todo code_code 没有找到 查询(tf_f_user_brandchange)不包含历史品牌
     * 
     * @param inparam
     *            查询所需参数
     * @param eparchyCode
     *            地州编码
     * @return IDataset 品牌资料列表
     * @throws Exception
     */
    public static IDataset queryUsingBrand(IData conParams, Pagination pagination) throws Exception
    {

        return Dao.qryByCode("TF_F_USER_BRANDCHANGE", "SEL_USINGBRAND_BY_USERID", conParams, pagination);
    }
}
