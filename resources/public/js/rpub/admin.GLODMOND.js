import * as cherry_core from "cherry-cljs/cljs.core.js";
import "flowbite";
import { useCallback } from "react";
import * as str from "cherry-cljs/lib/clojure.string.js";
import * as admin_impl from "rpub.admin.impl";
import { DAGProvider, use_dag } from "rpub.lib.dag.react";
import * as html from "rpub.lib.html";
import * as http from "rpub.lib.http";
import "rpub.plugins.content-types";
var dashboard_content_types = function(p__1) {
  const map__21 = p__1;
  const map__22 = cherry_core.__destructure_map.call(null, map__21);
  const content_types3 = cherry_core.get.call(null, map__22, cherry_core.keyword("content-types"));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "w-full md:w-1/2 md:px-2 mb-4", cherry_core.keyword("data-test-id"), "dashboard-content-types"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M5.005 10.19a1 1 0 0 1 1 1v.233l5.998 3.464L18 11.423v-.232a1 1 0 1 1 2 0V12a1 1 0 0 1-.5.866l-6.997 4.042a1 1 0 0 1-1 0l-6.998-4.042a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1ZM5 15.15a1 1 0 0 1 1 1v.232l5.997 3.464 5.998-3.464v-.232a1 1 0 1 1 2 0v.81a1 1 0 0 1-.5.865l-6.998 4.042a1 1 0 0 1-1 0L4.5 17.824a1 1 0 0 1-.5-.866v-.81a1 1 0 0 1 1-1Z", cherry_core.keyword("clip-rule"), "evenodd")), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M12.503 2.134a1 1 0 0 0-1 0L4.501 6.17A1 1 0 0 0 4.5 7.902l7.002 4.047a1 1 0 0 0 1 0l6.998-4.04a1 1 0 0 0 0-1.732l-6.997-4.042Z"))), "Content Types"), cherry_core.keyword("class"), "md:h-48", cherry_core.keyword("size"), cherry_core.keyword("half"), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), (() => {
    const vec__34 = admin_impl.pluralize.call(null, content_types3, "types");
    const num5 = cherry_core.nth.call(null, vec__34, 0, null);
    const word6 = cherry_core.nth.call(null, vec__34, 1, null);
    return cherry_core.vector(cherry_core.keyword("p"), cherry_core.array_map(cherry_core.keyword("class"), "mb-4"), "This site has ", cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold"), num5), " ", word6, " of content:");
  })(), cherry_core.vector(admin_impl.content_item_counts, cherry_core.array_map(cherry_core.keyword("content-types"), content_types3))))));
};
var dashboard_theme = function(p__6) {
  const map__71 = p__6;
  const map__72 = cherry_core.__destructure_map.call(null, map__71);
  const theme3 = cherry_core.get.call(null, map__72, cherry_core.keyword("theme"));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "w-full md:w-1/2 md:px-2 mb-4", cherry_core.keyword("data-test-id"), "dashboard-theme"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z", cherry_core.keyword("clip-rule"), "evenodd")), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Theme"), cherry_core.keyword("class"), "md:h-48", cherry_core.keyword("size"), cherry_core.keyword("half"), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), "This site is using the ", cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold underline"), cherry_core.keyword("label").call(null, theme3)), "."))));
};
var dashboard_plugins = function(p__8) {
  const map__91 = p__8;
  const map__92 = cherry_core.__destructure_map.call(null, map__91);
  const activated_plugins3 = cherry_core.get.call(null, map__92, cherry_core.keyword("activated-plugins"));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "w-full md:w-1/2 md:px-2 mb-4", cherry_core.keyword("data-test-id"), "dashboard-plugins"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z", cherry_core.keyword("clip-rule"), "evenodd")), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Plugins"), cherry_core.keyword("class"), "md:h-48", cherry_core.keyword("size"), cherry_core.keyword("half"), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), (() => {
    const vec__104 = admin_impl.pluralize.call(null, activated_plugins3, "plugin");
    const num5 = cherry_core.nth.call(null, vec__104, 0, null);
    const word6 = cherry_core.nth.call(null, vec__104, 1, null);
    return cherry_core.vector(cherry_core.keyword("p"), cherry_core.array_map(cherry_core.keyword("class"), "mb-4"), "This site has ", cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold"), num5), " ", word6, " activated:");
  })(), cherry_core.vector(cherry_core.keyword("div"), (() => {
    const iter__23116__auto__7 = function iter__13(s__14) {
      return new cherry_core.LazySeq(null, function() {
        let s__148 = s__14;
        while (true) {
          const temp__23033__auto__9 = cherry_core.seq.call(null, s__148);
          if (cherry_core.truth_.call(null, temp__23033__auto__9)) {
            const s__1410 = temp__23033__auto__9;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__1410))) {
              const c__23114__auto__11 = cherry_core.chunk_first.call(null, s__1410);
              const size__23115__auto__12 = cherry_core.count.call(null, c__23114__auto__11);
              const b__1613 = cherry_core.chunk_buffer.call(null, size__23115__auto__12);
              if ((() => {
                let i__1514 = 0;
                while (true) {
                  if (i__1514 < size__23115__auto__12) {
                    const vec__1715 = cherry_core._nth.call(null, c__23114__auto__11, i__1514);
                    const i16 = cherry_core.nth.call(null, vec__1715, 0, null);
                    const plugin17 = cherry_core.nth.call(null, vec__1715, 1, null);
                    cherry_core.chunk_append.call(null, b__1613, cherry_core.vector(cherry_core.keyword("span"), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i16, 0)) ? null : cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-300"), " \u2022 "), cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline text-nowrap font-semibold", cherry_core.keyword("href"), "/admin/plugins"), cherry_core.keyword("label").call(null, plugin17))));
                    let G__18 = cherry_core.unchecked_inc.call(null, i__1514);
                    i__1514 = G__18;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__1613), iter__13.call(null, cherry_core.chunk_rest.call(null, s__1410)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__1613), null);
              }
            } else {
              const vec__2019 = cherry_core.first.call(null, s__1410);
              const i20 = cherry_core.nth.call(null, vec__2019, 0, null);
              const plugin21 = cherry_core.nth.call(null, vec__2019, 1, null);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("span"), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i20, 0)) ? null : cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-300"), " \u2022 "), cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline text-nowrap font-semibold", cherry_core.keyword("href"), "/admin/plugins"), cherry_core.keyword("label").call(null, plugin21))), iter__13.call(null, cherry_core.rest.call(null, s__1410)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__7.call(null, cherry_core.map_indexed.call(null, cherry_core.vector, cherry_core.sort_by.call(null, cherry_core.keyword("label"), activated_plugins3)));
  })())))));
};
var dashboard_settings = function(p__23) {
  const map__241 = p__23;
  const map__242 = cherry_core.__destructure_map.call(null, map__241);
  const settings3 = cherry_core.get.call(null, map__242, cherry_core.keyword("settings"));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "w-full md:w-1/2 md:px-2 mb-4", cherry_core.keyword("data-test-id"), "dashboard-settings"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M9.586 2.586A2 2 0 0 1 11 2h2a2 2 0 0 1 2 2v.089l.473.196.063-.063a2.002 2.002 0 0 1 2.828 0l1.414 1.414a2 2 0 0 1 0 2.827l-.063.064.196.473H20a2 2 0 0 1 2 2v2a2 2 0 0 1-2 2h-.089l-.196.473.063.063a2.002 2.002 0 0 1 0 2.828l-1.414 1.414a2 2 0 0 1-2.828 0l-.063-.063-.473.196V20a2 2 0 0 1-2 2h-2a2 2 0 0 1-2-2v-.089l-.473-.196-.063.063a2.002 2.002 0 0 1-2.828 0l-1.414-1.414a2 2 0 0 1 0-2.827l.063-.064L4.089 15H4a2 2 0 0 1-2-2v-2a2 2 0 0 1 2-2h.09l.195-.473-.063-.063a2 2 0 0 1 0-2.828l1.414-1.414a2 2 0 0 1 2.827 0l.064.063L9 4.089V4a2 2 0 0 1 .586-1.414ZM8 12a4 4 0 1 1 8 0 4 4 0 0 1-8 0Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Settings"), cherry_core.keyword("class"), "md:h-48", cherry_core.keyword("size"), cherry_core.keyword("half"), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold"), "Permalinks: "), cherry_core.vector(cherry_core.keyword("code"), cherry_core.get_in.call(null, settings3, cherry_core.vector(cherry_core.keyword("permalink-single"), cherry_core.keyword("value")))))))));
};
var dashboard_user = function(p__25) {
  const map__261 = p__25;
  const map__262 = cherry_core.__destructure_map.call(null, map__261);
  const current_user3 = cherry_core.get.call(null, map__262, cherry_core.keyword("current-user"));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "w-full md:w-1/2 md:px-2 mb-4", cherry_core.keyword("data-test-id"), "dashboard-user"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M12 20a7.966 7.966 0 0 1-5.002-1.756l.002.001v-.683c0-1.794 1.492-3.25 3.333-3.25h3.334c1.84 0 3.333 1.456 3.333 3.25v.683A7.966 7.966 0 0 1 12 20ZM2 12C2 6.477 6.477 2 12 2s10 4.477 10 10c0 5.5-4.44 9.963-9.932 10h-.138C6.438 21.962 2 17.5 2 12Zm10-5c-1.84 0-3.333 1.455-3.333 3.25S10.159 13.5 12 13.5c1.84 0 3.333-1.455 3.333-3.25S13.841 7 12 7Z", cherry_core.keyword("clip-rule"), "evenodd"))), "User"), cherry_core.keyword("class"), "md:h-48", cherry_core.keyword("size"), cherry_core.keyword("half"), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), "You're logged in as ", cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold underline", cherry_core.keyword("href"), "/admin/users"), cherry_core.keyword("username").call(null, current_user3)), "."))));
};
var dashboard_server = function(p__27) {
  const map__281 = p__27;
  const map__282 = cherry_core.__destructure_map.call(null, map__281);
  const rpub_version3 = cherry_core.get.call(null, map__282, cherry_core.keyword("rpub-version"));
  const rpub_url4 = cherry_core.str.call(null, "https://github.com/rpub-clj/rpub/tree/v", rpub_version3);
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "w-full md:w-1/2 md:px-2 mb-4", cherry_core.keyword("data-test-id"), "dashboard-server"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M5 5a2 2 0 0 0-2 2v3a1 1 0 0 0 1 1h16a1 1 0 0 0 1-1V7a2 2 0 0 0-2-2H5Zm9 2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17ZM3 17v-3a1 1 0 0 1 1-1h16a1 1 0 0 1 1 1v3a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2Zm11-2a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H14Zm3 0a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Server"), cherry_core.keyword("class"), "md:h-48", cherry_core.keyword("size"), cherry_core.keyword("half"), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), "This server is running ", cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold underline", cherry_core.keyword("href"), rpub_url4, cherry_core.keyword("target"), "_blank"), cherry_core.str.call(null, "rPub v", rpub_version3)), "."))));
};
var dashboard_page = function(props) {
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex flex-wrap py-4 px-4 md:px-2"), cherry_core.vector(dashboard_content_types, props), cherry_core.vector(dashboard_theme, props), cherry_core.vector(dashboard_plugins, props), cherry_core.vector(dashboard_settings, props), cherry_core.vector(dashboard_user, props), cherry_core.vector(dashboard_server, props));
};
html.add_element.call(null, cherry_core.keyword("dashboard-page"), admin_impl.wrap_component.call(null, dashboard_page));
var settings_page = function(p__29) {
  const map__301 = p__29;
  const map__302 = cherry_core.__destructure_map.call(null, map__301);
  const props3 = map__302;
  const anti_forgery_token4 = cherry_core.get.call(null, map__302, cherry_core.keyword("anti-forgery-token"));
  const settings5 = cherry_core.get.call(null, map__302, cherry_core.keyword("settings"));
  const vec__316 = use_dag.call(null, cherry_core.vector(cherry_core.keyword("settings-page/field-values"), cherry_core.keyword("settings-page/submitting")));
  const map__347 = cherry_core.nth.call(null, vec__316, 0, null);
  const map__348 = cherry_core.__destructure_map.call(null, map__347);
  const field_values9 = cherry_core.get.call(null, map__348, cherry_core.keyword("settings-page/field-values"));
  const submitting10 = cherry_core.get.call(null, map__348, cherry_core.keyword("settings-page/submitting"));
  const push11 = cherry_core.nth.call(null, vec__316, 1, null);
  const settings_index12 = admin_impl.index_by.call(null, cherry_core.keyword("key"), settings5);
  const http_opts13 = cherry_core.array_map(cherry_core.keyword("anti-forgery-token"), anti_forgery_token4);
  const update_setting14 = function(setting_key, e) {
    const value15 = e.target.value;
    return push11.call(null, cherry_core.keyword("settings-page/change-input"), cherry_core.vector(setting_key, value15));
  };
  const submit_form16 = function(e) {
    e.preventDefault();
    push11.call(null, cherry_core.keyword("settings-page/submit-start"));
    const on_complete17 = function(_, err) {
      if (cherry_core.truth_.call(null, err)) {
        return push11.call(null, cherry_core.keyword("settings-page/submit-error"));
      } else {
        return window.location.reload();
      }
    };
    const settings18 = cherry_core.vals.call(null, cherry_core.update_vals.call(null, cherry_core.merge_with.call(null, function(_PERCENT_1, _PERCENT_2) {
      return cherry_core.assoc.call(null, _PERCENT_1, cherry_core.keyword("value"), _PERCENT_2);
    }, settings_index12, field_values9), function(_PERCENT_1) {
      return cherry_core.select_keys.call(null, _PERCENT_1, cherry_core.vector(cherry_core.keyword("key"), cherry_core.keyword("value")));
    }));
    const http_opts_SINGLEQUOTE_19 = cherry_core.merge.call(null, http_opts13, cherry_core.array_map(cherry_core.keyword("body"), cherry_core.array_map(cherry_core.keyword("settings"), settings18), cherry_core.keyword("on-complete"), on_complete17));
    return http.post.call(null, "/api/update-settings", http_opts_SINGLEQUOTE_19);
  };
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), "Settings", cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("section"), cherry_core.array_map(cherry_core.keyword("class"), "bg-white dark:bg-gray-900"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "max-w-2xl"), cherry_core.vector(cherry_core.keyword("form"), cherry_core.array_map(cherry_core.keyword("onSubmit"), submit_form16), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "grid gap-4 sm:grid-cols-2 sm:gap-6"), (() => {
    const iter__23116__auto__20 = function iter__35(s__36) {
      return new cherry_core.LazySeq(null, function() {
        let s__3621 = s__36;
        while (true) {
          const temp__23033__auto__22 = cherry_core.seq.call(null, s__3621);
          if (cherry_core.truth_.call(null, temp__23033__auto__22)) {
            const s__3623 = temp__23033__auto__22;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__3623))) {
              const c__23114__auto__24 = cherry_core.chunk_first.call(null, s__3623);
              const size__23115__auto__25 = cherry_core.count.call(null, c__23114__auto__24);
              const b__3826 = cherry_core.chunk_buffer.call(null, size__23115__auto__25);
              if ((() => {
                let i__3727 = 0;
                while (true) {
                  if (i__3727 < size__23115__auto__25) {
                    const setting28 = cherry_core._nth.call(null, c__23114__auto__24, i__3727);
                    cherry_core.chunk_append.call(null, b__3826, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("key").call(null, setting28), cherry_core.keyword("class"), "sm:col-span-2"), cherry_core.vector(cherry_core.keyword("label"), cherry_core.array_map(cherry_core.keyword("class"), "block mb-2 text-sm font-semibold text-gray-900 dark:text-white", cherry_core.keyword("for"), "name"), cherry_core.keyword("label").call(null, setting28)), cherry_core.vector(html.input2, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("name"), cherry_core.keyword("key").call(null, setting28), cherry_core.keyword("value"), (() => {
                      const or__23431__auto__29 = cherry_core.get.call(null, field_values9, cherry_core.keyword("key").call(null, setting28));
                      if (cherry_core.truth_.call(null, or__23431__auto__29)) {
                        return or__23431__auto__29;
                      } else {
                        return cherry_core.keyword("value").call(null, setting28);
                      }
                    })(), cherry_core.keyword("on-change"), function(_PERCENT_1) {
                      return update_setting14.call(null, cherry_core.keyword("key").call(null, setting28), _PERCENT_1);
                    }))));
                    let G__30 = cherry_core.unchecked_inc.call(null, i__3727);
                    i__3727 = G__30;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__3826), iter__35.call(null, cherry_core.chunk_rest.call(null, s__3623)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__3826), null);
              }
            } else {
              const setting31 = cherry_core.first.call(null, s__3623);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("key").call(null, setting31), cherry_core.keyword("class"), "sm:col-span-2"), cherry_core.vector(cherry_core.keyword("label"), cherry_core.array_map(cherry_core.keyword("class"), "block mb-2 text-sm font-semibold text-gray-900 dark:text-white", cherry_core.keyword("for"), "name"), cherry_core.keyword("label").call(null, setting31)), cherry_core.vector(html.input2, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("name"), cherry_core.keyword("key").call(null, setting31), cherry_core.keyword("value"), (() => {
                const or__23431__auto__32 = cherry_core.get.call(null, field_values9, cherry_core.keyword("key").call(null, setting31));
                if (cherry_core.truth_.call(null, or__23431__auto__32)) {
                  return or__23431__auto__32;
                } else {
                  return cherry_core.keyword("value").call(null, setting31);
                }
              })(), cherry_core.keyword("on-change"), function(_PERCENT_1) {
                return update_setting14.call(null, cherry_core.keyword("key").call(null, setting31), _PERCENT_1);
              }))), iter__35.call(null, cherry_core.rest.call(null, s__3623)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__20.call(null, cherry_core.sort_by.call(null, cherry_core.keyword("label"), cherry_core.vals.call(null, settings_index12)));
  })(), cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("submit"), cherry_core.keyword("class"), "w-[120px] text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800", cherry_core.keyword("disabled"), submitting10), cherry_core.truth_.call(null, submitting10) ? cherry_core.vector(cherry_core.keyword("span"), cherry_core.vector(html.spinner), "Saving") : "Save"))))))));
};
html.add_element.call(null, cherry_core.keyword("settings-page"), admin_impl.wrap_component.call(null, settings_page));
var users_columns = cherry_core.vector(cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("id"), cherry_core.keyword("name"), "ID"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("created-at"), cherry_core.keyword("name"), "Created At"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("created-by"), cherry_core.keyword("name"), "Created By"));
var columns = cherry_core.vector(cherry_core.array_map(cherry_core.keyword("name"), "Username", cherry_core.keyword("value"), function(p__39) {
  const map__401 = p__39;
  const map__402 = cherry_core.__destructure_map.call(null, map__401);
  const username3 = cherry_core.get.call(null, map__402, cherry_core.keyword("username"));
  return cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold"), username3);
}));
var users_page = function(p__41) {
  const map__421 = p__41;
  const map__422 = cherry_core.__destructure_map.call(null, map__421);
  const users3 = cherry_core.get.call(null, map__422, cherry_core.keyword("users"));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4"), cherry_core.vector(admin_impl.table, cherry_core.array_map(cherry_core.keyword("title"), "Users", cherry_core.keyword("columns"), columns, cherry_core.keyword("rows"), users3)));
};
html.add_element.call(null, cherry_core.keyword("users-page"), admin_impl.wrap_component.call(null, users_page));
var theme_icon = function(_) {
  return cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M13 10a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H14a1 1 0 0 1-1-1Z", cherry_core.keyword("clip-rule"), "evenodd")), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M2 6a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v12c0 .556-.227 1.06-.593 1.422A.999.999 0 0 1 20.5 20H4a2.002 2.002 0 0 1-2-2V6Zm6.892 12 3.833-5.356-3.99-4.322a1 1 0 0 0-1.549.097L4 12.879V6h16v9.95l-3.257-3.619a1 1 0 0 0-1.557.088L11.2 18H8.892Z", cherry_core.keyword("clip-rule"), "evenodd")));
};
var themes_page = function(p__43) {
  const map__441 = p__43;
  const map__442 = cherry_core.__destructure_map.call(null, map__441);
  const themes3 = cherry_core.get.call(null, map__442, cherry_core.keyword("themes"));
  const theme_name_setting4 = cherry_core.get.call(null, map__442, cherry_core.keyword("theme-name-setting"));
  const anti_forgery_token5 = cherry_core.get.call(null, map__442, cherry_core.keyword("anti-forgery-token"));
  const vec__456 = use_dag.call(null, cherry_core.vector(cherry_core.keyword("themes-page/current-theme-name-setting")));
  const map__487 = cherry_core.nth.call(null, vec__456, 0, null);
  const map__488 = cherry_core.__destructure_map.call(null, map__487);
  const current_theme_name_setting9 = cherry_core.get.call(null, map__488, cherry_core.keyword("themes-page/current-theme-name-setting"));
  const push10 = cherry_core.nth.call(null, vec__456, 1, null);
  const http_opts11 = cherry_core.array_map(cherry_core.keyword("anti-forgery-token"), anti_forgery_token5);
  const theme_name_value12 = cherry_core.keyword("value").call(null, (() => {
    const or__23431__auto__13 = current_theme_name_setting9;
    if (cherry_core.truth_.call(null, or__23431__auto__13)) {
      return or__23431__auto__13;
    } else {
      return theme_name_setting4;
    }
  })());
  const activated_QMARK_14 = function(_PERCENT_1) {
    return cherry_core._EQ_.call(null, cherry_core.keyword("label").call(null, _PERCENT_1), theme_name_value12);
  };
  const activate_theme15 = function(theme, e) {
    e.preventDefault();
    const body16 = cherry_core.array_map(cherry_core.keyword("settings"), cherry_core.vector(cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("theme-name"), cherry_core.keyword("value"), cherry_core.keyword("label").call(null, theme))));
    const on_complete17 = function(res, _err) {
      if (cherry_core.truth_.call(null, res)) {
        return push10.call(null, cherry_core.keyword("themes-page/activate-theme"), cherry_core.keyword("label").call(null, theme));
      }
    };
    const http_opts_SINGLEQUOTE_18 = cherry_core.merge.call(null, http_opts11, cherry_core.array_map(cherry_core.keyword("body"), body16, cherry_core.keyword("on-complete"), on_complete17));
    return http.post.call(null, "/api/update-settings", http_opts_SINGLEQUOTE_18);
  };
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4"), (() => {
    const iter__23116__auto__19 = function iter__49(s__50) {
      return new cherry_core.LazySeq(null, function() {
        let s__5020 = s__50;
        while (true) {
          const temp__23033__auto__21 = cherry_core.seq.call(null, s__5020);
          if (cherry_core.truth_.call(null, temp__23033__auto__21)) {
            const s__5022 = temp__23033__auto__21;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__5022))) {
              const c__23114__auto__23 = cherry_core.chunk_first.call(null, s__5022);
              const size__23115__auto__24 = cherry_core.count.call(null, c__23114__auto__23);
              const b__5225 = cherry_core.chunk_buffer.call(null, size__23115__auto__24);
              if ((() => {
                let i__5126 = 0;
                while (true) {
                  if (i__5126 < size__23115__auto__24) {
                    const theme27 = cherry_core._nth.call(null, c__23114__auto__23, i__5126);
                    const activated28 = activated_QMARK_14.call(null, theme27);
                    cherry_core.chunk_append.call(null, b__5225, cherry_core.vector(cherry_core.keyword("form"), cherry_core.array_map(cherry_core.keyword("method"), "post"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("label").call(null, theme27), cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center", cherry_core.keyword("style"), cherry_core.array_map(cherry_core.keyword("margin-top"), "-1px")), cherry_core.vector(theme_icon), cherry_core.keyword("label").call(null, theme27), cherry_core.truth_.call(null, activated28) ? cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-green-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow-inner ml-auto cursor-auto w-44", cherry_core.keyword("type"), "submit"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-6 h-6 text-white mr-2", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Active")) : cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44", cherry_core.keyword("onClick"), function(_PERCENT_1) {
                      return activate_theme15.call(null, theme27, _PERCENT_1);
                    }, cherry_core.keyword("type"), "submit"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-6 h-6 text-white dark:text-white mr-2", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M8 5v4.997a.31.31 0 0 1-.068.113c-.08.098-.213.207-.378.301-.947.543-1.713 1.54-2.191 2.488A6.237 6.237 0 0 0 4.82 14.4c-.1.48-.138 1.031.018 1.539C5.12 16.846 6.02 17 6.414 17H11v3a1 1 0 1 0 2 0v-3h4.586c.395 0 1.295-.154 1.575-1.061.156-.508.118-1.059.017-1.539a6.241 6.241 0 0 0-.541-1.5c-.479-.95-1.244-1.946-2.191-2.489a1.393 1.393 0 0 1-.378-.301.309.309 0 0 1-.068-.113V5h1a1 1 0 1 0 0-2H7a1 1 0 1 0 0 2h1Z"))), "Activate Theme"))), cherry_core.keyword("class"), "mb-4", cherry_core.keyword("content"), (() => {
                      const temp__23033__auto__29 = cherry_core.keyword("description").call(null, theme27);
                      if (cherry_core.truth_.call(null, temp__23033__auto__29)) {
                        const v30 = temp__23033__auto__29;
                        return cherry_core.vector(cherry_core.keyword("p"), v30);
                      }
                    })()))));
                    let G__31 = cherry_core.unchecked_inc.call(null, i__5126);
                    i__5126 = G__31;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__5225), iter__49.call(null, cherry_core.chunk_rest.call(null, s__5022)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__5225), null);
              }
            } else {
              const theme32 = cherry_core.first.call(null, s__5022);
              const activated33 = activated_QMARK_14.call(null, theme32);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("form"), cherry_core.array_map(cherry_core.keyword("method"), "post"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("label").call(null, theme32), cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center", cherry_core.keyword("style"), cherry_core.array_map(cherry_core.keyword("margin-top"), "-1px")), cherry_core.vector(theme_icon), cherry_core.keyword("label").call(null, theme32), cherry_core.truth_.call(null, activated33) ? cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-green-500 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow-inner ml-auto cursor-auto w-44", cherry_core.keyword("type"), "submit"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-6 h-6 text-white mr-2", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M2 12C2 6.477 6.477 2 12 2s10 4.477 10 10-4.477 10-10 10S2 17.523 2 12Zm13.707-1.293a1 1 0 0 0-1.414-1.414L11 12.586l-1.793-1.793a1 1 0 0 0-1.414 1.414l2.5 2.5a1 1 0 0 0 1.414 0l4-4Z", cherry_core.keyword("clip-rule"), "evenodd"))), "Active")) : cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44", cherry_core.keyword("onClick"), function(_PERCENT_1) {
                return activate_theme15.call(null, theme32, _PERCENT_1);
              }, cherry_core.keyword("type"), "submit"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-6 h-6 text-white dark:text-white mr-2", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M8 5v4.997a.31.31 0 0 1-.068.113c-.08.098-.213.207-.378.301-.947.543-1.713 1.54-2.191 2.488A6.237 6.237 0 0 0 4.82 14.4c-.1.48-.138 1.031.018 1.539C5.12 16.846 6.02 17 6.414 17H11v3a1 1 0 1 0 2 0v-3h4.586c.395 0 1.295-.154 1.575-1.061.156-.508.118-1.059.017-1.539a6.241 6.241 0 0 0-.541-1.5c-.479-.95-1.244-1.946-2.191-2.489a1.393 1.393 0 0 1-.378-.301.309.309 0 0 1-.068-.113V5h1a1 1 0 1 0 0-2H7a1 1 0 1 0 0 2h1Z"))), "Activate Theme"))), cherry_core.keyword("class"), "mb-4", cherry_core.keyword("content"), (() => {
                const temp__23033__auto__34 = cherry_core.keyword("description").call(null, theme32);
                if (cherry_core.truth_.call(null, temp__23033__auto__34)) {
                  const v35 = temp__23033__auto__34;
                  return cherry_core.vector(cherry_core.keyword("p"), v35);
                }
              })()))), iter__49.call(null, cherry_core.rest.call(null, s__5022)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__19.call(null, themes3);
  })());
};
html.add_element.call(null, cherry_core.keyword("themes-page"), admin_impl.wrap_component.call(null, themes_page));
var plugin_icon = function(_) {
  return cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "w-8 h-8 text-gray-500 dark:text-white mr-4", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("width"), "24", cherry_core.keyword("height"), "24", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 24 24"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M13 11.15V4a1 1 0 1 0-2 0v7.15L8.78 8.374a1 1 0 1 0-1.56 1.25l4 5a1 1 0 0 0 1.56 0l4-5a1 1 0 1 0-1.56-1.25L13 11.15Z", cherry_core.keyword("clip-rule"), "evenodd")), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("fill-rule"), "evenodd", cherry_core.keyword("d"), "M9.657 15.874 7.358 13H5a2 2 0 0 0-2 2v4a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-4a2 2 0 0 0-2-2h-2.358l-2.3 2.874a3 3 0 0 1-4.685 0ZM17 16a1 1 0 1 0 0 2h.01a1 1 0 1 0 0-2H17Z", cherry_core.keyword("clip-rule"), "evenodd")));
};
var plugin_url = function(plugin) {
  const vec__531 = str.split.call(null, cherry_core.keyword("key").call(null, plugin), /\//);
  const plugin_ns2 = cherry_core.nth.call(null, vec__531, 0, null);
  const suffix3 = cherry_core.last.call(null, str.split.call(null, plugin_ns2, /\./));
  return cherry_core.str.call(null, "https://github.com/rpub-clj/plugins/tree/main/plugins/", suffix3);
};
var plugins_page = function(p__56) {
  const map__571 = p__56;
  const map__572 = cherry_core.__destructure_map.call(null, map__571);
  const _props3 = map__572;
  const current_plugins4 = cherry_core.get.call(null, map__572, cherry_core.keyword("current-plugins"));
  const available_plugins5 = cherry_core.get.call(null, map__572, cherry_core.keyword("available-plugins"));
  const anti_forgery_token6 = cherry_core.get.call(null, map__572, cherry_core.keyword("anti-forgery-token"));
  const vec__587 = use_dag.call(null, cherry_core.vector(cherry_core.keyword("plugins-page/needs-restart"), cherry_core.keyword("plugins-page/activated-plugins")));
  const map__618 = cherry_core.nth.call(null, vec__587, 0, null);
  const map__619 = cherry_core.__destructure_map.call(null, map__618);
  const needs_restart10 = cherry_core.get.call(null, map__619, cherry_core.keyword("plugins-page/needs-restart"));
  const activated_plugins11 = cherry_core.get.call(null, map__619, cherry_core.keyword("plugins-page/activated-plugins"));
  const push12 = cherry_core.nth.call(null, vec__587, 1, null);
  const http_opts13 = cherry_core.array_map(cherry_core.keyword("anti-forgery-token"), anti_forgery_token6);
  const current_plugin_index14 = admin_impl.index_by.call(null, cherry_core.keyword("key"), current_plugins4);
  const activate_plugin15 = function(_e, plugin) {
    const plugin_SINGLEQUOTE_16 = cherry_core.assoc.call(null, plugin, cherry_core.keyword("activated"), true);
    const body17 = cherry_core.array_map(cherry_core.keyword("plugin"), cherry_core.update.call(null, cherry_core.select_keys.call(null, plugin_SINGLEQUOTE_16, cherry_core.vector(cherry_core.keyword("key"))), cherry_core.keyword("key"), function(_PERCENT_1) {
      return cherry_core.str.call(null, ":", _PERCENT_1);
    }));
    const on_complete18 = function(res, _err) {
      if (cherry_core.truth_.call(null, res)) {
        return push12.call(null, cherry_core.keyword("plugins-page/activate-plugin"), cherry_core.keyword("key").call(null, plugin_SINGLEQUOTE_16));
      }
    };
    const http_opts_SINGLEQUOTE_19 = cherry_core.merge.call(null, http_opts13, cherry_core.array_map(cherry_core.keyword("body"), body17, cherry_core.keyword("on-complete"), on_complete18));
    return http.post.call(null, "/api/activate-plugin", http_opts_SINGLEQUOTE_19);
  };
  const deactivate_plugin20 = function(_e, plugin) {
    const body21 = cherry_core.array_map(cherry_core.keyword("plugin"), cherry_core.update.call(null, cherry_core.select_keys.call(null, plugin, cherry_core.vector(cherry_core.keyword("key"))), cherry_core.keyword("key"), function(_PERCENT_1) {
      return cherry_core.str.call(null, ":", _PERCENT_1);
    }));
    const on_complete22 = function(res, _err) {
      if (cherry_core.truth_.call(null, res)) {
        return push12.call(null, cherry_core.keyword("plugins-page/deactivate-plugin"), cherry_core.keyword("key").call(null, plugin));
      }
    };
    const http_opts_SINGLEQUOTE_23 = cherry_core.merge.call(null, http_opts13, cherry_core.array_map(cherry_core.keyword("body"), body21, cherry_core.keyword("on-complete"), on_complete22));
    return http.post.call(null, "/api/deactivate-plugin", http_opts_SINGLEQUOTE_23);
  };
  const restart_server24 = function(_e) {
    const on_complete25 = function(_res, _err) {
      return null;
    };
    const http_opts_SINGLEQUOTE_26 = cherry_core.merge.call(null, http_opts13, cherry_core.array_map(cherry_core.keyword("on-complete"), on_complete25));
    push12.call(null, cherry_core.keyword("plugins-page/restart-server"));
    return http.post.call(null, "/api/restart-server", http_opts_SINGLEQUOTE_26);
  };
  const available_plugin_index27 = admin_impl.index_by.call(null, cherry_core.keyword("key"), available_plugins5);
  const activated_plugin_index28 = cherry_core.into.call(null, cherry_core.array_map(), cherry_core.map.call(null, function(k) {
    return cherry_core.vector(k, cherry_core.array_map(cherry_core.keyword("activated"), true));
  }, activated_plugins11));
  const combined_plugin_index29 = cherry_core.merge_with.call(null, cherry_core.merge, current_plugin_index14, available_plugin_index27, activated_plugin_index28);
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("title"), "Plugins", cherry_core.keyword("class"), "mb-4", cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex"), cherry_core.vector(cherry_core.keyword("p"), cherry_core.array_map(cherry_core.keyword("class"), "italic"), "Note: The server must be restarted after activating a plugin for the first time."), cherry_core.truth_.call(null, needs_restart10) ? cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("class"), "font-app-sans inline-flex items-center px-5 py-2.5 text-sm font-medium text-center text-white bg-blue-700 rounded-lg focus:ring-4 focus:ring-primary-200 dark:focus:ring-primary-900 hover:bg-primary-800 shadow ml-auto w-44", cherry_core.keyword("onClick"), restart_server24), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "inline-flex items-center mx-auto"), "Restart")) : null))), (() => {
    const iter__23116__auto__30 = function iter__62(s__63) {
      return new cherry_core.LazySeq(null, function() {
        let s__6331 = s__63;
        while (true) {
          const temp__23033__auto__32 = cherry_core.seq.call(null, s__6331);
          if (cherry_core.truth_.call(null, temp__23033__auto__32)) {
            const s__6333 = temp__23033__auto__32;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__6333))) {
              const c__23114__auto__34 = cherry_core.chunk_first.call(null, s__6333);
              const size__23115__auto__35 = cherry_core.count.call(null, c__23114__auto__34);
              const b__6536 = cherry_core.chunk_buffer.call(null, size__23115__auto__35);
              if ((() => {
                let i__6437 = 0;
                while (true) {
                  if (i__6437 < size__23115__auto__35) {
                    const plugin38 = cherry_core._nth.call(null, c__23114__auto__34, i__6437);
                    cherry_core.chunk_append.call(null, b__6536, cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("key").call(null, plugin38), cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center", cherry_core.keyword("style"), cherry_core.array_map(cherry_core.keyword("margin-top"), "-1px")), cherry_core.vector(plugin_icon), cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline", cherry_core.keyword("href"), plugin_url.call(null, plugin38), cherry_core.keyword("target"), "_blank"), (() => {
                      const or__23431__auto__39 = cherry_core.keyword("label").call(null, plugin38);
                      if (cherry_core.truth_.call(null, or__23431__auto__39)) {
                        return or__23431__auto__39;
                      } else {
                        return cherry_core.keyword("key").call(null, plugin38);
                      }
                    })()), cherry_core.truth_.call(null, cherry_core.keyword("activated").call(null, plugin38)) ? cherry_core.vector(html.activated_button, cherry_core.array_map(cherry_core.keyword("on-click"), function(_PERCENT_1) {
                      return deactivate_plugin20.call(null, _PERCENT_1, plugin38);
                    })) : cherry_core.vector(html.activate_button, cherry_core.array_map(cherry_core.keyword("label"), "Activate Plugin", cherry_core.keyword("on-click"), function(_PERCENT_1) {
                      return activate_plugin15.call(null, _PERCENT_1, plugin38);
                    }))), cherry_core.keyword("class"), "mb-4", cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), (() => {
                      const temp__23033__auto__40 = cherry_core.keyword("description").call(null, plugin38);
                      if (cherry_core.truth_.call(null, temp__23033__auto__40)) {
                        const v41 = temp__23033__auto__40;
                        return cherry_core.vector(cherry_core.keyword("p"), v41);
                      }
                    })()))));
                    let G__42 = cherry_core.unchecked_inc.call(null, i__6437);
                    i__6437 = G__42;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__6536), iter__62.call(null, cherry_core.chunk_rest.call(null, s__6333)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__6536), null);
              }
            } else {
              const plugin43 = cherry_core.first.call(null, s__6333);
              return cherry_core.cons.call(null, cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("key").call(null, plugin43), cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center", cherry_core.keyword("style"), cherry_core.array_map(cherry_core.keyword("margin-top"), "-1px")), cherry_core.vector(plugin_icon), cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline", cherry_core.keyword("href"), plugin_url.call(null, plugin43), cherry_core.keyword("target"), "_blank"), (() => {
                const or__23431__auto__44 = cherry_core.keyword("label").call(null, plugin43);
                if (cherry_core.truth_.call(null, or__23431__auto__44)) {
                  return or__23431__auto__44;
                } else {
                  return cherry_core.keyword("key").call(null, plugin43);
                }
              })()), cherry_core.truth_.call(null, cherry_core.keyword("activated").call(null, plugin43)) ? cherry_core.vector(html.activated_button, cherry_core.array_map(cherry_core.keyword("on-click"), function(_PERCENT_1) {
                return deactivate_plugin20.call(null, _PERCENT_1, plugin43);
              })) : cherry_core.vector(html.activate_button, cherry_core.array_map(cherry_core.keyword("label"), "Activate Plugin", cherry_core.keyword("on-click"), function(_PERCENT_1) {
                return activate_plugin15.call(null, _PERCENT_1, plugin43);
              }))), cherry_core.keyword("class"), "mb-4", cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), (() => {
                const temp__23033__auto__45 = cherry_core.keyword("description").call(null, plugin43);
                if (cherry_core.truth_.call(null, temp__23033__auto__45)) {
                  const v46 = temp__23033__auto__45;
                  return cherry_core.vector(cherry_core.keyword("p"), v46);
                }
              })()))), iter__62.call(null, cherry_core.rest.call(null, s__6333)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__30.call(null, cherry_core.sort_by.call(null, function(_PERCENT_1) {
      return str.lower_case.call(null, (() => {
        const or__23431__auto__47 = cherry_core.keyword("label").call(null, _PERCENT_1);
        if (cherry_core.truth_.call(null, or__23431__auto__47)) {
          return or__23431__auto__47;
        } else {
          return cherry_core.keyword("key").call(null, _PERCENT_1);
        }
      })());
    }, cherry_core.vals.call(null, combined_plugin_index29)));
  })());
};
html.add_element.call(null, cherry_core.keyword("plugins-page"), admin_impl.wrap_component.call(null, plugins_page));
export {
  dashboard_content_types,
  dashboard_page,
  dashboard_plugins,
  dashboard_server,
  dashboard_settings,
  dashboard_theme,
  dashboard_user,
  plugin_icon,
  plugin_url,
  plugins_page,
  settings_page,
  theme_icon,
  themes_page,
  users_page
};
