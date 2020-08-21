function insertProductCtrlInfo(data){
	if(data != null){
		$('#PRODUCTCTRL_TRADE_TYPE_CODE').val(data.get('TradeTypeCode',''));
		$('#PRODUCTCTRL_BOOKING_FLAG').val(data.get('BookingFlag',''));
		$('#PRODUCTCTRL_ADD_MEBUCA_FLAG').val(data.get('AddMebUcaFlag',''));
		$('#PRODUCTCTRL_PARAM_INFO').val(data.get('ParamInfo',''));
		
	}
	
}

function cleanProductCtrlInfo(){
	
	$('#PRODUCTCTRL_TRADE_TYPE_CODE').val('');
	$('#PRODUCTCTRL_BOOKING_FLAG').val('');
	$('#PRODUCTCTRL_ADD_MEBUCA_FLAG').val('');
	$('#PRODUCTCTRL_PARAM_INFO').val('');
	
}


function getAddMebUcaFlag(){
	return $('#PRODUCTCTRL_ADD_MEBUCA_FLAG').val();	
}


function getProductParamPage(){
	return $('#PRODUCTCTRL_PARAM_INFO').val();	
}

function renderProductCtrlInfo(productId, busiType){
	var result = true;
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.productctrlinfo.ProductCtrlInfoHttpHandler','getProductCtrlInfoByProductIdAndBusiType','&GRP_PRODUCT_ID='+productId+'&BUSI_TYPE='+busiType,
	function(data){
		insertProductCtrlInfo(data);
	},
	function(error_code,error_info,derror){
		cleanProductCtrlInfo();
	    result = false;
		showDetailErrorInfo(error_code,error_info,derror);
	},{async:false});
    return result;
}