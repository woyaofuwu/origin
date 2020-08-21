function initPageParam_110000008001()
{

}

//提交
function checkSubSvpn(obj)
{
	var operType=$("#cond_OPER_TYPE").val();
	
	if (operType=='CrtUs')
	{
    	var vpn_infos = $("#pam_VPN_INFOS").val();
    	var vpn_no = $("#pam_VPN_NO").val();
    	if(vpn_infos != "[]" && vpn_infos != "")	
    	{
    		if(vpn_no == "") 
    		{
    			$.validate.alerter.one($("#pam_VPN_NO")[0],"\u8BF7\u9009\u62E9\u0056\u0050\u004E\u7F16\u7801\uFF01");
				return false;
    		}
    	}
	}
	
	//不传到后台
	$("#pam_VPN_INFOS").val("");
	
	if (!chkPacDigitshead())
	{
		return false;
	}
	
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}

function chkPacDigitshead() 
{
	var pac = $.trim($("#pam_GRP_PAC_DIGITSHEAD").val());
	
	if(pac == "") 
	{
		$.validate.alerter.one($("#pam_GRP_PAC_DIGITSHEAD")[0],"\u51fa\u7fa4\u5b57\u51a0\u4e0d\u80fd\u4e3a\u7a7a\uff01");
		return false;
	}
	if(pac != "1" && pac != "9") 
	{
		$.validate.alerter.one($("#pam_GRP_PAC_DIGITSHEAD")[0],"\u51fa\u7fa4\u5b57\u51a0\u5fc5\u987b\u662f\u0031\u6216\u0039\uff01");
		return false;
	}
	$("#pam_GRP_PAC_DIGITSHEAD").val(pac);
	return true;
}