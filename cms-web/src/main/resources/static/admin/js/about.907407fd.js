(window["webpackJsonp"]=window["webpackJsonp"]||[]).push([["about"],{"482b":function(t,i,n){"use strict";var e=n("9efd"),a="/api/option",o={initialize:function(){return Object(e["a"])({url:"".concat(a,"/initialize"),method:"get"})},list:function(){return Object(e["a"])({url:a,method:"get"})},save:function(t){return Object(e["a"])({url:a,data:t,method:"post"})}};i["a"]=o},f820:function(t,i,n){"use strict";n.r(i);var e=function(){var t=this,i=t.$createElement,n=t._self._c||i;return n("div",[n("button",{on:{click:t.initialize}},[t._v("初始化系统")])])},a=[],o=n("482b"),c={methods:{initialize:function(){var t=this;o["a"].initialize().then((function(i){t.$notification["success"]({message:"操作"+i.data})}))}}},u=c,r=n("2877"),s=Object(r["a"])(u,e,a,!1,null,null,null);i["default"]=s.exports}}]);
//# sourceMappingURL=about.907407fd.js.map