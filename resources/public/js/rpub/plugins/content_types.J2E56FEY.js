import * as cherry_core from "cherry-cljs/cljs.core.js";
import { useEffect, useCallback } from "react";
import * as str from "cherry-cljs/lib/clojure.string.js";
import * as inflections from "rads.inflections";
import * as admin_impl from "rpub.admin.impl";
import { use_dag } from "rpub.lib.dag.react";
import * as html from "rpub.lib.html";
import * as http from "rpub.lib.http";
var random_uuid = function() {
  return crypto.randomUUID();
};
var quill_toolbar = cherry_core.vector(cherry_core.vector(cherry_core.array_map(cherry_core.keyword("header"), cherry_core.vector(1, 2, 3, 4, 5, 6, null))), cherry_core.vector("bold", "italic"), cherry_core.vector(cherry_core.array_map(cherry_core.keyword("list"), "bullet"), cherry_core.array_map(cherry_core.keyword("list"), "ordered")), cherry_core.vector("blockquote"), cherry_core.vector(cherry_core.array_map(cherry_core.keyword("align"), ""), cherry_core.array_map(cherry_core.keyword("align"), "center"), cherry_core.array_map(cherry_core.keyword("align"), "right")), cherry_core.vector("link"), cherry_core.vector("clean"));
var quill_defaults = cherry_core.array_map(cherry_core.keyword("theme"), cherry_core.keyword("snow"), cherry_core.keyword("modules"), cherry_core.array_map(cherry_core.keyword("toolbar"), quill_toolbar));
var start_quill_BANG_ = function(selector, opts) {
  const opts_SINGLEQUOTE_1 = cherry_core.merge.call(null, quill_defaults, cherry_core.dissoc.call(null, opts, cherry_core.keyword("html")));
  const instance2 = new Quill(selector, cherry_core.clj__GT_js.call(null, opts_SINGLEQUOTE_1));
  const temp__23033__auto__3 = cherry_core.keyword("html").call(null, opts);
  if (cherry_core.truth_.call(null, temp__23033__auto__3)) {
    const html4 = temp__23033__auto__3;
    instance2.clipboard.dangerouslyPasteHTML(html4);
  }
  ;
  return instance2;
};
var __GT_field = function(p__195) {
  const map__1961 = p__195;
  const map__1962 = cherry_core.__destructure_map.call(null, map__1961);
  const name3 = cherry_core.get.call(null, map__1962, cherry_core.keyword("name"));
  const type4 = cherry_core.get.call(null, map__1962, cherry_core.keyword("type"));
  return cherry_core.array_map(cherry_core.keyword("id"), random_uuid.call(null), cherry_core.keyword("name"), name3, cherry_core.keyword("type"), type4);
};
var __GT_slug = function(title) {
  return inflections.parameterize.call(null, title);
};
var __GT_content_type = function(p__197) {
  const map__1981 = p__197;
  const map__1982 = cherry_core.__destructure_map.call(null, map__1981);
  const id3 = cherry_core.get.call(null, map__1982, cherry_core.keyword("id"));
  const name4 = cherry_core.get.call(null, map__1982, cherry_core.keyword("name"));
  const slug5 = cherry_core.get.call(null, map__1982, cherry_core.keyword("slug"));
  const fields6 = cherry_core.get.call(null, map__1982, cherry_core.keyword("fields"));
  return cherry_core.array_map(cherry_core.keyword("id"), (() => {
    const or__23431__auto__7 = random_uuid.call(null);
    if (cherry_core.truth_.call(null, or__23431__auto__7)) {
      return or__23431__auto__7;
    } else {
      return id3;
    }
  })(), cherry_core.keyword("name"), name4, cherry_core.keyword("slug"), slug5, cherry_core.keyword("fields"), fields6, cherry_core.keyword("created-at"), /* @__PURE__ */ new Date(), cherry_core.keyword("content-item-count"), 0);
};
var content_type_fields_form = function(p__199) {
  const map__2001 = p__199;
  const map__2002 = cherry_core.__destructure_map.call(null, map__2001);
  const anti_forgery_token3 = cherry_core.get.call(null, map__2002, cherry_core.keyword("anti-forgery-token"));
  const content_type4 = cherry_core.get.call(null, map__2002, cherry_core.keyword("content-type"));
  const class$5 = cherry_core.get.call(null, map__2002, cherry_core.keyword("class"));
  const vec__2016 = use_dag.call(null, cherry_core.vector(cherry_core.keyword("all-content-types-page/selection")));
  const map__2047 = cherry_core.nth.call(null, vec__2016, 0, null);
  const map__2048 = cherry_core.__destructure_map.call(null, map__2047);
  const selection9 = cherry_core.get.call(null, map__2048, cherry_core.keyword("all-content-types-page/selection"));
  const push10 = cherry_core.nth.call(null, vec__2016, 1, null);
  const http_opts11 = cherry_core.array_map(cherry_core.keyword("anti-forgery-token"), anti_forgery_token3);
  const update_field12 = useCallback.call(null, function(content_type_id, field_key) {
    return html.debounce.call(null, function(e, content_type, field) {
      e.preventDefault();
      const updated_field13 = cherry_core.assoc.call(null, field, field_key, e.target.value);
      const http_opts_SINGLEQUOTE_14 = cherry_core.assoc.call(null, http_opts11, cherry_core.keyword("body"), cherry_core.merge.call(null, cherry_core.array_map(cherry_core.keyword("content-type-id"), content_type_id, cherry_core.keyword("content-field-id"), cherry_core.keyword("id").call(null, updated_field13)), cherry_core.select_keys.call(null, updated_field13, cherry_core.vector(cherry_core.keyword("name"), cherry_core.keyword("type"), cherry_core.keyword("rank")))));
      return http.post.call(null, "/api/update-content-type-field", http_opts_SINGLEQUOTE_14);
    }, html.default_debounce_timeout_ms);
  });
  const delete_field15 = function(e, content_type, field) {
    e.preventDefault();
    if (cherry_core.truth_.call(null, confirm(cherry_core.str.call(null, 'Are you sure you want to delete "', cherry_core.keyword("name").call(null, field), '"?')))) {
      const http_opts_SINGLEQUOTE_16 = cherry_core.assoc.call(null, http_opts11, cherry_core.keyword("body"), cherry_core.array_map(cherry_core.keyword("content-type-id"), cherry_core.keyword("id").call(null, content_type), cherry_core.keyword("content-field-id"), cherry_core.keyword("id").call(null, field)));
      return http.post.call(null, "/api/delete-content-type-field", http_opts_SINGLEQUOTE_16);
    }
  };
  const update_field_name17 = update_field12.call(null, cherry_core.keyword("id").call(null, content_type4), cherry_core.keyword("name"));
  const update_field_type18 = update_field12.call(null, cherry_core.keyword("id").call(null, content_type4), cherry_core.keyword("type"));
  return cherry_core.vector(cherry_core.keyword("form"), cherry_core.array_map(cherry_core.keyword("method"), "post", cherry_core.keyword("class"), class$5), cherry_core.vector(cherry_core.keyword("input"), cherry_core.array_map(cherry_core.keyword("id"), "__anti-forgery-token", cherry_core.keyword("name"), "__anti-forgery-token", cherry_core.keyword("type"), "hidden", cherry_core.keyword("value"), anti_forgery_token3)), cherry_core.vector(cherry_core.keyword("input"), cherry_core.array_map(cherry_core.keyword("type"), "hidden", cherry_core.keyword("name"), "content-type-id", cherry_core.keyword("value"), cherry_core.keyword("id").call(null, content_type4))), cherry_core.vector(cherry_core.keyword("input"), cherry_core.array_map(cherry_core.keyword("type"), "hidden", cherry_core.keyword("name"), "content-type-name", cherry_core.keyword("value"), cherry_core.keyword("name").call(null, content_type4))), cherry_core.vector(cherry_core.keyword("div"), (() => {
    const iter__23116__auto__19 = function iter__205(s__206) {
      return new cherry_core.LazySeq(null, function() {
        let s__20620 = s__206;
        while (true) {
          const temp__23033__auto__21 = cherry_core.seq.call(null, s__20620);
          if (cherry_core.truth_.call(null, temp__23033__auto__21)) {
            const s__20622 = temp__23033__auto__21;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__20622))) {
              const c__23114__auto__23 = cherry_core.chunk_first.call(null, s__20622);
              const size__23115__auto__24 = cherry_core.count.call(null, c__23114__auto__23);
              const b__20825 = cherry_core.chunk_buffer.call(null, size__23115__auto__24);
              if ((() => {
                let i__20726 = 0;
                while (true) {
                  if (i__20726 < size__23115__auto__24) {
                    const field27 = cherry_core._nth.call(null, c__23114__auto__23, i__20726);
                    cherry_core.chunk_append.call(null, b__20825, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "mb-2 pb-2 pt-2 flex items-center group", cherry_core.keyword("key"), cherry_core.keyword("id").call(null, field27)), cherry_core.vector(cherry_core.keyword("label"), cherry_core.array_map(cherry_core.keyword("for"), "field-name")), cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("on-focus"), function(_) {
                      return push10.call(null, cherry_core.keyword("all-content-types-page/select-content-type-field"), cherry_core.array_map(cherry_core.keyword("content-type"), content_type4, cherry_core.keyword("content-type-field"), field27));
                    }, cherry_core.keyword("size"), cherry_core.keyword("text-medium"), cherry_core.keyword("class"), cherry_core.str.call(null, "px-2 py-1 font-semibold border border-gray-200 ", "rounded-[6px] mr-4 max-w-xl"), cherry_core.keyword("placeholder"), "Field Name", cherry_core.keyword("name"), cherry_core.keyword("field-name"), cherry_core.keyword("readonly"), true, cherry_core.keyword("default-value"), cherry_core.keyword("name").call(null, field27))), cherry_core.vector(cherry_core.keyword("label"), cherry_core.array_map(cherry_core.keyword("for"), "field-type")), cherry_core.vector(html.select, cherry_core.array_map(cherry_core.keyword("name"), cherry_core.keyword("field-type"), cherry_core.keyword("on-focus"), function(_) {
                      return push10.call(null, cherry_core.keyword("all-content-types-page/select-content-type-field"), cherry_core.array_map(cherry_core.keyword("content-type"), content_type4, cherry_core.keyword("content-type-field"), field27));
                    }, cherry_core.keyword("default-value"), cherry_core.keyword("type").call(null, field27)), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("text"), cherry_core.keyword("value"), "text"), "Text"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("text-lg"), cherry_core.keyword("value"), "text-lg"), "Text (Large)"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("number"), cherry_core.keyword("value"), "number"), "Number"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("choice"), cherry_core.keyword("value"), "choice"), "Choice"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("datetime"), cherry_core.keyword("value"), "datetime"), "Date/Time"))));
                    let G__28 = cherry_core.unchecked_inc.call(null, i__20726);
                    i__20726 = G__28;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__20825), iter__205.call(null, cherry_core.chunk_rest.call(null, s__20622)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__20825), null);
              }
            } else {
              const field29 = cherry_core.first.call(null, s__20622);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "mb-2 pb-2 pt-2 flex items-center group", cherry_core.keyword("key"), cherry_core.keyword("id").call(null, field29)), cherry_core.vector(cherry_core.keyword("label"), cherry_core.array_map(cherry_core.keyword("for"), "field-name")), cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("on-focus"), function(_) {
                return push10.call(null, cherry_core.keyword("all-content-types-page/select-content-type-field"), cherry_core.array_map(cherry_core.keyword("content-type"), content_type4, cherry_core.keyword("content-type-field"), field29));
              }, cherry_core.keyword("size"), cherry_core.keyword("text-medium"), cherry_core.keyword("class"), cherry_core.str.call(null, "px-2 py-1 font-semibold border border-gray-200 ", "rounded-[6px] mr-4 max-w-xl"), cherry_core.keyword("placeholder"), "Field Name", cherry_core.keyword("name"), cherry_core.keyword("field-name"), cherry_core.keyword("readonly"), true, cherry_core.keyword("default-value"), cherry_core.keyword("name").call(null, field29))), cherry_core.vector(cherry_core.keyword("label"), cherry_core.array_map(cherry_core.keyword("for"), "field-type")), cherry_core.vector(html.select, cherry_core.array_map(cherry_core.keyword("name"), cherry_core.keyword("field-type"), cherry_core.keyword("on-focus"), function(_) {
                return push10.call(null, cherry_core.keyword("all-content-types-page/select-content-type-field"), cherry_core.array_map(cherry_core.keyword("content-type"), content_type4, cherry_core.keyword("content-type-field"), field29));
              }, cherry_core.keyword("default-value"), cherry_core.keyword("type").call(null, field29)), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("text"), cherry_core.keyword("value"), "text"), "Text"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("text-lg"), cherry_core.keyword("value"), "text-lg"), "Text (Large)"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("number"), cherry_core.keyword("value"), "number"), "Number"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("choice"), cherry_core.keyword("value"), "choice"), "Choice"), cherry_core.vector(cherry_core.keyword("option"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("datetime"), cherry_core.keyword("value"), "datetime"), "Date/Time"))), iter__205.call(null, cherry_core.rest.call(null, s__20622)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__19.call(null, cherry_core.sort_by.call(null, cherry_core.keyword("rank"), cherry_core.keyword("fields").call(null, content_type4)));
  })()));
};
var index_by = function(f, coll) {
  return cherry_core.into.call(null, cherry_core.array_map(), cherry_core.map.call(null, function(v) {
    return cherry_core.vector(f.call(null, v), v);
  }, coll));
};
var field_config = cherry_core.vector(cherry_core.array_map(cherry_core.keyword("label"), "Text", cherry_core.keyword("description"), "Ask for text with optional formatting."), cherry_core.array_map(cherry_core.keyword("label"), "Date and Time", cherry_core.keyword("description"), "Ask for a date and time with a date picker."), cherry_core.array_map(cherry_core.keyword("label"), "Number", cherry_core.keyword("description"), "Ask for a whole number or a decimal."), cherry_core.array_map(cherry_core.keyword("label"), "Media", cherry_core.keyword("description"), "Ask for an image or video."), cherry_core.array_map(cherry_core.keyword("label"), "Choice", cherry_core.keyword("description"), "Ask for a choice between multiple options."), cherry_core.array_map(cherry_core.keyword("label"), "Group", cherry_core.keyword("description"), "Combine multiple fields into a group."));
var all_content_types_page = function(p__209) {
  const map__2101 = p__209;
  const map__2102 = cherry_core.__destructure_map.call(null, map__2101);
  const content_types3 = cherry_core.get.call(null, map__2102, cherry_core.keyword("content-types"));
  const anti_forgery_token4 = cherry_core.get.call(null, map__2102, cherry_core.keyword("anti-forgery-token"));
  const vec__2115 = use_dag.call(null, cherry_core.vector(cherry_core.keyword("all-content-types-page/selection")));
  const map__2146 = cherry_core.nth.call(null, vec__2115, 0, null);
  const map__2147 = cherry_core.__destructure_map.call(null, map__2146);
  const selection8 = cherry_core.get.call(null, map__2147, cherry_core.keyword("all-content-types-page/selection"));
  const push9 = cherry_core.nth.call(null, vec__2115, 1, null);
  const _10 = useEffect.call(null, function() {
    return push9.call(null, cherry_core.keyword("init"), cherry_core.array_map(cherry_core.keyword("content-types"), content_types3));
  }, []);
  const content_types11 = cherry_core.map.call(null, function(_PERCENT_1) {
    return cherry_core.update.call(null, _PERCENT_1, cherry_core.keyword("created-at"), Date.parse);
  }, content_types3);
  const content_type_index12 = admin_impl.index_by.call(null, cherry_core.keyword("id"), content_types11);
  const http_opts13 = cherry_core.array_map(cherry_core.keyword("anti-forgery-token"), anti_forgery_token4);
  const set_content_type_name14 = useCallback.call(null, html.debounce.call(null, function(e, content_type) {
    const value15 = e.target.value;
    const content_type_SINGLEQUOTE_16 = cherry_core.select_keys.call(null, cherry_core.assoc.call(null, content_type, cherry_core.keyword("name"), value15), cherry_core.vector(cherry_core.keyword("id"), cherry_core.keyword("name")));
    const http_opts_SINGLEQUOTE_17 = cherry_core.assoc.call(null, http_opts13, cherry_core.keyword("body"), cherry_core.array_map(cherry_core.keyword("content-type"), content_type_SINGLEQUOTE_16));
    return http.post.call(null, "/api/update-content-type", http_opts_SINGLEQUOTE_17);
  }, html.default_debounce_timeout_ms));
  const new_content_type18 = function(e) {
    const content_type19 = __GT_content_type.call(null, cherry_core.array_map(cherry_core.keyword("name"), "New Content Type", cherry_core.keyword("slug"), "new-content-type", cherry_core.keyword("fields"), cherry_core.vector()));
    e.preventDefault();
    return http.post.call(null, "/api/new-content-type", http_opts13);
  };
  const delete_content_type20 = function(e, content_type) {
    if (cherry_core.truth_.call(null, confirm(cherry_core.str.call(null, 'Are you sure you want to delete "', cherry_core.keyword("name").call(null, content_type), '"?')))) {
      const http_opts_SINGLEQUOTE_21 = cherry_core.assoc.call(null, http_opts13, cherry_core.keyword("body"), cherry_core.array_map(cherry_core.keyword("content-type-id"), cherry_core.keyword("id").call(null, content_type)));
      e.preventDefault();
      return http.post.call(null, "/api/delete-content-type", http_opts_SINGLEQUOTE_21);
    }
  };
  const new_field22 = function(e, content_type, field) {
    e.preventDefault();
    const rank23 = cherry_core.apply.call(null, cherry_core.max, 0, cherry_core.map.call(null, cherry_core.keyword("rank"), cherry_core.keyword("fields").call(null, content_type))) + 1;
    const field_SINGLEQUOTE_24 = cherry_core.assoc.call(null, __GT_field.call(null, field), cherry_core.keyword("rank"), rank23);
    const http_opts_SINGLEQUOTE_25 = cherry_core.assoc.call(null, http_opts13, cherry_core.keyword("body"), cherry_core.array_map(cherry_core.keyword("content-type-id"), cherry_core.keyword("id").call(null, content_type)));
    return http.post.call(null, "/api/new-content-type-field", http_opts_SINGLEQUOTE_25);
  };
  const content_types26 = cherry_core.sort_by.call(null, cherry_core.keyword("created-at"), cherry_core._GT_, cherry_core.vals.call(null, content_type_index12));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex", cherry_core.keyword("onClick"), function(e) {
    if (cherry_core.truth_.call(null, e.target.closest("[data-content-type-id]"))) {
      return null;
    } else {
      return push9.call(null, cherry_core.keyword("all-content-types-page/clear-selection"));
    }
  }), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex-grow"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4 pr-[384px]"), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("class"), "pb-4", cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center"), cherry_core.vector(cherry_core.keyword("div"), "Content Types")), cherry_core.keyword("content"), cherry_core.vector(admin_impl.content_item_counts, cherry_core.array_map(cherry_core.keyword("content-types"), content_types26)))), (() => {
    const iter__23116__auto__27 = function iter__215(s__216) {
      return new cherry_core.LazySeq(null, function() {
        let s__21628 = s__216;
        while (true) {
          const temp__23033__auto__29 = cherry_core.seq.call(null, s__21628);
          if (cherry_core.truth_.call(null, temp__23033__auto__29)) {
            const s__21630 = temp__23033__auto__29;
            if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__21630))) {
              const c__23114__auto__31 = cherry_core.chunk_first.call(null, s__21630);
              const size__23115__auto__32 = cherry_core.count.call(null, c__23114__auto__31);
              const b__21833 = cherry_core.chunk_buffer.call(null, size__23115__auto__32);
              if ((() => {
                let i__21734 = 0;
                while (true) {
                  if (i__21734 < size__23115__auto__32) {
                    const content_type35 = cherry_core._nth.call(null, c__23114__auto__31, i__21734);
                    cherry_core.chunk_append.call(null, b__21833, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "pb-4", cherry_core.keyword("key"), cherry_core.keyword("id").call(null, content_type35), cherry_core.keyword("data-content-type-id"), cherry_core.keyword("id").call(null, content_type35)), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("on-click"), function(e) {
                      if (cherry_core.truth_.call(null, (() => {
                        const or__23431__auto__36 = e.target.closest("input");
                        if (cherry_core.truth_.call(null, or__23431__auto__36)) {
                          return or__23431__auto__36;
                        } else {
                          return e.target.closest("select");
                        }
                      })())) {
                        return null;
                      } else {
                        return push9.call(null, cherry_core.keyword("all-content-types-page/select-content-type"), cherry_core.array_map(cherry_core.keyword("content-type"), content_type35));
                      }
                    }, cherry_core.keyword("selected"), (() => {
                      const and__23449__auto__37 = cherry_core.not.call(null, cherry_core.keyword("content-type-field").call(null, selection8));
                      if (cherry_core.truth_.call(null, and__23449__auto__37)) {
                        return cherry_core._EQ_.call(null, cherry_core.get_in.call(null, selection8, cherry_core.vector(cherry_core.keyword("content-type"), cherry_core.keyword("id"))), cherry_core.keyword("id").call(null, content_type35));
                      } else {
                        return and__23449__auto__37;
                      }
                    })(), cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center group"), cherry_core.vector(cherry_core.keyword("h3"), cherry_core.array_map(cherry_core.keyword("class"), "text-2xl"), cherry_core.keyword("name").call(null, content_type35))), cherry_core.keyword("content"), cherry_core.truth_.call(null, cherry_core.seq.call(null, cherry_core.keyword("fields").call(null, content_type35))) ? cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("id").call(null, content_type35)), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-start")), cherry_core.vector(content_type_fields_form, cherry_core.array_map(cherry_core.keyword("content-type"), content_type35, cherry_core.keyword("anti-forgery-token"), anti_forgery_token4))) : null))));
                    let G__38 = cherry_core.unchecked_inc.call(null, i__21734);
                    i__21734 = G__38;
                    continue;
                  } else {
                    return true;
                  }
                  ;
                  break;
                }
              })()) {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__21833), iter__215.call(null, cherry_core.chunk_rest.call(null, s__21630)));
              } else {
                return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__21833), null);
              }
            } else {
              const content_type39 = cherry_core.first.call(null, s__21630);
              return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "pb-4", cherry_core.keyword("key"), cherry_core.keyword("id").call(null, content_type39), cherry_core.keyword("data-content-type-id"), cherry_core.keyword("id").call(null, content_type39)), cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("on-click"), function(e) {
                if (cherry_core.truth_.call(null, (() => {
                  const or__23431__auto__40 = e.target.closest("input");
                  if (cherry_core.truth_.call(null, or__23431__auto__40)) {
                    return or__23431__auto__40;
                  } else {
                    return e.target.closest("select");
                  }
                })())) {
                  return null;
                } else {
                  return push9.call(null, cherry_core.keyword("all-content-types-page/select-content-type"), cherry_core.array_map(cherry_core.keyword("content-type"), content_type39));
                }
              }, cherry_core.keyword("selected"), (() => {
                const and__23449__auto__41 = cherry_core.not.call(null, cherry_core.keyword("content-type-field").call(null, selection8));
                if (cherry_core.truth_.call(null, and__23449__auto__41)) {
                  return cherry_core._EQ_.call(null, cherry_core.get_in.call(null, selection8, cherry_core.vector(cherry_core.keyword("content-type"), cherry_core.keyword("id"))), cherry_core.keyword("id").call(null, content_type39));
                } else {
                  return and__23449__auto__41;
                }
              })(), cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center group"), cherry_core.vector(cherry_core.keyword("h3"), cherry_core.array_map(cherry_core.keyword("class"), "text-2xl"), cherry_core.keyword("name").call(null, content_type39))), cherry_core.keyword("content"), cherry_core.truth_.call(null, cherry_core.seq.call(null, cherry_core.keyword("fields").call(null, content_type39))) ? cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("id").call(null, content_type39)), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-start")), cherry_core.vector(content_type_fields_form, cherry_core.array_map(cherry_core.keyword("content-type"), content_type39, cherry_core.keyword("anti-forgery-token"), anti_forgery_token4))) : null))), iter__215.call(null, cherry_core.rest.call(null, s__21630)));
            }
          }
          ;
          break;
        }
      }, null, null);
    };
    return iter__23116__auto__27.call(null, content_types26);
  })())), (() => {
    const field42 = function(p__219) {
      const map__22043 = p__219;
      const map__22044 = cherry_core.__destructure_map.call(null, map__22043);
      const label45 = cherry_core.get.call(null, map__22044, cherry_core.keyword("label"));
      const description46 = cherry_core.get.call(null, map__22044, cherry_core.keyword("description"));
      return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "border border-gray-200 rounded-[6px] p-2 mb-4 bg-gray-50 cursor-move", cherry_core.keyword("draggable"), true), cherry_core.vector(cherry_core.keyword("h4"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold"), label45), cherry_core.vector(cherry_core.keyword("p"), cherry_core.array_map(cherry_core.keyword("class"), "text-sm text-gray-500"), description46));
    };
    return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4 pl-2 w-[376px] fixed right-0 bottom-0 top-12"), cherry_core.truth_.call(null, selection8) ? cherry_core.truth_.call(null, cherry_core.keyword("content-type-field").call(null, selection8)) ? cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("class"), "h-full", cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("h3"), cherry_core.array_map(cherry_core.keyword("class"), "text-2xl font-app-serif font-semibold"), cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "italic"), "Field: "), cherry_core.get_in.call(null, selection8, cherry_core.vector(cherry_core.keyword("content-type-field"), cherry_core.keyword("name")))), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "mb-8"), cherry_core.vector(cherry_core.keyword("h2"), cherry_core.array_map(cherry_core.keyword("class"), "text-xl font-app-serif font-semibold"), "Info")), cherry_core.vector(cherry_core.keyword("div"), cherry_core.vector(cherry_core.keyword("h2"), cherry_core.array_map(cherry_core.keyword("class"), "text-xl font-app-serif font-semibold"), "Conditions"), cherry_core.vector(cherry_core.keyword("p"), "foo"))))) : cherry_core.truth_.call(null, cherry_core.keyword("content-type").call(null, selection8)) ? cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("class"), "h-full", cherry_core.keyword("title"), cherry_core.vector(cherry_core.keyword("h3"), cherry_core.array_map(cherry_core.keyword("class"), "text-2xl font-app-serif font-semibold"), cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "italic"), "Content Type: "), cherry_core.get_in.call(null, selection8, cherry_core.vector(cherry_core.keyword("content-type"), cherry_core.keyword("name")))), cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.vector(cherry_core.keyword("h4"), cherry_core.array_map(cherry_core.keyword("class"), "text-xl font-app-serif font-semibold mb-4"), "Add Field"), cherry_core.vector(cherry_core.keyword("div"), (() => {
      const iter__23116__auto__47 = function iter__221(s__222) {
        return new cherry_core.LazySeq(null, function() {
          let s__22248 = s__222;
          while (true) {
            const temp__23033__auto__49 = cherry_core.seq.call(null, s__22248);
            if (cherry_core.truth_.call(null, temp__23033__auto__49)) {
              const s__22250 = temp__23033__auto__49;
              if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__22250))) {
                const c__23114__auto__51 = cherry_core.chunk_first.call(null, s__22250);
                const size__23115__auto__52 = cherry_core.count.call(null, c__23114__auto__51);
                const b__22453 = cherry_core.chunk_buffer.call(null, size__23115__auto__52);
                if ((() => {
                  let i__22354 = 0;
                  while (true) {
                    if (i__22354 < size__23115__auto__52) {
                      const n55 = cherry_core._nth.call(null, c__23114__auto__51, i__22354);
                      cherry_core.chunk_append.call(null, b__22453, field42.call(null, n55));
                      let G__56 = cherry_core.unchecked_inc.call(null, i__22354);
                      i__22354 = G__56;
                      continue;
                    } else {
                      return true;
                    }
                    ;
                    break;
                  }
                })()) {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__22453), iter__221.call(null, cherry_core.chunk_rest.call(null, s__22250)));
                } else {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__22453), null);
                }
              } else {
                const n57 = cherry_core.first.call(null, s__22250);
                return cherry_core.cons.call(null, field42.call(null, n57), iter__221.call(null, cherry_core.rest.call(null, s__22250)));
              }
            }
            ;
            break;
          }
        }, null, null);
      };
      return iter__23116__auto__47.call(null, field_config);
    })())))) : null : cherry_core.vector(admin_impl.box, cherry_core.array_map(cherry_core.keyword("class"), "h-full", cherry_core.keyword("content"), cherry_core.vector(cherry_core.keyword("div"), cherry_core.vector(cherry_core.keyword("ul"), cherry_core.array_map(cherry_core.keyword("class"), "text-sm mb-8 list-[disc] pl-4"), cherry_core.vector(cherry_core.keyword("li"), cherry_core.array_map(cherry_core.keyword("class"), "mb-2"), "Drag a field to the left to add it to a content type."), cherry_core.vector(cherry_core.keyword("li"), "Double-click a field to add it to the selected content type.")), cherry_core.vector(cherry_core.keyword("div"), (() => {
      const iter__23116__auto__58 = function iter__225(s__226) {
        return new cherry_core.LazySeq(null, function() {
          let s__22659 = s__226;
          while (true) {
            const temp__23033__auto__60 = cherry_core.seq.call(null, s__22659);
            if (cherry_core.truth_.call(null, temp__23033__auto__60)) {
              const s__22661 = temp__23033__auto__60;
              if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__22661))) {
                const c__23114__auto__62 = cherry_core.chunk_first.call(null, s__22661);
                const size__23115__auto__63 = cherry_core.count.call(null, c__23114__auto__62);
                const b__22864 = cherry_core.chunk_buffer.call(null, size__23115__auto__63);
                if ((() => {
                  let i__22765 = 0;
                  while (true) {
                    if (i__22765 < size__23115__auto__63) {
                      const n66 = cherry_core._nth.call(null, c__23114__auto__62, i__22765);
                      cherry_core.chunk_append.call(null, b__22864, field42.call(null, n66));
                      let G__67 = cherry_core.unchecked_inc.call(null, i__22765);
                      i__22765 = G__67;
                      continue;
                    } else {
                      return true;
                    }
                    ;
                    break;
                  }
                })()) {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__22864), iter__225.call(null, cherry_core.chunk_rest.call(null, s__22661)));
                } else {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__22864), null);
                }
              } else {
                const n68 = cherry_core.first.call(null, s__22661);
                return cherry_core.cons.call(null, field42.call(null, n68), iter__225.call(null, cherry_core.rest.call(null, s__22661)));
              }
            }
            ;
            break;
          }
        }, null, null);
      };
      return iter__23116__auto__58.call(null, field_config);
    })())))));
  })());
};
html.add_element.call(null, cherry_core.keyword("all-content-types-page"), admin_impl.wrap_component.call(null, all_content_types_page));
var months = cherry_core.vector("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December");
var format_datetime = function(date) {
  const month_idx1 = date.getMonth();
  const day2 = date.getDate();
  const year3 = date.getFullYear();
  const hours4 = date.getHours();
  const minutes5 = date.getMinutes();
  const month6 = cherry_core.get.call(null, months, month_idx1);
  const meridiem7 = hours4 < 12 ? "AM" : "PM";
  const h8 = cherry_core.mod.call(null, hours4, 12);
  const display_hours9 = h8 === 0 ? 12 : h8;
  const display_minutes10 = minutes5 < 10 ? cherry_core.str.call(null, "0", minutes5) : cherry_core.str.call(null, minutes5);
  return cherry_core.str.call(null, month6, " ", day2, ", ", year3, " ", display_hours9, ":", display_minutes10, " ", meridiem7);
};
var columns = cherry_core.vector(cherry_core.array_map(cherry_core.keyword("name"), "Title", cherry_core.keyword("value"), function(p__229) {
  const map__2301 = p__229;
  const map__2302 = cherry_core.__destructure_map.call(null, map__2301);
  const fields3 = cherry_core.get.call(null, map__2302, cherry_core.keyword("fields"));
  return cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold"), cherry_core.get.call(null, fields3, "Title"));
}), cherry_core.array_map(cherry_core.keyword("name"), "Author", cherry_core.keyword("value"), function(p__231) {
  const map__2324 = p__231;
  const map__2325 = cherry_core.__destructure_map.call(null, map__2324);
  const created_by6 = cherry_core.get.call(null, map__2325, cherry_core.keyword("created-by"));
  const map__2337 = created_by6;
  const map__2338 = cherry_core.__destructure_map.call(null, map__2337);
  const username9 = cherry_core.get.call(null, map__2338, cherry_core.keyword("username"));
  return cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "font-semibold"), username9);
}), cherry_core.array_map(cherry_core.keyword("name"), "Date", cherry_core.keyword("value"), function(p__234) {
  const map__23510 = p__234;
  const map__23511 = cherry_core.__destructure_map.call(null, map__23510);
  const created_at12 = cherry_core.get.call(null, map__23511, cherry_core.keyword("created-at"));
  const updated_at13 = cherry_core.get.call(null, map__23511, cherry_core.keyword("updated-at"));
  const G__23614 = (() => {
    const or__23431__auto__15 = updated_at13;
    if (cherry_core.truth_.call(null, or__23431__auto__15)) {
      return or__23431__auto__15;
    } else {
      return created_at12;
    }
  })();
  const G__23616 = G__23614 == null ? null : new Date(G__23614);
  if (G__23616 == null) {
    return null;
  } else {
    return format_datetime.call(null, G__23616);
  }
}));
var single_content_type_page = function(p__237) {
  const map__2381 = p__237;
  const map__2382 = cherry_core.__destructure_map.call(null, map__2381);
  const content_type3 = cherry_core.get.call(null, map__2382, cherry_core.keyword("content-type"));
  const content_items4 = cherry_core.get.call(null, map__2382, cherry_core.keyword("content-items"));
  const anti_forgery_token5 = cherry_core.get.call(null, map__2382, cherry_core.keyword("anti-forgery-token"));
  const http_opts6 = cherry_core.array_map(cherry_core.keyword("anti-forgery-token"), anti_forgery_token5);
  const content_items7 = cherry_core.map.call(null, function(content_item) {
    return cherry_core.update.call(null, content_item, cherry_core.keyword("fields"), function(_PERCENT_1) {
      return cherry_core.update_keys.call(null, _PERCENT_1, cherry_core.name);
    });
  }, content_items4);
  const delete_row8 = function(_, content_item) {
    const body9 = cherry_core.array_map(cherry_core.keyword("content-item-id"), cherry_core.keyword("id").call(null, content_item));
    const on_complete10 = function(_2, err) {
      if (cherry_core.truth_.call(null, err)) {
        return cherry_core.println.call(null, err);
      }
    };
    const http_opts_SINGLEQUOTE_11 = cherry_core.merge.call(null, http_opts6, cherry_core.array_map(cherry_core.keyword("body"), body9, cherry_core.keyword("on-complete"), on_complete10));
    return http.post.call(null, "/api/delete-content-item", http_opts_SINGLEQUOTE_11);
  };
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4"), cherry_core.vector(admin_impl.table, cherry_core.array_map(cherry_core.keyword("title"), cherry_core.keyword("name").call(null, content_type3), cherry_core.keyword("columns"), columns, cherry_core.keyword("rows"), cherry_core.map.call(null, function(_PERCENT_1) {
    return cherry_core.assoc.call(null, _PERCENT_1, cherry_core.keyword("content-type"), content_type3);
  }, content_items7), cherry_core.keyword("delete-row"), delete_row8)));
};
html.add_element.call(null, cherry_core.keyword("single-content-type-page"), admin_impl.wrap_component.call(null, single_content_type_page));
var editor_impl = function(p__239) {
  const map__2401 = p__239;
  const map__2402 = cherry_core.__destructure_map.call(null, map__2401);
  const props3 = map__2402;
  const on_start4 = cherry_core.get.call(null, map__2402, cherry_core.keyword("on-start"));
  return null;
};
var editor = function(props) {
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "editor bg-white"), cherry_core.vector(editor_impl, props));
};
var quill_get_semantic_html = function(quill) {
  return quill.getSemanticHTML();
};
var title_field_id = cherry_core.uuid.call(null, "cd334826-1ec6-4906-8e7f-16ece1865faf");
var slug_field_id = cherry_core.uuid.call(null, "6bd0ff7a-b720-4972-b98a-2aa85d179357");
var success_alert = function(p__241) {
  const map__2421 = p__241;
  const map__2422 = cherry_core.__destructure_map.call(null, map__2421);
  const message3 = cherry_core.get.call(null, map__2422, cherry_core.keyword("message"));
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "flex items-center p-4 mb-4 text-sm text-green-800 border border-green-300 rounded-lg bg-green-50 dark:bg-gray-800 dark:text-green-400 dark:border-green-800", cherry_core.keyword("role"), "alert"), cherry_core.vector(cherry_core.keyword("svg"), cherry_core.array_map(cherry_core.keyword("class"), "flex-shrink-0 inline w-4 h-4 me-3", cherry_core.keyword("aria-hidden"), "true", cherry_core.keyword("xmlns"), "http://www.w3.org/2000/svg", cherry_core.keyword("fill"), "currentColor", cherry_core.keyword("viewBox"), "0 0 20 20"), cherry_core.vector(cherry_core.keyword("path"), cherry_core.array_map(cherry_core.keyword("d"), "M10 .5a9.5 9.5 0 1 0 9.5 9.5A9.51 9.51 0 0 0 10 .5ZM9.5 4a1.5 1.5 0 1 1 0 3 1.5 1.5 0 0 1 0-3ZM12 15H8a1 1 0 0 1 0-2h1v-3H8a1 1 0 0 1 0-2h2a1 1 0 0 1 1 1v4h1a1 1 0 0 1 0 2Z"))), cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "sr-only"), "Info"), cherry_core.vector(cherry_core.keyword("div"), message3));
};
var content_type_new_item_form = function(p__243) {
  const map__2441 = p__243;
  const map__2442 = cherry_core.__destructure_map.call(null, map__2441);
  const anti_forgery_token3 = cherry_core.get.call(null, map__2442, cherry_core.keyword("anti-forgery-token"));
  const submit_form_url4 = cherry_core.get.call(null, map__2442, cherry_core.keyword("submit-form-url"));
  const submitting_button_text5 = cherry_core.get.call(null, map__2442, cherry_core.keyword("submitting-button-text"));
  const content_item6 = cherry_core.get.call(null, map__2442, cherry_core.keyword("content-item"));
  const title7 = cherry_core.get.call(null, map__2442, cherry_core.keyword("title"));
  const submit_button_text8 = cherry_core.get.call(null, map__2442, cherry_core.keyword("submit-button-text"));
  const content_type9 = cherry_core.get.call(null, map__2442, cherry_core.keyword("content-type"));
  const site_base_url10 = cherry_core.get.call(null, map__2442, cherry_core.keyword("site-base-url"));
  const submit_button_class11 = cherry_core.get.call(null, map__2442, cherry_core.keyword("submit-button-class"));
  const http_opts12 = cherry_core.array_map(cherry_core.keyword("anti-forgery-token"), anti_forgery_token3);
  const submitting13 = false;
  const content_item14 = cherry_core.array_map(cherry_core.keyword("form-fields"), (() => {
    const or__23431__auto__15 = cherry_core.keyword("document").call(null, content_item6);
    if (cherry_core.truth_.call(null, or__23431__auto__15)) {
      return or__23431__auto__15;
    } else {
      return cherry_core.array_map();
    }
  })());
  const messages16 = cherry_core.vector();
  const add_editor17 = function(_field_id, _e) {
    return null;
  };
  const add_message18 = function(_message) {
    return null;
  };
  const update_field19 = function(e, field_id) {
    const value20 = e.target.value;
    return null;
  };
  const submit_form21 = function(e, p__245) {
    const map__24622 = p__245;
    const map__24623 = cherry_core.__destructure_map.call(null, map__24622);
    const content_item_slug24 = cherry_core.get.call(null, map__24623, cherry_core.keyword("content-item-slug"));
    e.preventDefault();
    const v25 = null;
    const form_fields26 = cherry_core.get_in.call(null, v25, cherry_core.vector(cherry_core.keyword("content-item"), cherry_core.keyword("form-fields")));
    const editor_values27 = cherry_core.update_vals.call(null, cherry_core.keyword("editors").call(null, v25), quill_get_semantic_html);
    const document28 = cherry_core.merge.call(null, form_fields26, editor_values27, cherry_core.array_map(slug_field_id, content_item_slug24));
    const body29 = (() => {
      const G__24730 = cherry_core.array_map(cherry_core.keyword("content-type-id"), cherry_core.keyword("id").call(null, content_type9), cherry_core.keyword("document"), document28);
      if (cherry_core.truth_.call(null, content_item14)) {
        return cherry_core.assoc.call(null, G__24730, cherry_core.keyword("content-item-id"), cherry_core.keyword("id").call(null, content_item14));
      } else {
        return G__24730;
      }
    })();
    const on_complete31 = function(res, err) {
      cherry_core.println.call(null, res);
      if (cherry_core.truth_.call(null, err)) {
        return cherry_core.println.call(null, err);
      } else {
        if (cherry_core.truth_.call(null, content_item14)) {
          return add_message18.call(null, cherry_core.vector(success_alert, cherry_core.array_map(cherry_core.keyword("message"), cherry_core.vector(cherry_core.keyword("span.font-semibold"), inflections.capitalize.call(null, inflections.singular.call(null, cherry_core.name.call(null, cherry_core.keyword("slug").call(null, content_type9)))), " updated!"))));
        } else {
          return window.location = cherry_core.str.call(null, "/admin/content-types/", cherry_core.name.call(null, cherry_core.keyword("slug").call(null, content_type9)), "/", cherry_core.keyword("content-item-slug").call(null, res));
        }
      }
    };
    const http_opts_SINGLEQUOTE_32 = cherry_core.assoc.call(null, cherry_core.assoc.call(null, http_opts12, cherry_core.keyword("body"), body29), cherry_core.keyword("on-complete"), on_complete31);
    return http.post.call(null, submit_form_url4, http_opts_SINGLEQUOTE_32);
  };
  const router33 = null;
  return cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "p-4 pt-0"), admin_impl.box.call(null, cherry_core.array_map(cherry_core.keyword("title"), title7, cherry_core.keyword("content"), (() => {
    const content_item_slug34 = (() => {
      const or__23431__auto__35 = cherry_core.get_in.call(null, content_item14, cherry_core.vector(cherry_core.keyword("fields"), "Slug"));
      if (cherry_core.truth_.call(null, or__23431__auto__35)) {
        return or__23431__auto__35;
      } else {
        const G__24836 = cherry_core.get_in.call(null, content_item14, cherry_core.vector(cherry_core.keyword("form-fields"), title_field_id));
        if (G__24836 == null) {
          return null;
        } else {
          return __GT_slug.call(null, G__24836);
        }
      }
    })();
    const path_params37 = cherry_core.array_map(cherry_core.keyword("content-type-slug"), cherry_core.keyword("slug").call(null, content_type9), cherry_core.keyword("content-item-slug"), content_item_slug34);
    const match38 = null;
    const permalink_url39 = cherry_core.truth_.call(null, str.blank_QMARK_.call(null, content_item_slug34)) ? null : cherry_core.str.call(null, site_base_url10);
    const fields40 = cherry_core.map_indexed.call(null, cherry_core.vector, cherry_core.remove.call(null, cherry_core.comp.call(null, cherry_core.hash_set("Slug"), cherry_core.keyword("name")), cherry_core.sort_by.call(null, cherry_core.keyword("rank"), cherry_core.keyword("fields").call(null, content_type9))));
    return cherry_core.vector(cherry_core.keyword("div"), (() => {
      const iter__23116__auto__41 = function iter__249(s__250) {
        return new cherry_core.LazySeq(null, function() {
          let s__25042 = s__250;
          while (true) {
            const temp__23033__auto__43 = cherry_core.seq.call(null, s__25042);
            if (cherry_core.truth_.call(null, temp__23033__auto__43)) {
              const s__25044 = temp__23033__auto__43;
              if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__25044))) {
                const c__23114__auto__45 = cherry_core.chunk_first.call(null, s__25044);
                const size__23115__auto__46 = cherry_core.count.call(null, c__23114__auto__45);
                const b__25247 = cherry_core.chunk_buffer.call(null, size__23115__auto__46);
                if ((() => {
                  let i__25148 = 0;
                  while (true) {
                    if (i__25148 < size__23115__auto__46) {
                      const message49 = cherry_core._nth.call(null, c__23114__auto__45, i__25148);
                      cherry_core.chunk_append.call(null, b__25247, message49);
                      let G__50 = cherry_core.unchecked_inc.call(null, i__25148);
                      i__25148 = G__50;
                      continue;
                    } else {
                      return true;
                    }
                    ;
                    break;
                  }
                })()) {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__25247), iter__249.call(null, cherry_core.chunk_rest.call(null, s__25044)));
                } else {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__25247), null);
                }
              } else {
                const message51 = cherry_core.first.call(null, s__25044);
                return cherry_core.cons.call(null, message51, iter__249.call(null, cherry_core.rest.call(null, s__25044)));
              }
            }
            ;
            break;
          }
        }, null, null);
      };
      return iter__23116__auto__41.call(null, cherry_core.distinct.call(null, messages16));
    })(), cherry_core.vector(cherry_core.keyword("form"), cherry_core.array_map(cherry_core.keyword("on-submit"), function(_PERCENT_1) {
      return submit_form21.call(null, _PERCENT_1, cherry_core.array_map(cherry_core.keyword("content-item-slug"), content_item_slug34));
    }), cherry_core.vector(cherry_core.keyword("div"), (() => {
      const iter__23116__auto__52 = function iter__253(s__254) {
        return new cherry_core.LazySeq(null, function() {
          let s__25453 = s__254;
          while (true) {
            const temp__23033__auto__54 = cherry_core.seq.call(null, s__25453);
            if (cherry_core.truth_.call(null, temp__23033__auto__54)) {
              const s__25455 = temp__23033__auto__54;
              if (cherry_core.truth_.call(null, cherry_core.chunked_seq_QMARK_.call(null, s__25455))) {
                const c__23114__auto__56 = cherry_core.chunk_first.call(null, s__25455);
                const size__23115__auto__57 = cherry_core.count.call(null, c__23114__auto__56);
                const b__25658 = cherry_core.chunk_buffer.call(null, size__23115__auto__57);
                if ((() => {
                  let i__25559 = 0;
                  while (true) {
                    if (i__25559 < size__23115__auto__57) {
                      const vec__25760 = cherry_core._nth.call(null, c__23114__auto__56, i__25559);
                      const i61 = cherry_core.nth.call(null, vec__25760, 0, null);
                      const field62 = cherry_core.nth.call(null, vec__25760, 1, null);
                      const map__26063 = field62;
                      const map__26064 = cherry_core.__destructure_map.call(null, map__26063);
                      const type65 = cherry_core.get.call(null, map__26064, cherry_core.keyword("type"));
                      const name66 = cherry_core.get.call(null, map__26064, cherry_core.keyword("name"));
                      cherry_core.chunk_append.call(null, b__25658, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("id").call(null, field62)), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), cherry_core.str.call(null, "mb-2 pb-2 pt-2 ", cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i61, cherry_core.count.call(null, fields40) - 1)) ? null : "border-b")), (() => {
                        const G__26167 = type65;
                        const G__26168 = cherry_core.truth_.call(null, cherry_core.keyword_QMARK_.call(null, G__26167)) ? cherry_core.subs.call(null, cherry_core.str.call(null, G__26167), 1) : null;
                        switch (G__26168) {
                          case "text":
                            return cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("class"), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, cherry_core.keyword("name").call(null, field62), "Title")) ? "w-full" : null, cherry_core.keyword("name"), name66, cherry_core.keyword("placeholder"), name66, cherry_core.keyword("default-value"), cherry_core.get_in.call(null, content_item14, cherry_core.vector(cherry_core.keyword("form-fields"), cherry_core.keyword("id").call(null, field62))), cherry_core.keyword("on-change"), function(_PERCENT_1) {
                              return update_field19.call(null, _PERCENT_1, cherry_core.keyword("id").call(null, field62));
                            }));
                            break;
                          case "text-lg":
                            return cherry_core.vector(editor, cherry_core.array_map(cherry_core.keyword("class"), "h-72", cherry_core.keyword("html"), cherry_core.get_in.call(null, content_item14, cherry_core.vector(cherry_core.keyword("form-fields"), cherry_core.keyword("id").call(null, field62))), cherry_core.keyword("on-start"), function(_PERCENT_1) {
                              return add_editor17.call(null, cherry_core.keyword("id").call(null, field62), _PERCENT_1);
                            }));
                            break;
                          case "choice":
                            return cherry_core.vector(html.select);
                            break;
                          case "datetime":
                            return cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("name"), name66, cherry_core.keyword("placeholder"), name66));
                            break;
                          case "number":
                            return cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("number"), cherry_core.keyword("name"), name66, cherry_core.keyword("placeholder"), name66));
                            break;
                          default:
                            throw new Error(cherry_core.str.call(null, "No matching clause: ", G__26168));
                        }
                      })(), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, cherry_core.keyword("name").call(null, field62), "Title")) ? cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "mt-2 text-sm"), cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-500"), "Permalink: "), cherry_core.truth_.call(null, permalink_url39) ? cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline", cherry_core.keyword("href"), permalink_url39), permalink_url39) : cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-400"), site_base_url10, "/\u2026")) : null)));
                      let G__70 = cherry_core.unchecked_inc.call(null, i__25559);
                      i__25559 = G__70;
                      continue;
                    } else {
                      return true;
                    }
                    ;
                    break;
                  }
                })()) {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__25658), iter__253.call(null, cherry_core.chunk_rest.call(null, s__25455)));
                } else {
                  return cherry_core.chunk_cons.call(null, cherry_core.chunk.call(null, b__25658), null);
                }
              } else {
                const vec__26271 = cherry_core.first.call(null, s__25455);
                const i72 = cherry_core.nth.call(null, vec__26271, 0, null);
                const field73 = cherry_core.nth.call(null, vec__26271, 1, null);
                const map__26574 = field73;
                const map__26575 = cherry_core.__destructure_map.call(null, map__26574);
                const type76 = cherry_core.get.call(null, map__26575, cherry_core.keyword("type"));
                const name77 = cherry_core.get.call(null, map__26575, cherry_core.keyword("name"));
                return cherry_core.cons.call(null, cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("key"), cherry_core.keyword("id").call(null, field73)), cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), cherry_core.str.call(null, "mb-2 pb-2 pt-2 ", cherry_core.truth_.call(null, cherry_core._EQ_.call(null, i72, cherry_core.count.call(null, fields40) - 1)) ? null : "border-b")), (() => {
                  const G__26678 = type76;
                  const G__26679 = cherry_core.truth_.call(null, cherry_core.keyword_QMARK_.call(null, G__26678)) ? cherry_core.subs.call(null, cherry_core.str.call(null, G__26678), 1) : null;
                  switch (G__26679) {
                    case "text":
                      return cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("class"), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, cherry_core.keyword("name").call(null, field73), "Title")) ? "w-full" : null, cherry_core.keyword("name"), name77, cherry_core.keyword("placeholder"), name77, cherry_core.keyword("default-value"), cherry_core.get_in.call(null, content_item14, cherry_core.vector(cherry_core.keyword("form-fields"), cherry_core.keyword("id").call(null, field73))), cherry_core.keyword("on-change"), function(_PERCENT_1) {
                        return update_field19.call(null, _PERCENT_1, cherry_core.keyword("id").call(null, field73));
                      }));
                      break;
                    case "text-lg":
                      return cherry_core.vector(editor, cherry_core.array_map(cherry_core.keyword("class"), "h-72", cherry_core.keyword("html"), cherry_core.get_in.call(null, content_item14, cherry_core.vector(cherry_core.keyword("form-fields"), cherry_core.keyword("id").call(null, field73))), cherry_core.keyword("on-start"), function(_PERCENT_1) {
                        return add_editor17.call(null, cherry_core.keyword("id").call(null, field73), _PERCENT_1);
                      }));
                      break;
                    case "choice":
                      return cherry_core.vector(html.select);
                      break;
                    case "datetime":
                      return cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("text"), cherry_core.keyword("name"), name77, cherry_core.keyword("placeholder"), name77));
                      break;
                    case "number":
                      return cherry_core.vector(html.input, cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("number"), cherry_core.keyword("name"), name77, cherry_core.keyword("placeholder"), name77));
                      break;
                    default:
                      throw new Error(cherry_core.str.call(null, "No matching clause: ", G__26679));
                  }
                })(), cherry_core.truth_.call(null, cherry_core._EQ_.call(null, cherry_core.keyword("name").call(null, field73), "Title")) ? cherry_core.vector(cherry_core.keyword("div"), cherry_core.array_map(cherry_core.keyword("class"), "mt-2 text-sm"), cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-500"), "Permalink: "), cherry_core.truth_.call(null, permalink_url39) ? cherry_core.vector(cherry_core.keyword("a"), cherry_core.array_map(cherry_core.keyword("class"), "underline", cherry_core.keyword("href"), permalink_url39), permalink_url39) : cherry_core.vector(cherry_core.keyword("span"), cherry_core.array_map(cherry_core.keyword("class"), "text-gray-400"), site_base_url10, "/\u2026")) : null)), iter__253.call(null, cherry_core.rest.call(null, s__25455)));
              }
            }
            ;
            break;
          }
        }, null, null);
      };
      return iter__23116__auto__52.call(null, fields40);
    })(), cherry_core.vector(cherry_core.keyword("button"), cherry_core.array_map(cherry_core.keyword("type"), cherry_core.keyword("submit"), cherry_core.keyword("class"), cherry_core.str.call(null, "text-white bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800", submit_button_class11), cherry_core.keyword("disabled"), submitting13), cherry_core.truth_.call(null, submitting13) ? cherry_core.vector(cherry_core.keyword("span"), cherry_core.vector(html.spinner), submitting_button_text5) : submit_button_text8))));
  })())));
};
export {
  __GT_content_type,
  __GT_field,
  __GT_slug,
  all_content_types_page,
  columns,
  content_type_fields_form,
  content_type_new_item_form,
  editor,
  editor_impl,
  field_config,
  format_datetime,
  index_by,
  months,
  quill_defaults,
  quill_get_semantic_html,
  quill_toolbar,
  random_uuid,
  single_content_type_page,
  slug_field_id,
  start_quill_BANG_,
  success_alert,
  title_field_id
};
