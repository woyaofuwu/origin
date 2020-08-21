var changeMode = "";
var productNo = "";
$(function(){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	if("9983"==offerCode){
		 $("#batchAdd").css("display","none");
	}
	$("#EDIRECTLINEDATACHANGE_ImportLi").beforeAction(function(e){
		return confirm('是否导入?');
	});
	
	$("#EDIRECTLINEDATACHANGE_ImportLi").afterAction(function(e, status){
		debugger;
		if('ok' == status){
			$.beginPageLoading("正在处理...");
			ajaxSubmit('','batchImportFileForMarketing',null,null,function(datas){
				var chaSpecObjs = $("#DATALINE_PARAM input");
				for(var i = 0, size = chaSpecObjs.length; i < size; i++)
				{
					var chaSpecCode = chaSpecObjs[i].id;
					var line = chaSpecCode.split("_").pop();
					var chaValue = "";
					if(chaSpecObjs[i].type == "checkbox" )// ||  chaSpecObjs[i].type == "radio"
					{
						$("#"+chaSpecObjs[i].id).attr("checked","");
						var divFlag = true;
						for(var z=0;z<datas.length;z++){
							var data = datas.get(z);
							var values =  $("#"+line+"new .value");
							var flag = false; 
							var productNo = "";
							for(var j =0 ;j<values.length;j++){
								var atts = values[j].firstChild;
								if(atts!=null&&"PRODUCT_NO" == atts.getAttribute("name")&&
										data.get("PRODUCT_NO")==values[j].innerText){
									productNo = values[j].innerText;
									divFlag = false;
									//
									$("#"+chaSpecObjs[i].id).attr("checked","checked");
									$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORTACONTACT")).text(data.get("PORT_CONTACT_A"));
									$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORTACONTACTPHONE")).text(data.get("PORT_CONTACT_PHONE_A"));
									if(data.get("PORT_CONTACT_Z")!=null){
										$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORTZCONTACT")).text(data.get("PORT_CONTACT_Z"));
									}
									if(data.get("PORT_CONTACT_PHONE_Z")!=null){
										$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORTZCONTACTPHONE")).text(data.get("PORT_CONTACT_PHONE_Z"));
									}
									//
								}
//										offerChaSpecData.put("ATTR_VALUE", values[j].innerText);
//										offerChaSpecData.put("ATTR_NAME", atts.getAttribute("desc"));
//										offerChaSpecData.put("ATTR_CODE", atts.getAttribute("name"));
//										offerChaSpecDatas.add(offerChaSpecData);
							}
						}
						if(divFlag){
							var divDe = document.getElementById(line+"new");
							divDe.parentNode.removeChild(divDe);
						}
						
				}
				$.endPageLoading();
				$("#EDIRECTLINEDATACHANGE_Import_float").attr("class","c_float");
				MessageBox.success("导入成功！", "页面展示导入数据!",function(){
					flag="true";
				});
				}
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}		
	});
	
});

//审核不通过返回APPLY节点时，加载专线属性
function loadDataLineList(){
	if($("#cond_TEMPLET_ID").val()=="EDIRECTLINEDATACHANGE"){
		loadDataInfoLineList();//加载资料变更
		return ;
	}
	var ibsysid = $("#IBSYSID").val();
	var nodeId = $("#NODE_ID").val();
	
	//$.beginPageLoading();
	$.ajax.submit('','queryDataLineInfo','&IBSYSID='+ibsysid+'&NODE_ID='+nodeId,'',function(datas){
		var offerCode = $("#cond_OFFER_CODE").val();
		var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
		
		var offerchaList = new Wade.DatasetList($("#offerchalist").val());
		var temp = new Wade.DatasetList();
		for (var j = 0; j < offerchaList.length; j++) {
			var offercha = offerchaList[j];
			temp.add(offercha);
		}
		var templetId = $("#cond_TEMPLET_ID").val();
		if(templetId=="EDIRECTLINECANCEL"){
			$("#DATALINE_PARAM_UL").html("");//删除已经加载专线列表
		}else if(templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
			$("#changImport").css("display","");
			$("#DATALINE_PARAM_UL").html("");
		}
		for(var i=0;i<datas.length;i++){
			var dataLineInfos = new Wade.DatasetList();
			var data = datas.get(i);
			loadBatchDataLineParamList(data);
			var key = data.keys;
			for(var m=0;m<key.length;m++){
				var value = data.get(key[m]);
				var dataLineInfo = new Wade.DataMap();
				dataLineInfo.put("ATTR_CODE", key[m]);
				dataLineInfo.put("ATTR_VALUE", value);
				
				var name = "";
				if(key[m].indexOf("NOTIN_")>=0){
					name = key[m];
				}else{
					name = "pattr_"+key[m];
				}
				dataLineInfo.put("ATTR_NAME",$("input[name='"+name+"']").attr("desc"));
				
				dataLineInfos.add(dataLineInfo);
			}
			
			var changeMode = $("#pattr_CHANGEMODE").val();
			if(changeMode=="减容"){
				var dataBandWidthInfo = new Wade.DataMap();
				dataBandWidthInfo.put("ATTR_CODE", "HIDDEN_BANDWIDTH");
				dataBandWidthInfo.put("ATTR_VALUE", data.get("BANDWIDTH"));
				dataBandWidthInfo.put("ATTR_NAME","原来带宽");
				dataLineInfos.add(dataBandWidthInfo);
			}
			
			var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
			$("#"+offerChaHiddenId).val(dataLineInfos);
			maxIndex++;
			$("#DATALINE_MAX_INDEX").val(maxIndex);
			temp.add(dataLineInfos);
			
		}
		if(templetId=="DIRECTLINECHANGESIMPLE"){
			setExportLineNos();
		}
		
		$("#offerchalist").val(temp);
		
		//去除新增，删除和导入按钮
		$("#batchAdd").css("display", "none");
		$("#add").css("display", "none");
		$("#delete").css("display", "none");
		
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
function loadDataInfoLineList(){
	debugger;

	var ibsysid = $("#IBSYSID").val();
	var nodeId = $("#NODE_ID").val();
	var offerCode = $("#cond_OFFER_CODE").val();
	var inesccobusi400 = $("#CREATE_INESSCOBUSI400").val();
	var groupId = $("#cond_GROUP_ID").val();
	if(offerCode == null || offerCode == "")
	{
		$.validate.alerter.one($("cond_OFFER_NAME")[0], "请选择产品！");
		$("#cond_TEMPLET_ID").val("");
		return false;
	}
	if(groupId == null || groupId == ""){
		$.validate.alerter.one($("cond_GROUP_ID_INPUT")[0], "未获取到集团信息，请输入集团编码后回车键查询！");
		$("#cond_TEMPLET_ID").val("");
		return false;
	}
	var userId = $("#apply_USER_ID").val();
	var opertype = $("#cond_OPER_TYPE").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	if(!templetId){
		return false;
	}
	var param = "&TEMPLET_ID="+templetId+"&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val()+"&GROUP_ID="+$("#cond_GROUP_ID").val();
	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "queryInfo", param, "ParamPart,UploadPart,LineSpecialParam", function(data){
		$("#apply_USER_ID").val(userId);
		$("#OFFER_CODE_LI").css("display", "");
		$("#ParamPart").css("display", "");
		$("#cond_TEMPLET_ID").val(templetId);
		$("#cond_OPER_TYPE").val(opertype);

		$("#offerchalist").val(data.get("DATALINEINFO"));
		
		$.ajax.submit('','queryDataLineInfo','&IBSYSID='+ibsysid+'&NODE_ID='+nodeId,'',function(datas){
			var chaSpecObjs = $("#DATALINE_PARAM input");
			for(var i = 0, size = chaSpecObjs.length; i < size; i++)
			{
				var chaSpecCode = chaSpecObjs[i].id;
				var line = chaSpecCode.split("_").pop();
				var chaValue = "";
				if(chaSpecObjs[i].type == "checkbox" )// ||  chaSpecObjs[i].type == "radio"
				{
					for(var z=0;z<datas.length;z++){
						var data = datas.get(z);
						var values =  $("#"+line+"new .value");
						var flag = false; 
						var productNo = "";
						for(var j =0 ;j<values.length;j++){
							var atts = values[j].firstChild;
							if("PRODUCT_NO" == atts.getAttribute("name")&&
									data.get("pattr_PRODUCT_NO")==values[j].innerText){
								productNo = values[j].innerText;
								$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORT_CONTACT_A")).text(data.get("pattr_PORT_CONTACT_A"));
								$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORT_CONTACT_PHONE_A")).text(data.get("pattr_PORT_CONTACT_PHONE_A"));
								if(data.get("pattr_PORT_CONTACT_Z")!=null){
									$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORT_CONTACT_Z")).text(data.get("pattr_PORT_CONTACT_Z"));
								}
								if(data.get("pattr_PORT_CONTACT_PHONE_Z")!=null){
									$("#"+atts.getAttribute("id").replace("PRODUCT_NO","PORT_CONTACT_PHONE_Z")).text(data.get("pattr_PORT_CONTACT_PHONE_Z"));
								}
							}
						}
					}
					
				}
			}
			
			$.endPageLoading();
		});
		

	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});

	

	//$.beginPageLoading();
	setTimeout(function (){
		
	}, 1000);
	
}
function initPageParam(){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	if("9983"==offerCode){
		 $("#batchAdd").css("display","none");
	}
}

//新增专线参数
function addDataLineParam()
{
	debugger;
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var offerChaHiddenId = "OFFER_CHA_" + $("#cond_OFFER_CODE").val() + "_" + maxIndex + "_new";
	var offerCode = $("#cond_OFFER_CODE").val();
	if("9983"==offerCode&&"1"==maxIndex){
		MessageBox.alert("提示", "你已新增该商品参数，不能重复新增，或删除后再次新增！");
        return false;
	}
	
	initOfferCha(offerChaHiddenId);
}

//修改专线参数
function modifyDataLineParam(offerChaHiddenId)
{
	debugger;
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
    var offerCode = $("#cond_OFFER_CODE").val();
	var chks = $("#DATALINE_PARAM_UL [type=checkbox]");
	{
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)// 获取选中的列表
			{
				var arr = offerChaHiddenId.split("_");
				var chkId = arr[3];
				var idNum = chks[j].id.substring(9);
				//var f1 = chkId!=idNum;
				//var f2 = "9983"!=offerCode;
				if(chkId!=idNum && "9983"!=offerCode){
					MessageBox.alert("提示", "请对勾选的的信息进行修改！");
		            return false;
				}
			}
		}
	}
	
	initOfferCha(offerChaHiddenId);
}

//保存参数
function afterSubmitOfferCha(offerChaSpecDataset)
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#DATALINE_MAX_INDEX").val(); //先取最大序列，加载完html后，序列会加1
	
	loadDataLineParamList();
	
	var offerChaHiddenId = "OFFER_CHA_" + offerCode + "_" + maxIndex;
	$("#"+offerChaHiddenId).val(offerChaSpecDataset);
}

//回写参数列表
function loadDataLineParamList()
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var check_tag = $("#isCheckTag").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	
	var lineNo = $("#NOTIN_LINE_NO").val();
	var bandWidth = $("#NOTIN_RSRV_STR1").val();
	var price = $("#NOTIN_RSRV_STR2").val();
	var installationCost = $("#NOTIN_RSRV_STR3").val();//一次性费用（安装调试费）（元）
	
//	var tradeIdZHZG = $("#pattr_TRADEID").val();
	var bandWidthZHZG = $("#pattr_BANDWIDTH").val();
//	var bizSecurityLvZHZG = $("#pattr_BIZSECURITYLV").val();
	var userId = $("#pattr_USER_ID").val();
	var serialNumber = $("#pattr_SERIAL_NUMBER").val();
//	var isPreoccupyZHZG = $("#pattr_ISPREOCCUPY").val();
	
	var isPreOccupy = $("#pattr_ISPREOCCUPY").val();//资源是否预占
	if(isPreOccupy=="0"){
		isPreOccupy = "否";
	}else if(isPreOccupy=="1"){
		isPreOccupy = "是";
	}
	var preReason = $("#pattr_PREREASON").val();//资源预占原因
	
	if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
		var ipPrice = $("#NOTIN_RSRV_STR10").val();
		var softwarePrice = $("#NOTIN_RSRV_STR11").val();
		var netPrice = $("#NOTIN_RSRV_STR12").val();
		
		var transferMode = $("#pattr_TRANSFERMODE").val();//传输接入方式
		var bizsecurityLv = $("#pattr_BIZSECURITYLV").val();//业务保障等级
		var portACustom = $("#pattr_PORTACUSTOM").val();//用户名称
		var provinceA = $("#pattr_PROVINCEA").val();//所属省份
		var cityA = $("#pattr_CITYA").val();//所属地市
		var areaA = $("#pattr_AREAA").val();//所属区县
		var countyA = $("#pattr_COUNTYA").val();//街道/乡镇
		var villageA = $("#pattr_VILLAGEA").val();//门牌/村组
		var portAInteracetype = $("#pattr_PORTAINTERFACETYPE").val();//业务端口类型
		var portAContact = $("#pattr_PORTACONTACT").val();//用户技术联系人
		var portAContactPhone = $("#pattr_PORTACONTACTPHONE").val();//用户技术联系人电话
		var IPType = $("#pattr_IPTYPE").val();//IP地址类型
		var cusAppServiIPAddNum = $("#pattr_CUSAPPSERVIPADDNUM").val();//客户申请公网IP地址数
		var cusAppServiIPV4AddNum = $("#pattr_CUSAPPSERVIPV4ADDNUM").val();//申请公网IPV4地址数
		var cusAppServiIPV6AddNum = $("#pattr_CUSAPPSERVIPV6ADDNUM").val();//申请公网IPV6地址数
		var domainName = $("#pattr_DOMAINNAME").val();//域名
		var mainDomainAdd = $("#pattr_MAINDOMAINADD").val();//主域名服务器地址
		var tradeName = $("#pattr_TRADENAME").val();//专线名称
		var routeMode = $("#pattr_ROUTEMODE").val();//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		var IPChange = $("#pattr_IPCHANGE").val();//IP地址调整
		if(!IPChange){
			IPChange = "";
		}
		
	}else if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
		var groupPercent = $("#NOTIN_RSRV_STR6").val();
		var softwarePrice = $("#NOTIN_RSRV_STR11").val();
		var netPrice = $("#NOTIN_RSRV_STR12").val();
		var aPercent = $("#NOTIN_RSRV_STR7").val();
		var zPercent = $("#NOTIN_RSRV_STR8").val();
		var slaServiceCost = $("#NOTIN_RSRV_STR16").val();//SLA服务费（元/月）
		
		var routeMode = $("#pattr_ROUTEMODE").val();//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		var transferMode = $("#pattr_TRANSFERMODE").val();//传输接入方式
		var bizsecurityLv = $("#pattr_BIZSECURITYLV").val();//业务保障等级
		var portACustom = $("#pattr_PORTACUSTOM").val();//A端用户名称
		var provinceA = $("#pattr_PROVINCEA").val();//A端所属省份
		var cityA = $("#pattr_CITYA").val();//A端所属地市
		var areaA = $("#pattr_AREAA").val();//A端所属区县
		var countyA = $("#pattr_COUNTYA").val();//A端街道/乡镇
		var villageA = $("#pattr_VILLAGEA").val();//A端门牌/村组
		var portAInteracetype = $("#pattr_PORTAINTERFACETYPE").val();//A端口类型
		var portAContact = $("#pattr_PORTACONTACT").val();//A端用户技术联系人
		var portAContactPhone = $("#pattr_PORTACONTACTPHONE").val();//A端用户技术联系人电话
		var portZCustom = $("#pattr_PORTZCUSTOM").val();//Z端用户名称
		var provinceZ = $("#pattr_PROVINCEZ").val();//Z端所属省份
		var cityZ = $("#pattr_CITYZ").val();//Z端所属地市
		var areaZ = $("#pattr_AREAZ").val();//Z端所属区县
		var countyZ = $("#pattr_COUNTYZ").val();//Z端街道/乡镇
		var villageZ = $("#pattr_VILLAGEZ").val();//Z端门牌/村组
		var portZInteracetype = $("#pattr_PORTZINTERFACETYPE").val();//Z端口类型
		var portZContact = $("#pattr_PORTZCONTACT").val();//Z端用户技术联系人
		var portZContactPhone = $("#pattr_PORTZCONTACTPHONE").val();//Z端用户技术联系人电话
		var tradeName = $("#pattr_TRADENAME").val();//专线名称
	}else if(offerCode=="7010"){
		var voiceCommunicateCost = $("#NOTIN_RSRV_STR15").val();//语音通信费（元/分钟）
		
		var transferMode = $("#pattr_TRANSFERMODE").val();//传输接入方式
		var supportMode = $("#pattr_SUPPORTMODE").val();//语音接入类型
		var bizsecurityLv = $("#pattr_BIZSECURITYLV").val();//业务保障等级
		var repeaterNum = $("#pattr_REPEATERNUM").val();//中继数
		var amount = $("#pattr_AMOUNT").val();//座机数量
		var portACustom = $("#pattr_PORTACUSTOM").val();//用户名称
		var provinceA = $("#pattr_PROVINCEA").val();//所属省份
		var cityA = $("#pattr_CITYA").val();//所属地市
		var areaA = $("#pattr_AREAA").val();//所属区县
		var countyA = $("#pattr_COUNTYA").val();//街道/乡镇
		var villageA = $("#pattr_VILLAGEA").val();//门牌/村组
		var portAInteracetype = $("#pattr_PORTAINTERFACETYPE").val();//业务端口类型
		var portAContact = $("#pattr_PORTACONTACT").val();//用户技术联系人
		var portAContactPhone = $("#pattr_PORTACONTACTPHONE").val();//用户技术联系人电话
		var isCustomerpe = $("#pattr_ISCUSTOMERPE").val();//客户侧是否自备业务设备
		var customerDeviceMode = $("#pattr_CUSTOMERDEVICEMODE").val();//客户侧设备类型
		var customerDeviceType = $("#pattr_CUSTOMERDEVICETYPE").val();//客户侧设备型号
		var customerDeviceVendor = $("#pattr_CUSTOMERDEVICEVENDOR").val();//客户侧设备厂家
		var phonePermission = $("#pattr_PHONEPERMISSION").val();//开通权限
		var phoneList = $("#pattr_PHONELIST").val();//码号段表
		var conproductNo = $("#pattr_CONPRODUCTNO").val();//关联的产品实例编号
		var tradeName =  $("#pattr_TRADENAME").val();//专线名称
		
		var routeMode = $("#pattr_ROUTEMODE").val();//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
	}else if(offerCode=="7016"){
		var ipPrice = $("#NOTIN_RSRV_STR10").val();
		var softwarePrice = $("#NOTIN_RSRV_STR11").val();
		var netPrice = $("#NOTIN_RSRV_STR12").val();
		
		var transferMode = $("#pattr_TRANSFERMODE").val();//传输接入方式
		var bizsecurityLv = $("#pattr_BIZSECURITYLV").val();//业务保障等级
		var amount = $("#pattr_AMOUNT").val();//座机数量（单位：门）
//		var repeaterNum = $("#pattr_REPEATERNUM").val();//座机数量（单位：门）
		
		var portACustom = $("#pattr_PORTACUSTOM").val();//用户名称
		var provinceA = $("#pattr_PROVINCEA").val();//所属省份
		var cityA = $("#pattr_CITYA").val();//所属地市
		var areaA = $("#pattr_AREAA").val();//所属区县
		var countyA = $("#pattr_COUNTYA").val();//街道/乡镇
		var villageA = $("#pattr_VILLAGEA").val();//门牌/村组
		var portAInteracetype = $("#pattr_PORTAINTERFACETYPE").val();//业务端口类型
		var portARate = $("#pattr_PORTARATE").val();//业务端口速率
		var portAContact = $("#pattr_PORTACONTACT").val();//用户技术联系人
		var portAContactPhone = $("#pattr_PORTACONTACTPHONE").val();//用户技术联系人电话
//		var IPType = $("#pattr_IPTYPE").val();//IP地址类型
		var cusAppServiIPAddNum = $("#pattr_CUSAPPSERVIPADDNUM").val();//客户申请公网IP地址数
//		var cusAppServiIPV4AddNum = $("#pattr_CUSAPPSERVIPV4ADDNUM").val();//申请公网IPV4地址数
//		var cusAppServiIPV6AddNum = $("#pattr_CUSAPPSERVIPV6ADDNUM").val();//申请公网IPV6地址数
		var isCustomerpe =  $("#pattr_ISCUSTOMERPE").val();//客户侧是否自备业务设备
		var customerDeviceMode = $("#pattr_CUSTOMERDEVICEMODE").val();//客户侧设备类型
		var customerDeviceType = $("#pattr_CUSTOMERDEVICETYPE").val();//客户侧设备型号
		var customerDeviceVendor = $("#pattr_CUSTOMERDEVICEVENDOR").val();//客户侧设备厂家
		var phoneList = $("#pattr_PHONELIST").val();//
		var customerDeviceList = $("#pattr_CUSTOMERDEVICELIST").val();//
		var domainName = $("#pattr_DOMAINNAME").val();//域名
		var mainDomainAdd = $("#pattr_MAINDOMAINADD").val();//主域名服务器地址
		var tradeName = $("#pattr_TRADENAME").val();//专线名称
		var routeMode = $("#pattr_ROUTEMODE").val();//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		var IPChange = $("#pattr_IPCHANGE").val();//IP地址调整
		if(!IPChange){
			IPChange = "";
		}
		
	}
	
	else if("FOURMANAGE"==templetId||"TIMERREVIEWFOURMANAGE"==templetId){
		var busiCategory = $("#pattr_BUSI_CATEGORY").text();
		var busiNumber = $("#pattr_BUSI_NUMBER").val();
		var busiSpeci = $("#pattr_BUSI_SPECI").val();
		var underNumber = $("#pattr_UNDER_NUMBER").val();
		var underSpeci = $("#pattr_UNDER_SPECI").val();
	}else if("VOICEMANAGE"==templetId||"TIMERREVIEWVOICEMANAGE"==templetId){
		var busiCategory = $("#pattr_BUSI_CATEGORY").text();
		var specBartype = $("#pattr_SPEC_BARTYPE").text();
	}else if("MOBILE400OPEN"==templetId){
		var fouezzNumber = $("#pattr_VPRODUCT_FOUEZZ_NUMBER").val();
		var fouezzNumberType = $("#pattr_VPRODUCT_FOUEZZ_NUMBER_TYPE").val();
		var packageType = $("#pattr_VPRODUCT_PACKAGE_TYPE").val();
		var requrleDecription = $("#pattr_REQURIE_DECRIPTION").val();
	}
	
	if(templetId=="EDIRECTLINEOPEN"||templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		if(templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
			liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"' onclick='setExportLineNos();'></input></div>";
		}else{
			liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'></input></div>";
		}
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div name='scroller1' jwcid='@Scroller' class='c_scroll' style='height:3em'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidthZHZG+"</span>";
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
			liHtml += "<span class='label'>IP地址调整：</span><span class='value'>"+IPChange+"</span>";
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
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
//			liHtml += "<li>";
//			liHtml += "<span class='label'>带宽需求：</span><span class='value'>"+repeaterNum+"</span>";
//			liHtml += "</li>";
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
			liHtml += "<span class='label'>业务端口最大速率：</span><span class='value'>"+portARate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
			liHtml += "</li>";
//			liHtml += "<li>";
//			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
//			liHtml += "</li>";
//			liHtml += "<li>";
//			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
//			liHtml += "</li>";
//			liHtml += "<li>";
//			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
//			liHtml += "</li>";
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
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
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
		liHtml += "<div class='fn' productNo='"+lineNo+"' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="EDIRECTLINECHANGE"){
		debugger;
		var userId = $("#pattr_USER_ID").val();
		var serialNumber = $("#pattr_SERIAL_NUMBER").val();
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' userId='"+userId+"' serialNumber='"+serialNumber+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'></input></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div name='scroller1' jwcid='@Scroller' class='c_scroll' style='height:3em'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidthZHZG+"</span>";
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
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			
			liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
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
		liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}
	else if(templetId=="ERESOURCECONFIRMZHZG"||templetId=="ECHANGERESOURCECONFIRM"){
		var lineNo = $("#pattr_PRODUCTNO").val();
		var bandWidth = $("#pattr_BANDWIDTH").val();
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		if(templetId=="ECHANGERESOURCECONFIRM"){
			liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"'></input></div>";
		}else{
			liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'></input></div>";
		}
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div name='scroller1' jwcid='@Scroller' class='c_scroll' style='height:3em'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		if(offerCode=="7010"){
			liHtml += "<li>";
			liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			liHtml += "<li>";
			liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
			liHtml += "<li>";
			liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
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
		}
		if(offerCode=="7016"){
			liHtml += "<li>";
			liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
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
			liHtml += "<span class='label'>业务端口最大速率：</span><span class='value'>"+portARate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		liHtml += "</ul>";
		liHtml += "</div></div></div></div>";
		liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="FOURMANAGE"||"TIMERREVIEWFOURMANAGE"==templetId){
		var liHtml = "<li id='400_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='400_"+maxIndex+"'/></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务类别：</span><span class='value'>"+busiCategory+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>400号码数量：</span><span class='value'>"+busiNumber+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>400具体号码：</span><span class='value'>"+busiSpeci+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>下挂数量：</span><span class='value'>"+underNumber+"</span>";
		liHtml += "</li>";	
		liHtml += "<li>";
		liHtml += "<span class='label'>下挂具体号码：</span><span class='value'>"+underSpeci+"</span>";
		liHtml += "</li>";	
		liHtml += "</ul>";
		liHtml += "</div></div></div>";
		liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="VOICEMANAGE"||templetId=="TIMERREVIEWVOICEMANAGE"){
		var liHtml = "<li id='400_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='400_"+maxIndex+"'></input></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-2'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务类别：</span><span class='value'>"+busiCategory+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>线路类型：</span><span class='value'>"+specBartype+"</span>";
		liHtml += "</li>";
		liHtml += "</ul>";
		liHtml += "</div></div></div>";
		liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="MOBILE400OPEN"){
		var liHtml = "<li id='400_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='400_"+maxIndex+"'></input></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>400号码：</span><span class='value'>"+fouezzNumber+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>400号码类别：</span><span class='value'>"+fouezzNumberType+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>套餐类型：</span><span class='value'>"+packageType+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>业务需求描述：</span><span class='value'>"+requrleDecription+"</span>";
		liHtml += "</li>";	
		liHtml += "</ul>";
		liHtml += "</div></div></div>";
		liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}
	
	if(templetId=="DIRECTLINEMARKETINGADD"||templetId=="DIRECTLINEMARKETINGUPD"){
		lineNo = $("#pattr_NOTIN_LINE_NO").val();
		bandWidth = $("#pattr_NOTIN_RSRV_STR1").val();
		
		var term = $("#pattr_NOTIN_TERM").val();
		var cost = $("#pattr_NOTIN_COST").val();
		var paybackPeriod = $("#pattr_NOTIN_PAYBACK_PERIOD").val();
		var hundred = $("#pattr_NOTIN_HUNDRED").val();

		
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'></input></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线实例号：</span><span class='value'>"+lineNo+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>预计资费(元/月)：</span><span class='value'>"+cost+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>协议期限(年)：</span><span class='value'>"+term+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>回收期(年)：</span><span class='value'>"+paybackPeriod+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>百元值：</span><span class='value'>"+hundred+"</span>";
		liHtml += "</li>";
		liHtml += "</ul>";
		liHtml += "</div></div></div>";
		liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
		
	}
	
	
	var newOrModify = $("#OFFER_CHA_HIDDEN_ID").val(); 
	if(newOrModify.match("new")){
		$("#DATALINE_PARAM_UL").append(liHtml);
	}else{
		$("input:checkbox[name=DATALINE_TAG]:checked").each(function(){
			var checkBoxId = $(this).attr("id");
			$("#"+checkBoxId+"_LI").replaceWith(liHtml);
		});
	}
	if($("#DATALINE_PARAM_UL").children().length > 0)
	{
		$("#DATALINE_PARAM_UL").parent(".c_list").css("display", "");
	}
	
		maxIndex++;
	
	$("#DATALINE_MAX_INDEX").val(maxIndex);
	$("#check_tag").val(check_tag);
}

//删除专线参数列表
function deleteDataLineParam()
{
	var checkedNum = getCheckedBoxNum("DATALINE_TAG");
	if(checkedNum < 1)
	{
		MessageBox.alert("提示信息", "请选择需要删除的信息！");
		return false;
	}
	$("input:checkbox[name=DATALINE_TAG]:checked").each(function(){
		var checkBoxId = $(this).attr("id");
		$("#"+checkBoxId+"_LI").remove();
		if($("#DATALINE_PARAM_UL").children().length == 0)
		{
			$("#DATALINE_PARAM_UL").parent(".c_list").css("display", "none");
		}
		var offerCode = $("#cond_OFFER_CODE").val();
		if("9983"==offerCode){
		$("#DATALINE_MAX_INDEX").val("0");
		}
	});
}

//删除基本资料专线参数列表
function deleteDerectLineDataChangeParam()
{
	debugger;
	var checkedNum = getCheckedBoxNum("DATALINE_TAG");
	if(checkedNum < 1)
	{
		MessageBox.alert("提示信息", "请选择需要删除的信息！");
		return false;
	}
	$("input:checkbox[name=DATALINE_TAG]:checked").each(function(){
		var checkBoxId = $(this).attr("id");
		$("#"+checkBoxId+"_LI").remove();
		if($("#DATALINE_PARAM").children().length == 0)
		{
			$("#DATALINE_PARAM").parent(".c_list").css("display", "none");
		}
	});
}

function refreshLineType(lineScope,lineType){
	debugger;
	var lineScopeValue = lineScope.value;
	var productIdObj = $("#cond_OFFER_CODE").val();
	var directlineScope = $("#pattr_DIRECTLINE_SCOPE").val();//专线范围
	var directlineType = $("#pattr_DIRECTLINE_TYPE").val(); //专线类型
	
	changeBizRange();
}


function changeBizRange()
{  
	debugger;
	var directlineScope = $("#pattr_DIRECTLINE_SCOPE").val();
	var eomsBizRange = $("#eoms_BizRange").val();
	
	if(directlineScope && eomsBizRange)
	{
		if(directlineScope.value =='2'){
			$("#eoms_BizRange").val("省内跨地市");
		}else if(directlineScope.value == '1'){
			$("#eoms_BizRange").val("本地市");
		}else {
			$("#eoms_BizRange").val("类型不明确");
		}
	}
}

function showLineInfo(obj)
{
	debugger;
	var lineType = obj.value;
	var params = '&DIRECTLINE_TYPE='+lineType;
	$.beginPageLoading();
	$.ajax.submit('','changeEosAttrData',params,'','','',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function showLineInfoByLineType(lineType)
{
	debugger;
	var params = '&DIRECTLINE_TYPE='+lineType;
	$.beginPageLoading();
	$.ajax.submit('','changeEosAttrData',params,'','','',function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function loadBatchDataLineParamList(obj){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	var isReadonly = $("#IS_READONLY").val();
	
	if(templetId=="EDIRECTLINEOPEN"||templetId=="EDIRECTLINECHANGE"||templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		var price = obj.get("NOTIN_RSRV_STR2","");
		var installationCost = obj.get("NOTIN_RSRV_STR3","");// 一次性费用（安装调试费）（元）
		var userId = obj.get("USER_ID","");
		var serialNumber = obj.get("SERIAL_NUMBER","");
		
		var lineNo = obj.get("NOTIN_LINE_NO","");
		var bandWidth = obj.get("NOTIN_RSRV_STR1","");
		
		var isPreOccupy = obj.get("ISPREOCCUPY","");//资源是否预占
		if(isPreOccupy=="0"){
			isPreOccupy = "否";
		}else if(isPreOccupy=="1"){
			isPreOccupy = "是";
		}
		var preReason = obj.get("PREREASON","");//资源预占原因
		
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			var ipPrice = obj.get("NOTIN_RSRV_STR10","");
			var softwarePrice = obj.get("NOTIN_RSRV_STR11","");
			var netPrice = obj.get("NOTIN_RSRV_STR12","");
			
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("PORTACUSTOM","");//用户名称
			var provinceA = obj.get("PROVINCEA","");//所属省份
			var cityA = obj.get("CITYA","");//所属地市
			var areaA = obj.get("AREAA","");//所属区县
			var countyA = obj.get("COUNTYA","");//街道/乡镇
			var villageA = obj.get("VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
			var IPType = obj.get("IPTYPE","");//IP地址类型
			var cusAppServiIPAddNum = obj.get("CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
			var cusAppServiIPV4AddNum = obj.get("CUSAPPSERVIPV4ADDNUM","");//申请公网IPV4地址数
			var cusAppServiIPV6AddNum = obj.get("CUSAPPSERVIPV6ADDNUM","");//申请公网IPV6地址数
			var domainName = obj.get("DOMAINNAME","");//域名
			var mainDomainAdd = obj.get("MAINDOMAINADD","");//主域名服务器地址
			var tradeName = obj.get("TRADENAME","");//专线名称
			
			var routeMode = obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
			var IPChange = obj.get("IPCHANGE","");//IP地址调整
		}else if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
			var groupPercent = obj.get("NOTIN_RSRV_STR6","");
			var softwarePrice = obj.get("NOTIN_RSRV_STR11","");
			var netPrice = obj.get("NOTIN_RSRV_STR12","");
			var aPercent = obj.get("NOTIN_RSRV_STR7","");
			var zPercent = obj.get("NOTIN_RSRV_STR8","");
			var slaServiceCost = obj.get("NOTIN_RSRV_STR16","");// SLA服务费（元/月）
			
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var routeMode = obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("PORTACUSTOM","");//A端用户名称
			var provinceA = obj.get("PROVINCEA","");//A端所属省份
			var cityA = obj.get("CITYA","");//A端所属地市
			var areaA = obj.get("AREAA","");//A端所属区县
			var countyA = obj.get("COUNTYA","");//A端街道/乡镇
			var villageA = obj.get("VILLAGEA","");//A端门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//A端口类型
			var portAContact = obj.get("PORTACONTACT","");//A端用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//A端用户技术联系人电话
			var portZCustom = obj.get("PORTZCUSTOM","");//Z端用户名称
			var provinceZ = obj.get("PROVINCEZ","");//Z端所属省份
			var cityZ = obj.get("CITYZ","");//Z端所属地市
			var areaZ = obj.get("AREAZ","");//Z端所属区县
			var countyZ = obj.get("COUNTYZ","");//Z端街道/乡镇
			var villageZ = obj.get("VILLAGEZ","");//Z端门牌/村组
			var portZInteracetype = obj.get("PORTZINTERFACETYPE","");//Z端口类型
			var portZContact = obj.get("PORTZCONTACT","");//Z端用户技术联系人
			var portZContactPhone = obj.get("PORTZCONTACTPHONE","");//Z端用户技术联系人电话
			var tradeName = obj.get("TRADENAME","");//专线名称
		}else if(offerCode=="7010"){
			var voiceCommunicateCost = obj.get("NOTIN_RSRV_STR15","");// 语音通信费（元/分钟）
			
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var supportMode = obj.get("SUPPORTMODE","");//语音接入类型
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var repeaterNum = obj.get("REPEATERNUM","");//中继数
			var amount = obj.get("AMOUNT","");//座机数量
			var portACustom = obj.get("PORTACUSTOM","");//用户名称
			var provinceA = obj.get("PROVINCEA","");//所属省份
			var cityA = obj.get("CITYA","");//所属地市
			var areaA = obj.get("AREAA","");//所属区县
			var countyA = obj.get("COUNTYA","");//街道/乡镇
			var villageA = obj.get("VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
			var isCustomerpe = obj.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
			var customerDeviceMode = obj.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
			var customerDeviceType = obj.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
			var customerDeviceVendor = obj.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
			var phonePermission = obj.get("PHONEPERMISSION","");//开通权限
			var phoneList = obj.get("PHONELIST","");//码号段表
			var conproductNo = obj.get("CONPRODUCTNO","");//关联的产品实例编号
			var tradeName =  obj.get("TRADENAME","");//专线名称
			
			var routeMode =  obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
		}
		if(offerCode=="7016"){
			var ipPrice = obj.get("NOTIN_RSRV_STR10","");
			var softwarePrice = obj.get("NOTIN_RSRV_STR11","");
			var netPrice = obj.get("NOTIN_RSRV_STR12","");
			
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("PORTACUSTOM","");//用户名称
			var provinceA = obj.get("PROVINCEA","");//所属省份
			var cityA = obj.get("CITYA","");//所属地市
			var areaA = obj.get("AREAA","");//所属区县
			var countyA = obj.get("COUNTYA","");//街道/乡镇
			var villageA = obj.get("VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
			var cusAppServiIPAddNum = obj.get("CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
			var domainName = obj.get("DOMAINNAME","");//域名
			var mainDomainAdd = obj.get("MAINDOMAINADD","");//主域名服务器地址
			var tradeName = obj.get("TRADENAME","");//专线名称
			
			var routeMode = obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
			
			var portaRate = obj.get("PORTARATE","");//业务端口速率
			var isCustomerpe = obj.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
			var customerDeviceMode = obj.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
			var customerDeviceType = obj.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
			var customerDeviceVendor = obj.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
			var phoneList = obj.get("PHONELIST","");//码号段表
			var supportMode = obj.get("SUPPORTMODE","");//语音接入类型
			var amount = obj.get("AMOUNT","");//座机数量

		}
		
		
		
		lineNo = obj.get("PRODUCTNO","");
		bandWidth = obj.get("BANDWIDTH","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"' onclick='setExportLineNos()'></input></div>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>IP地址调整：</span><span class='value'>"+IPChange+"</span>";
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
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portaRate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
			liHtml += "</li>";
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
			liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
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
		if(templetId=="DIRECTLINECHANGESIMPLE"||templetId=="EDIRECTLINEOPEN"||templetId=="EDIRECTLINECHANGE"){
			liHtml += "<div class='fn' productNo='"+lineNo+"' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		}
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="ERESOURCECONFIRMZHZG"||templetId=="ECHANGERESOURCECONFIRM"){
		if(templetId=="ERESOURCECONFIRMZHZG"){
			var lineNo = obj.get("NOTIN_LINE_NO");
		}else if(templetId=="ECHANGERESOURCECONFIRM"){
			var lineNo = obj.get("pattr_TRADEID");
		}
		var bandWidth = obj.get("pattr_BANDWIDTH");
		
		var isPreOccupy = obj.get("pattr_ISPREOCCUPY","");//资源是否预占
		if(isPreOccupy=="0"){
			isPreOccupy = "否";
		}else if(isPreOccupy=="1"){
			isPreOccupy = "是";
		}
		var preReason = obj.get("pattr_PREREASON","");//资源预占原因
		var routeMode = obj.get("pattr_ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			var transferMode = obj.get("pattr_TRANSFERMODE","");//传输接入方式
			var bizsecurityLv = obj.get("pattr_BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("pattr_PORTACUSTOM","");//用户名称
			var provinceA = obj.get("pattr_PROVINCEA","");//所属省份
			var cityA = obj.get("pattr_CITYA","");//所属地市
			var areaA = obj.get("pattr_AREAA","");//所属区县
			var countyA = obj.get("pattr_COUNTYA","");//街道/乡镇
			var villageA = obj.get("pattr_VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("pattr_PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("pattr_PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("pattr_PORTACONTACTPHONE","");//用户技术联系人电话
			var IPType = obj.get("pattr_IPTYPE","");//IP地址类型
			var cusAppServiIPAddNum = obj.get("pattr_CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
			var cusAppServiIPV4AddNum = obj.get("pattr_CUSAPPSERVIPV4ADDNUM","");//申请公网IPV4地址数
			var cusAppServiIPV6AddNum = obj.get("pattr_CUSAPPSERVIPV6ADDNUM","");//申请公网IPV6地址数
			var domainName = obj.get("pattr_DOMAINNAME","");//域名
			var mainDomainAdd = obj.get("pattr_MAINDOMAINADD","");//主域名服务器地址
			var tradeName = obj.get("pattr_TRADENAME","");//专线名称
		}else if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
			var transferMode = obj.get("pattr_TRANSFERMODE","");//传输接入方式
			var bizsecurityLv = obj.get("pattr_BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("pattr_PORTACUSTOM","");//A端用户名称
			var provinceA = obj.get("pattr_PROVINCEA","");//A端所属省份
			var cityA = obj.get("pattr_CITYA","");//A端所属地市
			var areaA = obj.get("pattr_AREAA","");//A端所属区县
			var countyA = obj.get("pattr_COUNTYA","");//A端街道/乡镇
			var villageA = obj.get("pattr_VILLAGEA","");//A端门牌/村组
			var portAInteracetype = obj.get("pattr_PORTAINTERFACETYPE","");//A端口类型
			var portAContact = obj.get("pattr_PORTACONTACT","");//A端用户技术联系人
			var portAContactPhone = obj.get("pattr_PORTACONTACTPHONE","");//A端用户技术联系人电话
			var portZCustom = obj.get("pattr_PORTZCUSTOM","");//Z端用户名称
			var provinceZ = obj.get("pattr_PROVINCEZ","");//Z端所属省份
			var cityZ = obj.get("pattr_CITYZ","");//Z端所属地市
			var areaZ = obj.get("pattr_AREAZ","");//Z端所属区县
			var countyZ = obj.get("pattr_COUNTYZ","");//Z端街道/乡镇
			var villageZ = obj.get("pattr_VILLAGEZ","");//Z端门牌/村组
			var portZInteracetype = obj.get("pattr_PORTZINTERFACETYPE","");//Z端口类型
			var portZContact = obj.get("pattr_PORTZCONTACT","");//Z端用户技术联系人
			var portZContactPhone = obj.get("pattr_PORTZCONTACTPHONE","");//Z端用户技术联系人电话
			var tradeName = obj.get("pattr_TRADENAME","");//专线名称
		}else if(offerCode=="7010"){
			var transferMode = obj.get("pattr_TRANSFERMODE","");//传输接入方式
			var supportMode = obj.get("pattr_pattr_SUPPORTMODE","");//语音接入类型
			var bizsecurityLv = obj.get("pattr_BIZSECURITYLV","");//业务保障等级
			var repeaterNum = obj.get("pattr_REPEATERNUM","");//中继数
			var amount = obj.get("pattr_AMOUNT","");//座机数量
			var portACustom = obj.get("pattr_PORTACUSTOM","");//用户名称
			var provinceA = obj.get("pattr_PROVINCEA","");//所属省份
			var cityA = obj.get("pattr_CITYA","");//所属地市
			var areaA = obj.get("pattr_AREAA","");//所属区县
			var countyA = obj.get("pattr_COUNTYA","");//街道/乡镇
			var villageA = obj.get("pattr_VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("pattr_PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("pattr_PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("pattr_PORTACONTACTPHONE","");//用户技术联系人电话
			var isCustomerpe = obj.get("pattr_ISCUSTOMERPE","");//客户侧是否自备业务设备
			var customerDeviceMode = obj.get("pattr_CUSTOMERDEVICEMODE","");//客户侧设备类型
			var customerDeviceType = obj.get("pattr_CUSTOMERDEVICETYPE","");//客户侧设备型号
			var customerDeviceVendor = obj.get("pattr_CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
			var phonePermission = obj.get("pattr_PHONEPERMISSION","");//开通权限
			var phoneList = obj.get("pattr_PHONELIST","");//码号段表
			var conproductNo = obj.get("pattr_CONPRODUCTNO","");//关联的产品实例编号
			var tradeName =  obj.get("pattr_TRADENAME","");//专线名称
		}
		if(offerCode=="7016"){
			var ipPrice = obj.get("NOTIN_RSRV_STR10","");
			var softwarePrice = obj.get("NOTIN_RSRV_STR11","");
			var netPrice = obj.get("NOTIN_RSRV_STR12","");
			
			var transferMode = obj.get("pattr_TRANSFERMODE","");//传输接入方式
			var bizsecurityLv = obj.get("pattr_BIZSECURITYLV","");//业务保障等级
			var repeaterNum = obj.get("pattr_REPEATERNUM","");//宽带需求
			var amount = obj.get("pattr_AMOUNT","");//座机数量
			var portACustom = obj.get("pattr_PORTACUSTOM","");//用户名称
			var provinceA = obj.get("pattr_PROVINCEA","");//所属省份
			var cityA = obj.get("pattr_CITYA","");//所属地市
			var areaA = obj.get("pattr_AREAA","");//所属区县
			var countyA = obj.get("pattr_COUNTYA","");//街道/乡镇
			var villageA = obj.get("pattr_VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("pattr_PORTAINTERFACETYPE","");//业务端口类型
			var portARate = obj.get("pattr_PORTARATE","");//业务端口速率
			var portAContact = obj.get("pattr_PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("pattr_PORTACONTACTPHONE","");//用户技术联系人电话
			var domainName = obj.get("pattr_DOMAINNAME","");//域名
			var mainDomainAdd = obj.get("pattr_MAINDOMAINADD","");//主域名服务器地址
			var tradeName = obj.get("pattr_TRADENAME","");//专线名称
		}
		
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'></input></div>";
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
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
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
		}
		if(offerCode=="7016"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
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
			liHtml += "<span class='label'>业务端口最大速率：</span><span class='value'>"+portARate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
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
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		liHtml += "</ul>";
		liHtml += "</div></div></div></div>";
		liHtml += "<div class='fn' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="DIRECTLINEMARKETINGADD"||templetId=="DIRECTLINEMARKETINGUPD"){
		var lineNo = obj.get("NOTIN_LINE_NO","");
		var bandWidth = obj.get("NOTIN_RSRV_STR1","");
		var term = obj.get("NOTIN_TERM","");
		var cost = obj.get("NOTIN_COST","");
		var paybackPeriod = obj.get("NOTIN_PAYBACK_PERIOD","");
		var hundred = obj.get("NOTIN_HUNDRED","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'></input></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线实例号：</span><span class='value'>"+lineNo+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>专线宽带(兆)：</span><span class='value'>"+bandWidth+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>预计资费(元/月)：</span><span class='value'>"+cost+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>协议期限(年)：</span><span class='value'>"+term+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>回收期(年)：</span><span class='value'>"+paybackPeriod+"</span>";
		liHtml += "</li>";
		liHtml += "<li>";
		liHtml += "<span class='label'>百元值：</span><span class='value'>"+hundred+"</span>";
		liHtml += "</li>";
		liHtml += "</ul>";
		liHtml += "</div></div></div>";
		liHtml += "<div class='fn' ontap='modifyContracLineParam("+obj+");'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="EDIRECTLINECANCEL"){
		var userId = obj.get("USER_ID","");
		var serialNumber = obj.get("SERIAL_NUMBER","");
		
		var lineNo = obj.get("NOTIN_LINE_NO","");
		var bandWidth = obj.get("NOTIN_RSRV_STR1","");
		
		var isPreOccupy = obj.get("ISPREOCCUPY","");//资源是否预占
		if(isPreOccupy=="0"){
			isPreOccupy = "否";
		}else if(isPreOccupy=="1"){
			isPreOccupy = "是";
		}
		var preReason = obj.get("PREREASON","");//资源预占原因
		
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("PORTACUSTOM","");//用户名称
			var provinceA = obj.get("PROVINCEA","");//所属省份
			var cityA = obj.get("CITYA","");//所属地市
			var areaA = obj.get("AREAA","");//所属区县
			var countyA = obj.get("COUNTYA","");//街道/乡镇
			var villageA = obj.get("VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
			var IPType = obj.get("IPTYPE","");//IP地址类型
			var cusAppServiIPAddNum = obj.get("CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
			var cusAppServiIPV4AddNum = obj.get("CUSAPPSERVIPV4ADDNUM","");//申请公网IPV4地址数
			var cusAppServiIPV6AddNum = obj.get("CUSAPPSERVIPV6ADDNUM","");//申请公网IPV6地址数
			var domainName = obj.get("DOMAINNAME","");//域名
			var mainDomainAdd = obj.get("MAINDOMAINADD","");//主域名服务器地址
			var tradeName = obj.get("TRADENAME","");//专线名称
			
			var routeMode = obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
			var IPChange = obj.get("IPCHANGE","");//IP地址调整
		}else if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var routeMode = obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("PORTACUSTOM","");//A端用户名称
			var provinceA = obj.get("PROVINCEA","");//A端所属省份
			var cityA = obj.get("CITYA","");//A端所属地市
			var areaA = obj.get("AREAA","");//A端所属区县
			var countyA = obj.get("COUNTYA","");//A端街道/乡镇
			var villageA = obj.get("VILLAGEA","");//A端门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//A端口类型
			var portAContact = obj.get("PORTACONTACT","");//A端用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//A端用户技术联系人电话
			var portZCustom = obj.get("PORTZCUSTOM","");//Z端用户名称
			var provinceZ = obj.get("PROVINCEZ","");//Z端所属省份
			var cityZ = obj.get("CITYZ","");//Z端所属地市
			var areaZ = obj.get("AREAZ","");//Z端所属区县
			var countyZ = obj.get("COUNTYZ","");//Z端街道/乡镇
			var villageZ = obj.get("VILLAGEZ","");//Z端门牌/村组
			var portZInteracetype = obj.get("PORTZINTERFACETYPE","");//Z端口类型
			var portZContact = obj.get("PORTZCONTACT","");//Z端用户技术联系人
			var portZContactPhone = obj.get("PORTZCONTACTPHONE","");//Z端用户技术联系人电话
			var tradeName = obj.get("TRADENAME","");//专线名称
		}else if(offerCode=="7010"){
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var supportMode = obj.get("SUPPORTMODE","");//语音接入类型
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var repeaterNum = obj.get("REPEATERNUM","");//中继数
			var amount = obj.get("AMOUNT","");//座机数量
			var portACustom = obj.get("PORTACUSTOM","");//用户名称
			var provinceA = obj.get("PROVINCEA","");//所属省份
			var cityA = obj.get("CITYA","");//所属地市
			var areaA = obj.get("AREAA","");//所属区县
			var countyA = obj.get("COUNTYA","");//街道/乡镇
			var villageA = obj.get("VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
			var isCustomerpe = obj.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
			var customerDeviceMode = obj.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
			var customerDeviceType = obj.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
			var customerDeviceVendor = obj.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
			var phonePermission = obj.get("PHONEPERMISSION","");//开通权限
			var phoneList = obj.get("PHONELIST","");//码号段表
			var conproductNo = obj.get("CONPRODUCTNO","");//关联的产品实例编号
			var tradeName =  obj.get("TRADENAME","");//专线名称
			
			var routeMode =  obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
		}else if(offerCode=="7016"){
			
			var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
			var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
			var portACustom = obj.get("PORTACUSTOM","");//用户名称
			var provinceA = obj.get("PROVINCEA","");//所属省份
			var cityA = obj.get("CITYA","");//所属地市
			var areaA = obj.get("AREAA","");//所属区县
			var countyA = obj.get("COUNTYA","");//街道/乡镇
			var villageA = obj.get("VILLAGEA","");//门牌/村组
			var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
			var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
			var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
			var cusAppServiIPAddNum = obj.get("CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
			var domainName = obj.get("DOMAINNAME","");//域名
			var mainDomainAdd = obj.get("MAINDOMAINADD","");//主域名服务器地址
			var tradeName = obj.get("TRADENAME","");//专线名称
			
			var routeMode = obj.get("ROUTEMODE","");//路由保护方式
			if(routeMode=="0"){
				routeMode = "单节点单路由";
			}else if(routeMode=="1"){
				routeMode = "单节点双路由";
			}else if(routeMode=="2"){
				routeMode = "双节点双路由";
			}
			
			var portaRate = obj.get("PORTARATE","");//业务端口速率
			var isCustomerpe = obj.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
			var customerDeviceMode = obj.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
			var customerDeviceType = obj.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
			var customerDeviceVendor = obj.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
			var phoneList = obj.get("PHONELIST","");//码号段表
			var supportMode = obj.get("SUPPORTMODE","");//语音接入类型
			var amount = obj.get("AMOUNT","");//座机数量
		}
		
		lineNo = obj.get("PRODUCTNO","");
		bandWidth = obj.get("BANDWIDTH","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"' onclick='setExportLineNos()'></input></div>";
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
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
		}
		if(offerCode=="7016"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portaRate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
			liHtml += "</li>";
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
			liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		liHtml += "</ul>";
		liHtml += "</div></div></div></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}
	$("#DATALINE_PARAM_UL").append(liHtml);
	if($("#DATALINE_PARAM_UL").children().length > 0)
	{
		$("#DATALINE_PARAM_UL").parent(".c_list").css("display", "");
	}
	$("#DATALINE_MAX_INDEX").val(maxIndex);
}

//简单变更加载专线参数
function loadChangeDataLineParamList(obj){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var templetId = $("#cond_TEMPLET_ID").val();

	var price = obj.get("NOTIN_RSRV_STR2","");
	var installationCost = obj.get("NOTIN_RSRV_STR3","");// 一次性费用（安装调试费）（元）
	var userId = obj.get("USER_ID");
	var serialNumber = obj.get("SERIAL_NUMBER","");
	
	var lineNo = obj.get("NOTIN_LINE_NO","");
	var bandWidth = obj.get("NOTIN_RSRV_STR1","");
	
	var changeMode = $("#pattr_CHANGEMODE").val();

	if(templetId=="DIRECTLINECHANGESIMPLE"){
	if(""!=changeMode||null!=changeMode||undefined==changeMode){
			$.ajax.submit("", "hintInfo", "&PRODUCT_ID="+offerCode+"&CHANGEMODE="+changeMode, "TipInfoPart", function(data){
				$.endPageLoading();
			}, 
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			},{async:false});
		}
	}
	
	var isPreOccupy = obj.get("ISPREOCCUPY","");//资源是否预占
	if(isPreOccupy=="0"){
		isPreOccupy = "否";
	}else if(isPreOccupy=="1"){
		isPreOccupy = "是";
	}
	var preReason = obj.get("PREREASON","");//资源预占原因
	
	if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
		var ipPrice = obj.get("NOTIN_RSRV_STR10","");
		var softwarePrice = obj.get("NOTIN_RSRV_STR11","");
		var netPrice = obj.get("NOTIN_RSRV_STR12","");
		
		var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
		var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
		var portACustom = obj.get("PORTACUSTOM","");//用户名称
		var provinceA = obj.get("PROVINCEA","");//所属省份
		var cityA = obj.get("CITYA","");//所属地市
		var areaA = obj.get("AREAA","");//所属区县
		var countyA = obj.get("COUNTYA","");//街道/乡镇
		var villageA = obj.get("VILLAGEA","");//门牌/村组
		var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
		var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
		var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
		var IPType = obj.get("IPTYPE","");//IP地址类型
		var cusAppServiIPAddNum = obj.get("CUSAPPSERVIPADDNUM","0");//客户申请公网IP地址数
		var cusAppServiIPV4AddNum = obj.get("CUSAPPSERVIPV4ADDNUM","0");//申请公网IPV4地址数
		var cusAppServiIPV6AddNum = obj.get("CUSAPPSERVIPV6ADDNUM","0");//申请公网IPV6地址数
		var domainName = obj.get("DOMAINNAME","");//域名
		var mainDomainAdd = obj.get("MAINDOMAINADD","");//主域名服务器地址
		var tradeName = obj.get("TRADENAME","");//专线名称
		var IPchange = obj.get("IPCHANGE","");//IP地址调整
		var routeMode = obj.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
	}else if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
		var groupPercent = obj.get("NOTIN_RSRV_STR6");
		var softwarePrice = obj.get("NOTIN_RSRV_STR11");
		var netPrice = obj.get("NOTIN_RSRV_STR12");
		var aPercent = obj.get("NOTIN_RSRV_STR7");
		var zPercent = obj.get("NOTIN_RSRV_STR8");
		var slaServiceCost = obj.get("NOTIN_RSRV_STR16");// SLA服务费（元/月）
		
		var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
		var routeMode = obj.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
		var portACustom = obj.get("PORTACUSTOM","");//A端用户名称
		var provinceA = obj.get("PROVINCEA","");//A端所属省份
		var cityA = obj.get("CITYA","");//A端所属地市
		var areaA = obj.get("AREAA","");//A端所属区县
		var countyA = obj.get("COUNTYA","");//A端街道/乡镇
		var villageA = obj.get("VILLAGEA","");//A端门牌/村组
		var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//A端口类型
		var portAContact = obj.get("PORTACONTACT","");//A端用户技术联系人
		var portAContactPhone = obj.get("PORTACONTACTPHONE","");//A端用户技术联系人电话
		var portZCustom = obj.get("PORTZCUSTOM","");//Z端用户名称
		var provinceZ = obj.get("PROVINCEZ","");//Z端所属省份
		var cityZ = obj.get("CITYZ","");//Z端所属地市
		var areaZ = obj.get("AREAZ","");//Z端所属区县
		var countyZ = obj.get("COUNTYZ","");//Z端街道/乡镇
		var villageZ = obj.get("VILLAGEZ","");//Z端门牌/村组
		var portZInteracetype = obj.get("PORTZINTERFACETYPE","");//Z端口类型
		var portZContact = obj.get("PORTZCONTACT","");//Z端用户技术联系人
		var portZContactPhone = obj.get("PORTZCONTACTPHONE","");//Z端用户技术联系人电话
		var tradeName = obj.get("TRADENAME","");//专线名称
	}else if(offerCode=="7010"){
		var voiceCommunicateCost = obj.get("NOTIN_RSRV_STR15");// 语音通信费（元/分钟）
		
		var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
		var supportMode = obj.get("SUPPORTMODE","");//语音接入类型
		var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
		var repeaterNum = obj.get("REPEATERNUM","");//中继数
		var amount = obj.get("AMOUNT","");//座机数量
		var portACustom = obj.get("PORTACUSTOM","");//用户名称
		var provinceA = obj.get("PROVINCEA","");//所属省份
		var cityA = obj.get("CITYA","");//所属地市
		var areaA = obj.get("AREAA","");//所属区县
		var countyA = obj.get("COUNTYA","");//街道/乡镇
		var villageA = obj.get("VILLAGEA","");//门牌/村组
		var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
		var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
		var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
		var isCustomerpe = obj.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
		var customerDeviceMode = obj.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
		var customerDeviceType = obj.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
		var customerDeviceVendor = obj.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
		var phonePermission = obj.get("PHONEPERMISSION","");//开通权限
		var phoneList = obj.get("PHONELIST","");//码号段表
		var conproductNo = obj.get("CONPRODUCTNO","");//关联的产品实例编号
		var tradeName =  obj.get("TRADENAME","");//专线名称
		
		var routeMode =  obj.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
	}else if(offerCode=="7016"){
		var ipPrice = obj.get("NOTIN_RSRV_STR10","");
		var softwarePrice = obj.get("NOTIN_RSRV_STR11","");
		var netPrice = obj.get("NOTIN_RSRV_STR12","");
		
		var transferMode = obj.get("TRANSFERMODE","");//传输接入方式
		var bizsecurityLv = obj.get("BIZSECURITYLV","");//业务保障等级
		var portACustom = obj.get("PORTACUSTOM","");//用户名称
		var provinceA = obj.get("PROVINCEA","");//所属省份
		var cityA = obj.get("CITYA","");//所属地市
		var areaA = obj.get("AREAA","");//所属区县
		var countyA = obj.get("COUNTYA","");//街道/乡镇
		var villageA = obj.get("VILLAGEA","");//门牌/村组
		var portAInteracetype = obj.get("PORTAINTERFACETYPE","");//业务端口类型
		var portARate = obj.get("#PORTARATE","");//业务端口速率
		var portAContact = obj.get("PORTACONTACT","");//用户技术联系人
		var portAContactPhone = obj.get("PORTACONTACTPHONE","");//用户技术联系人电话
		var IPType = obj.get("IPTYPE","");//IP地址类型
		var cusAppServiIPAddNum = obj.get("CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
		var cusAppServiIPV4AddNum = obj.get("CUSAPPSERVIPV4ADDNUM","");//申请公网IPV4地址数
		var cusAppServiIPV6AddNum = obj.get("CUSAPPSERVIPV6ADDNUM","");//申请公网IPV6地址数
		var domainName = obj.get("DOMAINNAME","");//域名
		var mainDomainAdd = obj.get("MAINDOMAINADD","");//主域名服务器地址
		var tradeName = obj.get("TRADENAME","");//专线名称
		var IPchange = obj.get("IPCHANGE","");//IP地址调整
		var routeMode = obj.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		
		var isCustomerpe = obj.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
		var customerDeviceMode = obj.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
		var customerDeviceType = obj.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
		var customerDeviceVendor = obj.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
		var phoneList = obj.get("PHONELIST","");//码号段表
		var amount = obj.get("AMOUNT","");//座机数量

	}
	
	if(templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		lineNo = obj.get("PRODUCTNO","");
		bandWidth = obj.get("BANDWIDTH","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"' onclick='setExportLineNos()'></input></div>";
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
			liHtml += "<span class='label'>IP地址调整：</span><span class='value'>"+IPchange+"</span>";
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
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<span class='label'>业务端口最大速率：</span><span class='value'>"+portARate+"</span>";
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
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
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
		if(templetId=="DIRECTLINECHANGESIMPLE"){
			liHtml += "<div class='fn' productNo='"+lineNo+"' ontap='modifyDataLineParam(&#39;OFFER_CHA_"+offerCode+"_"+maxIndex+"&#39;);'><span class='e_ico-edit'></span></div>";
		}
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="ECHANGERESOURCECONFIRM"){
		lineNo = obj.get("PRODUCTNO","");
		bandWidth = obj.get("BANDWIDTH","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"'></input></div>";
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
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
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
		}
		if(offerCode=="7016"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		liHtml += "</ul>";
		liHtml += "</div></div></div></div>";
		liHtml += "<div class='fn' productNo='"+lineNo+"' ontap='modifyChangeDataLineParam(this);'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="EDIRECTLINECANCEL"){
		var userId = obj.get("USER_ID","");
		var serialNumber = obj.get("SERIAL_NUMBER","");
		
		lineNo = obj.get("PRODUCTNO","");
		bandWidth = obj.get("BANDWIDTH","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"' onclick='setExportLineNos()'></input></div>";
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
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
		}
		liHtml += "</ul>";
		liHtml += "</div></div></div></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}
	
	$("#DATALINE_PARAM_UL").append(liHtml);
	if($("#DATALINE_PARAM_UL").children().length > 0)
	{
		$("#DATALINE_PARAM_UL").parent(".c_list").css("display", "");
	}
	$("#DATALINE_MAX_INDEX").val(maxIndex);
}

//简单变更-修改专线参数
function modifyChangeDataLineParam(obj)
{
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
    
    var chks = $("#DATALINE_PARAM_UL [type=checkbox]");
	{
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)// 获取选中的列表
			{
				var productNo = $(chks[j]).attr("productno");
				var chkProductNo = $(obj).attr("productno");
				if(productNo!=chkProductNo){
					MessageBox.alert("提示", "请对勾选的的信息进行修改！");
		            return false;
				}
			}
		}
	}
    
    //专线变更要先判断是否选择了业务调整场景
//    if(templetId=="EDIRECTLINECHANGE"||templetId=="DIRECTLINECHANGESIMPLE"){
//    	var mode = $('#pattr_CHANGEMODE').val();
//    	if(!mode){
//    		MessageBox.alert("提示", "请先选择业务调整场景！");
//    		return false;
//    	}
//    }

	var userId = $("#apply_USER_ID").val();
	var ibsysId = $("#IBSYSID").val();
	var subIbsysId = $("#SUB_IBSYSID").val();
	var reecordNum = $("#RECORD_NUM").val();
	
	//var productNo = $(obj).attr("productNo");
	var offerCode = $("#cond_OFFER_CODE").val();
	var offerId = $("#cond_OFFER_ID").val();
	var operType = $("#cond_OPER_TYPE").val();
	var brandCode = $("#cond_BRAND_CODE").val();
	var showParamPart = $("#SHOW_PARAM_PART").val();
	var serialNumber = $("#cond_SERIAL_NUMBER").val();
	var nodeTemplete = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var datalineNum = $("#pattr_DATALINE_NUM").val();
	var offerChaHiddenId = "OFFER_CHA_" + $("#cond_OFFER_CODE").val() + "_" + maxIndex ;
	$("#OFFER_CHA_HIDDEN_ID").val(offerChaHiddenId); 
	
	//专线变更需判断
//	if(templetId=="EDIRECTLINECHANGE"||templetId=="DIRECTLINECHANGESIMPLE"){
//		$('#pattr_HIDDEN_CHANGEMODE').val(changeMode);
//		changeMode = $('#pattr_CHANGEMODE').val();
//		$('#pattr_HIDDEN_PRODUCTNO').val(productNo);
//		productNo = $(obj).attr("productNo");
//		//业务调整场景改变时，换一条专线进行修改时，maxIndex恢复为0，数据需要重新从表里面查询
//		if($('#pattr_HIDDEN_CHANGEMODE').val()!=changeMode || maxIndex =="0" || $('#pattr_HIDDEN_PRODUCTNO').val()!=productNo){
//			maxIndex = "0";
//		}else{
//			datalineNum++;
//			$("#DATALINE_MAX_INDEX").val(datalineNum);
//			$("#pattr_DATALINE_NUM").val(datalineNum);
//		}
//	}else{
//		if(maxIndex =="0"){
//			maxIndex = "0";
//		}else{
//			maxIndex++;
//			$("#DATALINE_MAX_INDEX").val(maxIndex);
//		}
//	}
	
	param = "&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&NODELIST="+nodeTemplete+"&OFFER_CHA_HIDDEN_ID="+offerChaHiddenId+"&PRODUCT_NO="+productNo+"&USER_ID="+userId+"&IBSYSID="+ibsysId+"&RECORD_NUM="+reecordNum+"&SUB_IBSYSID="+subIbsysId+"&INDEX="+maxIndex;
	
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
		//专线变更需判断
		if(templetId=="EDIRECTLINECHANGE"||templetId=="DIRECTLINECHANGESIMPLE" ||templetId=="ECHANGERESOURCECONFIRM"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
			if(maxIndex == "0"){
				datalineNum++;
				$("#DATALINE_MAX_INDEX").val(datalineNum);
				$("#pattr_DATALINE_NUM").val(datalineNum);
			}
			chooseMode();
		}else{
			if(maxIndex = "0"){
				maxIndex++;
				$("#DATALINE_MAX_INDEX").val(maxIndex);
			}
		}
		showPopup("popup02", "offerChaPopupItem", true);
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
//专线变更修改专线详细数据时，根据选择业务调整场景，判断选择地址是否隐藏
function chooseMode(){
	var productId = $("#cond_OFFER_CODE").val();
	var changeMode = $("#pattr_CHANGEMODE").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	
	var chaSpecObjs = $("#offerChaPopupItem input");
	
	if(changeMode=="同楼搬迁"){
		for(var i = 0, size = chaSpecObjs.length; i < size; i++)
		{
			var chaSpecCode = chaSpecObjs[i].id;
			if(!chaSpecCode){
				continue;
			}
			if(chaSpecCode == "pattr_PROVINCEA" || chaSpecCode == "pattr_PROVINCEZ" ||chaSpecCode == "pattr_BANDWIDTH"||chaSpecCode =="pattr_TRADEID"||chaSpecCode =="pattr_PRODUCTNO"){
				continue;
			}
			if(chaSpecCode.indexOf("pattr_")==0){
				$("#"+chaSpecCode).removeAttr("disabled");
			}
			
		}
	}else if(changeMode=="减容"&&productId=="7010"){
		for(var i = 0, size = chaSpecObjs.length; i < size; i++)
		{
			var chaSpecCode = chaSpecObjs[i].id;
			if(!chaSpecCode){
				continue;
			}
			if(chaSpecCode.indexOf("pattr_")==0){
				$("#"+chaSpecCode).attr("disabled","true");
			}
			
		}
	}
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
			
			$("#pattr_ISCUSTOMERPE").removeAttr("disabled");
			$("#pattr_CUSTOMERDEVICEMODE").removeAttr("disabled");
			$("#pattr_CUSTOMERDEVICETYPE").removeAttr("disabled"); 
			$("#pattr_CUSTOMERDEVICEVENDOR").removeAttr("disabled"); 
			$("#pattr_SUPPORTMODE").removeAttr("disabled"); 
			$("#pattr_CUSTOMERDEVICELIST").removeAttr("disabled"); 
			$("#pattr_PHONELIST").removeAttr("disabled"); 
			$("#pattr_AMOUNT").removeAttr("disabled");
		}
		
	}else if(changeMode=="业务保障级别调整"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");
		if(productId=="7012"||productId=="70121"||productId=="70122"){
			$("#NOTIN_RSRV_STR16").removeAttr("disabled");//SLA服务费(元/月)
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
	if(changeMode=="减容"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");
		$("#NOTIN_RSRV_STR2").removeAttr("disabled");//月租费
		$("#NOTIN_RSRV_STR3").removeAttr("disabled");//一次性费用(安装调试费)(元)
		$("#pattr_BANDWIDTH").removeAttr("disabled");
	} 
	if(changeMode=="IP地址调整"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");
		if(productId=="7011"||productId=="70111"||productId=="70112"){
			$("#NOTIN_RSRV_STR10").removeAttr("disabled");//IP地址使用费（元/月)
			$("#pattr_IPCHANGE").attr("nullable", "no");
			$("#pattr_IPCHANGE").removeAttr("disabled");
			$("#pattr_IPCHANGE").css("display","");
			$("#pattr_IPCHANGE").parent().parent().parent().css("display","");
			$("#pattr_IPCHANGE").parent().parent().parent().attr("class","link required");
			$("#pattr_IPTYPE").removeAttr("disabled");//IP地址类型
			$("#pattr_CUSAPPSERVIPV6ADDNUM").removeAttr("disabled");//申请公网IPV6地址数
			$("#pattr_CUSAPPSERVIPADDNUM").removeAttr("disabled");//客户申请公网IP地址数
			$("#pattr_CUSAPPSERVIPV4ADDNUM").removeAttr("disabled");//申请公网IPV4地址数
			$("#pattr_DOMAINNAME").removeAttr("disabled");//域名
			$("#pattr_MAINDOMAINADD").removeAttr("disabled");//主域名服务器地址
		}
		if(productId=="7016"){
			$("#NOTIN_RSRV_STR10").removeAttr("disabled");//IP地址使用费（元/月)
			$("#pattr_IPCHANGE").attr("nullable", "no");
			$("#pattr_IPCHANGE").removeAttr("disabled");
			$("#pattr_IPCHANGE").css("display","");
			$("#pattr_IPCHANGE").parent().parent().parent().css("display","");
			$("#pattr_IPCHANGE").parent().parent().parent().attr("class","link required");
//			$("#pattr_IPTYPE").removeAttr("disabled");//IP地址类型
//			$("#pattr_CUSAPPSERVIPV6ADDNUM").removeAttr("disabled");//申请公网IPV6地址数
			$("#pattr_CUSAPPSERVIPADDNUM").removeAttr("disabled");//客户申请公网IP地址数
//			$("#pattr_CUSAPPSERVIPV4ADDNUM").removeAttr("disabled");//申请公网IPV4地址数
			$("#pattr_DOMAINNAME").removeAttr("disabled");//域名
			$("#pattr_MAINDOMAINADD").removeAttr("disabled");//主域名服务器地址
		}
	}else if(changeMode=="同楼搬迁"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");
		$("#NOTIN_RSRV_STR3").removeAttr("disabled");//一次性费用（安装调试费）（元） 
		if(productId=="7010"){
			$("#pattr_BANDWIDTH").attr("disabled","disabled");
			$("#pattr_PHONEPERMISSION").attr("disabled","disabled");//开通权限
			$("#pattr_PHONELIST").attr("disabled","disabled");//号码段表
		}else if(productId=="7011"||productId=="70111"||productId=="70112"){
			$("#pattr_BANDWIDTH").attr("disabled","disabled");//带宽
			$("#pattr_BIZSECURITYLV").attr("disabled","disabled");//业务保障级别
			$("#pattr_IPTYPE").attr("disabled","disabled");//IP地址类型
			$("#pattr_CUSAPPSERVIPV6ADDNUM").attr("disabled","disabled");//申请公网IPV6地址数
			$("#pattr_CUSAPPSERVIPADDNUM").attr("disabled","disabled");//客户申请公网IP地址数
			$("#pattr_CUSAPPSERVIPV4ADDNUM").attr("disabled","disabled");//申请公网IPV4地址数
		}else if(productId=="7012"||productId=="70121"||productId=="70122"){
			$("#pattr_BANDWIDTH").attr("disabled","disabled");//带宽
			$("#pattr_BIZSECURITYLV").attr("disabled","disabled");//业务保障级别
		}else if(productId=="7016"){
			$("#pattr_BANDWIDTH").attr("disabled","disabled");//带宽
			$("#pattr_BIZSECURITYLV").attr("disabled","disabled");//业务保障级别
//			$("#pattr_IPTYPE").attr("disabled","disabled");//IP地址类型
//			$("#pattr_CUSAPPSERVIPV6ADDNUM").attr("disabled","disabled");//申请公网IPV6地址数
			$("#pattr_CUSAPPSERVIPADDNUM").attr("disabled","disabled");//客户申请公网IP地址数
//			$("#pattr_CUSAPPSERVIPV4ADDNUM").attr("disabled","disabled");//申请公网IPV4地址数
		}
		
	}else if(changeMode=="停机"||changeMode=="复机"){
		$("#quickAddrA").css("display", "none");
		$("#quickAddrZ").css("display", "none");
	}
	return true;
}

function modifyContracLineParam(obj){
	debugger;
	var offerListNum = $("#DATALINE_PARAM_UL").length;
    if (offerListNum != 0) {
        if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length == 0) {
            MessageBox.alert("提示", "请勾选需要变更的信息进行修改！");
            return false;
        }else if ($("#DATALINE_PARAM_UL input[type=checkbox]:checked").length > 1) {
            MessageBox.alert("提示", "每次修改只能勾选一条！");
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
	var offerChaHiddenId = "OFFER_CHA_" + $("#cond_OFFER_CODE").val() + "_" + maxIndex;
	$("#OFFER_CHA_HIDDEN_ID").val(offerChaHiddenId); 
	var rowData = new Wade.DataMap(obj);
	var productNo = rowData.get("NOTIN_LINE_NO","");
	var brandcode = rowData.get("NOTIN_RSRV_STR1","");
	var notinPaybackPreiod = rowData.get("NOTIN_PAYBACK_PERIOD","");
	var notinCost = rowData.get("NOTIN_COST","");
	var notinTerm = rowData.get("NOTIN_TERM","");
	var notinHunderd = rowData.get("NOTIN_HUNDRED","");
	
	param = "&NOTIN_HUNDRED="+notinHunderd+"&NOTIN_TERM="+notinTerm+"&NOTIN_COST="+notinCost+"&NOTIN_PAYBACK_PERIOD="+notinPaybackPreiod+"&NOTIN_RSRV_STR1="+brandcode+"&PRODUCT_NO="+productNo+"&OFFER_CODE="+offerCode+"&OFFER_ID="+offerId+"&OPER_TYPE="+operType+"&BRAND_CODE="+brandCode+"&SHOW_PARAM_PART="+showParamPart+"&SERIAL_NUMBER="+serialNumber+"&GROUP_ID="+$("#cond_GROUP_ID").val()+"&NODELIST="+nodeTemplete+"&OFFER_CHA_HIDDEN_ID="+offerChaHiddenId;
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("", "initOfferCha", param, "offerChaPopupItem", function(data){
		$.endPageLoading();
		maxIndex++;
		showPopup("popup02", "offerChaPopupItem", true);
		$("#NOTIN_LINE_NO").val(rowData.get("NOTIN_LINE_NO"));
		$("#NOTIN_RSRV_STR1").val(rowData.get("NOTIN_RSRV_STR1"));
		$("#NOTIN_RSRV_STR2").val(rowData.get("NOTIN_RSRV_STR2"));
		$("#NOTIN_RSRV_STR3").val(rowData.get("NOTIN_RSRV_STR3"));
//		$("#pattr_NOTIN_RSRV_STR4").val(rowData.get("NOTIN_RSRV_STR4"));
		$("#NOTIN_RSRV_STR9").val(rowData.get("NOTIN_LINE_NO"));
		if(offerCode=='7011'||offerCode=='70111'||offerCode=='70112'){
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
		if(offerCode=='7012'||offerCode=='70121'||offerCode=='70122'){
//			$("#NOTIN_RSRV_STR6").val(rowData.get("NOTIN_RSRV_STR6"));
			$("#NOTIN_RSRV_STR7").val(rowData.get("NOTIN_RSRV_STR7"));
			$("#NOTIN_RSRV_STR8").val(rowData.get("NOTIN_RSRV_STR8"));
			$("#NOTIN_RSRV_STR11").val(rowData.get("NOTIN_RSRV_STR11"));
			$("#NOTIN_RSRV_STR12").val(rowData.get("NOTIN_RSRV_STR12"));
			$("#NOTIN_RSRV_STR16").val(rowData.get("NOTIN_RSRV_STR16"));
			$("#pattr_ROUTEMODE").val(rowData.get("ROUTEMODE"));
			$("#pattr_BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_PORTACUSTOM").val(rowData.get("PORTACUSTOM"));
			$("#pattr_PROVINCEA").val(rowData.get("PROVINCEA"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			$("#pattr_PORTZCUSTOM").val(rowData.get("PORTZCUSTOM"));
			$("#pattr_COUNTYA").val(rowData.get("COUNTYA"));
			$("#pattr_VILLAGEA").val(rowData.get("VILLAGEA"));
			$("#pattr_PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_PORTACONTACT").val(rowData.get("PORTACONTACT"));
			$("#pattr_PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
			$("#pattr_PORTZCUSTOM").val(rowData.get("PORTZCUSTOM"));
			$("#pattr_PROVINCEZ").val(rowData.get("PROVINCEZ"));
			$("#pattr_COUNTYZ").val(rowData.get("COUNTYZ"));
			$("#pattr_VILLAGEZ").val(rowData.get("VILLAGEZ"));
			$("#pattr_PORTZINTERFACETYPE").val(rowData.get("PORTZINTERFACETYPE"));
			$("#pattr_PORTZCONTACT").val(rowData.get("PORTZCONTACT"));
			$("#pattr_PORTZCONTACTPHONE").val(rowData.get("PORTZCONTACTPHONE"));
			$("#pattr_TRADENAME").val(rowData.get("TRADENAME"));
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}

//回写参数列表
function sureHisDataLineParamList(rowData)
{
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	var maxIndex = $("#DATALINE_MAX_INDEX").val();
	var check_tag = $("#isCheckTag").val();
	
	var templetId = $("#cond_TEMPLET_ID").val();
	
	var price = rowData.get("NOTIN_RSRV_STR2","");
	var installationCost = rowData.get("NOTIN_RSRV_STR3","");// 一次性费用（安装调试费）（元）
	var userId = rowData.get("USER_ID");
	var serialNumber = rowData.get("SERIAL_NUMBER","");
	
	var lineNo = rowData.get("NOTIN_LINE_NO","");
	var bandWidth = rowData.get("NOTIN_RSRV_STR1","");
	
	var isPreOccupy = rowData.get("ISPREOCCUPY","");//资源是否预占
	if(isPreOccupy=="0"){
		isPreOccupy = "否";
	}else if(isPreOccupy=="1"){
		isPreOccupy = "是";
	}
	var preReason = rowData.get("PREREASON","");//资源预占原因
	
	if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
		var ipPrice = rowData.get("NOTIN_RSRV_STR10","");
		var softwarePrice = rowData.get("NOTIN_RSRV_STR11","");
		var netPrice = rowData.get("NOTIN_RSRV_STR12","");
		
		var transferMode = rowData.get("TRANSFERMODE","");//传输接入方式
		var bizsecurityLv = rowData.get("BIZSECURITYLV","");//业务保障等级
		var portACustom = rowData.get("PORTACUSTOM","");//用户名称
		var provinceA = rowData.get("PROVINCEA","");//所属省份
		var cityA = rowData.get("CITYA","");//所属地市
		var areaA = rowData.get("AREAA","");//所属区县
		var countyA = rowData.get("COUNTYA","");//街道/乡镇
		var villageA = rowData.get("VILLAGEA","");//门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE","");//业务端口类型
		var portAContact = rowData.get("PORTACONTACT","");//用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE","");//用户技术联系人电话
		var IPType = rowData.get("IPTYPE","");//IP地址类型
		var cusAppServiIPAddNum = rowData.get("CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
		var cusAppServiIPV4AddNum = rowData.get("CUSAPPSERVIPV4ADDNUM","");//申请公网IPV4地址数
		var cusAppServiIPV6AddNum = rowData.get("CUSAPPSERVIPV6ADDNUM","");//申请公网IPV6地址数
		var domainName = rowData.get("DOMAINNAME","");//域名
		var mainDomainAdd = rowData.get("MAINDOMAINADD","");//主域名服务器地址
		var tradeName = rowData.get("TRADENAME","");//专线名称
		var IPchange = rowData.get("IPCHANGE","");//IP地址调整
		
		var routeMode = rowData.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
	}else if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
		var groupPercent = rowData.get("NOTIN_RSRV_STR6","");
		var softwarePrice = rowData.get("NOTIN_RSRV_STR11","");
		var netPrice = rowData.get("NOTIN_RSRV_STR12","");
		var aPercent = rowData.get("NOTIN_RSRV_STR7","");
		var zPercent = rowData.get("NOTIN_RSRV_STR8","");
		var slaServiceCost = rowData.get("NOTIN_RSRV_STR16","");// SLA服务费（元/月）
		
		var transferMode = rowData.get("TRANSFERMODE","");//传输接入方式
		var routeMode = rowData.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		var bizsecurityLv = rowData.get("BIZSECURITYLV","");//业务保障等级
		var portACustom = rowData.get("PORTACUSTOM","");//A端用户名称
		var provinceA = rowData.get("PROVINCEA","");//A端所属省份
		var cityA = rowData.get("CITYA","");//A端所属地市
		var areaA = rowData.get("AREAA","");//A端所属区县
		var countyA = rowData.get("COUNTYA","");//A端街道/乡镇
		var villageA = rowData.get("VILLAGEA","");//A端门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE","");//A端口类型
		var portAContact = rowData.get("PORTACONTACT","");//A端用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE","");//A端用户技术联系人电话
		var portZCustom = rowData.get("PORTZCUSTOM","");//Z端用户名称
		var provinceZ = rowData.get("PROVINCEZ","");//Z端所属省份
		var cityZ = rowData.get("CITYZ","");//Z端所属地市
		var areaZ = rowData.get("AREAZ","");//Z端所属区县
		var countyZ = rowData.get("COUNTYZ","");//Z端街道/乡镇
		var villageZ = rowData.get("VILLAGEZ","");//Z端门牌/村组
		var portZInteracetype = rowData.get("PORTZINTERFACETYPE","");//Z端口类型
		var portZContact = rowData.get("PORTZCONTACT","");//Z端用户技术联系人
		var portZContactPhone = rowData.get("PORTZCONTACTPHONE","");//Z端用户技术联系人电话
		var tradeName = rowData.get("TRADENAME","");//专线名称
	}else if(offerCode=="7010"){
		var voiceCommunicateCost = rowData.get("NOTIN_RSRV_STR15","");// 语音通信费（元/分钟）
		
		var transferMode = rowData.get("TRANSFERMODE","");//传输接入方式
		var supportMode = rowData.get("SUPPORTMODE","");//语音接入类型
		var bizsecurityLv = rowData.get("BIZSECURITYLV","");//业务保障等级
		var repeaterNum = rowData.get("REPEATERNUM","");//中继数
		var amount = rowData.get("AMOUNT","");//座机数量
		var portACustom = rowData.get("PORTACUSTOM","");//用户名称
		var provinceA = rowData.get("PROVINCEA","");//所属省份
		var cityA = rowData.get("CITYA","");//所属地市
		var areaA = rowData.get("AREAA","");//所属区县
		var countyA = rowData.get("COUNTYA","");//街道/乡镇
		var villageA = rowData.get("VILLAGEA","");//门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE","");//业务端口类型
		var portAContact = rowData.get("PORTACONTACT","");//用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE","");//用户技术联系人电话
		var isCustomerpe = rowData.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
		var customerDeviceMode = rowData.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
		var customerDeviceType = rowData.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
		var customerDeviceVendor = rowData.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
		var phonePermission = rowData.get("PHONEPERMISSION","");//开通权限
		var phoneList = rowData.get("PHONELIST","");//码号段表
		var conproductNo = rowData.get("CONPRODUCTNO","");//关联的产品实例编号
		var tradeName =  rowData.get("TRADENAME","");//专线名称
		
		var routeMode =  rowData.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
	}else if(offerCode=="7016"){
		var ipPrice = rowData.get("NOTIN_RSRV_STR10","");
		var softwarePrice = rowData.get("NOTIN_RSRV_STR11","");
		var netPrice = rowData.get("NOTIN_RSRV_STR12","");
		
		var transferMode = rowData.get("TRANSFERMODE","");//传输接入方式
		var bizsecurityLv = rowData.get("BIZSECURITYLV","");//业务保障等级
		var portACustom = rowData.get("PORTACUSTOM","");//用户名称
		var provinceA = rowData.get("PROVINCEA","");//所属省份
		var cityA = rowData.get("CITYA","");//所属地市
		var areaA = rowData.get("AREAA","");//所属区县
		var countyA = rowData.get("COUNTYA","");//街道/乡镇
		var villageA = rowData.get("VILLAGEA","");//门牌/村组
		var portAInteracetype = rowData.get("PORTAINTERFACETYPE","");//业务端口类型
		var portAContact = rowData.get("PORTACONTACT","");//用户技术联系人
		var portAContactPhone = rowData.get("PORTACONTACTPHONE","");//用户技术联系人电话
		var cusAppServiIPAddNum = rowData.get("CUSAPPSERVIPADDNUM","");//客户申请公网IP地址数
		var domainName = rowData.get("DOMAINNAME","");//域名
		var mainDomainAdd = rowData.get("MAINDOMAINADD","");//主域名服务器地址
		var tradeName = rowData.get("TRADENAME","");//专线名称
		
		var routeMode = rowData.get("ROUTEMODE","");//路由保护方式
		if(routeMode=="0"){
			routeMode = "单节点单路由";
		}else if(routeMode=="1"){
			routeMode = "单节点双路由";
		}else if(routeMode=="2"){
			routeMode = "双节点双路由";
		}
		
		var portaRate = rowData.get("PORTARATE","");//业务端口速率
		var isCustomerpe = rowData.get("ISCUSTOMERPE","");//客户侧是否自备业务设备
		var customerDeviceMode = rowData.get("CUSTOMERDEVICEMODE","");//客户侧设备类型
		var customerDeviceType = rowData.get("CUSTOMERDEVICETYPE","");//客户侧设备型号
		var customerDeviceVendor = rowData.get("CUSTOMERDEVICEVENDOR","");//客户侧设备厂家
		var phoneList = rowData.get("PHONELIST","");//码号段表
		var supportMode = rowData.get("SUPPORTMODE","");//语音接入类型
		var amount = rowData.get("AMOUNT","");//座机数量

	}
	
	if(templetId=="DIRECTLINECHANGESIMPLE"||templetId=="MANUALSTOP"||templetId=="MANUALBACK"){
		lineNo = rowData.get("PRODUCTNO","");
		bandWidth = rowData.get("BANDWIDTH","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"' onclick='setExportLineNos()'></input></div>";
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
			liHtml += "<span class='label'>IP地址调整：</span><span class='value'>"+IPchange+"</span>";
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
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portaRate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
			liHtml += "</li>";
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
			liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
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
			
			liHtml += "<li>";
			liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portaRate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
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
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
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
		if(templetId=="DIRECTLINECHANGESIMPLE"){
			liHtml += "<div class='fn' productNo='"+lineNo+"' ontap='modifyChangeDataLineParam(this);'><span class='e_ico-edit'></span></div>";
		}
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="ECHANGERESOURCECONFIRM"||templetId=="ERESOURCECONFIRMZHZG"){
	
		var bandWidth = rowData.get("BANDWIDTH","");
		var lineNo = rowData.get("PRODUCTNO","");
		
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"'productNo='"+lineNo+"'userId='"+userId+"'serialNumber='"+serialNumber+"'></input></div>";
		liHtml += "<div class='main'>";
		liHtml += "<div class='title'>"+lineNo+"</div>";
		liHtml += "<div class='content e_hide-phone'>";
		liHtml += "<div name='scroller1' jwcid='@Scroller' class='c_scroll' style='height:3em'>";
		liHtml += "<div class='c_param c_param-label-auto c_param-col-3'>";
		liHtml += "<ul>";
		if(offerCode=="7010"){
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
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
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>IP带宽(兆)：</span><span class='value'>"+bandWidth+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务带宽(兆)：</span><span class='value'>"+bandWidth+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		if(offerCode=="7012"||offerCode=="70121"||offerCode=="70122"){
			liHtml += "<li>";
			liHtml += "<span class='label'>业务带宽(兆)：</span><span class='value'>"+bandWidth+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传入接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>路由保护方式：</span><span class='value'>"+routeMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源是否预占：</span><span class='value'>"+isPreOccupy+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>资源预占原因：</span><span class='value'>"+preReason+"</span>";
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
		}
		if(offerCode=="7016"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			/*liHtml += "<li>";
			liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
			liHtml += "</li>";*/
			/*liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";*/
			
			/*liHtml += "<li>";
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
			liHtml += "</li>";*/
			
			liHtml += "<li>";
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
			liHtml += "</li>";
			/*liHtml += "<li>";
			liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
			liHtml += "</li>";*/
			liHtml += "<li>";
			liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portaRate+"</span>";
			liHtml += "</li>";
//			liHtml += "<li>";
//			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
//			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";

		}
		liHtml += "</ul>";
		liHtml += "</div></div></div></div>";
		liHtml += "<div class='fn' ontap='modifyHisDataLineParam("+rowData+");'><span class='e_ico-edit'></span></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}else if(templetId=="EDIRECTLINECANCEL"){
		var userId = rowData.get("USER_ID","");
		var serialNumber = rowData.get("SERIAL_NUMBER","");
		
		lineNo = rowData.get("PRODUCTNO","");
		bandWidth = rowData.get("BANDWIDTH","");
		var liHtml = "<li id='DATALINE_"+maxIndex+"_LI'>";
		liHtml += "<div class='fn'><input type='checkbox' checked='checked' productNo='"+lineNo+"' name='DATALINE_TAG' id='DATALINE_"+maxIndex+"' userId='"+userId+"' serialNumber='"+serialNumber+"' onclick='setExportLineNos()'></input></div>";
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
		}
		if(offerCode=="7011"||offerCode=="70111"||offerCode=="70112"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
		}
		if(offerCode=="7016"){
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<li>";
			liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>传输接入方式：</span><span class='value'>"+transferMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>业务保障等级：</span><span class='value'>"+bizsecurityLv+"</span>";
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
			liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portaRate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人：</span><span class='value'>"+portAContact+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>用户技术联系人电话：</span><span class='value'>"+portAContactPhone+"</span>";
			liHtml += "</li>";
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
			liHtml += "<span class='label'>客户申请公网IP地址数：</span><span class='value'>"+cusAppServiIPAddNum+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>域名：</span><span class='value'>"+domainName+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>主域名服务器地址：</span><span class='value'>"+mainDomainAdd+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
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
			
			liHtml += "<li>";
			liHtml += "<span class='label'>业务端口速率：</span><span class='value'>"+portaRate+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>语音接入类型：</span><span class='value'>"+supportMode+"</span>";
			liHtml += "</li>";
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
			liHtml += "<span class='label'>座机数量：</span><span class='value'>"+amount+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>码号段表：</span><span class='value'>"+phoneList+"</span>";
			liHtml += "</li>";
			liHtml += "<li>";
			liHtml += "<span class='label'>专线名称：</span><span class='value'>"+tradeName+"</span>";
			liHtml += "</li>";
		}
		liHtml += "</ul>";
		liHtml += "</div></div></div></div>";
		liHtml += "<input type='hidden' id='OFFER_CHA_"+offerCode+"_"+maxIndex+"' value='' />";
		liHtml += "</li>";
	}
	
	$("#DATALINE_PARAM_UL").append(liHtml);
	if($("#DATALINE_PARAM_UL").children().length > 0)
	{
		$("#DATALINE_PARAM_UL").parent(".c_list").css("display", "");
	}
	
	$("#check_tag").val(check_tag);
}

function modifyHisDataLineParam(obj){
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
    if(templetId=="ECHANGERESOURCECONFIRM"){
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
		
		chooseMode();
		
		showPopup("popup02", "offerChaPopupItem", true);
		$("#pattr_PRODUCTNO").val(rowData.get("PRODUCTNO"));
		$("#pattr_USER_ID").val(rowData.get("USER_ID"));
		$("#pattr_SERIAL_NUMBER").val(rowData.get("SERIAL_NUMBER"));
		$("#pattr_TRADEID").val(rowData.get("TRADEID"));
		$("#pattr_BANDWIDTH").val(rowData.get("BANDWIDTH"));
		if(offerCode=='7011'||offerCode=='70111'||offerCode=='70112'){
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
		if(offerCode=='7012'||offerCode=='70121'||offerCode=='70121'){
			$("#pattr_ROUTEMODE").val(rowData.get("ROUTEMODE"));
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
//			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
			$("#pattr_TRADENAME").val(rowData.get("TRADENAME"));
		}
		if(offerCode=='7016'){
			$("#pattr_TRANSFERMODE").val(rowData.get("TRANSFERMODE"));
			$("#pattr_BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
			$("#pattr_PORTARATE").val(rowData.get("PORTARATE"));
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
			$("#pattr_AMOUNT").val(rowData.get("AMOUNT"));

		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
}