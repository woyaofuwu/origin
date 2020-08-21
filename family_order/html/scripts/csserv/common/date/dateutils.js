if(typeof(DateUtils)=="undefined"){
	window["DateUtils"]=function(){};
	DateUtils.prototype={
		toDate: function(strDate) {
			if (strDate.length == 19) {	// YYYY-MM-DD HH:MI:SS
				var year  = strDate.substring(0, 4);
				var month = strDate.substring(5, 7);
				var date  = strDate.substring(8, 10);
				var hour  = strDate.substring(11, 13);
				var min   = strDate.substring(14, 16);
				var sec   = strDate.substring(17, 19);
				return new Date(year, month - 1, date, hour, min, sec);
			} else if (strDate.length == 10) { // "YYYY-MM-DD"
				var year  = strDate.substring(0, 4);
				var month = strDate.substring(5, 7);
				var date  = strDate.substring(8, 10);
				return new Date(year, month - 1, date);
			} else if (strDate.length == 7) { // "YYYY-MM"
				var year  = strDate.substring(0, 4);
				var month = strDate.substring(5, 7);
				return new Date(year, month - 1);
			} else if (strDate.length == 4) { // "YYYY"
				var year  = strDate.substring(0, 4);
				return new Date(year);		
			} else {
				alert("DateUtils.toDate(strDate) error! invalid argument(format).");
			}
		},
		
		//转换成string类型 date参数为日期格式 可用toDate转换
		toString: function(date, format) {
			var strDate;
			var year  = date.getFullYear();
			var month = date.getMonth() + 1;
			var day   = date.getDate();
			var hour  = date.getHours();
			var min   = date.getMinutes();
			var sec   = date.getSeconds();
			month = (parseInt(month) < 10) ? ("0" + month) : (month);
			day   = (parseInt(day)   < 10) ? ("0" + day )  : (day);
			hour  = (parseInt(hour)  < 10) ? ("0" + hour)  : (hour);
			min   = (parseInt(min)   < 10) ? ("0" + min)   : (min);
			sec   = (parseInt(sec)   < 10) ? ("0" + sec)   : (sec);
			if ("YYYY-MM-DD HH:MI:SS" == format) {
				strDate = year + "-" + month + "-" + day + " " + hour + ":" + min + ":" + sec;
			} else if ("YYYY-MM-DD" == format) {
				strDate = year + "-" + month + "-" + day;
			} else if ("YYYY-MM" == format) {
				strDate = year + "-" + month;	
			} else if ("YYYY" == format) {
				strDate = year;		
			} else {
				alert("DateUtils.toString(date, format) error! invalid argument(format).");
			}
			return strDate;
		},
		
		getMonthDays: function(date,month) {
			var year = date.getFullYear();
			if (typeof month == "undefined") {
				month = date.getMonth();
			}
			if (((0 == (year%4)) && ( (0 != (year%100)) || (0 == (year%400)))) && month == 1) {
				return 29;
			} else {
				return DateUtils_MD[month];
			}
		},
		
		addSeconds: function(dayOffset, strBaseDate) {
			var date = (arguments.length == 1) ? this.toDate(this.today()) : this.toDate(strBaseDate);
			date = new Date(date.getTime() + parseInt(dayOffset) * 1000);
			return this.toString(new Date(date), "YYYY-MM-DD HH:MI:SS");
		},
				
		addDays: function(dayOffset, strBaseDate) {
			var date = (arguments.length == 1) ? this.toDate(this.today()) : this.toDate(strBaseDate);
			date = new Date(date.getTime() + parseInt(dayOffset) * 24 * 3600 * 1000);
			return this.toString(new Date(date), "YYYY-MM-DD HH:MI:SS");
		},
		
		addMonths: function(monthOffset, strBaseDate) {
			var date = (arguments.length == 1) ? this.toDate(this.today()): this.toDate(strBaseDate);
			var month=date.getMonth();
			var cd=date.getDate();
			var td=this.getMonthDays(date,date.getMonth() + parseInt(monthOffset));
			if(cd > td){date.setDate(td);}
			date.setMonth(date.getMonth() + parseInt(monthOffset));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		addMonthsForStart: function(monthOffset, strBaseDate) {
			var strDate = (arguments.length == 1) ? this.today() : strBaseDate;
			strDate = this.addMonths(monthOffset, strDate);
			return this.firstDayOfMonth(strDate);
		},
		
		addMonthsForEnd: function(monthOffset, strBaseDate) {
			var strDate = (arguments.length == 1) ? this.today() : strBaseDate;
			strDate = this.addMonths(monthOffset, strDate);
			return this.addDays(-1, this.firstDayOfMonth(strDate));
		},
		
		addYears: function(yearOffset, strBaseDate) {
			var date = (arguments.length == 1) ? this.toDate(this.today()) : this.toDate(strBaseDate);
			date.setYear(date.getYear() + parseInt(yearOffset));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		addYearsForStart: function(yearOffset, strBaseDate) {
			var strDate = (arguments.length == 1) ? this.today() : strBaseDate;
			strDate = this.addYears(yearOffset, strDate);
			return this.firstDayOfYear(strDate);
		},
		
		addYearsForEnd: function(yearOffset, strBaseDate) {
			var strDate = (arguments.length == 1) ? this.today() : strBaseDate;
			strDate = this.addYears(yearOffset, strDate);
			return this.firstDayOfYear(strDate);
		},
		
		addYearsPrevDay: function(yearOffset, strBaseDate) {
			var strDate = (arguments.length == 1) ? this.today() : strBaseDate;
			strDate = this.addYears(yearOffset, strDate);
			return this.addDays(-1,strDate);
		},
		
		sunOfWeek: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date = new Date(date - (date.getDay()) * (24 * 3600 * 1000));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");	
		},
		
		monOfWeek: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date = new Date(date - (date.getDay() - 1) * (24 * 3600 * 1000));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		tueOfWeek: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date = new Date(date - (date.getDay() - 2) * (24 * 3600 * 1000));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		wedOfWeek: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date = new Date(date - (date.getDay() - 3) * (24 * 3600 * 1000));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		turOfWeek: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date = new Date(date - (date.getDay() - 4) * (24 * 3600 * 1000));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		friOfWeek: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date = new Date(date - (date.getDay() - 5) * (24 * 3600 * 1000));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		satOfWeek: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date = new Date(date - (date.getDay() - 6) * (24 * 3600 * 1000));
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		firstDayOfMonth: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date.setDate(1);
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		lastDayOfMonth: function(strDate) {
			strDate = (arguments.length == 0) ? this.today() : (strDate);
			strDate = this.addMonths(1, strDate);
			strDate = this.firstDayOfMonth(strDate);
			strDate = this.addDays(-1, strDate);
			return strDate;
		},
		
		firstDayOfYear: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date.setMonth(0);
			date.setDate(1);
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		lastDayOfYear: function(strDate) {
			var date = (arguments.length == 0) ? this.toDate(this.today()) : this.toDate(strDate);
			date.setMonth(11);
			date.setDate(31);
			return this.toString(date, "YYYY-MM-DD HH:MI:SS");
		},
		
		today: function(format) {
			var today;
			if(getToday && typeof(getToday)=="function"){
				today=getToday();
				if(!today){alert("getToday方法取值为空!");}
			}else{alert("getToday方法不存在，将返回客户端当前日期!");}
			if(!today){
				if (arguments.length == 0) {
					today=this.toString(new Date(), "YYYY-MM-DD");
				} else {
					today=this.toString(new Date(), format);
				}
			}
			return today;
		}
	};
}
var dateUtils = new DateUtils();