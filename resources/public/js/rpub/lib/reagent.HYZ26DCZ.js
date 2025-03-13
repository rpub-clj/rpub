import * as cherry_core from "cherry-cljs/cljs.core.js";
import * as react from "react";
import * as str from "cherry-cljs/lib/clojure.string.js";
var add_classes = function(attrs, classes) {
  const classes_SINGLEQUOTE_1 = cherry_core.truth_.call(null, cherry_core.keyword("class").call(null, attrs)) ? cherry_core.conj.call(null, classes, cherry_core.keyword("class").call(null, attrs)) : classes;
  return str.join.call(null, " ", classes_SINGLEQUOTE_1);
};
var parse_element = function(form) {
  const parsed1 = cherry_core.truth_.call(null, cherry_core.map_QMARK_.call(null, cherry_core.second.call(null, form))) ? (() => {
    const vec__1742 = form;
    const seq__1753 = cherry_core.seq.call(null, vec__1742);
    const first__1764 = cherry_core.first.call(null, seq__1753);
    const seq__1755 = cherry_core.next.call(null, seq__1753);
    const el6 = first__1764;
    const first__1767 = cherry_core.first.call(null, seq__1755);
    const seq__1758 = cherry_core.next.call(null, seq__1755);
    const attrs9 = first__1767;
    const children10 = seq__1758;
    return cherry_core.array_map(cherry_core.keyword("el"), el6, cherry_core.keyword("attrs"), attrs9, cherry_core.keyword("children"), children10);
  })() : (() => {
    const vec__17711 = form;
    const seq__17812 = cherry_core.seq.call(null, vec__17711);
    const first__17913 = cherry_core.first.call(null, seq__17812);
    const seq__17814 = cherry_core.next.call(null, seq__17812);
    const el15 = first__17913;
    const children16 = seq__17814;
    return cherry_core.array_map(cherry_core.keyword("el"), el15, cherry_core.keyword("children"), children16);
  })();
  if (cherry_core.truth_.call(null, cherry_core.not.call(null, cherry_core.keyword_QMARK_.call(null, cherry_core.keyword("el").call(null, parsed1))))) {
    return parsed1;
  } else {
    const vec__18017 = str.split.call(null, cherry_core.name.call(null, cherry_core.keyword("el").call(null, parsed1)), /\./);
    const seq__18118 = cherry_core.seq.call(null, vec__18017);
    const first__18219 = cherry_core.first.call(null, seq__18118);
    const seq__18120 = cherry_core.next.call(null, seq__18118);
    const el_SINGLEQUOTE_21 = first__18219;
    const classes22 = seq__18120;
    const G__18323 = cherry_core.assoc.call(null, parsed1, cherry_core.keyword("el"), el_SINGLEQUOTE_21);
    if (cherry_core.truth_.call(null, cherry_core.seq.call(null, classes22))) {
      return cherry_core.update_in.call(null, G__18323, cherry_core.vector(cherry_core.keyword("attrs"), cherry_core.keyword("class")), add_classes, classes22);
    } else {
      return G__18323;
    }
  }
};
var __GT_clj_props = function(js_props) {
  return cherry_core.reduce.call(null, function(m, p__184) {
    const vec__1851 = p__184;
    const k2 = cherry_core.nth.call(null, vec__1851, 0, null);
    const v3 = cherry_core.nth.call(null, vec__1851, 1, null);
    return cherry_core.assoc.call(null, m, cherry_core.keyword.call(null, k2), v3);
  }, cherry_core.array_map(), Object.entries(js_props));
};
var Adapter = function(js_props) {
  const clj_props1 = cherry_core.dissoc.call(null, __GT_clj_props.call(null, js_props), cherry_core.keyword("__el"));
  return as_element.call(null, js_props.__el.call(null, clj_props1));
};
var string__GT_el = function(s) {
  return s;
};
var map__GT_el = function(m) {
  return cherry_core.reduce.call(null, function(acc, p__188) {
    const vec__1891 = p__188;
    const k2 = cherry_core.nth.call(null, vec__1891, 0, null);
    const v3 = cherry_core.nth.call(null, vec__1891, 1, null);
    acc[cherry_core.name.call(null, k2)] = v3;
    return acc;
  }, cherry_core.js_obj.call(null), m);
};
var keyword_vector__GT_el = function(form) {
  const map__1921 = parse_element.call(null, form);
  const map__1922 = cherry_core.__destructure_map.call(null, map__1921);
  const el3 = cherry_core.get.call(null, map__1922, cherry_core.keyword("el"));
  const attrs4 = cherry_core.get.call(null, map__1922, cherry_core.keyword("attrs"));
  const children5 = cherry_core.get.call(null, map__1922, cherry_core.keyword("children"));
  const children_SINGLEQUOTE_6 = as_element.call(null, children5);
  const attrs_SINGLEQUOTE_7 = (() => {
    const G__1938 = attrs4;
    if (G__1938 == null) {
      return null;
    } else {
      return as_element.call(null, G__1938);
    }
  })();
  return react.createElement.call(null, el3, attrs_SINGLEQUOTE_7, children_SINGLEQUOTE_6);
};
var fn_vector__GT_el = function(form) {
  const map__1941 = parse_element.call(null, form);
  const map__1942 = cherry_core.__destructure_map.call(null, map__1941);
  const el3 = cherry_core.get.call(null, map__1942, cherry_core.keyword("el"));
  const attrs4 = cherry_core.get.call(null, map__1942, cherry_core.keyword("attrs"));
  const children5 = cherry_core.get.call(null, map__1942, cherry_core.keyword("children"));
  const children_SINGLEQUOTE_6 = as_element.call(null, children5);
  const attrs_SINGLEQUOTE_7 = as_element.call(null, cherry_core.assoc.call(null, attrs4, cherry_core.keyword("__el"), el3));
  return react.createElement.call(null, Adapter, attrs_SINGLEQUOTE_7, children_SINGLEQUOTE_6);
};
var sequential__GT_el = function(xs) {
  return cherry_core.into_array.call(null, cherry_core.map.call(null, as_element, xs));
};
var as_element = function(form) {
  if (cherry_core.truth_.call(null, cherry_core.string_QMARK_.call(null, form))) {
    return string__GT_el.call(null, form);
  } else {
    if (cherry_core.truth_.call(null, (() => {
      const and__23449__auto__1 = cherry_core.map_QMARK_.call(null, form);
      if (cherry_core.truth_.call(null, and__23449__auto__1)) {
        return cherry_core.not.call(null, cherry_core.record_QMARK_.call(null, form));
      } else {
        return and__23449__auto__1;
      }
    })())) {
      return map__GT_el.call(null, form);
    } else {
      if (cherry_core.truth_.call(null, (() => {
        const and__23449__auto__2 = cherry_core.vector_QMARK_.call(null, form);
        if (cherry_core.truth_.call(null, and__23449__auto__2)) {
          return cherry_core.keyword_QMARK_.call(null, cherry_core.first.call(null, form));
        } else {
          return and__23449__auto__2;
        }
      })())) {
        return keyword_vector__GT_el.call(null, form);
      } else {
        if (cherry_core.truth_.call(null, (() => {
          const and__23449__auto__3 = cherry_core.vector_QMARK_.call(null, form);
          if (cherry_core.truth_.call(null, and__23449__auto__3)) {
            return cherry_core.fn_QMARK_.call(null, cherry_core.first.call(null, form));
          } else {
            return and__23449__auto__3;
          }
        })())) {
          return fn_vector__GT_el.call(null, form);
        } else {
          if (cherry_core.truth_.call(null, cherry_core.sequential_QMARK_.call(null, form))) {
            return sequential__GT_el.call(null, form);
          } else {
            if (cherry_core.truth_.call(null, cherry_core.keyword("else"))) {
              return form;
            } else {
              return null;
            }
          }
        }
      }
    }
  }
};
var reactify_component = function(reagent_component) {
  return function(props) {
    return as_element.call(null, cherry_core.vector(reagent_component, props));
  };
};
export {
  Adapter,
  __GT_clj_props,
  add_classes,
  as_element,
  fn_vector__GT_el,
  keyword_vector__GT_el,
  map__GT_el,
  parse_element,
  reactify_component,
  sequential__GT_el,
  string__GT_el
};
