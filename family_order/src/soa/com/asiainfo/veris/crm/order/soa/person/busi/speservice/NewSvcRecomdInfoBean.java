
package com.asiainfo.veris.crm.order.soa.person.busi.speservice;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.RecommparaQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserRecommInfoQry;

public class NewSvcRecomdInfoBean extends CSBizBean
{
    private static Logger logger = Logger.getLogger(NewSvcRecomdInfoBean.class);

    /**
     * 获取延迟推荐参数信息
     * 
     * @throws Exception
     * @return String
     */
    public String getDelayDay(IData param) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));

        IDataset delayList = new DatasetList();
        delayList = this.getUserRecommStatic(inparam);
        if (delayList.size() > 0)
        {
            IData data = delayList.getData(0);

            return data.getString("DATA_ID", "");
        }
        return null;
    }

    /**
     * 获取历史消费信息
     * 
     * @param pd
     * @param td
     * @throws Exception
     * @return IDataset
     */
    public IDataset getHistoryConsumeInfo(IData param) throws Exception
    {

        IDataset dataset = new DatasetList();
        IData inparam = new DataMap();
        inparam.put("USER_ID", param.getString("USER_ID"));
        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));

        IDataset historyConsumeAction = new DatasetList();
        historyConsumeAction = this.getUserWholeinfo(inparam);

        if (historyConsumeAction.size() > 0)
        {
            IData historyConsumeInfo = historyConsumeAction.getData(0);
            dataset.add(historyConsumeInfo);
        }
        return dataset;
    }

    /**
     * 获取历史推荐信息记录
     * 
     * @param pd
     * @param td
     * @throws Exception
     * @return IDataset
     */
    public IDataset getHistoryRecomdInfo(IData param) throws Exception
    {

        IData inparam = new DataMap();
        inparam.put("USER_ID", param.getString("USER_ID"));
        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        inparam.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
        inparam.put("RECOMM_TAG", "1");// 推荐标志 0=未推荐 1=已推荐

        return getUserRecommList(inparam);
    }

    /**
     * 获取推荐用语
     * 
     * @param pd
     * @param td
     * @param type
     *            0=获取推荐产品用语 1=获取推荐优惠用语 2=推荐服务用语 3=推荐平台业务用语 4=推荐活动用语
     * @throws Exception
     * @return String
     */
    public IDataset getRecomdString(IData param) throws Exception
    {

        IDataset dataset = new DatasetList();
        String type = param.getString("TYPE");
        IDataset svcInfolist = new DatasetList();

        IData inparam = new DataMap();

        inparam.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));

        if (type.equals("1"))
        { // 获取优惠推荐用语
            inparam.put("RECOMM_TYPE", "1");
            inparam.put("ELEMENT_ID", param.getString("recomd_DISCNT"));
        }
        else if (type.equals("2"))
        {// 获取优惠服务用语
            inparam.put("RECOMM_TYPE", "2");
            inparam.put("ELEMENT_ID", param.getString("recomd_SERVICE"));
        }
        else if (type.equals("0"))
        {// 获取推荐产品用语
            inparam.put("RECOMM_TYPE", "0");
            inparam.put("ELEMENT_ID", param.getString("recomd_PRODUCT"));
        }
        else if (type.equals("3"))
        {// 获取推荐平台服务用语
            inparam.put("RECOMM_TYPE", "3");
            inparam.put("ELEMENT_ID", param.getString("recomd_PLATSVC"));
        }
        else if (type.equals("4"))
        {// 获取推荐活动用语
            inparam.put("RECOMM_TYPE", "4");
            inparam.put("ELEMENT_ID", param.getString("recomd_ACTION"));
        }
        // 获取推荐用语
        svcInfolist = getUserRecommPara(inparam);

        if (svcInfolist.size() > 0)
        {
            IData data = svcInfolist.getData(0);
            dataset.add(data);
        }
        return dataset;
    }

		/**
     * 根据服务号码获取userId
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserInfo(String sn) throws Exception{
    	IDataset userinfo = UserInfoQry.getUserInfoBySerailNumber("0",sn);
    	return userinfo;
    }

    public IDataset getUserRecomm(IData param) throws Exception
    {

        return UserRecommInfoQry.getUserRecomm(param);
    }

    public IDataset getUserRecommList(IData param) throws Exception
    {

        return UserRecommInfoQry.getUserRecommList(param);
    }

    public IDataset getUserRecommPara(IData param) throws Exception
    {
        return RecommparaQry.getUserRecommPara(param.getString("RECOMM_TYPE"), param.getString("ELEMENT_ID"), param.getString("RECOMM_SOURCE"), param.getString("EPARCHY_CODE"));
    }

    public IDataset getUserRecommStatic(IData param) throws Exception
    {

        String eparchy_code = param.getString("EPARCHY_CODE");
        return StaticInfoQry.getUserRecommStatic(eparchy_code);
    }

    public IDataset getUserWholeinfo(IData param) throws Exception
    {

        return UserInfoQry.getUserWholeinfo(param);
    }

    public void insSms(IData sendData) throws Exception
    {
        Dao.insert("TI_O_SMS", sendData);
    }

}
