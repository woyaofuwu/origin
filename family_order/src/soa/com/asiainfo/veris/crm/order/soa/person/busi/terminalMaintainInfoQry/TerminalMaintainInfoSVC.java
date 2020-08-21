package com.asiainfo.veris.crm.order.soa.person.busi.terminalMaintainInfoQry;

import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * REQ201702200014关于在NGBOSS系统中开发终端维修查询界面的申请
 * 
 * @author zhuoyingzhi
 * @date 20170316
 * 
 */
public class TerminalMaintainInfoSVC extends CSBizService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1814212176503925400L;
	protected static Logger log = Logger.getLogger(TerminalMaintainInfoSVC.class);

	/**
	 * 终端维修情况查询(调用终端公司接口进行查询)
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset terminalMaintainInfoQry(IData input) throws Exception {
		try {
			// 获取参数信息
			IDataset commparaInfos = CommparaInfoQry.getOnlyByAttr("CSM", "1679", "0898");
			IData data = commparaInfos.getData(0);
			// 地址
			input.put("URL", data.getString("PARA_CODE17"));
			// 外网地址
			// input.put("URL","http://cmcc.waveinfotech.cn/System/interface/Dispatch.asmx");
			// 方法
			input.put("ASYNCLOCALPART", data.getString("PARA_CODE2"));
			// 用户名
			input.put("WSUSER", data.getString("PARA_CODE3"));
			// 密码
			input.put("WSPASS", data.getString("PARA_CODE4"));
			// 命名空间
			input.put("BIPNAMESPACE", data.getString("PARA_CODE5"));

			return callWebService(input);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}

	/**
	 * 
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IDataset callWebService(IData input) throws Exception {
		try {
			IDataset dataset = new DatasetList();

			/**
			 * 本地测试需要设置代理： 本地代理
			 */
			// Properties prop = System.getProperties();
			// prop.put("http.proxyHost","10.199.48.151");
			// prop.put("http.proxyPort","18081");

			Service service = new Service();
			Call call = (Call) service.createCall();

			String url = input.getString("URL");
			String bipNamespace = input.getString("BIPNAMESPACE");
			String asyncLocalPart = input.getString("ASYNCLOCALPART");// 方法
			String wsUser = input.getString("WSUSER");
			String wsPass = input.getString("WSPASS");

			// 组装请求参数
			String strApplyBeginDate = input.getString("strApplyBeginDate");
			String strApplyEndDate = input.getString("strApplyEndDate");
			// 手机号码
			String strMobilePhone = input.getString("MobilePhone");
			// 串号
			String strIMEI = input.getString("IMEI");
			// 网点代码
			String strSiteCode = input.getString("strSiteCode");

			call.setTargetEndpointAddress(url);
			// 调用的方法名
			call.setOperationName(new QName(bipNamespace, asyncLocalPart));
			call.setUseSOAPAction(true);
			call.setSOAPActionURI(bipNamespace + asyncLocalPart);

			call.addParameter(new QName(bipNamespace, "wsUser"), XMLType.XSD_STRING, ParameterMode.IN);
			call.addParameter(new QName(bipNamespace, "wsPass"), XMLType.XSD_STRING, ParameterMode.IN);
			// 受理开始日期
			call.addParameter(new QName(bipNamespace, "strApplyBeginDate"), XMLType.XSD_STRING, ParameterMode.IN);
			// 受理结束日期
			call.addParameter(new QName(bipNamespace, "strApplyEndDate"), XMLType.XSD_STRING, ParameterMode.IN);

			call.addParameter(new QName(bipNamespace, "strMobilePhone"), XMLType.XSD_STRING, ParameterMode.IN);
			// 串号
			call.addParameter(new QName(bipNamespace, "strIMEI"), XMLType.XSD_STRING, ParameterMode.IN);
			// 网点代码
			call.addParameter(new QName(bipNamespace, "strSiteCode"), XMLType.XSD_STRING, ParameterMode.IN);

			// 设置返回类型
			call.setReturnType(XMLType.XSD_STRING);
			// 设置超时时间
			call.setTimeout(120 * 1000);
			// 税控返回xml文本
			String responseXml = (String) call.invoke(new Object[] { wsUser, wsPass, strApplyBeginDate, strApplyEndDate, strMobilePhone, strIMEI, strSiteCode });

			Document doc2 = DocumentHelper.parseText(responseXml);
			// 获取文档的根节点
			Element rootElement2 = doc2.getRootElement();

			IData resultData = Element2Data(rootElement2);
			String resultState = resultData.getString("STATE");
			if ("SUCCESS".equals(resultState)) {
				return resultData.getDataset("DATALIST");
			} else {
				// 返回错误
				return dataset;
			}
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
	}

	/**
	 * 解析xml文件(对格式有要求)
	 * 
	 * @param element
	 * @return
	 */
	private static IData Element2Data(Element element) throws Exception {
		IData reuslt = new DataMap();
		try {
			// 存在data集合
			IDataset dataset = new DatasetList();
			List list = element.elements();
			// 只有一条时记录
			IData dataOne = new DataMap();
			for (Iterator<Element> it = list.iterator(); it.hasNext();) {
				Element elm = it.next();
				String name = elm.getName().toUpperCase();
				if (name.equals("DATA")) {
					IData d = new DataMap();
					List dataList = elm.elements();
					for (Iterator<Element> k = dataList.iterator(); k.hasNext();) {
						Element elmT = k.next();
						d.put(elmT.getName().toUpperCase(), elmT.getText());
					}
					dataset.add(d);
				} else {
					if ("STATE".equals(elm.getName().toUpperCase()) || "FAILREASON".equals(elm.getName().toUpperCase()) || "RECORDCOUNT".equals(elm.getName().toUpperCase())) {
						// 解析头
						reuslt.put(elm.getName().toUpperCase(), elm.getText());
					} else {
						// 记录单条信息
						dataOne.put(elm.getName().toUpperCase(), elm.getText());
					}
				}
			}

			if (IDataUtil.isNotEmpty(dataOne)) {
				dataset.add(dataOne);
			}

			// 返回集合
			reuslt.put("DATALIST", dataset);
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
		return reuslt;
	}
}
