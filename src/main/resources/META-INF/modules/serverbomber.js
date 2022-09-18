define([ "jquery", "t5/core/console" ], function($, console) {
	return {
		init : function() {
		},
		bombserver1 : function() {
			console.warn("HERE WE GO...");
			$(".bombing-area").append("HERE WE GO1...\n");
			var values = [ 2, 3, 4 ];
			bombServer(values);
		},
		bombserver2 : function() {
			console.warn("HERE WE GO...");
			$(".bombing-area").append("HERE WE GO2...\n");
			var values = [ 5, 6, 7 ];
			bombServer(values);
		}
	};
	function bombServer(values) {
		$(".bombing-area").append("i will bomb with " + values + "\n");
		for (var i = 0; i < 333; i++) {
			for (var j = 0; j < 3; j++) {
				var entry = values[j];
				doPost(entry, i);
			}
		}
	}
	function doPost(entry, counter) {
		var val = "";
		$.ajaxSetup({async: true});
		$.post("http://localhost:8080/bombserver:ajax3", {
			uuid: "",
			value: entry
		}, function(data, status) {
			val = counter + " bomb " + entry + "=" + data.value + "\n";
			printOnTextArea(val);
		});
	}
	function printOnTextArea(val) {
		$(".bombing-area").append(val);
		var psconsole = $('.bombing-area');
		if (psconsole.length) {
			psconsole.scrollTop(psconsole[0].scrollHeight
					- psconsole.height());
		}
	}
});