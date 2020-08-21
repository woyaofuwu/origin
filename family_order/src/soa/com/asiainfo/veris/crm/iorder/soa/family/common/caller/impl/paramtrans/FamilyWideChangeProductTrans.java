
package com.asiainfo.veris.crm.iorder.soa.family.common.caller.impl.paramtrans;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.KeyConstants;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.caller.interfaces.IParamTrans;
import com.asiainfo.veris.crm.iorder.soa.family.common.util.ParamTransUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;

/**
 * 家庭创建，已有宽带参数转换 对于宽带产品不变的不调产品变更服务，固话和tv继续往下走
 * 
 * @author duhj
 */
public class FamilyWideChangeProductTrans implements IParamTrans
{

    @Override
    public IData getTransParamters(IData input) throws Exception
    {
        IData widenetData = new DataMap();

        widenetData.putAll(input);

        // 校验入参
        checkInParam(widenetData);

        // 产品信息转换
        transProudctInfo(widenetData);

        if ("Y".equals(widenetData.getString("IS_CHANGE")))
        {
            // 设置默认参数
            setDefaultParamValue(widenetData);
        }

        // IMS参数转换 TV+参数转换
        transImsAndTVTradeParam(widenetData,input);

        return widenetData;
    }

    /**
     * 设置默认参数值
     * 
     * @param input
     * @throws Exception
     */
    public void setDefaultParamValue(IData input) throws Exception
    {
        // 传递登记服务名
        input.put("CALL_REGSVC", "SS.WidenetChangeProductNewRegSVC.tradeReg");// 宽带产品变更接口
        input.put("TRADE_TYPE_CODE", FamilyConstants.ROLE_TRADE_TYPE.WIDE_CHANGE);
        input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));// 这里看下是否宽带号码，应该451过来是
        String mainSerialNumber = input.getString("MEMBER_MAIN_SN");
        input.put("AUTH_SERIAL_NUMBER", mainSerialNumber);
        input.put("BOOKING_DATE", getBookingDate());
        input.put("CHANGE_TYPE", "1");// 只变产品，没有营销活动
        input.put("CHANGE_UP_DOWN_TAG", "1");// //是否升档标记，后面看加不加
        input.put("WIDE_USER_CREATE_SALE_ACTIVE", "1");// //是否要变宽带产品 后面看加不加

    }

    /*
     * 获取预约的产品变更时间
     */
    private String getBookingDate() throws Exception
    {
        String next_date = SysDateMgr.getDateNextMonthFirstDay(SysDateMgr.getSysTime());
        return next_date;
    }

    /**
     * 转换产品信息
     * 
     * @param input
     * @throws Exception
     */
    public void transProudctInfo(IData input) throws Exception
    {
        String userProductId = input.getString("USER_PRODUCT_ID");// 已有宽带产品变更
        String wideProductId = ParamTransUtil.findRoleMainOffer(input);// 家庭需要变更的新产品id
        if (StringUtils.isEmpty(wideProductId))
        {
            CSAppException.apperr(WidenetException.CRM_WIDENET_9);
        }

        if (StringUtils.equals(wideProductId, userProductId))
        {// 产品一样没必要变更产品,只是加入家庭圈即可
            input.put("IS_CHANGE", "N");
            return;
        }
        else
        {
            input.put("IS_CHANGE", "Y");
        }

        input.put("IS_CHANGE", "Y");
        input.put("NEW_PRODUCT_ID", wideProductId);// 需要变更的宽带产品id
        // 获取主产品下的必选元素
        String svcName = "CS.SelectedElementSVC.getUserElements";
        IData data = new DataMap();
        data.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        data.put("TRADE_TYPE_CODE", FamilyConstants.ROLE_TRADE_TYPE.WIDE_CHANGE);
        data.put("USER_PRODUCT_ID", input.getString("USER_PRODUCT_ID"));
        data.put("BOOKING_DATE", getBookingDate());// 下月开始
        data.put("NEW_PRODUCT_ID", wideProductId);
        data.put("USER_ID", input.getString("WIDE_USER_ID"));
        data.put("FIRST_DATE", "");
        data.put("ACCT_DAY", "1");// 这个参数？
        IDataset wideElementsInfos = CSAppCall.call(svcName, data);
        if (IDataUtil.isNotEmpty(wideElementsInfos))
        {
            IDataset selectedElements = new DatasetList();
            IDataset wideSelectedElements = wideElementsInfos.getData(0).getDataset("SELECTED_ELEMENTS");
            if(IDataUtil.isNotEmpty(wideSelectedElements)){
                for(int i=0;i<wideSelectedElements.size();i++){
                    IData tempWide = wideSelectedElements.getData(i);
                    String modifyTag = tempWide.getString("MODIFY_TAG");
                    if(modifyTag==BofConst.MODIFY_TAG_ADD ||modifyTag==BofConst.MODIFY_TAG_DEL||modifyTag==BofConst.MODIFY_TAG_UPD){
                        IData tempElement = new DataMap();
                        tempElement.put("ELEMENT_ID", tempWide.getString("ELEMENT_ID"));
                        tempElement.put("ELEMENT_TYPE_CODE", tempWide.getString("ELEMENT_TYPE_CODE"));
                        tempElement.put("PRODUCT_ID", tempWide.getString("PRODUCT_ID"));
                        tempElement.put("PACKAGE_ID", tempWide.getString("PACKAGE_ID"));
                        tempElement.put("MODIFY_TAG", tempWide.getString("MODIFY_TAG"));
                        tempElement.put("START_DATE", tempWide.getString("START_DATE"));
                        tempElement.put("END_DATE", tempWide.getString("END_DATE"));
                        tempElement.put("INST_ID", tempWide.getString("INST_ID"));
                        selectedElements.add(tempElement);                        
                    }
                }
            }
            
            
            input.put("SELECTED_ELEMENTS", selectedElements);

        }
    }

    /**
     * 宽带产品变更，下面的子角色有新开，有变更， 捞宽带资料表数据传递给下一层
     * 
     * @param input
     * @throws Exception
     */
    public void transImsAndTVTradeParam(IData param,IData input) throws Exception
    {
        if (input.containsKey("SUB_ROLES"))
        {
            IDataset subRoles = new DatasetList(input.getString("SUB_ROLES", "[]"));

            if (IDataUtil.isNotEmpty(subRoles))
            {

                IDataset widenetInofs = WidenetInfoQry.getUserWidenetInfo(input.getString("WIDE_USER_ID"));
                if (IDataUtil.isEmpty(widenetInofs))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "查询用户宽带信息出错：无记录！");
                }
                IData tempWideInfo = widenetInofs.getData(0);
                for (Object obj : subRoles)
                {
                    IData subRole = (DataMap) obj;
                    subRole.put(Route.ROUTE_EPARCHY_CODE, subRole.getString(KeyConstants.EPARCHY_CODE));
                    subRole.put("CONTACT", tempWideInfo.getString("CONTACT"));
                    subRole.put("CONTACT_PHONE", tempWideInfo.getString("CONTACT_PHONE"));
                    subRole.put("PHONE", tempWideInfo.getString("PHONE"));
                    subRole.put("STAND_ADDRESS", tempWideInfo.getString("STAND_ADDRESS"));
                    subRole.put("DETAIL_ADDRESS", tempWideInfo.getString("DETAIL_ADDRESS"));
                    subRole.put("STAND_ADDRESS_CODE", tempWideInfo.getString("STAND_ADDRESS_CODE"));
                    subRole.put("AREA_CODE", tempWideInfo.getString("RSRV_STR4"));
                    subRole.put("WIDE_PRODUCT_TYPE", tempWideInfo.getString("RSRV_STR2"));
                    subRole.put("DEVICE_ID", tempWideInfo.getString("RSRV_NUM1"));
                    subRole.put("BUSI_TYPE", input.getString("BUSI_TYPE"));
                }
            }

            input.put("SUB_ROLES", subRoles);
            param.put("SUB_ROLES", subRoles);
        }

    }

    /**
     * 参数校验
     * 
     * @param input
     * @throws Exception
     */
    public void checkInParam(IData input) throws Exception
    {

        IDataUtil.chkParam(input, "USER_PRODUCT_ID");// 已有宽带的产品id
        IDataUtil.chkParam(input, "WIDE_USER_ID");// 已有宽带的userId
        IDataUtil.chkParam(input, "WIDE_TYPE");// 已有宽带的产品类型

    }
}
