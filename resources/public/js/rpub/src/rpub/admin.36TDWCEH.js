import*as e from"cherry-cljs/cljs.core.js";import"preact/devtools";import*as Q from"cherry-cljs/lib/clojure.string.js";import*as u from"rpub.admin.impl";import{use_dag as B}from"rpub.lib.dag.react";import*as z from"rpub.lib.html";import*as q from"rpub.lib.http";import*as U from"rpub.plugins.content-types.admin";var E=function(r,a){const l=a,t=e.nth.call(null,l,0,null),c=e.nth.call(null,l,1,null);return e.assoc_in.call(null,r,e.vector(e.keyword("inputs"),t,e.keyword("value")),c)},N=function(r,a){return e.update.call(null,e.assoc.call(null,r,e.keyword("plugins-page/needs-restart"),!0),e.keyword("plugins-page/activated-plugins"),e.fnil.call(null,e.conj,e.hash_set()),a)},O=function(r,a){return e.update.call(null,e.assoc.call(null,r,e.keyword("plugins-page/needs-restart"),!0),e.keyword("plugins-page/activated-plugins"),e.disj,a)},K=function(r){return e.assoc.call(null,r,e.keyword("plugins-page/restarted"),!0)},P=function(r){return e.assoc.call(null,r,e.keyword("settings-page/submitting"),!0)},R=function(r){return e.assoc.call(null,r,e.keyword("settings-page/submitting"),!1)},F=function(r,a){return e.assoc.call(null,r,e.keyword("themes-page/current-theme-name-setting"),e.array_map(e.keyword("value"),a))},D=e.array_map(e.keyword("nodes"),e.array_map(e.keyword("plugins-page/needs-restart"),e.array_map(e.keyword("calc"),e.keyword("plugins-page/needs-restart")),e.keyword("plugins-page/restart-server"),e.array_map(e.keyword("push"),K),e.keyword("plugins-page/activated-plugins"),e.array_map(e.keyword("calc"),e.keyword("plugins-page/activated-plugins")),e.keyword("plugins-page/activate-plugin"),e.array_map(e.keyword("push"),N),e.keyword("plugins-page/deactivate-plugin"),e.array_map(e.keyword("push"),O)),e.keyword("edges"),e.vector(e.vector(e.keyword("plugins-page/activate-plugin"),e.keyword("plugins-page/needs-restart")),e.vector(e.keyword("plugins-page/activate-plugin"),e.keyword("plugins-page/activated-plugins")),e.vector(e.keyword("plugins-page/deactivate-plugin"),e.keyword("plugins-page/needs-restart")),e.vector(e.keyword("plugins-page/deactivate-plugin"),e.keyword("plugins-page/activated-plugins")))),Y=e.array_map(e.keyword("nodes"),e.array_map(e.keyword("settings-page/change-input"),e.array_map(e.keyword("push"),E),e.keyword("settings-page/field-values"),e.array_map(e.keyword("calc"),u.field_values),e.keyword("settings-page/submit-error"),e.array_map(e.keyword("push"),R),e.keyword("settings-page/submit-start"),e.array_map(e.keyword("push"),P),e.keyword("settings-page/submitting"),e.array_map(e.keyword("calc"),e.keyword("settings-page/submitting")),e.keyword("settings-page/update-settings"),e.array_map(e.keyword("push"),E)),e.keyword("edges"),e.vector(e.vector(e.keyword("settings-page/change-input"),e.keyword("settings-page/field-values")),e.vector(e.keyword("settings-page/submit-error"),e.keyword("settings-page/submitting")),e.vector(e.keyword("settings-page/submit-start"),e.keyword("settings-page/submitting")),e.vector(e.keyword("settings-page/update-settings"),e.keyword("settings-page/field-values")))),J=e.array_map(e.keyword("nodes"),e.array_map(e.keyword("themes-page/activate-theme"),e.array_map(e.keyword("push"),F),e.keyword("themes-page/current-theme-name-setting"),e.array_map(e.keyword("calc"),e.keyword("themes-page/current-theme-name-setting"))),e.keyword("edges"),e.vector(e.vector(e.keyword("themes-page/activate-theme"),e.keyword("themes-page/current-theme-name-setting")))),W=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("content-types"));return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"w-full md:w-1/2 md:px-2 mb-4",e.keyword("data-test-id"),"dashboard-content-types"),e.vector(u.box,e.array_map(e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M5.005 10.19a1 1 0 0 1 1 1v.233l5.998 3.464L18 11.423v-.232a1 1 0 1 1 2 0V12a1 1 0 0 1-.5.866l-6.997 4.042a1 1 0 0 1-1 0l-6.998-4.042a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1ZM5 15.15a1 1 0 0 1 1 1v.232l5.997 3.464 5.998-3.464v-.232a1 1 0 1 1 2 0v.81a1 1 0 0 1-.5.865l-6.998 4.042a1 1 0 0 1-1 0L4.5 17.824a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1Z",e.keyword("clip-rule"),"evenodd")),e.vector(e.keyword("path"),e.array_map(e.keyword("d"),"M12.503 2.134a1 1 0 0 0-1 0L4.501 6.17A1 1 0 0 0 4.5 7.902l7.002 4.047a1 1 0 0 0 1 0l6.998-4.04a1 1 0 0 0 0-1.732l-6.997-4.042Z"))),"Content Types"),e.keyword("class"),"md:h-48",e.keyword("size"),e.keyword("half"),e.keyword("content"),e.vector(e.keyword("div"),(()=>{const c=u.pluralize.call(null,t,"types"),k=e.nth.call(null,c,0,null),f=e.nth.call(null,c,1,null);return e.vector(e.keyword("p"),e.array_map(e.keyword("class"),"mb-4"),"This site has ",e.vector(e.keyword("span"),e.array_map(e.keyword("class"),"font-semibold"),k)," ",f," of content:")})(),e.vector(u.content_item_counts,e.array_map(e.keyword("content-types"),t))))))},X=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("theme"));return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"w-full md:w-1/2 md:px-2 mb-4",e.keyword("data-test-id"),"dashboard-theme"),e.vector(u.box,e.array_map(e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z",e.keyword("clip-rule"),"evenodd")),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z",e.keyword("clip-rule"),"evenodd"))),"Theme"),e.keyword("class"),"md:h-48",e.keyword("size"),e.keyword("half"),e.keyword("content"),e.vector(e.keyword("div"),"This site is using the ",e.vector(e.keyword("span"),e.array_map(e.keyword("class"),"font-semibold underline"),e.keyword("label").call(null,t)),"."))))},ee=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("activated-plugins"));return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"w-full md:w-1/2 md:px-2 mb-4",e.keyword("data-test-id"),"dashboard-plugins"),e.vector(u.box,e.array_map(e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z",e.keyword("clip-rule"),"evenodd")),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z",e.keyword("clip-rule"),"evenodd"))),"Plugins"),e.keyword("class"),"md:h-48",e.keyword("size"),e.keyword("half"),e.keyword("content"),e.vector(e.keyword("div"),(()=>{const c=u.pluralize.call(null,t,"plugin"),k=e.nth.call(null,c,0,null),f=e.nth.call(null,c,1,null);return e.vector(e.keyword("p"),e.array_map(e.keyword("class"),"mb-4"),"This site has ",e.vector(e.keyword("span"),e.array_map(e.keyword("class"),"font-semibold"),k)," ",f," activated:")})(),e.vector(e.keyword("div"),function k(f){return new e.LazySeq(null,function(){let $=f;for(;;){const L=e.seq.call(null,$);if(e.truth_.call(null,L)){const H=L;if(e.truth_.call(null,e.chunked_seq_QMARK_.call(null,H))){const b=e.chunk_first.call(null,H),x=e.count.call(null,b),v=e.chunk_buffer.call(null,x);return(()=>{let Z=0;for(;;){if(Z<x){const p=e._nth.call(null,b,Z),g=e.nth.call(null,p,0,null),h=e.nth.call(null,p,1,null);e.chunk_append.call(null,v,e.vector(e.keyword("span"),e.truth_.call(null,e._EQ_.call(null,g,0))?null:e.vector(e.keyword("span"),e.array_map(e.keyword("class"),"text-gray-300")," \u2022 "),e.vector(e.keyword("a"),e.array_map(e.keyword("class"),"underline text-nowrap font-semibold",e.keyword("href"),"/admin/plugins"),e.keyword("label").call(null,h)))),Z=e.unchecked_inc.call(null,Z);continue}else return!0;break}})()?e.chunk_cons.call(null,e.chunk.call(null,v),k.call(null,e.chunk_rest.call(null,H))):e.chunk_cons.call(null,e.chunk.call(null,v),null)}else{const b=e.first.call(null,H),x=e.nth.call(null,b,0,null),v=e.nth.call(null,b,1,null);return e.cons.call(null,e.vector(e.keyword("span"),e.truth_.call(null,e._EQ_.call(null,x,0))?null:e.vector(e.keyword("span"),e.array_map(e.keyword("class"),"text-gray-300")," \u2022 "),e.vector(e.keyword("a"),e.array_map(e.keyword("class"),"underline text-nowrap font-semibold",e.keyword("href"),"/admin/plugins"),e.keyword("label").call(null,v))),k.call(null,e.rest.call(null,H)))}}break}},null,null)}.call(null,e.map_indexed.call(null,e.vector,e.sort_by.call(null,e.keyword("label"),t))))))))},re=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("settings"));return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"w-full md:w-1/2 md:px-2 mb-4",e.keyword("data-test-id"),"dashboard-settings"),e.vector(u.box,e.array_map(e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M9.586 2.586A2 2 0 0 1 11 2h2a2 2 0 0 1 2 2v.089l.473.196.063-.063a2.002 2.002 0 0 1 2.828 0l1.414 1.414a2 2 0 0 1 0 2.827l-.063.064.196.473H20a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-.089l-.196.473.063.063a2.002 2.002 0 0 1 0 2.828l-1.414 1.414a2 2 0 0 1-2.828 0l-.063-.063-.473.196V20a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2v-.089l-.473-.196-.063.063a2.002 2.002 0 0 1-2.828 0l-1.414-1.414a2 2 0 0 1 0-2.827l.063-.064L4.089 15H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h.09l.195-.473-.063-.063a2 2 0 0 1 0-2.828l1.414-1.414a2 2 0 0 1 2.827 0l.064.063L9 4.089V4a2 2 0 0 1 .586-1.414ZM8 12a4 4 0 1 1 8 0 4 4 0 0 1-8 0Z",e.keyword("clip-rule"),"evenodd"))),"Settings"),e.keyword("class"),"md:h-48",e.keyword("size"),e.keyword("half"),e.keyword("content"),e.vector(e.keyword("div"),e.vector(e.keyword("div"),e.vector(e.keyword("span"),e.array_map(e.keyword("class"),"font-semibold"),"Permalinks: "),e.vector(e.keyword("code"),e.get_in.call(null,t,e.vector(e.keyword("permalink-single"),e.keyword("value")))))))))},le=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("current-user"));return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"w-full md:w-1/2 md:px-2 mb-4",e.keyword("data-test-id"),"dashboard-user"),e.vector(u.box,e.array_map(e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M12 20a7.966 7.966 0 0 1-5.002-1.756l.002.001v-.683c0-1.794 1.492-3.25 3.333-3.25h3.334c1.84 0 3.333 1.456 3.333 3.25v.683A7.966 7.966 0 0 1 12 20ZM2 12C2 6.477 6.477 2 12 2s10 4.477 10 10c0 5.5-4.44 9.963-9.932 10h-.138C6.438 21.962 2 17.5 2 12Zm10-5c-1.84 0-3.333 1.455-3.333 3.25S10.159 13.5 12 13.5c1.84 0 3.333-1.455 3.333-3.25S13.841 7 12 7Z",e.keyword("clip-rule"),"evenodd"))),"User"),e.keyword("class"),"md:h-48",e.keyword("size"),e.keyword("half"),e.keyword("content"),e.vector(e.keyword("div"),"You're logged in as ",e.vector(e.keyword("a"),e.array_map(e.keyword("class"),"font-semibold underline",e.keyword("href"),"/admin/users"),e.keyword("username").call(null,t)),"."))))},ae=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("rpub-version")),c=e.str.call(null,"https://github.com/rpub-clj/rpub/tree/v",t);return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"w-full md:w-1/2 md:px-2 mb-4",e.keyword("data-test-id"),"dashboard-server"),e.vector(u.box,e.array_map(e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M5 5a2 2 0 0 0-2 2v3a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1V7a2 2 0 0 0-2-2H5Zm9 2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17ZM3 17v-3a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v3a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2Zm11-2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z",e.keyword("clip-rule"),"evenodd"))),"Server"),e.keyword("class"),"md:h-48",e.keyword("size"),e.keyword("half"),e.keyword("content"),e.vector(e.keyword("div"),"This server is running ",e.vector(e.keyword("a"),e.array_map(e.keyword("class"),"font-semibold underline",e.keyword("href"),c,e.keyword("target"),"_blank"),e.str.call(null,"rPub v",t)),"."))))},te=function(r){return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex flex-wrap py-4 px-4 md:px-2"),e.vector(W,r),e.vector(X,r),e.vector(ee,r),e.vector(re,r),e.vector(le,r),e.vector(ae,r))},oe=function(r){const a=r,l=e.__destructure_map.call(null,a),t=l,c=e.get.call(null,l,e.keyword("settings")),k=B.call(null,e.vector(e.keyword("settings-page/field-values"),e.keyword("settings-page/submitting"))),f=e.nth.call(null,k,0,null),$=e.__destructure_map.call(null,f),L=e.get.call(null,$,e.keyword("settings-page/field-values")),H=e.get.call(null,$,e.keyword("settings-page/submitting")),b=e.nth.call(null,k,1,null),x=u.index_by.call(null,e.keyword("key"),c),v=e.array_map(e.keyword("format"),e.keyword("transit")),Z=function(g,h){const M=h.target.value;return b.call(null,e.keyword("settings-page/change-input"),e.vector(g,M))},p=function(g){g.preventDefault(),b.call(null,e.keyword("settings-page/submit-start"));const h=function(_,d){return e.truth_.call(null,d)?b.call(null,e.keyword("settings-page/submit-error")):window.location.reload()},M=e.vals.call(null,e.update_vals.call(null,e.merge_with.call(null,function(_,d){return e.assoc.call(null,_,e.keyword("value"),d)},x,L),function(_){return e.select_keys.call(null,_,e.vector(e.keyword("key"),e.keyword("value")))})),A=e.merge.call(null,v,e.array_map(e.keyword("body"),e.array_map(e.keyword("settings"),M),e.keyword("on-complete"),h));return q.post.call(null,"/admin/api/update-settings",A)};return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"p-4"),e.vector(u.box,e.array_map(e.keyword("title"),"Settings",e.keyword("content"),e.vector(e.keyword("section"),e.array_map(e.keyword("class"),"bg-white dark:bg-gray-900"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"max-w-2xl"),e.vector(e.keyword("form"),e.array_map(e.keyword("on-submit"),p),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"grid gap-4 sm:grid-cols-2 sm:gap-6"),function h(M){return new e.LazySeq(null,function(){let A=M;for(;;){const _=e.seq.call(null,A);if(e.truth_.call(null,_)){const d=_;if(e.truth_.call(null,e.chunked_seq_QMARK_.call(null,d))){const n=e.chunk_first.call(null,d),o=e.count.call(null,n),w=e.chunk_buffer.call(null,o);return(()=>{let y=0;for(;;){if(y<o){const i=e._nth.call(null,n,y);e.chunk_append.call(null,w,e.vector(e.keyword("div"),e.array_map(e.keyword("key"),e.keyword("key").call(null,i),e.keyword("class"),"sm:col-span-2"),e.vector(e.keyword("label"),e.array_map(e.keyword("class"),"block mb-2 text-sm font-semibold text-gray-900 dark:text-white",e.keyword("for"),"name"),e.keyword("label").call(null,i)),e.vector(z.input2,e.array_map(e.keyword("type"),e.keyword("text"),e.keyword("name"),e.keyword("key").call(null,i),e.keyword("value"),(()=>{const s=e.get.call(null,L,e.keyword("key").call(null,i));return e.truth_.call(null,s)?s:e.keyword("value").call(null,i)})(),e.keyword("on-change"),function(s){return Z.call(null,e.keyword("key").call(null,i),s)})))),y=e.unchecked_inc.call(null,y);continue}else return!0;break}})()?e.chunk_cons.call(null,e.chunk.call(null,w),h.call(null,e.chunk_rest.call(null,d))):e.chunk_cons.call(null,e.chunk.call(null,w),null)}else{const n=e.first.call(null,d);return e.cons.call(null,e.vector(e.keyword("div"),e.array_map(e.keyword("key"),e.keyword("key").call(null,n),e.keyword("class"),"sm:col-span-2"),e.vector(e.keyword("label"),e.array_map(e.keyword("class"),"block mb-2 text-sm font-semibold text-gray-900 dark:text-white",e.keyword("for"),"name"),e.keyword("label").call(null,n)),e.vector(z.input2,e.array_map(e.keyword("type"),e.keyword("text"),e.keyword("name"),e.keyword("key").call(null,n),e.keyword("value"),(()=>{const o=e.get.call(null,L,e.keyword("key").call(null,n));return e.truth_.call(null,o)?o:e.keyword("value").call(null,n)})(),e.keyword("on-change"),function(o){return Z.call(null,e.keyword("key").call(null,n),o)}))),h.call(null,e.rest.call(null,d)))}}break}},null,null)}.call(null,e.sort_by.call(null,e.keyword("label"),e.vals.call(null,x))),e.vector(z.submit_button,e.array_map(e.keyword("ready-label"),"Save",e.keyword("submit-label"),"Saving...",e.keyword("submitting"),H)))))))))},ye=e.vector(e.array_map(e.keyword("name"),"Username",e.keyword("value"),function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("username"));return e.vector(e.keyword("a"),e.array_map(e.keyword("class"),"font-semibold"),t)})),ne=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("users"));return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"p-4"),e.vector(u.table,e.array_map(e.keyword("title"),"Users",e.keyword("columns"),ye,e.keyword("rows"),t)))},V=function(r){return e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z",e.keyword("clip-rule"),"evenodd")),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z",e.keyword("clip-rule"),"evenodd")))},ce=function(r){const a=r,l=e.__destructure_map.call(null,a),t=e.get.call(null,l,e.keyword("themes")),c=e.get.call(null,l,e.keyword("theme-name-setting")),k=B.call(null,e.vector(e.keyword("themes-page/current-theme-name-setting"))),f=e.nth.call(null,k,0,null),$=e.__destructure_map.call(null,f),L=e.get.call(null,$,e.keyword("themes-page/current-theme-name-setting")),H=e.nth.call(null,k,1,null),b=e.array_map(e.keyword("format"),e.keyword("transit")),x=e.keyword("value").call(null,(()=>{const p=L;return e.truth_.call(null,p)?p:c})()),v=function(p){return e._EQ_.call(null,e.keyword("label").call(null,p),x)},Z=function(p,g){g.preventDefault();const h=e.array_map(e.keyword("settings"),e.vector(e.array_map(e.keyword("key"),e.keyword("theme-name"),e.keyword("value"),e.keyword("label").call(null,p)))),M=function(_,d){if(e.truth_.call(null,_))return H.call(null,e.keyword("themes-page/activate-theme"),e.keyword("label").call(null,p))},A=e.merge.call(null,b,e.array_map(e.keyword("body"),h,e.keyword("on-complete"),M));return q.post.call(null,"/api/update-settings",A)};return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"p-4"),function g(h){return new e.LazySeq(null,function(){let M=h;for(;;){const A=e.seq.call(null,M);if(e.truth_.call(null,A)){const _=A;if(e.truth_.call(null,e.chunked_seq_QMARK_.call(null,_))){const d=e.chunk_first.call(null,_),n=e.count.call(null,d),o=e.chunk_buffer.call(null,n);return(()=>{let w=0;for(;;){if(w<n){const y=e._nth.call(null,d,w),i=v.call(null,y);e.chunk_append.call(null,o,e.vector(e.keyword("form"),e.array_map(e.keyword("method"),"post"),e.vector(u.box,e.array_map(e.keyword("key"),e.keyword("label").call(null,y),e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center",e.keyword("style"),e.array_map(e.keyword("margin-top"),"-1px")),e.vector(V),e.keyword("label").call(null,y),e.truth_.call(null,i)?e.vector(e.keyword("button"),e.array_map(e.keyword("class"),"font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-green-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow-inner ml-auto cursor-auto w-44",e.keyword("type"),"submit"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"inline-flex items-center mx-auto"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-6 h-6 text-white mr-2",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z",e.keyword("clip-rule"),"evenodd"))),"Active")):e.vector(e.keyword("button"),e.array_map(e.keyword("class"),"font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44",e.keyword("on-click"),function(s){return Z.call(null,y,s)},e.keyword("type"),"submit"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"inline-flex items-center mx-auto"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-6 h-6 text-white dark:text-white mr-2",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("d"),"M8 5v4.997a.31.31 0 0 1-.068.113c-.08.098-.213.207-.378.301-.947.543-1.713 1.54-2.191 2.488A6.237 6.237 0 0 0 4.82 14.4c-.1.48-.138 1.031.018 1.539C5.12 16.846 6.02 17 6.414 17H11v3a1 1 0 1 0 2 0v-3h4.586c.395 0 1.295-.154 1.575-1.061.156-.508.118-1.059.017-1.539a6.241 6.241 0 0 0-.541-1.5c-.479-.95-1.244-1.946-2.191-2.489a1.393 1.393 0 0 1-.378-.301.309.309 0 0 1-.068-.113V5h1a1 1 0 1 0 0-2H7a1 1 0 1 0 0 2h1Z"))),"Activate Theme"))),e.keyword("class"),"mb-4",e.keyword("content"),(()=>{const s=e.keyword("description").call(null,y);if(e.truth_.call(null,s)){const G=s;return e.vector(e.keyword("p"),G)}})())))),w=e.unchecked_inc.call(null,w);continue}else return!0;break}})()?e.chunk_cons.call(null,e.chunk.call(null,o),g.call(null,e.chunk_rest.call(null,_))):e.chunk_cons.call(null,e.chunk.call(null,o),null)}else{const d=e.first.call(null,_),n=v.call(null,d);return e.cons.call(null,e.vector(e.keyword("form"),e.array_map(e.keyword("method"),"post"),e.vector(u.box,e.array_map(e.keyword("key"),e.keyword("label").call(null,d),e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center",e.keyword("style"),e.array_map(e.keyword("margin-top"),"-1px")),e.vector(V),e.keyword("label").call(null,d),e.truth_.call(null,n)?e.vector(e.keyword("button"),e.array_map(e.keyword("class"),"font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-green-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow-inner ml-auto cursor-auto w-44",e.keyword("type"),"submit"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"inline-flex items-center mx-auto"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-6 h-6 text-white mr-2",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z",e.keyword("clip-rule"),"evenodd"))),"Active")):e.vector(e.keyword("button"),e.array_map(e.keyword("class"),"font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44",e.keyword("on-click"),function(o){return Z.call(null,d,o)},e.keyword("type"),"submit"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"inline-flex items-center mx-auto"),e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-6 h-6 text-white dark:text-white mr-2",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("d"),"M8 5v4.997a.31.31 0 0 1-.068.113c-.08.098-.213.207-.378.301-.947.543-1.713 1.54-2.191 2.488A6.237 6.237 0 0 0 4.82 14.4c-.1.48-.138 1.031.018 1.539C5.12 16.846 6.02 17 6.414 17H11v3a1 1 0 1 0 2 0v-3h4.586c.395 0 1.295-.154 1.575-1.061.156-.508.118-1.059.017-1.539a6.241 6.241 0 0 0-.541-1.5c-.479-.95-1.244-1.946-2.191-2.489a1.393 1.393 0 0 1-.378-.301.309.309 0 0 1-.068-.113V5h1a1 1 0 1 0 0-2H7a1 1 0 1 0 0 2h1Z"))),"Activate Theme"))),e.keyword("class"),"mb-4",e.keyword("content"),(()=>{const o=e.keyword("description").call(null,d);if(e.truth_.call(null,o)){const w=o;return e.vector(e.keyword("p"),w)}})()))),g.call(null,e.rest.call(null,_)))}}break}},null,null)}.call(null,t))},I=function(r){return e.vector(e.keyword("svg"),e.array_map(e.keyword("class"),"w-8 h-8 text-gray-500 dark:text-white mr-4",e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("width"),"24",e.keyword("height"),"24",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 24 24"),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z",e.keyword("clip-rule"),"evenodd")),e.vector(e.keyword("path"),e.array_map(e.keyword("fill-rule"),"evenodd",e.keyword("d"),"M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z",e.keyword("clip-rule"),"evenodd")))},j=function(r){const a=Q.split.call(null,e.keyword("key").call(null,r),/\//),l=e.nth.call(null,a,0,null),t=e.last.call(null,Q.split.call(null,l,/\./));return e.str.call(null,"https://github.com/rpub-clj/plugins/tree/main/plugins/",t)},de=function(r){const a=r,l=e.__destructure_map.call(null,a),t=l,c=e.get.call(null,l,e.keyword("current-plugins")),k=e.get.call(null,l,e.keyword("available-plugins")),f=B.call(null,e.vector(e.keyword("plugins-page/needs-restart"),e.keyword("plugins-page/activated-plugins"))),$=e.nth.call(null,f,0,null),L=e.__destructure_map.call(null,$),H=e.get.call(null,L,e.keyword("plugins-page/needs-restart")),b=e.get.call(null,L,e.keyword("plugins-page/activated-plugins")),x=e.nth.call(null,f,1,null),v=e.array_map(e.keyword("format"),e.keyword("transit")),Z=u.index_by.call(null,e.keyword("key"),c),p=function(d,n){const o=e.assoc.call(null,n,e.keyword("activated"),!0),w=e.array_map(e.keyword("plugin"),e.select_keys.call(null,o,e.vector(e.keyword("key")))),y=function(m,s){if(e.truth_.call(null,m))return x.call(null,e.keyword("plugins-page/activate-plugin"),e.keyword("key").call(null,o))},i=e.merge.call(null,v,e.array_map(e.keyword("body"),w,e.keyword("on-complete"),y));return q.post.call(null,"/admin/api/activate-plugin",i)},g=function(d,n){const o=e.array_map(e.keyword("plugin"),e.select_keys.call(null,n,e.vector(e.keyword("key")))),w=function(i,m){if(e.truth_.call(null,i))return x.call(null,e.keyword("plugins-page/deactivate-plugin"),e.keyword("key").call(null,n))},y=e.merge.call(null,v,e.array_map(e.keyword("body"),o,e.keyword("on-complete"),w));return q.post.call(null,"/admin/api/deactivate-plugin",y)},h=function(d){const n=function(w,y){return null},o=e.merge.call(null,v,e.array_map(e.keyword("on-complete"),n));return x.call(null,e.keyword("plugins-page/restart-server")),q.post.call(null,"/api/restart-server",o)},M=u.index_by.call(null,e.keyword("key"),k),A=e.into.call(null,e.array_map(),e.map.call(null,function(d){return e.vector(d,e.array_map(e.keyword("activated"),!0))},b)),_=e.merge_with.call(null,e.merge,Z,M,A);return e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"p-4"),e.vector(u.box,e.array_map(e.keyword("title"),"Plugins",e.keyword("class"),"mb-4",e.keyword("content"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex"),e.vector(e.keyword("p"),e.array_map(e.keyword("class"),"italic"),"Note: The server must be restarted after activating a plugin for the first time."),e.truth_.call(null,H)?e.vector(e.keyword("button"),e.array_map(e.keyword("class"),"font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44",e.keyword("on-click"),h),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"inline-flex items-center mx-auto"),"Restart")):null))),function n(o){return new e.LazySeq(null,function(){let w=o;for(;;){const y=e.seq.call(null,w);if(e.truth_.call(null,y)){const i=y;if(e.truth_.call(null,e.chunked_seq_QMARK_.call(null,i))){const m=e.chunk_first.call(null,i),s=e.count.call(null,m),G=e.chunk_buffer.call(null,s);return(()=>{let T=0;for(;;){if(T<s){const C=e._nth.call(null,m,T);e.chunk_append.call(null,G,e.vector(u.box,e.array_map(e.keyword("key"),e.keyword("key").call(null,C),e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center",e.keyword("style"),e.array_map(e.keyword("margin-top"),"-1px")),e.vector(I),e.vector(e.keyword("a"),e.array_map(e.keyword("class"),"underline",e.keyword("href"),j.call(null,C),e.keyword("target"),"_blank"),(()=>{const S=e.keyword("label").call(null,C);return e.truth_.call(null,S)?S:e.keyword("key").call(null,C)})()),e.truth_.call(null,e.keyword("activated").call(null,C))?e.vector(z.activated_button,e.array_map(e.keyword("on-click"),function(S){return g.call(null,S,C)})):e.vector(z.activate_button,e.array_map(e.keyword("label"),"Activate Plugin",e.keyword("on-click"),function(S){return p.call(null,S,C)}))),e.keyword("class"),"mb-4",e.keyword("content"),e.vector(e.keyword("div"),(()=>{const S=e.keyword("description").call(null,C);if(e.truth_.call(null,S)){const se=S;return e.vector(e.keyword("p"),se)}})())))),T=e.unchecked_inc.call(null,T);continue}else return!0;break}})()?e.chunk_cons.call(null,e.chunk.call(null,G),n.call(null,e.chunk_rest.call(null,i))):e.chunk_cons.call(null,e.chunk.call(null,G),null)}else{const m=e.first.call(null,i);return e.cons.call(null,e.vector(u.box,e.array_map(e.keyword("key"),e.keyword("key").call(null,m),e.keyword("title"),e.vector(e.keyword("div"),e.array_map(e.keyword("class"),"flex items-center",e.keyword("style"),e.array_map(e.keyword("margin-top"),"-1px")),e.vector(I),e.vector(e.keyword("a"),e.array_map(e.keyword("class"),"underline",e.keyword("href"),j.call(null,m),e.keyword("target"),"_blank"),(()=>{const s=e.keyword("label").call(null,m);return e.truth_.call(null,s)?s:e.keyword("key").call(null,m)})()),e.truth_.call(null,e.keyword("activated").call(null,m))?e.vector(z.activated_button,e.array_map(e.keyword("on-click"),function(s){return g.call(null,s,m)})):e.vector(z.activate_button,e.array_map(e.keyword("label"),"Activate Plugin",e.keyword("on-click"),function(s){return p.call(null,s,m)}))),e.keyword("class"),"mb-4",e.keyword("content"),e.vector(e.keyword("div"),(()=>{const s=e.keyword("description").call(null,m);if(e.truth_.call(null,s)){const G=s;return e.vector(e.keyword("p"),G)}})()))),n.call(null,e.rest.call(null,i)))}}break}},null,null)}.call(null,e.sort_by.call(null,function(n){return Q.lower_case.call(null,(()=>{const o=e.keyword("label").call(null,n);return e.truth_.call(null,o)?o:e.keyword("key").call(null,n)})())},e.vals.call(null,_))))},ue=function(r){return u.add_page.call(null,e.merge.call(null,r,e.array_map(e.keyword("page-id"),e.keyword("dashboard-page"),e.keyword("component"),te))),u.add_page.call(null,e.merge.call(null,r,e.array_map(e.keyword("page-id"),e.keyword("settings-page"),e.keyword("component"),oe,e.keyword("dag-config"),Y))),u.add_page.call(null,e.merge.call(null,r,e.array_map(e.keyword("page-id"),e.keyword("users-page"),e.keyword("component"),ne))),u.add_page.call(null,e.merge.call(null,r,e.array_map(e.keyword("page-id"),e.keyword("themes-page"),e.keyword("component"),ce,e.keyword("dag-config"),J))),u.add_page.call(null,e.merge.call(null,r,e.array_map(e.keyword("page-id"),e.keyword("plugins-page"),e.keyword("component"),de,e.keyword("dag-config"),D)))},ie=(()=>{const r=function(a){const l=e.array.call(null),t=e.alength.call(null,arguments);let c=0;for(;;){if(c<t){l.push(arguments[c]),c=c+1;continue}break}const k=0<e.alength.call(null,l)?new e.IndexedSeq(l.slice(0),0,null):null;return r.cljs$core$IFn$_invoke$arity$variadic(k)};return r.cljs$core$IFn$_invoke$arity$variadic=function(a){const l=a,c=e.__destructure_map.call(null,l);return ue.call(null,c),U.add_elements.call(null,c),U.add_pages.call(null,c)},r.cljs$lang$maxFixedArity=0,r.cljs$lang$applyTo=function(a){return this.cljs$core$IFn$_invoke$arity$variadic(e.seq.call(null,a))},r})();export{N as activate_plugin,F as activate_theme,ue as add_pages,E as change_input,W as dashboard_content_types,te as dashboard_page,ee as dashboard_plugins,ae as dashboard_server,re as dashboard_settings,X as dashboard_theme,le as dashboard_user,O as deactivate_plugin,I as plugin_icon,j as plugin_url,de as plugins_page,D as plugins_page_dag_config,K as restart_server,oe as settings_page,Y as settings_page_dag_config,ie as start_BANG_,R as submit_error,P as submit_start,V as theme_icon,ce as themes_page,J as themes_page_dag_config,ne as users_page};
