define([ "jquery", "t5/core/console", "bootstrap/tooltip" ], function($, console) {
	return {
		initTooltips: function(email, nickname, firstName, lastname) {
			$('#email-wrapper').tooltip({'trigger':'hover', 'title': email});
			$('#nickName').tooltip({'trigger':'hover', 'title': nickname});
			$('#firstName').tooltip({'trigger':'hover', 'title': firstName});
			$('#lastName').tooltip({'trigger':'hover', 'title': lastname});
		}
	};
});
