$(function(){
	debugger;
	var exceptionId =$("#EXCEPTION").val();
	if(exceptionId){
		MessageBox.error("错误信息", exceptionId, function(btn){
			$.nav.close();
		});
	}
});




function submit(){
	debugger;
	if(!$.validate.verifyAll("IDC_PARAM_UL")){
		return false;
	}
	
	if(!$.validate.verifyAll("IDC_COMMON_UL")){
		return false;
	}
	var submitParam = new Wade.DataMap();
	submitParam.put("COMMON_DATA", saveEosCommonData());
	submitParam.put("BUSI_SPEC_RELE", saveBusiSpecReleData());
	submitParam.put("NODE_TEMPLETE", saveNodeTempleteData());
	submitParam.put("OTHER_LIST", saveAuditData());
	submitParam.put("IDC_DATA", saveIdcInfo());

	
	$.beginPageLoading("数据提交中，请稍后...");
	$.ajax.submit("", "submitForIdc", "&SUBMIT_PARAM="+submitParam.toString(), "", function(data){
		$.endPageLoading();
		
		if(data.get("ASSIGN_FLAG") == "true")
		{
			MessageBox.success("确认完成", "", function(btn){
				if("ext1" == btn){
					debugger;
					var urlArr = data.get("ASSIGN_URL").split("?");
					var pageName = getNavTitle();
					openNav('指派', urlArr[1].substring(13), '', '&BEFORE_NAV_TITLE='+getNavTitle(), urlArr[0]);
					closeNavByTitle(pageName);
				}
				if("ok" == btn){
					closeNav();
				}
			}, {"ext1" : "指派"});
		}
		else
		{
			MessageBox.success("确认完成", "", function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}


function saveEosCommonData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	if(!eosCommonData.get("CUST_NAME"))
	{
		eosCommonData.put("CUST_NAME", $("#CUST_NAME").val());
	}
	return eosCommonData;
}

function saveAuditData(){
	var auditChaSpecList = new Wade.DatasetList();
	var staffData = new Wade.DataMap();
	staffData.put("ATTR_CODE","CONSTRUCTION_STAFF_ID");
	staffData.put("ATTR_VALUE",$("#CONSTRUCTION_STAFF_ID").val());
	staffData.put("ATTR_NAME",$("#CONSTRUCTION_STAFF_ID").attr("desc"));
	auditChaSpecList.add(staffData);
	var auditResultData = new Wade.DataMap();
	auditResultData.put("ATTR_CODE","CONSTRUCTION_STATE");
	auditResultData.put("ATTR_VALUE",$("#CONSTRUCTION_STATE").val());
	auditResultData.put("ATTR_NAME",$("#CONSTRUCTION_STATE").attr("desc"));
	auditChaSpecList.add(auditResultData);
	var auditTextData = new Wade.DataMap();
	auditTextData.put("ATTR_CODE","AUDIT_TEXT");
	auditTextData.put("ATTR_VALUE",$("#AUDIT_TEXT").val());
	auditTextData.put("ATTR_NAME",$("#AUDIT_TEXT").attr("desc"));
	auditChaSpecList.add(auditTextData);
	
	return auditChaSpecList;
}

function saveBusiSpecReleData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	
	var busiSpecRele = new Wade.DataMap();
	busiSpecRele.put("NODE_ID", eosCommonData.get("NODE_ID"));
	
	return busiSpecRele;
}

function saveNodeTempleteData()
{
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	
	var nodeTemplete = new Wade.DataMap();
	nodeTemplete.put("BPM_TEMPLET_ID", eosCommonData.get("BPM_TEMPLET_ID"));
	nodeTemplete.put("BUSI_TYPE", eosCommonData.get("BUSI_TYPE"));
	nodeTemplete.put("BUSI_CODE", eosCommonData.get("BUSI_CODE"));
	nodeTemplete.put("IN_MODE_CODE", eosCommonData.get("IN_MODE_CODE"));
	
	return nodeTemplete;
}

//保存专线产品数据
function saveIdcInfo(){
	debugger;
	var eosCommonData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	var newIDCDataset = new Wade.DatasetList();
//	if(eosCommonData.get("BPM_TEMPLET_ID")=='EDIRECTLINEOPENIDC'){
		$("#IDC_PARAM_UL input[name=POOL_IBSYSID]").each(function(){
			var newIDCData = new Wade.DataMap();
			var newIDCattrDataset = new Wade.DatasetList();
			var ibsysid= $(this).val();
			var busiCode= "";

			var poolCodeInfos = $(this).closest("ul").find("input[name=POOL_CODE]");
			for (var j=0;j<poolCodeInfos.length;j++){
				var poolCodeInfo=poolCodeInfos[j];
				busiCode=$("#"+poolCodeInfo.id).val();
				if($("#"+poolCodeInfo.id).val()=='7041'){
					$("#"+poolCodeInfo.id).closest("ul").closest("li").find("div[id*=new] li input[name=IDC_7041_EquipmentName]").each(function(){
						var idcInfos = $(this).closest("ul").find("input");
						var idIndex=$(this).attr("indexx");
						var index=parseInt(idIndex)+1;
						for (var i=0;i<idcInfos.length;i++){
							var idcInfo=idcInfos[i];
							
							var newIdcData = new Wade.DataMap();
							newIdcData.put("ATTR_VALUE", idcInfo.value);
							newIdcData.put("ATTR_NAME", $("#"+idcInfo.id).attr("desc"));
							newIdcData.put("ATTR_CODE", idcInfo.name);
							newIdcData.put("GROUP_SEQ", index);
							newIdcData.put("RECORD_NUM", index);
							newIDCattrDataset.add(newIdcData);
						}
					});
				}
			}
			var newIdcData = new Wade.DataMap();
			var mgAffirmTime=$("#IDC_MgAffirmTime");
			newIdcData.put("ATTR_VALUE", mgAffirmTime.val());
			newIdcData.put("ATTR_NAME", mgAffirmTime.attr("desc"));
			newIdcData.put("ATTR_CODE", mgAffirmTime.attr("name"));
			newIdcData.put("GROUP_SEQ", "0");
			newIdcData.put("RECORD_NUM", "0");
			newIDCattrDataset.add(newIdcData);
			
			newIDCData.put("REL_IBSYSYID", ibsysid);
			newIDCData.put("REL_BUSI_CODE", busiCode);
			newIDCData.put("IDCATTR_LIST", newIDCattrDataset);
			newIDCDataset.add(newIDCData);

		});
//	}
	
	/*if(eosCommonData.get("BPM_TEMPLET_ID")=='EDIRECTLINEOPENIDC'&&eosCommonData.get("BUSI_CODE")=='7041'){
		$("#IDC_PARAM_UL input[name=IDC_7041_EquipmentName]").each(function(){
			var idcInfos = $(this).closest("ul").find("input");
			var idIndex=$(this).attr("id").replace("IDC_7041_EquipmentName","");
			var index=parseInt(idIndex)+1;
			for (var i=0;i<idcInfos.length;i++){
				var idcInfo=idcInfos[i];
				
				var newIdcData = new Wade.DataMap();
				newIdcData.put("ATTR_VALUE", idcInfo.value);
				newIdcData.put("ATTR_NAME", $("#"+idcInfo.id).attr("desc"));
				newIdcData.put("ATTR_CODE", idcInfo.name);
				newIdcData.put("GROUP_SEQ", index);
				newIdcData.put("RECORD_NUM", index);
				newIDCDataset.add(newIdcData);
			}
		});
	}*/
	
	
	return newIDCDataset;

}


function changeWay1(el)
{
	if(el.value==''){
		return false;
	}
  var date = new Date();
  var time = '';
  date.setTime(date.getTime());
  var effectiveWay = el.value;
  var effectiveWayId=el.id;
  var takeEffectTimeId=effectiveWayId.replace("IDC_7041_TakeEffectType","IDC_7041_TakeEffectTime");
  if(effectiveWay=='0')
  {	
	  //如果是最后一天不能选择次月
	  date.setTime(date.getTime()+24*60*60*1000);
	  if(date.getDate()=='1'){
		  $("#"+takeEffectTimeId).val("");
		  $("#"+takeEffectTimeId).val("");
		  $("#"+effectiveWayId).val("");
		  MessageBox.alert("提示","月末最后一天不能选择次月生效");
		  return;
	  }
	  date.setTime(date.getTime()-24*60*60*1000);
	  if(date.getMonth()+2 > '12'){
		  time = (date.getFullYear()+1)+"-01-01";
	  }else{
		  time = date.getFullYear()+"-" + PrefixInteger((date.getMonth()+2),2) + "-01";
	  }
	  $("#"+takeEffectTimeId).attr("disabled","ture");
	  $("#"+takeEffectTimeId).val(time+" 00:00:00");
  }else if(effectiveWay=='1'){
	  date.setTime(date.getTime()+24*60*60*1000);
	  time = date.getFullYear()+"-" + PrefixInteger((date.getMonth()+1),2) + "-" + PrefixInteger(date.getDate(),2);
	  $("#"+takeEffectTimeId).attr("disabled","ture");
	  $("#"+takeEffectTimeId).val(time+" 00:00:00");
  }else if(effectiveWay=='2'){
	  $("#"+takeEffectTimeId).attr("disabled","");
	  $("#"+takeEffectTimeId).val("");
  }
}
function PrefixInteger(num, length) {
	return (Array(length).join('0') + num).slice(-length);
}

