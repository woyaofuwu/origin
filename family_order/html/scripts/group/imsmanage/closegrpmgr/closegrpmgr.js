 
function showLayer(optionID) {
    var grp_user_id=$('#phone_user_id').val();    
	if(grp_user_id==""){
		  alert("请先查询集团用户资料！");
		  return false;
      }
    if($('#ctl_type').val()==""){
       $('#ctl_type').val('0');
    }
    	
	$('#'+optionID).css('display','');    
	 
	//新增集团闭合群
	if($('#ctl_type').val()=="0"){
		addOgrpFresh();
	}

}
function hideLayer(optionID) {
	$('#'+optionID).css('display','none');  
} 
function queryGrpCustInfo(){
    if(!$.validate.verifyAll("QueryCondition")) {
		return false;
	}
    $.beginPageLoading("数据查询中...");
	
	$.ajax.submit('QueryCondition', 'getGrpCustInfos', "", 'grpcustRefreshPart,closegrppart', function(data){
	    //查询集团闭合群业务列表
	    queryGrpOutinfo(); 
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });

}

/**
*查询成员闭合群信息
*/
function memFresh()
{
	var grp_user_id=$('#phone_user_id').val();
	var mem_serial_number = $('#cond_mem_sn').val();
	
  	if(grp_user_id=="")
  	{
		alert("请先输入集团服务号码，查询到资料后再进行操作！");
	  	return false;
    }
 
    $("#mem_part").css("display","");
    $("#closegrppart").css("display","none");
    $("#xunhupart").css("display","none");
    $("#daidapart").css("display","none");
 	  	  
	$("#JTli1").attr("className","");
   	$("#XHli2").attr("className","");
   	$("#DDli3").attr("className","");
   	$("#CYli4").attr("className","on");

	$.ajax.submit('', 'getMemInfo', '&MEM_SN='+mem_serial_number+'&phone_user_id='+grp_user_id, 'mem_part', 
	function(data)
	{
		$.endPageLoading();
	},
	function(error_code,error_info,derror)
	{
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//点新增按钮时显示新增详情区域
function addOgrpFresh(){
	$.ajax.submit('', 'getInitvalue', '', 'createOutgrp', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

//查询集团用户已有的闭合群
function queryGrpOutinfo(){
	var ctl_type=$('#ctl_type').val();	
	if(ctl_type==""){
    	$('#ctl_type').val('0');
    }
    
	var grp_user_id=$('#phone_user_id').val();
  	if(grp_user_id==""){
		alert("请先输入集团服务号码，查询到资料后再进行操作！");
	  	return false;
     }
  
	$("#mem_part").css("display","none");
    $("#closegrppart").css("display","");
    $("#xunhupart").css("display","none");
    $("#daidapart").css("display","none");
 	  	  
	$("#JTli1").attr("className","on");
   	$("#XHli2").attr("className","");
   	$("#DDli3").attr("className","");
   	$("#CYli4").attr("className","");
   	
	$.ajax.submit('grpcustRefreshPart', 'getGrpOutinfo', '&CTL_TYPE='+ctl_type, 'closegrppart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


//查询集团用户已有的寻呼组
function xunhuFresh(){ 
	var ctl_type=$('#ctl_type').val();	
	var grp_user_id=$("#phone_user_id").val();
	if(grp_user_id==""){
		alert("请先输入集团服务号码，查询到资料后再进行操作！");
		return false;
	}	  
	$("#mem_part").css("display","none");
    $("#closegrppart").css("display","none");
    $("#xunhupart").css("display","");
    $("#daidapart").css("display","none");
 	  	  
	$("#JTli1").attr("className","");
   	$("#XHli2").attr("className","on");
   	$("#DDli3").attr("className","");
   	$("#CYli4").attr("className","");
   	
	$.ajax.submit('grpcustRefreshPart', 'getXunhuinfo', '&CTL_TYPE='+ctl_type, 'xunhupart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
//查询集团用户已有的代答组
function daidaFresh(){
	var ctl_type=$('#ctl_type').val();	
	var grp_user_id=$("#phone_user_id").val();
	if(grp_user_id==""){
		alert("请先输入集团服务号码，查询到资料后再进行操作！");
		return false;
	}
    $("#mem_part").css("display","none");
    $("#closegrppart").css("display","none");
    $("#xunhupart").css("display","none");
    $("#daidapart").css("display","");
 	  	  
	$("#JTli1").attr("className","");
   	$("#XHli2").attr("className","");
   	$("#DDli3").attr("className","on");
   	$("#CYli4").attr("className","");
   	
	$.ajax.submit('grpcustRefreshPart', 'getDaiDainfo', '&CTL_TYPE='+ctl_type, 'daidapart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function hide_displaymemInfo(){
  var mem_part_disp=getElement("mem_part").style.display;

  if(mem_part_disp=="block"){
    getElement("contrl3").value="显示成员信息";
   getElement("mem_part").style.display = "none";
  }
   if(mem_part_disp=="none"){
   getElement("contrl3").value="隐藏成员信息";

   getElement("mem_part").style.display = "block";
  }

}

function queryMemInfo()
{
    if(!$.validate.verifyAll("memRefreshPart")) 
    {
		return false;
	}
	
	var ctl_type = $('#ctl_type').val();	
	var mem_serial_number = $('#cond_mem_sn').val();
	var grp_user_id=$('#phone_user_id').val();
	
	$.ajax.submit('', 'getMemInfo', '&CTL_TYPE='+ctl_type+'&MEM_SN='+mem_serial_number+'&phone_user_id='+grp_user_id, 'mem_part', 
	function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function delConfirm()
{
	MessageBox.confirm("提示信息","确定删除该条数据吗?",function(btn)
	{
		if(btn=='ok')
		{
			submitdelClosegrp();   
		}
	});
}

function checkRadioCloseGrp(part,obj){
	var checkRadioValue = "";
	var chkList = $("#"+part+" input[name="+obj+"]");
	for (var i = 0; i < chkList.length; i++) 
	{
		if (chkList[i].checked) 
		{
			checkRadioValue = chkList[i].value;
		}
	}
	return checkRadioValue;	
}

//删除网外关系
function submitdelClosegrp()
{
	var grp_user_id = $('#phone_user_id').val(); 
	var deal_type = $('#ctl_type').val();//把业务类型传到后台
	var user_eparchy_code;
	var outGrpData;
	var xunhuGrpData;
	var daidaGrpData;
	var mebCloseData;
	
	
	var grp_user_eparchy_code = $('#GRP_USER_EPARCHY_CODE').val();
	var meb_user_eparchy_code = $('#MEB_USER_EPARCHY_CODE').val();
 	
 	var fresharea ;
	if(deal_type == '0'){  //集团闭合群
	   fresharea='outgrpinfopart';
	   user_eparchy_code = grp_user_eparchy_code;	   
	   outGrpData  = checkRadioCloseGrp(fresharea,'outgrplist');
	}
	if(deal_type == '1'){  //集团成员闭合群
	   fresharea='closegrpmempart';
	   user_eparchy_code = meb_user_eparchy_code;
	   mebCloseData  = checkRadioCloseGrp(fresharea,'memclosegrplist');
	}               
	if(deal_type == '2'){  //寻呼组
	   fresharea='outgrpinfopart1';
	   user_eparchy_code = grp_user_eparchy_code;
	   xunhuGrpData  = checkRadioCloseGrp(fresharea,'xunhugrplist');
	}
	if(deal_type == '3'){  //代答组
	   fresharea='outgrpinfopart2';
	   user_eparchy_code = grp_user_eparchy_code;
	   daidaGrpData  = checkRadioCloseGrp(fresharea,'daidagrplist');
	} 
	    
	if (!$.validate.confirmAll(this)){  
		return false;
	}
	
	//提交前业务规则验证
	if(!submitRuleCheck()) {
		return false;
	}
		
	$.cssubmit.setParam("outgrplist", outGrpData);
	$.cssubmit.setParam("xunhugrplist", xunhuGrpData);
	$.cssubmit.setParam("daidagrplist", daidaGrpData);
	$.cssubmit.setParam("memclosegrplist", mebCloseData);
	$.cssubmit.setParam("USER_ID", grp_user_id);
   	$.cssubmit.setParam("CTL_TYPE", deal_type);
   	$.cssubmit.setParam("USER_EPARCHY_CODE", user_eparchy_code);
   	$("#CSSUBMIT_BUTTON").attr("listener","delCloseGrpInfo");
   
	$.cssubmit.registerTrade();		
}

/**
*新增闭合群(包含成员)
*/
function submitAddCloseGrp(){
	var ctl_flag = $('#ctl_type').val();
	var conpart = 'createOutgrp';
	var freshpart = 'outgrpinfopart';
	var user_eparchy_code;
	var grp_user_eparchy_code = $('#GRP_USER_EPARCHY_CODE').val();
	
	var mem_user_id = $('#mem_user_id').val();//把查询成员的USER_ID传到后台
	var mem_eparchy_code = $('#mem_eparchy_code').val();
	var grp_user_id = $('#phone_user_id').val(); 
	//集团闭合群
 	if(ctl_flag=="0"){
 		conpart = 'createOutgrp';
 		freshpart = 'outgrpinfopart';
	  	var colsegrpname = $('#close_grp_name').val();
	  	if(colsegrpname == ''){
	  		alert('闭合群名称不能为空!');
	  		$('#close_grp_name').focus();
	  		return false;
	  	}
	  	if($('#max_users').val() > 100){
			alert('闭合群的最大用户数不能大于100!');
			return false;
	 	}
	 	user_eparchy_code = grp_user_eparchy_code;
  	}
  	//成员闭合群
 	if(ctl_flag=="1"){
 		conpart = 'memRefreshPart,QueryCondition';
 		freshpart = 'closegrpmempart';
	 	if(!check_memcloseData()){
	  		return false;
	  	}
	  	user_eparchy_code = mem_eparchy_code;  	
 	}
 	//寻呼组
 	if(ctl_flag=="2"){
 		conpart = 'createOutgrp1';
 		freshpart = 'outgrpinfopart1';
	 	var hunting_team_type = $('#hunting_team_type').val(); 
	  	if(hunting_team_type == ''){
	  		alert('寻呼组类型不能为空!');
	  		$('#hunting_team_type').focus();
	  		return false;
	  	}
	  	user_eparchy_code = grp_user_eparchy_code;
  	}
  	//代答码
  	if(ctl_flag=="3"){
  		conpart = 'createOutgrp2';
  		freshpart = 'outgrpinfopart2';
	 	var access_code = $('#access_code').val(); 
	  	if(access_code == ''){
	  		alert('代答接入码不能为空!');
	  		return false;
	  	}
	  	user_eparchy_code = grp_user_eparchy_code;
  	} 	
	
	if(!$.validate.verifyAll(conpart)) {
		return false;
	}
	
	
    if (!$.validate.confirmAll(this)){   
		 return false;
	}
	//提交前业务规则验证
	if(!submitRuleCheck()) {
		return false;
	}	
	
	$.cssubmit.setParam("CTL_TYPE", ctl_flag);
   	$.cssubmit.setParam("USER_ID", grp_user_id);
   	$.cssubmit.setParam("MEM_USER_ID", mem_user_id);
   	$.cssubmit.setParam("MEM_EPARCHY_CODE", mem_eparchy_code);
   	$.cssubmit.setParam("USER_EPARCHY_CODE", user_eparchy_code);
   	
   	$("#CSSUBMIT_BUTTON").attr("area",conpart);  //设置刷新区域
   	$("#CSSUBMIT_BUTTON").attr("listener","addCloseGrpuu");  //设置执行动作
   
	$.cssubmit.registerTrade();	
}

function check_memcloseData(){
   var memuser_id = $('#mem_user_id').val();
   var selecCloseGrp = $('#cond_CLOSEGRPLIST').val();
   if(memuser_id==""){
	   alert("请先输入成员服务号码，查询到成员信息后再进行此操作！");
	   return false;
   }
   if(selecCloseGrp==""){
	   alert("请先选择闭合群后，再进行此操作！");
	   return false;
   }
   return true;
}

/**
*设置页面的控制类型值
*/
function fn_ctl_type(v){
	if(v=="GRP"){
		$('#ctl_type').val('0'); //集团闭合群业务 
	 }else if(v=="MEM"){
	 	$('#ctl_type').val('1'); //成员群组业务
	 }else if(v=="XUNHU"){
	 	$('#ctl_type').val('2'); //集团寻呼组业务
	 }else if(v=="DAIDA"){
	 	$('#ctl_type').val('3');//集团代答组业务
	}
}

function submitRuleCheck() {
	var x_deal = 0;	
	var ctl_flag = $('#ctl_type').val();
	
	var user_id_b = $('#mem_user_id').val();//把查询成员的USER_ID传到后台
	var user_id_a = $('#phone_user_id').val(); 
	
 	if(ctl_flag=="0"){
 		x_deal = 85;  // 闭合群新增业务
  	}
 	if(ctl_flag=="1"){
 		x_deal = 71;  // 成员闭合群新增业务
 	}
 	if(ctl_flag=="2"){
 		x_deal = 83;  // 寻呼组新增业务
  	}
  	if(ctl_flag=="3"){
  		x_deal = 84;  // 轮选组新增业务
  	} 
  	
	var result = ruleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CloseGrpMainRule','checkConfirmRule','&X_DEAL='+x_deal+'&USER_ID_A='+user_id_a+'&USER_ID_B='+user_id_b);
	return result;
}
