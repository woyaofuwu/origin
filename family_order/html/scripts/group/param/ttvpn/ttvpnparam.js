function validateParamPage(methodName) {
	if(methodName == 'CrtMb')
	{
		var vpncode = $("#pam_VPN_CODE").val();
		var pre = vpncode.substr(0,1);
		if(vpncode != null){
			if((vpncode.length < 3 ) && (vpncode.length != 0)) {
				alert("短号码必须为大于等于3位的数字！");
				return false;
			}
			if(pre=='1'){
				alert("短号码不能用【1】开头！");
				return false;
			}
			if(pre=='0'){
				alert("短号码不能用【0】开头！");
				return false;
			}
		}
	}
	return true;
}