//通过角色数据生成页面信息
function insertMebRoleBInfo(data){
	if(data != null){
		var leng = data.length;
		var roleObj = $('#ROLE_CODE_B');
		roleObj.html('');
		var roleSelInnerHTML = [];
		for(var i =0;i<leng;i++){
			var info = data.get(i);
			var roleId = info.get("ROLE_CODE_B");
			var roleName = info.get("ROLE_B");
			roleSelInnerHTML.push( '<option value="'+roleId+'">'+roleName+'</option>');
		}
		$.insertHtml('afterbegin',roleObj,roleSelInnerHTML.join(""));

	}
}
//清空角色信息
function cleanMebRoleBInfo(){
	var roleObj = $('#ROLE_CODE_B');
	roleObj.html('');
	var roleSelInnerHTML = [];
	roleSelInnerHTML.push('<option value="" title="--请选择--">--请选择--</option>');
	$.insertHtml('afterbegin',roleObj,roleSelInnerHTML.join(""));
}

//获取当前的角色信息
function getMebRoleCodeB(){
	return $('#ROLE_CODE_B').val();	
}

//设置成员角色的值
function setMebRoleCodeB(roleCode){
	 $('#ROLE_CODE_B').val(roleCode);	
}


//通过PRODUCTID查询成员角色列表
function renderMebRoleBInfos(productId){
	var result = true;
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmebrolebinfo.SelectMebRoleBInfoHttpHandler','qryMemberRoleBInfosByProductId','&GRP_PRODUCT_ID='+productId,
	function(data){
		insertMebRoleBInfo(data);
	},
	function(error_code,error_info,derror){
		cleanMebRoleBInfo();
	    result = false;
		showDetailErrorInfo(error_code,error_info,derror);
		$.endPageLoading();
		
    },{async:false});
    return result;
}

//通过成员用户ID，成员地州，集团用户ID查询成员角色
function renderMebRoleBInfoByMebUserId(mebUserId,grpUserId,mebEparchyCode){
	var result = true;
	Wade.httphandler.submit('','com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.selectmebrolebinfo.SelectMebRoleBInfoHttpHandler','qryMemberRoleBInfoByMebUserIdGrpUserIdProductId','&GRP_USER_ID='+grpUserId+'&MEB_USER_ID='+mebUserId+'&MEB_EPARCHY_CODE='+mebEparchyCode,
	function(data){
		if(data == null)
			return ;
			
		insertMebRoleBInfo(data.get("ROLE_LIST"));
		setMebRoleCodeB(data.get("ROLE_CODE_B"));
	},
	function(error_code,error_info,derror){
		cleanMebRoleBInfo();
	    result = false;
		showDetailErrorInfo(error_code,error_info,derror);
		$.endPageLoading();
		
    },{async:false});
    return result;
}
