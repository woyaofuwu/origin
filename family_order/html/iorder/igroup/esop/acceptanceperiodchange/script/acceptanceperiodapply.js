function qryInfos(){
	var ibsysid = $('#cond_IBSYSID').val();
	if(ibsysid == null || ibsysid == "" || ibsysid == undefined)
	{
		alert("请输入订单号!");
		return false;
	}
	
	$.beginPageLoading('正在查询订单信息...');
	$.ajax.submit(null,'queryInfosByIbsysid','&IBSYSID='+ibsysid,'GroupAndProduct,GroupInfo,ProductInfo',function(data){
		if(data.get("RESULT")=='0'){
			alert(data.get("error_message"));
		}
		$.endPageLoading();
		hidePopup(this);
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function qryProductInfo(obj){
	var productNo = $(obj).attr("productNo");
	var ibsysid = $(obj).attr("ibsysid");
	if(productNo == null || productNo == '' || productNo==undefined){
		return false;
	}
	if(ibsysid == null || ibsysid == '' || ibsysid==undefined){
		return false;
	}
	$.beginPageLoading('正在查询专线业务信息...');
	$.ajax.submit(null,'qryByIbsysidProductNo','&IBSYSID='+ibsysid+'&PRODUCTNO='+productNo,'ProductInfo',function(data){
		showPopup('qryPopup2','UI-moreProduct');
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}