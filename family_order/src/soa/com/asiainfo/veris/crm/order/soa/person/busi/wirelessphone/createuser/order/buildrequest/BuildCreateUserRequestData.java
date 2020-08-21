/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.buildrequest.BaseBuildCreateUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata.CreateUserRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata.DeviceInfoData;
import com.asiainfo.veris.crm.order.soa.person.busi.wirelessphone.createuser.order.requestdata.PostInfoData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildCreatePhoneUserRequestData.java
 * @Description: 该类的功能描述
 * @version: v1.0.0
 * @author: chengxf2
 * @date: 2014-7-9 上午09:44:07 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
 */

public class BuildCreateUserRequestData extends BaseBuildCreateUserRequestData implements IBuilder
{

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-9 上午10:12:53 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        super.buildBusiRequestData(param, brd);
        brd.getUca().getUser().setNetTypeCode("12");// 商务固话
        CreateUserRequestData createUserRd = (CreateUserRequestData) brd;
        createUserRd.setPostInfo(new PostInfoData(param));// 邮寄信息
        createUserRd.setSimCardNo(param.getString("SIM_CARD_NO"));// 157号码对应SIM卡号，同步写入固话资源信息
        createUserRd.setImsi(param.getString("IMSI"));// 157对应IMSI
        createUserRd.setSerialNumberBind(param.getString("SERIAL_NUMBER_BIND"));
        createUserRd.setSerialNumber(param.getString("SERIAL_NUMBER"));
        createUserRd.setSuperBankCode(param.getString("SUPER_BANK_CODE"));
        createUserRd.setOpenType(param.getString("OPEN_TYPE","0"));//受理类型：0非买断；1买断
        createUserRd.setGiftTelphone("0");//是否赠送话机：0否；1是
        createUserRd.setAreaType(param.getString("AREA_TYPE"));//1:海口;2:农村;3:各市县城;4:铁通省分免费
        // 预付费1后付费0
//fengxx        String prepayTag = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PRODUCT", "PRODUCT_ID", "PREPAY_TAG", createUserRd.getMainProduct().getProductId());
//        brd.getUca().getUser().setPrepayTag(prepayTag);
        
        String prepayTag = "";
    	IData prepayTagDataset = UpcCall.queryOfferByOfferId("P", createUserRd.getMainProduct().getProductId(), "Y");
		if(IDataUtil.isNotEmpty(prepayTagDataset)){
			prepayTag=prepayTagDataset.getString("PREPAY_TAG");
		}
		brd.getUca().getUser().setPrepayTag(prepayTag);
        this.bulidDeviceInfoData(param, createUserRd);
    }

    /**
     * @Function: bulidDeviceInfoData()
     * @Description: 构造设备信息
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: yxd
     * @date: 2014-7-25 下午7:56:21 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-25 yxd v1.0.0 修改原因
     */
    private void bulidDeviceInfoData(IData param, CreateUserRequestData brd) throws Exception
    {
        if (StringUtils.isBlank(param.getString("Device_Str")))
        {
            return;
        }
        IDataset deviceSet = new DatasetList(param.getString("Device_Str"));
        List<DeviceInfoData> deviceList = new ArrayList<DeviceInfoData>();
        if (DataSetUtils.isNotBlank(deviceSet))
        {
            DeviceInfoData deviceInfo = null;
            for (Object obj : deviceSet)
            {
                IData devData = (IData) obj;
                deviceInfo = new DeviceInfoData();
                // [{"DEVICE_KIND_CODE":"02","DEVICE_KIND":"TD座机","SALE_TYPE_CODE":"5424","SALE_TYPE":"TD机1","SALE_PRICE":"10","USE_TYPE_CODE":"4","USE_TYPE":"赠送","MATERIAL_CODE":"6324"}]
                deviceInfo.setDeviceCode(devData.getString("MATERIAL_CODE"));// 物品编码
                deviceInfo.setDeviceKindCode(devData.getString("DEVICE_KIND_CODE"));// 02
                deviceInfo.setDevicePrice(devData.getString("SALE_PRICE"));
                deviceInfo.setDeviceTypeCode("W");
                deviceInfo.setSaleTypeCode(devData.getString("SALE_TYPE_CODE"));
                deviceInfo.setSaleTypeDesc(devData.getString("SALE_TYPE"));
                deviceInfo.setUseTypeCode(devData.getString("USE_TYPE_CODE"));
                deviceList.add(deviceInfo);
            }
        }
        brd.setDeviceList(deviceList);
        if(deviceList.size() > 0)
        {
        	brd.setGiftTelphone("1");//是否赠送话机：0否；1是
        }
    }

    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-7-9 上午10:13:42 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-7-9 chengxf2 v1.0.0 修改原因
     */
    public BaseReqData getBlankRequestDataInstance()
    {
        return new CreateUserRequestData();
    }
}
