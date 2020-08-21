/**
 TODO:将字符串预编码成JSON的value中不产生冲突的串.
 */
var JCODEC = {
    enc_chars: '{}[]:\"\'\r\n',

    encode: function (src) {
        var reg = /[\(](?=[#]*[0-9]+\))/ig;
        var s = src.replace(reg, "(#");

        var i, c;
        for (i = 0; i < this.enc_chars.length; i++) {
            c = this.enc_chars[i];
            while (s.indexOf(c) > -1)
                s = s.replace(c, "(" + (i + 1) + ")");
        }
        return s;
    },

    decode: function (src) {
        var i, c, t;
        var s = src;
        for (i = 0; i < this.enc_chars.length; i++) {
            c = this.enc_chars[i];
            t = "(" + (i + 1) + ")";
            while (s.indexOf(t) > -1)
                s = s.replace(t, c);
        }

        var reg = /[\(]#(?=[#]*[0-9]+\))/ig;
        return s.replace(reg, "(");
    }
};

/**
 通用函数
 */
var __System = __System || {
    types: { Android: "Android", iOS: "iOS", Windows: "Windows", Macintosh: "Macintosh", Unknown: "Unknown" },

    type: function () {
        if (navigator.userAgent.toLowerCase().indexOf("iphone") > -1
            || navigator.userAgent.toLowerCase().indexOf("ipad") > -1
            || navigator.userAgent.toLowerCase().indexOf("ipod") > -1) {
            return this.types.iOS;
        }
        else if (navigator.userAgent.toLowerCase().indexOf("android") > -1) {
            return this.types.Android;
        }
        else if (navigator.userAgent.toLowerCase().indexOf("windows") > -1) {
            return this.types.Windows;
        }
        else if (navigator.userAgent.toLowerCase().indexOf("macintosh") > -1) {
            return this.types.Macintosh;
        }

        return this.types.Unknown;
    },

    RESERVED: null
};

/**
 实现Windows平台下的接口函数
 */
var __Windows = __Windows || {
    //RE-Package encoding & decoding function.
    enc: function (str) { return JCODEC.encode(str); },
    dec: function (str) { return JCODEC.decode(str); },

    //Transmit operation to OBJ-C side
    transmit: function (type, param) {
        var action = { action: null, param: null };
        action.action = this.enc(type);
        action.param = this.enc(param);

        window.location = "jcodec:" + this.enc($.stringifyJSON(action));
    },

    navigate: function (url) {
        //window.open(url);
        if (typeof (showModalDialog) != "undefined")
            showModalDialog(url, '对话框', 'dialogWidth:1024px;dialogHeight:720px; dialogLeft:200px;dialogTop:0px;center:yes;help:yes;resizable:no;status:no');
        else
            window.open(url);
    },

    modal: function (url) {
        if (typeof (showModalDialog) != "undefined")
            showModalDialog(url, '对话框', 'dialogWidth:1024px;dialogHeight:720px; dialogLeft:200px;dialogTop:0px;center:yes;help:yes;resizable:no;status:no');
        else
            window.open(url);
    },

    openSafari: function (url) {
        if (typeof (showModalDialog) != "undefined")
            showModalDialog(url, '对话框', 'dialogWidth:1024px;dialogHeight:720px; dialogLeft:200px;dialogTop:0px;center:yes;help:yes;resizable:no;status:no');
        else
            window.open(url);
    },

    openOpera: function (url) {
        if (typeof (showModalDialog) != "undefined")
            showModalDialog(url, '对话框', 'dialogWidth:1024px;dialogHeight:720px; dialogLeft:200px;dialogTop:0px;center:yes;help:yes;resizable:no;status:no');
        else
            window.open(url);
    },

    dialog: function (title, acts, fn) {
        Callbacks.DialogCallback = fn;
        var str = this.enc(title);
        for (var idx in acts) {
            str += "," + this.enc(acts[idx]);
        }

        var r = prompt(title, "输入0~" + (acts.length - 1) + "的一个选项");
        if (r) {
            if (isNaN(r) || r >= acts.length)
                alert("您输入的信息不正确");
            else
                fn(r, acts[r]);
        }
    },

    /**
     ajax: function (idx, type, url, mime, headers) {
		var param = {
		idx: idx,
		type: type,
		url: url,
		mime: mime,
		headers: headers
		};
		//this.transmit("ajax", $.stringifyJSON(param));
		alert("Will ignore ajax request : " + $.stringifyJSON(param));
		}
     */

    goLocation: function (url) {
        window.location = url;
    },

    goBack : function(){
        window.history.back();
    },

    alert: function (title, content) {
        if (content == null) {
            window.__alert(title);
        }
        else {
            window.__alert("<标题:>" + title + "\r\n" + content);
        }
    },

    //Constructor function.
    init: function () {
        window.__alert = window.alert;
        window.COBJ = this;

        window.COBJ.log = function (msg) {
            if (typeof window.COBJ.debug === 'undefined' || window.COBJ.debug.enabled === false) return;
            var pos = null;
            var file = null;
            var log = null;

            try {
                throw new Error();
            } catch (e) {
                try{
                    var poss = e.stack.split('\n')[2].split(":");
                    file = window.COBJ.debug.fullPath ? poss[1] : poss[1].split("/").pop();
                    pos = poss[poss.length - 2];
                }catch(e){
                    file = "";
                    pos = "";
                }
            }
            log = "[" + window.COBJ.debug.logPrefix + "@" + file + "+" + pos + "]:" + msg;

            if (window.console) window.console.log(log);
            if (window.COBJ.debug.toServer && window.COBJ.debug.toServerFail < 5) $.post(window.COBJ.debug.svrAddress, { "log": log }, function (r, s) {
                if (s != 'success') {
                    window.COBJ.debug.toServerFail = window.COBJ.debug.toServerFail || 1;
                    window.COBJ.debug.toServerFail++;
                }
            });
        };
    }
};

/**
 实现Android平台下的功能函数接口
 */
var __Android = __Android || {
    //RE-Package encoding & decoding function.
    enc: function (str) { return JCODEC.encode(str); },
    dec: function (str) { return JCODEC.decode(str); },

    cbk: function (idx) {
        return function (r, s) {
            setTimeout(function () {
                try {
                    var x = window.COBJ.ajaxs[idx];
                    x.MBOP.readyState = 4;
                    x.MBOP.statusText = s ? s : "success";
                    x.MBOP.responseHeaders = {};
                    x.MBOP.responseText = r;
                    x.MBOP.status = 200;

                    window.COBJ.ajaxs[idx].onreadystatechange();
                } catch (e) { alert(e); }
            }, 100);
        };
    },


    /**
     //submitMethod 1:post 2:get  if(get) postData==web content if(post) postData==a=1&b=2&c=3&d=4
     public void postMessage(final String url,final String postData,final String encode, String callback, final int submitMethod ,boolean isShowProgress){

	public void goLocation(final String url){...}

	public void postMessage(final String url,final String postData,final String encode, String callback){
	postMessage(url, postData, encode, callback, 2,true);
	}

	public void postMessage(final String url,final String postData,final String encode, String callback,boolean isShowProgress){
	postMessage(url, postData, encode, callback, 2,isShowProgress);
	} 
	*/
    ajax: function (xhr, idx, type, url, headers, content) {
        window.COBJ.ajaxs[idx] = xhr;

        //        xhr.MBOP.callback = function (result, status) {

        //            this.MBOP.readyState = 4;
        //            this.MBOP.statusText = status ? status : "success";

        //            this.complete(result, status);
        //        };

        //var callback = "window.COBJ.ajaxs[" + idx + "].MBOP.callback";
        var callback = "__Android.cbk('" + idx + "')";

        if (window.MBOP.postMessage){
            window.MBOP.postMessage(encodeURI(url),
                content,
                "",
                callback,
                type.toLowerCase() === "post" ? 1 : 2,
                true);
        }
        else
            alert("Calling window.COBJ.postMesasge error!");
    },

    goLocation: function (url) {
        window.MBOP.goLocation(url);
    },

    goBack : function(){
        window.MBOP.func_go_back();
    },

    //Constructor function.
    init: function () {
        try {
            window.COBJ = window.COBJ || {};
            window.COBJ.ajaxIndex = 1;
            window.COBJ.ajaxs = {};
            window.COBJ.enc = __Android.enc;
            window.COBJ.dec = __Android.dec;
            window.COBJ.goLocation = __Android.goLocation;
            window.COBJ.goBack = __Android.goBack;
            window.COBJ.ajax = function (xhr, idx, type, url, headers, content) {
                __Android.ajax(xhr, idx, type, url, headers, content);
            };

            window.COBJ.log = function (msg) {
                if (typeof window.COBJ.debug === 'undefined' || window.COBJ.debug.enabled === false) return;

                var pos = null;
                var file = null;
                var log = null;

                try {
                    throw new Error();
                } catch (e) {
                    try{
                        var poss = e.stack.split('\n')[2].split(":");
                        file = window.COBJ.debug.fullPath ? poss[1] : poss[1].split("/").pop();
                        pos = poss[poss.length - 2];
                    }catch(e){
                        file = "";
                        pos = "";
                    }
                }
                log = "[" + window.COBJ.debug.logPrefix + "@" + file + "+" + pos + "]:" + msg;

                if (window.console) window.console.log(log);
            };
        } catch (e) { alert("Initialize android mbop failed: " + e); }
    }
};

/**
 实现iOS平台下的功能函数接口
 */
var __iOS = __iOS || {
    //Constructor function.
    init: function () {

        window.MBOP = window.MBOP || {
            //RE-Package encoding & decoding function.
            enc: function (str) {
                return JCODEC.encode(str);
            },
            dec: function (str) {
                return JCODEC.decode(str);
            },

            //Transmit operation to OBJ-C side
            transmit: function () {

                var len = arguments.length;
                if (1 == len) {
                    var action = {
                        action: null
                    };
                    action.action = this.enc(arguments[0]);
                    window.location.hash = Math.random() + "jcodec:" + this.enc($.stringifyJSON(action));

                } else {
                    var action = {
                        action: null,
                        param: null
                    };
                    action.action = this.enc(arguments[0]);
                    action.param = encodeURI(this.enc(arguments[1]));
                    window.location.hash = Math.random() + "jcodec:" + this.enc($.stringifyJSON(action));
                }



            },
            func_visitor_home_page_login: function () {
                this.transmit("func_visitor_home_page_login");
            },
            func_visitor_home_page_register: function () {
                this.transmit("func_visitor_home_page_register");
            },

            func_elive_membership_disc_callphone: function (para) {
                this.transmit("func_elive_membership_disc_callphone", para);
            },
            func_elive_membership_active_callphone: function (para) {
                this.transmit("func_elive_membership_active_callphone", para);
            },
            func_go_back: function () {
                this.transmit("func_go_back");
            },
            func_go_forward: function () {
                this.transmit("func_go_forward");
            },
            func_go_back_or_forward: function (steps) {
                this.transmit("func_go_back_or_forward", steps);
            },
            func_go_location: function(index){
                this.transmit("func_go_location", index);
            },
            func_get_location: function(callback){
                this.transmit("func_get_location", callback);
            },
            func_get_width_height: function(callback){
                this.transmit("func_get_width_height",callback);
            },
            getCurPortalId: function(callback){
                this.transmit("getCurPortalId",callback);
            },
            resetCollectedPhoto: function(){
                this.transmit("resetCollectedPhoto");
            },
            func_idcard_is_collected: function(callback){
                this.transmit("func_idcard_is_collected",callback);
            },
            func_idcard_save: function(callback){
                this.transmit("func_idcard_save",callback);
            },
            func_idcard_upload: function(callback){
                this.transmit("func_idcard_upload",callback);
            },
            func_idcard_save_and_upload: function(){
                var param = {
                    callBack: arguments[0],
                    photoPrefix: arguments[1],
                    mark: arguments[2]
                };
                this.transmit("func_idcard_save_and_upload", $.stringifyJSON(param));
            },
            func_ecp_idcard_save_and_upload: function(){
                var param = {
                    callBack: arguments[0],
                    photoPrefix: arguments[1],
                    mark: arguments[2]
                };
                this.transmit("func_ecp_idcard_save_and_upload", $.stringifyJSON(param));
            },
            signIn: function () {
                var len = arguments.length;
                if (4 == len) {
                    var param = {
                        signObjType: arguments[0],
                        signObjTypeName: arguments[1],
                        signObjId: arguments[2],
                        signObjIdName: arguments[3]
                    };
                    this.transmit("signIn", $.stringifyJSON(param));

                } else if (5 == len) {
                    var param = {
                        signObjType: arguments[0],
                        signObjTypeName: arguments[1],
                        signObjId: arguments[2],
                        signObjIdName: arguments[3],
                        homeCity: arguments[4]
                    };
                    this.transmit("signIn", $.stringifyJSON(param));
                }

            },

            setCacheSignObjId: function () {
                var len = arguments.length;
                if (1 == len) {
                    var signObjId = arguments[0];
                    this.transmit("setCacheSignObjId", signObjId);
                } else if (2 == len) {
                    var param = {
                        signObjId: arguments[0],
                        signObjType: arguments[1]
                    };
                    this.transmit("setCacheSignObjId", $.stringifyJSON(param));
                } else if (3 == len) {
                    var param = {
                        signObjId: arguments[0],
                        signObjType: arguments[1],
                        homeCity: arguments[2]
                    };
                    this.transmit("setCacheSignObjId", $.stringifyJSON(param));
                }
            },

            getCacheSignInfo: function (callback) {
                this.transmit("getCacheSignInfo", callback);
            },

            pickDate: function (date, format, callback) {
                var param = {
                    date: date,
                    format: format,
                    callback: callback
                };
                this.transmit("pickDate", $.stringifyJSON(param));
            },
            pickTime: function (hourMinute, format, callback) {
                var param = {
                    hourMinute: hourMinute,
                    format: format,
                    callback: callback
                };
                this.transmit("pickTime", $.stringifyJSON(param));
            },
            hnpickDate: function (hourMinute, format, callback) {
                var param = {
                    hourMinute: hourMinute,
                    format: format,
                    callback: callback
                };
                this.transmit("hnpickDate", $.stringifyJSON(param));
            },
            hnPickDateTime: function (hourMinute, format, callback) {
                var param = {
                    hourMinute: hourMinute,
                    format: format,
                    callback: callback
                };
                this.transmit("hnPickDateTime", $.stringifyJSON(param));
            },
            hnpickDateMonth: function (hourMinute, format, callback) {
                var param = {
                    hourMinute: hourMinute,
                    format: format,
                    callback: callback
                };
                this.transmit("hnpickDateMonth", $.stringifyJSON(param));
            },

            devicePrint: function (content, callback) {
                var param = {
                    content: content,
                    callback: callback
                };
                this.transmit("devicePrint", $.stringifyJSON(param));
            },
            btPrint: function (content, callback) {
                var param = {
                    content: content,
                    callback: callback
                };
                this.transmit("btPrint", $.stringifyJSON(param));
            },

            btPrintReSelDev: function (content, callback) {
                var param = {
                    content: content,
                    callback: callback
                };
                this.transmit("btPrintReSelDev", $.stringifyJSON(param));
            },

            getPrintResult: function (callback) {
                this.transmit("getPrintResult", callback);
            },

            getDeviceName: function (callback) {
                this.transmit("getDeviceName", callback);
            },

            getDefaultPrintType: function (callback) {
                this.transmit("getDefaultPrintType", callback);
            },

            setDefaultPrintType: function () {
                var len = arguments.length;
                if (1 == len) {
                    var param = {
                        printType: arguments[0]
                    };
                    this.transmit("setDefaultPrintType", $.stringifyJSON(param));
                }
            },

            getPrintPreViewConf: function (callback) {
                this.transmit("getPrintPreViewConf", callback);
            },

            closeWebPlugin: function () {
                this.transmit("closeWebPlugin");
            },


            changeCloseMessage: function (msg) {
                this.transmit("changeCloseMessage", msg);
            },

            btIDCRead: function (callback) {
                this.transmit("btIDCRead", callback);
            },

            btIDCReadReSelDev: function (callback) {
                this.transmit("btIDCReadReSelDev", callback);
            },

            eleWebSign: function(data) {
                this.transmit("eleWebSign", data);
            },

            eleSign: function () {
                var len = arguments.length;
                if (1 == len) {
                    var param = {
                        callback: arguments[0]
                    };
                    this.transmit("eleSign", $.stringifyJSON(param));
                }else if (2 == len) {
                    var param = {
                        content: arguments[0],
                        callback: arguments[1]
                    };
                    this.transmit("eleSign", $.stringifyJSON(param));
                }else if (4 == len) {
                    var param = {
                        content: arguments[0],
                        callback: arguments[1],
                        frontalPhotoFileName: arguments[2],
                        oppositePhotoFileName: arguments[3]
                    };
                    this.transmit("eleSign", $.stringifyJSON(param));
                }
            },
            getALLIDCReadInfo: function (callback, getImg) {

                var param = {
                    callback: callback,
                    getImg: getImg
                };
                this.transmit("getALLIDCReadInfo", $.stringifyJSON(param));
            },

            getEleSignBase64: function (callback) {
                this.transmit("getEleSignBase64", callback);
            },

            uploadSignSucess: function (filePath) {
                this.transmit("uploadSignSucess", filePath);
            },
            getFileList: function (filePath, callback) {
                var param = {
                    filePath: filePath,
                    callback: callback
                };
                this.transmit("getFileList", $.stringifyJSON(param));
            },

            getLocalFileUrl: function (filePath, callback) {
                var param = {
                    filePath: filePath,
                    callback: callback
                };
                this.transmit("getLocalFileUrl", $.stringifyJSON(param));
            },


            deleteFile: function (filePath, callback) {
                var param = {
                    filePath: filePath,
                    callback: callback
                };
                this.transmit("deleteFile", $.stringifyJSON(param));
            },
            setGMRegister: function (url, callback) {
                var param = {
                    url: url,
                    callback: callback
                };
                this.transmit("setGMRegister", $.stringifyJSON(param));
            },

            getEleSignBase64: function (callback,filePath) {
                var param = {
                    callback: callback,
                    filePath: filePath
                };
                this.transmit("getEleSignBase64", $.stringifyJSON(param));
            },

            eleIdCardCollect: function (callback) {
                this.transmit("eleIdCardCollect", callback);
            },
            photosCollect: function (callback) {
                this.transmit("photosCollect", callback);
            },
            takePhoto: function(data,callback) {
                var param = {
                    data: data,
                    callback: callback
                };
                this.transmit("takePhoto", $.stringifyJSON(param));
            },
            takePhotograph: function(data,callback) {
                var param = {
                    data: data,
                    callback: callback
                };
                this.transmit("takePhotograph", $.stringifyJSON(param));
            },
            getSystemPhotosFromAlbum: function(data) {
                this.transmit("getSystemPhotosFromAlbum", data);
            },
            getCurrentLocationInfo: function(data) {
                this.transmit("getCurrentLocationInfo", data);
            },
            openFile: function(data) {
                this.transmit("openFile", data);
            },
            authRealnameProcess: function(data) {
                this.transmit("authRealnameProcess", data);
            },
            launchNetworkDesign: function(data) {
                this.transmit("launchNetworkDesign", data);
            },
            getALLIDCReadInfo:function (callback) {
                this.transmit("getALLIDCReadInfo", callback);
            },
            getALLIDCReadInfoAndPhotos:function (callback) {
                this.transmit("getALLIDCReadInfoAndPhotos", callback);
            },
            getFrontalIDCReadInfo:function (callback) {
                this.transmit("getFrontalIDCReadInfofunction", callback);
            },
            simGetSimInfo: function (callback) {
                this.transmit("simGetSimInfo", callback);
            },
            simGetSimInfoReSelDev: function (callback) {
                this.transmit("simGetSimInfoReSelDev", callback);
            },
            getShSoftwareId: function () {
                this.transmit("getShSoftwareId");
            },

            simVerifyWriteCardOS: function (simATR, componentKey, callback) {
                var param = {
                    simATR: simATR,
                    componentKey: componentKey,
                    callback: callback

                };
                this.transmit("simVerifyWriteCardOS", $.stringifyJSON(param));
            },

            simVerifyWriteCardOSReSelDev: function (simATR, componentKey, callback) {
                var param = {
                    simATR: simATR,
                    componentKey: componentKey,
                    callback: callback

                };
                this.transmit("simVerifyWriteCardOSReSelDev", $.stringifyJSON(param));
            },


            simWriteCard: function (componentName, componentDownloadPath, componentVersion, componentKey, msisdn_num, issueData, externData, callback) {
                var param = {
                    componentName: componentName,
                    componentDownloadPath: componentDownloadPath,
                    componentVersion: componentVersion,
                    componentKey: componentKey,
                    msisdn_num: msisdn_num,
                    issueData: issueData,
                    externData: externData,
                    callback: callback
                };
                this.transmit("simWriteCard", param);
            },

            simWriteCardReSelDev: function (componentName, componentDownloadPath, componentVersion, componentKey, msisdn_num, issueData, externData, callback) {
                var param = {
                    componentName: componentName,
                    componentDownloadPath: componentDownloadPath,
                    componentVersion: componentVersion,
                    componentKey: componentKey,
                    msisdn_num: msisdn_num,
                    issueData: issueData,
                    externData: externData,
                    callback: callback
                };
                this.transmit("simWriteCardReSelDev", $.stringifyJSON(param));
            },
            updateNavigator: function (items, selIndex) {
                var str = "";
                for (var idx in items) {
                    str += this.enc(items[idx]) + ",";
                }
                str = str.substring(0, str.length - 1);
                var param = {
                    items: str,
                    selIndex: selIndex
                };
                this.transmit("updateNavigator", $.stringifyJSON(param));
            },
            postMessage: function () {
                var len = arguments.length;
                if (4 == len) {
                    var param = {
                        url: arguments[0],
                        postData: arguments[1],
                        encode: arguments[2],
                        callback: arguments[3],
                    };
                    this.transmit("postMessage", $.stringifyJSON(param));

                } else if (5 == len) {
                    var param = {
                        url: arguments[0],
                        postData: arguments[1],
                        encode: arguments[2],
                        callback: arguments[3],
                        submitMethod: arguments[4]
                    };
                    this.transmit("postMessage", $.stringifyJSON(param));

                } else if (6 == len) {
                    var param = {
                        url: arguments[0],
                        postData: arguments[1],
                        encode: arguments[2],
                        callback: arguments[3],
                        submitMethod: arguments[4],
                        isShowProgress: arguments[5]
                    };
                    this.transmit("postMessage", $.stringifyJSON(param));

                }

            },
            goLocation: function (url) {
                this.transmit("goLocation", url);
            },

            showWait: function (msg, closeTime) {
                var param = {
                    msg: msg,
                    closeTime: closeTime
                };
                this.transmit("showWait", $.stringifyJSON(param));
            },
            closeWait: function () {

                this.transmit("closeWait");
            },
            barcodeScan: function (callback) {
                this.transmit("barcodeScan", callback);
            },
            //扫描企业营业执照抓取企业信息
            barcodeScanInternetWorm: function(data) {
                this.transmit("barcodeScanInternetWorm", data);
            },
            relogin: function(){
                this.transmit("relogin");
            },
            downloadPackage: function(productId,packageurl,id,name,volumn,callback){
                var param = {
                    productId: productId,
                    packageurl: packageurl,
                    id: id,
                    name: name,
                    volumn: volumn,
                    callback: callback
                };
                this.transmit("downloadPackage", $.stringifyJSON(param));
            },
            getPackageFileName: function(packageurl){
                this.transmit("getPackageFileName", packageurl);
            },
            packageIsExist: function (fileName,id){
                var param = {
                    fileName: fileName,
                    id: id
                };
                this.transmit("packageIsExist", $.stringifyJSON(param));
            },
            playPackage: function (packageName,packageId, callback) {
                var param = {
                    packageName: packageName,
                    packageId: packageId,
                    callback: callback
                };
                this.transmit("playPackage", $.stringifyJSON(param));
            },
            detectPackage: function(packageurl,packageid,callback) {
                var str1 = "";
                for (var idx in packageurl) {
                    str1 += packageurl[idx] + ",";
                }
                str1 = str1.substring(0, str1.length - 1);
                var str2 = "";
                for (var idx in packageid) {
                    str2 += packageid[idx] + ",";
                }
                str2 = str2.substring(0, str2.length - 1);
                var param = {
                    packageurl: str1,
                    packageid: str2,
                    callback: callback
                };
                this.transmit("detectPackage", $.stringifyJSON(param));
            },
            storeProduct: function(productId,productName){
                var param = {
                    productId: productId,
                    productName: productName
                };
                this.transmit("storeProduct", $.stringifyJSON(param));
            },
            productDocument: function()
            {
                this.transmit("productDocument");
            },
            alert: function () {
                window.MBOP.transmit("yxAlert", arguments[0]);

            },
            func_is_installValidation: function() {
                var param = {
                    callback: arguments[0]
                };
                this.transmit("func_is_installValidation", $.stringifyJSON(param));
            },
            func_download_file_by_self: function() {
                this.transmit("func_download_file_by_self");
            },
            func_realname_Authentication: function() {
                var param = {
                    callback: arguments[0],
                    transactionID: arguments[1],
                    billId: arguments[2],
                    account: arguments[3],
                    channelCode: arguments[4],
                    provinceCode: arguments[5],
                    signature: arguments[6]
                };
                this.transmit("func_realname_Authentication", $.stringifyJSON(param));
            },
            setCnt: function(type,count) {
                var param = {
                    type:type,
                    count: count
                };
                this.transmit("setCnt",$.stringifyJSON(param));
            },
            reduceCnt: function(type,count) {
                var param = {
                    type:type,
                    count: count
                };
                this.transmit("reduceCnt",$.stringifyJSON(param));
            },
            goWorkWarnDetail: function(infoId) {
                var param = {
                    infoId:infoId
                };
                this.transmit("goWorkWarnDetail",$.stringifyJSON(param));
            },
            goNoticeDetail: function(infoId) {
                var param = {
                    infoId:infoId
                };
                this.transmit("goNoticeDetail",$.stringifyJSON(param));
            },
            uploadImage: function() {
                var param = {
                    singleImageJsonData:arguments[0],
                    tempJsonImageDataInfo:arguments[1],
                    callback: arguments[2],
                    index: arguments[3],
                    isCompress: arguments[4] || true,
                    maxSize: arguments[5] || 100
                };
                this.transmit("uploadImage", $.stringifyJSON(param));
            },
//            uploadImage: function() {
//                var param = {
//			    singleImageJsonData:arguments[0],
//			    tempJsonImageDataInfo:arguments[1],
//	            callback: arguments[2],
//	            index: arguments[3],
//	            isCompress: arguments[4],
//            	maxSize:  arguments[5]
//	            };
//	            this.transmit("uploadImage", $.stringifyJSON(param));
//	        },
            getPhotoFromAlbum:  function() {
                var param = {
                    callback:arguments[0],
                    path:arguments[1],
                    rule: arguments[2]
                };
                this.transmit("getPhotoFromAlbum", $.stringifyJSON(param));
            },
            takeLocationPhoto: function(callback) {
                this.transmit("takeLocationPhoto", callback);
            },
            setExpandButton: function(){
                var param = {
                    name: arguments[0],
                    callback: arguments[1],
                };
                this.transmit("setExpandButton", $.stringifyJSON(param));
            },
            removeExpandButton: function(){
                this.transmit("removeExpandButton");
            },
            getContactInfo: function(){
                var param = {
                    callback: arguments[0],
                };
                this.transmit("getContactInfo", $.stringifyJSON(param));
            },
            copyText: function(){
                var param = {
                    text: arguments[0],
                    callback: arguments[1],
                };
                this.transmit("copyText", $.stringifyJSON(param));
            },
            labelPrint: function(){
                var param = {
                    content: arguments[0],
                    callback: arguments[1],
                };
                this.transmit("labelPrint", $.stringifyJSON(param));
            },
            toBizTrans: function() {
                this.transmit("toBizTrans");
            },
            toWorkManage: function() {
                this.transmit("toWorkManage");
            },
            toGroupServiceObj: function() {
                this.transmit("toGroupServiceObj");
            },
            goMenu: function(menuId) {
                var param = {
                    menuId:menuId
                };
                this.transmit("goMenu",$.stringifyJSON(param));
            },


            ajaxIndex: 0,
            ajaxs: {},
            ajax: function (xhr, idx, type, url, headers, content) {
                var param = {
                    idx: idx,
                    type: type,
                    url: url,
                    headers: headers,
                    content: content
                };

                this.transmit("ajax", $.stringifyJSON(param));
            },



        };

        window.alert = window.MBOP.alert;
        window.COBJ = window.COBJ || {};
        window.COBJ.ajaxIndex = 1;
        window.COBJ.ajaxs = {};
        window.COBJ.enc = window.MBOP.enc;
        window.COBJ.dec = window.MBOP.dec;
        window.COBJ.goLocation = function (url) {
            window.MBOP.goLocation(url);
        };

        window.COBJ.goBack = function () {
            window.MBOP.func_go_back();
        };

        window.COBJ.ajax = function (xhr, idx, type, url, headers, content) {
            window.MBOP.ajax(xhr, idx, type, url, headers, content);
        };

        window.COBJ.log = function (msg) {
            //alert('window.COBJ.log is fired');
        };
        //window.COBJ.log = function (msg) { console.log.apply(console,arguments); };
    }
};

/**
 初始化全局对象
 */
window.COBJ = window.COBJ || (__System.type() == __System.types.iOS ? window.console : {});

/**
 初始化平台基本对象
 */
(function () {
    try {
        if (__System.type() == __System.types.iOS)
            __iOS.init();
        else if (__System.type() == __System.types.Android)
            __Android.init();
        else if (__System.type() == __System.types.Windows
            || __System.type() == __System.types.Macintosh)
            __Windows.init();
    } catch (e) { alert("Initialization error : " + e); }
})();

/**
 Stand-alone providing functions, which may base on functions above.
 */

//Cache page, show it.
function goLocation(url) {
    try {
        window.COBJ.goLocation(url);
    } catch (e) { alert("COOBJ.goLocation error : " + e); };
}

//Call back function
function goBack(){
    try
    {
        window.COBJ.goBack();
    }
    catch (e)
    {
        alert("COBJ.goBack Error : " + e);
    }
}
