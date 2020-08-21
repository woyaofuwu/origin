var addressAdepter = function() {
	var srcObj = null;
	function setAddrPopupPageReturnValue(obj) {
		srcObj.setAddrPopupReturnValue(obj);
	}

	function popupPageAddressPage(that, type) {
		srcObj = that;
		var serialNumber = that.find("input[name=SERIAL_NUMBER]").val();
		var contactSn = that.find("input[name=CONTACT_PHONE]").val();
		var custName = that.find("input[name=CONTACT]").val();
		var param = "&AUTH_SERIAL_NUMBER=" + serialNumber + "&CUST_NAME=" + encodeURIComponent(custName) + "&CONTACT_SN=" + contactSn;
		param += "&IS_FAMILY_WIDE=" + "Y";// 家庭宽带过来

		if ("0" == type) {
			popupPage("标准地址选择", "res.popup.AddressQryNew", "init", param + '&TREE_TYPE=0', "iorder", "c_popup c_popup-full", null, function() {
				srcObj = null;
			});
		} else if ("1" == type) {
			popupPage("树状地址选择", "res.popup.AddressQryTreeNew", "init", param + '&TREE_TYPE=0', "iorder", "c_popup c_popup-full", null, function() {
				srcObj = null;
			});
		}
	}

	return {
		popupPageAddressPage : popupPageAddressPage,
		setAddrPopupPageReturnValue : setAddrPopupPageReturnValue
	}
}();