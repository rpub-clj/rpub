import*as l from"cherry-cljs/cljs.core.js";import*as s from"rpub.lib.transit";var _=function(){return document.querySelector("meta[name='csrf-token']").getAttribute("content")},b=function(i,d){const m=d,a=l.__destructure_map.call(null,m),w=a,o=l.get.call(null,a,l.keyword("body")),e=l.get.call(null,a,l.keyword("on-complete")),c=l.merge.call(null,l.array_map(l.keyword("format"),l.keyword("json")),w),u=(()=>{const t=l.keyword("format").call(null,c),n=l.truth_.call(null,l.keyword_QMARK_.call(null,t))?l.subs.call(null,l.str.call(null,t),1):null;switch(n){case"json":return"application/json";case"transit":return"application/transit+json";default:throw new Error(l.str.call(null,"No matching clause: ",n))}})(),y=l.array_map("X-CSRF-Token",_.call(null),"Accept",u,"Content-Type",u),p=function(t){const n=l.keyword("format").call(null,c),r=l.truth_.call(null,l.keyword_QMARK_.call(null,n))?l.subs.call(null,l.str.call(null,n),1):null;switch(r){case"json":return l.js__GT_clj.call(null,JSON.parse(t));case"transit":return s.read.call(null,t);default:throw new Error(l.str.call(null,"No matching clause: ",r))}},k=function(t){const n=l.keyword("format").call(null,c),r=l.truth_.call(null,l.keyword_QMARK_.call(null,n))?l.subs.call(null,l.str.call(null,n),1):null;switch(r){case"json":return JSON.stringify(l.clj__GT_js.call(null,t));case"transit":return s.write.call(null,t);default:throw new Error(l.str.call(null,"No matching clause: ",r))}},f=(()=>{const t=l.array_map(l.keyword("method"),l.keyword("post"),l.keyword("headers"),y);return l.truth_.call(null,o)?l.assoc.call(null,t,l.keyword("body"),k.call(null,o)):t})(),h=fetch(i,l.clj__GT_js.call(null,f));return l.truth_.call(null,e)&&h.then(function(t){return t.text()}).then(function(t){return e.call(null,p.call(null,t))},null).catch(function(t){return e.call(null,null,t)}),null};export{_ as get_csrf_token,b as post};
