
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;


public class UserOtherInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 获得USER_OTHER表中办理过影号的信息
     * 
     * @data 2013-3-26
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getDeptuyUserOther(IData input) throws Exception
    {

        IDataset dataset = new DatasetList();// ;

        return dataset;
    }

    /**
     * 获取集团营销预存信息 专线类
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getLeasedLineSalePayInfo(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        String eparchy_code = input.getString("EPARCHY_CODE");
        IDataset data = UserOtherInfoQry.getLeasedLineSalePayInfo(cust_id, eparchy_code);
        return data;
    }

    /**
     * 获取集团营销预存信息 非专线类
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getNLeasedLineSalePayInfo(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        String eparchy_code = input.getString("EPARCHY_CODE");
        IDataset data = UserOtherInfoQry.getNLeasedLineSalePayInfo(cust_id, eparchy_code);
        return data;
    }

    public IDataset getOtherInfoByIdPTag(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String processTag = input.getString("PROCESS_TAG");
        String rsrvValueCode = input.getString("RSRV_VALUE_CODE");
        return UserOtherInfoQry.getOtherInfoByIdPTag(userId, rsrvValueCode, processTag);
    }

    /**
     * 获取集团营销预存信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getSalePayInfo(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID");
        String eparchy_code = input.getString("EPARCHY_CODE");
        IDataset data = UserOtherInfoQry.getSalePayInfo(cust_id, eparchy_code);
        return data;
    }

    public IDataset getUserOtherByUseridRsrvcode(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String rsrv_value_code = input.getString("RSRV_VALUE_CODE");
        IDataset output = UserOtherInfoQry.getUserOtherByUseridRsrvcode(user_id, rsrv_value_code, null);
        return output;
    }

    public IDataset getUserOtherByUserRsrvValueCodeByEc(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String rsrv_value_code = input.getString("RSRV_VALUE_CODE");
        IDataset output = UserOtherInfoQry.getUserOtherByUserRsrvValueCodeByEc(user_id, rsrv_value_code);
        return output;
    }

    public IDataset getUserOtherByUserRsrvValueCodeForGrp(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String rsrvValueCode = input.getString("RSRV_VALUE_CODE");
        return UserOtherInfoQry.getUserOtherByUserRsrvValueCode(userId, rsrvValueCode, null);
    }

    public IDataset getUserOtherInfo(IData input) throws Exception
    {
        IDataset output = UserOtherInfoQry.getUserOtherInfo(input, null);
        return output;
    }

    /**
     * 查询用户集团预存金额
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserOtherInfoByGroupExchange(IData input) throws Exception
    {
        IDataset data = UserOtherInfoQry.getUserOtherInfoByGroupExchange(input);
        return data;
    }

    public IDataset getUserOtherInfoForGrp(IData input) throws Exception
    {
        IDataset output = UserOtherInfoQry.getUserOtherInfoForGrp(input, null);
        return output;
    }

    public IDataset getUserOtherUserId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String rsrv_value_code = input.getString("RSRV_VALUE_CODE");
        IDataset output = UserOtherInfoQry.getUserOtherUserId(user_id, rsrv_value_code, null);
        return output;
    }

    /**
     * 用户跨省信息查询
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserOverProvinceInfo(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String rsrv_value_code = input.getString("RSRV_VALUE_CODE");
        String rsrv_str1 = input.getString("RSRV_STR1");
        IDataset output = UserOtherInfoQry.getUserOverProvinceInfo(user_id, rsrv_value_code, rsrv_str1, null);
        return output;
    }

    public IDataset qrySumRsrvByUserId(IData inputData) throws Exception
    {
        return UserOtherInfoQry.qrySumRsrvByUserId(inputData);
    }

    public IDataset queryGaveUserBussPresent(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        return UserOtherInfoQry.queryGaveUserBussPresent(userId);
    }

    public IDataset queryUserBussPresent(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        return UserOtherInfoQry.queryUserBussPresent(userId);
    }

    /**
     * REQ201810090021智能组网终端出库接口
	 * @param param
	 * @throws Exception
	 * @author yanghb6
     * @date 2018-11-20
     */
    public static int insertMergeWideTerm(IData param)throws Exception{
    	return UserOtherInfoQry.insertMergeWideTerm(param);
    }
    

    public IDataset queryLineTradeAttr(IData input) throws Exception {
        String productNo = input.getString("PRODUCT_NO");
        return UserOtherInfoQry.queryLineTradeAttr(productNo);
    }
    
    public IDataset queryUserOtherInfos(IData param)throws Exception{
    	String userId = param.getString("USER_ID");
    	String RSRV_VALUE_CODE = param.getString("RSRV_VALUE_CODE");
    	String RSRV_VALUE = param.getString("RSRV_VALUE");
    	return UserOtherInfoQry.queryUserOtherInfos(userId,RSRV_VALUE_CODE,RSRV_VALUE);
    }
    /**
     * 根据user_id,查询用户的权益使用记录
     * 全球通客户权益办使用情况查询
     * @param userId
     * @param rightType
     * @param discntCode
     * @return
     * @throws Exception
     */
    public static IDataset getRightUseRecordByUserId( String userId) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);  
        IDataset dataset = Dao.qryByCode("TF_F_USER_OTHER", "SEL_RIGHT_USE_RECORD_BY_USERID", param);
        return dataset;
    }
}
