(function($) {

    var statics = {
        version: '1.2.0',

        defaultPreset : 'notice',

        presets : {
            notice : { // Do not remove this preset as it's used for the <i>backup</i> default behavior
                timeout : 2,
                type    : 'notice',
                css     : null
            },
            success : {
                timeout : 2,
                type    : 'success',
                css     : null
            },
            error : {
                timeout : 5,
                type    : 'error',
                css     : null
            },
            warning : {
                timeout : 5,
                type    : 'warning',
                css     : null
            }
        }
    };

    var methods = {
        init : function(options) {

        },

        /**
         * @param string text : Text for the message
         * @param mixed opts : Either a full set of options such as statics.presets.notice ; or the name of a preset or just the name of a css class
         */
        jnotify: function(text, opts, optss) {
            var options = $.extend({}, statics.presets[statics.defaultPreset]);
            if (typeof opts == 'string') {
                if (typeof statics.presets[opts] == 'object') {
                    opts = $.extend({}, statics.presets[opts]);
                    if (typeof optss == 'object') {
                        opts = $.extend(opts, optss);
                    }
                }
                else {
                    opts = {type: opts, css: opts};
                }
            }
            if (typeof opts == 'object') {
                $.extend(options, opts);
            }

            // Retreive the stack, create it if it isn't yet
            var $stack = $('body').find('#jnotify-stack');
            if ($stack.length == 0) {
                $stack = $('<div />');

                $stack.dblclick(function() {
                    $(this).empty();
                } );

                $stack.attr('id', 'jnotify-stack');
                
                $stack.css("margin-top","5px");
                $stack.css("margin-right","5px");
                $stack.css("position","fixed");
                $stack.css("top","0");
                $stack.css("right","0");
                $stack.css("text-align","right");                
                $stack.css("opacity","0.9");
                $stack.css("filter","alpha(opacity=90)");
                $stack.css("z-index","50001");

                
                $('body').prepend($stack);
            }

            // Create the notice
            var $notice = $('<div />');
            $notice.addClass('jnotify');
            $notice.css( "font-size", "13px" );
            $notice.css( "font-family","Open Sans, Arial, sans-serif");
            $notice.css( "text-align", "left" );
            $notice.css( "border", "#000000 1px solid" );
            $notice.css( "padding", "4px" );
            $notice.css( "font-weight", "bold" );
            $notice.css( "margin", "5px" );
            $notice.css( "padding", "5px 5px 5px 23px" );
            $notice.css( "-moz-border-radius", "8px" );
            $notice.css( "-webkit-border-radius", "8px" );
            $notice.css( "background", " none 3px 5px no-repeat" );
            $notice.css( "z-index", "50002" );
            $notice.css( "cursor", "pointer" );

            if(options.type == 'notice'){
                
            	$notice.css( "background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAKcSURBVDjLpZPLa9RXHMU/d0ysZEwmMQqZiTaP0agoaKGJUiwIxU0hUjtUQaIuXHSVbRVc+R8ICj5WvrCldJquhVqalIbOohuZxjDVxDSP0RgzyST9zdzvvffrQkh8tBs9yy9fPhw45xhV5X1U8+Yhc3U0LcEdVxdOVq20OA0ooQjhpnfhzuDZTx6++m9edfDFlZGMtXKxI6HJnrZGGtauAWAhcgwVnnB/enkGo/25859l3wIcvpzP2EhuHNpWF9/dWs/UnKW4EOGDkqhbQyqxjsKzMgM/P1ymhlO5C4ezK4DeS/c7RdzQoa3x1PaWenJjJZwT9rQ1gSp/js1jYoZdyfX8M1/mp7uFaTR8mrt29FEMQILr62jQ1I5kA8OF59jIItVA78dJertTiBNs1ZKfLNG+MUHX1oaURtIHEAOw3p/Y197MWHEJEUGCxwfHj8MTZIcnsGKxzrIURYzPLnJgbxvG2hMrKdjItjbV11CYKeG8R7ygIdB3sBMFhkem0RAAQ3Fuka7UZtRHrasOqhYNilOwrkrwnhCU/ON5/q04vHV48ThxOCuoAbxnBQB+am65QnO8FqMxNCjBe14mpHhxBBGCWBLxD3iyWMaYMLUKsO7WYH6Stk1xCAGccmR/Ozs/bKJuXS39R/YgIjgROloSDA39Deit1SZWotsjD8pfp5ONqZ6uTfyWn+T7X0f59t5fqDhUA4ry0fYtjJcWeZQvTBu4/VqRuk9/l9Fy5cbnX+6Od26s58HjWWaflwkusKGxjm1bmhkvLXHvh1+WMbWncgPfZN+qcvex6xnUXkzvSiYP7EvTvH4toDxdqDD4+ygT+cKMMbH+3MCZ7H9uAaDnqytpVX8cDScJlRY0YIwpAjcNcuePgXP/P6Z30QuoP4J7WbYhuQAAAABJRU5ErkJggg==)" );
            	$notice.css( "border-color", "#76A0CD" );
            	$notice.css( "color", "#1A5799" );
            	$notice.css( "background-color", "#EAF2F9" );            	
            }
            
			if(options.type == 'success'){
				$notice.css( "background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAKfSURBVDjLpZPrS1NhHMf9O3bOdmwDCWREIYKEUHsVJBI7mg3FvCxL09290jZj2EyLMnJexkgpLbPUanNOberU5taUMnHZUULMvelCtWF0sW/n7MVMEiN64AsPD8/n83uucQDi/id/DBT4Dolypw/qsz0pTMbj/WHpiDgsdSUyUmeiPt2+V7SrIM+bSss8ySGdR4abQQv6lrui6VxsRonrGCS9VEjSQ9E7CtiqdOZ4UuTqnBHO1X7YXl6Daa4yGq7vWO1D40wVDtj4kWQbn94myPGkCDPdSesczE2sCZShwl8CzcwZ6NiUs6n2nYX99T1cnKqA2EKui6+TwphA5k4yqMayopU5mANV3lNQTBdCMVUA9VQh3GuDMHiVcLCS3J4jSLhCGmKCjBEx0xlshjXYhApfMZRP5CyYD+UkG08+xt+4wLVQZA1tzxthm2tEfD3JxARH7QkbD1ZuozaggdZbxK5kAIsf5qGaKMTY2lAU/rH5HW3PLsEwUYy+YCcERmIjJpDcpzb6l7th9KtQ69fi09ePUej9l7cx2DJbD7UrG3r3afQHOyCo+V3QQzE35pvQvnAZukk5zL5qRL59jsKbPzdheXoBZc4saFhBS6AO7V4zqCpiawuptwQG+UAa7Ct3UT0hh9p9EnXT5Vh6t4C22QaUDh6HwnECOmcO7K+6kW49DKqS2DrEZCtfuI+9GrNHg4fMHVSO5kE7nAPVkAxKBxcOzsajpS4Yh4ohUPPWKTUh3PaQEptIOr6BiJjcZXCwktaAGfrRIpwblqOV3YKdhfXOIvBLeREWpnd8ynsaSJoyESFphwTtfjN6X1jRO2+FxWtCWksqBApeiFIR9K6fiTpPiigDoadqCEag5YUFKl6Yrciw0VOlhOivv/Ff8wtn0KzlebrUYwAAAABJRU5ErkJggg==)" );
            	$notice.css( "border-color", "#8FA350" );
            	$notice.css( "color", "#5F6C35" );
            	$notice.css( "background-color", "#E0E6CD" );              
			}
			 
			if(options.type == 'error'){
				$notice.css( "background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAJPSURBVDjLpZPLS5RhFMYfv9QJlelTQZwRb2OKlKuINuHGLlBEBEOLxAu46oL0F0QQFdWizUCrWnjBaDHgThCMoiKkhUONTqmjmDp2GZ0UnWbmfc/ztrC+GbM2dXbv4ZzfeQ7vefKMMfifyP89IbevNNCYdkN2kawkCZKfSPZTOGTf6Y/m1uflKlC3LvsNTWArr9BT2LAf+W73dn5jHclIBFZyfYWU3or7T4K7AJmbl/yG7EtX1BQXNTVCYgtgbAEAYHlqYHlrsTEVQWr63RZFuqsfDAcdQPrGRR/JF5nKGm9xUxMyr0YBAEXXHgIANq/3ADQobD2J9fAkNiMTMSFb9z8ambMAQER3JC1XttkYGGZXoyZEGyTHRuBuPgBTUu7VSnUAgAUAWutOV2MjZGkehgYUA6O5A0AlkAyRnotiX3MLlFKduYCqAtuGXpyH0XQmOj+TIURt51OzURTYZdBKV2UBSsOIcRp/TVTT4ewK6idECAihtUKOArWcjq/B8tQ6UkUR31+OYXP4sTOdisivrkMyHodWejlXwcC38Fvs8dY5xaIId89VlJy7ACpCNCFCuOp8+BJ6A631gANQSg1mVmOxxGQYRW2nHMha4B5WA3chsv22T5/B13AIicWZmNZ6cMchTXUe81Okzz54pLi0uQWp+TmkZqMwxsBV74Or3od4OISPr0e3SHa3PX0f3HXKofNH/UIG9pZ5PeUth+CyS2EMkEqs4fPEOBJLsyske48/+xD8oxcAYPzs4QaS7RR2kbLTTOTQieczfzfTv8QPldGvTGoF6/8AAAAASUVORK5CYII=)" );
            	$notice.css( "border-color", "#BE3825" );
            	$notice.css( "color", "#B81900" );
            	$notice.css( "background-color", "#F1C0BA" );               	
			}
			 
			if(options.type == 'warning'){
				$notice.css( "background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAABl0RVh0U29mdHdhcmUAQWRvYmUgSW1hZ2VSZWFkeXHJZTwAAAIsSURBVDjLpVNLSJQBEP7+h6uu62vLVAJDW1KQTMrINQ1vPQzq1GOpa9EppGOHLh0kCEKL7JBEhVCHihAsESyJiE4FWShGRmauu7KYiv6Pma+DGoFrBQ7MzGFmPr5vmDFIYj1mr1WYfrHPovA9VVOqbC7e/1rS9ZlrAVDYHig5WB0oPtBI0TNrUiC5yhP9jeF4X8NPcWfopoY48XT39PjjXeF0vWkZqOjd7LJYrmGasHPCCJbHwhS9/F8M4s8baid764Xi0Ilfp5voorpJfn2wwx/r3l77TwZUvR+qajXVn8PnvocYfXYH6k2ioOaCpaIdf11ivDcayyiMVudsOYqFb60gARJYHG9DbqQFmSVNjaO3K2NpAeK90ZCqtgcrjkP9aUCXp0moetDFEeRXnYCKXhm+uTW0CkBFu4JlxzZkFlbASz4CQGQVBFeEwZm8geyiMuRVntzsL3oXV+YMkvjRsydC1U+lhwZsWXgHb+oWVAEzIwvzyVlk5igsi7DymmHlHsFQR50rjl+981Jy1Fw6Gu0ObTtnU+cgs28AKgDiy+Awpj5OACBAhZ/qh2HOo6i+NeA73jUAML4/qWux8mt6NjW1w599CS9xb0mSEqQBEDAtwqALUmBaG5FV3oYPnTHMjAwetlWksyByaukxQg2wQ9FlccaK/OXA3/uAEUDp3rNIDQ1ctSk6kHh1/jRFoaL4M4snEMeD73gQx4M4PsT1IZ5AfYH68tZY7zv/ApRMY9mnuVMvAAAAAElFTkSuQmCC)" );
            	$notice.css( "border-color", "#D26D19" );
            	$notice.css( "color", "#8C4810" );
            	$notice.css( "background-color", "#F6D5B9" );             	
			}

 
            
            $notice.html(text);
            $notice.hide();
            var $close = $('<div />');
            $close.addClass('close');
            $close.css( "height", "16px" );
            $close.css( "width", "16px" );
            $close.css( "float", "right" );
            $close.css( "opacity", "0.4" );
            $close.css( "filter", "alpha(opacity=40)" );
            $close.css( "background-position", "top right" );
            $close.css( "cursor", "pointer" );
            $close.css( "background-image", "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAABGdBTUEAAK/INwWK6QAAAAlwSFlzAAAOwwAADsMBx2+oZAAAABl0RVh0U29mdHdhcmUAUGFpbnQuTkVUIHYzLjUuODc7gF0AAAHASURBVDhPlVPXisJQEJWFhcv+xD4v+YcFv8KCiIgiChp7QWwP9o69YQUX/MXI2ZkLuhuSPBi4ZCBzypy5sdlefPr9/oclZLfbfdJRrBoIrLTbbbVer38behh8OByw3++xWq0MJL1eTxkMBphMJqjVaj/lcvnrSfIAXy4X8NlsNphOp0+SbrerkDoWiwXO5zO4LhQKaiaTeZMkRCAIdD8ej7jdbmAnRABSVDqdjkLqmM/nOJ1O4JrAGoHfdWOQbUFNGpHher1iu91iOByC1DGbzcDkXOfzeS2dTgvTnGg+MRqNNLbKo7AiAzkXcoJcLmcNfjCSRdFqteQ4bJeB7Cqbzd5TqZS58n87BOZVSVWq0Ww2sV6veW7EYjHLFUsOalYYNB6PZQa8DcoGy+UStHskk0mEQiFzkkajIcGUgQSyMoWlxePxe6VSkVupVquIRCLw+Xx6EmJXGMCps10ie4BFNBoV4XBYo/Sls2KxiGAwCLfb/UdCN8vOFvmm8ZvC0lRVfQYWCASE3+/nDaBUKvEYcDqddt0qKSQ7f6Q57xSWIW2v1ys8Ho9mCn4wJRIJO1u2+plcLpdwOBx65Rf/ZkP7L6S1P2NS3cqVAAAAAElFTkSuQmCC)" );
         
            
            
            $close.hide();
            $notice.mouseenter(function() {
                $(this).find('.close').show();
            } ).mouseleave(function() {
                $(this).find('.close').hide();
            } );

            $notice.prepend($close);
            //$notice.style.color = "red";
            $stack.append($notice);
            $notice.slideDown('fast');

            if (options.timeout > 0) {
                $notice.delay(options.timeout * 1000).fadeOut('slow', function() {
                    $(this).detach().remove();
                } );
            }

            $notice.click(function() {
                $(this).fadeOut('fast', function() {
                    $(this).detach().remove();
                } );
            } );
        },

        addPreset: function(preset, options) {
            statics.presets[preset] = options;
            if (! statics.presets[preset].type) {
                statics.presets[preset].type = preset;
            }
            if (! statics.presets[preset].css) {
                statics.presets[preset].css = statics.presets[preset].type;
            }
        },

        updatePreset: function(preset, options) {
            if (statics.presets[preset]) {
                methods.addPreset(preset, $.extend(statics.presets[preset], options));
            }
        },

        removePreset: function(preset) {
            if (statics.presets[preset] && preset != 'notice') {
                delete statics.presets[preset];
                if (statics.defaultPreset == preset) {
                    methods.setDefaultPreset('notice');
                }
            } else {
                $.error('"notice" preset is not allowed to be removed.');
            }
        },

        getPreset: function(preset) {
            if (statics.presets[preset]) {
                return statics.presets[preset];
            }
        },

        getPresets: function() {
            return statics.presets;
        },

        setDefaultPreset: function(preset) {
            if (statics.presets[preset]) {
                statics.defaultPreset = preset;
            }
        },

        getDefaultPreset: function() {
            return methods.getPreset(statics.defaultPreset);
        },

        getDefaultPresetName: function() {
            return statics.defaultPreset;
        }
    };

    $.jnotify = function(method) {
        if (methods[method]) {
            return methods[method].apply(this, Array.prototype.slice.call(arguments, 1));
        }
        else if (typeof method === 'string') {
            return methods.jnotify.apply(this, arguments);
        }
        else if (typeof method === 'object' || ! method) {
            return methods.init.apply(this, arguments );
        }
        else {
            $.error('Method "' +  method + '" does not exist on jQuery.jnotify');
        }
    }

})(jQuery);