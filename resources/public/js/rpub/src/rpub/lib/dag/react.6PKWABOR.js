import*as l from"cherry-cljs/cljs.core.js";import*as y from"react";import{useCallback as I,useContext as j,useSyncExternalStore as x,useId as G}from"react";import*as s from"rpub.lib.dag";var h=y.createContext.call(null),F=function(n){return y.createElement.call(null,h.Provider,{value:l.array_map(l.keyword("dag-atom"),n["dag-atom"])},n.children)},b=function(n,r,e){return l.some.call(null,function(_){return l.get_in.call(null,n,l.vector(l.keyword("rpub.lib.dag/values"),_))!==l.get_in.call(null,r,l.vector(l.keyword("rpub.lib.dag/values"),_))},e)},w=function(n,r,e,_){return l.swap_BANG_.call(null,n,function(o){return l.reduce.call(null,function(c,a){if(l.truth_.call(null,l.not.call(null,l.vector_QMARK_.call(null,a))))return c;{const t=a,d=l.nth.call(null,t,0,null),m=l.nth.call(null,t,1,null),g=function(p){return l.get_in.call(null,c,l.vector(l.keyword("rpub.lib.dag/nodes"),d,l.keyword("calc"))).call(null,p,m)};return s.add_node.call(null,c,a,l.array_map(l.keyword("calc"),g),l.vector(l.vector(d,a)))}},o,e)}),l.add_watch.call(null,n,r,function(o,c,a,t){if(l.truth_.call(null,b.call(null,a,t,e)))return _.call(null)}),function(){return l.remove_watch.call(null,n,r),l.swap_BANG_.call(null,n,function(c){return l.reduce.call(null,function(a,t){return l.truth_.call(null,l.not.call(null,l.vector_QMARK_.call(null,t)))?a:s.remove_node.call(null,a,t)},c,e)})}},k=(()=>{const n=function(r){switch(l.alength.call(null,arguments)){case 0:return n.cljs$core$IFn$_invoke$arity$0();case 1:return n.cljs$core$IFn$_invoke$arity$1(arguments[0]);default:throw new Error(l.str.call(null,"Invalid arity: ",l.alength.call(null,arguments)))}};return n.cljs$core$IFn$_invoke$arity$0=function(){return k.call(null,null)},n.cljs$core$IFn$_invoke$arity$1=function(r){const e=j.call(null,h),_=l.__destructure_map.call(null,e),o=l.get.call(null,_,l.keyword("dag-atom")),c=G.call(null),a=function(u){return w.call(null,o,c,r,u)},t=function(){return l.deref.call(null,o)},d=x.call(null,a,t),m=l.truth_.call(null,s.assertions_enabled_QMARK_.call(null))?s.assert_valid_node_keys.call(null,d,r):null,g=l.select_keys.call(null,l.keyword("rpub.lib.dag/values").call(null,d),r),p=I.call(null,(()=>{const u=function($){const i=l.array.call(null),v=l.alength.call(null,arguments);let f=0;for(;;){if(f<v){i.push(arguments[f]),f=f+1;continue}break}const A=0<l.alength.call(null,i)?new l.IndexedSeq(i.slice(0),0,null):null;return u.cljs$core$IFn$_invoke$arity$variadic(A)};return u.cljs$core$IFn$_invoke$arity$variadic=function($){const i=l.swap_BANG_.call(null,o,function(v){return l.truth_.call(null,s.assertions_enabled_QMARK_.call(null))&&s.assert_valid_node_keys.call(null,v,l.take.call(null,1,$)),l.apply.call(null,s.push,v,$)});return l.select_keys.call(null,l.keyword("rpub.lib.dag/values").call(null,i),r)},u.cljs$lang$maxFixedArity=0,u.cljs$lang$applyTo=function($){return this.cljs$core$IFn$_invoke$arity$variadic(l.seq.call(null,$))},u})(),[]);return l.vector(g,p)},n.cljs$lang$maxFixedArity=1,n})();export{F as DAGProvider,w as subscribe,b as updated_QMARK_,k as use_dag};
