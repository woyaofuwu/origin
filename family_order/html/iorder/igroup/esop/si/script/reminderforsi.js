function queryInfos(obj)
{
	if(!$.validate.verifyAll("CondPart")){
		return false;
	}
	var ibsysid = $("#cond_IBSYSID").val();
	var busiType = $("#cond_BUSI_TYPE").val();
	var productId = $("#cond_PRODUCT_ID").val();
	var groupId = $("#cond_G_GROUP_ID").val();
	var custName = $("#cond_G_CUST_NAME").val();
	var beginDate = $("#cond_BEGIN_DATE").val();
	var endDate = $("#cond_END_DATE").val();
	
	var data = "&IBSYSID=" + ibsysid + "&BUSI_TYPE=" + busiType + "&PRODUCT_ID=" + productId + "&GROUP_ID=" + groupId  + "&BEGIN_DATE=" + beginDate  + "&END_DATE=" + endDate;
	
	$.beginPageLoading('正在查询业务信息...');
	$.ajax.submit(null,'queryInfos',data,'qryPart',function(data){
		$.endPageLoading();
		hidePopup(obj);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function queryGroupInfos(obj)
{
	var groupId = $(obj).html();
	debugger;
	$.beginPageLoading('正在查询业务信息...');
	$.ajax.submit(null,'',"&GROUP_ID="+groupId,'qryGroupItem',function(data){
		$.endPageLoading();
		showPopup('groupPopup','qryGroupItem', true);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function submit()
{
	if(!$.validate.verifyAll("ConfPart")){
		return false;
	}
	var dataStr = "";
	
	dataStr = dataStr + "&SUB_IBSYSID=" + $("#SUB_IBSYSID").val();
	dataStr = dataStr + "&BUSIFORM_ID=" + $("#BUSIFORM_ID").val();
	dataStr = dataStr + "&IBSYSID=" + $("#IBSYSID").val();
	dataStr = dataStr + "&RECORD_NUM=" + $("#RECORD_NUM").val();
	dataStr = dataStr + "&OPER_TYPE=" + $("#OPER_TYPE").val();
	dataStr = dataStr + "&OPCONTACT=" + $("#OPCONTACT").val();
	dataStr = dataStr + "&OPPERSON=" + $("#OPPERSON").val();
	dataStr = dataStr + "&OPDERAT=" + $("#OPDERAT").val();
	dataStr = dataStr + "&OPDESC=" + $("#OPDESC").val();
	dataStr = dataStr + "&SERIALNO=" + $("#SERIALNO").val();
	
	$.beginPageLoading('正在提交业务信息...');
	ajaxSubmit(this,'submit',dataStr,"",function(data){
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});	
}