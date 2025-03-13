import { jsx } from "react/jsx-runtime";
import * as cherry_core from "cherry-cljs/cljs.core.js";
import * as react from "react";
import { useState } from "react";
import * as set from "cherry-cljs/lib/clojure.set.js";
import * as str from "cherry-cljs/lib/clojure.string.js";
var default_debounce_timeout_ms = 500;
var debounce = function(f, wait) {
  const timeout1 = cherry_core.atom.call(null, null);
  const f144 = function(var_args) {
    const args1452 = cherry_core.array.call(null);
    const len__22514__auto__3 = cherry_core.alength.call(null, arguments);
    let i1464 = 0;
    while (true) {
      if (i1464 < len__22514__auto__3) {
        args1452.push(arguments[i1464]);
        let G__5 = i1464 + 1;
        i1464 = G__5;
        continue;
      }
      ;
      break;
    }
    ;
    const argseq__22770__auto__6 = 0 < cherry_core.alength.call(null, args1452) ? new cherry_core.IndexedSeq(args1452.slice(0), 0, null) : null;
    return f144.cljs$core$IFn$_invoke$arity$variadic(argseq__22770__auto__6);
  };
  f144.cljs$core$IFn$_invoke$arity$variadic = function(args) {
    const temp__23033__auto__7 = cherry_core.deref.call(null, timeout1);
    if (cherry_core.truth_.call(null, temp__23033__auto__7)) {
      const v8 = temp__23033__auto__7;
      clearTimeout(v8);
    }
    ;
    return cherry_core.reset_BANG_.call(null, timeout1, setTimeout(function() {
      return cherry_core.apply.call(null, f, args);
    }, wait));
  };
  f144.cljs$lang$maxFixedArity = 0;
  f144.cljs$lang$applyTo = function(seq147) {
    const self__22560__auto__9 = this;
    return self__22560__auto__9.cljs$core$IFn$_invoke$arity$variadic(cherry_core.seq.call(null, seq147));
  };
  return f144;
};
if (cherry_core.truth_.call(null, globalThis.React)) {
} else {
  globalThis.React = react;
}
;
var c = function(s) {
  return str.replace.call(null, s, ".", " ");
};
var attrs__GT_map = function(el) {
  return cherry_core.into.call(null, cherry_core.array_map(), (() => {
    const iter__23116__auto__1 = function iter__148(s__149) {
      return new cherry_core.LazySeq(null, function() {
        let s__1492 = s__149;
        while (true) {
          const temp__23033__auto__3 = cherry_core.seq.call(null, s__1492);
          if (cherry_core.truth_.call(null, temp__23033__auto__3)) {
            const s__1494 = temp__23033__auto__3;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__1494))) {
              const c__23114__auto__5 = cherry_core.chunk_first.call(null, s__1494);
              const size__23115__auto__6 = cherry_core.count.call(null, c__23114__auto__5);
              const b__1517 = cherry_core.chunk_buffer.call(null, size__23115__auto__6);
              if ((() => {
                let i__1508 = 0;
                while (true) {
                  if (i__1508 < size__23115__auto__6) {
                    const attr9 = cherry_core._nth.call(null, c__23114__auto__5, i__1508);
                    cherry_core.chunk_append.call(null, b__1517, cherry_core.vector(cherry_core.keyword.call(null, attr9.name), (() => {
                      const G__15210 = attr9;
                      const G__15211 = G__15210 == null ? null : G__15210.value;
                      const G__15212 = G__15211 == null ? null : JSON.parse.call(null, G__15211);
                      if (G__15212 == null) {
                        return null;
                      } else {
                        return cherry_core.js__GT_clj.call(null, G__15212, cherry_core.keyword("keywordize-keys"), true);
                      }
                    })()));
                    let G__13 = cherry_core.unchecked_inc.call(null, i__1508);
                    i__1508 = G__13;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__1517), iter__148.call(null, cherry_core.chunk_rest.call(null, s__1494)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__1517), null);
              }
            } else {
              const attr14 = cherry_core.first.call(null, s__1494);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword.call(null, attr14.name), (() => {
                const G__15315 = attr14;
                const G__15316 = G__15315 == null ? null : G__15315.value;
                const G__15317 = G__15316 == null ? null : JSON.parse.call(null, G__15316);
                if (G__15317 == null) {
                  return null;
                } else {
                  return cherry_core.js__GT_clj.call(null, G__15317, cherry_core.keyword("keywordize-keys"), true);
                }
              })()), iter__148.call(null, cherry_core.rest.call(null, s__1494)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__1.call(null, el.attributes);
  })());
};
var add_element = function(tag, component) {
  if (cherry_core.truth_.call(null, customElements.get(cherry_core.name.call(null, tag)))) {
    return null;
  } else {
    const klass1 = function self() {
      return Reflect.construct(HTMLElement, [], self);
    };
    klass1.prototype = Object.create(HTMLElement.prototype);
    klass1.prototype.connectedCallback = function() {
      const this$2 = this;
      const render3 = function() {
        const props4 = attrs__GT_map.call(null, this$2);
        const Component5 = function(_PERCENT_1) {
          return component.call(null, _PERCENT_1.children);
        };
        return react.render.call(null, /* @__PURE__ */ jsx(Component5, { children: props4 }), this$2);
      };
      return render3.call(null);
    };
    return customElements.define(cherry_core.name.call(null, tag), klass1);
  }
};
var button = function(props) {
  const defaults1 = cherry_core.array_map(cherry_core.keyword("color"), cherry_core.keyword("blue"));
  const classes2 = cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "flex.items-center.justify-center.text-white.focus:ring-4.font-medium.rounded-lg.text-sm.px-4.py-2.focus:outline-none.shadow.transition-colors.duration-75"));
  const attrs_SINGLEQUOTE_3 = (() => {
    const $4 = defaults1;
    const $5 = cherry_core.merge.call(null, $4, props);
    const $6 = cherry_core.assoc.call(null, $5, cherry_core.keyword("class"), cherry_core.str.call(null, cherry_core.keyword("class").call(null, $5), " ", "bg-", cherry_core.name.call(null, cherry_core.keyword("color").call(null, $5)), "-700 ", "hover:bg-", cherry_core.name.call(null, cherry_core.keyword("color").call(null, $5)), "-800 ", "focus:ring-", cherry_core.name.call(null, cherry_core.keyword("color").call(null, $5)), "-300"));
    const $7 = cherry_core.merge_with.call(null, function(_PERCENT_1, _PERCENT_2) {
      return cherry_core.str.call(null, _PERCENT_1, " ", _PERCENT_2);
    }, $6, classes2);
    const $8 = cherry_core.select_keys.call(null, $7, cherry_core.vector(cherry_core.keyword("class"), cherry_core.keyword("on-click")));
    return set.rename_keys.call(null, $8, cherry_core.array_map(cherry_core.keyword("on-click"), cherry_core.keyword("onClick")));
  })();
  return cherry_core.vector(cherry_core.keyword("button"), attrs_SINGLEQUOTE_3, cherry_core.keyword("children").call(null, props));
};
var activated_button = function(p__154) {
  const map__1551 = p__154;
  const map__1552 = cherry_core.__destructure_map.call(null, map__1551);
  const on_click3 = cherry_core.get.call(null, map__1552, cherry_core.keyword("on-click"));
  const vec__1564 = useState.call(null, false);
  const hover5 = cherry_core.nth.call(null, vec__1564, 0, null);
  const set_hover6 = cherry_core.nth.call(null, vec__1564, 1, null);
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "ml-auto", cherry_core.keyword("onMouseEnter"), function() {
    return set_hover6.call(null, true);
  }, cherry_core.keyword("onMouseLeave"), function() {
    return set_hover6.call(null, false);
  }, cherry_core.keyword("onClick"), on_click3), cherry_core.truth_.call(null, hover5) ? cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("type"), "submit", cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-red-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow w-44"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-6 h-6 text-white dark:text-white mr-2", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm5.757-1a1 1 0 1 0 0 2h8.486a1 1 0 1 0 0-2H7.757Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Deactivate")) : cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("type"), "submit", cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-green-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow-inner w-44"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-6 h-6 text-white mr-2", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Active")));
};
var activate_button = function(p__159) {
  const map__1601 = p__159;
  const map__1602 = cherry_core.__destructure_map.call(null, map__1601);
  const on_click3 = cherry_core.get.call(null, map__1602, cherry_core.keyword("on-click"));
  const label4 = cherry_core.get.call(null, map__1602, cherry_core.keyword("label"));
  return cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44", cherry_core.keyword("onClick"), on_click3), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-6 h-6 text-white dark:text-white mr-2", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm11-4.243a1 1 0 1 0-2 0V11H7.757a1 1 0 1 0 0 2H11v3.243a1 1 0 1 0 2 0V13h3.243a1 1 0 1 0 0-2H13V7.757Z", cherry_core.keyword("clip-rule"), "evenodd"))), label4));
};
var input = function(props) {
  const map__1611 = props;
  const map__1612 = cherry_core.__destructure_map.call(null, map__1611);
  const input_name3 = cherry_core.get.call(null, map__1612, cherry_core.keyword("name"));
  const on_change4 = cherry_core.get.call(null, map__1612, cherry_core.keyword("on-change"));
  const placeholder5 = cherry_core.get.call(null, map__1612, cherry_core.keyword("placeholder"));
  const on_focus6 = cherry_core.get.call(null, map__1612, cherry_core.keyword("on-focus"));
  const default_value7 = cherry_core.get.call(null, map__1612, cherry_core.keyword("default-value"));
  const readonly8 = cherry_core.get.call(null, map__1612, cherry_core.keyword("readonly"));
  const on_blur9 = cherry_core.get.call(null, map__1612, cherry_core.keyword("on-blur"));
  const type10 = cherry_core.get.call(null, map__1612, cherry_core.keyword("type"));
  const size11 = cherry_core.get.call(null, map__1612, cherry_core.keyword("size"));
  const class$12 = cherry_core.get.call(null, map__1612, cherry_core.keyword("class"));
  const vec__16213 = useState.call(null, default_value7);
  const current_value14 = cherry_core.nth.call(null, vec__16213, 0, null);
  const set_current_value15 = cherry_core.nth.call(null, vec__16213, 1, null);
  return cherry_core.vector(cherry_core.keyword("input"), cherry_core.merge.call(null, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.name.call(null, type10), cherry_core.keyword("class"), cherry_core.str.call(null, "bg-gray-50 border border-gray-200 text-gray-900 rounded-lg\n                   focus:border-primary-600 focus:ring-0 focus:ring-offset-0 block w-full\n                   p-2 5 dark:bg-gray-700 dark:border-gray-600\n                   dark:placeholder-gray-400 dark:text-white\n                   dark:focus:ring-primary-500 dark:focus:border-primary-500 ", cherry_core.name.call(null, (() => {
    const or__23431__auto__16 = size11;
    if (cherry_core.truth_.call(null, or__23431__auto__16)) {
      return or__23431__auto__16;
    } else {
      return cherry_core.keyword("text-sm");
    }
  })()), " ", class$12), cherry_core.keyword("name"), cherry_core.name.call(null, input_name3), cherry_core.keyword("placeholder"), placeholder5, cherry_core.keyword("onFocus"), on_focus6, cherry_core.keyword("onBlur"), on_blur9, cherry_core.keyword("onChange"), function(e) {
    const value17 = e.target.value;
    set_current_value15.call(null, value17);
    return on_change4.call(null, e);
  }, cherry_core.keyword("value"), current_value14), cherry_core.truth_.call(null, readonly8) ? cherry_core.array_map(cherry_core.keyword("readonly"), readonly8) : null));
};
var input2 = function(props) {
  const map__1651 = props;
  const map__1652 = cherry_core.__destructure_map.call(null, map__1651);
  const input_name3 = cherry_core.get.call(null, map__1652, cherry_core.keyword("name"));
  const on_change4 = cherry_core.get.call(null, map__1652, cherry_core.keyword("on-change"));
  const key5 = cherry_core.get.call(null, map__1652, cherry_core.keyword("key"));
  const placeholder6 = cherry_core.get.call(null, map__1652, cherry_core.keyword("placeholder"));
  const value7 = cherry_core.get.call(null, map__1652, cherry_core.keyword("value"));
  const readonly8 = cherry_core.get.call(null, map__1652, cherry_core.keyword("readonly"));
  const type9 = cherry_core.get.call(null, map__1652, cherry_core.keyword("type"));
  const size10 = cherry_core.get.call(null, map__1652, cherry_core.keyword("size"));
  const class$11 = cherry_core.get.call(null, map__1652, cherry_core.keyword("class"));
  return cherry_core.vector(cherry_core.keyword("input"), cherry_core.merge.call(null, cherry_core.array_map(cherry_core.keyword("key"), key5, cherry_core.keyword("type"), cherry_core.name.call(null, type9), cherry_core.keyword("class"), cherry_core.str.call(null, "bg-gray-50 border border-gray-300 text-gray-900 rounded-lg\n                         focus:ring-primary-600 focus:border-primary-600 block w-full\n                         p-2 5 dark:bg-gray-700 dark:border-gray-600\n                         dark:placeholder-gray-400 dark:text-white\n                         focus:ring-0 focus:ring-offset-0\n                         dark:focus:ring-primary-500 dark:focus:border-primary-500 ", cherry_core.name.call(null, (() => {
    const or__23431__auto__12 = size10;
    if (cherry_core.truth_.call(null, or__23431__auto__12)) {
      return or__23431__auto__12;
    } else {
      return cherry_core.keyword("text-sm");
    }
  })()), " ", class$11), cherry_core.keyword("name"), cherry_core.name.call(null, input_name3), cherry_core.keyword("placeholder"), placeholder6, cherry_core.keyword("onChange"), on_change4, cherry_core.keyword("value"), value7), cherry_core.truth_.call(null, readonly8) ? cherry_core.array_map(cherry_core.keyword("readonly"), readonly8) : null));
};
var select = function(p__166) {
  const map__1671 = p__166;
  const map__1672 = cherry_core.__destructure_map.call(null, map__1671);
  const input_name3 = cherry_core.get.call(null, map__1672, cherry_core.keyword("name"));
  const type4 = cherry_core.get.call(null, map__1672, cherry_core.keyword("type"));
  const default_value5 = cherry_core.get.call(null, map__1672, cherry_core.keyword("default-value"));
  const placeholder6 = cherry_core.get.call(null, map__1672, cherry_core.keyword("placeholder"));
  const on_change7 = cherry_core.get.call(null, map__1672, cherry_core.keyword("on-change"));
  const on_focus8 = cherry_core.get.call(null, map__1672, cherry_core.keyword("on-focus"));
  const on_blur9 = cherry_core.get.call(null, map__1672, cherry_core.keyword("on-blur"));
  const children10 = cherry_core.get.call(null, map__1672, cherry_core.keyword("children"));
  const vec__16811 = useState.call(null, default_value5);
  const current_value12 = cherry_core.nth.call(null, vec__16811, 0, null);
  const set_current_value13 = cherry_core.nth.call(null, vec__16811, 1, null);
  return cherry_core.apply.call(null, React.createElement, "select", cherry_core.clj__GT_js.call(null, cherry_core.array_map(cherry_core.keyword("type"), type4, cherry_core.keyword("name"), input_name3, cherry_core.keyword("class"), cherry_core.str.call(null, "appearance-none px-2 py-1 border border-gray-200 ", "focus:ring-0 focus:ring-offset-0 ", "rounded-[6px] mr-4"), cherry_core.keyword("placeholder"), placeholder6, cherry_core.keyword("onFocus"), on_focus8, cherry_core.keyword("onBlur"), on_blur9, cherry_core.keyword("value"), current_value12)), children10);
};
var spinner = function(_) {
  return cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "inline w-4 h-4 me-3 text-white animate-spin", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("role"), "status", cherry_core.keyword("viewBox"), "0 0 100 101", cherry_core.keyword("fill"), "none", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z", cherry_core.keyword("fill"), "#E5E7EB")), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z", cherry_core.keyword("fill"), "currentColor")));
};
export {
  activate_button,
  activated_button,
  add_element,
  attrs__GT_map,
  button,
  c,
  debounce,
  default_debounce_timeout_ms,
  input,
  input2,
  select,
  spinner
};
