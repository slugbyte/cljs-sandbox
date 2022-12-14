(ns supervisor.unit.blue-dot
  (:require
    [reagent.core :as reagent]
    [spade.core :as spade]
    [supervisor.base :as b]
    [supervisor.space :as s]
    [supervisor.util :as util]
    [supervisor.style :as style]
    [supervisor.data.theme :as d-theme]
    [supervisor.part.code-highlight :as code-highlight]
    ))

(spade/defclass css-blue-dot []
  (let [pallet @(d-theme/fetch-pallet)
        dot-color (:blue-dot pallet)
        blue-dot-code-bg (:blue-dot-code-bg pallet)
        blue-dot-modal-bg (:blue-dot-modal-bg pallet)
        ]
    [:&
     {:display :inline-block}
     [:.the-dot
      {:width :15px
       :height :15px
       :min-height :15px
       :border-radius :50%
       :background dot-color}
      (style/mixin-button-color dot-color) ]
     [:.the-modal
      {:position :fixed
       :width :100%
       :height :100%
       :top :0
       :left :0
       :background blue-dot-modal-bg
       :padding :20px
       }
      [:button
       {:margin-right :10px}]
      ]
     [:.the-hud
      {:width :100%
       :max-width :700px
       :margin  [[ :0 :auto]] }
       [:button
        (style/mixin-button-color blue-dot-code-bg)
        ]]
     [:.the-content
      {:height :95%
       :max-width :700px
       :margin  [[ :0 :auto]]
       }]
     ]))


(defn unit
  "blue-dot
  this component is for debugging the data inside of a view
  click it to open a modal that exposes the json/edn that the view contins"
  [props]
  (let [is-open (reagent/atom false)
        data (get props :data)
        content (util/to-json-pretty data)
        ]
    (fn []
      [s/box (style/tag [:blue-dot (css-blue-dot)])
       [s/box
        {:class (style/tag-value :the-dot)
         :on-click #(swap! is-open not)}]
       (when @is-open
         [s/box (style/tag :the-modal)
          [s/box (style/tag :the-hud)
            [b/ButtonDebug {:on-click #(swap! is-open not) } "close"]
            [b/ButtonDebug {:on-click #(util/copy-to-clipboard content)} "copy"]]
          [s/box (style/tag :the-content)
           [code-highlight/part :json content]]
          ])
       ])))
