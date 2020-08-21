
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esp.DataLineDiscntConst;
import com.asiainfo.veris.crm.order.soa.group.esp.DatalineEspUtil;

public class UserAttrInfoQrySVC extends CSBizService
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public static IDataset getUserAttrByRelaInstId(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        String instId = inparams.getString("RELA_INST_ID");
        return UserAttrInfoQry.getUserAttrByRelaInstId(userId, instId, BizRoute.getRouteId());

    }

    /**
     * chenyi 13-10-28 获取资料表的属性信息
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset getUserAttrByUserId(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        String code = inparams.getString("ATTR_CODE");

        return UserAttrInfoQry.getUserAttrByUserId(userId, code);
    }

    /*
     * 根据user_id查询对应的用户实例化资料信息 先查询,然后纵表数据 转横表数据 UserDom::USER_ATTR::TF_F_USER_ATTR::SEL_BY_USERID
     */
    public static IDataset getUserAttrByUserIda(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        return UserAttrInfoQry.getUserAttrByUserIda(userId);
    }

    /**
     * 特殊参数处理
     * 
     * @author liuxx3
     * @date 2014-07-31
     */
    public static IDataset getUserAttrByUserIdc(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String attrCode = input.getString("ATTR_CODE");
        IDataset output = UserAttrInfoQry.getUserAttrByUserIdc(user_id, attrCode);
        return output;
    }

    /**
     * SEL_BY_USERID_INSTID
     * 
     * @author xunyl
     * @version 创建时间：2013-3-14
     */
    public static IDataset getUserAttrByUserIdInstid(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        String inst_type = inparams.getString("INST_TYPE");
        String inst_id = inparams.getString("RELA_INST_ID");
        return UserAttrInfoQry.getUserAttrByUserIdInstid(userId, inst_type, inst_id);
    }

    /*
     * @author xunyl @description 根据用户编号查询用户所订购的产品属性信息 @date:2013-03-13
     */
    public static IDataset getUserProductAttrValue(IData inparams) throws Exception
    {
        String user_id = inparams.getString("USER_ID");
        String inst_type = inparams.getString("INST_TYPE");
        String attr_code = inparams.getString("ATTR_CODE");
        return UserAttrInfoQry.getUserProductAttrValue(user_id, inst_type, attr_code);
    }
    
    /*
     * @author chenjg @description 根据用户编号查询用户所订购的产品属性信息(不判断是否在有效时间内) @date:2019-10-09
     */
    public static IDataset getUserProductAttrValueForManualBack(IData inparams) throws Exception
    {
        String user_id = inparams.getString("USER_ID");
        String attr_code = inparams.getString("ATTR_CODE");
        String inst_type = inparams.getString("INST_TYPE");
        return UserAttrInfoQry.getGrpAttrInfoAttrCode(user_id,attr_code,inst_type);
    }

    /*
     * @description 根据用户编号，参数编号 查询参数资料
     * @author zhangcheng6
     * @date 2013-05-28
     */
    public static IDataset qryBbossUserAttrForGroupNew(IData param) throws Exception
    {
        String userId = param.getString("USER_ID");
        String attrGroup = param.getString("RSRV_STR4");
        String paramCode = param.getString("ATTR_CODE");
        return UserAttrInfoQry.qryBbossUserAttrForGroupNew(userId, attrGroup, paramCode);
    }

    /**
     * chenyi 13-10-28 获取资料表有效数据
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public static IDataset queryUserAllAttrs(IData inparams) throws Exception
    {
        String userId = inparams.getString("USER_ID");
        return UserAttrInfoQry.queryUserAllAttrs(userId);
    }

    /*
     * @function: queryUserAttrByAttrValue
     * @descrition: 根据属性编号和属性值查询属性资料表，判断该属性值之前是否被占用
     * @author: xunyl
     * @date: 2013-06-13
     */
    public static IDataset queryUserAttrByAttrValue(IData input) throws Exception
    {
        String paramCode = input.getString("ATTR_CODE");
        String paramValue = input.getString("ATTR_VALUE");

        return UserAttrInfoQry.queryUserAttrByAttrValue(paramCode, paramValue);
    }

    public IDataset getuserAttrBySvcId(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String service_id = input.getString("SERVICE_ID");
        IDataset output = UserAttrInfoQry.getuserAttrBySvcId(user_id, service_id);
        return output;
    }

    /**
     * 根据user_id,service_id查询用户订购的实例化信息
     * 
     * @param:user_id,service_id,inst_type
     * @version: v1.0.0
     * @author: liaolc
     */
    public IDataset getuserAttrBySvcIdAndInstType(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String service_id = input.getString("SERVICE_ID");
        String inst_type = input.getString("INST_TYPE");
        IDataset output = UserAttrInfoQry.getuserAttrBySvcIdAndInstType(user_id, service_id, inst_type);
        return output;
    }

    /**
     * 根据user_id,service_id查询用户订购的实例化信息
     * 
     * @param:user_id,service_id,inst_type
     * @version: v1.0.0
     * @author: liaolc
     */
    public IDataset getuserAttrBySvcIdAndInstTypeForGrp(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String service_id = input.getString("SERVICE_ID");
        String inst_type = input.getString("INST_TYPE");
        IDataset output = UserAttrInfoQry.getuserAttrBySvcIdAndInstTypeForGrp(user_id, service_id, inst_type);
        return output;
    }

    public IDataset getUserAttrByTypeCodeValue(IData input) throws Exception
    {
        String instType = input.getString("INST_TYPE");
        String attrCode = input.getString("ATTR_CODE");
        String attrValue = input.getString("ATTR_VALUE");
        IDataset output = UserAttrInfoQry.getUserAttrByTypeCodeValue(instType, attrCode, attrValue);
        return output;
    }

    /**
     * 根据USER_ID、PRODUCT_ID查询用户产品属性
     * 
     * @param inparams
     *            查询参数
     * @param page
     *            分页参数
     * @return 用户信息
     * @throws Exception
     */
    public IDataset getUserProductAttrByUP(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String attr_code = input.getString("PRODUCT_ID");
        String inst_type = input.getString("INST_TYPE");
        IDataset output = UserAttrInfoQry.getUserProductAttrByUP(user_id, attr_code, inst_type, null);
        return output;
    }

    /**
     * 通过userid和userida查询成员用户订购的集团用户的产品属性
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserProductAttrByUserIdAndUserIdA(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String userIdA = input.getString("USER_ID_A");
        String instType = input.getString("INST_TYPE");
        IDataset output = UserAttrInfoQry.getUserProductAttrByUserIdAndUserIdA(user_id, userIdA, instType);
        return output;
    }

    /**
     * 根据USER_Id、INST_TYPE查询用户产品属性
     * 
     * @param inparams
     *            查询参数
     * @param page
     *            分页参数
     * @return 用户信息
     * @throws Exception
     * @author xiajj
     */
    public IDataset getUserProductAttrByUTForGrp(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID");
        String inst_type = input.getString("INST_TYPE");
        IDataset output = UserAttrInfoQry.getUserProductAttrByUTForGrp(user_id, inst_type, null);
        return output;

    }
    
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGrpUserAttrGroupTypeByUserId(IData input) throws Exception
    {
        String userId = input.getString("USER_ID");
        String serviceId = input.getString("SERVICE_ID");
        String attrCode = input.getString("ATTR_CODE");
        IDataset output = UserAttrInfoQry.queryGrpUserAttrGroupTypeByUserId(userId, serviceId, attrCode);
        return output;
    }
    
    public IData getUserLineInfoByUserId(IData input) throws Exception
    {
    	String productId = input.getString("PRODUCT_ID");
    	if("7010".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.viopElementId);
		}else if("7011".equals(productId)||"97011".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.getElementIdToProductId("7011",input.getString("USER_ID")));
		}else if ("7012".equals(productId)||"97012".equals(productId)){
			input.put("DISCNT_CODE",DataLineDiscntConst.getElementIdToProductId("7012",input.getString("USER_ID")));
		}else if ("7016".equals(productId)||"97016".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.imsElementId);
		}else if (DataLineDiscntConst.internet1ProductId.equals(productId)||DataLineDiscntConst.internet1ProductIdMember.equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.internet1ElementId);
		}else if (DataLineDiscntConst.internet2ProductId.equals(productId)||DataLineDiscntConst.internet2ProductIdMember.equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.internet2ElementId);
		}else if (DataLineDiscntConst.dataline1ProductId.equals(productId)||DataLineDiscntConst.dataline1ProductIdMember.equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.dataline1ElementId);
		}else if (DataLineDiscntConst.dataline2ProductId.equals(productId)||DataLineDiscntConst.dataline2ProductIdMember.equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.dataline2ElementId);
		}
        IDataset dataLineInfos = UserAttrInfoQry.getUserLineInfoByUserId(input);
        IData dataLineInfo = DatalineEspUtil.saveProductParamInfoFrontDataset(dataLineInfos);
        return dataLineInfo;
    }
    
    public IData getDiscountByUserId(IData input) throws Exception
    {
    	String productId = input.getString("PRODUCT_ID");
    	if("7010".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.viopElementId);
		}else if("7011".equals(productId)||"97011".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.getElementIdToProductId("7011",input.getString("USER_ID")));
		}else if ("7012".equals(productId)||"97012".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.getElementIdToProductId("7012",input.getString("USER_ID")));
		}else if ("7016".equals(productId)||"97016".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.imsElementId);
		}else if ("70111".equals(productId)||"970111".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.internet1ElementId);
		}else if ("70112".equals(productId)||"970112".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.internet2ElementId);
		}else if ("70121".equals(productId)||"970121".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.dataline1ElementId);
		}else if ("70122".equals(productId)||"970122".equals(productId)){
			input.put("DISCNT_CODE", DataLineDiscntConst.dataline2ElementId);
		}
    	
        IDataset dataLineInfos = UserDiscntInfoQry.getInstIdByUserId(input);
        if(IDataUtil.isNotEmpty(dataLineInfos)) {
        	IData dataLineInstId = dataLineInfos.first();
        	String userId = input.getString("USER_ID");
        	String inst_type = input.getString("INST_TYPE");
        	String inst_id = dataLineInstId.getString("INST_ID");
        	IDataset userInfos = UserAttrInfoQry.getUserLineInfoByRelaInstId(userId,inst_type,inst_id);
        	if(IDataUtil.isNotEmpty(userInfos)) {
        		IData dataLineInfo = DatalineEspUtil.saveProductParamInfoFrontDataset(userInfos);
        		return dataLineInfo;
        	}
        }
        return new DataMap();
    }
}
