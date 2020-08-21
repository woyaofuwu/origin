
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BatChgM2MMemDiscntTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);
        // 构建服务请求数据
        builderSvcData(batData);
        // 构建套餐数据
        this.buildChgDiscountData(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IDataUtil.chkParam(condData, "PRODUCT_ID");
        IDataUtil.chkParam(condData, "USER_ID");
        IDataUtil.chkParam(batData, "SERIAL_NUMBER");		//成员号码
        IDataUtil.chkParam(batData, "DATA1");				//成员套餐编码
        IDataUtil.chkParam(batData, "DATA2");				//套餐折扣
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());
        
        svcData.put("USER_ID", condData.getString("USER_ID"));
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
        svcData.put("MEM_ROLE_B", condData.getString("MEM_ROLE_B", "1"));
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("ELEMENT_INFO", new DatasetList(condData.getString("ELEMENT_INFO", "[]")));
        svcData.put("RES_INFO", new DatasetList("[]"));
        svcData.put("PRODUCT_PARAM_INFO", new DatasetList("[]"));
        svcData.put("REMARK", batData.getString("REMARK", condData.getString("PRODUCT_ID") + "批量修改成员套餐折扣率"));

        // 业务是否预约 true 预约 false 非预约工单
        svcData.put("IF_BOOKING", svcData.getString("IF_BOOKING", "false"));

    }
    
    public void buildChgDiscountData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());
        String discnt_code = IDataUtil.chkParam(batData, "DATA1"); 	//优惠编码
        String discount = IDataUtil.chkParam(batData, "DATA2"); 	//折扣率
        String newdiscnt_code = IDataUtil.chkParam(batData, "DATA3"); 	//优惠编码
        String newdiscount = IDataUtil.chkParam(batData, "DATA4"); 	//折扣率
        try{
	        int idiscount = new Integer(discount);
            int inewdiscount = new Integer(newdiscount);
	        if(!(idiscount>=40 && idiscount<=100) || !(inewdiscount>=40 && inewdiscount<=100)){
	        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "折扣率必须为40至100之间的数字!");
	        }

        }catch(NumberFormatException e){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "折扣率必须为整数!");
        }

        String user_id = condData.getString("USER_ID"); // 集团产品用户user_id
        String groupId = IDataUtil.chkParam(condData, "GROUP_ID");// 集团id
        String productId = IDataUtil.chkParam(condData, "PRODUCT_ID");
        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");

        //获取成员产品信息
        String baseMemProduct = ProductMebInfoQry.getMemberMainProductByProductId(productId);
        if (StringUtils.isBlank(baseMemProduct.trim()))
        {
            CSAppException.apperr(GrpException.CRM_GRP_185);
        }
        
        //查询集团是否订购对应的集团产品
        if (StringUtils.isBlank(user_id.trim()))
        {
            IDataset userinfos = UserInfoQry.qryUserByGroupIdAndProductIdForGrp(groupId, productId);
            if (IDataUtil.isEmpty(userinfos))
            {
                CSAppException.apperr(GrpException.CRM_GRP_617, groupId, productId); // 集团未订购
            }
            user_id = userinfos.getData(0).getString("USER_ID");
        }

        //查询成员用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(serialNumber);
        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        //查询集团产品用户是否为成员定制了优惠
        String package_id = null;
        String newpackage_id = null;
        IDataset userGrpPackages = UserGrpPkgInfoQry.getUserGrpPackage(user_id, Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(userGrpPackages))
        {
            CSAppException.apperr(GrpException.CRM_GRP_89); // 该集团没有为成员定制优惠
        }
        for (int i = 0, size = userGrpPackages.size(); i < size; i++)
        {
            IData userGrpPackage = userGrpPackages.getData(i);
            if ("D".equals(userGrpPackage.getString("ELEMENT_TYPE_CODE")) && discnt_code.equals(userGrpPackage.getString("ELEMENT_ID")))
            {
                package_id = userGrpPackage.getString("PACKAGE_ID");
            }
            if ("D".equals(userGrpPackage.getString("ELEMENT_TYPE_CODE")) && newdiscnt_code.equals(userGrpPackage.getString("ELEMENT_ID")))
            {
                newpackage_id = userGrpPackage.getString("PACKAGE_ID");
            }
        }

        // 查询用户优惠信息
        IDataset userdiscnts = UserDiscntInfoQry.getUserDiscntByDiscntCode(userinfo.getString("USER_ID"), user_id, discnt_code, Route.CONN_CRM_CG); // 成员订购的资费
        IDataset element_info = new DatasetList();
        IData discntData = new DataMap();
        IData olddiscntData = new DataMap();

        // 用户优惠修改折扣率
        if (IDataUtil.isEmpty(userdiscnts))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_222);
        }
        if(discnt_code.equals(newdiscnt_code))
        {
            IData userDiscnt = userdiscnts.getData(0);
            //优惠请求参数
            discntData.put("ELEMENT_ID", discnt_code);
            discntData.put("INST_ID", userDiscnt.getString("INST_ID"));
            discntData.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
            discntData.put("END_DATE", userDiscnt.getString("END_DATE"));
            discntData.put("ELEMENT_TYPE_CODE", "D");
            discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            discntData.put("PRODUCT_ID", baseMemProduct);
            discntData.put("PACKAGE_ID", package_id);
            //折扣率属性参数
            IDataset attrParams = new DatasetList();
            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", "7050");
            attrParam.put("ATTR_VALUE", newdiscount);
            attrParams.add(attrParam);
            discntData.put("ATTR_PARAM", attrParams);

            element_info.add(discntData);
            svcData.put("ELEMENT_INFO", element_info);
        }else{
            //新增优惠属性拼装数据
            discntData.put("ELEMENT_ID", newdiscnt_code);
            discntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            discntData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            discntData.put("ELEMENT_TYPE_CODE", "D");
            discntData.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth());
            discntData.put("PRODUCT_ID", baseMemProduct);
            discntData.put("PACKAGE_ID", newpackage_id);
            discntData.put("INST_ID", "");

            //折扣率属性参数
            IDataset attrParams = new DatasetList();
            IData attrParam = new DataMap();
            attrParam.put("ATTR_CODE", "7050");
            attrParam.put("ATTR_VALUE", newdiscount);
            attrParams.add(attrParam);
            discntData.put("ATTR_PARAM", attrParams);

            element_info.add(discntData);

            //删除优惠属性拼装数据
            IData userDiscnt = userdiscnts.getData(0);
            olddiscntData.put("ELEMENT_ID", discnt_code);
            olddiscntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            olddiscntData.put("END_DATE", SysDateMgr.getLastDateThisMonth());
            olddiscntData.put("ELEMENT_TYPE_CODE", "D");
            olddiscntData.put("START_DATE", userDiscnt.getString("START_DATE"));
            olddiscntData.put("PRODUCT_ID", baseMemProduct);
            olddiscntData.put("PACKAGE_ID", package_id);
            olddiscntData.put("INST_ID", userDiscnt.getString("INST_ID"));

            //折扣率属性参数
            IDataset oldattrParams = new DatasetList();
            IData oldattrParam = new DataMap();
            oldattrParam.put("ATTR_CODE", "7050");
            oldattrParam.put("ATTR_VALUE", discount);
            oldattrParams.add(oldattrParam);
            olddiscntData.put("ATTR_PARAM", oldattrParams);

            element_info.add(olddiscntData);

            svcData.put("ELEMENT_INFO", element_info);

        }

    }

}
