<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<title>Document</title>
	<script src="https://cdn.bootcss.com/jquery/3.1.1/jquery.min.js"></script>
	<style>
		* {
			  font-family: 'Helvetica Neue', Helvetica, sans-serif;
			  font-size: 19px;
			  font-variant: normal;
			  padding: 0;
			  margin: 0;
			}

			html {
			  height: 100%;
			}

			body {
			  background: #424770;
			  display: flex;
			  align-items: center;
			  min-height: 100%;
			}

			form {
			  width: 480px;
			  margin: 20px auto;
			}

			label {
			  height: 35px;
			  position: relative;
			  color: #8798AB;
			  display: block;
			  margin-top: 30px;
			  margin-bottom: 20px;
			}

			label > span {
			  position: absolute;
			  top: 0;
			  left: 0;
			  width: 100%;
			  height: 100%;
			  font-weight: 300;
			  line-height: 32px;
			  color: #8798AB;
			  border-bottom: 1px solid #586A82;
			  transition: border-bottom-color 200ms ease-in-out;
			  cursor: text;
			  pointer-events: none;
			}

			label > span span {
			  position: absolute;
			  top: 0;
			  left: 0;
			  transform-origin: 0% 50%;
			  transition: transform 200ms ease-in-out;
			  cursor: text;
			}

			label .field.is-focused + span span,
			label .field:not(.is-empty) + span span {
			  transform: scale(0.68) translateY(-36px);
			  cursor: default;
			}

			label .field.is-focused + span {
			  border-bottom-color: #34D08C;
			}

			.field {
			  background: transparent;
			  font-weight: 300;
			  border: 0;
			  color: white;
			  outline: none;
			  cursor: text;
			  display: block;
			  width: 100%;
			  line-height: 32px;
			  padding-bottom: 3px;
			  transition: opacity 200ms ease-in-out;
			}

			.field::-webkit-input-placeholder { color: #8898AA; }
			.field::-moz-placeholder { color: #8898AA; }

			/* IE doesn't show placeholders when empty+focused */
			 .field:-ms-input-placeholder { color: #424770; }

			.field.is-empty:not(.is-focused) {
			  opacity: 0;
			}

			button {
			  float: left;
			  display: block;
			  background: #34D08C;
			  color: white;
			  border-radius: 2px;
			  border: 0;
			  margin-top: 20px;
			  font-size: 19px;
			  font-weight: 400;
			  width: 100%;
			  height: 47px;
			  line-height: 45px;
			  outline: none;
			}

			button:focus {
			  background: #24B47E;
			}

			button:active {
			  background: #159570;
			}

			.outcome {
			  float: left;
			  width: 100%;
			  padding-top: 8px;
			  min-height: 20px;
			  text-align: center;
			}

			.success, .error {
			  display: none;
			  font-size: 15px;
			}

			.success.visible, .error.visible {
			  display: inline;
			}

			.error {
			  color: #E4584C;
			}

			.success {
			  color: #34D08C;
			}

			.success .token {
			  font-weight: 500;
			  font-size: 15px;
			}
	</style>

</head>
<body>
	<script src="https://js.stripe.com/v3/"></script>
	<form>
	  <label>
	    <div id="card-element" class="field is-empty"></div>
	    <span><span>Credit or debit card</span></span>
	  </label>
	  <button type="submit">Pay $0.01</button>
	  <div class="outcome">
	    <div class="error" role="alert"></div>
	    <div class="success">
	      Success! Your Stripe token is <span class="token"></span>
	    </div>
	  </div>
	</form>
</body>

<script>
	var getUrlParameter = function getUrlParameter(sParam) {
	    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
	        sURLVariables = sPageURL.split('&'),
	        sParameterName,
	        i;
	
	    for (i = 0; i < sURLVariables.length; i++) {
	        sParameterName = sURLVariables[i].split('=');
	
	        if (sParameterName[0] === sParam) {
	            return sParameterName[1] === undefined ? true : sParameterName[1];
	        }
	    }
	};

	var stripe = Stripe('pk_test_6pRNASCoBOKtIshFeQd4XMUh');
	var elements = stripe.elements();

	var card = elements.create('card', {
	  iconStyle: 'solid',
	  style: {
	    base: {
	      iconColor: '#8898AA',
	      color: 'white',
	      lineHeight: '36px',
	      fontWeight: 300,
	      fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
	      fontSize: '19px',

	      '::placeholder': {
	        color: '#8898AA',
	      },
	    },
	    invalid: {
	      iconColor: '#e85746',
	      color: '#e85746',
	    }
	  },
	  classes: {
	    focus: 'is-focused',
	    empty: 'is-empty',
	  },
	});
	card.mount('#card-element');

	var inputs = document.querySelectorAll('input.field');
	Array.prototype.forEach.call(inputs, function(input) {
	  input.addEventListener('focus', function() {
	    input.classList.add('is-focused');
	  });
	  input.addEventListener('blur', function() {
	    input.classList.remove('is-focused');
	  });
	  input.addEventListener('keyup', function() {
	    if (input.value.length === 0) {
	      input.classList.add('is-empty');
	    } else {
	      input.classList.remove('is-empty');
	    }
	  });
	});

	function setOutcome(result) {
	  var successElement = document.querySelector('.success');
	  var errorElement = document.querySelector('.error');
	  successElement.classList.remove('visible');
	  errorElement.classList.remove('visible');

	  if (result.token) {
	    // Use the token to create a charge or a customer

	    submit(result.token.id);

	    successElement.querySelector('.token').textContent = result.token.id;
	    successElement.classList.add('visible');
	  } else if (result.error) {
	    errorElement.textContent = result.error.message;
	    errorElement.classList.add('visible');
	  }
	}

	card.on('change', function(event) {
	  setOutcome(event);
	});

	document.querySelector('form').addEventListener('submit', function(e) {
	  e.preventDefault();

	  stripe.createToken(card, {
	  	name: 'wewawa',
	  }).then(setOutcome);

	});



	function submit(token) {

		var url = 'http://localhost:8080/laresBeauty/payment/checkout';
		
		var amend_detail_arr = [];
		
		let product_detail = {
			product_id: 1,
			product_quantity: 1
		}
		amend_detail_arr.push(JSON.stringify(product_detail));
		amend_detail_arr.push(JSON.stringify(product_detail));

		var formData = new FormData();    
		formData.append('user_id', getUrlParameter('user_id'));
		formData.append('user_address_info_id', getUrlParameter('user_address_info_id'));
		formData.append('price', getUrlParameter('price'));
		formData.append('stripeToken', token);
		formData.append('amend_detail', amend_detail_arr);

		$.ajax({
		  url: url,
		  data: formData,
		  processData: false,
		  contentType: false,
		  type: 'POST',
		  success: function(data){
		    alert(data);
		  }
		});
	}

</script>

</html>