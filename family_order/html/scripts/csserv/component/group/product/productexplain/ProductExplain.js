function insertProductExplainInfo(data){

	$('#SPAN_PRODUCT_EXPLAIN_TITLE').html(data.get('PRODUCT_NAME'));
	$('#SPAN_PRODUCT_EXPLAIN_PRODUCT_ID').html(data.get('PRODUCT_ID'));
	
	var usetag = data.get('USE_TAG');
	if(usetag=='1'){
		$('#SPAN_PRODUCT_EXPLAIN_USE_TAG').html('是');
	}else{
		$('#SPAN_PRODUCT_EXPLAIN_USE_TAG').html('否');
	}
	$('#SPAN_PRODUCT_EXPLAIN_PRODUCT_EXPLAIN').html(data.get('PRODUCT_EXPLAIN'));
	$('#PRODUCT_NATURETAG').val(data.get('PRODUCT_NATURETAG'));
	$('#COMPIX_PRODUCT').val(data.get('COMPIX_PRODUCT'));
	$('#PRODUCT_IMMEDI_TAG').val(data.get('IMMEDI_TAG'));
	$('#PRODUCT_USE_TAG').val(data.get('USE_TAG'));
	
}


function cleanProductExplainInfo(){
	$('#SPAN_PRODUCT_EXPLAIN_TITLE').html('');
	$('#SPAN_PRODUCT_EXPLAIN_PRODUCT_ID').html('');
	$('#SPAN_PRODUCT_EXPLAIN_USE_TAG').html('');
	$('#SPAN_PRODUCT_EXPLAIN_PRODUCT_EXPLAIN').html('');
	$('#PRODUCT_NATURETAG').val('');
	$('#COMPIX_PRODUCT').val('');
	$('#PRODUCT_IMMEDI_TAG').val('');
	$('#PRODUCT_USE_TAG').val('');
	
}

function getProductExplainUseTag(){
	return $('#PRODUCT_USE_TAG').val();
}

function renderProductExplainInfo(productId){
	var result = true;
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.product.productexplain.ProductExplainInfoHttpHandler','getProductExplainInfoByProductId','&GRP_PRODUCT_ID='+productId,
	function(data){
		insertProductExplainInfo(data);
	},
	function(error_code,error_info,derror){
		cleanProductExplainInfo();
	    result = false;
		showDetailErrorInfo(error_code,error_info,derror);
		
    },{async:false});
    return result;
}
