/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BrandException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * @CREATED by gongp@2014-4-25 修改历史 Revision 2014-4-25 下午02:20:27
 */
public class FilteIncomePhoneSVC extends CSBizService
{

    private static final long serialVersionUID = 3656758960782596594L;

    /**
     * 开通、取消呼入限制，呼入限制增加、删除号码 必传字段：SERIAL_NUMBER(手机号码)
     * 
     * @param pd
     *            页面数据
     * @param data
     *            必传的一些参数
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-30
     */
    public IDataset filteIncomePhoneIntfReg(IData data) throws Exception
    {

        IDataUtil.chkParam(data, "SERIAL_NUMBER");
        IDataUtil.chkParam(data, "OPER_TYPE"); // 操作类型：0增加号码，1删除单个号码，2全部删除

        String serialNumber = data.getString("SERIAL_NUMBER");

        IDataset dataset = new DatasetList(data.getString("X_CODING_STR", "[]"));

        IDataset dealDatas = new DatasetList();
        for (int i = 0, size = dataset.size(); i < size; i++)
        {
            IData temp = dataset.getData(i);

            String filterNumber = "";
            if(!"2".equals(data.getString("OPER_TYPE"))){ //全部删除时不要校验
            	IDataUtil.chkParam(temp, "RSRV_STR1");
            	filterNumber = temp.getString("RSRV_STR1","");

            	if (filterNumber.equals(serialNumber))
            	{
            		// common.error("不能设置本身号码为拒接号码！");
            		CSAppException.apperr(CrmCommException.CRM_COMM_290);
            	}
            }
            IData filterData = new DataMap();
            filterData.put("REJECT_SN", filterNumber);
            filterData.put("REMARK", temp.getString("REMARK", "外围提供"));

            dealDatas.add(filterData);
        }

        String operType = data.getString("OPER_TYPE");
        IData inParam = new DataMap();

        inParam.put("OPEN_SMS", data.getString("X_TAG", "0"));
        inParam.put("SERIAL_NUMBER", serialNumber);
        inParam.put("OPER_TYPE", operType);
        inParam.put("SN_DATASET", dealDatas);

        String channelTag = data.getString("X_TAGCHAR", "1");

        if ("0".endsWith(channelTag) && "0".equals(operType))
        {
            dealDatas.clear();
            IData addData = new DataMap();
            String filterNumber = data.getString("SERIAL_NUMBER_A", "");
            if (filterNumber.equals(serialNumber))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_290);
            }
            addData.put("REJECT_SN", filterNumber);
            addData.put("REMARK", "外围提供");
            dealDatas.add(addData);
            inParam.put("SN_DATASET", dealDatas);
        }
        if ("0".endsWith(channelTag) && "2".equals(operType))
        {
            dealDatas.clear();
            IData delData = new DataMap();
            delData.put("REJECT_SN", "");
            delData.put("REMARK", "");
            dealDatas.add(delData);
            inParam.put("SN_DATASET", dealDatas);
        }

        if ("1".equals(operType) || "2".equals(operType))
        {

            return CSAppCall.call("SS.FilteIncomePhoneDelTradeRegSVC.tradeReg", inParam);

        }
        else if ("0".equals(operType))
        {

            return CSAppCall.call("SS.FilteIncomePhoneAddTradeRegSVC.tradeReg", inParam);
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_296);
        }

        return new DatasetList();
    }

    private String getDiscntCode() throws Exception
    {

        IDataset outDs = CommparaInfoQry.getCommPkInfo("CSM", "6010", "VPMN", "0898");

        if (IDataUtil.isEmpty(outDs))
        {
            // common.error("558601:获取优惠编码出错！");
            CSAppException.apperr(ElementException.CRM_ELEMENT_91);
        }
        return outDs.getData(0).getString("PARA_CODE1", "");// 优惠编码
    }

    public IDataset getFilteIncomePhoneSendSMSInfo(IData param) throws Exception
    {

        IData commInfo = new DataMap();

        IDataset outDs1 = UserOtherInfoQry.getUserOther(param.getString("USER_ID"), "271");// 已存在的拒接短信通知

        if (outDs1 != null && outDs1.size() > 0)
        {
            commInfo.put("SENDSMS_TAG", "1");// 1:已开通短信提醒；0：没有开通短信提醒
            commInfo.put("SENDSMS", "开通");
        }
        else
        {
            commInfo.put("SENDSMS_TAG", "0");// 1:已开通短信提醒；0：没有开通短信提醒
            commInfo.put("SENDSMS", "关闭");
        }

        IDataset outDs = UserOtherInfoQry.getUserOther(param.getString("USER_ID"), "1301");// 已存在的拒接号码

        if (IDataUtil.isNotEmpty(outDs))
        {
            commInfo.put("PHONE_SIZE", outDs.size());// 已存在的拒接号码记录总数
        }
        else
        {
            commInfo.put("PHONE_SIZE", "0");// 已存在的拒接号码记录总数
            // common.warn("用户还没有申请来电拒接，不能办理本业务");
            CSAppException.apperr(CrmUserException.CRM_USER_708);
        }

        IDataset dataset = new DatasetList();

        dataset.add(commInfo);

        return dataset;

    }

    public IDataset getFilteIncomePhoneTradeInfo(IData param) throws Exception
    {

        if ("G011".equals(param.getString("BRAND_CODE")))
        {// G011品牌不能办理
            // common.error("558607:业务受理条件判断：您当前的品牌是公话业务(G011)，不能办理此业务！");
            CSAppException.apperr(BrandException.CRM_BRAND_16);
        }
        if ("G005".equals(param.getString("BRAND_CODE")))
        {// G005品牌不能办理
            // common.error("558607:业务受理条件判断：您当前的品牌是随E行(G005)，不能办理此业务！");
            CSAppException.apperr(BrandException.CRM_BRAND_17);
        }

        IDataset outDs = UserOtherInfoQry.getUserOther(param.getString("USER_ID"), "1301");// 已存在的拒接号码
        IData commInfo = new DataMap();

        commInfo.put("PHONE_SIZE", outDs.size());// 已存在的拒接号码记录总数

        IDataset outDs1 = UserOtherInfoQry.getUserOther(param.getString("USER_ID"), "271");

        if (IDataUtil.isNotEmpty(outDs1))
        {
            commInfo.put("SENDSMS_TAG", "1");// 1:已开通短信提醒；0：没有开通短信提醒
        }
        else
        {
            commInfo.put("SENDSMS_TAG", "0");// 1:已开通短信提醒；0：没有开通短信提醒
        }

        commInfo.put("FILTERPHONEDS", outDs);

        commInfo.put("DISCNT_CODE", getDiscntCode()); // 获取优惠编码

        IDataset outDs2 = RelaUUInfoQry.getRelationUUInfoByDeputySn(param.getString("USER_ID"), "20", null);

        if (IDataUtil.isNotEmpty(outDs2))
        {
            commInfo.put("VPMN_TAG", "1");// VPMN用户标识:1表示为vpmn用户，默认为0
        }
        else
        {
            commInfo.put("VPMN_TAG", "0");// VPMN用户标识:1表示为vpmn用户，默认为0
        }

        IDataset outDs3 = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(param.getString("USER_ID"), "5860");

        if (IDataUtil.isNotEmpty(outDs3))
        {// /*监务通用户不允许办理增加呼入限制号码*/
            commInfo.put("JWTONG_TAG", "1");
        }
        else
        {
            commInfo.put("JWTONG_TAG", "0");
        }

        IDataset dataset = new DatasetList();

        dataset.add(commInfo);

        return dataset;
    }

}
