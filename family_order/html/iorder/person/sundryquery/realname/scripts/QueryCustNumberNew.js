//证件号码查询 
function queryCustNumber(){
	// 查询条件校验
	if (!$.validate.verifyAll("psptPart")) {
		return false;
	}
	
	var tag = $("#ProtraitCompare_Tag").val();
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	
	if (tag && tag != '1' && (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2")){
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit(null, 'verifyNOTCMPPriv', null, '', function(data){
			$.endPageLoading();			
			var permission = data.get("hasNOTCMPPriv");
			if(permission && permission == "true"){			
				tag = '1';
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			MessageBox.error("提示信息",error_info);
		},{
				"async" : false
		}
		);
	}

	if (tag && tag != '1' && (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2")){
		//弹框人像比对
        popupPage("请进行人像比对", "portraitComparison.PortraitComparison", "init", "", null, "full");
	}else {
		$.beginPageLoading("正在查询数据...");
		ajaxSubmit('psptPart', 'queryCustNumber','', 'QueryListPart', function(rtnData) { 
			$.endPageLoading();
			if(rtnData.get('VISIT_ALARM')=="1"){
                MessageBox.alert("告警提示", "访问已达到预警阀值!", function(btn) {
                });
			}
		}, function(code, info, detail) {
			$.endPageLoading();
			MessageBox.error("错误提示", info);
		}, function() {
			$.endPageLoading();
			MessageBox.alert("告警提示", "查询超时!", function(btn) {
			});
		});
		$("#ProtraitCompare_Tag").val("0");
	}
}

function queryCustNumberMod(){
	//查询条件校验
	if (!$.validate.verifyAll("psptPart")) {
		return false;
	}
	
	
	var tag = $("#ProtraitCompare_Tag").val();
	
	var psptTypeCode = $("#PSPT_TYPE_CODE").val();
	
	if (tag && tag != '1' && (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2")){
		$.beginPageLoading("正在查询数据...");
		$.ajax.submit(null, 'verifyNOTCMPPriv', null, '', function(data){
			$.endPageLoading();			
			var permission = data.get("hasNOTCMPPriv");
			if(permission && permission == "true"){			
				tag = '1';
			}
		},
		function(error_code,error_info,derror){
			$.endPageLoading();
			MessageBox.error("提示信息",error_info);
		},{
				"async" : false
		}
		);
	}

	if (tag && tag != '1' && (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2")){
		//弹框人像比对
        popupPage("请进行人像比对", "portraitComparison.PortraitComparison", "init", "", null, "full");
	}else {
	    $.beginPageLoading("正进行金库认证...");
		$.treasury.auth("CUST_4A_querySnByUsrpidMod",function(ret)
		{
	        $.endPageLoading();
			if(true === ret)
			{  
				var param ="&X_DATA_NOT_FUZZY=true";
				ajaxSubmit('psptPart', 'queryCustNumber',param, 'QueryListPart', function(rtnData) {
                    if(rtnData.get('VISIT_ALARM')=="1"){
                        MessageBox.alert("告警提示", "访问已达到预警阀值!", function(btn) {
                        });
                    }
				}, function(code, info, detail) {
					MessageBox.error("错误提示", info);
				}, function() {
					MessageBox.alert("告警提示", "查询超时!", function(btn) {
					});
				});
			}
			else
			{
				MessageBox.alert("错误提示", "认证失败");
				return false;
			}
		});
	    $.endPageLoading();
		$("#ProtraitCompare_Tag").val("0");
	}
}

/**
 * 扫描读取身份证信息
 */
function clickScanPspt(){
    getMsgByEForm("PSPT_ID",null,null,null,null,null,null,null);
}
