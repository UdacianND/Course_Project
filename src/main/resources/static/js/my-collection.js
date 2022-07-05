$(document).ready(function(){

    $("#add-collection").click(function(){
        $("#message-form").css("visibility","visible")
    })

    $('#contact-form').on( "submit", function(event) {
        event.preventDefault();

        let name = $('#input-collection-name').val();
        let topic = $('#input-collection-topic').val();
        let description = $('#input-collection-description').val();

        let fd = new FormData();
        fd.append('name', name)
        fd.append('topic', topic)
        fd.append( 'image', $('#inputGroupFile01').files[0] );
        fd.append('description', description)

        $.ajax({
            url: '/api/collections/add',
            data: fd,
            processData: false,
            contentType: false,
            type: 'POST',
            success: function(data){
                alert(data);
            }
        });
    });

    let fields = ['NUMBER','NUMBER','NUMBER', 'TEXT','TEXT','TEXT', 'TEXTAREA','TEXTAREA','TEXTAREA', 'CHECKBOX','CHECKBOX','CHECKBOX', 'DATE', 'DATE', 'DATE']

    $('#add-field-button').click(function(){
        let fieldType = $('.field-input').last().find(":selected").text();
        for (let i = 0; i < fields.length; i++) {
            if(fields[i] === fieldType){
                fields.splice(i,1)
                break
            }
        }

        let options = '<option>TYPE</option>\n'

        for (let i = 0; i < fields.length; i++) {
            options += '<option>'+fields[i]+'</option>\n'
        }

        let newField =
            '<div class="row">'+
                '<div class="col-md-12 field">\n' +
                '    <input type="text" name="fieldName" class="form-control field-input" placeholder="name">\n' +
                '   <select class="form-control field-input" name="type">\n' +
                options +
                '   </select>\n' +
                '</div>\n'+
            '</div>'
        $(newField).insertBefore("#add-field-row")
    })



})