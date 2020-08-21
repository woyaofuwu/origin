
package com.asiainfo.veris.crm.order.soa.person.busi.salecardopen.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.salecardopen.order.requestdata.SaleCardOpenReqData;
import com.ailk.common.data.IDataset;

/**
 * 买断开户
 * 
 * @author sunxin
 */
public class BuilderSaleCardOpenRequestData extends BaseBuilder implements IBuilder
{
    // @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        SaleCardOpenReqData SaleCardOpenRD = (SaleCardOpenReqData) brd;
        SaleCardOpenRD.setAdvancePay(param.getString("ADVANCE_PAY", ""));
        SaleCardOpenRD.setDevicePrice(param.getString("DEVICE_PRICE", ""));
        SaleCardOpenRD.setSaleTypeCode(param.getString("SALE_TYPE_CODE"));
        SaleCardOpenRD.setProductId(param.getString("PRODUCT_ID"));
        SaleCardOpenRD.setPackageId(param.getString("PACKAGE_ID"));
        SaleCardOpenRD.setAgentId(param.getString("DEPART_ID"));
        SaleCardOpenRD.setIsTag(param.getString("IS_TAG"));
        SaleCardOpenRD.setActiveTime(param.getString("ACTIVE_TIME"));
        makeFeeData(param, SaleCardOpenRD);
    }

    // @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new SaleCardOpenReqData();
    }

    public void makeFeeData(IData param, SaleCardOpenReqData brd) throws Exception
    {
        // String res_fee = param.getString("RES_FEE", "0");// 选号费
        String oper_fee = param.getString("DEVICE_PRICE", "0");// 卡费
        String advance_fee = param.getString("ADVANCE_PAY", "0");// 预存款
        // 卡费 如果为空，生成免费SIM卡的台帐0费用记录
        if (Integer.parseInt(oper_fee) >= 0)
        {
            FeeData feeData = new FeeData();
            feeData.setFeeMode("0");
            feeData.setFeeTypeCode("10");
            feeData.setFee(oper_fee);
            feeData.setOldFee(oper_fee);
            brd.addFeeData(feeData);
        }
        // 预存
        if (Integer.parseInt(advance_fee) >= 0)
        {
            FeeData feeData = new FeeData();
            feeData.setFeeMode("2");
            feeData.setFeeTypeCode("0");// 现金存折
            feeData.setFee(advance_fee);
            feeData.setOldFee(advance_fee);
            brd.addFeeData(feeData);
        }

    }
    
    // 重构父类的构建UcaData方法，买断sim，资源调用个人业务接口查询三户资料时，若存在网别为00和06的相同号码资源，则过滤网别为06的数据。 
    public UcaData buildUcaData(IData param) throws Exception
    {
        // 设置三户资料对象
        String sn = param.getString("SERIAL_NUMBER");
        String userId = "";
        UcaData uca = DataBusManager.getDataBus().getUca(sn);
        if (uca == null)
        {
            IDataset userSet = UserInfoQry.getUserInfoBySnNetTypeCode2(sn,"0");
            if(IDataUtil.isNotEmpty(userSet)){
            	userId = userSet.getData(0).getString("USER_ID");
            }
            if (StringUtils.isNotBlank(userId))
            {
                uca = UcaDataFactory.getUcaByUserId(userId);
            }
            else
            {
                uca = UcaDataFactory.getNormalUca(sn);
            }
        }
        return uca;
    }

}
