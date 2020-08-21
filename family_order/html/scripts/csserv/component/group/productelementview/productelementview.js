//生成集团元素列表信息
function insertProductElementViewList(productElements,viewid){
	cleanProductElementViewPart(viewid);
	var productElementViewBody = viewid+"_TBODY";
	productElements.each(function(item,idx){
		
		$("#"+productElementViewBody).prepend(makeProductElementViewHtml(item));
			
	});
	
}
//生成集团元素列表信息
function makeProductElementViewHtml(item){

	var showFiledStr = $('#PRODUCTELEMENTS_VIEW_SHOW_FILEDS').val();
	var html="";
	html += '<tr>';
	var showFiledS = showFiledStr.split(',');
	for( var i = 0; i< showFiledS.length; i++){
		var showFiledColumn = showFiledS[i];
		var showFiledValue = item.get(showFiledColumn);
		html += '<td class="e_center">' + showFiledValue+ '</td>';
	}
	html += '</tr>';
	
	return html;
}


function renderProductElementsView(userId, userIdA, productId, eparchyCode,viewId){
	var result = true;
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.product.productelementview.ProductElementViewHttpHandler','renderProductElementsList','&USER_ID='+userId+'&USER_ID_A='+userIdA+'&PRODUCT_ID='+productId+'&EPARCHY_CODE='+eparchyCode,
	function(data){
		insertProductElementViewList(data,viewId);
	},
	function(error_code,error_info,derror){
	    result = false;
		showDetailErrorInfo(error_code,error_info,derror);
		$.endPageLoading();
		
    },{async:false});
    
    return result;
}

function cleanProductElementViewPart(viewId){
	$("#"+viewId+"_TBODY").html('');
	
}
