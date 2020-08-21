
//初始成员归属集团退订的信息状态
function cleanExitGrpMemberBoxPart(){
	$("#exitGrpMemberBoxPart").html('');
	$("#exitGrpMemberBoxPart").css('display','none');
	
}
//显示成员归属集团退订的信息状态
function displayExitGrpMemberBoxPart(){
	$("#exitGrpMemberBoxPart").css('display','');
}

function inserExitGrpMemberBoxHtml(data){
	cleanExitGrpMemberBoxPart();
	if(data== null){
		return;
	}
	var  boxDisplayTag = data.get("HIDDEN_TAG");
	var  boxDisabledTag = data.get("DISABLED_TAG");
	var  boxCheckedTag = data.get("CEHCKED_TAG");
	var boxValue = '0';
	
	if(boxDisplayTag != 'false')
		return ;
	if(boxDisabledTag != 'true')
		boxDisabledTag = 'false';
	
	if(boxCheckedTag != 'true')
		boxCheckedTag = 'false'
	else
		boxValue = '1';
		
	var html = "";
	html += ' <li class="li"> ';
	html += ' <span class="label">成员退出归属集团</span> ';
	html += ' <span class="text" > ';
	html += ' <input type="checkbox" class="e_checkbox" name="parm_JOIN_IN_CHECK" id="parm_JOIN_IN_CHECK"   disabled="'+boxDisabledTag+'" checked="'+boxCheckedTag+'" onclick="'+exitGrpMemberBoxCheckAction(this)+'"/> ';
	html += ' <input type="text" id="parm_JOIN_IN" name="parm_JOIN_IN" value="'+boxValue+'" style="display:none;" /> ';
	html += ' </span> ';
	html += ' </span> ';
	html += ' </li> ';
	$("#exitGrpMemberBoxPart").prepend(html);
	displayExitGrpMemberBoxPart();
	
}

function renderExitGrpMemberBox(productId, mebSn, mebEparchCode){

	cleanExitGrpMemberBoxPart();
	if (productId == null || productId==''  ){ 
		$.showWarnMessage('获取产品ID出错！','ExitGrpMemberBox组件出错，获取产品ID失败！');
		return false;
	}
	
	if (mebSn == null || mebSn==''  ){ 
		$.showWarnMessage('获取成员号码出错！','ExitGrpMemberBox组件出错，获取成员号码失败！');
		return false;
	}
	
	if (mebEparchCode == null || mebEparchCode==''  ){ 
		$.showWarnMessage('获取成员地州编码出错！','ExitGrpMemberBox组件出错，获取成员定州编码失败！');
		return false;
	}
	
	$.beginPageLoading();
	Wade.httphandler.submit("","com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.exitgrpmemberbox.ExitGrpMemberBoxHttpHandler","qryExitGrpMemberBoxInfo","&PRODUCT_ID="+productId+"&SERIAL_NUMBER="+mebSn+"&EPARCHY_CODE="+mebEparchCode,
    	function(data){
    		if(data != null){
    			inserExitGrpMemberBoxHtml(data);
			}
			$.endPageLoading();
	    	
	    },
	    function(error_code,error_info,derror){
    		showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();
	    	
	    },{async:false});
	    
}

function exitGrpMemberBoxCheckAction(obj){

	if($(obj).attr('checked')== true){
		$('#parm_JOIN_IN').val('1');
	}else{
		$('#parm_JOIN_IN').val('0');
	}
}




