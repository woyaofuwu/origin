var before = function (fn, beforefn) {
    return function () {
        beforefn && beforefn.apply(this, arguments)
        return fn.apply(this, arguments)
    }
}

var after = function (fn, agterfn, obj) {
    return function () {
        var ret = fn.apply(obj, arguments)
        agterfn && agterfn.apply(obj, arguments)
        return ret;
    }
}

var extend = function (obj1, obj2) {
    for ( var attrName in obj2) {
        if (!(attrName in obj1))
            obj1[attrName] = obj2[attrName];
    }
    return obj1;
};
function convertData(ajaxData) {
    var data = {};
    if (ajaxData && ajaxData.length > 0) {
        ajaxData.eachKey(function (key) {
            if (!(key in data)) {
                data[key] = [];
            }
            if ($.DataMap(constdata.offerType).containsKey(key)) {
                var ls = ajaxData.get(key);
                if (ls && ls.length > 0) {
                    for (var i = 0; i < ls.length; i++) {
                        var offer = ls.get(i);
                        data[key].push(offer);
                    }
                }
            }
        });
    }
    return data;
}