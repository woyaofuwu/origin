$(function(){
	initPageOfferCha();
});

function initPageOfferCha()
{
	debugger;
	var subOfferCode = "";
	var userId = $("#apply_USER_ID").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	var offerId = $("#cond_OFFER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brandCode = $("#cond_BRAND_CODE").val();
	var showParamPart = $("#SHOW_PARAM_PART").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	
	var ibsysId = $("#IBSYSID").val();
	var subIbsysId = $("#SUB_IBSYSID").val();
	var reecordNum = $("#RECORD_NUM").val();
	
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	
	var nodeTemplete = new Wade.DataMap($("#NODE_TEMPLETE").text());
	
	if(showParamPart == "2")
	{
		var datalinOperType = $("#DATALINE_OPER_TYPE").val();
		if(datalinOperType == "1"){
			serialNumber = $("#DATALINE_SERIAL_NUMBER").val();
		}
		var serialSEQ = $("#DATALINE_MAX_INDEX").val();
		var serialNO = $("#serialNO").val();
//		if(operType == "21" || operType == "22"){
//			userId = $("#"+offerChaHiddenId.substr(10)).attr("userId");
//		}
	}
	var param="";
	if(ibsysId)
	{
		param = "&IBSYSID="+ibsysId+"&RECORD_NUM="+reecordNum+"&SUB_IBSYSID="+subIbsysId+"&SUB_OFFER_CODE="+subOfferCode+"&USER_ID="+userId+"&CUST_ID="+$("#cond_CUST_ID").val()+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&serialSEQ="+serialSEQ+"&NODELIST="+nodeTemplete+'&serialNO='+serialNO+"&INDEX="+maxIndex+"&NOT_QRY=true";
	}
	else
	{
		param = "&SUB_OFFER_CODE="+subOfferCode+"&USER_ID="+userId+"&CUST_ID="+$("#cond_CUST_ID").val()+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&serialSEQ="+serialSEQ+"&NODELIST="+nodeTemplete+'&serialNO='+serialNO+"&INDEX="+maxIndex+"&NOT_QRY=true";
	}
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//回写参数列表
function sureDataLineParamList(rowData)
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var check_tag = $("#isCheckTag").val();
	
	var lineNo = rowData.get("NOTIN_LINE_NO");
	var bandWidth = rowData.get("NOTIN_RSRV_STR1");
	var price = rowData.get("NOTIN_RSRV_STR2");
	var installationCost = rowData.get("NOTIN_RSRV_STR3");//一次性费用（安装调试费）（元）
	
	var userId = rowData.get("USER_ID");//成员用户ID
	var serialNumber = rowData.get("SERIAL_NUMBER");
	if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
		var ipPrice = rowData.get("NOTIN_RSRV_STR10");
		var softwarePrice = rowData.get("NOTIN_RSRV_STR11");
		var netPrice = rowData.get("NOTIN_RSRV_STR12");
		
		var transferMode = rowData.get("TRANSFERMODE");//传输接入方式
		var bizsecurityLv = rowData.get("BIZSECURITYLV");//业务保障等级
//		var portARate = rowData.get("PORTARATE");//业务端口速率
		var portACustom = rowData.get("PORTACUSTOM");//用户名称
		var provinceA = rowData.get("PROVINCEA");//所属省份
		var cityA = rowData.get("CITYA");//所属地市
		var areaA = rowData.get("AREAA");//所属区县
		var countyA = rowData.get("COUNTYA");//街道/乡镇
		var villageA = rowData.get("VILLAGEA");//门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE");//业务端口类型
		var portAContact = rowData.get("PORTACONTACT");//用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE");//用户技术联系人电话
		var IPType = rowData.get("IPTYPE");//IP地址类型
		var cusAppServiIPAddNum = rowData.get("CUSAPPSERVIPADDNUM");//客户申请公网IP地址数
		var cusAppServiIPV4AddNum = rowData.get("CUSAPPSERVIPV4ADDNUM");//申请公网IPV4地址数
		var cusAppServiIPV6AddNum = rowData.get("CUSAPPSERVIPV6ADDNUM");//申请公网IPV6地址数
		var domainName = rowData.get("DOMAINNAME");//域名
		var mainDomainAdd = rowData.get("MAINDOMAINADD");//主域名服务器地址
		var tradeName = rowData.get("TRADENAME");//专线名称
	}else if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
		var groupPercent = rowData.get("NOTIN_RSRV_STR6");
		var softwarePrice = rowData.get("NOTIN_RSRV_STR11");
		var netPrice = rowData.get("NOTIN_RSRV_STR12");
		var aPercent = rowData.get("NOTIN_RSRV_STR7");
		var zPercent = rowData.get("NOTIN_RSRV_STR8");
		var slaServiceCost = rowData.get("NOTIN_RSRV_STR16");//SLA服务费（元/月）
		
		var routeMode = rowData.get("ROUTEMODE");//路由保护方式
		var bizsecurityLv = rowData.get("BIZSECURITYLV");//业务保障等级
		var portACustom = rowData.get("PORTACUSTOM");//A端用户名称
		var provinceA = rowData.get("PROVINCEA");//A端所属省份
		var cityA = rowData.get("CITYA");//A端所属地市
		var areaA = rowData.get("AREAA");//A端所属区县
		var countyA = rowData.get("COUNTYA");//A端街道/乡镇
		var villageA = rowData.get("VILLAGEA");//A端门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE");//A端口类型
//		var portARate = rowData.get("PORTARATE");//A端口速率
		var portAContact = rowData.get("PORTACONTACT");//A端用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE");//A端用户技术联系人电话
		var portZCustom = rowData.get("PORTZCUSTOM");//Z端用户名称
		var provinceZ = rowData.get("PROVINCEZ");//Z端所属省份
		var cityZ = rowData.get("CITYZ");//Z端所属地市
		var areaZ = rowData.get("AREAZ");//Z端所属区县
		var countyZ = rowData.get("COUNTYZ");//Z端街道/乡镇
		var villageZ = rowData.get("VILLAGEZ");//Z端门牌/村组
		var portZInteracetype = rowData.get("PORTZINTERFACETYPE");//Z端口类型
		var portZContact = rowData.get("PORTZCONTACT");//Z端用户技术联系人
		var portZContactPhone = rowData.get("PORTZCONTACTPHONE");//Z端用户技术联系人电话
		var tradeName = rowData.get("TRADENAME");//专线名称
	}else if(offerCode=="7010"){
		var voiceCommunicateCost = rowData.get("NOTIN_RSRV_STR15");//语音通信费（元/分钟）
		
		var transferMode = rowData.get("TRANSFERMODE");//传输接入方式
		var supportMode = rowData.get("SUPPORTMODE");//语音接入类型
		var bizsecurityLv = rowData.get("BIZSECURITYLV");//业务保障等级
		var repeaterNum = rowData.get("REPEATERNUM");//中继数
		var amount = rowData.get("AMOUNT");//座机数量
		var portACustom = rowData.get("PORTACUSTOM");//用户名称
		var provinceA = rowData.get("PROVINCEA");//所属省份
		var cityA = rowData.get("CITYA");//所属地市
		var areaA = rowData.get("AREAA");//所属区县
		var countyA = rowData.get("COUNTYA");//街道/乡镇
		var villageA = rowData.get("VILLAGEA");//门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE");//业务端口类型
		var portAContact = rowData.get("PORTACONTACT");//用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE");//用户技术联系人电话
//		var portARate = rowData.get("PORTARATE");//业务端口速率
		var isCustomerpe = rowData.get("ISCUSTOMERPE");//客户侧是否自备业务设备
		var customerDeviceMode = rowData.get("CUSTOMERDEVICEMODE");//客户侧设备类型
		var customerDeviceType = rowData.get("CUSTOMERDEVICETYPE");//客户侧设备型号
		var customerDeviceVendor = rowData.get("CUSTOMERDEVICEVENDOR");//客户侧设备厂家
		var phonePermission = rowData.get("PHONEPERMISSION");//开通权限
		var phoneList = rowData.get("PHONELIST");//码号段表
		var conproductNo = rowData.get("CONPRODUCTNO");//关联的产品实例编号
		var tradeName = rowData.get("TRADENAME");//专线名称
	}else if(offerCode=="7016"){
		var ipPrice = rowData.get("NOTIN_RSRV_STR10");
		var softwarePrice = rowData.get("NOTIN_RSRV_STR11");
		var netPrice = rowData.get("NOTIN_RSRV_STR12");
		
		var transferMode = rowData.get("TRANSFERMODE","");//传输接入方式
		var supportMode = rowData.get("SUPPORTMODE","");//语音接入类型
		var bizsecurityLv = rowData.get("BIZSECURITYLV");//业务保障等级
//		var repeaterNum = rowData.get("REPEATERNUM");//宽带需求
		var amount = rowData.get("AMOUNT","");//座机数量
		var portACustom = rowData.get("PORTACUSTOM");//用户名称
		var provinceA = rowData.get("PROVINCEA");//所属省份
		var cityA = rowData.get("CITYA");//所属地市
		var areaA = rowData.get("AREAA");//所属区县
		var countyA = rowData.get("COUNTYA");//街道/乡镇
		var villageA = rowData.get("VILLAGEA");//门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE");//业务端口类型
		var portARate = rowData.get("PORTARATE");//业务端口速率
		var portAContact = rowData.get("PORTACONTACT");//用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE");//用户技术联系人电话
		var cusAppServiIPAddNum = rowData.get("CUSAPPSERVIPADDNUM");//客户申请公网IP地址数
		var domainName = rowData.get("DOMAINNAME");//域名
		var mainDomainAdd = rowData.get("MAINDOMAINADD");//主域名服务器地址
		var isCustomerpe = rowData.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
		var customerDeviceMode = rowData.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
		var customerDeviceType = rowData.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
		var customerDeviceVendor = rowData.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
		var phoneList = rowData.get("PHONELIST","");//
		var customerDeviceList = rowData.get("CUSTOMERDEVICELIST","");//客户侧设备厂家
		var tradeName = rowData.get("TRADENAME");//专线名称
		var portARate = rowData.get("PORTARATE","");//业务端口速率
	}
	
	var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
	liHtml += "<div class='fn'><input type='checkbox' checked='checked' userId="+userId+" serialNumber="+serialNumber+" name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'/></div>";
	liHtml += "<div class='main'>";
	liHtml += "<div class='title'>"+lineNo+"</div>";
	liHtml += "<div class='content e_hide-phone'>";
	liHtml += "<div name='scroller1' jwcid='@Scroller' class='c_scroll' style='height:3em'>";
	liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
	liHtml += "<ul>";
	liHtml += "<li>";
	liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
	liHtml += "</li>";
	if(offerCode=="7010"){
		liHtml += "<li>";
		liHtml += "<span class='label'>传入接入方式：</span><span class='value'>"+transferMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>中继数：</span><span class='value'>"+repeaterNum+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户名称：</span><span class='value'>"+portACustom+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属省份：</span><span class='value'>"+provinceA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属地市：</span><span class='value'>"+cityA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属区县：</span><span class='value'>"+areaA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>街道/乡镇：</span><span class='value'>"+countyA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>门牌/村组：</span><span class='value'>"+villageA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务端口类型：</span><span class='value'>"+portAInteracetype+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
		liHtml += "</li>";
//		liHtml += "<li>";
//		liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portARate+"</span>";
//		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧是否自备业务设备：</span><span class='value'>"+isCustomerpe+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧设备类型：</span><span class='value'>"+customerDeviceMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧设备型号：</span><span class='value'>"+customerDeviceType+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧设备厂家：</span><span class='value'>"+customerDeviceVendor+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>开通权限：</span><span class='value'>"+phonePermission+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>关联的产品实例编号：</span><span class='value'>"+conproductNo+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>语音通信费(元/分钟)：</span><span class='value'>"+voiceCommunicateCost+"</span>";
		liHtml += "</li>";
	}
	if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
		liHtml += "<li>";
		liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
		liHtml += "</li>";
//		liHtml += "<li>";
//		liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portARate+"</span>";
//		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户名称：</span><span class='value'>"+portACustom+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属省份：</span><span class='value'>"+provinceA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属地市：</span><span class='value'>"+cityA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属区县：</span><span class='value'>"+areaA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>街道/乡镇：</span><span class='value'>"+countyA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>门牌/村组：</span><span class='value'>"+villageA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务端口类型：</span><span class='value'>"+portAInteracetype+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>IP地址类型：</span><span class='value'>"+IPType+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>申请公网IPV4地址数：</span><span class='value'>"+cusAppServiIPV4AddNum+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>申请公网IPV6地址数：</span><span class='value'>"+cusAppServiIPV6AddNum+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>IP地址使用费：</span><span class='value'>"+ipPrice+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>软件应用服务费：</span><span class='value'>"+softwarePrice+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>网络技术支持服务费：</span><span class='value'>"+netPrice+"</span>";
		liHtml += "</li>";
	}
	if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
		liHtml += "<li>";
		liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端用户名称：</span><span class='value'>"+portACustom+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端所属省份：</span><span class='value'>"+provinceA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端所属地市：</span><span class='value'>"+cityA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端所属区县：</span><span class='value'>"+areaA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端街道/乡镇：</span><span class='value'>"+countyA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端门牌/村组：</span><span class='value'>"+villageA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端业务端口类型：</span><span class='value'>"+portAInteracetype+"</span>";
		liHtml += "</li>";
//		liHtml += "<li>";
//		liHtml += "<span class='label'>A端口速率：</span><span class='value'>"+portARate+"</span>";
//		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
		liHtml += "</li>";		
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端用户名称：</span><span class='value'>"+portZCustom+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端所属省份：</span><span class='value'>"+provinceZ+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端所属地市：</span><span class='value'>"+cityZ+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端所属区县：</span><span class='value'>"+areaZ+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端街道/乡镇：</span><span class='value'>"+countyZ+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端门牌/村组：</span><span class='value'>"+villageZ+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端业务端口类型：</span><span class='value'>"+portZInteracetype+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端用户技术联系人：</span><span class='value'>"+portZContact+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端用户技术联系人电话：</span><span class='value'>"+portZContactPhone+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>集团所在市县分成比例(%)：</span><span class='value'>"+groupPercent+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>A端所在市县分成比例(%)：</span><span class='value'>"+aPercent+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>Z端所在市县分成比例(%)：</span><span class='value'>"+zPercent+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>软件应用服务费：</span><span class='value'>"+softwarePrice+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>网络技术支持服务费：</span><span class='value'>"+netPrice+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>SLA服务费(元/月)：</span><span class='value'>"+slaServiceCost+"</span>";
		liHtml += "</li>";
	} 
	if(offerCode=="7016"){
		liHtml += "<li>";
		liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
		liHtml += "</li>";
		/*liHtml += "<li>";
		liHtml += "<span class='label'>带宽需求：</span><span class='value'>"+repeaterNum+"</span>";
		liHtml += "</li>";*/
		liHtml += "<li>";
		liHtml += "<span class='label'>座机数量（单位：门）：</span><span class='value'>"+amount+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户名称：</span><span class='value'>"+portACustom+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属省份：</span><span class='value'>"+provinceA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属地市：</span><span class='value'>"+cityA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>所属区县：</span><span class='value'>"+areaA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>街道/乡镇：</span><span class='value'>"+countyA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>门牌/村组：</span><span class='value'>"+villageA+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务端口类型：</span><span class='value'>"+portAInteracetype+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portARate+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
		liHtml += "</li>";
		/*liHtml += "<li>";
		liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
		liHtml += "</li>";*/
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧是否自备业务设备：</span><span class='value'>"+isCustomerpe+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧设备类型：</span><span class='value'>"+customerDeviceMode+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧设备型号：</span><span class='value'>"+customerDeviceType+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>客户侧设备厂家：</span><span class='value'>"+customerDeviceVendor+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>设备清单：</span><span class='value'>"+customerDeviceList+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>IP地址使用费：</span><span class='value'>"+ipPrice+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>软件应用服务费：</span><span class='value'>"+softwarePrice+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>网络技术支持服务费：</span><span class='value'>"+netPrice+"</span>";
		liHtml += "</li>";
	}
	liHtml += "<li>";
	liHtml += "<span class='label'>月租费(元/月)：</span><span class='value'>"+price+"</span>";
	liHtml += "</li>";
	liHtml += "<li>";
	liHtml += "<span class='label'>一次性费用(安装调试费)(元)：</span><span class='value'>"+installationCost+"</span>";
	liHtml += "</li>";
	liHtml += "</ul>";
	liHtml += "</div></div></div></div>";
	liHtml += "<div class='fn' ontap='modifyConfCrmDataLineParam("+rowData+");'><span class='e_ico-edit'></span></div>";
	liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
	liHtml += "</li>";
	
	$("#DATALINE_PARAM_UL").append(liHtml);
	if($("#DATALINE_PARAM_UL").children().length > 0)
	{
		$("#DATALINE_PARAM_UL").parent(".c_list").css("display", "");
	}
	maxIndex++;
	$("#DATALINE_MAX_INDEX").val(maxIndex);
	$("#check_tag").val(check_tag);
	
}

function modifyConfCrmDataLineParam(obj){
	debugger;
	var templetId = $("#cond_TEMPLET_ID").val();
    var offerListNum = $("#DATALINE_PARAM_UL").length;
    if (offerListNum != 0) {
        if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length == 0) {
            MessageBox.alert("提示", "请勾选需要变更的信息进行修改！");
            return false;
        }else if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length > 1) {
            MessageBox.alert("提示", "请只勾选一条需要变更的信息进行修改！");
            return false;
        }
    }
    //专线变更要先判断是否选择了业务调整场景
    if(templetId=="EDIRECTLINECHANGE"){
    	var mode = $('#pattr_CHANGEMODE').val();
    	if(mode==""){
    		MessageBox.alert("提示", "请先选择业务调整场景！");
    		return false;
    	}
    }
    
	var offerCode = $("#cond_OFFER_CODE").val();
	var offerId = $("#cond_OFFER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brandCode = $("#cond_BRAND_CODE").val();
	var showParamPart = $("#SHOW_PARAM_PART").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var nodeTemplete = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var offerChaHiddenId = "OFFER_CHA_" + $("#cond_OFFER_CODE").val() + "_" + maxIndex + "_confCrm";
	$("#OFFER_CHA_HIDDEN_ID").val(offerChaHiddenId); 
	var rowData = new Wade.DataMap(obj);
//	var productNo = rowData.get("NOTIN_LINE_NO");
	
	param = "&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&NODELIST="+nodeTemplete+"&OFFER_CHA_HIDDEN_ID="+offerChaHiddenId+"&INDEX="+maxIndex;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
		
		chooseMode2();
		
		showPopup("popup02", "offerChaPopupItem", true);
		var marketingTag = rowData.get("NOTIN_MARKETING_TAG");
		if(marketingTag=='1'){
			$("#NOTIN_RSRV_STR2").attr("disabled","true");
			$("#NOTIN_RSRV_STR3").attr("disabled","true");
			$("#NOTIN_RSRV_STR10").attr("disabled","true");
			$("#NOTIN_RSRV_STR11").attr("disabled","true");
			$("#NOTIN_RSRV_STR12").attr("disabled","true");
			$("#NOTIN_RSRV_STR16").attr("disabled","true");
			$("#NOTIN_RSRV_STR15").attr("disabled","true");
		}
		$("#NOTIN_LINE_NO").val(rowData.get("NOTIN_LINE_NO"));
		$("#NOTIN_RSRV_STR1").val(rowData.get("NOTIN_RSRV_STR1"));
		$("#NOTIN_RSRV_STR2").val(rowData.get("NOTIN_RSRV_STR2"));
		$("#NOTIN_RSRV_STR3").val(rowData.get("NOTIN_RSRV_STR3"));
		$("#NOTIN_RSRV_STR9").val(rowData.get("NOTIN_LINE_NO"));
		$("#pattr_PRODUCTNO").val(rowData.get("PRODUCTNO"));
		$("#pattr_USER_ID").val(rowData.get("USER_ID"));
		$("#pattr_SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
		$("#pattr_TRADEID").val(rowData.get("TRADEID"));
		$("#NOTIN_MARKETING_TAG").val(rowData.get("NOTIN_MARKETING_TAG"));
		$("#pattr_BANDWIDTH").val(rowData.get("NOTIN_RSRV_STR1"));
		$("#pattr_HIDDEN_BANDWIDTH").val(rowData.get("NOTIN_RSRV_STR1"));
		if(offerCode=='7011'||offerCode=="70111"||offerCode=="70112"){
			$("#NOTIN_RSRV_STR10").val(rowData.get("NOTIN_RSRV_STR10"));
			$("#NOTIN_RSRV_STR11").val(rowData.get("NOTIN_RSRV_STR11"));
			$("#NOTIN_RSRV_STR12").val(rowData.get("NOTIN_RSRV_STR12"));
			$("#pattr_TRANSFERMODE").val(rowData.get("TRANSFERMODE"));
			$("#pattr_BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_CITYA").val(rowData.get("CITYA"));
			$("#pattr_AREAA").val(rowData.get("AREAA"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			$("#pattr_IPTYPE").val(rowData.get("IPTYPE"));
			$("#pattr_CUSAPPSERVIPADDNUM").val(rowData.get("CUSAPPSERVIPADDNUM"));
			$("#pattr_CUSAPPSERVIPV4ADDNUM").val(rowData.get("CUSAPPSERVIPV4ADDNUM"));
			$("#pattr_CUSAPPSERVIPV6ADDNUM").val(rowData.get("CUSAPPSERVIPV6ADDNUM"));
			$("#pattr_DOMAINNAME").val(rowData.get("DOMAINNAME"));
			$("#pattr_MAINDOMAINADD").val(rowData.get("MAINDOMAINADD"));
			$("#pattr_TRADENAME").val(rowData.get("TRADENAME"));
		}
		if(offerCode=='7012'||offerCode=="70121"||offerCode=="70122"){
			if(rowData.get("NOTIN_RSRV_STR6")!=""){
				$("#NOTIN_RSRV_STR6").val(rowData.get("NOTIN_RSRV_STR6"));
			}else{
				$("#NOTIN_RSRV_STR6").val(20);
			}
			if(rowData.get("NOTIN_RSRV_STR7")!=""){
				$("#NOTIN_RSRV_STR7").val(rowData.get("NOTIN_RSRV_STR7"));
			}else{
				$("#NOTIN_RSRV_STR7").val(40);
			}
			if(rowData.get("NOTIN_RSRV_STR8")!=""){
				$("#NOTIN_RSRV_STR8").val(rowData.get("NOTIN_RSRV_STR8"));
			}else{
				$("#NOTIN_RSRV_STR8").val(40);
			}
			$("#NOTIN_RSRV_STR11").val(rowData.get("NOTIN_RSRV_STR11"));
			$("#NOTIN_RSRV_STR12").val(rowData.get("NOTIN_RSRV_STR12"));
			$("#NOTIN_RSRV_STR16").val(rowData.get("NOTIN_RSRV_STR16"));
			if(rowData.get("ROUTEMODE")=="单节点单路由"){
				var routeMode = "0";
			}else if(rowData.get("ROUTEMODE")=="单节点双路由"){
				var routeMode = "1";
			}else if(rowData.get("ROUTEMODE")=="双节点双路由"){
				var routeMode = "2";
			}
			$("#pattr_ROUTEMODE").val(routeMode);
			$("#pattr_BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_CITYA").val(rowData.get("CITYA"));
			$("#pattr_AREAA").val(rowData.get("AREAA"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			
			$("#pattr_PORTZCUSTOM").val(rowData.get("PORTZCUSTOM"));
			$("#pattr_PROVINCEZ").val(rowData.get("PROVINCEZ"));
			$("#pattr_CITYZ").val(rowData.get("CITYZ"));
			$("#pattr_AREAZ").val(rowData.get("AREAZ"));
			$("#pattr_COUNTYZ").val(rowData.get("COUNTYZ"));
			$("#pattr_VILLAGEZ").val(rowData.get("VILLAGEZ"));
			$("#pattr_PORTZINTERFACETYPE").val(rowData.get("PORTZINTERFACETYPE"));
			$("#pattr_PORTZCONTACT").val(rowData.get("PORTZCONTACT"));
			$("#pattr_PORTZCONTACTPHONE").val(rowData.get("PORTZCONTACTPHONE"));
			$("#pattr_TRADENAME").val(rowData.get("TRADENAME"));
		}
		if(offerCode=='7010'){
			$("#NOTIN_RSRV_STR15").val(rowData.get("NOTIN_RSRV_STR15"));
			$("#pattr_TRANSFERMODE").val(rowData.get("TRANSFERMODE"));
			$("#pattr_SUPPORTMODE").val(rowData.get("SUPPORTMODE"));
			$("#pattr_BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
			$("#pattr_REPEATERNUM").val(rowData.get("REPEATERNUM"));
			$("#pattr_BANDWIDTH").val(rowData.get("BANDWIDTH"));
			$("#pattr_AMOUNT").val(rowData.get("AMOUNT"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_CITYA").val(rowData.get("CITYA"));
			$("#pattr_AREAA").val(rowData.get("AREAA"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			var isCustomerpe = rowData.get("ISCUSTOMERPE");
			if(isCustomerpe=='是'){
				isCustomerpe = "1";
			}else if(isCustomerpe=='否'){
				isCustomerpe = "2";
			}
			$("#pattr_ISCUSTOMERPE").val(isCustomerpe);
			$("#pattr_CUSTOMERDEVICEMODE").val(rowData.get("CUSTOMERDEVICEMODE"));
			$("#pattr_CUSTOMERDEVICETYPE").val(rowData.get("CUSTOMERDEVICETYPE"));
			$("#pattr_CUSTOMERDEVICEVENDOR").val(rowData.get("CUSTOMERDEVICEVENDOR"));
			$("#pattr_PHONEPERMISSION").val(rowData.get("PHONEPERMISSION"));
			$("#pattr_PHONELIST").val(rowData.get("PHONELIST"));
			$("#pattr_CONPRODUCTNO").val(rowData.get("CONPRODUCTNO"));
			$("#pattr_TRADENAME").val(rowData.get("TRADENAME"));
			
		}
		if(offerCode=='7016'){
			var keyrowData=rowData.keys;
			if(keyrowData!=null){
				for(var r=0;r<keyrowData.length;r++){
					if(keyrowData[r]!=null&&keyrowData[r]!=''){
						if(keyrowData[r].indexOf("NOTIN_")>=0){
							$("#"+keyrowData[r]).val(rowData.get(keyrowData[r]));
						}else{
							$("#pattr_"+keyrowData[r]).val(rowData.get(keyrowData[r]));
						}
					}
				}
			}
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}

//专线变更修改专线详细数据时，根据选择业务调整场景
function chooseMode2(){
	debugger;
	var productId = $("#cond_OFFER_CODE").val();
	var changeMode = $("#pattr_CHANGEMODE").val();
	if(changeMode=="扩容"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");
		$("#NOTIN_RSRV_STR2").removeAttr("disabled");//月租费
		$("#NOTIN_RSRV_STR3").removeAttr("disabled");//一次性费用(安装调试费)(元)
		if(productId=="7012"||productId=="70121"||productId=="70122"){
			$("#pattr_ROUTEMODE").attr("disabled","true");//路由保护方式
			$("#pattr_BIZSECURITYLV").attr("disabled","true");//业务保障等级
			$("#pattr_PORTACUSTOM").attr("disabled","true");//A端用户名称
			$("#pattr_PORTAINTERFACETYPE").attr("disabled","true");//A端口类型
			$("#pattr_PORTACONTACT").attr("disabled","true");//A端用户技术联系人
			$("#pattr_PORTACONTACTPHONE").attr("disabled","true");//A端用户技术联系人电话
			$("#pattr_PORTZCUSTOM").attr("disabled","true");//Z端用户名称
			$("#pattr_PORTZINTERFACETYPE").attr("disabled","true");//Z端口类型
			$("#pattr_PORTZCONTACT").attr("disabled","true");//Z端用户技术联系人
			$("#pattr_PORTZCONTACTPHONE").attr("disabled","true");//Z端用户技术联系人电话
			$("#pattr_TRADENAME").attr("disabled","true");//专线名称
		}
		if(productId=="7011"||productId=="70111"||productId=="70112"){
			$("#pattr_BANDWIDTH").removeAttr("disabled");//业务带宽(单位：M)
		}
		if(productId=="7010"){
			$("#pattr_TRANSFERMODE").attr("disabled","true");//路由保护方式
			$("#pattr_SUPPORTMODE").attr("disabled","true");//语音接入类型
			$("#pattr_BIZSECURITYLV").attr("disabled","true");//业务保障等级
			$("#pattr_REPEATERNUM").attr("disabled","true");//中继数
			$("#pattr_AMOUNT").attr("disabled","true");//座机数
			$("#pattr_PORTACUSTOM").attr("disabled","true");//用户名称
			$("#pattr_COUNTYA").attr("disabled","true");//A端街道/乡镇
			$("#pattr_VILLAGEA").attr("disabled","true");//A端门牌/村组
			$("#pattr_PORTAINTERFACETYPE").attr("disabled","true");//业务端口类型
			$("#pattr_PORTACONTACT").attr("disabled","true");//用户技术联系人
			$("#pattr_PORTACONTACTPHONE").attr("disabled","true");//A端用户技术联系人电话
			$("#pattr_PORTARATE").attr("disabled","true");//业务端口速率
			$("#pattr_ISCUSTOMERPE").attr("disabled","true");//客户侧是否自备业务设备
			$("#pattr_CUSTOMERDEVICEMODE").attr("disabled","true");//客户侧设备类型
			$("#pattr_CUSTOMERDEVICETYPE").attr("disabled","true");//客户侧设备型号
			$("#pattr_CUSTOMERDEVICEVENDOR").attr("disabled","true");//客户侧设备厂家
			$("#pattr_CONPRODUCTNO").attr("disabled","true");//关联的产品实例编号
			$("#pattr_TRADENAME").attr("disabled","true");//专线名称
			$("#pattr_PHONEPERMISSION").attr("disabled","true"); //开通权限
			$("#pattr_PHONELIST").attr("disabled","true");//码号段表
			
			
			
		}
		if(productId=="7016"){
			$("#pattr_BANDWIDTH").removeAttr("disabled");//业务带宽(单位：M)
			$("#NOTIN_RSRV_STR11").attr("disabled","true");// 软件应用服务费（元）
			$("#NOTIN_RSRV_STR12").attr("disabled","true");// 技术支持服务费（元）
			$("#NOTIN_RSRV_STR10").attr("disabled","true");// IP地址使用费（元）

		}
		
	}
	if(changeMode=="异楼搬迁"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");

		$("#NOTIN_RSRV_STR3").removeAttr("disabled");// 一次性费用（安装调试费）（元）
		$("#NOTIN_RSRV_STR11").removeAttr("disabled");// 软件应用服务费（元）
		$("#NOTIN_RSRV_STR12").removeAttr("disabled");// 技术支持服务费（元）

		if(productId=="7012"||productId=="70121"||productId=="70122"){
			$("#pattr_BANDWIDTH").attr("disabled","true");//业务带宽(单位：M)
			$("#pattr_CITYA").removeAttr("disabled");//A端所属地市
			$("#pattr_AREAA").removeAttr("disabled");//A端所属区县
			$("#pattr_COUNTYA").removeAttr("disabled");//A端街道/乡镇
			$("#pattr_VILLAGEA").removeAttr("disabled");//A端门牌/村组
			$("#pattr_CITYZ").removeAttr("disabled");//Z端所属地市
			$("#pattr_AREAZ").removeAttr("disabled");//Z端所属区县
			$("#pattr_COUNTYZ").removeAttr("disabled");//Z端街道/乡镇
			$("#pattr_VILLAGEZ").removeAttr("disabled");//Z端门牌/村组
		}
		if(productId=="7011"||productId=="70111"||productId=="70112"){
			$("#pattr_BANDWIDTH").attr("disabled","true");//业务带宽(单位：M)
			$("#pattr_TRANSFERMODE").removeAttr("disabled");//传输接入方式
			$("#pattr_BIZSECURITYLV").removeAttr("disabled");//业务保障等级
			$("#pattr_PORTACUSTOM").removeAttr("disabled");//A端用户名称
			$("#pattr_CITYA").removeAttr("disabled");//A端所属地市
			$("#pattr_AREAA").removeAttr("disabled");//A端所属区县
			$("#pattr_COUNTYA").removeAttr("disabled");//A端街道/乡镇
			$("#pattr_VILLAGEA").removeAttr("disabled");//A端门牌/村组
			$("#pattr_PORTAINTERFACETYPE").removeAttr("disabled");//A端口类型
			$("#pattr_PORTACONTACT").removeAttr("disabled");//A端用户技术联系人
			$("#pattr_PORTACONTACTPHONE").removeAttr("disabled");//A端用户技术联系人电话
			$("#pattr_DOMAINNAME").removeAttr("disabled");//域名
			$("#pattr_MAINDOMAINADD").removeAttr("disabled");//主域名服务器地址
			$("#pattr_TRADENAME").removeAttr("disabled");//专线名称
		}
		if(productId=="7010"){
			$("#pattr_BANDWIDTH").attr("disabled","true");//业务带宽(单位：M)
			$("#pattr_PHONEPERMISSION").attr("disabled","true"); //开通权限
			$("#pattr_PHONELIST").attr("disabled","true");//码号段表
			$("#pattr_CITYA").removeAttr("disabled");//所属地市
			$("#pattr_AREAA").removeAttr("disabled");//A端所属区县
		}
		if(productId=="7016"){
			$("#pattr_BANDWIDTH").attr("disabled","true");//业务带宽(单位：M)
			$("#pattr_TRANSFERMODE").removeAttr("disabled");//传输接入方式
			$("#pattr_BIZSECURITYLV").removeAttr("disabled");//业务保障等级
			$("#pattr_PORTACUSTOM").removeAttr("disabled");//A端用户名称
			$("#pattr_CITYA").removeAttr("disabled");//A端所属地市
			$("#pattr_AREAA").removeAttr("disabled");//A端所属区县
			$("#pattr_COUNTYA").removeAttr("disabled");//A端街道/乡镇
			$("#pattr_VILLAGEA").removeAttr("disabled");//A端门牌/村组
			$("#pattr_PORTAINTERFACETYPE").removeAttr("disabled");//A端口类型
			$("#pattr_PORTACONTACT").removeAttr("disabled");//A端用户技术联系人
			$("#pattr_PORTACONTACTPHONE").removeAttr("disabled");//A端用户技术联系人电话
			$("#pattr_DOMAINNAME").removeAttr("disabled");//域名
			$("#pattr_MAINDOMAINADD").removeAttr("disabled");//主域名服务器地址
			$("#pattr_TRADENAME").removeAttr("disabled");//专线名称
			
			$("#pattr_SUPPORTMODE").removeAttr("disabled");//语音接入类型
			$("#pattr_ISCUSTOMERPE").removeAttr("disabled");//客户侧是否自备业务设备
			$("#pattr_CUSTOMERDEVICEMODE").removeAttr("disabled");//客户侧设备类型
			$("#pattr_CUSTOMERDEVICETYPE").removeAttr("disabled");//客户侧设备型号
			$("#pattr_CUSTOMERDEVICEVENDOR").removeAttr("disabled");//客户侧设备厂家
			$("#pattr_CUSTOMERDEVICELIST").removeAttr("disabled");//设备清单
			$("#pattr_PHONELIST").removeAttr("disabled");//码号段表
			$("#pattr_AMOUNT").removeAttr("disabled");//	座机数量
			
		}
		
	}else if(changeMode=="业务保障级别调整"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");
		if(productId=="7012"||productId=="70121"||productId=="70122"){
			$("#NOTIN_RSRV_STR16").removeAttr("disabled");//SLA服务费(元/月)
		}
		if(productId=="7016"){
			$("#NOTIN_RSRV_STR2").attr("disabled","true");// 
			$("#NOTIN_RSRV_STR3").attr("disabled","true");// 
			$("#NOTIN_RSRV_STR11").attr("disabled","true");// 软件应用服务费（元）
			$("#NOTIN_RSRV_STR12").attr("disabled","true");// 技术支持服务费（元）
			$("#NOTIN_RSRV_STR10").attr("disabled","true");// IP地址使用费（元）		
		}
		
		
		$("#pattr_BANDWIDTH").attr("disabled","true");//业务带宽(单位：M)
		$("#pattr_BIZSECURITYLV").removeAttr("disabled");//业务保障等级
		$("#pattr_PORTACUSTOM").attr("disabled","true");//A端用户名称
		$("#pattr_PORTAINTERFACETYPE").attr("disabled","true");//A端口类型
		$("#pattr_PORTACONTACT").attr("disabled","true");//A端用户技术联系人
		$("#pattr_PORTACONTACTPHONE").attr("disabled","true");//A端用户技术联系人电话
		$("#pattr_PORTZCUSTOM").attr("disabled","true");//Z端用户名称
		$("#pattr_PORTZINTERFACETYPE").attr("disabled","true");//Z端口类型
		$("#pattr_PORTZCONTACT").attr("disabled","true");//Z端用户技术联系人
		$("#pattr_PORTZCONTACTPHONE").attr("disabled","true");//Z端用户技术联系人电话
		$("#pattr_TRADENAME").attr("disabled","true");//专线名称
	}
	return true;
}
