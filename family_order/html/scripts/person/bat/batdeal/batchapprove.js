function saveAudiInfo() {
	var auditInfo = $("#APPROVEFLAG").val();
	var auditRemark = $("#REMARK").val();
	$.setReturnValue({"AUDIT_INFO":auditInfo,"AUDIT_REMARK":auditRemark});
}