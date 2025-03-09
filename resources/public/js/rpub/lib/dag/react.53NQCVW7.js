import*as l from"cherry-cljs/cljs.core.js";import*as h from"react";import{useCallback as w,createContext as g,useContext as y,useSyncExternalStore as A,useId as x}from"react";import*as d from"rpub.lib.dag";var p=g.call(null),G=function(n){return h.createElement.call(null,p.Provider,{value:l.array_map(l.keyword("dag-atom"),n["dag-atom"])},n.children)},m=function(n,a,e){return l.some.call(null,function(t){return l.get_in.call(null,n,l.vector(l.keyword("rpub.lib.dag/values"),t))!==l.get_in.call(null,a,l.vector(l.keyword("rpub.lib.dag/values"),t))},e)},b=function(n,a,e,t){return l.swap_BANG_.call(null,n,function(s){return l.reduce.call(null,function(r,u){if(l.truth_.call(null,l.not.call(null,l.vector_QMARK_.call(null,u))))return r;{const c=u,o=l.nth.call(null,c,0,null),i=l.nth.call(null,c,1,null),_=function(f){return l.get_in.call(null,r,l.vector(l.keyword("rpub.lib.dag/nodes"),o,l.keyword("calc"))).call(null,f,i)};return d.add_node.call(null,r,u,l.array_map(l.keyword("calc"),_),l.vector(l.vector(o,u)))}},s,e)}),l.add_watch.call(null,n,a,function(s,r,u,c){if(l.truth_.call(null,m.call(null,u,c,e)))return t.call(null)}),function(){return l.remove_watch.call(null,n,a),l.swap_BANG_.call(null,n,function(r){return l.reduce.call(null,function(u,c){return l.truth_.call(null,l.not.call(null,l.vector_QMARK_.call(null,c)))?u:d.remove_node.call(null,u,c)},r,e)})}},k=function(n){const a=y.call(null,p),e=l.__destructure_map.call(null,a),t=l.get.call(null,e,l.keyword("dag-atom")),s=w.call(null,function(o){const i=o,_=l.nth.call(null,i,0,null),f=l.nth.call(null,i,1,null);return l.swap_BANG_.call(null,t,function(v){return d.push.call(null,v,_,f)})},[]),r=x.call(null),u=A.call(null,function(o){return b.call(null,t,r,n,o)},function(){return l.deref.call(null,t)}),c=l.select_keys.call(null,l.keyword("rpub.lib.dag/values").call(null,u),n);return l.vector(c,s)};export{G as DAGProvider,b as subscribe,m as updated_QMARK_,k as use_dag};
