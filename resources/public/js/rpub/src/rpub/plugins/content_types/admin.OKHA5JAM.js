import*as e from"cherry-cljs/cljs.core.js";import*as d from"rpub.admin.impl";import*as _ from"rpub.lib.html";import*as i from"rpub.lib.reagent";import*as f from"rpub.plugins.content-types.admin.all-content-types-page";import*as v from"rpub.plugins.content-types.admin.single-content-item-page";import*as $ from"rpub.plugins.content-types.admin.single-content-type-page";var p=function(a){return e.keyword("name").call(null,a)},m=function(a){const r=a,l=e.__destructure_map.call(null,r),n=l,t=e.get.call(null,l,e.keyword("field")),o=e.get.call(null,l,e.keyword("editing")),c=e.get.call(null,l,e.keyword("creating")),u=e.get.call(null,l,e.keyword("value")),s=e.get.call(null,l,e.keyword("on-change"));return e.vector(_.input2,(()=>{const y=e.array_map(e.keyword("type"),e.keyword("text"),e.keyword("name"),e.keyword("name").call(null,t),e.keyword("on-change"),s),g=e.truth_.call(null,o)?e.assoc.call(null,y,e.keyword("value"),u):y;return e.truth_.call(null,c)?e.assoc.call(null,g,e.keyword("placeholder"),p.call(null,t)):g})())},k=function(a){const r=a,l=e.__destructure_map.call(null,r),n=e.get.call(null,l,e.keyword("field")),t=e.get.call(null,l,e.keyword("editing")),o=e.get.call(null,l,e.keyword("creating")),c=e.get.call(null,l,e.keyword("value"));return e.vector(_.textarea,(()=>{const u=e.array_map(e.keyword("name"),e.keyword("name").call(null,n),e.keyword("on-change"),e.prn),s=e.truth_.call(null,t)?e.assoc.call(null,u,e.keyword("value"),c):u;return e.truth_.call(null,o)?e.assoc.call(null,s,e.keyword("placeholder"),p.call(null,n)):s})())},w=function(a){return e.vector(e.keyword("div.relative.max-w-sm"),e.vector(e.keyword("div.absolute.inset-y-0.start-0.flex.items-center.ps-3.5.pointer-events-none"),e.vector(e.keyword("svg.w-4.h-4.text-gray-500"),e.array_map(e.keyword("aria-hidden"),"true",e.keyword("xmlns"),"http://www.w3.org/2000/svg",e.keyword("fill"),"currentColor",e.keyword("viewBox"),"0 0 20 20"),e.vector(e.keyword("path"),e.array_map(e.keyword("d"),"M20 4a2 2 0 0 0-2-2h-2V1a1 1 0 0 0-2 0v1h-3V1a1 1 0 0 0-2 0v1H6V1a1 1 0 0 0-2 0v1H2a2 2 0 0 0-2 2v2h20V4ZM0 18a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V8H0v10Zm5-8h10a1 1 0 0 1 0 2H5a1 1 0 0 1 0-2Z")))),e.vector(e.keyword("input#default-datepicker"),e.array_map(e.keyword("class"),"bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full ps-10 p-2 5",e.keyword("datepicker"),"datepicker",e.keyword("datepicker-buttons"),"datepicker-buttons",e.keyword("datepicker-autoselect-today"),"datepicker-autoselect-today",e.keyword("type"),"text",e.keyword("placeholder"),"Select date")))},h=(()=>{const a=function(r){const l=e.array.call(null),n=e.alength.call(null,arguments);let t=0;for(;;){if(t<n){l.push(arguments[t]),t=t+1;continue}break}const o=0<e.alength.call(null,l)?new e.IndexedSeq(l.slice(0),0,null):null;return a.cljs$core$IFn$_invoke$arity$variadic(o)};return a.cljs$core$IFn$_invoke$arity$variadic=function(r){const l=r,t=e.__destructure_map.call(null,l);return _.add_element.call(null,e.keyword("rpub-field-types-text"),i.reactify_component.call(null,m)),_.add_element.call(null,e.keyword("rpub-field-types-text-lg"),i.reactify_component.call(null,k)),_.add_element.call(null,e.keyword("rpub-field-types-datetime"),i.reactify_component.call(null,w))},a.cljs$lang$maxFixedArity=0,a.cljs$lang$applyTo=function(r){return this.cljs$core$IFn$_invoke$arity$variadic(e.seq.call(null,r))},a})(),x=(()=>{const a=function(r){const l=e.array.call(null),n=e.alength.call(null,arguments);let t=0;for(;;){if(t<n){l.push(arguments[t]),t=t+1;continue}break}const o=0<e.alength.call(null,l)?new e.IndexedSeq(l.slice(0),0,null):null;return a.cljs$core$IFn$_invoke$arity$variadic(o)};return a.cljs$core$IFn$_invoke$arity$variadic=function(r){const l=r,t=e.__destructure_map.call(null,l);return d.add_page.call(null,e.merge.call(null,t,f.config)),d.add_page.call(null,e.merge.call(null,t,v.config)),d.add_page.call(null,e.merge.call(null,t,$.config))},a.cljs$lang$maxFixedArity=0,a.cljs$lang$applyTo=function(r){return this.cljs$core$IFn$_invoke$arity$variadic(e.seq.call(null,r))},a})();export{h as add_elements,x as add_pages,p as field_type_label,w as rpub_field_types_datetime,m as rpub_field_types_text,k as rpub_field_types_text_lg};
