function checkSub(obj)
{
	if($("#pam_MODIFY_TAG").val()=="0"&&$("#pam_GRP_PLAT_SYNC_STATE").val()=="P")
	{
		MessageBox.alert("",'该业务集团订购为暂停状态,暂时不能加入成员');
		return false;
	}	
	
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}

function initPageParam()
{
	//初始化服务参数界面
	dealOperStateoptions();
	
	// 变更时不能变服务参数信息
	if($('#pam_MODIFY_TAG').val()=='2'){
		$("#pam_OPER_STATE").closest("li").css("display","none");
		$("#pam_OPER_STATE").css("display","none");
		$("#pam_PLAT_SYNC_STATE").closest("li").css("display","none");
		$("#pam_PLAT_SYNC_STATE").css("display","none");
	}
}

function dealOperStateoptions()
{  
 	var modifytag=$("#pam_MODIFY_TAG").val();
 	var bizattr=$("#pam_BIZ_ATTR").val();
 	
 	$('#pam_OPER_STATE').html("");
 	$('#pam_OPER_STATE option').length = 0;  //清空操作状态选项 
 	if("0"==modifytag)//新增时
 	{ 
 		$("#pam_OPER_STATE").val("01"); //新增
	    $('#pam_OPER_STATE').attr('disabled',true);
 	}
 	else if("2"==modifytag)//对原有记录进行修改时
 	{
 	  var platsyncState=$("#pam_PLAT_SYNC_STATE").val();
 	  if(platsyncState == '')
 	  {
 	    platsyncState="1";
 	  }
 	  if (platsyncState == "P")
 	  {
 		 pam_OPER_STATE.remove("01");
		    pam_OPER_STATE.remove("02");
		    pam_OPER_STATE.remove("04");
		    pam_OPER_STATE.remove("08");
	 		$("#pam_OPER_STATE").val("05");//默认选为恢复
 	  }
 	  else
 	  {
 		 pam_OPER_STATE.remove("01");
		    pam_OPER_STATE.remove("02");
		    pam_OPER_STATE.remove("05");
			$("#pam_OPER_STATE").val("08"); //默认选为变更
 	  }
 	} 
 	
 	setStatetype();		
}

/**
 * 作用：根据不同的操作类型，页面输入框的可见性.
 *  	04- 暂停 05-恢复 ,08-变更
 */
function setStatetype(){
    var operState=$("#pam_OPER_STATE").val();
    
    $("#pam_OPER_STATE").attr('disabled',true);
    $("#pam_PLAT_SYNC_STATE").attr('disabled',true);
    $("#pam_BIZ_CODE").attr('disabled',true);
    $("#pam_BIZ_NAME").attr('disabled',true);
    $("#pam_BIZ_IN_CODE").attr('disabled',true);
    $("#pam_BIZ_ATTR").attr('disabled',true);
    $("#pam_EXPECT_TIME").attr('disabled',true);
    $("#pam_BIZ_IN_CODE_A").attr('disabled',true);
    $("#pam_GRP_PLAT_SYNC_STATE").attr('disabled',true);
    
    if (operState == "08"||operState == "04")
    {
	  $("#pam_OPER_STATE").attr('disabled',false);
    } 

}