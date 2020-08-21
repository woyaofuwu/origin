
package com.asiainfo.veris.crm.order.soa.person.busi.bulletinfo;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.BulletException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.sysorg.StaffInfoQry;

public class BulletCreateBean extends CSBizBean
{

    /**
     * 公告发布接口
     * 
     * @param input
     * @return
     * @throws Exception
     * @author zhouwu
     * @date 2014-07-01 20:32:25
     */
    public IData createBullet(IData input) throws Exception
    {
        String topic = input.getString("INFO_TOPIC");// 公告主题
        String content = input.getString("INFO_CONTENT");// 公告内容
        String staffid = input.getString("STAFF_ID");// 工号
        String rece = input.getString("RECE_OBJS");// 接收对象

        if (StringUtils.isBlank(topic))
        {
            // 参数缺失:需要传入公告主题
            CSAppException.apperr(BulletException.CRM_BULLET_1);
        }

        if (StringUtils.isBlank(content))
        {
            // 参数缺失:需要传入公告内容
            CSAppException.apperr(BulletException.CRM_BULLET_3);
        }

        if (StringUtils.isBlank(staffid))
        {
            // 参数缺失:需要传入登录员工ID
            CSAppException.apperr(BulletException.CRM_BULLET_2);
        }

        if (StringUtils.isBlank(rece))
        {
            // 参数缺失:需要传入公告接收对象
            CSAppException.apperr(BulletException.CRM_BULLET_4);
        }

        IDataset staffInfo = StaffInfoQry.qryStaffInfoByStaffId(staffid);
        if (IDataUtil.isEmpty(staffInfo))
        {
            // 发布工号[%s]不存在，请确认
            CSAppException.apperr(BulletException.CRM_BULLET_5, staffid);
        }

        boolean isProvince = false;
        String[] reces = StringUtils.split(rece, ",");
        for (int i = 0, length = reces.length; i < length; i++)
        {
            if (StringUtils.equals(reces[i], "HAIN"))
            {
                isProvince = true;// 全省发布
                break;
            }
        }

        String seqInfoId = SeqMgr.getInfoId();
        String sendMonth = SysDateMgr.getMonthForDate(SysDateMgr.getSysDate());

        IData param = new DataMap();
        param.put("INFO_ID", seqInfoId);
        param.put("INFO_TOPIC", topic);
        param.put("INFO_CONTENT", content);
        param.put("INFO_TYPE", "2");// 信息类型：2-公告
        param.put("INFO_STATUS", "0");
        param.put("SEND_MONTH", sendMonth);
        param.put("INFO_AUTH", staffid);
        param.put("DEPART_ID", input.getString("DEPART_ID"));
        param.put("CITY_CODE", input.getString("CITY_CODE"));
        param.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));
        param.put("INFO_SEND_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));

        Dao.insert("TF_F_INFO", param);

        if (isProvince)
        {
            param.put("INST_EPARCHY_CODE", "HAIN");
            insertInfoInstByArea(param);
        }
        else
        {
            for (int i = 0, length = reces.length; i < length; i++)
            {
                param.put("INST_EPARCHY_CODE", reces[i]);
                insertInfoInstByArea(param);
            }
        }

        // 需发给发布公告者本身
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT F.* FROM TF_F_INFO F INNER JOIN TF_F_INFO_INSTANCE FI ON F.INFO_ID=FI.INFO_ID ");
        sql.append("AND F.INFO_ID=:INFO_ID ");
        sql.append("AND F.INFO_TYPE='2' ");
        sql.append("AND FI.RECE_OBJ_TYPE='2' ");
        sql.append("AND FI.RECE_OBJ=:STAFF_ID");

        IData data = new DataMap();
        data.put("INFO_ID", seqInfoId);
        data.put("STAFF_ID", staffid);

        int count = Dao.getCount(sql.toString(), data);

        if (count == 0)
        {
            IData infoInst = new DataMap();
            String sqlInfoInstId = SeqMgr.getInfoInstId();
            infoInst.put("INST_ID", sqlInfoInstId);
            infoInst.put("INFO_ID", seqInfoId);
            infoInst.put("INST_STATUS", "0");
            infoInst.put("RECE_OBJ", staffid);
            infoInst.put("RECE_OBJ_TYPE", "2");
            infoInst.put("SEND_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND));
            infoInst.put("SEND_MONTH", SysDateMgr.getMonthForDate(SysDateMgr.getSysDate()));

            Dao.insert("TF_F_INFO_INSTANCE", infoInst);
        }

        IData backInfo = new DataMap();

        backInfo.put("X_RESULTINFO", "公告发布成功");

        return backInfo;
    }

    public void insertInfoInstByArea(IData param) throws Exception
    {
        String info_id = param.getString("INFO_ID");
        String send_time = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);
        String send_month = SysDateMgr.getMonthForDate(SysDateMgr.getSysDate());
        String eparchy_code = param.getString("INST_EPARCHY_CODE", "");

        IData data = new DataMap();
        data.put("INFO_ID", info_id);
        data.put("SEND_TIME", send_time);
        data.put("SEND_MONTH", send_month);
        data.put("EPARCHY_CODE", eparchy_code);

        Dao.executeUpdateByCodeCode("TF_F_INFO_INSTANCE", "INSERT_INFO_INSTANCE", data);
    }

}
