import * as cherry_core from "cherry-cljs/cljs.core.js";
import * as dep from "rads.dependency";
var dependents = function(p__127) {
  const map__1281 = p__127;
  const map__1282 = cherry_core.__destructure_map.call(null, map__1281);
  const nodes3 = cherry_core.get.call(null, map__1282, cherry_core.keyword("rpub.lib.dag/nodes"));
  const edges4 = cherry_core.get.call(null, map__1282, cherry_core.keyword("rpub.lib.dag/edges"));
  const sorted5 = dep.topo_sort.call(null, edges4);
  return cherry_core.into.call(null, cherry_core.array_map(), cherry_core.map.call(null, function(node_key) {
    return cherry_core.vector(node_key, cherry_core.filter.call(null, dep.transitive_dependents.call(null, edges4, node_key), sorted5));
  }, cherry_core.filter.call(null, function(_PERCENT_1) {
    return cherry_core.get_in.call(null, nodes3, cherry_core.vector(_PERCENT_1, cherry_core.keyword("push")));
  }, dep.nodes.call(null, edges4))));
};
var add_edges = function(graph, edges) {
  return cherry_core.reduce.call(null, function(g, p__129) {
    const vec__1301 = p__129;
    const from2 = cherry_core.nth.call(null, vec__1301, 0, null);
    const to3 = cherry_core.nth.call(null, vec__1301, 1, null);
    return dep.depend.call(null, g, to3, from2);
  }, graph, edges);
};
var __GT_dag = function(p__133) {
  const map__1341 = p__133;
  const map__1342 = cherry_core.__destructure_map.call(null, map__1341);
  const nodes3 = cherry_core.get.call(null, map__1342, cherry_core.keyword("nodes"));
  const edges4 = cherry_core.get.call(null, map__1342, cherry_core.keyword("edges"));
  const edges_SINGLEQUOTE_5 = add_edges.call(null, dep.graph.call(null), edges4);
  return cherry_core.array_map(cherry_core.keyword("rpub.lib.dag/nodes"), nodes3, cherry_core.keyword("rpub.lib.dag/edges"), edges_SINGLEQUOTE_5, cherry_core.keyword("rpub.lib.dag/values"), cherry_core.array_map(), cherry_core.keyword("rpub.lib.dag/dependents"), dependents.call(null, cherry_core.array_map(cherry_core.keyword("rpub.lib.dag/nodes"), nodes3, cherry_core.keyword("rpub.lib.dag/edges"), edges_SINGLEQUOTE_5)));
};
var recalculate = function(dag, node_key) {
  const calc_fn1 = cherry_core.get_in.call(null, dag, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/nodes"), node_key, cherry_core.keyword("calc")));
  const calc_input2 = cherry_core.assoc.call(null, cherry_core.keyword("rpub.lib.dag/acc").call(null, dag), cherry_core.keyword("rpub.lib.dag/values"), cherry_core.keyword("rpub.lib.dag/values").call(null, dag));
  const new_val3 = calc_fn1.call(null, calc_input2);
  return cherry_core.assoc_in.call(null, dag, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/values"), node_key), new_val3);
};
var push = (() => {
  const f135 = function(var_args) {
    const G__1381 = cherry_core.alength.call(null, arguments);
    switch (G__1381) {
      case 2:
        return f135.cljs$core$IFn$_invoke$arity$2(arguments[0], arguments[1]);
        break;
      case 3:
        return f135.cljs$core$IFn$_invoke$arity$3(arguments[0], arguments[1], arguments[2]);
        break;
      default:
        throw new Error(cherry_core.str.call(null, "Invalid arity: ", cherry_core.alength.call(null, arguments)));
    }
  };
  f135.cljs$core$IFn$_invoke$arity$2 = function(dag, node_key) {
    return push.call(null, dag, node_key, cherry_core.keyword("rpub.lib.dag/no-value"));
  };
  f135.cljs$core$IFn$_invoke$arity$3 = function(dag, node_key, v) {
    const push_fn3 = cherry_core.get_in.call(null, dag, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/nodes"), node_key, cherry_core.keyword("push")));
    const dependents4 = cherry_core.get_in.call(null, dag, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/dependents"), node_key));
    const dag_SINGLEQUOTE_5 = cherry_core.truth_.call(null, cherry_core._EQ_.call(null, v, cherry_core.keyword("rpub.lib.dag/no-value"))) ? cherry_core.update.call(null, dag, cherry_core.keyword("rpub.lib.dag/acc"), push_fn3) : cherry_core.update.call(null, dag, cherry_core.keyword("rpub.lib.dag/acc"), push_fn3, v);
    return cherry_core.reduce.call(null, recalculate, dag_SINGLEQUOTE_5, dependents4);
  };
  f135.cljs$lang$maxFixedArity = 3;
  return f135;
})();
var add_node = function(dag, node_key, node_config, edges) {
  const edges_SINGLEQUOTE_1 = add_edges.call(null, cherry_core.keyword("rpub.lib.dag/edges").call(null, dag), edges);
  const dag_SINGLEQUOTE_2 = cherry_core.assoc.call(null, cherry_core.assoc_in.call(null, dag, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/nodes"), node_key), node_config), cherry_core.keyword("rpub.lib.dag/edges"), edges_SINGLEQUOTE_1);
  return recalculate.call(null, cherry_core.assoc.call(null, dag_SINGLEQUOTE_2, cherry_core.keyword("rpub.lib.dag/dependents"), dependents.call(null, dag_SINGLEQUOTE_2)), node_key);
};
var remove_node = function(dag, node_key) {
  const dag_SINGLEQUOTE_1 = cherry_core.update.call(null, cherry_core.update.call(null, dag, cherry_core.keyword("rpub.lib.dag/nodes"), cherry_core.dissoc, node_key), cherry_core.keyword("rpub.lib.dag/edges"), dep.remove_all, node_key);
  return cherry_core.assoc.call(null, dag_SINGLEQUOTE_1, cherry_core.keyword("rpub.lib.dag/dependents"), dependents.call(null, dag_SINGLEQUOTE_1));
};
var add_tracing = function(dag_config) {
  const wrap_fn1 = function(f) {
    const f139 = function(var_args) {
      const args1403 = cherry_core.array.call(null);
      const len__22514__auto__4 = cherry_core.alength.call(null, arguments);
      let i1415 = 0;
      while (true) {
        if (i1415 < len__22514__auto__4) {
          args1403.push(arguments[i1415]);
          let G__6 = i1415 + 1;
          i1415 = G__6;
          continue;
        }
        ;
        break;
      }
      ;
      const argseq__22770__auto__7 = 0 < cherry_core.alength.call(null, args1403) ? new cherry_core.IndexedSeq(args1403.slice(0), 0, null) : null;
      return f139.cljs$core$IFn$_invoke$arity$variadic(argseq__22770__auto__7);
    };
    f139.cljs$core$IFn$_invoke$arity$variadic = function(args) {
      const ret8 = cherry_core.apply.call(null, f, args);
      cherry_core.prn.call(null, f, cherry_core.array_map(cherry_core.keyword("args"), args, cherry_core.keyword("ret"), ret8));
      return ret8;
    };
    f139.cljs$lang$maxFixedArity = 0;
    f139.cljs$lang$applyTo = function(seq142) {
      const self__22560__auto__9 = this;
      return self__22560__auto__9.cljs$core$IFn$_invoke$arity$variadic(cherry_core.seq.call(null, seq142));
    };
    return f139;
  };
  const wrap_node2 = function(v) {
    const G__14310 = v;
    const G__14311 = cherry_core.truth_.call(null, cherry_core.keyword("calc").call(null, v)) ? cherry_core.update.call(null, G__14310, cherry_core.keyword("calc"), wrap_fn1) : G__14310;
    if (cherry_core.truth_.call(null, cherry_core.keyword("push").call(null, v))) {
      return cherry_core.update.call(null, G__14311, cherry_core.keyword("push"), wrap_fn1);
    } else {
      return G__14311;
    }
  };
  return cherry_core.update.call(null, dag_config, cherry_core.keyword("rpub.lib.dag/nodes"), cherry_core.update_vals, wrap_node2);
};
export {
  __GT_dag,
  add_edges,
  add_node,
  add_tracing,
  dependents,
  push,
  recalculate,
  remove_node
};
