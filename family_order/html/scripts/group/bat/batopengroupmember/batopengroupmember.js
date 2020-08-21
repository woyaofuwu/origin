 
function refreshProductInfoArea(data) {
	insertGroupCustInfo(data);
	var custId = $('#CUST_ID').val();
	
	$.beginPageLoading('正在查询集团开户可订购的产品列表...');
	$.ajax.submit('CondGroupPart','queryProductTypeList','&CUST_ID='+custId,'productInfoArea',function(data){
		
		$("#ImsInfoPart").css("display","none");
		
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function errorAction() {
	clearGroupCustInfo();
	  //清空集团产品树信息
    cleanGroupProductTree();
}

// 选中IMS语音产品显示
function displayIms(productId) {	
	
    var obj = $("#ImsInfoPart"); 
    var GroupUserInfoPart = $("#GroupUserInfoPart"); 
    var GroupUserInfoImsPart = $("#GroupUserInfoImsPart");
	if(productId == "801110"){
	    obj.css("display","");
	    GroupUserInfoPart.css("display", "none");
	    GroupUserInfoImsPart.css("display", "");
	}else{
		obj.css("display","none")
		GroupUserInfoPart.css("display", "");
	    GroupUserInfoImsPart.css("display", "none");
	}	
}


function addChoiceArea(str) {
	$('#'+str).click();
}

function refreshProduct(){
	var productVal = $("#userProductInfo").val();
	if("" == productVal || null == productVal || undefined == productVal) {
		alert('请选择集团产品!');
		return false;
	}else {
		var productNameStr = $("#userProductInfo :selected").text();
		var productId = $("#userProductInfo :selected").val();
		$("#PRODUCT_ID").val(productId);
		$("#PRODUCT_NAME").val(productNameStr);
		displayIms(productId);
		
		var productId = $("#PRODUCT_ID").val();
		var groupId = $('#POP_cond_GROUP_ID').val();
		$.beginPageLoading('正在查询产品包元素中...');
		$.ajax.submit(this,'refreshProduct','&PRODUCT_ID='+productId+'&GROUP_ID='+groupId,'ProductPackagePart',function(data){
			
			$.endPageLoading();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
}

/**产品拼串*/
function setData(obj){
	if(!$.validate.verifyAll("productInfoArea")) {
		return false;
	}
	var productId = $("#userProductInfo :selected").val();
	if(productId == "801110"){
		//IMS语音 :801110
		if(!$.validate.verifyAll("GroupUserInfoImsPart")) {
			return false;
		}
		
		/**
		 * REQ201801150022_新增IMS号码开户人像比对功能
		 * @author zhuoyingzhi
		 * @date 20180326
		 */
		var cmpTag = "1";
		$.ajax.submit(null,'isBatCmpPic','','',
				function(data){ 
			var flag=data.get("CMPTAG");
			if(flag=="0"){ 
				cmpTag = "0";
			}
			$.endPageLoading();
		},function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{
			"async" : false
		});
		
		if(cmpTag == "0"){
			//客户人像比对信息
			var picid = $("#MEM_CUST_INFO_PIC_ID").val();
			
			if(null != picid && picid == "ERROR"){
				MessageBox.error("告警提示","客户"+$("#MEM_CUST_INFO_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			//客户证件类型
			var psptTypeCode=$("#MEM_CUST_INFO_PSPT_TYPE_CODE").val();
			
			//经办人信息(人像比对信息)
			var agentpicid = $("#MEM_CUST_INFO_AGENT_PIC_ID").val();
			//经办人证件类型
			var agentTypeCode = $("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val();
			
			if((psptTypeCode == "0" || psptTypeCode == "1" ) && picid == ""){
				
				//经办人名称
			    var  custName = $("#MEM_CUST_INFO_AGENT_CUST_NAME").val();
			    //经办人证件号码
				var  psptId = $("#MEM_CUST_INFO_AGENT_PSPT_ID").val();
				//经办人证件地址
				var  agentPsptAddr= $("#MEM_CUST_INFO_AGENT_PSPT_ADDR").val();
				
				if(agentTypeCode == ''&& custName == '' && psptId == '' && agentPsptAddr== ''){
					MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
					return false;
				}
				
				if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
					MessageBox.error("告警提示","请进行客户或经办人摄像!",null, null, null, null);
					return false;
				}
			}
			
			if(null != agentpicid && agentpicid == "ERROR"){
				MessageBox.error("告警提示","经办人"+$("#MEM_CUST_INFO_AGENT_PIC_STREAM").val(),null, null, null, null);
				return false;
			}
			
			if((agentTypeCode == "0" || agentTypeCode == "1" ) && agentpicid == ""){
				MessageBox.error("告警提示","请进行经办人摄像!",null, null, null, null);
				return false;
			}
		}		
	   /*********************REQ201801150022_新增IMS号码开户人像比对功能_end*************************/
		
	}else{
		if(!$.validate.verifyAll("GroupUserInfoPart")) {
			return false;
		}
	}
	if(!$.validate.verifyAll("ImsInfoPart")) {
		return false;
	}
	if(!$.validate.verifyAll("ProductPackagePart")) {
		return false;
	}
		
	var tempElements = selectedElements.getSubmitData();
	$("#SELECTED_ELEMENTS").val(tempElements);
	var flag = true;
	var productId = $("#PRODUCT_ID").val();
	$.ajax.submit('GroupUserPart', 'checkMustDiscnt','&GRPPRODUCTID='+productId, null,function(data){
			$.endPageLoading();
			verifyBySelectDiscnt(data);
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
			flag = false;
		},{async:false});
	return flag;	
}

function verifyBySelectDiscnt(data) {
	var checkresult = data.get('checkresult');
	if(""==checkresult || null==checkresult) {
		commSubmit();
	}else {
		$.showWarnMessage('未选择必选包元素!',checkresult);
		return false;
	}
}

function commSubmit() {
	var group_id = $("#GROUP_ID").val();
	var productId = $("#PRODUCT_ID").val();
	var elements = $("#SELECTED_ELEMENTS").val();
	var cust_id = $("#CUST_ID").val();
	var user_eparchy_code = $('#USER_EPARCHY_CODE').val();
	var teltype = $("#CUST_INFO_TELTYPE").val();
	
	var base = $.DataMap();
	var mem_user_info = $.DataMap();
	//IMS产品
	if(productId == "801110"){
		base.put("PSPT_TYPE_CODE", $.trim($("#MEM_CUST_INFO_PSPT_TYPE_CODE").val()));
		base.put("PSPT_ID", $.trim($("#MEM_CUST_INFO_PSPT_ID").val()));
		base.put("REAL_NAME", $.trim($("#MEM_CUST_INFO_REAL_NAME").val()));
		base.put("CONTACT_PHONE", $.trim($("#MEM_CUST_INFO_CONTACT_PHONE").val()));
		base.put("CUST_NAME", $.trim($("#MEM_CUST_INFO_CUST_NAME").val()));
		base.put("POST_CODE", $.trim($("#MEM_CUST_INFO_POST_CODE").val()));
		base.put("PSPT_ADDRESS", $.trim($("#MEM_CUST_INFO_PSPT_ADDRESS").val()));
		base.put("BIRTHDAY", $.trim($("#MEM_CUST_INFO_BIRTHDAY").val()));
		//营业执照信息
		base.put("legalperson", $.trim($("#MEM_CUST_INFO_legalperson").val()));		//法人
		base.put("startdate", $.trim($("#MEM_CUST_INFO_startdate").val()));			//成立日期
		base.put("termstartdate", $.trim($("#MEM_CUST_INFO_termstartdate").val()));	//营业开始时间
		base.put("termenddate", $.trim($("#MEM_CUST_INFO_termenddate").val()));		//营业结束时间
		//组织机构信息
		base.put("orgtype", $.trim($("#MEM_CUST_INFO_orgtype").val()));				//机构类型
		base.put("effectiveDate", $.trim($("#MEM_CUST_INFO_effectiveDate").val()));	//有效日期
		base.put("expirationDate", $.trim($("#MEM_CUST_INFO_expirationDate").val()));//失效日期
		//经办人信息
		base.put("AGENT_CUST_NAME", $.trim($("#MEM_CUST_INFO_AGENT_CUST_NAME").val()));
		base.put("AGENT_PSPT_TYPE_CODE", $.trim($("#MEM_CUST_INFO_AGENT_PSPT_TYPE_CODE").val()));
		base.put("AGENT_PSPT_ID", $.trim($("#MEM_CUST_INFO_AGENT_PSPT_ID").val()));
		base.put("AGENT_PSPT_ADDR", $.trim($("#MEM_CUST_INFO_AGENT_PSPT_ADDR").val()));
		//使用人信息
		base.put("USE", $.trim($("#MEM_CUST_INFO_USE").val()));
		base.put("USE_PSPT_TYPE_CODE", $.trim($("#MEM_CUST_INFO_USE_PSPT_TYPE_CODE").val()));
		base.put("USE_PSPT_ID", $.trim($("#MEM_CUST_INFO_USE_PSPT_ID").val()));
		base.put("USE_PSPT_ADDR", $.trim($("#MEM_CUST_INFO_USE_PSPT_ADDR").val()));
		//责任人信息
		base.put("RSRV_STR2", $.trim($("#MEM_CUST_INFO_RSRV_STR2").val()));
		base.put("RSRV_STR3", $.trim($("#MEM_CUST_INFO_RSRV_STR3").val()));
		base.put("RSRV_STR4", $.trim($("#MEM_CUST_INFO_RSRV_STR4").val()));
		base.put("RSRV_STR5", $.trim($("#MEM_CUST_INFO_RSRV_STR5").val()));
		//用户类型
		mem_user_info.put("USER_TYPE_CODE", $.trim($("#MEM_USER_INFO_USER_TYPE_CODE").val()));
	}else{
		base.put("PSPT_TYPE_CODE", $.trim($("#OMEM_CUST_INFO_PSPT_TYPE_CODE").val()));
		base.put("POST_CODE", $.trim($("#OMEM_CUST_INFO_POST_CODE").val()));
		base.put("CONTACT_PHONE", $.trim($("#OMEM_CUST_INFO_CONTACT_PHONE").val()));
		base.put("CUST_NAME", $.trim($("#OMEM_CUST_INFO_CUST_NAME").val()));
		base.put("PSPT_ID", $.trim($("#OMEM_CUST_INFO_PSPT_ID").val()));
		mem_user_info.put("USER_TYPE_CODE", $.trim($("#OMEM_USER_INFO_USER_TYPE_CODE").val()));
	}
	
	mem_user_info.put("REMARK","批量成员开户");
	if ( elements == "" ) 
		return false;    
	var idata = $.DatasetList(elements);
	
	var info = $.DataMap();
	info.put("ELEMENT_INFO", idata);
	info.put("MEM_CUST_INFO", base);
	info.put("MEM_USER_INFO", mem_user_info);
	info.put("CUST_INFO_TELTYPE", teltype);
	info.put("GROUP_ID", group_id);
	info.put("PRODUCT_ID",productId);
	info.put("CUST_ID", cust_id);
	info.put('USER_EPARCHY_CODE',user_eparchy_code);
	info.put('ROUTE_EPARCHY_CODE',user_eparchy_code);
	
	//$.setReturnValue({'POP_CODING_STR':"产品ID:"+productId+" 集团ID:"+group_id},false);
 	//$.setReturnValue({'CODING_STR':info},true);
	
	/**
	 * REQ201801150022_新增IMS号码开户人像比对功能
	 * @author zhuoyingzhi
	 * @date 20180327
	 */
	if(productId == "801110"){
		//客户摄像
		info.put("custInfo_PIC_ID", $.trim($("#MEM_CUST_INFO_PIC_ID").val()));
		//经办人摄像
		info.put("custInfo_AGENT_PIC_ID", $.trim($("#MEM_CUST_INFO_AGENT_PIC_ID").val()));
		//IMS固话批量开户标志
		info.put("BATCH_OPER_TYPE", "BATOPENGROUPMEM");
	}
	
 	parent.$('#POP_CODING_STR').val("产品ID:"+productId+" 集团ID:"+group_id);
	parent.$('#CODING_STR').val(info);
 	
	parent.hiddenPopupPageGrp();
}

/**
 * 校验证件类型和证件号码
 */
function validPspt()
{
	if($("#OMEM_CUST_INFO_PSPT_ID").val()=='')
		return true;
	var busi_licence_type_obj = $("#OMEM_CUST_INFO_PSPT_TYPE_CODE");
    var lic_type = busi_licence_type_obj.val();
    if(lic_type==null || lic_type=='')
    {
    	alert("证件类型不能为空");
        return false;  
    }
    else if(lic_type == 'E')
    {    
    	var lic_no =  $("#OMEM_CUST_INFO_PSPT_ID").val();
        if(lic_no.length!=15&&lic_no.length!=18 &&lic_no.length!=13&&lic_no.length!=20&&lic_no.length!=22&&lic_no.length!=24){
        	alert("营业执照类型校验：营业执照证件号码长度需满足13位、15位、18位、20位、22位或24位！当前："+lic_no.length+"位。");
        	var obj =  $("#OMEM_CUST_INFO_PSPT_ID");
        	obj.val("");
        	obj.focus();
       	 return false;
        }
    }
    return true;
}




