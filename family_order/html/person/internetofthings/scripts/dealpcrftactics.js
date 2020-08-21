function refreshPartAtferAuth(data)
{
	$.beginPageLoading("查询中。。。");
	$("#USER_ID").val(data.get("USER_INFO").get("USER_ID"));
	var reqStr = "&USER_ID="+data.get("USER_INFO").get("USER_ID")+"&SERIAL_NUMBER="+data.get("USER_INFO").get("SERIAL_NUMBER");
	$.ajax.submit('AuthPart', 'getUserPcrfs', reqStr, 'PcrfListPart', function(data){
		$("#EditPcrfPart").removeClass("e_dis");
		$.endPageLoading(); 
	},
	function(error_code,error_info){
		$.endPageLoading(); 
		alert(error_info);
    });
}


function createPcrfReq() {	
	
	var flag ="0";
	var ServiceCode = $("#pam_ServiceCode").val();
	var ServiceUsageState = $("#pam_ServiceUsageState").val();
	var BillingType = $("#pam_ServiceBillingType").val();
	var modifyTag = $("#pam_MODIFY_TAG").val();
	if (!$.validate.verifyAll("EditPart")) {
		return false;
	}

	//获取编辑区的数据
    var custEdit = $.ajax.buildJsonData("EditPart");
	var deptTable=$.table.get("PcrfReqTable").getTableData(null,true);	
	var queryPcrfInfoTable =$.table.get("UserPcrfsTable").getTableData(null,true);
	if(flag == "0"){	
		if(modifyTag  == "0"){  //新增
			for(var i=0;i<queryPcrfInfoTable.length;i++){
				var pamServiceCode = queryPcrfInfoTable.get(i).get("SERVICE_CODE");
				if(pamServiceCode == ServiceCode){
					alert("该策略对应的业务订购唯一编码（ServiceCode："+pamServiceCode+"）已经存在，不能重复新增！");
					return false;
				}
			}
		}
		for(var i=0;i<deptTable.length;i++){
			var pamServiceCode = deptTable.get(i).get("SERVICE_CODE");
			//alert(pamServiceCode);
			if(pamServiceCode == ServiceCode){
				alert("该策略对应的业务订购唯一编码（ServiceCode："+pamServiceCode+"）已经存在，不能重复操作！");
				return false;
			}
		}
		//新增成员
		custEdit["SERVICE_ID"] = $("#pam_ServiceId").val();
		custEdit["SERVICE_NAME"] = $("#pam_ServiceName").val();
		custEdit["RELA_INST_ID"] = $("#pam_RELA_INST_ID").val();
		custEdit["INST_ID"] = $("#pam_INST_ID").val();
		custEdit["SERVICE_CODE"] = ServiceCode;
		custEdit["USAGE_STATE_text"] = $("#pam_ServiceUsageState option:selected").text();
		custEdit["USAGE_STATE"] =$("#pam_ServiceUsageState option:selected").val();
		custEdit["BILLING_TYPE_text"] = $("#pam_ServiceBillingType option:selected").text();
		custEdit["BILLING_TYPE"] =$("#pam_ServiceBillingType option:selected").val();
		custEdit["START_DATE"] = $("#pam_ServiceStartDateTime").val();
		custEdit["END_DATE"] = $("#pam_ServiceEndDateTime").val();
//		if($("#pam_MODIFY_TAG").val() == ""){
//			custEdit["MODIFY_TAG"] = "0";
//			custEdit["MODIFY_TAG_text"] = "新增";
//		}else{
			custEdit["MODIFY_TAG"] = $("#pam_MODIFY_TAG").val();
			custEdit["MODIFY_TAG_text"] = $("#pam_MODIFY_TAG_text").val();
//		}
		
	    $.table.get("PcrfReqTable").addRow(custEdit);
	}
	cleanPcrfReq();	
	$("#pam_ServiceCode").attr("disabled","");
	$("#pam_ServiceId").attr("disabled","");
}

function tableRowClick() {
//获取选择行的数据
	delAble =true;
}

function deletePcrfReq(){
	var rowData = $.table.get("PcrfReqTable").getRowData();
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	$.table.get("PcrfReqTable").deleteRow();	

   cleanPcrfReq();
}

function cleanPcrfReq(){
	
	$("#pam_ServiceCode").val("");
	$("#pam_ServiceUsageState").val("");	
	$("#pam_ServiceBillingType").val("");
	$("#pam_ServiceId").val("");
	$("#pam_ServiceName").val("");
	$("#pam_RELA_INST_ID").val("");
	$("#pam_INST_ID").val("");
	$("#pam_MODIFY_TAG").val("0");
	$("#pam_MODIFY_TAG_text").val("新增");
}

function addPcrfinfo(){	
	cleanPcrfReq();
	$("#pam_MODIFY_TAG").val("0");
	$("#pam_MODIFY_TAG_text").val("新增");
	$("#pam_ServiceCode").attr("disabled","");
	$("#pam_ServiceId").attr("disabled","");
}

function editPcrfInfo(){	
	var rowData = $.table.get("UserPcrfsTable").getRowData();
	
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行修改操作！");
		return false;
	}
	cleanPcrfReq();
	$("#pam_ServiceCode").attr("disabled",true);
	$("#pam_ServiceId").attr("disabled",true);
	$("#pam_ServiceCode").val(rowData.get("SERVICE_CODE"));
	$("#pam_ServiceUsageState").val(rowData.get("USAGE_STATE"));	
	$("#pam_ServiceBillingType").val(rowData.get("BILLING_TYPE"));	
	$("#pam_ServiceId").val(rowData.get("SERVICE_ID"));	
	$("#pam_ServiceName").val(rowData.get("SERVICE_NAME"));	
	$("#pam_RELA_INST_ID").val(rowData.get("RELA_INST_ID"));
	$("#pam_INST_ID").val(rowData.get("INST_ID"));
	$("#pam_MODIFY_TAG").val("2");
	$("#pam_MODIFY_TAG_text").val("变更");
}

function deletPcrfInfo(){
	var rowData = $.table.get("UserPcrfsTable").getRowData();
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行修改操作！");
		return false;
	}
	cleanPcrfReq();
	$("#pam_ServiceCode").attr("disabled",true);
	$("#pam_ServiceCode").val(rowData.get("SERVICE_CODE"));
	$("#pam_ServiceUsageState").val(rowData.get("USAGE_STATE"));	
	$("#pam_ServiceBillingType").val(rowData.get("BILLING_TYPE"));
	$("#pam_ServiceId").val(rowData.get("SERVICE_ID"));	
	$("#pam_ServiceName").val(rowData.get("SERVICE_NAME"));	
	$("#pam_RELA_INST_ID").val(rowData.get("RELA_INST_ID"));	
	$("#pam_INST_ID").val(rowData.get("INST_ID"));
	$("#pam_MODIFY_TAG").val("1");
	$("#pam_MODIFY_TAG_text").val("删除");
	
	 createPcrfReq();
}

function queryUserSvc(){
	$.beginPageLoading("查询中。。。");
	$.ajax.submit('AuthPart,HiddenPart', 'getUserSvcs', '', 'SvcListPart', function(data){
		$("#DetailPopup").css('display','');
		$.endPageLoading(); 
	},
	function(error_code,error_info){
		$.endPageLoading(); 
		alert(error_info);
    });
}

function selectSvc(obj){
	
	var instID = $(obj).val();
	var svcNo = $(obj).attr('svcNo');
	var svcName = $(obj).attr('svcName');

	$("#pam_RELA_INST_ID").val(instID);
    $("#pam_ServiceId").val(svcNo);
    $("#pam_ServiceName").val(svcName);

	$('#DetailPopup').css('display','none');
}

function submitPcrfReqs(obj) {
	var pcrfReqTable=$.table.get("PcrfReqTable").getTableData(null,true);
	$("#X_BATPCRFREQ_STR").val(pcrfReqTable); 
	if($('#X_BATPCRFREQ_STR').val()=="[]" || $('#X_BATPCRFREQ_STR').val()=="" )
		{
		   MessageBox.alert("提示","您没有进行任何操作，不能提交");
		   return false;
		}
		
		return true;
}
function changeSelect(){
	
}