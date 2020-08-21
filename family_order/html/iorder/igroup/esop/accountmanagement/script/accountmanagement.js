function creatAct(){
	var custId = $("#CUST_ID").val();
	if(custId == null || custId == ''||custId == undefined){
		MessageBox.alert("提示","请先查询客户信息！"); 
		return false;
	}
	openNav("集团账户管理", "igroup.creategroupacct", "queryAcctInfoList", "&CUST_ID="+custId, "/order/iorder");
}


function qryLineInfos(){
	var groupId = $("#cond_GROUP_ID").val();
	if(groupId == null || groupId == ''||groupId == undefined){
		MessageBox.alert("提示","请输入集团编码后再查询！"); 
		return false;
	}
	var acctOperType = $("#ACCT_OPERTYPE").val();
	if(acctOperType == null || acctOperType == ''||acctOperType == undefined){
		MessageBox.alert("提示","请选择操作类型！"); 
		return false;
	}
	
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit(null,'qryLineInfos','&GROUP_ID='+groupId+'&ACCT_OPERTYPE='+acctOperType,'GroupInfo,LineInfo',function(data){
		
		$.endPageLoading();
		refreshAcctList();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function checkCondition(obj){
	var objVal = $(obj).val();
	if(objVal != undefined && objVal!=null && objVal != ''){
		if(!$.isNumeric(objVal)){
			MessageBox.alert("提示",$(obj).attr("desc")+"不合法，请重新填写！");
			$(obj).val("");
			return false;
		}
	}
}

function changeOperType(){
	var operType = $("#ACCT_OPERTYPE").val();
	if(operType=="2"){
		$("#ACCT_LIST_PART").css("display","none");
		$("#REFRESH_PART").css("display","none");
	}else if(operType=="1"){
		$("#ACCT_LIST_PART").css("display","");
		$("#REFRESH_PART").css("display","");
	}
	if($("#cond_GROUP_ID").val()){
		qryLineInfos();
	}
}

function refreshAcctList(){
	var custId = $("#CUST_ID").val();
	
	$.beginPageLoading('正在查询账户信息...');
	$.ajax.submit(null,'qryAcctList','&CUST_ID='+custId,'AcctList',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

//提交方法
function submit(){
	if(!$("#uploadDate").text()){
		$.validate.alerter.one($("#simpleupload")[0], "请上传审批附件！");
		//alert("请先上传附件！");
		return false;
	}
	if(!$("#pattr_TITLE").val()){
		$.validate.alerter.one($("#pattr_TITLE")[0], "请填写工单主题！");
		return false;
	}
	if(!$("#pattr_URGENCY_LEVEL").val()){
		$.validate.alerter.one($("#pattr_URGENCY_LEVEL")[0], "请选择工单紧急程度！");
		return false;
	}
	
	var operType = $("#ACCT_OPERTYPE").val();
	
	var acctId = $("#ACCT_ID").val();
	
	var eomsAttrList = new Wade.DatasetList();
	if(operType == '1'){
		if(!acctId){
			$.validate.alerter.one($("#ACCT_ID")[0], "请您先选择账户！");
			return false;
		}
		eomsAttrList = savaAcctInfo(eomsAttrList);
	}
	eomsAttrList = savaAcctOperInfo(eomsAttrList);
	
	var rowDatas = myTable.getCheckedRowsData("TRADES");
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"请您先选择需要处理的专线!");
		return false;
	}
	
	for(var i=0;i<rowDatas.length;i++){
		var rowData = rowDatas.get(i);
		var attrData = new Wade.DataMap();
		var serialNumber = rowData.get("SERIAL_NUMBER");
		if(operType == '1'&& rowData.get("ACCT_ID")==acctId){
			MessageBox.alert("提示","专线用户"+serialNumber+"账户即为"+acctId+",无法进行拆分！");
			return false;
		}
		attrData.put("ATTR_CODE","SERIAL_NUMBER");
		attrData.put("ATTR_NAME","处理用户");
		attrData.put("ATTR_VALUE",serialNumber);
		attrData.put("PRODUCT_ID",rowData.get("PRODUCT_ID"));
		attrData.put("ACCT_ID",rowData.get("ACCT_ID"));
		eomsAttrList.add(attrData);
	}
	
	var orderDataTitle = new Wade.DataMap();
	orderDataTitle.put("ATTR_CODE","TITLE");
	orderDataTitle.put("ATTR_NAME","工单主题");
	orderDataTitle.put("ATTR_VALUE",$("#pattr_TITLE").val());
	eomsAttrList.add(orderDataTitle);
	
	var orderDataUrgencyLevel = new Wade.DataMap();
	orderDataUrgencyLevel.put("ATTR_CODE","URGENCY_LEVEL");
	orderDataUrgencyLevel.put("ATTR_NAME","工单紧急程度");
	orderDataUrgencyLevel.put("ATTR_VALUE",$("#pattr_URGENCY_LEVEL").val());
	eomsAttrList.add(orderDataUrgencyLevel);
	
	var attachList = savaAttachList();
	var custData = saveCustData();
	var orderData = saveOrderData();
	
	var submitParam = new Wade.DataMap();
	submitParam.put("EOMS_ATTR_LIST",eomsAttrList);
	submitParam.put("CUST_DATA",custData);
	submitParam.put("ORDER_DATA",orderData);
	submitParam.put("ATTACH_LIST",attachList);
	submitParam.put("OPERTYPE",operType);
	submitParam.put("ACCT_ID",acctId);
	
	$.beginPageLoading('数据校验中...');
	$.ajax.submit(null,'checkAcctInfo',"&SUBMIT_PARAM="+submitParam.toString(),'',function(datas){
		$.endPageLoading();
		//if(data.get("CHECK_TAG")=="true"){
		var message = "";

		for(var i=0;i<datas.length;i++){
			var data = datas.get(i);
			message += data.get("RESULT");
		}
		if(!message){
			message = "校验通过！";
		}
			MessageBox.confirm("提示信息",message, function(btn){
				if(btn=="ok"){
					$.beginPageLoading("数据提交中，请稍后...");
					$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
						$.endPageLoading();
						debugger;
						if(data.get("ASSIGN_FLAG") == "true")
						{
							MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
								if("ext1" == btn){
									debugger;
									var urlArr = data.get("ASSIGN_URL").split("?"); 
									var pageName = getNavTitle(); 
									openNav('指派', urlArr[1].substring(13), '', '', urlArr[0]); 
									closeNavByTitle(pageName);
								}
								if("ok" == btn){
									closeNav();
								}
							}, {"ext1" : "指派"});
						}else if(data.get("ALERT_FLAG")== "true"){
							MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
								if("ext1" == btn){
									var urlArr = data.get("ALERT_URL").split("?");
									var ALERT_NAME = data.get("ALERT_NAME");
									var pageName = getNavTitle();
									openNav(ALERT_NAME, urlArr[1].substring(13), '', '', urlArr[0]); 
									closeNavByTitle(pageName);
								}
								if("ok" == btn){
									closeNav();
								}
							}, {"ext1" : "下一步"});
						}
						else
						{
							MessageBox.success("提交成功", "定单号："+data.get("IBSYSID"), function(btn){
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
			});
		//}
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}

function savaAcctInfo(eomsAttrList){
	var acctId = $("#ACCT_ID").val();
	var acctData = new Wade.DataMap();
	acctData.put("ATTR_CODE","ACCT_ID");
	acctData.put("ATTR_NAME","账户ID");
	acctData.put("ATTR_VALUE",acctId);
	eomsAttrList.add(acctData);
	return eomsAttrList;
}

function savaAcctOperInfo(eomsAttrList){
	var operType = $("#ACCT_OPERTYPE").val();
	var acctOperData = new Wade.DataMap();
	acctOperData.put("ATTR_CODE","OPERTYPE");
	acctOperData.put("ATTR_NAME","操作类型");
	acctOperData.put("ATTR_VALUE",operType);
	eomsAttrList.add(acctOperData);
	return eomsAttrList;
}

function saveCustData(){
	var custData = new Wade.DataMap();
	custData.put("GROUP_ID",$("#GROUP_ID").text());
	custData.put("CUST_NAME", $("#CUST_NAME").text());
	return custData;
}

function savaAttachList(){
	var attachList = new Wade.DatasetList();
	var attachData = new Wade.DataMap($("#uploadDate").text());
	attachList.add(attachData);
	return attachList;
}

function saveOrderData(){
	var orderData = new Wade.DataMap();
	orderData.put("TITLE",$("#pattr_TITLE").val());
	orderData.put("URGENCY_LEVEL",$("#pattr_URGENCY_LEVEL").val());
	return orderData;
}

//加载指定fileId的文件信息,fileId为文件上传后在数据库表里的唯一标识
//simpleupload.loadFile('10006438');

//绑定上传完成后的回调事件
$(function(){
	$("#simpleupload").afterAction(function(e, file){
		var data = new Wade.DataMap();
		data.put("FILE_ID", file.fileId);
		data.put("FILE_NAME", file.name);
		data.put("ATTACH_TYPE", "P");
		$("#uploadDate").text(data.toString());
		//alert(file.name + "|" + file.fileId); // 从 file 对象中获取文件名和上传后的文件 id
	});
});
