import*as l from"cherry-cljs/cljs.core.js";import{useEffect as sl}from"react";import*as il from"cherry-cljs/lib/clojure.string.js";import*as J from"rads.inflections";import*as S from"rpub.admin.impl";import{use_dag as V}from"rpub.lib.dag.react";import*as A from"rpub.lib.html";import*as W from"rpub.lib.http";var Z=function(){return crypto.randomUUID()},X=l.vector(l.vector(l.array_map(l.keyword("header"),l.vector(1,2,3,4,5,6,null))),l.vector("bold","italic"),l.vector(l.array_map(l.keyword("list"),"bullet"),l.array_map(l.keyword("list"),"ordered")),l.vector("blockquote"),l.vector(l.array_map(l.keyword("align"),""),l.array_map(l.keyword("align"),"center"),l.array_map(l.keyword("align"),"right")),l.vector("link"),l.vector("clean")),ll=l.array_map(l.keyword("theme"),l.keyword("snow"),l.keyword("modules"),l.array_map(l.keyword("toolbar"),X)),kl=function(a,s){const r=l.merge.call(null,ll,l.dissoc.call(null,s,l.keyword("html"))),d=new Quill(a,l.clj__GT_js.call(null,r)),c=l.keyword("html").call(null,s);if(l.truth_.call(null,c)){const p=c;d.clipboard.dangerouslyPasteHTML(p)}return d},wl=function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("name")),c=l.get.call(null,r,l.keyword("type"));return l.array_map(l.keyword("id"),Z.call(null),l.keyword("name"),d,l.keyword("type"),c)},el=function(a){return J.parameterize.call(null,a)},pl=function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("id")),c=l.get.call(null,r,l.keyword("name")),p=l.get.call(null,r,l.keyword("slug")),b=l.get.call(null,r,l.keyword("fields"));return l.array_map(l.keyword("id"),(()=>{const Q=Z.call(null);return l.truth_.call(null,Q)?Q:d})(),l.keyword("name"),c,l.keyword("slug"),p,l.keyword("fields"),b,l.keyword("created-at"),new Date,l.keyword("content-item-count"),0)},j=function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("anti-forgery-token")),c=l.get.call(null,r,l.keyword("content-type")),p=l.get.call(null,r,l.keyword("class")),b=V.call(null,l.vector(l.keyword("all-content-types-page/selection"))),Q=l.nth.call(null,b,0,null),i=l.__destructure_map.call(null,Q),f=l.get.call(null,i,l.keyword("all-content-types-page/selection")),k=l.nth.call(null,b,1,null),K=l.array_map(l.keyword("anti-forgery-token"),d,l.keyword("format"),l.keyword("transit"));return l.vector(l.keyword("form"),l.array_map(l.keyword("method"),"post",l.keyword("class"),p),l.vector(l.keyword("input"),l.array_map(l.keyword("id"),"__anti-forgery-token",l.keyword("name"),"__anti-forgery-token",l.keyword("type"),"hidden",l.keyword("value"),d)),l.vector(l.keyword("input"),l.array_map(l.keyword("type"),"hidden",l.keyword("name"),"content-type-id",l.keyword("value"),l.keyword("id").call(null,c))),l.vector(l.keyword("input"),l.array_map(l.keyword("type"),"hidden",l.keyword("name"),"content-type-name",l.keyword("value"),l.keyword("name").call(null,c))),l.vector(l.keyword("div"),function D(q){return new l.LazySeq(null,function(){let h=q;for(;;){const w=l.seq.call(null,h);if(l.truth_.call(null,w)){const m=w;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,m))){const _=l.chunk_first.call(null,m),x=l.count.call(null,_),n=l.chunk_buffer.call(null,x);return(()=>{let e=0;for(;;){if(e<x){const t=l._nth.call(null,_,e),y=(()=>{const o=l._EQ_.call(null,l.get_in.call(null,f,l.vector(l.keyword("content-type"),l.keyword("id"))),l.keyword("id").call(null,c));return l.truth_.call(null,o)?l._EQ_.call(null,l.get_in.call(null,f,l.vector(l.keyword("content-type-field"),l.keyword("id"))),l.keyword("id").call(null,t)):o})();l.chunk_append.call(null,n,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),l.str.call(null,"my-2 flex items-center","w-full font-semibold border ","rounded-[6px] hover:border-blue-500 ",l.truth_.call(null,y)?"ring-2 ring-blue-400 border-blue-500":"border-gray-200"),l.keyword("data-content-type-field-id"),l.keyword("id").call(null,t),l.keyword("key"),l.keyword("id").call(null,t)),l.vector(l.keyword("label"),l.array_map(l.keyword("for"),"field-name")),l.vector(l.keyword("div.w-full.pl-2.py-2"),l.array_map(l.keyword("onClick"),function(o){return k.call(null,l.keyword("all-content-types-page/select-content-type-field"),l.array_map(l.keyword("content-type"),c,l.keyword("content-type-field"),t))}),l.keyword("name").call(null,t)),l.vector(l.keyword("div.bg-gray-100.px-2.py-2.border-l.border-l-gray-200"),l.array_map(l.keyword("class"),"min-w-[150px] rounded-r-[6px]",l.keyword("onClick"),function(o){return k.call(null,l.keyword("all-content-types-page/select-content-type-field"),l.array_map(l.keyword("content-type"),c,l.keyword("content-type-field"),t))}),(()=>{const o=l.keyword("type").call(null,t);switch(l.truth_.call(null,l.keyword_QMARK_.call(null,o))?l.subs.call(null,l.str.call(null,o),1):null){case"text":return"Text";case"text-lg":return"Text (Large)";default:return l.name.call(null,l.keyword("type").call(null,t))}})()))),e=l.unchecked_inc.call(null,e);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,n),D.call(null,l.chunk_rest.call(null,m))):l.chunk_cons.call(null,l.chunk.call(null,n),null)}else{const _=l.first.call(null,m),x=(()=>{const n=l._EQ_.call(null,l.get_in.call(null,f,l.vector(l.keyword("content-type"),l.keyword("id"))),l.keyword("id").call(null,c));return l.truth_.call(null,n)?l._EQ_.call(null,l.get_in.call(null,f,l.vector(l.keyword("content-type-field"),l.keyword("id"))),l.keyword("id").call(null,_)):n})();return l.cons.call(null,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),l.str.call(null,"my-2 flex items-center","w-full font-semibold border ","rounded-[6px] hover:border-blue-500 ",l.truth_.call(null,x)?"ring-2 ring-blue-400 border-blue-500":"border-gray-200"),l.keyword("data-content-type-field-id"),l.keyword("id").call(null,_),l.keyword("key"),l.keyword("id").call(null,_)),l.vector(l.keyword("label"),l.array_map(l.keyword("for"),"field-name")),l.vector(l.keyword("div.w-full.pl-2.py-2"),l.array_map(l.keyword("onClick"),function(n){return k.call(null,l.keyword("all-content-types-page/select-content-type-field"),l.array_map(l.keyword("content-type"),c,l.keyword("content-type-field"),_))}),l.keyword("name").call(null,_)),l.vector(l.keyword("div.bg-gray-100.px-2.py-2.border-l.border-l-gray-200"),l.array_map(l.keyword("class"),"min-w-[150px] rounded-r-[6px]",l.keyword("onClick"),function(n){return k.call(null,l.keyword("all-content-types-page/select-content-type-field"),l.array_map(l.keyword("content-type"),c,l.keyword("content-type-field"),_))}),(()=>{const n=l.keyword("type").call(null,_);switch(l.truth_.call(null,l.keyword_QMARK_.call(null,n))?l.subs.call(null,l.str.call(null,n),1):null){case"text":return"Text";case"text-lg":return"Text (Large)";default:return l.name.call(null,l.keyword("type").call(null,_))}})())),D.call(null,l.rest.call(null,m)))}}break}},null,null)}.call(null,l.sort_by.call(null,l.keyword("rank"),l.keyword("fields").call(null,c)))))},O=l.vector(l.array_map(l.keyword("label"),"Text",l.keyword("description"),"Ask for text with optional formatting.",l.keyword("type"),l.keyword("text")),l.array_map(l.keyword("label"),"Date and Time",l.keyword("description"),"Ask for a date and time with a date picker.",l.keyword("type"),l.keyword("datetime")),l.array_map(l.keyword("label"),"Number",l.keyword("description"),"Ask for a whole number or a decimal.",l.keyword("type"),l.keyword("number")),l.array_map(l.keyword("label"),"Media",l.keyword("description"),"Ask for an image or video.",l.keyword("type"),l.keyword("media")),l.array_map(l.keyword("label"),"Choice",l.keyword("description"),"Ask for a choice between multiple options.",l.keyword("type"),l.keyword("choice")),l.array_map(l.keyword("label"),"Group",l.keyword("description"),"Combine multiple fields into a group.",l.keyword("type"),l.keyword("group"))),tl=function(a){const s=a,r=l.__destructure_map.call(null,s),d=r,c=l.get.call(null,r,l.keyword("anti-forgery-token")),p=V.call(null,l.vector(l.keyword("all-content-types-page/selection"),l.keyword("model/content-types-index"))),b=l.nth.call(null,p,0,null),Q=l.__destructure_map.call(null,b),i=l.get.call(null,Q,l.keyword("all-content-types-page/selection")),f=l.get.call(null,Q,l.keyword("model/content-types-index")),k=l.nth.call(null,p,1,null),K=sl.call(null,function(){return k.call(null,l.keyword("init"),l.select_keys.call(null,d,l.vector(l.keyword("content-types"))))},[]),F=l.array_map(l.keyword("anti-forgery-token"),c,l.keyword("format"),l.keyword("transit")),D=function(h,w){return l.println.call(null,confirm(l.str.call(null,'Are you sure you want to delete the "',l.keyword("name").call(null,w),'" field?')))},q=l.sort_by.call(null,l.keyword("created-at"),l._GT_,l.vals.call(null,f));return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex",l.keyword("onClick"),function(h){return l.truth_.call(null,(()=>{const w=h.target.closest("[data-no-select]");return l.truth_.call(null,w)?w:h.target.closest("[data-content-type-field-id]")})())?null:k.call(null,l.keyword("all-content-types-page/clear-selection"))}),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex-grow"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4 pr-[384px]"),l.vector(S.box,l.array_map(l.keyword("class"),"pb-4",l.keyword("data-no-select"),!0,l.keyword("title"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center"),l.vector(l.keyword("div"),"Content Types")),l.keyword("content"),l.vector(S.content_item_counts,l.array_map(l.keyword("content-types"),q)))),function w(m){return new l.LazySeq(null,function(){let _=m;for(;;){const x=l.seq.call(null,_);if(l.truth_.call(null,x)){const n=x;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,n))){const e=l.chunk_first.call(null,n),t=l.count.call(null,e),y=l.chunk_buffer.call(null,t);return(()=>{let u=0;for(;;){if(u<t){const o=l._nth.call(null,e,u);l.chunk_append.call(null,y,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"pb-4",l.keyword("key"),l.keyword("id").call(null,o),l.keyword("data-no-select"),!0),l.vector(S.box,l.array_map(l.keyword("hover"),!0,l.keyword("on-click"),function(v){return l.truth_.call(null,v.target.closest("[data-content-type-field-id]"))?null:k.call(null,l.keyword("all-content-types-page/select-content-type"),l.array_map(l.keyword("content-type"),o))},l.keyword("on-drag-over"),function(v){return v.preventDefault()},l.keyword("on-drop"),function(v){return k.call(null,l.keyword("all-content-types-page/drag-drop"),l.array_map(l.keyword("content-type"),o))},l.keyword("selected"),(()=>{const v=l.not.call(null,l.keyword("content-type-field").call(null,i));return l.truth_.call(null,v)?l._EQ_.call(null,l.get_in.call(null,i,l.vector(l.keyword("content-type"),l.keyword("id"))),l.keyword("id").call(null,o)):v})(),l.keyword("title"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center group"),l.vector(l.keyword("h3"),l.array_map(l.keyword("class"),"text-2xl"),l.keyword("name").call(null,o))),l.keyword("content"),l.truth_.call(null,l.seq.call(null,l.keyword("fields").call(null,o)))?l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,o)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-start")),l.vector(j,l.array_map(l.keyword("content-type"),o,l.keyword("anti-forgery-token"),c))):null)))),u=l.unchecked_inc.call(null,u);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,y),w.call(null,l.chunk_rest.call(null,n))):l.chunk_cons.call(null,l.chunk.call(null,y),null)}else{const e=l.first.call(null,n);return l.cons.call(null,l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"pb-4",l.keyword("key"),l.keyword("id").call(null,e),l.keyword("data-no-select"),!0),l.vector(S.box,l.array_map(l.keyword("hover"),!0,l.keyword("on-click"),function(t){return l.truth_.call(null,t.target.closest("[data-content-type-field-id]"))?null:k.call(null,l.keyword("all-content-types-page/select-content-type"),l.array_map(l.keyword("content-type"),e))},l.keyword("on-drag-over"),function(t){return t.preventDefault()},l.keyword("on-drop"),function(t){return k.call(null,l.keyword("all-content-types-page/drag-drop"),l.array_map(l.keyword("content-type"),e))},l.keyword("selected"),(()=>{const t=l.not.call(null,l.keyword("content-type-field").call(null,i));return l.truth_.call(null,t)?l._EQ_.call(null,l.get_in.call(null,i,l.vector(l.keyword("content-type"),l.keyword("id"))),l.keyword("id").call(null,e)):t})(),l.keyword("title"),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center group"),l.vector(l.keyword("h3"),l.array_map(l.keyword("class"),"text-2xl"),l.keyword("name").call(null,e))),l.keyword("content"),l.truth_.call(null,l.seq.call(null,l.keyword("fields").call(null,e)))?l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,e)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-start")),l.vector(j,l.array_map(l.keyword("content-type"),e,l.keyword("anti-forgery-token"),c))):null))),w.call(null,l.rest.call(null,n)))}}break}},null,null)}.call(null,q))),(()=>{const h=function(w){const m=w,_=l.__destructure_map.call(null,m),x=_,n=l.get.call(null,_,l.keyword("label")),e=l.get.call(null,_,l.keyword("description")),t=l.get.call(null,_,l.keyword("selected"));return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),l.str.call(null,"border rounded-[6px] p-2 mb-4 bg-gray-50 cursor-move ",l.truth_.call(null,t)?"ring-2 ring-blue-400 border-blue-500":"border-gray-200"),l.keyword("draggable"),!0,l.keyword("onDragStart"),function(y){return k.call(null,l.keyword("all-content-types-page/drag-start"),x),l.println.call(null,l.keyword("onDragStart"),y)}),l.vector(l.keyword("h4"),l.array_map(l.keyword("class"),"font-semibold"),n),l.vector(l.keyword("p"),l.array_map(l.keyword("class"),"text-sm text-gray-500"),e))};return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4 pl-2 w-[376px] fixed right-0 bottom-0 top-12",l.keyword("data-no-select"),!0),l.truth_.call(null,i)?l.truth_.call(null,l.keyword("content-type-field").call(null,i))?l.vector(S.box,l.array_map(l.keyword("class"),"h-full",l.keyword("title"),l.vector(l.keyword("h3"),l.array_map(l.keyword("class"),"text-2xl font-app-serif font-semibold"),l.vector(l.keyword("span.italic.text-blue-600"),"Field: "),l.get_in.call(null,i,l.vector(l.keyword("content-type-field"),l.keyword("name")))),l.keyword("content"),l.vector(l.keyword("div"),l.vector(l.keyword("div.mb-8"),l.vector(A.action_button,l.array_map(l.keyword("class"),"mt-2 mr-2"),"Rename Field"),l.vector(A.delete_button,l.array_map(l.keyword("class"),"mt-2",l.keyword("on-click"),function(w){return D.call(null,w,l.keyword("content-type-field").call(null,i))}),"Delete Field")),l.vector(l.keyword("h4"),l.array_map(l.keyword("class"),"text-xl font-app-serif font-semibold mb-4"),"Change Field Type"),l.vector(l.keyword("div"),function m(_){return new l.LazySeq(null,function(){let x=_;for(;;){const n=l.seq.call(null,x);if(l.truth_.call(null,n)){const e=n;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,e))){const t=l.chunk_first.call(null,e),y=l.count.call(null,t),u=l.chunk_buffer.call(null,y);return(()=>{let o=0;for(;;){if(o<y){const M=l._nth.call(null,t,o);l.chunk_append.call(null,u,h.call(null,l.assoc.call(null,M,l.keyword("selected"),l._EQ_.call(null,l.get_in.call(null,i,l.vector(l.keyword("content-type-field"),l.keyword("type"))),l.keyword("type").call(null,M))))),o=l.unchecked_inc.call(null,o);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,u),m.call(null,l.chunk_rest.call(null,e))):l.chunk_cons.call(null,l.chunk.call(null,u),null)}else{const t=l.first.call(null,e);return l.cons.call(null,h.call(null,l.assoc.call(null,t,l.keyword("selected"),l._EQ_.call(null,l.get_in.call(null,i,l.vector(l.keyword("content-type-field"),l.keyword("type"))),l.keyword("type").call(null,t)))),m.call(null,l.rest.call(null,e)))}}break}},null,null)}.call(null,O))))):l.truth_.call(null,l.keyword("content-type").call(null,i))?l.vector(S.box,l.array_map(l.keyword("class"),"h-full",l.keyword("title"),l.vector(l.keyword("h3"),l.array_map(l.keyword("class"),"text-2xl font-app-serif font-semibold"),l.vector(l.keyword("span.italic.text-blue-600"),"Content Type: "),l.get_in.call(null,i,l.vector(l.keyword("content-type"),l.keyword("name")))),l.keyword("content"),l.vector(l.keyword("div"),l.vector(l.keyword("div.mb-8"),l.vector(A.action_button,l.array_map(l.keyword("class"),"mt-2 mr-2"),"Rename Content Type"),l.vector(A.delete_button,l.array_map(l.keyword("class"),"mt-2",l.keyword("on-click"),function(w){return D.call(null,w,l.keyword("content-type-field").call(null,i))}),"Delete Content Type")),l.vector(l.keyword("h4"),l.array_map(l.keyword("class"),"text-xl font-app-serif font-semibold mb-4"),"Add Field"),l.vector(l.keyword("div"),function m(_){return new l.LazySeq(null,function(){let x=_;for(;;){const n=l.seq.call(null,x);if(l.truth_.call(null,n)){const e=n;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,e))){const t=l.chunk_first.call(null,e),y=l.count.call(null,t),u=l.chunk_buffer.call(null,y);return(()=>{let o=0;for(;;){if(o<y){const M=l._nth.call(null,t,o);l.chunk_append.call(null,u,h.call(null,M)),o=l.unchecked_inc.call(null,o);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,u),m.call(null,l.chunk_rest.call(null,e))):l.chunk_cons.call(null,l.chunk.call(null,u),null)}else{const t=l.first.call(null,e);return l.cons.call(null,h.call(null,t),m.call(null,l.rest.call(null,e)))}}break}},null,null)}.call(null,O))))):null:l.vector(S.box,l.array_map(l.keyword("class"),"h-full",l.keyword("title"),l.vector(l.keyword("h3"),l.array_map(l.keyword("class"),"text-2xl font-app-serif font-semibold"),"Add Field"),l.keyword("content"),l.vector(l.keyword("div"),l.vector(l.keyword("div"),function m(_){return new l.LazySeq(null,function(){let x=_;for(;;){const n=l.seq.call(null,x);if(l.truth_.call(null,n)){const e=n;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,e))){const t=l.chunk_first.call(null,e),y=l.count.call(null,t),u=l.chunk_buffer.call(null,y);return(()=>{let o=0;for(;;){if(o<y){const M=l._nth.call(null,t,o);l.chunk_append.call(null,u,h.call(null,M)),o=l.unchecked_inc.call(null,o);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,u),m.call(null,l.chunk_rest.call(null,e))):l.chunk_cons.call(null,l.chunk.call(null,u),null)}else{const t=l.first.call(null,e);return l.cons.call(null,h.call(null,t),m.call(null,l.rest.call(null,e)))}}break}},null,null)}.call(null,O))))))})())};A.add_element.call(null,l.keyword("all-content-types-page"),S.wrap_component.call(null,tl),l.array_map(l.keyword("format"),l.keyword("transit")));var rl=l.vector("January","February","March","April","May","June","July","August","September","October","November","December"),nl=function(a){const s=a.getMonth(),r=a.getDate(),d=a.getFullYear(),c=a.getHours(),p=a.getMinutes(),b=l.get.call(null,rl,s),Q=c<12?"AM":"PM",i=l.mod.call(null,c,12),f=i===0?12:i,k=p<10?l.str.call(null,"0",p):l.str.call(null,p);return l.str.call(null,b," ",r,", ",d," ",f,":",k," ",Q)},ol=l.vector(l.array_map(l.keyword("name"),"Title",l.keyword("value"),function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("fields"));return l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"font-semibold"),l.get.call(null,d,"Title"))}),l.array_map(l.keyword("name"),"Author",l.keyword("value"),function(a){const s=a,r=l.__destructure_map.call(null,s),c=l.get.call(null,r,l.keyword("created-by")),p=l.__destructure_map.call(null,c),b=l.get.call(null,p,l.keyword("username"));return l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"font-semibold"),b)}),l.array_map(l.keyword("name"),"Date",l.keyword("value"),function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("created-at")),c=l.get.call(null,r,l.keyword("updated-at")),p=(()=>{const Q=c;return l.truth_.call(null,Q)?Q:d})(),b=p==null?null:new Date(p);return b==null?null:nl.call(null,b)})),al=function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("content-type")),c=l.get.call(null,r,l.keyword("content-items")),p=l.get.call(null,r,l.keyword("anti-forgery-token")),b=l.array_map(l.keyword("anti-forgery-token"),p,l.keyword("format"),l.keyword("transit")),Q=l.map.call(null,function(f){return l.update.call(null,f,l.keyword("fields"),function(k){return l.update_keys.call(null,k,l.name)})},c),i=function(f,k){const K=l.array_map(l.keyword("content-item-id"),l.keyword("id").call(null,k)),F=function(q,h){if(l.truth_.call(null,h))return l.println.call(null,h)},D=l.merge.call(null,b,l.array_map(l.keyword("body"),K,l.keyword("on-complete"),F));return W.post.call(null,"/api/delete-content-item",D)};return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4"),l.vector(S.table,l.array_map(l.keyword("title"),l.keyword("name").call(null,d),l.keyword("columns"),ol,l.keyword("rows"),l.map.call(null,function(f){return l.assoc.call(null,f,l.keyword("content-type"),d)},Q),l.keyword("delete-row"),i)))};A.add_element.call(null,l.keyword("single-content-type-page"),S.wrap_component.call(null,al),l.array_map(l.keyword("format"),l.keyword("transit")));var cl=function(a){return null},B=function(a){return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"editor bg-white"),l.vector(cl,a))},ul=function(a){return a.getSemanticHTML()},_l=l.uuid.call(null,"cd334826-1ec6-4906-8e7f-16ece1865faf"),dl=l.uuid.call(null,"6bd0ff7a-b720-4972-b98a-2aa85d179357"),yl=function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("message"));return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"flex items-center p-4 mb-4 text-sm text-green-800 border border-green-300 rounded-lg bg-green-50 dark:bg-gray-800 dark:text-green-400 dark:border-green-800",l.keyword("role"),"alert"),l.vector(l.keyword("svg"),l.array_map(l.keyword("class"),"flex-shrink-0 inline w-4 h-4 me-3",l.keyword("aria-hidden"),"true",l.keyword("xmlns"),"http://www.w3.org/2000/svg",l.keyword("fill"),"currentColor",l.keyword("viewBox"),"0 0 20 20"),l.vector(l.keyword("path"),l.array_map(l.keyword("d"),"M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"))),l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"sr-only"),"Info"),l.vector(l.keyword("div"),d))},ml=function(a){const s=a,r=l.__destructure_map.call(null,s),d=l.get.call(null,r,l.keyword("anti-forgery-token")),c=l.get.call(null,r,l.keyword("submit-form-url")),p=l.get.call(null,r,l.keyword("submitting-button-text")),b=l.get.call(null,r,l.keyword("content-item")),Q=l.get.call(null,r,l.keyword("title")),i=l.get.call(null,r,l.keyword("submit-button-text")),f=l.get.call(null,r,l.keyword("content-type")),k=l.get.call(null,r,l.keyword("site-base-url")),K=l.get.call(null,r,l.keyword("submit-button-class")),F=l.array_map(l.keyword("anti-forgery-token"),d,l.keyword("format"),l.keyword("transit")),D=!1,q=l.array_map(l.keyword("form-fields"),(()=>{const n=l.keyword("document").call(null,b);return l.truth_.call(null,n)?n:l.array_map()})()),h=l.vector(),w=function(n,e){return null},m=function(n){return null},_=function(n,e){const t=n.target.value;return null},x=function(n,e){const t=e,y=l.__destructure_map.call(null,t),u=l.get.call(null,y,l.keyword("content-item-slug"));n.preventDefault();const o=null,M=l.get_in.call(null,o,l.vector(l.keyword("content-item"),l.keyword("form-fields"))),v=l.update_vals.call(null,l.keyword("editors").call(null,o),ul),E=l.merge.call(null,M,v,l.array_map(dl,u)),z=(()=>{const G=l.array_map(l.keyword("content-type-id"),l.keyword("id").call(null,f),l.keyword("document"),E);return l.truth_.call(null,q)?l.assoc.call(null,G,l.keyword("content-item-id"),l.keyword("id").call(null,q)):G})(),R=function(G,L){return l.println.call(null,G),l.truth_.call(null,L)?l.println.call(null,L):l.truth_.call(null,q)?m.call(null,l.vector(yl,l.array_map(l.keyword("message"),l.vector(l.keyword("span.font-semibold"),J.capitalize.call(null,J.singular.call(null,l.name.call(null,l.keyword("slug").call(null,f))))," updated!")))):window.location=l.str.call(null,"/admin/content-types/",l.name.call(null,l.keyword("slug").call(null,f)),"/",l.keyword("content-item-slug").call(null,G))},g=l.assoc.call(null,l.assoc.call(null,F,l.keyword("body"),z),l.keyword("on-complete"),R);return W.post.call(null,c,g)};return l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"p-4 pt-0"),S.box.call(null,l.array_map(l.keyword("title"),Q,l.keyword("content"),(()=>{const n=(()=>{const y=l.get_in.call(null,q,l.vector(l.keyword("fields"),"Slug"));if(l.truth_.call(null,y))return y;{const u=l.get_in.call(null,q,l.vector(l.keyword("form-fields"),_l));return u==null?null:el.call(null,u)}})(),e=l.truth_.call(null,il.blank_QMARK_.call(null,n))?null:l.str.call(null,k),t=l.map_indexed.call(null,l.vector,l.remove.call(null,l.comp.call(null,l.hash_set("Slug"),l.keyword("name")),l.sort_by.call(null,l.keyword("rank"),l.keyword("fields").call(null,f))));return l.vector(l.keyword("div"),function u(o){return new l.LazySeq(null,function(){let M=o;for(;;){const v=l.seq.call(null,M);if(l.truth_.call(null,v)){const E=v;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,E))){const z=l.chunk_first.call(null,E),R=l.count.call(null,z),g=l.chunk_buffer.call(null,R);return(()=>{let G=0;for(;;){if(G<R){const L=l._nth.call(null,z,G);l.chunk_append.call(null,g,L),G=l.unchecked_inc.call(null,G);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,g),u.call(null,l.chunk_rest.call(null,E))):l.chunk_cons.call(null,l.chunk.call(null,g),null)}else{const z=l.first.call(null,E);return l.cons.call(null,z,u.call(null,l.rest.call(null,E)))}}break}},null,null)}.call(null,l.distinct.call(null,h)),l.vector(l.keyword("form"),l.array_map(l.keyword("on-submit"),function(y){return x.call(null,y,l.array_map(l.keyword("content-item-slug"),n))}),l.vector(l.keyword("div"),function u(o){return new l.LazySeq(null,function(){let M=o;for(;;){const v=l.seq.call(null,M);if(l.truth_.call(null,v)){const E=v;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,E))){const z=l.chunk_first.call(null,E),R=l.count.call(null,z),g=l.chunk_buffer.call(null,R);return(()=>{let G=0;for(;;){if(G<R){const L=l._nth.call(null,z,G),I=l.nth.call(null,L,0,null),T=l.nth.call(null,L,1,null),U=T,N=l.__destructure_map.call(null,U),H=l.get.call(null,N,l.keyword("type")),C=l.get.call(null,N,l.keyword("name"));l.chunk_append.call(null,g,l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,T)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),l.str.call(null,"mb-2 pb-2 pt-2 ",l.truth_.call(null,l._EQ_.call(null,I,l.count.call(null,t)-1))?null:"border-b")),(()=>{const Y=H,$=l.truth_.call(null,l.keyword_QMARK_.call(null,Y))?l.subs.call(null,l.str.call(null,Y),1):null;switch($){case"text":return l.vector(A.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("class"),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,T),"Title"))?"w-full":null,l.keyword("name"),C,l.keyword("placeholder"),C,l.keyword("default-value"),l.get_in.call(null,q,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,T))),l.keyword("on-change"),function(P){return _.call(null,P,l.keyword("id").call(null,T))}));case"text-lg":return l.vector(B,l.array_map(l.keyword("class"),"h-72",l.keyword("html"),l.get_in.call(null,q,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,T))),l.keyword("on-start"),function(P){return w.call(null,l.keyword("id").call(null,T),P)}));case"choice":return l.vector(A.select);case"datetime":return l.vector(A.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("name"),C,l.keyword("placeholder"),C));case"number":return l.vector(A.input,l.array_map(l.keyword("type"),l.keyword("number"),l.keyword("name"),C,l.keyword("placeholder"),C));default:throw new Error(l.str.call(null,"No matching clause: ",$))}})(),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,T),"Title"))?l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"mt-2 text-sm"),l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-500"),"Permalink: "),l.truth_.call(null,e)?l.vector(l.keyword("a"),l.array_map(l.keyword("class"),"underline",l.keyword("href"),e),e):l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-400"),k,"/\u2026")):null))),G=l.unchecked_inc.call(null,G);continue}else return!0;break}})()?l.chunk_cons.call(null,l.chunk.call(null,g),u.call(null,l.chunk_rest.call(null,E))):l.chunk_cons.call(null,l.chunk.call(null,g),null)}else{const z=l.first.call(null,E),R=l.nth.call(null,z,0,null),g=l.nth.call(null,z,1,null),G=g,L=l.__destructure_map.call(null,G),I=l.get.call(null,L,l.keyword("type")),T=l.get.call(null,L,l.keyword("name"));return l.cons.call(null,l.vector(l.keyword("div"),l.array_map(l.keyword("key"),l.keyword("id").call(null,g)),l.vector(l.keyword("div"),l.array_map(l.keyword("class"),l.str.call(null,"mb-2 pb-2 pt-2 ",l.truth_.call(null,l._EQ_.call(null,R,l.count.call(null,t)-1))?null:"border-b")),(()=>{const U=I,N=l.truth_.call(null,l.keyword_QMARK_.call(null,U))?l.subs.call(null,l.str.call(null,U),1):null;switch(N){case"text":return l.vector(A.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("class"),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,g),"Title"))?"w-full":null,l.keyword("name"),T,l.keyword("placeholder"),T,l.keyword("default-value"),l.get_in.call(null,q,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,g))),l.keyword("on-change"),function(H){return _.call(null,H,l.keyword("id").call(null,g))}));case"text-lg":return l.vector(B,l.array_map(l.keyword("class"),"h-72",l.keyword("html"),l.get_in.call(null,q,l.vector(l.keyword("form-fields"),l.keyword("id").call(null,g))),l.keyword("on-start"),function(H){return w.call(null,l.keyword("id").call(null,g),H)}));case"choice":return l.vector(A.select);case"datetime":return l.vector(A.input,l.array_map(l.keyword("type"),l.keyword("text"),l.keyword("name"),T,l.keyword("placeholder"),T));case"number":return l.vector(A.input,l.array_map(l.keyword("type"),l.keyword("number"),l.keyword("name"),T,l.keyword("placeholder"),T));default:throw new Error(l.str.call(null,"No matching clause: ",N))}})(),l.truth_.call(null,l._EQ_.call(null,l.keyword("name").call(null,g),"Title"))?l.vector(l.keyword("div"),l.array_map(l.keyword("class"),"mt-2 text-sm"),l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-500"),"Permalink: "),l.truth_.call(null,e)?l.vector(l.keyword("a"),l.array_map(l.keyword("class"),"underline",l.keyword("href"),e),e):l.vector(l.keyword("span"),l.array_map(l.keyword("class"),"text-gray-400"),k,"/\u2026")):null)),u.call(null,l.rest.call(null,E)))}}break}},null,null)}.call(null,t),l.vector(l.keyword("button"),l.array_map(l.keyword("type"),l.keyword("submit"),l.keyword("class"),l.str.call(null,"text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800",K),l.keyword("disabled"),D),l.truth_.call(null,D)?l.vector(l.keyword("span"),l.vector(A.spinner),p):i))))})())))};export{pl as __GT_content_type,wl as __GT_field,el as __GT_slug,tl as all_content_types_page,ol as columns,j as content_type_fields_form,ml as content_type_new_item_form,B as editor,cl as editor_impl,O as field_config,nl as format_datetime,rl as months,ll as quill_defaults,ul as quill_get_semantic_html,X as quill_toolbar,Z as random_uuid,al as single_content_type_page,dl as slug_field_id,kl as start_quill_BANG_,yl as success_alert,_l as title_field_id};
