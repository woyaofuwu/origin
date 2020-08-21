//绑定上传完成后的回调事件
$(function(){
	$("#simpleupload").afterAction(function(e, file){
		if(containSpecial(file.name)){
			MessageBox.alert("错误", "【"+file.name+"】文件名称包含特殊字符，请修改后再上传！");
			$("##simpleupload").val("");
			return false; 
		}
		
		var data = new Wade.DataMap();
		data.put("FILE_ID", file.fileId);
		data.put("FILE_NAME", file.name);
		data.put("ATTACH_TYPE", "P");
		$("#uploadData").text(data.toString());
		//alert(file.name + "|" + file.fileId); // 从 file 对象中获取文件名和上传后的文件 id
	});
});

function containSpecial(str){
	var containSpecial = RegExp("[%?？]");//过滤%,？,?特殊字符
	return (containSpecial.test(str));
}

function qryInfos(){
	var param = "";
	if($("#cond_GROUPID").val()){
		param += "&GROUP_ID="+$("#cond_GROUPID").val();
	}
	if($("#cond_SERIAL_NUMBER").val()){
		param += "&SERIAL_NUMBER="+$("#cond_SERIAL_NUMBER").val();
	}
	if($("#cond_PRODUCTNO").val()){
		param += "&PRODUCT_NO="+$("#cond_PRODUCTNO").val();
	}
	if(!param){
		alert("请至少输入一个查询条件！");
		return false;
	}
	var isRed = $("#cond_IS_RED_LIST").val();
	if(!isRed){
		alert("请选择是否已添加红单！");
		return false;
	}
	param += "&IS_RED="+isRed;
	$.beginPageLoading("数据查询中，请稍后...");
	$.ajax.submit(null,'qryLineInfos',param,'GroupInfo,LineInfo,Submit',function(data){
		if(isRed == '1'){
			$("#OrderPart").attr("style","display:none");
			$("#TABLE_TH").attr("style","display:none");
			$("td[name=TABLE_TD]").each(function(){
				$(this).attr("style","display:none");
			});
			$("#Submit").attr("style","display:none");
		}else{
			$("#OrderPart").attr("style","");
			$("#TABLE_TH").attr("style","");
			$("td[name=TABLE_TD]").each(function(){
				$(this).attr("style","");
			});
			$("#Submit").attr("style","");
		}
		
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function submit(){
	var isRed = $("#IS_RED_LIST").val();
	if(isRed == "1"){
		MessageBox.alert("错误", "请查询非红名单专线，并选择后在提交！");
		return false;
	}
	
	if(!$("#pattr_TITLE").val()){
		MessageBox.alert("错误", "请填写工单标题!");
		return false;
	}
	
	if(!$("#pattr_URGENCY_LEVEL").val()){
		MessageBox.alert("错误", "请选择工单紧急程度!");
		return false;
	}
	
	if(!$("#pattr_START_DATE").val()){
		MessageBox.alert("错误", "请选择红名单开始时间!");
		return false;
	}
	
	if(!$("#pattr_END_DATE").val()){
		MessageBox.alert("错误", "请选择红名单结束时间!");
		return false;
	}
	
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"请您先选择需要处理的专线!");
		return false;
	}
	
	var serialNumbers = "";
	var productId = "";
	for(var i=0;i<rowDatas.length;i++){
		var rowData = rowDatas.get(i);
		var serialNumber = rowData.get("SERIAL_NUMBER");
		//var addTag = rowData.get("ADD_TAG");
		var productId = rowData.get("PRODUCT_ID");
		//serialNumbers += serialNumber+"+"+addTag+";";
		serialNumbers += serialNumber+";";
	}
	//产品编码转换
	if(productId == '97011'){
		productId='7011';
	}else if(productId == '97012'){
		productId='7012';
	}else if(productId == '97016'){
		productId='7016';
	}else if(productId == '970111'){
		productId='70111';
	}else if(productId == '970112'){
		productId='70112';
	}else if(productId == '970121'){
		productId='70121';
	}else if(productId == '970122'){
		productId='70122';
	}
	
	var attachList = new Wade.DatasetList();
	if($("#uploadData").text()){
		var attachData = new Wade.DataMap($("#uploadData").text());
		attachList.add(attachData);
	}else{
		$.validate.alerter.one($("simpleupload")[0], "请上传审批附件！");
		return false;
	}
	
	//订单级信息
	var orderData = new Wade.DataMap();
	orderData.put("TITLE",$("#pattr_TITLE").val());
	orderData.put("URGENCY_LEVEL",$("#pattr_URGENCY_LEVEL").val());
	
	var submitParam = new Wade.DataMap();
	submitParam.put("COMMON_DATA", saveEosCommonData(productId));
	submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData(productId));
	submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());
	submitParam.put("CUST_DATA", savaCustData());
	submitParam.put("EOMS_ATTR_LIST", builderEomsAttrData());
	submitParam.put("ORDER_DATA", orderData);
	submitParam.put("ATTACH_LIST", attachList);
	submitParam.put("SERIAL_NUMBERS",serialNumbers);
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("流程创建成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					debugger;
					var urlArr = data.get("ASSIGN_URL").split("?");
					var pageName = getNavTitle();
					openNav('指派', urlArr[1].substring(13), '', '&BEFORE_NAV_TITLE='+getNavTitle(), urlArr[0]);
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "指派"});
		}else if(data.get("ALERT_FLAG")== "true"){
			MessageBox.success("流程创建成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ext1" == btn){
					var urlArr = data.get("ALERT_URL").split("?");
					var ALERT_NAME = data.get("ALERT_NAME");
					var pageName = getNavTitle();
					openNav(ALERT_NAME, urlArr[1].substring(13), '', '&BEFORE_NAV_TITLE='+getNavTitle(), urlArr[0]);
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "下一步"});
		}
		else
		{
			MessageBox.success("流程创建成功", "定单号："+data.get("IBSYSID"), function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}
		
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function saveEosCommonData(productId)
{
	var eosCommonData = new Wade.DataMap();
	eosCommonData.put("CUST_NAME", $("#CUST_NAME").val());
	eosCommonData.put("PRODUCT_ID", productId);
	eosCommonData.put("BUSIFORM_OPER_TYPE","61");
	return eosCommonData;
}

function savaCustData(){
	var custData = new Wade.DataMap();
	custData.put("GROUP_ID",$("#GROUP_ID").val());
	custData.put("CUST_NAME", $("#CUST_NAME").val());
	
	return custData;
}

function saveBusiSpecReleData(productId){
	var busiSpecReleData = new Wade.DataMap();
	busiSpecReleData.put("BPM_TEMPLET_ID","ADDCREDITREDLIST");
	busiSpecReleData.put("BUSI_TYPE","P");
	busiSpecReleData.put("BUSI_CODE",productId);
	busiSpecReleData.put("IN_MODE_CODE","0");
	return busiSpecReleData;
}

function saveNodeTempleteData(){
	var nodeTempleteData = new Wade.DataMap();
	nodeTempleteData.put("NODE_ID","apply");
	return nodeTempleteData;
}

function builderEomsAttrData(){
	var eomsAttrDataset = new Wade.DatasetList();
	var eomsAttrTitleData = new Wade.DataMap();
	eomsAttrTitleData.put("ATTR_CODE","TITLE");
	eomsAttrTitleData.put("ATTR_NAME",$("#pattr_TITLE").attr("desc"));
	eomsAttrTitleData.put("ATTR_VALUE",$("#pattr_TITLE").val());
	eomsAttrDataset.add(eomsAttrTitleData);
	
	var eomsAttrUrLevData = new Wade.DataMap();
	eomsAttrUrLevData.put("ATTR_CODE","URGENCY_LEVEL");
	eomsAttrUrLevData.put("ATTR_NAME",$("#pattr_URGENCY_LEVEL").attr("desc"));
	eomsAttrUrLevData.put("ATTR_VALUE",$("#pattr_URGENCY_LEVEL").val());
	eomsAttrDataset.add(eomsAttrUrLevData);
	
	var eomsAttrStartData = new Wade.DataMap();
	eomsAttrStartData.put("ATTR_CODE","START_DATE");
	eomsAttrStartData.put("ATTR_NAME",$("#pattr_START_DATE").attr("desc"));
	eomsAttrStartData.put("ATTR_VALUE",$("#pattr_START_DATE").val());
	eomsAttrDataset.add(eomsAttrStartData);
	
	var eomsAttrEndData = new Wade.DataMap();
	eomsAttrEndData.put("ATTR_CODE","END_DATE");
	eomsAttrEndData.put("ATTR_NAME",$("#pattr_END_DATE").attr("desc"));
	eomsAttrEndData.put("ATTR_VALUE",$("#pattr_END_DATE").val());
	eomsAttrDataset.add(eomsAttrEndData);
	
	var eomsAttrGroupIdData = new Wade.DataMap();
	eomsAttrGroupIdData.put("ATTR_CODE","GROUP_ID");
	eomsAttrGroupIdData.put("ATTR_NAME","集团编码");
	eomsAttrGroupIdData.put("ATTR_VALUE",$("#GROUP_ID").val());
	eomsAttrDataset.add(eomsAttrGroupIdData);
	
	var eomsAttrCustNameData = new Wade.DataMap();
	eomsAttrCustNameData.put("ATTR_CODE","CUST_NAME");
	eomsAttrCustNameData.put("ATTR_NAME","客户名称");
	eomsAttrCustNameData.put("ATTR_VALUE",$("#CUST_NAME").val());
	eomsAttrDataset.add(eomsAttrCustNameData);
	
	return eomsAttrDataset;
}