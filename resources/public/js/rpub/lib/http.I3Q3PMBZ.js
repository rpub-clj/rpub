import * as cherry_core from "cherry-cljs/cljs.core.js";
var post = function(url, p__171) {
  const map__1721 = p__171;
  const map__1722 = cherry_core.__destructure_map.call(null, map__1721);
  const anti_forgery_token3 = cherry_core.get.call(null, map__1722, cherry_core.keyword("anti-forgery-token"));
  const body4 = cherry_core.get.call(null, map__1722, cherry_core.keyword("body"));
  const on_complete5 = cherry_core.get.call(null, map__1722, cherry_core.keyword("on-complete"));
  const headers6 = cherry_core.array_map("X-CSRF-Token", anti_forgery_token3, "Accept", "application/json", "Content-Type", "application/json");
  const params7 = (() => {
    const G__1738 = cherry_core.array_map(cherry_core.keyword("method"), cherry_core.keyword("post"), cherry_core.keyword("headers"), headers6);
    if (cherry_core.truth_.call(null, body4)) {
      return cherry_core.assoc.call(null, G__1738, cherry_core.keyword("body"), JSON.stringify(cherry_core.clj__GT_js.call(null, body4)));
    } else {
      return G__1738;
    }
  })();
  const req9 = fetch(url, cherry_core.clj__GT_js.call(null, params7));
  if (cherry_core.truth_.call(null, on_complete5)) {
    req9.then(function(_PERCENT_1) {
      return _PERCENT_1.text();
    }).then(function(_PERCENT_1) {
      return on_complete5.call(null, cherry_core.js__GT_clj.call(null, JSON.parse(_PERCENT_1)), null);
    }).catch(function(_PERCENT_1) {
      return on_complete5.call(null, null, _PERCENT_1);
    });
  }
  ;
  return null;
};
export {
  post
};
