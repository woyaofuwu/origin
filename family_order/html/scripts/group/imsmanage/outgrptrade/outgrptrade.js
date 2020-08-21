
function queryGrpCustInfo(){
	if(!$.validate.verifyAll("QueryGrpPart")) {
		return false;
	}
	$.ajax.submit('QueryGrpPart,ActiveNav', 'getGrpCustInfos', null, 'vpmnRefreshPart,grpcustRefreshPart', 
	function(data){
		queryGrpOutinfo(); 
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
  
}
  
function memFresh(){
	var grp_user_id=$('#phone_user_id').val();
   	if(grp_user_id==""){
		alert("请先查询集团用户资料！");
		return false;
	}
   	$("#groupLi").attr("className","");
   	$("#memLi").attr("className","on");
	$("#mem_part").css("display","");
	$("#outmebinfopart").css("display","");
	
	$("#outgrpinfopart").css("display","none");
	
  	//需要删除集团的网外信息
	if(!$.validate.verifyAll("QueryGrpPart")) {
		return false;
	}
	$.ajax.submit('QueryGrpPart', 'delGrpOutinfo', null, 'outmebinfopart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function queryGrpOutinfo(){
	var ctl_type=$('#ctl_type').val();	
	if(ctl_type==""){
    	$('#ctl_type').val('0');
    }
    var deal_type=$('#ctl_type').val();//把业务类型传到后台
	var grp_user_id=$('#phone_user_id').val();   
	if(grp_user_id==""){
		alert("请先查询集团用户VPN资料！"); 
		return false;
	}
	
    $("#groupLi").attr("className","on");
    $("#memLi").attr("className","");
	$("#outgrpinfopart").css("display","");
	
	$("#mem_part").css("display","none");
	$("#outmebinfopart").css("display","none");
	
	if(!$.validate.verifyAll("QueryGrpPart")) {
		return false;
	}
	
	$.ajax.submit('vpmnRefreshPart', 'getGrpOutinfo', '&CTL_TYPE='+deal_type, 'outgrpinfopart',function(data){
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

function queryMemInfo(){	
	if(!$.validate.verifyAll("QueryMemPart")) {
		return false;
	} 
	var mem_serial_number = $('#cond_mem_sn').val();
	var phone_user_id = $('#phone_user_id').val();
	var param = '&MEM_SN='+mem_serial_number+'&phone_user_id='+phone_user_id;
	$.ajax.submit('QueryMemPart', 'getMemInfo', param, 'memRefreshPart', 
	function(data){ 
		getMemOutGrp();
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
} 
function getMemOutGrp(){
	var deal_type=$('#ctl_type').val();//把业务类型传到后台
	var mem_serial_number= $('#cond_mem_sn').val();
	var mem_user_id= $('#mem_user_id').val();
    var grp_user_id = $('#phone_user_id').val(); 
  //  var param = '&cond_mem_sn='+mem_serial_number+'&mem_user_id='+mem_user_id+'&grp_userid='+grp_user_id+'&CTL_TYPE='+deal_type;
    $.ajax.submit('QueryMemPart,memRefreshPart', 'getMemOutGrp', '&CTL_TYPE='+deal_type, 'outmebinfopart', 
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
	MessageBox.confirm("提示信息","确定删除该网外号码吗?",function(btn)
	{
		if(btn=='ok')
		{			
			submitdelOgrp();   	
		}
	});
}

function checkRadioOutGrp(part,obj){
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
function submitdelOgrp()
{
	var phone_user_id = $('#phone_user_id').val();
	var deal_type=$('#ctl_type').val();//把业务类型传到后台
	if (!$.validate.verifyAll("QueryGrpPart"))
	{	
		return false;
	}
	var grp_user_eparchy_code = $('#GRP_USER_EPARCHY_CODE').val();
	var meb_user_eparchy_code = $('#MEB_USER_EPARCHY_CODE').val();
	var fresharea ;	
	var user_eparchy_code;
	var outGrpData;
	if(deal_type == '0')   //集团网外号码
	{  
	   fresharea='outgrpinfopart';
	   user_eparchy_code = grp_user_eparchy_code;
	   outGrpData = checkRadioOutGrp(fresharea,'outgrplist');
	}
	if(deal_type == '1')   //成员网外号码
	{ 
	   fresharea='outmebinfopart';
	   user_eparchy_code = meb_user_eparchy_code;
	   outGrpData = checkRadioOutGrp(fresharea,'memoutgrplist');
	}   	
	
	//提交前业务规则验证
	if(!submitRuleCheck()) 
	{
		return false;
	}
	
	$.cssubmit.clearParam();
	$.cssubmit.setParam("OUT_GRP_LIST", outGrpData);	    	
   	$.cssubmit.setParam("CTL_TYPE", deal_type);
   	$.cssubmit.setParam("GRP_USER_ID", phone_user_id);
   	$.cssubmit.setParam("USER_EPARCHY_CODE", user_eparchy_code);
   		
   	$("#CSSUBMIT_BUTTON").attr("listener","delOutGrpInfo");
	$.cssubmit.registerTrade();	
}

function fn_ctl_type(v){   
	if(v=="GRP"){
   		$('#ctl_type').val('0');//集团业务
   	}else if(v=="MEM"){
   		$('#ctl_type').val('1');//成员业务
  	} 
}

function showLayer(optionID) {
	$('#out_grp_num').val('');
	var ctl_type = $('#ctl_type').val();
    var grp_user_id=$('#phone_user_id').val();    
	if(grp_user_id==""){
		  alert("请先查询集团用户VPN资料！");
		  return false;
      }
    if(ctl_type==""){
    	$('#ctl_type').val('0');
    }
    if(ctl_type=="1"){//新增成员网外号码
	    var phone_mem_name=$('#phone_mem_name').val();    
		if(phone_mem_name==""){
			  alert("请先查询成员用户信息！");
			  return false;
	     }
    }   
    $('#'+optionID).css('display','');     
}
		
function submitLayer(optionID) {
    var grp_user_id=$('#phone_user_id').val();    
	if(grp_user_id==""){
		  alert("请先查询集团用户VPN资料！");
		  return false;
      }
    if(!$.validate.verifyAll(optionID)) {
		return false;
	} 
    submitAddOutPhone();
}				

//新增网外关系
function submitAddOutPhone(){
    var deal_type= $('#ctl_type').val();//把业务类型传到后台
    var grp_user_id= $('#phone_user_id').val();
    var cond_mem_sn=$('#cond_mem_sn').val();
    var grp_user_eparchy_code = $('#GRP_USER_EPARCHY_CODE').val();
    var meb_user_eparchy_code = $('#MEB_USER_EPARCHY_CODE').val();
    var out_grp_num = $('#out_grp_num').val();
    var user_eparchy_code;
    if(deal_type == '0')  //集团网外号码
    {
    	user_eparchy_code = grp_user_eparchy_code;
    }
    if(deal_type == '1')   //成员网外号码
	{ 
	   user_eparchy_code = meb_user_eparchy_code;
	}   
    //提交前业务规则验证
	if(!submitRuleCheck()) 
	{
		return false;
	}

	$.cssubmit.setParam("CTL_TYPE", deal_type);
	$.cssubmit.setParam("cond_mem_sn", cond_mem_sn);	   	
   	$.cssubmit.setParam("out_grp_num", out_grp_num);
   	$.cssubmit.setParam("GRP_USER_ID", grp_user_id);
   	$.cssubmit.setParam("USER_EPARCHY_CODE", user_eparchy_code);
   	$("#CSSUBMIT_BUTTON").attr("listener","addOutUU");
   
	$.cssubmit.registerTrade();	
}		

function hideLayer(optionID) {
	var grp_user_id=$('#phone_user_id').val();    
	if(grp_user_id==""){
		alert("请先查询集团用户VPN资料！");
	  	return false;
    }
	$('#'+optionID).css('display','none'); 
}

function submitRuleCheck() {
	var x_deal =0;	
	var ctl_flag = $('#ctl_type').val();
	
	var user_id_b = $('#mem_user_id').val();//把查询成员的USER_ID传到后台
	var user_id_a = $('#phone_user_id').val(); 
	
 	if(ctl_flag=="0"){
 		x_deal = 2;  // 集团网外号码维护
  	}
  	if(ctl_flag=="1"){
 		x_deal = 1;  // 成员网外号码维护
  	}	
  	
	var result = ruleCheck('com.asiainfo.veris.crm.order.web.frame.csview.group.rule.CloseGrpMainRule','checkConfirmRule','&X_DEAL='+x_deal+'&USER_ID_A='+user_id_a+'&USER_ID_B='+user_id_b);
	return result;
}
 