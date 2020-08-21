var table = new Wade.DatasetList();
//var tableFrom = new Wade.DatasetList();
//var tableFromr = new Wade.DatasetList();
function initPageParam_110000006130(){
	//debugger;
	//$("#ZyhtTable tbody tr").attr("class","");
	var offerTpye = $("#cond_OPER_TYPE").val();
	window["ZyhtTable"] = new Wade.Table("ZyhtTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#ZyhtTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(ZyhtTable.getSelectedRowData());
	});
	$.each(table,function(index,data) { 
		ZyhtTable.addRow($.parseJSON(data.toString()));
	});
	if("CrtUs"==offerTpye){
		 $("#pam_VPN_NO2").closest("li").css("display", "none");
		 $("#pam_VPN_NO2").attr("nullable", "yes");
	}
	if("ChgUs"==offerTpye){
		 $("#pam_VPN_NO1").closest("li").css("display", "none");
		 $("#pam_VPN_NO1").attr("nullable", "yes");
		 $("#CALLCENTERTYPE").val("");
		 $("#CALLCENTERSHOW").val("");
		 $("#pam_VPN_NO").val($("#pam_VPN_NO2").val());
		//var superTelEdit = $("#SUPERNUMBER").val();	
		 var superTelEdit = $("#SUPER_INFOS").val();	
		superTelEdit = superTelEdit.replace(/'/g, '"');
		var superTelEditData = new Wade.DatasetList(superTelEdit);	
		if(table.length==0){
     		  $.each(superTelEditData,function(index,data) { 
     			  $("#CALLCENTERTYPE1").val(data.get("CALLCENTERTYPE"));
     			  $("#CALLCENTERSHOW1").val(data.get("CALLCENTERSHOW"));
     			  var callCenterTypeName =  $("#CALLCENTERTYPE1").text();
     			  var callCenterShowName =  $("#CALLCENTERSHOW1").text();
     			  data.put("callCenterTypeName",callCenterTypeName);
     			  data.put("callCenterShowName",callCenterShowName);
     			  ZyhtTable.addRow($.parseJSON(data.toString()));
     			  //tableFromr = ZyhtTable.getData();
     		  });
     	  }
		
	  }
	$("#ZyhtTable tbody tr").attr("class","");//去掉背景色
}


/*function setPamSuperNumber() {
	var data = ZyhtTable.getData();
	$("#pam_SUPERNUMBER").val(data.toString());	
}*/

function addSuperTel(){
	 //效验表单
    if(!verifySupTelListTable()) return false;   
    var pamSerialNumber = $("#pam_EXCHANGETELE_SN").val(); 
    var serialNumber = $("#EXCHANGETELE_SN").val(); 
	if (pamSerialNumber != serialNumber)
	{
		MessageBox.alert("","请验证总机号码！");
		return false;
	}
	
    if(!verifySupTelListTable()) return false;   
	//判断是否注册和注销码相同
	var corp_regcode = $("#CORP_REGCODE").val() ;
	var corp_deregcode = $("#CORP_DEREGCODE").val() ;
	if (corp_regcode != "" && corp_deregcode != "" && corp_regcode == corp_deregcode){
		MessageBox.alert("","\u6ce8\u518c\u7801\u548c\u6ce8\u9500\u7801\u4e0d\u80fd\u76f8\u540c");
		return false;
	}	
	
	//新增表格行数据,并且保存参数
	var superTelEdit = $.ajax.buildJsonData("partSuperTelInfo");
//	var newd=ZyhtTable.getData();
	table.add(ZyhtTable.getData);
	var list =  ZyhtTable.getData(true,'EXCHANGETELE_SN');
	var flag = 0;
	list.each(function(item,index,totalcount){
		if($("#EXCHANGETELE_SN").val()==list.get(index,'EXCHANGETELE_SN')){
			flag = 1;
		}
	});
	
	
	if (flag==0){
		var callCenterTypeName = $("#CALLCENTERTYPE").text();
		var callCenterShowName = $("#CALLCENTERSHOW").text();
		var data = $.DataMap(superTelEdit);
		data.put("callCenterTypeName",callCenterTypeName);
		data.put("callCenterShowName",callCenterShowName);
		ZyhtTable.addRow($.parseJSON(data.toString()));
		//setPamSuperNumber();
		delAll(superTelEdit);
	}else{
		MessageBox.alert("","关键字段\"总机号码\"已经存在相同的记录\"" + $("#pam_EXCHANGETELE_SN").val() + "\"");
	}
	$("#ZyhtTable tbody tr").attr("class","");
}



function delSuperTel(){
	table.remove(ZyhtTable.getData());
	ZyhtTable.deleteRow(ZyhtTable.selected);
    //setPamSuperNumber();
}

function updateSuperTel(){
	//效验表单
    if(!verifySupTelListTable()) return false;  
    var serialNumber = $("#pam_EXCHANGETELE_SN").val();   
    if(serialNumber == ""){
		//alert ("\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		$.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0], "\u8bf7\u8f93\u5165\u53f7\u7801\uff01");
		return false;
    } 	
	
	//判断是否注册和注销码相同
	var corp_regcode = $("#CORP_REGCODE").val() ;
	var corp_deregcode = $("#CORP_DEREGCODE").val() ;
	if (corp_regcode != "" && corp_deregcode != "" && corp_regcode == corp_deregcode){
		MessageBox.alert("","\u6ce8\u518c\u7801\u548c\u6ce8\u9500\u7801\u4e0d\u80fd\u76f8\u540c");
		return false;
	}	
	
	//新增表格行数据,并且保存参数
	var superTelEdit = $.ajax.buildJsonData("partSuperTelInfo");
	var newd=ZyhtTable.getData();
	table.add(ZyhtTable.getData);
	var callCenterTypeName;
	var callCenterShowName;
	var callCenterTypeName = $("#CALLCENTERTYPE").text();
	var callCenterShowName = $("#CALLCENTERSHOW").text();
	var data = $.DataMap(superTelEdit);
	data.put("callCenterTypeName",callCenterTypeName);
	data.put("callCenterShowName",callCenterShowName);
	ZyhtTable.updateRow($.parseJSON(data.toString()),ZyhtTable.selected);
	//setPamSuperNumber();
	delAll(superTelEdit);
}

function tableRowClick(data) {
	//获取选择行的数据
//	 var rowData = $.table.get("ZyhtTable").getRowData();
	 $("#pam_EXCHANGETELE_SN").val(data.get("EXCHANGETELE_SN"));
	 $("#EXCHANGETELE_SN").val(data.get("EXCHANGETELE_SN"));
	 $("#E_CUST_NAME").val(data.get("E_CUST_NAME"));
	 $("#E_CUST_ID").val(data.get("E_CUST_ID"));
	 $("#E_USER_ID").val(data.get("E_USER_ID"));
	 $("#E_EPARCHY_CODE").val(data.get("E_EPARCHY_CODE"));
	 $("#E_EPARCHY_NAME").val(data.get("E_EPARCHY_NAME"));
	 $("#MAXWAITINGLENGTH").val(data.get("MAXWAITINGLENGTH"));
	 $("#CALLCENTERTYPE").val(data.get("CALLCENTERTYPE"));
	 $("#callCenterTypeName").val(data.get("callCenterTypeName"));
	 $("#CALLCENTERSHOW").val(data.get("CALLCENTERSHOW"));
	 $("#callCenterShowName").val(data.get("callCenterShowName"));
	 $("#CORP_REGCODE").val(data.get("CORP_REGCODE"));
	 $("#CORP_DEREGCODE").val(data.get("CORP_DEREGCODE"));

}


function initCrtUs() {  
	var tableedit = $.table.get("ZyhtTable");
}


function validateSuperTeleInfo(){
	
	var serialNumber = $('#pam_EXCHANGETELE_SN').val();
	
	if(serialNumber == null || serialNumber.length == 0){
		MessageBox.alert("","总机号码不能为空!");
		$('#pam_EXCHANGETELE_SN').focus();
		return;
	}
	
	if(!$.validate.verifyField($("#pam_EXCHANGETELE_SN"))) return false;
	
	var param = "&SERIAL_NUMBER=" + serialNumber ;
	$.beginPageLoading();
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.offercha.centrexsupertel.CentrexsupertelParamHandler", "checkSuperTeleInfo", param,function(data){
		$.endPageLoading();
		sucValidateSuperTelInfo(data);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		$("#EXCHANGETELE_SN").val("");
	    $("#E_CUST_NAME").val("");
	    $("#E_CUST_ID").val("");
	    $("#E_USER_ID").val("");
	    $("#E_EPARCHY_CODE").val("");
		showDetailErrorInfo(error_code,error_info,derror);
    });

}

function sucValidateSuperTelInfo(data){
//	console.log(data);
	debugger;
	var result = data.get("AJAX_DATA");
	$("#pam_EXCHANGETELE_SN").val(result.get("EXCHANGETELE_SN"));
	$("#EXCHANGETELE_SN").val(result.get("EXCHANGETELE_SN"));
    $("#E_CUST_NAME").val(result.get("CUST_NAME"));
    $("#E_CUST_ID").val(result.get("CUST_ID"));
    $("#E_USER_ID").val(result.get("USER_ID"));
    $("#E_EPARCHY_CODE").val(result.get("EPARCHY_CODE"));
    $("#E_EPARCHY_NAME").val(result.get("EPARCHY_NAME"));
    $.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0], "融合总机号码可以使用！\n");
}



function setDefaultNumber(){
	var  exchangeteleSn = $("#EXCHANGETELE_SN").val();
	if(""==exchangeteleSn || undefined ==exchangeteleSn){
		$.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0],"总机号码未进行验证，请验证后再设置！");
		return false;
	}
	var ecDffaultNumber = $("#pam_EC_DEFAULT_NUMBER").val();
	if(""==ecDffaultNumber || undefined ==ecDffaultNumber){
		$("#pam_EC_DEFAULT_NUMBER").val(exchangeteleSn);
	}else if(ecDffaultNumber!=exchangeteleSn){
		$.validate.alerter.one($("#pam_EC_DEFAULT_NUMBER")[0],"呼叫显示总机号码"+ecDffaultNumber+"变更为"+exchangeteleSn);
		$("#pam_EC_DEFAULT_NUMBER").val(exchangeteleSn);
		return true;
	}
	 
}


function verifySupTelListTable(){
	var  pamExchangeteleSn = $("#pam_EXCHANGETELE_SN").val();
	if(""==pamExchangeteleSn || undefined ==pamExchangeteleSn){
		$.validate.alerter.one($("#pam_EXCHANGETELE_SN")[0],"总机号码未进行验证或填写，请验证或填写后再新增！");
		return false;
	}
	var  exchangeteleSn = $("#EXCHANGETELE_SN").val();
	if(""==exchangeteleSn || undefined ==exchangeteleSn){
		$.validate.alerter.one($("#EXCHANGETELE_SN")[0],"服务号码为空，请进行总机号码验证，获取服务号码！");
		return false;
	}
	var  eparchyName = $("#E_EPARCHY_NAME").val();
	if(""==eparchyName || undefined ==eparchyName){
		$.validate.alerter.one($("#E_EPARCHY_NAME")[0],"归属地州为空，请进行总机号码验证，获取归属地州！");
		return false;
	}
	var  custName = $("#E_CUST_NAME").val();
	if(""==custName || undefined ==custName){
		$.validate.alerter.one($("#E_CUST_NAME")[0],"客户名称为空，请进行总机号码验证，获取客户名称为空！");
		return false;
	}
	var  maxwaiting = $("#MAXWAITINGLENGTH").val();
	if(""==maxwaiting || undefined ==maxwaiting){
		$.validate.alerter.one($("#MAXWAITINGLENGTH")[0],"等待列数为空，请填写！");
		return false;
	}
	var  callcenterType = $("#CALLCENTERTYPE").val();
	if(""==callcenterType || undefined ==callcenterType){
		$.validate.alerter.one($("#CALLCENTERTYPE")[0],"呼叫类型未选择，请选择！");
		return false;
	}
	var  callcenterShow = $("#CALLCENTERSHOW").val();
	if(""==callcenterShow || undefined ==callcenterShow){
		$.validate.alerter.one($("#CALLCENTERSHOW")[0],"呼群用户显号码未选择，请选择！");
		return false;
	}
	var  corpRegcode = $("#CORP_REGCODE").val();
	if(""==corpRegcode || undefined ==corpRegcode){
		$.validate.alerter.one($("#CORP_REGCODE")[0],"话务员注册码未填写，请填写！");
		return false;
	}
	var  corpDerecode = $("#CORP_DEREGCODE").val();
	if(""==corpDerecode || undefined ==corpDerecode){
		$.validate.alerter.one($("#CORP_DEREGCODE")[0],"话务员注销码未填写，请填写！");
		return false;
	}

	return true;
}

function checkFiled(obj){
	var keyValue = $("#" + obj).val();	
	
	if(null == keyValue || "" == keyValue){
	    MessageBox.alert("",$("#" + obj).attr("desc") + '\u4e0d\u80fd\u4e3a\u7a7a');
	    $("#" + obj).focus()
	 	return false;
	}
	
	return $.validate.verifyField($("#" + obj));
	
	return true;
}

function setCallCenterTypeName()
{
   $("#callCenterTypeName").val($('#CALLCENTERTYPE option:selected').text()); 
}

function setCallCenterShowName()
{  
  $("#callCenterShowName").val($('#CALLCENTERSHOW option:selected').text()); 
}

function delAll(data){
	var idata = $.DataMap(data);
	idata.eachKey(function(item,index,totalcount){
		$("#"+item).val('');
	});
}

function checkSub(obj){
	debugger;
	table = ZyhtTable.getData();
	var offerTpye = $("#cond_OPER_TYPE").val();
	/*if("ChgUs"==offerTpye){
		tableFromer();
	}*/
	
	if("CrtUs"==offerTpye){
		$("#pam_VPN_NO").val($("#pam_VPN_NO1").val());
		//$("#SUPERNUMBER").val(table.toString());
		 //var superNum =  new Wade.DatasetList($("#SUPERNUMBER").val());
		 //if(superNum.length==0){
		if(table.length==0){
			MessageBox.alert("提示信息","请至少添加一条总机信息");
			return false; 
		}
	}
	
	var oldPamAttr = $("#SUPER_INFOS").val();
	
	var attrList = new Wade.DatasetList();
	attrList = compareDataset(table,oldPamAttr);
	$("#SUPERNUMBER").val(attrList.toString());
	
	
	
	$("#pam_EXCHANGETELE_SN").attr("nullable", "yes");
	$("#E_CUST_NAME").attr("nullable", "yes");
	$("#E_EPARCHY_NAME").attr("nullable", "yes");
	$("#MAXWAITINGLENGTH").attr("nullable", "yes");
	$("#CALLCENTERTYPE").attr("nullable", "yes");
	$("#CALLCENTERSHOW").attr("nullable", "yes");
	$("#CORP_REGCODE").attr("nullable", "yes");
	$("#CORP_DEREGCODE").attr("nullable", "yes");
	$("#EXCHANGETELE_SN").attr("nullable", "yes");
	
	if(submitOfferCha()){
		backPopup(obj);
	}
}

function compareDataset(strNewValue,strOldValue){
	if (strOldValue == ""){
        strOldValue="[]";
	}
    if (strNewValue == ""){
        strNewValue = "[]";
    }
    
    var newValueSet = strNewValue;
    var oldValueSet = new Wade.DatasetList(strOldValue);
    var resultSet = new Wade.DatasetList();
    
    //筛选变更、不变、新增
    for (var i=0;i<newValueSet.length;i++){
    	var isfound = "false";
    	var newValueColumn = newValueSet.get(i);
	    for (var j=0;j<oldValueSet.length;j++) {
	        var oldValueColumn = oldValueSet.get(j);
	        if(oldValueColumn.get('EXCHANGETELE_SN') == newValueColumn.get('EXCHANGETELE_SN')){
	        	if(oldValueColumn.get('MAXWAITINGLENGTH') == newValueColumn.get('MAXWAITINGLENGTH')&&
	        		oldValueColumn.get('CALLCENTERTYPE') == newValueColumn.get('CALLCENTERTYPE')&&
	        		oldValueColumn.get('CALLCENTERSHOW') == newValueColumn.get('CALLCENTERSHOW')&&
	        		oldValueColumn.get('CORP_REGCODE') == newValueColumn.get('CORP_REGCODE')&&
	        		oldValueColumn.get('CORP_DEREGCODE') == newValueColumn.get('CORP_DEREGCODE')){
	        		
	        		newValueColumn.put("tag","");
	        	}else{
	        		newValueColumn.put("tag","2");
	        	}
	        	var isfound = "true";
				resultSet.add(newValueColumn);
	        }
	    }
	    if(isfound == "false"){
	    	newValueColumn.put("tag","0");
       		resultSet.add(newValueColumn);
	    }
	}
	
  //筛选删除的数据
	for (var i=0;i<oldValueSet.length;i++) {
        var oldValueColumn = oldValueSet.get(i);
        var isfound = "false";
        for (var j=0;j<newValueSet.length;j++) {
            var newValueColumn = newValueSet.get(j);
            if(oldValueColumn.get('pam_EXCHANGETELE_SN') == newValueColumn.get('pam_EXCHANGETELE_SN'))
            {
            	isfound = "true";
            	break;
            }
        }
        if (isfound == "false") {
            oldValueColumn.put("tag","1");
            resultSet.add(oldValueColumn);
        }
   }
   
   return resultSet.toString();
}