function submitColorring(obj)
{
	
	if(!submitOfferCha())
		return false; 
	
	var membEndDate = $("#pam_MEB_END_DATE").val();
	if("" != membEndDate){
	
		var date = membEndDate.split("/");
		var year = date[0];
		var month = date[1];
		var day = new Date(year, month, 0);
		var lastDate = year + "/" + month + "/" + day.getDate();
		if (membEndDate != lastDate) {
			$.validate.alerter.one($("#pam_MEB_END_DATE")[0], "产品参数信息中,到期日期只能为选择月份的最后一天！\n");			
		   	return false;
		}
	}
	backPopup(obj);
}