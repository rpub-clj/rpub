import { jsx } from "react/jsx-runtime";
import * as cherry_core from "cherry-cljs/cljs.core.js";
import { useId } from "react";
import * as str from "cherry-cljs/lib/clojure.string.js";
import * as inflections from "rads.inflections";
import * as dag from "rpub.admin.dag";
import { DAGProvider } from "rpub.lib.dag.react";
import * as r from "rpub.lib.reagent";
var c = function(s) {
  return str.replace.call(null, s, ".", " ");
};
var table = function(p__81) {
  const map__821 = p__81;
  const map__822 = cherry_core.__destructure_map.call(null, map__821);
  const title3 = cherry_core.get.call(null, map__822, cherry_core.keyword("title"));
  const rows4 = cherry_core.get.call(null, map__822, cherry_core.keyword("rows"));
  const header_buttons5 = cherry_core.get.call(null, map__822, cherry_core.keyword("header-buttons"));
  const columns6 = cherry_core.get.call(null, map__822, cherry_core.keyword("columns"));
  const delete_row7 = cherry_core.get.call(null, map__822, cherry_core.keyword("delete-row"));
  const table_id8 = useId.call(null);
  return cherry_core.vector(cherry_core.keyword("section"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "bg-gray-50.dark:bg-gray-900.antialiased")), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "bg-white.dark:bg-gray-800.relative.shadow.sm:rounded-lg.overflow-hidden")), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "flex.flex-col.md:flex-row.items-center.justify-between.space-y-3.md:space-y-0.md:space-x-4.p-4")), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "w-full.md:w-1/2")), cherry_core.vector(cherry_core.keyword("h2"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "text-3xl.font-semibold.font-app-serif")), title3)), cherry_core.truth_.call(null, header_buttons5) ? cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "w-full md:w-auto flex flex-col md:flex-row space-y-2 md:space-y-0 items-stretch md:items-center justify-end md:space-x-3 flex-shrink-0"), cherry_core.vector(header_buttons5)) : null), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "overflow-x-auto")), cherry_core.vector(cherry_core.keyword("table"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "w-full.text-sm.text-left.text-gray-500.dark:text-gray-400")), cherry_core.vector(cherry_core.keyword("thead"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "text-xs.text-gray-700.uppercase.bg-gray-50.dark:bg-gray-700.dark:text-gray-400")), cherry_core.vector(cherry_core.keyword("tr"), (() => {
    const iter__23116__auto__9 = function iter__83(s__84) {
      return new cherry_core.LazySeq(null, function() {
        let s__8410 = s__84;
        while (true) {
          const temp__23033__auto__11 = cherry_core.seq.call(null, s__8410);
          if (cherry_core.truth_.call(null, temp__23033__auto__11)) {
            const s__8412 = temp__23033__auto__11;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__8412))) {
              const c__23114__auto__13 = cherry_core.chunk_first.call(null, s__8412);
              const size__23115__auto__14 = cherry_core.count.call(null, c__23114__auto__13);
              const b__8615 = cherry_core.chunk_buffer.call(null, size__23115__auto__14);
              if ((() => {
                let i__8516 = 0;
                while (true) {
                  if (i__8516 < size__23115__auto__14) {
                    const column17 = cherry_core._nth.call(null, c__23114__auto__13, i__8516);
                    cherry_core.chunk_append.call(null, b__8615, cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-4"), cherry_core.keyword("scope"), "col", cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column17)), cherry_core.keyword("name").call(null, column17)));
                    let G__18 = cherry_core.unchecked_inc.call(null, i__8516);
                    i__8516 = G__18;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__8615), iter__83.call(null, cherry_core.chunk_rest.call(null, s__8412)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__8615), null);
              }
            } else {
              const column19 = cherry_core.first.call(null, s__8412);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-4"), cherry_core.keyword("scope"), "col", cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column19)), cherry_core.keyword("name").call(null, column19)), iter__83.call(null, cherry_core.rest.call(null, s__8412)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__9.call(null, columns6);
  })(), cherry_core.truth_.call(null, delete_row7) ? cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3"), cherry_core.keyword("scope"), "col"), cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "sr-only")), "Actions")) : null)), cherry_core.vector(cherry_core.keyword("tbody"), (() => {
    const iter__23116__auto__20 = function iter__87(s__88) {
      return new cherry_core.LazySeq(null, function() {
        let s__8821 = s__88;
        while (true) {
          const temp__23033__auto__22 = cherry_core.seq.call(null, s__8821);
          if (cherry_core.truth_.call(null, temp__23033__auto__22)) {
            const s__8823 = temp__23033__auto__22;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__8823))) {
              const c__23114__auto__24 = cherry_core.chunk_first.call(null, s__8823);
              const size__23115__auto__25 = cherry_core.count.call(null, c__23114__auto__24);
              const b__9026 = cherry_core.chunk_buffer.call(null, size__23115__auto__25);
              if ((() => {
                let i__8927 = 0;
                while (true) {
                  if (i__8927 < size__23115__auto__25) {
                    const row28 = cherry_core._nth.call(null, c__23114__auto__24, i__8927);
                    cherry_core.chunk_append.call(null, b__9026, cherry_core.vector(cherry_core.keyword("tr"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "border-b.dark:border-gray-700"), cherry_core.keyword("key"), cherry_core.keyword("id").call(null, row28)), (() => {
                      const iter__23116__auto__29 = function iter__91(s__92) {
                        return new cherry_core.LazySeq(null, function() {
                          let s__9230 = s__92;
                          while (true) {
                            const temp__23033__auto__31 = cherry_core.seq.call(null, s__9230);
                            if (cherry_core.truth_.call(null, temp__23033__auto__31)) {
                              const s__9232 = temp__23033__auto__31;
                              if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__9232))) {
                                const c__23114__auto__33 = cherry_core.chunk_first.call(null, s__9232);
                                const size__23115__auto__34 = cherry_core.count.call(null, c__23114__auto__33);
                                const b__9435 = cherry_core.chunk_buffer.call(null, size__23115__auto__34);
                                if ((() => {
                                  let i__9336 = 0;
                                  while (true) {
                                    if (i__9336 < size__23115__auto__34) {
                                      const vec__9537 = cherry_core._nth.call(null, c__23114__auto__33, i__9336);
                                      const i38 = cherry_core.nth.call(null, vec__9537, 0, null);
                                      const column39 = cherry_core.nth.call(null, vec__9537, 1, null);
                                      cherry_core.chunk_append.call(null, b__9435, cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i38, 0)) ? cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3.font-medium.text-gray-900.whitespace-nowrap.dark:text-white"), cherry_core.keyword("scope"), "row", cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column39)), cherry_core.keyword("value").call(null, column39).call(null, row28)) : cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3"), cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column39)), cherry_core.keyword("value").call(null, column39).call(null, row28)));
                                      let G__40 = cherry_core.unchecked_inc.call(null, i__9336);
                                      i__9336 = G__40;
                                      continue;
                                    } else {
                                      return true;
                                    }
                                    ;
                                    break;
                                  }
                                })()) {
                                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__9435), iter__91.call(null, cherry_core.chunk_rest.call(null, s__9232)));
                                } else {
                                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__9435), null);
                                }
                              } else {
                                const vec__9841 = cherry_core.first.call(null, s__9232);
                                const i42 = cherry_core.nth.call(null, vec__9841, 0, null);
                                const column43 = cherry_core.nth.call(null, vec__9841, 1, null);
                                return cherry_core.cons.call(null, cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i42, 0)) ? cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3.font-medium.text-gray-900.whitespace-nowrap.dark:text-white"), cherry_core.keyword("scope"), "row", cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column43)), cherry_core.keyword("value").call(null, column43).call(null, row28)) : cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3"), cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column43)), cherry_core.keyword("value").call(null, column43).call(null, row28)), iter__91.call(null, cherry_core.rest.call(null, s__9232)));
                              }
                            }
                            ;
                            break;
                          }
                        }, null, null);
                      };
                      return iter__23116__auto__29.call(null, cherry_core.map_indexed.call(null, cherry_core.vector, columns6));
                    })(), cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3.flex.items-center.justify-end")), cherry_core.truth_.call(null, delete_row7) ? (() => {
                      cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "inline-flex.items-center.text-sm.font-medium.hover:bg-gray-100.dark:hover:bg-gray-700.p-1.5.dark:hover-bg-gray-800.text-center.text-gray-500.hover:text-gray-800.rounded-lg.focus:outline-none.dark:text-gray-400.dark:hover:text-gray-100"), cherry_core.keyword("type"), "button", cherry_core.keyword("data-dropdown-toggle"), cherry_core.str.call(null, table_id8, "-actions-", cherry_core.keyword("id").call(null, row28))), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "w-5.h-5"), cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewbox"), "0 0 20 20", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z"))));
                      return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "hidden.z-10.w-30.bg-white.rounded.divide-y.divide-gray-100.shadow.dark:bg-gray-700.dark:divide-gray-600"), cherry_core.keyword("id"), cherry_core.str.call(null, table_id8, "-actions-", cherry_core.keyword("id").call(null, row28))), cherry_core.vector(cherry_core.keyword("ul"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "py-1.text-sm"), cherry_core.keyword("aria-labelledby"), "benq-ex2710q-dropdown-button"), cherry_core.vector(cherry_core.keyword("li"), cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "flex.w-full.items-center.py-2.px-4.hover:bg-gray-100.dark:hover:bg-gray-600.text-red-500.dark:hover:text-red-400"), cherry_core.keyword("type"), "button", cherry_core.keyword("on-click"), function(_PERCENT_1) {
                        return delete_row7.call(null, _PERCENT_1, row28);
                      }), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "w-4.h-4.mr-2"), cherry_core.keyword("viewbox"), "0 0 14 15", cherry_core.keyword("fill"), "none", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("aria-hidden"), "true"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("clip-rule"), "evenodd", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("d"), "M6.09922 0.300781C5.93212 0.30087 5.76835 0.347476 5.62625 0.435378C5.48414 0.523281 5.36931 0.649009 5.29462 0.798481L4.64302 2.10078H1.59922C1.36052 2.10078 1.13161 2.1956 0.962823 2.36439C0.79404 2.53317 0.699219 2.76209 0.699219 3.00078C0.699219 3.23948 0.79404 3.46839 0.962823 3.63718C1.13161 3.80596 1.36052 3.90078 1.59922 3.90078V12.9008C1.59922 13.3782 1.78886 13.836 2.12643 14.1736C2.46399 14.5111 2.92183 14.7008 3.39922 14.7008H10.5992C11.0766 14.7008 11.5344 14.5111 11.872 14.1736C12.2096 13.836 12.3992 13.3782 12.3992 12.9008V3.90078C12.6379 3.90078 12.8668 3.80596 13.0356 3.63718C13.2044 3.46839 13.2992 3.23948 13.2992 3.00078C13.2992 2.76209 13.2044 2.53317 13.0356 2.36439C12.8668 2.1956 12.6379 2.10078 12.3992 2.10078H9.35542L8.70382 0.798481C8.62913 0.649009 8.5143 0.523281 8.37219 0.435378C8.23009 0.347476 8.06631 0.30087 7.89922 0.300781H6.09922ZM4.29922 5.70078C4.29922 5.46209 4.39404 5.23317 4.56282 5.06439C4.73161 4.8956 4.96052 4.80078 5.19922 4.80078C5.43791 4.80078 5.66683 4.8956 5.83561 5.06439C6.0044 5.23317 6.09922 5.46209 6.09922 5.70078V11.1008C6.09922 11.3395 6.0044 11.5684 5.83561 11.7372C5.66683 11.906 5.43791 12.0008 5.19922 12.0008C4.96052 12.0008 4.73161 11.906 4.56282 11.7372C4.39404 11.5684 4.29922 11.3395 4.29922 11.1008V5.70078ZM8.79922 4.80078C8.56052 4.80078 8.33161 4.8956 8.16282 5.06439C7.99404 5.23317 7.89922 5.46209 7.89922 5.70078V11.1008C7.89922 11.3395 7.99404 11.5684 8.16282 11.7372C8.33161 11.906 8.56052 12.0008 8.79922 12.0008C9.03791 12.0008 9.26683 11.906 9.43561 11.7372C9.6044 11.5684 9.69922 11.3395 9.69922 11.1008V5.70078C9.69922 5.46209 9.6044 5.23317 9.43561 5.06439C9.26683 4.8956 9.03791 4.80078 8.79922 4.80078Z"))), "Delete"))));
                    })() : null)));
                    let G__44 = cherry_core.unchecked_inc.call(null, i__8927);
                    i__8927 = G__44;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__9026), iter__87.call(null, cherry_core.chunk_rest.call(null, s__8823)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__9026), null);
              }
            } else {
              const row45 = cherry_core.first.call(null, s__8823);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("tr"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "border-b.dark:border-gray-700"), cherry_core.keyword("key"), cherry_core.keyword("id").call(null, row45)), (() => {
                const iter__23116__auto__46 = function iter__101(s__102) {
                  return new cherry_core.LazySeq(null, function() {
                    let s__10247 = s__102;
                    while (true) {
                      const temp__23033__auto__48 = cherry_core.seq.call(null, s__10247);
                      if (cherry_core.truth_.call(null, temp__23033__auto__48)) {
                        const s__10249 = temp__23033__auto__48;
                        if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__10249))) {
                          const c__23114__auto__50 = cherry_core.chunk_first.call(null, s__10249);
                          const size__23115__auto__51 = cherry_core.count.call(null, c__23114__auto__50);
                          const b__10452 = cherry_core.chunk_buffer.call(null, size__23115__auto__51);
                          if ((() => {
                            let i__10353 = 0;
                            while (true) {
                              if (i__10353 < size__23115__auto__51) {
                                const vec__10554 = cherry_core._nth.call(null, c__23114__auto__50, i__10353);
                                const i55 = cherry_core.nth.call(null, vec__10554, 0, null);
                                const column56 = cherry_core.nth.call(null, vec__10554, 1, null);
                                cherry_core.chunk_append.call(null, b__10452, cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i55, 0)) ? cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3.font-medium.text-gray-900.whitespace-nowrap.dark:text-white"), cherry_core.keyword("scope"), "row", cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column56)), cherry_core.keyword("value").call(null, column56).call(null, row45)) : cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3"), cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column56)), cherry_core.keyword("value").call(null, column56).call(null, row45)));
                                let G__57 = cherry_core.unchecked_inc.call(null, i__10353);
                                i__10353 = G__57;
                                continue;
                              } else {
                                return true;
                              }
                              ;
                              break;
                            }
                          })()) {
                            return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__10452), iter__101.call(null, cherry_core.chunk_rest.call(null, s__10249)));
                          } else {
                            return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__10452), null);
                          }
                        } else {
                          const vec__10858 = cherry_core.first.call(null, s__10249);
                          const i59 = cherry_core.nth.call(null, vec__10858, 0, null);
                          const column60 = cherry_core.nth.call(null, vec__10858, 1, null);
                          return cherry_core.cons.call(null, cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i59, 0)) ? cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3.font-medium.text-gray-900.whitespace-nowrap.dark:text-white"), cherry_core.keyword("scope"), "row", cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column60)), cherry_core.keyword("value").call(null, column60).call(null, row45)) : cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3"), cherry_core.keyword("key"), cherry_core.keyword("name").call(null, column60)), cherry_core.keyword("value").call(null, column60).call(null, row45)), iter__101.call(null, cherry_core.rest.call(null, s__10249)));
                        }
                      }
                      ;
                      break;
                    }
                  }, null, null);
                };
                return iter__23116__auto__46.call(null, cherry_core.map_indexed.call(null, cherry_core.vector, columns6));
              })(), cherry_core.vector(cherry_core.keyword("td"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "px-4.py-3.flex.items-center.justify-end")), cherry_core.truth_.call(null, delete_row7) ? (() => {
                cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "inline-flex.items-center.text-sm.font-medium.hover:bg-gray-100.dark:hover:bg-gray-700.p-1.5.dark:hover-bg-gray-800.text-center.text-gray-500.hover:text-gray-800.rounded-lg.focus:outline-none.dark:text-gray-400.dark:hover:text-gray-100"), cherry_core.keyword("type"), "button", cherry_core.keyword("data-dropdown-toggle"), cherry_core.str.call(null, table_id8, "-actions-", cherry_core.keyword("id").call(null, row45))), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "w-5.h-5"), cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewbox"), "0 0 20 20", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M6 10a2 2 0 11-4 0 2 2 0 014 0zM12 10a2 2 0 11-4 0 2 2 0 014 0zM16 12a2 2 0 100-4 2 2 0 000 4z"))));
                return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "hidden.z-10.w-30.bg-white.rounded.divide-y.divide-gray-100.shadow.dark:bg-gray-700.dark:divide-gray-600"), cherry_core.keyword("id"), cherry_core.str.call(null, table_id8, "-actions-", cherry_core.keyword("id").call(null, row45))), cherry_core.vector(cherry_core.keyword("ul"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "py-1.text-sm"), cherry_core.keyword("aria-labelledby"), "benq-ex2710q-dropdown-button"), cherry_core.vector(cherry_core.keyword("li"), cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "flex.w-full.items-center.py-2.px-4.hover:bg-gray-100.dark:hover:bg-gray-600.text-red-500.dark:hover:text-red-400"), cherry_core.keyword("type"), "button", cherry_core.keyword("on-click"), function(_PERCENT_1) {
                  return delete_row7.call(null, _PERCENT_1, row45);
                }), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), c.call(null, "w-4.h-4.mr-2"), cherry_core.keyword("viewbox"), "0 0 14 15", cherry_core.keyword("fill"), "none", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("aria-hidden"), "true"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("clip-rule"), "evenodd", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("d"), "M6.09922 0.300781C5.93212 0.30087 5.76835 0.347476 5.62625 0.435378C5.48414 0.523281 5.36931 0.649009 5.29462 0.798481L4.64302 2.10078H1.59922C1.36052 2.10078 1.13161 2.1956 0.962823 2.36439C0.79404 2.53317 0.699219 2.76209 0.699219 3.00078C0.699219 3.23948 0.79404 3.46839 0.962823 3.63718C1.13161 3.80596 1.36052 3.90078 1.59922 3.90078V12.9008C1.59922 13.3782 1.78886 13.836 2.12643 14.1736C2.46399 14.5111 2.92183 14.7008 3.39922 14.7008H10.5992C11.0766 14.7008 11.5344 14.5111 11.872 14.1736C12.2096 13.836 12.3992 13.3782 12.3992 12.9008V3.90078C12.6379 3.90078 12.8668 3.80596 13.0356 3.63718C13.2044 3.46839 13.2992 3.23948 13.2992 3.00078C13.2992 2.76209 13.2044 2.53317 13.0356 2.36439C12.8668 2.1956 12.6379 2.10078 12.3992 2.10078H9.35542L8.70382 0.798481C8.62913 0.649009 8.5143 0.523281 8.37219 0.435378C8.23009 0.347476 8.06631 0.30087 7.89922 0.300781H6.09922ZM4.29922 5.70078C4.29922 5.46209 4.39404 5.23317 4.56282 5.06439C4.73161 4.8956 4.96052 4.80078 5.19922 4.80078C5.43791 4.80078 5.66683 4.8956 5.83561 5.06439C6.0044 5.23317 6.09922 5.46209 6.09922 5.70078V11.1008C6.09922 11.3395 6.0044 11.5684 5.83561 11.7372C5.66683 11.906 5.43791 12.0008 5.19922 12.0008C4.96052 12.0008 4.73161 11.906 4.56282 11.7372C4.39404 11.5684 4.29922 11.3395 4.29922 11.1008V5.70078ZM8.79922 4.80078C8.56052 4.80078 8.33161 4.8956 8.16282 5.06439C7.99404 5.23317 7.89922 5.46209 7.89922 5.70078V11.1008C7.89922 11.3395 7.99404 11.5684 8.16282 11.7372C8.33161 11.906 8.56052 12.0008 8.79922 12.0008C9.03791 12.0008 9.26683 11.906 9.43561 11.7372C9.6044 11.5684 9.69922 11.3395 9.69922 11.1008V5.70078C9.69922 5.46209 9.6044 5.23317 9.43561 5.06439C9.26683 4.8956 9.03791 4.80078 8.79922 4.80078Z"))), "Delete"))));
              })() : null)), iter__87.call(null, cherry_core.rest.call(null, s__8823)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__20.call(null, rows4);
  })())))));
};
var box = function(p__111) {
  const map__1121 = p__111;
  const map__1122 = cherry_core.__destructure_map.call(null, map__1121);
  const title3 = cherry_core.get.call(null, map__1122, cherry_core.keyword("title"));
  const content4 = cherry_core.get.call(null, map__1122, cherry_core.keyword("content"));
  const class$5 = cherry_core.get.call(null, map__1122, cherry_core.keyword("class"));
  const size6 = cherry_core.get.call(null, map__1122, cherry_core.keyword("size"));
  const selected7 = cherry_core.get.call(null, map__1122, cherry_core.keyword("selected"));
  const on_click8 = cherry_core.get.call(null, map__1122, cherry_core.keyword("on-click"));
  return cherry_core.vector(cherry_core.keyword("section"), cherry_core.array_map(cherry_core.keyword("class"), cherry_core.str.call(null, "bg-gray-50 dark:bg-gray-900 antialiased ", class$5), cherry_core.keyword("onClick"), on_click8), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), cherry_core.str.call(null, "h-full border bg-white dark:bg-gray-800 relative shadow sm:rounded-lg md:overflow-auto ", cherry_core.truth_.call(null, selected7) ? "border-blue-500" : "border-white")), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex flex-col md:flex-row items-center justify-between space-y-3 md:space-y-0 md:space-x-4 p-6"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), cherry_core.str.call(null, "w-full ", cherry_core.truth_.call(null, cherry_core._EQ_.call(null, size6, cherry_core.keyword("half"))) ? "md:w-1/2" : null)), cherry_core.vector(cherry_core.keyword("h2"), cherry_core.array_map(cherry_core.keyword("class"), "text-3xl font-semibold font-app-serif"), title3))), cherry_core.truth_.call(null, content4) ? cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "md:overflow-auto p-6 pt-0"), content4) : null));
};
var pluralize = function(coll, word) {
  return str.split.call(null, inflections.pluralize.call(null, cherry_core.count.call(null, coll), inflections.singular.call(null, word)), / /);
};
var content_item_count_text = function(p__113) {
  const map__1141 = p__113;
  const map__1142 = cherry_core.__destructure_map.call(null, map__1141);
  const content_type3 = cherry_core.get.call(null, map__1142, cherry_core.keyword("content-type"));
  return inflections.pluralize.call(null, cherry_core.keyword("content-item-count").call(null, content_type3), inflections.singular.call(null, cherry_core.keyword("name").call(null, content_type3)));
};
var content_item_counts = function(p__115) {
  const map__1161 = p__115;
  const map__1162 = cherry_core.__destructure_map.call(null, map__1161);
  const content_types3 = cherry_core.get.call(null, map__1162, cherry_core.keyword("content-types"));
  return cherry_core.vector(cherry_core.keyword("ul"), (() => {
    const iter__23116__auto__4 = function iter__117(s__118) {
      return new cherry_core.LazySeq(null, function() {
        let s__1185 = s__118;
        while (true) {
          const temp__23033__auto__6 = cherry_core.seq.call(null, s__1185);
          if (cherry_core.truth_.call(null, temp__23033__auto__6)) {
            const s__1187 = temp__23033__auto__6;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__1187))) {
              const c__23114__auto__8 = cherry_core.chunk_first.call(null, s__1187);
              const size__23115__auto__9 = cherry_core.count.call(null, c__23114__auto__8);
              const b__12010 = cherry_core.chunk_buffer.call(null, size__23115__auto__9);
              if ((() => {
                let i__11911 = 0;
                while (true) {
                  if (i__11911 < size__23115__auto__9) {
                    const vec__12112 = cherry_core._nth.call(null, c__23114__auto__8, i__11911);
                    const i13 = cherry_core.nth.call(null, vec__12112, 0, null);
                    const content_type14 = cherry_core.nth.call(null, vec__12112, 1, null);
                    cherry_core.chunk_append.call(null, b__12010, cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("id").call(null, content_type14)), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i13, 0)) ? null : cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-300"), " \u2022 "), cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline text-nowrap font-semibold", cherry_core.keyword("href"), cherry_core.str.call(null, "/admin/content-types/", cherry_core.name.call(null, cherry_core.keyword("slug").call(null, content_type14)))), cherry_core.vector(content_item_count_text, cherry_core.array_map(cherry_core.keyword("content-type"), content_type14)))));
                    let G__15 = cherry_core.unchecked_inc.call(null, i__11911);
                    i__11911 = G__15;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__12010), iter__117.call(null, cherry_core.chunk_rest.call(null, s__1187)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__12010), null);
              }
            } else {
              const vec__12416 = cherry_core.first.call(null, s__1187);
              const i17 = cherry_core.nth.call(null, vec__12416, 0, null);
              const content_type18 = cherry_core.nth.call(null, vec__12416, 1, null);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("id").call(null, content_type18)), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i17, 0)) ? null : cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-300"), " \u2022 "), cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline text-nowrap font-semibold", cherry_core.keyword("href"), cherry_core.str.call(null, "/admin/content-types/", cherry_core.name.call(null, cherry_core.keyword("slug").call(null, content_type18)))), cherry_core.vector(content_item_count_text, cherry_core.array_map(cherry_core.keyword("content-type"), content_type18)))), iter__117.call(null, cherry_core.rest.call(null, s__1187)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__4.call(null, cherry_core.map_indexed.call(null, cherry_core.vector, cherry_core.sort_by.call(null, cherry_core.keyword("name"), content_types3)));
  })());
};
var wrap_component = function(f) {
  return function(props) {
    return /* @__PURE__ */ jsx(DAGProvider, { "dag-atom": dag.dag_atom, children: r.as_element.call(null, cherry_core.vector(f, props)) });
  };
};
var index_by = function(f, coll) {
  return cherry_core.into.call(null, cherry_core.array_map(), cherry_core.map.call(null, function(v) {
    return cherry_core.vector(f.call(null, v), v);
  }, coll));
};
export {
  box,
  c,
  content_item_count_text,
  content_item_counts,
  index_by,
  pluralize,
  table,
  wrap_component
};
