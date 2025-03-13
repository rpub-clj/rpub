import * as cherry_core from "cherry-cljs/cljs.core.js";
import * as react from "react";
import { useCallback, useContext, useSyncExternalStore, useId } from "react";
import * as dag from "rpub.lib.dag";
var DAGContext = react.createContext.call(null);
var DAGProvider = function(props) {
  return react.createElement.call(null, DAGContext.Provider, { "value": cherry_core.array_map(cherry_core.keyword("dag-atom"), props["dag-atom"]) }, props.children);
};
var updated_QMARK_ = function(old_val, new_val, node_keys) {
  return cherry_core.some.call(null, function(_PERCENT_1) {
    return cherry_core.get_in.call(null, old_val, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/values"), _PERCENT_1)) !== cherry_core.get_in.call(null, new_val, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/values"), _PERCENT_1));
  }, node_keys);
};
var subscribe = function(dag_atom, component_id, node_keys, on_change) {
  cherry_core.swap_BANG_.call(null, dag_atom, function(current_dag) {
    return cherry_core.reduce.call(null, function(d, k) {
      if (cherry_core.truth_.call(null, cherry_core.not.call(null, cherry_core.vector_QMARK_.call(null, k)))) {
        return d;
      } else {
        const vec__2671 = k;
        const parent2 = cherry_core.nth.call(null, vec__2671, 0, null);
        const opts3 = cherry_core.nth.call(null, vec__2671, 1, null);
        const calc_fn4 = function(db) {
          const f5 = cherry_core.get_in.call(null, d, cherry_core.vector(cherry_core.keyword("rpub.lib.dag/nodes"), parent2, cherry_core.keyword("calc")));
          return f5.call(null, db, opts3);
        };
        return dag.add_node.call(null, d, k, cherry_core.array_map(cherry_core.keyword("calc"), calc_fn4), cherry_core.vector(cherry_core.vector(parent2, k)));
      }
    }, current_dag, node_keys);
  });
  cherry_core.add_watch.call(null, dag_atom, component_id, function(_, _6, old_val, new_val) {
    if (cherry_core.truth_.call(null, updated_QMARK_.call(null, old_val, new_val, node_keys))) {
      return on_change.call(null);
    }
  });
  return function unsubscribe() {
    cherry_core.remove_watch.call(null, dag_atom, component_id);
    return cherry_core.swap_BANG_.call(null, dag_atom, function(current_dag) {
      return cherry_core.reduce.call(null, function(d, k) {
        if (cherry_core.truth_.call(null, cherry_core.not.call(null, cherry_core.vector_QMARK_.call(null, k)))) {
          return d;
        } else {
          return dag.remove_node.call(null, d, k);
        }
      }, current_dag, node_keys);
    });
  };
};
var use_dag = function(node_keys) {
  const map__2701 = useContext.call(null, DAGContext);
  const map__2702 = cherry_core.__destructure_map.call(null, map__2701);
  const dag_atom3 = cherry_core.get.call(null, map__2702, cherry_core.keyword("dag-atom"));
  const component_id4 = useId.call(null);
  const sub5 = function(_PERCENT_1) {
    return subscribe.call(null, dag_atom3, component_id4, node_keys, _PERCENT_1);
  };
  const get_snapshot6 = function() {
    return cherry_core.deref.call(null, dag_atom3);
  };
  const dag7 = useSyncExternalStore.call(null, sub5, get_snapshot6);
  const values8 = cherry_core.select_keys.call(null, cherry_core.keyword("rpub.lib.dag/values").call(null, dag7), node_keys);
  const push9 = useCallback.call(null, (() => {
    const f271 = function(var_args) {
      const G__27410 = cherry_core.alength.call(null, arguments);
      switch (G__27410) {
        case 1:
          return f271.cljs$core$IFn$_invoke$arity$1(arguments[0]);
          break;
        case 2:
          return f271.cljs$core$IFn$_invoke$arity$2(arguments[0], arguments[1]);
          break;
        default:
          throw new Error(cherry_core.str.call(null, "Invalid arity: ", cherry_core.alength.call(null, arguments)));
      }
    };
    f271.cljs$core$IFn$_invoke$arity$1 = function(k) {
      cherry_core.swap_BANG_.call(null, dag_atom3, dag.push, k);
      return null;
    };
    f271.cljs$core$IFn$_invoke$arity$2 = function(k, v) {
      cherry_core.swap_BANG_.call(null, dag_atom3, dag.push, k, v);
      return null;
    };
    f271.cljs$lang$maxFixedArity = 2;
    return f271;
  })(), []);
  return cherry_core.vector(values8, push9);
};
export {
  DAGProvider,
  subscribe,
  updated_QMARK_,
  use_dag
};
