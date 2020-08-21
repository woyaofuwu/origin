//生成集团用户列表信息
function insertGrpUserList(grpUserList){
	cleanGrpUserList();
	if(grpUserList instanceof $.DatasetList){
		grpUserList.each(function(item,idx){
			
			$("#grpuserlist_tbody").prepend(makeGrpUserHtml(item));
				
		});
	}else if(grpUserList instanceof $.DataMap){
		$("#grpuserlist_tbody").prepend(makeGrpUserHtml(grpUserList));
	}
}
//生成集团用户列表信息
function makeGrpUserHtml(item){
	var userId = item.get('USER_ID');
	var sn = item.get('SERIAL_NUMBER');
	var openDate = item.get('OPEN_DATE');
	var clickAction = $('#GRPUSERLIST_CLICK_ACTION').val();
	var ifHasCheck = $('#GRPUSERLIST_IF_HAS_CHECK').val();
	var html="";
	html += '<tr>';
	if(ifHasCheck != 'false'){
		var productId = item.get('PRODUCT_ID');
		var eparchycode = item.get('EPARCHY_CODE');
		var checked = item.get('CHECKED');
		if(checked == "true"){
			checked=' checked="true" ';
		}else{
			checked=" ";
		}
			
		html += '<td class="e_center"> <input id="usercheck" name="usercheck"  type="radio" value="' + productId+ '" onclick="'+clickAction+ '" sn="'+sn+ '" eparchycode="'+eparchycode+ '"'+checked+ ' userida="'+userId+'"/></td>';
	}
	html += '<td >' + userId+ '</td>';
	html += '<td >' + sn+ '</td>';
	html += '<td >' + openDate+ '</td>';
	html += '</tr>';
	
	return html;
}
//清空集团用户列表
function cleanGrpUserList(){
	$("#grpuserlist_tbody").html('');
	if($('#GRPUSERLIST_IF_HAS_CHECK').val() != 'false'){
		cleanSelGroupUserInfo();
	}
}

//生成集团用户信息
function insertSelGroupUserInfo(selUserData){
	$('#GRP_SN').val(selUserData.get('SERIAL_NUMBER'));
	$('#GRP_USER_ID').val(selUserData.get('USER_ID'));
	$('#GRP_USER_EPARCHYCODE').val(selUserData.get('EPARCHY_CODE'));
	$('#GRP_PRODUCT_ID').val(selUserData.get('PRODUCT_ID'));
	$('#GRP_PRODUCT_NAME').val(selUserData.get('PRODUCT_NAME'));
}

//清空用户区域
function cleanSelGroupUserInfo(){
	$('#GRP_SN').val('');
	$('#GRP_USER_ID').val('');
	$('#PRODUCT_ID').val('');
	$('#GRP_PRODUCT_NAME').val('');
	$('#GRP_USER_EPARCHYCODE').val('');
}


//选中集团用户列表某个用户后，做基本赋值
function chooseUserProductsBase(obj){

	var useid = obj.attr('userida');
	if($('#GRP_USER_ID').val() == useid){
		return false;
	}
	var sn = obj.attr('sn');
	var productid  =obj.val();
	var eparchycode = obj.attr('eparchycode');
	$('#GRP_USER_ID').val(useid); 
	$('#GRP_SN').val(sn); 
	$('#GRP_PRODUCT_ID').val(productid);
	$('#GRP_USER_EPARCHYCODE').val(eparchycode);
	
	return true;
	
}