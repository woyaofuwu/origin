var flag = "false";
$(function(){
	debugger;
	// 绑定导入组件的导入前事件,返回 false 时，取消导入
	$("#importFile").beforeAction(function(e){
//		MessageBox.alert("业务提示",hint);
		return confirm("是否导入？");
			
	});
	var productId = $("#PRODUCT_ID").val();
	// 绑定导入组件的导入后事件，第二个形参为导入完成状态，ok 为成功完成
	$("#importFile").afterAction(function(e, status){
		debugger;
		if('ok' == status){
			$.beginPageLoading("正在处理...");
			ajaxSubmit('','importFile',"&PRODUCT_ID="+productId,'',function(data){
				$.endPageLoading();
				$("#myTable tbody").html("");
				  for (var i = 0; i < data.length; i++) {
			            var info = data.get(i);
			            myTable.addRow($.parseJSON(info.toString()));
			        }
				flag="true";
	    		
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}		
	});
	$("#myExport").beforeAction(function(e){
		if ($("#tableBody").children("tr").length < 1) {
            MessageBox.alert("提示信息", "没有可导出的数据！");
            return false;
        }
		return confirm('是否导出?');
	});
//	var groupInfo = $.enterpriseLogin.getInfo().get("GROUP_INFO");
//	if(groupInfo!=undefined && groupInfo!=null){
//		 $("#POP_cond_GROUP_ID").val(groupInfo.get("GROUP_ID"));
//		/*$("#cond_GROUP_NAME").val(groupInfo.get("GROUP_NAME"));*/
//	}	
	
	$("#paidanxinxi").css("display","none");
	$("#openDefaultOp").css("display","");
	$("#hideDefaultOp").css("display","none");
	
	$("#fileupload").select(function(){
		var fileList = new Wade.DatasetList();
		
		var obj = this.val();
		var fileIdArr = obj.ID.split(",");
		var fileNameArr = obj.NAME.split(",");
		for(var i = 0, size = fileIdArr.length; i < size; i++)
		{
			if(fileIdArr[i] != "")
			{
				var data = new Wade.DataMap();
				data.put("FILE_ID", fileIdArr[i]);
				data.put("FILE_NAME", fileNameArr[i]);
				data.put("ATTACH_TYPE", "P");
				fileList.add(data);
			}
		}
		$("#ATTACH_FILE_LIST").text(fileList.toString());
		
		$("#upfileText").text(obj.NAME);
		$("#upfileValue").val(obj.ID);
		
		$("#ATTACH_FILE_NAME").val(obj.NAME);
		$("#ATTACH_FILE_ID").val(obj.ID);
		
		hidePopup("popup01");
	});
	
	$("#fileupload").clear(function(){
		$("#upfileText").text("");
		$("#upfileValue").val("");
		
		$("#ATTACH_FILE_NAME").val("");
		$("#ATTACH_FILE_ID").val("");
		
		$("#ATTACH_FILE_LIST").text("");
	});
	
	if($("#FILEUPLOAD_FILELIST").val()){
		var fileListP = $("#FILEUPLOAD_FILELIST").val();
		fileupload.loadFile(fileListP);
	}
	
	$("#CONTRACT_FILE_LIST").afterAction(function(e, file){
		var data1 = new Wade.DataMap();
		data1.put("FILE_ID", file.fileId);
		data1.put("FILE_NAME", file.name);
		data1.put("ATTACH_TYPE", "C");
		$("#C_FILE_LIST").text(data1.toString());
		$("#C_FILE_LIST").val(file.fileId+':'+file.name);
		
		$("#C_FILE_LIST_NAME").val(file.name);
	});
	
	
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();//流程名
	var productId = $("#PRODUCT_ID").val();//产品Id
	var changemode = $("#CHANGEMODE").val();//变更场景
	//初始化展示字段
	if ("7012" == productId||"70121" == productId||"70122" == productId) {
	    //勘察  
	    if ("ERESOURCECONFIRMZHZG" == bpmTempletId || "ECHANGERESOURCECONFIRM" == bpmTempletId) {
	        $("#ISPREOCCUPY").closest("li").css("display", "");
	        $("#PREREASON").closest("li").css("display", "");
	        $("#TRANSFERMODE").closest("li").css("display", "");
	    }
	    $("#PRODUCTNO").closest("li").css("display", "");
	    $("#ROUTEMODE").closest("li").css("display", "");
	    $("#BIZSECURITYLV").closest("li").css("display", "");
	    $("#BANDWIDTH").closest("li").css("display", "");
	    $("#PORTACUSTOM").closest("li").css("display", "");
	    $("#PROVINCEA").closest("li").css("display", "");
	    $("#CITYA").closest("li").css("display", "");
	    $("#AREAA").closest("li").css("display", "");
	    $("#COUNTYA").closest("li").css("display", "");
	    $("#VILLAGEA").closest("li").css("display", "");
	    $("#PORTAINTERFACETYPE").closest("li").css("display", "");
	    $("#PORTACONTACT").closest("li").css("display", "");
	    $("#PORTACONTACTPHONE").closest("li").css("display", "");
	    $("#PORTZCUSTOM").closest("li").css("display", "");
	    $("#PROVINCEZ").closest("li").css("display", "");
	    $("#CITYZ").closest("li").css("display", "");
	    $("#AREAZ").closest("li").css("display", "");
	    $("#COUNTYZ").closest("li").css("display", "");
	    $("#VILLAGEZ").closest("li").css("display", "");
	    $("#PORTZINTERFACETYPE").closest("li").css("display", "");
	    $("#PORTZCONTACT").closest("li").css("display", "");
	    $("#PORTZCONTACTPHONE").closest("li").css("display", "");
	    $("#TRADENAME").closest("li").css("display", "");

	} else if ("7011" == productId||"70111" == productId||"70112" == productId) {
	    $("#PRODUCTNO").closest("li").css("display", "");
	    $("#TRANSFERMODE").closest("li").css("display", "");
	    $("#BIZSECURITYLV").closest("li").css("display", "");
	    $("#BANDWIDTH").closest("li").css("display", "");
	    $("#PORTACUSTOM").closest("li").css("display", "");
	    $("#PROVINCEA").closest("li").css("display", "");
	    $("#CITYA").closest("li").css("display", "");
	    $("#AREAA").closest("li").css("display", "");
	    $("#COUNTYA").closest("li").css("display", "");
	    $("#VILLAGEA").closest("li").css("display", "");
	    $("#PORTAINTERFACETYPE").closest("li").css("display", "");
	    $("#PORTACONTACT").closest("li").css("display", "");
	    $("#PORTACONTACTPHONE").closest("li").css("display", "");
	    $("#TRADENAME").closest("li").css("display", "");

	    //互联网专线开通
	    if ("EDIRECTLINEOPENPBOSS" == bpmTempletId) {
	        $("#CUSAPPSERVIPADDNUM").closest("li").css("display", "");
	        $("#DOMAINNAME").closest("li").css("display", "");
	        $("#MAINDOMAINADD").closest("li").css("display", "");

	        //勘察 开通 //勘察 变更
	    } else if ("ERESOURCECONFIRMZHZG" == bpmTempletId || "ECHANGERESOURCECONFIRM" == bpmTempletId) {
	        $("#ISPREOCCUPY").closest("li").css("display", "");
	        $("#ROUTEMODE").closest("li").css("display", "");
	        $("#PREREASON").closest("li").css("display", "");

	    }
	    //变更分场景
	    else if ("EDIRECTLINECHANGEPBOSS" == bpmTempletId||"DIRECTLINECHANGESIMPLE"==bpmTempletId) {
	        $("#IPTYPE").closest("li").css("display", "");
	        $("#CUSAPPSERVIPADDNUM").closest("li").css("display", "");
	        $("#CUSAPPSERVIPV4ADDNUM").closest("li").css("display", "");
	        $("#CUSAPPSERVIPV6ADDNUM").closest("li").css("display", "");
	        $("#IPCHANGE").closest("li").css("display", "");
	        $("#DOMAINNAME").closest("li").css("display", "");
	        $("#MAINDOMAINADD").closest("li").css("display", "");
	    }
	} else if ("7016" == productId) {
	    $("#PRODUCTNO").closest("li").css("display", "");
	    $("#TRANSFERMODE").closest("li").css("display", "");
	    $("#BIZSECURITYLV").closest("li").css("display", "");
	    $("#BANDWIDTH").closest("li").css("display", "");
	    $("#PORTACUSTOM").closest("li").css("display", "");
	    $("#PROVINCEA").closest("li").css("display", "");
	    $("#CITYA").closest("li").css("display", "");
	    $("#AREAA").closest("li").css("display", "");
	    $("#COUNTYA").closest("li").css("display", "");
	    $("#VILLAGEA").closest("li").css("display", "");
	    $("#PORTAINTERFACETYPE").closest("li").css("display", "");
	    $("#PORTACONTACT").closest("li").css("display", "");
	    $("#PORTACONTACTPHONE").closest("li").css("display", "");
	    $("#TRADENAME").closest("li").css("display", "");

	    //IMS专线开通
	    if ("EDIRECTLINEOPENPBOSS" == bpmTempletId) {
	        $("#CUSAPPSERVIPADDNUM").closest("li").css("display", "");
	        $("#DOMAINNAME").closest("li").css("display", "");
	        $("#MAINDOMAINADD").closest("li").css("display", "");

	        //勘察 开通 //勘察 变更
	    } else if ("ERESOURCECONFIRMZHZG" == bpmTempletId || "ECHANGERESOURCECONFIRM" == bpmTempletId) {
	        $("#ISPREOCCUPY").closest("li").css("display", "");
	        $("#ROUTEMODE").closest("li").css("display", "");
	        $("#PREREASON").closest("li").css("display", "");

	    }
	    //变更分场景
	    else if ("EDIRECTLINECHANGEPBOSS" == bpmTempletId||"DIRECTLINECHANGESIMPLE"==bpmTempletId) {
//	        $("#IPTYPE").closest("li").css("display", "");
	        $("#CUSAPPSERVIPADDNUM").closest("li").css("display", "");
//	        $("#CUSAPPSERVIPV4ADDNUM").closest("li").css("display", "");
//	        $("#CUSAPPSERVIPV6ADDNUM").closest("li").css("display", "");
	        $("#IPCHANGE").closest("li").css("display", "");
	        $("#DOMAINNAME").closest("li").css("display", "");
	        $("#MAINDOMAINADD").closest("li").css("display", "");
	    }
	}
	
	
});

function checkSub(obj) {
	debugger;
	if(flag=="false"){
		$.validate.alerter.one(flag,"您没有做修改或者导入操作，不能重派!");
		return false;
	}
	var tableList = myTable.getData(true);
	var dataset = new Wade.DatasetList();
    for (var i = 0, len = tableList.length; i < len; i++) {
	    var data = tableList.get(i);
//	    if (data.map.ISPREOCCUPY) {
//	        data.map.ISPREOCCUPY = convertSelectValue(data.map.ISPREOCCUPY, "value");
//	    }
//	    if (data.map.ROUTEMODE) {
//	        data.map.ROUTEMODE = convertSelectValue(data.map.ROUTEMODE, "value");
//	    }
	    dataset.add(data);
	}
    var  attachInfos = saveAttach();
    var  contractInfos = contractAttach();
    var buildingsection =$("#BUILDINGSECTION").val();
    var publicAttrInfo = new Wade.DataMap($("#PUBLIC_ATTR_LIST").text());
    $.beginPageLoading("正在处理...");
    	ajaxSubmit('daorudaochu','checkinWorkSheet',"&CONTRACTATTACH_LIST="+contractInfos+"&PUBLIC_ATTR_LIST="+publicAttrInfo.toString()+"&ATTACH_LIST="+attachInfos+ "&BUILDINGSECTION="+buildingsection+ "&DATALINE_LIST_INFO="+dataset,'',function(data){
    		$.endPageLoading();
    		MessageBox.success("提交", "操作成功！",function(){
    			closeNav();
    		});
    	},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
}

function saveAttach()
{
	//附件
	var attachList = new Wade.DatasetList($("#ATTACH_FILE_LIST").text());
	for(var i = 0, size = attachList.length; i < size; i++)
	{
		attachList.get(i).put("REMARK", $("#ATTACH_REMARK").val()); 
	}
	return attachList;
}


function contractAttach()
{
	debugger;
	var attachList = new Wade.DatasetList();
	//合同附件
	var contractFile = new Wade.DataMap($("#C_FILE_LIST").text());
	if(contractFile.items!=""){
		attachList.add(contractFile);
	}
	return attachList;
}  


function changeDefaultOp(obj,tp){
	if("1"==tp){
		 $("#paidanxinxi").css("display","");
		 $("#openDefaultOp").css("display","none");
		 $("#hideDefaultOp").css("display","");
	}
	if("2"==tp){
		 $("#paidanxinxi").css("display","none");
		 $("#openDefaultOp").css("display","");
		 $("#hideDefaultOp").css("display","none");
	}
	
}

function doReturnValue() {
	debugger;
    var lengths = myTable.selected;
	var rowData = myTable.getCheckedRowsData("ckline");
	var addInfo = rowData.get(0);
	var batchLengths = Number($("#LENGTHS_SUM").val());
	if( 5 < batchLengths){
		$.validate.alerter.one(rowData,"需要重派的专线条数大于5条,不能单个修改，请选择导出修改后，再进行导入!");
		return false;
	}
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();//流程名
	var productId = $("#PRODUCT_ID").val();//产品Id
	var changemode = $("#CHANGEMODE").val();//变更场景
	
	if ("7012"==productId||"70121" == productId||"70122" == productId) {
		//专线开通
		if ("EDIRECTLINEOPENPBOSS"==bpmTempletId) {
			$("#PRODUCTNO").val(addInfo.get("PRODUCTNO"));
		//勘察  
		} else if("ERESOURCECONFIRMZHZG"==bpmTempletId || "ECHANGERESOURCECONFIRM"==bpmTempletId){
			$("#PRODUCTNO").val(addInfo.get("PRODUCTNO"));
			$("#ISPREOCCUPY").val(convertSelectValue(addInfo.get("ISPREOCCUPY"), "value"));
			$("#PREREASON").val(addInfo.get("PREREASON"));
			$("#TRANSFERMODE").val(addInfo.get("TRANSFERMODE"));
		}
		$("#ROUTEMODE").val(convertSelectValue(addInfo.get("ROUTEMODE"), "value"));
		$("#BIZSECURITYLV").val(addInfo.get("BIZSECURITYLV"));
		$("#BANDWIDTH").val(addInfo.get("BANDWIDTH"));
		$("#PORTACUSTOM").val(addInfo.get("PORTACUSTOM"));
		$("#PROVINCEA").val(addInfo.get("PROVINCEA"));
		$("#CITYA").val(addInfo.get("CITYA"));
		$("#AREAA").val(addInfo.get("AREAA"));
		$("#COUNTYA").val(addInfo.get("COUNTYA"));
		$("#VILLAGEA").val(addInfo.get("VILLAGEA"));
		$("#PORTAINTERFACETYPE").val(addInfo.get("PORTAINTERFACETYPE"));
		$("#PORTACONTACT").val(addInfo.get("PORTACONTACT"));
		$("#PORTACONTACTPHONE").val(addInfo.get("PORTACONTACTPHONE"));
		$("#PORTZCUSTOM").val(addInfo.get("PORTZCUSTOM"));
		$("#PROVINCEZ").val(addInfo.get("PROVINCEZ"));
		$("#CITYZ").val(addInfo.get("CITYZ"));
		$("#AREAZ").val(addInfo.get("AREAZ"));
		$("#COUNTYZ").val(addInfo.get("COUNTYZ"));
		$("#VILLAGEZ").val(addInfo.get("VILLAGEZ"));
		$("#PORTZINTERFACETYPE").val(addInfo.get("PORTZINTERFACETYPE"));
		$("#PORTZCONTACT").val(addInfo.get("PORTZCONTACT"));
		$("#PORTZCONTACTPHONE").val(addInfo.get("PORTZCONTACTPHONE"));
		$("#TRADENAME").val(addInfo.get("TRADENAME"));
		//变更分场景
		if("EDIRECTLINECHANGEPBOSS"==bpmTempletId||"DIRECTLINECHANGESIMPLE"==bpmTempletId){
			internetLinePartInit(bpmTempletId, changemode, addInfo);
		}

	} else if ("7011"==productId||"70111" == productId||"70112" == productId) {
		//互联网专线开通
		if ("EDIRECTLINEOPENPBOSS"==bpmTempletId) {
			$("#PRODUCTNO").val(addInfo.get("PRODUCTNO"));
			$("#TRANSFERMODE").val(addInfo.get("TRANSFERMODE"));
			$("#BIZSECURITYLV").val(addInfo.get("BIZSECURITYLV"));
			$("#BANDWIDTH").val(addInfo.get("BANDWIDTH"));
			$("#PORTACUSTOM").val(addInfo.get("PORTACUSTOM"));

			$("#PROVINCEA").val(addInfo.get("PROVINCEA"));
			$("#CITYA").val(addInfo.get("CITYA"));
			$("#AREAA").val(addInfo.get("AREAA"));
			$("#COUNTYA").val(addInfo.get("COUNTYA"));
			$("#VILLAGEA").val(addInfo.get("VILLAGEA"));
			$("#PORTAINTERFACETYPE").val(addInfo.get("PORTAINTERFACETYPE"));
			$("#PORTACONTACT").val(addInfo.get("PORTACONTACT"));
			$("#PORTACONTACTPHONE").val(addInfo.get("PORTACONTACTPHONE"));

			$("#CUSAPPSERVIPADDNUM").val(addInfo.get("CUSAPPSERVIPADDNUM"));
			$("#DOMAINNAME").val(addInfo.get("DOMAINNAME"));
			$("#MAINDOMAINADD").val(addInfo.get("MAINDOMAINADD"));
			$("#TRADENAME").val(addInfo.get("TRADENAME"));
		//勘察 开通 //勘察 变更
		} else if("ERESOURCECONFIRMZHZG"==bpmTempletId || "ECHANGERESOURCECONFIRM"==bpmTempletId){
			$("#PRODUCTNO").val(addInfo.get("PRODUCTNO"));
			$("#ISPREOCCUPY").val(convertSelectValue(addInfo.get("ISPREOCCUPY"), "value"));
			$("#PREREASON").val(addInfo.get("PREREASON"));
			$("#TRANSFERMODE").val(addInfo.get("TRANSFERMODE"));
			$("#ROUTEMODE").val(convertSelectValue(addInfo.get("ROUTEMODE"), "value"));
			$("#BIZSECURITYLV").val(addInfo.get("BIZSECURITYLV"));
			$("#BANDWIDTH").val(addInfo.get("BANDWIDTH"));
			$("#PORTACUSTOM").val(addInfo.get("PORTACUSTOM"));

			$("#PROVINCEA").val(addInfo.get("PROVINCEA"));
			$("#CITYA").val(addInfo.get("CITYA"));
			$("#AREAA").val(addInfo.get("AREAA"));
			$("#COUNTYA").val(addInfo.get("COUNTYA"));
			$("#VILLAGEA").val(addInfo.get("VILLAGEA"));
			$("#PORTAINTERFACETYPE").val(addInfo.get("PORTAINTERFACETYPE"));
			$("#PORTACONTACT").val(addInfo.get("PORTACONTACT"));
			$("#PORTACONTACTPHONE").val(addInfo.get("PORTACONTACTPHONE"));
			 
			$("#TRADENAME").val(addInfo.get("TRADENAME"));
		}
		//变更分场景
		else if("EDIRECTLINECHANGEPBOSS"==bpmTempletId||"DIRECTLINECHANGESIMPLE"==bpmTempletId){
			dataLinePartInit(bpmTempletId, changemode, addInfo);
		}
	}
	else if ("7016"==productId) {
		//IMS专线开通
		if ("EDIRECTLINEOPENPBOSS"==bpmTempletId) {
			$("#PRODUCTNO").val(addInfo.get("PRODUCTNO"));
			$("#TRANSFERMODE").val(addInfo.get("TRANSFERMODE"));
			$("#BIZSECURITYLV").val(addInfo.get("BIZSECURITYLV"));
			$("#BANDWIDTH").val(addInfo.get("BANDWIDTH"));
			$("#PORTACUSTOM").val(addInfo.get("PORTACUSTOM"));

			$("#PROVINCEA").val(addInfo.get("PROVINCEA"));
			$("#CITYA").val(addInfo.get("CITYA"));
			$("#AREAA").val(addInfo.get("AREAA"));
			$("#COUNTYA").val(addInfo.get("COUNTYA"));
			$("#VILLAGEA").val(addInfo.get("VILLAGEA"));
			$("#PORTAINTERFACETYPE").val(addInfo.get("PORTAINTERFACETYPE"));
			$("#PORTACONTACT").val(addInfo.get("PORTACONTACT"));
			$("#PORTACONTACTPHONE").val(addInfo.get("PORTACONTACTPHONE"));

			$("#CUSAPPSERVIPADDNUM").val(addInfo.get("CUSAPPSERVIPADDNUM"));
			$("#DOMAINNAME").val(addInfo.get("DOMAINNAME"));
			$("#MAINDOMAINADD").val(addInfo.get("MAINDOMAINADD"));
			$("#TRADENAME").val(addInfo.get("TRADENAME"));
		//勘察 开通 //勘察 变更
		} else if("ERESOURCECONFIRMZHZG"==bpmTempletId || "ECHANGERESOURCECONFIRM"==bpmTempletId){
			$("#PRODUCTNO").val(addInfo.get("PRODUCTNO"));
			$("#ISPREOCCUPY").val(convertSelectValue(addInfo.get("ISPREOCCUPY"), "value"));
			$("#PREREASON").val(addInfo.get("PREREASON"));
			$("#TRANSFERMODE").val(addInfo.get("TRANSFERMODE"));
			$("#ROUTEMODE").val(convertSelectValue(addInfo.get("ROUTEMODE"), "value"));
			$("#BIZSECURITYLV").val(addInfo.get("BIZSECURITYLV"));
			$("#BANDWIDTH").val(addInfo.get("BANDWIDTH"));
			$("#PORTACUSTOM").val(addInfo.get("PORTACUSTOM"));

			$("#PROVINCEA").val(addInfo.get("PROVINCEA"));
			$("#CITYA").val(addInfo.get("CITYA"));
			$("#AREAA").val(addInfo.get("AREAA"));
			$("#COUNTYA").val(addInfo.get("COUNTYA"));
			$("#VILLAGEA").val(addInfo.get("VILLAGEA"));
			$("#PORTAINTERFACETYPE").val(addInfo.get("PORTAINTERFACETYPE"));
			$("#PORTACONTACT").val(addInfo.get("PORTACONTACT"));
			$("#PORTACONTACTPHONE").val(addInfo.get("PORTACONTACTPHONE"));
			 
			$("#TRADENAME").val(addInfo.get("TRADENAME"));
		}
		//变更分场景
		else if("EDIRECTLINECHANGEPBOSS"==bpmTempletId||"DIRECTLINECHANGESIMPLE"==bpmTempletId){
			dataLinePartInit(bpmTempletId, changemode, addInfo);
		}
	}
	//显示修改区域
	$("#CheckPart").show();
 	$("#OpenSubmit").show();
}

function submitState() {
    debugger;
    var attrData = new Wade.DataMap();

    var bpmTempletId = $("#BPM_TEMPLET_ID").val(); //流程名
    var productId = $("#PRODUCT_ID").val(); //产品Id
    var changemode = $("#CHANGEMODE").val(); //变更场景
    if ("7012"==productId||"70121" == productId||"70122" == productId) {
		if("ERESOURCECONFIRMZHZG"==bpmTempletId || "ECHANGERESOURCECONFIRM"==bpmTempletId){
 			attrData.put("ISPREOCCUPY",convertSelectValue($("#ISPREOCCUPY").val(), "ISPREOCCUPY"));
			attrData.put("PREREASON",$("#PREREASON").val());
			attrData.put("TRANSFERMODE",$("#TRANSFERMODE").val());
		}
		attrData.put("PRODUCTNO",$("#PRODUCTNO").val());
		attrData.put("ROUTEMODE",convertSelectValue($("#ROUTEMODE").val(), "ROUTEMODE"));
		attrData.put("BIZSECURITYLV",$("#BIZSECURITYLV").val());
		attrData.put("BANDWIDTH",$("#BANDWIDTH").val());
		attrData.put("PORTACUSTOM",$("#PORTACUSTOM").val());
		attrData.put("PROVINCEA",$("#PROVINCEA").val());
		attrData.put("CITYA",$("#CITYA").val());
		attrData.put("AREAA",$("#AREAA").val());
		attrData.put("COUNTYA",$("#COUNTYA").val());
		attrData.put("VILLAGEA",$("#VILLAGEA").val());
		attrData.put("PORTAINTERFACETYPE",$("#PORTAINTERFACETYPE").val());
		attrData.put("PORTACONTACT",$("#PORTACONTACT").val());
		attrData.put("PORTACONTACTPHONE",$("#PORTACONTACTPHONE").val());
		attrData.put("PORTZCUSTOM",$("#PORTZCUSTOM").val());
		attrData.put("PROVINCEZ",$("#PROVINCEZ").val());
		attrData.put("CITYZ",$("#CITYZ").val());
		attrData.put("AREAZ",$("#AREAZ").val());
		attrData.put("COUNTYZ",$("#COUNTYZ").val());
		attrData.put("VILLAGEZ",$("#VILLAGEZ").val());
		attrData.put("PORTZINTERFACETYPE",$("#PORTZINTERFACETYPE").val());
		attrData.put("PORTZCONTACT",$("#PORTZCONTACT").val());
		attrData.put("PORTZCONTACTPHONE",$("#PORTZCONTACTPHONE").val());
		attrData.put("TRADENAME",$("#TRADENAME").val());	 
	} else if ("7011"==productId||"70111" == productId||"70112" == productId) {
		attrData.put("PRODUCTNO",$("#PRODUCTNO").val());
		attrData.put("TRANSFERMODE",$("#TRANSFERMODE").val());
		attrData.put("BIZSECURITYLV",$("#BIZSECURITYLV").val());
		attrData.put("BANDWIDTH",$("#BANDWIDTH").val());
		attrData.put("PORTACUSTOM",$("#PORTACUSTOM").val());
		attrData.put("PROVINCEA",$("#PROVINCEA").val());
		attrData.put("CITYA",$("#CITYA").val());
		attrData.put("AREAA",$("#AREAA").val());
		attrData.put("COUNTYA",$("#COUNTYA").val());
		attrData.put("VILLAGEA",$("#VILLAGEA").val());
		attrData.put("PORTAINTERFACETYPE",$("#PORTAINTERFACETYPE").val());
		attrData.put("PORTACONTACT",$("#PORTACONTACT").val());
		attrData.put("PORTACONTACTPHONE",$("#PORTACONTACTPHONE").val());
		attrData.put("TRADENAME",$("#TRADENAME").val());
		//互联网专线开通
		if ("EDIRECTLINEOPENPBOSS"==bpmTempletId) {
     		attrData.put("CUSAPPSERVIPADDNUM",$("#CUSAPPSERVIPADDNUM").val());
     		attrData.put("DOMAINNAME",$("#DOMAINNAME").val());
     		attrData.put("MAINDOMAINADD",$("#MAINDOMAINADD").val());
  		//勘察 开通 //勘察 变更
		} else if("ERESOURCECONFIRMZHZG"==bpmTempletId || "ECHANGERESOURCECONFIRM"==bpmTempletId){
 			attrData.put("ISPREOCCUPY", convertSelectValue($("#ISPREOCCUPY").val(), "ISPREOCCUPY"));	
   			attrData.put("ROUTEMODE", convertSelectValue($("#ROUTEMODE").val(), "ROUTEMODE"));	
   			attrData.put("PREREASON",$("#PREREASON").val());	
 		}
		//变更分场景
		else if("EDIRECTLINECHANGEPBOSS"==bpmTempletId||"DIRECTLINECHANGESIMPLE"==bpmTempletId){
    		attrData.put("IPTYPE",$("#IPTYPE").val());	
    		attrData.put("CUSAPPSERVIPADDNUM",$("#CUSAPPSERVIPADDNUM").val());	
    		attrData.put("CUSAPPSERVIPV4ADDNUM",$("#CUSAPPSERVIPV4ADDNUM").val());	
    		attrData.put("CUSAPPSERVIPV6ADDNUM",$("#CUSAPPSERVIPV6ADDNUM").val());	
    		attrData.put("IPCHANGE",$("#IPCHANGE").val());	
    		attrData.put("DOMAINNAME",$("#DOMAINNAME").val());	
    		attrData.put("MAINDOMAINADD",$("#MAINDOMAINADD").val());	
 			 
 		}
	} else if ("7016"==productId) {
		attrData.put("PRODUCTNO",$("#PRODUCTNO").val());
		attrData.put("TRANSFERMODE",$("#TRANSFERMODE").val());
		attrData.put("BIZSECURITYLV",$("#BIZSECURITYLV").val());
		attrData.put("BANDWIDTH",$("#BANDWIDTH").val());
		attrData.put("PORTACUSTOM",$("#PORTACUSTOM").val());
		attrData.put("PROVINCEA",$("#PROVINCEA").val());
		attrData.put("CITYA",$("#CITYA").val());
		attrData.put("AREAA",$("#AREAA").val());
		attrData.put("COUNTYA",$("#COUNTYA").val());
		attrData.put("VILLAGEA",$("#VILLAGEA").val());
		attrData.put("PORTAINTERFACETYPE",$("#PORTAINTERFACETYPE").val());
		attrData.put("PORTACONTACT",$("#PORTACONTACT").val());
		attrData.put("PORTACONTACTPHONE",$("#PORTACONTACTPHONE").val());
		attrData.put("TRADENAME",$("#TRADENAME").val());
		//ims专线开通
		if ("EDIRECTLINEOPENPBOSS"==bpmTempletId) {
     		attrData.put("CUSAPPSERVIPADDNUM",$("#CUSAPPSERVIPADDNUM").val());
     		attrData.put("DOMAINNAME",$("#DOMAINNAME").val());
     		attrData.put("MAINDOMAINADD",$("#MAINDOMAINADD").val());
  		//勘察 开通 //勘察 变更
		} else if("ERESOURCECONFIRMZHZG"==bpmTempletId || "ECHANGERESOURCECONFIRM"==bpmTempletId){
 			attrData.put("ISPREOCCUPY", convertSelectValue($("#ISPREOCCUPY").val(), "ISPREOCCUPY"));	
   			attrData.put("ROUTEMODE", convertSelectValue($("#ROUTEMODE").val(), "ROUTEMODE"));	
   			attrData.put("PREREASON",$("#PREREASON").val());	
 		}
		//变更分场景
		else if("EDIRECTLINECHANGEPBOSS"==bpmTempletId||"DIRECTLINECHANGESIMPLE"==bpmTempletId){
//    		attrData.put("IPTYPE",$("#IPTYPE").val());	
    		attrData.put("CUSAPPSERVIPADDNUM",$("#CUSAPPSERVIPADDNUM").val());	
//    		attrData.put("CUSAPPSERVIPV4ADDNUM",$("#CUSAPPSERVIPV4ADDNUM").val());	
//    		attrData.put("CUSAPPSERVIPV6ADDNUM",$("#CUSAPPSERVIPV6ADDNUM").val());	
    		attrData.put("IPCHANGE",$("#IPCHANGE").val());	
    		attrData.put("DOMAINNAME",$("#DOMAINNAME").val());	
    		attrData.put("MAINDOMAINADD",$("#MAINDOMAINADD").val());	
 			 
 		}
	}
    myTable.updateRow($.parseJSON(attrData.toString()), myTable.selected);
    //修改区域修改或者导出导入修改都设置为已修改状态
    flag = "true";
    $("#CheckPart").hide();
    $("#OpenSubmit").hide();
}

function convertSelectValue(value, type){
	debugger;
	if (type =='value')
	return value == '单节点单路由' ? '0' : 
		   value == '单节点双路由' ? '1' :
		   value == '双节点双路由' ? '2' :
		   value == '否' ? '0' :
		   value == '是' ?'1' : '';
	if (type =='ISPREOCCUPY')
	return value == '0' ? '否' : 
		   value == '1' ? '是' :'';
	if (type =='ROUTEMODE')
	return value == '0' ? '单节点单路由' : 
		   value == '1' ? '单节点双路由' :
		   value == '2' ? '双节点双路由':'';	   
}

function internetLinePartInit(bpmTempletId, changemode, rowData) {
    $("#PRODUCTNO").val(rowData.get("PRODUCTNO"));

    if (changemode == "扩容") {
        $("#ROUTEMODE").attr("disabled", true);
        $("#BIZSECURITYLV").attr("disabled", true);
//        $("#BANDWIDTH").attr("disabled", true);
        $("#PORTACUSTOM").attr("disabled", true);
        $("#PROVINCEA").attr("disabled", true);
        $("#CITYA").attr("disabled", true);
        $("#AREAA").attr("disabled", true);
        $("#COUNTYA").attr("disabled", true);
        $("#VILLAGEA").attr("disabled", true);
        $("#PORTAINTERFACETYPE").attr("disabled", true);
        $("#PORTACONTACT").attr("disabled", true);
        $("#PORTACONTACTPHONE").attr("disabled", true);
        $("#PORTZCUSTOM").attr("disabled", true);
        $("#PROVINCEZ").attr("disabled", true);
        $("#CITYZ").attr("disabled", true);
        $("#AREAZ").attr("disabled", true);
        $("#COUNTYZ").attr("disabled", true);
        $("#VILLAGEZ").attr("disabled", true);
        $("#PORTZINTERFACETYPE").attr("disabled", true);
        $("#PORTZCONTACT").attr("disabled", true);
        $("#PORTZCONTACTPHONE").attr("disabled", true);
        $("#TRADENAME").attr("disabled", true);
    } else if (changemode == "异楼搬迁") {
    	$("#BANDWIDTH").attr("disabled", true);
    	$("#PROVINCEA").attr("disabled", true);
    	$("#PROVINCEZ").attr("disabled", true);
    } else if (changemode == "同楼搬迁") {
    	$("#BIZSECURITYLV").attr("disabled", true);
    	$("#BANDWIDTH").attr("disabled", true);
    	$("#PROVINCEA").attr("disabled", true);
    	$("#PROVINCEZ").attr("disabled", true);
    } else if (changemode == "业务保障级别调整") {
    	$("#BANDWIDTH").attr("disabled", true);
        $("#BANDWIDTH").attr("disabled", true);
    	$("#PORTACUSTOM").attr("disabled", true);
    	$("#PROVINCEA").attr("disabled", true);
    	$("#CITYA").attr("disabled", true);
    	$("#AREAA").attr("disabled", true);
    	$("#COUNTYA").attr("disabled", true);
    	$("#VILLAGEA").attr("disabled", true);
    	$("#PORTAINTERFACETYPE").attr("disabled", true);
    	$("#PORTACONTACT").attr("disabled", true);
    	$("#PORTACONTACTPHONE").attr("disabled", true);
    	$("#PORTZCUSTOM").attr("disabled", true);
    	$("#PROVINCEZ").attr("disabled", true);
    	$("#CITYZ").attr("disabled", true);
    	$("#AREAZ").attr("disabled", true);
    	$("#COUNTYZ").attr("disabled", true);
    	$("#VILLAGEZ").attr("disabled", true);
    	$("#PORTZINTERFACETYPE").attr("disabled", true);
    	$("#PORTZCONTACT").attr("disabled", true);
    	$("#PORTZCONTACTPHONE").attr("disabled", true);
    	$("#TRADENAME").attr("disabled", true);
		$("#ROUTEMODE").attr("disabled", true);
        $("#BIZSECURITYLV").attr("disabled", true);
    } else if (changemode == "减容") {
    	$("#ROUTEMODE").attr("disabled", true);
    	$("#BIZSECURITYLV").attr("disabled", true);
    	$("#BANDWIDTH").attr("disabled", true);
    	$("#PORTACUSTOM").attr("disabled", true);
    	$("#PROVINCEA").attr("disabled", true);
    	$("#CITYA").attr("disabled", true);
    	$("#AREAA").attr("disabled", true);
    	$("#COUNTYA").attr("disabled", true);
    	$("#VILLAGEA").attr("disabled", true);
    	$("#PORTAINTERFACETYPE").attr("disabled", true);
    	$("#PORTACONTACT").attr("disabled", true);
    	$("#PORTACONTACTPHONE").attr("disabled", true);
    	$("#PORTZCUSTOM").attr("disabled", true);
    	$("#PROVINCEZ").attr("disabled", true);
    	$("#CITYZ").attr("disabled", true);
    	$("#AREAZ").attr("disabled", true);
    	$("#COUNTYZ").attr("disabled", true);
    	$("#VILLAGEZ").attr("disabled", true);
    	$("#PORTZINTERFACETYPE").attr("disabled", true);
    	$("#PORTZCONTACT").attr("disabled", true);
    	$("#PORTZCONTACTPHONE").attr("disabled", true);
    	$("#TRADENAME").attr("disabled", true);
    } 

}

function dataLinePartInit(bpmTempletId, changemode, rowData){
	    $("#PRODUCTNO").val(rowData.get("PRODUCTNO"));
	    $("#TRANSFERMODE").val(rowData.get("TRANSFERMODE"));
	    $("#BIZSECURITYLV").val(rowData.get("BIZSECURITYLV"));
	    $("#BANDWIDTH").val(rowData.get("BANDWIDTH"));
	    $("#PORTACUSTOM").val(rowData.get("PORTACUSTOM"));

	    $("#PROVINCEA").val(rowData.get("PROVINCEA"));
	    $("#CITYA").val(rowData.get("CITYA"));
	    $("#AREAA").val(rowData.get("AREAA"));
	    $("#COUNTYA").val(rowData.get("COUNTYA"));
	    $("#VILLAGEA").val(rowData.get("VILLAGEA"));
	    $("#PORTAINTERFACETYPE").val(rowData.get("PORTAINTERFACETYPE"));
	    $("#PORTACONTACT").val(rowData.get("PORTACONTACT"));
	    $("#PORTACONTACTPHONE").val(rowData.get("PORTACONTACTPHONE"));
	     
	    $("#IPTYPE").val(rowData.get("IPTYPE"));
	    $("#CUSAPPSERVIPADDNUM").val(rowData.get("CUSAPPSERVIPADDNUM"));
	    $("#CUSAPPSERVIPV4ADDNUM").val(rowData.get("CUSAPPSERVIPV4ADDNUM"));
	    $("#CUSAPPSERVIPV6ADDNUM").val(rowData.get("CUSAPPSERVIPV6ADDNUM"));
	    $("#IPCHANGE").val(rowData.get("IPCHANGE"));
	    $("#DOMAINNAME").val(rowData.get("DOMAINNAME"));
	    $("#MAINDOMAINADD").val(rowData.get("MAINDOMAINADD"));
	    $("#TRADENAME").val(rowData.get("TRADENAME"));
	    if (changemode == "扩容" || changemode == "减容") {
	         $("#TRANSFERMODE").attr("disabled", true);
	         $("#BIZSECURITYLV").attr("disabled", true);
	         
	         $("#PORTACUSTOM").attr("disabled", true);
	         $("#PROVINCEA").attr("disabled", true);
	         $("#CITYA").attr("disabled", true);
	         $("#AREAA").attr("disabled", true);
	         $("#COUNTYA").attr("disabled", true);
	         $("#VILLAGEA").attr("disabled", true);
	         $("#PORTAINTERFACETYPE").attr("disabled", true);
	         $("#PORTACONTACT").attr("disabled", true);
	         $("#PORTACONTACTPHONE").attr("disabled", true);
	         $("#IPTYPE").attr("disabled", true);
	         $("#CUSAPPSERVIPADDNUM").attr("disabled", true);
	         $("#CUSAPPSERVIPV4ADDNUM").attr("disabled", true);
	         $("#CUSAPPSERVIPV6ADDNUM").attr("disabled", true);
	         $("#IPCHANGE").attr("disabled", true);
	         $("#DOMAINNAME").attr("disabled", true);
	         $("#MAINDOMAINADD").attr("disabled", true);
	         $("#TRADENAME").attr("disabled", true);
	        
	    } else if (changemode == "同楼搬迁") {
	    	 $("#BIZSECURITYLV").attr("disabled", true);
	    	  $("#PROVINCEA").attr("disabled", true);
	    	  $("#BANDWIDTH").attr("disabled", true);
	    } else if (changemode == "异楼搬迁") {
	    	  $("#PROVINCEA").attr("disabled", true);
	    	  $("#BANDWIDTH").attr("disabled", true);
	    } else if (changemode == "业务保障级别调整") {
	    	 $("#TRANSFERMODE").attr("disabled", true);
 	         $("#BANDWIDTH").attr("disabled", true);
	         $("#PORTACUSTOM").attr("disabled", true);
	         $("#PROVINCEA").attr("disabled", true);
	         $("#CITYA").attr("disabled", true);
	         $("#AREAA").attr("disabled", true);
	         $("#COUNTYA").attr("disabled", true);
	         $("#VILLAGEA").attr("disabled", true);
	         $("#PORTAINTERFACETYPE").attr("disabled", true);
	         $("#PORTACONTACT").attr("disabled", true);
	         $("#PORTACONTACTPHONE").attr("disabled", true);
	         $("#IPTYPE").attr("disabled", true);
	         $("#CUSAPPSERVIPADDNUM").attr("disabled", true);
	         $("#CUSAPPSERVIPV4ADDNUM").attr("disabled", true);
	         $("#CUSAPPSERVIPV6ADDNUM").attr("disabled", true);
	         $("#IPCHANGE").attr("disabled", true);
	         $("#DOMAINNAME").attr("disabled", true);
	         $("#MAINDOMAINADD").attr("disabled", true);
	         $("#TRADENAME").attr("disabled", true);
	    } else if (changemode == "IP地址调整") {
	    	 $("#TRANSFERMODE").attr("disabled", true);
 	         $("#BANDWIDTH").attr("disabled", true);
	         $("#PORTACUSTOM").attr("disabled", true);
	         $("#PROVINCEA").attr("disabled", true);
	         $("#CITYA").attr("disabled", true);
	         $("#AREAA").attr("disabled", true);
	         $("#COUNTYA").attr("disabled", true);
	         $("#VILLAGEA").attr("disabled", true);
	         $("#PORTAINTERFACETYPE").attr("disabled", true);
	         $("#PORTACONTACT").attr("disabled", true);
	         $("#PORTACONTACTPHONE").attr("disabled", true);
 	         $("#TRADENAME").attr("disabled", true);
	    }
}
