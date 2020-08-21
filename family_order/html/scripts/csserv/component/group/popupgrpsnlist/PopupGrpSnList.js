function getMenberGroupInfo(grpsn,userId){
	if (userId == ""){
		$.showWarnMessage('查询失败！','集团服务号码不能为空');
		return false;
	}
	//存在自定义的点击方法 ，则放弃公用逻辑的执行
	var selfMethod =$('#GRPINFO_SELF_CLICKMETHOD').val();
	if(selfMethod && selfMethod != ''){
		eval(selfMethod);
		return;
	}
	var method = $("#GRPINFO_LISTENER_NAME").val();
	var busiType = $('#GRPINFO_BUSI_TYPE').val();
	var params ='&cond_GROUP_SERIAL_NUMBER='+grpsn+'&GRP_USER_ID='+userId+'&BUSI_TYPE='+busiType;
	if(method == null || method ==''){
		getMenberGroupInfoHttpHandler(params);
	}else{
		getMenberGroupInfoListener(method,params);
	}
	
}

function getMenberGroupInfoListener(method,params){
	
	$.beginPageLoading();
	var refreshpart = $('#GRPINFO_REFRESH_PART').val();
	$.ajax.submit('',method,params,refreshpart,
	function(data){
   		$.endPageLoading();
   		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){
    		
    		$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		} 
   		var successJsMethod = $('#GRPINFO_SUCCESS_JSMETHOD').val();
		if(successJsMethod != ''){
			eval(successJsMethod);
		}
		
   		 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		var errorJsMethod = $('#GRPINFO_ERROR_JSMETHOD').val();
		if(errorJsMethod != ''){
			eval(errorJsMethod);
		}
		
		showDetailErrorInfo(error_code,error_info,derror);
    });
	
		
}

function getMenberGroupInfoHttpHandler(params){
	
	$.beginPageLoading();
	var methodName = $('#GRPINFO_METHOD_NAME').val();
	if(methodName == null || methodName =='')
		methodName ="getGroupBaseInfo";	
	var clazzName = $('#GRPINFO_CLAZZ_NAME').val();
	if(clazzName == null || clazzName =='')
		clazzName = 'com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.popupgrpsnlist.PopupGrpSnListHttpHandler';
	Wade.httphandler.submit('',clazzName,methodName,params,
	
	function(data){
   		$.endPageLoading();
   		var resultcode = data.get('X_RESULTCODE','0');
   		if(resultcode!='0'){
    		
    		$.showWarnMessage(data.get('X_RESULTTITLE',''),data.get('X_RESULTINFO',''));
   		} 
   		var successJsMethod = $('#GRPINFO_SUCCESS_JSMETHOD').val();
		if(successJsMethod != ''){
			eval(successJsMethod);
		}
		
   		 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		var errorJsMethod = $('#GRPINFO_ERROR_JSMETHOD').val();
		if(errorJsMethod != ''){
			eval(errorJsMethod);
		}
		
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function hiddenPopupGrpList(){
	$('#grpSnList').css('display','none');
}

function delPopupGrpList(){
	$('#grpSnList').html('');
		
}


function renderGrpList(grpList){
	$('#grpSnList').css('display','');
	$("#popupGrpSnListBody").html('');
	grpList.each(function(item,idx){
		
		$("#popupGrpSnListBody").prepend(makePopupGrpListHTML(item));
			
	});
		
}

function makePopupGrpListHTML(item){
	
	var custName = item.get('CUST_NAME');
	var groupId = item.get('GROUP_ID');
	var userId = item.get('USER_ID');
	var sn = item.get('SERIAL_NUMBER');
	var productName = item.get('PRODUCT_NAME');
	var productId = item.get('PRODUCT_ID');
	var productType = item.get('PRODUCT_TYPE');
	var openDate = item.get('OPEN_DATE');
	var className = item.get('CLASS_NAME');
	var juristicTypeName = item.get('JURISTIC_TYPE_NAME');
	var juristicName = item.get('JURISTIC_NAME');
	var enterpriseTypeName = item.get('ENTERPRISE_TYPE_NAME');
	var callingTypeName = item.get('CALLING_TYPE_NAME');
	var onclickstr ="hiddenPopupGrpList();getMenberGroupInfo($(this).attr('sn'),$(this).attr('groupUserId'))";
	var html="";
	html += '<tr>';
	html += '<td >' + custName+ '</td>';
	html += '<td >' + groupId+ '</td>';
	html += '<td >' + userId+ '</td>';
	html += '<td > <a  title=' +sn+ ' sn='+sn+' groupUserId='+userId+ ' productId='+productId+' operType='+productType+' groupId='+groupId +' productName='+productName+
										' onclick="'+onclickstr+'" > <span>' + sn+ '</span></a></td>';
	html += '<td >' + productName+ '</td>';
	html += '<td >' + openDate+ '</td>';
	html += '<td >' + className+ '</td>';
	html += '<td >' + juristicTypeName+ '</td>';
	html += '<td >' + juristicName+ '</td>';
	html += '<td >' + enterpriseTypeName+ '</td>';
	html += '<td >' + callingTypeName+ '</td>';
	html += '</tr>';
	
	return html;
}