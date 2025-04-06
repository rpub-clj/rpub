import*as l from"cherry-cljs/cljs.core.js";import*as y from"react";import*as M from"cherry-cljs/lib/clojure.set.js";import*as T from"cherry-cljs/lib/clojure.string.js";var k=function(n,e){const r=l.truth_.call(null,l.keyword("class").call(null,n))?l.conj.call(null,e,l.keyword("class").call(null,n)):e;return l.assoc.call(null,n,l.keyword("class"),T.join.call(null," ",r))},w=function(n){const e=l.name.call(null,n),r=l.re_find.call(null,/^([^#.]+)/,e),t=l.nth.call(null,r,0,null),a=l.nth.call(null,r,1,null),c=l.re_find.call(null,/#([^.#]+)/,e),o=l.nth.call(null,c,0,null),u=l.nth.call(null,c,1,null),_=l.map.call(null,l.second,l.re_seq.call(null,/\.([^.#]+)/,e));return l.array_map(l.keyword("tag"),a,l.keyword("id"),u,l.keyword("classes"),_)},h=l.hash_map(l.keyword("on-drag-exit"),l.keyword("onDragExit"),l.keyword("on-mouse-enter"),l.keyword("onMouseEnter"),l.keyword("on-focus"),l.keyword("onFocus"),l.keyword("on-drop"),l.keyword("onDrop"),l.keyword("on-blur"),l.keyword("onBlur"),l.keyword("on-drag"),l.keyword("onDrag"),l.keyword("on-mouse-down"),l.keyword("onMouseDown"),l.keyword("on-click"),l.keyword("onClick"),l.keyword("on-drag-leave"),l.keyword("onDragLeave"),l.keyword("on-drag-start"),l.keyword("onDragStart"),l.keyword("on-drag-enter"),l.keyword("onDragEnter"),l.keyword("on-submit"),l.keyword("onSubmit"),l.keyword("on-mouse-leave"),l.keyword("onMouseLeave"),l.keyword("on-drag-over"),l.keyword("onDragOver"),l.keyword("on-change"),l.keyword("onChange"),l.keyword("on-drag-end"),l.keyword("onDragEnd")),i=function(n){const e=l.truth_.call(null,l.map_QMARK_.call(null,l.second.call(null,n)))?(()=>{const r=n,t=l.seq.call(null,r),a=l.first.call(null,t),c=l.next.call(null,t),o=a,u=l.first.call(null,c),_=l.next.call(null,c),d=u,G=_;return l.array_map(l.keyword("el"),o,l.keyword("attrs"),d,l.keyword("children"),G)})():(()=>{const r=n,t=l.seq.call(null,r),a=l.first.call(null,t),c=l.next.call(null,t),o=a,u=c;return l.array_map(l.keyword("el"),o,l.keyword("children"),u)})();if(l.truth_.call(null,l.not.call(null,l.keyword_QMARK_.call(null,l.keyword("el").call(null,e)))))return e;{const r=w.call(null,l.keyword("el").call(null,e)),t=l.__destructure_map.call(null,r),a=l.get.call(null,t,l.keyword("tag")),c=l.get.call(null,t,l.keyword("id")),o=l.get.call(null,t,l.keyword("classes")),_=l.update.call(null,l.assoc.call(null,e,l.keyword("el"),a),l.keyword("attrs"),M.rename_keys,h),d=l.truth_.call(null,c)?l.assoc_in.call(null,_,l.vector(l.keyword("attrs"),l.keyword("id")),c):_;return l.truth_.call(null,l.seq.call(null,o))?l.update.call(null,d,l.keyword("attrs"),k,o):d}},p=function(n){return l.reduce.call(null,function(e,r){const t=r,a=l.nth.call(null,t,0,null),c=l.nth.call(null,t,1,null);return l.assoc.call(null,e,l.keyword.call(null,a),c)},l.array_map(),Object.entries(n))},g=function(n){const e=l.dissoc.call(null,p.call(null,n),l.keyword("__el"));return s.call(null,n.__el.call(null,e))},v=function(n){return n},f=function(n){return l.reduce.call(null,function(e,r){const t=r,a=l.nth.call(null,t,0,null),c=l.nth.call(null,t,1,null);return e[l.name.call(null,a)]=c,e},l.js_obj.call(null),n)},m=function(n){const e=i.call(null,n),r=l.__destructure_map.call(null,e),t=l.get.call(null,r,l.keyword("el")),a=l.get.call(null,r,l.keyword("attrs")),c=l.get.call(null,r,l.keyword("children")),o=s.call(null,c),u=(()=>{const _=a;return _==null?null:s.call(null,_)})();return y.createElement.call(null,t,u,o)},E=function(n){const e=i.call(null,n),r=l.__destructure_map.call(null,e),t=l.get.call(null,r,l.keyword("el")),a=l.get.call(null,r,l.keyword("attrs")),c=l.get.call(null,r,l.keyword("children")),o=s.call(null,c),u=s.call(null,l.assoc.call(null,a,l.keyword("__el"),t));return y.createElement.call(null,g,u,o)},Q=function(n){return l.into_array.call(null,l.map.call(null,s,n))},s=function(n){return l.truth_.call(null,l.string_QMARK_.call(null,n))?v.call(null,n):l.truth_.call(null,(()=>{const e=l.map_QMARK_.call(null,n);return l.truth_.call(null,e)?l.not.call(null,l.record_QMARK_.call(null,n)):e})())?f.call(null,n):l.truth_.call(null,(()=>{const e=l.vector_QMARK_.call(null,n);return l.truth_.call(null,e)?l.keyword_QMARK_.call(null,l.first.call(null,n)):e})())?m.call(null,n):l.truth_.call(null,(()=>{const e=l.vector_QMARK_.call(null,n);return l.truth_.call(null,e)?l.fn_QMARK_.call(null,l.first.call(null,n)):e})())?E.call(null,n):l.truth_.call(null,l.sequential_QMARK_.call(null,n))?Q.call(null,n):l.truth_.call(null,l.keyword("else"))?n:null},q=function(n){return function(e){return s.call(null,l.vector(n,e))}};export{g as Adapter,p as __GT_clj_props,k as add_classes,s as as_element,h as attr_aliases,E as fn_vector__GT_el,m as keyword_vector__GT_el,f as map__GT_el,i as parse_element,w as parse_hiccup_tag,q as reactify_component,Q as sequential__GT_el,v as string__GT_el};
