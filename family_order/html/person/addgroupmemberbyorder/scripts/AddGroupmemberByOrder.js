//集团资料查询成功后调用的方法
function selectGroupAfterAction(data) {
	var custName = data.get("CUST_NAME");
	var groupId = data.get("GROUP_ID");
	var custId = data.get("CUST_ID");
	$("#GROUP_ID_NAME").val(custName);//界面将集团客户编码转换成名称
	$("#GROUP_CUST_ID").val(groupId);//集团客户编码
	$("#GROUP_CUST_NAME").val(custName);
	$("#GROUP_CUSTID").val(custId);//集团的custId(集团客户标识)

}
//集团资料查询失败后调用的方法
function selectGroupErrorAfterAction() {
	alert("查询数据出错");
	return;
}
//批量导入
function batImportBook(){
//	backPopup('UI-popup');
	var importFile = $("#FILE_ID").val();
    if (importFile == "") {
        alert("请选择需要导入的文件！");
        return;
    }
	$.beginPageLoading("正在录入数据...");
	var param = "&params=" + importFile;
	ajaxSubmit(null, "batImportBook",
			param, null, function(data) {
				$.endPageLoading();
				if (data.get('SUCCESS') == 'SUCCESS') {    //弹出返回的页面提示信息
				   alert("导入数据成功");
				}else{
					alert("导入数据出错");
				}
				
			}, function(error_code, error_info, derror) {
				$.endPageLoading();
				alert(error_info);
			});
}
function qryButton(){
	var serial = $("#SERIAL_NUMBER1").val();
	if (serial == '') {
		alert("请输入手机号码");
		return;
	}
	ajaxSubmit('searchPart','querySubscriber','','submitPart',function(data){
		var alertInfo = data.get('ALERT_INFO');
		var remark898 = data.get('REMARK898');
		var groupbook = data.get('GROUPBOOK');
		var remarkvpmn = data.get('REMARKVPMN');
		
		$("#ALERT_INFO").val(alertInfo);
		if (alertInfo == '1') {    //弹出返回的页面提示信息
			$("#SERIAL_NUMBER1").val(serial);
			alert("非正常用户，不能成为集团通讯录成员！");
			return;
		}
		//移动用户
		var mobileFlag = $("#IS_MOBILE").val();
		if(mobileFlag == '1'){
			$("#GROUP_898").attr('disabled',true);
			$("#CITY_CODE").attr('disabled',true);
			$("#USEPSPT_TYPE_CODE").attr('disabled',true);
			$("#GROUP_BOOK_MEMBER").attr('disabled',true);
			$("#GROUNP_V_MEMBER").attr('disabled',true);
			$("#USEPSPT_END_DATE").attr('disabled',true);
			
			$("#CUST_NAME").attr('disabled',true);
			$("#USECUST_NAME").attr('disabled',true);
			$("#SERIAL_NUMBER").attr('disabled',true);
			$("#EPARCHY_NAME").attr('disabled',true);
			$("#USEPSPT_ID").attr('disabled',true);
			$("#USEPSPT_ADDR").attr('disabled',true);
			$("#USEPSPT_END_DATE").attr('disabled',true);
			$("#EPARCHY_CODE").attr('disabled',true);
		}
		if(remark898 == '2'){
			$("#GROUNP_NAME").attr('disabled',true);
		}
		if(groupbook == '2'){
			$("#GROUP_ID").attr('disabled',true);
			$("#GROUP_CUST_NAME1").attr('disabled',true);
			$("#IS_REMOVE").attr('disabled',true);
			
		}
		if(remarkvpmn == '2'){
			$("#VPN_NAME").attr('disabled',true);
		}
		}, function(code, info, detail) {
		    $.endPageLoading();
		    MessageBox.error("错误提示", info, function(btn) {
		    }, null, detail);
		   }, function() {
		    $.endPageLoading();
		    MessageBox.alert("告警提示", "操作超时!", function(btn) {
		    });
		  });

}
function setMessageStatus(){
	var memberKind = $("#MEMBER_KIND").val();
	if(memberKind == '2'){
		document.getElementById("CLASS_CONTACT").classList.add("e_required");
	}
	if(memberKind == '0' || memberKind == '1'){
		document.getElementById("CLASS_CONTACT").classList.remove("e_required");
	}
	
}
function queryStatus(){
	var hideFlag = $("#SELECT_NUM").val();
	if(hideFlag == '2'){//根据客户姓名录入
		$("#CUST_NAME").val("");
		$("#USECUST_NAME").val("");
		$("#SERIAL_NUMBER").val("");
		$("#MEMBER_KIND").val("");
		$("#IS_CONTACT").val("");
		$("#IS_MOBILE").val("");
		$("#EPARCHY_CODE").val("");
		$("#CITY_CODE").val("");
		$("#USEPSPT_TYPE_CODE").val("");
		$("#USEPSPT_ID").val("");
		$("#USEPSPT_ADDR").val("");
		$("#USEPSPT_END_DATE").val("");
		$("#USEPOST_ADDR").val("");
		$("#USEPHONE").val("");
		$("#SVC_MODE_CODE").val("");
		$("#DEPART").val("");
		$("#DUTY").val("");
		$("#GROUP_898").val("");
		$("#GROUNP_NAME").val("");
		$("#GROUNP_V_MEMBER").val("");
		$("#VPN_NAME").val("");
		$("#GROUP_BOOK_MEMBER").val("");
		$("#GROUP_ID").val("");
		$("#GROUP_CUST_NAME1").val("");
		$("#IS_REMOVE").val("");
		$("#LIKE_MOBILE_BUSI").val("");
		$("#LIKE_DISCNT_TYPE").val("");
		$("#LIKE_ACT").val("");
		$("#REMARK").val("");
		
		
		
		$("#bquery").css("display","none");
		$("#NO_PHONE").val("1");
		$("#GROUP_898").attr('disabled','');
		$("#CITY_CODE").attr('disabled','');
		$("#USEPSPT_TYPE_CODE").attr('disabled','');
		$("#GROUP_BOOK_MEMBER").attr('disabled','');
		$("#GROUNP_V_MEMBER").attr('disabled','');
		$("#USEPSPT_END_DATE").attr('disabled','');
		
		$("#CUST_NAME").attr('disabled','');
		$("#USECUST_NAME").attr('disabled','');
		$("#SERIAL_NUMBER").attr('disabled','');
		$("#EPARCHY_NAME").attr('disabled','');
		$("#USEPSPT_ID").attr('disabled','');
		$("#USEPSPT_ADDR").attr('disabled','');
		$("#USEPSPT_END_DATE").attr('disabled','');
		$("#EPARCHY_CODE").attr('disabled','');
	}
	if(hideFlag == '1'){//根据服务号码查
		$("#bquery").css("display","");
		$("#NO_PHONE").val("2");
		
		$("#CUST_NAME").val("");
		$("#USECUST_NAME").val("");
		$("#SERIAL_NUMBER").val("");
		$("#MEMBER_KIND").val("");
		$("#IS_CONTACT").val("");
		$("#IS_MOBILE").val("");
		$("#EPARCHY_CODE").val("");
		$("#CITY_CODE").val("");
		$("#USEPSPT_TYPE_CODE").val("");
		$("#USEPSPT_ID").val("");
		$("#USEPSPT_ADDR").val("");
		$("#USEPSPT_END_DATE").val("");
		$("#USEPOST_ADDR").val("");
		$("#USEPHONE").val("");
		$("#SVC_MODE_CODE").val("");
		$("#DEPART").val("");
		$("#DUTY").val("");
		$("#GROUP_898").val("");
		$("#GROUNP_NAME").val("");
		$("#GROUNP_V_MEMBER").val("");
		$("#VPN_NAME").val("");
		$("#GROUP_BOOK_MEMBER").val("");
		$("#GROUP_ID").val("");
		$("#GROUP_CUST_NAME1").val("");
		$("#IS_REMOVE").val("");
		$("#LIKE_MOBILE_BUSI").val("");
		$("#LIKE_DISCNT_TYPE").val("");
		$("#LIKE_ACT").val("");
		$("#REMARK").val("");
	}
}
function submitData(){
	var selectNum = $("#SELECT_NUM").val();//2根据号码查   1根据姓名查
	var queryFlag = $("#QUERY_FLAG").val();//1是查询过了
	if('1' == selectNum && '1' != queryFlag){
		alert("根据号码录入，请先点击查询客户资料！");
		return;
	}
	var serial = $("#SERIAL_NUMBER1").val();
	if (serial == '') {
		alert("服务号码或客户姓名为空，请先输入号码查询客户资料或输入客户姓名再录入");
		return;
	}
	var serialPh = $("#SERIAL_NUMBER").val();
	if (serialPh == '') {
		alert("电话号码不可为空！");
		return;
	}
	var custNameEx = $("#CUST_NAME").val();
	if (custNameEx == '') {
		alert("开户客户不可为空！");
		return;
	}
	var usercustNameEx = $("#USECUST_NAME").val();
	if (usercustNameEx == '') {
		alert("使用客户不可为空！");
		return;
	}
	var eparchyCodeEx = $("#EPARCHY_CODE").val();
	if (eparchyCodeEx == '') {
		alert("归属地州不可为空！");
		return;
	}
	var cityCodeEx = $("#CITY_CODE").val();
	if (cityCodeEx == '') {
		alert("归属业务区不可为空！");
		return;
	}
	var memberId = $("#MEMBER_ID").val();
	var groupCustName = $('#MEMBER_GROUP_CUST_NAME').val();
	var alertInfo = $("#ALERT_INFO").val();
	var groupCustId = $("#GROUP_CUST_ID").val();
	var isRemove = $("#IS_REMOVE").val();
	if(alertInfo == '1'){
		alert("非正常用户，不能成为集团通讯录成员！");
		return;
	}
	var memberKind = $("#MEMBER_KIND").val();
	if(memberKind == ''){
		alert("成员类型不可为空！");
		return;
	}
	var memberKind = $("#MEMBER_KIND").val();
	var isContact = $("#IS_CONTACT").val();
	if(memberKind == '2'){
		if(isContact == ''){
			alert("是否可接触服务客户不可为空！");
			return;
		}
	}
	
	if (memberId != '') {
			if(isRemove == ''){
				alert("请选择是否迁移");
				return;
			}
			if(isRemove == '1'){
				if(groupCustId == ''){
					alert("集团客户名称不能为空");
					return;
				}
				var memberGroupId = $("#MEMBER_GROUP_ID").val();
				var groupCustId = $("#GROUP_CUST_ID").val();
				var CustName = $("#GROUP_CUST_NAME").val();
				
				if(memberGroupId == groupCustId){
					alert("该号码已是:"+CustName+"集团成员，不允许重复迁移同一集团！");
					return;
				}
			}
		    if(isRemove == '2'){
			    alert("该号码已在集团通讯录，不允许提交！");
				  return;
		    }
		MessageBox.confirm("告警提示",'该号码已是'+groupCustName+'集团的通讯录成员，是否继续?',function(ret){
			  if(ret=="ok"){
			   ajaxSubmit('submitPart','submitCheck','','',function(data){
				   if (data.get('SUCCESS') == 'SUCCESS') {    //弹出返回的页面提示信息
					   alert("录入成功");
					   $("#QUERY_FLAG").val('2');
					}else{
						alert("录入数据出错");
						
						$("#QUERY_FLAG").val('2');
					}
					return;
					}, function(code, info, detail) {
					    $.endPageLoading();
					    MessageBox.error("错误提示", info, function(btn) {
					    }, null, detail);
					   }, function() {
					    $.endPageLoading();
					    MessageBox.alert("告警提示", "操作超时!", function(btn) {
					    });
					  });
			  }
		});
	}else{
		if(groupCustId == ''){
			alert("集团客户名称不能为空");
			return;
		}
		MessageBox.confirm("提示",'确认提交?',function(ret){
			  if(ret=="ok"){
				  ajaxSubmit('submitPart','submitCheck','','',function(data){
					   if (data.get('SUCCESS') == 'SUCCESS') {    //弹出返回的页面提示信息
						   alert("录入成功");
						   $("#QUERY_FLAG").val('2');
						}else{
							alert("录入数据出错");
							$("#QUERY_FLAG").val('2');
						}
						return;
						}, function(code, info, detail) {
						    $.endPageLoading();
						    MessageBox.error("错误提示", info, function(btn) {
						    }, null, detail);
						   }, function() {
						    $.endPageLoading();
						    MessageBox.alert("告警提示", "操作超时!", function(btn) {
						    });
						  });
			  }
		});
		
	}
	
	

}
/**
 *导入模板 
 */
function openHref(){
	window.location.href='attach?action=downloadweb&realName=TXLMEMBER.xlsx&filePath=template/bat/group/TXLMEMBER.xlsx'
}
