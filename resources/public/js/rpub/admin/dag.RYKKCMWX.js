import * as cherry_core from "cherry-cljs/cljs.core.js";
import * as admin_impl from "rpub.admin.impl";
import * as dag from "rpub.lib.dag";
var init = function(_, p__66) {
  const map__671 = p__66;
  const map__672 = cherry_core.__destructure_map.call(null, map__671);
  const content_types3 = cherry_core.get.call(null, map__672, cherry_core.keyword("content-types"));
  const content_types_index4 = admin_impl.index_by.call(null, cherry_core.keyword("id"), content_types3);
  return cherry_core.array_map(cherry_core.keyword("model/content-types-index"), content_types_index4);
};
var field_values = (() => {
  const f68 = function(var_args) {
    const G__711 = cherry_core.alength.call(null, arguments);
    switch (G__711) {
      case 1:
        return f68.cljs$core$IFn$_invoke$arity$1(arguments[0]);
        break;
      case 2:
        return f68.cljs$core$IFn$_invoke$arity$2(arguments[0], arguments[1]);
        break;
      default:
        throw new Error(cherry_core.str.call(null, "Invalid arity: ", cherry_core.alength.call(null, arguments)));
    }
  };
  f68.cljs$core$IFn$_invoke$arity$1 = function(db) {
    return field_values.call(null, db, cherry_core.keys.call(null, cherry_core.keyword("inputs").call(null, db)));
  };
  f68.cljs$core$IFn$_invoke$arity$2 = function(db, ks) {
    return cherry_core.update_vals.call(null, cherry_core.select_keys.call(null, cherry_core.keyword("inputs").call(null, db), ks), cherry_core.keyword("value"));
  };
  f68.cljs$lang$maxFixedArity = 2;
  return f68;
})();
var model_settings = function(db) {
  return cherry_core.merge.call(null, cherry_core.keyword("settings").call(null, db), field_values.call(null, db));
};
var change_input = function(db, p__72) {
  const vec__731 = p__72;
  const k2 = cherry_core.nth.call(null, vec__731, 0, null);
  const v3 = cherry_core.nth.call(null, vec__731, 1, null);
  return cherry_core.assoc_in.call(null, db, cherry_core.vector(cherry_core.keyword("inputs"), k2, cherry_core.keyword("value")), v3);
};
var activate_plugin = function(db, k) {
  return cherry_core.update.call(null, cherry_core.assoc.call(null, db, cherry_core.keyword("plugins-page/needs-restart"), true), cherry_core.keyword("plugins-page/activated-plugins"), cherry_core.fnil.call(null, cherry_core.conj, cherry_core.hash_set()), k);
};
var deactivate_plugin = function(db, k) {
  return cherry_core.update.call(null, cherry_core.assoc.call(null, db, cherry_core.keyword("plugins-page/needs-restart"), true), cherry_core.keyword("plugins-page/activated-plugins"), cherry_core.disj, k);
};
var restart_server = function(db) {
  return cherry_core.assoc.call(null, db, cherry_core.keyword("plugins-page/restarted"), true);
};
var submit_start = function(db) {
  return cherry_core.assoc.call(null, db, cherry_core.keyword("settings-page/submitting"), true);
};
var submit_error = function(db) {
  return cherry_core.assoc.call(null, db, cherry_core.keyword("settings-page/submitting"), false);
};
var activate_theme = function(db, theme_label) {
  return cherry_core.assoc.call(null, db, cherry_core.keyword("themes-page/current-theme-name-setting"), cherry_core.array_map(cherry_core.keyword("value"), theme_label));
};
var select_content_type = function(db, p__76) {
  const map__771 = p__76;
  const map__772 = cherry_core.__destructure_map.call(null, map__771);
  const content_type3 = cherry_core.get.call(null, map__772, cherry_core.keyword("content-type"));
  const selection4 = cherry_core.array_map(cherry_core.keyword("content-type"), cherry_core.select_keys.call(null, content_type3, cherry_core.vector(cherry_core.keyword("id"))));
  return cherry_core.assoc.call(null, db, cherry_core.keyword("all-content-types-page/selection"), selection4);
};
var select_content_type_field = function(db, p__78) {
  const map__791 = p__78;
  const map__792 = cherry_core.__destructure_map.call(null, map__791);
  const content_type3 = cherry_core.get.call(null, map__792, cherry_core.keyword("content-type"));
  const content_type_field4 = cherry_core.get.call(null, map__792, cherry_core.keyword("content-type-field"));
  const selection5 = cherry_core.array_map(cherry_core.keyword("content-type"), cherry_core.select_keys.call(null, content_type3, cherry_core.vector(cherry_core.keyword("id"))), cherry_core.keyword("content-type-field"), cherry_core.select_keys.call(null, content_type_field4, cherry_core.vector(cherry_core.keyword("id"))));
  return cherry_core.assoc.call(null, db, cherry_core.keyword("all-content-types-page/selection"), selection5);
};
var clear_selection = function(db) {
  return cherry_core.dissoc.call(null, db, cherry_core.keyword("all-content-types-page/selection"));
};
var selection = function(db) {
  const sel1 = cherry_core.get.call(null, db, cherry_core.keyword("all-content-types-page/selection"));
  const content_type_id2 = cherry_core.get_in.call(null, sel1, cherry_core.vector(cherry_core.keyword("content-type"), cherry_core.keyword("id")));
  const content_type_field_id3 = cherry_core.get_in.call(null, sel1, cherry_core.vector(cherry_core.keyword("content-type-field"), cherry_core.keyword("id")));
  const content_type4 = cherry_core.get_in.call(null, db, cherry_core.vector(cherry_core.keyword("model/content-types-index"), content_type_id2));
  const G__805 = null;
  const G__806 = cherry_core.truth_.call(null, cherry_core.keyword("content-type").call(null, sel1)) ? cherry_core.assoc.call(null, G__805, cherry_core.keyword("content-type"), content_type4) : G__805;
  if (cherry_core.truth_.call(null, cherry_core.keyword("content-type-field").call(null, sel1))) {
    return cherry_core.assoc.call(null, G__806, cherry_core.keyword("content-type-field"), cherry_core.first.call(null, cherry_core.filter.call(null, function(_PERCENT_1) {
      return cherry_core._EQ_.call(null, cherry_core.keyword("id").call(null, _PERCENT_1), content_type_field_id3);
    }, cherry_core.keyword("fields").call(null, content_type4))));
  } else {
    return G__806;
  }
};
var dag_config = cherry_core.array_map(cherry_core.keyword("nodes"), cherry_core.hash_map(cherry_core.keyword("all-content-types-page/selection"), cherry_core.array_map(cherry_core.keyword("calc"), selection), cherry_core.keyword("themes-page/current-theme-name-setting"), cherry_core.array_map(cherry_core.keyword("calc"), cherry_core.keyword("themes-page/current-theme-name-setting")), cherry_core.keyword("init"), cherry_core.array_map(cherry_core.keyword("push"), init), cherry_core.keyword("plugins-page/restart-server"), cherry_core.array_map(cherry_core.keyword("push"), restart_server), cherry_core.keyword("settings-page/submitting"), cherry_core.array_map(cherry_core.keyword("calc"), cherry_core.keyword("settings-page/submitting")), cherry_core.keyword("themes-page/activate-theme"), cherry_core.array_map(cherry_core.keyword("push"), activate_theme), cherry_core.keyword("all-content-types-page/select-content-type"), cherry_core.array_map(cherry_core.keyword("push"), select_content_type), cherry_core.keyword("plugins-page/activated-plugins"), cherry_core.array_map(cherry_core.keyword("calc"), cherry_core.keyword("plugins-page/activated-plugins")), cherry_core.keyword("all-content-types-page/clear-selection"), cherry_core.array_map(cherry_core.keyword("push"), clear_selection), cherry_core.keyword("settings-page/change-input"), cherry_core.array_map(cherry_core.keyword("push"), change_input), cherry_core.keyword("settings-page/update-settings"), cherry_core.array_map(cherry_core.keyword("push"), change_input), cherry_core.keyword("model/settings"), cherry_core.array_map(cherry_core.keyword("calc"), model_settings), cherry_core.keyword("plugins-page/deactivate-plugin"), cherry_core.array_map(cherry_core.keyword("push"), deactivate_plugin), cherry_core.keyword("settings-page/submit-start"), cherry_core.array_map(cherry_core.keyword("push"), submit_start), cherry_core.keyword("model/site-url"), cherry_core.array_map(cherry_core.keyword("calc"), cherry_core.comp.call(null, cherry_core.keyword("site-url"), cherry_core.keyword("settings"))), cherry_core.keyword("plugins-page/activate-plugin"), cherry_core.array_map(cherry_core.keyword("push"), activate_plugin), cherry_core.keyword("all-content-types-page/select-content-type-field"), cherry_core.array_map(cherry_core.keyword("push"), select_content_type_field), cherry_core.keyword("plugins-page/needs-restart"), cherry_core.array_map(cherry_core.keyword("calc"), cherry_core.keyword("plugins-page/needs-restart")), cherry_core.keyword("settings-page/submit-error"), cherry_core.array_map(cherry_core.keyword("push"), submit_error), cherry_core.keyword("settings-page/field-values"), cherry_core.array_map(cherry_core.keyword("calc"), field_values)), cherry_core.keyword("edges"), cherry_core.vector(cherry_core.vector(cherry_core.keyword("all-content-types-page/clear-selection"), cherry_core.keyword("all-content-types-page/selection")), cherry_core.vector(cherry_core.keyword("all-content-types-page/select-content-type"), cherry_core.keyword("all-content-types-page/selection")), cherry_core.vector(cherry_core.keyword("all-content-types-page/select-content-type-field"), cherry_core.keyword("all-content-types-page/selection")), cherry_core.vector(cherry_core.keyword("init"), cherry_core.keyword("model/settings")), cherry_core.vector(cherry_core.keyword("model/settings"), cherry_core.keyword("model/site-url")), cherry_core.vector(cherry_core.keyword("model/settings"), cherry_core.keyword("settings-page/field-values")), cherry_core.vector(cherry_core.keyword("plugins-page/activate-plugin"), cherry_core.keyword("plugins-page/needs-restart")), cherry_core.vector(cherry_core.keyword("plugins-page/activate-plugin"), cherry_core.keyword("plugins-page/activated-plugins")), cherry_core.vector(cherry_core.keyword("plugins-page/deactivate-plugin"), cherry_core.keyword("plugins-page/needs-restart")), cherry_core.vector(cherry_core.keyword("plugins-page/deactivate-plugin"), cherry_core.keyword("plugins-page/activated-plugins")), cherry_core.vector(cherry_core.keyword("settings-page/change-input"), cherry_core.keyword("settings-page/field-values")), cherry_core.vector(cherry_core.keyword("settings-page/submit-error"), cherry_core.keyword("settings-page/submitting")), cherry_core.vector(cherry_core.keyword("settings-page/submit-start"), cherry_core.keyword("settings-page/submitting")), cherry_core.vector(cherry_core.keyword("settings-page/update-settings"), cherry_core.keyword("model/settings")), cherry_core.vector(cherry_core.keyword("themes-page/activate-theme"), cherry_core.keyword("themes-page/current-theme-name-setting"))));
if (typeof dag_atom !== "undefined") {
} else {
  var dag_atom = cherry_core.atom.call(null, dag.add_tracing.call(null, dag.__GT_dag.call(null, dag_config)));
}
;
export {
  activate_plugin,
  activate_theme,
  change_input,
  clear_selection,
  dag_atom,
  dag_config,
  deactivate_plugin,
  field_values,
  init,
  model_settings,
  restart_server,
  select_content_type,
  select_content_type_field,
  selection,
  submit_error,
  submit_start
};
