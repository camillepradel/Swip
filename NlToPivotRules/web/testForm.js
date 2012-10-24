sendPost = function(){
    var t = $.parseJSON($('#dependencyTree').val());
    
    var form = $('<form method="post" action="http://localhost:8080/NlToPivotRules/resources/rest/dependenciesToPivot" name="dopost" style="display:none;"><input type="text" name="lang" value="en" /></form>');
    $('body').append(form);
    $(form).submit();

//    $('#inset_form').html('<form method="post" action="http://localhost:8080/NlToPivotRules/resources/rest/dependenciesToPivot name="dopost" style="display:none;"><input type="text" name="lang" value="en" /></form>');
//
//    document.forms['dopost'].submit();
};