// sample topology overlay - client side
//
// This is the glue that binds our business logic (in mmwaveTopovDemo.js)
// to the overlay framework.

(function () {
    'use strict';

    // injected refs
    var $log, tov, mtds, tts;

    // internal state should be kept in the service module (not here)
        // the viewbox is the same name as the icon, prefixed with an underscore:
        var viewbox = '0 0 110 110';
        // the path data (concatenated so it fits nicely on the screen)
        var mmwaveIcon = 'M57.2,20.1c-12.2,0-22,9.9-22,22s9.9,22,' +
        '22,22s22-9.9,22-22S69.3,20.1,57.2,20.1z M63.4,51.6l-' +
        '6.2-3.3l-6.2,3.3l1.2-6.9l-5-4.9l6.9-1l3.1-6.3l3.1,' +
        '6.3l6.9,1l-5,4.9L63.4,51.6z M93.7,81.6H20.6V70.6h73.' +
        '2V81.6z';


    // alarm clock glyph (view box 110x110)
    var clock = 'M92.9,61.3a39,39,0,1,1-39-39,39,39,0,0,1,39,39h0Z' +
        'M44,19.3c-4.4-7.4-14.8-9.3-23.2-4.2S9.1,30.2,13.5,37.6m80.8,0' +
        'c4.4-7.4,1.2-17.5-7.3-22.5s-18.8-3.2-23.3,4.2m-8.4,1.8V16.5h4.4' +
        'V11.9H48.2v4.6h4.6v4.6M51.6,56.4H51.5' +
        'a5.4,5.4,0,0,0,2.4,10.3,4.7,4.7,0,0,0,4.9-3.1H74.5' +
        'a2.2,2.2,0,0,0,2.4-2.2,2.4,2.4,0,0,0-2.4-2.3H58.8' +
        'a5.3,5.3,0,0,0-2.5-2.6H56.2V32.9' +
        'a2.3,2.3,0,0,0-.6-1.7,2.2,2.2,0,0,0-1.6-.7,2.4,2.4,0,0,0-2.4,2.4' +
        'h0V56.4M82.2,91.1l-7.1,5.3-0.2.2-1.2,2.1a0.6,0.6,0,0,0,.2.8' +
        'h0.2c2.6,0.4,10.7.9,10.3-1.2m-60.8,0c-0.4,2.1,7.7,1.6,10.3,1.2' +
        'h0.2a0.6,0.6,0,0,0,.2-0.8l-1.2-2.1-0.2-.2-7.1-5.3';


    // our overlay definition
    var overlay = {
        // NOTE: this must match the ID defined in AppUiTopovOverlay
        overlayId: 'mm-wave overlay',
        glyphId: '*mmwaveIcon',
        tooltip: 'MM-wave Topo Overlay',

        // These glyphs get installed using the overlayId as a prefix.
        // e.g. 'star4' is installed as 'meowster-overlay-star4'
        // They can be referenced (from this overlay) as '*star4'
        // That is, the '*' prefix stands in for 'meowster-overlay-'
        glyphs: {
            mmwaveIcon: {
                vb: viewbox,
                d:  mmwaveIcon
            }
        },

        activate: function () {
            $log.debug("MM-wave topology overlay ACTIVATED");
        },
        deactivate: function () {
            $log.debug("MM-wave topology overlay DEACTIVATED");
        },

        //detail panel button definitions
        //Added to device panel, defined in server java part
        buttons: {
            foo: {
                gid: 'chain',
                tt: 'A FOO action',
                cb: function (data) {
                    $log.debug('FOO action invoked with data:', data);
                }
            },
            bar: {
                gid: '*banner',
                tt: 'A BAR action',
                cb: function (data) {
                    $log.debug('BAR action invoked with data:', data);
                }
            }
        },

        // Key bindings for traffic overlay buttons
        // NOTE: fully qual. button ID is derived from overlay-id and key-name
        keyBindings: {
            0: {
                cb: function () { mtds.stopDisplay(); },
                tt: 'Cancel Display Mode',
                gid: 'xMark'
            },
            K: {
                cb: function () { mtds.startDisplay(); },
                tt: 'Start Display Mode',
                gid: '*mmwaveIcon'
            },
            V: {
                cb: function () { tts.showRelatedIntents(); },
                tt: 'Show all related intents',
                gid: 'm_relatedIntents'
            },
            leftArrow: {
                cb: function () { tts.showPrevIntent(); },
                tt: 'Show previous related intent',
                gid: 'm_prev'
            },
            rightArrow: {
                cb: function () { tts.showNextIntent(); },
                tt: 'Show next related intent',
                gid: 'm_next'
            },
            W: {
                cb: function () { tts.showSelectedIntentTraffic(); },
                tt: 'Monitor traffic of selected intent',
                gid: 'm_intentTraffic'
            },
            9:{
                cb: function () { mtds.updateLinkAnnotations(); },
                tt: 'go to die',
                gid: '*clock'
            },


            _keyOrder: [
                'K', '0', 'V', 'leftArrow', 'rightArrow', 'W','9'
            ]
        },

        hooks: {
            // hook for handling escape key
            // Must return true to consume ESC, false otherwise.
            escape: function () {
                // Must return true to consume ESC, false otherwise.
                return mtds.stopDisplay();
            },

            // hooks for when the selection changes...
            empty: function () {
                selectionCallback('empty');
            },
            single: function (data) {
                selectionCallback('single', data);
            },
            mouseout: function () {
                $log.debug('mouseout');
                mtds.updateDisplay();
            }
        }
    };


    function buttonCallback(x) {
        $log.debug('Toolbar-button callback', x);
    }

    function selectionCallback(x, d) {
        $log.debug('Selection callback', x, d);
    }

    // invoke code to register with the overlay service
    angular.module('ovMmwaveTopov')
        .run(['$log', 'TopoOverlayService', 'mmwaveTopovDemoService','TopoTrafficService',

        function (_$log_, _tov_, _mtds_,_tts_) {
            $log = _$log_;
            tov = _tov_;
            mtds = _mtds_;
            tts = _tts_;
            tov.register(overlay);
        }]);

}());
