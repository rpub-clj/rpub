import*as l from"cherry-cljs/cljs.core.js";import{useEffect as Gl,useState as W,useCallback as ol}from"react";import*as Tl from"cherry-cljs/lib/clojure.string.js";import*as ll from"rads.inflections";import*as F from"rpub.admin.impl";import*as m from"rpub.lib.html";import*as I from"rpub.lib.http";import*as cl from"rpub.lib.reagent";var el=function(){return crypto.randomUUID()},ul=l.vector(l.vector(l.array_map(l.keyword("header"),l.vector(1,2,3,4,5,6,null))),l.vector("bold","italic"),l.vector(l.array_map(l.keyword("list"),"bullet"),l.array_map(l.keyword("list"),"ordered")),l.vector("blockquote"),l.vector(l.array_map(l.keyword("align"),""),l.array_map(l.keyword("align"),"center"),l.array_map(l.keyword("align"),"right")),l.vector("link"),l.vector("clean")),dl=l.array_map(l.keyword("theme"),l.keyword("snow"),l.keyword("modules"),l.array_map(l.keyword("toolbar"),ul)),_l=function(t,n){const e=l.merge.call(null,dl,l.dissoc.call(null,n,l.keyword("html"))),a=new Quill(t,l.clj__GT_js.call(null,e)),d=l.keyword("html").call(null,n);if(l.truth_.call(null,d)){const y=d;a.clipboard.dangerouslyPasteHTML(y)}return a},yl=function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("name")),d=l.get.call(null,e,l.keyword("type"));return l.array_map(l.keyword("id"),el.call(null),l.keyword("name"),a,l.keyword("type"),d)},sl=function(t){return ll.parameterize.call(null,t)},kl=function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("id")),d=l.get.call(null,e,l.keyword("name")),y=l.get.call(null,e,l.keyword("slug")),u=l.get.call(null,e,l.keyword("fields"));return l.array_map(l.keyword("id"),(()=>{const _=el.call(null);return l.truth_.call(null,_)?_:a})(),l.keyword("name"),d,l.keyword("slug"),y,l.keyword("fields"),u,l.keyword("created-at"),new Date,l.keyword("content-item-count"),0)},tl=function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("state")),d=l.get.call(null,e,l.keyword("set-state")),y=l.get.call(null,e,l.keyword("anti-forgery-token")),u=l.get.call(null,e,l.keyword("content-type")),_=l.get.call(null,e,l.keyword("class")),p=l.array_map(l.keyword("anti-forgery-token"),y),f=ol.call(null,function(G,i){return m.debounce.call(null,function(w,T,b){w.preventDefault();const o=l.assoc.call(null,b,i,w.target.value),c=l.assoc.call(null,p,l.keyword("body"),l.merge.call(null,l.array_map(l.keyword("content-type-id"),G,l.keyword("content-field-id"),l.keyword("id").call(null,o)),l.select_keys.call(null,o,l.vector(l.keyword("name"),l.keyword("type"),l.keyword("rank")))));return I.post.call(null,"/api/update-content-type-field",c),d.call(null,l.update_in.call(null,a,l.vector(l.keyword("content-type-index"),l.keyword("id").call(null,T),l.keyword("fields")),function(v){return l.map.call(null,function(r){return l.truth_.call(null,l._EQ_.call(null,l.keyword("id").call(null,r),l.keyword("id").call(null,o)))?o:r},v)}))},m.default_debounce_timeout_ms)}),E=function(G,i,w){if(G.preventDefault(),l.truth_.call(null,confirm(l.str.call(null,'Are you sure you want to delete "',l.keyword("name").call(null,w),'"?')))){const T=l.assoc.call(null,p,l.keyword("body"),l.array_map(l.keyword("content-type-id"),l.keyword("id").call(null,i),l.keyword("content-field-id"),l.keyword("id").call(null,w)));return I.post.call(null,"/api/delete-content-type-field",T),d.call(null,l.update_in.call(null,a,l.vector(l.keyword("content-type-index"),l.keyword("id").call(null,i),l.keyword("fields")),function(b){return l.remove.call(null,function(o){return l._EQ_.call(null,l.keyword("id").call(null,o),l.keyword("id").call(null,w))},b)}))}},U=f.call(null,l.keyword("id").call(null,u),l.keyword("name")),O=f.call(null,l.keyword("id").call(null,u),l.keyword("type"));return l.vector(l.keyword("form"),l.array_map(l.keyword("method"),"post",l.keyword("class"),_),l.vector(l.keyword("input"),l.array_map(l.keyword("id"),"__anti-forgery-token",l.keyword("name"),"__anti-forgery-token",l.keyword("type"),"hidden",l.keyword("value"),y)),l.vector(l.keyword("input"),l.array_map(l.keyword("type"),"hidden",l.keyword("name"),"content-type-id",l.keyword("value"),l.keyword("id").call(null,u))),l.vector(l.keyword("input"),l.array_map(l.keyword("type"),"hidden",l.keyword("name"),"content-type-name",l.keyword("value"),l.keyword("name").call(null,u))),l.vector(l.keyword("div"),function i(w){return new l.LazySeq(null,function(){let T=w;for(;;){const b=l.seq.call(null,T);if(l.truth_.call(null,b)){const o=b;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,o))){const c=l.chunk_first.call(null,o),v=l.count.call(null,c),r=l.chunk_buffer.call(null,v);return(()=>{let s=0;for(;;){if(s<v){const k=l._nth.call(null,c,s);l.chunk_append.call(null,r,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"mb-2 pb-2 pt-2 flex items-center group",l.keyword("key"),l.keyword("id").call(null,k)),l.vector(l.keyword("label"),l.array_map(l.keyword("for"),l.keyword("field-name"))),l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("size"),l.keyword("text-medium"),l.keyword("class"),l.str.call(null,"px-2 py-1 font-semibold border border-gray-300 ","rounded-[6px] mr-4 max-w-xl"),l.keyword("placeholder"),"Field Name",l.keyword("name"),l.keyword("field-name"),l.keyword("readonly"),!0,l.keyword("default-value"),l.keyword("name").call(null,k))),l.vector(l.keyword("label"),l.array_map(l.keyword("for"),l.keyword("field-type"))),l.vector(m.select,l.array_map(l.keyword("name"),l.keyword("field-type"),l.keyword("default-value"),l.keyword("type").call(null,k),l.keyword("readonly"),!0),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("text"),l.keyword("value"),"text"),"Text"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("text-lg"),l.keyword("value"),"text-lg"),"Text (Large)"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("number"),l.keyword("value"),"number"),"Number"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("choice"),l.keyword("value"),"choice"),"Choice"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("datetime"),l.keyword("value"),"datetime"),"Date/Time")))),s=l.unchecked_inc.call(null,s);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,r),i.call(null,l.chunk_rest.call(null,o))):l.chunk_cons.call(null,l.chunk.call(null,r),null)}else{const c=l.first.call(null,o);return l.cons.call(null,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"mb-2 pb-2 pt-2 flex items-center group",l.keyword("key"),l.keyword("id").call(null,c)),l.vector(l.keyword("label"),l.array_map(l.keyword("for"),l.keyword("field-name"))),l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("size"),l.keyword("text-medium"),l.keyword("class"),l.str.call(null,"px-2 py-1 font-semibold border border-gray-300 ","rounded-[6px] mr-4 max-w-xl"),l.keyword("placeholder"),"Field Name",l.keyword("name"),l.keyword("field-name"),l.keyword("readonly"),!0,l.keyword("default-value"),l.keyword("name").call(null,c))),l.vector(l.keyword("label"),l.array_map(l.keyword("for"),l.keyword("field-type"))),l.vector(m.select,l.array_map(l.keyword("name"),l.keyword("field-type"),l.keyword("default-value"),l.keyword("type").call(null,c),l.keyword("readonly"),!0),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("text"),l.keyword("value"),"text"),"Text"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("text-lg"),l.keyword("value"),"text-lg"),"Text (Large)"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("number"),l.keyword("value"),"number"),"Number"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("choice"),l.keyword("value"),"choice"),"Choice"),l.vector(l.keyword("option"),l.array_map(l.keyword("key"),l.keyword("datetime"),l.keyword("value"),"datetime"),"Date/Time"))),i.call(null,l.rest.call(null,o)))}}break}},null,null)}.call(null,l.sort_by.call(null,l.keyword("rank"),l.keyword("fields").call(null,u)))))},il=function(t,n){return l.into.call(null,l.array_map(),l.map.call(null,function(e){return l.vector(t.call(null,e),e)},n))},wl=function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("content-types")),d=l.get.call(null,e,l.keyword("anti-forgery-token")),y=l.map.call(null,function(o){return l.update.call(null,o,l.keyword("created-at"),Date.parse)},a),u=W.call(null,l.array_map(l.keyword("content-type-index"),il.call(null,l.keyword("id"),y))),_=l.nth.call(null,u,0,null),p=l.nth.call(null,u,1,null),f=l.array_map(l.keyword("anti-forgery-token"),d),E=ol.call(null,m.debounce.call(null,function(o,c){const v=o.target.value,r=l.select_keys.call(null,l.assoc.call(null,c,l.keyword("name"),v),l.vector(l.keyword("id"),l.keyword("name"))),s=l.assoc.call(null,f,l.keyword("body"),l.array_map(l.keyword("content-type"),r));return I.post.call(null,"/api/update-content-type",s),p.call(null,l.assoc_in.call(null,_,l.vector(l.keyword("content-type-index"),l.keyword("id").call(null,c),l.keyword("name")),v))},m.default_debounce_timeout_ms)),U=function(o){const c=kl.call(null,l.array_map(l.keyword("name"),"New Content Type",l.keyword("slug"),"new-content-type",l.keyword("fields"),l.vector()));return o.preventDefault(),I.post.call(null,"/api/new-content-type",f),p.call(null,l.assoc_in.call(null,_,l.vector(l.keyword("content-type-index"),l.keyword("id").call(null,c)),c))},O=function(o,c){if(l.truth_.call(null,confirm(l.str.call(null,'Are you sure you want to delete "',l.keyword("name").call(null,c),'"?')))){const v=l.assoc.call(null,f,l.keyword("body"),l.array_map(l.keyword("content-type-id"),l.keyword("id").call(null,c)));return o.preventDefault(),I.post.call(null,"/api/delete-content-type",v),p.call(null,l.update.call(null,_,l.keyword("content-type-index"),function(r){return l.dissoc.call(null,r,l.keyword("id").call(null,c))}))}},G=function(o,c,v){o.preventDefault();const r=l.apply.call(null,l.max,0,l.map.call(null,l.keyword("rank"),l.keyword("fields").call(null,c)))+1,s=l.assoc.call(null,yl.call(null,v),l.keyword("rank"),r),k=l.assoc.call(null,f,l.keyword("body"),l.array_map(l.keyword("content-type-id"),l.keyword("id").call(null,c)));return I.post.call(null,"/api/new-content-type-field",k),p.call(null,l.update_in.call(null,_,l.vector(l.keyword("content-type-index"),l.keyword("id").call(null,c),l.keyword("fields")),l.conj,s))},i=_,w=l.__destructure_map.call(null,i),T=l.get.call(null,w,l.keyword("content-type-index")),b=l.sort_by.call(null,l.keyword("created-at"),l._GT_,l.vals.call(null,T));return l.vector(l.keyword("div"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4"),l.vector(F.box,l.array_map(l.keyword("title"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center"),l.vector(l.keyword("div"),"Content Types")),l.keyword("content"),l.vector(F.content_item_counts,l.array_map(l.keyword("content-types"),b))))),function c(v){return new l.LazySeq(null,function(){let r=v;for(;;){const s=l.seq.call(null,r);if(l.truth_.call(null,s)){const k=s;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,k))){const x=l.chunk_first.call(null,k),K=l.count.call(null,x),q=l.chunk_buffer.call(null,K);return(()=>{let R=0;for(;;){if(R<K){const D=l._nth.call(null,x,R);l.chunk_append.call(null,q,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4 pt-0",l.keyword("key"),l.keyword("id").call(null,D)),l.vector(F.box,l.array_map(l.keyword("title"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center group"),l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("class"),"max-w-xl",l.keyword("size"),l.keyword("text-2xl"),l.keyword("name"),l.keyword("content-type-name"),l.keyword("placeholder"),"Name",l.keyword("readonly"),!0,l.keyword("default-value"),l.keyword("name").call(null,D)))),l.keyword("content"),l.truth_.call(null,l.seq.call(null,l.keyword("fields").call(null,D)))?l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,D)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-start")),l.vector(tl,l.array_map(l.keyword("state"),_,l.keyword("set-state"),p,l.keyword("content-type"),D,l.keyword("anti-forgery-token"),d))):null)))),R=l.unchecked_inc.call(null,R);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,q),c.call(null,l.chunk_rest.call(null,k))):l.chunk_cons.call(null,l.chunk.call(null,q),null)}else{const x=l.first.call(null,k);return l.cons.call(null,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4 pt-0",l.keyword("key"),l.keyword("id").call(null,x)),l.vector(F.box,l.array_map(l.keyword("title"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center group"),l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("class"),"max-w-xl",l.keyword("size"),l.keyword("text-2xl"),l.keyword("name"),l.keyword("content-type-name"),l.keyword("placeholder"),"Name",l.keyword("readonly"),!0,l.keyword("default-value"),l.keyword("name").call(null,x)))),l.keyword("content"),l.truth_.call(null,l.seq.call(null,l.keyword("fields").call(null,x)))?l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,x)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-start")),l.vector(tl,l.array_map(l.keyword("state"),_,l.keyword("set-state"),p,l.keyword("content-type"),x,l.keyword("anti-forgery-token"),d))):null))),c.call(null,l.rest.call(null,k)))}}break}},null,null)}.call(null,b))};m.add_element.call(null,l.keyword("all-content-types-page"),cl.reactify_component.call(null,wl));var ml=l.vector("January","February","March","April","May","June","July","August","September","October","November","December"),pl=function(t){const n=t.getMonth(),e=t.getDate(),a=t.getFullYear(),d=t.getHours(),y=t.getMinutes(),u=l.get.call(null,ml,n),_=d<12?"AM":"PM",p=l.mod.call(null,d,12),f=p==0?12:p,E=y<10?l.str.call(null,"0",y):l.str.call(null,y);return l.str.call(null,u," ",e,", ",a," ",f,":",E," ",_)},fl=l.vector(l.array_map(l.keyword("name"),"Title",l.keyword("value"),function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("fields"));return l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"font-semibold"),l.get.call(null,a,"Title"))}),l.array_map(l.keyword("name"),"Author",l.keyword("value"),function(t){const n=t,e=l.__destructure_map.call(null,n),d=l.get.call(null,e,l.keyword("created-by")),y=l.__destructure_map.call(null,d),u=l.get.call(null,y,l.keyword("username"));return l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"font-semibold"),u)}),l.array_map(l.keyword("name"),"Date",l.keyword("value"),function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("created-at")),d=l.get.call(null,e,l.keyword("updated-at")),y=(()=>{const _=d;return l.truth_.call(null,_)?_:a})(),u=y==null?null:new Date(y);return u==null?null:pl.call(null,u)})),vl=function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("content-type")),d=l.get.call(null,e,l.keyword("content-items")),y=l.get.call(null,e,l.keyword("anti-forgery-token")),u=l.array_map(l.keyword("anti-forgery-token"),y),_=W.call(null,l.array_map(l.keyword("content-items"),l.map.call(null,function(i){return l.update.call(null,i,l.keyword("fields"),function(w){return l.update_keys.call(null,w,l.name)})},d))),p=l.nth.call(null,_,0,null),f=l.nth.call(null,_,1,null),E=function(i,w){const T=l.array_map(l.keyword("content-item-id"),l.keyword("id").call(null,w)),b=function(c,v){return l.truth_.call(null,v)?l.println.call(null,v):f.call(null,l.update.call(null,p,l.keyword("content-items"),function(r){return l.remove.call(null,function(s){return l._EQ_.call(null,l.keyword("id").call(null,s),l.keyword("id").call(null,w))},r)}))},o=l.merge.call(null,u,l.array_map(l.keyword("body"),T,l.keyword("on-complete"),b));return I.post.call(null,"/api/delete-content-item",o)},U=p,O=l.__destructure_map.call(null,U),G=l.get.call(null,O,l.keyword("content-items"));return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4"),l.vector(F.table,l.array_map(l.keyword("title"),l.keyword("name").call(null,a),l.keyword("columns"),fl,l.keyword("rows"),l.map.call(null,function(i){return l.assoc.call(null,i,l.keyword("content-type"),a)},G),l.keyword("delete-row"),E)))};m.add_element.call(null,l.keyword("single-content-type-page"),cl.reactify_component.call(null,vl));var hl=function(t){const n=t,e=l.__destructure_map.call(null,n),a=e,d=l.get.call(null,e,l.keyword("on-start")),y=l.str.call(null,l.gensym.call(null)),u=W.call(null,!1),_=l.nth.call(null,u,0,null),p=l.nth.call(null,u,1,null),f=l.dissoc.call(null,a,l.keyword("on-start"));return Gl.call(null,function(){if(l.truth_.call(null,l.not.call(null,_))){const E=document.getElementById(y);if(l.truth_.call(null,E)){const U=E,O=l.dissoc.call(null,a,l.keyword("on-start")),G=_l.call(null,U,O);p.call(null,!0),d.call(null,G)}}return null},[_]),l.vector(l.keyword("div"),l.merge.call(null,f,l.array_map(l.keyword("id"),y)))},rl=function(t){return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"editor bg-white"),l.vector(hl,t))},gl=function(t){return t.getSemanticHTML()},bl=l.uuid.call(null,"cd334826-1ec6-4906-8e7f-16ece1865faf"),xl=l.uuid.call(null,"6bd0ff7a-b720-4972-b98a-2aa85d179357"),El=function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("message"));return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center p-4 mb-4 text-sm text-green-800 border border-green-300 rounded-lg bg-green-50 dark:bg-gray-800 dark:text-green-400 dark:border-green-800",l.keyword("role"),"alert"),l.vector(l.keyword("svg"),l.array_map(l.keyword("class"),"flex-shrink-0 inline w-4 h-4 me-3",l.keyword("aria-hidden"),"true",l.keyword("xmlns"),"http://www.w3.org/2000/svg",l.keyword("fill"),"currentColor",l.keyword("viewBox"),"0 0 20 20"),l.vector(l.keyword("path"),l.array_map(l.keyword("d"),"M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"))),l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"sr-only"),"Info"),l.vector(l.keyword("div"),a))},Ql=function(t){const n=t,e=l.__destructure_map.call(null,n),a=l.get.call(null,e,l.keyword("anti-forgery-token")),d=l.get.call(null,e,l.keyword("submit-form-url")),y=l.get.call(null,e,l.keyword("submitting-button-text")),u=l.get.call(null,e,l.keyword("content-item")),_=l.get.call(null,e,l.keyword("title")),p=l.get.call(null,e,l.keyword("submit-button-text")),f=l.get.call(null,e,l.keyword("content-type")),E=l.get.call(null,e,l.keyword("site-base-url")),U=l.get.call(null,e,l.keyword("submit-button-class")),O=l.array_map(l.keyword("anti-forgery-token"),a),G=W.call(null,l.array_map(l.keyword("submitting"),!1,l.keyword("editors"),l.array_map(),l.keyword("content-item"),l.array_map(l.keyword("form-fields"),(()=>{const r=l.keyword("document").call(null,u);return l.truth_.call(null,r)?r:l.array_map()})()),l.keyword("messages"),l.vector())),i=l.nth.call(null,G,0,null),w=l.nth.call(null,G,1,null),T=function(r,s){return w.call(null,l.assoc_in.call(null,i,l.vector(l.keyword("editors"),r),s))},b=function(r){return w.call(null,l.update.call(null,i,l.keyword("messages"),l.conj,r))},o=function(r,s){const k=r.target.value;return w.call(null,l.assoc_in.call(null,i,l.vector(l.keyword("content-item"),l.keyword("form-fields"),s),k))},c=function(r,s){const k=s,x=l.__destructure_map.call(null,k),K=l.get.call(null,x,l.keyword("content-item-slug"));r.preventDefault();const q=w.call(null,l.assoc.call(null,i,l.keyword("submitting"),!0)),R=l.get_in.call(null,q,l.vector(l.keyword("content-item"),l.keyword("form-fields"))),D=l.update_vals.call(null,l.keyword("editors").call(null,q),gl),M=l.merge.call(null,R,D,l.array_map(xl,K)),P=(()=>{const A=l.array_map(l.keyword("content-type-id"),l.keyword("id").call(null,f),l.keyword("document"),M);return l.truth_.call(null,u)?l.assoc.call(null,A,l.keyword("content-item-id"),l.keyword("id").call(null,u)):A})(),z=function(A,j){return l.println.call(null,A),l.truth_.call(null,j)?l.println.call(null,j):l.truth_.call(null,u)?b.call(null,l.vector(El,l.array_map(l.keyword("message"),l.vector(l.keyword("span.font-semibold"),ll.capitalize.call(null,ll.singular.call(null,l.name.call(null,l.keyword("slug").call(null,f))))," updated!")))):window.location=l.str.call(null,"/admin/content-types/",l.name.call(null,l.keyword("slug").call(null,f)),"/",l.keyword("content-item-slug").call(null,A)),w.call(null,l.assoc.call(null,i,l.keyword("submitting"),!1))},S=l.assoc.call(null,l.assoc.call(null,O,l.keyword("body"),P),l.keyword("on-complete"),z);return I.post.call(null,d,S)},v=null;return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4 pt-0"),F.box.call(null,l.array_map(l.keyword("title"),_,l.keyword("content"),(()=>{const r=i,s=l.__destructure_map.call(null,r),k=l.get.call(null,s,l.keyword("content-item")),x=l.get.call(null,s,l.keyword("messages")),K=l.get.call(null,s,l.keyword("submitting")),q=(()=>{const z=l.get_in.call(null,k,l.vector(l.keyword("fields"),"Slug"));if(l.truth_.call(null,z))return z;{const S=l.get_in.call(null,k,l.vector(l.keyword("form-fields"),bl));return S==null?null:sl.call(null,S)}})(),R=l.array_map(l.keyword("content-type-slug"),l.keyword("slug").call(null,f),l.keyword("content-item-slug"),q),D=null,M=l.truth_.call(null,Tl.blank_QMARK_.call(null,q))?null:l.str.call(null,E),P=l.map_indexed.call(null,l.vector,l.remove.call(null,l.comp.call(null,l.hash_set("Slug"),l.keyword("name")),l.sort_by.call(null,l.keyword("rank"),l.keyword("fields").call(null,f))));return l.vector(l.keyword("div"),function S(A){return new l.LazySeq(null,function(){let j=A;for(;;){const J=l.seq.call(null,j);if(l.truth_.call(null,J)){const Q=J;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,Q))){const L=l.chunk_first.call(null,Q),C=l.count.call(null,L),h=l.chunk_buffer.call(null,C);return(()=>{let N=0;for(;;){if(N<C){const H=l._nth.call(null,L,N);l.chunk_append.call(null,h,H),N=l.unchecked_inc.call(null,N);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,h),S.call(null,l.chunk_rest.call(null,Q))):l.chunk_cons.call(null,l.chunk.call(null,h),null)}else{const L=l.first.call(null,Q);return l.cons.call(null,L,S.call(null,l.rest.call(null,Q)))}}break}},null,null)}.call(null,l.distinct.call(null,x)),l.vector(l.keyword("form"),l.array_map(l.keyword("on-submit"),function(z){return c.call(null,z,l.array_map(l.keyword("content-item-slug"),q))}),l.vector(l.keyword("div"),function S(A){return new l.LazySeq(null,function(){let j=A;for(;;){const J=l.seq.call(null,j);if(l.truth_.call(null,J)){const Q=J;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,Q))){const L=l.chunk_first.call(null,Q),C=l.count.call(null,L),h=l.chunk_buffer.call(null,C);return(()=>{let N=0;for(;;){if(N<C){const H=l._nth.call(null,L,N),$=l.nth.call(null,H,0,null),g=l.nth.call(null,H,1,null),V=g,Z=l.__destructure_map.call(null,V),Y=l.get.call(null,Z,l.keyword("type")),B=l.get.call(null,Z,l.keyword("name"));l.chunk_append.call(null,h,l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,g)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),l.str.call(null,"mb-2 pb-2 pt-2 ",l.truth_.call(null,l._EQ_.call(null,$,l.count.call(null,P)-1))?null:"border-b")),(()=>{const nl=Y,al=l.truth_.call(null,l.keyword_QMARK_.call(null,nl))?l.subs.call(null,l.str.call(null,nl),1):null;switch(al){case"text":return l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("class"),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,g),"Title"))?"w-full":null,l.keyword("name"),B,l.keyword("placeholder"),B,l.keyword("default-value"),l.get_in.call(null,k,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,g))),l.keyword("on-change"),function(X){return o.call(null,X,l.keyword("id").call(null,g))}));case"text-lg":return l.vector(rl,l.array_map(l.keyword("class"),"h-72",l.keyword("html"),l.get_in.call(null,k,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,g))),l.keyword("on-start"),function(X){return T.call(null,l.keyword("id").call(null,g),X)}));case"choice":return l.vector(m.select);case"datetime":return l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("name"),B,l.keyword("placeholder"),B));case"number":return l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("number"),l.keyword("name"),B,l.keyword("placeholder"),B));default:throw new Error(l.str.call(null,"No matching clause: ",al))}})(),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,g),"Title"))?l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"mt-2 text-sm"),l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-500"),"Permalink: "),l.truth_.call(null,M)?l.vector(l.keyword("a"),l.array_map(l.keyword("class"),"underline",l.keyword("href"),M),M):l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-400"),E,"/\u2026")):null))),N=l.unchecked_inc.call(null,N);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,h),S.call(null,l.chunk_rest.call(null,Q))):l.chunk_cons.call(null,l.chunk.call(null,h),null)}else{const L=l.first.call(null,Q),C=l.nth.call(null,L,0,null),h=l.nth.call(null,L,1,null),N=h,H=l.__destructure_map.call(null,N),$=l.get.call(null,H,l.keyword("type")),g=l.get.call(null,H,l.keyword("name"));return l.cons.call(null,l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,h)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),l.str.call(null,"mb-2 pb-2 pt-2 ",l.truth_.call(null,l._EQ_.call(null,C,l.count.call(null,P)-1))?null:"border-b")),(()=>{const V=$,Z=l.truth_.call(null,l.keyword_QMARK_.call(null,V))?l.subs.call(null,l.str.call(null,V),1):null;switch(Z){case"text":return l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("class"),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,h),"Title"))?"w-full":null,l.keyword("name"),g,l.keyword("placeholder"),g,l.keyword("default-value"),l.get_in.call(null,k,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,h))),l.keyword("on-change"),function(Y){return o.call(null,Y,l.keyword("id").call(null,h))}));case"text-lg":return l.vector(rl,l.array_map(l.keyword("class"),"h-72",l.keyword("html"),l.get_in.call(null,k,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,h))),l.keyword("on-start"),function(Y){return T.call(null,l.keyword("id").call(null,h),Y)}));case"choice":return l.vector(m.select);case"datetime":return l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("name"),g,l.keyword("placeholder"),g));case"number":return l.vector(m.input,l.array_map(l.keyword("type"),l.keyword("number"),l.keyword("name"),g,l.keyword("placeholder"),g));default:throw new Error(l.str.call(null,"No matching clause: ",Z))}})(),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,h),"Title"))?l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"mt-2 text-sm"),l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-500"),"Permalink: "),l.truth_.call(null,M)?l.vector(l.keyword("a"),l.array_map(l.keyword("class"),"underline",l.keyword("href"),M),M):l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-400"),E,"/\u2026")):null)),S.call(null,l.rest.call(null,Q)))}}break}},null,null)}.call(null,P),l.vector(l.keyword("button"),l.array_map(l.keyword("type"),l.keyword("submit"),l.keyword("class"),l.str.call(null,"text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800",U),l.keyword("disabled"),K),l.truth_.call(null,K)?l.vector(l.keyword("span"),l.vector(m.spinner),y):p))))})())))};export{kl as __GT_content_type,yl as __GT_field,sl as __GT_slug,wl as all_content_types_page,fl as columns,tl as content_type_fields_form,Ql as content_type_new_item_form,rl as editor,hl as editor_impl,pl as format_datetime,il as index_by,ml as months,dl as quill_defaults,gl as quill_get_semantic_html,ul as quill_toolbar,el as random_uuid,vl as single_content_type_page,xl as slug_field_id,_l as start_quill_BANG_,El as success_alert,bl as title_field_id};
