package com.asiainfo.veris.crm.iorder.web.igroup.esop.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ailk.biz.BizVisit;
import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.file.FileMan;
import com.ailk.common.file.FileUtil;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.FileManHelper;
import com.ailk.common.util.IFileAction;
import com.ailk.common.util.Utility;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.client.ServiceFactory;

public class FileServletEsop extends HttpServlet 
{
	private static final long serialVersionUID = -7701665746267673724L;
	protected IData initConfig;
	protected IFileAction action;
	
	public void init(ServletConfig config) throws ServletException {
        super.init(config);
        action = FileUtil.getFileAction();
    }
	
	public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
		HttpServletRequest hreq = (HttpServletRequest) req;
		HttpServletResponse hres = (HttpServletResponse) res;

		String fileidStr = req.getParameter("FILE_ID");
		if(StringUtils.isEmpty(fileidStr))
		{
			return;
		}
		
		String[] fileids = fileidStr.split("\\|");
		if(fileids.length != 3)
		{
			return;
		}
		
		String fileid = fileids[0];
		String subibsysid = fileids[1];
		String actionName = fileids[2];

		//设置FileAction的context参数
		//action.getContext().put(IFileAction.FILE_ACTION_PRIV_CHECK_TAG, "false");
		//设置Visit
		action.setVisit(new BizVisit());

		try {
			if ("upload".equals(actionName)) {
				
			}
			if ("download".equals(actionName)) {
				download(this.initConfig, hreq, hres, fileid,subibsysid);
			} else {
				
			}
		} catch (Exception e) {
			Utility.getBottomException(e).printStackTrace();
		}finally{
			//action.clearContext();
			//action.clearVisit();
		}
	}
	
	protected String download(IData config, HttpServletRequest request, HttpServletResponse response, String fileId,String subibsysid) throws Exception {
		File file = null;
		try {
			
			IDataInput param = new DataInput();
			param.getData().put("FILE_ID", fileId);
			param.getData().put("SUB_IBSYSID", subibsysid);
			
	        IDataOutput out = ServiceFactory.call("SS.WorkformAttachSVC.qryExistsFile", param);
	        IDataset attachInfos = out.getData();
	        
	        if(DataUtils.isEmpty(attachInfos))
	        {
	        	return "fail";
	        }
	        
	        String real = attachInfos.first().getString("ATTACH_NAME", "");
	        file = action.download(fileId, false);
			
	        FileManHelper.writeInputToOutput(new FileInputStream(file), FileMan.getOutputStreamByImpExp(request, response, real));
		} catch (Exception e) {
			Utility.error(Utility.getBottomException(e));
		} finally {
			if ((!(GlobalCfg.getProperty("fileman.mode", "local").equals("local"))) && (file != null)
					&& (file.exists())) {
				file.delete();
				if (file.exists()) {
					file.delete();
				}
			}
		}

		return "ok";
	}

	public void destroy(){
		if(null != action){
			//action.clearContext();
			//action.clearVisit();
		}
	}

}
