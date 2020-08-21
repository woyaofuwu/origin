
function afterSubmitSerialNumber(data){

	var serialNumber = $("#SERIAL_NUMBER");
	if(!$.validate.verifyField(serialNumber)){
			return false;
	}
	
	$.beginPageLoading("查询用户信息中");
	var reqStr = "&SERIAL_NUMBER="+$("#SERIAL_NUMBER").val();
	$.ajax.submit(null,'getElements',reqStr,'platsvcParam,platsvcParam2,platsvcParam3,platsvcParam4,platsvcParam5,platsvcParam6,platsvcParam7,HiddenPart',function(data){
		$.endPageLoading();
     	if(data.length>0){
     		$("#show").css('display','inline-block');
     		$("#SERIAL_NUMBER").attr('disabled','disabled');
     		$("#bt_search").attr('disabled','disabled');
     		debugger;
     		pageDate(data);
     	}else{
     		$.endPageLoading();
     		MessageBox.alert("提示","用户未订购定向语音服务");
     	}
		
		},function(e,i){
			$.endPageLoading();
			MessageBox.alert("提示",e+":"+i);
		});
}

function pageDate(data){
	data.eachKey(
		function(key)
		{
			if(key=="userAreaFlag"){
				var value = data.get(key,"");
				if(value == "1"){
					$("#Areaoper").css('display','inline-block');
					$(".checkAreas").attr('disabled','');
					
				}
				
			}
			if(key=="UserFlag"){
				var value = data.get(key,"");
				if(value == "0"){
					
				}else if(value == "1"){
					$("#IDDFlag").addClass('required');
					$("#AutoForwardFlag").addClass('required');
					$("#RoamingFlag").addClass('required');
					$("#UserClass").addClass('required');
				}
				$("#USER_FLAG").val(value);
			}
			
			if(key=="GroupType"){
				var value = data.get(key,"");
				if(value == "2"){
					$("#pam_LockFlag").val('');
					$("#pam_LockFlag").attr('disabled','disabled');
					$("#pam_AutoForwardFlag").val('0');
					$("#pam_AutoForwardFlag").attr('disabled','disabled');
					$("#pam_userWhiteNumFlag").attr('disabled','disabled');
					$("#pam_userWhiteNumOperType").attr('disabled','disabled');
					$(".checkNumbers").attr('disabled','disabled');
					$(".user_number").attr('disabled','disabled');
				}else if(value=="1"){
					$("#pam_userShutFlag").val('');
					$("#pam_userShutFlag").attr('disabled','disabled');
				}
				$("#GroupType").val(value);
			}
		}
	
	);
	
	setWhiteNumDisable();
	setAreaDisable();
}

function setAreaDisable(){
	var length2 = $(".user_area").length;
	for(var j = 0 ; j<length2 ; j++){
		//下拉框的限制区域
		var objArea = $(".user_area").eq(j);
		if(objArea || objArea != undefined)
		{
			objArea.attr('disabled','false');
		}
		if(objArea || objArea != undefined)
		{
			//限制区域的复选框
			var objCheck =  objArea.parent().parent().children().eq(0).children().eq(0);
			if(objCheck || objCheck != undefined)
			{
				objCheck.attr('disabled','disabled');
			}
		}
	}
}

function getAreaFlag(){
	var userAreaFlagVal = $("#pam_userAreaFlag").val();
	var havingAreaLength = $(".HavingArea").length;
	if(userAreaFlagVal == "1"){//选择了多省漫游
		$("#Areaoper").css('display','inline-block');
		$(".checkAreas").attr('disabled','');
		if(havingAreaLength > 0)
		{
			setAreaDisableTow();
		}
		if(havingAreaLength <=0){
			setAreaDisableTow();
		}
	}else{
		$("#Areaoper").css('display','none');
		$(".checkAreas").attr('disabled','disabled');
		if(havingAreaLength > 0){
			setAreaDisableTow();
		}
		if(havingAreaLength <= 0){
			setAreaDisableTow();
		}
	}
	
	//个人用户/企业客户成员区域限制标识,从多省漫游修改为其他的区域限制标识时,
	//如果已有限制区域有列表时,并且没有点删除掉时,拦截提示要先删除掉已有限制区域,即选中
	if(havingAreaLength > 0)
	{
		if(userAreaFlagVal != "1")
		{
			var count = 0;
			var userAreaLength = $(".user_area").length;
			for(var j = 0 ; j<userAreaLength ; j++){
				var box = $(".user_area").eq(j).parent().parent().children().eq(0).children().eq(0);
				if(box.attr('checked')=='checked'||box.attr('checked')== true){
					count = count + 1;
				}
			}
			if((havingAreaLength != count && count != 0) || (havingAreaLength != count && count == 0)){
				MessageBox.alert("提示","从多省漫游修改为其他的区域限制标识时,请先选择删除区域限制后再修改!");
			}
		}
	}
}

function setAreaDisableTow(){
	var length2 = $(".user_area").length;
	for(var j = 0 ; j<length2 ; j++){
		//下拉框的限制区域
		var objArea = $(".user_area").eq(j);
		if(objArea || objArea != undefined)
		{
			objArea.attr('disabled','false');
		}
		if(objArea || objArea != undefined)
		{
			//限制区域的复选框
			var objCheck =  objArea.parent().parent().children().eq(0).children().eq(0);
			if(objCheck || objCheck != undefined)
			{
				objCheck.attr('disabled','disabled');
			}
		}
	}
}

function changeArea(obj){

	var operType = $("#pam_userAreaOperType").val();
	var havingAreaLength = $(".HavingArea").length;
	if(operType != "" && operType == '2' )
	{
		if(havingAreaLength<=0){
			MessageBox.alert("提示", "限制区域无删除的区域列表,不可能选择删除!");
		}
	}
	
	if(havingAreaLength <=0)
	{
		if(operType != "" && operType == "1")
		{
			setAreaEnableTow();
		} 
		else 
		{	
			setAreaDisableTow();
		}
	}
	
	/*
	if(operType != "" && operType == '2' && havingAreaLength > 0)
	{
		setAreaEnable();
	}
	if(operType != "" && operType == '2' && havingAreaLength <= 0)
	{
		setAreaEnableTow();
	}
	if(operType != "" && operType == '1' && havingAreaLength <= 0)
	{
		setAreaEnableTow();
	}
	if(operType != "" && operType == '1' && havingAreaLength > 0)
	{
		setAreaDisableTow();
	}
*/
	
	var length = $(".checkAreas").length;
	for(var i=length;i>=0;i--){
		var check = $(".checkAreas").eq(i).attr('checked');
		if(check == 'checked'||check == true){
			var lines = $(".checkAreas").eq(i).parent().parent();
			lines.attr("id","delete");
			$("#delete").remove();
		}		
	}
}


function setAreaEnable(){
	var length2 = $(".user_area").length;
	for(var j = 0 ; j<length2 ; j++){
		var objArea = $(".user_area").eq(j);
		//if(objArea || objArea != undefined)
		//{
		//	objArea.attr('disabled','');
		//}
		if(objArea || objArea != undefined)
		{
			//限制区域的复选框
			var objCheck =  objArea.parent().parent().children().eq(0).children().eq(0);
			if(objCheck || objCheck != undefined)
			{
				objCheck.attr('disabled','');
			}
		}
	}
}

function setAreaEnableTow(){
	var length2 = $(".user_area").length;
	for(var j = 0 ; j<length2 ; j++){
		//下拉框的限制区域
		var objArea = $(".user_area").eq(j);
		if(objArea || objArea != undefined)
		{
			objArea.attr('disabled','');
		}
		if(objArea || objArea != undefined)
		{
			//限制区域的复选框
			var objCheck =  objArea.parent().parent().children().eq(0).children().eq(0);
			if(objCheck || objCheck != undefined)
			{
				objCheck.attr('disabled','');
			}
		}
	}
}

function setAreaDisableTow(){
	var length2 = $(".user_area").length;
	for(var j = 0 ; j<length2 ; j++){
		//下拉框的限制区域
		var objArea = $(".user_area").eq(j);
		if(objArea || objArea != undefined)
		{
			objArea.attr('disabled','false');
		}
		if(objArea || objArea != undefined)
		{
			//限制区域的复选框
			var objCheck =  objArea.parent().parent().children().eq(0).children().eq(0);
			if(objCheck || objCheck != undefined)
			{
				objCheck.attr('disabled','disabled');
			}
		}
	}
}

function delArea(obj){
	var oper = $("#pam_userAreaOperType").val();
	var parent = $("#userAreas").parent();
	if(oper == "2"){
		var num = $(obj).children().text();
		var length = $(".user_area").length;
		for(var i=0;i<length;i++){
			var whiteNum = $(".user_area").eq(i).val();
			if(num == whiteNum){
				return;
			}
		}
		$(".user_area").eq(length-1).val(num);
		$(".checkAreas").eq(length-1).attr('checked','checked');
		
		var line1 = $("#userAreas").clone();
		line1.find('.checkAreas').attr('checked','');
		line1.find('.user_area').val('');
		parent.append(line1);
	}
}

function addAreaLine(obj){
	var box = $(obj);
	//先判断号码的正确性
	var operCode = $("#pam_userAreaOperType").val();
	if(operCode == ""){
		box.attr('checked','');
		MessageBox.alert("提示","请先选择限制区域操作类型!",null, null, null);
		return;
	}
	if(operCode == "1"){
		var number = $(obj).parent().next().children().val();
		var length = $(".HavingArea").length;
		for(var i = 0 ; i < length ; i++){
			if(number == $(".HavingArea").eq(i).text()){
				box.attr('checked','');
				MessageBox.alert("提示","已有该限制区域，无法新增!",null, null, null);
				return;
			}
		}
	}
	
	if(operCode == "2"){
		var number = $(obj).parent().next().children().val();
		var length = $(".HavingArea").length;
		var flag = true;
		for(var i = 0 ; i < length ; i++){
			if(number == $(".HavingArea").eq(i).text()){
				flag = false;
				break;
			}
		}
		if(flag){
			box.attr('checked','');
			MessageBox.alert("提示","不存在该限制区域，无法删除!",null, null, null);
			return;
		}
	}
	
	var parent = $("#userAreas").parent();
	var num = parent.children('tr').length;
	var numCheck = $(".checkAreas[type='checkbox']:checked").length;
	if(num == numCheck){
		var line1 = $("#userAreas").clone();
		line1.find('.checkAreas').attr('checked','');
		line1.find('.user_area').val('');
		parent.append(line1);
		
	}
	
	if(num>1){
		var box = $(obj);
		if(box.attr('checked')=='checked'||box.attr('checked')== true){
			
		}else{
			var lines = box.parent().parent();
			lines.attr("id","delete");
			$("#delete").remove();
		}
	}
	
}

function checkArea(obj){
	var operCode = $("#pam_userAreaOperType").val();
	var number = $(obj).val();
	var box = $(obj).parent().prev().children();
	var length = $(".user_area").length;
	var flag = 0;
	for(var i = 0 ; i< length ; i++){
		var in_num = $(".user_area").eq(i).val();
		if(in_num == ""){
			continue;
		}
		if(number == in_num){
			flag++;
		}
	}
	if(flag > 1){
		MessageBox.alert("提示","操作的限制区域不能重复!",null, null, null);
		$(obj).val('');
		return;
	}

	if(operCode == "1"){
		var length1 = $(".HavingArea").length;
		for(var i = 0 ; i < length1 ; i++){
			if(number == $(".HavingArea").eq(i).text()){
				box.attr('checked','');
				MessageBox.alert("提示","已有该限制区域，无法新增!",null, null, null);
				return;
			}
		}
	}
	
	if(operCode == "2"){
		var length1 = $(".HavingArea").length;
		var flag = true;
		for(var i = 0 ; i < length1 ; i++){
			if(number == $(".HavingArea").eq(i).text()){
				flag = false;
				break;
			}
		}
		if(flag){
			box.attr('checked','');
			MessageBox.alert("提示","不存在该限制区域，无法删除!",null, null, null);
			return;
		}
	}
}


function setWhiteNumDisable(){
	var length2 = $(".user_number").length;
	for(var j = 0 ; j<length2 ; j++){
		var objNumber = $(".user_number").eq(j);
		if(objNumber || objNumber != undefined)
		{
			objNumber.attr('disabled','false');
		}
		if(objNumber || objNumber != undefined)
		{
			var objCheck =  objNumber.parent().parent().children().eq(0).children().eq(0);
			if(objCheck || objCheck != undefined)
			{
				objCheck.attr('disabled','disabled');
			}
		}
	}
}

function setWhiteNumEnable(){
	var length2 = $(".user_number").length;
	for(var j = 0 ; j<length2 ; j++){
		var objNumber = $(".user_number").eq(j);
		if(objNumber || objNumber != undefined)
		{
			objNumber.attr('disabled','');
		}
		if(objNumber || objNumber != undefined)
		{
			var objCheck =  objNumber.parent().parent().children().eq(0).children().eq(0);
			if(objCheck || objCheck != undefined)
			{
				objCheck.attr('disabled','');
			}
		}
	}
}

function addWhiteLine(obj){
	var box = $(obj);
	debugger;
	//先判断号码的正确性
	var operCode = $("#pam_userWhiteNumOperType").val();
	if(operCode == ""){
		box.attr('checked','');
		MessageBox.alert("提示","请先选择用户白名单操作类型!",null, null, null);
		return;
	}
	if(operCode == "1"){
		var number = $(obj).parent().next().children().val();
		var length = $(".HavingNum").length;
		for(var i = 0 ; i < length ; i++){
			if(number == $(".HavingNum").eq(i).text()){
				box.attr('checked','');
				MessageBox.alert("提示","已有该白名单号码，无法新增!",null, null, null);
				return;
			}
		}
	}
	if(operCode == "2"){
		var number = $(obj).parent().next().children().val();
		var length = $(".HavingNum").length;
		var flag = true;
		for(var i = 0 ; i < length ; i++){
			if(number == $(".HavingNum").eq(i).text()){
				flag = false;
				break;
			}
		}
		if(flag){
			box.attr('checked','');
			MessageBox.alert("提示","不存在该白名单号码，无法删除!",null, null, null);
			return;
		}
	}
	
	var parent = $("#userNumbers").parent();
	var num = parent.children('tr').length;
	var numCheck = $(".checkNumbers[type='checkbox']:checked").length;
	if(num == numCheck){
		var line1 = $("#userNumbers").clone();
		line1.find('.checkNumbers').attr('checked','');
		line1.find('.user_number').val('');
		parent.append(line1);
	}
	
	if(num>1){
		var box = $(obj);
		if(box.attr('checked')=='checked'||box.attr('checked')== true){
			
		}else{
			var lines = box.parent().parent();
			lines.attr("id","delete");
			$("#delete").remove();
		}
	}
	
}

function checkWhiteNumber(obj){
	var operCode = $("#pam_userWhiteNumOperType").val();
	var number = $(obj).val();
	var box = $(obj).parent().prev().children();
	var length = $(".user_number").length;
	var flag = 0;
	for(var i = 0 ; i< length ; i++){
		var in_num = $(".user_number").eq(i).val();
		if(in_num == ""){
			continue;
		}
		if(number == in_num){
			flag++;
		}
	}
	if(flag > 1){
		MessageBox.alert("提示","操作的白名单不能重复!",null, null, null);
		$(obj).val('');
		return;
	}
	
	if(operCode == "1"){
		var length = $(".HavingNum").length;
		for(var i = 0 ; i < length ; i++){
			if(number == $(".HavingNum").eq(i).text()){
				box.attr('checked','');
				MessageBox.alert("提示","已有该白名单号码，无法新增!",null, null, null);
				return;
			}
		}
	}
	
	if(operCode == "2"){
		var length = $(".HavingNum").length;
		var flag = true;
		for(var i = 0 ; i < length ; i++){
			if(number == $(".HavingNum").eq(i).text()){
				flag = false;
				break;
			}
		}
		if(flag){
			box.attr('checked','');
			MessageBox.alert("提示","不存在该白名单号码，无法删除!",null, null, null);
			return;
		}
	}
	
}

function delNum(obj){
	var oper = $("#pam_userWhiteNumOperType").val();
	var parent=$("#userNumbers").parent();
	if(oper == "2"){
		var num = $(obj).children().text();
		var length = $(".user_number").length;
		for(var i=0;i<length;i++){
			var whiteNum = $(".user_number").eq(i).val();
			if(num == whiteNum){
				return;
			}
		}
		$(".user_number").eq(length-1).val(num);
		$(".checkNumbers").eq(length-1).attr('checked','checked');
		
		var line1 = $("#userNumbers").clone();
		line1.find('.checkNumbers').attr('checked','');
		line1.find('.user_number').val('');
		parent.append(line1);
	}
}

function changeWhiteType(obj){
	var checkNumberLength = $(".checkNumbers").length;
	
	var operType = $("#pam_userWhiteNumOperType").val();
	var havingNumLength = $(".HavingNum").length;//已有白名单号码列表的长度
	if(havingNumLength <=0)
	{
		if(operType != "" && operType == "1")
		{
			setWhiteNumEnable();
		} 
		else 
		{	
			setWhiteNumDisable();
		}
	}
		
	for(var i=checkNumberLength;i>=0;i--){
		var check = $(".checkNumbers").eq(i).attr('checked');
		if(check == 'checked'||check == true){
			var lines = $(".checkNumbers").eq(i).parent().parent();
			lines.attr("id","delete");
			$("#delete").remove();
		}		
	}
}



function reset(){
	var href = window.location.href;
	if(href){
		if(href.lastIndexOf("#nogo") == href.length-5){
			href = href.substring(0, href.length-5);
		}
		window.location.href = href;
	}
}


function setData(obj){
	$.beginPageLoading("信息校验并提交中");
	var maplist = $.DatasetList();
	debugger;
	var serial_number = $("#SERIAL_NUMBER").val();
	if(serial_number == undefined || serial_number ==""){
		$.endPageLoading();
		MessageBox.alert("提示", "服务号码未填写无法提交!");
		return ;
	}
	
	var user_flag = $("#UserFlag").val();
	var PhoneNumber = $("#pam_PhoneNumber").val();
	//var IDDFlag = $("#pam_IDDFlag").val();
	//var AutoForwardFlag = $("#pam_AutoForwardFlag").val();
	//var RoamingFlag = $("#pam_RoamingFlag").val();
	//var UserClass = $("#pam_UserClass").val();
	//var UserAccount = $("#pam_UserAccount").val();
	//var LockFlag = $("#pam_LockFlag").val();
	//var userShutFlag = $("#pam_userShutFlag").val();
	//var OrderID = $("#pam_OrderID").val();
	
	/*
	if(user_flag == "1"){
		if(IDDFlag==""||AutoForwardFlag==""||RoamingFlag==""||UserClass==""){
			$.endPageLoading();
			MessageBox.alert("提示", "国际长途权限,自动前转,国际漫游权限,个人通话阀值均不可为空!");
			return ;
		}
	}
	*/
	
	var userWhiteNumFlag = $("#pam_userWhiteNumFlag").val();
	var userWhiteNumOperType = $("#pam_userWhiteNumOperType").val();
	var havingNumLength = $(".HavingNum").length;//已有白名单号码列表的长度
	var userNumberLength = $(".user_number").length;
	if(havingNumLength > 0)
	{
		if(userWhiteNumOperType == "")
		{
			$.endPageLoading();
			MessageBox.alert("提示","已有白名单区域已经有了白名单号码,白名单的操作类型不能选择空!");
			return;
		}
		
		if(userWhiteNumOperType == "2")
		{
			var count = 0;
			for(var j = 0 ; j<userNumberLength ; j++){
				var box = $(".user_number").eq(j).parent().parent().children().eq(0).children().eq(0);
				if(box.attr('checked')=='checked'||box.attr('checked')== true){
					count = count + 1;
				}
			}
			if((havingNumLength != count && count != 0) || (havingNumLength != count && count == 0)){
				$.endPageLoading();
				MessageBox.alert("提示","白名单删除时,请选择全部白名单后再提交!");
				return;
			}
		}
	}
		
	if((userWhiteNumOperType != "" && ($(".checkNumbers[type='checkbox']:checked").length>0))
		||(userWhiteNumOperType == "" && ($(".checkNumbers[type='checkbox']:checked").length == 0)))
	{
	}
	else if(userWhiteNumOperType == '1' && havingNumLength > 0)
	{
	}
	else
	{
		$.endPageLoading();
		MessageBox.alert("提示","用户白名单操作类型、用户白名单应该同时出现");
		return ;
	}
	
	for(var j = 0 ; j<userNumberLength ; j++){//白名单添加时,修改白名单区域的勾选填写框没有填写值时,拦截提示
		var box = $(".user_number").eq(j).parent().parent().children().eq(0).children().eq(0);
		if(box.attr('checked')=='checked'||box.attr('checked')== true){
			var userNumberVal = $(".user_number").eq(j).val();
			if(userNumberVal == null || userNumberVal == "")
			{
				$.endPageLoading();
				MessageBox.alert("提示","选中的修改白名单区域的填写框为空!");
				return ;
			}
		}
	}
	
	for(var i = 0 ; i<userNumberLength ; i++){
		if($(".user_number").eq(i).val()==undefined || $(".user_number").eq(i).val() == ""){
			
		}else{
			var box = $(".user_number").eq(i).parent().parent().children().eq(0).children().eq(0);
			if(box.attr('checked')=='checked'||box.attr('checked')== true){
				var whiteNum = $.DataMap();
				whiteNum.put("ATTR_CODE","userWhiteNum");
				whiteNum.put("ATTR_VALUE",$(".user_number").eq(i).val());
				if(userWhiteNumOperType == "1"){
					whiteNum.put("MODIFY_TAG","0");
				}else if(userWhiteNumOperType == "2"){
					whiteNum.put("MODIFY_TAG","1");
				}
				maplist.add(whiteNum);
			}	
		}
	}
	
	/*
	var flag = $("#USER_FLAG").val();
	if(flag == "1"){
		if(IDDFlag == "" || AutoForwardFlag =="" || RoamingFlag=="" || UserClass==""){
			$.endPageLoading();
			MessageBox.alert("提示", "企业客户成员国际长途权限、企业客户成员分钟数使用完毕后是否支持自动前转、企业客户成员国际漫游权限、企业客户成员个人通话阀值的级别为必填!");
			return ;
		}
	}else{
		if(IDDFlag == "" && AutoForwardFlag =="" && RoamingFlag=="" && UserClass=="" && UserAccount=="" && LockFlag=="" && userShutFlag==""){
			$.endPageLoading();
			MessageBox.alert("提示", "企业客户成员国际长途权限、企业客户成员分钟数使用完毕后是否支持自动前转、企业客户成员国际漫游权限、企业客户成员个人通话阀值的级别、当月企业客户成员新增的通话分钟数、被叫闭锁功能标识、集群类企业客户成员闭锁功能标识至少填其一!");
			return ;
		}
	}
	*/
	
	var userAreaFlag = $("#pam_userAreaFlag").val();
	var userAreaOperType = $("#pam_userAreaOperType").val();
	var userAreaLength = $(".user_area").length;
	var havingAreaLength = $(".HavingArea").length;//已有限制区域的长度
	
	for(var j = 0 ; j<userAreaLength ; j++){//限制区域选择添加时，如果限制区域列表选择的选项时,没有填写值，则拦截提示
		var box = $(".user_area").eq(j).parent().parent().children().eq(0).children().eq(0);
		if(box.attr('checked')=='checked'||box.attr('checked')== true){
			var userAreaVal = $(".user_area").eq(j).val();
			if(userAreaVal == null || userAreaVal == "")
			{
				$.endPageLoading();
				MessageBox.alert("提示","选择中的限制区域列表未选择区域!请选择!");
				return ;
			}
		}
	}
	
	for(var j = 0 ; j<userAreaLength ; j++){
		if($(".user_area").eq(j).val()==undefined || $(".user_area").eq(j).val() == ""){
			
		}else{
			var box = $(".user_area").eq(j).parent().parent().children().eq(0).children().eq(0);
			if(box.attr('checked')=='checked'||box.attr('checked')== true){
				var userArea = $.DataMap();
				userArea.put("ATTR_CODE","userArea");
				userArea.put("ATTR_VALUE",$(".user_area").eq(j).val());
				if(userAreaOperType == "1"){
					userArea.put("MODIFY_TAG","0");
				}else if(userAreaOperType == "2"){
					userArea.put("MODIFY_TAG","1");
				}
				maplist.add(userArea);
			}
		}
	}
	
	//个人用户/企业客户成员区域限制标识,从多省漫游修改为其他的区域限制标识时,
	//如果已有限制区域有列表时,并且没有点删除掉时,拦截提示要先删除掉已有限制区域,即选中
	if(havingAreaLength > 0)
	{
		if(userAreaFlag != "1")
		{
			var count = 0;
			//var userAreaLength = $(".user_area").length;
			for(var j = 0 ; j<userAreaLength ; j++){
				var box = $(".user_area").eq(j).parent().parent().children().eq(0).children().eq(0);
				if(box.attr('checked')=='checked'||box.attr('checked')== true){
					count = count + 1;
				}
			}
			if((havingAreaLength != count && count != 0) || (havingAreaLength != count && count == 0)){
				$.endPageLoading();
				MessageBox.alert("提示","从多省漫游修改为其他的区域限制标识时,请先选择删除区域限制修改后再提交!");
				return;
			}
		}
		
		if(userAreaOperType == "")
		{
			$.endPageLoading();
			MessageBox.alert("提示","已有限制区域列表有限制区域,操作类型不能选择空!");
			return;
		}

		if(userAreaOperType == "2")
		{
			var count = 0;
			//var userAreaLength = $(".user_area").length;
			for(var j = 0 ; j<userAreaLength ; j++){
				var box = $(".user_area").eq(j).parent().parent().children().eq(0).children().eq(0);
				if(box.attr('checked')=='checked'||box.attr('checked')== true){
					count = count + 1;
				}
			}
			if((havingAreaLength != count && count != 0) || (havingAreaLength != count && count == 0)){
				$.endPageLoading();
				MessageBox.alert("提示","限制区域删除时,请选择全部选择完删除的限制区域再提交!");
				return;
			}
		}
	}
	
	if(havingAreaLength<=0 && userAreaFlag == "1")
	{
		if(userAreaOperType == null || userAreaOperType == "")
		{
			$.endPageLoading();
			MessageBox.alert("提示","区域限制标识是多省漫游,限制区域操作区域的操作类型不能为空!");
			return;
		}
	}
	if((userAreaOperType != "" && $(".checkAreas[type='checkbox']:checked").length>0)
		||(userAreaOperType == "" && $(".checkAreas[type='checkbox']:checked").length==0))
	{
	}
	else if(userAreaOperType == '1' && havingAreaLength > 0)
	{
	}
	else
	{
		$.endPageLoading();
		MessageBox.alert("提示","限制区域操作类型、限制区域列表应该同时出现");
		return ;
	}
	
	/*
	var map3 = $.DataMap();
	map3.put("ATTR_CODE","IDDFlag");
	map3.put("ATTR_VALUE",IDDFlag);
	var map4 = $.DataMap();
	map4.put("ATTR_CODE","AutoForwardFlag");
	map4.put("ATTR_VALUE",AutoForwardFlag);
	var map5 = $.DataMap();
	map5.put("ATTR_CODE","RoamingFlag");
	map5.put("ATTR_VALUE",RoamingFlag);
	var map6 = $.DataMap();
	map6.put("ATTR_CODE","UserClass");
	map6.put("ATTR_VALUE",UserClass);
	var map7 = $.DataMap();
	map7.put("ATTR_CODE","UserAccount");
	map7.put("ATTR_VALUE",UserAccount);
	var map8 = $.DataMap();
	map8.put("ATTR_CODE","LockFlag");
	map8.put("ATTR_VALUE",LockFlag);
	var map9 = $.DataMap();
	map9.put("ATTR_CODE","userShutFlag");
	map9.put("ATTR_VALUE",userShutFlag);
	var map10 = $.DataMap();
	map10.put("ATTR_CODE","OrderID");
	map10.put("ATTR_VALUE",OrderID);
	*/
	
	var map11 = $.DataMap();
	map11.put("ATTR_CODE","userWhiteNumFlag");
	map11.put("ATTR_VALUE",userWhiteNumFlag);
	var map12 = $.DataMap();
	map12.put("ATTR_CODE","userWhiteNumOperType");
	map12.put("ATTR_VALUE",userWhiteNumOperType);
	
	var map13 = $.DataMap();
	map13.put("ATTR_CODE","userAreaFlag");
	map13.put("ATTR_VALUE",userAreaFlag);
	var map14 = $.DataMap();
	map14.put("ATTR_CODE","userAreaOperType");
	map14.put("ATTR_VALUE",userAreaOperType);
	
	var map15 = $.DataMap();
	map15.put("ATTR_CODE","PhoneNumber");
	map15.put("ATTR_VALUE",PhoneNumber);
	map15.put("MODIFY_TAG","2");
	
	
	//maplist.add(map3);
	//maplist.add(map4);
	//maplist.add(map5);
	//maplist.add(map6);
	//maplist.add(map7);
	//maplist.add(map8);
	//maplist.add(map9);
	//maplist.add(map10);
	maplist.add(map11);
	maplist.add(map12);
	maplist.add(map13);
	maplist.add(map14);
	maplist.add(map15);
	
	//$.endPageLoading();
	//alert("能提交");
	//return false;
	var param = "&ATTRS="+maplist.toString();
	$.ajax.submit("HiddenPart","submit",param,null,function(data){
		$.endPageLoading();
		MessageBox.alert("提示","提交成功,订单编号:"+data.get("ORDER_ID"));
		$("#SERIAL_NUMBER").val('');
		$("#SERIAL_NUMBER").attr('disabled','');
		$("#bt_search").attr('disabled','');
		$("#show").css('display','none');
	},function(error_code,error_info,derror){
		$.endPageLoading();
		MessageBox.alert("报错",error_code+":"+error_info);
	});
	
}
