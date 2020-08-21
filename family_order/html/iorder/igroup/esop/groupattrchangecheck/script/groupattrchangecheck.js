function submitApply(){
	var chackGrpAttrs = $("#EOS_CHECK_GRP_ATTRS").val();
	var checkDate = $("#CHECK_DATE").val();
	var checkName = $("#CHECK_NAME").val();
	var checkOprtion = $("#CHECK_OPRTION").val();
	var auditInfos   = $("#AUDIT_INFO input");
	var eosCheckGrpAttrs =  $("#EOS_CHECK_GRP_ATTRS").val();
	
	var auditOtherData =  new Wade.DatasetList();
	for(var i = 0, size = auditInfos.length; i < size; i++)
	{
		var orderCode = auditInfos[i].id;
		var orderValue = $("#"+orderCode).val();
		
		var orderInfoData = new Wade.DataMap();
		orderInfoData.put("ATTR_VALUE", orderValue);
		orderInfoData.put("ATTR_NAME", auditInfos[i].getAttribute("desc"));
		orderInfoData.put("ATTR_CODE", orderCode);
		orderInfoData.put("RECORD_NUM","0");
		auditOtherData.add(orderInfoData);
	}
	if(""== eosCheckGrpAttrs||null == eosCheckGrpAttrs){
		$.validate.alerter.one($("#EOS_CHECK_GRP_ATTRS")[0], "审核结果不能为空！");
		return false;
	}
	/*var param  = new Wade.DataMap();
	param.put("OTHER_LIST",auditOtherData);
	param.put("EOS_CHECK_GRP_ATTRS",chackGrpAttrs);
	param.put("CHECK_DATE",checkDate);
	param.put("CHECK_NAME",checkName);
	param.put("CHECK_OPRTION",checkName);*/
	var comData;
	if($("#EOS_COMMON_DATA").text()){
		comData = new Wade.DataMap($("#EOS_COMMON_DATA").text());
	}else{
		MessageBox.alert("错误", "未获取到流程公共信息！");
		return false;
	}
	 $.beginPageLoading();
	 ajaxSubmit("","submit", '&EOS_CHECK_GRP_ATTRS=' + chackGrpAttrs +'&CHECK_DATE=' +checkDate+'&CHECK_NAME=' +checkName+'&CHECK_OPRTION=' +checkOprtion+'&OTHER_LIST='+auditOtherData+'&COMMON_DATA='+comData.toString(), null, function(data){
		 $.endPageLoading();
			debugger;
			if(data.get("ASSIGN_FLAG") == "true")
			{
				MessageBox.success("审批成功", "定单号："+data.get("IBSYSID"), function(btn){
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
			}else if(data.get("ALERT_FLAG")== "true"){
				MessageBox.success("流程创建成功", "定单号："+data.get("IBSYSID"), function(btn){
					if("ext1" == btn){
						var urlArr = data.get("ALERT_URL").split("?");
						var ALERT_NAME = data.get("ALERT_NAME");
						var pageName = getNavTitle();
						openNav(ALERT_NAME, urlArr[1].substring(13), '', '&BEFORE_NAV_TITLE='+getNavTitle(), urlArr[0]);
						closeNavByTitle(pageName);
					}
					if("ok" == btn){
						closeNav();
					}
				}, {"ext1" : "下一步"});
			}
			else
			{
				MessageBox.success("审批成功", "定单号："+data.get("IBSYSID"), function(btn){
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
