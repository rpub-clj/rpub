import*as l from"cherry-cljs/cljs.core.js";import*as y from"rpub.lib.http";var h=function(n){const t=typeof setTimeout<"u";return t&&l.boolean$.call(null,setTimeout(n,0))};if(!(typeof o<"u"))var o=null;var m=function(){if(o==null)return o=l.atom.call(null,l.hash_set())},G=function(n){return m.call(null),l.swap_BANG_.call(null,o,l.conj,n),null},v=function(n){return m.call(null),l.swap_BANG_.call(null,o,l.disj,n),null},k=function(n){return m.call(null),h.call(null,function(){let t=l.seq.call(null,l.deref.call(null,o)),u=null,_=0,c=0;for(;;){if(c<_){const f=l._nth.call(null,u,c);try{f.call(null,n)}catch{}let e=t,r=u,i=_,s=l.unchecked_inc.call(null,c);t=e,u=r,_=i,c=s;continue}else{const f=l.seq.call(null,t);if(l.truth_.call(null,f)){const e=f;if(l.truth_.call(null,l.chunked_seq_QMARK_.call(null,e))){const r=l.chunk_first.call(null,e);let i=l.chunk_rest.call(null,e),s=r,p=l.count.call(null,r),d=0;t=i,u=s,_=p,c=d;continue}else{const r=l.first.call(null,e);try{r.call(null,n)}catch{}let i=l.next.call(null,e),s=null,p=0,d=0;t=i,u=s,_=p,c=d;continue}}}break}})},a=function(n){const t=l.truth_.call(null,(()=>{const _=l.map_QMARK_.call(null,n);return l.truth_.call(null,_)?l.not.call(null,l.record_QMARK_.call(null,n)):_})())?l.update_vals.call(null,l.update_keys.call(null,n,a),a):l.truth_.call(null,l.set_QMARK_.call(null,n))?l.into.call(null,l.hash_set(),l.map.call(null,a),n):l.truth_.call(null,l.list_QMARK_.call(null,n))?l.into.call(null,l.list.call(null),l.map.call(null,a),n):l.truth_.call(null,l.vector_QMARK_.call(null,n))?l.into.call(null,l.vector(),l.map.call(null,a),n):l.truth_.call(null,l.sequential_QMARK_.call(null,n))?l.sequence.call(null,l.map.call(null,a),n):l.truth_.call(null,l.keyword("else"))?n:null,u=l.meta.call(null,n);if(l.truth_.call(null,u)){const _=u;return l.array_map(l.keyword("rpub.admin.tap/value"),t,l.keyword("rpub.admin.tap/meta"),_)}else return t},A=function(n){const t=a.call(null,n);return y.post.call(null,"/admin/tap",l.array_map(l.keyword("body"),t,l.keyword("format"),l.keyword("transit")))};export{h as _STAR_exec_tap_fn_STAR_,G as add_tap,m as maybe_init_tapset,a as reify_metadata,A as remote_tap,v as remove_tap,k as tap_GT_};
