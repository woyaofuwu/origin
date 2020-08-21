
package com.asiainfo.veris.crm.order.soa.person.busi.twoDimensionCode;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqInstId;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.twodimension.TwoDimensionCodeQry;

/**
 * 二维码数据操作服务
 * 
 * @author dengshu
 * @date 2014年5月21日-下午4:53:37
 */
public class TwoDimCodeManageSVC extends CSBizService
{

    /**
     * 加入操作参数
     * 
     * @param dataset
     * @param modifyTag
     * @throws Exception
     */
    private void addModifyTag(IDataset baseInfo, String modifyTag) throws Exception
    {
        if (baseInfo == null)
        {
            return;
        }

        for (int i = 0; i < baseInfo.size(); i++)
        {
            IData tdata = baseInfo.getData(i);
            tdata.put("MODIFY_TAG", modifyTag);
        }
    }

    /**
     * 审批二维码
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset approvalTwoDimCode(IData inparams) throws Exception
    {
        int updateFlag = Dao.executeUpdateByCodeCode("TD_B_BARCODE", "UPD_BARCODE_STATE", inparams);
        // 结果集
        IDataset result = new DatasetList();
        IData data = new DataMap();

        if (updateFlag > 0)
        {
            data.put("SUCCESS", true);
        }
        else
        {
            data.put("SUCCESS", false);
        }

        return result;
    }

    /** 删除 */
    public IDataset deleteTwoDimCode(IData inparams) throws Exception
    {
        boolean flag = false;
        // 插入历史表
        this.insertBarcodeHistory(inparams, BofConst.MODIFY_TAG_DEL);

        IDataset dataSet = new DatasetList();
        inparams.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
        dataSet.add(inparams);
        this.insertConfigHistory(dataSet);

        // 删除实体数据
        Dao.delete("TD_B_BARCODE_CONFIG", dataSet, new String[]
        { "BARCODE_ID" }, Route.CONN_CRM_CEN);
        Dao.delete("TD_B_BARCODE", inparams);

        // 删除成功标示
        flag = true;

        IDataset result = new DatasetList();
        IData data = new DataMap();
        data.put("SUCCESS", flag);

        return result;
    }

    /**
     * 记录操作历史表TD_BH_BARCODE
     * 
     * @param dataset
     * @throws Exception
     */
    private void insertBarcodeHistory(IData inparams, String modifyTag) throws Exception
    {
        if (inparams == null)
        {
            return;
        }

        IData date = new DataMap();
        date.put("BARCODE_ID", inparams.getString("BARCODE_ID"));
        // 查找原始数据
        IDataset baseInfo = TwoDimensionCodeQry.queryBaseTwoDimensionCode(date, null);
        // 加入操作标示
        this.addModifyTag(baseInfo, modifyTag);
        if (!baseInfo.isEmpty())
        {
            // 生成主键
            String hisId = Dao.getSequence(SeqInstId.class, Route.CONN_CRM_CEN);
            baseInfo.getData(0).put("BARCODE_HISID", hisId);
        }
        Dao.insert("TD_BH_BARCODE", baseInfo, Route.CONN_CRM_CEN);
    }

    /**
     * 记录操作历史表
     * 
     * @param dataset
     * @throws Exception
     */
    private void insertConfigHistory(IDataset inparams) throws Exception
    {
        if (inparams == null)
        {
            return;
        }
        // 查找原始数据
        for (int i = 0; i < inparams.size(); i++)
        {

            IData tdata = inparams.getData(i);
            IDataset detailInfo = TwoDimensionCodeQry.queryDetailTwoDimensionCode(tdata);

            if (!detailInfo.isEmpty())
            {
                // 数据库中查询到数据
                IData oldDate = detailInfo.getData(0);
                // 历史表主键
                String hisId = Dao.getSequence(SeqInstId.class, Route.CONN_CRM_CEN);
                // 用新ID覆盖原来ID
                oldDate.put("ECONFIG_ID", hisId);
                // 修改标记
                oldDate.put("MODIFY_TAG", tdata.getString("MODIFY_TAG"));
                Dao.insert("TD_BH_BARCODE_CONFIG", detailInfo, Route.CONN_CRM_CEN);
            }

        }

    }

    /** 查询 */
    public IDataset queryTwoDimCode(IData inparams) throws Exception
    {

        return TwoDimensionCodeQry.queryBaseTwoDimensionCode(inparams, this.getPagination());
    }

    /**
     * 查询明细
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset queryTwoDimCodeDetail(IData inparams) throws Exception
    {
        IDataset baseInfo = TwoDimensionCodeQry.queryBaseTwoDimensionCode(inparams, null);
        IDataset detailInfo = TwoDimensionCodeQry.queryDetailTwoDimensionCode(inparams);
        // 封装结果
        IDataset result = new DatasetList();
        IData data = new DataMap();
        data.put("baseInfo", baseInfo);
        data.put("detailInfo", detailInfo);
        result.add(data);

        return result;
    }

    /**
     * 保存二维码信息
     * 
     * @param inparams
     * @return
     * @throws Exception
     */
    public IDataset saveTwoDimCode(IData inparams) throws Exception
    {

        boolean flag = false;
        // 新增数据
        if (inparams.getBoolean("createFlag"))
        {
            flag = Dao.insert("TD_B_BARCODE", inparams);
        }
        else
        {
            this.insertBarcodeHistory(inparams, BofConst.MODIFY_TAG_UPD);
            Dao.executeUpdateByCodeCode("TD_B_BARCODE", "UPD_BARCODE_DTAIL", inparams);
        }
        //
        submitEditPart(inparams);

        flag = true;

        IDataset result = new DatasetList();
        IData data = new DataMap();
        data.put("SUCCESS", flag);

        return result;
    }

    /**
     * 操作 TD_B_BARCODE_CONFIG表
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset submitEditPart(IData input) throws Exception
    {
        // 提交数据集
        IDataset submitInfoSet = input.getDataset("edit_table");
        if (submitInfoSet == null || submitInfoSet.size() == 0)
        {
            return null;
        }
        // 保存提交参数
        IDataset addDataset = new DatasetList();
        IDataset delDataset = new DatasetList();
        IDataset uptDataset = new DatasetList();
        IDataset resultDataset = new DatasetList();
        IData counts = new DataMap();// 新增记录数, 删除记录数, 修改记录数
        IData baseData = new DataMap();// 新增记录数, 删除记录数, 修改记录数
        String tableName = "TD_B_BARCODE_CONFIG";

        // 拼入, 下面拼入其他字段
        baseData.put("START_DATE", input.getString("START_DATE"));
        baseData.put("END_DATE", input.getString("END_DATE"));
        baseData.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));
        baseData.put("UPDATE_DEPART_ID", input.getString("UPDATE_DEPART_ID"));
        baseData.put("UPDATE_STAFF_ID", input.getString("UPDATE_STAFF_ID"));
        baseData.put("UPDATE_TIME", input.getString("UPDATE_TIME"));
        baseData.put("BARCODE_ID", input.getString("BARCODE_ID"));

        // 获取操作数据
        for (int i = 0; i < submitInfoSet.size(); i++)
        {

            IData data = submitInfoSet.getData(i);
            // 加入基础数据
            data.putAll(baseData);

            if (BofConst.MODIFY_TAG_ADD.equals(data.getString("tag", "")))
            {// 新增
                // 生成主键
                String econfigId = Dao.getSequence(SeqInstId.class, Route.CONN_CRM_CEN);
                data.put("ECONFIG_ID", econfigId);
                addDataset.add(data);
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(data.getString("tag", "")))
            {// 删除
                data.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                delDataset.add(data);
            }
            else if (BofConst.MODIFY_TAG_UPD.equals(data.getString("tag", "")))
            {// 修改
                // 用于保存历史数据标示
                data.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD);
                uptDataset.add(data);
            }

        }

        // 批量持久化
        if (addDataset.size() != 0)
        {
            int[] countInsert = Dao.insert(tableName, addDataset);
            counts.put("ADD_COOUNTS", countInsert.length);
        }

        if (delDataset.size() != 0)
        {
            // 写历史表
            insertConfigHistory(delDataset);

            int[] countDelete = Dao.delete(tableName, delDataset, new String[]
            { "ECONFIG_ID" }, Route.CONN_CRM_CEN);
            counts.put("DELETE_COOUNTS", countDelete.length);

        }

        if (uptDataset.size() != 0)
        {
            // 写历史表
            insertConfigHistory(uptDataset);

            int count = 0;
            for (Object tdata : uptDataset)
            {
                boolean flag = Dao.update(tableName, (IData) tdata, new String[]
                { "ECONFIG_ID" }, Route.CONN_CRM_CEN);
                if (flag)
                {
                    count++;
                }
            }
            counts.put("UPDATE_COOUNTS", count);
        }

        resultDataset.add(counts);

        return resultDataset;
    }

}
