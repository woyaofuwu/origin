function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	var userInfo = data.get("USER_INFO");
	$.ajax.submit('', 'loadChildInfo', "&USER_ID="+userInfo.get("USER_ID")+"&SERIAL_NUMBER="+userInfo.get("SERIAL_NUMBER")+"&USER_STATE_CODESET="+userInfo.get("USER_STATE_CODESET"),
			'EditPart,memberSnPart,curMemberCountPart', function() {
					dealCheckBoxDisabled();
					$("#EditPart").removeClass("e_dis");
					$("#EditPart").attr("disabled",false);
					$("#bcreate").attr("disabled",false);
					$("#bdelete").attr("disabled",false);
					$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
	
}

function tableRowClick() {
	// 获取选择行的数据
	var rowData = $.table.get("memberTable").getRowData();
	$("#SERIAL_NUMBER_B").val(rowData.get("SERIAL_NUMBER_B"));
	
}
function dealCheckBoxDisabled(){
	var boxObj=$(":checkbox");
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	var size = boxObj.length;
	for(i=0; i<size; i++){ 
		if(boxObj[i].value == lastTimeThisMonthValue){
			boxObj[i].disabled = "true";
			boxObj[i].parentNode.parentNode.disabled = "true";
			boxObj[i].parentNode.parentNode.className="e_dis";
		}
	}
}
function isCanChecked(obj){
	var trObj = obj.parentNode.parentNode;
	var endDate = trObj.childNodes[3];
	var theEndDates = $('#END_DATE_THIS_ACCT').val();
	if(endDate.innerText == theEndDates){
		alert('已经被删除的号码不能再删除');
		obj.checked=false;
		return false;
	}
}
/**
 * 新增完后清空原始数据
 */
function cleanInputElement(){
	$("#SERIAL_NUMBER_B").val("");
}
function addOrModifyMember(){
	/* 校验所有的输入框 */
	if(!$.validate.verifyAll("EditPart")) {
		return false;
	}
	var  mSerialNumber =$("#SERIAL_NUMBER_B").val();
	
	var fmyCurCount=$('#FMY_CUR_COUNT').html();
	var toFmyCurCount = parseFloat(fmyCurCount) + 1;
	var mebLimValue = $('#MEB_LIM').val();
	if(toFmyCurCount>parseFloat(mebLimValue)){
		alert('您的统付成员号码数量已经达到' +mebLimValue+ '个的上限，不能再增加成员号码！');
		return false;
	}
	
	if(mSerialNumber==$('#AUTH_SERIAL_NUMBER').val()){		
		alert('对不起，成员号码不能和主卡号码一样，请重新输入！');
		return false;
	}
	
	var boxObj=$(":checkbox");
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	var size = boxObj.length;
	for(i=0; i<size; i++){ 
		if(boxObj[i].value != lastTimeThisMonthValue && trim(boxObj[i].parentNode.parentNode.childNodes[1].innerText)==mSerialNumber
				&&(trim(boxObj[i].parentNode.parentNode.childNodes[4].innerText) =="" ||
						trim(boxObj[i].parentNode.parentNode.childNodes[4].innerText) =="0")){
				alert("该号码已经存在,请重新输入！");
				return false;
		}
		
		if(boxObj[i].value != lastTimeThisMonthValue){
			if(trim(boxObj[i].parentNode.parentNode.childNodes[1].innerText)==mSerialNumber
					&&trim(boxObj[i].parentNode.parentNode.childNodes[4].innerText)=="1"){
				alert("本次删除的号码不能再新增！");
				return false;
				
			}
		}
	}
	
	checkMember();
}
//$.userCheck.checkUser("SERIAL_NUMBER_B");//身份校验	
/*去掉空格*/
function trim(str) {
	return str.replace(/^\s+|\s+$/,'');
}
function checkMember(){
	
	var boxObj=$(":checkbox");
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	var size = boxObj.length;
	for(i=0; i<size; i++){ 
		if(boxObj[i].value == lastTimeThisMonthValue){
			if(trim(boxObj[i].parentNode.parentNode.childNodes[1].innerText)==$("#SERIAL_NUMBER_B").val()){
				modifyMember(boxObj[i]);
				cleanInputElement();
				return ;
			}
		}
	}
	$.beginPageLoading("正在校验副号码...");
	var snb = $("#SERIAL_NUMBER_B").val();
	$.ajax.submit('', 'checkBySerialNumber', "&CHECK_SERIAL_NUMBER="+snb+"&MAIN_SERIAL_NUMBER="+$("#AUTH_SERIAL_NUMBER").val(),
			'', 
		function(data) {
			var msg = data.get("MSG");
			var code = data.get("CODE");

				if(code != 0 && code != 3){
					alert(msg);
					$.endPageLoading();
					return ;
				}else if(code == 3){
					if(confirm("["+snb+"]已经办理集团统付业务，在统一付费业务生效期间，" +
							"\n您的费用将由主号码支付，原有的集团付费业务将暂时失效。" +
							"\n您是否继续办理？")){
						addMember(data);
					}else{
						$.endPageLoading();
						return false;
					}
				}else{
					addMember(data);
				}
				cleanInputElement();
				$.endPageLoading();
			},function(error_code,error_info){
				$.MessageBox.error(error_code,error_info);
				$.endPageLoading();
			});	
}
function addMember(data){
	
	var fmyCurCount=$('#FMY_CUR_COUNT').html();
	var toFmyCurCount = parseFloat(fmyCurCount) + 1;
	
	var html=[];
	
	html.push('<tr>');
	html.push('<td><input class="e_center" type="checkbox" onclick="isCanChecked(this);" value=""/></td>');
	html.push('<td>'+$("#SERIAL_NUMBER_B").val()+'</td>');
	html.push('<td>'+$("#COMM_START_DATE").val()+'</td>');
	html.push('<td>'+$("#COMM_END_DATE").val()+'</td>');
	html.push('<td style="display:none;">0</td>');
	html.push('<td style="display:none;">'+ data.get("SELF_START_DATE")+'</td>');
	html.push('<td style="display:none;">'+ data.get("ACCT_DAY")+'</td>');
	html.push('</tr>');
	
	$.insertHtml('beforeend',$("#memberTable"),html.join(""));
	
	$('#FMY_CUR_COUNT').html(toFmyCurCount);
}
function modifyMember(obj){
	
	var endDate =$("#COMM_END_DATE").val();
	
	obj.parentNode.parentNode.childNodes[3].innerText=endDate;
	obj.parentNode.parentNode.childNodes[4].innerText="2";
	obj.disabled = false;
	obj.checked = "";
	obj.parentNode.parentNode.disabled = false;
	obj.parentNode.parentNode.className="";	
}
function delMember(){
	
	var boxObj=$(":checkbox:checked");
	var lastTimeThisMonthValue = $('#END_DATE_THIS_ACCT').val();
	var size = boxObj.length;
	if(size<1){
		alert("请您选择记录后再进行删除操作！");
		return false;
	}
	for(i=0; i<size; i++){ 
		if(trim(boxObj[i].parentNode.parentNode.childNodes[4].innerText)=="0"){
			$(boxObj[i].parentNode.parentNode).remove();
			var fmyCurCount=$('#FMY_CUR_COUNT').html();
			var toFmyCurCount = parseFloat(fmyCurCount) - 1;
			$('#FMY_CUR_COUNT').html(toFmyCurCount);
		}else{
			boxObj[i].parentNode.parentNode.childNodes[3].innerText=lastTimeThisMonthValue;
			boxObj[i].parentNode.parentNode.childNodes[4].innerText="1";
			boxObj[i].disabled = "true";
			boxObj[i].checked = "";
			boxObj[i].parentNode.parentNode.disabled = "true";
			boxObj[i].parentNode.parentNode.className="e_dis";
		}	
	}
}

function submitTrade(){
	var boxObj=$(":checkbox");
	var size = boxObj.length;
	var checkedDatas = $.DatasetList();
	var warnInfo ="";
	var startDate ="";
	for(i=0; i<size; i++){ 	
		if(trim(boxObj[i].parentNode.parentNode.childNodes[4].innerText)!=""){
			
			var data = new $.DataMap();
			data.put("SERIAL_NUMBER_B",trim(boxObj[i].parentNode.parentNode.childNodes[1].innerText));
			data.put("START_DATE",trim(boxObj[i].parentNode.parentNode.childNodes[2].innerText));
			data.put("END_DATE",trim(boxObj[i].parentNode.parentNode.childNodes[3].innerText));
			data.put("MODIFY_TAG",trim(boxObj[i].parentNode.parentNode.childNodes[4].innerText));
			checkedDatas.add(data);
			
			var targetAcctDay = trim(boxObj[i].parentNode.parentNode.childNodes[6].innerText);
			var selfStartDate = trim(boxObj[i].parentNode.parentNode.childNodes[5].innerText);
			//如果副号码不为1号
			if("1" != targetAcctDay){
				warnInfo += "号码" + data.get("SERIAL_NUMBER_B") + "的结账日为: " + targetAcctDay + "号；"
			}
			if(startDate < selfStartDate){
				startDate = selfStartDate;
			}
		}
	}

	if(checkedDatas.length<1){
		alert("未做任何操作，不允许提交订单！");
		return false;
	}
	
	if("1" != $("#MAIN_ACCT_DAY").val()){
		warnInfo += "号码" + $("#AUTH_SERIAL_NUMBER").val() + "的结账日为: " + $("#MAIN_ACCT_DAY").val() 
		+ "号；";
	}
	if(warnInfo != ""){
		warnInfo += "办理统一付费业务受理后结账日将变为1号，" + startDate + "开始生效!";
		alert(warnInfo);
	}
	var param ="&MEMBER_DATAS="+ checkedDatas.toString();
	
	$.cssubmit.addParam(param);
	
	return true;
	
}



/**
 * REQ201803260019++关于申请下放二级副以上领导手机代付铁通固定电话费用的权限的需求
 * 有权限的工号，对领导号码，免密添加
 */

function addMemberBeforeCheckSN(fieldName){
	var snObj=$("#"+fieldName);
	snObj.val($.trim(snObj.val()));
	if(snObj && snObj != ""){
		
		$.ajax.submit('', 'checkLeaderSerialNumberAndPermission', "&SERIAL_NUMBER="+snObj.val(), '', function(data)
		{
			var resultcode = data.get(0).get("X_RESULTCODE");
			if(resultcode == '-1'){
				$.userCheck.checkUser('SERIAL_NUMBER_B');
			}else{
				addOrModifyMember();
			}
		},
		function(error_code,error_info)
		{
			alert(error_info);
		});
		
	}else{
		alert("请输入成员号码!");
	}
}






































