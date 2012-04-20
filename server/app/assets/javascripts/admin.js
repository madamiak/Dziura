$(function() {
    //ukrywanie komunikatów
    if($('div.notice')) $('div.notice').delay(4000).slideUp();
    
    //jQuery UI
    //ustawianie styli globalnych
    $('#container').addClass('ui-widget');
    $('#auth').addClass('ui-widget');
    
    //ustawianie styli tabel
    $('tbody tr').hover(function() {
        $(this).toggleClass('ui-state-hover');
    });
    $('table').addClass('ui-widget ui-widget-content');
    $('thead tr').addClass('ui-widget-header');
    
    //ustawianie styli forms
    $("input:submit, button, a.btn").button();
    $( "a.btn_dialog")
    .button()
    .click(function() {
       var url = $(this).attr('href');
       var dialog_window =  $( "#dialog-form" );
       setContentDialogWindow(dialog_window, url)
       dialog_window.dialog( "open" );
       return false;
    });
    
    //tworzenie okien dialogowych
    $( "#dialog-form" ).dialog({
        autoOpen: false,
        height: 300,
        width: 350,
        modal: true,
        buttons: {
            //            "Create an account": function() {
            //                var bValid = true;
            //                allFields.removeClass( "ui-state-error" );
            //
            //                bValid = bValid && checkLength( name, "username", 3, 16 );
            //                bValid = bValid && checkLength( email, "email", 6, 80 );
            //                bValid = bValid && checkLength( password, "password", 5, 16 );
            //
            //                bValid = bValid && checkRegexp( name, /^[a-z]([0-9a-z_])+$/i, "Username may consist of a-z, 0-9, underscores, begin with a letter." );
            //                // From jquery.validate.js (by joern), contributed by Scott Gonzalez: http://projects.scottsplayground.com/email_address_validation/
            //                bValid = bValid && checkRegexp( email, /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.?$/i, "eg. ui@jquery.com" );
            //                bValid = bValid && checkRegexp( password, /^([0-9a-zA-Z])+$/, "Password field only allow : a-z 0-9" );
            //
            //                if ( bValid ) {
            //                    $( "#users tbody" ).append( "<tr>" +
            //                        "<td>" + name.val() + "</td>" + 
            //                        "<td>" + email.val() + "</td>" + 
            //                        "<td>" + password.val() + "</td>" +
            //                        "</tr>" ); 
            //                    $( this ).dialog( "close" );
            //                }
            //            },
            Cancel: function() {
                $( this ).dialog( "close" );
            }
        },
        close: function() {
            allFields.val( "" ).removeClass( "ui-state-error" );
        }
    });
    
    //wypelnianie okna dialogowego
    function setContentDialogWindow(dialog_window, url){
        $.get(url,
            function(data){
                dialog_window.html(data);
            
                // pobieranie i ustawianie tytuly okna
                title = dialog_window.find('h1').html();
                dialog_window.find('h1').empty();
                dialog_window.dialog( "option", "title", title );
            });
        
        
       
    }
});