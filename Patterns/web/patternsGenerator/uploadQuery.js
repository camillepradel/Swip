
$(function()
{
    var sparqlEditor = CodeMirror.fromTextArea(document.getElementById("sparqlquery"), {
            mode: "application/x-sparql-query",
            tabMode: "indent",
            matchBrackets: true,
          });

    var turtleArea = CodeMirror.fromTextArea(document.getElementById("spinquery"), {
            mode: "text/turtle",
            tabMode: "indent",
            matchBrackets: true,
            readOnly: "true"
          });

    var sparqUpdatelEditor = CodeMirror.fromTextArea(document.getElementById("updatequery"), {
            mode: "application/x-sparql-query",
            tabMode: "indent",
            matchBrackets: true,
          });

    $('#sparqlToSpinButton').focus();

    $('#sparqlToSpinButton').click(function()
    {
        var text = sparqlEditor.getValue();
        $.ajax
        ({
            type: 'POST',
            dataType: 'text',
            url: 'http://spinservices.org:8080/spin/sparqlmotion',
            data: {
                id: 'sparql2spin',
                text: text,
                format: "turtle",
            },
            success: function(response, textStatus, jqXHR) {
                turtleArea.setValue(response);
            },
        })
        .fail(function(jqXHR, textStatus) {
            alert('Ajax error, please try again !');
        });
	});

    function setLocalName(hashFunction) {
        var hash = hashFunction(sparqlEditor.getValue().replace(/\s+/g, ''));
        $('#urilocalname').val(hash);
    }

    $('#md5').click(function()
    {
        setLocalName(CryptoJS.MD5);
    });
    $('#sha1').click(function()
    {
        setLocalName(CryptoJS.SHA1);
    });

    $('#generate').click(function()
    {
        var state = 'prefix';
        var updatequery = '';
        lines = turtleArea.getValue().split('\n');
        for (i=0;i<lines.length;i++) {
            var line = lines[i];
            if (state == 'prefix') {
                if (line.substring(0, 7) == '@prefix') {
                    updatequery += line.substring(1, line.length-2) + '\n';
                } else {
                    state = 'data';
                    updatequery += 'prefix query: <' + $('#uriprefix').val() + '>\n\nINSERT DATA {\n  GRAPH <' + $('#namedgraph').val() + '>\n  {\n';
                }
            }
            if (state == 'data') {
                updatequery += '    ' + line.replace('[]', 'query:' + $('#urilocalname').val()) + '\n';
            }
        }
        updatequery += '  }\n}';
        sparqUpdatelEditor.setValue(updatequery);
    });

    $('#posturl').change(function() {
        $('#postform').attr('action', $('#posturl').val());
    });

    // $('#commit').click(function()
    // {
    //     var update = sparqUpdatelEditor.getValue();
    //     // console.log(update);
    //     $.ajax
    //     ({
    //         type: 'POST',
    //         dataType: 'text',
    //         url: $('sparqlendpoint').val() + 'update',
    //         data: {
    //             update: update,
    //         },
    //         success: function(response, textStatus, jqXHR) {
    //             $('#commitresult').val('success');
    //         },
    //     })
    //     .fail(function(jqXHR, textStatus) {
    //         console.log(jqXHR);
    //         console.log(textStatus);
    //         alert('Ajax error, please try again !');
    //     });
    // });
});
