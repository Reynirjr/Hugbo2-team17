$(function() {
  $('.js-action-dropdown').each(function() {
    $(this).val('')
  })

  $('.refund_popup_box').click(function() {
    $('.refund_popup_box').removeClass('selected')
    $(this).addClass('selected')

    $('.grayout').show()
    $($(this).children('.grayout:first')).hide()

    $('#refund_type').val($(this).data('type'))
    $('#refund_error_box').hide()
  })

  $('.refund_popup_box').click(function() {
    if($('#vat_refund_box').hasClass('selected')) {
      $('#markAsTaxExempt').show();
      $('#markAsTaxExempt').find('input:checkbox').removeAttr('disabled');
    } else {
      $('#markAsTaxExempt').hide();
      $('#markAsTaxExempt').find('input:checkbox').attr('disabled', true);
    }
  })

  $('#show-vat-refund').click(function() {
    $('#vat-checkbox-wrapper').hide()
    $('#vat_refund_box').show()
  })
})

function select_account_type() {
  var type = $('#account_type').val()
  if (type == 'individual') {
    $('#individual').show()
    $('#corporation').hide()
    $('#i_desc_legend').show()
    $('#c_desc_legend').hide()
  } else if (type != null) {
    $('#individual').hide()
    $('#corporation').show()
    $('#i_desc_legend').hide()
    $('#c_desc_legend').show()
  }
}

function country_is_eu(country) {
  return (
    [
      'BE',
      'BG',
      'CZ',
      'DK',
      'DE',
      'EE',
      'IE',
      'EL',
      'ES',
      'FI',
      'FR',
      'HR',
      'IT',
      'CY',
      'LV',
      'LT',
      'LU',
      'HU',
      'MT',
      'NL',
      'AT',
      'PL',
      'PT',
      'RO',
      'SI',
      'SK',
      'SE',
      'GB',
    ].indexOf(country) != -1
  )
}

function country_part_of(country) {
  switch (country) {
    case "IM":
    case "JE":
    case "GG":
      return "GB";
    default:
      return country;
  }
}

function country_is_iban(country) {
  return (PaddleVars.ibanCountries || []).indexOf(country) >= 0
}

function toggle_country_fields(countrySelector, parentSelector) {
  var country = ($(countrySelector).val() || '').toUpperCase() // depending on where it comes from: country selector or bankCountry selector
  var $parent = $(parentSelector)
  var bankCountrySelector = $('select[name="bank_country"]')
  var bankCountry = bankCountrySelector.is(':visible') ? bankCountrySelector.val() : ''
  var bankCurrencySelector = $('select[name="bank_currency"]')
  var bankCurrency = bankCurrencySelector.is(':visible') ? bankCurrencySelector.val() : ''

  // country and bankCountry ebing override if it's an island part of another country or similar
  country = country_part_of(country)
  bankCountry = country_part_of(bankCountry)
  accountCountry = country_part_of($('#country').val())

  // reset the default fields
  $parent.find('[class$="_only"], [class*="_only "]').hide()
  $parent.find('[class^="not_"], [class*=" not_"]').show()

  var countrySpecificFields = $parent.find('.' + country.toLowerCase() + '_only')
  countrySpecificFields.show()
  $parent.find('.not_' + country.toLowerCase()).hide()

  var groupCodeMap = {
    iban: countrySpecificFields.length === 0 && country_is_iban(country),
    eu: country_is_eu(country),
  }

  // conditionally show/hide country specific fields
  for (var groupCode in groupCodeMap) {
    if (groupCodeMap[groupCode]) {
      $parent.find('.' + groupCode + '_only').show()
      $parent.find('.not_' + groupCode).hide()
    }
  }

  if (bankCountry === 'GB' && bankCurrency !== 'GBP') {
    $('.gb_only').hide()
    $('.not_iban').hide()
    $('.iban_only').show()
    $('.not_gb').show()
  }

  if (bankCountry === 'US') {
    $('.not_us').hide()
  }

  if (bankCountry === 'US' || accountCountry === 'US') {
    $('#wire-transfer-option').text("Bank transfer")
    $('#wire-transfer-details-title').text("Bank transfer details")
  } else {
    $('#wire-transfer-option').text("Wire transfer")
    $('#wire-transfer-details-title').text("Wire transfer details")
  }
}

function change_bank_country() {
  toggle_country_fields('#bank_country', '#payment-wire')
}

function change_country() {
  toggle_country_fields('#country', '#about-business')
}

function submit_payout() {
  // Get the button reference at the start
  var $saveButton = $("input[type='button'].pui-btn-primary[name='action']")

  // Check if button is already disabled (request in progress)
  if ($saveButton.prop('disabled')) {
    return false
  }

  // Disable button immediately to prevent spam clicks
  $saveButton.prop('disabled', true)

  // check form fields for client-side validation
  var type = $('#account_type').val()
  var country = $('#country').val()
  var errors = []

  if (!country || country.length == 0) errors.push('You must select a country')
  if (!type || type.length == 0) errors.push('You must choose an account type')
  //about your business/self
  if (type == 'individual') {
    if (!$('#individual input[name=i_street]').val()) errors.push('You must enter a street name')
    if (!$('#individual input[name=i_postcode]').val()) errors.push('You must enter a postcode')
    if (!$('#individual input[name=i_city]').val()) errors.push('You must enter a city')
  } else {
    if (!$('#corporation input[name=legal_name]').val()) errors.push('You must enter a legal name')
    if (!$('#corporation input[name=c_street]').val()) errors.push('You must enter a street name')
    if (!$('#corporation input[name=c_postcode]').val()) errors.push('You must enter a postcode')
    if (!$('#corporation input[name=c_city]').val()) errors.push('You must enter a city')
    if (country_is_eu(country) && !$('#corporation input[name=company_no]').val())
      errors.push('You must enter a company number')
  }
  if ($('select[name=state]').is(':visible') && !$('select[name=state]').val()) errors.push('You must select a state')
  if ($('select[name=province]').is(':visible') && !$('select[name=province]').val())
    errors.push('You must select a province')
  //about you
  if (!$('#about input[name=first_name]').val()) errors.push('You must enter a first name')
  if (!$('#about input[name=last_name]').val()) errors.push('You must enter a last name')
  if (!$('#about input[name=day]').val()) errors.push('You must enter a day')
  if (!$('#about input[name=month]').val()) errors.push('You must enter a month')
  if (!$('#about input[name=year]').val()) errors.push('You must enter a year')
  //payout details
  var method = $('#payout_method').val()
  switch (method) {
    case 'payoneer':
      if (!$('#payment-payoneer input[name=payoneer_email]').val()) errors.push('You must enter a Payoneer email')
      break
    case 'paypal':
      if (!$('#payment-paypal input[name=paypal_email]').val()) errors.push('You must enter a paypal email')
      break
    case 'wire':
      if (!$('#payment-wire input[name=bank_name]').val()) errors.push('You must enter a bank name')
      if (!$('#payment-wire textarea[name=bank_address]').val()) errors.push('You must enter a bank address')
      if (!$('#payment-wire select[name=bank_country]').val()) errors.push('You must enter a bank country')
      if (!$('#payment-wire select[name=bank_currency]').val()) errors.push('You must enter a transfer currency')
      if (!$('#payment-wire input[name=account_name]').val()) errors.push('You must enter an account name')
      if ($('#payment-wire input[name=bic_swift]').is(':visible') && !$('#payment-wire input[name=bic_swift]').val())
        errors.push('You must enter a BIC/SWIFT code')
      if ($('#payment-wire input[name=iban]').is(':visible') && !$('#payment-wire input[name=iban]').val())
        errors.push('You must enter an IBAN code')
      if ($('#payment-wire input[name=account_no]').is(':visible') && !$('#payment-wire input[name=account_no]').val())
        errors.push('You must enter an account number')
      if ($('#payment-wire input[name=sort_code]').is(':visible') && !$('#payment-wire input[name=sort_code]').val())
        errors.push('You must enter a sort code')
      break
    case 'check':
      if (!$('#payment-check textarea[name=check_address]').val()) errors.push('You must enter a check address')
      if (!$('#payment-check input[name=payout_name]').val()) errors.push('You must enter a payout name')
      break
    default:
      errors.push('You must select a payment method')
  }
  $('.vendor-message').hide()
  $('#payout_error').hide()
  if (errors.length) {
    // Re-enable button if there are validation errors
    $saveButton.prop('disabled', false)

    // Display element with ID=payout_error
    $('#payout_error').show()
    $('#payout_error .pui-alert-danger').html('<ul><li>' + errors.join('</li><li>') + '</li></ul>')

    // sometimes errors are not visible in small screens, so we should scroll page up
    $('html, body').animate({ scrollTop: 0 }, 'slow')
    return false
  } else {
    var $form = $('#payout_form')

    // clear hidden country specific fields
    var $fields = $form.find('[class$="_only"], [class*="_only "], [class^="not_"], [class*=" not_"]')
    $fields.filter(':hidden').find('input, select').val('')

    // clear invalid fields in #about-business
    var $toClear = (type === 'individual') ? '#corporation' : '#individual'
    $($toClear).find('input, select').val('')

    $form.submit()
  }
}

var update_queue = []
update_queue.ready = true
function send_update(data) {
  update_queue.push(data)
  release_update()
}
function release_update() {
  if (!update_queue.ready) {
    window.setTimeout(release_update, 250)
    return
  }
  update_queue.ready = false
  if (update_queue.length == 0) return
  data = update_queue.shift()
  data._token = server_data.csrf_token
  if (server_data.product_state) {
    data.state = server_data.product_state
  }

  var onComplete = data.onComplete || false
  delete data.onComplete

  var onFail = data.onFail || false
  delete data.onFail

  $.post(server_data.update_url, data, function(result) {
    var data = typeof result === 'object' ? result : JSON.parse(result)

    if (data.success === false && onFail) {
      update_queue.ready = true
      return onFail(data)
    }
    if (!server_data.product && data.product) {
      url = window.location.href + '/' + data.product
      history.replaceState({}, document.title, url)
    }
    if (data.is_devmate_product_connected) {
      $('select[name="fulfillment"] option[value="devmate"]').show()
    }
    $.each(data, function(i, e) {
      server_data[i] = e
    })
    if ($('form[target*=iframe]').parents('#add-product-final').length) {
      $('form[target*=iframe]').attr('action', server_data.upload_url)
    }
    update_queue.ready = true
    server_data.product_loaded = true
    if (onComplete) onComplete(data)
  })
}
function confirm_redirect(url, message) {
  if (!confirm(message)) return
  window.location.href = url
}

function confirmSubmitAndDisableClick(event, form, message) {
  if (!confirm(message)) return
  $(event)
    .prop('onclick', null)
    .off('click');
  form.submit();
}

//Helper method for uploading icon and screenshot files
function doFilePickerUpload(options, policy, signature) {
  var client = filestack.init(server_data.filepicker_key, {
    security: {
      policy: policy,
      signature: signature,
    }
  });
  var deferred = $.Deferred()
  var getUploadAjax

  getUploadAjax = function(res) {
    // this is executed when file is uploaded, we need now to store details in our db
    var request_data = res.filesUploaded[0]
    if (!request_data) {
      return deferred.reject('Failed to upload file.')
    }
    request_data._token = server_data.csrf_token
    request_data.upload_type = options.upload_type

    return $.ajax({
      url: server_data.upload_url,
      type: 'POST',
      data: request_data,
    })
  }

  if (typeof options.getUploadAjax !== 'undefined') {
    getUploadAjax = options.getUploadAjax
  }

  var pickerConfig = {
    storeTo: {
      location: 's3',
      path: options.path,
      container: options.container,
      region: 'us-east-1',
      access: typeof options.access === 'undefined' ? 'public' : options.access,
    },
    onUploadDone: function(res) {
      getUploadAjax(res)
        .done(deferred.resolve)
        .fail(deferred.reject)
    },
    transformationsUI: false,
    fromSources: ["local_file_system", "imagesearch", "instagram", "facebook", "googledrive"]
  }

  if (typeof options.maxSize !== 'undefined') {
    pickerConfig.maxSize = parseInt(options.maxSize)
  }

  if (typeof options.accept !== 'undefined') {
    pickerConfig.accept = options.accept
  }

  if (typeof options.errorHandler !== 'undefined') {
    pickerConfig.onFileUploadFailed = options.errorHandler
  }

  if (typeof options.closeHandler !== 'undefined') {
    pickerConfig.onClose = options.closeHandler
  }

  client.picker(pickerConfig).open()

  return deferred
}

function isSessionStorageAvailable() {
  try {
    var storage = window.sessionStorage,
      x = '__storage_test__'
    storage.setItem(x, x)
    storage.removeItem(x)
    return true
  } catch (e) {
    return (
      e instanceof DOMException &&
      // everything except Firefox
      (e.code === 22 ||
        // Firefox
        e.code === 1014 ||
        // test name field too, because code might not be present
        // everything except Firefox
        e.name === 'QuotaExceededError' ||
        // Firefox
        e.name === 'NS_ERROR_DOM_QUOTA_REACHED') &&
      // acknowledge QuotaExceededError only if there's something already stored
      storage.length !== 0
    )
  }
}

function getFromSessionStorage(key, fallback) {
  if (!isSessionStorageAvailable()) {
    return fallback
  }

  var storedValue = window.sessionStorage.getItem(key)

  if (!storedValue || isNaN(storedValue)) {
    return fallback
  }

  return storedValue
}

function setInSessionStorage(key, value) {
  if (!isSessionStorageAvailable()) {
    return false
  }

  try {
    window.sessionStorage.setItem(key, value)
    return true
  } catch (error) {
    return false
  }
}

$(document).ready(function() {
  $('#darkfreeze').click(function() {
    $('#darkfreeze .loading').effect('shake')
  })

  /* Expandable menu items in nav */
  var navExpandInProgress = false
  $('.navigation-expandable > a').click(function(e) {
    // Don't double run if still animating
    if (!navExpandInProgress) {
      navExpandInProgress = true
      var target = $(this)
        .siblings('.navigation')
        .eq(0)
      target.toggle('blind', {
        duration: 160,
        complete: function() {
          navExpandInProgress = false
        },
      })
      $(this)
        .parent()
        .toggleClass('open') // Feels more responsive if we add the style immediately
    }
  })

  /* sidebar balance slider */
  $(function() {
    var statNumber = getFromSessionStorage('financialStatNumber', 0)

    var $financialStats = $('div.financial-stats')

    var numberOfStats = $financialStats.length - 1

    $financialStats.eq(statNumber).show()
    if (!$financialStats.is(':visible')) {
      $financialStats.eq(0).show()
    }

    $('.balance-container .navigator.right').click(function() {
      var financialStatNumber = getVisibleStat($financialStats)

      $financialStats.hide()
      financialStatNumber++
      if (financialStatNumber > numberOfStats) {
        financialStatNumber = 0
      }
      setInSessionStorage('financialStatNumber', financialStatNumber)
      $financialStats.eq(financialStatNumber).show()
    })

    $('.balance-container .navigator.left').click(function() {
      var financialStatNumber = getVisibleStat($financialStats)

      $financialStats.hide()
      financialStatNumber--
      if (financialStatNumber < 0) {
        financialStatNumber = numberOfStats
      }

      setInSessionStorage('financialStatNumber', financialStatNumber)
      $financialStats.eq(financialStatNumber).show()
    })

    function getVisibleStat($financialStats) {
      var financialStatNumber = 0
      $financialStats.each(function(index, value) {
        if ($(this).is(':visible')) {
          financialStatNumber = index
        }
      })
      return financialStatNumber
    }
  })

  if ($('#payout_form').length) {
    var form_original_data = $('#payout_form').serialize()
    $('#next_button').click(function(event) {
      if ($('#payout_form').serialize() != form_original_data) {
        if (confirm('Leave without saving changes?')) {
          $('#payout_form button').attr('disabled', 'disabled')
          //allow default (the form submit) to proceed
        } else {
          event.preventDefault()
        }
      }
    })
  }

  $('body').on('click', '.pui-popup:visible', function(event) {
    if (event.target == $('.pui-popup:visible').get(0)) {
      closePopup()
    }
  })

  $(document).on('click', '.js-close-popup', function(event) {
    closePopup(event)
  })

  $(document).on('click', '.pui-popup-link', function(event) {
    var id = $(this).data('popup')
    openPopup(id, event)
  })

  $(document).on('click', '.pui-alert .close', function(event) {
    $(this)
      .closest('.pui-alert')
      .remove()
  })

  $(document).on('change', '.pui-popup-2 form select', function(event) {
    var selectBox = $(this)
    var selectedOption = selectBox.find('option:selected')

    if (selectedOption.val() === '') {
      selectBox.removeClass('paddleBlue600').addClass('paddleBlue300')
    } else {
      selectBox.removeClass('paddleBlue300').addClass('paddleBlue600')
    }
  })

  // Disable input buttons on payout form when POST request is in flight
  // Wrapped in $().ready(handler) jQuery so that it runs once the DOM is safe to manipulate
  $(document).on('click', "#payout_form input[type='submit'][name='action']", function (event) {
    // 'this' refers to the actual button that was clicked
    var $clickedButton = $(this);
    var $form = $('#payout_form');

    // Select all submit buttons with name="action" within the form to disable them
    var $allActionButtons = $form.find("input[type='submit'][name='action']");

    // Check if any of the action buttons are already disabled (e.g., if one was clicked and logic is re-triggered)
    if ($clickedButton.prop('disabled')) {
      return false;
    }

    // Remove any existing hidden 'action' input to ensure we use the current clicked button's value
    $form.find("input[type='hidden'][name='action']").remove();

    // append the input's name and value as a hidden element, Request::input['action'] is used by PHP
    $form.append($("<input>", {
      type: "hidden",
      name: $clickedButton.attr('name'), // Should be 'action'
      value: $clickedButton.val()      // e.g., "Save" or "Save & Approve"
    }));

    // Disable all action buttons immediately to prevent spam clicks
    $allActionButtons.prop('disabled', true);

    // Submit the form - The form submission will now use the value from the hidden input
    $form.submit();
  });
})

function openPopup(id, event) {
  if (!$(id).length) {
    return false
  }
  $(id).fadeIn(300)
  $('body').addClass('pui-popup-open')
  $(id).trigger('openPopup', [event])
}

function closePopup(event) {
  $('.pui-popup').fadeOut(300)
  $('body').removeClass('pui-popup-open')
  $('.pui-popup').trigger('closePopup', [event])
}

function showSuccessMessage(messages) {
  var $messageContainer = addMessageContainerElement('success')
  $.each(messages, function(index, message) {
    $messageContainer.find('ul').append($('<li>').text(message))
  })
}

function showFailMessage(messages) {
  var $messageContainer = addMessageContainerElement('danger')
  $.each(messages, function(index, message) {
    $messageContainer.find('ul').append($('<li>').text(message))
  })
}

function addMessageContainerElement(type) {
  $('.vendor-message').remove()
  return $('#vendor-content').prepend(
    '<div class="vendor-message"><fieldset class="pui-alert pui-alert-' + type + '"><ul></ul></fieldset></div>',
  )
}
