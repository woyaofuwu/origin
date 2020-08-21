function qryTransferInfos(obj){
	var param = '';
	if(!$.validate.verifyAll("qryInfo")){
		  return false;
	 }
	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('qryInfo','qryTransferInfosRecords',null,'queryPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
function staffQuery(){
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("staffForm",'qryStaffinfo',null,'staffParts',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}
function staffIdQuery(){
	$.beginPageLoading("数据查询中...");
	ajaxSubmit("staffForm2",'qryStaffIdinfo',null,'staffParts',function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function openStaffPop(obj,partId){
	forwardPopup(obj,'staffPopupItem');
	setReturnValue.partID  = partId;
}
function setReturnValue(el){
	var staffId = $(el).attr("staff_id");
	if(setReturnValue.partID){
		$("#"+setReturnValue.partID).val(staffId);
	}else{
		$("#pattr_newStaffId").val(staffId);
	}
	delete setReturnValue.partID;
	backPopup(el, "staffPopupItem", true);
}
function toUpperStr(){
	var staffId = document.getElementById("cond_StaffId");
	staffId.value = staffId.value.toUpperCase();
}