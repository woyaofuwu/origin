ModifyOrderOfferTime = function(id) {
	this.id = id;
	this.activeDateField = null;
	this.startDateContainer = this.id + "_startDateFieldContainer";
	this.startDateField = this.id + "_startDateField";
	this.endDateField = this.id + "_endDateField";
	this.realEndDateField = this.id + "_realEndDateField";
	
	//平台赠送设置
	this.startDatePlatField = this.id + "_startDatePlatField";
	this.endDatePlatField = this.id + "_endDatePlatField";
	this.giftSnPlatField = this.id + "_giftSnPlatField";

};
ModifyOrderOfferTime.prototype = {
	render : function(offer, fn) {
		this.offer = offer;
		this.afterSetTimeAction = fn;
		this.initStartDateField();
		this.initEndDateField();
		showPopup(this.id + "_TimePop", this.id + "_TimeItem");
	},
	initStartDateField : function() {
		var offer = this.offer;
		var startDate = offer.get("START_DATE");
		$("#" + this.startDateField).val(startDate.substring(0, 10));
		if("1" == offer.get("MODIFY_TAG") || "exist" == offer.get("MODIFY_TAG")) {
			$("#" + this.startDateField).attr("disabled", true)
		}else {
			$("#" + this.startDateField).attr("disabled", false)
		}
	},
	initEndDateField : function() {
		var offer = this.offer;
		var endDate = offer.get("END_DATE");
		$("#" + this.endDateField).val(endDate.substring(0, 10));
		if("0" == offer.get("MODIFY_TAG") || "exist" == offer.get("MODIFY_TAG")) {
			$("#" + this.endDateField).attr("disabled", true)
		}else {
			$("#" + this.endDateField).attr("disabled", false)
		}
	},
	changeStartDate : function() {
		var offer = this.offer;
		var newStartDate = $("#" + this.startDateField).val();
		var currentDate = new Date().format("yyyy-MM-dd");
		if(newStartDate < currentDate){
        	MessageBox.alert("开始时间不能小于当前时间");
        	$("#" + this.startDateField).val(offer.get("START_DATE").substring(0, 10));
        	return false;
        }
		
		$.beginPageLoading("加载中。。。。。");
		var that = this;
		var params = "&ACTION=CAL_ENDDATE" + "&OFFER=" + offer + "&BOOKING_DATE=" + newStartDate;
		params += changeoffer.initDataParam();
		$.ajax.submit(null, null, params, this.id + "Panel", function(data) {
			that.afterChangeStartDate(data);
		});
	},
	afterChangeStartDate : function(data) {
		var startDate = data.get("START_DATE");
		var endDate = data.get("END_DATE");
		
		$("#" + this.startDateField).attr("disabled", false);
		$("#" + this.startDateField).val(startDate);
		$("#" + this.endDateField).val(endDate.substring(0, 10));
		$("#" + this.realEndDateField).val(endDate.substring(0, 10));
		
		$.endPageLoading();
	},
	
	changeEndDate : function() {
		var offer = this.offer;
		var newEndDate = $("#" + this.endDateField).val();
		offer.put("POP_END_DATE", newEndDate);
		var currentDate = new Date().format("yyyy-MM-dd");
		if(newEndDate < currentDate){
        	MessageBox.alert("结束时间不能小于当前时间");
        	$("#" + this.endDateField).val(offer.get("END_DATE").substring(0, 10));
        	return false;
        }
		
		$.beginPageLoading("加载中。。。。。");
		var that = this;
		var params = "&ACTION=CAL_ENDDATE" + "&OFFER=" + offer.toString() + "&BOOKING_DATE=" + newEndDate;
		params += changeoffer.initDataParam();
		$.ajax.submit(null, null, params, this.id + "Panel", function(data) {
			that.afterChangeEndDate(data);
		});
	},
	afterChangeEndDate : function(data) {
		var startDate = data.get("START_DATE");
		var endDate = data.get("END_DATE");
		var popEndDate = data.get("POP_END_DATE");
		
		$("#" + this.startDateField).val(startDate);
		$("#" + this.endDateField).attr("disabled", false)
		$("#" + this.endDateField).val(popEndDate);
		$("#" + this.realEndDateField).val(endDate);
		
		$.endPageLoading();
	},

	setTime : function() {
		var offer = this.offer;
		var oldStartDate = offer.get("START_DATE");
		var newStartDate = $("#" + this.startDateField).val();
		if (oldStartDate != newStartDate) {
			if (!offer.containsKey("OLD_START_DATE")) {
				offer.put("OLD_START_DATE", offer.get("START_DATE"));
			}

			offer.put("START_DATE", newStartDate);
		}

		var oldEndDate = offer.get("END_DATE");
		var newEndDate = $("#" + this.realEndDateField).val();
		if (oldEndDate.substring(0, 10) != newEndDate) {
			if (!offer.containsKey("OLD_END_DATE")) {
				offer.put("OLD_END_DATE", offer.get("END_DATE"));
			}
			offer.put("END_DATE", newEndDate + " 23:59:59");
		}
		if (typeof (this.afterSetTimeAction) == "function") {
			this.afterSetTimeAction();
		}
		this.afterSetTimeAction = null;
	},
	
	ok : function(obj) {
		if (this.checkDate($("#" + this.startDateField).val(), $("#" + this.endDateField).val())) {
			this.setTime();
			hidePopup(obj);
		}
	},

	checkDate : function(startDate, endDate) {
		var isCheck = $.verifylib.checkDate(endDate, "yyyy-MM-dd");
		if (isCheck) {
			if (startDate > endDate) {
				alert("结束时间不能小于开始时间");
				return false;
			} else {
				var isDate = this.isDate(endDate);
				if (!isDate) {
					alert("输入的日期不正确");
					return false;
				}
			}
		} else {
			alert("输入有误，请重新输入");
			return false;
		}
		return true;
	},
	isDate : function(dateValue) {
		var regex = new RegExp(
				"^(?:(?:([0-9]{4}(-|\/)(?:(?:0?[1,3-9]|1[0-2])(-|\/)(?:29|30)|((?:0?[13578]|1[02])(-|\/)31)))|([0-9]{4}(-|\/)(?:0?[1-9]|1[0-2])(-|\/)(?:0?[1-9]|1\\d|2[0-8]))|(((?:(\\d\\d(?:0[48]|[2468][048]|[13579][26]))|(?:0[48]00|[2468][048]00|[13579][26]00))(-|\/)0?2(-|\/)29))))$");
		if (!regex.test(dateValue)) {
			return false;
		}
		return true;
	},
	
	
	okPlat : function(obj) {
		if (this.checkDate($("#" + this.startDatePlatField).val(), $("#" + this.endDatePlatField).val())) {
			this.setTimePlat();
			hidePopup(obj);
		}
	},
	
	renderPlat : function(offer, fn) {
		this.offer = offer;
		this.afterSetTimeAction = fn;
		if(offer.get("GIFT_SERIAL_NUMBER")!="undefined"&&offer.get("GIFT_SERIAL_NUMBER")!=null){
			$("#" + this.giftSnPlatField).val(offer.get("GIFT_SERIAL_NUMBER"));
			$("#" + this.endDatePlatField).val(offer.get("GIFT_END_DATE"));
		}else{
			$("#" + this.giftSnPlatField).val("");
			$("#" + this.endDatePlatField).val("");
		}
		
		//this.initStartDateField();
		//this.initEndDateField();
		showPopup(this.id + "_PlatPop", this.id + "_PlatItem");
	},
	setTimePlat : function() {
		var offer = this.offer;
        var giftSerialNumber = $("#" + this.giftSnPlatField).val();
		var serialNumber = $.auth.getAuthData().get("USER_INFO").get(
				"SERIAL_NUMBER")

		if ($.trim(giftSerialNumber) == '') {
			MessageBox.alert("提示", "赠送号码不能为空");
			return false;
		}

		if (giftSerialNumber == serialNumber) {
			MessageBox.alert("提示", "赠送号码不能是本人的号码");
			return false;
		}
        var giftStartDate = offer.get("START_DATE");
        var giftEndDate =  $("#" + this.endDatePlatField).val();
        if(giftEndDate<=giftStartDate){
            MessageBox.alert("提示","赠送结束时间不能小于赠送开始时间");
            return false;
        }
		
        offer.put("GIFT_SERIAL_NUMBER",giftSerialNumber);
        offer.put("GIFT_START_DATE",giftStartDate);
        offer.put("GIFT_END_DATE",giftEndDate);
        MessageBox.alert("赠送设置成功！");
	},
	cancelGiftPlat : function(obj) {
		var offer = this.offer;		
		if (offer.containsKey("GIFT_SERIAL_NUMBER")) {
			offer.removeKey("GIFT_SERIAL_NUMBER");
			$("#" + this.giftSnPlatField).val("");
		}
		if (offer.containsKey("GIFT_START_DATE")) {
			offer.removeKey("GIFT_START_DATE");
			$("#" + this.startDatePlatField).val();
		}
		if (offer.containsKey("GIFT_END_DATE")) {
			offer.removeKey("GIFT_END_DATE");
			$("#" + this.endDatePlatField).val();
		}
		hidePopup(obj);
	}
	
	
}
