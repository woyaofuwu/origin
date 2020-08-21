function submitApply(){
	var chackGrpAttrs = $("#EOS_CHECK_GRP_ATTRS").val();
	var checkDate = $("#CHECK_DATE").val();
	var checkName = $("#CHECK_NAME").val();
	var checkOprtion = $("#CHECK_OPRTION").val();

	 $.beginPageLoading();
	 ajaxSubmit("","submitChange", '&EOS_CHECK_GRP_ATTRS=' + chackGrpAttrs +'&CHECK_DATE=' +checkDate+'&CHECK_NAME=' +checkName+'&CHECK_OPRTION=' +checkOprtion, null, function(data){
			$.endPageLoading();	
		}, function(error_code, error_info) {
			MessageBox.error(error_code, error_info);
			$.endPageLoading();
		});
}