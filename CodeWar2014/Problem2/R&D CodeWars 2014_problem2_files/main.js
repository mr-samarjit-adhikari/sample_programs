$(document).ready(function(){

	smoothScroll.init({
	    speed: 500, // Integer. How fast to complete the scroll in milliseconds
	    easing: 'easeInOutCubic', // Easing pattern to use
	    updateURL: true, // Boolean. Whether or not to update the URL with the anchor hash on scroll
	    offset: 60, // Integer. How far to offset the scrolling anchor location in pixels
	    callbackBefore: function ( toggle, anchor ) {}, // Function to run before scrolling
	    callbackAfter: function ( toggle, anchor ) {} // Function to run after scrolling
	});

		//Disable registration begins

				var form = document.getElementById("signup-form");
	               var elements = form.elements;
	               for (var i = 0, len = elements.length; i < len; ++i) {
						elements[i].readOnly = true;
				    }
				var btn=document.getElementById("signup-btn");
				btn.disabled =true;

		//Disbale registration ends	


});