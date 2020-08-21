
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class MFileInfoQrySVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 根据STAFF_ID查询该员工导入的文件类型
     * 
     * @author weixb3
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryFileInfo(IData data) throws Exception
    {
        String createStaffID = data.getString("CREATE_STAFF_ID");
        String fileName = data.getString("FILE_NAME");
        String startDate = data.getString("START_DATE");
        String endDate = data.getString("END_DATE");
        String fileCode = data.getString("FILE_CODE");
        return MFileInfoQry.queryFileInfo(createStaffID, fileName, startDate, endDate, fileCode, getPagination());
    }
    
    /**
     * @description 根据文件编号查询文件对应的信息
     * @author xunyl
     * @date 2013-12-04
     */
    public static IDataset qryFileInfoListByFileID(IData data)throws Exception{
        String fileId = data.getString("FILE_ID");
        return MFileInfoQry.qryFileInfoListByFileID(fileId);
    }
    
    /**
     * @description 根据文件名称获取文件列表
     * @author xunyl
     * @date 2015-03-30
     */
    public static IDataset qryFileInfoListByFileName(IData data)throws Exception{
        String fileId = data.getString("FILE_NAME");
        return MFileInfoQry.qryFileInfoListByFileName(fileId);
    }
	
	 public IDataset qryFileInfoByFileId(IData iData) throws Exception
    {
    	return MFileInfoQry.qryFileInfoListByFileID(IDataUtil.chkParam(iData, "FILE_ID"));
    }

}
