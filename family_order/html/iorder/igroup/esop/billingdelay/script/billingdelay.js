//绑定上传完成后的回调事件
$(function(){
	$("#simpleupload").afterAction(function(e, file){
		if(containSpecial(file.name)){
			MessageBox.alert("错误", "【"+file.name+"】文件名称包含特殊字符，请修改后再上传！");
			return false; 
		}
		
		var data = new Wade.DataMap();
		data.put("FILE_ID", file.fileId);
		data.put("FILE_NAME", file.name);
		data.put("ATTACH_TYPE", "P");
		$("#uploadDate").text(data.toString());
		//alert(file.name + "|" + file.fileId); // 从 file 对象中获取文件名和上传后的文件 id
	});
});

function containSpecial(str){
	var containSpecial = RegExp("[%?？]");//过滤%,？,?特殊字符
	return (containSpecial.test(str));
}

function qryInfos(){
	var ibsysid = $('#cond_IBSYSID').val();
	if(!ibsysid)
	{
		alert("请输入开通单流水号！");
		return false;
	}
	
	$.beginPageLoading('正在查询订单信息...');
	$.ajax.submit(null,'queryInfosByIbsysid','&IBSYSID='+ibsysid+'&GROUP_ID='+$('#cond_GROUP_ID').val(),'BillingMode,GroupInfo,LineInfo,busiInfo,oldBilingInfo',function(data){
		if(data.get("RESULT")=='0'){
			alert(data.get("error_message"));
		}
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function showSubscribeList(){
	var groupid = $("#cond2_GROUP_ID").val();
	if(!groupid){
		alert("请输入集团编码！");
		return false;
	}
	
	var startDate = $("#cond2_START_DATE").val();
	var endDate = $("#cond2_END_DATE").val();
	
	$.beginPageLoading('正在查询订单信息...');
	$.ajax.submit(null,'querySubscribeList','&GROUP_ID='+groupid+'&START_DATE='+startDate+'&END_DATE='+endDate,'SubscribeInfo',function(data){
		$.endPageLoading();
		backPopup("qryPopup2", "chooseOfferItem", true);
		showPopup('qryPopup1','moreSubscribe');
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function setSubscribeReturnValue(el){
	var ibsysid = $(el).attr("ibsysid");
	var groupid = $(el).attr("groupid");
	$("#cond_IBSYSID").val(ibsysid);
	$("#cond_GROUP_ID").val(groupid);
	backPopup("qryPopup1", "moreSubscribe", true);
}

function changeAccpt(){
	var monthInterval = $("#MONTH_INTERVAL").val();
	var newAccepttancePeriod = $("#NEW_ACCEPTTANCE_PERIOD").val();
	if(monthInterval>=2 && newAccepttancePeriod =="1"){
		$("#FILE_PART").css("display","");
	}else{
		$("#FILE_PART").css("display","none");
	}
}

/*function qryProductInfo(obj){
	var productNo = $(obj).attr("productNo");
	var ibsysid = $(obj).attr("ibsysid");
	var productId = $(obj).attr("productId");
	if(productNo == null || productNo == '' || productNo==undefined){
		return false;
	}
	if(ibsysid == null || ibsysid == '' || ibsysid==undefined){
		return false;
	}
	if(productId == null || productId == '' || productId==undefined){
		return false;
	}
	$.beginPageLoading('正在查询专线业务信息...');
	$.ajax.submit(null,'qryByIbsysidProductNo','&IBSYSID='+ibsysid+'&PRODUCTNO='+productNo+'&PRODUCT_ID='+productId,'ProductInfo',function(data){
		showPopup('qryPopup2','UI-moreProduct');
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}*/

function submit(){
	
	if(!$("#NEW_ACCEPTTANCE_PERIOD").val()){
		alert("请选择变更计费方式！");
		return false;
	}
	if(!$("#pattr_TITLE").val()){
		alert("请填写工单主题！");
		return false;
	}
	if(!$("#pattr_URGENCY_LEVEL").val()){
		alert("请选择工单紧急程度！");
		return false;
	}
	var rowDatas1 = myTable1.getCheckedRowsData("TRADES");
	
	if(rowDatas1==null||rowDatas1==""){
		$.validate.alerter.one(rowDatas1,"请至少选择一条专线进行计费方式变更！");
		return false;
	}
	
	var monthInterval = $("#MONTH_INTERVAL").val();
	var newAccepttancePeriod = $("#NEW_ACCEPTTANCE_PERIOD").val();
	var attachList = new Wade.DatasetList();
	if(monthInterval>=2 && newAccepttancePeriod =="1"){
		if($("#uploadDate").text()){
			var attachData = new Wade.DataMap($("#uploadDate").text());
			attachList.add(attachData);
		}else{
			$.validate.alerter.one($("simpleupload")[0], "请上传审批附件！");
			return false;
		}
	}
	
	var busiData = "";
	if($("#EOS_BUSI_DATA").text()){
		busiData = new Wade.DataMap($("#EOS_BUSI_DATA").text());
	}else{
		alert("未取到流程相关信息！");
		return false;
	}
	var eosCommonData = new Wade.DataMap();
	var message ="流程创建成功";
	var submitParam = new Wade.DataMap();
	submitParam.put("BUSI_SPEC_RELE", busiData.get("BUSI_SPEC_RELE"));
	submitParam.put("NODE_TEMPLETE", busiData.get("NODE_TEMPLETE"));
	submitParam.put("COMMON_DATA", saveEosCommonData());
	submitParam.put("EOMS_ATTR_LIST", saveEomsAttrList());
	submitParam.put("OFFER_DATA", saveOfferData());
	submitParam.put("CUST_DATA", saveCustData());
	submitParam.put("ORDER_DATA", saveOrderData());//订单级信息EOMS_ATTR_LIST
	submitParam.put("ATTACH_LIST", attachList);
	submitParam.put("BILING_DATA", saveBilingData());
	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submit", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		debugger;
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
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
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
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
			MessageBox.success(message, "定单号："+data.get("IBSYSID"), function(btn){
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

function saveEomsAttrList(){
	var eomsAttrList = new Wade.DatasetList();
	var titleData = new Wade.DataMap();
	titleData.put("ATTR_CODE","TITLE");
	titleData.put("ATTR_NAME","工单主题");
	titleData.put("ATTR_VALUE",$("#pattr_TITLE").val());
	eomsAttrList.add(titleData);
	
	var urgData = new Wade.DataMap();
	urgData.put("ATTR_CODE","URGENCY_LEVEL");
	urgData.put("ATTR_NAME","工单紧急程度");
	urgData.put("ATTR_VALUE",$("#pattr_URGENCY_LEVEL").val());
	eomsAttrList.add(urgData);
	
	var ibsysidData = new Wade.DataMap();
	ibsysidData.put("ATTR_CODE","IBSYSID");
	ibsysidData.put("ATTR_NAME","开通单流水号");
	ibsysidData.put("ATTR_VALUE",$("#pattr_IBSYSID").val());
	eomsAttrList.add(ibsysidData);
	
	var newAccpData = new Wade.DataMap();
	newAccpData.put("ATTR_CODE","NEW_ACCEPTTANCE_PERIOD");
	newAccpData.put("ATTR_NAME","计费延期方式");
	newAccpData.put("ATTR_VALUE",$("#NEW_ACCEPTTANCE_PERIOD").val());
	eomsAttrList.add(newAccpData);
	var flag=false;
	var chks = $("#myTbody [type=checkbox]");
	{
		//var tempDataLine = new Wade.DatasetList();
		var newLinei=1;
		for ( var j = 0; j < chks.length; j++) 
		{
			if (chks[j].checked)// 获取选中的列表
			{
				flag=true;
				var newLineData = new Wade.DataMap();
				newLineData.put("ATTR_CODE","PRODUCTNO");
				newLineData.put("ATTR_NAME","专线实例号");
				newLineData.put("ATTR_VALUE",$(chks[j]).attr("PRODUCTNO"));
				newLineData.put("RECORD_NUM",newLinei);
				eomsAttrList.add(newLineData);
				newLineData = new Wade.DataMap();
				newLineData.put("ATTR_CODE","USER_ID");
				newLineData.put("ATTR_NAME","成员id");
				newLineData.put("ATTR_VALUE",$(chks[j]).attr("USERID"));
				newLineData.put("RECORD_NUM",newLinei);
				eomsAttrList.add(newLineData);
				newLinei++;
			}
		}
	}
	if(!flag){
		MessageBox.alert("请至少选择一条专线进行计费方式变更");
		return false;
	}
	
	
	return eomsAttrList;
}

function saveEosCommonData(){
	var eosCommonData = new Wade.DataMap();
	if($("#EOS_COMMON_DATA").text()){
		eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
		if(!eosCommonData.get("CUST_NAME"))
		{
			eosCommonData.put("CUST_NAME", $("#CUST_NAME").val());
		}
	}
	return eosCommonData;
}

function saveOfferData(){
	var rowDatas = myTable1.getCheckedRowsData("TRADES");
	
	if(rowDatas==null||rowDatas==""){
		$.validate.alerter.one(rowDatas,"查询专线数据有误，请重新查询后在提交！");
		return false;
	}
	
	var offerData = new Wade.DataMap();
	var suboffers = new Wade.DatasetList();
	for(var i=0;i<rowDatas.length;i++){
		var rowData = rowDatas.get(i);
		var suboffer = new Wade.DataMap();
		suboffer.put("OFFER_CODE",rowData.get("PRODUCT_ID"));
		suboffer.put("OFFER_NAME",rowData.get("PRODUCT_NAME"));
		suboffer.put("OFFER_TYPE","P");
		suboffer.put("USER_ID",rowData.get("USER_ID"));
		suboffers.add(suboffer);
	}
	offerData.put("SUBOFFERS",suboffers);
	offerData.put("OFFER_CODE",$("#pattr_BUSI_CODE").val());
	offerData.put("OFFER_NAME",$("#pattr_PRODUCT_NAME").val());
	return offerData;
}

function saveOrderData(){
	var orderData = new Wade.DataMap();
	orderData.put("TITLE",$("#pattr_TITLE").val());
	orderData.put("URGENCY_LEVEL",$("#pattr_URGENCY_LEVEL").val());
	return orderData;
}

function saveCustData(){
	var custData = new Wade.DataMap();
	custData.put("GROUP_ID",$("#GROUP_ID").val());
	custData.put("CUST_NAME", $("#CUST_NAME").val());
	return custData;
}

function saveBilingData(){
	var bilingData = new Wade.DataMap();
	bilingData.put("ACCEPT_DATE",$("#ACCEPT_DATE").val());
	bilingData.put("END_DATE",$("#END_DATE").val());
	bilingData.put("START_DATE",$("#START_DATE").val());
	bilingData.put("NEW_ACCEPTTANCE_PERIOD",$("#NEW_ACCEPTTANCE_PERIOD").val());
	return bilingData;
}